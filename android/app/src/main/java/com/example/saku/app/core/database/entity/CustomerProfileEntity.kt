package com.example.saku.app.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.saku.app.core.network.dto.AlamatDetailDto
import com.example.saku.app.core.network.dto.CustomerProfileDto

@Entity(tableName = "customer_profile")
data class CustomerProfileEntity(
    @PrimaryKey
    val id: String,
    val nik: String? = null,
    val nama: String? = null,
    val username: String? = null,
    val email: String? = null,
    val noHp: String? = null,
    val namaBank: String? = null,
    val noRekening: String? = null,
    val namaRekening: String? = null,
    val isKycVerified: Boolean = false,
    val statusVerifikasi: String? = null,
    val catatanVerifikasi: String? = null,
    val pekerjaan: String? = null,
    val tempatKerja: String? = null,
    val statusPekerjaan: String? = null,
    val penghasilanBulanan: Double? = null,
    val lamaBekerjaBulan: Int? = null,
    val totalCicilanLainBulanan: Double? = null,
    val skorKredit: Int? = null,
    val statusScoring: String? = null,
    val totalPlafond: Double? = null,
    val usedPlafond: Double? = null,
    val availablePlafond: Double? = null,
    val tierPlafond: String? = null,
    val sukuBunga: Double? = null,
    val biayaAdmin: Double? = null,
    val alamatKtpFormatted: String? = null,
    val alamatDomisiliFormatted: String? = null,
    val fotoKtp: String? = null,
    val fotoSelfie: String? = null,
    val createdDate: String? = null,
    val lastUpdated: Long = System.currentTimeMillis()
) {
    fun toDto(): CustomerProfileDto {
        return CustomerProfileDto(
            id = id,
            nik = nik,
            nama = nama,
            username = username,
            email = email,
            noHp = noHp,
            namaBank = namaBank,
            noRekening = noRekening,
            namaRekening = namaRekening,
            isKycVerified = isKycVerified,
            statusVerifikasi = statusVerifikasi,
            catatanVerifikasi = catatanVerifikasi,
            pekerjaan = pekerjaan,
            tempatKerja = tempatKerja,
            statusPekerjaan = statusPekerjaan,
            penghasilanBulanan = penghasilanBulanan,
            lamaBekerjaBulan = lamaBekerjaBulan,
            totalCicilanLainBulanan = totalCicilanLainBulanan,
            skorKredit = skorKredit,
            statusScoring = statusScoring,
            totalPlafond = totalPlafond,
            usedPlafond = usedPlafond,
            availablePlafond = availablePlafond,
            tierPlafond = tierPlafond,
            sukuBunga = sukuBunga,
            biayaAdmin = biayaAdmin,
            alamatKtp = if (alamatKtpFormatted != null) AlamatDetailDto(formattedAddress = alamatKtpFormatted, alamatLengkap = alamatKtpFormatted) else null,
            alamatDomisili = if (alamatDomisiliFormatted != null) AlamatDetailDto(formattedAddress = alamatDomisiliFormatted, alamatLengkap = alamatDomisiliFormatted) else null,
            fotoKtp = fotoKtp,
            fotoSelfie = fotoSelfie,
            createdDate = createdDate
        )
    }

    companion object {
        fun fromDto(dto: CustomerProfileDto): CustomerProfileEntity {
            return CustomerProfileEntity(
                id = dto.id ?: "current_user",
                nik = dto.nik,
                nama = dto.nama,
                username = dto.username,
                email = dto.email,
                noHp = dto.noHp,
                namaBank = dto.namaBank,
                noRekening = dto.noRekening,
                namaRekening = dto.namaRekening,
                isKycVerified = dto.isKycVerified == true,
                statusVerifikasi = dto.statusVerifikasi,
                catatanVerifikasi = dto.catatanVerifikasi,
                pekerjaan = dto.pekerjaan,
                tempatKerja = dto.tempatKerja,
                statusPekerjaan = dto.statusPekerjaan,
                penghasilanBulanan = dto.penghasilanBulanan,
                lamaBekerjaBulan = dto.lamaBekerjaBulan,
                totalCicilanLainBulanan = dto.totalCicilanLainBulanan,
                skorKredit = dto.skorKredit,
                statusScoring = dto.statusScoring,
                totalPlafond = dto.totalPlafond,
                usedPlafond = dto.usedPlafond,
                availablePlafond = dto.availablePlafond,
                tierPlafond = dto.tierPlafond,
                sukuBunga = dto.sukuBunga,
                biayaAdmin = dto.biayaAdmin,
                alamatKtpFormatted = dto.alamatKtp?.formattedAddress ?: dto.alamatKtp?.alamatLengkap,
                alamatDomisiliFormatted = dto.alamatDomisili?.formattedAddress ?: dto.alamatDomisili?.alamatLengkap,
                fotoKtp = dto.fotoKtp,
                fotoSelfie = dto.fotoSelfie,
                createdDate = dto.createdDate
            )
        }
    }
}