package com.example.funfactoftheday.onboarding

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentPagerAdapter
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.example.funfactoftheday.R
import com.example.funfactoftheday.databinding.FragmentOnboardingBinding
import timber.log.Timber

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [OnboardingFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class OnboardingFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var binding:FragmentOnboardingBinding

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
        binding = FragmentOnboardingBinding.inflate(inflater, container, false)
        return binding.root
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment OnboardingFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            OnboardingFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as AppCompatActivity).supportActionBar?.hide()
        binding.btnPrev.visibility = View.INVISIBLE
        val images = arrayListOf(R.drawable.page1, R.drawable.page2, R.drawable.page3)
        val viewPagerItems = arrayListOf<ViewPagerItem>()
        for(image in images){
            viewPagerItems.add(ViewPagerItem(image))
        }
        binding.viewPager.adapter = ViewPagerAdapter(viewPagerItems)
        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback(){
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                if(position == 0){
                    binding.btnPrev.visibility = View.INVISIBLE
                    binding.btnNext.text = "Next"
                }
                if(position == 1){
                    binding.btnPrev.visibility = View.VISIBLE
                    binding.btnNext.text = "Next"
                }
                if(position == 2){
                    binding.btnPrev.visibility = View.VISIBLE
                    binding.btnNext.text = "Finish"
                    Toast.makeText(requireContext(),
                        "Please click the Finish Button to finish the Tutorial",
                        Toast.LENGTH_LONG).show()
                }
            }
        })
        binding.btnPrev.setOnClickListener{
            binding.viewPager.currentItem = binding.viewPager.currentItem - 1
        }
        binding.btnNext.setOnClickListener{
            if(binding.viewPager.currentItem == 2){
                finishOnboarding()
            }
            binding.viewPager.currentItem = binding.viewPager.currentItem + 1
        }
    }

    private fun Fragment.finishOnboarding(){
        val builder: AlertDialog.Builder = AlertDialog.Builder(context)
        builder
            .setMessage("Please click confirm to finish the tutorial.")
            .setTitle("Go to App?")
            .setPositiveButton("Confirm"){ _, _ ->
                val sharedPref = requireContext().getSharedPreferences("Onboard", Context.MODE_PRIVATE)
                val editor = sharedPref.edit()
                editor.apply{
                    putBoolean("Onboard", true)
                    apply()
                }
                findNavController().navigate(R.id.homePageFragment)
            }
            .setNegativeButton("Cancel"){_,_->
            }
            .setCancelable(true)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        (activity as AppCompatActivity).supportActionBar?.show()
    }
}