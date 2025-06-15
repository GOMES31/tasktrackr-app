package com.example.tasktrackr_app.components

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.tasktrackr_app.ui.theme.TaskTrackrTheme


@Composable
fun ErrorMessage(
    modifier: Modifier = Modifier,
    text: String
) {
    Text(
        text = text,
        color = TaskTrackrTheme.colorScheme.primary,
        style = TaskTrackrTheme.typography.body,
        modifier = modifier
    )
}