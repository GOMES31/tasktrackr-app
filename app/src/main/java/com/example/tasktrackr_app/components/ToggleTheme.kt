package com.example.tasktrackr_app.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.tasktrackr_app.R
import com.example.tasktrackr_app.ui.theme.LocalAppThemeState

@Composable
fun ToggleTheme(modifier: Modifier = Modifier, ) {
    val (isDarkMode, toggle) = LocalAppThemeState.current
    val iconRes = if(isDarkMode){
        R.drawable.sun
    } else {
        R.drawable.moon
    }


    Image(
        painter = painterResource(id = iconRes),
        contentDescription = null,
        modifier = modifier
            .size(30.dp)
            .clickable { toggle() }
    )
}
