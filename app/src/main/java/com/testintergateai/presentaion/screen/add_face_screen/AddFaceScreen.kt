package com.testintergateai.presentaion.screen.add_face_screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.testintergateai.domain.model.FaceAnalyzer
import com.testintergateai.domain.tempData.TempData
import com.testintergateai.presentaion.ui.component.CameraView
import com.testintergateai.presentaion.ui.theme.spacing

@Composable
fun AddFaceScreen() {

    var faceAnalyzer by remember {
        mutableStateOf<FaceAnalyzer?>(null)
    }

    var isOpenDialog by remember {
        mutableStateOf(false)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.background)
    ) {

        CameraView(
            modifier = Modifier
                .size(width = MaterialTheme.spacing.size250, height = MaterialTheme.spacing.size250)
                .align(Alignment.CenterHorizontally),
            onResult = {
                if (it.isNotEmpty()) {
                    faceAnalyzer = it.first()
                }
            }
        )

        faceAnalyzer?.let {

            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(MaterialTheme.spacing.size100)
            )

            Text(
                text = "Face Detection",
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(MaterialTheme.spacing.size32)
            )

            it.face?.let { face ->
                Image(
                    modifier = Modifier
                        .size(width = MaterialTheme.spacing.size150, height = MaterialTheme.spacing.size150)
                        .align(Alignment.CenterHorizontally),
                    bitmap = face.asImageBitmap(),
                    contentDescription = "",
                    contentScale = ContentScale.FillBounds
                )
            }

            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(MaterialTheme.spacing.size32)
            )

            Button(
                onClick = {
                    isOpenDialog = true
                },
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text(text = "Add Face")
            }
        }

        if (isOpenDialog && faceAnalyzer != null) {
            DialogSaveFace(
                faceAnalyzer = faceAnalyzer!!,
                onDismissRequest = {
                    isOpenDialog = false
                },
                onConfirmation = { name, faceAnalyzer ->
                    TempData.registered[name] = faceAnalyzer
                    isOpenDialog = false
                }
            )
        }

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DialogSaveFace(
    faceAnalyzer: FaceAnalyzer,
    onDismissRequest: () -> Unit,
    onConfirmation: (String, FaceAnalyzer) -> Unit,
) {
    var name by remember {
        mutableStateOf("")
    }

    Dialog(onDismissRequest = { onDismissRequest.invoke() }) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(MaterialTheme.spacing.size375)
                .padding(MaterialTheme.spacing.size16),
            shape = RoundedCornerShape(MaterialTheme.spacing.size16),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                faceAnalyzer.face?.let {
                    Image(
                        bitmap = it.asImageBitmap(),
                        contentDescription = "",
                        contentScale = ContentScale.FillHeight,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp),
                    )
                }

                Text(
                    text = "Name",
                    modifier = Modifier.padding(MaterialTheme.spacing.size16),
                )

                TextField(
                    value = name,
                    onValueChange = {
                        name = it
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(MaterialTheme.spacing.size16)
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                ) {
                    TextButton(
                        onClick = { onDismissRequest() },
                        modifier = Modifier.padding(MaterialTheme.spacing.size8),
                    ) {
                        Text("Dismiss")
                    }

                    TextButton(
                        onClick = { onConfirmation.invoke(name, faceAnalyzer) },
                        modifier = Modifier.padding(MaterialTheme.spacing.size8),
                    ) {
                        Text("Confirm")
                    }
                }
            }
        }
    }
}