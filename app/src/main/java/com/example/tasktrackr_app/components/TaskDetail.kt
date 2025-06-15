package com.example.tasktrackr_app.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.tasktrackr_app.R
import com.example.tasktrackr_app.data.remote.response.data.TaskData
import com.example.tasktrackr_app.ui.theme.TaskTrackrTheme
import com.example.tasktrackr_app.utils.DateUtils

@Composable
fun TaskDetail(
    modifier: Modifier = Modifier,
    isVisible: Boolean = false,
    task: TaskData,
    onDismiss: () -> Unit = {},
    onEditTask: (TaskData) -> Unit
) {
    val observations = task.observations ?: emptyList()
    val timeSpent = remember(task.startDate, task.endDate) {
        DateUtils.calculateTimeSpent(task.startDate, task.endDate)
    }

    val fixedObservationItemHeight = 56.dp
    val observationsCardHeight = (fixedObservationItemHeight * 2) + 40.5.dp

    if (isVisible) {
        Dialog(
            onDismissRequest = { onDismiss() },
            properties = DialogProperties(
                dismissOnBackPress = true,
                dismissOnClickOutside = true,
                usePlatformDefaultWidth = false
            )
        ) {
            Card(
                modifier = modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp, top = 125.dp)
                    .height(548.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = TaskTrackrTheme.colorScheme.cardBackground
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                border = BorderStroke(1.dp, TaskTrackrTheme.colorScheme.secondary)
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
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
                        IconButton(
                            onClick = { onDismiss() },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.close),
                                contentDescription = stringResource(R.string.button_close),
                                tint = TaskTrackrTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = stringResource(R.string.task_description_label) + " ${task.description ?: stringResource(R.string.no_description_available)}",
                        style = TaskTrackrTheme.typography.body,
                        color = TaskTrackrTheme.colorScheme.text,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = stringResource(R.string.start_date_label) + " ${task.startDate?.let { DateUtils.formatFullDate(it) } ?: stringResource(R.string.not_available)}",
                        style = TaskTrackrTheme.typography.body,
                        color = TaskTrackrTheme.colorScheme.text
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = stringResource(R.string.end_date_label) + " ${task.endDate?.let { DateUtils.formatFullDate(it) } ?: stringResource(R.string.not_available)}",
                        style = TaskTrackrTheme.typography.body,
                        color = TaskTrackrTheme.colorScheme.text
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = stringResource(R.string.time_spent_label) + " $timeSpent",
                        style = TaskTrackrTheme.typography.body,
                        color = TaskTrackrTheme.colorScheme.text
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    val statusText = when (task.status?.lowercase()) {
                        "in_progress" -> stringResource(R.string.tab_in_progress)
                        "pending" -> stringResource(R.string.tab_pending)
                        "finished" -> stringResource(R.string.tab_finished)
                        else -> task.status ?: stringResource(R.string.not_available)
                    }

                    Text(
                        text = stringResource(R.string.completion_status_label) + " $statusText",
                        style = TaskTrackrTheme.typography.body,
                        color = TaskTrackrTheme.colorScheme.text
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = stringResource(R.string.observations_label),
                        style = TaskTrackrTheme.typography.subHeader,
                        color = TaskTrackrTheme.colorScheme.secondary
                    )

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(observationsCardHeight),
                        shape = RoundedCornerShape(8.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = TaskTrackrTheme.colorScheme.inputBackground
                        ),
                        border = BorderStroke(1.dp, TaskTrackrTheme.colorScheme.secondary)
                    ) {
                        if (observations.isEmpty()) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(12.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = stringResource(R.string.no_observations_available),
                                    style = TaskTrackrTheme.typography.body,
                                    color = TaskTrackrTheme.colorScheme.text.copy(alpha = 0.6f),
                                    textAlign = TextAlign.Center
                                )
                            }
                        } else {
                            LazyColumn(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(12.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                items(observations) { observation ->
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(fixedObservationItemHeight),
                                        verticalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = observation.message,
                                            style = TaskTrackrTheme.typography.body,
                                            color = TaskTrackrTheme.colorScheme.text,
                                            maxLines = 2,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        observation.createdAt?.let { createdAt ->
                                            Text(
                                                text = DateUtils.formatDateOnly(createdAt) ?: stringResource(R.string.not_available),
                                                style = TaskTrackrTheme.typography.body.copy(
                                                    fontSize = TaskTrackrTheme.typography.body.fontSize * 0.85f
                                                ),
                                                color = TaskTrackrTheme.colorScheme.text.copy(alpha = 0.6f)
                                            )
                                        }
                                    }
                                    if (observations.size > 1 && observations.last() != observation) {
                                        HorizontalDivider(
                                            modifier = Modifier.padding(vertical = 4.dp),
                                            thickness = 0.5.dp,
                                            color = TaskTrackrTheme.colorScheme.secondary.copy(alpha = 0.2f)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    CustomButton(
                        text = stringResource(R.string.update_task_button),
                        icon = painterResource(id = R.drawable.edit),
                        enabled = true,
                        onClick = {
                            onEditTask(task)
                            onDismiss()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.CenterHorizontally)
                    )
                }
            }
        }
    }
}
