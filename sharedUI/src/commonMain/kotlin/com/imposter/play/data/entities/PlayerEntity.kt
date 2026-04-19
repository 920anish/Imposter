package com.imposter.play.data.entities


import androidx.room3.Entity
import androidx.room3.PrimaryKey
import androidx.room3.Index

@Entity(
    tableName = "players",
    indices = [
        Index(value = ["name"], unique = true),
        Index(value = ["isActive", "lobbyOrder"])
    ]
)
data class PlayerEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val name: String,

    val isActive: Boolean = true,

    val lobbyOrder: Int = 0,

    val totalWins: Int = 0,
    val totalGames: Int = 0
)
