package com.testintergateai.presentaion.screen.main

import android.Manifest
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.testintergateai.presentaion.screen.camera.CameraScreen
import com.testintergateai.presentaion.screen.no_permission.NoPermissionScreen

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun MainScreen() {
    val permissionStates = rememberMultiplePermissionsState(
        permissions = listOf(
            Manifest.permission.CAMERA,
           // Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
    )
    MainContent(
        hasPermission = permissionStates.allPermissionsGranted,
        onRequestPermission = permissionStates::launchMultiplePermissionRequest
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