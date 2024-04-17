package com.example.funfactoftheday.settings

import androidx.annotation.Keep
import androidx.lifecycle.*
import com.example.funfactoftheday.database.AppRepository
import com.example.funfactoftheday.database.models.CategoriesWithFacts
import com.example.funfactoftheday.database.models.CategoryModel
import com.example.funfactoftheday.database.models.FactModel
import com.example.funfactoftheday.notifications.NotificationsViewModel
import kotlinx.coroutines.async

@Keep
class SettingsPageViewModel(private val appRepository: AppRepository):ViewModel() {

    val allFacts: LiveData<List<FactModel>> = appRepository.allFacts.asLiveData()
    val favoriteFacts:LiveData<List<FactModel>> = appRepository.favoriteFacts.asLiveData()

    suspend fun getFactsOfFavoriteCategories():
            LiveData<List<CategoriesWithFacts>> = viewModelScope.async {
        return@async appRepository.getFactsOfFavoriteCategories()
    }.await()

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