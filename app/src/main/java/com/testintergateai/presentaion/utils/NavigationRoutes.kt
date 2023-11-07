package com.testintergateai.presentaion.utils

object Graph {
    const val ROOT = "root_graph"
}

sealed class Screen(
    val route: String
) {
    object StartScreen : Screen("start_screen")
    object RecognitionScreen : Screen(
        route = "Recognition_screen"
    )

    object AddFaceScreen : Screen(
        route = "add_face_screen"
    )

    object FaceScreen : Screen(
        route = "face_screen"
    )
}