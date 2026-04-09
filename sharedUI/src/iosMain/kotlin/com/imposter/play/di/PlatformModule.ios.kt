package com.imposter.play.di

import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.room3.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.imposter.play.data.local.AppDatabase
import imposter.sharedui.generated.resources.Res
import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.usePinned
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.runBlocking
import okio.Path.Companion.toPath
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.koin.dsl.module
import platform.Foundation.NSData
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSUserDomainMask
import platform.Foundation.create
import platform.Foundation.writeToFile


@OptIn(ExperimentalForeignApi::class, ExperimentalResourceApi::class, BetaInteropApi::class)
actual fun platformModule() = module {
    single {
        PreferenceDataStoreFactory.createWithPath(
            produceFile = {
                val path = NSFileManager.defaultManager.URLForDirectory(
                    directory = NSDocumentDirectory,
                    inDomain = NSUserDomainMask,
                    appropriateForURL = null,
                    create = false,
                    error = null
                )?.path ?: ""
                "$path/imposter.preferences_pb".toPath()
            }
        )
    }

    single<AppDatabase> {
        val ioDispatcher: CoroutineDispatcher = get()
        val documentsPath = NSFileManager.defaultManager.URLForDirectory(
            directory = NSDocumentDirectory,
            inDomain = NSUserDomainMask,
            appropriateForURL = null,
            create = true,
            error = null
        )?.path ?: ""
        val dbPath = "$documentsPath/${AppDatabase.DATABASE_NAME}"

        // Copy prepopulated DB from bundle on first launch
        if (!NSFileManager.defaultManager.fileExistsAtPath(dbPath)) {
            runBlocking {
                val bytes = Res.readBytes("files/initial_words.db")
                bytes.usePinned { pinned ->
                    val nsData = NSData.create(
                        bytes = pinned.addressOf(0),
                        length = bytes.size.toULong()
                    )
                    nsData.writeToFile(dbPath, true)
                }
            }
        }

        Room.databaseBuilder<AppDatabase>(
            name = dbPath,
        )
            .setDriver(BundledSQLiteDriver())
            .setQueryCoroutineContext(ioDispatcher)
            .build()
    }
}