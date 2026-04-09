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
) {
    val normalizedPlayerNames: List<String>
        get() = (0 until config.playerCount).map { index ->
            config.playerNames.getOrNull(index)?.trim().takeUnless { it.isNullOrBlank() } ?: "Player ${index + 1}"
        }

    val winningPlayerIndex: Int?
        get() = votes.maxByOrNull { it.votes }?.playerIndex
}


sealed class PlayerRole {
    data class Crew(val word: String) : PlayerRole()
    data class Imposter(
        val hint: String,
        val hintEnabled: Boolean,
    ) : PlayerRole()
    data object Unknown : PlayerRole()
}

