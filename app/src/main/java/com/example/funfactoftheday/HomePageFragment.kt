package com.example.funfactoftheday

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.funfactoftheday.database.AppDao
import com.example.funfactoftheday.database.AppDatabase
import com.example.funfactoftheday.database.models.CategoryModel
import com.example.funfactoftheday.database.models.FactModel
import com.example.funfactoftheday.database.reletions.CategoryModelCrossRef
import com.example.funfactoftheday.databinding.FragmentHomePageBinding
import kotlinx.coroutines.launch
import timber.log.Timber

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [HomePageFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HomePageFragment : Fragment() {

    private lateinit var binding:FragmentHomePageBinding
    private lateinit var appDao:AppDao
    private lateinit var lebronFact:String
    private lateinit var sportsCategory:String
    private lateinit var flatFact:String

    // TODO: Rename and change types of parameters
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
        inflater: LayoutInflater, container: ViewGroup?,
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
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            HomePageFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    //TODO Figure out Databinding in Fragment and make sure the RV adapter gets attached
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Timber.plant(Timber.DebugTree())

        appDao = AppDatabase.getInstance(requireContext()).appDao

        val testFacts = mutableListOf(
            FactModel( "The World Is Flat", true),
            FactModel( "Lebron James is a goat", true),
            FactModel( "Ryan Gosling to HOT", true),
            FactModel( "Lebron James is 3 Feet and 2 Inches", true),
            FactModel( "Dabbington City Shall We?", true)
        )

        val testCategories = mutableListOf(
            CategoryModel("Sports"),
            CategoryModel("Science"),
            CategoryModel("Entertainment")
        )

        val factCategoryRelations = mutableListOf(
            CategoryModelCrossRef("The World Is Flat", "Science"),
            CategoryModelCrossRef("Lebron James is a goat", "Sports"),
            CategoryModelCrossRef("Lebron James is a goat", "Entertainment"),
            CategoryModelCrossRef("Ryan Gosling to HOT", "Entertainment"),
            CategoryModelCrossRef("Lebron James is 3 Feet and 2 Inches", "Sports"),
            CategoryModelCrossRef("Dabbington City Shall We", "Entertainment")
        )

        lifecycleScope.launch {
            testFacts.forEach{appDao.insertFact(it)}
            testCategories.forEach{appDao.insertCategory(it)}
            factCategoryRelations.forEach{appDao.insertCategoryModelCrossRef(it)}

            lebronFact = appDao.getCategoriesOfFacts("Lebron James is a goat").toString()
            sportsCategory = appDao.getFactsOfCategories("Sports").toString()
            flatFact = appDao.getCategoriesOfFacts("The World Is Flat").toString()

            Timber.e("LebronFact: $lebronFact !!! sportsCategory: $sportsCategory !!! flatFact: $flatFact")
//            Toast.makeText(requireContext(), "LebronFact: $lebronFact !!! sportsCategory: $sportsCategory !!! flatFact: $flatFact", Toast.LENGTH_LONG).show()
        }

        val adapter = FactsAdapter(testFacts)

        binding.rvFactsHomePage.adapter = adapter
        binding.rvFactsHomePage.layoutManager = LinearLayoutManager(requireContext())

    }
}