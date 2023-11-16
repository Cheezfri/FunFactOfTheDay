package com.example.funfactoftheday.homepage

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.funfactoftheday.AddAFactAndCategoryFragment
import com.example.funfactoftheday.FactApplication
import com.example.funfactoftheday.FactsAdapter
import com.example.funfactoftheday.database.models.FactModel
import com.example.funfactoftheday.databinding.FactBinding
import com.example.funfactoftheday.databinding.FragmentHomePageBinding
import kotlinx.coroutines.launch
import timber.log.Timber

// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"
//TODO: keep scroll position for recyclerview after update, maybe findviewholder. scrolltopositoin
/**
 * A simple [Fragment] subclass.
 * Use the [HomePageFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HomePageFragment : Fragment(), FactsAdapter.OnItemClickListener, SearchView.OnQueryTextListener {

    private lateinit var binding:FragmentHomePageBinding
    private lateinit var adapter: FactsAdapter
    private var tempFacts:MutableList<FactModel> = mutableListOf()

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

    /*
    search "dog"
    fav a few
    click x in text search box. prob fixed this one
     */
    private fun searchFactDatabase(query: String){
        homePageViewModel.viewModelScope.launch {
            for(fact in tempFacts){
                homePageViewModel.insertFact(fact)
            }
            tempFacts.removeAll(tempFacts)
        }
        val searchQuery = "%$query%"
        homePageViewModel.searchFactDatabase(searchQuery).observe(this) { list ->
                list.sortedBy { !it.isFavorite }.let {
                    adapter.submitList(it)
            }
        }
    }

    override fun onItemClick(itemBinding: FactBinding) {

//        position = ((binding.rvFactsHomePage.layoutManager) as LinearLayoutManager).findFirstVisibleItemPosition()
//        startView = ((binding.rvFactsHomePage.layoutManager) as LinearLayoutManager).getChildAt(0)
//        topView = if (startView == null) 0 else startView!!.top - binding.rvFactsHomePage.paddingTop
//        ((binding.rvFactsHomePage.layoutManager) as LinearLayoutManager).scrollToPositionWithOffset(position, topView)

        val fact = FactModel(itemBinding.tvFactName.text as String, itemBinding.cbFavorite.isChecked)
        tempFacts.add(fact)
//        homePageViewModel.viewModelScope.launch {
//            homePageViewModel.insertFact(fact)
//        }

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
        val layoutManager = LinearLayoutManager(requireContext())
        binding.rvFactsHomePage.layoutManager = layoutManager
        binding.rvFactsHomePage.setItemViewCacheSize(10000)

        homePageViewModel.allFacts.observe(viewLifecycleOwner){ facts ->
            if(facts.isNotEmpty()){
                facts.sortedBy { !it.isFavorite }.let {
                    adapter.submitList(it as MutableList<FactModel>?)
                }
            }
            onQueryTextChange(binding.searchViewFacts.query.toString())
        }

        binding.searchViewFacts.setOnQueryTextListener(this)
//        binding.searchViewFacts.isSubmitButtonEnabled = true

//        RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY

        binding.btnGenerateFunFact.setOnClickListener {
            homePageViewModel.viewModelScope.launch {
                for(fact in tempFacts){
                    homePageViewModel.insertFact(fact)
                }
                tempFacts.removeAll(tempFacts)
        }
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

    override fun onPause() {
        super.onPause()
        homePageViewModel.viewModelScope.launch {
            for(fact in tempFacts){
                homePageViewModel.insertFact(fact)
            }
            tempFacts.removeAll(tempFacts)
        }
    }

}
