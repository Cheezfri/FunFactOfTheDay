package com.example.funfactoftheday

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.example.funfactoftheday.database.models.CategoryModel
import com.example.funfactoftheday.database.models.FactModel
import com.example.funfactoftheday.databinding.ActivityMainBinding
import com.example.funfactoftheday.databinding.FragmentAddACategoryBinding
import com.example.funfactoftheday.databinding.FragmentAddAFactAndCategoryBinding
import com.example.funfactoftheday.databinding.FragmentAddAFactBinding
import com.example.funfactoftheday.homepage.HomePageViewModel
import kotlinx.coroutines.launch
import timber.log.Timber

class MainActivity : AppCompatActivity(), AddAFactAndCategoryFragment.NoticeDialogListener, AddACategoryFragment.NoticeDialogListener, AddAFactFragment.NoticeDialogListener {

//    private lateinit var navController: NavController
    private lateinit var binding: ActivityMainBinding

    private val viewModel:HomePageViewModel by viewModels{
        HomePageViewModel.HomePageViewModelFactory((this.applicationContext as FactApplication).repository)
    }

    override fun onDialogPositiveClick(dialog: DialogFragment, binding: FragmentAddAFactAndCategoryBinding) {
        // User taps the dialog's positive button.
//        viewModel.insertCategoryModelCrossRef(binding.etFactName.text.toString(), binding.etCategoryName.text.toString())

    viewModel.viewModelScope.launch {
        viewModel.insertCategory(CategoryModel(binding.etCategoryName.text.toString(), binding.cbFavoriteCategory.isChecked))
        viewModel.insertFact(FactModel(binding.etFactName.text.toString(), binding.cbFavoriteFact.isChecked))
        viewModel.insertCategoryModelCrossRef(binding.etFactName.text.toString(), binding.etCategoryName.text.toString())
//        Timber.e("From Main Act: Fact: $isOldFact !!! Category: $isOldCategory")
    }

//        Timber.e("Positive Click in Homepage: ${binding.etCategoryName.text} and ${binding.etFactName.text}")
//        Timber.e("Positive Click in Homepage: ${binding.etFactName.text} and ${binding.etCategoryName.text} and ${viewModel.result.value}")
    }

    override fun onDialogPositiveClick(dialog: DialogFragment, binding: FragmentAddACategoryBinding) {
        // User taps the dialog's positive button.
        viewModel.viewModelScope.launch {
            viewModel.insertCategory(CategoryModel(binding.etCategoryName.text.toString(), binding.cbFavorite.isChecked))
            Timber.e("From Main Act:Category: ${binding.etCategoryName.text} ${binding.cbFavorite.isChecked}")
        }
    }

    override fun onDialogPositiveClick(dialog: DialogFragment, binding: FragmentAddAFactBinding, category: String) {
        // User taps the dialog's positive button.
        viewModel.viewModelScope.launch {
            viewModel.insertFact(FactModel(binding.etFactName.text.toString(), binding.cbFavoriteFact.isChecked))
            viewModel.insertCategoryModelCrossRef(binding.etFactName.text.toString(), category)
            Timber.e("From Main Act:Category: ${binding.etFactName.text} ${binding.cbFavoriteFact.isChecked}")
        }
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