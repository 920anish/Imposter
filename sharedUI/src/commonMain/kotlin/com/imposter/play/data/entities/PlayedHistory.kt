package com.imposter.play.data.entities



import androidx.room3.Entity
import androidx.room3.PrimaryKey
import androidx.room3.ForeignKey
import androidx.room3.Index


@Entity(
    tableName = "played_history",
    foreignKeys = [
        ForeignKey(
            entity = WordEntity::class,
            parentColumns = ["id"],
            childColumns = ["wordId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["wordId"]),
        Index(value = ["timestamp"])
    ]
)
data class PlayedHistoryEntity(
    @PrimaryKey
    val wordId: Long,
    val timestamp: Long
)