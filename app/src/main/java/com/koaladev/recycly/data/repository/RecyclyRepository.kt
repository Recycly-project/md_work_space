package com.koaladev.recycly.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.koaladev.recycly.data.response.UploadWasteCollectionResponse
import com.koaladev.recycly.data.retrofit.ApiService
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class RecyclyRepository(
    private val apiService: ApiService
) {
    suspend fun uploadImage(file: File): LiveData<Result<UploadWasteCollectionResponse>> {
        val result = MutableLiveData<Result<UploadWasteCollectionResponse>>()
        val requestBody = RequestBody.create("image/jpeg".toMediaType(), file)
        val imagePart = MultipartBody.Part.createFormData("image", file.name, requestBody)

        apiService.uploadWasteCollection(imagePart).enqueue(object : Callback<UploadWasteCollectionResponse> {
            override fun onResponse(call: Call<UploadWasteCollectionResponse>, response: Response<UploadWasteCollectionResponse>) {
                if (response.isSuccessful) {
                    result.postValue(Result.success(response.body()) as Result<UploadWasteCollectionResponse>?)
                } else {
                    result.postValue(Result.failure(Throwable(response.errorBody()?.string())))
                }
            }

            override fun onFailure(call: Call<UploadWasteCollectionResponse>, t: Throwable) {
                result.postValue(Result.failure(t))
            }
        })
        return result
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
