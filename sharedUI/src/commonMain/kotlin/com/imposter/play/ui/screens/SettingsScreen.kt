package com.imposter.play.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.imposter.play.engine.GameConfig
import com.imposter.play.theme.ColorBorder
import com.imposter.play.theme.ColorCrew
import com.imposter.play.theme.ColorImp
import com.imposter.play.theme.ColorMuted
import com.imposter.play.theme.ColorText
import com.imposter.play.theme.ColorWarn
import com.imposter.play.theme.ColorWin
import com.imposter.play.ui.components.GridBackground
import com.imposter.play.ui.components.PrimaryButton
import imposter.sharedui.generated.resources.Res
import imposter.sharedui.generated.resources.nav_customize_difficulty
import imposter.sharedui.generated.resources.nav_customize_easy
import imposter.sharedui.generated.resources.nav_customize_hard
import imposter.sharedui.generated.resources.nav_customize_hint_off
import imposter.sharedui.generated.resources.nav_customize_hint_on
import imposter.sharedui.generated.resources.nav_customize_imposter_hint
import imposter.sharedui.generated.resources.nav_customize_medium
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject

@Composable
fun SettingsScreen(
    config: GameConfig,
    onConfigChange: (GameConfig) -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: CustomizeViewModel = koinInject(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()

    Box(modifier = modifier.fillMaxSize()) {
        GridBackground(tint = ColorBorder, opacity = 0.32f)
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 24.dp)
                .padding(top = 48.dp, bottom = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
            Text(
                text = "GAME SETTINGS",
                color = ColorText,
                style = MaterialTheme.typography.displayMedium
            )
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .border(1.dp, ColorBorder)
                        .clickable(onClick = onClose),
                    contentAlignment = Alignment.Center,
                ) {
                    Text("✕", color = ColorMuted)
                }
            }
            Spacer(Modifier.height(16.dp))

            Text(
                text = stringResource(Res.string.nav_customize_difficulty),
                color = ColorMuted,
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(Modifier.height(8.dp))
            val labels = listOf(
                stringResource(Res.string.nav_customize_easy),
                stringResource(Res.string.nav_customize_medium),
                stringResource(Res.string.nav_customize_hard),
            )
            val colors = listOf(ColorWin, ColorWarn, ColorImp)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                labels.forEachIndexed { index, label ->
                    Box(
                        Modifier.weight(1f).height(36.dp).border(
                            1.dp,
                            if (uiState.difficulty == index) colors[index].copy(alpha = 0.6f) else ColorBorder
                        ).background(
                            if (uiState.difficulty == index) colors[index].copy(alpha = 0.12f) else Color.Transparent
                        ).clickable {
                            viewModel.setDifficulty(index)
                            onConfigChange(config.copy(difficulty = index))
                        },
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = label,
                            color = if (uiState.difficulty == index) colors[index] else ColorMuted,
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                }
            }
            Spacer(Modifier.height(14.dp))

            Text(
                text = stringResource(Res.string.nav_customize_imposter_hint),
                color = ColorMuted,
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                Box(
                    Modifier.weight(1f).height(36.dp)
                        .border(
                            1.dp,
                            if (!uiState.imposterHintEnabled) ColorCrew.copy(alpha = 0.6f) else ColorBorder
                        )
                        .background(if (!uiState.imposterHintEnabled) ColorCrew.copy(alpha = 0.12f) else Color.Transparent)
                        .clickable {
                            viewModel.setHintEnabled(false)
                            onConfigChange(config.copy(imposterHintEnabled = false))
                        },
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = stringResource(Res.string.nav_customize_hint_off),
                        color = if (!uiState.imposterHintEnabled) ColorCrew else ColorMuted,
                        style = MaterialTheme.typography.labelSmall,
                    )
                }
                Box(
                    Modifier.weight(1f).height(36.dp)
                        .border(
                            1.dp,
                            if (uiState.imposterHintEnabled) ColorCrew.copy(alpha = 0.6f) else ColorBorder
                        )
                        .background(if (uiState.imposterHintEnabled) ColorCrew.copy(alpha = 0.12f) else Color.Transparent)
                        .clickable {
                            viewModel.setHintEnabled(true)
                            onConfigChange(config.copy(imposterHintEnabled = true))
                        },
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = stringResource(Res.string.nav_customize_hint_on),
                        color = if (uiState.imposterHintEnabled) ColorCrew else ColorMuted,
                        style = MaterialTheme.typography.labelSmall,
                    )
                }
            }
            Spacer(Modifier.height(14.dp))

            Text(
                text = "DISCUSSION TIMER",
                color = ColorMuted,
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                Box(
                    Modifier.weight(1f).height(36.dp)
                        .border(1.dp, if (!uiState.isTimerEnabled) ColorCrew.copy(alpha = 0.6f) else ColorBorder)
                        .background(if (!uiState.isTimerEnabled) ColorCrew.copy(alpha = 0.12f) else Color.Transparent)
                        .clickable {
                            viewModel.setTimerEnabled(false)
                            onConfigChange(config.copy(isTimerEnabled = false))
                        },
                    contentAlignment = Alignment.Center,
                ) {
                    Text("OFF", color = if (!uiState.isTimerEnabled) ColorCrew else ColorMuted)
                }
                Box(
                    Modifier.weight(1f).height(36.dp)
                        .border(1.dp, if (uiState.isTimerEnabled) ColorCrew.copy(alpha = 0.6f) else ColorBorder)
                        .background(if (uiState.isTimerEnabled) ColorCrew.copy(alpha = 0.12f) else Color.Transparent)
                        .clickable {
                            viewModel.setTimerEnabled(true)
                            onConfigChange(config.copy(isTimerEnabled = true))
                        },
                    contentAlignment = Alignment.Center,
                ) {
                    Text("ON", color = if (uiState.isTimerEnabled) ColorCrew else ColorMuted)
                }
            }

            Spacer(Modifier.height(20.dp))
            PrimaryButton(text = "DONE", onClick = onClose)
        }
    }
}
