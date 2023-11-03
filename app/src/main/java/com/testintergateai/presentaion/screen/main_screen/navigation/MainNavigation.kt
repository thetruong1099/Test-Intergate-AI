package com.testintergateai.presentaion.screen.main_screen.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.testintergateai.presentaion.navigation_graph.MainNavGraph
import com.testintergateai.presentaion.screen.main_screen.MainScreen
import com.testintergateai.presentaion.ui.component.MainBottomBarComponent
import com.testintergateai.presentaion.utils.Graph
import com.testintergateai.presentaion.utils.Screen

fun NavGraphBuilder.mainNavGraph() {
    composable(
        route = Graph.MAIN
    ) {
        val navController = rememberNavController()
        val navBackStackEntry by navController.currentBackStackEntryAsState()

        val bottomBar: @Composable () -> Unit = {
            MainBottomBarComponent(
                screens = listOf(
                    Screen.HomeScreen,
                    Screen.AddFaceScreen,
                    Screen.FaceScreen
                ),
                onNavigateTo = {
                    navController.navigate(it.route)
                },
                currentDestination = navBackStackEntry?.destination
            )
        }

        val mainNavGraph: @Composable () -> Unit = {
            MainNavGraph(navController = navController)
        }

        MainScreen(
            mainNavGraph = mainNavGraph,
            bottomBar = bottomBar
        )
    }
}