package com.example.tasktrackr_app.ui.screens.tasks

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
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
import com.example.tasktrackr_app.ui.theme.TaskTrackrTheme
import com.example.tasktrackr_app.ui.viewmodel.AuthViewModel
import com.example.tasktrackr_app.ui.viewmodel.ObservationViewModel
import com.example.tasktrackr_app.ui.viewmodel.TaskViewModel
import com.example.tasktrackr_app.ui.viewmodel.UserViewModel
import java.util.Locale


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
fun MyTasks(
    modifier: Modifier = Modifier,
    navController: NavController,
    onLanguageSelected: (Locale) -> Unit = {},
    userViewModel: UserViewModel,
    taskViewModel: TaskViewModel,
    authViewModel: AuthViewModel,
    observationViewModel: ObservationViewModel,
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

    LaunchedEffect(Unit) {
        userViewModel.fetchUserTasks()
    }

    LaunchedEffect(operationSuccess) {
        if (operationSuccess && isTaskFormVisible) {
            isTaskFormVisible = false
            taskToEdit = null
            taskViewModel.clearData()
            userViewModel.fetchUserTasks()
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

                    AddTaskButton(
                        onAddTaskClick = {
                            taskViewModel.clearData()
                            taskToEdit = null
                            isTaskFormVisible = true
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
                        projectId = 1L,
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
