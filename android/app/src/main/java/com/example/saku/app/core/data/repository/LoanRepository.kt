package com.example.saku.app.core.data.repository

import com.example.saku.app.core.network.ApiResult
import com.example.saku.app.core.network.dto.AngsuranItemDto
import com.example.saku.app.core.network.dto.LoanApplicationItemDto
import com.example.saku.app.core.network.dto.PengajuanPinjamanRequestDto
import com.example.saku.app.core.network.dto.PengajuanStepResponseDto
import okhttp3.MultipartBody

interface LoanRepository {
    // Mengambil riwayat daftar pinjaman milik customer login
    suspend fun getMyLoans(): ApiResult<List<LoanApplicationItemDto>>

    // Mengambil rincian pinjaman berdasarkan ID
    suspend fun getLoanById(id: String): ApiResult<LoanApplicationItemDto>

    // Mengirim formulir data pinjaman langkah pertama
    suspend fun submitLoanStep1(request: PengajuanPinjamanRequestDto): ApiResult<PengajuanStepResponseDto>

    // Mengunggah file berkas dokumen pinjaman langkah kedua
    suspend fun submitLoanStep2(
        pengajuanId: String,
        slipGaji: MultipartBody.Part?,
        rekeningKoran: MultipartBody.Part?,
        npwp: MultipartBody.Part?
    ): ApiResult<PengajuanStepResponseDto>

    // Mengambil rincian jadwal cicilan angsuran pinjaman
    suspend fun getJadwalAngsuran(pengajuanId: String): ApiResult<List<AngsuranItemDto>>
}