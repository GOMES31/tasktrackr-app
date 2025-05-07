package com.example.tasktrackr_app.composables

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.tasktrackr_app.ui.theme.TaskTrackrTheme

@Composable
fun TextInputField(label: String) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = label,
            color = TaskTrackrTheme.colorScheme.primary,
            style = TaskTrackrTheme.typography.label
        )
        BasicTextField(
            value = "",
            onValueChange = {},
            textStyle = TaskTrackrTheme.typography.placeholder,
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    border = BorderStroke(
                        width = 2.dp,
                        color = TaskTrackrTheme.colorScheme.primary
                    ),
                    shape = RoundedCornerShape(8.dp)
                )
                .background(
                    color = TaskTrackrTheme.colorScheme.inputBackground,
                    shape = RoundedCornerShape(8.dp)
                )
                .padding(16.dp)
        )
    }
}
