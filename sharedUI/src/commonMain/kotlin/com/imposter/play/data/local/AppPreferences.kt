package com.imposter.play.data.local

import androidx.datastore.core.DataStore
import androidx.datastore.core.IOException
import androidx.datastore.preferences.core.*
import com.imposter.play.data.Difficulty
import com.imposter.play.data.GameSettings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

const val CATEGORY_ALL = "all"

class AppPreferences(private val dataStore: DataStore<Preferences>) {

    private object Keys {
        val PLAYER_COUNT = intPreferencesKey("player_count")
        val DIFFICULTY_LEVEL = intPreferencesKey("difficulty_level")
        val IS_TIMER_ENABLED = booleanPreferencesKey("is_timer_enabled")
        val SELECTED_CATEGORY_IDS = stringSetPreferencesKey("selected_category_ids")
        val HINTS_ENABLED = booleanPreferencesKey("hints_enabled")
    }

    // Default is "all" so new users start with the full Random experience.
    private val defaultCategories = setOf(CATEGORY_ALL)

    val settings: Flow<GameSettings> = dataStore.data
        .catch { exception ->
            if (exception is IOException) emit(emptyPreferences()) else throw exception
        }
        .map { prefs ->
            val selected = prefs[Keys.SELECTED_CATEGORY_IDS] ?: defaultCategories

            GameSettings(
                playerCount = prefs[Keys.PLAYER_COUNT] ?: 3,
                difficulty = Difficulty.fromInt(prefs[Keys.DIFFICULTY_LEVEL] ?: 1),
                isTimerEnabled = prefs[Keys.IS_TIMER_ENABLED] ?: true,
                selectedCategoryIds = selected,
                isHintEnabled = prefs[Keys.HINTS_ENABLED] ?: false
            )
        }

    suspend fun setPlayerCount(count: Int) {
        dataStore.edit { it[Keys.PLAYER_COUNT] = count.coerceIn(3, 10) }
    }

    suspend fun setDifficulty(difficulty: Difficulty) {
        dataStore.edit { it[Keys.DIFFICULTY_LEVEL] = difficulty.level }
    }

    suspend fun setTimerEnabled(enabled: Boolean) {
        dataStore.edit { it[Keys.IS_TIMER_ENABLED] = enabled }
    }

    suspend fun setSelectedCategories(categoryIds: Set<String>) {
        dataStore.edit { prefs ->
            prefs[Keys.SELECTED_CATEGORY_IDS] = categoryIds.ifEmpty {
                setOf(CATEGORY_ALL)
            }
        }
    }

    suspend fun setHintsEnabled(enabled: Boolean) {
        dataStore.edit { it[Keys.HINTS_ENABLED] = enabled }
    }
}