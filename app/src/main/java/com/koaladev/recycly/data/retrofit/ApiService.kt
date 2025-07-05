package com.koaladev.recycly.data.retrofit

import com.koaladev.recycly.data.response.GetWasteCollectionResponse
import com.koaladev.recycly.data.response.HistoryResponse
import com.koaladev.recycly.data.response.LoginResponse
import com.koaladev.recycly.data.response.RegisterResponse
import com.koaladev.recycly.data.response.ScanQrResponse
import com.koaladev.recycly.data.response.UserResponse
import com.koaladev.recycly.data.response.QrHistoryResponse
import com.koaladev.recycly.data.response.RedeemResponse
import com.koaladev.recycly.data.response.RewardsResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path

interface ApiService{

    @Multipart
    @POST("auth/register")
    suspend fun register(
        @Part("email") email: RequestBody,
        @Part("password") password: RequestBody,
        @Part("fullName") fullName: RequestBody,
        @Part("address") address: RequestBody,
        @Part ktp: MultipartBody.Part
    ): RegisterResponse

    @FormUrlEncoded
    @POST("auth/login")
    suspend fun login(
        @Field("email") email: String,
        @Field("password") password: String
    ): LoginResponse

    @GET("users/{id}")
    suspend fun getUserById(
        @Path("id") id: String,
        @Header("Authorization") token: String
    ): UserResponse

    @Multipart
    @POST("users/{id}/waste-collections")
    suspend fun uploadWasteCollections(
        @Path("id") userId: String,
        @Header("Authorization") token: String,
        @Part imagePart: MultipartBody.Part
    ): Response<GetWasteCollectionResponse>

    @GET("users/{id}/waste-collections")
    suspend fun getWasteCollections(
        @Path("id") userId: String,
        @Header("Authorization") token: String
    ): HistoryResponse

    // Endpoint baru untuk scan QR
    @Multipart
    @POST("users/{id}/scan-qr")
    suspend fun scanQr(
        @Path("id") userId: String,
        @Header("Authorization") token: String,
        @Part qrCodeImage: MultipartBody.Part // Mengirim file gambar
    ): ScanQrResponse

    // Endpoint baru untuk riwayat QR
    @GET("users/{id}/qr-history")
    suspend fun getQrHistory(
        @Path("id") userId: String,
        @Header("Authorization") token: String
    ): QrHistoryResponse

    @GET("rewards")
    suspend fun getRewards(
        @Header("Authorization") token: String
    ): RewardsResponse

    @POST("users/{userId}/rewards/{rewardId}/redeem")
    suspend fun redeemReward(
        @Path("userId") userId: String,
        @Path("rewardId") rewardId: String,
        @Header("Authorization") token: String
    ): RedeemResponse
}