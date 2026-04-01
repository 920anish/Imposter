package com.imposter.play.androidApp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.imposter.play.App
import com.imposter.play.di.initKoin

class AppActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initKoin(prefsPath = "${filesDir.absolutePath}/imposter_prefs.json")
        enableEdgeToEdge()
        setContent { 
            App()
        }
    }
}
