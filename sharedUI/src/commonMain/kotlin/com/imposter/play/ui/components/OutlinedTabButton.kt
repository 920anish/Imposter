package com.imposter.play.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.imposter.play.theme.ColorBorder
import com.imposter.play.theme.ColorCrew
import com.imposter.play.theme.ColorMuted
import com.imposter.play.theme.ColorSurface

@Composable
fun OutlinedTabButton(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .height(38.dp)
            .border(1.dp, if (selected) ColorCrew else ColorBorder)
            .background(if (selected) ColorCrew.copy(alpha = 0.12f) else ColorSurface)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            color = if (selected) ColorCrew else ColorMuted,
            style = MaterialTheme.typography.labelSmall,
        )
    }
}
