package com.koaladev.recycly.ui.viewmodel

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.koaladev.recycly.data.repository.RecyclyRepository
import com.koaladev.recycly.data.response.UploadResponse
import kotlinx.coroutines.launch
import java.io.File

class RecyclyViewModel(private val recyclyRepository: RecyclyRepository): ViewModel() {

    // Get
    private var _points: MutableLiveData<Int> = MutableLiveData()
    val points: LiveData<Int> get() = _points

    fun setPoints(point: Int) {
        _points.value = point
    }

    private val _currentImageUri = MutableLiveData<Uri?>()
    val currentImageUri: LiveData<Uri?> get() = _currentImageUri

    private val _uploadResult = MutableLiveData<Result<UploadResponse>>()
    val uploadResult: LiveData<Result<UploadResponse>> get() = _uploadResult

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    fun setCurrentImageUri(uri: Uri?) {
        _currentImageUri.value = uri
    }

    private fun addPoints(point: Int) {
        val currentPoints = _points.value?: 0
        _points.value = currentPoints + point
    }

    fun uploadImage(file: File) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val result = recyclyRepository.uploadImage(file)
                _uploadResult.value = result
                if (result.isSuccess) {
                    result.getOrNull()?.points ?.let { addPoints(it) }
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
