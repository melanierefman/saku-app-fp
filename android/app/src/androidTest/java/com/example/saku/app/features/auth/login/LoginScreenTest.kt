package com.example.saku.app.features.auth.login

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.saku.app.core.data.UserSession
import com.example.saku.app.core.data.repository.AuthRepository
import com.example.saku.app.core.network.ApiResult
import com.example.saku.app.core.network.dto.AuthResponse
import com.example.saku.app.core.network.dto.ResetPasswordRequest
import com.example.saku.app.core.network.dto.SendOtpResponse
import com.example.saku.app.ui.theme.SAKUAppTheme
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LoginScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    class FakeAuthRepository(
        var shouldSucceed: Boolean = true,
        var errorMessage: String = "Username atau password salah"
    ) : AuthRepository {
        override val userSession: Flow<UserSession?> = flowOf(null)
        override val isLoggedIn: Flow<Boolean> = flowOf(false)

        var loginCallCount = 0
        var lastUsername = ""
        var lastPassword = ""

        override suspend fun login(usernameOrEmail: String, password: String): ApiResult<AuthResponse> {
            loginCallCount++
            lastUsername = usernameOrEmail
            lastPassword = password
            return if (shouldSucceed) {
                ApiResult.Success(
                    AuthResponse(
                        accessToken = "mock_access_token_12345",
                        refreshToken = "mock_refresh_token_67890"
                    ),
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

    @Test
    fun loginScreen_displaysAllInitialUiElements() {
        val fakeRepo = FakeAuthRepository()
        val viewModel = LoginViewModel(ApplicationProvider.getApplicationContext(), fakeRepo)

        composeTestRule.setContent {
            SAKUAppTheme {
                LoginScreen(
                    onNavigateToRegister = {},
                    onNavigateToHome = {},
                    onNavigateToForgotPassword = {},
                    viewModel = viewModel
                )
            }
        }

        composeTestRule.onNodeWithText("Masuk ke SAKU").assertIsDisplayed()
        composeTestRule.onNodeWithText("Email / Username").assertIsDisplayed()
        composeTestRule.onNodeWithText("Password").assertIsDisplayed()
        composeTestRule.onNodeWithText("Masuk").assertIsDisplayed()
        composeTestRule.onNodeWithText("Lupa Password?").assertIsDisplayed()
        composeTestRule.onNodeWithText("Daftar Sekarang").assertIsDisplayed()
    }

    @Test
    fun loginScreen_emptyCredentials_showsValidationErrors() {
        val fakeRepo = FakeAuthRepository()
        val viewModel = LoginViewModel(ApplicationProvider.getApplicationContext(), fakeRepo)

        composeTestRule.setContent {
            SAKUAppTheme {
                LoginScreen(
                    onNavigateToRegister = {},
                    onNavigateToHome = {},
                    onNavigateToForgotPassword = {},
                    viewModel = viewModel
                )
            }
        }

        // Click Masuk without entering any input
        composeTestRule.onNodeWithText("Masuk").performClick()

        composeTestRule.onNodeWithText("Username atau email wajib diisi").assertIsDisplayed()
        assertEquals(0, fakeRepo.loginCallCount)
    }

    @Test
    fun loginScreen_passwordLessThan6Chars_showsMinLengthError() {
        val fakeRepo = FakeAuthRepository()
        val viewModel = LoginViewModel(ApplicationProvider.getApplicationContext(), fakeRepo)

        composeTestRule.setContent {
            SAKUAppTheme {
                LoginScreen(
                    onNavigateToRegister = {},
                    onNavigateToHome = {},
                    onNavigateToForgotPassword = {},
                    viewModel = viewModel
                )
            }
        }

        viewModel.onUsernameChange("melanie_refman")
        viewModel.onPasswordChange("12345")

        composeTestRule.onNodeWithText("Masuk").performClick()

        composeTestRule.onNodeWithText("Password minimal 6 karakter").assertIsDisplayed()
        assertEquals(0, fakeRepo.loginCallCount)
    }

    @Test
    fun loginScreen_successfulLogin_triggersOnNavigateToHome() {
        val fakeRepo = FakeAuthRepository(shouldSucceed = true)
        val viewModel = LoginViewModel(ApplicationProvider.getApplicationContext(), fakeRepo)
        var navigatedToHome = false

        composeTestRule.setContent {
            SAKUAppTheme {
                LoginScreen(
                    onNavigateToRegister = {},
                    onNavigateToHome = { navigatedToHome = true },
                    onNavigateToForgotPassword = {},
                    viewModel = viewModel
                )
            }
        }

        viewModel.onUsernameChange("melanie_refman")
        viewModel.onPasswordChange("password123")

        composeTestRule.onNodeWithText("Masuk").performClick()

        assertTrue(navigatedToHome)
        assertEquals(1, fakeRepo.loginCallCount)
        assertEquals("melanie_refman", fakeRepo.lastUsername)
        assertEquals("password123", fakeRepo.lastPassword)
    }

    @Test
    fun loginScreen_failedLogin_displaysErrorMessage() {
        val fakeRepo = FakeAuthRepository(shouldSucceed = false, errorMessage = "Email atau password salah")
        val viewModel = LoginViewModel(ApplicationProvider.getApplicationContext(), fakeRepo)

        composeTestRule.setContent {
            SAKUAppTheme {
                LoginScreen(
                    onNavigateToRegister = {},
                    onNavigateToHome = {},
                    onNavigateToForgotPassword = {},
                    viewModel = viewModel
                )
            }
        }

        viewModel.onUsernameChange("melanie_refman")
        viewModel.onPasswordChange("wrongpassword")

        composeTestRule.onNodeWithText("Masuk").performClick()

        composeTestRule.onNodeWithText("Email atau password salah").assertIsDisplayed()
        assertEquals(1, fakeRepo.loginCallCount)
    }

    @Test
    fun loginScreen_clickRegister_triggersOnNavigateToRegister() {
        val fakeRepo = FakeAuthRepository()
        val viewModel = LoginViewModel(ApplicationProvider.getApplicationContext(), fakeRepo)
        var navigatedToRegister = false

        composeTestRule.setContent {
            SAKUAppTheme {
                LoginScreen(
                    onNavigateToRegister = { navigatedToRegister = true },
                    onNavigateToHome = {},
                    onNavigateToForgotPassword = {},
                    viewModel = viewModel
                )
            }
        }

        composeTestRule.onNodeWithText("Daftar Sekarang").performClick()
        assertTrue(navigatedToRegister)
    }

    @Test
    fun loginScreen_clickForgotPassword_triggersOnNavigateToForgotPassword() {
        val fakeRepo = FakeAuthRepository()
        val viewModel = LoginViewModel(ApplicationProvider.getApplicationContext(), fakeRepo)
        var navigatedToForgot = false

        composeTestRule.setContent {
            SAKUAppTheme {
                LoginScreen(
                    onNavigateToRegister = {},
                    onNavigateToHome = {},
                    onNavigateToForgotPassword = { navigatedToForgot = true },
                    viewModel = viewModel
                )
            }
        }

        composeTestRule.onNodeWithText("Lupa Password?").performClick()
        assertTrue(navigatedToForgot)
    }
}
