package com.koaladev.recycly.data.response

import com.google.gson.annotations.SerializedName

data class HistoryResponse(
    @field:SerializedName("status")
    val status: String,
    @field:SerializedName("message")
    val message: String,
    @field:SerializedName("data")
    val data: GetHistoryData
)
data class GetHistoryData(
    @field:SerializedName("wasteCollections")
    val wasteCollections: List<HistoryItem>
)
data class HistoryItem(
    @field:SerializedName("image")
    val image: String,
    @field:SerializedName("createdAt")
    val createdAt: String,
    @field:SerializedName("id")
    val id: String,
    @field:SerializedName("label")
    val label: String,
    @field:SerializedName("userId")
    val userId: String,
    @field:SerializedName("points")
    val points: Int
)