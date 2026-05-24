package com.imposter.play.androidApp

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.imposter.play.App
import com.imposter.play.engine.StartupReadiness

class AppActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {

        val splashScreen = installSplashScreen()
        
        try {
            splashScreen.setKeepOnScreenCondition { !StartupReadiness.isReady.value }
        } catch (_: UnsupportedOperationException) {
            // If the device doesn't support the SplashScreen API, we can ignore the exception and proceed without it.
            // This ensures that the app remains functional on older devices while still providing a splash screen experience on supported devices.
        }

        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(Color.TRANSPARENT)
        )
        setContent {
            App()
        }
    }
}
