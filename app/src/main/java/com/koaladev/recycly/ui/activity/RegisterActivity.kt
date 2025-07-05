package com.koaladev.recycly.ui.activity

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.koaladev.recycly.data.pref.UserPreference
import com.koaladev.recycly.data.pref.dataStore
import com.koaladev.recycly.data.repository.RecyclyRepository
import com.koaladev.recycly.data.retrofit.ApiConfigAuth
import com.koaladev.recycly.databinding.ActivityRegisterBinding
import com.koaladev.recycly.ui.viewmodel.RegisterViewModel
import com.koaladev.recycly.ui.viewmodel.RegisterViewModelFactory
import java.io.File

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var viewModel: RegisterViewModel
    private lateinit var repository: RecyclyRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Setup Toolbar
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        repository = RecyclyRepository.getInstance(
            UserPreference.getInstance(dataStore),
            ApiConfigAuth.getApiService()
        )
        viewModel = ViewModelProvider(this, RegisterViewModelFactory(repository))[RegisterViewModel::class.java]

        // Mengatur listener hanya untuk tombol register
        binding.btnRegister.setOnClickListener { registerUser() }
    }
    // Fungsi ini akan dipanggil saat tombol kembali di toolbar ditekan
    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }

    private fun registerUser() {
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()
        val fullName = binding.etFullname.text.toString().trim()
        val address = binding.etAddress.text.toString().trim()

        if (email.isEmpty() || password.isEmpty() || fullName.isEmpty() || address.isEmpty()) {
            Toast.makeText(this, "Harap isi semua kolom", Toast.LENGTH_SHORT).show()
            return
        }

        // TODO: Backend Anda saat ini membutuhkan file KTP.
        // Untuk sementara, kita akan membuat file dummy agar tidak error.
        // Nanti, ini harus disesuaikan dengan alur bisnis Anda (misal: KTP opsional di backend).
        val dummyFile = File.createTempFile("dummy_ktp", ".jpg", cacheDir)

        viewModel.register(email, password, fullName, address, dummyFile)
        viewModel.registerResult.observe(this) { result ->
            when (result.status) {
                "success" -> {
                    AlertDialog.Builder(this)
                        .setTitle("Berhasil!")
                        .setMessage("Akun untuk $email berhasil dibuat. Silakan login.")
                        .setPositiveButton("OK") { _, _ ->
                            finish() // Kembali ke halaman login
                        }
                        .setCancelable(false)
                        .create()
                        .show()
                }
                "fail" -> {
                    AlertDialog.Builder(this)
                        .setTitle("Gagal")
                        .setMessage("Registrasi gagal: ${result.message}")
                        .setPositiveButton("OK", null)
                        .create()
                        .show()
                }
                else -> {
                    Toast.makeText(this, "Terjadi kesalahan tidak diketahui", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}