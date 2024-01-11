package com.example.funfactoftheday.database.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "fact_table")
data class FactModel (
    @PrimaryKey(autoGenerate = false)
    val factName: String,
    val isFavorite: Boolean = false,
    var isDeletable: Boolean = false
)