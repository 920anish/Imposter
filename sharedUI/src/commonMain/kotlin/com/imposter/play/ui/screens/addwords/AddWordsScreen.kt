package com.imposter.play.ui.screens.addwords

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
import androidx.compose.foundation.layout.safeDrawingPadding
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
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.imposter.play.data.entities.CategoryEntity
import com.imposter.play.data.repository.CustomWordItem
import com.imposter.play.theme.ColorBorder
import com.imposter.play.theme.ColorCrew
import com.imposter.play.theme.ColorImp
import com.imposter.play.theme.ColorMuted
import com.imposter.play.theme.ColorSurface
import com.imposter.play.theme.ColorText
import com.imposter.play.theme.ColorWin
import com.imposter.play.ui.components.CloseableScreenHeader
import com.imposter.play.ui.components.GridBackground
import com.imposter.play.ui.components.OutlinedTabButton
import com.imposter.play.ui.components.PrimaryButton
import imposter.sharedui.generated.resources.Res
import imposter.sharedui.generated.resources.nav_add_words_add
import imposter.sharedui.generated.resources.nav_add_words_add_category
import imposter.sharedui.generated.resources.nav_add_words_categories_tab
import imposter.sharedui.generated.resources.nav_add_words_category
import imposter.sharedui.generated.resources.nav_add_words_category_name_placeholder
import imposter.sharedui.generated.resources.nav_add_words_custom_categories
import imposter.sharedui.generated.resources.nav_add_words_custom_words
import imposter.sharedui.generated.resources.nav_add_words_delete
import imposter.sharedui.generated.resources.nav_add_words_difficulty
import imposter.sharedui.generated.resources.nav_add_words_edit
import imposter.sharedui.generated.resources.nav_add_words_empty
import imposter.sharedui.generated.resources.nav_add_words_empty_custom_categories
import imposter.sharedui.generated.resources.nav_add_words_hint
import imposter.sharedui.generated.resources.nav_add_words_hint_placeholder
import imposter.sharedui.generated.resources.nav_add_words_history_category_item
import imposter.sharedui.generated.resources.nav_add_words_history_tab
import imposter.sharedui.generated.resources.nav_add_words_save
import imposter.sharedui.generated.resources.nav_add_words_title
import imposter.sharedui.generated.resources.nav_add_words_unknown_difficulty
import imposter.sharedui.generated.resources.nav_add_words_word
import imposter.sharedui.generated.resources.nav_add_words_word_placeholder
import imposter.sharedui.generated.resources.nav_add_words_words_tab
import imposter.sharedui.generated.resources.nav_customize_easy
import imposter.sharedui.generated.resources.nav_customize_hard
import imposter.sharedui.generated.resources.nav_customize_medium
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject

private enum class AddWordsTab { Words, Categories, History }

@Composable
fun AddWordsScreen(
    onClose: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AddWordsViewModel = koinInject(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val contentScrollState = rememberScrollState()
    var tab by remember { mutableStateOf(AddWordsTab.Words) }

    val difficultyLabels = listOf(
        stringResource(Res.string.nav_customize_easy),
        stringResource(Res.string.nav_customize_medium),
        stringResource(Res.string.nav_customize_hard),
    )
    val difficultyColors = listOf(ColorWin, ColorCrew, ColorImp)
    val customCategories = uiState.categories.filter { it.isCustom }

    Box(modifier = modifier.fillMaxSize()) {
        GridBackground(tint = ColorBorder, opacity = 0.32f)
        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
                .padding(horizontal = 24.dp)
                .padding(top = 48.dp, bottom = 24.dp),
        ) {
            CloseableScreenHeader(
                title = stringResource(Res.string.nav_add_words_title),
                onClose = onClose,
            )

            Spacer(Modifier.height(16.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                OutlinedTabButton(
                    text = stringResource(Res.string.nav_add_words_words_tab),
                    selected = tab == AddWordsTab.Words,
                    onClick = { tab = AddWordsTab.Words },
                    modifier = Modifier.weight(1f),
                )
                OutlinedTabButton(
                    text = stringResource(Res.string.nav_add_words_categories_tab),
                    selected = tab == AddWordsTab.Categories,
                    onClick = { tab = AddWordsTab.Categories },
                    modifier = Modifier.weight(1f),
                )
                OutlinedTabButton(
                    text = stringResource(Res.string.nav_add_words_history_tab),
                    selected = tab == AddWordsTab.History,
                    onClick = { tab = AddWordsTab.History },
                    modifier = Modifier.weight(1f),
                )
            }

            Spacer(Modifier.height(14.dp))
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .verticalScroll(contentScrollState),
            ) {
                when (tab) {
                    AddWordsTab.Words -> WordsTabContent(
                        uiState = uiState,
                        difficultyLabels = difficultyLabels,
                        difficultyColors = difficultyColors,
                        onWordTextChange = viewModel::onWordTextChange,
                        onHintTextChange = viewModel::onHintTextChange,
                        onDifficultyChange = viewModel::setDifficulty,
                        onCategorySelect = viewModel::selectCategory,
                    )

                    AddWordsTab.Categories -> CategoriesTabContent(
                        customCategoryName = uiState.customCategoryName,
                        customCategories = customCategories,
                        onCustomCategoryNameChange = viewModel::onCustomCategoryNameChange,
                        onAddCategory = viewModel::addCustomCategory,
                        onDeleteCategory = viewModel::deleteCustomCategory,
                    )

                    AddWordsTab.History -> HistoryTabContent(
                        customWords = uiState.customWords,
                        customCategories = customCategories,
                        difficultyLabels = difficultyLabels,
                        onEditWord = { wordId ->
                            viewModel.startEditing(wordId)
                            tab = AddWordsTab.Words
                        },
                        onDeleteWord = viewModel::deleteWord,
                    )
                }
                Spacer(Modifier.height(8.dp))
            }

            if (tab == AddWordsTab.Words) {
                Spacer(Modifier.height(12.dp))
                PrimaryButton(
                    text = if (uiState.editingWordId == null) stringResource(Res.string.nav_add_words_add) else stringResource(
                        Res.string.nav_add_words_save
                    ),
                    onClick = viewModel::saveWord,
                )
            }
        }
    }
}

@Composable
private fun WordsTabContent(
    uiState: AddWordsUiState,
    difficultyLabels: List<String>,
    difficultyColors: List<Color>,
    onWordTextChange: (String) -> Unit,
    onHintTextChange: (String) -> Unit,
    onDifficultyChange: (Int) -> Unit,
    onCategorySelect: (String) -> Unit,
) {
    Text(
        text = stringResource(Res.string.nav_add_words_word),
        color = ColorMuted,
        style = MaterialTheme.typography.labelSmall,
    )
    Spacer(Modifier.height(8.dp))
    FormInput(
        value = uiState.wordText,
        placeholder = stringResource(Res.string.nav_add_words_word_placeholder),
        onValueChange = onWordTextChange,
    )

    Spacer(Modifier.height(12.dp))
    Text(
        text = stringResource(Res.string.nav_add_words_hint),
        color = ColorMuted,
        style = MaterialTheme.typography.labelSmall,
    )
    Spacer(Modifier.height(8.dp))
    FormInput(
        value = uiState.hintText,
        placeholder = stringResource(Res.string.nav_add_words_hint_placeholder),
        onValueChange = onHintTextChange,
    )

    Spacer(Modifier.height(12.dp))
    Text(
        text = stringResource(Res.string.nav_add_words_difficulty),
        color = ColorMuted,
        style = MaterialTheme.typography.labelSmall,
    )
    Spacer(Modifier.height(8.dp))
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
        difficultyLabels.forEachIndexed { index, label ->
            val selected = uiState.difficultyLevel == index
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(38.dp)
                    .border(1.dp, if (selected) difficultyColors[index] else ColorBorder)
                    .background(if (selected) difficultyColors[index].copy(alpha = 0.12f) else ColorSurface)
                    .clickable { onDifficultyChange(index) },
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = label,
                    color = if (selected) difficultyColors[index] else ColorMuted,
                    style = MaterialTheme.typography.labelSmall,
                )
            }
        }
    }

    Spacer(Modifier.height(12.dp))
    Text(
        text = stringResource(Res.string.nav_add_words_category),
        color = ColorMuted,
        style = MaterialTheme.typography.labelSmall,
    )
    Spacer(Modifier.height(8.dp))
    uiState.categories.chunked(2).forEach { row ->
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            row.forEach { category ->
                val selected = uiState.selectedCategoryId == category.id
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(42.dp)
                        .border(1.dp, if (selected) ColorCrew else ColorBorder)
                        .background(if (selected) ColorCrew.copy(alpha = 0.12f) else ColorSurface)
                        .clickable { onCategorySelect(category.id) },
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = category.name,
                        color = if (selected) ColorCrew else ColorText,
                        style = MaterialTheme.typography.labelSmall,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
            if (row.size == 1) {
                Spacer(Modifier.weight(1f))
            }
        }
        Spacer(Modifier.height(8.dp))
    }
}

@Composable
private fun CategoriesTabContent(
    customCategoryName: String,
    customCategories: List<CategoryEntity>,
    onCustomCategoryNameChange: (String) -> Unit,
    onAddCategory: () -> Unit,
    onDeleteCategory: (String) -> Unit,
) {
    Text(
        text = stringResource(Res.string.nav_add_words_custom_categories),
        color = ColorMuted,
        style = MaterialTheme.typography.labelSmall,
    )
    Spacer(Modifier.height(8.dp))
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        FormInput(
            value = customCategoryName,
            placeholder = stringResource(Res.string.nav_add_words_category_name_placeholder),
            onValueChange = onCustomCategoryNameChange,
            modifier = Modifier.weight(1f),
        )
        MiniActionButton(
            label = stringResource(Res.string.nav_add_words_add_category),
            onClick = onAddCategory,
            height = 44.dp,
        )
    }
    Spacer(Modifier.height(12.dp))

    if (customCategories.isEmpty()) {
        EmptyStatePanel(text = stringResource(Res.string.nav_add_words_empty_custom_categories))
    } else {
        customCategories.forEach { category ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(ColorSurface)
                    .border(1.dp, ColorBorder)
                    .padding(horizontal = 12.dp, vertical = 10.dp),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "${category.name} · ${category.wordCount}",
                        color = ColorText,
                        style = MaterialTheme.typography.bodySmall,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    MiniActionButton(
                        label = stringResource(Res.string.nav_add_words_delete),
                        onClick = { onDeleteCategory(category.id) },
                    )
                }
            }
            Spacer(Modifier.height(8.dp))
        }
    }
}

@Composable
private fun HistoryTabContent(
    customWords: List<CustomWordItem>,
    customCategories: List<CategoryEntity>,
    difficultyLabels: List<String>,
    onEditWord: (Long) -> Unit,
    onDeleteWord: (Long) -> Unit,
) {
    Text(
        text = stringResource(Res.string.nav_add_words_custom_words),
        color = ColorMuted,
        style = MaterialTheme.typography.labelSmall,
    )
    Spacer(Modifier.height(8.dp))
    if (customWords.isEmpty()) {
        EmptyStatePanel(text = stringResource(Res.string.nav_add_words_empty))
    } else {
        customWords.forEach { word ->
            CustomWordRow(
                word = word,
                difficultyLabel = difficultyLabels.getOrElse(word.difficultyLevel) {
                    stringResource(Res.string.nav_add_words_unknown_difficulty)
                },
                onEdit = { onEditWord(word.id) },
                onDelete = { onDeleteWord(word.id) },
            )
            Spacer(Modifier.height(8.dp))
        }
    }

    Spacer(Modifier.height(14.dp))
    Text(
        text = stringResource(Res.string.nav_add_words_custom_categories),
        color = ColorMuted,
        style = MaterialTheme.typography.labelSmall,
    )
    Spacer(Modifier.height(8.dp))
    if (customCategories.isEmpty()) {
        EmptyStatePanel(text = stringResource(Res.string.nav_add_words_empty_custom_categories))
    } else {
        customCategories.forEach { category ->
            Text(
                text = stringResource(
                    Res.string.nav_add_words_history_category_item,
                    category.name,
                    category.wordCount.toString(),
                ),
                color = ColorText,
                style = MaterialTheme.typography.bodySmall,
            )
            Spacer(Modifier.height(4.dp))
        }
    }
}

@Composable
private fun EmptyStatePanel(text: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(ColorSurface)
            .border(1.dp, ColorBorder)
            .padding(horizontal = 14.dp, vertical = 16.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text.uppercase(),
            color = ColorMuted,
            style = MaterialTheme.typography.labelMedium,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun FormInput(
    value: String,
    placeholder: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier.fillMaxWidth(),
) {
    Box(
        modifier = modifier
            .height(44.dp)
            .background(ColorSurface)
            .border(1.dp, ColorBorder)
            .padding(horizontal = 12.dp),
        contentAlignment = Alignment.CenterStart,
    ) {
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            singleLine = true,
            textStyle = MaterialTheme.typography.bodyMedium.copy(color = ColorText),
            cursorBrush = SolidColor(ColorCrew),
            modifier = Modifier.fillMaxWidth(),
            decorationBox = { innerTextField ->
                if (value.isEmpty()) {
                    Text(
                        text = placeholder,
                        color = ColorMuted,
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
                innerTextField()
            },
        )
    }
}

@Composable
private fun CustomWordRow(
    word: CustomWordItem,
    difficultyLabel: String,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(ColorSurface)
            .border(1.dp, ColorBorder)
            .padding(12.dp),
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = word.text,
                color = ColorText,
                style = MaterialTheme.typography.titleMedium,
            )
            if (word.hint.isNotBlank()) {
                Text(
                    text = word.hint,
                    color = ColorMuted,
                    style = MaterialTheme.typography.bodySmall,
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "${word.categoryName} · $difficultyLabel",
                    color = ColorMuted,
                    style = MaterialTheme.typography.labelSmall,
                )
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    MiniActionButton(
                        label = stringResource(Res.string.nav_add_words_edit),
                        onClick = onEdit,
                    )
                    MiniActionButton(
                        label = stringResource(Res.string.nav_add_words_delete),
                        onClick = onDelete,
                    )
                }
            }
        }
    }
}

@Composable
private fun MiniActionButton(
    label: String,
    onClick: () -> Unit,
    height: Dp = 30.dp,
) {
    Box(
        modifier = Modifier
            .height(height)
            .border(1.dp, ColorBorder)
            .padding(horizontal = 10.dp)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = label,
            color = ColorMuted,
            style = MaterialTheme.typography.labelSmall,
        )
    }
}
