package com.imposter.play.ui.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.imposter.play.theme.ColorBorder
import com.imposter.play.theme.ColorMuted
import com.imposter.play.theme.IBMPlexMono
import imposter.sharedui.generated.resources.Res
import imposter.sharedui.generated.resources.preview_badge
import org.jetbrains.compose.resources.stringResource

@Composable
fun MonoBadge(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = ColorMuted,
    border: Color = ColorBorder,
    contentPadding: PaddingValues = PaddingValues(horizontal = 16.dp, vertical = 5.dp),
) {
    Text(
        text = text,
        modifier = modifier
            .border(width = 1.dp, color = border)
            .padding(contentPadding),
        color = color,
        textAlign = TextAlign.Center,
        style = androidx.compose.material3.MaterialTheme.typography.titleSmall.copy(
            fontFamily = IBMPlexMono,
        ),
    )
}

@Composable
private fun MonoBadgePreview() {
    MonoBadge(
        text = stringResource(Res.string.preview_badge),
        modifier = Modifier.fillMaxWidth(),
    )
}
