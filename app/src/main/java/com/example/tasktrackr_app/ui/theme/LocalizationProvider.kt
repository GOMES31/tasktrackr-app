package com.example.tasktrackr_app.ui.theme

import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import java.util.Locale

@Composable
fun LocalizationProvider(
    locale: Locale,
    content: @Composable () -> Unit
) {
    val ctx = LocalContext.current

    val config = remember(locale) {
        Configuration(ctx.resources.configuration).apply {
            setLocale(locale)
        }
    }

    val localizedContext = remember(config) {
        ctx.createConfigurationContext(config)
    }

    CompositionLocalProvider(
        LocalContext provides localizedContext
    ) {
        content()
    }
}
