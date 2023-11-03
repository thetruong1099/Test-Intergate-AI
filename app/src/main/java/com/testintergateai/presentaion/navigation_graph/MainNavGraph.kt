package com.testintergateai.presentaion.navigation_graph

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.testintergateai.presentaion.screen.add_face_screen.navigation.addFaceScreen
import com.testintergateai.presentaion.screen.face_screen.navigation.faceScreen
import com.testintergateai.presentaion.screen.home_screen.navigation.homeScreen
import com.testintergateai.presentaion.utils.Graph
import com.testintergateai.presentaion.utils.Screen

@Composable
fun MainNavGraph(
    navController: NavHostController,
) {
    NavHost(
        navController = navController,
        route = Graph.MAIN,
        startDestination = Screen.HomeScreen.route
    ) {
        homeScreen()

        addFaceScreen()

        faceScreen()
    }
}