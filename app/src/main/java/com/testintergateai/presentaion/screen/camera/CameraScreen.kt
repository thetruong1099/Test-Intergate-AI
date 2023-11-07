package com.testintergateai.presentaion.screen.camera

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Size
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.testintergateai.data.TfLiteLandmarkClassifier
import com.testintergateai.presentaion.utils.FaceDetectionAnalyzer2
import com.testintergateai.presentaion.utils.toListImageBitmap
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream

@Composable
fun CameraScreen(
    viewModel: CameraViewModel
) {
    Content2()
}

@Composable
fun Content2() {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraProviderFuture = remember(context) { ProcessCameraProvider.getInstance(context) }
    var listRect by remember {
        mutableStateOf(listOf(android.graphics.Rect()))
    }
    var imageBitmaps by remember {
        mutableStateOf<List<ImageBitmap?>>(listOf())
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.5f)
        ) {
            AndroidView(
                modifier = Modifier
                    .fillMaxSize(),
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
                                    FaceDetectionAnalyzer2(
                                        context = context,
                                        classifier = TfLiteLandmarkClassifier(
                                            context = context
                                        ),
                                        onResult = {
                                            listRect = it
                                        },
                                        getImageFace = { bitmaps ->
                                            imageBitmaps = bitmaps.toListImageBitmap()
                                            //Log.d("DevLog", "Content2: $bitmaps")
//                                            CoroutineScope(Dispatchers.IO).launch {
//                                                bitmaps.forEach {
//                                                    it?.let {
//                                                        saveToStorage(context = context, bitmap = it)
//                                                    }
//                                                }
//                                            }
                                        }
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

            listRect.forEach { rect ->
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val paint = Paint()
                    paint.color = Color.Red
                    paint.style = PaintingStyle.Stroke
                    paint.strokeWidth = 5f

                    val rect = Rect(
                        left = rect.left.toFloat(),
                        right = rect.right.toFloat(),
                        top = rect.top.toFloat(),
                        bottom = rect.bottom.toFloat()
                    )

                    this.drawContext.canvas.drawRect(rect, paint)
                }
            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            items(items = imageBitmaps) { item ->
                item?.let {
                    Image(
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight(0.5f), bitmap = item, contentDescription = ""
                    )
                }
            }
        }
    }
}

private fun saveToStorage(context: Context, bitmap: Bitmap) {
    val imageName = "${System.currentTimeMillis()}.jpg"
    var fos: OutputStream? = null
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        context.contentResolver?.also { resolver ->
            val contentValues = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, imageName)
                put(MediaStore.MediaColumns.MIME_TYPE, "image/jpg")
                put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
            }
            val imageUri: Uri? = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
            fos = imageUri?.let {
                resolver.openOutputStream(it)
            }
        }
    } else {
        val imageDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
        val image = File(imageDirectory, imageName)
        fos = FileOutputStream(image)
    }

    fos?.use {
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it)
    }
}
