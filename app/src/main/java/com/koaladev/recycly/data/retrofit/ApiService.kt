package com.koaladev.recycly.data.retrofit

import com.koaladev.recycly.data.response.UploadResponse
import com.koaladev.recycly.data.response.UploadWasteCollectionResponse
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface ApiService{
    @Multipart
    @POST("verifyWasteCollection")
    suspend fun uploadWasteCollection(
        @Part image: MultipartBody.Part
    ): Response<UploadResponse>
}