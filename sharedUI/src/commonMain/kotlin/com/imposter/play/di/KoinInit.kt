package com.imposter.play.di

import org.koin.core.context.startKoin
import org.koin.mp.KoinPlatform

fun initKoin(prefsPath: String) {
    if (KoinPlatform.getKoinOrNull() != null) return
    startKoin {
        modules(appModule(prefsPath = prefsPath))
    }
}
