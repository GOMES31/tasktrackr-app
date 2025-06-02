package com.example.tasktrackr_app.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.tasktrackr_app.R
import com.example.tasktrackr_app.ui.theme.TaskTrackrTheme

@Composable
fun TaskDetail(
    modifier: Modifier = Modifier,
    isVisible: Boolean = false,
    onDismiss: () -> Unit = {},
    taskTitle: String = "Title of Task",
    taskDescription: String = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.",
    location: String = "",
    startDate: String = "HH:mm dd/MM/yyyy",
    endDate: String = "HH:mm dd/MM/yyyy",
    progress: Int = 50,
    timeSpent: String = "1h 30m",
    completionStatus: String = "Finished/Not Finished",
    observations: List<String> = listOf(
        "Lorem ipsum dolor sit amet, consectetur adipiscing elit.",
        "Sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.",
        "Ut enim ad minim veniam, quis nostrud exercitation ullamco."
    ),
    onEditTask: () -> Unit = {}
) {
    if (!isVisible) return

    var selectedObservationIndex by remember { mutableStateOf(0) }
    var isDropdownExpanded by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Transparent)
            .clickable(
                indication = null,
                interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() }
            ) {
            }
    ) {
        Card(
            modifier = Modifier
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
                    .verticalScroll(rememberScrollState())
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Text(
                        text = taskTitle,
                        style = TaskTrackrTheme.typography.subHeader,
                        color = TaskTrackrTheme.colorScheme.secondary,
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.close),
                            contentDescription = "Close",
                            tint = TaskTrackrTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = stringResource(R.string.task_description_label) + " $taskDescription",
                    style = TaskTrackrTheme.typography.body,
                    color = TaskTrackrTheme.colorScheme.text,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = stringResource(R.string.task_location_label) + " $location",
                    style = TaskTrackrTheme.typography.body,
                    color = TaskTrackrTheme.colorScheme.text
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = stringResource(R.string.start_date_label) + " $startDate",
                    style = TaskTrackrTheme.typography.body,
                    color = TaskTrackrTheme.colorScheme.text
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = stringResource(R.string.end_date_label) + " $endDate",
                    style = TaskTrackrTheme.typography.body,
                    color = TaskTrackrTheme.colorScheme.text
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = stringResource(R.string.progress_label) + " $progress%",
                    style = TaskTrackrTheme.typography.body,
                    color = TaskTrackrTheme.colorScheme.text
                )

                Spacer(modifier = Modifier.height(8.dp))

                LinearProgressIndicator(
                    progress = progress / 100f,
                    modifier = Modifier.fillMaxWidth(),
                    color = TaskTrackrTheme.colorScheme.tertiary,
                    trackColor = TaskTrackrTheme.colorScheme.inputBackground
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = stringResource(R.string.time_spent_label) + " $timeSpent",
                    style = TaskTrackrTheme.typography.body,
                    color = TaskTrackrTheme.colorScheme.text
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = stringResource(R.string.completion_status_label) + " $completionStatus",
                    style = TaskTrackrTheme.typography.body,
                    color = TaskTrackrTheme.colorScheme.text
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.observations_label),
                        style = TaskTrackrTheme.typography.body,
                        color = TaskTrackrTheme.colorScheme.text
                    )

                    Box {
                        OutlinedButton(
                            onClick = { isDropdownExpanded = true },
                            modifier = Modifier
                                .background(
                                    TaskTrackrTheme.colorScheme.inputBackground,
                                    RoundedCornerShape(4.dp)
                                ),
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = TaskTrackrTheme.colorScheme.inputBackground
                            ),
                            border = null
                        ) {
                            Text(
                                text = if (observations.isNotEmpty()) {
                                    "${stringResource(R.string.observation_item_label)} ${selectedObservationIndex + 1} ▼"
                                } else {
                                    "${stringResource(R.string.no_observations_available)} ▼"
                                },
                                style = TaskTrackrTheme.typography.body,
                                color = TaskTrackrTheme.colorScheme.text
                            )
                        }

                        DropdownMenu(
                            expanded = isDropdownExpanded,
                            onDismissRequest = { isDropdownExpanded = false },
                            modifier = Modifier.background(TaskTrackrTheme.colorScheme.cardBackground)
                        ) {
                            observations.forEachIndexed { index, _ ->
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            text = "${stringResource(R.string.observation_item_label)} ${index + 1}",
                                            style = TaskTrackrTheme.typography.body,
                                            color = TaskTrackrTheme.colorScheme.text
                                        )
                                    },
                                    onClick = {
                                        selectedObservationIndex = index
                                        isDropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                val observationTextLineHeight = TaskTrackrTheme.typography.body.lineHeight.value
                val desiredObservationCardHeight = (2 * observationTextLineHeight).dp + (12.dp * 2)

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(desiredObservationCardHeight),
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = TaskTrackrTheme.colorScheme.inputBackground
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .padding(12.dp)
                            .verticalScroll(rememberScrollState())
                    ) {
                        Text(
                            text = if (observations.isNotEmpty() && selectedObservationIndex < observations.size) {
                                observations[selectedObservationIndex]
                            } else {
                                stringResource(R.string.no_observations_available)
                            },
                            style = TaskTrackrTheme.typography.body,
                            color = TaskTrackrTheme.colorScheme.text,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                CustomButton(
                    text = stringResource(R.string.edit_profile),
                    icon = painterResource(id = R.drawable.edit),
                    enabled = true,
                    onClick = onEditTask,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }
        }
    }
}