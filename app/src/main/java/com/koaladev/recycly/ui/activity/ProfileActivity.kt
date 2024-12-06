package com.koaladev.recycly.ui.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.appbar.MaterialToolbar
import com.koaladev.recycly.R
import com.koaladev.recycly.data.repository.RecyclyRepository
import com.koaladev.recycly.data.repository.SessionPreferences
import com.koaladev.recycly.data.retrofit.ApiConfigAuth
import com.koaladev.recycly.databinding.ActivityProfileBinding
import com.koaladev.recycly.helper.ToolbarTitleUpdater
import com.koaladev.recycly.ui.viewmodel.LoginViewModel
import com.koaladev.recycly.ui.viewmodel.LoginViewModelFactory

class ProfileActivity : AppCompatActivity(), ToolbarTitleUpdater {

    private lateinit var binding: ActivityProfileBinding
    private lateinit var toolbar: MaterialToolbar
    private lateinit var viewModel: LoginViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val apiService = ApiConfigAuth.getApiService()
        val repository = RecyclyRepository.getInstance(apiService)
        val sessionPreferences = SessionPreferences.getInstance(applicationContext)
        val factory = LoginViewModelFactory(repository, sessionPreferences)
        viewModel = ViewModelProvider(this, factory)[LoginViewModel::class.java]

        toolbar = binding.toolbar
        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        updateToolbarTitle(getString(R.string.bottom_user_profile))

        setupEdgeToEdge()

        binding.logoutButton.setOnClickListener {
            viewModel.logout()
            Toast.makeText(this, "Logout berhasil", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }

    override fun updateToolbarTitle(title: String) {
        toolbar.title = title
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun setupEdgeToEdge() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, windowInsets ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(insets.left, insets.top, insets.right, insets.bottom)
            WindowInsetsCompat.CONSUMED
        }
    }
}