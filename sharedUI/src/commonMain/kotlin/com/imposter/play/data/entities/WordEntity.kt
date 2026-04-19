package com.imposter.play.data.entities


import androidx.room3.Entity
import androidx.room3.PrimaryKey
import androidx.room3.ForeignKey
import androidx.room3.Index
import androidx.room3.ColumnInfo

@Entity(
    tableName = "words",
    foreignKeys = [
        ForeignKey(
            entity = CategoryEntity::class,
            parentColumns = ["id"],
            childColumns = ["categoryId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["categoryId"]),
        Index(value = ["categoryId", "difficultyLevel"])
    ]
)
data class WordEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val text: String,

    val hint: String?,

    val categoryId: String,

    val difficultyLevel: Int,

    @ColumnInfo(defaultValue = "0")
    val isCustom: Boolean = false,
)
