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
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
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
import com.imposter.play.theme.ColorImp
import com.imposter.play.theme.ColorImpDim
import com.imposter.play.theme.ColorMuted
import imposter.sharedui.generated.resources.Res
import imposter.sharedui.generated.resources.preview_danger
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun DangerButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    leadingIconRes: DrawableResource? = null,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val tintAlpha by animateFloatAsState(
        targetValue = if (pressed && enabled) 0.25f else 0.1f,
        animationSpec = spring(),
        label = "dangerTintAlpha",
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = if (enabled) ColorImp.copy(alpha = 0.45f) else ColorBorder,
            )
            .background(ColorImp.copy(alpha = tintAlpha))
            .alpha(if (enabled) 1f else 0.45f)
            .clickable(
                enabled = enabled,
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick,
            )
            .padding(vertical = 14.dp),
    ) {
        Row(
            modifier = Modifier.align(Alignment.Center),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (leadingIconRes != null) {
                Icon(
                    painter = painterResource(leadingIconRes),
                    contentDescription = null,
                    tint = if (enabled) ColorImp else ColorMuted,
                    modifier = Modifier.size(20.dp),
                )
            }
            Text(
                text = text,
                color = if (enabled) ColorImp else ColorMuted,
                style = androidx.compose.material3.MaterialTheme.typography.headlineMedium.copy(
                    fontFamily = BebasNeue,
                ),
            )
        }
    }
}

@Composable
private fun DangerButtonPreview() {
    DangerButton(
        text = stringResource(Res.string.preview_danger),
        onClick = {},
        modifier = Modifier.fillMaxWidth().padding(16.dp),
    )
}
