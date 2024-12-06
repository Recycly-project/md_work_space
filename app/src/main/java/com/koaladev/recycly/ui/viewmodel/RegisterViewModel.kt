package com.koaladev.recycly.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.koaladev.recycly.data.repository.RecyclyRepository
import com.koaladev.recycly.data.response.Data
import com.koaladev.recycly.data.response.RegisterResponse
import kotlinx.coroutines.launch
import java.io.File

class RegisterViewModel(private val repository: RecyclyRepository) : ViewModel() {

    private val _registerResult = MutableLiveData<RegisterResponse>()
    val registerResult: LiveData<RegisterResponse> = _registerResult

    fun register(email: String, password: String, fullName: String, address: String, ktp: File) {
        viewModelScope.launch {
            try {
                val response = repository.register(email, password, fullName, address, ktp)
                _registerResult.value = response
            } catch (e: Exception) {
                _registerResult.value = RegisterResponse(
                    data = Data(userId = ""),
                    message = e.message ?: "Unknown error occurred",
                    status = "fail"
                )
            }
        }
    }
}

class RegisterViewModelFactory(private val repository: RecyclyRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RegisterViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return RegisterViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}