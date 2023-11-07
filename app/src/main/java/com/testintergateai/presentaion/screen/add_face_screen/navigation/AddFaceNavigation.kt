package com.testintergateai.presentaion.screen.add_face_screen.navigation

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.testintergateai.presentaion.screen.add_face_screen.AddFaceScreen
import com.testintergateai.presentaion.utils.Screen

fun NavGraphBuilder.addFaceScreen() {
    composable(
        route = Screen.AddFaceScreen.route
    ) {
        AddFaceScreen()
    }
}