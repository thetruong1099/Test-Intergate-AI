package com.testintergateai.presentaion.screen.camera

import android.graphics.Rect
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

class CameraViewModel : ViewModel() {

    private val _face_detected_rect = mutableStateOf(Rect())
    val faceBounds: State<Rect> = _face_detected_rect

    fun onFaceDetected(bounds: Rect){
        _face_detected_rect.value = bounds
    }
}