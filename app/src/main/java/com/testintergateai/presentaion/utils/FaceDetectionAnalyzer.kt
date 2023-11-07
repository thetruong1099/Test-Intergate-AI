package com.testintergateai.presentaion.utils

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import android.util.Pair
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions
import com.testintergateai.domain.model.FaceAnalyzer
import com.testintergateai.domain.tempData.TempData
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.io.IOException
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel
import kotlin.math.sqrt

@SuppressLint("UnsafeOptInUsageError")
class FaceDetectionAnalyzer(
    private val context: Context,
    private val isRecognition: Boolean = false,
    private val onResult: (List<FaceAnalyzer>) -> Unit
) : ImageAnalysis.Analyzer {

    private val inputSize = 112
    private val outputSize = 192
    private val imageMean = 128.0f
    private val imageSTD = 128.0f

    private var frameSkipCounter = 0
    private val realTimeOpts = FaceDetectorOptions.Builder()
        .setContourMode(FaceDetectorOptions.CONTOUR_MODE_ALL)
        .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
        .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_NONE)
        .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_NONE)
        .setMinFaceSize(0.2f)
        .enableTracking()
        .build()
    private val detector = FaceDetection.getClient(realTimeOpts)

    override fun analyze(imageProxy: ImageProxy) {
        val imageRotation = imageProxy.imageInfo.rotationDegrees
        val mediaImage = imageProxy.image
        mediaImage?.let {
            val inputImage = InputImage.fromMediaImage(mediaImage, imageRotation)
            val tempBitmap = imageProxy.toBitmap().rotateBitmap(imageRotation.toFloat())

            detector.process(inputImage)
                .addOnSuccessListener { faces ->
                    if (frameSkipCounter % 60 == 0) {
                        val result = mutableListOf<FaceAnalyzer>()
                        val bitmaps = faces.toListBitmap(bitmap = tempBitmap, width = inputSize, height = inputSize)
                        if (isRecognition) {
                            val result = mutableListOf<FaceAnalyzer>()
                            Log.d("DevLog", "analyze registered: ${TempData.registered}")
                            bitmaps.forEachIndexed { index, bitmap ->
                                val embeedings = faceRecognition(bitmap)
                                var name: String? = null
                                embeedings?.let {
                                    name = compareFace(embeedings)
                                }

                                Log.d("DevLog", "analyze: $name")

                                result.add(FaceAnalyzer(rect = faces[index].boundingBox, face = bitmap, name = name))
                            }

                            onResult.invoke(result)
                        } else if (bitmaps.isNotEmpty()) {
                            val embeedings = faceRecognition(bitmaps.first())
                            embeedings?.let {
                                result.add(FaceAnalyzer(face = bitmaps.first(), extra = embeedings))
                            }
                        }
                        onResult.invoke(result)
                    }

                    frameSkipCounter++

                    imageProxy.close()
                }
                .addOnFailureListener {
                    imageProxy.close()
                }
                .addOnCompleteListener {
                    imageProxy.close()
                }
        }
    }

    private fun faceRecognition(faceBitmap: Bitmap): Array<FloatArray>? {
        try {
            val tfLite =
                loadModelFile(
                    activity = context,
                    modelFile = "mobile_face_net.tflite"
                )?.let { Interpreter(it) }

            val imgData = ByteBuffer.allocateDirect(1 * inputSize * inputSize * 3 * 4)
            imgData.order(ByteOrder.nativeOrder())
            val intValues = IntArray(inputSize * inputSize)

            val bitmap = faceBitmap.resized(width = inputSize, height = inputSize)
            bitmap.getPixels(intValues, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height)
            imgData.rewind()

            for (i in 0 until inputSize) {
                for (j in 0 until inputSize) {
                    val pixelValue = intValues[i * inputSize + j]
                    imgData.putFloat(((pixelValue shr 16 and 0xFF) - imageMean) / imageSTD)
                    imgData.putFloat(((pixelValue shr 8 and 0xFF) - imageMean) / imageSTD)
                    imgData.putFloat(((pixelValue and 0xFF) - imageMean) / imageSTD)
                }
            }

            //imgData is input to our model
            val inputArray = arrayOf<Any>(imgData)
            val outputMap: MutableMap<Int, Any> = HashMap()
            val embeedings = Array(1) { FloatArray(outputSize) }
            outputMap[0] = embeedings
            tfLite?.runForMultipleInputsOutputs(inputArray, outputMap)
            return embeedings
        } catch (e: IOException) {
            Log.d("DevLog", "faceRecognition error: ${e.message}")
            return null
        }
    }

    @Throws(IOException::class)
    private fun loadModelFile(activity: Context, modelFile: String): MappedByteBuffer? {
        val fileDescriptor = activity.assets.openFd(modelFile)
        val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
        val fileChannel = inputStream.channel
        val startOffset = fileDescriptor.startOffset
        val declaredLength = fileDescriptor.declaredLength
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
    }

    private fun compareFace(embeedings: Array<FloatArray>): String? {
        var name: String? = null
        if (TempData.registered.size > 0) {
            val nearest = findNearest(embeedings[0])

            if (nearest?.get(0) != null) {
                name = nearest[0]!!.first
                // label = name;
                //distance_local = nearest[0]!!.second
            }
        }
        return name
    }

    //Compare Faces by distance between face embeddings
    private fun findNearest(emb: FloatArray): List<Pair<String, Float>?>? {
        val neighbourList: MutableList<Pair<String, Float>?> = ArrayList()
        var ret: Pair<String, Float>? = null
        var prevRet: Pair<String, Float>? = null

        TempData.registered.forEach { entry ->
            val name = entry.key
            val knownEmb = entry.value.extra.first()
            var distance = 0f

            for (i in emb.indices) {
                val diff = emb[i] - knownEmb[i]
                distance += diff * diff
            }

            distance = sqrt(distance.toDouble()).toFloat()

            if (ret == null || distance < ret!!.second) {
                prevRet = ret
                ret = Pair(name, distance)
            }
        }

        if (prevRet == null) prevRet = ret
        neighbourList.add(ret)
        neighbourList.add(prevRet)

        return neighbourList
    }
}