package com.example.saku.app.features.auth.login

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.saku.app.core.data.TokenManager
import com.example.saku.app.core.network.ApiClient
import com.example.saku.app.core.network.ApiResult
import com.example.saku.app.core.network.dto.AuthResponse
import com.example.saku.app.core.network.dto.LoginRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LoginViewModel(application: Application) : AndroidViewModel(application) {

    private val authApiService = ApiClient.getAuthApiService(application)
    private val tokenManager = TokenManager.getInstance(application)

    var username = MutableStateFlow("")
        private set
    var password = MutableStateFlow("")
        private set

    var usernameError = MutableStateFlow<String?>(null)
        private set
    var passwordError = MutableStateFlow<String?>(null)
        private set

    private val _loginState = MutableStateFlow<ApiResult<AuthResponse>>(ApiResult.Idle)
    val loginState: StateFlow<ApiResult<AuthResponse>> = _loginState.asStateFlow()

    fun onUsernameChange(input: String) {
        username.value = input
        if (usernameError.value != null) usernameError.value = null
    }

    fun onPasswordChange(input: String) {
        password.value = input
        if (passwordError.value != null) passwordError.value = null
    }

    fun login(onSuccess: () -> Unit) {
        val userVal = username.value.trim()
        val passVal = password.value.trim()

        var hasError = false
        if (userVal.isBlank()) {
            usernameError.value = "Username atau email wajib diisi"
            hasError = true
        }

        if (passVal.isBlank()) {
            passwordError.value = "Password wajib diisi"
            hasError = true
        } else if (passVal.length < 6) {
            passwordError.value = "Password minimal 6 karakter"
            hasError = true
        }

        if (hasError) return

        viewModelScope.launch {
            _loginState.value = ApiResult.Loading
            try {
                val response = authApiService.login(LoginRequest(userVal, passVal))
                if (response.isSuccessful && response.body() != null) {
                    val apiResponse = response.body()!!
                    val authData = apiResponse.data
                    if (authData != null) {
                        val user = authData.user
                        tokenManager.saveAuthTokens(
                            accessToken = authData.accessToken,
                            refreshToken = authData.refreshToken,
                            id = user?.id ?: "",
                            username = user?.username ?: userVal,
                            nama = user?.nama ?: "",
                            email = user?.email ?: "",
                            noHp = user?.noHp ?: "",
                            role = user?.role ?: "CUSTOMER",
                            isKycVerified = user?.isKycVerified ?: false
                        )
                        _loginState.value = ApiResult.Success(authData, apiResponse.message)
                        onSuccess()
                    } else {
                        _loginState.value = ApiResult.Error("Respon login tidak valid")
                    }
                } else {
                    val errorMsg = ApiClient.parseError(response)
                    _loginState.value = ApiResult.Error(errorMsg, response.code())
                }
            } catch (e: Exception) {
                _loginState.value = ApiResult.Error(
                    e.localizedMessage ?: "Tidak dapat terhubung ke server backend. Pastikan server aktif."
                )
            }
        }
    }

    fun resetState() {
        _loginState.value = ApiResult.Idle
    }
}
