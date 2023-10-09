package com.example.funfactoftheday.homepage

import androidx.annotation.Keep
import androidx.lifecycle.*
import com.example.funfactoftheday.database.AppRepository
import com.example.funfactoftheday.database.models.CategoryModel
import com.example.funfactoftheday.database.models.FactModel
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.Timer

@Keep
class HomePageViewModel (private val appRepository: AppRepository): ViewModel() {

    val allFacts:LiveData<List<FactModel>> = appRepository.allFacts.asLiveData()

    fun insertFact(factModel: FactModel) = viewModelScope.launch {
        appRepository.insertFact(factModel)
        Timber.e("Inserted ${factModel.factName}")
    }

    fun insertCategory(categoryModel: CategoryModel) = viewModelScope.launch{
        appRepository.insertCategory(categoryModel)
        Timber.e("Inserted ${categoryModel.categoryName}")
    }

    fun insertCategoryModelCrossRef(factName: String, categoryName: String) = viewModelScope.launch {
        appRepository.insertCategoryModelCrossRef(factName, categoryName)
    }

    suspend fun doesFactExist(factName: String): Boolean = viewModelScope.async {
        return@async appRepository.doesFactExist(factName)
    }.await()

    suspend fun doesCategoryExist(categoryName: String): Boolean = viewModelScope.async {
        return@async appRepository.doesCategoryExist(categoryName)
    }.await()

    class HomePageViewModelFactory(private val repository: AppRepository): ViewModelProvider.Factory{
        override fun <T: ViewModel> create(modelClass: Class<T>): T {
            if(modelClass.isAssignableFrom(HomePageViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return HomePageViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }

}