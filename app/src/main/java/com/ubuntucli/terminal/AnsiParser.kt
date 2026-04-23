package com.ubuntucli.terminal

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString

object AnsiParser {
    fun parse(text: String): AnnotatedString {
        return buildAnnotatedString {
            val regex = "\u001B\\[([0-9;]*)m".toRegex()
            var lastIndex = 0

            regex.findAll(text).forEach { match ->
                append(text.substring(lastIndex, match.range.first))
                val code = match.groupValues[1]
                val color = when (code) {
                    "31" -> Color.Red
                    "32" -> Color.Green
                    "33" -> Color.Yellow
                    "34" -> Color.Blue
                    "35" -> Color.Magenta
                    "36" -> Color.Cyan
                    "37" -> Color.White
                    "0" -> Color.Unspecified
                    else -> null
                }
                if (color != null) {
                    pushStyle(SpanStyle(color = color))
                }
                lastIndex = match.range.last + 1
            }
            append(text.substring(lastIndex))
        }
    }
}
