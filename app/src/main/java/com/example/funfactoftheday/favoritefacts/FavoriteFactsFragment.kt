package com.example.funfactoftheday.favoritefacts

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.viewModels
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.funfactoftheday.FactApplication
import com.example.funfactoftheday.FactsAdapter
import com.example.funfactoftheday.database.models.FactModel
import com.example.funfactoftheday.databinding.FactBinding
import com.example.funfactoftheday.databinding.FragmentFavoriteFactsBinding
import com.example.funfactoftheday.homepage.HomePageViewModel
import kotlinx.coroutines.launch
import timber.log.Timber

// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [FavoriteFactsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class FavoriteFactsFragment : Fragment(), FactsAdapter.OnItemClickListener, SearchView.OnQueryTextListener {

    private lateinit var binding:FragmentFavoriteFactsBinding
    private lateinit var adapter: FactsAdapter
    private var tempFacts:MutableList<FactModel> = mutableListOf()

    private val favoriteFactsViewModel: FavoriteFactsViewModel by viewModels {
        FavoriteFactsViewModel.FavoriteFactsViewModelFactory((context?.applicationContext as FactApplication).repository)
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        if(query != null){
            searchFactDatabase(query)
        }
        return true
    }

    override fun onQueryTextChange(query: String?): Boolean {
        if(query != null){
            searchFactDatabase(query)
        }
        return true
    }

    private fun searchFactDatabase(query: String){
        favoriteFactsViewModel.viewModelScope.launch {
            for(fact in tempFacts){
                favoriteFactsViewModel.insertFact(fact)
            }
            tempFacts.removeAll(tempFacts)
        }
        val searchQuery = "%$query%"
        favoriteFactsViewModel.searchFavoriteFacts(searchQuery).observe(this) { list ->
            list.sortedBy { !it.isFavorite }.let {
                adapter.submitList(it)
            }
        }
    }

    override fun onItemClick(itemBinding: FactBinding) {
        val fact = FactModel(itemBinding.tvFactName.text as String, itemBinding.cbFavorite.isChecked)
        tempFacts.add(fact)
//        favoriteFactsViewModel.insertFact(fact)
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
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        binding = FragmentFavoriteFactsBinding.inflate(inflater, container, false)
        return binding.root
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment FavoriteFactsFragment.
         */

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            FavoriteFactsFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = FactsAdapter(this)
        binding.rvFavoriteFactsPage.adapter = adapter
        binding.rvFavoriteFactsPage.layoutManager = LinearLayoutManager(requireContext())
        binding.rvFavoriteFactsPage.setItemViewCacheSize(10000)

        favoriteFactsViewModel.favoriteFacts.observe(viewLifecycleOwner){ facts ->
            facts.let {
                adapter.submitList(it as MutableList<FactModel>?)
            }
            onQueryTextChange(binding.searchViewFacts.query.toString())
        }

        binding.searchViewFacts.setOnQueryTextListener(this)
//        binding.searchViewFacts.isSubmitButtonEnabled = true

    }

    override fun onResume() {
        super.onResume()
        if(binding.searchViewFacts.query.toString().isEmpty()){
            onQueryTextSubmit("")
        }
    }

    override fun onPause() {
        super.onPause()
        favoriteFactsViewModel.viewModelScope.launch {
            for(fact in tempFacts){
                favoriteFactsViewModel.insertFact(fact)
            }
            tempFacts.removeAll(tempFacts)
        }
    }

}