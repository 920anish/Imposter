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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.imposter.play.engine.GameConfig
import com.imposter.play.ui.components.GridBackground
import com.imposter.play.ui.components.PrimaryButton
import com.imposter.play.theme.ColorBorder
import com.imposter.play.theme.ColorBorder2
import com.imposter.play.theme.ColorCrew
import com.imposter.play.theme.ColorCrewDim
import com.imposter.play.theme.ColorDim
import com.imposter.play.theme.ColorMuted
import com.imposter.play.theme.ColorSurface
import com.imposter.play.theme.ColorText
import com.imposter.play.theme.ColorWarn
import com.imposter.play.theme.ColorWin
import imposter.sharedui.generated.resources.Res
import imposter.sharedui.generated.resources.nav_customize_add_player
import imposter.sharedui.generated.resources.nav_customize_category
import imposter.sharedui.generated.resources.nav_customize_close
import imposter.sharedui.generated.resources.nav_customize_difficulty
import imposter.sharedui.generated.resources.nav_customize_easy
import imposter.sharedui.generated.resources.nav_customize_hard
import imposter.sharedui.generated.resources.nav_customize_hint_off
import imposter.sharedui.generated.resources.nav_customize_hint_on
import imposter.sharedui.generated.resources.nav_customize_imposter_hint
import imposter.sharedui.generated.resources.nav_customize_medium
import imposter.sharedui.generated.resources.nav_customize_play
import imposter.sharedui.generated.resources.nav_customize_players_hint
import imposter.sharedui.generated.resources.nav_customize_random
import imposter.sharedui.generated.resources.nav_customize_tab_category
import imposter.sharedui.generated.resources.nav_customize_tab_players
import imposter.sharedui.generated.resources.nav_customize_title
import org.jetbrains.compose.resources.stringResource

@Composable
fun CustomizeScreen(
    config: GameConfig,
    onConfigChange: (GameConfig) -> Unit,
    onPlay: (GameConfig) -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val players = remember(config.playerCount, config.playerNames) {
        mutableStateListOf<String>().apply {
            addAll((0 until config.playerCount.coerceIn(3, 10)).map { index ->
                config.playerNames.getOrNull(index).orEmpty()
            })
        }
    }
    var tab by remember { mutableStateOf("players") }
    val categoryKeys = remember {
        listOf("ANIMALS", "FOOD", "CITIES", "MOVIES", "SPORTS", "SCIENCE", "TECH", "MUSIC", "GEOGRAPHY")
    }
    var categoryIndex by remember(config.category) {
        mutableIntStateOf(categoryKeys.indexOf(config.category.uppercase()).coerceAtLeast(-1))
    }
    var difficulty by remember(config.difficulty) { mutableIntStateOf(config.difficulty.coerceIn(0, 2)) }

    androidx.compose.foundation.layout.Box(modifier = modifier.fillMaxSize()) {
        GridBackground(tint = ColorBorder, opacity = 0.32f)
        Column(
            modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp).padding(top = 48.dp, bottom = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column {
                    Text(text = stringResource(Res.string.nav_customize_title), color = ColorText, style = androidx.compose.material3.MaterialTheme.typography.displayMedium)
                }
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
            Spacer(Modifier.height(18.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                TabButton(
                    text = stringResource(Res.string.nav_customize_tab_players),
                    active = tab == "players",
                    modifier = Modifier.weight(1f),
                ) { tab = "players" }
                TabButton(
                    text = stringResource(Res.string.nav_customize_tab_category),
                    active = tab == "category",
                    modifier = Modifier.weight(1f),
                ) { tab = "category" }
            }
            Spacer(Modifier.height(16.dp))
            if (tab == "players") {
                Text(
                    text = stringResource(Res.string.nav_customize_players_hint),
                    style = androidx.compose.material3.MaterialTheme.typography.labelMedium,
                    color = ColorMuted,
                    modifier = Modifier.fillMaxWidth(),
                )
                Spacer(Modifier.height(10.dp))
                players.forEachIndexed { index, name ->
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                        Box(Modifier.size(44.dp).border(1.dp, ColorBorder), contentAlignment = Alignment.Center) {
                            Text(text = "${index + 1}".padStart(2, '0'), color = ColorMuted)
                        }
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(44.dp)
                                .background(ColorSurface)
                                .border(1.dp, if (name.isBlank()) ColorBorder else ColorBorder2)
                                .padding(horizontal = 12.dp),
                            contentAlignment = Alignment.CenterStart,
                        ) {
                            BasicTextField(
                                value = name,
                                onValueChange = { updated ->
                                    players[index] = updated
                                    onConfigChange(
                                        config.copy(
                                            playerCount = players.size.coerceIn(3, 10),
                                            playerNames = players.toList(),
                                        )
                                    )
                                },
                                singleLine = true,
                                textStyle = androidx.compose.material3.MaterialTheme.typography.bodyMedium.copy(color = ColorText),
                                modifier = Modifier.fillMaxWidth(),
                                decorationBox = { innerTextField ->
                                    if (name.isBlank()) {
                                        Text("Player ${index + 1}", color = ColorMuted, style = androidx.compose.material3.MaterialTheme.typography.bodyMedium)
                                    }
                                    innerTextField()
                                },
                            )
                        }
                        if (players.size > 3) {
                            Box(
                                Modifier.size(44.dp).border(1.dp, ColorBorder).clickable {
                                    players.removeAt(index)
                                    onConfigChange(
                                        config.copy(
                                            playerCount = players.size.coerceIn(3, 10),
                                            playerNames = players.toList(),
                                        )
                                    )
                                },
                                contentAlignment = Alignment.Center,
                            ) { Text("×", color = ColorMuted) }
                        }
                    }
                    Spacer(Modifier.height(8.dp))
                }
                if (players.size < 10) {
                    Box(
                        Modifier.fillMaxWidth().height(44.dp).border(1.dp, ColorBorder).clickable {
                            players.add("")
                            onConfigChange(
                                config.copy(
                                    playerCount = players.size.coerceIn(3, 10),
                                    playerNames = players.toList(),
                                )
                            )
                        },
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = stringResource(Res.string.nav_customize_add_player),
                            color = ColorMuted,
                            style = androidx.compose.material3.MaterialTheme.typography.labelMedium,
                        )
                    }
                }
            } else {
                Text(
                    text = stringResource(Res.string.nav_customize_difficulty),
                    color = ColorMuted,
                    style = androidx.compose.material3.MaterialTheme.typography.labelSmall,
                    modifier = Modifier.fillMaxWidth(),
                )
                Spacer(Modifier.height(8.dp))
                val labels = listOf(
                    stringResource(Res.string.nav_customize_easy),
                    stringResource(Res.string.nav_customize_medium),
                    stringResource(Res.string.nav_customize_hard),
                )
                val colors = listOf(ColorWin, ColorWarn, com.imposter.play.theme.ColorImp)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                    labels.forEachIndexed { index, label ->
                        Box(
                            Modifier.weight(1f).height(36.dp).border(1.dp, if (difficulty == index) colors[index].copy(alpha = 0.6f) else ColorBorder).background(
                                if (difficulty == index) colors[index].copy(alpha = 0.12f) else Color.Transparent
                            ).clickable {
                                difficulty = index
                                onConfigChange(config.copy(difficulty = difficulty))
                            },
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(text = label, color = if (difficulty == index) colors[index] else ColorMuted, style = androidx.compose.material3.MaterialTheme.typography.labelSmall)
                        }
                    }
                }
                Spacer(Modifier.height(14.dp))
                Text(
                    text = stringResource(Res.string.nav_customize_imposter_hint),
                    color = ColorMuted,
                    style = androidx.compose.material3.MaterialTheme.typography.labelSmall,
                    modifier = Modifier.fillMaxWidth(),
                )
                Spacer(Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                    Box(
                        Modifier.weight(1f).height(36.dp)
                            .border(1.dp, if (!config.imposterHintEnabled) ColorCrew.copy(alpha = 0.6f) else ColorBorder)
                            .background(if (!config.imposterHintEnabled) ColorCrew.copy(alpha = 0.12f) else Color.Transparent)
                            .clickable { onConfigChange(config.copy(imposterHintEnabled = false)) },
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = stringResource(Res.string.nav_customize_hint_off),
                            color = if (!config.imposterHintEnabled) ColorCrew else ColorMuted,
                            style = androidx.compose.material3.MaterialTheme.typography.labelSmall,
                        )
                    }
                    Box(
                        Modifier.weight(1f).height(36.dp)
                            .border(1.dp, if (config.imposterHintEnabled) ColorCrew.copy(alpha = 0.6f) else ColorBorder)
                            .background(if (config.imposterHintEnabled) ColorCrew.copy(alpha = 0.12f) else Color.Transparent)
                            .clickable { onConfigChange(config.copy(imposterHintEnabled = true)) },
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = stringResource(Res.string.nav_customize_hint_on),
                            color = if (config.imposterHintEnabled) ColorCrew else ColorMuted,
                            style = androidx.compose.material3.MaterialTheme.typography.labelSmall,
                        )
                    }
                }
                Spacer(Modifier.height(14.dp))
                Text(
                    text = stringResource(Res.string.nav_customize_category),
                    color = ColorMuted,
                    style = androidx.compose.material3.MaterialTheme.typography.labelSmall,
                    modifier = Modifier.fillMaxWidth(),
                )
                Spacer(Modifier.height(8.dp))
                Box(
                    Modifier.fillMaxWidth().height(44.dp).border(1.dp, if (categoryIndex == -1) ColorCrew.copy(alpha = 0.5f) else ColorBorder)
                        .background(if (categoryIndex == -1) ColorCrewDim else Color.Transparent)
                        .clickable {
                            categoryIndex = -1
                            onConfigChange(config.copy(category = "RANDOM"))
                        },
                    contentAlignment = Alignment.Center,
                ) {
                    Text(stringResource(Res.string.nav_customize_random), color = if (categoryIndex == -1) ColorCrew else ColorMuted)
                }
                Spacer(Modifier.height(10.dp))
                LazyVerticalGrid(columns = GridCells.Fixed(3), horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.height(280.dp)) {
                    itemsIndexed(categoryKeys) { index, category ->
                        Box(
                            modifier = Modifier
                                .height(84.dp)
                                .background(if (categoryIndex == index) ColorCrewDim else ColorSurface)
                                .border(1.dp, if (categoryIndex == index) ColorCrew.copy(alpha = 0.55f) else ColorBorder)
                                .clickable {
                                    categoryIndex = index
                                    onConfigChange(config.copy(category = category))
                                }
                                .padding(8.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                text = category.lowercase().replaceFirstChar { it.uppercase() },
                                color = if (categoryIndex == index) ColorCrew else ColorText,
                                style = androidx.compose.material3.MaterialTheme.typography.labelMedium,
                                textAlign = TextAlign.Center,
                            )
                        }
                    }
                }
            }
            Spacer(Modifier.weight(1f))
            Spacer(Modifier.height(16.dp))
            PrimaryButton(
                text = stringResource(Res.string.nav_customize_play),
                onClick = {
                    onPlay(
                        config.copy(
                            playerCount = players.size.coerceIn(3, 10),
                            playerNames = players.toList(),
                            category = if (categoryIndex < 0) "RANDOM" else categoryKeys[categoryIndex],
                            difficulty = difficulty,
                        )
                    )
                },
            )
        }
    }
}

@Composable
private fun TabButton(
    text: String,
    active: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    Box(
        modifier = modifier
            .height(38.dp)
            .border(1.dp, if (active) ColorCrew.copy(alpha = 0.5f) else ColorBorder)
            .background(if (active) ColorCrewDim else Color.Transparent)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            style = androidx.compose.material3.MaterialTheme.typography.labelSmall,
            color = if (active) ColorCrew else ColorMuted,
        )
    }
}
