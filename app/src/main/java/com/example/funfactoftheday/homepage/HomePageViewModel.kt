package com.example.funfactoftheday.homepage

import androidx.annotation.Keep
import androidx.lifecycle.*
import com.example.funfactoftheday.database.AppRepository
import com.example.funfactoftheday.database.models.FactModel
import kotlinx.coroutines.launch
import timber.log.Timber

@Keep
class HomePageViewModel (private val appRepository: AppRepository): ViewModel() {

    val allFacts:LiveData<List<FactModel>> = appRepository.allFacts.asLiveData()

    fun insertFact(factModel: FactModel) = viewModelScope.launch {
        appRepository.insertFact(factModel)
        Timber.e("Inserted ${factModel.factName}")
    }

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