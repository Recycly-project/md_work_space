package com.koaladev.recycly.data.response

import com.google.gson.annotations.SerializedName

data class UploadResponse(
    @SerializedName("label") val label: String?,
    @SerializedName("points") val points: Int?,
)
