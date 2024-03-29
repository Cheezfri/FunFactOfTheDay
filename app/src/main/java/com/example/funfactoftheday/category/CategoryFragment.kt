package com.example.funfactoftheday.category

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.funfactoftheday.AddAFactFragment
import com.example.funfactoftheday.DeleteACategoryFragment
import com.example.funfactoftheday.FactApplication
import com.example.funfactoftheday.FactsAdapter
import com.example.funfactoftheday.database.models.CategoryModel
import com.example.funfactoftheday.database.models.FactModel
import com.example.funfactoftheday.databinding.FactBinding
import com.example.funfactoftheday.databinding.FragmentCategoryBinding
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.*

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [CategoryFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class CategoryFragment : Fragment(), FactsAdapter.OnItemClickListener, SearchView.OnQueryTextListener  {

    private lateinit var binding: FragmentCategoryBinding
    private lateinit var adapter: FactsAdapter
    private lateinit var categoryModel: CategoryModel
    private lateinit var currentListOfFacts:List<FactModel>
    private var isQueryHappening = false
    private var currentQuery = ""
    private var tempFacts:MutableList<FactModel> = mutableListOf()

    private val categoryViewModel: CategoryViewModel by viewModels {
        CategoryViewModel.CategoryViewModelFactory((context?.applicationContext as FactApplication).repository)
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        if(query != null){
            isQueryHappening = true
            currentQuery = query
            searchFactDatabase(query)
        }
        return false
    }

    override fun onQueryTextChange(query: String?): Boolean {
        if(query != null){
            isQueryHappening = true
            currentQuery = query
            searchFactDatabase(query)
        }
        return false
    }

    private fun searchFactDatabase(query: String){
        categoryViewModel.viewModelScope.launch {
            for(fact in tempFacts){
                categoryViewModel.insertFact(fact)
            }
            tempFacts.removeAll(tempFacts)
        }
        var listToAdd = mutableListOf<FactModel>()

        for(item in currentListOfFacts){
            if(item.factName.toLowerCase().contains(query.lowercase(Locale.getDefault()))){
                listToAdd.add(item)
            }
        }
//        if(listToAdd.isNotEmpty()){
            adapter.submitList(listToAdd.sortedBy { !it.isFavorite })
//        }
    }

    override fun onFavoriteClick(itemBinding: FactBinding) {
        val fact = FactModel(itemBinding.tvFactName.text as String, itemBinding.cbFavorite.isChecked)
        tempFacts.add(fact)
//        categoryViewModel.insertFact(fact)
    }

    override fun onDeleteClick(itemBinding: FactBinding) {
        TODO("Not yet implemented")
    }

    override fun onTextHold(itemBinding: FactBinding) {
        TODO("Not yet implemented")
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
        binding = FragmentCategoryBinding.inflate(inflater, container, false)
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
            CategoryFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = FactsAdapter(this)
        binding.rvFactsCategoryFragment.adapter = adapter
        binding.rvFactsCategoryFragment.layoutManager = LinearLayoutManager(requireContext())
        binding.rvFactsCategoryFragment.setItemViewCacheSize(10000)

        setFragmentResultListener("CategoriesToCategoryRequestKey"){ requestKey, bundle ->
            val result = bundle.getBundle("CategoriesToCategoryBundleKey")
            categoryModel = result!!.getParcelable<CategoryModel>("categoryModel")!!

            categoryViewModel.viewModelScope.launch{
                categoryViewModel.getFactsOfCategories(categoryModel!!).observe(viewLifecycleOwner){ items ->
                    items.let { itt ->
                        currentListOfFacts = itt.facts
                        if(currentListOfFacts.isEmpty()){
                            binding.rvFactsCategoryFragment.visibility = View.INVISIBLE
                            binding.searchViewFacts.visibility = View.INVISIBLE
                            binding.btnDeleteCategory.visibility = View.VISIBLE
                            binding.tvEmptyList.visibility = View.VISIBLE
                        } else {
                            binding.rvFactsCategoryFragment.visibility = View.VISIBLE
                            binding.searchViewFacts.visibility = View.VISIBLE
                            binding.btnDeleteCategory.visibility = View.INVISIBLE
                            binding.tvEmptyList.visibility = View.INVISIBLE
                            if(isQueryHappening){
                                searchFactDatabase(currentQuery)
                            } else {
                                adapter.submitList(itt.facts.sortedBy { !it.isFavorite })
                            }
                        }
                    }
                }
            }

            binding.btnDeleteCategory.setOnClickListener{
                val fragment = DeleteACategoryFragment.newInstance(categoryModel.categoryName)
                fragment.show((activity as AppCompatActivity).supportFragmentManager, "showPopUp")
            }

            binding.searchViewFacts.setOnQueryTextListener(this)
//            binding.searchViewFacts.isSubmitButtonEnabled = true
            binding.btnGenerateFunFact.setOnClickListener{
                categoryViewModel.viewModelScope.launch {
                    for(fact in tempFacts){
                        categoryViewModel.insertFact(fact)
                    }
                    tempFacts.removeAll(tempFacts)
                }
                val fragment = AddAFactFragment.newInstance(categoryModel.categoryName)
                fragment.show((activity as AppCompatActivity).supportFragmentManager, "showPopUp")
            }

        }

    }

    override fun onPause() {
        super.onPause()
        categoryViewModel.viewModelScope.launch {
            for(fact in tempFacts){
                categoryViewModel.insertFact(fact)
            }
            tempFacts.removeAll(tempFacts)
        }
    }

//    override fun onResume() {
//        super.onResume()
//        if(binding.searchViewFacts.query.toString().isEmpty()){
//            onQueryTextSubmit("")
//        }
//    }

}