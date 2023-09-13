package com.example.funfactoftheday.database

import androidx.room.*
import com.example.funfactoftheday.database.models.CategoriesWithFacts
import com.example.funfactoftheday.database.models.CategoryModel
import com.example.funfactoftheday.database.models.FactModel
import com.example.funfactoftheday.database.models.FactsWithCategories
import com.example.funfactoftheday.database.reletions.CategoryModelCrossRef

@Dao
interface AppDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFact(fact: FactModel)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategory(category: CategoryModel)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategoryModelCrossRef(crossRef: CategoryModelCrossRef)

    @Transaction
    @Query("SELECT * FROM category_table WHERE categoryName = :categoryName")
    suspend fun getFactsOfCategories(categoryName: String): List<CategoriesWithFacts>

    @Transaction
    @Query("SELECT * FROM fact_table WHERE factName = :factName")
    suspend fun getCategoriesOfFacts(factName: String): List<FactsWithCategories>
}