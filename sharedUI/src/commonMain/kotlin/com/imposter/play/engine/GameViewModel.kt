package com.imposter.play.engine

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.imposter.play.data.GamePrefs
import com.imposter.play.data.GamePrefsStore
import com.imposter.play.data.WordDeck
import com.imposter.play.data.WordDictionary
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.random.Random

class GameViewModel(
    private val prefsStore: GamePrefsStore,
) : ViewModel() {

    private val _session = MutableStateFlow(GameSession())
    val session: StateFlow<GameSession> = _session.asStateFlow()

    private var wordDeck: WordDeck? = null
    private var discussionTimerJob: Job? = null

    fun onIntent(intent: GameIntent) {
        when (intent) {
            is GameIntent.UpdateSetupConfig -> updateSetupConfig(intent.config)
            is GameIntent.StartGame -> startGame(intent.config)
            GameIntent.RevealCard -> revealCard()
            GameIntent.NextPlayer -> nextPlayer()
            GameIntent.ToggleTimer -> toggleTimer()
            GameIntent.StartVoting -> startVoting()
            is GameIntent.CastVote -> castVote(intent.playerIndex)
            GameIntent.RevealResult -> revealResult()
            GameIntent.PlayAgain -> playAgain()
        }
    }

    fun loadPrefs() {
        viewModelScope.launch {
            val prefs = prefsStore.load()
            _session.value = _session.value.copy(
                config = GameConfig(
                    playerCount = prefs.playerCount.coerceIn(3, 10),
                    playerNames = prefs.playerNames,
                    category = prefs.lastCategory,
                    difficulty = prefs.lastDifficulty.coerceIn(0, 2),
                    imposterHintEnabled = prefs.imposterHintEnabled,
                )
            )
        }
    }

    private fun updateSetupConfig(config: GameConfig) {
        val normalized = config.copy(
            playerCount = config.playerCount.coerceIn(3, 10),
            category = normalizeCategory(config.category),
            difficulty = config.difficulty.coerceIn(0, 2),
        )
        _session.value = _session.value.copy(
            config = normalized
        )
        viewModelScope.launch {
            prefsStore.save(
                GamePrefs(
                    playerCount = normalized.playerCount,
                    playerNames = normalized.playerNames,
                    lastCategory = normalized.category,
                    lastDifficulty = normalized.difficulty,
                    imposterHintEnabled = normalized.imposterHintEnabled,
                )
            )
        }
    }

    private fun startGame(config: GameConfig) {
        val normalizedConfig = config.copy(
            playerCount = config.playerCount.coerceIn(3, 10),
            category = normalizeCategory(config.category),
            difficulty = config.difficulty.coerceIn(0, 2),
        )
        wordDeck = WordDeck(
            category = normalizedConfig.category,
            difficulty = normalizedConfig.difficulty,
        )

        val selectedWord = wordDeck!!.nextWord()
        val imposterIndex = Random.nextInt(until = normalizedConfig.playerCount)
        discussionTimerJob?.cancel()
        _session.value = GameSession(
            config = normalizedConfig,
            state = GameState.RoleReveal(playerIndex = 0, isRevealed = false),
            imposterIndex = imposterIndex,
            currentWord = selectedWord,
            votes = (0 until normalizedConfig.playerCount).map { VoteCount(it, 0) },
            revealedPlayers = emptySet(),
        )

        persistStartPreferences(normalizedConfig)
    }

    private fun revealCard() {
        val current = _session.value
        val state = current.state as? GameState.RoleReveal ?: return
        _session.value = current.copy(
            state = state.copy(isRevealed = true),
            revealedPlayers = current.revealedPlayers + state.playerIndex,
        )
    }

    private fun nextPlayer() {
        val current = _session.value
        val state = current.state as? GameState.RoleReveal ?: return
        val lastPlayerIndex = current.config.playerCount - 1

        if (state.playerIndex >= lastPlayerIndex) {
            discussionTimerJob?.cancel()
            _session.value = current.copy(
                state = GameState.Discussion(secondsLeft = 180, isRunning = true),
            )
            startDiscussionCountdown()
            return
        }

        _session.value = current.copy(
            state = GameState.RoleReveal(
                playerIndex = state.playerIndex + 1,
                isRevealed = false,
            ),
        )
    }

    private fun toggleTimer() {
        val current = _session.value
        val state = current.state as? GameState.Discussion ?: return

        if (state.isRunning) {
            discussionTimerJob?.cancel()
            _session.value = current.copy(
                state = state.copy(isRunning = false),
            )
            return
        }

        _session.value = current.copy(
            state = state.copy(isRunning = true),
        )
        startDiscussionCountdown()
    }

    private fun startDiscussionCountdown() {
        discussionTimerJob?.cancel()
        discussionTimerJob = viewModelScope.launch {
            while (true) {
                val current = _session.value
                val state = current.state as? GameState.Discussion ?: break
                if (!state.isRunning) break
                if (state.secondsLeft <= 0) {
                    _session.value = current.copy(state = GameState.Voting)
                    break
                }
                delay(1_000)
                val refreshed = _session.value
                val refreshedState = refreshed.state as? GameState.Discussion ?: break
                if (!refreshedState.isRunning) break
                _session.value = refreshed.copy(
                    state = refreshedState.copy(secondsLeft = refreshedState.secondsLeft - 1),
                )
            }
        }
    }

    private fun startVoting() {
        discussionTimerJob?.cancel()
        val current = _session.value
        _session.value = current.copy(state = GameState.Voting)
    }

    private fun castVote(playerIndex: Int) {
        val current = _session.value
        if (current.state !is GameState.Voting) return
        if (playerIndex !in 0 until current.config.playerCount) return
        val totalVotesCast = current.votes.sumOf { it.votes }
        if (totalVotesCast >= current.config.playerCount) return

        val updatedVotes = current.votes.map { vote ->
            if (vote.playerIndex == playerIndex) vote.copy(votes = vote.votes + 1) else vote
        }
        _session.value = current.copy(votes = updatedVotes)
    }

    private fun revealResult() {
        val current = _session.value
        if (current.state !is GameState.Voting) return
        val caught = current.winningPlayerIndex == current.imposterIndex
        _session.value = current.copy(
            state = GameState.Result(imposterCaught = caught),
        )
    }

    private fun playAgain() {
        discussionTimerJob?.cancel()
        val preservedConfig = _session.value.config
        _session.value = GameSession(config = preservedConfig)
    }

    private fun persistStartPreferences(config: GameConfig) {
        viewModelScope.launch {
            prefsStore.save(
                GamePrefs(
                    playerCount = config.playerCount,
                    playerNames = config.playerNames,
                    lastCategory = config.category,
                    lastDifficulty = config.difficulty,
                    imposterHintEnabled = config.imposterHintEnabled,
                )
            )
        }
    }

    fun currentPlayerRole(): PlayerRole {
        val current = _session.value
        val roleState = current.state as? GameState.RoleReveal ?: return PlayerRole.Unknown
        return if (roleState.playerIndex == current.imposterIndex) {
            PlayerRole.Imposter(
                hint = current.currentWord.hint.ifBlank { "???" },
                hintEnabled = current.config.imposterHintEnabled,
            )
        } else {
            PlayerRole.Crew(word = current.currentWord.real)
        }
    }

    private fun normalizeCategory(category: String): String {
        val normalized = category.trim().uppercase()
        return if (normalized == WordDictionary.CATEGORY_RANDOM || normalized in WordDictionary.words.keys) {
            normalized
        } else {
            WordDictionary.CATEGORY_RANDOM
        }
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
