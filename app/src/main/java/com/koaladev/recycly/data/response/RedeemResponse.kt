package com.koaladev.recycly.data.response

import com.google.gson.annotations.SerializedName

data class RedeemResponse(
    @field:SerializedName("status")
    val status: String,

    @field:SerializedName("message")
    val message: String
)