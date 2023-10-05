package com.example.funfactoftheday

import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.funfactoftheday.categories.CategoriesFragment
import com.example.funfactoftheday.database.models.CategoryModel
import com.example.funfactoftheday.database.models.FactModel
import com.example.funfactoftheday.databinding.ActivityMainBinding
import com.example.funfactoftheday.databinding.FragmentAddAFactBinding
import com.example.funfactoftheday.favoritefacts.FavoriteFactsFragment
import com.example.funfactoftheday.homepage.HomePageFragment
import com.example.funfactoftheday.homepage.HomePageViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import timber.log.Timber

class MainActivity : AppCompatActivity(), AddAFactFragment.NoticeDialogListener {

//    private lateinit var navController: NavController
    private lateinit var binding: ActivityMainBinding

    private val viewModel:HomePageViewModel by viewModels{
        HomePageViewModel.HomePageViewModelFactory((this.applicationContext as FactApplication).repository)
    }

    override fun onDialogPositiveClick(dialog: DialogFragment, binding: FragmentAddAFactBinding) {
        // User taps the dialog's positive button.
//        viewModel.insertCategoryModelCrossRef(binding.etFactName.text.toString(), binding.etCategoryName.text.toString())

    viewModel.viewModelScope.launch {
        val isOldFact = viewModel.doesFactExist(binding.etFactName.text.toString())
        val isOldCategory = viewModel.doesCategoryExist(binding.etCategoryName.text.toString())
        if(!isOldFact){
            viewModel.insertFact(FactModel(binding.etFactName.text.toString()))
        }
        if(!isOldCategory){
            viewModel.insertCategory(CategoryModel(binding.etCategoryName.text.toString()))
        }
        viewModel.insertCategoryModelCrossRef(binding.etFactName.text.toString(), binding.etCategoryName.text.toString())
        Timber.e("From Main Act: Fact: $isOldFact !!! Category: $isOldCategory")
    }

//        Timber.e("Positive Click in Homepage: ${binding.etCategoryName.text} and ${binding.etFactName.text}")
//        Timber.e("Positive Click in Homepage: ${binding.etFactName.text} and ${binding.etCategoryName.text} and ${viewModel.result.value}")
    }

    override fun onDialogNegativeClick(dialog: DialogFragment) {
        // User taps the dialog's negative button.
        Timber.e("Negative Click in Homepage")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainer) as NavHostFragment
        val navController = navHostFragment.navController

        NavigationUI.setupWithNavController(binding.bottomNavigationView, navController)

        binding.bottomNavigationView.setOnItemSelectedListener{
            NavigationUI.onNavDestinationSelected(it, navController)
            navController.popBackStack(it.itemId, inclusive = false)
            true
        }

    }

//    private fun setCurrentFragment(fragment: Fragment) =
//        supportFragmentManager.beginTransaction().apply {
//            Timber.e("SetCurrentFragment")
//            replace(R.id.fragmentContainer, fragment)
//            commit()
//        }

}