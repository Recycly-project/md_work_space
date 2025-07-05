package com.koaladev.recycly.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import com.koaladev.recycly.data.pref.UserModel
import com.koaladev.recycly.data.pref.UserPreference
import com.koaladev.recycly.data.response.GetWasteCollectionResponse
import com.koaladev.recycly.data.response.HistoryResponse
import com.koaladev.recycly.data.response.LoginResponse
import com.koaladev.recycly.data.retrofit.ApiService
import okhttp3.MultipartBody
import java.io.File
import com.koaladev.recycly.data.response.RegisterResponse
import com.koaladev.recycly.data.response.UserResponse
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import com.koaladev.recycly.data.response.QrHistoryResponse
import com.koaladev.recycly.data.response.RewardsResponse
import com.koaladev.recycly.data.response.RedeemResponse

class RecyclyRepository private constructor(
    private val userPreference: UserPreference,
    private val apiService: ApiService
) {

    suspend fun getWasteCollections(userId: String, token: String): HistoryResponse {
        return apiService.getWasteCollections(userId, "Bearer $token")
    }

    // Fungsi baru untuk mengambil riwayat QR
    suspend fun getQrHistory(userId: String, token: String): QrHistoryResponse {
        return apiService.getQrHistory(userId, "Bearer $token")
    }

    suspend fun getUserById(id: String, token: String): UserResponse {
        val user_token = "Bearer $token"
        return apiService.getUserById(id, user_token)
    }

    suspend fun getRewards(token: String): RewardsResponse {
        return apiService.getRewards("Bearer $token")
    }

    suspend fun redeemReward(userId: String, rewardId: String, token: String): RedeemResponse {
        return apiService.redeemReward(userId, rewardId, "Bearer $token")
    }

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

    fun getSession(): LiveData<UserModel> {
        return userPreference.getSession().asLiveData()
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
