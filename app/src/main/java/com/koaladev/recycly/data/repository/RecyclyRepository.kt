package com.koaladev.recycly.data.repository

import android.util.Log
import com.koaladev.recycly.data.pref.UserModel
import com.koaladev.recycly.data.pref.UserPreference
import com.koaladev.recycly.data.response.LoginResponse
import com.koaladev.recycly.data.response.UploadResponse
import com.koaladev.recycly.data.response.UploadWasteCollectionResponse
import com.koaladev.recycly.data.retrofit.ApiService
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import com.koaladev.recycly.data.response.RegisterResponse
import kotlinx.coroutines.flow.Flow
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Response

class RecyclyRepository private constructor(
    private val userPreference: UserPreference,
    private val apiService: ApiService
) {

    suspend fun register(email: String, password: String, fullName: String, address: String, ktp: File): RegisterResponse {
        val ktpRequestBody = ktp.asRequestBody("image/*".toMediaTypeOrNull())
        val ktpPart = MultipartBody.Part.createFormData("ktp", ktp.name, ktpRequestBody)

        return apiService.register(
            email = email.toRequestBody("text/plain".toMediaTypeOrNull()),
            password = password.toRequestBody("text/plain".toMediaTypeOrNull()),
            fullName = fullName.toRequestBody("text/plain".toMediaTypeOrNull()),
            address = address.toRequestBody("text/plain".toMediaTypeOrNull()),
            ktp = ktpPart
        )
    }

    suspend fun login(email: String, password: String): LoginResponse {
        try {
            return apiService.login(email, password)
        } catch (e: Exception) {
            throw e
        }
    }

    suspend fun saveSession(user: UserModel) {
        userPreference.saveSession(user)
    }

    fun getSession(): Flow<UserModel> {
        return userPreference.getSession()
    }

    suspend fun logout() {
        userPreference.logout()
    }

    companion object {
        @Volatile
        private var instance: RecyclyRepository? = null
        fun getInstance(
            userPreference: UserPreference,
            apiService: ApiService
        ): RecyclyRepository =
            instance ?: synchronized(this) {
                instance ?: RecyclyRepository(userPreference, apiService)
            }.also { instance = it }
    }
}
