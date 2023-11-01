package com.testintergateai.presentaion.utils

import android.annotation.SuppressLint
import android.graphics.Bitmap
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.Face
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions

@SuppressLint("UnsafeOptInUsageError")
class FaceDetectionAnalyzer(
    private val faceDetected: (List<Face>) -> Unit,
    private val onResult: (Bitmap) -> Unit
) : ImageAnalysis.Analyzer {

    private var frameSkipCounter = 0

    override fun analyze(imageProxy: ImageProxy) {

        if (frameSkipCounter % 60 == 0) {
            val rotationDegrees = imageProxy.imageInfo.rotationDegrees
            val bitmap = imageProxy
                .toBitmap()
                .centerCrop(321, 321)
            onResult(bitmap)

            val image = InputImage.fromBitmap(bitmap, 0)
            val options = FaceDetectorOptions.Builder()
                .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
                .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
                .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
                .setMinFaceSize(0.15f)
                .enableTracking()
                .build()
            val detector = FaceDetection.getClient(options)
            val result = detector.process(image)
                .addOnSuccessListener { faces ->
                    // Task completed successfully
                    // ...
                    faceDetected(faces)
                }
                .addOnFailureListener { e ->
                    // Task failed with an exception
                    // ...
                }

            /*val results = classifier.classify(bitmap, rotationDegrees)
            onResults(results)*/
        }
        frameSkipCounter++

        imageProxy.close()

        /*val mediaImage = imageProxy.image
        if (mediaImage != null) {
            val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)

            val options = FaceDetectorOptions.Builder()
                .setContourMode(FaceDetectorOptions.CONTOUR_MODE_ALL)
                .build()

            val detector = FaceDetection.getClient(options)

            detector.process(image)
                .addOnSuccessListener { faces ->
                    val bounds = faces.firstOrNull()?.boundingBox ?: Rect(0, 0, 0, 0)
                    faceDetected(bounds)
                }
                .addOnFailureListener { e ->
                    e.printStackTrace()
                }
                .addOnCompleteListener {
                    mediaImage.close()
                }
        }*/
    }
}