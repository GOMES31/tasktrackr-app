package com.example.tasktrackr_app.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.example.tasktrackr_app.ui.theme.TaskTrackrTheme

@Composable
fun AuthLink(text: String, redirect: String) {
    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = text,
            color = TaskTrackrTheme.colorScheme.primary,
            fontStyle = FontStyle.Italic,
            style = TaskTrackrTheme.typography.caption
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = redirect,
            color = TaskTrackrTheme.colorScheme.accent,
            fontStyle = FontStyle.Italic,
            textDecoration = TextDecoration.Underline,
            style = TaskTrackrTheme.typography.caption
        )
    }
}

