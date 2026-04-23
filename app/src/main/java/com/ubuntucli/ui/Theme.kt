package com.ubuntucli.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
fun Theme(theme: String = "Hacker", content: @Composable () -> Unit) {
    val primaryColor = when(theme) {
        "Amber" -> Color(0xFFFFB000)
        "White" -> Color.White
        else -> Color(0xFF00FF00) // Hacker Green
    }

    val colors = darkColorScheme(
        primary = primaryColor,
        secondary = primaryColor.copy(alpha = 0.7f),
        background = Color.Black,
        surface = Color(0xFF121212),
        onPrimary = Color.Black,
        onBackground = Color.White,
        onSurface = Color.White
    )
    MaterialTheme(colorScheme = colors, content = content)
}
