package com.imposter.play.engine

data class GameConfig(
    val playerCount: Int = 4,
    val playerNames: List<String> = emptyList(),
    val category: String = "RANDOM",
    val difficulty: Int = 1,
    val imposterHintEnabled: Boolean = false,
)
