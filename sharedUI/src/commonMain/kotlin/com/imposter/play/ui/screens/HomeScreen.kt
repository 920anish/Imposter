package com.imposter.play.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.imposter.play.engine.GameConfig
import com.imposter.play.theme.ColorBorder
import com.imposter.play.theme.ColorBorder2
import com.imposter.play.theme.ColorMuted
import com.imposter.play.theme.ColorSurface
import com.imposter.play.theme.ColorText
import com.imposter.play.ui.components.GhostButton
import com.imposter.play.ui.components.GridBackground
import com.imposter.play.ui.components.MonoBadge
import com.imposter.play.ui.components.PickerStepButton
import com.imposter.play.ui.components.PrimaryButton
import imposter.sharedui.generated.resources.Res
import imposter.sharedui.generated.resources.nav_home_badge
import imposter.sharedui.generated.resources.nav_home_customize
import imposter.sharedui.generated.resources.nav_home_play_now
import imposter.sharedui.generated.resources.nav_home_players
import imposter.sharedui.generated.resources.nav_home_subtitle
import imposter.sharedui.generated.resources.nav_home_title
import org.jetbrains.compose.resources.stringResource

@Composable
fun HomeScreen(
    config: GameConfig,
    onDecreasePlayers: () -> Unit,
    onIncreasePlayers: () -> Unit,
    onPlayNow: (GameConfig) -> Unit,
    onCustomize: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    val playerCount = config.playerCount.coerceIn(3, 10)
    Box(
        modifier = modifier.fillMaxSize(),
    ) {
        GridBackground(tint = ColorBorder, opacity = 0.5f)
        val scrollState = rememberScrollState()
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            // Top spacer for centering
            Spacer(Modifier.weight(1f))
            MonoBadge(
                text = stringResource(Res.string.nav_home_badge),
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(Modifier.height(20.dp))
            Text(
                text = stringResource(Res.string.nav_home_title),
                style = MaterialTheme.typography.displayLarge,
                color = ColorText,
                textAlign = TextAlign.Center,
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = stringResource(Res.string.nav_home_subtitle),
                style = MaterialTheme.typography.titleSmall,
                color = ColorText.copy(alpha = 0.9f),
                textAlign = TextAlign.Center,
            )
            Spacer(Modifier.height(36.dp))
            Text(
                text = stringResource(Res.string.nav_home_players),
                style = MaterialTheme.typography.titleSmall,
                color = ColorMuted,
            )
            Spacer(Modifier.height(10.dp))
            Text(
                text = "Manage exact player activation/deactivation in Customize",
                style = MaterialTheme.typography.labelSmall,
                color = ColorMuted,
                textAlign = TextAlign.Center,
            )
            Spacer(Modifier.height(10.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                PickerStepButton(
                    label = "−",
                    enabled = playerCount > 3,
                    onClick = onDecreasePlayers,
                )
                Box(
                    modifier = Modifier
                        .width(80.dp)
                        .height(64.dp)
                        .background(ColorSurface)
                        .border(1.5.dp, ColorBorder2),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = playerCount.toString(),
                        style = MaterialTheme.typography.displayMedium,
                        color = ColorText,
                        textAlign = TextAlign.Center,
                    )
                }
                PickerStepButton(
                    label = "+",
                    enabled = playerCount < 10,
                    onClick = onIncreasePlayers,
                )
            }
            Spacer(Modifier.height(36.dp))
            PrimaryButton(
                text = stringResource(Res.string.nav_home_play_now),
                onClick = {
                    onPlayNow(config.copy(playerCount = playerCount))
                },
            )
            Spacer(Modifier.height(10.dp))
            GhostButton(
                text = stringResource(Res.string.nav_home_customize),
                onClick = onCustomize,
                modifier = Modifier.fillMaxWidth().height(52.dp),
            )
            // Bottom spacer for centering
            Spacer(Modifier.weight(1f))
        }
    }
}
