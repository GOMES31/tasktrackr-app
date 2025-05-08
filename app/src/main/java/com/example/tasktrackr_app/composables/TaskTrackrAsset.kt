package com.example.tasktrackr_app.composables

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import com.example.tasktrackr_app.R
import com.example.tasktrackr_app.ui.theme.LocalAppThemeState

@Composable
fun TaskTrackrLogo(modifier: Modifier = Modifier) {
    val (isDarkMode, _) = LocalAppThemeState.current
    val logoRes = if(isDarkMode){
        R.drawable.logo_darkmode
    } else{
        R.drawable.logo_lightmode
    }

    Image(
        painter            = painterResource(logoRes),
        contentDescription = "App Logo",
        modifier           = modifier
    )
}

@Composable
fun TaskTrackrIcon(modifier: Modifier = Modifier) {
    val (isDarkMode, _) = LocalAppThemeState.current
    val logoRes = if(isDarkMode){
        R.drawable.icon_darkmode
    } else{
        R.drawable.icon_lightmode
    }

    Image(
        painter            = painterResource(logoRes),
        contentDescription = "Logo",
        modifier           = modifier
    )
}