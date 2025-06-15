package com.example.tasktrackr_app.ui.screens.projects

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.tasktrackr_app.R
import com.example.tasktrackr_app.components.*
import com.example.tasktrackr_app.data.remote.response.data.TaskData
import com.example.tasktrackr_app.ui.screens.tasks.AddTaskButton
import com.example.tasktrackr_app.ui.theme.TaskTrackrTheme
import com.example.tasktrackr_app.ui.viewmodel.AuthViewModel
import com.example.tasktrackr_app.ui.viewmodel.ObservationViewModel
import com.example.tasktrackr_app.ui.viewmodel.TaskViewModel
import com.example.tasktrackr_app.ui.viewmodel.UserViewModel
import com.example.tasktrackr_app.ui.viewmodel.ProjectViewModel
import java.util.Locale

@Composable
fun ProjectTasks(
    modifier: Modifier = Modifier,
    navController: NavController,
    userViewModel: UserViewModel,
    taskViewModel: TaskViewModel,
    authViewModel: AuthViewModel,
    observationViewModel: ObservationViewModel,
    projectViewModel: ProjectViewModel,
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

    val fetchedObservations by taskViewModel.observations.collectAsState()
    val taskError by taskViewModel.errorMessage.collectAsState()
    val operationSuccess by taskViewModel.operationSuccess.collectAsState()

    LaunchedEffect(projectId) {
        projectViewModel.clearData()
        projectViewModel.getProjectById(projectId)
        projectViewModel.getTasksForProject(projectId)
    }

    LaunchedEffect(operationSuccess) {
        if (operationSuccess && isTaskFormVisible) {
            isTaskFormVisible = false
            taskToEdit = null
            taskViewModel.clearData()
            projectViewModel.getTasksForProject(projectId)
        }
    }

    LaunchedEffect(taskError) {
        if (taskError != null) {
            taskViewModel.clearData()
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
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
                when {
                    isLoadingProject -> {
                        Text(
                            text = stringResource(R.string.loading_project_details),
                            style = TaskTrackrTheme.typography.header,
                            color = TaskTrackrTheme.colorScheme.primary,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    currentProject != null -> {
                        Text(
                            text = currentProject!!.name,
                            style = TaskTrackrTheme.typography.header,
                            color = TaskTrackrTheme.colorScheme.primary,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    else -> {
                        Text(
                            text = stringResource(R.string.project_not_found),
                            style = TaskTrackrTheme.typography.header,
                            color = TaskTrackrTheme.colorScheme.primary,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
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
                            }
                        )

                        AddTaskButton(
                            onAddTaskClick = {
                                taskViewModel.clearData()
                                taskToEdit = null
                                isTaskFormVisible = true
                            }
                        )

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

        selectedTaskForDetail?.let { task ->
            val taskWithObservations = task.copy(observations = fetchedObservations)

            TaskDetail(
                isVisible = isTaskDetailVisible,
                task = taskWithObservations,
                onDismiss = {
                    isTaskDetailVisible = false
                    selectedTaskForDetail = null
                    taskViewModel.clearData()
                },
                onEditTask = { taskBeingEdited ->
                    taskToEdit = taskBeingEdited
                    isTaskFormVisible = true
                    taskViewModel.clearData()
                }
            )
        }

        TaskForm(
            isVisible = isTaskFormVisible,
            onDismiss = {
                isTaskFormVisible = false
                taskViewModel.clearData()
                taskToEdit = null
            },
            onSave = { data ->
                if (taskToEdit != null) {
                    taskViewModel.updateTask(
                        id = taskToEdit!!.id,
                        title = data.title,
                        description = data.description,
                        status = data.status,
                        startDate = data.startDate,
                        endDate = data.endDate
                    )
                } else {
                    taskViewModel.createTask(
                        title = data.title,
                        description = data.description,
                        projectId = projectId,
                        status = data.status,
                        startDate = data.startDate,
                        endDate = data.endDate
                    )
                }
            },
            isEditMode = taskToEdit != null,
            existingTask = taskToEdit,
            observationViewModel = observationViewModel,
            taskViewModel = taskViewModel
        )
    }
}