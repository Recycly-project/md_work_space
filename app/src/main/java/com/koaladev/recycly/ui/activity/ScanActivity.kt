package com.koaladev.recycly.ui.activity

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.koaladev.recycly.R
import com.koaladev.recycly.data.response.ScanQrResponse
import com.koaladev.recycly.databinding.ActivityScanBinding
import com.koaladev.recycly.ui.viewmodel.RecyclyViewModel
import com.koaladev.recycly.ui.viewmodel.ViewModelFactory
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class ScanActivity : AppCompatActivity() {

    private val viewModel: RecyclyViewModel by viewModels {
        ViewModelFactory.getInstance(this)
    }
    private lateinit var binding: ActivityScanBinding
    private lateinit var cameraExecutor: ExecutorService
    private var imageCapture: ImageCapture? = null

    // Launcher untuk memilih gambar dari galeri
    private val galleryLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            handleImageUpload(it)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityScanBinding.inflate(layoutInflater)
        setContentView(binding.root)

        cameraExecutor = Executors.newSingleThreadExecutor()

        if (allPermissionsGranted()) {
            startCamera()
        } else {
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)
        }

        binding.btnBack.setOnClickListener { finish() }
        binding.imageCaptureButton.setOnClickListener { takePhoto() }

        // Tambahkan listener untuk tombol upload
        // Pastikan Anda memiliki tombol dengan id 'uploadButton' di activity_scan.xml
        binding.uploadButton.setOnClickListener { openGallery() }
    }

    private fun takePhoto() {
        binding.progressBar.visibility = View.VISIBLE
        val imageCapture = this.imageCapture ?: return

        val photoFile = File(
            outputDirectory,
            SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSS", Locale.US).format(System.currentTimeMillis()) + ".jpg"
        )

        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) {
                    binding.progressBar.visibility = View.GONE
                    Log.e(TAG, "Gagal mengambil foto.", exc)
                    Toast.makeText(baseContext, "Gagal mengambil foto.", Toast.LENGTH_SHORT).show()
                }

                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    val compressedFile = compressImage(photoFile)
                    sendToServer(compressedFile)
                }
            }
        )
    }

    // Fungsi untuk membuka galeri
    private fun openGallery() {
        galleryLauncher.launch("image/*")
    }

    // Fungsi untuk menangani gambar yang dipilih dari galeri
    private fun handleImageUpload(uri: Uri) {
        binding.progressBar.visibility = View.VISIBLE
        try {
            val inputStream: InputStream? = contentResolver.openInputStream(uri)
            val tempFile = File.createTempFile("uploaded_qr", ".jpg", cacheDir)
            val fos = FileOutputStream(tempFile)

            inputStream?.use { input ->
                fos.use { output ->
                    input.copyTo(output)
                }
            }
            val compressedFile = compressImage(tempFile)
            sendToServer(compressedFile)
        } catch (e: Exception) {
            binding.progressBar.visibility = View.GONE
            Log.e(TAG, "Gagal memproses gambar dari galeri", e)
            Toast.makeText(this, "Gagal memproses gambar.", Toast.LENGTH_SHORT).show()
        }
    }

    // Fungsi terpusat untuk mengirim file ke server
    private fun sendToServer(file: File) {
        viewModel.getSession().observe(this@ScanActivity) { user ->
            if (user.isLogin) {
                viewModel.scanQrCode(user.id, user.token, file)
            } else {
                binding.progressBar.visibility = View.GONE
                Toast.makeText(this, "Sesi tidak valid, silakan login kembali.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun compressImage(file: File): File {
        val maxFileSize = 1_000_000 // 1MB
        if (file.length() <= maxFileSize) {
            Log.d(TAG, "Gambar tidak perlu dikompres: ${file.length() / 1024} KB")
            return file
        }

        val bitmap = BitmapFactory.decodeFile(file.path)
        var compressQuality = 90
        val bmpStream = ByteArrayOutputStream()

        do {
            bmpStream.reset()
            bitmap.compress(Bitmap.CompressFormat.JPEG, compressQuality, bmpStream)
            compressQuality -= 5
        } while (bmpStream.size() > maxFileSize && compressQuality > 10)

        FileOutputStream(file).use { it.write(bmpStream.toByteArray()) }
        Log.d(TAG, "Gambar dikompres menjadi: ${file.length() / 1024} KB")
        return file
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder().build().also {
                it.surfaceProvider = binding.viewFinder.surfaceProvider
            }

            imageCapture = ImageCapture.Builder().build()

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(this, CameraSelector.DEFAULT_BACK_CAMERA, preview, imageCapture)
            } catch (exc: Exception) {
                Log.e(TAG, "Gagal memulai kamera.", exc)
            }
        }, ContextCompat.getMainExecutor(this))

        observeViewModel()
    }

    private fun observeViewModel() {
        viewModel.scanResult.observe(this) { result ->
            binding.progressBar.visibility = View.GONE
            result.onSuccess { response ->
                showResultDialog(response)
            }.onFailure {
                showErrorDialog(it.message ?: "Terjadi kesalahan tidak diketahui.")
            }
        }
    }

    private fun showResultDialog(response: ScanQrResponse) {
        val earnedPoints = response.data?.pointsAdded ?: 0
        val message = "Pesan: ${response.message}\nPoin didapat: $earnedPoints"

        AlertDialog.Builder(this)
            .setTitle("Scan Berhasil")
            .setMessage(message)
            .setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
                finish()
            }
            .setCancelable(false)
            .show()
    }

    private fun showErrorDialog(errorMessage: String) {
        AlertDialog.Builder(this)
            .setTitle("Scan Gagal")
            .setMessage(errorMessage)
            .setPositiveButton("Coba Lagi") { dialog, _ -> dialog.dismiss() }
            .setNegativeButton("Tutup") { dialog, _ ->
                dialog.dismiss()
                finish()
            }
            .setCancelable(false)
            .show()
    }

    private val outputDirectory: File by lazy {
        val mediaDir = externalMediaDirs.firstOrNull()?.let {
            File(it, resources.getString(R.string.app_name)).apply { mkdirs() }
        }
        if (mediaDir != null && mediaDir.exists()) mediaDir else filesDir
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                startCamera()
            } else {
                Toast.makeText(this, "Izin kamera tidak diberikan.", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }

    companion object {
        private const val TAG = "ScanActivity"
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
    }
}