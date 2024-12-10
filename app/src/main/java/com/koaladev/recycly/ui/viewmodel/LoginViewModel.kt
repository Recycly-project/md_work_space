package com.koaladev.recycly.ui.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.koaladev.recycly.BuildConfig.BASE_URL_AUTH
import com.koaladev.recycly.data.pref.UserModel
import com.koaladev.recycly.data.pref.UserPreference
import com.koaladev.recycly.data.repository.RecyclyRepository
import com.koaladev.recycly.data.response.LoginResponse
import kotlinx.coroutines.launch


class LoginViewModel(
    private val repository: RecyclyRepository,
    private val userPreference: UserPreference
) : ViewModel() {

    fun saveSession(user: UserModel) {
        viewModelScope.launch {
            repository.saveSession(user)
        }
    }

    fun login(email: String, password: String, onResult: (Boolean, String, String, String, String) -> Unit) {
        Log.d("LoginModel", "Melakukan login untuk: $email")
        viewModelScope.launch {
            try {
                val response = repository.login(email, password)
                Log.d("LoginViewModel", "Received response: ${Gson().toJson(response)}")
                if (response.status == "success") {
                    val loginResult = response.data
                    Log.d("Login", "Login successful for email: $email")
                    onResult(
                        true,
                        loginResult.token,
                        loginResult.user.id,
                        loginResult.user.email,
                        loginResult.user.fullName,
                    )
                } else {
                    Log.e("Login", "Login failed: ${response.message}")
                    onResult(false, "", "", "", response.message)
                }
            } catch (e: Exception) {
                onResult(false, "", "", "", e.message.toString())
                Log.e("Login", "login: ${e.message}")
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            userPreference.logout()
        }
    }
}

class LoginViewModelFactory(
    private val repository: RecyclyRepository,
    private val userPreference: UserPreference
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return LoginViewModel(repository, userPreference) as T
        }
        throw IllegalArgumentException("ViewModel Class tidak dikenali")
    }
}