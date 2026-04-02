package com.imposter.play.data

import kotlinx.serialization.Serializable

@Serializable
data class GamePrefs(
    val playerCount: Int = 4,
    val playerNames: List<String> = emptyList(),
    val lastCategory: String = "RANDOM",
    val lastDifficulty: Int = 1,
    val imposterHintEnabled: Boolean = false,
)
