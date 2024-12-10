package com.koaladev.recycly.ui.activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.koaladev.recycly.data.pref.UserModel
import com.koaladev.recycly.data.pref.UserPreference
import com.koaladev.recycly.data.pref.dataStore
import com.koaladev.recycly.data.repository.RecyclyRepository
import com.koaladev.recycly.data.retrofit.ApiConfigAuth
import com.koaladev.recycly.databinding.ActivityLoginBinding
import com.koaladev.recycly.ui.viewmodel.LoginViewModel
import com.koaladev.recycly.ui.viewmodel.ViewModelFactory

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val viewModel by viewModels<LoginViewModel> {
        ViewModelFactory.getInstance(this)
    }
    private lateinit var userPreference: UserPreference
    private lateinit var repository: RecyclyRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)


        userPreference = UserPreference.getInstance(dataStore)
        repository = RecyclyRepository.getInstance(
            UserPreference.getInstance(dataStore),
            ApiConfigAuth.getApiService()
        )

        binding.btnRegister.setOnClickListener{
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        setupLogin()
    }

    private fun setupLogin() {
        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            when {
                email.isEmpty() -> {
                    Toast.makeText(this, "Email should not be empty!", Toast.LENGTH_SHORT).show()
                    binding.btnLogin.doResult(false)
                }
                password.isEmpty() -> {
                    Toast.makeText(this, "Password should not be empty!", Toast.LENGTH_SHORT).show()
                    binding.btnLogin.doResult(false)
                }
                else -> {
                    viewModel.login(email, password) { isSuccess, token, id, email, name ->
                        binding.btnLogin.startLoading()

                        if (isSuccess) {
                            binding.btnLogin.doResult(true)
                            Handler(Looper.getMainLooper()).postDelayed({
                                viewModel.saveSession(UserModel(id, token, name, email, true))
                                val intent = Intent(this, MainActivity::class.java)
                                Toast.makeText(this, "Success", Toast.LENGTH_SHORT).show()
                                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                startActivity(intent)
                                finish()
                            }, 900)
                        } else {
                            binding.btnLogin.doResult(false)
                            AlertDialog.Builder(this)
                                .setTitle("Ooops!")
                                .setMessage("Login Failed \nPlease check your email and password!")
                                .setPositiveButton("OK", null)
                                .create()
                                .show()
                        }
                    }
                }
            }
        }
    }
}