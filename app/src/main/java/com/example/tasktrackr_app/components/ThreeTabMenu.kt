package com.example.tasktrackr_app.components

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

enum class TaskFilter {
    TODAY, PENDING, COMPLETED
}

data class Task(
    val id: String = "",
    val title: String,
    val description: String,
    val location: String = "",
    val startDate: String = "",
    val endDate: String = "",
    val progress: Int = 0,
    val timeSpent: String = "",
    val isCompleted: Boolean = false,
    val observations: List<String> = emptyList()
)

data class TaskFormData(
    val title: String,
    val description: String,
    val location: String,
    val startDate: String,
    val endDate: String,
    val progress: Int,
    val timeSpent: String,
    val isCompleted: Boolean,
    val observations: List<String>
)

@Composable
fun ThreeTabMenu(
    modifier: Modifier = Modifier,
    selectedFilter: TaskFilter = TaskFilter.TODAY,
    onFilterSelected: (TaskFilter) -> Unit = {},
    onTaskSelected: (Task) -> Unit = {}
) {
    val placeholderTasks = remember {
        listOf(
            Task(
                id = "1",
                title = "Complete Project Proposal",
                description = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.",
                location = "Office Building A",
                startDate = "09:00 01/06/2025",
                endDate = "17:00 03/06/2025",
                progress = 75,
                timeSpent = "2h 30m",
                isCompleted = false,
                observations = listOf(
                    "Initial research completed successfully",
                    "Need to gather more market data",
                    "Client feedback incorporated into draft"
                )
            ),
            Task(
                id = "2",
                title = "Team Meeting Preparation",
                description = "Prepare agenda and materials for quarterly team meeting. Include performance metrics and project updates.",
                location = "Conference Room B",
                startDate = "14:00 02/06/2025",
                endDate = "16:00 02/06/2025",
                progress = 50,
                timeSpent = "1h 15m",
                isCompleted = false,
                observations = listOf(
                    "Agenda draft completed",
                    "Still need to compile metrics"
                )
            ),
            Task(
                id = "3",
                title = "Database Migration",
                description = "Migrate legacy database to new cloud infrastructure. Includes data validation and testing phases.",
                location = "Data Center",
                startDate = "10:00 28/05/2025",
                endDate = "18:00 30/05/2025",
                progress = 100,
                timeSpent = "8h 45m",
                isCompleted = true,
                observations = listOf(
                    "Migration completed successfully",
                    "All data validation tests passed",
                    "Performance improved by 40%"
                )
            ),
            Task(
                id = "4",
                title = "Client Presentation Design",
                description = "Create visual presentation for client proposal including charts, mockups, and timeline.",
                location = "Design Studio",
                startDate = "09:30 04/06/2025",
                endDate = "12:00 05/06/2025",
                progress = 25,
                timeSpent = "45m",
                isCompleted = false,
                observations = listOf(
                    "Initial wireframes created"
                )
            ),
            Task(
                id = "5",
                title = "Code Review Session",
                description = "Review and provide feedback on team members' code submissions for the current sprint.",
                location = "Remote",
                startDate = "15:00 01/06/2025",
                endDate = "16:30 01/06/2025",
                progress = 90,
                timeSpent = "1h 20m",
                isCompleted = false,
                observations = listOf(
                    "Most code reviews completed",
                    "Found minor optimization opportunities",
                    "Good code quality overall"
                )
            )
        )
    }

    val filteredTasks = remember(selectedFilter) {
        when (selectedFilter) {
            TaskFilter.TODAY -> placeholderTasks
            TaskFilter.PENDING -> placeholderTasks.filter { !it.isCompleted }
            TaskFilter.COMPLETED -> placeholderTasks.filter { it.isCompleted }
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
                        text = stringResource(R.string.tab_today),
                        isSelected = selectedFilter == TaskFilter.TODAY,
                        onClick = { onFilterSelected(TaskFilter.TODAY) },
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
                        if (selectedFilter == TaskFilter.TODAY) {
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
                    text = stringResource(R.string.task_description_label) + " ${task.description}",
                    style = TaskTrackrTheme.typography.body,
                    color = TaskTrackrTheme.colorScheme.text,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                )
            }

            if (task.progress > 0) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = stringResource(R.string.task_progress_label) + " ${task.progress}%",
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