package com.example.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = RoyalBlueLight,
    onPrimary = Color.White,
    primaryContainer = RoyalBlueDark,
    onPrimaryContainer = RoyalBlueContainer,
    secondary = PurpleLight,
    onSecondary = Color.White,
    secondaryContainer = PurpleDark,
    onSecondaryContainer = PurpleContainer,
    tertiary = GoldAccent,
    onTertiary = DarkNavy900,
    background = DarkNavy900,
    onBackground = Color(0xFFF1F5F9),
    surface = DarkNavy800,
    onSurface = Color(0xFFF8FAFC),
    surfaceVariant = DarkNavy700,
    onSurfaceVariant = Color(0xFFCBD5E1),
    outline = Gray600,
    error = BrandRed,
    onError = Color.White
)

private val LightColorScheme = lightColorScheme(
    primary = RoyalBlue,
    onPrimary = Color.White,
    primaryContainer = RoyalBlueContainer,
    onPrimaryContainer = RoyalBlueDark,
    secondary = PurplePrimary,
    onSecondary = Color.White,
    secondaryContainer = PurpleContainer,
    onSecondaryContainer = PurpleDark,
    tertiary = BrandOrange,
    onTertiary = Color.White,
    background = Color(0xFFF8FAFC),
    onBackground = DarkNavy900,
    surface = Color.White,
    onSurface = DarkNavy900,
    surfaceVariant = Color(0xFFF1F5F9),
    onSurfaceVariant = Gray600,
    outline = Gray300,
    error = BrandRed,
    onError = Color.White
)

@Composable
fun JibonifyTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
