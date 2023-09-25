package com.example.funfactoftheday.database.reletions

import androidx.room.Entity

@Entity(primaryKeys = ["factName", "categoryName"], tableName = "CategoryModelCrossRef_table")
data class CategoryModelCrossRef (
    val factName: String,
    val categoryName: String
)