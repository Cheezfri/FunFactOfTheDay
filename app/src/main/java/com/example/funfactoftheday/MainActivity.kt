package com.example.funfactoftheday

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.viewModelScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.example.funfactoftheday.category.CategoryFragmentDirections
import com.example.funfactoftheday.database.models.CategoryModel
import com.example.funfactoftheday.database.models.FactModel
import com.example.funfactoftheday.databinding.*
import com.example.funfactoftheday.homepage.HomePageViewModel
import kotlinx.coroutines.launch
import timber.log.Timber

class MainActivity : AppCompatActivity(), AddAFactAndCategoryFragment.NoticeDialogListener, AddACategoryFragment.NoticeDialogListener, AddAFactFragment.NoticeDialogListener, DeleteACategoryFragment.NoticeDialogListener, DeleteFactFragment.NoticeDialogListener {

    private lateinit var binding: ActivityMainBinding

    private val viewModel:HomePageViewModel by viewModels{
        HomePageViewModel.HomePageViewModelFactory((this.applicationContext as FactApplication).repository)
    }

    private fun makeStringOneSpaceBetweenWordsEndWithPeriod(input: String): String {
        // Split the input by one or more spaces
        var fact = input.trim()
        if(!fact.endsWith(".")){
            fact = "$fact."
        }
        // Split the input by spaces and filter out any empty parts
        val parts = fact.split(" ").filter { it.isNotEmpty() }
        // Join the filtered parts with a single space
        val rejoinedString = parts.joinToString(" ")
        // Capitalize the first word
        return rejoinedString.replaceFirstChar { it.uppercase() }
    }

    private fun makeStringOneSpaceBetweenWordsAndCapitalize(input: String): String {
        // Split the input by one or more spaces
        val parts = input.trim().split("\\s+".toRegex())
        // Capitalize each word
        val capitalizedParts = parts.map { it.replaceFirstChar { itt -> itt.uppercaseChar() } }
        // Join the parts with a single space
        return capitalizedParts.joinToString(" ")
    }

    override fun onDialogPositiveClick(dialog: DialogFragment, binding: FragmentAddAFactAndCategoryBinding) {
        // User taps the dialog's positive button.
        viewModel.viewModelScope.launch {
            val factText = makeStringOneSpaceBetweenWordsEndWithPeriod(binding.etFactName.text.toString())
            val categoryText = makeStringOneSpaceBetweenWordsAndCapitalize(binding.etCategoryName.text.toString())
            if(binding.etFactName.text.toString().trim().isEmpty()){
                Toast.makeText(applicationContext, "Error: Fact Text was Empty. No Fact Added", Toast.LENGTH_LONG).show()
                return@launch
            }
            if(binding.etCategoryName.text.toString().trim().isEmpty()){
                Toast.makeText(applicationContext, "Error: Category Text was Empty. No Category Added", Toast.LENGTH_LONG).show()
                return@launch
            }
            if(viewModel.doesFactExist(factText)){
                Toast.makeText(applicationContext, "Error: Fact already exists. No Fact Added", Toast.LENGTH_LONG).show()
                return@launch
            }
            if(viewModel.doesCategoryExist(categoryText)){
                Timber.e("The Category Exists: $categoryText")
                Toast.makeText(applicationContext, "Error: Category already exists. No Fact Added", Toast.LENGTH_LONG).show()
                return@launch
            }
            viewModel.insertCategory(CategoryModel(categoryText, binding.cbFavoriteCategory.isChecked))
            viewModel.insertFact(FactModel(factText, binding.cbFavoriteFact.isChecked))
            viewModel.insertCategoryModelCrossRef(factText, categoryText)
        }
    }

    override fun onDialogPositiveClick(dialog: DialogFragment, binding: FragmentAddACategoryBinding) {
        // User taps the dialog's positive button.
        viewModel.viewModelScope.launch {
            var categoryText = makeStringOneSpaceBetweenWordsAndCapitalize(binding.etCategoryName.text.toString())
            if(binding.etCategoryName.text.toString().trim().isEmpty()){
                Toast.makeText(applicationContext, "Error: Category Text was Empty. No Category Added", Toast.LENGTH_LONG).show()
                return@launch
            }
            if(viewModel.doesCategoryExist(categoryText)){
                Timber.e("The Category Exists: $categoryText")
                Toast.makeText(applicationContext, "Error: Category already exists. No Fact Added", Toast.LENGTH_LONG).show()
                return@launch
            }
            viewModel.insertCategory(CategoryModel(categoryText, binding.cbFavorite.isChecked))
        }
    }

    override fun onDialogPositiveClick(dialog: DialogFragment, binding: FragmentAddAFactBinding, category: String) {
        // User taps the dialog's positive button.
        viewModel.viewModelScope.launch {
            var factText = makeStringOneSpaceBetweenWordsEndWithPeriod(binding.etFactName.text.toString())
            if(binding.etFactName.text.toString().trim().isEmpty()){
                Toast.makeText(applicationContext, "Error: Fact Text was Empty. No Fact Added", Toast.LENGTH_LONG).show()
                return@launch
            }
            if(viewModel.doesFactExist(factText)){
                Toast.makeText(applicationContext, "Error: Fact already exists. No Fact Added", Toast.LENGTH_LONG).show()
                return@launch
            }
            viewModel.insertFact(FactModel(factText, binding.cbFavoriteFact.isChecked))
            viewModel.insertCategoryModelCrossRef(factText, category)
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

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainer) as NavHostFragment
        val navController = navHostFragment.navController

        navController.addOnDestinationChangedListener{_, destination,_ ->
            when(destination.id){
                R.id.onboardingFragment -> binding.bottomNavigationView.visibility = View.GONE
                else -> binding.bottomNavigationView.visibility = View.VISIBLE
            }
        }

        val sharedPref = getSharedPreferences("Onboard", MODE_PRIVATE).apply {
            if(!getBoolean("Onboard", false)){
                navController.navigate(R.id.onboardingFragment)
            }
        }
        setSupportActionBar(binding.myToolbar)
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
        val currentFacts = sharedPref.getStringSet("TestStringSet", HashSet<String>())

        if(currentFacts.isNullOrEmpty()){
            viewModel.allFacts.observe(this){ facts ->
                if(facts.isNotEmpty()){
                    for(fact in facts){
                        testSet.add(fact.factName)
                    }
                }
                editor.apply{
                    putStringSet("TestStringSet", testSet)
                    apply()
                    Timber.e("Added all facts")
                }
            }
        }
    }

}