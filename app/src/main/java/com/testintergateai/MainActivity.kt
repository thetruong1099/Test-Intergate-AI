package com.testintergateai

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.testintergateai.presentaion.screen.main.MainScreen
import com.testintergateai.presentaion.ui.theme.TestIntergateAITheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            TestIntergateAITheme {
                // A surface container using the 'background' color from the theme
                MainScreen()
            }
        }
    }
}