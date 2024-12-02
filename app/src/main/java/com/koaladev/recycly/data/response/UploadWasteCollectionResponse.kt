package com.koaladev.recycly.data.response

import com.google.gson.annotations.SerializedName

data class UploadWasteCollectionResponse(
    @SerializedName("status") val status: String,
    @SerializedName("message") val message: String,
    @SerializedName("data") val data: WasteCollectionData
)

data class WasteCollectionData(
    @SerializedName("wasteCollection") val wasteCollection: WasteCollection
)

data class WasteCollection(
    @SerializedName("id") val id: Int,
    @SerializedName("userId") val userId: String,
    @SerializedName("label") val label: String,
    @SerializedName("points") val points: Int,
    @SerializedName("image") val image: String,
    @SerializedName("createdAt") val createdAt: String
)