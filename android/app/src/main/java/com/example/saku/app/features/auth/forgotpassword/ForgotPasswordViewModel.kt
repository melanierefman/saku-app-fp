package com.example.saku.app.features.auth.forgotpassword

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.saku.app.core.data.repository.AuthRepository
import com.example.saku.app.core.network.ApiResult
import com.example.saku.app.core.network.dto.ResetPasswordRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

enum class ForgotPasswordStep {
    REQUEST_OTP,
    VERIFY_OTP,
    RESET_PASSWORD,
    SUCCESS
}

class ForgotPasswordViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    var currentStep = MutableStateFlow(ForgotPasswordStep.REQUEST_OTP)
        private set

    var email = MutableStateFlow("")
        private set
    var otpCode = MutableStateFlow("")
        private set
    var newPassword = MutableStateFlow("")
        private set
    var confirmPassword = MutableStateFlow("")
        private set

    var emailError = MutableStateFlow<String?>(null)
        private set
    var otpError = MutableStateFlow<String?>(null)
        private set
    var passwordError = MutableStateFlow<String?>(null)
        private set
    var confirmPasswordError = MutableStateFlow<String?>(null)
        private set

    private val _actionState = MutableStateFlow<ApiResult<String>>(ApiResult.Idle)
    val actionState: StateFlow<ApiResult<String>> = _actionState.asStateFlow()

    fun onEmailChange(input: String) {
        email.value = input
        if (emailError.value != null) emailError.value = null
    }

    fun onOtpChange(input: String) {
        otpCode.value = input
        if (otpError.value != null) otpError.value = null
    }

    fun onNewPasswordChange(input: String) {
        newPassword.value = input
        if (passwordError.value != null) passwordError.value = null
    }

    fun onConfirmPasswordChange(input: String) {
        confirmPassword.value = input
        if (confirmPasswordError.value != null) confirmPasswordError.value = null
    }

    fun sendOtp() {
        val emailVal = email.value.trim()
        if (emailVal.isBlank() || !emailVal.contains("@")) {
            emailError.value = "Masukkan alamat email yang valid"
            return
        }

        viewModelScope.launch {
            _actionState.value = ApiResult.Loading
            when (val result = authRepository.forgotPassword(emailVal)) {
                is ApiResult.Success -> {
                    val msg = result.message ?: "Kode OTP telah dikirim ke email Anda"
                    _actionState.value = ApiResult.Success(msg, msg)
                    currentStep.value = ForgotPasswordStep.VERIFY_OTP
                }
                is ApiResult.Error -> {
                    _actionState.value = ApiResult.Error(result.message, result.statusCode)
                }
                else -> {}
            }
        }
    }

    fun verifyOtp() {
        val otpVal = otpCode.value.trim()
        if (otpVal.length < 6) {
            otpError.value = "Masukkan 6 digit kode OTP"
            return
        }
        currentStep.value = ForgotPasswordStep.RESET_PASSWORD
    }

    fun changeEmail() {
        otpCode.value = ""
        otpError.value = null
        currentStep.value = ForgotPasswordStep.REQUEST_OTP
    }

    fun resetPassword() {
        val otpVal = otpCode.value.trim()
        val passVal = newPassword.value.trim()
        val confirmVal = confirmPassword.value.trim()

        var hasError = false
        if (otpVal.length < 4) {
            otpError.value = "Kode OTP tidak valid"
            hasError = true
        }
        if (passVal.length < 6) {
            passwordError.value = "Password baru minimal 6 karakter"
            hasError = true
        }
        if (passVal != confirmVal) {
            confirmPasswordError.value = "Konfirmasi password tidak cocok"
            hasError = true
        }

        if (hasError) return

        viewModelScope.launch {
            _actionState.value = ApiResult.Loading
            val request = ResetPasswordRequest(
                email = email.value.trim(),
                otpCode = otpVal,
                newPassword = passVal,
                confirmNewPassword = confirmVal
            )
            when (val result = authRepository.resetPassword(request)) {
                is ApiResult.Success -> {
                    val msg = result.message ?: "Password berhasil diubah"
                    _actionState.value = ApiResult.Success(msg, msg)
                    currentStep.value = ForgotPasswordStep.SUCCESS
                }
                is ApiResult.Error -> {
                    _actionState.value = ApiResult.Error(result.message, result.statusCode)
                }
                else -> {}
            }
        }
    }
}