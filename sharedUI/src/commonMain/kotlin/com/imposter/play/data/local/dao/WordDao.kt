package com.imposter.play.data.local.dao

import androidx.room3.Dao
import androidx.room3.Delete
import androidx.room3.Insert
import androidx.room3.OnConflictStrategy
import androidx.room3.Query
import com.imposter.play.data.entities.WordEntity
import kotlinx.coroutines.flow.Flow

data class CustomWordWithCategoryEntity(
    val id: Long,
    val text: String,
    val hint: String?,
    val categoryId: String,
    val difficultyLevel: Int,
    val categoryName: String,
)

@Dao
interface WordDao {

    /**
     * Get a random word from selected categories, excluding recently played words.
     * This is the "pool-based" approach - all words have equal probability.
     *
     * @param categoryIds Set of category IDs to pull from
     * @param excludeWordIds Word IDs to exclude (from PlayedHistory)
     * @param difficulty Difficulty level filter (0=easy, 1=medium, 2=hard)
     */
    @Query("""
        SELECT * FROM words 
        WHERE categoryId IN (:categoryIds) 
        AND difficultyLevel = :difficulty
        AND id NOT IN (:excludeWordIds)
        ORDER BY RANDOM() 
        LIMIT 1
    """)
    suspend fun getRandomWord(
        categoryIds: Set<String>,
        excludeWordIds: Set<Long>,
        difficulty: Int,
    ): WordEntity?

    @Query(
        """
        SELECT * FROM words
        WHERE categoryId IN (:categoryIds)
        AND difficultyLevel = :difficulty
        ORDER BY RANDOM()
        LIMIT 1
        """
    )
    suspend fun getRandomWordNoExclusions(
        categoryIds: Set<String>,
        difficulty: Int,
    ): WordEntity?

    /**
     * Get a random word from ALL categories (CATEGORY_ALL mode).
     * Bypasses category filter for full random pool.
     */
    @Query("""
        SELECT * FROM words 
        WHERE difficultyLevel = :difficulty
        AND id NOT IN (:excludeWordIds)
        ORDER BY RANDOM() 
        LIMIT 1
    """)
    suspend fun getRandomWordFromAll(
        excludeWordIds: Set<Long>,
        difficulty: Int,
    ): WordEntity?

    @Query(
        """
        SELECT * FROM words
        WHERE difficultyLevel = :difficulty
        ORDER BY RANDOM()
        LIMIT 1
        """
    )
    suspend fun getRandomWordFromAllNoExclusions(difficulty: Int): WordEntity?

    @Query("SELECT * FROM words WHERE categoryId = :categoryId ORDER BY text ASC")
    suspend fun getByCategory(categoryId: String): List<WordEntity>

    @Query("SELECT * FROM words WHERE id = :id")
    suspend fun getById(id: Long): WordEntity?

    @Query(
        """
        SELECT w.id, w.text, w.hint, w.categoryId, w.difficultyLevel, c.name AS categoryName
        FROM words w
        INNER JOIN categories c ON c.id = w.categoryId
        WHERE w.isCustom = 1
        ORDER BY w.id DESC
        """
    )
    fun getCustomWordsFlow(): Flow<List<CustomWordWithCategoryEntity>>

    @Query("SELECT * FROM words WHERE id = :id AND isCustom = 1 LIMIT 1")
    suspend fun getCustomById(id: Long): WordEntity?

    @Query(
        """
        SELECT * FROM words
        WHERE categoryId = :categoryId
        AND text = :text
        AND difficultyLevel = :difficultyLevel
        LIMIT 1
        """
    )
    suspend fun findByNaturalKey(
        categoryId: String,
        text: String,
        difficultyLevel: Int,
    ): WordEntity?

    @Query(
        """
        SELECT * FROM words
        WHERE categoryId = :categoryId
        AND difficultyLevel = :difficultyLevel
        AND LOWER(text) = LOWER(:text)
        LIMIT 1
        """
    )
    suspend fun findByNaturalKeyCaseInsensitive(
        categoryId: String,
        text: String,
        difficultyLevel: Int,
    ): WordEntity?

    @Query(
        """
        SELECT * FROM words
        WHERE categoryId = :categoryId
        AND difficultyLevel = :difficultyLevel
        AND LOWER(text) = LOWER(:text)
        AND id != :excludeId
        LIMIT 1
        """
    )
    suspend fun findByNaturalKeyCaseInsensitiveExcludingId(
        categoryId: String,
        text: String,
        difficultyLevel: Int,
        excludeId: Long,
    ): WordEntity?

    @Query("SELECT COUNT(*) FROM words WHERE categoryId = :categoryId")
    suspend fun getCountByCategory(categoryId: String): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(word: WordEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(words: List<WordEntity>)

    @Delete
    suspend fun delete(word: WordEntity)

    @Query("DELETE FROM words WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM words WHERE id = :id AND isCustom = 1")
    suspend fun deleteCustomById(id: Long): Int

    @Query("UPDATE words SET hint = :hint WHERE id = :id")
    suspend fun updateHintById(id: Long, hint: String?)

    @Query(
        """
        UPDATE words
        SET text = :text,
            hint = :hint,
            categoryId = :categoryId,
            difficultyLevel = :difficultyLevel
        WHERE id = :id
        AND isCustom = 1
        """
    )
    suspend fun updateCustomWordById(
        id: Long,
        text: String,
        hint: String?,
        categoryId: String,
        difficultyLevel: Int,
    ): Int
}
