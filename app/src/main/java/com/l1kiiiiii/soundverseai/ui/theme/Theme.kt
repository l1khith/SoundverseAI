package com.l1kiiiiii.soundverseai.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val SoundverseDarkColorScheme = darkColorScheme(
    primary           = PrimaryAccent,
    onPrimary         = White,
    primaryContainer  = GradientEnd,
    onPrimaryContainer= White,
    secondary         = GradientStart,
    onSecondary       = White,
    background        = BackgroundBottom,
    onBackground      = TextPrimary,
    surface           = CardSurface,
    onSurface         = TextPrimary,
    surfaceVariant    = CardSurfaceAlt,
    onSurfaceVariant  = TextMuted,
    outline           = StrokeSubtle,
    error             = Color(0xFFCF6679),
    onError           = White
)

@Composable
fun SoundverseAITheme(
    darkTheme: Boolean = true, // Always dark per design
    content: @Composable () -> Unit
) {
    val colorScheme = SoundverseDarkColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = BackgroundTop.toArgb()
            window.navigationBarColor = BackgroundBottom.toArgb()
            WindowCompat.getInsetsController(window, view).apply {
                isAppearanceLightStatusBars = false
                isAppearanceLightNavigationBars = false
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography  = SoundverseTypography,
        content     = content
    )
}