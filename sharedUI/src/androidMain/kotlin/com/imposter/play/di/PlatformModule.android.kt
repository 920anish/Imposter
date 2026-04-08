package com.imposter.play.di

import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.room3.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.imposter.play.data.local.AppDatabase
import okio.Path.Companion.toPath
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

actual fun platformModule() = module {
    single {
        PreferenceDataStoreFactory.createWithPath(
            produceFile = {
                androidContext().filesDir.resolve("imposter.preferences_pb").absolutePath.toPath()
            }
        )
    }

    single<AppDatabase> {
        val context = androidContext()
        val dbFile = context.getDatabasePath(AppDatabase.DATABASE_NAME)
        Room.databaseBuilder<AppDatabase>(
            context = context,
            name = dbFile.absolutePath,
        )
            .setDriver(BundledSQLiteDriver())
            .createFromAsset("files/initial_words.db")
            .build()
    }
}