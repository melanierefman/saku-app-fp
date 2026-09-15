package com.example.saku.app.core.data.repository

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.saku.app.core.data.TokenManager
import com.example.saku.app.core.network.ApiResponse
import com.example.saku.app.core.network.ApiResult
import com.example.saku.app.core.network.api.AuthApiService
import com.example.saku.app.core.network.dto.AuthResponse
import com.example.saku.app.core.network.dto.ForgotPasswordRequest
import com.example.saku.app.core.network.dto.LoginRequest
import com.example.saku.app.core.network.dto.RefreshTokenRequest
import com.example.saku.app.core.network.dto.RegisterStep1KtpRequestDto
import com.example.saku.app.core.network.dto.RegisterStep1Request
import com.example.saku.app.core.network.dto.RegisterStep2PersonalRequestDto
import com.example.saku.app.core.network.dto.RegisterStep2Request
import com.example.saku.app.core.network.dto.RegisterStep3Request
import com.example.saku.app.core.network.dto.RegisterStep5CompleteRequest
import com.example.saku.app.core.network.dto.RegisterStepResponse
import com.example.saku.app.core.network.dto.ResetPasswordRequest
import com.example.saku.app.core.network.dto.SendOtpRequest
import com.example.saku.app.core.network.dto.SendOtpResponse
import com.example.saku.app.core.network.dto.UserLoginProfileDto
import com.example.saku.app.core.network.dto.VerifyOtpRequest
import com.example.saku.app.core.network.dto.VerifyOtpResponse
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import retrofit2.Response

@RunWith(AndroidJUnit4::class)
class AuthRepositoryAndroidTest {

    private lateinit var context: Context
    private lateinit var tokenManager: TokenManager
    private lateinit var fakeAuthApiService: FakeAuthApiService
    private lateinit var authRepository: AuthRepository

    class FakeAuthApiService : AuthApiService {
        var shouldLoginSucceed = true
        var mockAccessToken = "access_token_123"
        var mockRefreshToken = "refresh_token_456"

        override suspend fun login(request: LoginRequest): Response<ApiResponse<AuthResponse>> {
            return if (shouldLoginSucceed) {
                Response.success(
                    ApiResponse(
                        statusCode = 200,
                        message = "Login berhasil",
                        data = AuthResponse(
                            accessToken = mockAccessToken,
                            refreshToken = mockRefreshToken,
                            user = UserLoginProfileDto(
                                id = "usr-123",
                                username = request.username,
                                nama = "Melanie Refman",
                                email = "melanie@example.com",
                                noHp = "08123456789",
                                role = "CUSTOMER",
                                isKycVerified = true
                            )
                        )
                    )
                )
            } else {
                Response.error(401, "{\"message\":\"Username atau password salah\"}".toResponseBody())
            }
        }

        override suspend fun forgotPassword(request: ForgotPasswordRequest): Response<ApiResponse<SendOtpResponse>> =
            Response.success(ApiResponse(statusCode = 200, message = "OTP Terkirim", data = SendOtpResponse("OK", 5)))

        override suspend fun resetPassword(request: ResetPasswordRequest): Response<ApiResponse<String>> =
            Response.success(ApiResponse(statusCode = 200, message = "Password diubah", data = "OK"))

        override suspend fun refreshToken(request: RefreshTokenRequest): Response<ApiResponse<AuthResponse>> =
            Response.success(ApiResponse(statusCode = 200, data = AuthResponse(accessToken = "new_token", refreshToken = "new_refresh")))

        override suspend fun logout(): Response<ApiResponse<String>> =
            Response.success(ApiResponse(statusCode = 200, message = "Logout berhasil", data = "OK"))

        override suspend fun sendOtp(request: SendOtpRequest): Response<ApiResponse<SendOtpResponse>> =
            Response.success(ApiResponse(statusCode = 200, data = SendOtpResponse("OK", 5)))

        override suspend fun verifyOtp(request: VerifyOtpRequest): Response<ApiResponse<VerifyOtpResponse>> =
            Response.success(ApiResponse(statusCode = 200, data = VerifyOtpResponse(valid = true)))

        override suspend fun registerStep1Ktp(customerId: String, ktp: MultipartBody.Part?, data: RequestBody): Response<ApiResponse<RegisterStepResponse>> =
            Response.success(ApiResponse(statusCode = 200, data = RegisterStepResponse(customerId, 1, "OK")))

        override suspend fun registerStep1KtpJson(customerId: String, request: RegisterStep1KtpRequestDto): Response<ApiResponse<RegisterStepResponse>> =
            Response.success(ApiResponse(statusCode = 200, data = RegisterStepResponse(customerId, 1, "OK")))

        override suspend fun registerStep2Personal(customerId: String, request: RegisterStep2PersonalRequestDto): Response<ApiResponse<RegisterStepResponse>> =
            Response.success(ApiResponse(statusCode = 200, data = RegisterStepResponse(customerId, 2, "OK")))

        override suspend fun registerStep3Liveness(customerId: String, selfie: MultipartBody.Part): Response<ApiResponse<RegisterStepResponse>> =
            Response.success(ApiResponse(statusCode = 200, data = RegisterStepResponse(customerId, 3, "OK")))

        override suspend fun registerStep4Tnc(customerId: String): Response<ApiResponse<RegisterStepResponse>> =
            Response.success(ApiResponse(statusCode = 200, data = RegisterStepResponse(customerId, 4, "OK")))

        override suspend fun registerStep5Complete(customerId: String, request: RegisterStep5CompleteRequest): Response<ApiResponse<RegisterStepResponse>> =
            Response.success(ApiResponse(statusCode = 200, data = RegisterStepResponse(customerId, 5, "OK")))

        override suspend fun registerStep1(request: RegisterStep1Request): Response<ApiResponse<RegisterStepResponse>> =
            Response.success(ApiResponse(statusCode = 200, data = RegisterStepResponse("1", 1, "OK")))

        override suspend fun registerStep2(customerId: String, request: RegisterStep2Request): Response<ApiResponse<RegisterStepResponse>> =
            Response.success(ApiResponse(statusCode = 200, data = RegisterStepResponse(customerId, 2, "OK")))

        override suspend fun registerStep3(customerId: String, request: RegisterStep3Request): Response<ApiResponse<RegisterStepResponse>> =
            Response.success(ApiResponse(statusCode = 200, data = RegisterStepResponse(customerId, 3, "OK")))

        override suspend fun registerStep4(customerId: String, ktp: MultipartBody.Part?, selfie: MultipartBody.Part?): Response<ApiResponse<RegisterStepResponse>> =
            Response.success(ApiResponse(statusCode = 200, data = RegisterStepResponse(customerId, 4, "OK")))
    }

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        tokenManager = TokenManager.getInstance(context)
        fakeAuthApiService = FakeAuthApiService()
        authRepository = AuthRepositoryImpl(fakeAuthApiService, tokenManager)
    }

    @After
    fun tearDown() = runBlocking {
        tokenManager.clearSession()
    }

    @Test
    fun login_success_savesTokensAndSession() = runBlocking {
        val result = authRepository.login("melanie", "password123")

        assertTrue(result is ApiResult.Success)
        val successData = (result as ApiResult.Success).data
        assertEquals("access_token_123", successData.accessToken)

        // Verify saved session in DataStore
        val session = authRepository.userSession.first()
        assertNotNull(session)
        assertEquals("usr-123", session?.id)
        assertEquals("melanie", session?.username)
        assertEquals("Melanie Refman", session?.nama)
        assertEquals("access_token_123", session?.accessToken)
    }

    @Test
    fun login_error_returnsApiResultError() = runBlocking {
        fakeAuthApiService.shouldLoginSucceed = false

        val result = authRepository.login("melanie", "wrongpassword")

        assertTrue(result is ApiResult.Error)
        val session = authRepository.userSession.first()
        assertNull(session)
    }

    @Test
    fun logout_clearsSession() = runBlocking {
        tokenManager.saveAuthTokens(
            accessToken = "token_to_clear",
            refreshToken = "refresh_to_clear",
            username = "melanie"
        )
        assertNotNull(authRepository.userSession.first())

        val logoutResult = authRepository.logout()
        assertTrue(logoutResult is ApiResult.Success)

        val sessionAfterLogout = authRepository.userSession.first()
        assertNull(sessionAfterLogout)
    }
}
