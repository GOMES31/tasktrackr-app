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
import com.example.tasktrackr_app.ui.screens.tasks.MyTasks
import com.example.tasktrackr_app.ui.screens.projects.ProjectsPage
import com.example.tasktrackr_app.ui.theme.LocalizationProvider
import com.example.tasktrackr_app.ui.theme.TaskTrackrTheme
import com.example.tasktrackr_app.ui.viewmodel.AuthViewModel
import com.example.tasktrackr_app.ui.viewmodel.UserViewModel
import com.example.tasktrackr_app.ui.viewmodel.ObservationViewModel
import com.example.tasktrackr_app.ui.viewmodel.ProjectViewModel
import java.util.Locale
import androidx.compose.runtime.CompositionLocalProvider
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.example.tasktrackr_app.ui.screens.team.AddTeamMember
import com.example.tasktrackr_app.ui.screens.team.CreateTeam
import com.example.tasktrackr_app.ui.screens.team.EditTeamMember
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
import androidx.compose.runtime.rememberCoroutineScope
import com.example.tasktrackr_app.ui.screens.projects.ProjectTasks
import com.example.tasktrackr_app.ui.viewmodel.TaskViewModel
import com.example.tasktrackr_app.utils.SessionManager
import kotlinx.coroutines.launch


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
    val taskViewModel: TaskViewModel = viewModel()
    val observationViewModel: ObservationViewModel = viewModel()
    val projectViewModel: ProjectViewModel = viewModel()
    val activity = LocalContext.current as ComponentActivity

    val toastMessage by NotificationHelper.message
    val isToastVisible by NotificationHelper.visible
    val isToastSuccess by NotificationHelper.success

    fun clearAppData() {
        userViewModel.clearData()
        teamViewModel.clearData()
        authViewModel.clearData()
        taskViewModel.clearData()
        observationViewModel.clearData()
        projectViewModel.clearData()
    }

    val scope = rememberCoroutineScope()


    // Register session expiration
    DisposableEffect(Unit) {
        val job = scope.launch {
            SessionManager.sessionEvents.collect { event ->
                when (event) {
                    is SessionManager.SessionEvent.SessionExpired -> {
                        // Clear app data on session expiration
                        clearAppData()

                        // Navigate to sign-in screen
                        navController.navigate("signin") {
                            popUpTo(0) { inclusive = true }
                        }

                    }
                }
            }
        }

        onDispose {
            job.cancel()
        }
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
                                isSideMenuVisible = false
                                navController.navigate("signin") {
                                    popUpTo(0) { inclusive = true }
                                }
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
                                userViewModel = userViewModel,
                                authViewModel = authViewModel,
                                onLanguageSelected = { newLocale -> currentLocale = newLocale }
                            )
                        }

                        composable("edit-user-profile") {
                            EditUserProfile(
                                navController = navController,
                                userViewModel = userViewModel,
                                authViewModel = authViewModel,
                                onLanguageSelected = { newLocale -> currentLocale = newLocale }
                            )
                        }

                        composable("user-teams") {
                            UserTeams(
                                navController = navController,
                                userViewModel = userViewModel,
                                authViewModel = authViewModel,
                                onLanguageSelected = { newLocale -> currentLocale = newLocale }
                            )
                        }

                        composable("create-team") {
                            CreateTeam(
                                navController = navController,
                                teamViewModel = teamViewModel,
                                authViewModel = authViewModel,
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
                                    authViewModel = authViewModel,
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
                                    authViewModel = authViewModel,
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
                                    authViewModel = authViewModel,
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
                                    authViewModel = authViewModel,
                                    onLanguageSelected = { newLocale -> currentLocale = newLocale },
                                    teamId = teamId
                                )
                            }
                        }
                        composable(
                            route = "edit-team-member/{teamId}/member/{memberId}",
                            arguments = listOf(
                                navArgument("teamId") { type = NavType.StringType },
                                navArgument("memberId") { type = NavType.StringType }
                            )
                        ) { backStackEntry ->
                            val teamId = backStackEntry.arguments?.getString("teamId")
                            val memberId = backStackEntry.arguments?.getString("memberId")
                            if (teamId != null && memberId != null) {
                                EditTeamMember(
                                    navController = navController,
                                    teamViewModel = teamViewModel,
                                    authViewModel = authViewModel,
                                    onLanguageSelected = { newLocale -> currentLocale = newLocale },
                                    teamId = teamId,
                                    memberId = memberId.replace("member", "")
                                )
                            }
                        }

                        composable("my-tasks") {
                            MyTasks(
                                navController = navController,
                                authViewModel = authViewModel,
                                userViewModel = userViewModel,
                                taskViewModel = taskViewModel,
                                observationViewModel = observationViewModel,
                                onLanguageSelected = { newLocale -> currentLocale = newLocale }
                            )
                        }

                        composable("projects") {
                            ProjectsPage(
                                navController = navController,
                                authViewModel = authViewModel,
                                projectViewModel = projectViewModel,
                                userViewModel = userViewModel,
                                onLanguageSelected = { newLocale -> currentLocale = newLocale }
                            )
                        }
                        composable(
                            route = "projects/{projectId}/",
                            arguments = listOf(navArgument("projectId") { type = NavType.LongType })
                        ) { backStackEntry ->
                            val projectId = backStackEntry.arguments?.getLong("projectId") ?: -1L
                            ProjectTasks(
                                navController = navController,
                                userViewModel = userViewModel,
                                taskViewModel = taskViewModel,
                                authViewModel = authViewModel,
                                observationViewModel = observationViewModel,
                                projectViewModel = projectViewModel,
                                projectId = projectId,
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
