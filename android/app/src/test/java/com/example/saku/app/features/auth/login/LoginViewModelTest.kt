package com.example.saku.app.features.auth.login

import android.app.Application
import androidx.test.core.app.ApplicationProvider
import com.example.saku.app.core.data.UserSession
import com.example.saku.app.core.data.repository.AuthRepository
import com.example.saku.app.core.network.ApiResult
import com.example.saku.app.core.network.dto.AuthResponse
import com.example.saku.app.core.network.dto.ResetPasswordRequest
import com.example.saku.app.core.network.dto.SendOtpResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
class LoginViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var application: Application
    private lateinit var fakeAuthRepository: FakeAuthRepository
    private lateinit var viewModel: LoginViewModel

    class FakeAuthRepository(
        var shouldSucceed: Boolean = true,
        var errorMessage: String = "Email atau password salah"
    ) : AuthRepository {
        override val userSession: Flow<UserSession?> = flowOf(null)
        override val isLoggedIn: Flow<Boolean> = flowOf(false)

        var loginCallCount = 0

        override suspend fun login(usernameOrEmail: String, password: String): ApiResult<AuthResponse> {
            loginCallCount++
            return if (shouldSucceed) {
                ApiResult.Success(
                    AuthResponse(accessToken = "token_123", refreshToken = "refresh_456"),
                    "Login berhasil"
                )
            } else {
                ApiResult.Error(errorMessage, 401)
            }
        }

        override suspend fun forgotPassword(emailOrPhone: String): ApiResult<SendOtpResponse> =
            ApiResult.Error("Not implemented")

        override suspend fun resetPassword(request: ResetPasswordRequest): ApiResult<String> =
            ApiResult.Error("Not implemented")

        override suspend fun logout(): ApiResult<Unit> = ApiResult.Success(Unit)
        override suspend fun clearSession() {}

        override suspend fun sendOtp(email: String, purpose: String): ApiResult<SendOtpResponse> =
            ApiResult.Error("Not implemented")

        override suspend fun verifyOtp(email: String, otpCode: String, purpose: String): ApiResult<com.example.saku.app.core.network.dto.VerifyOtpResponse> =
            ApiResult.Error("Not implemented")

        override suspend fun registerStep1Ktp(
            customerId: String,
            ktp: okhttp3.MultipartBody.Part?,
            data: okhttp3.RequestBody
        ): ApiResult<com.example.saku.app.core.network.dto.RegisterStepResponse> =
            ApiResult.Error("Not implemented")

        override suspend fun registerStep1KtpJson(
            customerId: String,
            request: com.example.saku.app.core.network.dto.RegisterStep1KtpRequestDto
        ): ApiResult<com.example.saku.app.core.network.dto.RegisterStepResponse> =
            ApiResult.Error("Not implemented")

        override suspend fun registerStep2Personal(
            customerId: String,
            request: com.example.saku.app.core.network.dto.RegisterStep2PersonalRequestDto
        ): ApiResult<com.example.saku.app.core.network.dto.RegisterStepResponse> =
            ApiResult.Error("Not implemented")

        override suspend fun registerStep3Liveness(
            customerId: String,
            selfie: okhttp3.MultipartBody.Part
        ): ApiResult<com.example.saku.app.core.network.dto.RegisterStepResponse> =
            ApiResult.Error("Not implemented")

        override suspend fun registerStep4Tnc(customerId: String): ApiResult<com.example.saku.app.core.network.dto.RegisterStepResponse> =
            ApiResult.Error("Not implemented")

        override suspend fun registerStep5Complete(
            customerId: String,
            request: com.example.saku.app.core.network.dto.RegisterStep5CompleteRequest
        ): ApiResult<com.example.saku.app.core.network.dto.RegisterStepResponse> =
            ApiResult.Error("Not implemented")
    }

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        fakeAuthRepository = FakeAuthRepository()
        viewModel = LoginViewModel(fakeAuthRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `login with blank username shows username error and does not call repository`() = runTest {
        viewModel.onUsernameChange("   ")
        viewModel.onPasswordChange("password123")

        viewModel.login(onSuccess = {})

        assertEquals("Username atau email wajib diisi", viewModel.usernameError.value)
        assertEquals(0, fakeAuthRepository.loginCallCount)
        assertEquals(ApiResult.Idle, viewModel.loginState.value)
    }

    @Test
    fun `login with blank password shows password error and does not call repository`() = runTest {
        viewModel.onUsernameChange("melanie")
        viewModel.onPasswordChange("   ")

        viewModel.login(onSuccess = {})

        assertEquals("Password wajib diisi", viewModel.passwordError.value)
        assertEquals(0, fakeAuthRepository.loginCallCount)
    }

    @Test
    fun `login with password less than 6 chars shows min length error`() = runTest {
        viewModel.onUsernameChange("melanie")
        viewModel.onPasswordChange("12345")

        viewModel.login(onSuccess = {})

        assertEquals("Password minimal 6 karakter", viewModel.passwordError.value)
        assertEquals(0, fakeAuthRepository.loginCallCount)
    }

    @Test
    fun `typing valid input clears validation errors`() = runTest {
        viewModel.onUsernameChange("")
        viewModel.onPasswordChange("")
        viewModel.login(onSuccess = {})
        assertNotNull(viewModel.usernameError.value)
        assertNotNull(viewModel.passwordError.value)

        viewModel.onUsernameChange("melanie")
        assertNull(viewModel.usernameError.value)

        viewModel.onPasswordChange("password123")
        assertNull(viewModel.passwordError.value)
    }

    @Test
    fun `login success calls repository and invokes onSuccess callback`() = runTest {
        var successCallbackCalled = false
        fakeAuthRepository.shouldSucceed = true

        viewModel.onUsernameChange("melanie")
        viewModel.onPasswordChange("password123")

        viewModel.login(onSuccess = { successCallbackCalled = true })
        advanceUntilIdle()

        assertEquals(1, fakeAuthRepository.loginCallCount)
        assertTrue(viewModel.loginState.value is ApiResult.Success)
        assertTrue(successCallbackCalled)
    }

    @Test
    fun `login failure updates loginState to Error and does not invoke onSuccess`() = runTest {
        var successCallbackCalled = false
        fakeAuthRepository.shouldSucceed = false

        viewModel.onUsernameChange("melanie")
        viewModel.onPasswordChange("wrongpassword")

        viewModel.login(onSuccess = { successCallbackCalled = true })
        advanceUntilIdle()

        assertEquals(1, fakeAuthRepository.loginCallCount)
        assertTrue(viewModel.loginState.value is ApiResult.Error)
        assertEquals("Email atau password salah", (viewModel.loginState.value as ApiResult.Error).message)
        assertTrue(!successCallbackCalled)
    }
}
