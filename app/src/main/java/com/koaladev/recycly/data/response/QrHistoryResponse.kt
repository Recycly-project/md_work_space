package com.koaladev.recycly.data.response

import com.google.gson.annotations.SerializedName

// Kelas utama untuk seluruh respons
data class QrHistoryResponse(
    @field:SerializedName("status")
    val status: String,

    @field:SerializedName("message")
    val message: String,

    @field:SerializedName("data")
    val data: GetQrHistoryData
)

// Kelas untuk membungkus list riwayat
data class GetQrHistoryData(
    @field:SerializedName("qrCodeHistory")
    val qrCodeHistory: List<QrHistoryItem>
)

// Item riwayat, sesuai dengan model Prisma
data class QrHistoryItem(
    @field:SerializedName("id")
    val id: String,

    @field:SerializedName("qrCodeId")
    val qrCodeId: String,

    @field:SerializedName("points")
    val points: Int,

    @field:SerializedName("bottles")
    val bottles: Int,

    // Menggunakan 'scannedAt' untuk tanggal pemindaian
    @field:SerializedName("scannedAt")
    val scannedAt: String
)