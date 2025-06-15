package com.example.tasktrackr_app.ui.screens.team

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
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
import com.example.tasktrackr_app.components.CustomButton
import com.example.tasktrackr_app.components.SideMenu
import com.example.tasktrackr_app.components.TopBar
import com.example.tasktrackr_app.ui.theme.TaskTrackrTheme
import com.example.tasktrackr_app.ui.viewmodel.AuthViewModel
import com.example.tasktrackr_app.ui.viewmodel.TeamViewModel
import com.example.tasktrackr_app.utils.NotificationHelper
import java.util.Locale

@Composable
fun EditTeamMember(
    modifier: Modifier = Modifier,
    navController: NavController,
    teamViewModel: TeamViewModel,
    authViewModel: AuthViewModel,
    onLanguageSelected: (Locale) -> Unit = {},
    teamId: String,
    memberId: String
) {
    var selectedRole by remember { mutableStateOf("MEMBER") }
    var isSideMenuVisible by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val updateMemberSuccess by teamViewModel.updateMemberSuccess.collectAsState()
    val errorCode by teamViewModel.errorCode.collectAsState()
    val selectedTeam by teamViewModel.selectedTeam.collectAsState()

    val roles = listOf(
        Pair("Project Manager", "PROJECT_MANAGER"),
        Pair("Member", "MEMBER")
    )

    LaunchedEffect(key1 = Unit) {
        teamViewModel.loadTeam(teamId)
    }

    LaunchedEffect(selectedTeam) {
        selectedTeam?.members?.find { it.id == memberId.toLong() }?.let { member ->
            selectedRole = member.role
        }
    }

    LaunchedEffect(updateMemberSuccess) {
        if (updateMemberSuccess) {
            NotificationHelper.showNotification(context, R.string.team_member_role_update_success, true)
            teamViewModel.resetUpdateMemberSuccess()
            navController.popBackStack()
        }
    }

    LaunchedEffect(errorCode) {
        if (errorCode != null) {
            NotificationHelper.showNotification(context, R.string.team_member_role_update_error, false)
            teamViewModel.clearData()
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(TaskTrackrTheme.colorScheme.background),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TopBar(onMenuClick = { isSideMenuVisible = true })

        Column(
            modifier = Modifier
                .padding(horizontal = 32.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = stringResource(R.string.edit_member),
                color = TaskTrackrTheme.colorScheme.primary,
                textAlign = TextAlign.Center,
                style = TaskTrackrTheme.typography.header,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(40.dp))

            selectedTeam?.members?.find { it.id == memberId.toLong() }?.let { member ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            color = TaskTrackrTheme.colorScheme.inputBackground,
                            shape = RoundedCornerShape(8.dp)
                        )
                        .border(
                            BorderStroke(1.dp, TaskTrackrTheme.colorScheme.primary),
                            shape = RoundedCornerShape(8.dp)
                        )
                        .padding(16.dp)
                ) {
                    Column {
                        Text(
                            text = member.name,
                            color = TaskTrackrTheme.colorScheme.secondary,
                            style = TaskTrackrTheme.typography.smallTitles,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = member.email,
                            color = TaskTrackrTheme.colorScheme.text,
                            style = TaskTrackrTheme.typography.body,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = stringResource(R.string.team_role),
                color = TaskTrackrTheme.colorScheme.primary,
                style = TaskTrackrTheme.typography.body,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            )

            var dropdownMenuExpanded by remember { mutableStateOf(false) }

            Box(
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(
                            BorderStroke(1.dp, TaskTrackrTheme.colorScheme.primary),
                            shape = RoundedCornerShape(8.dp)
                        )
                        .background(
                            color = TaskTrackrTheme.colorScheme.inputBackground,
                            shape = RoundedCornerShape(8.dp)
                        )
                        .clickable { dropdownMenuExpanded = true }
                        .padding(16.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Text(
                        text = roles.find { it.second == selectedRole }?.first ?: "Member",
                        style = TaskTrackrTheme.typography.body,
                        color = TaskTrackrTheme.colorScheme.text
                    )
                }

                DropdownMenu(
                    expanded = dropdownMenuExpanded,
                    onDismissRequest = { dropdownMenuExpanded = false },
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .background(TaskTrackrTheme.colorScheme.inputBackground)
                ) {
                    roles.forEach { role ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(
                                    BorderStroke(1.dp, TaskTrackrTheme.colorScheme.primary),
                                    shape = RoundedCornerShape(0.dp)
                                )
                        ) {
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = role.first,
                                        color = TaskTrackrTheme.colorScheme.text
                                    )
                                },
                                onClick = {
                                    selectedRole = role.second
                                    dropdownMenuExpanded = false
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(TaskTrackrTheme.colorScheme.inputBackground)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            CustomButton(
                text = stringResource(R.string.confirm_changes),
                modifier = Modifier.width(200.dp),
                enabled = true,
                onClick = {
                    teamViewModel.updateMember(
                        teamId = teamId,
                        memberId = memberId.toLong(),
                        role = selectedRole
                    )
                }
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
