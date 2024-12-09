package com.koaladev.recycly.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.koaladev.recycly.data.repository.RecyclyRepository
import com.koaladev.recycly.data.response.LoginResponse
import kotlinx.coroutines.launch
import android.content.Context
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import com.koaladev.recycly.data.repository.SessionPreferences
import com.koaladev.recycly.data.retrofit.ApiConfig
import kotlinx.coroutines.flow.map


class LoginViewModel(
    private val repository: RecyclyRepository,
    private val sessionPreferences: SessionPreferences
) : ViewModel() {

    private val _loginResult = MutableLiveData<Result<LoginResponse>>()
    val loginResult: LiveData<Result<LoginResponse>> = _loginResult

    val isLoggedIn: LiveData<Boolean> = sessionPreferences.getSession().map { it.isLogin }.asLiveData()

    fun login(email: String, password: String) {
        viewModelScope.launch {
            try {
                val response = repository.login(email, password)
                if (response.status == "success") {
                    sessionPreferences.saveSession(response.data.token, response.data.user.id, response.data.user.email, response.data.user.fullName, response.data
                        .user.isAdmin)
                    _loginResult.value = Result.success(response)
                } else {
                    _loginResult.value = Result.failure(Exception(response.message))
                }
            } catch (e: Exception) {
                _loginResult.value = Result.failure(e)
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            sessionPreferences.logout()
        }
    }
}

class LoginViewModelFactory(
    private val repository: RecyclyRepository,
    private val sessionPreferences: SessionPreferences
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return LoginViewModel(repository, sessionPreferences) as T
        }
        throw IllegalArgumentException("ViewModel Class tidak dikenali")
    }
}