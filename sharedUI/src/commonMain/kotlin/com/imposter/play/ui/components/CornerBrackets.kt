package com.imposter.play.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.imposter.play.theme.ColorBorder2

@Composable
fun CornerBrackets(
    modifier: Modifier = Modifier,
    color: Color = ColorBorder2,
    bracketSize: Dp = 10.dp,
    horizontalInset: Dp = 10.dp,
    topInset: Dp = 10.dp,
    bottomInset: Dp = 10.dp,
    strokeWidth: Dp = 1.dp,
) {
    Canvas(modifier = modifier.fillMaxSize()) {
        val bracket = bracketSize.toPx()
        val insetX = horizontalInset.toPx()
        val insetTopPx = topInset.toPx()
        val insetBottomPx = bottomInset.toPx()
        val stroke = strokeWidth.toPx()
        val halfStroke = stroke / 2f

        val left = insetX + halfStroke
        val top = insetTopPx + halfStroke
        val right = this.size.width - insetX - halfStroke
        val bottom = this.size.height - insetBottomPx - halfStroke

        // Top-left
        drawLine(color, Offset(left, top), Offset(left + bracket, top), stroke)
        drawLine(color, Offset(left, top), Offset(left, top + bracket), stroke)
        // Top-right
        drawLine(color, Offset(right, top), Offset(right - bracket, top), stroke)
        drawLine(color, Offset(right, top), Offset(right, top + bracket), stroke)
        // Bottom-left
        drawLine(color, Offset(left, bottom), Offset(left + bracket, bottom), stroke)
        drawLine(color, Offset(left, bottom), Offset(left, bottom - bracket), stroke)
        // Bottom-right
        drawLine(color, Offset(right, bottom), Offset(right - bracket, bottom), stroke)
        drawLine(color, Offset(right, bottom), Offset(right, bottom - bracket), stroke)
    }
}

