package com.imposter.play.data.repository

import com.imposter.play.data.Word
import com.imposter.play.data.entities.PlayedHistoryEntity
import com.imposter.play.data.local.CATEGORY_ALL
import com.imposter.play.data.local.PlayedHistoryDao
import com.imposter.play.data.local.WordDao
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class WordRepository(
    private val wordDao: WordDao,
    private val playedHistoryDao: PlayedHistoryDao,
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

        val wordEntity = if (CATEGORY_ALL in selectedCategoryIds) {
            wordDao.getRandomWordFromAll(excludeIds, difficulty)
        } else {
            wordDao.getRandomWord(selectedCategoryIds, excludeIds, difficulty)
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

    /**
     * Clear played history (for testing/reset)
     */
    suspend fun clearHistory()  = withContext(ioDispatcher){
        playedHistoryDao.clearAll()
    }
}
