package com.koaladev.recycly.data.pref

data class UserModel(
    val id: String,
    val token: String,
    val name: String,
    val email: String,
    val isLogin: Boolean = false
)
