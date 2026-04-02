package com.imposter.play.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.imposter.play.theme.ColorBorder2
import com.imposter.play.theme.ColorDim
import com.imposter.play.theme.ColorSurface
import com.imposter.play.theme.ColorText

@Composable
fun PickerStepButton(
    label: String,
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Text(
        text = label,
        modifier = modifier
            .width(52.dp)
            .height(64.dp)
            .background(ColorSurface)
            .border(1.5.dp, ColorBorder2)
            .clickable(enabled = enabled, onClick = onClick)
            .padding(top = 8.dp),
        style = androidx.compose.material3.MaterialTheme.typography.displaySmall,
        color = if (enabled) ColorText else ColorDim,
        textAlign = TextAlign.Center,
    )
}
