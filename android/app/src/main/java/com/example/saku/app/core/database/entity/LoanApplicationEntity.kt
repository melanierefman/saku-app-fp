package com.example.saku.app.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.saku.app.core.network.dto.AngsuranItemDto
import com.example.saku.app.core.network.dto.LoanApplicationItemDto
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

@Entity(tableName = "loan_applications")
data class LoanApplicationEntity(
    @PrimaryKey
    val id: String,
    val nomorPengajuan: String?,
    val customerId: String?,
    val namaCustomer: String?,
    val branchId: String?,
    val namaCabang: String?,
    val kotaCabang: String?,
    val jumlahPinjaman: Double?,
    val tenorBulan: Int?,
    val tujuanPinjaman: String?,
    val bunga: Double?,
    val biayaAdmin: Double?,
    val estimasiAngsuranBulanan: Double?,
    val skorKesehatan: Int?,
    val statusPengajuan: String?,
    val catatanReview: String?,
    val createdDate: String?,
    val updatedDate: String?,
    val listAngsuranJson: String? = null,
    val lastUpdated: Long = System.currentTimeMillis()
) {
    fun toDto(): LoanApplicationItemDto {
        val gson = Gson()
        val angsuranList: List<AngsuranItemDto>? = if (!listAngsuranJson.isNullOrBlank()) {
            try {
                val type = object : TypeToken<List<AngsuranItemDto>>() {}.type
                gson.fromJson(listAngsuranJson, type)
            } catch (e: Exception) {
                null
            }
        } else null

        return LoanApplicationItemDto(
            id = id,
            nomorPengajuan = nomorPengajuan,
            customerId = customerId,
            namaCustomer = namaCustomer,
            branchId = branchId,
            namaCabang = namaCabang,
            kotaCabang = kotaCabang,
            jumlahPinjaman = jumlahPinjaman,
            tenorBulan = tenorBulan,
            tujuanPinjaman = tujuanPinjaman,
            bunga = bunga,
            biayaAdmin = biayaAdmin,
            estimasiAngsuranBulanan = estimasiAngsuranBulanan,
            skorKesehatan = skorKesehatan,
            statusPengajuan = statusPengajuan,
            catatanReview = catatanReview,
            createdDate = createdDate,
            updatedDate = updatedDate,
            listAngsuran = angsuranList
        )
    }

    companion object {
        fun fromDto(dto: LoanApplicationItemDto): LoanApplicationEntity {
            val gson = Gson()
            val angsuranJson = if (!dto.listAngsuran.isNullOrEmpty()) {
                gson.toJson(dto.listAngsuran)
            } else null

            return LoanApplicationEntity(
                id = dto.id ?: java.util.UUID.randomUUID().toString(),
                nomorPengajuan = dto.nomorPengajuan,
                customerId = dto.customerId,
                namaCustomer = dto.namaCustomer,
                branchId = dto.branchId,
                namaCabang = dto.namaCabang,
                kotaCabang = dto.kotaCabang,
                jumlahPinjaman = dto.jumlahPinjaman,
                tenorBulan = dto.tenorBulan,
                tujuanPinjaman = dto.tujuanPinjaman,
                bunga = dto.bunga,
                biayaAdmin = dto.biayaAdmin,
                estimasiAngsuranBulanan = dto.estimasiAngsuranBulanan,
                skorKesehatan = dto.skorKesehatan,
                statusPengajuan = dto.statusPengajuan,
                catatanReview = dto.catatanReview,
                createdDate = dto.createdDate,
                updatedDate = dto.updatedDate,
                listAngsuranJson = angsuranJson
            )
        }
    }
}