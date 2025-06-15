package com.example.tasktrackr_app.ui.screens.team

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.tasktrackr_app.R
import com.example.tasktrackr_app.components.SideMenu
import com.example.tasktrackr_app.components.TeamMemberCard
import com.example.tasktrackr_app.components.TopBar
import com.example.tasktrackr_app.ui.theme.TaskTrackrTheme
import com.example.tasktrackr_app.ui.viewmodel.AuthViewModel
import com.example.tasktrackr_app.ui.viewmodel.TeamViewModel
import com.example.tasktrackr_app.utils.NotificationHelper
import java.util.Locale

@Composable
fun TeamMembers(
    modifier: Modifier = Modifier,
    navController: NavController,
    teamViewModel: TeamViewModel,
    authViewModel: AuthViewModel,
    onLanguageSelected: (Locale) -> Unit = {},
    teamId: String
) {
    var isSideMenuVisible by remember { mutableStateOf(false) }
    val teamData by teamViewModel.selectedTeam.collectAsState()
    val memberRemoved by teamViewModel.memberRemoved.collectAsState()
    val context = LocalContext.current
    val userData by authViewModel.userData.collectAsState()

    val currentUserEmail = userData?.email
    val isCurrentUserAdmin = teamData?.members?.any {
        it.email == currentUserEmail && it.role == "ADMIN"
    } ?: false

    LaunchedEffect(teamId) {
        teamViewModel.loadTeam(teamId)
    }

    LaunchedEffect(memberRemoved) {
        if (memberRemoved) {
            NotificationHelper.showNotification(context, R.string.team_member_removed_success, true)
            teamViewModel.resetMemberRemoved()
            navController.popBackStack()
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(TaskTrackrTheme.colorScheme.background),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TopBar(
                onMenuClick = { isSideMenuVisible = true }
            )

            Column(
                modifier = Modifier.padding(horizontal = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = stringResource(R.string.team_members),
                    color = TaskTrackrTheme.colorScheme.primary,
                    textAlign = TextAlign.Center,
                    style = TaskTrackrTheme.typography.header,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(24.dp))

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(teamData?.members ?: emptyList()) { member ->
                        val showActions = isCurrentUserAdmin && member.email != currentUserEmail && member.role != "ADMIN"

                        TeamMemberCard(
                            member = member,
                            isAdmin = isCurrentUserAdmin,
                            showActions = showActions,
                            onEditClick = {
                                navController.navigate("edit-team-member/${teamId}/member/${member.id}")
                            },
                            onRemoveClick = {
                                teamViewModel.removeMember(teamId, member.id)
                            }
                        )
                    }
                }
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
