package com.example.saku.app.core.data.repository

import com.example.saku.app.core.database.dao.LoanDao
import com.example.saku.app.core.database.entity.LoanApplicationEntity
import com.example.saku.app.core.network.ApiClient
import com.example.saku.app.core.network.ApiResult
import com.example.saku.app.core.network.api.CustomerApiService
import com.example.saku.app.core.network.dto.AngsuranItemDto
import com.example.saku.app.core.network.dto.LoanApplicationItemDto
import com.example.saku.app.core.network.dto.PengajuanPinjamanRequestDto
import com.example.saku.app.core.network.dto.PengajuanStepResponseDto
import okhttp3.MultipartBody
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LoanRepositoryImpl @Inject constructor(
    private val customerApiService: CustomerApiService,
    private val loanDao: LoanDao
) : LoanRepository {

    override suspend fun getMyLoans(): ApiResult<List<LoanApplicationItemDto>> {
        return try {
            val response = customerApiService.getMyLoans()
            if (response.isSuccessful && response.body()?.data != null) {
                val list = response.body()!!.data ?: emptyList()
                // Cache into Room Database
                if (list.isNotEmpty()) {
                    loanDao.insertLoans(list.map { LoanApplicationEntity.fromDto(it) })
                }
                ApiResult.Success(list, response.body()?.message)
            } else {
                // Offline fallback from Room
                val cached = loanDao.getAllLoans()
                if (cached.isNotEmpty()) {
                    ApiResult.Success(cached.map { it.toDto() }, "Menampilkan data pinjaman tersimpan")
                } else {
                    ApiResult.Error(ApiClient.parseError(response), response.code())
                }
            }
        } catch (e: Exception) {
            // Offline fallback from Room
            val cached = loanDao.getAllLoans()
            if (cached.isNotEmpty()) {
                ApiResult.Success(cached.map { it.toDto() }, "Offline mode - data pinjaman lokal")
            } else {
                ApiResult.Error(e.localizedMessage ?: "Gagal memuat daftar pinjaman")
            }
        }
    }

    override suspend fun getLoanById(id: String): ApiResult<LoanApplicationItemDto> {
        return try {
            val response = customerApiService.getLoanById(id)
            if (response.isSuccessful && response.body()?.data != null) {
                val loan = response.body()!!.data!!
                loanDao.insertLoan(LoanApplicationEntity.fromDto(loan))
                ApiResult.Success(loan, response.body()?.message)
            } else {
                val cached = loanDao.getLoanById(id)
                if (cached != null) {
                    ApiResult.Success(cached.toDto(), "Menampilkan detail pinjaman tersimpan")
                } else {
                    ApiResult.Error(ApiClient.parseError(response), response.code())
                }
            }
        } catch (e: Exception) {
            val cached = loanDao.getLoanById(id)
            if (cached != null) {
                ApiResult.Success(cached.toDto(), "Offline mode - data detail lokal")
            } else {
                ApiResult.Error(e.localizedMessage ?: "Gagal memuat detail pinjaman")
            }
        }
    }

    override suspend fun submitLoanStep1(request: PengajuanPinjamanRequestDto): ApiResult<PengajuanStepResponseDto> {
        return try {
            val response = customerApiService.submitLoanStep1(request)
            if (response.isSuccessful && response.body()?.data != null) {
                val stepData = response.body()!!.data!!
                stepData.data?.let { loanItem ->
                    loanDao.insertLoan(LoanApplicationEntity.fromDto(loanItem))
                }
                ApiResult.Success(stepData, response.body()?.message)
            } else {
                ApiResult.Error(ApiClient.parseError(response), response.code())
            }
        } catch (e: Exception) {
            ApiResult.Error(e.localizedMessage ?: "Gagal membuat pengajuan pinjaman")
        }
    }

    override suspend fun submitLoanStep2(
        pengajuanId: String,
        slipGaji: MultipartBody.Part?,
        rekeningKoran: MultipartBody.Part?,
        npwp: MultipartBody.Part?
    ): ApiResult<PengajuanStepResponseDto> {
        return try {
            val response = customerApiService.submitLoanStep2(pengajuanId, slipGaji, rekeningKoran, npwp)
            if (response.isSuccessful && response.body()?.data != null) {
                val stepData = response.body()!!.data!!
                stepData.data?.let { loanItem ->
                    loanDao.insertLoan(LoanApplicationEntity.fromDto(loanItem))
                }
                ApiResult.Success(stepData, response.body()?.message)
            } else {
                ApiResult.Error(ApiClient.parseError(response), response.code())
            }
        } catch (e: Exception) {
            ApiResult.Error(e.localizedMessage ?: "Gagal mengunggah dokumen pinjaman")
        }
    }

    override suspend fun getJadwalAngsuran(pengajuanId: String): ApiResult<List<AngsuranItemDto>> {
        return try {
            val response = customerApiService.getJadwalAngsuran(pengajuanId)
            if (response.isSuccessful && response.body()?.data != null) {
                ApiResult.Success(response.body()!!.data ?: emptyList(), response.body()?.message)
            } else {
                ApiResult.Error(ApiClient.parseError(response), response.code())
            }
        } catch (e: Exception) {
            ApiResult.Error(e.localizedMessage ?: "Gagal memuat jadwal angsuran")
        }
    }
}
