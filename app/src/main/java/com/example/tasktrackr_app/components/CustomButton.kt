package com.example.tasktrackr_app.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.tasktrackr_app.ui.theme.TaskTrackrTheme

@Composable
fun CustomButton(
    modifier: Modifier = Modifier,
    text: String? = null,
    enabled: Boolean = false,
    icon: Painter? = null,
    iconSize: Dp = 20.dp,
    onClick: () -> Unit
) {
    val isIconOnly = text == null

    Button(
        onClick = onClick,
        modifier = if (isIconOnly) modifier else modifier.height(48.dp),
        enabled = enabled,
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = TaskTrackrTheme.colorScheme.primary,
            contentColor = TaskTrackrTheme.colorScheme.buttonText
        ),
        contentPadding = if (isIconOnly) PaddingValues(0.dp) else ButtonDefaults.ContentPadding
    ) {
        text?.let { textValue ->
            Text(
                text = textValue,
                style = TaskTrackrTheme.typography.button,
                color = TaskTrackrTheme.colorScheme.buttonText
            )
        }

        icon?.let { iconPainter ->
            if (text != null) {
                Spacer(modifier = Modifier.width(10.dp))
            }
            Image(
                painter = iconPainter,
                contentDescription = null,
                modifier = Modifier.size(iconSize)
            )
        }
    }
}