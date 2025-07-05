package com.koaladev.recycly.data.response

import com.google.gson.annotations.SerializedName

data class RewardsResponse(
    @field:SerializedName("status")
    val status: String,

    @field:SerializedName("message")
    val message: String,

    @field:SerializedName("data")
    val data: List<RewardItem>
)

data class RewardItem(
    @field:SerializedName("id")
    val id: String,

    @field:SerializedName("title")
    val title: String,

    @field:SerializedName("description")
    val description: String,

    @field:SerializedName("redeemPoint")
    val redeemPoint: Int
)