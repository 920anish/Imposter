package com.imposter.play.data.repository

import com.imposter.play.data.entities.PlayerEntity
import com.imposter.play.data.local.dao.PlayerDao
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
     * Add a new player.
     */
    suspend fun addPlayer(name: String, isActive: Boolean = true): PlayerEntity = withContext(ioDispatcher)  {
        val allPlayers = playerDao.getAll()
        val existingNames = allPlayers.map { it.name }.toSet()
        val requested = name.trim().take(10).ifBlank { "Player 1" }
        var finalName = requested
        if (finalName in existingNames) {
            var next = 1
            while (true) {
                val candidate = "Player $next".take(10)
                if (candidate !in existingNames) {
                    finalName = candidate
                    break
                }
                next++
            }
        }

        val newPlayer = PlayerEntity(
            name = finalName,
            isActive = isActive,
            lobbyOrder = (allPlayers.maxOfOrNull { it.lobbyOrder } ?: -1) + 1,
        )
        val id = playerDao.insert(newPlayer)
         newPlayer.copy(id = id)
    }

    suspend fun setPlayerActive(playerId: Long, active: Boolean) = withContext(ioDispatcher) {
        playerDao.setActive(playerId, active)
    }

    suspend fun renamePlayer(playerId: Long, name: String) = withContext(ioDispatcher) {
        val trimmed = name.trim().take(10)
        if (trimmed.isEmpty()) return@withContext

        val duplicate = playerDao.getByNameExcludingId(trimmed, playerId)
        if (duplicate != null) return@withContext

        val current = playerDao.getById(playerId) ?: return@withContext
        playerDao.update(current.copy(name = trimmed))
    }

    suspend fun ensureDefaultPlayers() = withContext(ioDispatcher) {
        val allPlayers = playerDao.getAll()
        if (allPlayers.isNotEmpty()) return@withContext

        playerDao.insertAll(
            listOf(
                PlayerEntity(name = "Player 1", isActive = true, lobbyOrder = 0),
                PlayerEntity(name = "Player 2", isActive = true, lobbyOrder = 1),
                PlayerEntity(name = "Player 3", isActive = true, lobbyOrder = 2),
            )
        )
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
