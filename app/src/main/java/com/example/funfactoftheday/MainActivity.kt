package com.example.funfactoftheday

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.example.funfactoftheday.categories.CategoriesFragment
import com.example.funfactoftheday.databinding.ActivityMainBinding
import com.example.funfactoftheday.favoritefacts.FavoriteFactsFragment
import com.example.funfactoftheday.homepage.HomePageFragment

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val homePageFragment = HomePageFragment()
        val categoryFragment = CategoriesFragment()
        val favoriteFactsFragment = FavoriteFactsFragment()

        setCurrentFragment(homePageFragment)

        binding.bottomNavigationView.setOnItemSelectedListener{
            when(it.itemId){
                R.id.miHome ->setCurrentFragment(homePageFragment)
                R.id.miFavorites -> setCurrentFragment(favoriteFactsFragment)
                R.id.miCategories -> setCurrentFragment(categoryFragment)
            }
            true
        }

    }

    private fun setCurrentFragment(fragment: Fragment) =
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.fragmentContainer, fragment)
            commit()
        }

}