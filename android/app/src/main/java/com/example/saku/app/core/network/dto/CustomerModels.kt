package com.example.saku.app.core.network.dto

import com.google.gson.annotations.SerializedName
import java.math.BigDecimal

data class CustomerProfileDto(
    @SerializedName("id") val id: String? = null,
    @SerializedName("nik") val nik: String? = null,
    @SerializedName("nama") val nama: String? = null,
    @SerializedName("username") val username: String? = null,
    @SerializedName("email") val email: String? = null,
    @SerializedName("noHp") val noHp: String? = null,
    @SerializedName("namaBank") val namaBank: String? = null,
    @SerializedName("noRekening") val noRekening: String? = null,
    @SerializedName("namaRekening") val namaRekening: String? = null,
    @SerializedName("status") val status: Boolean? = true,
    @SerializedName("isKycVerified") val isKycVerified: Boolean? = false,
    @SerializedName("statusVerifikasi") val statusVerifikasi: String? = null,
    @SerializedName("catatanVerifikasi") val catatanVerifikasi: String? = null,
    @SerializedName("pekerjaan") val pekerjaan: String? = null,
    @SerializedName("tempatKerja") val tempatKerja: String? = null,
    @SerializedName("statusPekerjaan") val statusPekerjaan: String? = null,
    @SerializedName("penghasilanBulanan") val penghasilanBulanan: Double? = null,
    @SerializedName("totalPlafond") val totalPlafond: Double? = null,
    @SerializedName("usedPlafond") val usedPlafond: Double? = null,
    @SerializedName("availablePlafond") val availablePlafond: Double? = null
)

data class LoanApplicationItemDto(
    @SerializedName("id") val id: String? = null,
    @SerializedName("nomorPengajuan") val nomorPengajuan: String? = null,
    @SerializedName("jumlahPinjaman") val jumlahPinjaman: Double? = null,
    @SerializedName("tenorBulan") val tenorBulan: Int? = null,
    @SerializedName("tujuanPinjaman") val tujuanPinjaman: String? = null,
    @SerializedName("bunga") val bunga: Double? = null,
    @SerializedName("estimasiAngsuranBulanan") val estimasiAngsuranBulanan: Double? = null,
    @SerializedName("statusPengajuan") val statusPengajuan: String? = null,
    @SerializedName("catatanReview") val catatanReview: String? = null,
    @SerializedName("createdDate") val createdDate: String? = null
)
