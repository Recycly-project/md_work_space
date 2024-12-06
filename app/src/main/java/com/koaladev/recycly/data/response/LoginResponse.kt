package com.koaladev.recycly.data.response

data class LoginResponse(
	val data: TokenResponse,
	val message: String,
	val status: String
)

data class TokenResponse(
	val token: String
)