package com.koaladev.recycly.ui.activity

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModelProvider
import com.koaladev.recycly.data.pref.UserPreference
import com.koaladev.recycly.data.pref.dataStore
import com.koaladev.recycly.data.repository.RecyclyRepository
import com.koaladev.recycly.data.retrofit.ApiConfig
import com.koaladev.recycly.data.retrofit.ApiConfigAuth
import com.koaladev.recycly.data.retrofit.ApiService
import com.koaladev.recycly.databinding.ActivityRegisterBinding
import com.koaladev.recycly.ui.viewmodel.RegisterViewModel
import com.koaladev.recycly.ui.viewmodel.RegisterViewModelFactory
import com.koaladev.recycly.utils.createCustomTempFile
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var viewModel: RegisterViewModel
    private var currentPhotoPath: String? = null
    private lateinit var repository: RecyclyRepository


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)


        repository = RecyclyRepository.getInstance(
            UserPreference.getInstance(dataStore),
            ApiConfigAuth.getApiService()
        )
        viewModel = ViewModelProvider(this, RegisterViewModelFactory(repository))[RegisterViewModel::class.java]


        binding.btnCaptureKtp.setOnClickListener { captureKtp() }
        binding.btnRegister.setOnClickListener { registerUser() }

    }

    private fun captureKtp() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), REQUEST_CAMERA_PERMISSION)
        } else {
            launchCamera()
        }
    }

    private fun launchCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.resolveActivity(packageManager)?.let {
            val photoFile: File? = try {
                createCustomTempFile(applicationContext)
            } catch (ex: Exception) {
                null
            }
            photoFile?.also {
                val photoURI: Uri = FileProvider.getUriForFile(
                    this,
                    "com.koaladev.recycly.fileprovider",
                    it
                )
                currentPhotoPath = it.absolutePath
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                startActivityForResult(intent, REQUEST_IMAGE_CAPTURE)
            }
        }
    }

    private fun registerUser() {
        val email = binding.etEmail.text.toString()
        val password = binding.etPassword.text.toString()
        val fullName = binding.etFullname.text.toString()
        val address = binding.etAddress.text.toString()

        if (email.isEmpty() || password.isEmpty() || fullName.isEmpty() || address.isEmpty() || currentPhotoPath == null) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            return
        }

        val ktpFile = File(currentPhotoPath!!)
        val compressedKtpFile = compressImage(ktpFile)
        viewModel.register(email, password, fullName, address, compressedKtpFile)
        viewModel.registerResult.observe(this) { result ->
            when (result.status) {
                "success" -> {
                    AlertDialog.Builder(this)
                        .setTitle("Yeah!")
                        .setMessage("Account for $email successfully created, please Login now!")
                        .setPositiveButton("OK") { _, _ ->
                            finish()
                        }
                        .create()
                        .show()
                }
                "fail" -> {
                    AlertDialog.Builder(this)
                        .setTitle("Oops!")
                        .setMessage("Registration failed: ${result.message}")
                        .setPositiveButton("OK", null)
                        .create()
                        .show()
                }
                else -> {
                    Toast.makeText(this, "Unknown error occurred", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun compressImage(file: File): File {
        val maxFileSize = 2 * 1024 * 1024 // 2MB in bytes

        val bitmap = BitmapFactory.decodeFile(file.path)
        var compressQuality = 100
        var streamLength: Int
        val bmpStream = ByteArrayOutputStream()

        do {
            bmpStream.reset()
            bitmap.compress(Bitmap.CompressFormat.JPEG, compressQuality, bmpStream)
            val bmpPicByteArray = bmpStream.toByteArray()
            streamLength = bmpPicByteArray.size
            compressQuality -= 5
        } while (streamLength > maxFileSize && compressQuality > 5)

        FileOutputStream(file).use { fos ->
            fos.write(bmpStream.toByteArray())
        }

        return file
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                launchCamera()
            } else {
                Toast.makeText(this, "Camera permission is required to capture KTP", Toast.LENGTH_SHORT).show()
            }
        }
    }

    @Deprecated("This method has been deprecated in favor of using the Activity Result API\n      which brings increased type safety via an {@link ActivityResultContract} and the prebuilt\n      contracts for common intents available in\n      {@link androidx.activity.result.contract.ActivityResultContracts}, provides hooks for\n      testing, and allow receiving results in separate, testable classes independent from your\n      activity. Use\n      {@link #registerForActivityResult(ActivityResultContract, ActivityResultCallback)}\n      with the appropriate {@link ActivityResultContract} and handling the result in the\n      {@link ActivityResultCallback#onActivityResult(Object) callback}.")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            binding.ivKtpPreview.setImageURI(Uri.parse(currentPhotoPath))
        }
    }

    companion object {
        private const val REQUEST_IMAGE_CAPTURE = 1
        private const val REQUEST_CAMERA_PERMISSION = 2
    }
}