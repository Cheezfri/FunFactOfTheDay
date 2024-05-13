package com.example.funfactoftheday.category

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.funfactoftheday.*
import com.example.funfactoftheday.database.models.CategoryModel
import com.example.funfactoftheday.database.models.FactModel
import com.example.funfactoftheday.databinding.FactBinding
import com.example.funfactoftheday.databinding.FragmentCategoryBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
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

    val args: CategoryFragmentArgs by navArgs()
    private lateinit var binding: FragmentCategoryBinding
    private lateinit var adapter: FactsAdapter
    private lateinit var categoryModel: CategoryModel
    private lateinit var currentListOfFacts:List<FactModel>
    private var isQueryHappening = false
    private var currentQuery = ""
    private var factsToFavorite:MutableList<FactModel> = mutableListOf()
    private var factsToDelete:MutableList<FactModel> = mutableListOf()

    fun sendResult(resultKey: String, resultBundle: Bundle) {
        requireActivity().supportFragmentManager.setFragmentResult(resultKey, resultBundle)
    }

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
            for(fact in factsToFavorite){
                categoryViewModel.insertFact(fact)
            }
            factsToFavorite.removeAll(factsToFavorite)
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
        val fact = FactModel(itemBinding.tvFactName.text as String, itemBinding.cbFavorite.isChecked, false)
        if(factsToFavorite.contains(FactModel(fact.factName, !fact.isFavorite, false))){
            factsToFavorite.remove(FactModel(fact.factName, !fact.isFavorite, false))
        }
        factsToFavorite.add(fact)
    }

    override fun onDeleteClick(itemBinding: FactBinding) {
        val fact = FactModel(itemBinding.tvFactName.text as String, itemBinding.cbFavorite.isChecked, true)
        if(factsToDelete.contains(FactModel(fact.factName, fact.isFavorite, true))){
            factsToDelete.remove(FactModel(fact.factName, fact.isFavorite, true))
        } else{
            factsToDelete.add(fact)
        }
    }

    override fun onTextHold(itemBinding: FactBinding) {
        CoroutineScope(Dispatchers.IO).launch {
            categoryViewModel.toggleDeletable()
            categoryViewModel.viewModelScope.launch {
                if(categoryViewModel.returnDeletable()){
                    binding.btnGenerateFunFact.visibility = View.INVISIBLE
                    binding.btnDeleteFact.visibility = View.VISIBLE
                } else{
                    binding.btnGenerateFunFact.visibility = View.VISIBLE
                    binding.btnDeleteFact.visibility = View.INVISIBLE
                }
            }
        }
//        findNavController().navigate(R.id.deleteFactsFragment)
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
        val categoryModel = args.categoryModel

        val activity = requireActivity() as AppCompatActivity
        activity.supportActionBar?.title = "Facts about ${categoryModel.categoryName}"

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
                val isDeletable = categoryViewModel.returnDeletable()
                for(fact in factsToFavorite){
                    if(isDeletable){
                        val factToInsert = FactModel(fact.factName, fact.isFavorite, true)
                        categoryViewModel.insertFact(factToInsert)
                    } else{
                        val factToInsert = FactModel(fact.factName, fact.isFavorite, false)
                        categoryViewModel.insertFact(factToInsert)
                    }
                }
                factsToFavorite.removeAll(factsToFavorite)
            }
            val fragment = AddAFactFragment.newInstance(categoryModel.categoryName)
            fragment.show((activity as AppCompatActivity).supportFragmentManager, "showPopUp")
        }

        binding.btnDeleteFact.setOnClickListener{
            val fragment = DeleteFactFragment.newInstance(factsToDelete.toTypedArray(), categoryModel.categoryName)
            fragment.show((activity as AppCompatActivity).supportFragmentManager, "showPopUp")
        }
    }

    override fun onResume() {
        super.onResume()
//        CoroutineScope(Dispatchers.IO).launch {
//            if(categoryViewModel.returnDeletable()){
//                categoryViewModel.toggleDeletable()
//            }
//            categoryViewModel.viewModelScope.launch {
//                if(categoryViewModel.returnDeletable()){
//                    binding.btnGenerateFunFact.visibility = View.INVISIBLE
//                    binding.btnDeleteFact.visibility = View.VISIBLE
//                } else{
//                    binding.btnGenerateFunFact.visibility = View.VISIBLE
//                    binding.btnDeleteFact.visibility = View.INVISIBLE
//                }
//            }
//        }
    }

    override fun onPause() {
        super.onPause()
        CoroutineScope(Dispatchers.IO).launch{
            if(categoryViewModel.returnDeletable()){
                categoryViewModel.toggleDeletable()
            }
            categoryViewModel.viewModelScope.launch {
                for(fact in factsToFavorite){
                    val factToInsert = FactModel(fact.factName, fact.isFavorite, false)
                    categoryViewModel.insertFact(factToInsert)
                }
                factsToFavorite.removeAll(factsToFavorite)
            }
        }
//        categoryViewModel.viewModelScope.launch {
//            val isDeletable = categoryViewModel.returnDeletable()
//            for(fact in factsToFavorite){
//                if(isDeletable){
//                    val factToInsert = FactModel(fact.factName, fact.isFavorite, true)
//                    categoryViewModel.insertFact(factToInsert)
//                } else{
//                    val factToInsert = FactModel(fact.factName, fact.isFavorite, false)
//                    categoryViewModel.insertFact(factToInsert)
//                }
//            }
//            factsToFavorite.removeAll(factsToFavorite)
//        }
    }

}