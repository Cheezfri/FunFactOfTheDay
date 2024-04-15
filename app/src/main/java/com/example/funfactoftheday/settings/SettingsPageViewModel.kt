package com.example.funfactoftheday.settings

import androidx.annotation.Keep
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import com.example.funfactoftheday.database.AppRepository
import com.example.funfactoftheday.database.models.CategoryModel
import com.example.funfactoftheday.database.models.FactModel
import com.example.funfactoftheday.notifications.NotificationsViewModel

@Keep
class SettingsPageViewModel(private val appRepository: AppRepository):ViewModel() {

    val allFacts: LiveData<List<FactModel>> = appRepository.allFacts.asLiveData()
    val favoriteFacts:LiveData<List<FactModel>> = appRepository.favoriteFacts.asLiveData()

//    fun returnFavoriteCategories(): LiveData<List<CategoryModel>>{
//        return appRepository.returnFavoriteCategories().asLiveData()
//    }


    class SettingsPageViewModelFactory(private val repository: AppRepository): ViewModelProvider.Factory{
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if(modelClass.isAssignableFrom(SettingsPageViewModel::class.java)){
                @Suppress("UNCHECKED_CAST")
                return SettingsPageViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}