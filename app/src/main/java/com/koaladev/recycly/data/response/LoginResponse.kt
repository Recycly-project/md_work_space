package com.koaladev.recycly.data.response

import com.google.gson.annotations.SerializedName

data class LoginResponse(
	@SerializedName("status") val status: String,
	@SerializedName("message") val message: String,
	@SerializedName("data") val data: LoginData
)

data class LoginData(
	@SerializedName("token") val token: String,
	@SerializedName("user") val user: User
)

data class User(
	@SerializedName("id") val id: String,
	@SerializedName("email") val email: String,
	@SerializedName("fullName") val fullName: String,
	@SerializedName("isAdmin") val isAdmin: Boolean
)
