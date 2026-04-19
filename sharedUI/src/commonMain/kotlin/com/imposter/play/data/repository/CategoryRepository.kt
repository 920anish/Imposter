package com.imposter.play.data.repository

import com.imposter.play.data.entities.CategoryEntity
import com.imposter.play.data.local.dao.CategoryDao
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

enum class CategoryMutationResult {
    Success,
    InvalidInput,
    Duplicate,
    InUse,
    NotFound,
}

data class CategoryMutationOutcome(
    val result: CategoryMutationResult,
    val categoryId: String? = null,
)

class CategoryRepository(
    private val categoryDao: CategoryDao,
    private val ioDispatcher: CoroutineDispatcher
) {
    /**
     * Get all categories as a Flow for reactive UI
     */
    fun getAllCategoriesFlow(): Flow<List<CategoryEntity>> = categoryDao.getAllFlow()

    suspend fun createCustomCategory(name: String): CategoryMutationOutcome = withContext(ioDispatcher) {
        val normalizedName = name.trim().replace(Regex("\\s+"), " ")
        if (normalizedName.isEmpty()) {
            return@withContext CategoryMutationOutcome(CategoryMutationResult.InvalidInput)
        }

        val duplicate = categoryDao.getByNameIgnoreCase(normalizedName)
        if (duplicate != null) {
            return@withContext CategoryMutationOutcome(CategoryMutationResult.Duplicate)
        }

        val allCategories = categoryDao.getAll()
        val nextDisplayOrder = categoryDao.getMaxDisplayOrder() + 1
        val existingIds = allCategories.map { it.id }.toSet()
        val baseId = buildCustomCategoryId(normalizedName)
        var generatedId = baseId
        var suffix = 2
        while (generatedId in existingIds) {
            generatedId = "${baseId}_$suffix"
            suffix++
        }

        categoryDao.insert(
            CategoryEntity(
                id = generatedId,
                name = normalizedName,
                iconRes = "custom_category",
                isEnabled = true,
                displayOrder = nextDisplayOrder,
                isCustom = true,
                wordCount = 0,
            )
        )
        CategoryMutationOutcome(
            result = CategoryMutationResult.Success,
            categoryId = generatedId,
        )
    }

    suspend fun deleteCustomCategory(categoryId: String): CategoryMutationResult = withContext(ioDispatcher) {
        val existing = categoryDao.getById(categoryId) ?: return@withContext CategoryMutationResult.NotFound
        if (!existing.isCustom) return@withContext CategoryMutationResult.InvalidInput

        val deleted = categoryDao.deleteCustomById(categoryId)
        if (deleted > 0) CategoryMutationResult.Success else CategoryMutationResult.NotFound
    }

    private fun buildCustomCategoryId(name: String): String {
        val slug = name
            .lowercase()
            .replace(Regex("[^a-z0-9]+"), "_")
            .trim('_')
            .ifEmpty { "custom" }
        return "custom_$slug"
    }
}
