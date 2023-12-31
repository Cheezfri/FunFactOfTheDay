package com.example.funfactoftheday.categories

import androidx.annotation.Keep
import androidx.lifecycle.*
import com.example.funfactoftheday.database.AppRepository
import com.example.funfactoftheday.database.models.CategoryModel
import com.example.funfactoftheday.database.models.FactModel
import kotlinx.coroutines.launch

@Keep
class CategoriesViewModel (private val appRepository: AppRepository): ViewModel(){

    val allCategories: LiveData<List<CategoryModel>> = appRepository.allCategories.asLiveData()

    fun insertCategory(categoryModel: CategoryModel) = viewModelScope.launch {
        appRepository.insertCategory(categoryModel)
    }

    fun searchCategoryDatabase(searchQuery:String): LiveData<List<CategoryModel>>{
        return appRepository.searchCategoryDatabase(searchQuery).asLiveData()
    }

//    fun insertCategoryModelCrossRef(factName: String, categoryName: String) = viewModelScope.launch {
//        appRepository.insertCategoryModelCrossRefAndFactAndCategory(factName, categoryName)
//    }

    class CategoriesViewModelFactory(private val repository: AppRepository): ViewModelProvider.Factory{
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if(modelClass.isAssignableFrom(CategoriesViewModel::class.java)){
                @Suppress("UNCHECKED_CAST")
                return CategoriesViewModel(repository) as T
            }
            throw IllegalArgumentException ("Unknown ViewModel class")
        }
    }

}