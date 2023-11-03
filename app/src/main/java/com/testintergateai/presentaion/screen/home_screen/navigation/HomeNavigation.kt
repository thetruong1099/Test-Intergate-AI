package com.testintergateai.presentaion.screen.home_screen.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.testintergateai.presentaion.screen.home_screen.HomeScreen
import com.testintergateai.presentaion.utils.Screen

fun NavGraphBuilder.homeScreen() {
    composable(
        route = Screen.HomeScreen.route
    ) {
        HomeScreen()
    }
}