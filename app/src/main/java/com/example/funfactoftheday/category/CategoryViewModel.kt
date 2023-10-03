package com.example.funfactoftheday.category

import androidx.lifecycle.*
import com.example.funfactoftheday.database.AppRepository
import com.example.funfactoftheday.database.models.CategoriesWithFacts
import com.example.funfactoftheday.database.models.CategoryModel
import kotlinx.coroutines.launch

class CategoryViewModel(private val appRepository: AppRepository): ViewModel() {

    fun getFactsOfCategories(categoryModel: CategoryModel): LiveData<List<CategoriesWithFacts>> {
        val result = MutableLiveData<List<CategoriesWithFacts>>()
        viewModelScope.launch {
            result.value = appRepository.getFactsOfCategories(categoryModel).value
        }
        return result
    }

    class CategoryViewModel(private val repository: AppRepository): ViewModelProvider.Factory{
        override fun <T: ViewModel> create(modelClass: Class<T>): T {
            if(modelClass.isAssignableFrom(CategoryViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return CategoryViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }

}