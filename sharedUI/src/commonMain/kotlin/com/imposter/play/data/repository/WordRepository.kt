package com.imposter.play.data.repository

import com.imposter.play.engine.Word
import com.imposter.play.data.entities.WordEntity
import com.imposter.play.data.entities.PlayedHistoryEntity
import com.imposter.play.data.local.CATEGORY_ALL
import com.imposter.play.data.local.dao.CategoryDao
import com.imposter.play.data.local.dao.PlayedHistoryDao
import com.imposter.play.data.local.dao.WordDao
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

data class CustomWordItem(
    val id: Long,
    val text: String,
    val hint: String,
    val categoryId: String,
    val categoryName: String,
    val difficultyLevel: Int,
)

enum class CustomWordMutationResult {
    Success,
    InvalidInput,
    Duplicate,
    NotFound,
}

class WordRepository(
    private val wordDao: WordDao,
    private val playedHistoryDao: PlayedHistoryDao,
    private val categoryDao: CategoryDao,
    private val ioDispatcher: CoroutineDispatcher
) {
    /**
     * Get a random word from selected categories, excluding recently played words.
     * Uses pool-based selection - all words have equal probability.
     */
    suspend fun getRandomWord(
        selectedCategoryIds: Set<String>,
        difficulty: Int,
    ): Word?  = withContext(ioDispatcher){
        val excludeIds = playedHistoryDao.getRecentWordIds().toSet()

        val initialWord = if (CATEGORY_ALL in selectedCategoryIds) {
            wordDao.getRandomWordFromAll(excludeIds, difficulty)
        } else {
            wordDao.getRandomWord(selectedCategoryIds, excludeIds, difficulty)
        }

        val wordEntity = initialWord ?: run {
            // If all words for this pool are in history, rotate by clearing the history buffer.
            playedHistoryDao.clearAll()
            if (CATEGORY_ALL in selectedCategoryIds) {
                wordDao.getRandomWordFromAllNoExclusions(difficulty)
            } else {
                wordDao.getRandomWordNoExclusions(selectedCategoryIds, difficulty)
            }
        }

        // Mark as played and prune history
        wordEntity?.let { entity ->
            val now = kotlin.time.Clock.System.now()
            playedHistoryDao.markPlayed(
                PlayedHistoryEntity(
                    wordId = entity.id,
                    timestamp = now.toEpochMilliseconds()
                )
            )
            playedHistoryDao.pruneHistory()
        }

        return@withContext wordEntity?.let {
            Word(real = it.text, hint = it.hint ?: "")
        }
    }

    fun getCustomWordsFlow(): Flow<List<CustomWordItem>> =
        wordDao.getCustomWordsFlow().map { words ->
            words.map { word ->
                CustomWordItem(
                    id = word.id,
                    text = word.text,
                    hint = word.hint.orEmpty(),
                    categoryId = word.categoryId,
                    categoryName = word.categoryName,
                    difficultyLevel = word.difficultyLevel,
                )
            }
        }

    suspend fun addCustomWord(
        text: String,
        hint: String,
        categoryId: String,
        difficultyLevel: Int,
    ): CustomWordMutationResult = withContext(ioDispatcher) {
        val normalizedText = text.trim()
        val normalizedHint = hint.trim()
        val normalizedCategoryId = categoryId.trim()

        if (normalizedText.isEmpty() || normalizedCategoryId.isEmpty() || difficultyLevel !in 0..2) {
            return@withContext CustomWordMutationResult.InvalidInput
        }

        val duplicate = wordDao.findByNaturalKeyCaseInsensitive(
            categoryId = normalizedCategoryId,
            text = normalizedText,
            difficultyLevel = difficultyLevel,
        )
        if (duplicate != null) {
            return@withContext CustomWordMutationResult.Duplicate
        }

        wordDao.insert(
            WordEntity(
                text = normalizedText,
                hint = normalizedHint.ifEmpty { null },
                categoryId = normalizedCategoryId,
                difficultyLevel = difficultyLevel,
                isCustom = true,
            )
        )
        syncCategoryWordCount(normalizedCategoryId)
        CustomWordMutationResult.Success
    }

    suspend fun updateCustomWord(
        id: Long,
        text: String,
        hint: String,
        categoryId: String,
        difficultyLevel: Int,
    ): CustomWordMutationResult = withContext(ioDispatcher) {
        val existing = wordDao.getCustomById(id) ?: return@withContext CustomWordMutationResult.NotFound
        val normalizedText = text.trim()
        val normalizedHint = hint.trim()
        val normalizedCategoryId = categoryId.trim()

        if (normalizedText.isEmpty() || normalizedCategoryId.isEmpty() || difficultyLevel !in 0..2) {
            return@withContext CustomWordMutationResult.InvalidInput
        }

        val duplicate = wordDao.findByNaturalKeyCaseInsensitiveExcludingId(
            categoryId = normalizedCategoryId,
            text = normalizedText,
            difficultyLevel = difficultyLevel,
            excludeId = id,
        )
        if (duplicate != null) {
            return@withContext CustomWordMutationResult.Duplicate
        }

        wordDao.updateCustomWordById(
            id = id,
            text = normalizedText,
            hint = normalizedHint.ifEmpty { null },
            categoryId = normalizedCategoryId,
            difficultyLevel = difficultyLevel,
        )

        syncCategoryWordCount(existing.categoryId, normalizedCategoryId)
        CustomWordMutationResult.Success
    }

    suspend fun deleteCustomWord(id: Long): Boolean = withContext(ioDispatcher) {
        val existing = wordDao.getCustomById(id) ?: return@withContext false
        val deletedRows = wordDao.deleteCustomById(id)
        if (deletedRows > 0) {
            syncCategoryWordCount(existing.categoryId)
            true
        } else {
            false
        }
    }

    private suspend fun syncCategoryWordCount(vararg categoryIds: String) {
        categoryIds
            .asSequence()
            .filter { it.isNotBlank() }
            .toSet()
            .forEach { categoryId ->
                val count = wordDao.getCountByCategory(categoryId)
                categoryDao.updateWordCount(categoryId, count)
            }
    }
}
