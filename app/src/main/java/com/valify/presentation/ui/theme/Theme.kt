package com.valify.presentation.ui.theme

import androidx.compose.material.MaterialTheme
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorPalette = lightColors(
    primary = Color(0xFF2196F3),
    primaryVariant = Color(0xFF1976D2),
    secondary = Color(0xFF03DAC5),
    background = Color.White,
    surface = Color.White,
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onBackground = Color.Black,
    onSurface = Color.Black,
)

@Composable
fun ValifyTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colors = LightColorPalette,
        content = content
    )
}
