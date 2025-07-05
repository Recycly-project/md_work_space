package com.koaladev.recycly.data.response

import com.google.gson.annotations.SerializedName

// Kelas ini mendefinisikan struktur utama dari respons JSON
data class ScanQrResponse(
    @field:SerializedName("status")
    val status: String,

    @field:SerializedName("message")
    val message: String,

    // Objek 'data' bisa jadi null jika request gagal, jadi kita tandai dengan '?'
    @field:SerializedName("data")
    val data: ScanQrData?
)

// Kelas ini mendefinisikan struktur dari objek 'data' di dalam JSON
data class ScanQrData(
    @field:SerializedName("qrCodeId")
    val qrCodeId: String,

    @field:SerializedName("pointsAdded")
    val pointsAdded: Int,

    @field:SerializedName("bottlesCollected")
    val bottlesCollected: Int
)