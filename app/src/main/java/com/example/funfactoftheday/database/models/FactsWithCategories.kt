package com.example.funfactoftheday.database.models

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.example.funfactoftheday.database.reletions.CategoryModelCrossRef

data class FactsWithCategories (
    @Embedded val fact: FactModel,
    @Relation(
        parentColumn = "factName",
        entityColumn = "categoryName",
        associateBy = Junction(CategoryModelCrossRef::class)
    )
    val categories:List<CategoryModel>
)