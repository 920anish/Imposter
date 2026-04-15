package com.imposter.play.engine

import com.imposter.play.data.Word

sealed class GameState {
    data object Idle : GameState()

    data class RoleReveal(
        val playerIndex: Int,
        val isRevealed: Boolean,
    ) : GameState()

    data class Discussion(
        val secondsLeft: Int,
        val isRunning: Boolean,
    ) : GameState()

    data object Voting : GameState()

    data class Result(
        val imposterCaught: Boolean,
    ) : GameState()
}

data class VoteCount(
    val playerIndex: Int,
    val votes: Int,
)

data class GameSession(
    val config: GameConfig = GameConfig(),
    val state: GameState = GameState.Idle,
    val imposterIndex: Int = 0,
    val currentWord: Word = Word(real = "PLACEHOLDER", hint = ""),
    val votes: List<VoteCount> = emptyList(),
    val revealedPlayers: Set<Int> = emptySet(),
    val playerNames: List<String> = emptyList(),
    val playerIds: List<Long> = emptyList(),
) {
    val normalizedPlayerNames: List<String>
        get() = playerNames.ifEmpty {
            (0 until config.playerCount).map { index ->
                "Player ${index + 1}"
            }
        }

    val winningPlayerIndex: Int?
        get() {
            val maxVotes = votes.maxOfOrNull { it.votes } ?: return null
            val leaders = votes.filter { it.votes == maxVotes }
            return if (leaders.size == 1) leaders.first().playerIndex else null
        }
}


sealed class PlayerRole {
    data class Crew(val word: String) : PlayerRole()
    data class Imposter(
        val hint: String,
        val hintEnabled: Boolean,
    ) : PlayerRole()
    data object Unknown : PlayerRole()
}
