package com.example.funfactoftheday.categories

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.os.bundleOf
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.funfactoftheday.*
import com.example.funfactoftheday.category.CategoryFragment
import com.example.funfactoftheday.database.AppDao
import com.example.funfactoftheday.database.AppDatabase
import com.example.funfactoftheday.database.models.CategoryModel
import com.example.funfactoftheday.databinding.CategoryBinding
import com.example.funfactoftheday.databinding.FragmentCategoriesBinding
import kotlinx.coroutines.launch
import timber.log.Timber


// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [CategoriesFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class CategoriesFragment : Fragment(), CategoryAdapter.OnItemClickListener, SearchView.OnQueryTextListener{

    private lateinit var binding:FragmentCategoriesBinding
    private lateinit var adapter:CategoryAdapter
    private var tempCategories:MutableList<CategoryModel> = mutableListOf()

    private val categoriesViewModel: CategoriesViewModel by viewModels {
        CategoriesViewModel.CategoriesViewModelFactory((context?.applicationContext as FactApplication).repository)
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        if(query != null){
            searchCategoryDatabase(query)
        }
        return true
    }

    override fun onQueryTextChange(query: String?): Boolean {
        if(query != null){
            searchCategoryDatabase(query)
        }
        return true
    }

    private fun searchCategoryDatabase(query: String){
        categoriesViewModel.viewModelScope.launch {
            for(category in tempCategories){
                categoriesViewModel.insertCategory(category)
            }
            tempCategories.removeAll(tempCategories)
        }
        val searchQuery = "%$query%"
        categoriesViewModel.searchCategoryDatabase(searchQuery).observe(this) { list ->
            list.sortedBy { !it.isFavorite }.let {
                adapter.submitList(it)
            }
        }
    }

    override fun onFavoriteClick(itemBinding: CategoryBinding){
        val category = CategoryModel(itemBinding.tvCategoryName.text.toString(), itemBinding.cbFavorite.isChecked)
        tempCategories.add(category)
    //        categoriesViewModel.insertCategory(category)
    }

    override fun onTextClick(itemBinding: CategoryBinding){
        val categoryModel = CategoryModel(itemBinding.tvCategoryName.text.toString(), itemBinding.cbFavorite.isChecked)
        val bundle = Bundle()
        bundle.putParcelable("categoryModel", categoryModel)
        findNavController().navigate(CategoriesFragmentDirections.actionCategoriesFragmentToCategoryFragment(categoryModel))
//        setFragmentResult("CategoriesToCategoryRequestKey", bundleOf("CategoriesToCategoryBundleKey" to bundle))
    }


    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentCategoriesBinding.inflate(inflater, container, false)
        return binding.root
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment CategoryFragment.
         */
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            CategoriesFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val activity = requireActivity() as AppCompatActivity
        activity.supportActionBar?.title = "All Categories"

        adapter = CategoryAdapter(this, this)
        binding.rvCategoriesPage.adapter = adapter
        binding.rvCategoriesPage.layoutManager = LinearLayoutManager(requireContext())
        binding.rvCategoriesPage.setItemViewCacheSize(10000)

        categoriesViewModel.allCategories.observe(viewLifecycleOwner){ categories ->
            if(categories.isNotEmpty()){
                categories.sortedBy { !it.isFavorite }.let{
                    adapter.submitList(it as MutableList<CategoryModel>?)
                }
            }
//            onQueryTextChange(binding.searchViewCategories.query.toString())
        }

        binding.searchViewCategories.setOnQueryTextListener(this)
//        binding.searchViewCategories.isSubmitButtonEnabled = true

        binding.btnGenerateCategory.setOnClickListener{
            categoriesViewModel.viewModelScope.launch {
                for(category in tempCategories){
                    categoriesViewModel.insertCategory(category)
                }
                tempCategories.removeAll(tempCategories)
            }
            val fragment = AddACategoryFragment()
            fragment.show((activity as AppCompatActivity).supportFragmentManager, "showPopUp")
        }

    }

    override fun onResume() {
        super.onResume()
        if(binding.searchViewCategories.query.toString().isEmpty()){
            onQueryTextSubmit("")
        }
    }

    override fun onPause() {
        super.onPause()
        categoriesViewModel.viewModelScope.launch {
            for(category in tempCategories){
                categoriesViewModel.insertCategory(category)
            }
            tempCategories.removeAll(tempCategories)
        }
    }

}