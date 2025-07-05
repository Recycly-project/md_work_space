package com.koaladev.recycly.ui.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.appbar.MaterialToolbar
import com.koaladev.recycly.R
import com.koaladev.recycly.data.retrofit.ApiConfigAuth
import com.koaladev.recycly.databinding.ActivityProfileBinding
import com.koaladev.recycly.helper.ToolbarTitleUpdater
import com.koaladev.recycly.ui.viewmodel.RecyclyViewModel
import com.koaladev.recycly.ui.viewmodel.ViewModelFactory

class ProfileActivity : AppCompatActivity(), ToolbarTitleUpdater {

    private lateinit var binding: ActivityProfileBinding
    private lateinit var toolbar: MaterialToolbar
    private val viewModel: RecyclyViewModel by viewModels{
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        toolbar = binding.toolbar
        setSupportActionBar(toolbar)

        // Mengaktifkan tombol kembali
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        updateToolbarTitle(getString(R.string.bottom_user_profile))

        setupEdgeToEdge()

        binding.logoutButton.setOnClickListener {
            viewModel.logout()
        }

        viewModel.getSession().observe(this) { user ->
            if (!user.isLogin) {
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            } else {
                binding.nameTextView.text = user.name
                binding.emailTextView.text = user.email
            }
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