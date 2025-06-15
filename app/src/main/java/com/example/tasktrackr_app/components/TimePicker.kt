package com.example.tasktrackr_app.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerDefaults
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.tasktrackr_app.R
import com.example.tasktrackr_app.ui.theme.TaskTrackrTheme
import com.example.tasktrackr_app.utils.DateUtils
import java.util.Calendar
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePicker(
    isVisible: Boolean,
    initialDate: Date?,
    title: String,
    onDismiss: () -> Unit,
    onTimeSelected: (Date) -> Unit
) {
    if (!isVisible) return

    val calendar = Calendar.getInstance().apply {
        time = initialDate ?: Date()
    }
    val timePickerState = rememberTimePickerState(
        initialHour = calendar.get(Calendar.HOUR_OF_DAY),
        initialMinute = calendar.get(Calendar.MINUTE),
        is24Hour = true
    )

    val timePickerColors = TimePickerDefaults.colors(
        clockDialColor = TaskTrackrTheme.colorScheme.cardBackground,
        selectorColor = TaskTrackrTheme.colorScheme.accent,
        containerColor = TaskTrackrTheme.colorScheme.cardBackground,
        periodSelectorBorderColor = TaskTrackrTheme.colorScheme.secondary,
        clockDialSelectedContentColor = TaskTrackrTheme.colorScheme.buttonText,
        clockDialUnselectedContentColor = TaskTrackrTheme.colorScheme.text,
        periodSelectorSelectedContainerColor = TaskTrackrTheme.colorScheme.accent,
        periodSelectorUnselectedContainerColor = TaskTrackrTheme.colorScheme.cardBackground,
        periodSelectorSelectedContentColor = TaskTrackrTheme.colorScheme.buttonText,
        periodSelectorUnselectedContentColor = TaskTrackrTheme.colorScheme.text,
        timeSelectorSelectedContainerColor = TaskTrackrTheme.colorScheme.accent,
        timeSelectorUnselectedContainerColor = TaskTrackrTheme.colorScheme.cardBackground,
        timeSelectorSelectedContentColor = TaskTrackrTheme.colorScheme.buttonText,
        timeSelectorUnselectedContentColor = TaskTrackrTheme.colorScheme.text
    )

    Dialog(
        onDismissRequest = onDismiss
    ) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = TaskTrackrTheme.colorScheme.cardBackground,
            tonalElevation = 8.dp
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = title,
                    style = TaskTrackrTheme.typography.header,
                    color = TaskTrackrTheme.colorScheme.primary,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                TimePicker(
                    state = timePickerState,
                    colors = timePickerColors
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = TaskTrackrTheme.colorScheme.text
                        ),
                        border = BorderStroke(
                            width = 1.dp,
                            color = TaskTrackrTheme.colorScheme.secondary
                        )
                    ) {
                        Text(stringResource(R.string.button_cancel))
                    }

                    Button(
                        onClick = {
                            val currentDate = initialDate ?: Date()
                            val updatedDate = DateUtils.updateTime(
                                currentDate,
                                timePickerState.hour,
                                timePickerState.minute
                            )
                            onTimeSelected(updatedDate)
                            onDismiss()
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = TaskTrackrTheme.colorScheme.primary
                        )
                    ) {
                        Text(
                            stringResource(R.string.button_ok),
                            color = TaskTrackrTheme.colorScheme.buttonText
                        )
                    }
                }
            }
        }
    }
}