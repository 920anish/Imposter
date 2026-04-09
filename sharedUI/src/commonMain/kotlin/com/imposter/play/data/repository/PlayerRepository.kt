package com.imposter.play.data.repository

import com.imposter.play.data.entities.PlayerEntity
import com.imposter.play.data.local.PlayerDao
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class PlayerRepository(
    private val playerDao: PlayerDao,
    private val ioDispatcher: CoroutineDispatcher
) {
    /**
     * Get all players as a Flow for reactive UI
     */
    fun getAllPlayersFlow(): Flow<List<PlayerEntity>> = playerDao.getAllFlow()

    /**
     * Get active players as a Flow
     */
    fun getActivePlayersFlow(): Flow<List<PlayerEntity>> = playerDao.getActiveFlow()

    /**
     * Get all players
     */
    suspend fun getAllPlayers(): List<PlayerEntity> = withContext(ioDispatcher){ playerDao.getAll() }

    /**
     * Get only active players (for current game)
     */
    suspend fun getActivePlayers(): List<PlayerEntity> = withContext(ioDispatcher){ playerDao.getActive() }

    /**
     * Get active player count
     */
    suspend fun getActiveCount(): Int = withContext(ioDispatcher){ playerDao.getActiveCount() }

    /**
     * Add a new player or get existing by name
     */
    suspend fun addPlayer(name: String): PlayerEntity = withContext(ioDispatcher)  {
        val existing = playerDao.getByName(name)
        if (existing != null) {
            // Reactivate existing player
            playerDao.setActive(existing.id, true)
            return@withContext existing
        }

        val newPlayer = PlayerEntity(
            name = name,
            isActive = true,
            lobbyOrder = playerDao.getActiveCount(),
        )
        val id = playerDao.insert(newPlayer)
         newPlayer.copy(id = id)
    }

    /**
     * Remove player from active game (deactivate, don't delete)
     */
    suspend fun deactivatePlayer(playerId: Long) = withContext(ioDispatcher) {
        playerDao.setActive(playerId, false)
    }

    suspend fun setPlayerActive(playerId: Long, active: Boolean) = withContext(ioDispatcher) {
        playerDao.setActive(playerId, active)
    }

    /**
     * Delete a player permanently
     */
    suspend fun deletePlayer(playerId: Long) = withContext(ioDispatcher) {
        playerDao.deleteById(playerId)
    }

    /**
     * Update player lobby order (for reordering)
     */
    suspend fun updateLobbyOrder(playerId: Long, order: Int) =withContext(ioDispatcher) {
        playerDao.setLobbyOrder(playerId, order)
    }

    /**
     * Record game result for a player
     */
    suspend fun recordGameResult(playerId: Long, won: Boolean)  = withContext(ioDispatcher){
        playerDao.recordGameResult(playerId, won)
    }

    /**
     * Record game results for all players
     * @param imposterWon true if imposter won, false if crew won
     * @param imposterPlayerId the player who was the imposter
     * @param allPlayerIds all players in the game
     */
    suspend fun recordGameResults(
        imposterWon: Boolean,
        imposterPlayerId: Long,
        allPlayerIds: List<Long>,
    )  = withContext(ioDispatcher){
        allPlayerIds.forEach { playerId ->
            val isImposter = playerId == imposterPlayerId
            val won = if (isImposter) imposterWon else !imposterWon
            playerDao.recordGameResult(playerId, won)
        }
    }
}
