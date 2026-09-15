package com.example.saku.app.core.data.repository

import com.example.saku.app.core.network.ApiClient
import com.example.saku.app.core.network.ApiResult
import com.example.saku.app.core.network.api.CustomerApiService
import com.example.saku.app.core.network.dto.AngsuranItemDto
import com.example.saku.app.core.network.dto.LoanApplicationItemDto
import com.example.saku.app.core.network.dto.PengajuanPinjamanRequestDto
import com.example.saku.app.core.network.dto.PengajuanStepResponseDto
import okhttp3.MultipartBody

class LoanRepositoryImpl(
    private val customerApiService: CustomerApiService
) : LoanRepository {

    override suspend fun getMyLoans(): ApiResult<List<LoanApplicationItemDto>> {
        return try {
            val response = customerApiService.getMyLoans()
            if (response.isSuccessful && response.body()?.data != null) {
                ApiResult.Success(response.body()!!.data ?: emptyList(), response.body()?.message)
            } else {
                ApiResult.Error(ApiClient.parseError(response), response.code())
            }
        } catch (e: Exception) {
            ApiResult.Error(e.localizedMessage ?: "Gagal memuat daftar pinjaman")
        }
    }

    override suspend fun getLoanById(id: String): ApiResult<LoanApplicationItemDto> {
        return try {
            val response = customerApiService.getLoanById(id)
            if (response.isSuccessful && response.body()?.data != null) {
                ApiResult.Success(response.body()!!.data!!, response.body()?.message)
            } else {
                ApiResult.Error(ApiClient.parseError(response), response.code())
            }
        } catch (e: Exception) {
            ApiResult.Error(e.localizedMessage ?: "Gagal memuat detail pinjaman")
        }
    }

    override suspend fun submitLoanStep1(request: PengajuanPinjamanRequestDto): ApiResult<PengajuanStepResponseDto> {
        return try {
            val response = customerApiService.submitLoanStep1(request)
            if (response.isSuccessful && response.body()?.data != null) {
                ApiResult.Success(response.body()!!.data!!, response.body()?.message)
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
                ApiResult.Success(response.body()!!.data!!, response.body()?.message)
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
