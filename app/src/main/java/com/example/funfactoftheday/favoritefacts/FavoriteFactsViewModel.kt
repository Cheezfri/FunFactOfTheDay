package com.example.funfactoftheday.favoritefacts

import androidx.annotation.Keep
import androidx.lifecycle.*
import com.example.funfactoftheday.database.AppRepository
import com.example.funfactoftheday.database.models.FactModel
import com.example.funfactoftheday.homepage.HomePageViewModel
import kotlinx.coroutines.launch
import timber.log.Timber

@Keep
class FavoriteFactsViewModel (private val appRepository: AppRepository): ViewModel(){

    val allFacts:LiveData<List<FactModel>> = appRepository.allFacts.asLiveData()
    val favoriteFacts:LiveData<List<FactModel>> = appRepository.favoriteFacts.asLiveData()

    fun insertFact(factModel: FactModel) = viewModelScope.launch {
        appRepository.insertFact(factModel)
        Timber.e("Inserted ${factModel.factName}")
    }

    class FavoriteFactsViewModelFactory(private val repository: AppRepository): ViewModelProvider.Factory{
        override fun <T: ViewModel> create(modelClass: Class<T>): T {
            if(modelClass.isAssignableFrom(FavoriteFactsViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return FavoriteFactsViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }

}