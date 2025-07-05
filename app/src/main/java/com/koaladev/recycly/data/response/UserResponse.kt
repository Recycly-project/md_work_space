package com.koaladev.recycly.data.response

import com.google.gson.annotations.SerializedName

data class UserResponse(
    @SerializedName("status") val status: String,
    @SerializedName("data") val data: UserData
)

data class UserData(
    @SerializedName("user") val user: UserFullData
)

data class UserFullData(
    @SerializedName("id") val id: String,
    @SerializedName("email") val email: String,
    @SerializedName("fullName") val fullName: String,
    @SerializedName("address") val address: String,
    @SerializedName("ktp") val ktp: Any?,
    @SerializedName("totalPoints") val totalPoints: Int
)