package com.koaladev.recycly.ui.viewmodel

import android.app.Application
import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.koaladev.recycly.data.pref.UserModel
import com.koaladev.recycly.data.repository.RecyclyRepository
import com.koaladev.recycly.data.repository.WasteRepository
import com.koaladev.recycly.data.response.UploadResponse
import kotlinx.coroutines.launch
import java.io.File

class RecyclyViewModel(
    application: Application,
    private val recyclyRepository: RecyclyRepository,
    private val wasteRepository: WasteRepository
): AndroidViewModel(application) {

    private val _points = MutableLiveData<Int>()
    val points: LiveData<Int> get() = _points

    private val _currentImageUri = MutableLiveData<Uri?>()
    val currentImageUri: LiveData<Uri?> get() = _currentImageUri

    private val _uploadResult = MutableLiveData<Result<UploadResponse>>()
    val uploadResult: LiveData<Result<UploadResponse>> get() = _uploadResult

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    fun getSession(): LiveData<UserModel> {
        return recyclyRepository.getSession().asLiveData()
    }

    fun logout() {
        viewModelScope.launch {
            recyclyRepository.logout()
        }
    }

    init {
        refreshPoints()
    }
    fun setCurrentImageUri(uri: Uri?) {
        _currentImageUri.value = uri
    }

    fun addPoints(newPoints: Int) {
        val currentPoints = _points.value ?: 0
        val updatedPoints = currentPoints + newPoints
        _points.value = updatedPoints
        Log.d("RecyclyViewModel", "Points added: $newPoints, Total: $updatedPoints")

        val sharedPref = getApplication<Application>().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            putInt("POINTS", updatedPoints)
            apply()
        }

        Log.d("RecyclyViewModel", "Points added: $newPoints, Total: $updatedPoints")

    }

    fun refreshPoints() {
        val sharedPref = getApplication<Application>().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val savedPoints = sharedPref.getInt("POINTS", 0)
        _points.value = savedPoints

        Log.d("RecyclyViewModel", "Points refreshed: $savedPoints")
    }

    fun uploadImage(file: File) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val result = wasteRepository.uploadImage(file)
                _uploadResult.value = result
                if (result.isSuccess) {
                    result.getOrNull()?.points ?.let {
                        refreshPoints()
                    }
                }
            } catch (e: Exception) {
                Log.e("ViewModel", "Upload failed", e)
                _uploadResult.value = Result.failure(e)
            } finally {
                _isLoading.value = false
            }
        }
    }
}
