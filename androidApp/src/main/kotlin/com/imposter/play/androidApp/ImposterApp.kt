package com.imposter.play.androidApp

import android.app.Application
import com.imposter.play.di.initKoin
import org.koin.android.ext.koin.androidContext


class ImposterApp : Application() {
    override fun onCreate() {
        super.onCreate()

        initKoin {
            androidContext(this@ImposterApp)
        }


    }
}