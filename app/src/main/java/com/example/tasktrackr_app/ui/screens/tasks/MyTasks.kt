package com.example.tasktrackr_app.ui.screens.tasks

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
import com.example.tasktrackr_app.ui.theme.TaskTrackrTheme
import com.example.tasktrackr_app.ui.viewmodel.TaskViewModel
import com.example.tasktrackr_app.ui.viewmodel.UserViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import java.util.Locale
import java.util.Date

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
    userViewModel: UserViewModel = viewModel(),
    taskViewModel: TaskViewModel = viewModel()
) {
    var selectedFilter by remember { mutableStateOf(TaskFilter.IN_PROGRESS) }
    var isSideMenuVisible by remember { mutableStateOf(false) }
    var isTaskDetailVisible by remember { mutableStateOf(false) }
    var isTaskFormVisible by remember { mutableStateOf(false) }


    val userTasks by userViewModel.userTasks.collectAsState()
    val isLoadingTasks by userViewModel.isLoadingTasks.collectAsState()

    val currentTask by taskViewModel.currentTask.collectAsState()


    var taskToEdit by remember { mutableStateOf<Task?>(null) }
    LaunchedEffect(currentTask) {
        taskToEdit = currentTask?.toTask()
    }

    var selectedTask by remember { mutableStateOf<Task?>(null) }

    LaunchedEffect(Unit) {
        userViewModel.fetchUserTasks()
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
                    val tasksToDisplay = userTasks?.map { it.toTask() } ?: emptyList()

                    ThreeTabMenu(
                        selectedFilter = selectedFilter,
                        onFilterSelected = { selectedFilter = it },
                        tasks = tasksToDisplay,
                        onTaskSelected = { task ->
                            selectedTask = task
                            isTaskDetailVisible = true
                        }
                    )
                }

                AddTaskButton(
                    onAddTaskClick = {
                        taskViewModel.clearCurrentTask()
                        isTaskFormVisible = true
                    }
                )
                /*
                TaskSummary()
                 */
            }
        }

        SideMenu(
            isVisible = isSideMenuVisible,
            navController = navController,
            onDismiss = { isSideMenuVisible = false },
            onLanguageSelected = onLanguageSelected,
        )

        /*
        TaskDetail(
            isVisible = isTaskDetailVisible,
            onDismiss = { isTaskDetailVisible = false },
            taskTitle = selectedTask?.title ?: "",
            taskDescription = selectedTask?.description ?: "",
            startDate = selectedTask?.startDate ?: "",
            endDate = selectedTask?.endDate ?: "",
            timeSpent = selectedTask?.timeSpent ?: "",
            completionStatus = if (selectedTask?.isCompleted == true) "Completed" else "Not Completed",
            observations = selectedTask?.observations ?: emptyList(),
            onEditTask = {
                selectedTask?.id?.let { taskId ->
                    taskViewModel.getTaskById(taskId)
                }
                isTaskDetailVisible = false
                isTaskFormVisible = true
            },
        )

        TaskForm(
            isVisible = isTaskFormVisible,
            onDismiss = {
                isTaskFormVisible = false
                taskViewModel.clearCurrentTask()
            },
            onSave = { taskFormData ->
                if (taskToEdit != null) {
                    taskToEdit?.id?.let { id ->
                        val status = if (taskFormData.isCompleted) "FINISHED" else "PENDING"

                        taskViewModel.updateTask(
                            id = id,
                            title = taskFormData.title,
                            description = taskFormData.description,
                            status = status,
                            startDate = Date(),
                            endDate = Date(),
                            assigneeIds = null
                        )
                    }
                } else {
                    val status = if (taskFormData.isCompleted) "FINISHED" else "PENDING"

                    taskViewModel.createTask(
                        title = taskFormData.title,
                        description = taskFormData.description,
                        projectId = 1L,
                        startDate = Date(),
                        endDate = Date(),
                        status = status,
                        assigneeIds = null
                    )
                }

                isTaskFormVisible = false
                taskViewModel.clearCurrentTask()
                userViewModel.fetchUserTasks()
            },
            isEditMode = taskToEdit != null,
            existingTask = taskToEdit?.let { task ->
                TaskFormData(
                    title = task.title,
                    description = task.description ?: "",
                    startDate = task.startDate ?: "",
                    endDate = task.endDate ?: "",
                    timeSpent = task.timeSpent,
                    isCompleted = task.isCompleted,
                    observations = task.observations
                )
            }
        )
        */
    }
}