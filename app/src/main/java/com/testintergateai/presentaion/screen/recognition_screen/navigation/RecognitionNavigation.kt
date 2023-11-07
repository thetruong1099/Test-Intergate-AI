package com.testintergateai.presentaion.screen.recognition_screen.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.testintergateai.presentaion.screen.recognition_screen.RecognitionScreen
import com.testintergateai.presentaion.utils.Screen

fun NavGraphBuilder.recognitionScreen() {
    composable(
        route = Screen.RecognitionScreen.route
    ) {
        RecognitionScreen()
    }
}