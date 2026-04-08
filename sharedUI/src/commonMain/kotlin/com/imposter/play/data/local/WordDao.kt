package com.imposter.play.data.local

import androidx.room3.Dao
import androidx.room3.Delete
import androidx.room3.Insert
import androidx.room3.OnConflictStrategy
import androidx.room3.Query
import com.imposter.play.data.entities.WordEntity

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

    @Query("SELECT * FROM words WHERE categoryId = :categoryId ORDER BY text ASC")
    suspend fun getByCategory(categoryId: String): List<WordEntity>

    @Query("SELECT * FROM words WHERE id = :id")
    suspend fun getById(id: Long): WordEntity?

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
}
