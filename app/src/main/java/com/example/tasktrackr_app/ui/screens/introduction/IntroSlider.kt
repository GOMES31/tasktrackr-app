package com.example.tasktrackr_app.ui.screens.introduction

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.tasktrackr_app.R
import com.example.tasktrackr_app.components.*
import com.example.tasktrackr_app.ui.theme.TaskTrackrTheme
import kotlinx.coroutines.delay
import java.util.Locale

@Composable
fun IntroSlider(
    navController: NavController,
    modifier: Modifier = Modifier,
    onLanguageSelected: (Locale) -> Unit = {}
) {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.toFloat()
    val screenHeight = configuration.screenHeightDp.toFloat()

    // Visibility state for components
    var isEmailVisible by remember { mutableStateOf(false) }
    var isPasswordVisible by remember { mutableStateOf(false) }
    var isAuthLinkVisible by remember { mutableStateOf(false) }
    var isButtonVisible by remember { mutableStateOf(false) }
    var isHeaderVisible by remember { mutableStateOf(false) }
    var isLogoVisible by remember { mutableStateOf(false) }

    // Animations
    val headerRowAlpha = remember { Animatable(0f) }
    val logoTranslationY = remember { Animatable(-screenHeight) }
    val signInTitleAlpha = remember { Animatable(0f) }
    val emailInputTranslationX = remember { Animatable(screenWidth) }
    val passwordInputTranslationX = remember { Animatable(screenWidth) }
    val authLinkTranslationX = remember { Animatable(screenWidth) }
    val buttonAlpha = remember { Animatable(0f) }
    val buttonScale = remember { Animatable(0f) }

    // Animation Sequence
    LaunchedEffect(Unit) {
        signInTitleAlpha.animateTo(1f, tween(1000))
        delay(300)

        isEmailVisible = true
        emailInputTranslationX.animateTo(0f, tween(1000, easing = EaseOutQuint))
        delay(200)

        isPasswordVisible = true
        passwordInputTranslationX.animateTo(0f, tween(1000, easing = EaseOutQuint))
        delay(200)

        isAuthLinkVisible = true
        authLinkTranslationX.animateTo(0f, tween(1000, easing = EaseOutQuint))
        delay(200)

        isButtonVisible = true
        buttonAlpha.animateTo(1f, tween(500))
        buttonScale.animateTo(1f, tween(800, easing = EaseOutQuint))
        delay(200)

        isHeaderVisible = true
        headerRowAlpha.animateTo(1f, tween(1000))
        delay(200)

        isLogoVisible = true
        logoTranslationY.animateTo(0f, tween(1000, easing = EaseOutBounce))

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
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Header row
        if (isHeaderVisible) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .alpha(headerRowAlpha.value),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                ToggleTheme()
                Spacer(modifier = Modifier.width(1.dp))
                LanguageMenu(onLanguageSelected = onLanguageSelected)
            }
        } else {
            Spacer(modifier = Modifier.height(60.dp))
        }

        Spacer(modifier = Modifier.height(30.dp))

        // Logo
        if (isLogoVisible) {
            TaskTrackrLogo(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(88.dp)
                    .graphicsLayer(translationY = logoTranslationY.value)
                    .padding(bottom = 30.dp)
            )
        } else {
            Spacer(modifier = Modifier.height(88.dp))
        }

        Spacer(modifier = Modifier.height(30.dp))

        // Sign-in title
        Text(
            text = stringResource(R.string.sign_in),
            style = TaskTrackrTheme.typography.header,
            color = TaskTrackrTheme.colorScheme.primary,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .alpha(signInTitleAlpha.value)
                .fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(30.dp))

        // Email input field
        if (isEmailVisible) {
            TextInputField(
                label = stringResource(R.string.email),
                placeholder = stringResource(R.string.email_input_placeholder),
                modifier = Modifier
                    .offset(x = emailInputTranslationX.value.dp)
                    .width(320.dp)
                    .padding(vertical = 8.dp)
            )
        } else {
            Spacer(modifier = Modifier.height(52.dp))
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Password input field
        if (isPasswordVisible) {
            TextInputField(
                label = stringResource(R.string.password),
                placeholder = stringResource(R.string.password_input_placeholder),
                modifier = Modifier
                    .offset(x = passwordInputTranslationX.value.dp)
                    .width(320.dp)
                    .padding(vertical = 8.dp)
            )
        } else {
            Spacer(modifier = Modifier.height(52.dp))
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Auth link
        if (isAuthLinkVisible) {
            AuthLink(
                text = stringResource(R.string.sign_up_link_message),
                redirect = stringResource(R.string.sign_up),
                onClick = { },
                modifier = Modifier
                    .offset(x = authLinkTranslationX.value.dp)
            )
        } else {
            Spacer(modifier = Modifier.height(20.dp))
        }

        Spacer(modifier = Modifier.height(30.dp))

        // Sign-in button
        if (isButtonVisible) {
            CustomButton(
                text = stringResource(R.string.sign_in),
                modifier = Modifier
                    .alpha(buttonAlpha.value)
                    .graphicsLayer(
                        scaleX = buttonScale.value,
                        scaleY = buttonScale.value
                    )
                    .width(200.dp)
            )
        } else {
            Spacer(modifier = Modifier.height(48.dp))
        }
    }
}
