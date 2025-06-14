package com.example.tasktrackr_app.components

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.res.stringResource
import com.example.tasktrackr_app.R
import com.example.tasktrackr_app.utils.DateUtils
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePicker(
    isVisible: Boolean,
    initialDate: Date?,
    onDismiss: () -> Unit,
    onDateSelected: (Date) -> Unit
) {
    if (!isVisible) return

    val datePickerState = rememberDatePickerState(initialSelectedDateMillis = initialDate?.time)

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                val selectedDateMillis = datePickerState.selectedDateMillis
                if (selectedDateMillis != null) {
                    val newDate = Date(selectedDateMillis)
                    val combinedDate = DateUtils.combineDateAndTime(newDate, initialDate)
                    onDateSelected(combinedDate)
                }
                onDismiss()
            }) {
                Text(stringResource(R.string.button_ok))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.button_cancel))
            }
        }
    ) {
        DatePicker(state = datePickerState)
    }
}