package com.example.saku.app.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.unit.sp
import com.example.saku.app.R

// Primary Font: Overused Grotesk (Self-hosted variable font asset matching frontend)
@OptIn(ExperimentalTextApi::class)
val OverusedGrotesk = FontFamily(
    Font(
        R.font.overused_grotesk,
        weight = FontWeight.Light,
        variationSettings = FontVariation.Settings(FontVariation.weight(300))
    ),
    Font(
        R.font.overused_grotesk,
        weight = FontWeight.Normal,
        variationSettings = FontVariation.Settings(FontVariation.weight(400))
    ),
    Font(
        R.font.overused_grotesk,
        weight = FontWeight.Medium,
        variationSettings = FontVariation.Settings(FontVariation.weight(500))
    ),
    Font(
        R.font.overused_grotesk,
        weight = FontWeight.SemiBold,
        variationSettings = FontVariation.Settings(FontVariation.weight(600))
    ),
    Font(
        R.font.overused_grotesk,
        weight = FontWeight.Bold,
        variationSettings = FontVariation.Settings(FontVariation.weight(700))
    ),
    Font(
        R.font.overused_grotesk,
        weight = FontWeight.ExtraBold,
        variationSettings = FontVariation.Settings(FontVariation.weight(800))
    ),
    Font(
        R.font.overused_grotesk,
        weight = FontWeight.Black,
        variationSettings = FontVariation.Settings(FontVariation.weight(900))
    )
)

private val defaultLineHeightStyle = LineHeightStyle(
    alignment = LineHeightStyle.Alignment.Center,
    trim = LineHeightStyle.Trim.Both
)

private val defaultPlatformTextStyle = PlatformTextStyle(
    includeFontPadding = false
)

val Typography = Typography(
    displayLarge = TextStyle(
        fontFamily = OverusedGrotesk,
        fontWeight = FontWeight.Bold,
        fontSize = 32.sp,
        lineHeight = 36.sp,
        letterSpacing = (-0.5).sp,
        platformStyle = defaultPlatformTextStyle,
        lineHeightStyle = defaultLineHeightStyle
    ),
    displayMedium = TextStyle(
        fontFamily = OverusedGrotesk,
        fontWeight = FontWeight.Bold,
        fontSize = 28.sp,
        lineHeight = 32.sp,
        platformStyle = defaultPlatformTextStyle,
        lineHeightStyle = defaultLineHeightStyle
    ),
    headlineLarge = TextStyle(
        fontFamily = OverusedGrotesk,
        fontWeight = FontWeight.Bold,
        fontSize = 24.sp,
        lineHeight = 28.sp,
        platformStyle = defaultPlatformTextStyle,
        lineHeightStyle = defaultLineHeightStyle
    ),
    headlineMedium = TextStyle(
        fontFamily = OverusedGrotesk,
        fontWeight = FontWeight.SemiBold,
        fontSize = 20.sp,
        lineHeight = 24.sp,
        platformStyle = defaultPlatformTextStyle,
        lineHeightStyle = defaultLineHeightStyle
    ),
    titleLarge = TextStyle(
        fontFamily = OverusedGrotesk,
        fontWeight = FontWeight.SemiBold,
        fontSize = 18.sp,
        lineHeight = 22.sp,
        platformStyle = defaultPlatformTextStyle,
        lineHeightStyle = defaultLineHeightStyle
    ),
    titleMedium = TextStyle(
        fontFamily = OverusedGrotesk,
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp,
        lineHeight = 20.sp,
        platformStyle = defaultPlatformTextStyle,
        lineHeightStyle = defaultLineHeightStyle
    ),
    bodyLarge = TextStyle(
        fontFamily = OverusedGrotesk,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 20.sp,
        platformStyle = defaultPlatformTextStyle,
        lineHeightStyle = defaultLineHeightStyle
    ),
    bodyMedium = TextStyle(
        fontFamily = OverusedGrotesk,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 18.sp,
        platformStyle = defaultPlatformTextStyle,
        lineHeightStyle = defaultLineHeightStyle
    ),
    bodySmall = TextStyle(
        fontFamily = OverusedGrotesk,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 15.sp,
        platformStyle = defaultPlatformTextStyle,
        lineHeightStyle = defaultLineHeightStyle
    ),
    labelLarge = TextStyle(
        fontFamily = OverusedGrotesk,
        fontWeight = FontWeight.SemiBold,
        fontSize = 14.sp,
        lineHeight = 18.sp,
        platformStyle = defaultPlatformTextStyle,
        lineHeightStyle = defaultLineHeightStyle
    ),
    labelMedium = TextStyle(
        fontFamily = OverusedGrotesk,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        lineHeight = 15.sp,
        platformStyle = defaultPlatformTextStyle,
        lineHeightStyle = defaultLineHeightStyle
    ),
    labelSmall = TextStyle(
        fontFamily = OverusedGrotesk,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 14.sp,
        platformStyle = defaultPlatformTextStyle,
        lineHeightStyle = defaultLineHeightStyle
    )
)