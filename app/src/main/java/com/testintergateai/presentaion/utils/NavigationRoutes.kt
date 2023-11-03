package com.testintergateai.presentaion.utils

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Face
import androidx.compose.material.icons.outlined.Home
import androidx.compose.ui.graphics.vector.ImageVector

object Graph {
    const val ROOT = "root_graph"
    const val MAIN = "main_graph"
}

sealed class Screen(
    val route: String,
    val tile: String? = null,
    val selectedIcon: ImageVector? = null,
    val unselectedIcon: ImageVector? = null,
    var routePath: String? = null,
    var clearBackStack: Boolean = false,
) {
    object StartScreen : Screen("start_screen")
    object HomeScreen : Screen(
        route = "home_screen",
        tile = "Home",
        selectedIcon = Icons.Default.Home,
        unselectedIcon = Icons.Outlined.Home
    )

    object AddFaceScreen : Screen(
        route = "add_face_screen",
        tile = "Add Face",
        selectedIcon = Icons.Default.AddCircle,
        unselectedIcon = Icons.Outlined.Add
    )

    object FaceScreen : Screen(
        route = "face_screen",
        tile = "Face",
        selectedIcon = Icons.Default.Face,
        unselectedIcon = Icons.Outlined.Face
    )
}