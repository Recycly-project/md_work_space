package com.koaladev.recycly.data.repository

import com.koaladev.recycly.data.response.UploadResponse
import com.koaladev.recycly.data.response.UploadWasteCollectionResponse
import com.koaladev.recycly.data.retrofit.ApiService
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import com.koaladev.recycly.data.response.RegisterResponse
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody

class RecyclyRepository(
    private val apiService: ApiService
) {
        suspend fun uploadImage(file: File): Result<UploadResponse> {
            return try {
                val requestBody = RequestBody.create("image/jpeg".toMediaType(), file)
                val imagePart = MultipartBody.Part.createFormData("image", file.name, requestBody)

                val response = apiService.uploadWasteCollection(imagePart)
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null) {
                        val uploadResponse = UploadResponse(
                            label = body.label,
                            points = body.points
                        )
                        Result.success(uploadResponse)
                    } else {
                        Result.failure(Exception("Response body null"))
                    }
                } else {
                    Result.failure(Exception(response.errorBody()?.string() ?: "Unknown error"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    suspend fun register(email: String, password: String, fullName: String, address: String, ktp: File): Result<RegisterResponse> {
        return try {
            val ktpRequestBody = ktp.asRequestBody("image/*".toMediaTypeOrNull())
            val ktpPart = MultipartBody.Part.createFormData("ktp", ktp.name, ktpRequestBody)

            val response = apiService.register(
                email = email.toRequestBody("text/plain".toMediaTypeOrNull()),
                password = password.toRequestBody("text/plain".toMediaTypeOrNull()),
                fullName = fullName.toRequestBody("text/plain".toMediaTypeOrNull()),
                address = address.toRequestBody("text/plain".toMediaTypeOrNull()),
                ktp = ktpPart
            )

            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    Result.success(body)
                } else {
                    Result.failure(Exception("Response body is null"))
                }
            } else {
                Result.failure(Exception("Registration failed: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    companion object {
        @Volatile
        private var instance: RecyclyRepository? = null
        fun getInstance(apiService: ApiService): RecyclyRepository =
            instance ?: synchronized(this) {
                instance ?: RecyclyRepository(apiService)
            }.also { instance = it }
    }
}
