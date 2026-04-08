package com.imposter.play.androidApp

import android.app.Application
import com.imposter.play.di.initKoin

class ImposterApp : Application() {
    override fun onCreate() {
        super.onCreate()
        initKoin()
    }
}