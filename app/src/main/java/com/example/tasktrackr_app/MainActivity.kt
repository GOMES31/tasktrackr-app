package com.example.tasktrackr_app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import java.util.Locale
import com.example.tasktrackr_app.ui.screens.authentication.SignInScreen
import com.example.tasktrackr_app.ui.theme.LocalizationProvider
import com.example.tasktrackr_app.ui.theme.TaskTrackrTheme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {

            var currentLocale by remember { mutableStateOf(Locale.getDefault()) }

            LocalizationProvider(locale = currentLocale) {
                TaskTrackrTheme(isDarkTheme = false) {
                    SignInScreen { newLocale ->
                        currentLocale = newLocale
                    }
                }
            }
        }
    }
}
