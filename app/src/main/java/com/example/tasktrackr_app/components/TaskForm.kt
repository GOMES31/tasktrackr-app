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
import androidx.compose.ui.unit.dp
import com.example.tasktrackr_app.R
import com.example.tasktrackr_app.ui.theme.TaskTrackrTheme

@Composable
fun TaskForm(
    modifier: Modifier = Modifier,
    isVisible: Boolean = false,
    onDismiss: () -> Unit = {},
    onSave: (TaskFormData) -> Unit = {},
    isEditMode: Boolean = false,
    existingTask: TaskFormData? = null
) {
    if (!isVisible) return

    var title by remember { mutableStateOf(existingTask?.title ?: "") }
    var description by remember { mutableStateOf(existingTask?.description ?: "") }
    var startDate by remember { mutableStateOf(existingTask?.startDate ?: "") }
    var endDate by remember { mutableStateOf(existingTask?.endDate ?: "") }
    var timeSpent by remember { mutableStateOf(existingTask?.timeSpent ?: "") }
    var isCompleted by remember { mutableStateOf(existingTask?.isCompleted ?: false) }

    var showTitleError by remember { mutableStateOf(false) }
    var showStartDateError by remember { mutableStateOf(false) }
    var showEndDateError by remember { mutableStateOf(false) }

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
                .padding(start = 16.dp, end = 16.dp, top = 126.dp)
                .height(650.dp),
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
                        text = if (isEditMode) stringResource(R.string.edit_task_title) else stringResource(R.string.add_task_title),
                        style = TaskTrackrTheme.typography.subHeader,
                        color = TaskTrackrTheme.colorScheme.primary,
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

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = stringResource(R.string.task_title_label),
                    style = TaskTrackrTheme.typography.label,
                    color = TaskTrackrTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(4.dp))

                OutlinedTextField(
                    value = title,
                    onValueChange = {
                        title = it
                        if (showTitleError && it.isNotBlank()) {
                            showTitleError = false
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = {
                        Text(
                            stringResource(R.string.task_title_placeholder),
                            style = TaskTrackrTheme.typography.placeholder,
                            color = TaskTrackrTheme.colorScheme.text.copy(alpha = 0.6f)
                        )
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedContainerColor = TaskTrackrTheme.colorScheme.inputBackground,
                        focusedContainerColor = TaskTrackrTheme.colorScheme.inputBackground,
                        unfocusedBorderColor = TaskTrackrTheme.colorScheme.primary,
                        focusedBorderColor = TaskTrackrTheme.colorScheme.primary
                    ),
                    shape = RoundedCornerShape(8.dp)
                )
                if (showTitleError) {
                    ErrorMessage(
                        text = stringResource(R.string.error_title_empty)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = stringResource(R.string.task_description_label),
                    style = TaskTrackrTheme.typography.label,
                    color = TaskTrackrTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(4.dp))

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp),
                    placeholder = {
                        Text(
                            stringResource(R.string.task_description_placeholder),
                            style = TaskTrackrTheme.typography.placeholder,
                            color = TaskTrackrTheme.colorScheme.text.copy(alpha = 0.6f)
                        )
                    },
                    maxLines = 4,
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedContainerColor = TaskTrackrTheme.colorScheme.inputBackground,
                        focusedContainerColor = TaskTrackrTheme.colorScheme.inputBackground,
                        unfocusedBorderColor = TaskTrackrTheme.colorScheme.primary,
                        focusedBorderColor = TaskTrackrTheme.colorScheme.primary
                    ),
                    shape = RoundedCornerShape(8.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Removed: Location Text and OutlinedTextField
                // Removed: Spacer(modifier = Modifier.height(12.dp)) after location

                Text(
                    text = stringResource(R.string.start_date_label),
                    style = TaskTrackrTheme.typography.label,
                    color = TaskTrackrTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(4.dp))

                OutlinedTextField(
                    value = startDate,
                    onValueChange = {
                        startDate = it
                        if (showStartDateError && it.isNotBlank()) {
                            showStartDateError = false
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = {
                        Text(
                            stringResource(R.string.start_date_placeholder),
                            style = TaskTrackrTheme.typography.placeholder,
                            color = TaskTrackrTheme.colorScheme.text.copy(alpha = 0.6f)
                        )
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedContainerColor = TaskTrackrTheme.colorScheme.inputBackground,
                        focusedContainerColor = TaskTrackrTheme.colorScheme.inputBackground,
                        unfocusedBorderColor = TaskTrackrTheme.colorScheme.primary,
                        focusedBorderColor = TaskTrackrTheme.colorScheme.primary
                    ),
                    shape = RoundedCornerShape(8.dp)
                )
                if (showStartDateError) {
                    ErrorMessage(
                        text = stringResource(R.string.error_start_date_empty)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = stringResource(R.string.end_date_label),
                    style = TaskTrackrTheme.typography.label,
                    color = TaskTrackrTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(4.dp))

                OutlinedTextField(
                    value = endDate,
                    onValueChange = {
                        endDate = it
                        if (showEndDateError && it.isNotBlank()) {
                            showEndDateError = false
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = {
                        Text(
                            stringResource(R.string.end_date_placeholder),
                            style = TaskTrackrTheme.typography.placeholder,
                            color = TaskTrackrTheme.colorScheme.text.copy(alpha = 0.6f)
                        )
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedContainerColor = TaskTrackrTheme.colorScheme.inputBackground,
                        focusedContainerColor = TaskTrackrTheme.colorScheme.inputBackground,
                        unfocusedBorderColor = TaskTrackrTheme.colorScheme.primary,
                        focusedBorderColor = TaskTrackrTheme.colorScheme.primary
                    ),
                    shape = RoundedCornerShape(8.dp)
                )
                if (showEndDateError) {
                    ErrorMessage(
                        text = stringResource(R.string.error_end_date_empty)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = stringResource(R.string.time_spent_label),
                    style = TaskTrackrTheme.typography.label,
                    color = TaskTrackrTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(4.dp))

                OutlinedTextField(
                    value = timeSpent,
                    onValueChange = { timeSpent = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = {
                        Text(
                            stringResource(R.string.time_spent_placeholder),
                            style = TaskTrackrTheme.typography.placeholder,
                            color = TaskTrackrTheme.colorScheme.text.copy(alpha = 0.6f)
                        )
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedContainerColor = TaskTrackrTheme.colorScheme.inputBackground,
                        focusedContainerColor = TaskTrackrTheme.colorScheme.inputBackground,
                        unfocusedBorderColor = TaskTrackrTheme.colorScheme.primary,
                        focusedBorderColor = TaskTrackrTheme.colorScheme.primary
                    ),
                    shape = RoundedCornerShape(8.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.completion_status_label),
                        style = TaskTrackrTheme.typography.label,
                        color = TaskTrackrTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Checkbox(
                        checked = isCompleted,
                        onCheckedChange = { isCompleted = it },
                        colors = CheckboxDefaults.colors(
                            checkedColor = TaskTrackrTheme.colorScheme.accent,
                            uncheckedColor = TaskTrackrTheme.colorScheme.primary
                        )
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    CustomButton(
                        text = if (isEditMode) stringResource(R.string.update_task_button) else stringResource(R.string.save_task_button),
                        icon = painterResource(id = R.drawable.edit),
                        enabled = true,
                        onClick = {
                            showTitleError = false
                            showStartDateError = false
                            showEndDateError = false

                            var hasError = false

                            if (title.isBlank()) {
                                showTitleError = true
                                hasError = true
                            }
                            if (startDate.isBlank()) {
                                showStartDateError = true
                                hasError = true
                            }
                            if (endDate.isBlank()) {
                                showEndDateError = true
                                hasError = true
                            }

                            if (!hasError) {
                                val taskFormData = TaskFormData(
                                    title = title,
                                    description = description,
                                    startDate = startDate,
                                    endDate = endDate,
                                    timeSpent = timeSpent,
                                    isCompleted = isCompleted,
                                    observations = emptyList()
                                )
                                onSave(taskFormData)
                            }
                        }
                    )
                }
            }
        }
    }
}