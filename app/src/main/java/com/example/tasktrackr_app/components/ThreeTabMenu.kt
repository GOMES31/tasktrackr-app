package com.example.tasktrackr_app.components

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.tasktrackr_app.R
import com.example.tasktrackr_app.data.remote.response.data.ObservationData
import com.example.tasktrackr_app.data.remote.response.data.TaskData
import com.example.tasktrackr_app.ui.theme.TaskTrackrTheme
import com.example.tasktrackr_app.utils.DateUtils
import java.util.*

enum class TaskFilter {
    IN_PROGRESS, PENDING, FINISHED
}

data class TaskFormData(
    val title: String,
    val description: String,
    val startDate: Date?,
    val endDate: Date?,
    val status: String,
    val observations: List<ObservationData>,
    val assigneeIds: List<String>
)

@Composable
fun ThreeTabMenu(
    modifier: Modifier = Modifier,
    selectedFilter: TaskFilter = TaskFilter.IN_PROGRESS,
    onFilterSelected: (TaskFilter) -> Unit = {},
    tasks: List<TaskData> = emptyList(),
    onTaskSelected: (TaskData) -> Unit = {}
) {
    val filteredTasks = remember(selectedFilter, tasks) {
        when (selectedFilter) {
            TaskFilter.IN_PROGRESS -> tasks.filter {
                it.status == "IN_PROGRESS"
            }
            TaskFilter.PENDING -> tasks.filter {
                it.status == "PENDING"
            }
            TaskFilter.FINISHED -> tasks.filter {
                it.status == "FINISHED"
            }
        }
    }

    LaunchedEffect(tasks) {
        val uniqueStatuses = tasks.map { it.status }.distinct()
        uniqueStatuses.forEach { status ->
            val count = tasks.count { it.status == status }
            Log.d("ThreeTabMenu", "Status: $status, Count: $count")
        }
    }

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
                        text = stringResource(R.string.in_progress),
                        isSelected = selectedFilter == TaskFilter.IN_PROGRESS,
                        onClick = { onFilterSelected(TaskFilter.IN_PROGRESS) },
                        modifier = Modifier.weight(1f)
                    )
                    TaskFilterTab(
                        text = stringResource(R.string.pending),
                        isSelected = selectedFilter == TaskFilter.PENDING,
                        onClick = { onFilterSelected(TaskFilter.PENDING) },
                        modifier = Modifier.weight(1f)
                    )
                    TaskFilterTab(
                        text = stringResource(R.string.finished),
                        isSelected = selectedFilter == TaskFilter.FINISHED,
                        onClick = { onFilterSelected(TaskFilter.FINISHED) },
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
                        if (selectedFilter == TaskFilter.FINISHED) {
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
                    .height(453.dp),
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
                                    TaskFilter.FINISHED -> "No finished tasks"
                                },
                                style = TaskTrackrTheme.typography.body,
                                color = TaskTrackrTheme.colorScheme.text,
                                textAlign = TextAlign.Center
                            )
                            if (tasks.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Total tasks available: ${tasks.size}",
                                    style = TaskTrackrTheme.typography.body,
                                    color = TaskTrackrTheme.colorScheme.text,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                } else {
                    items(filteredTasks) { task ->
                        TaskCard(
                            task = task,
                            onOpenClick = { onTaskSelected(task) }
                        )
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
            color = if (isSelected) TaskTrackrTheme.colorScheme.accent else TaskTrackrTheme.colorScheme.text,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun TaskCard(
    modifier: Modifier = Modifier,
    task: TaskData,
    onOpenClick: () -> Unit = {}
) {
    val timeSpent = remember(task.startDate, task.endDate) {
        DateUtils.calculateTimeSpent(task.startDate, task.endDate)
    }

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
            Text(
                text = stringResource(R.string.time_spent_label) + " $timeSpent",
                style = TaskTrackrTheme.typography.body,
                color = TaskTrackrTheme.colorScheme.accent,
            )
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