package com.imposter.play.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.imposter.play.theme.BebasNeue
import com.imposter.play.theme.ColorBorder
import com.imposter.play.theme.ColorMuted
import com.imposter.play.theme.ColorText
import imposter.sharedui.generated.resources.Res
import imposter.sharedui.generated.resources.phase2_preview_primary
import org.jetbrains.compose.resources.stringResource

@Composable
fun PrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val fillAlpha by animateFloatAsState(
        targetValue = if (pressed && enabled) 0.15f else 0f,
        animationSpec = spring(),
        label = "primaryFillAlpha",
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = if (enabled) ColorText.copy(alpha = 0.6f) else ColorBorder,
            )
            .background(ColorText.copy(alpha = fillAlpha))
            .alpha(if (enabled) 1f else 0.45f)
            .clickable(
                enabled = enabled,
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick,
            )
            .padding(vertical = 14.dp),
    ) {
        Text(
            text = text,
            color = if (enabled) ColorText else ColorMuted,
            style = androidx.compose.material3.MaterialTheme.typography.headlineMedium.copy(
                fontFamily = BebasNeue,
            ),
            modifier = Modifier.align(Alignment.Center),
        )
    }
}

@Composable
private fun PrimaryButtonPreview() {
    Row(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        PrimaryButton(
            text = stringResource(Res.string.phase2_preview_primary),
            onClick = {},
            modifier = Modifier.weight(1f),
        )
    }
}
