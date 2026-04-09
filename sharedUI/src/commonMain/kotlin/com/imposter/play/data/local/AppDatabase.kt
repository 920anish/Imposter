package com.imposter.play.data.local

import androidx.room3.ConstructedBy
import androidx.room3.Database
import androidx.room3.RoomDatabase
import androidx.room3.RoomDatabaseConstructor
import com.imposter.play.data.entities.CategoryEntity
import com.imposter.play.data.entities.PlayedHistoryEntity
import com.imposter.play.data.entities.PlayerEntity
import com.imposter.play.data.entities.WordEntity

@Database(
    entities = [
        CategoryEntity::class,
        WordEntity::class,
        PlayerEntity::class,
        PlayedHistoryEntity::class,
    ],
    version = 1,
    exportSchema = true,
)
@ConstructedBy(AppDatabaseConstructor::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun categoryDao(): CategoryDao
    abstract fun wordDao(): WordDao
    abstract fun playerDao(): PlayerDao
    abstract fun playedHistoryDao(): PlayedHistoryDao

    companion object {
        const val DATABASE_NAME = "imposter.db"
    }
}

// Room KSP generates the actual implementation for KMP
expect object AppDatabaseConstructor : RoomDatabaseConstructor<AppDatabase> {
    override fun initialize(): AppDatabase
}
