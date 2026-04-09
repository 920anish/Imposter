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
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.imposter.play.data.entities.CategoryEntity
import com.imposter.play.data.entities.PlayerEntity
import com.imposter.play.data.local.CATEGORY_ALL
import com.imposter.play.engine.GameConfig
import com.imposter.play.theme.ColorBorder
import com.imposter.play.theme.ColorBorder2
import com.imposter.play.theme.ColorCrew
import com.imposter.play.theme.ColorImp
import com.imposter.play.theme.ColorMuted
import com.imposter.play.theme.ColorSurface
import com.imposter.play.theme.ColorText
import com.imposter.play.theme.ColorWarn
import com.imposter.play.theme.ColorWin
import com.imposter.play.ui.components.GridBackground
import com.imposter.play.ui.components.PrimaryButton
import imposter.sharedui.generated.resources.Res
import imposter.sharedui.generated.resources.nav_customize_add_player
import imposter.sharedui.generated.resources.nav_customize_category
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
import org.koin.compose.koinInject

@Composable
fun CustomizeScreen(
    config: GameConfig,
    onConfigChange: (GameConfig) -> Unit,
    onPlay: (GameConfig) -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: CustomizeViewModel = koinInject(),
) {
    val uiState by viewModel.uiState.collectAsState()
    var tab by remember { mutableStateOf("players") }

    Box(modifier = modifier.fillMaxSize()) {
        GridBackground(tint = ColorBorder, opacity = 0.32f)
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .padding(top = 48.dp, bottom = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            // Fixed header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column {
                    Text(
                        text = stringResource(Res.string.nav_customize_title),
                        color = ColorText,
                        style = MaterialTheme.typography.displayMedium
                    )
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
            // Fixed tabs
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
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

            // Scrollable content area
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState()),
            ) {
                if (tab == "players") {
                    PlayersTabContent(
                        players = uiState.players,
                        onTogglePlayer = { playerId, isActive ->
                            viewModel.setPlayerActive(playerId, isActive)
                            onConfigChange(
                                config.copy(
                                    playerCount = uiState.players.count { it.isActive }.coerceIn(3, 10),
                                )
                            )
                        },
                        onDeletePlayer = { playerId ->
                            viewModel.deletePlayer(playerId)
                            onConfigChange(
                                config.copy(
                                    playerCount = uiState.players.count { it.isActive }.coerceIn(3, 10),
                                )
                            )
                        },
                        onAddPlayer = { name ->
                            viewModel.addPlayer(name)
                        },
                    )
                } else {
                    CategoryTabContent(
                        categories = uiState.categories,
                        selectedCategoryIds = uiState.selectedCategoryIds,
                        difficulty = uiState.difficulty,
                        imposterHintEnabled = uiState.imposterHintEnabled,
                        onCategoryToggle = { categoryId -> viewModel.toggleCategory(categoryId) },
                        onDifficultySelect = { level -> viewModel.setDifficulty(level) },
                        onHintToggle = { enabled -> viewModel.setHintEnabled(enabled) },
                    )
                }
            }

            // Fixed bottom button
            Spacer(Modifier.height(16.dp))
            PrimaryButton(
                text = stringResource(Res.string.nav_customize_play),
                onClick = {
                    onPlay(
                        config.copy(
                            playerCount = uiState.players.count { it.isActive }.coerceIn(3, 10),
                            difficulty = uiState.difficulty,
                            imposterHintEnabled = uiState.imposterHintEnabled,
                        )
                    )
                },
            )
        }
    }
}

@Composable
private fun PlayersTabContent(
    players: List<PlayerEntity>,
    onTogglePlayer: (Long, Boolean) -> Unit,
    onDeletePlayer: (Long) -> Unit,
    onAddPlayer: (String) -> Unit,
) {
    Text(
        text = stringResource(Res.string.nav_customize_players_hint),
        style = MaterialTheme.typography.labelMedium,
        color = ColorMuted,
        modifier = Modifier.fillMaxWidth(),
    )
    Spacer(Modifier.height(10.dp))
    players.forEachIndexed { index, player ->
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                Modifier.size(44.dp).border(1.dp, ColorBorder),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "${index + 1}".padStart(2, '0'), color = ColorMuted)
            }
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(44.dp)
                    .background(ColorSurface)
                    .border(1.dp, if (player.isActive) ColorBorder2 else ColorBorder)
                    .padding(horizontal = 12.dp),
                contentAlignment = Alignment.CenterStart,
            ) {
                Text(
                    text = player.name,
                    color = if (player.isActive) ColorText else ColorMuted,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            Box(
                Modifier
                    .size(44.dp)
                    .border(1.dp, if (player.isActive) ColorCrew.copy(alpha = 0.6f) else ColorBorder)
                    .background(if (player.isActive) ColorCrew.copy(alpha = 0.12f) else Color.Transparent)
                    .clickable { onTogglePlayer(player.id, !player.isActive) },
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = if (player.isActive) "ON" else "OFF",
                    color = if (player.isActive) ColorCrew else ColorMuted,
                    style = MaterialTheme.typography.labelSmall,
                )
            }
            Box(
                Modifier.size(44.dp).border(1.dp, ColorBorder)
                    .clickable { onDeletePlayer(player.id) },
                contentAlignment = Alignment.Center,
            ) { Text("×", color = ColorMuted) }
        }
        Spacer(Modifier.height(8.dp))
    }
    if (players.size < 20) {
        var newPlayerName by remember { mutableStateOf("") }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(44.dp)
                    .background(ColorSurface)
                    .border(1.dp, ColorBorder)
                    .padding(horizontal = 12.dp),
                contentAlignment = Alignment.CenterStart,
            ) {
                BasicTextField(
                    value = newPlayerName,
                    onValueChange = { newPlayerName = it },
                    singleLine = true,
                    textStyle = MaterialTheme.typography.bodyMedium.copy(color = ColorText),
                    cursorBrush = androidx.compose.ui.graphics.SolidColor(ColorCrew),
                    modifier = Modifier.fillMaxWidth(),
                    decorationBox = { innerTextField ->
                        if (newPlayerName.isBlank()) {
                            Text(
                                "New player name",
                                color = ColorMuted,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                        innerTextField()
                    },
                )
            }
            Box(
                Modifier
                    .height(44.dp)
                    .border(1.dp, ColorBorder)
                    .padding(horizontal = 12.dp)
                    .clickable {
                        val name = newPlayerName.trim()
                        if (name.isNotEmpty()) {
                            onAddPlayer(name)
                            newPlayerName = ""
                        }
                    },
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = stringResource(Res.string.nav_customize_add_player),
                    color = ColorMuted,
                    style = MaterialTheme.typography.labelMedium,
                )
            }
        }
    }
}

@Composable
private fun CategoryTabContent(
    categories: List<CategoryEntity>,
    selectedCategoryIds: Set<String>,
    difficulty: Int,
    imposterHintEnabled: Boolean,
    onCategoryToggle: (String) -> Unit,
    onDifficultySelect: (Int) -> Unit,
    onHintToggle: (Boolean) -> Unit,
) {
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
                    if (difficulty == index) colors[index].copy(alpha = 0.6f) else ColorBorder
                ).background(
                    if (difficulty == index) colors[index].copy(alpha = 0.12f) else Color.Transparent
                ).clickable { onDifficultySelect(index) },
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = label,
                    color = if (difficulty == index) colors[index] else ColorMuted,
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
                    if (!imposterHintEnabled) ColorCrew.copy(alpha = 0.6f) else ColorBorder
                )
                .background(if (!imposterHintEnabled) ColorCrew.copy(alpha = 0.12f) else Color.Transparent)
                .clickable { onHintToggle(false) },
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = stringResource(Res.string.nav_customize_hint_off),
                color = if (!imposterHintEnabled) ColorCrew else ColorMuted,
                style = MaterialTheme.typography.labelSmall,
            )
        }
        Box(
            Modifier.weight(1f).height(36.dp)
                .border(
                    1.dp,
                    if (imposterHintEnabled) ColorCrew.copy(alpha = 0.6f) else ColorBorder
                )
                .background(if (imposterHintEnabled) ColorCrew.copy(alpha = 0.12f) else Color.Transparent)
                .clickable { onHintToggle(true) },
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = stringResource(Res.string.nav_customize_hint_on),
                color = if (imposterHintEnabled) ColorCrew else ColorMuted,
                style = MaterialTheme.typography.labelSmall,
            )
        }
    }
    Spacer(Modifier.height(14.dp))
    Text(
        text = stringResource(Res.string.nav_customize_category),
        color = ColorMuted,
        style = MaterialTheme.typography.labelSmall,
        modifier = Modifier.fillMaxWidth(),
    )
    Spacer(Modifier.height(8.dp))
    
    // "All Categories" option
    val isAllSelected = CATEGORY_ALL in selectedCategoryIds
    Box(
        Modifier.fillMaxWidth().height(44.dp)
            .border(1.dp, if (isAllSelected) ColorCrew.copy(alpha = 0.5f) else ColorBorder)
            .background(if (isAllSelected) ColorCrew.copy(alpha = 0.12f) else Color.Transparent)
            .clickable { onCategoryToggle(CATEGORY_ALL) },
        contentAlignment = Alignment.Center,
    ) {
        Text(
            stringResource(Res.string.nav_customize_random),
            color = if (isAllSelected) ColorCrew else ColorMuted
        )
    }
    Spacer(Modifier.height(10.dp))
    
    // Category grid with multi-select
    categories.chunked(3).forEach { rowCategories ->
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            rowCategories.forEach { category ->
                val isSelected = category.id in selectedCategoryIds
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(84.dp)
                        .background(if (isSelected) ColorCrew.copy(alpha = 0.12f) else ColorSurface)
                        .border(
                            1.dp,
                            if (isSelected) ColorCrew.copy(alpha = 0.55f) else ColorBorder
                        )
                        .clickable { onCategoryToggle(category.id) }
                        .padding(8.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = category.name,
                            color = if (isSelected) ColorCrew else ColorText,
                            style = MaterialTheme.typography.labelMedium,
                            textAlign = TextAlign.Center,
                        )
                        Text(
                            text = "${category.wordCount} words",
                            color = ColorMuted,
                            style = MaterialTheme.typography.labelSmall,
                        )
                    }
                }
            }
            // Fill empty slots in last row
            repeat(3 - rowCategories.size) {
                Spacer(Modifier.weight(1f))
            }
        }
        Spacer(Modifier.height(8.dp))
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
            .background(if (active) ColorCrew.copy(alpha = 0.12f) else Color.Transparent)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            color = if (active) ColorCrew else ColorMuted,
        )
    }
}
