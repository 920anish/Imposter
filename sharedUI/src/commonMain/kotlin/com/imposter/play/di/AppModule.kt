package com.imposter.play.di

import com.imposter.play.data.local.AppDatabase
import com.imposter.play.data.local.AppPreferences
import org.koin.dsl.module

val appModule = module {
    // Shared preferences (DataStore)
    single { AppPreferences(get()) }

    // Room DAOs
    single { get<AppDatabase>().categoryDao() }
    single { get<AppDatabase>().wordDao() }
    single { get<AppDatabase>().playerDao() }
    single { get<AppDatabase>().playedHistoryDao() }
}

