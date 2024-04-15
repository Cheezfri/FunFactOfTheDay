package com.example.funfactoftheday.notifications

import androidx.annotation.Keep
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import com.example.funfactoftheday.database.AppRepository
import com.example.funfactoftheday.database.models.CategoryModel
import com.example.funfactoftheday.database.models.FactModel

@Keep
class NotificationsViewModel(private val appRepository: AppRepository){

    val allFacts: LiveData<List<FactModel>> = appRepository.allFacts.asLiveData()

//    fun returnFavoriteCategories(): LiveData<List<CategoryModel>>{
//        return appRepository.returnFavoriteCategories().asLiveData()
//    }

    class NotificationsViewModelFactory(private val repository: AppRepository): ViewModelProvider.Factory{
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
           if(modelClass.isAssignableFrom(NotificationsViewModel::class.java)){
               @Suppress("UNCHECKED_CAST")
               return NotificationsViewModel(repository) as T
           }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }

}