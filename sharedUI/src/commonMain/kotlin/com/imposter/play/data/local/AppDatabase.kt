@file:Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")

package com.imposter.play.data.local

import androidx.room3.ConstructedBy
import androidx.room3.Database
import androidx.room3.AutoMigration
import androidx.room3.DeleteColumn
import androidx.room3.RoomDatabase
import androidx.room3.RoomDatabaseConstructor
import androidx.room3.migration.AutoMigrationSpec
import com.imposter.play.data.entities.CategoryEntity
import com.imposter.play.data.entities.PlayedHistoryEntity
import com.imposter.play.data.entities.PlayerEntity
import com.imposter.play.data.entities.WordEntity
import com.imposter.play.data.local.dao.CategoryDao
import com.imposter.play.data.local.dao.PlayedHistoryDao
import com.imposter.play.data.local.dao.PlayerDao
import com.imposter.play.data.local.dao.WordDao

@Database(
    entities = [
        CategoryEntity::class,
        WordEntity::class,
        PlayerEntity::class,
        PlayedHistoryEntity::class,
    ],
    version = 3,
    exportSchema = true,
    autoMigrations = [
        AutoMigration(from = 1, to = 2),
        AutoMigration(from = 2, to = 3, spec = AppDatabaseMigration2To3::class),
    ],
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

@DeleteColumn(tableName = "categories", columnName = "isEnabled")
@DeleteColumn(tableName = "players", columnName = "avatarRes")
class AppDatabaseMigration2To3 : AutoMigrationSpec

// Room KSP generates the actual implementation for KMP
expect object AppDatabaseConstructor : RoomDatabaseConstructor<AppDatabase> {
    override fun initialize(): AppDatabase
}
