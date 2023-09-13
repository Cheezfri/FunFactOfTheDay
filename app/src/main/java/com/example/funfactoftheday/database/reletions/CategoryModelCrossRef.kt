package com.example.funfactoftheday.database.reletions

import androidx.room.Entity

@Entity(primaryKeys = ["factName", "categoryName"])
data class CategoryModelCrossRef (
    val factName: String,
    val categoryName: String
)