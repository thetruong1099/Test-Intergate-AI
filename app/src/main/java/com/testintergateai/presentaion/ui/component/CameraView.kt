package com.testintergateai.presentaion.ui.component

import android.util.Size
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.testintergateai.domain.model.FaceAnalyzer
import com.testintergateai.presentaion.utils.FaceDetectionAnalyzer

@Composable
fun CameraView(
    modifier: Modifier = Modifier,
    isRecognition: Boolean = false,
    onResult: (List<FaceAnalyzer>) -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraProviderFuture = remember(context) { ProcessCameraProvider.getInstance(context) }
    AndroidView(
        modifier = modifier,
        factory = { context ->
            val previewView = PreviewView(context)
            val executor = ContextCompat.getMainExecutor(context)

            cameraProviderFuture.addListener({
                val cameraProvider = cameraProviderFuture.get()
                val preview = Preview.Builder().build().also {
                    it.setSurfaceProvider(previewView.surfaceProvider)
                }

                val cameraSelector = CameraSelector.Builder()
                    .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                    .build()

                val imageAnalysis = ImageAnalysis.Builder()
                    .setTargetResolution(Size(previewView.width, previewView.height))
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_BLOCK_PRODUCER)
                    .setImageQueueDepth(10)
                    .build()
                    .apply {
                        setAnalyzer(
                            executor,
                            FaceDetectionAnalyzer(
                                context = context,
                                isRecognition = isRecognition,
                                onResult = { onResult.invoke(it) }
                            )
                        )
                    }

                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    lifecycleOwner,
                    cameraSelector,
                    preview,
                    imageAnalysis
                )
            }, executor)

            previewView
        }
    )
}