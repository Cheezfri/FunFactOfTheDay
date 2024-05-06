package com.example.funfactoftheday

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.os.bundleOf
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import com.example.funfactoftheday.categories.CategoriesFragmentDirections
import com.example.funfactoftheday.category.CategoryFragment
import com.example.funfactoftheday.category.CategoryFragmentDirections
import com.example.funfactoftheday.database.AppDatabase
import com.example.funfactoftheday.database.models.CategoryModel
import com.example.funfactoftheday.database.models.FactModel
import com.example.funfactoftheday.databinding.*
import com.example.funfactoftheday.homepage.HomePageViewModel
import com.google.android.material.color.DynamicColors
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import timber.log.Timber

class MainActivity : AppCompatActivity(), AddAFactAndCategoryFragment.NoticeDialogListener, AddACategoryFragment.NoticeDialogListener, AddAFactFragment.NoticeDialogListener, DeleteACategoryFragment.NoticeDialogListener, DeleteFactFragment.NoticeDialogListener {

    private lateinit var binding: ActivityMainBinding


    private val viewModel:HomePageViewModel by viewModels{
        HomePageViewModel.HomePageViewModelFactory((this.applicationContext as FactApplication).repository)
    }

    override fun onDialogPositiveClick(dialog: DialogFragment, binding: FragmentAddAFactAndCategoryBinding) {
        // User taps the dialog's positive button.
        val factText = binding.etFactName.text.toString().trim()
        val categoryText = binding.etCategoryName.text.toString().trim()
        if(factText.isEmpty()){
            Toast.makeText(this, "Error: Fact Text was Empty. No Fact Added", Toast.LENGTH_LONG).show()
        } else
            if(categoryText.isEmpty()){
                Toast.makeText(this, "Error: Category Text was Empty. No Fact Added", Toast.LENGTH_LONG).show()
            } else{
                viewModel.viewModelScope.launch {
                    viewModel.insertCategory(CategoryModel(binding.etCategoryName.text.toString(), binding.cbFavoriteCategory.isChecked))
                    viewModel.insertFact(FactModel(binding.etFactName.text.toString(), binding.cbFavoriteFact.isChecked))
                    viewModel.insertCategoryModelCrossRef(binding.etFactName.text.toString(), binding.etCategoryName.text.toString())
                }
            }
    }

    override fun onDialogPositiveClick(dialog: DialogFragment, binding: FragmentAddACategoryBinding) {
        // User taps the dialog's positive button.
        val categoryText = binding.etCategoryName.text.toString().trim()
        if(categoryText.isEmpty()){
            Toast.makeText(this, "Error: Category Text was Empty. No Fact Added", Toast.LENGTH_LONG).show()
        } else {
            viewModel.viewModelScope.launch {
                viewModel.insertCategory(CategoryModel(binding.etCategoryName.text.toString(), binding.cbFavorite.isChecked))
            }
        }
    }

    override fun onDialogPositiveClick(dialog: DialogFragment, binding: FragmentAddAFactBinding, category: String) {
        // User taps the dialog's positive button.
        val factText = binding.etFactName.text.toString().trim()
        if(factText.isEmpty()){
            Toast.makeText(this, "Error: Fact Text was Empty. No Fact Added", Toast.LENGTH_LONG).show()
        } else {
            viewModel.viewModelScope.launch {
                viewModel.insertFact(FactModel(binding.etFactName.text.toString(), binding.cbFavoriteFact.isChecked))
                viewModel.insertCategoryModelCrossRef(binding.etFactName.text.toString(), category)
            }
        }
    }

    override fun onDialogPositiveClick(dialog: DialogFragment, binding: FragmentDeleteACategoryBinding, category: String) {
        findNavController(R.id.fragmentContainer).navigate(R.id.categoriesFragment)
        viewModel.viewModelScope.launch {
            viewModel.deleteCategory(category)
        }
    }

    override fun onDialogPositiveClick(dialog: DialogFragment, toDelete: Array<FactModel>?, categoryName: String) {
        viewModel.viewModelScope.launch {
            if (toDelete != null) {
                for(fact in toDelete){
                    viewModel.deleteFact(fact.factName)
                }
            }
            Timber.e("category: $categoryName")
            if(categoryName != ""){
                val categoryModel = CategoryModel(categoryName)
                val bundle = Bundle()
                bundle.putParcelable("categoryModel", categoryModel)
                findNavController(R.id.fragmentContainer).navigate(CategoryFragmentDirections.actionCategoryFragmentSelf(categoryModel))
            } else {
                findNavController(R.id.fragmentContainer).navigate(R.id.homePageFragment)
            }
        }
    }

    override fun onDialogNegativeClick(dialog: DialogFragment) {
        // User taps the dialog's negative button.
//        Timber.e("Negative Click in Homepage")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen().apply {
            setKeepOnScreenCondition{
                !viewModel.isReady.value
            }
        }
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        val sharedPref = getSharedPreferences("Onboard", MODE_PRIVATE).apply {
//            if(!getBoolean("Onboard", false)){
//                startActivity(Intent(applicationContext, OnBoardingActivity::class.java))
//            }
//        }

        setSupportActionBar(binding.myToolbar)
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainer) as NavHostFragment
        val navController = navHostFragment.navController

        NavigationUI.setupWithNavController(binding.bottomNavigationView, navController)
        binding.bottomNavigationView.setOnItemSelectedListener{
            NavigationUI.onNavDestinationSelected(it, navController)
            navController.popBackStack(it.itemId, inclusive = false)
            true
        }

    }

    override fun onPause() {
        super.onPause()
        val sharedPref = getSharedPreferences("FactsSharedPreferences", MODE_PRIVATE)
        val editor = sharedPref.edit()
        val testSet:HashSet<String> = HashSet()

        viewModel.allFacts.observe(this){ facts ->
            if(facts.isNotEmpty()){
                for(fact in facts){
                    testSet.add(fact.factName)
                }
            }
            editor.apply{
                putStringSet("TestStringSet", testSet)
                apply()
            }
        }
    }

}