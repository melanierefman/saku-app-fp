package com.example.saku.app.core.network.api

import com.example.saku.app.core.network.ApiResponse
import com.example.saku.app.core.network.dto.AuthResponse
import com.example.saku.app.core.network.dto.ForgotPasswordRequest
import com.example.saku.app.core.network.dto.LoginRequest
import com.example.saku.app.core.network.dto.RefreshTokenRequest
import com.example.saku.app.core.network.dto.RegisterStep1Request
import com.example.saku.app.core.network.dto.RegisterStep2Request
import com.example.saku.app.core.network.dto.RegisterStep3Request
import com.example.saku.app.core.network.dto.RegisterStepResponse
import com.example.saku.app.core.network.dto.ResetPasswordRequest
import com.example.saku.app.core.network.dto.SendOtpResponse
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

interface AuthApiService {

    @GET("auth/customer/register/check-nik")
    suspend fun checkNik(
        @Query("nik") nik: String,
        @Query("customerId") customerId: String? = null
    ): Response<ApiResponse<Boolean>>

    @GET("auth/customer/register/check-phone")
    suspend fun checkPhone(
        @Query("phone") phone: String,
        @Query("customerId") customerId: String? = null
    ): Response<ApiResponse<Boolean>>

    @POST("auth/customer/login")
    suspend fun login(
        @Body request: LoginRequest
    ): Response<ApiResponse<AuthResponse>>

    @POST("auth/customer/forgot-password")
    suspend fun forgotPassword(
        @Body request: ForgotPasswordRequest
    ): Response<ApiResponse<SendOtpResponse>>

    @POST("auth/customer/reset-password")
    suspend fun resetPassword(
        @Body request: ResetPasswordRequest
    ): Response<ApiResponse<String>>

    @POST("auth/customer/refresh-token")
    suspend fun refreshToken(
        @Body request: RefreshTokenRequest
    ): Response<ApiResponse<AuthResponse>>

    @POST("auth/customer/logout")
    suspend fun logout(): Response<ApiResponse<String>>

    // OTP
    @POST("auth/otp/send")
    suspend fun sendOtp(
        @Body request: com.example.saku.app.core.network.dto.SendOtpRequest
    ): Response<ApiResponse<SendOtpResponse>>

    @POST("auth/otp/verify")
    suspend fun verifyOtp(
        @Body request: com.example.saku.app.core.network.dto.VerifyOtpRequest
    ): Response<ApiResponse<com.example.saku.app.core.network.dto.VerifyOtpResponse>>

    // 5-Step KYC Registration Endpoints
    @Multipart
    @POST("auth/customer/register/step1-ktp/{customerId}")
    suspend fun registerStep1Ktp(
        @Path("customerId") customerId: String,
        @Part ktp: MultipartBody.Part?,
        @Part("data") data: okhttp3.RequestBody
    ): Response<ApiResponse<RegisterStepResponse>>

    @POST("auth/customer/register/step1-ktp/{customerId}")
    suspend fun registerStep1KtpJson(
        @Path("customerId") customerId: String,
        @Body request: com.example.saku.app.core.network.dto.RegisterStep1KtpRequestDto
    ): Response<ApiResponse<RegisterStepResponse>>

    @PUT("auth/customer/register/step2-personal/{customerId}")
    suspend fun registerStep2Personal(
        @Path("customerId") customerId: String,
        @Body request: com.example.saku.app.core.network.dto.RegisterStep2PersonalRequestDto
    ): Response<ApiResponse<RegisterStepResponse>>

    @Multipart
    @POST("auth/customer/register/step3-liveness/{customerId}")
    suspend fun registerStep3Liveness(
        @Path("customerId") customerId: String,
        @Part selfie: MultipartBody.Part
    ): Response<ApiResponse<RegisterStepResponse>>

    @PUT("auth/customer/register/step4-tnc/{customerId}")
    suspend fun registerStep4Tnc(
        @Path("customerId") customerId: String
    ): Response<ApiResponse<RegisterStepResponse>>

    @POST("auth/customer/register/step5-complete/{customerId}")
    suspend fun registerStep5Complete(
        @Path("customerId") customerId: String,
        @Body request: com.example.saku.app.core.network.dto.RegisterStep5CompleteRequest
    ): Response<ApiResponse<RegisterStepResponse>>

    // Legacy (4-Step) Registration
    @POST("auth/customer/register/step1")
    suspend fun registerStep1(
        @Body request: RegisterStep1Request
    ): Response<ApiResponse<RegisterStepResponse>>

    @PUT("auth/customer/register/step2/{customerId}")
    suspend fun registerStep2(
        @Path("customerId") customerId: String,
        @Body request: RegisterStep2Request
    ): Response<ApiResponse<RegisterStepResponse>>

    @PUT("auth/customer/register/step3/{customerId}")
    suspend fun registerStep3(
        @Path("customerId") customerId: String,
        @Body request: RegisterStep3Request
    ): Response<ApiResponse<RegisterStepResponse>>

    @Multipart
    @POST("auth/customer/register/step4/{customerId}")
    suspend fun registerStep4(
        @Path("customerId") customerId: String,
        @Part ktp: MultipartBody.Part?,
        @Part selfie: MultipartBody.Part?
    ): Response<ApiResponse<RegisterStepResponse>>
}
