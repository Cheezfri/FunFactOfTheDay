package com.example.funfactoftheday.categories

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.funfactoftheday.CategoryAdapter
import com.example.funfactoftheday.FactApplication
import com.example.funfactoftheday.FactsAdapter
import com.example.funfactoftheday.R
import com.example.funfactoftheday.database.AppDao
import com.example.funfactoftheday.database.AppDatabase
import com.example.funfactoftheday.database.models.CategoryModel
import com.example.funfactoftheday.databinding.CategoryBinding
import com.example.funfactoftheday.databinding.FragmentCategoriesBinding
import kotlinx.coroutines.launch
import timber.log.Timber

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [CategoriesFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class CategoriesFragment : Fragment(), CategoryAdapter.OnItemClickListener  {

    private lateinit var binding:FragmentCategoriesBinding

    private val categoriesViewModel: CategoriesViewModel by viewModels {
        CategoriesViewModel.CategoriesViewModelFactory((context?.applicationContext as FactApplication).repository)
    }

    override fun onFavoriteClick(itemBinding: CategoryBinding){
        itemBinding.cbFavorite
    }

    override fun onTextClick(itemBinding: CategoryBinding){
        val categoryModel = CategoryModel(itemBinding.tvCategoryName.text.toString(), itemBinding.cbFavorite.isChecked)
        val bundle = Bundle()
        bundle.putParcelable("categoryModel", categoryModel)
        setFragmentResult("requestKey", bundleOf("bundleKey" to bundle))
        findNavController().navigate(R.id.categoryFragment)
    }


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
        // TODO: Rename and change types and number of parameters
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

//        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainer) as NavHostFragment
//        val navController = navHostFragment.navController

        val adapter = CategoryAdapter(this, this)
        binding.rvCategoriesPage.adapter = adapter
        binding.rvCategoriesPage.layoutManager = LinearLayoutManager(requireContext())

        categoriesViewModel.allCategories.observe(viewLifecycleOwner){ categories ->
            categories.let{
                adapter.submitList(it as MutableList<CategoryModel>?)
            }
        }

    }

}