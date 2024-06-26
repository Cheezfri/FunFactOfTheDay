package com.example.funfactoftheday.settings

import android.Manifest
import android.Manifest.permission.POST_NOTIFICATIONS
import android.app.Activity
import android.app.AlarmManager
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.viewModelScope
import com.example.funfactoftheday.DataBinderMapperImpl
import com.example.funfactoftheday.FactApplication
import com.example.funfactoftheday.R
import com.example.funfactoftheday.database.models.CategoriesWithFacts
import com.example.funfactoftheday.databinding.FragmentSettingsBinding
import com.example.funfactoftheday.notifications.AlarmItem
import com.example.funfactoftheday.notifications.AndroidAlarmScheduler
import com.example.funfactoftheday.notifications.FunFactNotificationService
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch
import timber.log.Timber
import java.time.LocalDateTime

// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [SettingsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SettingsFragment : Fragment() {

    private var param1: String? = null
    private var param2: String? = null
    private lateinit var  binding:FragmentSettingsBinding

    private val settingsPageViewModel:SettingsPageViewModel by viewModels {
        SettingsPageViewModel.SettingsPageViewModelFactory((context?.applicationContext as FactApplication).repository)
    }

    private val permissionLauncherSingle = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ){ isGranted->
        Timber.e("isGranted: $isGranted")
        if(!isGranted) {
            if(ContextCompat.checkSelfPermission(requireContext(), POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED){
                if(!ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), POST_NOTIFICATIONS)){
                    Timber.e("Permission Permanently denied")
                    //Navigate to some shit
                    openAppSettings()
                }
            } else {
                Toast.makeText(requireContext(), "Permission Denied: Cannot Send Fun Facts", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun Fragment.openAppSettings(){
        val builder: AlertDialog.Builder = AlertDialog.Builder(context)
        builder
            .setMessage("Please click confirm and allow notifications.")
            .setTitle("Notifications are disabled")
            .setPositiveButton("Confirm"){ _, _ ->
                activity?.run{
                    Intent(
                        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                        Uri.fromParts("package", packageName, null)
                    ).also(::startActivity)
                }
            }
            .setNegativeButton("Cancel"){_,_->
                Toast.makeText(requireContext(), "Permission Denied: Cannot Send Fun Facts", Toast.LENGTH_LONG).show()
            }
            .setCancelable(true)
        val dialog: AlertDialog = builder.create()
        dialog.show()
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
        binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment SettingsFragment.
         */
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            SettingsFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Timber.e("onViewCreated")

        val activity = requireActivity() as AppCompatActivity
        activity.supportActionBar?.title = "Settings"

//        val service = FunFactNotificationService(requireContext())
//        binding.button.setOnClickListener {
//            service.showNotification(factName = "first Notification!!!!!!!!!!!")
//        }

        val sharedPref = requireContext().getSharedPreferences("SettingsSpinnerSharedPreferences", Context.MODE_PRIVATE)
        val editor = sharedPref.edit()

        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.what_kind_notifications_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner.
            binding.spinnerWhatKindFunFactsSend.adapter = adapter
        }

        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.how_often_notifications_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner.
            binding.spinnerHowOftenNotifications.adapter = adapter
        }

        val whatKind = sharedPref.getInt("what_kind", 0)
        val howOften = sharedPref.getInt("how_often", 0)
        val isEnabled = sharedPref.getBoolean("enable_notifications", false)
        Timber.e("enable: $isEnabled !! WhatKindAfter: $whatKind !! howOften: $howOften")
        binding.spinnerHowOftenNotifications.setSelection(howOften)
        binding.spinnerWhatKindFunFactsSend.setSelection(whatKind)
        binding.switchEnableNotifications.isChecked = isEnabled

        var isFavoriteFactsEmpty = false
        var isFavoriteCategoriesEmpty = false
        settingsPageViewModel.favoriteFacts.observe(viewLifecycleOwner){ facts ->
            isFavoriteFactsEmpty = facts.isNullOrEmpty()
        }
        settingsPageViewModel.viewModelScope.launch {
            settingsPageViewModel.getFactsOfFavoriteCategories().observe(viewLifecycleOwner){ it ->
                isFavoriteCategoriesEmpty = it.isEmpty()
            }
        }

        binding.switchEnableNotifications.setOnClickListener{
            if(binding.switchEnableNotifications.isChecked){
                permissionLauncherSingle.launch(POST_NOTIFICATIONS)

            }
            if(binding.switchEnableNotifications.isChecked &&
                (binding.spinnerWhatKindFunFactsSend.selectedItem.toString() == "Favorite Facts Only") &&
                isFavoriteFactsEmpty){
                Toast.makeText(requireContext(),
                    "Favorite Facts are empty! Please add Favorite Facts or switch to All Facts!",
                    Toast.LENGTH_LONG).show()
            }
            if(binding.switchEnableNotifications.isChecked &&
                    binding.spinnerWhatKindFunFactsSend.selectedItem.toString() == "Favorite Categories Only" &&
                        isFavoriteCategoriesEmpty
                    ){
                Toast.makeText(requireContext(), "Favorite Categories are empty! Please add Favorite Categories or switch to All Facts!", Toast.LENGTH_LONG).show()
            }
            editor.apply{
                putBoolean("enable_notifications", binding.switchEnableNotifications.isChecked)
                apply()
            }
        }

        binding.spinnerWhatKindFunFactsSend.onItemSelectedListener = object:AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                if(p0!!.selectedItem.toString() == "Favorite Facts Only" &&
                    binding.switchEnableNotifications.isChecked && isFavoriteFactsEmpty){
                    Toast.makeText(requireContext(), "Favorite Facts are empty! Please add Favorite Facts or switch to All Facts!", Toast.LENGTH_LONG).show()
                }
                if(p0!!.selectedItem.toString() == "Favorite Categories Only" &&
                    binding.switchEnableNotifications.isChecked && isFavoriteCategoriesEmpty){
                    Toast.makeText(requireContext(), "Favorite Categories are empty! Please add Favorite Categories or switch to All Facts!", Toast.LENGTH_LONG).show()
                }
                editor.apply{
                    putInt("what_kind", p2)
                    apply()
                }
            }
            override fun onNothingSelected(p0: AdapterView<*>?) {
            }
        }

        binding.spinnerHowOftenNotifications.onItemSelectedListener = object: AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                editor.apply{
                    putInt("how_often", p2)
                    apply()
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
            }
        }

    }


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onPause() {
        super.onPause()
        Timber.e("onPause")
        val howOften = binding.spinnerHowOftenNotifications.selectedItem.toString()
        val whatKind = binding.spinnerWhatKindFunFactsSend.selectedItem.toString()
        val enabled = binding.switchEnableNotifications.isChecked
        var alarmItem: AlarmItem? = null
        val factsSharedPreferences = requireContext().getSharedPreferences("FactsSharedPreferences",
            AppCompatActivity.MODE_PRIVATE
        )
        val editor = factsSharedPreferences.edit()

        val interval = when (howOften){
            "Twice A Day" -> AlarmManager.INTERVAL_HALF_DAY
            "Daily" -> AlarmManager.INTERVAL_DAY
            "Twice a Week" -> AlarmManager.INTERVAL_DAY * 3
            "Weekly" -> AlarmManager.INTERVAL_DAY * 6
            else -> AlarmManager.INTERVAL_DAY
        }
        val scheduler = AndroidAlarmScheduler(requireContext(), interval)

        if(enabled){
            if(whatKind == "All Facts"){
                settingsPageViewModel.allFacts.observe(viewLifecycleOwner){ facts->
                    val transformedFacts:MutableList<String> = mutableListOf()
                    if(!facts.isNullOrEmpty()){
                        for(fact in facts){
                            transformedFacts.add(fact.factName)
                        }
                    }
                    if (!transformedFacts.isNullOrEmpty()) {
                        alarmItem = AlarmItem(
                            time = LocalDateTime.now().plusSeconds(5.toLong()),
                            messages = transformedFacts.toTypedArray())
                        alarmItem?.let(scheduler::schedule)
                        editor.apply{
                            putStringSet("TestStringSet", transformedFacts.toSet())
                            apply()
                            Timber.e("Added all facts")
                        }
                    }
                }
            }

            if(whatKind == "Favorite Facts Only"){
                settingsPageViewModel.favoriteFacts.observe(viewLifecycleOwner){ facts->
                    val transformedFacts:MutableList<String> = mutableListOf()
                    if(!facts.isNullOrEmpty()){
                        for(fact in facts){
                            transformedFacts.add(fact.factName)
                        }
                    }
                    if (!transformedFacts.isNullOrEmpty()) {
                        alarmItem = AlarmItem(
                            time = LocalDateTime.now().plusSeconds(5.toLong()),
                            messages = transformedFacts.toTypedArray())
                        alarmItem?.let(scheduler::schedule)
                        editor.apply{
                            putStringSet("TestStringSet", transformedFacts.toSet())
                            apply()
                            Timber.e("Added Favorite Facts only")
                        }
                    }
                }
            }

            if(whatKind == "Favorite Categories Only"){
                settingsPageViewModel.viewModelScope.launch {
                    val transformedFacts:MutableList<String> = mutableListOf()
                    settingsPageViewModel.getFactsOfFavoriteCategories()
                    settingsPageViewModel.getFactsOfFavoriteCategories().observe(viewLifecycleOwner){ items ->
                        val list: List<CategoriesWithFacts> = items
                        for(item in list){
                            Timber.e("Category: ${item.category.categoryName}")
                            for(fact in item.facts){
                                if(!transformedFacts.contains(fact.factName)){
                                    transformedFacts.add(fact.factName)
                                    Timber.e("Fact: ${fact.factName}")
                                }
                            }
                        }
                        if (!transformedFacts.isNullOrEmpty()) {
                            for(fact in transformedFacts){
                                Timber.e("TF: $fact")
                            }
                            alarmItem = AlarmItem(
                                time = LocalDateTime.now().plusSeconds(5.toLong()),
                                messages = transformedFacts.toTypedArray())
                            alarmItem?.let(scheduler::schedule)
                            editor.apply{
                                putStringSet("TestStringSet", transformedFacts.toSet())
                                apply()
                                Timber.e("Added Favorite Categories only")
                            }
                        }
                    }
                }
            }
        }
    }

}