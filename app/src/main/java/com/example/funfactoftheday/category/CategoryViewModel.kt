package com.example.funfactoftheday.category

import androidx.lifecycle.*
import com.example.funfactoftheday.database.AppRepository
import com.example.funfactoftheday.database.models.CategoriesWithFacts
import com.example.funfactoftheday.database.models.CategoryModel
import com.example.funfactoftheday.database.models.FactModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import timber.log.Timber

class CategoryViewModel(private val appRepository: AppRepository): ViewModel() {

    suspend fun getFactsOfCategories(categoryModel: CategoryModel):
            LiveData<CategoriesWithFacts> = viewModelScope.async {
        return@async appRepository.getFactsOfCategories(categoryModel)
    }.await()

    fun insertFact(factModel: FactModel) = viewModelScope.launch{
            appRepository.insertFact(factModel)
        }

    suspend fun toggleDeletable(){
        Timber.e("toggleDelete in ViewModel")
        appRepository.toggleDeletable()
    }

    suspend fun returnDeletable(): Boolean = viewModelScope.async {
        return@async appRepository.returnDeletable()
    }.await()

    suspend fun deleteFact(factName: String){
        appRepository.deleteFact(factName)
    }

//    fun searchFactDatabase(searchQuery:String): LiveData<List<FactModel>>{
//        return appRepository.searchFactDatabase(searchQuery).asLiveData()
//    }
//
//    suspend fun searchFactOfSpecificCategory(searchQuery: String, listToSearch:List<FactModel>):
//            LiveData<List<FactModel>> = viewModelScope.async {
//                return@async appRepository.searchFactOfSpecificCategory(searchQuery, listToSearch).asLiveData()
//    }.await()

    class CategoryViewModelFactory(private val repository: AppRepository): ViewModelProvider.Factory{
        override fun <T: ViewModel> create(modelClass: Class<T>): T {
            if(modelClass.isAssignableFrom(CategoryViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return CategoryViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }

}