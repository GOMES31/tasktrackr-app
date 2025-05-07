package com.example.tasktrackr_app.composables

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.tasktrackr_app.R

@Composable
fun ToggleTheme(
    isDarkTheme: Boolean = false,
//    onToggleTheme: () -> Unit,
    modifier: Modifier = Modifier
) {
    val iconRes = if (isDarkTheme) R.drawable.sun else R.drawable.moon
    val contentDescription = if (isDarkTheme) "Switch to Light Mode" else "Switch to Dark Mode"

    Image(
        painter = painterResource(id = iconRes),
        contentDescription = contentDescription,
        modifier = modifier
            .size(30.dp)
            .clickable { /*onToggleTheme()*/ }
    )
}
