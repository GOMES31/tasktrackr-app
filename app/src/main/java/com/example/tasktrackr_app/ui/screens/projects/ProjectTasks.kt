package com.example.tasktrackr_app.ui.screens.projects

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.verticalScroll
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
import com.example.tasktrackr_app.components.SideMenu
import com.example.tasktrackr_app.components.TaskDetail
import com.example.tasktrackr_app.components.TaskFilter
import com.example.tasktrackr_app.components.TaskForm
import com.example.tasktrackr_app.components.TaskSummary
import com.example.tasktrackr_app.components.ThreeTabMenu
import com.example.tasktrackr_app.components.TopBar
import com.example.tasktrackr_app.data.remote.response.data.TaskData
import com.example.tasktrackr_app.data.remote.response.data.TeamData
import com.example.tasktrackr_app.ui.theme.TaskTrackrTheme
import com.example.tasktrackr_app.ui.viewmodel.AuthViewModel
import com.example.tasktrackr_app.ui.viewmodel.ObservationViewModel
import com.example.tasktrackr_app.ui.viewmodel.ProjectViewModel
import com.example.tasktrackr_app.ui.viewmodel.TaskViewModel
import com.example.tasktrackr_app.ui.viewmodel.TeamViewModel
import com.example.tasktrackr_app.ui.viewmodel.UserViewModel
import java.util.Locale

private const val TAG = "ProjectTasks"

@Composable
fun AddTaskButton(
    modifier: Modifier = Modifier,
    onAddTaskClick: () -> Unit = {}
) {
    CustomButton(
        modifier = modifier,
        text = stringResource(R.string.add_task_button),
        icon = painterResource(id = R.drawable.plus),
        enabled = true,
        onClick = onAddTaskClick
    )
}

@Composable
fun ProjectTasks(
    modifier: Modifier = Modifier,
    navController: NavController,
    userViewModel: UserViewModel,
    taskViewModel: TaskViewModel,
    authViewModel: AuthViewModel,
    observationViewModel: ObservationViewModel,
    projectViewModel: ProjectViewModel,
    teamViewModel: TeamViewModel,
    projectId: Long,
    onLanguageSelected: (Locale) -> Unit = {}
) {
    var selectedFilter by remember { mutableStateOf(TaskFilter.IN_PROGRESS) }
    var isSideMenuVisible by remember { mutableStateOf(false) }
    var isTaskFormVisible by remember { mutableStateOf(false) }
    var isTaskDetailVisible by remember { mutableStateOf(false) }
    var selectedTaskForDetail by remember { mutableStateOf<TaskData?>(null) }
    var taskToEdit by remember { mutableStateOf<TaskData?>(null) }

    val currentProject by projectViewModel.currentProject.collectAsState()
    val isLoadingProject by projectViewModel.loading.collectAsState()
    val projectTasks by projectViewModel.tasksForProject.collectAsState()

    // Get team data from TeamViewModel
    val selectedTeam by teamViewModel.selectedTeam.collectAsState()

    val fetchedObservations by taskViewModel.observations.collectAsState()
    val taskError by taskViewModel.errorMessage.collectAsState()
    val operationSuccess by taskViewModel.operationSuccess.collectAsState()
    var teamDataForForm by remember { mutableStateOf<TeamData?>(null) }

    val userData by authViewModel.userData.collectAsState()
    val currentUserEmail = userData?.email

    // Load team data when project is loaded
    LaunchedEffect(currentProject) {
        currentProject?.team?.id?.let { teamId ->
            Log.d(TAG, "Loading team details for team ID: $teamId")
            teamViewModel.loadTeam(teamId.toString())
        }
    }

    val canAddTask = selectedTeam?.members?.any { member ->
        member.email == currentUserEmail && (member.role == "ADMIN" || member.role == "PROJECT_MANAGER")
    } ?: false

    LaunchedEffect(currentUserEmail) {
        Log.d(TAG, "Current user email: $currentUserEmail")
    }

    LaunchedEffect(selectedTeam) {
        selectedTeam?.members?.forEachIndexed { index, member ->
            Log.d(TAG, "Team member $index: Email = ${member.email}, Role = ${member.role}")
        } ?: Log.d(TAG, "No team members found for selected team or selected team is null.")
    }

    LaunchedEffect(canAddTask) {
        Log.d(TAG, "canAddTask value: $canAddTask")
    }

    LaunchedEffect(projectId) {
        projectViewModel.clearData()
        teamViewModel.clearData()
        projectViewModel.getProjectById(projectId)
        projectViewModel.getTasksForProject(projectId)
        Log.d(TAG, "Fetching project and tasks for projectId: $projectId")
    }

    LaunchedEffect(operationSuccess) {
        if (operationSuccess && isTaskFormVisible) {
            isTaskFormVisible = false
            taskToEdit = null
            taskViewModel.clearData()
            projectViewModel.getTasksForProject(projectId)
            Log.d(TAG, "Task operation successful, refreshing tasks.")
        }
    }

    LaunchedEffect(taskError) {
        if (taskError != null) {
            Log.e(TAG, "Task error: $taskError")
            taskViewModel.clearData()
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
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
                when {
                    isLoadingProject -> {
                        Text(
                            text = stringResource(R.string.loading_project_details),
                            style = TaskTrackrTheme.typography.header,
                            color = TaskTrackrTheme.colorScheme.primary,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Log.d(TAG, "Loading project details...")
                    }
                    currentProject != null -> {
                        Text(
                            text = currentProject!!.name,
                            style = TaskTrackrTheme.typography.header,
                            color = TaskTrackrTheme.colorScheme.primary,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Log.d(TAG, "Project loaded: ${currentProject!!.name}")
                    }
                    else -> {
                        Text(
                            text = stringResource(R.string.project_not_found),
                            style = TaskTrackrTheme.typography.header,
                            color = TaskTrackrTheme.colorScheme.primary,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Log.d(TAG, "Project not found.")
                    }
                }

                when {
                    isLoadingProject -> {
                        CircularProgressIndicator()
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            stringResource(R.string.fetching_tasks),
                            style = MaterialTheme.typography.bodyLarge,
                            color = TaskTrackrTheme.colorScheme.text
                        )
                        Log.d(TAG, "Fetching tasks for project...")
                    }
                    currentProject != null -> {
                        ThreeTabMenu(
                            selectedFilter = selectedFilter,
                            onFilterSelected = { selectedFilter = it },
                            tasks = projectTasks,
                            onTaskSelected = { task ->
                                selectedTaskForDetail = task
                                isTaskDetailVisible = true
                                taskViewModel.getObservationsForTask(task.id)
                                Log.d(TAG, "Task selected for detail view: ${task.title}")
                            }
                        )

                        if (canAddTask) {
                            AddTaskButton(
                                onAddTaskClick = {
                                    taskViewModel.clearData()
                                    taskToEdit = null
                                    isTaskFormVisible = true
                                    Log.d(TAG, "Add Task button clicked. Showing task form.")
                                }
                            )
                        } else {
                            Log.d(TAG, "Add Task button not shown because canAddTask is false.")
                        }

                        TaskSummary(
                            tasks = projectTasks
                        )
                    }
                }
            }
        }

        SideMenu(
            isVisible = isSideMenuVisible,
            navController = navController,
            onDismiss = {
                isSideMenuVisible = false
                Log.d(TAG, "Side menu dismissed.")
            },
            onLanguageSelected = onLanguageSelected,
            onSignOut = {
                authViewModel.signOut {
                    isSideMenuVisible = false
                    navController.navigate("signin") {
                        popUpTo(0) { inclusive = true }
                    }
                    Log.d(TAG, "User signed out. Navigating to signin.")
                }
            }

        )

        selectedTaskForDetail?.let { task ->
            val taskWithObservations = task.copy(observations = fetchedObservations)

            TaskDetail(
                isVisible = isTaskDetailVisible,
                task = taskWithObservations,
                onDismiss = {
                    isTaskDetailVisible = false
                    selectedTaskForDetail = null
                    taskViewModel.clearData()
                    Log.d(TAG, "Task detail dismissed.")
                },
                onEditTask = { taskBeingEdited ->
                    taskToEdit = taskBeingEdited
                    isTaskFormVisible = true
                    taskViewModel.clearData()
                    Log.d(TAG, "Editing task: ${taskBeingEdited.title}")
                }
            )
        }

        TaskForm(
            isVisible = isTaskFormVisible,
            onDismiss = {
                isTaskFormVisible = false
                taskViewModel.clearData()
                taskToEdit = null
                Log.d(TAG, "Task form dismissed.")
            },
            onSave = { data ->
                if (taskToEdit != null) {
                    taskViewModel.updateTask(
                        id = taskToEdit!!.id,
                        title = data.title,
                        description = data.description,
                        status = data.status,
                        startDate = data.startDate,
                        endDate = data.endDate,
                        assigneeIds = data.assigneeIds.toList().map { it.toLong() },
                    )
                    Log.d(TAG, "Updating task: ${data.title}")
                } else {
                    taskViewModel.createTask(
                        title = data.title,
                        description = data.description,
                        projectId = projectId,
                        status = data.status,
                        startDate = data.startDate,
                        endDate = data.endDate
                    )
                    Log.d(TAG, "Creating new task: ${data.title} for project $projectId")
                }
            },
            isEditMode = taskToEdit != null,
            existingTask = taskToEdit,
            observationViewModel = observationViewModel,
            taskViewModel = taskViewModel,
            authViewModel = authViewModel,
            teamData = selectedTeam
        )
    }
}