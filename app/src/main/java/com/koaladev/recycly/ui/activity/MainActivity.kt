package com.koaladev.recycly.ui.activity

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.appbar.MaterialToolbar
import com.koaladev.recycly.R
import com.koaladev.recycly.data.repository.RecyclyRepository
import com.koaladev.recycly.data.repository.SessionPreferences
import com.koaladev.recycly.data.retrofit.ApiConfigAuth
import com.koaladev.recycly.databinding.ActivityMainBinding
import com.koaladev.recycly.helper.ToolbarTitleUpdater
import com.koaladev.recycly.ui.viewmodel.LoginViewModel
import com.koaladev.recycly.ui.viewmodel.LoginViewModelFactory

class MainActivity : AppCompatActivity(), ToolbarTitleUpdater {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private lateinit var toolbar: MaterialToolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        toolbar = binding.toolbar
        setSupportActionBar(toolbar)

        setupEdgeToEdge()
        setupNavigation()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.userProfileFragment -> {
                navController.navigate(R.id.profileActivity)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun updateToolbarTitle(title: String) {
        toolbar.title = title
    }

    private fun setupEdgeToEdge() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { _, windowInsets ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())

            binding.root.setPadding(insets.left, insets.top, insets.right, 0)
            binding.bottomNavbar.setPadding(0, 0, 0, insets.bottom)

            WindowInsetsCompat.CONSUMED
        }
    }

    private fun setupNavigation() {
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController
        binding.bottomNavbar.setupWithNavController(navController)

        navController.addOnDestinationChangedListener{ _, destination, _ ->
            updateToolbarTitleDestination(destination)
        }
        // soon handle reselection to prevent unnecessary navigation
        binding.bottomNavbar.setOnItemReselectedListener { /* Do nothing */ }
    }

    private fun updateToolbarTitleDestination(destination: NavDestination) {
        val title = when (destination.id) {
            R.id.homeFragment -> getString(R.string.bottom_home_title)
            R.id.historyFragment -> getString(R.string.bottom_history)
            R.id.pointFragment -> getString(R.string.bottom_point)
            else -> getString(R.string.app_name)
        }
        updateToolbarTitle(title)
    }

}

