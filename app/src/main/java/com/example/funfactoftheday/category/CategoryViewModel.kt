package com.example.funfactoftheday.category

import androidx.lifecycle.*
import com.example.funfactoftheday.database.AppRepository
import com.example.funfactoftheday.database.models.CategoriesWithFacts
import com.example.funfactoftheday.database.models.CategoryModel
import com.example.funfactoftheday.database.models.FactModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class CategoryViewModel(private val appRepository: AppRepository): ViewModel() {

    suspend fun getFactsOfCategories(categoryModel: CategoryModel):
            LiveData<List<CategoriesWithFacts>> = viewModelScope.async {
        return@async appRepository.getFactsOfCategories(categoryModel)
    }.await()

    fun insertFact(factModel: FactModel) = viewModelScope.launch{
            appRepository.insertFact(factModel)
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