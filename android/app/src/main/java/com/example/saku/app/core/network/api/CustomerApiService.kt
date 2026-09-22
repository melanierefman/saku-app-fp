package com.example.saku.app.core.network.api

import com.example.saku.app.core.network.ApiResponse
import com.example.saku.app.core.network.dto.AngsuranItemDto
import com.example.saku.app.core.network.dto.ChangePasswordRequestDto
import com.example.saku.app.core.network.dto.CustomerProfileDto
import com.example.saku.app.core.network.dto.FcmTokenRequestDto
import com.example.saku.app.core.network.dto.LoanApplicationItemDto
import com.example.saku.app.core.network.dto.NotifikasiItemDto
import com.example.saku.app.core.network.dto.PengajuanPinjamanRequestDto
import com.example.saku.app.core.network.dto.PengajuanStepResponseDto
import com.example.saku.app.core.network.dto.PublicPlafondDto
import com.example.saku.app.core.network.dto.SimulasiPinjamanResponseDto
import com.example.saku.app.core.network.dto.UnreadNotifikasiCountDto
import com.example.saku.app.core.network.dto.UpdateDomisiliRequestDto
import com.example.saku.app.core.network.dto.UpdatePekerjaanRequestDto
import com.example.saku.app.core.network.dto.UpdateRekeningRequestDto
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface CustomerApiService {

    // Mengambil profil data nasabah login
    @GET("customer/profile")
    suspend fun getProfile(): Response<ApiResponse<CustomerProfileDto>>

    // Memperbarui informasi rekening bank nasabah
    @PUT("customer/profile/rekening")
    suspend fun updateRekening(
        @Body request: UpdateRekeningRequestDto
    ): Response<ApiResponse<CustomerProfileDto>>

    // Memperbarui alamat domisili nasabah
    @PUT("customer/profile/domisili")
    suspend fun updateDomisili(
        @Body request: UpdateDomisiliRequestDto
    ): Response<ApiResponse<CustomerProfileDto>>

    // Memperbarui data pekerjaan dan penghasilan nasabah
    @PUT("customer/profile/pekerjaan")
    suspend fun updatePekerjaan(
        @Body request: UpdatePekerjaanRequestDto
    ): Response<ApiResponse<CustomerProfileDto>>

    // Mengubah kata sandi akun nasabah
    @PUT("customer/profile/change-password")
    suspend fun changePassword(
        @Body request: ChangePasswordRequestDto
    ): Response<ApiResponse<String>>

    // Memperbarui token FCM perangkat nasabah
    @PUT("customer/profile/fcm-token")
    suspend fun updateFcmToken(
        @Body request: FcmTokenRequestDto
    ): Response<ApiResponse<String>>

    // Mengunggah pembaruan dokumen KYC KTP dan Selfie
    @Multipart
    @POST("customer/profile/kyc-documents")
    suspend fun updateKycDocuments(
        @Part ktp: MultipartBody.Part? = null,
        @Part selfie: MultipartBody.Part? = null
    ): Response<ApiResponse<CustomerProfileDto>>

    // Mengambil riwayat seluruh pengajuan pinjaman nasabah
    @GET("customer/pengajuan-pinjaman/my")
    suspend fun getMyLoans(): Response<ApiResponse<List<LoanApplicationItemDto>>>

    // Mengambil detail pengajuan pinjaman berdasarkan ID
    @GET("customer/pengajuan-pinjaman/{id}")
    suspend fun getLoanById(
        @Path("id") id: String
    ): Response<ApiResponse<LoanApplicationItemDto>>

    // Pengajuan pinjaman langkah 1: Formulir pinjaman dan cabang
    @POST("customer/pengajuan-pinjaman/step1")
    suspend fun submitLoanStep1(
        @Body request: PengajuanPinjamanRequestDto
    ): Response<ApiResponse<PengajuanStepResponseDto>>

    // Pengajuan pinjaman langkah 2: Unggah dokumen persyaratan
    @Multipart
    @POST("customer/pengajuan-pinjaman/step2/{pengajuanId}")
    suspend fun submitLoanStep2(
        @Path("pengajuanId") pengajuanId: String,
        @Part slipGaji: MultipartBody.Part? = null,
        @Part rekeningKoran: MultipartBody.Part? = null,
        @Part npwp: MultipartBody.Part? = null
    ): Response<ApiResponse<PengajuanStepResponseDto>>

    // Mengambil jadwal simulasi / rincian angsuran pinjaman
    @GET("customer/pengajuan-pinjaman/{pengajuanId}/angsuran")
    suspend fun getJadwalAngsuran(
        @Path("pengajuanId") pengajuanId: String
    ): Response<ApiResponse<List<AngsuranItemDto>>>

    // Mengambil daftar notifikasi nasabah
    @GET("customer/notifikasi")
    suspend fun getNotifications(
        @Query("status") status: String? = null
    ): Response<ApiResponse<List<NotifikasiItemDto>>>

    // Mengambil jumlah notifikasi yang belum dibaca
    @GET("customer/notifikasi/unread-count")
    suspend fun getUnreadCount(): Response<ApiResponse<UnreadNotifikasiCountDto>>

    // Menandai satu notifikasi telah dibaca
    @PUT("customer/notifikasi/{id}/read")
    suspend fun markNotificationRead(
        @Path("id") id: String
    ): Response<ApiResponse<NotifikasiItemDto>>

    // Menandai semua notifikasi telah dibaca
    @PUT("customer/notifikasi/read-all")
    suspend fun markAllNotificationsRead(): Response<ApiResponse<String>>

    // Mengambil daftar tier plafond publik
    @GET("public/plafond")
    suspend fun getPublicPlafonds(): Response<ApiResponse<List<PublicPlafondDto>>>

    // Menghitung estimasi simulasi pinjaman publik
    @GET("public/simulasi")
    suspend fun hitungSimulasi(
        @Query("jumlahPinjaman") jumlahPinjaman: Double,
        @Query("tenorBulan") tenorBulan: Int
    ): Response<ApiResponse<SimulasiPinjamanResponseDto>>
}