package com.imposter.play.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.imposter.play.data.entities.CategoryEntity
import com.imposter.play.data.local.AppPreferences
import com.imposter.play.data.local.CATEGORY_ALL
import com.imposter.play.data.repository.CategoryRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

data class CustomizeUiState(
    val categories: List<CategoryEntity> = emptyList(),
    val selectedCategoryIds: Set<String> = setOf(CATEGORY_ALL),
    val difficulty: Int = 1,
    val imposterHintEnabled: Boolean = true,
    val isLoading: Boolean = true,
)

class CustomizeViewModel(
    private val categoryRepository: CategoryRepository,
    private val appPreferences: AppPreferences,
) : ViewModel() {

    private val _uiState = MutableStateFlow(CustomizeUiState())
    val uiState: StateFlow<CustomizeUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            val categories = categoryRepository.getAllCategories()
            val settings = appPreferences.settings.first()
            
            _uiState.value = CustomizeUiState(
                categories = categories,
                selectedCategoryIds = settings.selectedCategoryIds.ifEmpty { setOf(CATEGORY_ALL) },
                difficulty = settings.difficulty.level,
                imposterHintEnabled = settings.isHintEnabled,
                isLoading = false,
            )
        }
    }

    fun toggleCategory(categoryId: String) {
        val current = _uiState.value.selectedCategoryIds.toMutableSet()
        
        if (categoryId == CATEGORY_ALL) {
            // Selecting "All" clears other selections
            current.clear()
            current.add(CATEGORY_ALL)
        } else {
            // Selecting a specific category removes "All"
            current.remove(CATEGORY_ALL)
            
            if (current.contains(categoryId)) {
                current.remove(categoryId)
                // If nothing selected, default to "All"
                if (current.isEmpty()) {
                    current.add(CATEGORY_ALL)
                }
            } else {
                current.add(categoryId)
            }
        }
        
        _uiState.value = _uiState.value.copy(selectedCategoryIds = current)
        
        viewModelScope.launch {
            appPreferences.setSelectedCategories(current)
        }
    }

    fun setDifficulty(level: Int) {
        _uiState.value = _uiState.value.copy(difficulty = level)
        viewModelScope.launch {
            appPreferences.setDifficulty(com.imposter.play.data.Difficulty.fromInt(level))
        }
    }

    fun setHintEnabled(enabled: Boolean) {
        _uiState.value = _uiState.value.copy(imposterHintEnabled = enabled)
        viewModelScope.launch {
            appPreferences.setHintsEnabled(enabled)
        }
    }
}
