package com.koaladev.recycly.data.response

data class LoginResponse(
	val data: TokenResponse,
	val message: String,
	val status: String
)

data class TokenResponse(
	val token: String,
	val user: UserData
)

data class UserData(
    val id: String,
    val email: String,
	val fullName: String,
	val isAdmin: Boolean
)