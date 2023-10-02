package com.example.funfactoftheday.categories

import androidx.annotation.Keep
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import com.example.funfactoftheday.database.AppRepository
import com.example.funfactoftheday.database.models.CategoryModel

@Keep
class CategoriesViewModel (private val appRepository: AppRepository): ViewModel(){

    val allCategories: LiveData<List<CategoryModel>> = appRepository.allCategories.asLiveData()

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