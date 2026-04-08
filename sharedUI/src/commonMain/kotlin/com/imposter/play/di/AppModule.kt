package com.imposter.play.di

import com.imposter.play.data.local.AppPreferences
import com.imposter.play.data.GamePrefsStore
import com.imposter.play.engine.GameViewModel
import org.koin.dsl.module

val appModule = module {
    // Shared Logic
    single { AppPreferences(get()) }

    // In the future, we add Room here:
    // single { get<DatabaseBuilder>().build() }
}

