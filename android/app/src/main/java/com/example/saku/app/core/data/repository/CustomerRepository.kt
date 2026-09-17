package com.example.saku.app.core.data.repository

import com.example.saku.app.core.network.ApiResult
import com.example.saku.app.core.network.dto.ChangePasswordRequestDto
import com.example.saku.app.core.network.dto.CustomerProfileDto
import com.example.saku.app.core.network.dto.PublicPlafondDto
import com.example.saku.app.core.network.dto.SimulasiPinjamanResponseDto
import com.example.saku.app.core.network.dto.UpdateDomisiliRequestDto
import com.example.saku.app.core.network.dto.UpdatePekerjaanRequestDto
import com.example.saku.app.core.network.dto.UpdateProfileRequestDto
import com.example.saku.app.core.network.dto.UpdateRekeningRequestDto
import kotlinx.coroutines.flow.Flow

interface CustomerRepository {
    val cachedProfileFlow: Flow<String?>
    suspend fun getCachedProfileJsonSync(): String?
    suspend fun getProfile(): ApiResult<CustomerProfileDto>
    suspend fun updateProfile(request: UpdateProfileRequestDto): ApiResult<CustomerProfileDto>
    suspend fun updateRekening(request: UpdateRekeningRequestDto): ApiResult<CustomerProfileDto>
    suspend fun updateDomisili(request: UpdateDomisiliRequestDto): ApiResult<CustomerProfileDto>
    suspend fun updatePekerjaan(request: UpdatePekerjaanRequestDto): ApiResult<CustomerProfileDto>
    suspend fun changePassword(request: ChangePasswordRequestDto): ApiResult<String>
    suspend fun updateKycDocuments(ktp: okhttp3.MultipartBody.Part?, selfie: okhttp3.MultipartBody.Part?): ApiResult<CustomerProfileDto>
    suspend fun getPublicPlafonds(): ApiResult<List<PublicPlafondDto>>
    suspend fun hitungSimulasi(jumlahPinjaman: Double, tenorBulan: Int): ApiResult<SimulasiPinjamanResponseDto>
    suspend fun saveCachedProfileJson(profileJson: String)
    suspend fun updateProfileData(nama: String?, email: String?, noHp: String?, isKycVerified: Boolean?)
}
