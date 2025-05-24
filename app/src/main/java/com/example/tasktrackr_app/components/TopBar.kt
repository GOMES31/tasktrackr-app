package com.example.tasktrackr_app.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.tasktrackr_app.R
import com.example.tasktrackr_app.ui.theme.TaskTrackrTheme

@Composable
fun TopBar(
    modifier: Modifier = Modifier,
    onMenuClick: () -> Unit = {}
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(60.dp)
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Always show the icon_lightmode
        Image(
            painter = painterResource(id = R.drawable.icon_lightmode),
            contentDescription = stringResource(R.string.app_name),
            modifier = Modifier.size(40.dp)
        )

        // Always show the burger menu icon
        Image(
            painter = painterResource(id = R.drawable.menu_burger),
            contentDescription = "Menu",
            modifier = Modifier
                .size(32.dp)
                .clickable { onMenuClick() }
        )
    }
}
