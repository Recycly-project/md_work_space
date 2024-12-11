package com.koaladev.recycly.data.response

import com.google.gson.annotations.SerializedName

data class GetWasteCollectionResponse(
    @field:SerializedName("status")
    val status: String,
    @field:SerializedName("message")
    val message: String,
    @field:SerializedName("data")
    val data: WasteCollectionData
)

data class WasteCollectionData(
    @field:SerializedName("label")
    val label: String,
    @field:SerializedName("points")
    val points: Int
)
