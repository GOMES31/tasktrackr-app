package com.example.tasktrackr_app.ui.screens.projects

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.tasktrackr_app.R
import com.example.tasktrackr_app.components.CustomButton
import com.example.tasktrackr_app.components.DatePicker
import com.example.tasktrackr_app.components.TimePicker
import com.example.tasktrackr_app.data.remote.response.data.AuthData
import com.example.tasktrackr_app.data.remote.response.data.TeamData
import com.example.tasktrackr_app.ui.theme.TaskTrackrTheme
import com.example.tasktrackr_app.utils.DateUtils
import java.util.Date

data class ProjectFormData(
    val name: String,
    val description: String,
    val teamId: Long,
    val startDate: Date?,
    val endDate: Date?,
    val status: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProjectForm(
    isVisible: Boolean,
    adminTeams: List<TeamData>,
    currentUserData: AuthData?,
    onDismiss: () -> Unit,
    onSave: (ProjectFormData) -> Unit
) {
    if (!isVisible) return

    var projectName by remember { mutableStateOf("") }
    var projectDescription by remember { mutableStateOf("") }
    var selectedTeam by remember { mutableStateOf<TeamData?>(null) }
    var isTeamDropdownExpanded by remember { mutableStateOf(false) }
    var startDate by remember { mutableStateOf<Date?>(null) }
    var endDate by remember { mutableStateOf<Date?>(null) }
    var selectedStatus by remember { mutableStateOf("NOT_STARTED") }
    var isStatusDropdownExpanded by remember { mutableStateOf(false) }

    // Date and time picker states
    var showStartDatePicker by remember { mutableStateOf(false) }
    var showEndDatePicker by remember { mutableStateOf(false) }
    var showStartTimePicker by remember { mutableStateOf(false) }
    var showEndTimePicker by remember { mutableStateOf(false) }
    var settingTimeForStartDate by remember { mutableStateOf(false) }
    var tempDateForTimePicker by remember { mutableStateOf<Date?>(null) }

    var showNameError by remember { mutableStateOf(false) }
    var showDescriptionError by remember { mutableStateOf(false) }
    var showTeamError by remember { mutableStateOf(false) }

    val statusOptions = listOf(
        "IN_PROGRESS" to stringResource(R.string.in_progress),
        "FINISHED" to stringResource(R.string.finished),
        "PENDING" to stringResource(R.string.pending)
    )

    // Filter teams where current user is admin
    val userAdminTeams = remember(adminTeams, currentUserData?.email) {
        adminTeams.filter { team ->
            val currentUserEmail = currentUserData?.email
            team.members?.any { member ->
                member.email == currentUserEmail && member.role == "ADMIN"
            } ?: false
        }
    }

    // Validation
    val currentStartDate = startDate
    val currentEndDate = endDate
    val isFormValid = projectName.isNotBlank() &&
            projectDescription.isNotBlank() &&
            selectedTeam != null &&
            (currentEndDate == null || currentStartDate == null || !currentEndDate.before(currentStartDate))

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(
                containerColor = TaskTrackrTheme.colorScheme.cardBackground
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            border = BorderStroke(1.dp, TaskTrackrTheme.colorScheme.secondary)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.add_project),
                        style = TaskTrackrTheme.typography.header,
                        color = TaskTrackrTheme.colorScheme.primary
                    )

                    IconButton(
                        onClick = onDismiss,
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

                // Check if user has admin teams
                if (userAdminTeams.isEmpty()) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = TaskTrackrTheme.colorScheme.inputBackground
                        ),
                        border = BorderStroke(1.dp, TaskTrackrTheme.colorScheme.primary)
                    ) {
                        Text(
                            text = "You need to be an admin of at least one team to create a project.",
                            style = TaskTrackrTheme.typography.body,
                            color = TaskTrackrTheme.colorScheme.text,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(16.dp)
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        CustomButton(
                            text = "Close",
                            onClick = onDismiss,
                            enabled = true
                        )
                    }
                    return@Column
                }

                Text(
                    text = "Project Name",
                    style = TaskTrackrTheme.typography.label,
                    color = TaskTrackrTheme.colorScheme.primary
                )
                OutlinedTextField(
                    value = projectName,
                    onValueChange = {
                        projectName = it
                        showNameError = false
                    },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = {
                        Text(
                            "Enter project name",
                            style = TaskTrackrTheme.typography.placeholder,
                            color = TaskTrackrTheme.colorScheme.text.copy(alpha = 0.6f)
                        )
                    },
                    singleLine = true,
                    isError = showNameError,
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedContainerColor = TaskTrackrTheme.colorScheme.inputBackground,
                        focusedContainerColor = TaskTrackrTheme.colorScheme.inputBackground,
                        unfocusedBorderColor = TaskTrackrTheme.colorScheme.primary,
                        focusedBorderColor = TaskTrackrTheme.colorScheme.primary
                    ),
                    shape = RoundedCornerShape(8.dp)
                )
                if (showNameError) {
                    Text(
                        text = "Project name is required",
                        color = Color.Red,
                        style = TaskTrackrTheme.typography.caption,
                        modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                    )
                }

                Text(
                    text = "Description",
                    style = TaskTrackrTheme.typography.label,
                    color = TaskTrackrTheme.colorScheme.primary
                )
                OutlinedTextField(
                    value = projectDescription,
                    onValueChange = {
                        projectDescription = it
                        showDescriptionError = false
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp),
                    placeholder = {
                        Text(
                            "Enter project description",
                            style = TaskTrackrTheme.typography.placeholder,
                            color = TaskTrackrTheme.colorScheme.text.copy(alpha = 0.6f)
                        )
                    },
                    maxLines = 3,
                    isError = showDescriptionError,
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedContainerColor = TaskTrackrTheme.colorScheme.inputBackground,
                        focusedContainerColor = TaskTrackrTheme.colorScheme.inputBackground,
                        unfocusedBorderColor = TaskTrackrTheme.colorScheme.primary,
                        focusedBorderColor = TaskTrackrTheme.colorScheme.primary
                    ),
                    shape = RoundedCornerShape(8.dp)
                )
                if (showDescriptionError) {
                    Text(
                        text = "Project description is required",
                        color = Color.Red,
                        style = TaskTrackrTheme.typography.caption,
                        modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                    )
                }

                Text(
                    text = "Select Team",
                    style = TaskTrackrTheme.typography.label,
                    color = TaskTrackrTheme.colorScheme.primary
                )
                ExposedDropdownMenuBox(
                    expanded = isTeamDropdownExpanded,
                    onExpandedChange = { isTeamDropdownExpanded = !isTeamDropdownExpanded }
                ) {
                    OutlinedTextField(
                        value = selectedTeam?.name ?: "",
                        onValueChange = { },
                        readOnly = true,
                        placeholder = {
                            Text(
                                "Choose a team",
                                style = TaskTrackrTheme.typography.placeholder,
                                color = TaskTrackrTheme.colorScheme.text.copy(alpha = 0.6f)
                            )
                        },
                        trailingIcon = {
                            Icon(
                                imageVector = Icons.Default.ArrowDropDown,
                                contentDescription = "Dropdown",
                                tint = TaskTrackrTheme.colorScheme.primary
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                        isError = showTeamError,
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedContainerColor = TaskTrackrTheme.colorScheme.inputBackground,
                            focusedContainerColor = TaskTrackrTheme.colorScheme.inputBackground,
                            unfocusedBorderColor = TaskTrackrTheme.colorScheme.primary,
                            focusedBorderColor = TaskTrackrTheme.colorScheme.primary
                        ),
                        shape = RoundedCornerShape(8.dp)
                    )

                    ExposedDropdownMenu(
                        expanded = isTeamDropdownExpanded,
                        onDismissRequest = { isTeamDropdownExpanded = false },
                        modifier = Modifier.background(TaskTrackrTheme.colorScheme.cardBackground)
                    ) {
                        userAdminTeams.forEach { team ->
                            DropdownMenuItem(
                                text = {
                                    Column {
                                        Text(
                                            text = team.name,
                                            style = TaskTrackrTheme.typography.body,
                                            color = TaskTrackrTheme.colorScheme.text
                                        )
                                        Text(
                                            text = "Admin",
                                            style = TaskTrackrTheme.typography.caption,
                                            color = TaskTrackrTheme.colorScheme.primary
                                        )
                                    }
                                },
                                onClick = {
                                    selectedTeam = team
                                    isTeamDropdownExpanded = false
                                    showTeamError = false
                                }
                            )
                        }
                    }
                }
                if (showTeamError) {
                    Text(
                        text = "Please select a team",
                        color = Color.Red,
                        style = TaskTrackrTheme.typography.caption,
                        modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                    )
                }

                Text(
                    text = "Project Status",
                    style = TaskTrackrTheme.typography.label,
                    color = TaskTrackrTheme.colorScheme.primary
                )
                ExposedDropdownMenuBox(
                    expanded = isStatusDropdownExpanded,
                    onExpandedChange = { isStatusDropdownExpanded = !isStatusDropdownExpanded }
                ) {
                    OutlinedTextField(
                        value = statusOptions.find { it.first == selectedStatus }?.second ?: "",
                        onValueChange = { },
                        readOnly = true,
                        placeholder = {
                            Text(
                                "Select status",
                                style = TaskTrackrTheme.typography.placeholder,
                                color = TaskTrackrTheme.colorScheme.text.copy(alpha = 0.6f)
                            )
                        },
                        trailingIcon = {
                            Icon(
                                imageVector = Icons.Default.ArrowDropDown,
                                contentDescription = "Dropdown",
                                tint = TaskTrackrTheme.colorScheme.primary
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedContainerColor = TaskTrackrTheme.colorScheme.inputBackground,
                            focusedContainerColor = TaskTrackrTheme.colorScheme.inputBackground,
                            unfocusedBorderColor = TaskTrackrTheme.colorScheme.primary,
                            focusedBorderColor = TaskTrackrTheme.colorScheme.primary
                        ),
                        shape = RoundedCornerShape(8.dp)
                    )

                    ExposedDropdownMenu(
                        expanded = isStatusDropdownExpanded,
                        onDismissRequest = { isStatusDropdownExpanded = false },
                        modifier = Modifier.background(TaskTrackrTheme.colorScheme.cardBackground)
                    ) {
                        statusOptions.forEach { (statusValue, statusLabel) ->
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        statusLabel,
                                        style = TaskTrackrTheme.typography.body,
                                        color = TaskTrackrTheme.colorScheme.text
                                    )
                                },
                                onClick = {
                                    selectedStatus = statusValue
                                    isStatusDropdownExpanded = false
                                }
                            )
                        }
                    }
                }

                Text(
                    text = "Start Date",
                    style = TaskTrackrTheme.typography.label,
                    color = TaskTrackrTheme.colorScheme.primary
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = startDate?.let { DateUtils.formatTime(it) } ?: "",
                        onValueChange = { },
                        modifier = Modifier
                            .weight(0.4f)
                            .clickable {
                                settingTimeForStartDate = true
                                tempDateForTimePicker = startDate ?: Date()
                                showStartTimePicker = true
                            },
                        placeholder = {
                            Text(
                                "HH:mm",
                                style = TaskTrackrTheme.typography.placeholder,
                                color = TaskTrackrTheme.colorScheme.text.copy(alpha = 0.6f)
                            )
                        },
                        readOnly = true,
                        enabled = false,
                        colors = OutlinedTextFieldDefaults.colors(
                            disabledContainerColor = TaskTrackrTheme.colorScheme.inputBackground,
                            disabledBorderColor = TaskTrackrTheme.colorScheme.primary,
                            disabledTextColor = TaskTrackrTheme.colorScheme.text
                        ),
                        shape = RoundedCornerShape(8.dp)
                    )

                    OutlinedTextField(
                        value = startDate?.let { DateUtils.formatDateOnly(it) } ?: "",
                        onValueChange = { },
                        modifier = Modifier
                            .weight(0.6f)
                            .clickable { showStartDatePicker = true },
                        placeholder = {
                            Text(
                                "dd/mm/yyyy",
                                style = TaskTrackrTheme.typography.placeholder,
                                color = TaskTrackrTheme.colorScheme.text.copy(alpha = 0.6f)
                            )
                        },
                        readOnly = true,
                        enabled = false,
                        trailingIcon = {
                            Icon(
                                painter = painterResource(id = R.drawable.calendar),
                                contentDescription = "Select date",
                                modifier = Modifier.clickable { showStartDatePicker = true },
                                tint = TaskTrackrTheme.colorScheme.primary
                            )
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            disabledContainerColor = TaskTrackrTheme.colorScheme.inputBackground,
                            disabledBorderColor = TaskTrackrTheme.colorScheme.primary,
                            disabledTextColor = TaskTrackrTheme.colorScheme.text
                        ),
                        shape = RoundedCornerShape(8.dp)
                    )
                }

                Text(
                    text = "End Date",
                    style = TaskTrackrTheme.typography.label,
                    color = TaskTrackrTheme.colorScheme.primary
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = endDate?.let { DateUtils.formatTime(it) } ?: "",
                        onValueChange = { },
                        modifier = Modifier
                            .weight(0.4f)
                            .clickable {
                                settingTimeForStartDate = false
                                tempDateForTimePicker = endDate ?: Date()
                                showEndTimePicker = true
                            },
                        placeholder = {
                            Text(
                                "HH:mm",
                                style = TaskTrackrTheme.typography.placeholder,
                                color = TaskTrackrTheme.colorScheme.text.copy(alpha = 0.6f)
                            )
                        },
                        readOnly = true,
                        enabled = false,
                        colors = OutlinedTextFieldDefaults.colors(
                            disabledContainerColor = TaskTrackrTheme.colorScheme.inputBackground,
                            disabledBorderColor = TaskTrackrTheme.colorScheme.primary,
                            disabledTextColor = TaskTrackrTheme.colorScheme.text
                        ),
                        shape = RoundedCornerShape(8.dp)
                    )

                    OutlinedTextField(
                        value = endDate?.let { DateUtils.formatDateOnly(it) } ?: "",
                        onValueChange = { },
                        modifier = Modifier
                            .weight(0.6f)
                            .clickable { showEndDatePicker = true },
                        placeholder = {
                            Text(
                                "dd/mm/yyyy",
                                style = TaskTrackrTheme.typography.placeholder,
                                color = TaskTrackrTheme.colorScheme.text.copy(alpha = 0.6f)
                            )
                        },
                        readOnly = true,
                        enabled = false,
                        trailingIcon = {
                            Icon(
                                painter = painterResource(id = R.drawable.calendar),
                                contentDescription = "Select date",
                                modifier = Modifier.clickable { showEndDatePicker = true },
                                tint = TaskTrackrTheme.colorScheme.primary
                            )
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            disabledContainerColor = TaskTrackrTheme.colorScheme.inputBackground,
                            disabledBorderColor = TaskTrackrTheme.colorScheme.primary,
                            disabledTextColor = TaskTrackrTheme.colorScheme.text
                        ),
                        shape = RoundedCornerShape(8.dp)
                    )
                }

                val tempStartDate = startDate
                val tempEndDate = endDate
                if (tempStartDate != null && tempEndDate != null && tempEndDate.before(tempStartDate)) {
                    Text(
                        text = "End date cannot be before start date",
                        style = TaskTrackrTheme.typography.caption,
                        color = Color.Red
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    CustomButton(
                        text = "Create Project",
                        icon = painterResource(id = R.drawable.edit),
                        enabled = isFormValid,
                        onClick = {
                            showNameError = false
                            showDescriptionError = false
                            showTeamError = false

                            var hasError = false

                            if (projectName.isBlank()) {
                                showNameError = true
                                hasError = true
                            }

                            if (projectDescription.isBlank()) {
                                showDescriptionError = true
                                hasError = true
                            }

                            if (selectedTeam == null) {
                                showTeamError = true
                                hasError = true
                            }

                            if (!hasError) {
                                selectedTeam?.let { team ->
                                    onSave(
                                        ProjectFormData(
                                            name = projectName.trim(),
                                            description = projectDescription.trim(),
                                            teamId = team.id,
                                            startDate = startDate,
                                            endDate = endDate,
                                            status = selectedStatus
                                        )
                                    )
                                }
                            }
                        }
                    )
                }
            }
        }

        DatePicker(
            isVisible = showStartDatePicker,
            initialDate = startDate,
            onDismiss = { showStartDatePicker = false },
            onDateSelected = { newDate ->
                startDate = if (startDate != null) {
                    DateUtils.combineDateAndTime(newDate, startDate!!)
                } else {
                    newDate
                }
                showStartDatePicker = false
            }
        )

        DatePicker(
            isVisible = showEndDatePicker,
            initialDate = endDate,
            onDismiss = { showEndDatePicker = false },
            onDateSelected = { newDate ->
                endDate = if (endDate != null) {
                    DateUtils.combineDateAndTime(newDate, endDate!!)
                } else {
                    newDate
                }
                showEndDatePicker = false
            }
        )

        TimePicker(
            isVisible = showStartTimePicker,
            initialDate = tempDateForTimePicker,
            title = "Select Start Time",
            onDismiss = { showStartTimePicker = false },
            onTimeSelected = { newTime ->
                if (settingTimeForStartDate) {
                    startDate = if (startDate != null) {
                        DateUtils.combineDateAndTime(startDate!!, newTime)
                    } else {
                        newTime
                    }
                }
                showStartTimePicker = false
            }
        )

        TimePicker(
            isVisible = showEndTimePicker,
            initialDate = tempDateForTimePicker,
            title = "Select End Time",
            onDismiss = { showEndTimePicker = false },
            onTimeSelected = { newTime ->
                if (!settingTimeForStartDate) {
                    endDate = if (endDate != null) {
                        DateUtils.combineDateAndTime(endDate!!, newTime)
                    } else {
                        newTime
                    }
                }
                showEndTimePicker = false
            }
        )
    }
}