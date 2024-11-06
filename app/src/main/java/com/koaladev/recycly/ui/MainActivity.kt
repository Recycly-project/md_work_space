package com.koaladev.recycly.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.koaladev.recycly.R
import com.koaladev.recycly.databinding.ActivityMainBinding
import com.koaladev.recycly.fragment.HistoryFragment
import com.koaladev.recycly.fragment.HomeFragment
import com.koaladev.recycly.fragment.PointFragment
import com.koaladev.recycly.fragment.UserProfileFragment

class MainActivity : AppCompatActivity() {

    // initialize view binding
    private lateinit var binding: ActivityMainBinding

    // initialize bottom navigation bar and fragment manager
    private lateinit var bottomNavBar: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // set up bottom navigation bar and fragment manager
        bottomNavBar = binding.bottomNavigation
        bottomNavBar.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.bottom_home -> {
                    setupBottomNavBar(HomeFragment())
                    true
                }
                R.id.bottom_history -> {
                    setupBottomNavBar(HistoryFragment())
                    true
                }
                R.id.bottom_point -> {
                    setupBottomNavBar(PointFragment())
                    true
                }
                R.id.bottom_user_profile -> {
                    setupBottomNavBar(UserProfileFragment())
                    true
                }
                else -> false
            }
        }
        setupBottomNavBar(HomeFragment())
    }

    private fun setupBottomNavBar(fragment: Fragment) {
        supportFragmentManager.beginTransaction().replace(R.id.main, fragment).commit()
    }
}

