package com.example.tasktrackr_app.ui.screens.projects

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.tasktrackr_app.R
import com.example.tasktrackr_app.components.CustomButton
import com.example.tasktrackr_app.components.ProjectMenu
import com.example.tasktrackr_app.components.SideMenu
import com.example.tasktrackr_app.components.TopBar
import com.example.tasktrackr_app.ui.theme.TaskTrackrTheme
import com.example.tasktrackr_app.ui.viewmodel.AuthViewModel
import com.example.tasktrackr_app.ui.viewmodel.UserViewModel
import com.example.tasktrackr_app.ui.viewmodel.ProjectViewModel
import java.util.Locale

@Composable
fun AddProjectButton(
    modifier: Modifier = Modifier,
    onAddProjectClick: () -> Unit = {}
) {
    CustomButton(
        modifier = modifier,
        text = stringResource(R.string.add_project_button),
        icon = painterResource(id = R.drawable.plus),
        enabled = true,
        onClick = onAddProjectClick
    )
}

@Composable
fun ProjectsPage(
    navController: NavController,
    userViewModel: UserViewModel,
    projectViewModel: ProjectViewModel,
    authViewModel: AuthViewModel,
    onLanguageSelected: (Locale) -> Unit = {}
) {
    val userProjects by userViewModel.userProjects.collectAsState()
    val isLoadingProjects by userViewModel.isLoadingProjects.collectAsState()
    val userTeams by userViewModel.userTeams.collectAsState()
    val userData by authViewModel.userData.collectAsState()

    val adminTeams = remember(userTeams, userData) {
        userTeams?.filter { teamData ->
            val currentUserEmail = userData?.email
            teamData.members?.any { member ->
                member.email == currentUserEmail && member.role == "ADMIN"
            } ?: false
        } ?: emptyList()
    }

    val currentUserData = userData

    var isSideMenuVisible by remember { mutableStateOf(false) }
    var isProjectFormVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        userViewModel.fetchUserProjects()
        userViewModel.fetchUserTeams()
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(TaskTrackrTheme.colorScheme.background),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TopBar(onMenuClick = { isSideMenuVisible = true })

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = stringResource(R.string.my_projects),
                    style = TaskTrackrTheme.typography.header,
                    color = TaskTrackrTheme.colorScheme.primary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                if (isLoadingProjects) {
                    CircularProgressIndicator(modifier = Modifier.size(50.dp))
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        stringResource(R.string.loading_project_details),
                        style = MaterialTheme.typography.bodyLarge,
                        color = TaskTrackrTheme.colorScheme.text
                    )
                } else {
                    ProjectMenu(
                        projects = userProjects ?: emptyList(),
                        onProjectSelected = { project ->
                            navController.navigate("projects/${project.id}/")
                        }
                    )
                    AddProjectButton(
                        modifier = Modifier
                            .padding(horizontal = 16.dp),
                        onAddProjectClick = {
                            isProjectFormVisible = true
                        }
                    )

                }
            }
        }

        // Project Form Dialog
        ProjectForm(
            isVisible = isProjectFormVisible,
            adminTeams = adminTeams,
            currentUserData = currentUserData,
            onDismiss = {
                isProjectFormVisible = false
            },
            onSave = { projectFormData ->
                projectViewModel.createProject(
                    name = projectFormData.name,
                    description = projectFormData.description,
                    teamId = projectFormData.teamId,
                    startDate = projectFormData.startDate,
                    endDate = projectFormData.endDate,
                    status = projectFormData.status
                )
                isProjectFormVisible = false
                userViewModel.fetchUserProjects()
            }
        )

        SideMenu(
            isVisible = isSideMenuVisible,
            navController = navController,
            onDismiss = {
                isSideMenuVisible = false
            },
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