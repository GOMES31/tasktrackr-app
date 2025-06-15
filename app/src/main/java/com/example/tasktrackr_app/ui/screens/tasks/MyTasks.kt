package com.example.tasktrackr_app.ui.screens.tasks

import android.util.Log

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
import com.example.tasktrackr_app.components.* // Ensure TaskFormData is imported from here
import com.example.tasktrackr_app.data.remote.response.data.TaskData
import com.example.tasktrackr_app.data.remote.response.data.TeamData
import com.example.tasktrackr_app.ui.theme.TaskTrackrTheme
import com.example.tasktrackr_app.ui.viewmodel.AuthViewModel
import com.example.tasktrackr_app.ui.viewmodel.ObservationViewModel
import com.example.tasktrackr_app.ui.viewmodel.TaskViewModel
import com.example.tasktrackr_app.ui.viewmodel.UserViewModel
import com.example.tasktrackr_app.ui.viewmodel.TeamViewModel
import com.example.tasktrackr_app.ui.viewmodel.ProjectViewModel
import java.util.Locale
import java.util.Date

data class TaskFormData(
    val title: String,
    val description: String,
    val status: String,
    val startDate: Date?,
    val endDate: Date?,
    val assigneeIds: List<Long>?
)


@Composable
fun MyTasks(
    modifier: Modifier = Modifier,
    navController: NavController,
    userViewModel: UserViewModel,
    taskViewModel: TaskViewModel,
    authViewModel: AuthViewModel,
    teamViewModel: TeamViewModel,
    observationViewModel: ObservationViewModel,
    projectViewModel: ProjectViewModel,
    onLanguageSelected: (Locale) -> Unit = {}
) {
    var selectedFilter by remember { mutableStateOf(TaskFilter.IN_PROGRESS) }
    var isSideMenuVisible by remember { mutableStateOf(false) }
    var isTaskFormVisible by remember { mutableStateOf(false) }
    var isTaskDetailVisible by remember { mutableStateOf(false) }
    var selectedTaskForDetail by remember { mutableStateOf<TaskData?>(null) }
    var taskToEdit by remember { mutableStateOf<TaskData?>(null) }

    val userTasks by userViewModel.userTasks.collectAsState()
    val isLoadingTasks by userViewModel.isLoadingTasks.collectAsState()
    val fetchedObservations by taskViewModel.observations.collectAsState()
    val taskError by taskViewModel.errorMessage.collectAsState()
    val operationSuccess by taskViewModel.operationSuccess.collectAsState()

    val currentProject by projectViewModel.currentProject.collectAsState()
    val selectedTeam by teamViewModel.selectedTeam.collectAsState()
    var teamDataForForm by remember { mutableStateOf<TeamData?>(null) }

    LaunchedEffect(Unit) {
        userViewModel.fetchUserTasks()
    }

    LaunchedEffect(operationSuccess) {
        if (operationSuccess && isTaskFormVisible) {
            isTaskFormVisible = false
            taskToEdit = null
            taskViewModel.clearData()
            userViewModel.fetchUserTasks()
            projectViewModel.clearData()
            teamViewModel.clearData()
            teamDataForForm = null
        }
    }

    LaunchedEffect(taskError) {
        if (taskError != null) {
            taskViewModel.clearData()
            projectViewModel.clearData()
            teamViewModel.clearData()
            teamDataForForm = null
        }
    }

    LaunchedEffect(taskToEdit) {
        if (taskToEdit != null) {
            val projectId = taskToEdit?.project?.id
            if (projectId != null) {
                Log.d("MyTasks", "Fetching project for task edit: Project ID = $projectId")
                projectViewModel.getProjectById(projectId)
            } else {
                Log.d("MyTasks", "taskToEdit.project.id is null, cannot fetch team data.")
                teamDataForForm = null
            }
        } else {
            projectViewModel.clearData()
            teamViewModel.clearData()
            teamDataForForm = null
        }
    }

    LaunchedEffect(currentProject) {
        currentProject?.team?.id?.let { teamId ->
            Log.d("MyTasks", "Project loaded, fetching team data for Team ID: $teamId")
            // Ensure teamId is converted to String if loadTeam expects String
            teamViewModel.loadTeam(teamId.toString())
        } ?: run {
            Log.d("MyTasks", "currentProject or currentProject.team.id is null. Cannot load team.")
            teamDataForForm = null
        }
    }

    LaunchedEffect(selectedTeam) {
        Log.d("MyTasks", "selectedTeam from TeamViewModel updated: $selectedTeam")
        teamDataForForm = selectedTeam
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
                Text(
                    text = stringResource(R.string.my_tasks),
                    style = TaskTrackrTheme.typography.header,
                    color = TaskTrackrTheme.colorScheme.primary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                if (isLoadingTasks) {
                    CircularProgressIndicator()
                } else {
                    ThreeTabMenu(
                        selectedFilter = selectedFilter,
                        onFilterSelected = { selectedFilter = it },
                        tasks = userTasks ?: emptyList(),
                        onTaskSelected = { task ->
                            selectedTaskForDetail = task
                            isTaskDetailVisible = true
                            taskViewModel.getObservationsForTask(task.id)
                        }
                    )
                    TaskSummary(
                        tasks = userTasks ?: emptyList()
                    )
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
                projectViewModel.clearData()
                teamViewModel.clearData()
                teamDataForForm = null
            },
            // The 'onSave' lambda now receives TaskFormData which includes assigneeIds
            onSave = { data ->
                val assigneeIdLongs: List<Long>? = data.assigneeIds
                    ?.mapNotNull { it.toString().toLongOrNull() }
                    ?.takeIf { it.isNotEmpty() }

                if (taskToEdit != null) {
                    taskViewModel.updateTask(
                        id = taskToEdit!!.id,
                        title = data.title,
                        description = data.description,
                        status = data.status,
                        startDate = data.startDate,
                        endDate = data.endDate,
                        assigneeIds = assigneeIdLongs
                    )
                } else {
                    taskViewModel.createTask(
                        title = data.title,
                        description = data.description,
                        projectId = 1L,
                        status = data.status,
                        startDate = data.startDate,
                        endDate = data.endDate,
                        assigneeIds = assigneeIdLongs
                    )
                }
            },
            isEditMode = taskToEdit != null,
            existingTask = taskToEdit,
            observationViewModel = observationViewModel,
            taskViewModel = taskViewModel,
            authViewModel = authViewModel,
            teamData = teamDataForForm
        )
    }
}
