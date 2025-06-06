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
import com.example.tasktrackr_app.ui.screens.tasks.MyTasks
import com.example.tasktrackr_app.ui.theme.LocalizationProvider
import com.example.tasktrackr_app.ui.theme.TaskTrackrTheme
import com.example.tasktrackr_app.ui.viewmodel.AuthViewModel
import com.example.tasktrackr_app.ui.viewmodel.UserViewModel

import java.util.Locale
import androidx.compose.runtime.CompositionLocalProvider
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.example.tasktrackr_app.ui.screens.team.AddTeamMember
import com.example.tasktrackr_app.ui.screens.team.CreateTeam
import com.example.tasktrackr_app.ui.screens.team.EditTeamProfile
import com.example.tasktrackr_app.ui.screens.team.TeamMembers
import com.example.tasktrackr_app.ui.screens.team.TeamProfile
import com.example.tasktrackr_app.ui.screens.user.UserTeams
import com.example.tasktrackr_app.ui.viewmodel.TeamViewModel
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import com.example.tasktrackr_app.components.CustomToast
import com.example.tasktrackr_app.utils.NotificationHelper
import androidx.compose.ui.Alignment
import com.example.tasktrackr_app.components.SideMenu
import com.example.tasktrackr_app.ui.screens.user.EditUserProfile
import com.example.tasktrackr_app.ui.screens.user.UserProfile

@SuppressLint("ContextCastToActivity")
@Composable
fun TaskTrackrApp() {
    var isSideMenuVisible by rememberSaveable { mutableStateOf(false) }
    var currentLocale by rememberSaveable { mutableStateOf(Locale.getDefault()) }
    val isDarkTheme by rememberSaveable { mutableStateOf(false) }
    val navController = rememberNavController()

    val authViewModel: AuthViewModel = viewModel()
    val userViewModel: UserViewModel = viewModel()
    val teamViewModel: TeamViewModel = viewModel()

    val activity = LocalContext.current as ComponentActivity

    val toastMessage by NotificationHelper.message
    val isToastVisible by NotificationHelper.visible
    val isToastSuccess by NotificationHelper.success

    fun clearAppData() {
        userViewModel.clearData()
        teamViewModel.clearData()
    }

    LocalizationProvider(locale = currentLocale) {
        TaskTrackrTheme(isDarkTheme = isDarkTheme) {
            CompositionLocalProvider(
                LocalActivityResultRegistryOwner provides activity
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    SideMenu(
                        isVisible = isSideMenuVisible,
                        navController = navController,
                        onDismiss = { isSideMenuVisible = false },
                        onLanguageSelected = { newLocale -> currentLocale = newLocale },
                        onSignOut = {
                            authViewModel.signOut {
                                clearAppData()
                                navController.navigate("signin") {
                                    popUpTo(navController.graph.startDestinationId) { inclusive = true }
                                }
                                NotificationHelper.showNotification(activity, R.string.session_expired, false)
                            }
                        }
                    )
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

                        composable(
                            route = "edit-team/{teamId}",
                            arguments = listOf(navArgument("teamId") { type = NavType.StringType })
                        ) { backStackEntry ->
                            val teamId = backStackEntry.arguments?.getString("teamId")
                            if (teamId != null) {
                                EditTeamProfile(
                                    navController = navController,
                                    teamViewModel = teamViewModel,
                                    onLanguageSelected = { newLocale -> currentLocale = newLocale },
                                    teamId = teamId
                                )
                            }
                        }

                        composable(
                            route = "add-team-members/{teamId}",
                            arguments = listOf(navArgument("teamId") { type = NavType.StringType })
                        ) { backStackEntry ->
                            val teamId = backStackEntry.arguments?.getString("teamId")
                            if (teamId != null) {
                                AddTeamMember(
                                    navController = navController,
                                    teamViewModel = teamViewModel,
                                    onLanguageSelected = { newLocale -> currentLocale = newLocale },
                                    teamId = teamId
                                )
                            }
                        }

                        composable(
                            route = "team-members/{teamId}",
                            arguments = listOf(navArgument("teamId") { type = NavType.StringType })
                        ) { backStackEntry ->
                            val teamId = backStackEntry.arguments?.getString("teamId")
                            if (teamId != null) {
                                TeamMembers(
                                    navController = navController,
                                    teamViewModel = teamViewModel,
                                    onLanguageSelected = { newLocale -> currentLocale = newLocale },
                                    teamId = teamId
                                )
                            }
                        }

                        composable("my-tasks") {
                            MyTasks(
                                navController = navController,
                                onLanguageSelected = { newLocale -> currentLocale = newLocale }
                            )
                        }

                    }

                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.BottomCenter
                    ) {
                        CustomToast(
                            message = toastMessage,
                            isVisible = isToastVisible,
                            isSuccess = isToastSuccess,
                            onDismiss = { NotificationHelper.hideToast() }
                        )
                    }
                }
            }
        }
    }
}
