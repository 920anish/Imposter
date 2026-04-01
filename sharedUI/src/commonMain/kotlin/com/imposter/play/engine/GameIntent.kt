package com.imposter.play.engine

sealed class GameIntent {
    data class StartGame(val config: GameConfig) : GameIntent()
    data object RevealCard : GameIntent()
    data object NextPlayer : GameIntent()
    data object ToggleTimer : GameIntent()
    data object StartVoting : GameIntent()
    data class CastVote(val playerIndex: Int) : GameIntent()
    data object RevealResult : GameIntent()
    data object PlayAgain : GameIntent()
}

