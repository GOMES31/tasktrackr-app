package com.example.tasktrackr_app.ui.screens.introduction

import android.annotation.SuppressLint
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.tasktrackr_app.R
import com.example.tasktrackr_app.components.TaskTrackrLogo
import com.example.tasktrackr_app.ui.theme.TaskTrackrTheme
import kotlinx.coroutines.delay
import java.util.Locale

@SuppressLint("UseOfNonLambdaOffsetOverload")
@Composable
fun IntroSlider(
    navController: NavController,
    modifier: Modifier = Modifier,
    onLanguageSelected: (Locale) -> Unit = {}
) {
    val configuration = LocalConfiguration.current
    configuration.screenHeightDp.dp

    val logoAlpha = remember { Animatable(0f) }
    val descAlpha = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        logoAlpha.animateTo(1f, tween(durationMillis = 1000))
        delay(500)
        descAlpha.animateTo(1f, tween(durationMillis = 1000))
        delay(1000)
        navController.navigate("signin") {
            popUpTo("intro") { inclusive = true }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(TaskTrackrTheme.colorScheme.background)
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TaskTrackrLogo(
            modifier = Modifier
                .alpha(logoAlpha.value)
                .size(120.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            modifier = Modifier.fillMaxWidth(),
            text = stringResource(R.string.app_description),
            color = TaskTrackrTheme.colorScheme.primary,
            textAlign = TextAlign.Center,
            style = TaskTrackrTheme.typography.body
        )
    }
}
