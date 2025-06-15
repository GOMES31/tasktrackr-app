package com.example.tasktrackr_app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

private val lightColorScheme = AppColorScheme(
        primary = Color(0xFFD32F2F),
        secondary = Color(0xFF1976D2),
        tertiary = Color(0xFF388E3C),
        accent = Color(0xFFFF9800),
        background = Color(0xFFFAFAFA),
        text = Color(0xFF212121),
        cardBackground = Color(0xFFFFFFFF),
        buttonText = Color(0xFFFFFFFF),
        inputBackground = Color(0xFFE0E0E0)
)


private val darkColorScheme = AppColorScheme(
        primary = Color(0xFFD32F2F),
        secondary = Color(0xFF1976D2),
        tertiary = Color(0xFF388E3C),
        accent = Color(0xFFFBC02D),
        background = Color(0xFF121211),
        text = Color(0xFFF5F5F5),
        cardBackground = Color(0xFF1E1E1E),
        buttonText = Color(0xFFFFFFFF),
        inputBackground = Color(0xFF424242)
)

private val typography = AppTypography(
        body = TextStyle(
        fontFamily = Roboto,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp
        ),

        bodyStrong =  TextStyle(
        fontFamily = Roboto,
        fontWeight = FontWeight.Bold,
        fontSize = 16.sp,
        lineHeight = 24.sp
        ),

        bodySmall = TextStyle(
        fontFamily = Roboto,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 16.sp
        ),

        header = TextStyle(
        fontFamily = RobotoCondensed,
        fontWeight = FontWeight.Bold,
        fontSize = 28.sp,
        lineHeight = 34.sp
        ),

        subHeader = TextStyle(
        fontFamily = RobotoCondensed,
        fontWeight = FontWeight.Medium,
        fontSize = 21.sp,
        lineHeight = 28.sp
        ),

        largeTitles = TextStyle(
        fontFamily = RobotoCondensed,
        fontWeight = FontWeight.Bold,
        fontSize = 38.sp,
        lineHeight = 46.sp
        ),

        smallTitles = TextStyle(
        fontFamily = RobotoCondensed,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp
        ),

        caption = TextStyle(
        fontFamily = Roboto,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 16.sp
        ),

        button = TextStyle(
        fontFamily = RobotoCondensed,
        fontWeight = FontWeight.Bold,
        fontSize = 16.sp,
        lineHeight = 24.sp
        ),

        label = TextStyle(
        fontFamily = Roboto,
        fontWeight = FontWeight.Bold,
        fontSize = 14.sp,
        lineHeight = 20.sp
        ),

        placeholder = TextStyle(
        fontFamily = RobotoItalic,
        fontSize = 12.sp,
        lineHeight = 16.sp
        )
)

@Composable
fun TaskTrackrTheme(
    isDarkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
){
    var isDarkMode by remember { mutableStateOf(isDarkTheme)}
    val toggle = { isDarkMode = !isDarkMode }

    val colorScheme = if (isDarkMode) darkColorScheme else lightColorScheme
    CompositionLocalProvider(
        LocalAppColorScheme provides colorScheme,
        LocalAppTypography provides typography,
        LocalAppThemeState provides AppThemeState(isDarkMode, toggle)
    ){
       content()
    }
}

object TaskTrackrTheme {
    val colorScheme: AppColorScheme
        @Composable get() = LocalAppColorScheme.current

    val typography: AppTypography
        @Composable get() = LocalAppTypography.current
}