package com.example.saku.app.core.data.repository

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.saku.app.core.data.TokenManager
import com.example.saku.app.core.network.ApiResponse
import com.example.saku.app.core.network.api.AuthApiService
import com.example.saku.app.core.network.dto.AuthResponse
import com.example.saku.app.core.network.dto.ForgotPasswordRequest
import com.example.saku.app.core.network.dto.LoginRequest
import com.example.saku.app.core.network.dto.RefreshTokenRequest
import com.example.saku.app.core.network.dto.RegisterStep2PersonalRequestDto
import com.example.saku.app.core.network.dto.RegisterStep5CompleteRequest
import com.example.saku.app.core.network.dto.RegisterStepResponse
import com.example.saku.app.core.network.dto.ResetPasswordRequest
import com.example.saku.app.core.network.dto.SendOtpRequest
import com.example.saku.app.core.network.dto.SendOtpResponse
import com.example.saku.app.core.network.dto.UserLoginProfileDto
import com.example.saku.app.core.network.dto.VerifyOtpRequest
import com.example.saku.app.core.network.dto.VerifyOtpResponse
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import retrofit2.Response

@RunWith(RobolectricTestRunner::class)
class AuthRepositoryTest {

    private lateinit var context: Context
    private lateinit var tokenManager: TokenManager
    private lateinit var fakeAuthApiService: FakeAuthApiService
    private lateinit var authRepository: AuthRepository

    class FakeAuthApiService : AuthApiService {
        override suspend fun login(request: LoginRequest): Response<ApiResponse<AuthResponse>> =
            Response.success(
                ApiResponse(
                    statusCode = 200,
                    message = "Login berhasil",
                    data = AuthResponse(
                        accessToken = "access_token_123",
                        refreshToken = "refresh_token_456",
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

        override suspend fun checkNik(nik: String, customerId: String?): Response<ApiResponse<Boolean>> =
            Response.success(ApiResponse(statusCode = 200, data = true))

        override suspend fun checkPhone(phone: String, customerId: String?): Response<ApiResponse<Boolean>> =
            Response.success(ApiResponse(statusCode = 200, data = true))

        override suspend fun registerStep1Ktp(customerId: String, ktp: MultipartBody.Part?, data: RequestBody): Response<ApiResponse<RegisterStepResponse>> =
            Response.success(ApiResponse(statusCode = 200, data = RegisterStepResponse(customerId, 1, "OK")))

        override suspend fun registerStep1KtpJson(customerId: String, request: com.example.saku.app.core.network.dto.RegisterStep1KtpRequestDto): Response<ApiResponse<RegisterStepResponse>> =
            Response.success(ApiResponse(statusCode = 200, data = RegisterStepResponse(customerId, 1, "OK")))

        override suspend fun registerStep2Personal(customerId: String, request: RegisterStep2PersonalRequestDto): Response<ApiResponse<RegisterStepResponse>> =
            Response.success(ApiResponse(statusCode = 200, data = RegisterStepResponse(customerId, 2, "OK")))

        override suspend fun registerStep3Liveness(customerId: String, selfie: MultipartBody.Part): Response<ApiResponse<RegisterStepResponse>> =
            Response.success(ApiResponse(statusCode = 200, data = RegisterStepResponse(customerId, 3, "OK")))

        override suspend fun registerStep4Tnc(customerId: String): Response<ApiResponse<RegisterStepResponse>> =
            Response.success(ApiResponse(statusCode = 200, data = RegisterStepResponse(customerId, 4, "OK")))

        override suspend fun registerStep5Complete(customerId: String, request: RegisterStep5CompleteRequest): Response<ApiResponse<RegisterStepResponse>> =
            Response.success(ApiResponse(statusCode = 200, data = RegisterStepResponse(customerId, 5, "OK")))
    }

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        tokenManager = TokenManager.getInstance(context)
        fakeAuthApiService = FakeAuthApiService()
        val db = com.example.saku.app.core.database.AppDatabase.getInstance(context)
        authRepository = AuthRepositoryImpl(
            authApiService = fakeAuthApiService,
            tokenManager = tokenManager,
            customerDao = db.customerDao(),
            loanDao = db.loanDao(),
            notificationDao = db.notificationDao()
        )
    }

    @After
    fun tearDown() = runTest {
        tokenManager.clearSession()
    }

    @Test
    fun `userSession flow emits null initially and emits updated session when saved`() = runTest {
        // 1. Initial State: Tidak ada sesi tersimpan (null)
        val initialSession = authRepository.userSession.first()
        assertNull(initialSession)

        // 2. Simpan sesi login ke TokenManager
        tokenManager.saveAuthTokens(
            accessToken = "access_token_123",
            refreshToken = "refresh_token_456",
            id = "usr-123",
            username = "melanie_refman",
            nama = "Melanie Refman",
            email = "melanie@example.com",
            noHp = "08123456789",
            role = "CUSTOMER",
            isKycVerified = true
        )

        // 3. Observe Sesi: userSession Flow langsung mengalirkan data sesi terbaru
        val observedSession = authRepository.userSession.first()
        assertNotNull(observedSession)
        assertEquals("usr-123", observedSession?.id)
        assertEquals("melanie_refman", observedSession?.username)
        assertEquals("Melanie Refman", observedSession?.nama)
        assertEquals("melanie@example.com", observedSession?.email)
        assertEquals("08123456789", observedSession?.noHp)
        assertEquals("CUSTOMER", observedSession?.role)
        assertEquals(true, observedSession?.isKycVerified)
        assertEquals("access_token_123", observedSession?.accessToken)
        assertEquals("refresh_token_456", observedSession?.refreshToken)

        // 4. Clear Session / Logout -> userSession kembali mengalirkan null
        authRepository.clearSession()
        val sessionAfterClear = authRepository.userSession.first()
        assertNull(sessionAfterClear)
    }

    @Test
    fun `isLoggedIn flow emits false initially, true after login, and false after logout`() = runTest {
        // 1. Initial State: User belum login
        assertFalse(authRepository.isLoggedIn.first())

        // 2. Lakukan login melalui repository
        authRepository.login("melanie_refman", "password123")

        // 3. Observe isLoggedIn: bernilai true
        assertTrue(authRepository.isLoggedIn.first())

        // 4. Lakukan logout
        authRepository.logout()

        // 5. Observe isLoggedIn: kembali bernilai false
        assertFalse(authRepository.isLoggedIn.first())
    }
}