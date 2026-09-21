package com.example.saku.app.core.data.repository

import com.example.saku.app.core.data.TokenManager
import com.example.saku.app.core.data.UserSession
import com.example.saku.app.core.database.dao.CustomerDao
import com.example.saku.app.core.database.dao.LoanDao
import com.example.saku.app.core.database.dao.NotificationDao
import com.example.saku.app.core.network.ApiClient
import com.example.saku.app.core.network.ApiResult
import com.example.saku.app.core.network.api.AuthApiService
import com.example.saku.app.core.network.dto.AuthResponse
import com.example.saku.app.core.network.dto.ForgotPasswordRequest
import com.example.saku.app.core.network.dto.LoginRequest
import com.example.saku.app.core.network.dto.RegisterStep1KtpRequestDto
import com.example.saku.app.core.network.dto.RegisterStep2PersonalRequestDto
import com.example.saku.app.core.network.dto.RegisterStep5CompleteRequest
import com.example.saku.app.core.network.dto.RegisterStepResponse
import com.example.saku.app.core.network.dto.ResetPasswordRequest
import com.example.saku.app.core.network.dto.SendOtpRequest
import com.example.saku.app.core.network.dto.SendOtpResponse
import com.example.saku.app.core.network.dto.VerifyOtpRequest
import com.example.saku.app.core.network.dto.VerifyOtpResponse
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val authApiService: AuthApiService,
    private val tokenManager: TokenManager,
    private val customerDao: CustomerDao,
    private val loanDao: LoanDao,
    private val notificationDao: NotificationDao
) : AuthRepository {

    override val userSession: Flow<UserSession?> = tokenManager.userSessionFlow
    override val isLoggedIn: Flow<Boolean> = tokenManager.isLoggedInFlow

    override suspend fun login(usernameOrEmail: String, password: String): ApiResult<AuthResponse> {
        return try {
            val response = authApiService.login(LoginRequest(usernameOrEmail, password))
            if (response.isSuccessful && response.body() != null) {
                val apiResponse = response.body()!!
                val authData = apiResponse.data
                if (authData != null) {
                    val user = authData.user
                    tokenManager.saveAuthTokens(
                        accessToken = authData.accessToken,
                        refreshToken = authData.refreshToken,
                        id = user?.id ?: "",
                        username = user?.username ?: usernameOrEmail,
                        nama = user?.nama ?: "",
                        email = user?.email ?: "",
                        noHp = user?.noHp ?: "",
                        role = user?.role ?: "CUSTOMER",
                        isKycVerified = user?.isKycVerified ?: false
                    )
                    ApiResult.Success(authData, apiResponse.message)
                } else {
                    ApiResult.Error("Respon login tidak valid")
                }
            } else {
                val errorMsg = ApiClient.parseError(response)
                ApiResult.Error(errorMsg, response.code())
            }
        } catch (e: Exception) {
            ApiResult.Error(
                e.localizedMessage ?: "Tidak dapat terhubung ke server backend. Pastikan server aktif."
            )
        }
    }

    override suspend fun forgotPassword(emailOrPhone: String): ApiResult<SendOtpResponse> {
        return try {
            val response = authApiService.forgotPassword(ForgotPasswordRequest(emailOrPhone))
            if (response.isSuccessful && response.body()?.data != null) {
                ApiResult.Success(response.body()!!.data!!, response.body()?.message)
            } else {
                ApiResult.Error(ApiClient.parseError(response), response.code())
            }
        } catch (e: Exception) {
            ApiResult.Error(e.localizedMessage ?: "Gagal memproses lupa password.")
        }
    }

    override suspend fun resetPassword(request: ResetPasswordRequest): ApiResult<String> {
        return try {
            val response = authApiService.resetPassword(request)
            if (response.isSuccessful) {
                ApiResult.Success(response.body()?.data ?: "Password berhasil diubah", response.body()?.message)
            } else {
                ApiResult.Error(ApiClient.parseError(response), response.code())
            }
        } catch (e: Exception) {
            ApiResult.Error(e.localizedMessage ?: "Gagal mereset password.")
        }
    }

    override suspend fun logout(): ApiResult<Unit> {
        return try {
            try {
                authApiService.logout()
            } catch (_: Exception) {}
            clearLocalData()
            ApiResult.Success(Unit, "Berhasil keluar")
        } catch (e: Exception) {
            clearLocalData()
            ApiResult.Error(e.localizedMessage ?: "Gagal logout")
        }
    }

    override suspend fun clearSession() {
        clearLocalData()
    }

    private suspend fun clearLocalData() {
        tokenManager.clearSession()
        try {
            customerDao.clearProfile()
            loanDao.clearLoans()
            notificationDao.clearNotifications()
            FirebaseMessaging.getInstance().deleteToken()
        } catch (_: Exception) {}
    }

    override suspend fun sendOtp(email: String, purpose: String): ApiResult<SendOtpResponse> {
        return try {
            val response = authApiService.sendOtp(SendOtpRequest(email, purpose))
            if (response.isSuccessful && response.body()?.data != null) {
                ApiResult.Success(response.body()!!.data!!, response.body()?.message)
            } else {
                ApiResult.Error(ApiClient.parseError(response), response.code())
            }
        } catch (e: Exception) {
            ApiResult.Error(e.localizedMessage ?: "Gagal mengirim kode OTP.")
        }
    }

    override suspend fun verifyOtp(email: String, otpCode: String, purpose: String): ApiResult<VerifyOtpResponse> {
        return try {
            val response = authApiService.verifyOtp(VerifyOtpRequest(email, otpCode, purpose))
            if (response.isSuccessful && response.body()?.data != null) {
                ApiResult.Success(response.body()!!.data!!, response.body()?.message)
            } else {
                ApiResult.Error(ApiClient.parseError(response), response.code())
            }
        } catch (e: Exception) {
            ApiResult.Error(e.localizedMessage ?: "Gagal memverifikasi OTP.")
        }
    }

    override suspend fun checkNik(nik: String, customerId: String?): ApiResult<Boolean> {
        return try {
            val response = authApiService.checkNik(nik, customerId)
            if (response.isSuccessful && response.body()?.data != null) {
                ApiResult.Success(response.body()!!.data!!, response.body()?.message)
            } else {
                ApiResult.Error(ApiClient.parseError(response), response.code())
            }
        } catch (e: Exception) {
            ApiResult.Error(e.localizedMessage ?: "Gagal memverifikasi NIK.")
        }
    }

    override suspend fun checkPhone(phone: String, customerId: String?): ApiResult<Boolean> {
        return try {
            val response = authApiService.checkPhone(phone, customerId)
            if (response.isSuccessful && response.body()?.data != null) {
                ApiResult.Success(response.body()!!.data!!, response.body()?.message)
            } else {
                ApiResult.Error(ApiClient.parseError(response), response.code())
            }
        } catch (e: Exception) {
            ApiResult.Error(e.localizedMessage ?: "Gagal memverifikasi nomor handphone.")
        }
    }

    override suspend fun registerStep1Ktp(
        customerId: String,
        ktp: MultipartBody.Part?,
        data: RequestBody
    ): ApiResult<RegisterStepResponse> {
        return try {
            val response = authApiService.registerStep1Ktp(customerId, ktp, data)
            if (response.isSuccessful && response.body()?.data != null) {
                ApiResult.Success(response.body()!!.data!!, response.body()?.message)
            } else {
                ApiResult.Error(ApiClient.parseError(response), response.code())
            }
        } catch (e: Exception) {
            ApiResult.Error(e.localizedMessage ?: "Gagal menyimpan data identitas KTP.")
        }
    }

    override suspend fun registerStep1KtpJson(
        customerId: String,
        request: RegisterStep1KtpRequestDto
    ): ApiResult<RegisterStepResponse> {
        return try {
            val response = authApiService.registerStep1KtpJson(customerId, request)
            if (response.isSuccessful && response.body()?.data != null) {
                ApiResult.Success(response.body()!!.data!!, response.body()?.message)
            } else {
                ApiResult.Error(ApiClient.parseError(response), response.code())
            }
        } catch (e: Exception) {
            ApiResult.Error(e.localizedMessage ?: "Gagal menyimpan data identitas KTP.")
        }
    }

    override suspend fun registerStep2Personal(
        customerId: String,
        request: RegisterStep2PersonalRequestDto
    ): ApiResult<RegisterStepResponse> {
        return try {
            val response = authApiService.registerStep2Personal(customerId, request)
            if (response.isSuccessful && response.body()?.data != null) {
                ApiResult.Success(response.body()!!.data!!, response.body()?.message)
            } else {
                ApiResult.Error(ApiClient.parseError(response), response.code())
            }
        } catch (e: Exception) {
            ApiResult.Error(e.localizedMessage ?: "Gagal menyimpan data pekerjaan & rekening.")
        }
    }

    override suspend fun registerStep3Liveness(
        customerId: String,
        selfie: MultipartBody.Part
    ): ApiResult<RegisterStepResponse> {
        return try {
            val response = authApiService.registerStep3Liveness(customerId, selfie)
            if (response.isSuccessful && response.body()?.data != null) {
                ApiResult.Success(response.body()!!.data!!, response.body()?.message)
            } else {
                ApiResult.Error(ApiClient.parseError(response), response.code())
            }
        } catch (e: Exception) {
            ApiResult.Error(e.localizedMessage ?: "Gagal memproses verifikasi liveness selfie.")
        }
    }

    override suspend fun registerStep4(
        customerId: String,
        ktp: MultipartBody.Part?,
        selfie: MultipartBody.Part?
    ): ApiResult<RegisterStepResponse> {
        return try {
            val response = authApiService.registerStep4(customerId, ktp, selfie)
            if (response.isSuccessful && response.body()?.data != null) {
                ApiResult.Success(response.body()!!.data!!, response.body()?.message)
            } else {
                ApiResult.Error(ApiClient.parseError(response), response.code())
            }
        } catch (e: Exception) {
            ApiResult.Error(e.localizedMessage ?: "Gagal mengunggah dokumen pendaftaran.")
        }
    }

    override suspend fun registerStep4Tnc(customerId: String): ApiResult<RegisterStepResponse> {
        return try {
            val response = authApiService.registerStep4Tnc(customerId)
            if (response.isSuccessful && response.body()?.data != null) {
                ApiResult.Success(response.body()!!.data!!, response.body()?.message)
            } else {
                ApiResult.Error(ApiClient.parseError(response), response.code())
            }
        } catch (e: Exception) {
            ApiResult.Error(e.localizedMessage ?: "Gagal menyetujui syarat dan ketentuan.")
        }
    }

    override suspend fun registerStep5Complete(
        customerId: String,
        request: RegisterStep5CompleteRequest
    ): ApiResult<RegisterStepResponse> {
        return try {
            val response = authApiService.registerStep5Complete(customerId, request)
            if (response.isSuccessful && response.body()?.data != null) {
                ApiResult.Success(response.body()!!.data!!, response.body()?.message)
            } else {
                ApiResult.Error(ApiClient.parseError(response), response.code())
            }
        } catch (e: Exception) {
            ApiResult.Error(e.localizedMessage ?: "Gagal menyelesaikan pendaftaran akun.")
        }
    }
}
