package com.example.funfactoftheday.homepage

import androidx.annotation.Keep
import androidx.lifecycle.*
import com.example.funfactoftheday.database.AppRepository
import com.example.funfactoftheday.database.models.CategoryModel
import com.example.funfactoftheday.database.models.FactModel
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.Timer

@Keep
class HomePageViewModel (private val appRepository: AppRepository): ViewModel() {

    private val _isReady = MutableStateFlow(false)
    val isReady = _isReady.asStateFlow()
    val allFacts:LiveData<List<FactModel>> = appRepository.allFacts.asLiveData()

    init {
        viewModelScope.launch {
            //R should be API level 30
            if(android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.R){
                delay(1000)
            }
            _isReady.value = true
        }
    }

    fun insertFact(factModel: FactModel) = viewModelScope.launch {
        appRepository.insertFact(factModel)
    }

    fun insertCategory(categoryModel: CategoryModel) = viewModelScope.launch{
        appRepository.insertCategory(categoryModel)
        Timber.e("Inserted ${categoryModel.categoryName}")
    }

    fun insertCategoryModelCrossRef(factName: String, categoryName: String) = viewModelScope.launch {
        appRepository.insertCategoryModelCrossRef(factName, categoryName)
    }

    suspend fun doesFactExist(factName: String): Boolean = viewModelScope.async {
        return@async appRepository.doesFactExist (factName)
    }.await()

    suspend fun doesCategoryExist(categoryName: String): Boolean = viewModelScope.async {
        return@async appRepository.doesCategoryExist(categoryName)
    }.await()

    fun searchFactDatabase(searchQuery:String): LiveData<List<FactModel>>{
        return appRepository.searchFactDatabase(searchQuery).asLiveData()
    }

    suspend fun deleteCategory(categoryName: String){
        appRepository.deleteCategory(categoryName)
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