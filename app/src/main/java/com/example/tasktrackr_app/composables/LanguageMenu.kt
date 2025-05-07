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
fun LanguageMenu(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    Image(
        painter = painterResource(id = R.drawable.globe),
        contentDescription = "Language Menu Icon",
        modifier = modifier
            .size(30.dp)
            .then(
                if (onClick != null) Modifier.clickable { onClick() } else Modifier
            )
    )
}