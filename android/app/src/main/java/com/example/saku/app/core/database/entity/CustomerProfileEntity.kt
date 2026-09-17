package com.example.saku.app.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.saku.app.core.network.dto.AlamatDetailDto
import com.example.saku.app.core.network.dto.CustomerProfileDto

@Entity(tableName = "customer_profile")
data class CustomerProfileEntity(
    @PrimaryKey
    val id: String,
    val nik: String?,
    val nama: String?,
    val username: String?,
    val email: String?,
    val noHp: String?,
    val namaBank: String?,
    val noRekening: String?,
    val namaRekening: String?,
    val isKycVerified: Boolean,
    val statusVerifikasi: String?,
    val catatanVerifikasi: String?,
    val pekerjaan: String?,
    val tempatKerja: String?,
    val statusPekerjaan: String?,
    val penghasilanBulanan: Double?,
    val lamaBekerjaBulan: Int?,
    val totalCicilanLainBulanan: Double?,
    val totalPlafond: Double?,
    val usedPlafond: Double?,
    val availablePlafond: Double?,
    val tierPlafond: String?,
    val sukuBunga: Double?,
    val biayaAdmin: Double?,
    val alamatKtpFormatted: String?,
    val alamatDomisiliFormatted: String?,
    val fotoKtp: String?,
    val fotoSelfie: String?,
    val createdDate: String?,
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
