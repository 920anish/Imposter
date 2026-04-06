package com.imposter.play.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.imposter.play.theme.ColorBg
import com.imposter.play.theme.ColorBorder

@Composable
fun GridBackground(
    modifier: Modifier = Modifier,
    tint: Color = ColorBorder,
    opacity: Float = 0.05f,
    spacing: Dp = 44.dp,
) {
    val alpha = opacity.coerceIn(0f, 1f)
    val gridColor = tint.copy(alpha = alpha)

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(ColorBg)
            .drawWithCache {
                val spacingPx = spacing.toPx().coerceAtLeast(8f)
                val stroke = 0.5.dp.toPx().coerceAtLeast(1f)
                onDrawBehind {
                    var x = 0f
                    while (x <= size.width) {
                        drawLine(
                            color = gridColor,
                            start = Offset(x, 0f),
                            end = Offset(x, size.height),
                            strokeWidth = stroke,
                        )
                        x += spacingPx
                    }

                    var y = 0f
                    while (y <= size.height) {
                        drawLine(
                            color = gridColor,
                            start = Offset(0f, y),
                            end = Offset(size.width, y),
                            strokeWidth = stroke,
                        )
                        y += spacingPx
                    }
                }
            }
    )
}

