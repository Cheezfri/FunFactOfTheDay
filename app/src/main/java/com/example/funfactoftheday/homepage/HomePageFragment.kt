package com.example.funfactoftheday.homepage

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Rect
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.funfactoftheday.AddAFactAndCategoryFragment
import com.example.funfactoftheday.FactApplication
import com.example.funfactoftheday.FactsAdapter
import com.example.funfactoftheday.R
import com.example.funfactoftheday.database.models.FactModel
import com.example.funfactoftheday.databinding.FactBinding
import com.example.funfactoftheday.databinding.FragmentHomePageBinding
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch
import timber.log.Timber
import kotlin.math.abs
import kotlin.math.roundToInt
import java.util.*

// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"
/**
 * A simple [Fragment] subclass.
 * Use the [HomePageFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HomePageFragment : Fragment(), FactsAdapter.OnItemClickListener, SearchView.OnQueryTextListener {

    private lateinit var dragHelper: ItemTouchHelper
    private lateinit var swipeHelper: ItemTouchHelper
    private lateinit var binding:FragmentHomePageBinding
    private lateinit var adapter: FactsAdapter
    private var tempFacts:MutableList<FactModel> = mutableListOf()

    private val Int.dp
        get() = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            toFloat(), resources.displayMetrics
        ).roundToInt()

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

    fun startDragging(holder: RecyclerView.ViewHolder) {
        dragHelper.startDrag(holder)
    }

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

    override fun onFavoriteClick(itemBinding: FactBinding) {
        val fact = FactModel(itemBinding.tvFactName.text as String, itemBinding.cbFavorite.isChecked)
        tempFacts.add(fact)
//        homePageViewModel.viewModelScope.launch {
//            homePageViewModel.insertFact(fact)
//        }
    }

//    override fun onTextClick(itemBinding: FactBinding, holder: FactsAdapter.FactsViewHolder) {
//        startDragging(holder)
//    }

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

        val displayMetrics: DisplayMetrics = resources.displayMetrics
        val height = (displayMetrics.heightPixels / displayMetrics.density).toInt().dp
        val width = (displayMetrics.widthPixels / displayMetrics.density).toInt().dp

        val deleteIcon = resources.getDrawable(R.drawable.ic_baseline_delete_24, null)

        val deleteColor = resources.getColor(android.R.color.holo_red_light)
        val archiveColor = resources.getColor(android.R.color.holo_green_light)


        swipeHelper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(
            0,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ) = true

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                Snackbar.make(
                    getView()!!,
                    if (direction == ItemTouchHelper.RIGHT) "Deleted" else "Archived",
                    Snackbar.LENGTH_SHORT
                ).show()
            }

            override fun onChildDraw(
                canvas: Canvas,
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                dX: Float,
                dY: Float,
                actionState: Int,
                isCurrentlyActive: Boolean
            ) {
                //1. Background color based upon direction swiped
//                when {
//                    abs(dX) < width / 3 -> canvas.drawColor(Color.GRAY)
//                    dX > width / 3 -> canvas.drawColor(deleteColor)
//                    else -> canvas.drawColor(archiveColor)
//                }

                //2. Printing the icons
                val textMargin =  resources.getDimension(R.dimen.text_margin).roundToInt()
                deleteIcon.bounds = Rect(
                    textMargin,
                    viewHolder.itemView.top + textMargin + 8.dp,
                    textMargin + deleteIcon.intrinsicWidth,
                    viewHolder.itemView.top + deleteIcon.intrinsicHeight
                            + textMargin + 8.dp
                )
                deleteIcon.bounds = Rect(
                    width - textMargin - deleteIcon.intrinsicWidth,
                    viewHolder.itemView.top + textMargin + 8.dp,
                    width - textMargin,
                    viewHolder.itemView.top + deleteIcon.intrinsicHeight
                            + textMargin + 8.dp
                )

                //3. Drawing icon based upon direction swiped
                if (dX > 0) deleteIcon.draw(canvas) else deleteIcon.draw(canvas)

                super.onChildDraw(
                    canvas,
                    recyclerView,
                    viewHolder,
                    dX,
                    dY,
                    actionState,
                    isCurrentlyActive
                )
            }
        })

        dragHelper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP or ItemTouchHelper.DOWN, 0
        ) {

            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {

                viewHolder.itemView.elevation = 16F

                val from = viewHolder.adapterPosition
                val to = target.adapterPosition

//                Collections.swap(list, from, to)
                adapter.notifyItemMoved(from, to)
                return true
            }

            override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
                super.onSelectedChanged(viewHolder, actionState)
                viewHolder?.itemView?.elevation = 0F
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int){
                //TODO: Make the onswipe open a popup asking are you sure you want to delete
                val pos = viewHolder.adapterPosition
                (binding.rvFactsHomePage.adapter as RecyclerView.Adapter).notifyItemRemoved(pos)
                adapter.notifyItemRemoved(pos)
            }

        })

        swipeHelper.attachToRecyclerView(binding.rvFactsHomePage)
        dragHelper.attachToRecyclerView(binding.rvFactsHomePage)

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
