package com.imposter.play.data.local.dao

import androidx.room3.Dao
import androidx.room3.Insert
import androidx.room3.OnConflictStrategy
import androidx.room3.Query
import com.imposter.play.data.entities.PlayedHistoryEntity

@Dao
interface PlayedHistoryDao {

    /**
     * Mark a word as played with current timestamp.
     * Uses REPLACE to update timestamp if word was already in history.
     */
    @Insert(onConflict = OnConflictStrategy.Companion.REPLACE)
    suspend fun markPlayed(entry: PlayedHistoryEntity)

    /**
     * Get IDs of recently played words (for exclusion filter).
     */
    @Query("SELECT wordId FROM played_history")
    suspend fun getRecentWordIds(): List<Long>

    /**
     * Prune the oldest entries to keep history within buffer size.
     * This ensures the database stays lean and words can re-enter the pool.
     *
     * The "1000-word buffer" rule: words are locked until 1000 other words played.
     */
    @Query("""
        DELETE FROM played_history 
        WHERE wordId IN (
            SELECT wordId FROM played_history 
            ORDER BY timestamp ASC 
            LIMIT MAX(0, (SELECT COUNT(*) FROM played_history) - :bufferSize)
        )
    """)
    suspend fun pruneHistory(bufferSize: Int = 1000)

    /**
     * Clear all history (useful for testing or reset).
     */
    @Query("DELETE FROM played_history")
    suspend fun clearAll()

    /**
     * Get count of played words in history.
     */
    @Query("SELECT COUNT(*) FROM played_history")
    suspend fun getCount(): Int
}