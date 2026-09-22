package com.example.saku.app.core.data.repository

import com.example.saku.app.core.data.TokenManager
import com.example.saku.app.core.database.dao.CustomerDao
import com.example.saku.app.core.database.entity.CustomerProfileEntity
import com.example.saku.app.core.network.ApiClient
import com.example.saku.app.core.network.ApiResult
import com.example.saku.app.core.network.api.CustomerApiService
import com.example.saku.app.core.network.dto.ChangePasswordRequestDto
import com.example.saku.app.core.network.dto.CustomerProfileDto
import com.example.saku.app.core.network.dto.PublicPlafondDto
import com.example.saku.app.core.network.dto.SimulasiPinjamanResponseDto
import com.example.saku.app.core.network.dto.UpdateDomisiliRequestDto
import com.example.saku.app.core.network.dto.UpdatePekerjaanRequestDto
import com.example.saku.app.core.network.dto.UpdateRekeningRequestDto
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CustomerRepositoryImpl @Inject constructor(
    private val customerApiService: CustomerApiService,
    private val tokenManager: TokenManager,
    private val customerDao: CustomerDao
) : CustomerRepository {

    override val cachedProfileFlow: Flow<String?> = tokenManager.cachedProfileJsonFlow

    // Mengambil profil tersimpan format JSON secara sinkron
    override suspend fun getCachedProfileJsonSync(): String? {
        return tokenManager.getCachedProfileJsonSync()
    }

    // Mengambil data profil lengkap nasabah dengan fallback offline Room DB
    override suspend fun getProfile(): ApiResult<CustomerProfileDto> {
        return try {
            val response = customerApiService.getProfile()
            if (response.isSuccessful && response.body()?.data != null) {
                val profileDto = response.body()!!.data!!
                try {
                    customerDao.insertProfile(CustomerProfileEntity.fromDto(profileDto))
                } catch (dbEx: Exception) {
                    // Abaikan kesalahan penulisan database lokal
                }
                ApiResult.Success(profileDto, response.body()?.message)
            } else {
                try {
                    val cached = customerDao.getProfile()
                    if (cached != null) {
                        ApiResult.Success(cached.toDto(), "Menampilkan data tersimpan")
                    } else {
                        ApiResult.Error(ApiClient.parseError(response), response.code())
                    }
                } catch (dbEx: Exception) {
                    ApiResult.Error(ApiClient.parseError(response), response.code())
                }
            }
        } catch (e: Exception) {
            try {
                val cached = customerDao.getProfile()
                if (cached != null) {
                    ApiResult.Success(cached.toDto(), "Offline mode - data profil lokal")
                } else {
                    ApiResult.Error(e.localizedMessage ?: "Gagal memuat profil nasabah")
                }
            } catch (dbEx: Exception) {
                ApiResult.Error(e.localizedMessage ?: "Gagal memuat profil nasabah")
            }
        }
    }

    // Memperbarui informasi rekening bank nasabah
    override suspend fun updateRekening(request: UpdateRekeningRequestDto): ApiResult<CustomerProfileDto> {
        return try {
            val response = customerApiService.updateRekening(request)
            if (response.isSuccessful && response.body()?.data != null) {
                val profileDto = response.body()!!.data!!
                customerDao.insertProfile(CustomerProfileEntity.fromDto(profileDto))
                ApiResult.Success(profileDto, response.body()?.message)
            } else {
                ApiResult.Error(ApiClient.parseError(response), response.code())
            }
        } catch (e: Exception) {
            ApiResult.Error(e.localizedMessage ?: "Gagal memperbarui rekening")
        }
    }

    // Memperbarui alamat domisili nasabah
    override suspend fun updateDomisili(request: UpdateDomisiliRequestDto): ApiResult<CustomerProfileDto> {
        return try {
            val response = customerApiService.updateDomisili(request)
            if (response.isSuccessful && response.body()?.data != null) {
                val profileDto = response.body()!!.data!!
                customerDao.insertProfile(CustomerProfileEntity.fromDto(profileDto))
                ApiResult.Success(profileDto, response.body()?.message)
            } else {
                ApiResult.Error(ApiClient.parseError(response), response.code())
            }
        } catch (e: Exception) {
            ApiResult.Error(e.localizedMessage ?: "Gagal memperbarui domisili")
        }
    }

    // Memperbarui data pekerjaan dan penghasilan nasabah
    override suspend fun updatePekerjaan(request: UpdatePekerjaanRequestDto): ApiResult<CustomerProfileDto> {
        return try {
            val response = customerApiService.updatePekerjaan(request)
            if (response.isSuccessful && response.body()?.data != null) {
                val profileDto = response.body()!!.data!!
                customerDao.insertProfile(CustomerProfileEntity.fromDto(profileDto))
                ApiResult.Success(profileDto, response.body()?.message)
            } else {
                ApiResult.Error(ApiClient.parseError(response), response.code())
            }
        } catch (e: Exception) {
            ApiResult.Error(e.localizedMessage ?: "Gagal memperbarui data pekerjaan")
        }
    }

    // Mengubah kata sandi akun nasabah
    override suspend fun changePassword(request: ChangePasswordRequestDto): ApiResult<String> {
        return try {
            val response = customerApiService.changePassword(request)
            if (response.isSuccessful) {
                ApiResult.Success(response.body()?.data ?: "Kata sandi berhasil diubah", response.body()?.message)
            } else {
                ApiResult.Error(ApiClient.parseError(response), response.code())
            }
        } catch (e: Exception) {
            ApiResult.Error(e.localizedMessage ?: "Gagal mengubah kata sandi")
        }
    }

    // Mengambil daftar limit plafond pinjaman publik
    override suspend fun getPublicPlafonds(): ApiResult<List<PublicPlafondDto>> {
        return try {
            val response = customerApiService.getPublicPlafonds()
            if (response.isSuccessful && response.body()?.data != null) {
                ApiResult.Success(response.body()!!.data ?: emptyList(), response.body()?.message)
            } else {
                ApiResult.Error(ApiClient.parseError(response), response.code())
            }
        } catch (e: Exception) {
            ApiResult.Error(e.localizedMessage ?: "Gagal memuat daftar limit plafond")
        }
    }

    // Menghitung simulasi pinjaman cicilan bulanan
    override suspend fun hitungSimulasi(
        jumlahPinjaman: Double,
        tenorBulan: Int
    ): ApiResult<SimulasiPinjamanResponseDto> {
        return try {
            val response = customerApiService.hitungSimulasi(jumlahPinjaman, tenorBulan)
            if (response.isSuccessful && response.body()?.data != null) {
                ApiResult.Success(response.body()!!.data!!, response.body()?.message)
            } else {
                ApiResult.Error(ApiClient.parseError(response), response.code())
            }
        } catch (e: Exception) {
            ApiResult.Error(e.localizedMessage ?: "Gagal menghitung simulasi pinjaman")
        }
    }

    // Mengunggah dokumen KYC terbaru (KTP dan Selfie)
    override suspend fun updateKycDocuments(
        ktp: okhttp3.MultipartBody.Part?,
        selfie: okhttp3.MultipartBody.Part?
    ): ApiResult<CustomerProfileDto> {
        return try {
            val response = customerApiService.updateKycDocuments(ktp, selfie)
            if (response.isSuccessful && response.body()?.data != null) {
                val profile = response.body()!!.data!!
                customerDao.insertProfile(CustomerProfileEntity.fromDto(profile))
                ApiResult.Success(profile, response.body()?.message)
            } else {
                ApiResult.Error(ApiClient.parseError(response), response.code())
            }
        } catch (e: Exception) {
            ApiResult.Error(e.localizedMessage ?: "Gagal memperbarui dokumen KYC.")
        }
    }

    // Menyimpan string profil JSON ke preferences
    override suspend fun saveCachedProfileJson(profileJson: String) {
        tokenManager.saveCachedProfileJson(profileJson)
    }

    // Memperbarui data ringkas profil di session lokal
    override suspend fun updateProfileData(
        nama: String?,
        email: String?,
        noHp: String?,
        isKycVerified: Boolean?
    ) {
        tokenManager.updateProfileData(nama, email, noHp, isKycVerified)
    }
}