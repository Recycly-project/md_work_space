package com.koaladev.recycly.ui.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.koaladev.recycly.data.repository.RecyclyRepository
import com.koaladev.recycly.data.repository.SessionPreferences
import com.koaladev.recycly.data.retrofit.ApiConfigAuth
import com.koaladev.recycly.databinding.ActivityLoginBinding
import com.koaladev.recycly.ui.viewmodel.LoginViewModel
import com.koaladev.recycly.ui.viewmodel.LoginViewModelFactory

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var viewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val apiService = ApiConfigAuth.getApiService()
        val repository = RecyclyRepository.getInstance(apiService)
        val sessionPreferences = SessionPreferences.getInstance(applicationContext)
        val factory = LoginViewModelFactory(repository, sessionPreferences)
        viewModel = ViewModelProvider(this, factory)[LoginViewModel::class.java]

        viewModel.isLoggedIn.observe(this) { isLoggedIn ->
            if (isLoggedIn) {
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
        }

        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()
            if (email.isNotEmpty() && password.isNotEmpty()) {
                viewModel.login(email, password)
            } else {
                Toast.makeText(this, "Email dan password harus diisi", Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.loginResult.observe(this) { result ->
            result.onSuccess { loginResponse ->
                Toast.makeText(this, loginResponse.message, Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }.onFailure { exception ->
                Toast.makeText(this, exception.message, Toast.LENGTH_SHORT).show()
            }
        }
    }
}