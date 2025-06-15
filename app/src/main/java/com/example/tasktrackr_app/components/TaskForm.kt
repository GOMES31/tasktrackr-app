package com.example.tasktrackr_app.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.tasktrackr_app.R
import com.example.tasktrackr_app.data.remote.response.data.TaskData
import com.example.tasktrackr_app.data.remote.response.data.ObservationData
import com.example.tasktrackr_app.data.remote.response.data.TeamMemberData
import com.example.tasktrackr_app.data.remote.response.data.TeamData
import com.example.tasktrackr_app.ui.theme.TaskTrackrTheme
import com.example.tasktrackr_app.ui.viewmodel.ObservationViewModel
import com.example.tasktrackr_app.ui.viewmodel.TaskViewModel
import com.example.tasktrackr_app.ui.viewmodel.AuthViewModel
import com.example.tasktrackr_app.utils.DateUtils
import java.util.*
import android.util.Log

private const val TAG = "TaskFormAssigneeLog"

private fun getTeamMemberIdentifier(member: TeamMemberData): String {
    return member.email
}

private fun getTeamMemberName(member: TeamMemberData): String {
    return member.name
}

private fun getTeamMemberRole(member: TeamMemberData): String {
    return when (member.role) {
        "ADMIN" -> "Admin"
        "PROJECT_MANAGER" -> "Project Manager"
        else -> member.role.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
    }
}

@Composable
fun TaskForm(
    modifier: Modifier = Modifier,
    isVisible: Boolean = false,
    onDismiss: () -> Unit = {},
    onSave: (TaskFormData) -> Unit = {},
    isEditMode: Boolean = false,
    existingTask: TaskData? = null,
    observationViewModel: ObservationViewModel,
    taskViewModel: TaskViewModel,
    authViewModel: AuthViewModel,
    teamData: TeamData? = null,
) {
    if (!isVisible) return

    val userData by authViewModel.userData.collectAsState()
    val currentUserEmail = userData?.email

    val canAssignMembers = teamData?.members?.any { member ->
        val isCurrentUser = member.email == currentUserEmail
        val isAdminOrPM = member.role == "ADMIN" || member.role == "PROJECT_MANAGER"
        isCurrentUser && isAdminOrPM
    } ?: run {
        false
    }

    val availableTeamMembers = teamData?.members

    var title by remember(isVisible, existingTask) {
        mutableStateOf(existingTask?.title ?: "")
    }
    var description by remember(isVisible, existingTask) {
        mutableStateOf(existingTask?.description ?: "")
    }
    var startDate by remember(isVisible, existingTask) {
        mutableStateOf<Date?>(existingTask?.startDate)
    }
    var endDate by remember(isVisible, existingTask) {
        mutableStateOf<Date?>(existingTask?.endDate)
    }
    var status by remember(isVisible, existingTask) {
        mutableStateOf(existingTask?.status ?: "PENDING")
    }

    var selectedAssignees by remember(isVisible, existingTask?.id) {
        mutableStateOf(mutableListOf<TeamMemberData>())
    }

    LaunchedEffect(selectedAssignees) {
    }

    LaunchedEffect(existingTask, teamData, isVisible) {
        if (isVisible && existingTask != null && teamData != null) {
            val assigneesFromTask = existingTask.assignees?.let { assignees ->

                when {
                    assignees.isEmpty() -> {
                        emptyList()
                    }
                    else -> {
                        val result = assignees.mapNotNull { assignee ->

                            val assigneeEmail = when (assignee) {
                                is TeamMemberData -> {
                                    assignee.email
                                }
                                is String -> {
                                    assignee
                                }
                                is Map<*, *> -> {
                                    val email = assignee["email"] as? String
                                    email
                                }
                                else -> {
                                    null
                                }
                            }

                            assigneeEmail?.let { email ->
                                val foundMember = teamData.members?.find { member ->
                                    member.email == email
                                }
                                foundMember
                            }
                        }
                        result
                    }
                }
            } ?: emptyList()

            selectedAssignees = assigneesFromTask.toMutableList()

        }
    }

    var isAssigneeDropdownExpanded by remember { mutableStateOf(false) }
    var showAssigneeError by remember { mutableStateOf(false) }

    var showObservationDialog by remember { mutableStateOf(false) }
    var editingObservation by remember { mutableStateOf<ObservationData?>(null) }
    var newObservationText by remember { mutableStateOf("") }

    var observations by remember(isVisible, existingTask) {
        mutableStateOf(existingTask?.observations?.toMutableList() ?: mutableListOf<ObservationData>())
    }

    val fetchedObservationsFromVm by taskViewModel?.observations?.collectAsState() ?: remember { mutableStateOf(emptyList()) }

    LaunchedEffect(fetchedObservationsFromVm, isEditMode, existingTask?.id) {
        if (isEditMode && existingTask != null && fetchedObservationsFromVm.isNotEmpty()) {
            observations = fetchedObservationsFromVm.toMutableList()
        }
    }

    val isLoadingObservations by taskViewModel?.isLoading?.collectAsState() ?: remember { mutableStateOf(false) }

    var showStartDatePicker by remember { mutableStateOf(false) }
    var showEndDatePicker by remember { mutableStateOf(false) }
    var showStartTimePicker by remember { mutableStateOf(false) }
    var showEndTimePicker by remember { mutableStateOf(false) }
    var expanded by remember { mutableStateOf(false) }

    var settingTimeForStartDate by remember { mutableStateOf(false) }
    var tempDateForTimePicker by remember { mutableStateOf<Date?>(null) }

    var showTitleError by remember { mutableStateOf(false) }

    val statusOptions = listOf(
        "IN_PROGRESS" to stringResource(R.string.in_progress),
        "FINISHED" to stringResource(R.string.finished),
        "PENDING" to stringResource(R.string.pending)
    )

    LaunchedEffect(existingTask?.id, isVisible) {
        if (isVisible && isEditMode && existingTask?.id != null) {
            taskViewModel?.getObservationsForTask(existingTask.id)
        }
    }


observationViewModel?.let { viewModel ->
    val currentObservation by viewModel.currentObservation.collectAsState()
    val loading by viewModel.loading.collectAsState()
    val error by viewModel.error.collectAsState()

    LaunchedEffect(currentObservation) {
        currentObservation?.let { observation ->
            if (editingObservation != null) {
                val updatedList = observations.toMutableList()
                val index = updatedList.indexOfFirst { it.id == observation.id }
                if (index != -1) {
                    updatedList[index] = observation
                    observations = updatedList
                }
            } else {
                observations = (observations + observation).toMutableList()
            }

            existingTask?.id?.let { taskId ->
                taskViewModel?.getObservationsForTask(taskId)
            }

            showObservationDialog = false
            newObservationText = ""
            editingObservation = null
        }
    }

    error?.let { errorMessage ->
        LaunchedEffect(errorMessage) {
            viewModel.clearData()
        }
    }
}
    
        Dialog(
            onDismissRequest = onDismiss,
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Card(
                modifier = modifier
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
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isEditMode) stringResource(R.string.edit_task_title) else stringResource(R.string.add_task_title),
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
                            showTitleError = false
                        },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = {
                            Text(
                                stringResource(R.string.task_title_placeholder),
                                style = TaskTrackrTheme.typography.placeholder,
                                color = TaskTrackrTheme.colorScheme.text.copy(alpha = 0.6f)
                            )
                        },
                        isError = showTitleError,
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedContainerColor = TaskTrackrTheme.colorScheme.inputBackground,
                            focusedContainerColor = TaskTrackrTheme.colorScheme.inputBackground,
                            unfocusedBorderColor = TaskTrackrTheme.colorScheme.primary,
                            focusedBorderColor = TaskTrackrTheme.colorScheme.primary
                        ),
                        shape = RoundedCornerShape(8.dp)
                    )
                    if (showTitleError) {
                        Text(
                            text = stringResource(R.string.error_title_empty),
                            color = Color.Red,
                            style = TaskTrackrTheme.typography.caption,
                            modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                        )
                    }
    
                    Spacer(modifier = Modifier.height(16.dp))
    
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
                            .height(80.dp),
                        placeholder = {
                            Text(
                                stringResource(R.string.task_description_placeholder),
                                style = TaskTrackrTheme.typography.placeholder,
                                color = TaskTrackrTheme.colorScheme.text.copy(alpha = 0.6f)
                            )
                        },
                        maxLines = 3,
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedContainerColor = TaskTrackrTheme.colorScheme.inputBackground,
                            focusedContainerColor = TaskTrackrTheme.colorScheme.inputBackground,
                            unfocusedBorderColor = TaskTrackrTheme.colorScheme.primary,
                            focusedBorderColor = TaskTrackrTheme.colorScheme.primary
                        ),
                        shape = RoundedCornerShape(8.dp)
                    )
    
                    Spacer(modifier = Modifier.height(16.dp))
    
                    Text(
                        text = stringResource(R.string.start_date_label),
                        style = TaskTrackrTheme.typography.label,
                        color = TaskTrackrTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
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
                                    contentDescription = stringResource(R.string.select_date),
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
    
                    Spacer(modifier = Modifier.height(16.dp))
    
                    Text(
                        text = stringResource(R.string.end_date_label),
                        style = TaskTrackrTheme.typography.label,
                        color = TaskTrackrTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
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
                                    contentDescription = stringResource(R.string.select_date),
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
    
                    Spacer(modifier = Modifier.height(16.dp))
    
                    Text(
                        text = stringResource(R.string.completion_status_label),
                        style = TaskTrackrTheme.typography.label,
                        color = TaskTrackrTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Box {
                        OutlinedTextField(
                            value = statusOptions.find { it.first == status }?.second ?: status,
                            onValueChange = { },
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { expanded = true },
                            placeholder = {
                                Text(
                                    stringResource(R.string.status_placeholder),
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
                            shape = RoundedCornerShape(8.dp),
                            trailingIcon = {
                                Icon(
                                    painter = painterResource(id = R.drawable.arrow_drop_down),
                                    contentDescription = "Dropdown",
                                    tint = TaskTrackrTheme.colorScheme.primary
                                )
                            }
                        )
                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false },
                            modifier = Modifier.background(TaskTrackrTheme.colorScheme.cardBackground)
                        ) {
                            statusOptions.forEach { (statusValue, statusDisplay) ->
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            text = statusDisplay,
                                            style = TaskTrackrTheme.typography.body,
                                            color = TaskTrackrTheme.colorScheme.text
                                        )
                                    },
                                    onClick = {
                                        status = statusValue
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }

                    if (isEditMode && canAssignMembers) {
                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "Assigned Team Members",
                            style = TaskTrackrTheme.typography.label,
                            color = TaskTrackrTheme.colorScheme.primary
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        val memberCardHeight = 56.dp
                        val assignedMembersContainerHeight = memberCardHeight * 2 + 24.dp


                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(assignedMembersContainerHeight),
                            shape = RoundedCornerShape(8.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = TaskTrackrTheme.colorScheme.inputBackground
                            ),
                            border = BorderStroke(1.dp, TaskTrackrTheme.colorScheme.primary)
                        ) {
                            if (selectedAssignees.isNotEmpty()) {
                                LazyColumn(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(12.dp),
                                    verticalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    items(selectedAssignees) { assignee ->

                                        Card(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(memberCardHeight),
                                            shape = RoundedCornerShape(4.dp),
                                            colors = CardDefaults.cardColors(
                                                containerColor = TaskTrackrTheme.colorScheme.cardBackground
                                            ),
                                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                                        ) {
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxSize()
                                                    .padding(horizontal = 12.dp, vertical = 8.dp),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Column(
                                                    modifier = Modifier.weight(1f),
                                                    verticalArrangement = Arrangement.Center
                                                ) {
                                                    Text(
                                                        text = getTeamMemberName(assignee),
                                                        style = TaskTrackrTheme.typography.body,
                                                        color = TaskTrackrTheme.colorScheme.text,
                                                        maxLines = 1,
                                                        overflow = TextOverflow.Ellipsis
                                                    )
                                                    Text(
                                                        text = getTeamMemberRole(assignee),
                                                        style = TaskTrackrTheme.typography.caption,
                                                        color = TaskTrackrTheme.colorScheme.primary
                                                    )
                                                }

                                                CustomButton(
                                                    icon = painterResource(id = R.drawable.minus),
                                                    onClick = {
                                                        selectedAssignees = selectedAssignees.filter {
                                                            getTeamMemberIdentifier(it) != getTeamMemberIdentifier(assignee)
                                                        }.toMutableList()
                                                    },
                                                    modifier = Modifier.size(24.dp),
                                                    iconSize = 12.dp,
                                                    enabled = true
                                                )
                                            }
                                        }
                                    }
                                }
                            } else {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(12.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "No team members assigned",
                                        style = TaskTrackrTheme.typography.body,
                                        color = TaskTrackrTheme.colorScheme.text.copy(alpha = 0.6f),
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "Available Team Members",
                            style = TaskTrackrTheme.typography.label,
                            color = TaskTrackrTheme.colorScheme.primary
                        )

                        Spacer(modifier = Modifier.height(8.dp))




                        val unassignedMembers = availableTeamMembers?.filter { member ->
                            val isNotAssigned = selectedAssignees.none {
                                val result = getTeamMemberIdentifier(it) == getTeamMemberIdentifier(member)
                                result
                            }
                            isNotAssigned
                        } ?: emptyList()

                        val availableMembersContainerHeight = if (unassignedMembers.isNotEmpty()) {
                            val visibleMembers = minOf(unassignedMembers.size, 3)
                            memberCardHeight * visibleMembers + 24.dp
                        } else {
                            80.dp
                        }


                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(availableMembersContainerHeight),
                            shape = RoundedCornerShape(8.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = TaskTrackrTheme.colorScheme.inputBackground
                            ),
                            border = BorderStroke(1.dp, TaskTrackrTheme.colorScheme.primary)
                        ) {
                            if (unassignedMembers.isNotEmpty()) {
                                LazyColumn(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(12.dp),
                                    verticalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    items(unassignedMembers) { member ->
                                        Card(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(memberCardHeight),
                                            shape = RoundedCornerShape(4.dp),
                                            colors = CardDefaults.cardColors(
                                                containerColor = TaskTrackrTheme.colorScheme.cardBackground
                                            ),
                                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                                        ) {
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxSize()
                                                    .padding(horizontal = 12.dp, vertical = 8.dp),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Column(
                                                    modifier = Modifier.weight(1f),
                                                    verticalArrangement = Arrangement.Center
                                                ) {
                                                    Text(
                                                        text = getTeamMemberName(member),
                                                        style = TaskTrackrTheme.typography.body,
                                                        color = TaskTrackrTheme.colorScheme.text,
                                                        maxLines = 1,
                                                        overflow = TextOverflow.Ellipsis
                                                    )
                                                    Text(
                                                        text = getTeamMemberRole(member),
                                                        style = TaskTrackrTheme.typography.caption,
                                                        color = TaskTrackrTheme.colorScheme.primary
                                                    )
                                                }

                                                CustomButton(
                                                    icon = painterResource(id = R.drawable.plus),
                                                    onClick = {
                                                        selectedAssignees = (selectedAssignees + member).toMutableList()
                                                        showAssigneeError = false
                                                    },
                                                    modifier = Modifier.size(24.dp),
                                                    iconSize = 12.dp,
                                                    enabled = true
                                                )
                                            }
                                        }
                                    }
                                }
                            } else {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(12.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "All team members are assigned",
                                        style = TaskTrackrTheme.typography.body,
                                        color = TaskTrackrTheme.colorScheme.text.copy(alpha = 0.6f),
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }

                        if (showAssigneeError) {
                            Text(
                                text = "Please assign at least one team member",
                                color = Color.Red,
                                style = TaskTrackrTheme.typography.caption,
                                modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                            )
                        }
                    } else {
                    }

                    Spacer(modifier = Modifier.height(24.dp))
    
                    if (isEditMode && existingTask != null) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = stringResource(R.string.observations_label),
                                style = TaskTrackrTheme.typography.label,
                                color = TaskTrackrTheme.colorScheme.primary
                            )
                            CustomButton(
                                icon = painterResource(id = R.drawable.plus),
                                onClick = {
                                    editingObservation = null
                                    newObservationText = ""
                                    showObservationDialog = true
                                },
                                modifier = Modifier
                                    .padding(end = 4.dp)
                                    .size(24.dp),
                                iconSize = 12.dp,
                                enabled = true
                            )
                        }
    
                        Spacer(modifier = Modifier.height(8.dp))
    
                        val fixedObservationItemHeight = 56.dp
    
                        val observationsCardHeight = (fixedObservationItemHeight * 2) + 40.5.dp
    
                        if (isLoadingObservations) {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(observationsCardHeight),
                                shape = RoundedCornerShape(8.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = TaskTrackrTheme.colorScheme.inputBackground
                                ),
                                border = BorderStroke(1.dp, TaskTrackrTheme.colorScheme.primary)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(12.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator(
                                        color = TaskTrackrTheme.colorScheme.primary,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            }
                        } else if (observations.isNotEmpty()) {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(observationsCardHeight),
                                shape = RoundedCornerShape(8.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = TaskTrackrTheme.colorScheme.inputBackground
                                ),
                                border = BorderStroke(1.dp, TaskTrackrTheme.colorScheme.primary)
                            ) {
                                LazyColumn(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(12.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    items(observations) { observation ->
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(fixedObservationItemHeight),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.Top
                                        ) {
                                            Column(
                                                modifier = Modifier.weight(1f),
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
                                                        text = DateUtils.formatDateOnly(createdAt) ?: "N/A",
                                                        style = TaskTrackrTheme.typography.body.copy(
                                                            fontSize = TaskTrackrTheme.typography.body.fontSize * 0.85f
                                                        ),
                                                        color = TaskTrackrTheme.colorScheme.text.copy(alpha = 0.6f)
                                                    )
                                                }
                                            }
                                            Spacer(modifier = Modifier.width(8.dp))
    
                                            CustomButton(
                                                onClick = {
                                                    editingObservation = observation
                                                    newObservationText = observation.message
                                                    showObservationDialog = true
                                                },
                                                icon = painterResource(id = R.drawable.edit),
                                                iconSize = 16.dp,
                                                enabled = true
                                            )
                                        }
                                        if (observations.last() != observation) {
                                            HorizontalDivider(
                                                modifier = Modifier.padding(vertical = 4.dp),
                                                thickness = 0.5.dp,
                                                color = TaskTrackrTheme.colorScheme.primary.copy(alpha = 0.2f)
                                            )
                                        }
                                    }
                                }
                            }
                        } else {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(observationsCardHeight),
                                shape = RoundedCornerShape(8.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = TaskTrackrTheme.colorScheme.inputBackground
                                ),
                                border = BorderStroke(1.dp, TaskTrackrTheme.colorScheme.primary)
                            ) {
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
                            }
                        }
    
                        Spacer(modifier = Modifier.height(24.dp))
                    }

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
                                showAssigneeError = false
                                var hasError = false

                                if (title.isBlank()) {
                                    showTitleError = true
                                    hasError = true
                                }


                                if (isEditMode && canAssignMembers && selectedAssignees.isEmpty()) {
                                    showAssigneeError = true
                                    hasError = true
                                }

                                if (!hasError) {
                                    val assigneeIds = selectedAssignees.mapNotNull { assignee ->
                                        try {
                                            when {
                                                assignee.id is String && assignee.id.isNotBlank() -> assignee.id
                                                assignee.id is Number -> assignee.id.toString()
                                                else -> {
                                                    null
                                                }
                                            }
                                        } catch (e: Exception) {
                                            null
                                        }
                                    }.also { ids ->
                                    }

                                    val taskFormData = TaskFormData(
                                        title = title.trim(),
                                        description = description.trim(),
                                        startDate = startDate,
                                        endDate = endDate,
                                        status = status,
                                        observations = observations,
                                        assigneeIds = assigneeIds
                                    )

                                    if (isEditMode && canAssignMembers && (taskFormData.assigneeIds.isNullOrEmpty())) {
                                    } else {
                                        onSave(taskFormData)
                                    }
                                } else {
                                }
                            }
                        )
                    }
                }
            }
    
            if (showObservationDialog) {
                Dialog(
                    onDismissRequest = {
                        showObservationDialog = false
                        editingObservation = null
                        newObservationText = ""
                    },
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
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = if (editingObservation != null) "Edit Observation" else "Add Observation",
                                style = TaskTrackrTheme.typography.subHeader,
                                color = TaskTrackrTheme.colorScheme.primary
                            )
    
                            Spacer(modifier = Modifier.height(16.dp))
    
                            OutlinedTextField(
                                value = newObservationText,
                                onValueChange = { newObservationText = it },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(120.dp),
                                placeholder = {
                                    Text(
                                        "Enter your observation...",
                                        style = TaskTrackrTheme.typography.placeholder,
                                        color = TaskTrackrTheme.colorScheme.text.copy(alpha = 0.6f)
                                    )
                                },
                                maxLines = 5,
                                colors = OutlinedTextFieldDefaults.colors(
                                    unfocusedContainerColor = TaskTrackrTheme.colorScheme.inputBackground,
                                    focusedContainerColor = TaskTrackrTheme.colorScheme.inputBackground,
                                    unfocusedBorderColor = TaskTrackrTheme.colorScheme.primary,
                                    focusedBorderColor = TaskTrackrTheme.colorScheme.primary
                                ),
                                shape = RoundedCornerShape(8.dp)
                            )
    
                            Spacer(modifier = Modifier.height(16.dp))
    
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                CustomButton(
                                    onClick = {
                                        showObservationDialog = false
                                        editingObservation = null
                                        newObservationText = ""
                                    },
                                    modifier = Modifier.weight(1f),
                                    text = "Cancel",
                                    enabled = true
                                )
    
                                CustomButton(
                                    onClick = {
                                        if (newObservationText.isNotBlank() && existingTask != null) {
                                            val observationToEdit = editingObservation
                                            if (observationToEdit != null && observationViewModel != null) {
                                                observationViewModel.updateObservation(
                                                    observationToEdit.id,
                                                    newObservationText.trim(),
                                                    null
                                                )
                                            } else if (observationViewModel != null) {
                                                observationViewModel.createObservation(
                                                    existingTask.id,
                                                    newObservationText.trim(),
                                                    null
                                                )
                                            }
                                        }
                                    },
                                    modifier = Modifier.weight(1f),
                                    text = if (editingObservation != null) "Update" else "Add",
                                    enabled = newObservationText.isNotBlank() && observationViewModel != null
                                )
                            }
                        }
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