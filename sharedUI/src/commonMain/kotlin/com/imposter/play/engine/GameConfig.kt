package com.imposter.play.engine

data class GameConfig(
    val playerCount: Int = 3,
    val difficulty: Int = 1,
    val imposterHintEnabled: Boolean = false,
    val isTimerEnabled: Boolean = true,
)
