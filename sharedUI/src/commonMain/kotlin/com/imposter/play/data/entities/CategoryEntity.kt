package com.imposter.play.data.entities


import androidx.room3.Entity
import androidx.room3.Index
import androidx.room3.PrimaryKey



@Entity(
    tableName = "categories",
    indices = [
        Index(value = ["displayOrder"])
    ]
)
data class CategoryEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val iconRes: String,
    val displayOrder: Int = 0,
    val isCustom: Boolean = false,
    val wordCount: Int = 0
)
