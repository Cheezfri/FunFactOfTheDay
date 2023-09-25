package com.example.funfactoftheday.database

import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import com.example.funfactoftheday.database.models.FactModel
import kotlinx.coroutines.flow.Flow

class AppRepository(private val appDao:AppDao) {

    val allFacts: Flow<List<FactModel>> = appDao.getAllFacts()

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insert(factModel: FactModel){
        appDao.insertFact(factModel)
    }

//    @WorkerThread
//    suspend fun getCategoriesOfFacts(factModel: FactModel){
//        appDao.getCategoriesOfFacts()
//    }

}