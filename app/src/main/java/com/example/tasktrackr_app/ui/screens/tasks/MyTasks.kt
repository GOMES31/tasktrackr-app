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
    onLanguageSelected: (java.util.Locale) -> Unit = {}
) {
    var selectedFilter by remember { mutableStateOf(TaskFilter.TODAY) }
    var isSideMenuVisible by remember { mutableStateOf(false) }
    var isTaskDetailVisible by remember { mutableStateOf(false) }
    var isTaskFormVisible by remember { mutableStateOf(false) }
    var taskToEdit by remember { mutableStateOf<Task?>(null) }

    var selectedTask by remember { mutableStateOf<Task?>(null) }

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

                ThreeTabMenu(
                    selectedFilter = selectedFilter,
                    onFilterSelected = { selectedFilter = it },
                    onTaskSelected = { task ->
                        selectedTask = task
                        isTaskDetailVisible = true
                    }
                )

                AddTaskButton(
                    onAddTaskClick = {
                        taskToEdit = null
                        isTaskFormVisible = true
                    }
                )

                TaskSummary()
            }
        }

        SideMenu(
            isVisible = isSideMenuVisible,
            navController = navController,
            onDismiss = { isSideMenuVisible = false },
            onLanguageSelected = onLanguageSelected,
        )

        TaskDetail(
            isVisible = isTaskDetailVisible,
            onDismiss = { isTaskDetailVisible = false },
            taskTitle = selectedTask?.title ?: "",
            taskDescription = selectedTask?.description ?: "",
            location = selectedTask?.location ?: "",
            startDate = selectedTask?.startDate ?: "",
            endDate = selectedTask?.endDate ?: "",
            progress = selectedTask?.progress ?: 0,
            timeSpent = selectedTask?.timeSpent ?: "",
            completionStatus = if (selectedTask?.isCompleted == true) "Completed" else "Not Completed",
            observations = selectedTask?.observations ?: emptyList(),
            onEditTask = {
                taskToEdit = selectedTask
                isTaskDetailVisible = false
                isTaskFormVisible = true
            },
        )

        TaskForm(
            isVisible = isTaskFormVisible,
            onDismiss = {
                isTaskFormVisible = false
                taskToEdit = null
            },
            onSave = { taskFormData ->
                if (taskToEdit != null) {
                    println("Editing task: ${taskFormData.title}")
                } else {
                    println("Adding new task: ${taskFormData.title}")
                }

                isTaskFormVisible = false
                taskToEdit = null
            },
            isEditMode = taskToEdit != null,
            existingTask = taskToEdit?.let { task ->
                TaskFormData(
                    title = task.title,
                    description = task.description,
                    location = task.location,
                    startDate = task.startDate,
                    endDate = task.endDate,
                    progress = task.progress,
                    timeSpent = task.timeSpent,
                    isCompleted = task.isCompleted,
                    observations = task.observations
                )
            }
        )
    }
}