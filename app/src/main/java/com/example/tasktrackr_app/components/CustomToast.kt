package com.example.tasktrackr_app.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.tasktrackr_app.ui.theme.TaskTrackrTheme
import kotlinx.coroutines.delay

@Composable
fun CustomToast(
    message: String,
    isVisible: Boolean,
    isSuccess: Boolean = true,
    durationMillis: Long = 2000,
    onDismiss: () -> Unit
) {
    val borderColor = if (isSuccess) TaskTrackrTheme.colorScheme.tertiary else TaskTrackrTheme.colorScheme.primary

    AnimatedVisibility(
        visible = isVisible,
        enter = fadeIn(animationSpec = tween(300)) +
               slideInVertically(animationSpec = tween(300)) { fullHeight -> fullHeight },
        exit = fadeOut(animationSpec = tween(300)) +
              slideOutVertically(animationSpec = tween(300)) { fullHeight -> fullHeight }
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            contentAlignment = Alignment.BottomCenter
        ) {
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(TaskTrackrTheme.colorScheme.background)
                    .border(2.dp, borderColor, RoundedCornerShape(8.dp))
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TaskTrackrIcon(
                    modifier = Modifier.size(24.dp)
                )

                Text(
                    text = message,
                    color = TaskTrackrTheme.colorScheme.text,
                    textAlign = TextAlign.Center,
                    style = TaskTrackrTheme.typography.body
                )
            }
        }
    }

    LaunchedEffect(isVisible) {
        if (isVisible) {
            delay(durationMillis)
            onDismiss()
        }
    }
}
