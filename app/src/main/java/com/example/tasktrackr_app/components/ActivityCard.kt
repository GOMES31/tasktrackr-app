package com.example.tasktrackr_app.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.tasktrackr_app.ui.theme.TaskTrackrTheme

@Composable
fun ActivityCard() {
    Card(
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, TaskTrackrTheme.colorScheme.secondary),
        colors = CardDefaults.cardColors(
            containerColor = TaskTrackrTheme.colorScheme.cardBackground
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.Top
        ) {
            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = "Info",
                tint = TaskTrackrTheme.colorScheme.text,
                modifier = Modifier
                    .size(24.dp)
                    .align(Alignment.Top)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Task Name",
                    color = TaskTrackrTheme.colorScheme.secondary,
                    style = TaskTrackrTheme.typography.bodyStrong
                )
                Text(
                    text = "Description of what was done. Ex: change the finish date of the task, added comments to a task etc.",
                    color = TaskTrackrTheme.colorScheme.text,
                    style = TaskTrackrTheme.typography.bodySmall
                )
            }
        }
    }
}
