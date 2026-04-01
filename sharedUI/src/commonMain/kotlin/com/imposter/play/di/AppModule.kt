package com.imposter.play.di

import com.imposter.play.data.GamePrefsStore
import com.imposter.play.engine.GameViewModel
import org.koin.dsl.module

fun appModule(prefsPath: String = "imposter_prefs.json") = module {
    single { GamePrefsStore(filePath = prefsPath) }
    single { GameViewModel(prefsStore = get()) }
}

