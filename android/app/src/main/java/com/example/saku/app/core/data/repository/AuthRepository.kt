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

    // Melakukan login dengan username/email dan password
    suspend fun login(usernameOrEmail: String, password: String): ApiResult<AuthResponse>

    // Mengajukan reset password menggunakan email atau nomor HP
    suspend fun forgotPassword(emailOrPhone: String): ApiResult<SendOtpResponse>

    // Mengonfirmasi perubahan password baru
    suspend fun resetPassword(request: ResetPasswordRequest): ApiResult<String>

    // Melakukan logout dan membersihkan session lokal
    suspend fun logout(): ApiResult<Unit>

    // Membersihkan session lokal tanpa memanggil API logout
    suspend fun clearSession()

    // Memeriksa apakah NIK sudah terdaftar
    suspend fun checkNik(nik: String, customerId: String? = null): ApiResult<Boolean>

    // Memeriksa apakah nomor HP sudah terdaftar
    suspend fun checkPhone(phone: String, customerId: String? = null): ApiResult<Boolean>

    // Mengirim kode OTP ke email
    suspend fun sendOtp(email: String, purpose: String = "REGISTRATION"): ApiResult<SendOtpResponse>

    // Memvalidasi kode OTP yang dimasukkan
    suspend fun verifyOtp(email: String, otpCode: String, purpose: String = "REGISTRATION"): ApiResult<VerifyOtpResponse>

    // Mengunggah dokumen KTP untuk registrasi step 1
    suspend fun registerStep1Ktp(customerId: String, ktp: MultipartBody.Part?, data: RequestBody): ApiResult<RegisterStepResponse>

    // Menyimpan data KTP berformat JSON untuk registrasi step 1
    suspend fun registerStep1KtpJson(customerId: String, request: RegisterStep1KtpRequestDto): ApiResult<RegisterStepResponse>

    // Menyimpan data pribadi dan pekerjaan untuk registrasi step 2
    suspend fun registerStep2Personal(customerId: String, request: RegisterStep2PersonalRequestDto): ApiResult<RegisterStepResponse>

    // Mengunggah rekaman/foto liveness untuk registrasi step 3
    suspend fun registerStep3Liveness(customerId: String, selfie: MultipartBody.Part): ApiResult<RegisterStepResponse>

    // Menyetujui syarat dan ketentuan untuk registrasi step 4
    suspend fun registerStep4Tnc(customerId: String): ApiResult<RegisterStepResponse>

    // Menyelesaikan pembuatan akun customer untuk registrasi step 5
    suspend fun registerStep5Complete(customerId: String, request: RegisterStep5CompleteRequest): ApiResult<RegisterStepResponse>
}