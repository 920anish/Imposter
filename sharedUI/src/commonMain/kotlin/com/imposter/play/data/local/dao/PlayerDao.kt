package com.imposter.play.data.local.dao

import androidx.room3.Dao
import androidx.room3.Delete
import androidx.room3.Insert
import androidx.room3.OnConflictStrategy
import androidx.room3.Query
import androidx.room3.Update
import com.imposter.play.data.entities.PlayerEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PlayerDao {

    @Query("SELECT * FROM players ORDER BY lobbyOrder ASC")
    fun getAllFlow(): Flow<List<PlayerEntity>>

    @Query("SELECT * FROM players ORDER BY lobbyOrder ASC")
    suspend fun getAll(): List<PlayerEntity>

    @Query("SELECT * FROM players WHERE isActive = 1 ORDER BY lobbyOrder ASC")
    suspend fun getActive(): List<PlayerEntity>

    @Query("SELECT * FROM players WHERE isActive = 1 ORDER BY lobbyOrder ASC")
    fun getActiveFlow(): Flow<List<PlayerEntity>>

    @Query("SELECT * FROM players WHERE id = :id")
    suspend fun getById(id: Long): PlayerEntity?

    @Query("SELECT * FROM players WHERE name = :name LIMIT 1")
    suspend fun getByName(name: String): PlayerEntity?

    @Query("SELECT * FROM players WHERE name = :name AND id != :excludeId LIMIT 1")
    suspend fun getByNameExcludingId(name: String, excludeId: Long): PlayerEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(player: PlayerEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(players: List<PlayerEntity>)

    @Update
    suspend fun update(player: PlayerEntity)

    @Delete
    suspend fun delete(player: PlayerEntity)

    @Query("DELETE FROM players WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("UPDATE players SET isActive = :active WHERE id = :playerId")
    suspend fun setActive(playerId: Long, active: Boolean)

    @Query("UPDATE players SET lobbyOrder = :order WHERE id = :playerId")
    suspend fun setLobbyOrder(playerId: Long, order: Int)

    @Query("""
        UPDATE players 
        SET totalGames = totalGames + 1, 
            totalWins = totalWins + CASE WHEN :won THEN 1 ELSE 0 END
        WHERE id = :playerId
    """)
    suspend fun recordGameResult(playerId: Long, won: Boolean)

    @Query("SELECT COUNT(*) FROM players WHERE isActive = 1")
    suspend fun getActiveCount(): Int
}