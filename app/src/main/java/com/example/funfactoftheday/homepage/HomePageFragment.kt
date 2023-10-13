package com.example.funfactoftheday.homepage

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.funfactoftheday.AddAFactAndCategoryFragment
import com.example.funfactoftheday.FactApplication
import com.example.funfactoftheday.FactsAdapter
import com.example.funfactoftheday.database.models.FactModel
import com.example.funfactoftheday.databinding.FactBinding
import com.example.funfactoftheday.databinding.FragmentHomePageBinding
import timber.log.Timber

// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [HomePageFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HomePageFragment : Fragment(), FactsAdapter.OnItemClickListener, SearchView.OnQueryTextListener {

    private lateinit var binding:FragmentHomePageBinding
    private lateinit var adapter: FactsAdapter

    private val homePageViewModel: HomePageViewModel by viewModels {
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
        homePageViewModel.searchFactDatabase(searchQuery).observe(this) { list ->
                list.sortedBy { !it.isFavorite }.let {
                    adapter.submitList(it)
            }
        }
    }

    override fun onItemClick(itemBinding: FactBinding) {
        val fact = FactModel(itemBinding.tvFactName.text as String, itemBinding.cbFavorite.isChecked)
        homePageViewModel.insertFact(fact)
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

    //method used to inflate layout
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        binding = FragmentHomePageBinding.inflate(inflater, container, false)
        return binding.root
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment HomePageFragment.
         */
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            HomePageFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Timber.plant(Timber.DebugTree())

        adapter = FactsAdapter(this)
        binding.rvFactsHomePage.adapter = adapter
        binding.rvFactsHomePage.layoutManager = LinearLayoutManager(requireContext())

        homePageViewModel.allFacts.observe(viewLifecycleOwner){ facts ->
            if(facts.isNotEmpty()){
                facts.sortedBy { !it.isFavorite }.let {
                    adapter.submitList(it as MutableList<FactModel>?)
                    Timber.e("List Submitted")
                    for(i in it){
                        Timber.e("Fact: ${i.factName} isFav: ${i.isFavorite}")
                    }
                }
            }
        }

        binding.searchViewFacts.setOnQueryTextListener(this)
        binding.searchViewFacts.isSubmitButtonEnabled = true

        binding.btnGenerateFunFact.setOnClickListener {
            val fragment = AddAFactAndCategoryFragment()
            fragment.show((activity as AppCompatActivity).supportFragmentManager, "showPopUp")
        }
    }

    override fun onResume() {
        super.onResume()
        if(binding.searchViewFacts.query.toString().isEmpty()){
            onQueryTextSubmit("")
        }
    }

}
