package com.imposter.play.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.imposter.play.theme.ColorBg
import com.imposter.play.theme.ColorBorder2

@Composable
fun CornerBrackets(
    modifier: Modifier = Modifier,
    color: Color = ColorBorder2,
    bracketSize: Dp = 10.dp,
    inset: Dp = 10.dp,
    strokeWidth: Dp = 1.dp,
) {
    Canvas(modifier = modifier.fillMaxSize()) {
        val bracket = bracketSize.toPx()
        val padding = inset.toPx()
        val stroke = strokeWidth.toPx()

        val right = this.size.width - padding
        val bottom = this.size.height - padding

        // Top-left
        drawLine(color, Offset(padding, padding), Offset(padding + bracket, padding), stroke)
        drawLine(color, Offset(padding, padding), Offset(padding, padding + bracket), stroke)
        // Top-right
        drawLine(color, Offset(right, padding), Offset(right - bracket, padding), stroke)
        drawLine(color, Offset(right, padding), Offset(right, padding + bracket), stroke)
        // Bottom-left
        drawLine(color, Offset(padding, bottom), Offset(padding + bracket, bottom), stroke)
        drawLine(color, Offset(padding, bottom), Offset(padding, bottom - bracket), stroke)
        // Bottom-right
        drawLine(color, Offset(right, bottom), Offset(right - bracket, bottom), stroke)
        drawLine(color, Offset(right, bottom), Offset(right, bottom - bracket), stroke)
    }
}

@Composable
private fun CornerBracketsPreview() {
    Box(modifier = Modifier.fillMaxSize().background(ColorBg)) {
        CornerBrackets()
    }
}
