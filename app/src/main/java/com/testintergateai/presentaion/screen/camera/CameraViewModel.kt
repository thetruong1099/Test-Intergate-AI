package com.testintergateai.presentaion.screen.camera

import android.graphics.Rect
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

class CameraViewModel : ViewModel() {

    private val _face_detected_rect = mutableStateOf(listOf(Rect()))
    val faceBounds: State<List<Rect>> = _face_detected_rect

    fun onFaceDetected(bounds: List<Rect>) {
        _face_detected_rect.value = bounds
    }
}