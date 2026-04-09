package com.imposter.play.data.repository

import com.imposter.play.data.entities.CategoryEntity
import com.imposter.play.data.local.CategoryDao
import kotlinx.coroutines.flow.Flow

class CategoryRepository(
    private val categoryDao: CategoryDao,
) {
    /**
     * Get all categories as a Flow for reactive UI
     */
    fun getAllCategoriesFlow(): Flow<List<CategoryEntity>> = categoryDao.getAllFlow()

    /**
     * Get all categories
     */
    suspend fun getAllCategories(): List<CategoryEntity> = categoryDao.getAll()

    /**
     * Get only enabled categories
     */
    suspend fun getEnabledCategories(): List<CategoryEntity> = categoryDao.getEnabled()

    /**
     * Toggle category enabled state
     */
    suspend fun toggleCategory(categoryId: String, enabled: Boolean) {
        categoryDao.setEnabled(categoryId, enabled)
    }

    /**
     * Add a custom category
     */
    suspend fun addCustomCategory(
        id: String,
        name: String,
        iconRes: String,
        displayOrder: Int = 0,
    ) {
        categoryDao.insert(
            CategoryEntity(
                id = id,
                name = name,
                iconRes = iconRes,
                isEnabled = true,
                displayOrder = displayOrder,
                isCustom = true,
                wordCount = 0,
            )
        )
    }

    /**
     * Delete a custom category (only custom categories can be deleted)
     */
    suspend fun deleteCustomCategory(categoryId: String) {
        categoryDao.deleteCustomById(categoryId)
    }

    /**
     * Update word count for a category
     */
    suspend fun updateWordCount(categoryId: String, count: Int) {
        categoryDao.updateWordCount(categoryId, count)
    }
}
