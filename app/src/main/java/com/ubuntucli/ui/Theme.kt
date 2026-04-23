package com.ubuntucli.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
fun Theme(content: @Composable () -> Unit) {
    val colors = darkColorScheme(
        primary = Color(0xFF00FF00),
        background = Color.Black,
        surface = Color(0xFF121212)
    )
    MaterialTheme(colorScheme = colors, content = content)
}
