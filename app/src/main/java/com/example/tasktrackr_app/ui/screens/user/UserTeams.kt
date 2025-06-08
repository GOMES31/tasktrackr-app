package com.example.tasktrackr_app.ui.screens.user

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.collectAsState
import com.example.tasktrackr_app.R
import com.example.tasktrackr_app.components.CustomButton
import com.example.tasktrackr_app.components.SideMenu
import com.example.tasktrackr_app.components.TeamCard
import com.example.tasktrackr_app.components.TopBar
import com.example.tasktrackr_app.ui.theme.TaskTrackrTheme
import com.example.tasktrackr_app.ui.viewmodel.AuthViewModel
import com.example.tasktrackr_app.ui.viewmodel.UserViewModel
import java.util.Locale

@Composable
fun UserTeams(
    modifier: Modifier = Modifier,
    navController: NavController,
    userViewModel: UserViewModel,
    authViewModel: AuthViewModel,
    onLanguageSelected: (Locale) -> Unit = {}
) {
    var isSideMenuVisible by remember { mutableStateOf(false) }
    val teams by userViewModel.userTeams.collectAsState()
    val isLoading by userViewModel.isLoadingTeams.collectAsState()

    LaunchedEffect(Unit) {
        userViewModel.fetchUserTeams()
    }

    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(TaskTrackrTheme.colorScheme.background),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TopBar(
                onMenuClick = { isSideMenuVisible = true },
            )

            Column(
                modifier = Modifier.padding(horizontal = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = stringResource(R.string.teams),
                    color = TaskTrackrTheme.colorScheme.primary,
                    textAlign = TextAlign.Center,
                    style = TaskTrackrTheme.typography.header,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(32.dp))

                if (isLoading) {
                    CircularProgressIndicator(color = TaskTrackrTheme.colorScheme.primary)
                } else if (teams.isNullOrEmpty()) {
                    Text(
                        text = stringResource(R.string.no_teams_message),
                        color = TaskTrackrTheme.colorScheme.text,
                        textAlign = TextAlign.Center,
                        style = TaskTrackrTheme.typography.body,
                        modifier = Modifier.fillMaxWidth()
                    )
                } else {
                    LazyColumn {
                        items(teams!!) { team ->
                            TeamCard(
                                name = team.name,
                                members = team.members,
                                imageUrl = team.imageUrl,
                                teamId = team.id.toString(),
                                onTeamClick = { teamId ->
                                    navController.navigate("team-profile/$teamId")
                                }
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                CustomButton(
                    text = stringResource(R.string.create_team),
                    modifier = Modifier.width(180.dp),
                    enabled = true,
                    onClick = { navController.navigate("create-team") }
                )
            }
        }

        SideMenu(
            isVisible = isSideMenuVisible,
            navController = navController,
            onDismiss = { isSideMenuVisible = false },
            onLanguageSelected = onLanguageSelected,
            onSignOut = {
                authViewModel.signOut {
                    isSideMenuVisible = false
                    navController.navigate("signin") {
                        popUpTo(0) { inclusive = true }
                    }
                }
            }
        )
    }
}
