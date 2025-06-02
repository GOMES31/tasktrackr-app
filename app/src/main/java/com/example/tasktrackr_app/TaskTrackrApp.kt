package com.example.tasktrackr_app

import android.annotation.SuppressLint
import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivityResultRegistryOwner
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.tasktrackr_app.ui.screens.authentication.SignIn
import com.example.tasktrackr_app.ui.screens.authentication.SignUp
import com.example.tasktrackr_app.ui.screens.introduction.IntroSlider
import com.example.tasktrackr_app.ui.screens.user.EditUserProfile
import com.example.tasktrackr_app.ui.screens.user.UserProfile
import com.example.tasktrackr_app.ui.theme.LocalizationProvider
import com.example.tasktrackr_app.ui.theme.TaskTrackrTheme
import com.example.tasktrackr_app.ui.viewmodel.AuthViewModel
import com.example.tasktrackr_app.ui.viewmodel.UserViewModel
import java.util.Locale
import androidx.compose.runtime.CompositionLocalProvider
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.example.tasktrackr_app.ui.screens.team.CreateTeam
import com.example.tasktrackr_app.ui.screens.team.TeamProfile
import com.example.tasktrackr_app.ui.screens.user.UserTeams
import com.example.tasktrackr_app.ui.viewmodel.TeamViewModel

@SuppressLint("ContextCastToActivity")
@Composable
fun TaskTrackrApp() {
    var currentLocale by rememberSaveable { mutableStateOf(Locale.getDefault()) }
    val isDarkTheme by rememberSaveable { mutableStateOf(false) }
    val navController = rememberNavController()

    val authViewModel: AuthViewModel = viewModel()
    val userViewModel: UserViewModel = viewModel()
    val teamViewModel: TeamViewModel = viewModel()

    val activity = LocalContext.current as ComponentActivity

    LocalizationProvider(locale = currentLocale) {
        TaskTrackrTheme(isDarkTheme = isDarkTheme) {
            CompositionLocalProvider(
                LocalActivityResultRegistryOwner provides activity
            ) {
                NavHost(
                    navController = navController,
                    startDestination = "intro"
                ) {
                    composable("intro") {
                        IntroSlider(navController)
                    }

                    composable("signin") {
                        SignIn(
                            navController = navController,
                            authViewModel = authViewModel,
                            userViewModel = userViewModel,
                            onLanguageSelected = { newLocale -> currentLocale = newLocale }
                        )
                    }

                    composable("signup") {
                        SignUp(
                            navController = navController,
                            authViewModel = authViewModel,
                            userViewModel = userViewModel,
                            onLanguageSelected = { newLocale -> currentLocale = newLocale }
                        )
                    }

                    composable("user-profile") {
                        UserProfile(
                            navController = navController,
                            viewModel = userViewModel,
                            onLanguageSelected = { newLocale -> currentLocale = newLocale }
                        )
                    }

                    composable("edit-user-profile") {
                        EditUserProfile(
                            navController = navController,
                            viewModel = userViewModel,
                            onLanguageSelected = { newLocale -> currentLocale = newLocale }
                        )
                    }

                    composable("user-teams"){
                        UserTeams(
                            navController = navController,
                            userViewModel = userViewModel,
                            onLanguageSelected = { newLocale -> currentLocale = newLocale }
                        )
                    }

                    composable("create-team") {
                        CreateTeam(
                            navController = navController,
                            teamViewModel = teamViewModel,
                            onLanguageSelected = { newLocale -> currentLocale = newLocale }
                        )
                    }

                    composable(
                        route = "team-profile/{teamId}",
                        arguments = listOf(navArgument("teamId") { type = NavType.StringType })
                    ) { backStackEntry ->
                        val teamId = backStackEntry.arguments?.getString("teamId")
                        if (teamId != null) {
                            TeamProfile(
                                navController = navController,
                                teamViewModel = teamViewModel,
                                onLanguageSelected = { newLocale -> currentLocale = newLocale },
                                teamId = teamId
                            )
                        }
                    }

                }
            }
        }
    }
}
