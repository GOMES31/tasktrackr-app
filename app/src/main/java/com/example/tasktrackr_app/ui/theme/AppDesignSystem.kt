package com.example.tasktrackr_app.ui.theme

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle

// Color Scheme
data class AppColorScheme(
    val primary: Color,
    val secondary: Color,
    val tertiary: Color,
    val accent: Color,
    val background: Color,
    val text: Color,
    val cardBackground: Color,
    val buttonText: Color,
    val inputBackground: Color
)

// Typography
data class AppTypography(
    val body: TextStyle,
    val bodyStrong: TextStyle,
    val bodySmall: TextStyle,
    val header: TextStyle,
    val subHeader: TextStyle,
    val largeTitles: TextStyle,
    val smallTitles: TextStyle,
    val caption: TextStyle,
    val button: TextStyle,
    val label: TextStyle,
    val placeholder: TextStyle,
)

val LocalAppColorScheme = staticCompositionLocalOf {
    AppColorScheme(
        primary = Color.Unspecified,
        secondary = Color.Unspecified,
        tertiary = Color.Unspecified,
        accent = Color.Unspecified,
        background = Color.Unspecified,
        text = Color.Unspecified,
        cardBackground = Color.Unspecified,
        buttonText = Color.Unspecified,
        inputBackground = Color.Unspecified

    )
}


val LocalAppTypography = staticCompositionLocalOf {
    AppTypography(
        body = TextStyle.Default,
        bodyStrong = TextStyle.Default,
        bodySmall = TextStyle.Default,
        header = TextStyle.Default,
        subHeader = TextStyle.Default,
        largeTitles = TextStyle.Default,
        smallTitles = TextStyle.Default,
        caption = TextStyle.Default,
        button = TextStyle.Default,
        label = TextStyle.Default,
        placeholder = TextStyle.Default
    )
}
