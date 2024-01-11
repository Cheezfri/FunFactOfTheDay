package com.example.funfactoftheday.deletepage

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.viewModels
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.funfactoftheday.FactApplication
import com.example.funfactoftheday.FactsAdapter
import com.example.funfactoftheday.R
import com.example.funfactoftheday.database.models.FactModel
import com.example.funfactoftheday.databinding.FactBinding
import com.example.funfactoftheday.databinding.FragmentDeleteFactsBinding
import com.example.funfactoftheday.homepage.HomePageViewModel
import kotlinx.coroutines.launch

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [DeleteFactsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class DeleteFactsFragment : Fragment(), FactsAdapter.OnItemClickListener, SearchView.OnQueryTextListener {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var binding: FragmentDeleteFactsBinding
    private lateinit var adapter: FactsAdapter
    private var factsToFavorite:MutableList<FactModel> = mutableListOf()
    private var factsToDelete:MutableList<FactModel> = mutableListOf()

    private val viewModel: HomePageViewModel by viewModels {
        HomePageViewModel.HomePageViewModelFactory((context?.applicationContext as FactApplication).repository)
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
        val searchQuery = "%$query%"
        viewModel.searchFactDatabase(searchQuery).observe(this) { list ->
            list.sortedBy { !it.isFavorite }.let {
                adapter.submitList(it)
            }
        }
    }

    override fun onFavoriteClick(itemBinding: FactBinding) {
        val fact = FactModel(itemBinding.tvFactName.text as String, itemBinding.cbFavorite.isChecked)
        factsToFavorite.add(fact)
    }

    override fun onDeleteClick(itemBinding: FactBinding) {
        val fact = FactModel(itemBinding.tvFactName.text as String, itemBinding.cbFavorite.isChecked)
        factsToDelete.add(fact)
    }

    override fun onTextHold(itemBinding: FactBinding) {
        findNavController().popBackStack()
    }

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
        binding = FragmentDeleteFactsBinding.inflate(inflater, container, false)
        return binding.root
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment DeleteFactsFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            DeleteFactsFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = FactsAdapter(this)

        binding.rvFactsHomePage.adapter = adapter
        val layoutManager = LinearLayoutManager(requireContext())
        binding.rvFactsHomePage.layoutManager = layoutManager
        binding.rvFactsHomePage.setItemViewCacheSize(10000)

        viewModel.allFacts.observe(viewLifecycleOwner){ facts ->
            if(facts.isNotEmpty()){
                facts.sortedBy { !it.isFavorite }.let {
                    adapter.submitList(it as MutableList<FactModel>?)
                }
            }
            onQueryTextChange(binding.searchViewFacts.query.toString())
        }

        binding.searchViewFacts.setOnQueryTextListener(this)

    }

    override fun onResume() {
        super.onResume()
        if(binding.searchViewFacts.query.toString().isEmpty()){
            onQueryTextSubmit("")
        }
    }

    override fun onPause() {
        super.onPause()
        viewModel.viewModelScope.launch {
            for(fact in factsToFavorite){
                viewModel.insertFact(fact)
            }
            factsToFavorite.removeAll(factsToFavorite)
        }
    }

}