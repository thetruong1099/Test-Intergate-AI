package com.testintergateai.presentaion.utils

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.Rect
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions

@SuppressLint("UnsafeOptInUsageError")
class FaceDetectionAnalyzer1(
    private val faceDetected: (List<Rect>) -> Unit,
    private val onResult: (Bitmap) -> Unit
) : ImageAnalysis.Analyzer {

    private var frameSkipCounter = 0

    override fun analyze(imageProxy: ImageProxy) {

        if (frameSkipCounter % 60 == 0) {
            val rotationDegrees = imageProxy.imageInfo.rotationDegrees
            val bitmap = imageProxy
                .toBitmap()
            //.centerCrop(321, 321)

            val image = InputImage.fromBitmap(bitmap, rotationDegrees)
            val options = FaceDetectorOptions.Builder()
                .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
                .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
                .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
                .setMinFaceSize(0.2f)
                .enableTracking()
                .build()

            FaceDetection.getClient(options).process(image)
                .addOnSuccessListener { faces ->
                    // Task completed successfully
                    // ...

                    /*if (faces.isNotEmpty()) {
                        onResult(convertFaceObjectToBitmap(face = faces.first(), bitmap = bitmap))
                    }*/


                    val rects = mutableListOf<Rect>()
                    faces.forEach {
                        rects.add(it.boundingBox)
                    }

                    faceDetected(rects)
                }
                .addOnFailureListener { e ->
                    // Task failed with an exception
                    // ...
                }

//            val results = classifier.classify(bitmap, rotationDegrees)
//            onResults(results)
        }
        frameSkipCounter++

        imageProxy.close()
    }
}