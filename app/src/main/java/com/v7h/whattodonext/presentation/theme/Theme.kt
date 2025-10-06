package com.v7h.whattodonext.presentation.theme

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
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// Light theme color scheme using custom design system
private val LightColorScheme = lightColorScheme(
    primary = Primary,
    onPrimary = LightOnPrimary,
    primaryContainer = Primary.copy(alpha = 0.1f),
    onPrimaryContainer = Primary,
    
    secondary = SubtleLight,
    onSecondary = LightOnSurface,
    secondaryContainer = SubtleLight.copy(alpha = 0.1f),
    onSecondaryContainer = SubtleLight,
    
    tertiary = AcceptButton,
    onTertiary = LightOnPrimary,
    tertiaryContainer = AcceptButton.copy(alpha = 0.1f),
    onTertiaryContainer = AcceptButton,
    
    error = ErrorLight,
    onError = LightOnPrimary,
    errorContainer = ErrorLight.copy(alpha = 0.1f),
    onErrorContainer = ErrorLight,
    
    background = BackgroundLight,
    onBackground = LightOnBackground,
    
    surface = SurfaceLight,
    onSurface = LightOnSurface,
    surfaceVariant = SurfaceLight.copy(alpha = 0.8f),
    onSurfaceVariant = SubtleLight,
    
    outline = BorderLight,
    outlineVariant = BorderLight.copy(alpha = 0.5f),
    
    surfaceTint = Primary,
    inverseSurface = ContentLight,
    inverseOnSurface = SurfaceLight,
    inversePrimary = Primary.copy(alpha = 0.8f)
)

// Dark theme color scheme using custom design system
private val DarkColorScheme = darkColorScheme(
    primary = Primary,
    onPrimary = DarkOnPrimary,
    primaryContainer = Primary.copy(alpha = 0.2f),
    onPrimaryContainer = Primary.copy(alpha = 0.9f),
    
    secondary = SubtleDark,
    onSecondary = DarkOnSurface,
    secondaryContainer = SubtleDark.copy(alpha = 0.2f),
    onSecondaryContainer = SubtleDark,
    
    tertiary = AcceptButton,
    onTertiary = DarkOnPrimary,
    tertiaryContainer = AcceptButton.copy(alpha = 0.2f),
    onTertiaryContainer = AcceptButton.copy(alpha = 0.9f),
    
    error = ErrorDark,
    onError = DarkOnPrimary,
    errorContainer = ErrorDark.copy(alpha = 0.2f),
    onErrorContainer = ErrorDark.copy(alpha = 0.9f),
    
    background = BackgroundDark,
    onBackground = DarkOnBackground,
    
    surface = SurfaceDark,
    onSurface = DarkOnSurface,
    surfaceVariant = SurfaceDark.copy(alpha = 0.8f),
    onSurfaceVariant = SubtleDark,
    
    outline = BorderDark,
    outlineVariant = BorderDark.copy(alpha = 0.5f),
    
    surfaceTint = Primary,
    inverseSurface = ContentDark,
    inverseOnSurface = SurfaceDark,
    inversePrimary = Primary.copy(alpha = 0.8f)
)

@Composable
fun WhatToDoNextTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Enable dynamic color to use system colors
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> {
            android.util.Log.d("WhatToDoNextTheme", "Using system dark theme")
            darkColorScheme()
        }
        else -> {
            android.util.Log.d("WhatToDoNextTheme", "Using system light theme")
            lightColorScheme()
        }
    }
    
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = CustomTypography,
        shapes = CustomShapes,
        content = content
    )
}
