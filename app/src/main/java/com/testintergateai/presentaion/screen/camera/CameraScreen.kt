package com.testintergateai.presentaion.screen.camera

import android.util.Log
import androidx.camera.core.Camera
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.CameraController
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.PaintingStyle
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.testintergateai.presentaion.utils.FaceDetectionAnalyzer

@Composable
fun CameraScreen(
    viewModel: CameraViewModel
) {

    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    var bitmap by remember {
        mutableStateOf<ImageBitmap?>(null)
    }

    val analyzer = remember {
        FaceDetectionAnalyzer(
            faceDetected = {
                Log.d("DevLog", "CameraScreen: $it")
            },
            onResult = { result ->
                bitmap = result.asImageBitmap()
//            viewModel.onFaceDetected(result)
            }
        )
    }

    val controller = remember {
        LifecycleCameraController(context).apply {
            setEnabledUseCases(CameraController.IMAGE_ANALYSIS)
            setImageAnalysisAnalyzer(
                ContextCompat.getMainExecutor(context),
                analyzer
            )
        }
    }

    val cameraProviderFuture = remember(context) { ProcessCameraProvider.getInstance(context) }

    val cameraProvider = remember(cameraProviderFuture) { cameraProviderFuture.get() }
    var camera: Camera? by remember { mutableStateOf(null) }

    Column(modifier = Modifier.fillMaxSize()) {

        bitmap?.let {
            Image(bitmap = it, contentDescription = "")
        }


        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = {
                PreviewView(it).apply {
                    this.controller = controller
                    controller.bindToLifecycle(lifecycleOwner)
                }
            })

        /*AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { context ->
                val previewView = PreviewView(context)
                val preview = Preview.Builder().build()
                val selector = CameraSelector.Builder()
                    .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                    .build()
                preview.setSurfaceProvider(previewView.surfaceProvider)
                val imageAnalysis = ImageAnalysis.Builder()
                    .setTargetResolution(
                        Size(
                            previewView.width,
                            previewView.height
                        )
                    )
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_BLOCK_PRODUCER)
                    .build()

                imageAnalysis.setAnalyzer(
                    ContextCompat.getMainExecutor(context),
                    FaceDetectionAnalyzer { result ->
                        viewModel.onFaceDetected(result)
                    }
                )

                try {
                    cameraProvider.unbindAll()
                    camera = cameraProvider.bindToLifecycle(
                        lifecycleOwner,
                        selector,
                        preview,
                        imageAnalysis
                    )
                } catch (e: Exception) {
                    e.printStackTrace()
                }

                previewView
            }
        )*/

        Canvas(modifier = Modifier) {
            val paint = Paint()
            paint.color = Color.Red
            paint.style = PaintingStyle.Stroke
            paint.strokeWidth = 10f
            val rect = Rect(
                left = viewModel.faceBounds.value.left.toFloat(),
                right = viewModel.faceBounds.value.right.toFloat(),
                top = viewModel.faceBounds.value.top.toFloat(),
                bottom = viewModel.faceBounds.value.bottom.toFloat()
            )

            this.drawContext.canvas.drawRect(rect, paint)
        }
    }
}
