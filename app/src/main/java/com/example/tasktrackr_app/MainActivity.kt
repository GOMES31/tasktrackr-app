package com.example.tasktrackr_app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.tasktrackr_app.ui.screens.authentication.SignIn
import com.example.tasktrackr_app.ui.screens.authentication.SignUp
import com.example.tasktrackr_app.ui.screens.profile.UserProfile
import com.example.tasktrackr_app.ui.screens.introduction.IntroSlider
import com.example.tasktrackr_app.ui.screens.profile.EditUserProfile
import com.example.tasktrackr_app.ui.theme.LocalizationProvider
import com.example.tasktrackr_app.ui.theme.TaskTrackrTheme
import com.example.tasktrackr_app.ui.viewmodel.AuthViewModel
import com.example.tasktrackr_app.ui.viewmodel.UserViewModel
import com.example.tasktrackr_app.utils.LocalImageStorage
import java.util.Locale

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        LocalImageStorage.init(this)

        setContent {
            TaskTrackrApp()
        }
    }
}

