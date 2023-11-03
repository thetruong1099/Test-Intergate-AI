package com.testintergateai.presentaion.utils

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.Rect
import android.util.Log
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions
import com.testintergateai.domain.LandmarkClassifier

class FaceDetectionAnalyzer2(
    private val classifier: LandmarkClassifier,
    private val onResult: (List<Rect>) -> Unit,
    private val getImageFace: (Bitmap?) -> Unit
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

            val tempBitmap = imageProxy.toBitmap()

            detector.process(inputImage)
                .addOnSuccessListener { faces ->
                    if (frameSkipCounter % 60 == 0) {
                        onResult.invoke(faces.toListRect())
                        faces.forEach {
                            val imageBitmap = tempBitmap.cropToBox(it.boundingBox, imageRotation)?.resizedForTfLite()
                            imageBitmap?.let { bitmap ->
                                val results = classifier.classify(bitmap = bitmap, rotation = imageRotation)
                            }
                        }
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
}