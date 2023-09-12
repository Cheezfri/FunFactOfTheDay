package com.example.funfactoftheday.database.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "fact_table")
data class FactModel (
    @PrimaryKey
    val factId: String,
    val factSentence: String,
    val isFavorite: Boolean = false,
)