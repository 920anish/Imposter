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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.unit.dp
import com.imposter.play.data.entities.CategoryEntity
import com.imposter.play.data.entities.PlayerEntity
import com.imposter.play.data.local.CATEGORY_ALL
import com.imposter.play.engine.GameConfig
import com.imposter.play.theme.ColorBorder
import com.imposter.play.theme.ColorBorder2
import com.imposter.play.theme.ColorCrew
import com.imposter.play.theme.ColorMuted
import com.imposter.play.theme.ColorSurface
import com.imposter.play.theme.ColorText
import com.imposter.play.ui.components.GridBackground
import com.imposter.play.ui.components.PrimaryButton
import imposter.sharedui.generated.resources.Res
import imposter.sharedui.generated.resources.nav_customize_play
import imposter.sharedui.generated.resources.nav_customize_random
import imposter.sharedui.generated.resources.nav_customize_tab_category
import imposter.sharedui.generated.resources.nav_customize_tab_players
import imposter.sharedui.generated.resources.nav_customize_title
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyListState

@Composable
fun CustomizeScreen(
    config: GameConfig,
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
        ) {

            // HEADER
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = stringResource(Res.string.nav_customize_title),
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

            Spacer(Modifier.height(18.dp))

            // TABS
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                TabButton(
                    text = stringResource(Res.string.nav_customize_tab_players),
                    active = tab == "players",
                    modifier = Modifier.weight(1f)
                ) { tab = "players" }

                TabButton(
                    text = stringResource(Res.string.nav_customize_tab_category),
                    active = tab == "category",
                    modifier = Modifier.weight(1f)
                ) { tab = "category" }
            }

            Spacer(Modifier.height(16.dp))

            // CONTENT
            if (tab == "players") {
                PlayersTabContent(
                    players = uiState.players,
                    onTogglePlayer = viewModel::setPlayerActive,
                    onDeletePlayer = viewModel::deletePlayer,
                    onAddPlayer = viewModel::addPlayer,
                    onRenamePlayer = viewModel::renamePlayer,
                    onReorderPlayers = viewModel::reorderPlayers,
                    modifier = Modifier.weight(1f)
                )
            } else {
                CategoryTabContent(
                    categories = uiState.categories,
                    selectedCategoryIds = uiState.selectedCategoryIds,
                    onCategoryToggle = viewModel::toggleCategory,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(Modifier.height(16.dp))

            PrimaryButton(
                text = stringResource(Res.string.nav_customize_play),
                onClick = {
                    val count = uiState.players.count { it.isActive }.coerceIn(3, 10)
                    onPlay(config.copy(playerCount = count))
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
    onRenamePlayer: (Long, String) -> Unit,
    onReorderPlayers: (List<Long>) -> Unit,
    modifier: Modifier = Modifier
) {
    val lazyListState = rememberLazyListState()
    var orderedPlayers by remember(players) { mutableStateOf(players) }

    val reorderState = rememberReorderableLazyListState(lazyListState) { from, to ->
        orderedPlayers = orderedPlayers.toMutableList().apply {
            add(to.index, removeAt(from.index))
        }
        onReorderPlayers(orderedPlayers.map { it.id })
    }

    val activeCount = orderedPlayers.count { it.isActive }

    Column(modifier) {

        LazyColumn(
            state = lazyListState,
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.weight(1f)
        ) {
            itemsIndexed(
                items = orderedPlayers,
                key = { _, item -> item.id }
            ) { index, player ->

                ReorderableItem(reorderState, key = player.id) { isDragging ->

                    val canToggle =
                        if (player.isActive) activeCount > 3 else activeCount < 10
                    val canDelete =
                        !player.isActive || activeCount > 3

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                if (isDragging) ColorSurface.copy(alpha = 0.98f) else androidx.compose.ui.graphics.Color.Transparent
                            )
                            .border(1.dp, if (isDragging) ColorBorder2 else androidx.compose.ui.graphics.Color.Transparent)
                            .alpha(if (isDragging) 0.92f else 1f),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        // DRAG
                        Box(
                            Modifier
                                .size(44.dp)
                                .border(1.dp, ColorBorder)
                                .draggableHandle(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "${index + 1}".padStart(2, '0'), color = ColorMuted)
                        }

                        // NAME
                        var name by remember(player.id, player.name) {
                            mutableStateOf(player.name)
                        }

                        Box(
                            Modifier
                                .weight(1f)
                                .height(44.dp)
                                .background(ColorSurface)
                                .border(
                                    1.dp,
                                    if (player.isActive) ColorBorder2 else ColorBorder
                                )
                                .padding(horizontal = 12.dp),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            BasicTextField(
                                value = name,
                                onValueChange = {
                                    name = it.take(10)
                                },
                                singleLine = true,
                                textStyle = MaterialTheme.typography.bodyMedium.copy(
                                    color = if (player.isActive) ColorText else ColorMuted
                                ),
                                cursorBrush = androidx.compose.ui.graphics.SolidColor(ColorCrew),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .onFocusChanged { focusState ->
                                        if (!focusState.isFocused) {
                                            val committed = name.trim().take(10)
                                            if (committed.isNotEmpty() && committed != player.name) {
                                                onRenamePlayer(player.id, committed)
                                            }
                                            if (committed.isEmpty()) {
                                                name = player.name
                                            }
                                        }
                                    }
                            )
                        }

                        // TOGGLE
                        Box(
                            Modifier
                                .size(44.dp)
                                .border(1.dp, ColorBorder)
                                .clickable(enabled = canToggle) {
                                    onTogglePlayer(player.id, !player.isActive)
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (player.isActive) "ON" else "OFF",
                                color = when {
                                    !canToggle -> ColorMuted.copy(alpha = 0.45f)
                                    player.isActive -> ColorCrew
                                    else -> ColorMuted
                                },
                                style = MaterialTheme.typography.labelSmall,
                            )
                        }

                        // DELETE
                        Box(
                            Modifier
                                .size(44.dp)
                                .border(1.dp, ColorBorder)
                                .clickable(enabled = canDelete) {
                                    onDeletePlayer(player.id)
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text("×", color = if (canDelete) ColorMuted else ColorMuted.copy(alpha = 0.45f))
                        }
                    }
                }
            }

            item {
                Spacer(Modifier.height(14.dp))

                Box(
                    Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                        .border(1.dp, ColorBorder)
                        .clickable {
                            onAddPlayer("Player ${orderedPlayers.size + 1}")
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text("ADD", color = ColorMuted)
                }
            }
        }
    }
}

@Composable
private fun CategoryTabContent(
    categories: List<CategoryEntity>,
    selectedCategoryIds: Set<String>,
    onCategoryToggle: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier.verticalScroll(rememberScrollState())) {
        val isAll = CATEGORY_ALL in selectedCategoryIds

        Box(
            Modifier
                .fillMaxWidth()
                .height(44.dp)
                .border(1.dp, if (isAll) ColorCrew else ColorBorder)
                .clickable { onCategoryToggle(CATEGORY_ALL) },
            contentAlignment = Alignment.Center
        ) {
            Text(
                stringResource(Res.string.nav_customize_random),
                color = if (isAll) ColorCrew else ColorMuted
            )
        }

        Spacer(Modifier.height(10.dp))

        categories.chunked(3).forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                row.forEach { category ->
                    val selected = category.id in selectedCategoryIds

                    Box(
                        Modifier
                            .weight(1f)
                            .height(84.dp)
                            .background(if (selected) ColorCrew.copy(0.12f) else ColorSurface)
                            .border(
                                1.dp,
                                if (selected) ColorCrew else ColorBorder
                            )
                            .clickable { onCategoryToggle(category.id) },
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = category.name,
                                color = if (selected) ColorCrew else ColorText,
                                style = MaterialTheme.typography.labelMedium,
                            )
                            Text(
                                text = "${category.wordCount} words",
                                color = ColorMuted,
                                style = MaterialTheme.typography.labelSmall,
                            )
                        }
                    }
                }
            }
            Spacer(Modifier.height(8.dp))
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
        modifier
            .height(38.dp)
            .border(1.dp, if (active) ColorCrew else ColorBorder)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(text, color = if (active) ColorCrew else ColorMuted)
    }
}
