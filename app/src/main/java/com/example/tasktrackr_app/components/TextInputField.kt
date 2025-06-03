package com.example.tasktrackr_app.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.example.tasktrackr_app.ui.theme.TaskTrackrTheme

@Composable
fun TextInputField(
    modifier: Modifier = Modifier,
    label: String,
    placeholder: String = "",
    value: String,
    onValueChange: (String) -> Unit,
    isPassword: Boolean = false
) {
    var text by remember { mutableStateOf("") }

    Column(
        modifier = modifier
    ) {
        Text(
            text = label,
            color = TaskTrackrTheme.colorScheme.primary,
            style = TaskTrackrTheme.typography.label
        )

        Spacer(modifier = Modifier.height(8.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    BorderStroke(1.dp, TaskTrackrTheme.colorScheme.primary),
                    shape = RoundedCornerShape(8.dp)
                )
                .background(
                    color = TaskTrackrTheme.colorScheme.inputBackground,
                    shape = RoundedCornerShape(8.dp)
                )
                .padding(16.dp)
        ) {
            if (value.isEmpty() && placeholder.isNotEmpty()) {
                Text(
                    text = placeholder,
                    style = TaskTrackrTheme.typography.placeholder,
                    color = TaskTrackrTheme.colorScheme.text
                )
            }

            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                singleLine = true,
                textStyle = TaskTrackrTheme.typography.body.copy(color = TaskTrackrTheme.colorScheme.text),
                visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
