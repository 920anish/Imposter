package com.imposter.play.di

import org.koin.core.context.startKoin
import org.koin.mp.KoinPlatform

fun initKoin(prefsPath: String) {
    //android has application class + splashscreen api , for now this is just a guard for iOS
    if (KoinPlatform.getKoinOrNull() != null) return
    startKoin {
        modules(appModule(prefsPath = prefsPath))
    }
}
