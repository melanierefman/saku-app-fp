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
import com.example.saku.app.core.network.dto.UpdateProfileRequestDto
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

    override suspend fun getCachedProfileJsonSync(): String? {
        return tokenManager.getCachedProfileJsonSync()
    }

    override suspend fun getProfile(): ApiResult<CustomerProfileDto> {
        return try {
            val response = customerApiService.getProfile()
            if (response.isSuccessful && response.body()?.data != null) {
                val profileDto = response.body()!!.data!!
                // Save to Room Database safely
                try {
                    customerDao.insertProfile(CustomerProfileEntity.fromDto(profileDto))
                } catch (dbEx: Exception) {
                    // ignore DB caching error so API response is not blocked
                }
                ApiResult.Success(profileDto, response.body()?.message)
            } else {
                // Try offline cache from Room
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
            // Offline fallback from Room Database
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

    override suspend fun updateProfile(request: UpdateProfileRequestDto): ApiResult<CustomerProfileDto> {
        return try {
            val response = customerApiService.updateProfile(request)
            if (response.isSuccessful && response.body()?.data != null) {
                val profileDto = response.body()!!.data!!
                customerDao.insertProfile(CustomerProfileEntity.fromDto(profileDto))
                ApiResult.Success(profileDto, response.body()?.message)
            } else {
                ApiResult.Error(ApiClient.parseError(response), response.code())
            }
        } catch (e: Exception) {
            ApiResult.Error(e.localizedMessage ?: "Gagal memperbarui profil")
        }
    }

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

    override suspend fun saveCachedProfileJson(profileJson: String) {
        tokenManager.saveCachedProfileJson(profileJson)
    }

    override suspend fun updateProfileData(
        nama: String?,
        email: String?,
        noHp: String?,
        isKycVerified: Boolean?
    ) {
        tokenManager.updateProfileData(nama, email, noHp, isKycVerified)
    }
}
