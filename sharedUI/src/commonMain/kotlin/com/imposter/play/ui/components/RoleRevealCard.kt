package com.imposter.play.ui.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun RoleRevealCard(
    accent: Color,
    accentDim: Color,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(24.dp),
    minHeight: Dp = 0.dp,
    maxHeight: Dp? = null,
    shadowRadius: Dp = 18.dp,
    content: @Composable BoxScope.() -> Unit,
) {
    val infinite = rememberInfiniteTransition(label = "cardGlow")
    val glowAlpha = infinite.animateFloat(
        initialValue = 0.25f,
        targetValue = 0.5f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "glowAlpha",
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = minHeight)
            .let { base -> if (maxHeight != null) base.heightIn(max = maxHeight) else base }
            .shadow(shadowRadius, ambientColor = accent.copy(alpha = glowAlpha.value), spotColor = accent.copy(alpha = glowAlpha.value))
            .background(accentDim)
            .border(1.dp, accent),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(contentPadding),
            content = content,
        )
    }
}
