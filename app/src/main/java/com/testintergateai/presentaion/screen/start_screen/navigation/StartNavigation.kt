package com.testintergateai.presentaion.screen.start_screen.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.testintergateai.presentaion.screen.start_screen.StartScreen
import com.testintergateai.presentaion.utils.Screen

fun NavGraphBuilder.startScreen(
    navigateToMainScreen: () -> Unit
) {
    composable(
        route = Screen.StartScreen.route
    ) {
        StartScreen(
            navigateToMainScreen = { navigateToMainScreen.invoke() }
        )
    }
}