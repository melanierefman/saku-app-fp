package com.example.saku.app.core.network.dto

import com.google.gson.annotations.SerializedName

data class AlamatDetailDto(
    @SerializedName("jenisAlamat") val jenisAlamat: String? = null,
    @SerializedName("alamatLengkap") val alamatLengkap: String? = null,
    @SerializedName("rt") val rt: String? = null,
    @SerializedName("rw") val rw: String? = null,
    @SerializedName("kelurahan") val kelurahan: String? = null,
    @SerializedName("kecamatan") val kecamatan: String? = null,
    @SerializedName("kotaKabupaten") val kotaKabupaten: String? = null,
    @SerializedName("provinsi") val provinsi: String? = null,
    @SerializedName("kodePos") val kodePos: String? = null,
    @SerializedName("formattedAddress") val formattedAddress: String? = null
)

data class CustomerProfileDto(
    @SerializedName("id") val id: String? = null,
    @SerializedName("nik") val nik: String? = null,
    @SerializedName("nama") val nama: String? = null,
    @SerializedName("username") val username: String? = null,
    @SerializedName("email") val email: String? = null,
    @SerializedName("noHp") val noHp: String? = null,
    @SerializedName("namaIbuKandung") val namaIbuKandung: String? = null,
    @SerializedName("namaBank") val namaBank: String? = null,
    @SerializedName("noRekening") val noRekening: String? = null,
    @SerializedName("namaRekening") val namaRekening: String? = null,
    @SerializedName("status") val status: Boolean? = true,
    @SerializedName("isKycVerified") val isKycVerified: Boolean? = false,
    @SerializedName("statusVerifikasi") val statusVerifikasi: String? = null,
    @SerializedName("catatanVerifikasi") val catatanVerifikasi: String? = null,
    @SerializedName("tanggalVerifikasi") val tanggalVerifikasi: String? = null,
    @SerializedName("alamatKtp") val alamatKtp: AlamatDetailDto? = null,
    @SerializedName("alamatDomisili") val alamatDomisili: AlamatDetailDto? = null,
    @SerializedName("fotoKtp") val fotoKtp: String? = null,
    @SerializedName("fotoSelfie") val fotoSelfie: String? = null,
    @SerializedName("pekerjaan") val pekerjaan: String? = null,
    @SerializedName("tempatKerja") val tempatKerja: String? = null,
    @SerializedName("statusPekerjaan") val statusPekerjaan: String? = null,
    @SerializedName("penghasilanBulanan") val penghasilanBulanan: Double? = null,
    @SerializedName("lamaBekerjaBulan") val lamaBekerjaBulan: Int? = null,
    @SerializedName("totalCicilanLainBulanan") val totalCicilanLainBulanan: Double? = null,
    @SerializedName("skorKredit") val skorKredit: Int? = null,
    @SerializedName("statusScoring") val statusScoring: String? = null,
    @SerializedName("totalPlafond") val totalPlafond: Double? = null,
    @SerializedName("usedPlafond") val usedPlafond: Double? = null,
    @SerializedName("availablePlafond") val availablePlafond: Double? = null,
    @SerializedName("tierPlafond") val tierPlafond: String? = null,
    @SerializedName("sukuBunga") val sukuBunga: Double? = null,
    @SerializedName("biayaAdmin") val biayaAdmin: Double? = null,
    @SerializedName("createdDate") val createdDate: String? = null
)

// NOTIFIKASI DTO
data class NotifikasiItemDto(
    @SerializedName("id") val id: String? = null,
    @SerializedName("type") val type: String? = null,
    @SerializedName("channel") val channel: String? = null,
    @SerializedName("judul") val judul: String? = null,
    @SerializedName("pesan") val pesan: String? = null,
    @SerializedName("status") val status: String? = null,
    @SerializedName("isRead", alternate = ["read"]) val isRead: Boolean? = false,
    @SerializedName("pengajuanPinjamanId") val pengajuanPinjamanId: String? = null,
    @SerializedName("createdDate") val createdDate: String? = null
) {
    val isNotificationRead: Boolean
        get() = isRead == true ||
                "SUDAH_DIBACA".equals(status, ignoreCase = true) ||
                "READ".equals(status, ignoreCase = true)
}

data class UnreadNotifikasiCountDto(
    @SerializedName("unreadCount") val unreadCount: Long = 0L
)

data class UpdateRekeningRequestDto(
    @SerializedName("namaBank") val namaBank: String,
    @SerializedName("noRekening") val noRekening: String,
    @SerializedName("namaRekening") val namaRekening: String
)

data class UpdateDomisiliRequestDto(
    @SerializedName("alamatLengkap") val alamatLengkap: String,
    @SerializedName("rt") val rt: String? = null,
    @SerializedName("rw") val rw: String? = null,
    @SerializedName("kelurahan") val kelurahan: String? = null,
    @SerializedName("kecamatan") val kecamatan: String? = null,
    @SerializedName("kotaKabupaten") val kotaKabupaten: String? = null,
    @SerializedName("provinsi") val provinsi: String? = null,
    @SerializedName("kodePos") val kodePos: String? = null
)

data class UpdatePekerjaanRequestDto(
    @SerializedName("pekerjaan") val pekerjaan: String,
    @SerializedName("tempatKerja") val tempatKerja: String? = null,
    @SerializedName("statusPekerjaan") val statusPekerjaan: String? = null,
    @SerializedName("penghasilanBulanan") val penghasilanBulanan: Double? = null,
    @SerializedName("lamaBekerjaBulan") val lamaBekerjaBulan: Int? = null,
    @SerializedName("totalCicilanLainBulanan") val totalCicilanLainBulanan: Double? = null
)

data class ChangePasswordRequestDto(
    @SerializedName("oldPassword") val oldPassword: String,
    @SerializedName("newPassword") val newPassword: String,
    @SerializedName("confirmPassword") val confirmPassword: String
)

// PUBLIC SIMULATION & PLAFOND DTOs
data class SimulasiPinjamanResponseDto(
    @SerializedName("jumlahPinjaman") val jumlahPinjaman: Double? = null,
    @SerializedName("tenorBulan") val tenorBulan: Int? = null,
    @SerializedName("sukuBungaPersen") val sukuBungaPersen: Double? = null,
    @SerializedName("cicilanPokokBulanan") val cicilanPokokBulanan: Double? = null,
    @SerializedName("bungaBulanan") val bungaBulanan: Double? = null,
    @SerializedName("totalCicilanBulanan") val totalCicilanBulanan: Double? = null,
    @SerializedName("biayaAdmin") val biayaAdmin: Double? = null,
    @SerializedName("totalPembayaran") val totalPembayaran: Double? = null,
    @SerializedName("estimasiTierPlafond") val estimasiTierPlafond: String? = null
)

data class PublicPlafondDto(
    @SerializedName("id") val id: String? = null,
    @SerializedName("nama") val nama: String? = null,
    @SerializedName("minPlafond") val minPlafond: Double? = null,
    @SerializedName("maxPlafond") val maxPlafond: Double? = null,
    @SerializedName("bunga") val bunga: Double? = null,
    @SerializedName("biayaAdmin") val biayaAdmin: Double? = null,
    @SerializedName("minSkor") val minSkor: Int? = null,
    @SerializedName("maxSkor") val maxSkor: Int? = null
)

// LOAN APPLICATION DTOs
data class LoanApplicationItemDto(
    @SerializedName("id") val id: String? = null,
    @SerializedName("nomorPengajuan") val nomorPengajuan: String? = null,
    @SerializedName("customerId") val customerId: String? = null,
    @SerializedName("namaCustomer") val namaCustomer: String? = null,
    @SerializedName("branchId") val branchId: String? = null,
    @SerializedName("namaCabang") val namaCabang: String? = null,
    @SerializedName("kotaCabang") val kotaCabang: String? = null,
    @SerializedName("jumlahPinjaman") val jumlahPinjaman: Double? = null,
    @SerializedName("tenorBulan") val tenorBulan: Int? = null,
    @SerializedName("tujuanPinjaman") val tujuanPinjaman: String? = null,
    @SerializedName("bunga") val bunga: Double? = null,
    @SerializedName("biayaAdmin") val biayaAdmin: Double? = null,
    @SerializedName("estimasiAngsuranBulanan") val estimasiAngsuranBulanan: Double? = null,
    @SerializedName("skorKesehatan") val skorKesehatan: Int? = null,
    @SerializedName("statusPengajuan") val statusPengajuan: String? = null,
    @SerializedName("catatanReview") val catatanReview: String? = null,
    @SerializedName("createdDate") val createdDate: String? = null,
    @SerializedName("updatedDate") val updatedDate: String? = null,
    @SerializedName("dokumenList") val dokumenList: List<DokumenPinjamanDto>? = null,
    @SerializedName("listAngsuran") val listAngsuran: List<AngsuranItemDto>? = null
)

data class PengajuanPinjamanRequestDto(
    @SerializedName("jumlahPinjaman") val jumlahPinjaman: Double,
    @SerializedName("tenorBulan") val tenorBulan: Int,
    @SerializedName("tujuanPinjaman") val tujuanPinjaman: String,
    @SerializedName("mstBranchId") val mstBranchId: String? = null
)

data class PengajuanStepResponseDto(
    @SerializedName("pengajuanId") val pengajuanId: String? = null,
    @SerializedName("nomorPengajuan") val nomorPengajuan: String? = null,
    @SerializedName("step") val step: Int? = null,
    @SerializedName("message") val message: String? = null,
    @SerializedName("data") val data: LoanApplicationItemDto? = null
)

data class AngsuranItemDto(
    @SerializedName("id") val id: String? = null,
    @SerializedName("cicilanKe") val cicilanKe: Int? = null,
    @SerializedName("jumlahAngsuran") val jumlahAngsuran: Double? = null,
    @SerializedName("jatuhTempo") val jatuhTempo: String? = null,
    @SerializedName("statusBayar") val statusBayar: String? = null
)

data class DokumenPinjamanDto(
    @SerializedName("id") val id: String? = null,
    @SerializedName("docType") val docType: String? = null,
    @SerializedName("fileUrl") val fileUrl: String? = null,
    @SerializedName("createdDate") val createdDate: String? = null
)