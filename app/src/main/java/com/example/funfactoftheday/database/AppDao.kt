package com.example.funfactoftheday.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.funfactoftheday.database.models.CategoriesWithFacts
import com.example.funfactoftheday.database.models.CategoryModel
import com.example.funfactoftheday.database.models.FactModel
import com.example.funfactoftheday.database.models.FactsWithCategories
import com.example.funfactoftheday.database.reletions.CategoryModelCrossRef
import kotlinx.coroutines.flow.Flow

@Dao
interface AppDao {

    @Transaction
    suspend fun deleteAll(){
        deleteCategories()
        deleteFacts()
        deleteCategoryModelCrossRef()
    }

    @Query("DELETE FROM category_table")
    fun deleteCategories()

    @Query("DELETE FROM fact_table")
    fun deleteFacts()

    @Query("DELETE FROM CategoryModelCrossRef_table")
    fun deleteCategoryModelCrossRef()

    @Upsert
    suspend fun insertFact(fact: FactModel)

    @Upsert
    suspend fun insertCategory(category: CategoryModel)

    @Upsert
    suspend fun insertCategoryModelCrossRef(crossRef: CategoryModelCrossRef)

    @Transaction
    @Query("SELECT * FROM category_table")
    fun getAllCategories(): Flow<List<CategoryModel>>

    @Transaction
    @Query("SELECT * FROM fact_table")
    fun getAllFacts(): Flow<List<FactModel>>

    @Transaction
    @Query("SELECT * FROM category_table WHERE categoryName = :categoryName")
    fun getFactsOfCategories(categoryName: String): Flow<List<CategoriesWithFacts>>

    @Transaction
    @Query("SELECT * FROM fact_table WHERE factName = :factName")
    fun getCategoriesOfFacts(factName: String): Flow<List<FactsWithCategories>>
}