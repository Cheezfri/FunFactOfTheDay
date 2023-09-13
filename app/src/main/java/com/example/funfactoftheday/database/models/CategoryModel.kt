package com.example.funfactoftheday.database.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "category_table")
data class CategoryModel (
    @PrimaryKey(autoGenerate = false)
    val categoryName: String
)