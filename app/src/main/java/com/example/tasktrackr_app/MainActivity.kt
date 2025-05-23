package com.example.tasktrackr_app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.tasktrackr_app.ui.screens.authentication.SignIn
import com.example.tasktrackr_app.ui.screens.authentication.SignUp
import com.example.tasktrackr_app.ui.screens.profile.ProfileScreen
import com.example.tasktrackr_app.ui.screens.introduction.IntroSlider
import com.example.tasktrackr_app.ui.theme.LocalizationProvider
import com.example.tasktrackr_app.ui.theme.TaskTrackrTheme
import java.util.Locale

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            var currentLocale by remember { mutableStateOf(Locale.getDefault()) }

            val navController = rememberNavController()

            LocalizationProvider(locale = currentLocale) {
                TaskTrackrTheme(isDarkTheme = false) {
                    NavHost(navController = navController, startDestination = "intro") {
                        composable("intro") {
                            IntroSlider(navController = navController)
                        }
                        composable("signin") {
                            SignIn(
                                navController = navController,
                                onLanguageSelected = { newLocale ->
                                    currentLocale = newLocale
                                }
                            )
                        }
                        composable("signup") {
                            SignUp(
                                navController = navController,
                                onLanguageSelected = { newLocale ->
                                    currentLocale = newLocale
                                }
                            )
                        }
                        composable("profile"){
                            ProfileScreen(
                                navController = navController,
                                onLanguageSelected = { newLocale ->
                                    currentLocale = newLocale
                                }
                            )
                    }
                }
            }
        }
    }
}
