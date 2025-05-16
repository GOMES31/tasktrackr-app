package com.example.tasktrackr_app.components

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.tasktrackr_app.ui.theme.TaskTrackrTheme

@Composable
fun CustomButton(
    modifier: Modifier = Modifier,
    text: String,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .height(48.dp),
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = TaskTrackrTheme.colorScheme.primary,
            contentColor = TaskTrackrTheme.colorScheme.buttonText
        )
    ) {
        Text(
            text = text,
            style = TaskTrackrTheme.typography.button
        )
    }
}
