package com.example.tasktrackr_app.components

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.tasktrackr_app.R
import com.example.tasktrackr_app.ui.theme.TaskTrackrTheme
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

// Updated TaskFilter enum to match new tabs
enum class TaskFilter {
    IN_PROGRESS, PENDING, COMPLETED
}

data class Task(
    val id: Long,
    val title: String,
    val description: String?,
    val startDate: String?,
    val endDate: String?,
    val timeSpent: String,
    val isCompleted: Boolean, // This might need to be adjusted if status is used directly
    val status: String, // Added status to Task data class
    val observations: List<Observation>
) {
    data class Observation(
        val id: Long,
        val message: String,
        val createdAt: String?
    )
}

data class TaskFormData(
    val title: String,
    val description: String,
    val startDate: String,
    val endDate: String,
    val timeSpent: String,
    val isCompleted: Boolean,
    val observations: List<Observation>
) {
    data class Observation(
        val id: Long,
        val message: String,
        val createdAt: String?
    )
}

// Helper function to calculate time spent between two dates
private fun calculateTimeSpent(startDate: Date?, endDate: Date?): String {
    if (startDate == null || endDate == null) return "N/A"

    val diffMillis = endDate.time - startDate.time
    val hours = TimeUnit.MILLISECONDS.toHours(diffMillis)
    val minutes = TimeUnit.MILLISECONDS.toMinutes(diffMillis) % 60

    return when {
        hours > 0 -> "${hours}h ${minutes}m"
        minutes > 0 -> "${minutes}m"
        else -> "0m"
    }
}

// Helper function to format date to string
private fun formatDate(date: Date?): String? {
    return date?.let {
        SimpleDateFormat("HH:mm dd/MM/yyyy", Locale.getDefault()).format(it)
    }
}

// Extension function to convert TaskData to Task
fun com.example.tasktrackr_app.data.remote.response.data.TaskData.toTask(): Task {
    val timeSpent = calculateTimeSpent(this.startDate, this.endDate)

    return Task(
        id = this.id,
        title = this.title,
        description = this.description,
        startDate = formatDate(this.startDate),
        endDate = formatDate(this.endDate),
        timeSpent = timeSpent,
        isCompleted = (this.status == "FINISHED"), // Set isCompleted based on backend status
        status = this.status, // Pass the backend status directly
        observations = this.observations?.map { it.toObservation() } ?: emptyList()
    )
}

fun com.example.tasktrackr_app.data.remote.response.data.TaskData.ObservationData.toObservation(): Task.Observation {
    return Task.Observation(
        id = this.id,
        message = this.message,
        createdAt = formatDate(this.createdAt)
    )
}

// Updated filtering logic in ThreeTabMenu composable
@Composable
fun ThreeTabMenu(
    modifier: Modifier = Modifier,
    selectedFilter: TaskFilter = TaskFilter.IN_PROGRESS,
    onFilterSelected: (TaskFilter) -> Unit = {},
    tasks: List<Task> = emptyList(),
    onTaskSelected: (Task) -> Unit = {}
) {
    val filteredTasks = remember(selectedFilter, tasks) {
        when (selectedFilter) {
            TaskFilter.IN_PROGRESS -> tasks.filter { task ->
                task.status == "IN_PROGRESS"
            }
            TaskFilter.PENDING -> tasks.filter { task ->
                task.status == "PENDING"
            }
            TaskFilter.COMPLETED -> tasks.filter { task ->
                task.status == "FINISHED"
            }
        }
    }

    // Debug logging to check what statuses are actually in your tasks
    LaunchedEffect(tasks) {
        val uniqueStatuses = tasks.map { it.status }.distinct()
        Log.d("TaskFilter", "Available task statuses: $uniqueStatuses")
        Log.d("TaskFilter", "Total tasks: ${tasks.size}")

        // Log count per status for debugging
        uniqueStatuses.forEach { status ->
            val count = tasks.count { it.status == status }
            Log.d("TaskFilter", "Status '$status': $count tasks")
        }
    }

    // Rest of your ThreeTabMenu implementation remains the same...
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = TaskTrackrTheme.colorScheme.cardBackground
        ),
        border = androidx.compose.foundation.BorderStroke(
            width = 1.dp,
            color = TaskTrackrTheme.colorScheme.secondary
        )
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TaskFilterTab(
                        text = stringResource(R.string.tab_in_progress),
                        isSelected = selectedFilter == TaskFilter.IN_PROGRESS,
                        onClick = { onFilterSelected(TaskFilter.IN_PROGRESS) },
                        modifier = Modifier.weight(1f)
                    )

                    TaskFilterTab(
                        text = stringResource(R.string.tab_pending),
                        isSelected = selectedFilter == TaskFilter.PENDING,
                        onClick = { onFilterSelected(TaskFilter.PENDING) },
                        modifier = Modifier.weight(1f)
                    )

                    TaskFilterTab(
                        text = stringResource(R.string.tab_completed),
                        isSelected = selectedFilter == TaskFilter.COMPLETED,
                        onClick = { onFilterSelected(TaskFilter.COMPLETED) },
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(3.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Box(
                        modifier = Modifier.weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        if (selectedFilter == TaskFilter.IN_PROGRESS) {
                            Box(
                                modifier = Modifier
                                    .width(30.dp)
                                    .height(3.dp)
                                    .background(
                                        color = TaskTrackrTheme.colorScheme.accent,
                                        shape = RoundedCornerShape(2.dp)
                                    )
                            )
                        }
                    }

                    Box(
                        modifier = Modifier.weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        if (selectedFilter == TaskFilter.PENDING) {
                            Box(
                                modifier = Modifier
                                    .width(30.dp)
                                    .height(3.dp)
                                    .background(
                                        color = TaskTrackrTheme.colorScheme.accent,
                                        shape = RoundedCornerShape(2.dp)
                                    )
                            )
                        }
                    }

                    Box(
                        modifier = Modifier.weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        if (selectedFilter == TaskFilter.COMPLETED) {
                            Box(
                                modifier = Modifier
                                    .width(30.dp)
                                    .height(3.dp)
                                    .background(
                                        color = TaskTrackrTheme.colorScheme.accent,
                                        shape = RoundedCornerShape(2.dp)
                                    )
                            )
                        }
                    }
                }
            }

            HorizontalDivider(
                modifier = Modifier.fillMaxWidth(),
                thickness = 1.dp,
                color = TaskTrackrTheme.colorScheme.secondary
            )

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(450.dp),
                verticalArrangement = Arrangement.Top
            ) {
                if (filteredTasks.isEmpty()) {
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = when (selectedFilter) {
                                    TaskFilter.IN_PROGRESS -> "No tasks in progress"
                                    TaskFilter.PENDING -> "No pending tasks"
                                    TaskFilter.COMPLETED -> "No completed tasks"
                                },
                                style = TaskTrackrTheme.typography.body,
                                color = TaskTrackrTheme.colorScheme.text,
                                textAlign = TextAlign.Center
                            )

                            // Debug info - remove this in production
                            if (tasks.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Total tasks available: ${tasks.size}",
                                    style = TaskTrackrTheme.typography.body,
                                    color = TaskTrackrTheme.colorScheme.text.copy(alpha = 0.6f),
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                } else {
                    items(filteredTasks.size) { index ->
                        TaskCard(
                            task = filteredTasks[index],
                            onOpenClick = {
                                onTaskSelected(filteredTasks[index])
                            }
                        )

                        if (index < filteredTasks.size - 1) {
                            HorizontalDivider(
                                modifier = Modifier.fillMaxWidth(),
                                thickness = 1.dp,
                                color = TaskTrackrTheme.colorScheme.secondary
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TaskFilterTab(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxHeight()
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = TaskTrackrTheme.typography.smallTitles,
            color = if (isSelected) {
                TaskTrackrTheme.colorScheme.accent
            } else {
                TaskTrackrTheme.colorScheme.text
            },
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun TaskCard(
    modifier: Modifier = Modifier,
    task: Task,
    onOpenClick: () -> Unit = {}
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(150.dp)
            .padding(horizontal = 20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .padding(top = 16.dp, bottom = 12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    text = task.title,
                    style = TaskTrackrTheme.typography.subHeader,
                    color = TaskTrackrTheme.colorScheme.secondary,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                Text(
                    text = stringResource(R.string.task_description_label) + " ${task.description ?: "No description"}",
                    style = TaskTrackrTheme.typography.body,
                    color = TaskTrackrTheme.colorScheme.text,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                )
            }

            if (task.timeSpent != "N/A") {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = stringResource(R.string.time_spent_label) + " ${task.timeSpent}",
                    style = TaskTrackrTheme.typography.body,
                    color = TaskTrackrTheme.colorScheme.accent,
                )
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        Button(
            onClick = onOpenClick,
            colors = ButtonDefaults.buttonColors(
                containerColor = TaskTrackrTheme.colorScheme.primary
            ),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
                .height(48.dp)
                .widthIn(min = 70.dp),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 0.dp)
        ) {
            Text(
                text = stringResource(R.string.button_open),
                style = TaskTrackrTheme.typography.button,
                color = TaskTrackrTheme.colorScheme.buttonText
            )
        }
    }
}