package com.testintergateai.presentaion.screen.start_screen

import android.Manifest
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.testintergateai.R

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun StartScreen(
    navigateToMainScreen: () -> Unit
) {
    val cameraPermissionState: PermissionState = rememberPermissionState(permission = Manifest.permission.CAMERA)

    if (cameraPermissionState.status.isGranted) {
        navigateToMainScreen.invoke()
    } else {
        NoPermissionCompose(onRequestPermission = cameraPermissionState::launchPermissionRequest)
    }
}

@Composable
fun NoPermissionCompose(
    onRequestPermission: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically)
        ) {
            Text(
                textAlign = TextAlign.Center,
                text = stringResource(id = R.string.camera_permission_request)
            )

            Button(onClick = onRequestPermission) {
                Text(text = stringResource(id = R.string.grant_permission))
            }
        }
    }
}