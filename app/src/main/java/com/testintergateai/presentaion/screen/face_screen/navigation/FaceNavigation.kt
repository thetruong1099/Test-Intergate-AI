package com.testintergateai.presentaion.screen.face_screen.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.testintergateai.presentaion.screen.face_screen.FaceScreen
import com.testintergateai.presentaion.utils.Screen

fun NavGraphBuilder.faceScreen() {
    composable(
        route = Screen.FaceScreen.route
    ) {
        FaceScreen()
    }
}