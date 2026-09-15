package com.example.saku.app.core.data.repository

import com.example.saku.app.core.data.UserSession
import com.example.saku.app.core.network.ApiResult
import com.example.saku.app.core.network.dto.AuthResponse
import com.example.saku.app.core.network.dto.RegisterStep1KtpRequestDto
import com.example.saku.app.core.network.dto.RegisterStep2PersonalRequestDto
import com.example.saku.app.core.network.dto.RegisterStep5CompleteRequest
import com.example.saku.app.core.network.dto.RegisterStepResponse
import com.example.saku.app.core.network.dto.ResetPasswordRequest
import com.example.saku.app.core.network.dto.SendOtpResponse
import com.example.saku.app.core.network.dto.VerifyOtpResponse
import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody
import okhttp3.RequestBody

interface AuthRepository {
    val userSession: Flow<UserSession?>
    val isLoggedIn: Flow<Boolean>

    suspend fun login(usernameOrEmail: String, password: String): ApiResult<AuthResponse>
    suspend fun forgotPassword(emailOrPhone: String): ApiResult<SendOtpResponse>
    suspend fun resetPassword(request: ResetPasswordRequest): ApiResult<String>
    suspend fun logout(): ApiResult<Unit>
    suspend fun clearSession()

    // Registration & OTP
    suspend fun sendOtp(email: String, purpose: String = "REGISTRATION"): ApiResult<SendOtpResponse>
    suspend fun verifyOtp(email: String, otpCode: String, purpose: String = "REGISTRATION"): ApiResult<VerifyOtpResponse>
    suspend fun registerStep1Ktp(customerId: String, ktp: MultipartBody.Part?, data: RequestBody): ApiResult<RegisterStepResponse>
    suspend fun registerStep1KtpJson(customerId: String, request: RegisterStep1KtpRequestDto): ApiResult<RegisterStepResponse>
    suspend fun registerStep2Personal(customerId: String, request: RegisterStep2PersonalRequestDto): ApiResult<RegisterStepResponse>
    suspend fun registerStep3Liveness(customerId: String, selfie: MultipartBody.Part): ApiResult<RegisterStepResponse>
    suspend fun registerStep4Tnc(customerId: String): ApiResult<RegisterStepResponse>
    suspend fun registerStep5Complete(customerId: String, request: RegisterStep5CompleteRequest): ApiResult<RegisterStepResponse>
}
