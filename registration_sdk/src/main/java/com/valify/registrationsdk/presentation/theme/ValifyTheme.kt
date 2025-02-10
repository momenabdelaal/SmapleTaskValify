package com.valify.registrationsdk.presentation.theme

import androidx.compose.material.Colors
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

data class ValifyColors(
    val primary: Color = Color(0xFF263AC2),
    val secondary: Color = Color(0xFF03DAC5),
    val background: Color = Color.White,
    val error: Color = Color(0xFFB00020),
    val onPrimary: Color = Color.White,
    val onSecondary: Color = Color.Black,
    val onBackground: Color = Color.Black,
    val onError: Color = Color.White
)

data class ValifyThemeConfig(
    val colors: ValifyColors = ValifyColors(),
    val typography: Typography = Typography(),
    val cornerRadius: Float = 8f,
    val spacing: Float = 16f
)

val LocalValifyTheme = staticCompositionLocalOf { ValifyThemeConfig() }

@Composable
fun ValifyTheme(
    themeConfig: ValifyThemeConfig = ValifyThemeConfig(),
    content: @Composable () -> Unit
) {
    val colors = Colors(
        primary = themeConfig.colors.primary,
        primaryVariant = themeConfig.colors.primary,
        secondary = themeConfig.colors.secondary,
        secondaryVariant = themeConfig.colors.secondary,
        background = themeConfig.colors.background,
        surface = themeConfig.colors.background,
        error = themeConfig.colors.error,
        onPrimary = themeConfig.colors.onPrimary,
        onSecondary = themeConfig.colors.onSecondary,
        onBackground = themeConfig.colors.onBackground,
        onSurface = themeConfig.colors.onBackground,
        onError = themeConfig.colors.onError,
        isLight = true
    )

    MaterialTheme(
        colors = colors,
        typography = themeConfig.typography,
        content = content
    )
}
