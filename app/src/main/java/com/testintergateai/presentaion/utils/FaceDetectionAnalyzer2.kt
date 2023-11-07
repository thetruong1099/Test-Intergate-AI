package com.testintergateai.presentaion.utils

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.Log
import android.view.Surface
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions
import com.testintergateai.domain.LandmarkClassifier
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.task.core.BaseOptions
import org.tensorflow.lite.task.core.vision.ImageProcessingOptions
import org.tensorflow.lite.task.vision.classifier.ImageClassifier
import java.io.FileInputStream
import java.io.IOException
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel

class FaceDetectionAnalyzer2(
    private val context: Context,
    private val classifier: LandmarkClassifier,
    private val onResult: (List<Rect>) -> Unit,
    private val getImageFace: (List<Bitmap?>) -> Unit
) : ImageAnalysis.Analyzer {

    private val realTimeOpts = FaceDetectorOptions.Builder()
        .setContourMode(FaceDetectorOptions.CONTOUR_MODE_ALL)
        .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
        .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_NONE)
        .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_NONE)
        .setMinFaceSize(0.2f)
        .enableTracking()
        .build()

    private val detector = FaceDetection.getClient(realTimeOpts)
    private var frameSkipCounter = 0

    @SuppressLint("UnsafeOptInUsageError")
    override fun analyze(imageProxy: ImageProxy) {
        val mediaImage = imageProxy.image
        val imageRotation = imageProxy.imageInfo.rotationDegrees
        mediaImage?.let {
            val inputImage = InputImage.fromMediaImage(mediaImage, imageRotation)

            val tempBitmap = imageProxy.toBitmap().rotateBitmap(imageRotation.toFloat())

            detector.process(inputImage)
                .addOnSuccessListener { faces ->
                    if (frameSkipCounter % 60 == 0) {
                        onResult.invoke(faces.toListRect())
                        val bitmaps = faces.toListBitmap(bitmap = tempBitmap)
                        getImageFace.invoke(bitmaps)
                        bitmaps.forEach {
                            faceRecognition(it)
                            //faceRecognition(it, imageRotation)
//                            val results = classifier.classify(bitmap = it, rotation = imageRotation)
//                            Log.d("DevLog", "analyze: $results")
                        }
//                        faces.forEach {
//                            val imageBitmap = tempBitmap.cropToBox(it.boundingBox, imageRotation)?.resizedForTfLite()
//                            imageBitmap?.let { bitmap ->
//                                val results = classifier.classify(bitmap = bitmap, rotation = imageRotation)
//                                Log.d("DevLog", "analyze: $results")
//                            }
//                        }
//                        faces.firstOrNull()?.let {
//                            val image = tempBitmap.cropToBox(it.boundingBox, imageRotation)
//                            getImageFace.invoke(image)
//                        }
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

    private fun faceRecognition(faceBitmap: Bitmap) {
        val inputSize = 112
        val IMAGE_MEAN = 128.0f
        val IMAGE_STD = 128.0f
        val OUTPUT_SIZE = 192
        try {
            val tfLite =
                loadModelFile(
                    activity = context,
                    MODEL_FILE = "model_face_test.tflite"
                )?.let { Interpreter(it) }//FileUtil.loadMappedFile(context, "mobile_face_net.tflite")


            //Create ByteBuffer to store normalized image
            val imgData = ByteBuffer.allocateDirect(1 * inputSize * inputSize * 3 * 4)
            imgData.order(ByteOrder.nativeOrder())
            val intValues = IntArray(inputSize * inputSize)

            val bitmap = faceBitmap.resizedForTfLite112()
            //get pixel values from Bitmap to normalize
            bitmap.getPixels(intValues, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height)
            imgData.rewind()

            for (i in 0 until inputSize) {
                for (j in 0 until inputSize) {
                    val pixelValue = intValues[i * inputSize + j]
                    imgData.putFloat(((pixelValue shr 16 and 0xFF) - IMAGE_MEAN) / IMAGE_STD)
                    imgData.putFloat(((pixelValue shr 8 and 0xFF) - IMAGE_MEAN) / IMAGE_STD)
                    imgData.putFloat(((pixelValue and 0xFF) - IMAGE_MEAN) / IMAGE_STD)
                }
            }

            //imgData is input to our model
            val inputArray = arrayOf<Any>(imgData)
            val outputMap: MutableMap<Int, Any> = HashMap()
            val embeedings = Array(1) { FloatArray(OUTPUT_SIZE) }
            outputMap[0] = embeedings
            tfLite?.runForMultipleInputsOutputs(inputArray, outputMap)

            Log.d("DevLog", "faceRecognition: $embeedings")

        } catch (e: IOException) {
            Log.d("DevLog", "faceRecognition: ${e.message}")
        }
    }

    private fun faceRecognition(bitmap: Bitmap, rotation: Int) {

        val baseOptions = BaseOptions.builder()
            .setNumThreads(2)
            .build()

        val options = ImageClassifier.ImageClassifierOptions.builder()
            .setBaseOptions(baseOptions)
            .setMaxResults(3)
            .setScoreThreshold(0.5f)
            .build()

        val classifierModel = ImageClassifier.createFromFileAndOptions(context, "model_face_test.tflite", options)

        val imageProcessor = ImageProcessor.Builder().build()
        val tensorImage = imageProcessor.process(TensorImage.fromBitmap(bitmap))

        val imageProcessingOptions = ImageProcessingOptions.builder()
            .setOrientation(getOrientationFromRotation(rotation))
            .build()

        val results = classifierModel?.classify(tensorImage, imageProcessingOptions)
        Log.d("DevLog", "classify: $results")
    }

    private fun resize(image: Bitmap, width: Int, height: Int): Bitmap {
        return Bitmap.createScaledBitmap(image, width, height, false)
    }

    private fun convertGray(image: Bitmap): Bitmap {
        val grayscale = Bitmap.createBitmap(image.width, image.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(grayscale)
        val paint = Paint()
        paint.color = Color.GRAY
        paint.isDither = true
        canvas.drawBitmap(image, 0f, 0f, paint)

        return grayscale
    }

    private fun normalize(image: Bitmap): Bitmap {
        val normalizedImage = Bitmap.createBitmap(image.width, image.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(normalizedImage)
        val paint = Paint()
        paint.color = Color.WHITE
        canvas.drawBitmap(image, 0f, 0f, paint)

        return normalizedImage
    }

    private fun getOrientationFromRotation(rotation: Int): ImageProcessingOptions.Orientation {
        return when (rotation) {
            Surface.ROTATION_270 -> ImageProcessingOptions.Orientation.BOTTOM_RIGHT
            Surface.ROTATION_90 -> ImageProcessingOptions.Orientation.TOP_LEFT
            Surface.ROTATION_180 -> ImageProcessingOptions.Orientation.RIGHT_BOTTOM
            else -> ImageProcessingOptions.Orientation.RIGHT_TOP
        }
    }

    @Throws(IOException::class)
    private fun loadModelFile(activity: Context, MODEL_FILE: String): MappedByteBuffer? {
        val fileDescriptor = activity.assets.openFd(MODEL_FILE)
        val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
        val fileChannel = inputStream.channel
        val startOffset = fileDescriptor.startOffset
        val declaredLength = fileDescriptor.declaredLength
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
    }

}