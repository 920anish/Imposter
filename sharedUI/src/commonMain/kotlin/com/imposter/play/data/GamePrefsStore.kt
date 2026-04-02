package com.imposter.play.data

import io.github.xxfast.kstore.KStore
import io.github.xxfast.kstore.file.storeOf
import kotlinx.coroutines.flow.Flow
import kotlinx.io.files.Path

class GamePrefsStore(
    private val store: KStore<GamePrefs>,
) {
    constructor(filePath: String) : this(
        store = storeOf(
            file = Path(filePath),
            default = GamePrefs(),
        )
    )

    val updates: Flow<GamePrefs?>
        get() = store.updates

    suspend fun load(): GamePrefs = store.get() ?: GamePrefs()

    suspend fun save(prefs: GamePrefs) {
        store.set(prefs)
    }
}
