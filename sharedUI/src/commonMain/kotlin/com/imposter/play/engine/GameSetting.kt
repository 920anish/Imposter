package com.imposter.play.engine


enum class Difficulty(val level: Int) {
    EASY(0),
    MEDIUM(1),
    HARD(2);

    companion object {
        fun fromInt(value: Int) = entries.find { it.level == value } ?: MEDIUM
    }
}

data class GameSettings(
    val playerCount: Int,
    val difficulty: Difficulty,
    val isTimerEnabled: Boolean,
    val isHintEnabled: Boolean,
    val selectedCategoryIds: Set<String>
)