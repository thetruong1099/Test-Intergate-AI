package com.testintergateai.presentaion.screen.main

import android.Manifest
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.testintergateai.presentaion.screen.camera.CameraScreen
import com.testintergateai.presentaion.screen.no_permission.NoPermissionScreen

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun MainScreen() {
    val cameraPermissionState: PermissionState = rememberPermissionState(permission = Manifest.permission.CAMERA)
    MainContent(
        hasPermission = cameraPermissionState.status.isGranted,
        onRequestPermission = cameraPermissionState::launchPermissionRequest
    )
}

@Composable
private fun MainContent(
    hasPermission: Boolean,
    onRequestPermission: () -> Unit
) {
    if (hasPermission) {
        CameraScreen(viewModel = hiltViewModel())
    } else {
        NoPermissionScreen(onRequestPermission)
    }
}