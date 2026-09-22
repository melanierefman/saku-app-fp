package com.example.saku.app.core.data.repository

import com.example.saku.app.core.network.ApiResult
import com.example.saku.app.core.network.dto.ChangePasswordRequestDto
import com.example.saku.app.core.network.dto.CustomerProfileDto
import com.example.saku.app.core.network.dto.PublicPlafondDto
import com.example.saku.app.core.network.dto.SimulasiPinjamanResponseDto
import com.example.saku.app.core.network.dto.UpdateDomisiliRequestDto
import com.example.saku.app.core.network.dto.UpdatePekerjaanRequestDto
import com.example.saku.app.core.network.dto.UpdateRekeningRequestDto
import kotlinx.coroutines.flow.Flow

interface CustomerRepository {
    val cachedProfileFlow: Flow<String?>

    // Mengambil profil tersimpan format JSON secara sinkron
    suspend fun getCachedProfileJsonSync(): String?

    // Mengambil data profil lengkap nasabah
    suspend fun getProfile(): ApiResult<CustomerProfileDto>

    // Memperbarui informasi nomor rekening nasabah
    suspend fun updateRekening(request: UpdateRekeningRequestDto): ApiResult<CustomerProfileDto>

    // Memperbarui informasi alamat domisili nasabah
    suspend fun updateDomisili(request: UpdateDomisiliRequestDto): ApiResult<CustomerProfileDto>

    // Memperbarui informasi pekerjaan dan gaji nasabah
    suspend fun updatePekerjaan(request: UpdatePekerjaanRequestDto): ApiResult<CustomerProfileDto>

    // Mengubah kata sandi akun nasabah
    suspend fun changePassword(request: ChangePasswordRequestDto): ApiResult<String>

    // Mengunggah dokumen KYC terbaru (KTP & Selfie)
    suspend fun updateKycDocuments(ktp: okhttp3.MultipartBody.Part?, selfie: okhttp3.MultipartBody.Part?): ApiResult<CustomerProfileDto>

    // Mengambil daftar tier plafond publik
    suspend fun getPublicPlafonds(): ApiResult<List<PublicPlafondDto>>

    // Menghitung simulasi angsuran pinjaman
    suspend fun hitungSimulasi(jumlahPinjaman: Double, tenorBulan: Int): ApiResult<SimulasiPinjamanResponseDto>

    // Menyimpan string profil JSON ke preferences
    suspend fun saveCachedProfileJson(profileJson: String)

    // Memperbarui data ringkas profil di session lokal
    suspend fun updateProfileData(nama: String?, email: String?, noHp: String?, isKycVerified: Boolean?)
}