package com.imposter.play.engine

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.imposter.play.data.Difficulty
import com.imposter.play.data.Word
import com.imposter.play.data.local.AppPreferences
import com.imposter.play.data.repository.PlayerRepository
import com.imposter.play.data.repository.WordCatalogUpdater
import com.imposter.play.data.repository.WordRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlin.random.Random

class GameViewModel(
    private val appPreferences: AppPreferences,
    private val wordRepository: WordRepository,
    private val playerRepository: PlayerRepository,
    private val wordCatalogUpdater: WordCatalogUpdater,
) : ViewModel() {

    private val _session = MutableStateFlow(GameSession())
    val session: StateFlow<GameSession> = _session.asStateFlow()

    private var discussionTimerJob: Job? = null
    private var activePlayersJob: Job? = null

    fun onIntent(intent: GameIntent) {
        when (intent) {
            is GameIntent.UpdateSetupConfig -> updateSetupConfig(intent.config)
            GameIntent.IncreasePlayerCount -> increasePlayerCount()
            GameIntent.DecreasePlayerCount -> decreasePlayerCount()
            is GameIntent.StartGame -> {
                viewModelScope.launch {
                    startGameInternal(intent.config)
                }
            }
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
            wordCatalogUpdater.applyPendingUpdates()
            playerRepository.ensureDefaultPlayers()
            val activeCount = playerRepository.getActiveCount().coerceIn(3, 10)
            val settings = appPreferences.settings.first()
            _session.value = _session.value.copy(
                config = _session.value.config.copy(
                    playerCount = activeCount,
                    difficulty = settings.difficulty.level,
                    imposterHintEnabled = settings.isHintEnabled,
                    isTimerEnabled = settings.isTimerEnabled,
                )
            )
            appPreferences.setPlayerCount(activeCount)
            StartupReadiness.isReady.value = true
            observeActivePlayers()
        }
    }

    private fun observeActivePlayers() {
        if (activePlayersJob != null) return
        activePlayersJob = viewModelScope.launch {
            playerRepository.getActivePlayersFlow().collectLatest { activePlayers ->
                val current = _session.value
                if (current.state !is GameState.Idle) return@collectLatest
                val activeCount = activePlayers.size.coerceIn(3, 10)
                if (current.config.playerCount == activeCount) return@collectLatest
                _session.value = current.copy(config = current.config.copy(playerCount = activeCount))
                appPreferences.setPlayerCount(activeCount)
            }
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
            val firstInactive = allPlayers.firstOrNull { !it.isActive }
            if (firstInactive != null) {
                playerRepository.setPlayerActive(firstInactive.id, true)
            } else {
                playerRepository.addPlayer("Player ${allPlayers.size + 1}".take(10))
            }
        }
    }

    private fun decreasePlayerCount() {
        viewModelScope.launch {
            val activePlayers = playerRepository.getActivePlayers()
            if (activePlayers.size <= 3) return@launch

            val last = activePlayers.lastOrNull() ?: return@launch
            playerRepository.setPlayerActive(last.id, false)
        }
    }

    suspend fun startGameAndAwait(config: GameConfig): Boolean = startGameInternal(config)

    private suspend fun startGameInternal(config: GameConfig): Boolean {
        val normalizedConfig = config.copy(
            playerCount = config.playerCount.coerceIn(3, 10),
            difficulty = config.difficulty.coerceIn(0, 2),
        )

        val settings = appPreferences.settings.first()
        val activePlayers = playerRepository.getActivePlayers()
        val playerNames = activePlayers.map { it.name }
        val playerIds = activePlayers.map { it.id }
        val playerCount = playerNames.size.coerceIn(3, 10)
        if (playerCount < 3) return false

        val selectedWord = wordRepository.getRandomWord(
            selectedCategoryIds = settings.selectedCategoryIds,
            difficulty = normalizedConfig.difficulty,
        ) ?: return false

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
            playerIds = playerIds,
        )

        persistStartPreferences(normalizedConfig)
        return true
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
        val winner = current.winningPlayerIndex
        val caught = winner != null && winner == current.imposterIndex
        _session.value = current.copy(
            state = GameState.Result(imposterCaught = caught),
        )
        val imposterPlayerId = current.playerIds.getOrNull(current.imposterIndex) ?: return
        val allPlayerIds = current.playerIds
        viewModelScope.launch {
            playerRepository.recordGameResults(
                imposterWon = !caught,
                imposterPlayerId = imposterPlayerId,
                allPlayerIds = allPlayerIds,
            )
        }
    }

    private fun playAgain() {
        discussionTimerJob?.cancel()
        val preservedConfig = _session.value.config
        _session.value = GameSession(
            config = preservedConfig,
            playerNames = _session.value.playerNames,
            playerIds = _session.value.playerIds,
        )
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
            val hint = current.currentWord.hint.trim()
            val hintEnabled = current.config.imposterHintEnabled && hint.isNotEmpty()
            PlayerRole.Imposter(
                hint = hint,
                hintEnabled = hintEnabled,
            )
        } else {
            PlayerRole.Crew(word = current.currentWord.real)
        }
    }
}
