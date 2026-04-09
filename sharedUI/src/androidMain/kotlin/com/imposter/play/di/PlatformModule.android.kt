package com.imposter.play.di

import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.room3.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.imposter.play.data.local.AppDatabase
import kotlinx.coroutines.runBlocking
import okio.Path.Companion.toPath
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import java.io.File
import imposter.sharedui.generated.resources.Res

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

        // Use a temporary file in cache to bridge CMP Resources to Room
        val tempFile = File(context.cacheDir, "temp_initial_words.db")

        if (!dbFile.exists()) {
            runBlocking {
                try {
                    // This pulls from commonMain/composeResources/files/initial_words.db
                    val bytes = Res.readBytes("files/initial_words.db")
                    tempFile.writeBytes(bytes)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }

        Room.databaseBuilder<AppDatabase>(
            context = context,
            name = dbFile.absolutePath,
        )
            .setDriver(BundledSQLiteDriver())
            // Point to the physical file we just extracted
            .createFromFile(tempFile)
            .build()
    }
}