package com.koaladev.recycly.data.repository

import android.util.Log
import com.koaladev.recycly.data.response.GetWasteCollectionResponse
import com.koaladev.recycly.data.response.ScanQrResponse
import com.koaladev.recycly.data.retrofit.ApiService
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Response
import java.io.File

class WasteRepository(private val apiService: ApiService) {
    suspend fun uploadImage(id: String, token: String, file: File): Result<GetWasteCollectionResponse> {
        return try {
            val requestBody = file.asRequestBody("image/jpeg".toMediaType())
            val imagePart = MultipartBody.Part.createFormData("image", file.name, requestBody)

            val response = apiService.uploadWasteCollections(id, "Bearer $token", imagePart)
            Log.d("UploadResponse", "Response: $response")
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    val uploadResult = GetWasteCollectionResponse(
                        status = body.status,
                        message = body.message,
                        data = body.data
                    )
                    Result.success(uploadResult)
                } else {
                    Result.failure(Exception("Response body is null"))
                }
            } else {
                Result.failure(Exception("Error: ${response.errorBody()} ${response.body()?.data}"))
            }
        } catch (e: Exception) {
            Log.e("WasteRepository", "Error uploading image", e)
            Result.failure(e)
        }
    }
    suspend fun scanQrCode(id: String, token: String, imageFile: File): Result<ScanQrResponse> {
        return try {
            val requestImageFile = imageFile.asRequestBody("image/jpeg".toMediaType())
            val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
                "qrCodeImage", // Nama field harus cocok dengan backend
                imageFile.name,
                requestImageFile
            )

            val response = apiService.scanQr(id, "Bearer $token", imageMultipart)
            Log.d("ScanQrResponse", "Response: $response")

            if (response.status == "success") {
                Result.success(response)
            } else {
                Result.failure(Exception("Gagal memindai QR: ${response.message}"))
            }
        } catch (e: Exception) {
            Log.e("WasteRepository", "Error scanning QR code", e)
            Result.failure(e)
        }
    }
}