package com.example.tasktrackr_app.ui.screens.team

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.tasktrackr_app.R
import com.example.tasktrackr_app.components.*
import com.example.tasktrackr_app.ui.theme.TaskTrackrTheme
import com.example.tasktrackr_app.ui.viewmodel.AuthViewModel
import com.example.tasktrackr_app.ui.viewmodel.TeamViewModel
import com.example.tasktrackr_app.utils.LocalImageStorage
import java.util.Locale

@Composable
fun TeamProfile(
    modifier: Modifier = Modifier,
    navController: NavController,
    teamViewModel: TeamViewModel,
    authViewModel: AuthViewModel,
    onLanguageSelected: (Locale) -> Unit = {},
    teamId: String
) {
    var isSideMenuVisible by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val teamData by teamViewModel.selectedTeam.collectAsState()
    val userData by authViewModel.userData.collectAsState()

    // Check if current user is admin
    val isAdmin = remember(teamData, userData) {
        val currentUserEmail = userData?.email

        teamData?.members?.any { member ->
            member.email == currentUserEmail && member.role == "ADMIN"
        } ?: false
    }

    LaunchedEffect(teamId) {
        teamViewModel.loadTeam(teamId)
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
                modifier = Modifier
                    .padding(horizontal = 32.dp)
                    .verticalScroll(rememberScrollState())
                    .padding(bottom = 80.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = stringResource(R.string.team_profile),
                    color = TaskTrackrTheme.colorScheme.primary,
                    textAlign = TextAlign.Center,
                    style = TaskTrackrTheme.typography.header,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Team Logo
                Box(
                    modifier = Modifier
                        .size(90.dp)
                        .clip(CircleShape)
                        .border(2.dp, TaskTrackrTheme.colorScheme.text, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    teamData?.imageUrl?.let { url ->
                        val imageFile = LocalImageStorage.getImageFile(context, url)
                        if (imageFile != null) {
                            AsyncImage(
                                model = imageFile,
                                contentDescription = null,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Image(
                                painter = painterResource(R.drawable.default_profile),
                                contentDescription = null,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        }
                    } ?: Image(
                        painter = painterResource(R.drawable.default_profile),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Team Name
                Text(
                    text = teamData?.name ?: "",
                    color = TaskTrackrTheme.colorScheme.secondary,
                    style = TaskTrackrTheme.typography.header
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Department
                Text(
                    text = teamData?.department ?: "",
                    color = TaskTrackrTheme.colorScheme.accent,
                    style = TaskTrackrTheme.typography.body
                )

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally)
                ) {
                // Admin Buttons
                if (isAdmin) {
                        CustomButton(
                            text = stringResource(R.string.edit_team),
                            modifier = Modifier
                                .width(110.dp)
                                .height(60.dp),
                            enabled = true,
                            onClick = { navController.navigate("edit-team/$teamId") }
                        )

                        CustomButton(
                            text = stringResource(R.string.add_member),
                            modifier = Modifier
                                .width(110.dp)
                                .height(60.dp),
                            enabled = true,
                            onClick = { navController.navigate("add-team-members/$teamId") }
                        )
                    }

                CustomButton(
                    text = stringResource(R.string.view_team_members),
                    modifier = Modifier
                        .width(110.dp)
                        .height(60.dp),
                    enabled = true,
                    onClick = { navController.navigate("team-members/$teamId") }
                )

                }


                Spacer(modifier = Modifier.height(32.dp))

                HorizontalDivider(
                    thickness = 2.dp,
                    color = TaskTrackrTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = stringResource(R.string.recent_projects),
                    color = TaskTrackrTheme.colorScheme.primary,
                    style = TaskTrackrTheme.typography.subHeader,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                if (teamData?.projects.isNullOrEmpty()) {
                    Text(
                        text = stringResource(R.string.team_no_recent_projects),
                        color = TaskTrackrTheme.colorScheme.text,
                        style = TaskTrackrTheme.typography.body,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                } else {
                    // Placeholder for projects
                    repeat(3) {
                        ActivityCard()
                        Spacer(modifier = Modifier.height(12.dp))
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
