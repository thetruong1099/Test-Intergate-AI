package com.testintergateai.presentaion.navigation_graph

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.testintergateai.presentaion.screen.main_screen.navigation.mainNavGraph
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
            navigateToMainScreen = {
                navController.apply {
                    popBackStack()
                    navigate(Graph.MAIN)
                }
            }
        )

        mainNavGraph()
    }
}