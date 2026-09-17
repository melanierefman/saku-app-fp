package com.example.saku.app.core.network.api

import com.example.saku.app.core.network.ApiResponse
import com.example.saku.app.core.network.dto.AngsuranItemDto
import com.example.saku.app.core.network.dto.ChangePasswordRequestDto
import com.example.saku.app.core.network.dto.CustomerProfileDto
import com.example.saku.app.core.network.dto.LoanApplicationItemDto
import com.example.saku.app.core.network.dto.NotifikasiItemDto
import com.example.saku.app.core.network.dto.PengajuanPinjamanRequestDto
import com.example.saku.app.core.network.dto.PengajuanStepResponseDto
import com.example.saku.app.core.network.dto.PublicPlafondDto
import com.example.saku.app.core.network.dto.SimulasiPinjamanResponseDto
import com.example.saku.app.core.network.dto.UnreadNotifikasiCountDto
import com.example.saku.app.core.network.dto.UpdateDomisiliRequestDto
import com.example.saku.app.core.network.dto.UpdatePekerjaanRequestDto
import com.example.saku.app.core.network.dto.UpdateProfileRequestDto
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

    // Profil Nasabah
    @GET("customer/profile")
    suspend fun getProfile(): Response<ApiResponse<CustomerProfileDto>>

    @PUT("customer/profile")
    suspend fun updateProfile(
        @Body request: UpdateProfileRequestDto
    ): Response<ApiResponse<CustomerProfileDto>>

    @PUT("customer/profile/rekening")
    suspend fun updateRekening(
        @Body request: UpdateRekeningRequestDto
    ): Response<ApiResponse<CustomerProfileDto>>

    @PUT("customer/profile/domisili")
    suspend fun updateDomisili(
        @Body request: UpdateDomisiliRequestDto
    ): Response<ApiResponse<CustomerProfileDto>>

    @PUT("customer/profile/pekerjaan")
    suspend fun updatePekerjaan(
        @Body request: UpdatePekerjaanRequestDto
    ): Response<ApiResponse<CustomerProfileDto>>

    @PUT("customer/profile/change-password")
    suspend fun changePassword(
        @Body request: ChangePasswordRequestDto
    ): Response<ApiResponse<String>>

    @PUT("customer/profile/fcm-token")
    suspend fun updateFcmToken(
        @Body request: com.example.saku.app.core.network.dto.FcmTokenRequestDto
    ): Response<ApiResponse<String>>

    @Multipart
    @POST("customer/profile/kyc-documents")
    suspend fun updateKycDocuments(
        @Part ktp: MultipartBody.Part? = null,
        @Part selfie: MultipartBody.Part? = null
    ): Response<ApiResponse<CustomerProfileDto>>


    // Pengajuan Pinjaman
    @GET("customer/pengajuan-pinjaman/my")
    suspend fun getMyLoans(): Response<ApiResponse<List<LoanApplicationItemDto>>>

    @GET("customer/pengajuan-pinjaman/{id}")
    suspend fun getLoanById(
        @Path("id") id: String
    ): Response<ApiResponse<LoanApplicationItemDto>>

    @POST("customer/pengajuan-pinjaman/step1")
    suspend fun submitLoanStep1(
        @Body request: PengajuanPinjamanRequestDto
    ): Response<ApiResponse<PengajuanStepResponseDto>>

    @Multipart
    @POST("customer/pengajuan-pinjaman/step2/{pengajuanId}")
    suspend fun submitLoanStep2(
        @Path("pengajuanId") pengajuanId: String,
        @Part slipGaji: MultipartBody.Part? = null,
        @Part rekeningKoran: MultipartBody.Part? = null,
        @Part npwp: MultipartBody.Part? = null
    ): Response<ApiResponse<PengajuanStepResponseDto>>

    @GET("customer/pengajuan-pinjaman/{pengajuanId}/angsuran")
    suspend fun getJadwalAngsuran(
        @Path("pengajuanId") pengajuanId: String
    ): Response<ApiResponse<List<AngsuranItemDto>>>


    // Notifikasi Nasabah
    @GET("customer/notifikasi")
    suspend fun getNotifications(
        @Query("status") status: String? = null
    ): Response<ApiResponse<List<NotifikasiItemDto>>>

    @GET("customer/notifikasi/unread-count")
    suspend fun getUnreadCount(): Response<ApiResponse<UnreadNotifikasiCountDto>>

    @GET("customer/notifikasi/{id}")
    suspend fun getNotificationDetail(
        @Path("id") id: String
    ): Response<ApiResponse<NotifikasiItemDto>>

    @PUT("customer/notifikasi/{id}/read")
    suspend fun markNotificationRead(
        @Path("id") id: String
    ): Response<ApiResponse<NotifikasiItemDto>>

    @PUT("customer/notifikasi/read-all")
    suspend fun markAllNotificationsRead(): Response<ApiResponse<String>>


    // Public Simulation & Plafond
    @GET("public/plafond")
    suspend fun getPublicPlafonds(): Response<ApiResponse<List<PublicPlafondDto>>>

    @GET("public/simulasi")
    suspend fun hitungSimulasi(
        @Query("jumlahPinjaman") jumlahPinjaman: Double,
        @Query("tenorBulan") tenorBulan: Int
    ): Response<ApiResponse<SimulasiPinjamanResponseDto>>
}
