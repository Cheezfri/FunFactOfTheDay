package com.example.funfactoftheday.database

import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import com.example.funfactoftheday.database.models.CategoriesWithFacts
import com.example.funfactoftheday.database.models.CategoryModel
import com.example.funfactoftheday.database.models.FactModel
import kotlinx.coroutines.flow.Flow

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
    suspend fun getFactsOfCategories(categoryModel: CategoryModel): LiveData<List<CategoriesWithFacts>>{
        return appDao.getFactsOfCategories(categoryModel.categoryName).asLiveData()
    }

}