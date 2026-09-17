package com.example.saku.app.core.data.repository

import com.example.saku.app.core.network.ApiResult
import com.example.saku.app.core.network.dto.AngsuranItemDto
import com.example.saku.app.core.network.dto.LoanApplicationItemDto
import com.example.saku.app.core.network.dto.PengajuanPinjamanRequestDto
import com.example.saku.app.core.network.dto.PengajuanStepResponseDto
import okhttp3.MultipartBody

interface LoanRepository {
    suspend fun getMyLoans(): ApiResult<List<LoanApplicationItemDto>>
    suspend fun getLoanById(id: String): ApiResult<LoanApplicationItemDto>
    suspend fun submitLoanStep1(request: PengajuanPinjamanRequestDto): ApiResult<PengajuanStepResponseDto>
    suspend fun submitLoanStep2(
        pengajuanId: String,
        slipGaji: MultipartBody.Part?,
        rekeningKoran: MultipartBody.Part?,
        npwp: MultipartBody.Part?
    ): ApiResult<PengajuanStepResponseDto>
    suspend fun getJadwalAngsuran(pengajuanId: String): ApiResult<List<AngsuranItemDto>>
}
