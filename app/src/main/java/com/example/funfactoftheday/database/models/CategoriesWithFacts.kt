package com.example.funfactoftheday.database.models

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.example.funfactoftheday.database.reletions.CategoryModelCrossRef

data class CategoriesWithFacts (
    @Embedded val category:CategoryModel,
    @Relation(
        parentColumn = "categoryName",
        entityColumn =  "factName",
        associateBy = Junction(CategoryModelCrossRef::class)
    )
    val facts:List<FactModel>
)