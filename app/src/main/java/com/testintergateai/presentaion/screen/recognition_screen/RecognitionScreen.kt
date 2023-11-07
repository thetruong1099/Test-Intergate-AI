package com.testintergateai.presentaion.screen.recognition_screen

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.testintergateai.domain.model.FaceAnalyzer
import com.testintergateai.domain.tempData.TempData
import com.testintergateai.presentaion.ui.component.CameraView
import com.testintergateai.presentaion.ui.theme.spacing

@Composable
fun RecognitionScreen() {
    var result by remember {
        mutableStateOf<List<FaceAnalyzer>>(listOf())
    }

    Column(modifier = Modifier.fillMaxSize()) {

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(MaterialTheme.spacing.size50)
                .padding(MaterialTheme.spacing.size8)
        ) {
            if (TempData.registered.isEmpty()) {
                Text(text = "Không có data đối chiếu")
            } else {
                LazyRow(

                ) {
                    items(items = result) {
                        it.name?.let { name ->
                            Text(text = name)
                        }
                    }
                }
            }
        }

        Box(modifier = Modifier.fillMaxSize()) {
            CameraView(
                modifier = Modifier.fillMaxSize(),
                isRecognition = true,
                onResult = {
                    Log.d("DevLog", "RecognitionScreen: $it")
                    if (it.isNotEmpty()) {
                        result = it
                    }
                }
            )
        }
    }

}