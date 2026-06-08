package com.l1kiiiiii.soundverseai.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// Use system default sans-serif (similar to Inter/Roboto which ships on Android devices)
val SoundverseFontFamily = FontFamily.Default

val SoundverseTypography = Typography(
    // Display / Hero headers
    displayLarge = TextStyle(
        fontFamily = SoundverseFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 36.sp,
        lineHeight = 44.sp,
        color = TextPrimary
    ),
    // Screen title — "Ready to share"
    displayMedium = TextStyle(
        fontFamily = SoundverseFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 28.sp,
        lineHeight = 34.sp,
        color = TextPrimary
    ),
    // "PULSE PLAYGROUND" header bar
    titleLarge = TextStyle(
        fontFamily = SoundverseFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 1.8.sp,
        color = TextPrimary
    ),
    titleMedium = TextStyle(
        fontFamily = SoundverseFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        color = TextPrimary
    ),
    // Chat bubble body text
    bodyLarge = TextStyle(
        fontFamily = SoundverseFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 15.sp,
        lineHeight = 22.sp,
        color = TextPrimary
    ),
    bodyMedium = TextStyle(
        fontFamily = SoundverseFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 13.sp,
        lineHeight = 20.sp,
        color = TextMuted
    ),
    bodySmall = TextStyle(
        fontFamily = SoundverseFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        color = TextMuted
    ),
    // Button labels
    labelLarge = TextStyle(
        fontFamily = SoundverseFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 15.sp,
        lineHeight = 20.sp,
        color = TextPrimary
    ),
    labelMedium = TextStyle(
        fontFamily = SoundverseFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        color = TextMuted
    ),
    labelSmall = TextStyle(
        fontFamily = SoundverseFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 10.sp,
        lineHeight = 14.sp,
        color = TextMuted
    )
)