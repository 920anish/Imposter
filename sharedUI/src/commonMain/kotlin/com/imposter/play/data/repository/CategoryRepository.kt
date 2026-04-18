package com.imposter.play.data.repository

import com.imposter.play.data.entities.CategoryEntity
import com.imposter.play.data.local.dao.CategoryDao
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class CategoryRepository(
    private val categoryDao: CategoryDao,
    private val ioDispatcher: CoroutineDispatcher
) {
    /**
     * Get all categories as a Flow for reactive UI
     */
    fun getAllCategoriesFlow(): Flow<List<CategoryEntity>> = categoryDao.getAllFlow()

    /**
     * Add a custom category
     */
    suspend fun addCustomCategory(
        id: String,
        name: String,
        iconRes: String,
        displayOrder: Int = 0,
    ) = withContext(ioDispatcher) {
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
    suspend fun deleteCustomCategory(categoryId: String) = withContext(ioDispatcher) {
        categoryDao.deleteCustomById(categoryId)
    }

    /**
     * Update word count for a category
     */
    suspend fun updateWordCount(categoryId: String, count: Int) = withContext(ioDispatcher){
        categoryDao.updateWordCount(categoryId, count)
    }
}
