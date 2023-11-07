package com.testintergateai.presentaion.screen.start_screen

import android.Manifest
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.testintergateai.R
import com.testintergateai.presentaion.ui.theme.spacing

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun StartScreen(
    navigateToRecognitionScreen: () -> Unit,
    navigateToAddFaceScreen: () -> Unit
) {
    val permissionStates = rememberMultiplePermissionsState(
        permissions = listOf(
            Manifest.permission.CAMERA,
            // Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
    )

    if (permissionStates.allPermissionsGranted) {
        Content(
            navigateToRecognitionScreen = { navigateToRecognitionScreen.invoke() },
            navigateToAddFaceScreen = { navigateToAddFaceScreen.invoke() })
    } else {
        NoPermissionCompose(onRequestPermission = permissionStates::launchMultiplePermissionRequest)
    }
}

@Composable
private fun Content(
    navigateToRecognitionScreen: () -> Unit,
    navigateToAddFaceScreen: () -> Unit
) {
    Column {

        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .height(MaterialTheme.spacing.size32)
        )

        Button(
            modifier = Modifier.align(Alignment.CenterHorizontally),
            onClick = { navigateToRecognitionScreen.invoke() }) {
            Text(text = "Recognition")
        }

        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .height(MaterialTheme.spacing.size32)
        )

        Button(
            modifier = Modifier.align(Alignment.CenterHorizontally),
            onClick = { navigateToAddFaceScreen.invoke() }) {
            Text(text = "Add Face")
        }
    }
}

@Composable
private fun NoPermissionCompose(
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