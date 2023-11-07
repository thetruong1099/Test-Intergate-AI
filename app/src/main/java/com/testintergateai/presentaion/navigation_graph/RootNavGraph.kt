package com.testintergateai.presentaion.navigation_graph

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.testintergateai.presentaion.screen.add_face_screen.navigation.addFaceScreen
import com.testintergateai.presentaion.screen.recognition_screen.navigation.recognitionScreen
import com.testintergateai.presentaion.screen.start_screen.navigation.startScreen
import com.testintergateai.presentaion.utils.Graph
import com.testintergateai.presentaion.utils.Screen

@Composable
fun RootNavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        route = Graph.ROOT,
        startDestination = Screen.StartScreen.route
    ) {
        startScreen(
            navigateToRecognitionScreen = {
                navController.apply {
                    navigate(Screen.RecognitionScreen.route)
                }
            },
            navigateToAddFaceScreen = {
                navController.apply {
                    navigate(Screen.AddFaceScreen.route)
                }
            }
        )

        recognitionScreen()

        addFaceScreen()
    }
}