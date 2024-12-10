package com.koaladev.recycly.ui.viewmodel

import androidx.lifecycle.ViewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.koaladev.recycly.data.repository.RecyclyRepository
import com.koaladev.recycly.data.response.HistoryItem
import kotlinx.coroutines.launch

class HistoryViewModel(
    private val repository: RecyclyRepository,
) : ViewModel() {

    private val _wasteCollections = MutableLiveData<List<HistoryItem>>()
    val wasteCollections: LiveData<List<HistoryItem>> = _wasteCollections

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    fun getWasteCollections(userId: String, token: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                if (userId.isNotEmpty() && token.isNotEmpty()) {
                    val response = repository.getWasteCollections(userId, token)
                    _wasteCollections.value = response.data.wasteCollections
                    Log.d("HistoryViewModel", "Waste collections retrieved: ${response.data.wasteCollections}")
                } else {
                    _error.value = "User ID or token is missing"
                    Log.e("HistoryViewModel", "User ID or token is missing. User ID empty: ${userId.isEmpty()}, Token empty: ${token.isEmpty()}")
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "An error occurred"
                Log.e("HistoryViewModel", "Error retrieving waste collections", e)
            } finally {
                _isLoading.value = false
            }
        }
    }
}
