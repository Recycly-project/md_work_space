// app/src/main/java/com/koaladev/recycly/ui/viewmodel/HistoryViewModel.kt

package com.koaladev.recycly.ui.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.koaladev.recycly.data.repository.RecyclyRepository
import com.koaladev.recycly.data.response.QrHistoryItem // Impor baru
import kotlinx.coroutines.launch


class HistoryViewModel(
    private val repository: RecyclyRepository,
) : ViewModel() {

    private val _qrHistory = MutableLiveData<List<QrHistoryItem>>()
    val qrHistory: LiveData<List<QrHistoryItem>> = _qrHistory

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    fun getQrHistory(userId: String, token: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                if (userId.isNotEmpty() && token.isNotEmpty()) {
                    val response = repository.getQrHistory(userId, token)
                    // Sesuaikan cara mengakses data
                    _qrHistory.value = response.data.qrCodeHistory
                    Log.d("HistoryViewModel", "Riwayat QR diambil: ${response.data.qrCodeHistory}")
                } else {
                    _error.value = "User ID atau token hilang"
                    Log.e("HistoryViewModel", "User ID atau token hilang. User ID kosong: ${userId.isEmpty()}, Token kosong: ${token.isEmpty()}")
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "Terjadi kesalahan"
                Log.e("HistoryViewModel", "Error mengambil riwayat QR", e)
            } finally {
                _isLoading.value = false
            }
        }
    }
}