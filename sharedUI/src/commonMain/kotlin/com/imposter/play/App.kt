package com.imposter.play

import androidx.compose.runtime.*
import com.imposter.play.di.appModule
import com.imposter.play.theme.AppTheme
import org.koin.mp.KoinPlatform

@Composable
fun App() = AppTheme {
    LaunchedEffect(Unit) {
        if (KoinPlatform.getKoinOrNull() == null) {
            KoinPlatform.startKoin(modules = listOf(appModule()), level = org.koin.core.logger.Level.NONE)
        }
    }
}
