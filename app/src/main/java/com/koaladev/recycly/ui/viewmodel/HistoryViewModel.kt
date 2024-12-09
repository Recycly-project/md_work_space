package com.koaladev.recycly.ui.history

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.koaladev.recycly.data.repository.RecyclyRepository
import com.koaladev.recycly.data.repository.SessionPreferences
import com.koaladev.recycly.data.response.WasteCollectionsItem
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class HistoryViewModel(
    private val repository: RecyclyRepository,
    private val sessionPreferences: SessionPreferences
) : ViewModel() {

    private val _wasteCollections = MutableLiveData<List<WasteCollectionsItem>>()
    val wasteCollections: LiveData<List<WasteCollectionsItem>> = _wasteCollections

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    fun getWasteCollections() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val userId = sessionPreferences.getUserId()
                val token = sessionPreferences.getToken()

                Log.d("HistoryViewModel", "User ID: $userId")
                Log.d("HistoryViewModel", "Token: ${token.take(5)}...") // Only log the first 5 characters of the token for security

                if (userId.isNotEmpty() && token.isNotEmpty()) {
                    val response = repository.getWasteCollections(userId, token)
                    _wasteCollections.value = response.data.wasteCollections
                    Log.d("HistoryViewModel", "Waste collections retrieved: ${response.data.wasteCollections.size}")
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

//class HistoryViewModel(
//    private val repository: RecyclyRepository,
//    private val sessionPreferences: SessionPreferences
//) : ViewModel() {
//
//    private val _wasteCollections = MutableLiveData<List<WasteCollectionsItem>>()
//    val wasteCollections: LiveData<List<WasteCollectionsItem>> = _wasteCollections
//
//    private val _isLoading = MutableLiveData<Boolean>()
//    val isLoading: LiveData<Boolean> = _isLoading
//
//    private val _error = MutableLiveData<String?>()
//    val error: LiveData<String?> = _error
//
//    fun getWasteCollections() {
//        viewModelScope.launch {
//            _isLoading.value = true
//            _error.value = null
//            try {
//                val session = sessionPreferences.getSession().first()
//                val response = repository.getWasteCollections(session.userId, session.token)
//                if (response.status == "success") {
//                    _wasteCollections.value = response.data.wasteCollections
//                } else {
//                    _error.value = response.message
//                }
//                _isLoading.value = false
//            } catch (e: Exception) {
//                _error.value = e.message ?: "An unknown error occurred"
//                _isLoading.value = false
//            }
//        }
//    }
//}

class HistoryViewModelFactory(
    private val repository: RecyclyRepository,
    private val sessionPreferences: SessionPreferences
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HistoryViewModel::class.java)) {
            return HistoryViewModel(repository, sessionPreferences) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}