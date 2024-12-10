package com.koaladev.recycly.ui.activity

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.koaladev.recycly.data.response.GetWasteCollectionResponse
import com.koaladev.recycly.databinding.ActivityScanBinding
import com.koaladev.recycly.ui.viewmodel.RecyclyViewModel
import com.koaladev.recycly.ui.viewmodel.ViewModelFactory
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

typealias LumaListener = (luma: Double) -> Unit

class ScanActivity : AppCompatActivity() {

    private val viewModel: RecyclyViewModel by viewModels{
        ViewModelFactory.getInstance(this)
    }
    private lateinit var binding: ActivityScanBinding
    private lateinit var cameraExecutor: ExecutorService
    private var imageCapture: ImageCapture? = null
    private val rViewModel: RecyclyViewModel by viewModels{
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        outputDirectory = getOutputDirectory(this)
        binding = ActivityScanBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (allPermissionsGranted()) {
            startCamera()
        } else {
            ActivityCompat.requestPermissions(
                this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)
        }

        rViewModel.getSession().observe(this) { user ->
            if (!user.isLogin) {
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            } else {
                binding.imageCaptureButton.setOnClickListener { takePhoto(user.id, user.token) }
            }
        }

        cameraExecutor = Executors.newSingleThreadExecutor()
    }

    private fun takePhoto(id: String, token: String) {
        val imageCapture = imageCapture ?: return
        val photoFile = File(outputDirectory, "${System.currentTimeMillis()}.jpg")
        val outputFileOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        imageCapture.takePicture(
            outputFileOptions,
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) {
                    Log.e(TAG, "Photo capture failed: ${exc.message}", exc)
                }

                override fun onImageSaved(output: ImageCapture.OutputFileResults){
                    val savedUri = output.savedUri ?: Uri.fromFile(photoFile)
                    val compressedFile = compressImage(savedUri)
                    viewModel.setCurrentImageUri(Uri.fromFile(compressedFile))
                    val msg = "Berhasil mengambil gambar: ${compressedFile.absolutePath}"
                    Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
                    Log.d(TAG, msg)

                    uploadImage(id, token, compressedFile)
                }
            }
        )
    }

    private fun compressImage(uri: Uri): File {
        val maxFileSize = 2 * 1024 * 1024 // 2MB in bytes

        val inputStream = contentResolver.openInputStream(uri)
        val bitmap = BitmapFactory.decodeStream(inputStream)
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

        val outputFile = File(cacheDir, "compressed_${System.currentTimeMillis()}.jpg")
        FileOutputStream(outputFile).use { fos ->
            fos.write(bmpStream.toByteArray())
        }

        return outputFile
    }
    private fun uploadImage(id: String, token: String, file: File) {
        viewModel.uploadImage(id, token, file)
        viewModel.uploadResult.observe(this) { result ->
            runOnUiThread {
                binding.progressBar.visibility = View.GONE
                when {
                    result.isSuccess -> {
                        val response = result.getOrNull()
                        if (response != null) {
                            showResultDialog(response)
                        } else {
                            showErrorDialog("Berhasil mengambil gambar, tetapi tidak ada respons dari server.")
                        }
                    }
                    result.isFailure -> {
                        val exception = result.exceptionOrNull()
                        val errorMessage = when (exception) {
                            is retrofit2.HttpException -> {
                                try {
                                    // Mencoba untuk mendapatkan pesan error dari response body
                                    val errorBody = exception.response()?.errorBody()?.string()
                                    val errorJson = JSONObject(errorBody ?: "")
                                    errorJson.optString("message", "Terjadi kesalahan: ${exception.code()}")
                                } catch (e: Exception) {
                                    "Terjadi kesalahan: ${exception.code()}"
                                }
                            }
                            is java.net.UnknownHostException -> "Tidak dapat terhubung ke server. Periksa koneksi internet Anda."
                            else -> exception?.message ?: "Terjadi kesalahan yang tidak diketahui"
                        }
                        showErrorDialog(errorMessage)
                    }
                }
            }
        }
        binding.progressBar.visibility = View.VISIBLE
    }

    private fun showResultDialog(response: GetWasteCollectionResponse) {
        try {
            val wasteCollections = response.data?.wasteCollections
            if (wasteCollections.isNullOrEmpty()) {
                showErrorDialog("Data waste collection tidak ditemukan")
                return
            }

            // Asumsikan kita hanya mengambil item pertama dari list
            val firstWasteCollection = wasteCollections.first()

            val earnedPoints = firstWasteCollection.points ?: 0
            viewModel.addPoints(earnedPoints)

            val sharedPref = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
            with(sharedPref.edit()) {
                putInt("POINTS", viewModel.points.value ?: 0)
                apply()
            }

            Log.d("ScanActivity", "Points added: $earnedPoints, Total: ${viewModel.points.value}")

            val message = "Type: ${firstWasteCollection.label ?: "Tidak diketahui"}\n" +
                    "Status: ${response.status ?: "Tidak diketahui"}\n" +
                    "Point: $earnedPoints"

            AlertDialog.Builder(this)
                .setTitle("Hasil")
                .setMessage(message)
                .setPositiveButton("OK") { dialog, _ ->
                    dialog.dismiss()
                    finish()
                }
                .show()
        } catch (e: Exception) {
            Log.e(TAG, "Error showing result dialog", e)
            showErrorDialog("Error menampilkan hasil: ${e.message}")
        }
    }

    private fun showErrorDialog(errorMessage: String) {
        try {
            AlertDialog.Builder(this)
                .setTitle("Error")
                .setMessage(errorMessage)
                .setPositiveButton("OK") { dialog, _ ->
                    dialog.dismiss()
                    finish()
                }
                .show()
        } catch (e: Exception) {
            Log.e(TAG, "Error showing error dialog", e)
        }
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder()
                .build()
                .also {
                    it.surfaceProvider = binding.viewFinder.surfaceProvider
                }

            imageCapture = ImageCapture.Builder()
                .build()

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, imageCapture)

            } catch(exc: Exception) {
                Log.e(TAG, "Use case binding failed", exc)
            }

        }, ContextCompat.getMainExecutor(this))
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    @SuppressLint("MissingSuperCall")
    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults:
        IntArray) {
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                startCamera()
            } else {
                Toast.makeText(this,
                    "Permission tidak diberikan",
                    Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }

    companion object {
        private lateinit var outputDirectory: File
        private const val TAG = "CameraXApp"
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS =
            mutableListOf (
                Manifest.permission.CAMERA,
                Manifest.permission.RECORD_AUDIO
            ).apply {
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                    add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                }
            }.toTypedArray()

        private fun getOutputDirectory(context: Context): File {
            val mediaDir = context.externalMediaDirs.firstOrNull()?.let {
                File(it, "RecyclyApp").apply { mkdirs() }
            }
            return if (mediaDir != null && mediaDir.exists())
                mediaDir else context.filesDir
        }
    }
}