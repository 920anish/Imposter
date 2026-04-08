package com.imposter.play.data.local

import androidx.room3.Dao
import androidx.room3.Delete
import androidx.room3.Insert
import androidx.room3.OnConflictStrategy
import androidx.room3.Query
import androidx.room3.Update
import com.imposter.play.data.entities.CategoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDao {

    @Query("SELECT * FROM categories ORDER BY displayOrder ASC")
    fun getAllFlow(): Flow<List<CategoryEntity>>

    @Query("SELECT * FROM categories ORDER BY displayOrder ASC")
    suspend fun getAll(): List<CategoryEntity>

    @Query("SELECT * FROM categories WHERE isEnabled = 1 ORDER BY displayOrder ASC")
    suspend fun getEnabled(): List<CategoryEntity>

    @Query("SELECT * FROM categories WHERE id IN (:ids)")
    suspend fun getByIds(ids: Set<String>): List<CategoryEntity>

    @Query("SELECT * FROM categories WHERE id = :id")
    suspend fun getById(id: String): CategoryEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(category: CategoryEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(categories: List<CategoryEntity>)

    @Update
    suspend fun update(category: CategoryEntity)

    @Delete
    suspend fun delete(category: CategoryEntity)

    @Query("DELETE FROM categories WHERE id = :id AND isCustom = 1")
    suspend fun deleteCustomById(id: String)

    @Query("UPDATE categories SET wordCount = :count WHERE id = :categoryId")
    suspend fun updateWordCount(categoryId: String, count: Int)

    @Query("UPDATE categories SET isEnabled = :enabled WHERE id = :categoryId")
    suspend fun setEnabled(categoryId: String, enabled: Boolean)
}
