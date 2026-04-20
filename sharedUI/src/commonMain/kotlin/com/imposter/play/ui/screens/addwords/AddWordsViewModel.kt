package com.imposter.play.ui.screens.addwords

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.imposter.play.data.entities.CategoryEntity
import com.imposter.play.data.repository.CategoryMutationResult
import com.imposter.play.data.repository.CategoryRepository
import com.imposter.play.data.repository.CustomWordItem
import com.imposter.play.data.repository.CustomWordMutationResult
import com.imposter.play.data.repository.WordRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

data class AddWordsUiState(
    val categories: List<CategoryEntity> = emptyList(),
    val customWords: List<CustomWordItem> = emptyList(),
    val selectedCategoryId: String? = null,
    val customCategoryName: String = "",
    val difficultyLevel: Int = 1,
    val wordText: String = "",
    val hintText: String = "",
    val editingWordId: Long? = null,
)

class AddWordsViewModel(
    private val categoryRepository: CategoryRepository,
    private val wordRepository: WordRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddWordsUiState())
    val uiState: StateFlow<AddWordsUiState> = _uiState.asStateFlow()

    init {
        observeData()
    }

    private fun observeData() {
        viewModelScope.launch {
            combine(
                categoryRepository.getAllCategoriesFlow(),
                wordRepository.getCustomWordsFlow(),
            ) { categories, customWords ->
                categories to customWords
            }.collectLatest { (categories, customWords) ->
                val current = _uiState.value
                val selectedCategoryId = current.selectedCategoryId
                    ?.takeIf { selected -> categories.any { it.id == selected } }
                    ?: categories.firstOrNull()?.id

                _uiState.value = current.copy(
                    categories = categories,
                    customWords = customWords,
                    selectedCategoryId = selectedCategoryId,
                )
            }
        }
    }

    fun onWordTextChange(value: String) {
        _uiState.value = _uiState.value.copy(
            wordText = value.take(48),
        )
    }

    fun onHintTextChange(value: String) {
        _uiState.value = _uiState.value.copy(
            hintText = value.take(96),
        )
    }

    fun onCustomCategoryNameChange(value: String) {
        _uiState.value = _uiState.value.copy(
            customCategoryName = value.take(28),
        )
    }

    fun selectCategory(categoryId: String) {
        _uiState.value = _uiState.value.copy(
            selectedCategoryId = categoryId,
        )
    }

    fun setDifficulty(level: Int) {
        if (level !in 0..2) return
        _uiState.value = _uiState.value.copy(
            difficultyLevel = level,
        )
    }

    fun startEditing(wordId: Long) {
        val word = _uiState.value.customWords.firstOrNull { it.id == wordId } ?: return
        _uiState.value = _uiState.value.copy(
            editingWordId = word.id,
            wordText = word.text,
            hintText = word.hint,
            selectedCategoryId = word.categoryId,
            difficultyLevel = word.difficultyLevel,
        )
    }

    fun addCustomCategory() {
        val state = _uiState.value
        viewModelScope.launch {
            val outcome = categoryRepository.createCustomCategory(state.customCategoryName)
            when (outcome.result) {
                CategoryMutationResult.Success -> {
                    _uiState.value = _uiState.value.copy(
                        customCategoryName = "",
                        selectedCategoryId = outcome.categoryId ?: state.selectedCategoryId,
                    )
                }
                CategoryMutationResult.InvalidInput -> Unit
                CategoryMutationResult.Duplicate -> Unit
                CategoryMutationResult.InUse,
                CategoryMutationResult.NotFound -> Unit
            }
        }
    }

    fun deleteCustomCategory(categoryId: String) {
        viewModelScope.launch {
            val result = categoryRepository.deleteCustomCategory(categoryId)
            val current = _uiState.value
            val fallbackCategory = current.categories.firstOrNull { it.id != categoryId }?.id
            when (result) {
                CategoryMutationResult.Success -> {
                    _uiState.value = _uiState.value.copy(
                        selectedCategoryId = if (current.selectedCategoryId == categoryId) fallbackCategory else current.selectedCategoryId,
                    )
                }
                CategoryMutationResult.NotFound,
                CategoryMutationResult.InvalidInput,
                CategoryMutationResult.Duplicate,
                CategoryMutationResult.InUse -> Unit
            }
        }
    }

    fun saveWord() {
        val state = _uiState.value
        val selectedCategoryId = state.selectedCategoryId ?: return

        viewModelScope.launch {
            val result = if (state.editingWordId == null) {
                wordRepository.addCustomWord(
                    text = state.wordText,
                    hint = state.hintText,
                    categoryId = selectedCategoryId,
                    difficultyLevel = state.difficultyLevel,
                )
            } else {
                wordRepository.updateCustomWord(
                    id = state.editingWordId,
                    text = state.wordText,
                    hint = state.hintText,
                    categoryId = selectedCategoryId,
                    difficultyLevel = state.difficultyLevel,
                )
            }

            when (result) {
                CustomWordMutationResult.Success -> {
                    _uiState.value = _uiState.value.copy(
                        editingWordId = null,
                        wordText = "",
                        hintText = "",
                        difficultyLevel = 1,
                    )
                }

                CustomWordMutationResult.InvalidInput -> Unit
                CustomWordMutationResult.Duplicate -> Unit
                CustomWordMutationResult.NotFound -> Unit
            }
        }
    }

    fun deleteWord(wordId: Long) {
        viewModelScope.launch {
            val deleted = wordRepository.deleteCustomWord(wordId)
            if (!deleted) return@launch
            val current = _uiState.value
            val clearedEditing = current.editingWordId == wordId
            _uiState.value = _uiState.value.copy(
                editingWordId = if (clearedEditing) null else current.editingWordId,
                wordText = if (clearedEditing) "" else current.wordText,
                hintText = if (clearedEditing) "" else current.hintText,
                difficultyLevel = if (clearedEditing) 1 else current.difficultyLevel,
            )
        }
    }
}
