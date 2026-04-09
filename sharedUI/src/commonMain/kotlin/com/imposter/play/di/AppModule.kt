package com.imposter.play.di

import com.imposter.play.data.local.AppDatabase
import com.imposter.play.data.local.AppPreferences
import com.imposter.play.data.repository.CategoryRepository
import com.imposter.play.data.repository.PlayerRepository
import com.imposter.play.data.repository.WordRepository
import com.imposter.play.engine.GameViewModel
import com.imposter.play.ui.screens.CustomizeViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import org.koin.dsl.module

val appModule = module {
    single { Dispatchers.IO }

    // Shared preferences (DataStore)
    single { AppPreferences(get()) }

    // Room DAOs
    single { get<AppDatabase>().categoryDao() }
    single { get<AppDatabase>().wordDao() }
    single { get<AppDatabase>().playerDao() }
    single { get<AppDatabase>().playedHistoryDao() }

    // Repositories
    single { WordRepository(get(), get() , get()) }
    single { CategoryRepository(get(), get()) }
    single { PlayerRepository(get(), get()) }

    // ViewModels
    factory { GameViewModel(get(), get()) }
    factory { CustomizeViewModel(get(), get()) }



}
