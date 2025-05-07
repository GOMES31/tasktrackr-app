package com.example.tasktrackr_app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.example.tasktrackr_app.ui.screens.authentication.SignInScreen
import com.example.tasktrackr_app.ui.theme.TaskTrackrTheme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {


            TaskTrackrTheme() {
                SignInScreen()
            }
        }
    }
}
