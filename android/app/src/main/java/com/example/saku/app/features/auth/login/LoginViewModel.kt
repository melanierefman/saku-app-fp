package com.example.saku.app.features.auth.login

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.saku.app.core.data.TokenManager
import com.example.saku.app.core.data.repository.AuthRepository
import com.example.saku.app.core.data.repository.AuthRepositoryImpl
import com.example.saku.app.core.network.ApiClient
import com.example.saku.app.core.network.ApiResult
import com.example.saku.app.core.network.dto.AuthResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LoginViewModel @JvmOverloads constructor(
    application: Application,
    private val authRepository: AuthRepository = AuthRepositoryImpl(
        ApiClient.getAuthApiService(application),
        TokenManager.getInstance(application)
    )
) : AndroidViewModel(application) {

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
            val result = authRepository.login(userVal, passVal)
            _loginState.value = result
            if (result is ApiResult.Success) {
                onSuccess()
            }
        }
    }

    fun resetState() {
        _loginState.value = ApiResult.Idle
    }
}

