package com.example.saku.app.core.network.api

import com.example.saku.app.core.network.ApiResponse
import com.example.saku.app.core.network.dto.AuthResponse
import com.example.saku.app.core.network.dto.ForgotPasswordRequest
import com.example.saku.app.core.network.dto.LoginRequest
import com.example.saku.app.core.network.dto.RefreshTokenRequest
import com.example.saku.app.core.network.dto.RegisterStep1KtpRequestDto
import com.example.saku.app.core.network.dto.RegisterStep2PersonalRequestDto
import com.example.saku.app.core.network.dto.RegisterStep5CompleteRequest
import com.example.saku.app.core.network.dto.RegisterStepResponse
import com.example.saku.app.core.network.dto.ResetPasswordRequest
import com.example.saku.app.core.network.dto.SendOtpRequest
import com.example.saku.app.core.network.dto.SendOtpResponse
import com.example.saku.app.core.network.dto.VerifyOtpRequest
import com.example.saku.app.core.network.dto.VerifyOtpResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface AuthApiService {

    // Validasi ketersediaan NIK customer
    @GET("auth/customer/register/check-nik")
    suspend fun checkNik(
        @Query("nik") nik: String,
        @Query("customerId") customerId: String? = null
    ): Response<ApiResponse<Boolean>>

    // Validasi ketersediaan nomor HP customer
    @GET("auth/customer/register/check-phone")
    suspend fun checkPhone(
        @Query("phone") phone: String,
        @Query("customerId") customerId: String? = null
    ): Response<ApiResponse<Boolean>>

    // Autentikasi login customer
    @POST("auth/customer/login")
    suspend fun login(
        @Body request: LoginRequest
    ): Response<ApiResponse<AuthResponse>>

    // Mengajukan permintaan reset password melalui OTP
    @POST("auth/customer/forgot-password")
    suspend fun forgotPassword(
        @Body request: ForgotPasswordRequest
    ): Response<ApiResponse<SendOtpResponse>>

    // Mengubah password baru dengan verifikasi token/OTP
    @POST("auth/customer/reset-password")
    suspend fun resetPassword(
        @Body request: ResetPasswordRequest
    ): Response<ApiResponse<String>>

    // Memperbarui token akses dengan refresh token
    @POST("auth/customer/refresh-token")
    suspend fun refreshToken(
        @Body request: RefreshTokenRequest
    ): Response<ApiResponse<AuthResponse>>

    // Keluar dari sesi login customer
    @POST("auth/customer/logout")
    suspend fun logout(): Response<ApiResponse<String>>

    // Mengirimkan kode OTP ke email
    @POST("auth/otp/send")
    suspend fun sendOtp(
        @Body request: SendOtpRequest
    ): Response<ApiResponse<SendOtpResponse>>

    // Memverifikasi kode OTP yang dimasukkan
    @POST("auth/otp/verify")
    suspend fun verifyOtp(
        @Body request: VerifyOtpRequest
    ): Response<ApiResponse<VerifyOtpResponse>>

    // Registrasi langkah 1: Unggah file dan data KTP
    @Multipart
    @POST("auth/customer/register/step1-ktp/{customerId}")
    suspend fun registerStep1Ktp(
        @Path("customerId") customerId: String,
        @Part ktp: MultipartBody.Part?,
        @Part("data") data: RequestBody
    ): Response<ApiResponse<RegisterStepResponse>>

    // Registrasi langkah 1: Simpan data KTP (format JSON)
    @POST("auth/customer/register/step1-ktp/{customerId}")
    suspend fun registerStep1KtpJson(
        @Path("customerId") customerId: String,
        @Body request: RegisterStep1KtpRequestDto
    ): Response<ApiResponse<RegisterStepResponse>>

    // Registrasi langkah 2: Simpan data pribadi dan pekerjaan
    @PUT("auth/customer/register/step2-personal/{customerId}")
    suspend fun registerStep2Personal(
        @Path("customerId") customerId: String,
        @Body request: RegisterStep2PersonalRequestDto
    ): Response<ApiResponse<RegisterStepResponse>>

    // Registrasi langkah 3: Unggah foto selfie liveness
    @Multipart
    @POST("auth/customer/register/step3-liveness/{customerId}")
    suspend fun registerStep3Liveness(
        @Path("customerId") customerId: String,
        @Part selfie: MultipartBody.Part
    ): Response<ApiResponse<RegisterStepResponse>>

    // Registrasi langkah 4: Konfirmasi syarat dan ketentuan
    @PUT("auth/customer/register/step4-tnc/{customerId}")
    suspend fun registerStep4Tnc(
        @Path("customerId") customerId: String
    ): Response<ApiResponse<RegisterStepResponse>>

    // Registrasi langkah 5: Pembuatan kredensial dan aktivasi akun
    @POST("auth/customer/register/step5-complete/{customerId}")
    suspend fun registerStep5Complete(
        @Path("customerId") customerId: String,
        @Body request: RegisterStep5CompleteRequest
    ): Response<ApiResponse<RegisterStepResponse>>
}