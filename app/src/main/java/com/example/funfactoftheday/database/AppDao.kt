package com.example.funfactoftheday.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.funfactoftheday.database.models.CategoriesWithFacts
import com.example.funfactoftheday.database.models.CategoryModel
import com.example.funfactoftheday.database.models.FactModel
import com.example.funfactoftheday.database.models.FactsWithCategories
import com.example.funfactoftheday.database.reletions.CategoryModelCrossRef
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.toList

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
    @Query("SELECT * FROM fact_table WHERE isFavorite = true")
    fun getFavoriteFacts():Flow<List<FactModel>>

    @Transaction
    @Query("SELECT * FROM fact_table WHERE factName LIKE :searchQuery AND isFavorite = true")
    fun searchFavoriteFacts(searchQuery: String):Flow<List<FactModel>>

    @Transaction
    @Query("SELECT * FROM category_table WHERE categoryName = :categoryName")
    fun getFactsOfCategories(categoryName: String): Flow<CategoriesWithFacts>

    @Transaction
    @Query("SELECT * FROM fact_table WHERE factName = :factName")
    fun getCategoriesOfFacts(factName: String): Flow<FactsWithCategories>

    @Transaction
    @Query("SELECT EXISTS(SELECT * FROM category_table WHERE categoryName = :categoryName)")
    fun doesCategoryExist(categoryName: String):Boolean

    @Transaction
    @Query("SELECT EXISTS(SELECT * FROM fact_table WHERE factName = :factName)")
    fun doesFactExist(factName: String):Boolean

    @Query("SELECT * FROM fact_table WHERE factName LIKE :searchQuery")
    fun searchFactDatabase(searchQuery: String): Flow<List<FactModel>>

    @Query("SELECT * FROM category_table where categoryName Like :searchQuery")
    fun searchCategoryDatabase(searchQuery: String): Flow<List<CategoryModel>>

//    @Query("SELECT * FROM :listToSearch WHERE factName LIKE :searchQuery")
//    fun searchFactOfSpecificCategory(searchQuery: String, listToSearch:List<FactModel>): Flow<List<FactModel>>

//    @Transaction
//    suspend fun searchFactOfSpecificCategory(searchQuery: String, categoryName: String): Flow<List<FactModel>>{
//        val listToSearch = getFactsOfCategories(categoryName).toList()[0][0].facts
//        return customSearch(searchQuery, listToSearch)
//    }

}