package com.example.funfactoftheday.database

import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import com.example.funfactoftheday.database.models.CategoriesWithFacts
import com.example.funfactoftheday.database.models.CategoryModel
import com.example.funfactoftheday.database.models.FactModel
import com.example.funfactoftheday.database.reletions.CategoryModelCrossRef
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class AppRepository(private val appDao:AppDao) {

    val allFacts: Flow<List<FactModel>> = appDao.getAllFacts()
    val favoriteFacts: Flow<List<FactModel>> = appDao.getFavoriteFacts()
    val allCategories: Flow<List<CategoryModel>> = appDao.getAllCategories()

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insertFact(factModel: FactModel){
        appDao.insertFact(factModel)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insertCategory(categoryModel: CategoryModel){
        appDao.insertCategory(categoryModel)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insertCategoryModelCrossRef(factName: String, categoryName: String){
        appDao.insertCategoryModelCrossRef(CategoryModelCrossRef(factName, categoryName))
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun doesCategoryExist(categoryName: String): Boolean{
        return withContext(Dispatchers.IO){
            appDao.doesCategoryExist(categoryName)
        }
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun doesFactExist(factName: String): Boolean{
        return withContext(Dispatchers.IO){
            appDao.doesFactExist(factName)
        }
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun getFactsOfCategories(categoryModel: CategoryModel): LiveData<CategoriesWithFacts>{
        return withContext(Dispatchers.IO){
            appDao.getFactsOfCategories(categoryModel.categoryName).asLiveData()
        }
    }

    fun searchFactDatabase(searchQuery: String): Flow<List<FactModel>>{
        return appDao.searchFactDatabase(searchQuery)
    }

    fun searchCategoryDatabase(searchQuery: String): Flow<List<CategoryModel>>{
        return appDao.searchCategoryDatabase(searchQuery)
    }

//    fun searchFactOfSpecificCategory(searchQuery: String, listToSearch:List<FactModel>): Flow<List<FactModel>>{
//        return appDao.searchFactOfSpecificCategory(searchQuery, listToSearch)
//    }

}