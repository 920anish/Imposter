package com.imposter.play.engine

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.imposter.play.data.Difficulty
import com.imposter.play.data.Word
import com.imposter.play.data.local.AppPreferences
import com.imposter.play.data.repository.PlayerRepository
import com.imposter.play.data.repository.WordRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlin.random.Random

class GameViewModel(
    private val appPreferences: AppPreferences,
    private val wordRepository: WordRepository,
    private val playerRepository: PlayerRepository,
) : ViewModel() {

    private val _session = MutableStateFlow(GameSession())
    val session: StateFlow<GameSession> = _session.asStateFlow()

    private var discussionTimerJob: Job? = null

    fun onIntent(intent: GameIntent) {
        when (intent) {
            is GameIntent.UpdateSetupConfig -> updateSetupConfig(intent.config)
            GameIntent.IncreasePlayerCount -> increasePlayerCount()
            GameIntent.DecreasePlayerCount -> decreasePlayerCount()
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
            playerRepository.ensureDefaultPlayers()
            val activeCount = playerRepository.getActiveCount().coerceIn(3, 10)
            val settings = appPreferences.settings.first()
            _session.value = _session.value.copy(
                config = GameConfig(
                    playerCount = activeCount,
                    difficulty = settings.difficulty.level,
                    imposterHintEnabled = settings.isHintEnabled,
                    isTimerEnabled = settings.isTimerEnabled,
                )
            )
            appPreferences.setPlayerCount(activeCount)
        }
    }

    private fun updateSetupConfig(config: GameConfig) {
        val normalized = config.copy(
            playerCount = config.playerCount.coerceIn(3, 10),
            difficulty = config.difficulty.coerceIn(0, 2),
        )
        _session.value = _session.value.copy(
            config = normalized
        )
        viewModelScope.launch {
            appPreferences.setPlayerCount(normalized.playerCount)
            appPreferences.setDifficulty(Difficulty.fromInt(normalized.difficulty))
            appPreferences.setHintsEnabled(normalized.imposterHintEnabled)
            appPreferences.setTimerEnabled(normalized.isTimerEnabled)
        }
    }

    private fun increasePlayerCount() {
        viewModelScope.launch {
            val activePlayers = playerRepository.getActivePlayers()
            if (activePlayers.size >= 10) return@launch

            val allPlayers = playerRepository.getAllPlayers()
            val inactive = allPlayers.firstOrNull { !it.isActive }
            if (inactive != null) {
                playerRepository.setPlayerActive(inactive.id, true)
            } else {
                playerRepository.addPlayer("Player ${allPlayers.size + 1}")
            }
            val updatedCount = playerRepository.getActiveCount().coerceIn(3, 10)
            _session.value = _session.value.copy(config = _session.value.config.copy(playerCount = updatedCount))
            appPreferences.setPlayerCount(updatedCount)
        }
    }

    private fun decreasePlayerCount() {
        viewModelScope.launch {
            val activePlayers = playerRepository.getActivePlayers()
            if (activePlayers.size <= 3) return@launch

            val last = activePlayers.lastOrNull() ?: return@launch
            playerRepository.setPlayerActive(last.id, false)
            val updatedCount = playerRepository.getActiveCount().coerceIn(3, 10)
            _session.value = _session.value.copy(config = _session.value.config.copy(playerCount = updatedCount))
            appPreferences.setPlayerCount(updatedCount)
        }
    }

    private fun startGame(config: GameConfig) {
        val normalizedConfig = config.copy(
            playerCount = config.playerCount.coerceIn(3, 10),
            difficulty = config.difficulty.coerceIn(0, 2),
        )

        viewModelScope.launch {
            val settings = appPreferences.settings.first()
            val activePlayers = playerRepository.getActivePlayers()
            val playerNames = activePlayers.map { it.name }

            val playerCount = playerNames.size.coerceIn(3, 10)
            if (playerCount < 3) return@launch

            // Get random word from repository (handles exclusion + marking as played)
            val selectedWord = wordRepository.getRandomWord(
                selectedCategoryIds = settings.selectedCategoryIds,
                difficulty = normalizedConfig.difficulty,
            ) ?: Word(real = "No words available", hint = "Check database")

            val imposterIndex = Random.nextInt(until = playerCount)
            discussionTimerJob?.cancel()

            _session.value = GameSession(
                config = normalizedConfig.copy(playerCount = playerCount, isTimerEnabled = settings.isTimerEnabled),
                state = GameState.RoleReveal(playerIndex = 0, isRevealed = false),
                imposterIndex = imposterIndex,
                currentWord = selectedWord,
                votes = (0 until playerCount).map { VoteCount(it, 0) },
                revealedPlayers = emptySet(),
                playerNames = playerNames,
            )

            persistStartPreferences(normalizedConfig)
        }
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
                state = GameState.Discussion(
                    secondsLeft = 180,
                    isRunning = current.config.isTimerEnabled,
                ),
            )
            if (current.config.isTimerEnabled) {
                startDiscussionCountdown()
            }
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
            appPreferences.setPlayerCount(config.playerCount)
            appPreferences.setDifficulty(Difficulty.fromInt(config.difficulty))
            appPreferences.setHintsEnabled(config.imposterHintEnabled)
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
}
