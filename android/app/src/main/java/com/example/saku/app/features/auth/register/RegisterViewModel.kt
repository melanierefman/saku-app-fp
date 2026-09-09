package com.example.saku.app.features.auth.register

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.saku.app.core.network.ApiClient
import com.example.saku.app.core.network.dto.AlamatCustomerDto
import com.example.saku.app.core.network.dto.RegisterStep1Request
import com.example.saku.app.core.network.dto.RegisterStep2Request
import com.example.saku.app.core.network.dto.RegisterStep3Request
import com.example.saku.app.core.network.dto.SendOtpRequest
import com.example.saku.app.core.network.dto.VerifyOtpRequest
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody

data class RegisterUiState(
    val currentStep: Int = 1,
    val customerId: String? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null,
    val isRegistrationComplete: Boolean = false,

    // Step 1: Account info
    val email: String = "",
    val username: String = "",
    val noHp: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val otpCode: String = "",
    val isOtpSent: Boolean = false,
    val isOtpVerified: Boolean = false,
    val otpCountdown: Int = 0,

    // Step 2: Personal & Employment
    val nik: String = "",
    val namaLengkap: String = "",
    val namaBank: String = "BCA",
    val noRekening: String = "",
    val namaRekening: String = "",
    val pekerjaan: String = "",
    val tempatKerja: String = "",
    val statusPekerjaan: String = "TETAP",
    val pendapatan: String = "",
    val lamaBekerjaBulan: String = "",
    val totalCicilanLainnya: String = "0",

    // Step 3: Address
    val alamatKtp: AlamatFormState = AlamatFormState(),
    val sameAsKtp: Boolean = true,
    val alamatDomisili: AlamatFormState = AlamatFormState(),

    // Step 4: KYC Files
    val ktpUri: Uri? = null,
    val selfieUri: Uri? = null
)

data class AlamatFormState(
    val alamatLengkap: String = "",
    val rt: String = "",
    val rw: String = "",
    val kelurahan: String = "",
    val kecamatan: String = "",
    val kotaKabupaten: String = "",
    val provinsi: String = "",
    val kodePos: String = ""
)

class RegisterViewModel(application: Application) : AndroidViewModel(application) {

    private val apiService = ApiClient.getAuthApiService(application)
    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

    private var countdownJob: Job? = null

    // -----------------------------------------------------------
    // Step 1 Form Updates
    // -----------------------------------------------------------
    fun onEmailChange(v: String) { _uiState.value = _uiState.value.copy(email = v, errorMessage = null) }
    fun onUsernameChange(v: String) { _uiState.value = _uiState.value.copy(username = v, errorMessage = null) }
    fun onNoHpChange(v: String) { _uiState.value = _uiState.value.copy(noHp = v, errorMessage = null) }
    fun onPasswordChange(v: String) { _uiState.value = _uiState.value.copy(password = v, errorMessage = null) }
    fun onConfirmPasswordChange(v: String) { _uiState.value = _uiState.value.copy(confirmPassword = v, errorMessage = null) }
    fun onOtpCodeChange(v: String) { _uiState.value = _uiState.value.copy(otpCode = v, errorMessage = null) }

    fun sendOtp() {
        val email = _uiState.value.email.trim()
        if (email.isBlank() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _uiState.value = _uiState.value.copy(errorMessage = "Format email tidak valid")
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            try {
                val response = apiService.sendOtp(SendOtpRequest(email = email, purpose = "REGISTRATION"))
                if (response.isSuccessful && response.body()?.isSuccess == true) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isOtpSent = true,
                        successMessage = "Kode OTP telah dikirim ke email Anda"
                    )
                    startCountdown()
                } else {
                    val errorMsg = ApiClient.parseError(response)
                    _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = errorMsg)
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = "Terjadi kesalahan jaringan")
            }
        }
    }

    private fun startCountdown() {
        countdownJob?.cancel()
        countdownJob = viewModelScope.launch {
            _uiState.value = _uiState.value.copy(otpCountdown = 60)
            while (_uiState.value.otpCountdown > 0) {
                delay(1000)
                _uiState.value = _uiState.value.copy(otpCountdown = _uiState.value.otpCountdown - 1)
            }
        }
    }

    fun verifyOtp() {
        val email = _uiState.value.email.trim()
        val otp = _uiState.value.otpCode.trim()
        if (otp.length != 6) {
            _uiState.value = _uiState.value.copy(errorMessage = "Kode OTP harus 6 digit")
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            try {
                val response = apiService.verifyOtp(VerifyOtpRequest(email = email, otpCode = otp, purpose = "REGISTRATION"))
                if (response.isSuccessful && response.body()?.isSuccess == true) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isOtpVerified = true,
                        successMessage = "Email berhasil diverifikasi"
                    )
                } else {
                    val errorMsg = ApiClient.parseError(response)
                    _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = errorMsg)
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = "Terjadi kesalahan jaringan")
            }
        }
    }

    fun submitStep1() {
        val s = _uiState.value
        if (s.email.isBlank() || s.username.isBlank() || s.noHp.isBlank() || s.password.isBlank() || s.confirmPassword.isBlank()) {
            _uiState.value = s.copy(errorMessage = "Semua kolom wajib diisi")
            return
        }
        if (s.password != s.confirmPassword) {
            _uiState.value = s.copy(errorMessage = "Konfirmasi password tidak cocok")
            return
        }
        if (s.password.length < 8) {
            _uiState.value = s.copy(errorMessage = "Password minimal 8 karakter")
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            try {
                val req = RegisterStep1Request(
                    email = s.email.trim(),
                    username = s.username.trim(),
                    noHp = s.noHp.trim(),
                    password = s.password,
                    confirmPassword = s.confirmPassword,
                    otpCode = if (s.otpCode.isNotBlank()) s.otpCode.trim() else null
                )
                val response = apiService.registerStep1(req)
                if (response.isSuccessful && response.body()?.isSuccess == true) {
                    val custId = response.body()?.data?.customerId
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        customerId = custId,
                        currentStep = 2,
                        successMessage = "Step 1 berhasil"
                    )
                } else {
                    val errorMsg = ApiClient.parseError(response)
                    _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = errorMsg)
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = "Koneksi gagal. Cek backend Anda.")
            }
        }
    }

    // -----------------------------------------------------------
    // Step 2 Form Updates
    // -----------------------------------------------------------
    fun onNikChange(v: String) { _uiState.value = _uiState.value.copy(nik = v, errorMessage = null) }
    fun onNamaLengkapChange(v: String) { _uiState.value = _uiState.value.copy(namaLengkap = v, errorMessage = null) }
    fun onNamaBankChange(v: String) { _uiState.value = _uiState.value.copy(namaBank = v, errorMessage = null) }
    fun onNoRekeningChange(v: String) { _uiState.value = _uiState.value.copy(noRekening = v, errorMessage = null) }
    fun onNamaRekeningChange(v: String) { _uiState.value = _uiState.value.copy(namaRekening = v, errorMessage = null) }
    fun onPekerjaanChange(v: String) { _uiState.value = _uiState.value.copy(pekerjaan = v, errorMessage = null) }
    fun onTempatKerjaChange(v: String) { _uiState.value = _uiState.value.copy(tempatKerja = v, errorMessage = null) }
    fun onStatusPekerjaanChange(v: String) { _uiState.value = _uiState.value.copy(statusPekerjaan = v, errorMessage = null) }
    fun onPendapatanChange(v: String) { _uiState.value = _uiState.value.copy(pendapatan = v, errorMessage = null) }
    fun onLamaBekerjaChange(v: String) { _uiState.value = _uiState.value.copy(lamaBekerjaBulan = v, errorMessage = null) }
    fun onCicilanLainnyaChange(v: String) { _uiState.value = _uiState.value.copy(totalCicilanLainnya = v, errorMessage = null) }

    fun submitStep2() {
        val s = _uiState.value
        val custId = s.customerId
        if (custId.isNullOrBlank()) {
            _uiState.value = s.copy(errorMessage = "ID Customer tidak ditemukan. Ulangi step 1.")
            return
        }
        if (s.nik.length != 16) {
            _uiState.value = s.copy(errorMessage = "NIK harus 16 digit")
            return
        }
        if (s.namaLengkap.isBlank() || s.noRekening.isBlank() || s.namaRekening.isBlank() || s.pekerjaan.isBlank() || s.tempatKerja.isBlank()) {
            _uiState.value = s.copy(errorMessage = "Semua field data pribadi wajib diisi")
            return
        }
        val pendapatanVal = s.pendapatan.toDoubleOrNull() ?: 0.0
        val lamaBekerjaVal = s.lamaBekerjaBulan.toIntOrNull() ?: 0
        val cicilanVal = s.totalCicilanLainnya.toDoubleOrNull() ?: 0.0

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            try {
                val req = RegisterStep2Request(
                    nik = s.nik.trim(),
                    namaLengkap = s.namaLengkap.trim(),
                    namaRekening = s.namaRekening.trim(),
                    namaBank = s.namaBank.trim(),
                    noRekening = s.noRekening.trim(),
                    pekerjaan = s.pekerjaan.trim(),
                    tempatKerja = s.tempatKerja.trim(),
                    statusPekerjaan = s.statusPekerjaan,
                    pendapatan = pendapatanVal,
                    lamaBekerjaBulan = lamaBekerjaVal,
                    totalCicilanLainnya = cicilanVal
                )
                val response = apiService.registerStep2(custId, req)
                if (response.isSuccessful && response.body()?.isSuccess == true) {
                    _uiState.value = _uiState.value.copy(isLoading = false, currentStep = 3, successMessage = "Step 2 berhasil")
                } else {
                    val errorMsg = ApiClient.parseError(response)
                    _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = errorMsg)
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = "Koneksi gagal. Cek backend Anda.")
            }
        }
    }

    // -----------------------------------------------------------
    // Step 3 Form Updates
    // -----------------------------------------------------------
    fun onAlamatKtpChange(alamat: AlamatFormState) { _uiState.value = _uiState.value.copy(alamatKtp = alamat, errorMessage = null) }
    fun onSameAsKtpToggle(same: Boolean) { _uiState.value = _uiState.value.copy(sameAsKtp = same, errorMessage = null) }
    fun onAlamatDomisiliChange(alamat: AlamatFormState) { _uiState.value = _uiState.value.copy(alamatDomisili = alamat, errorMessage = null) }

    fun submitStep3() {
        val s = _uiState.value
        val custId = s.customerId
        if (custId.isNullOrBlank()) {
            _uiState.value = s.copy(errorMessage = "ID Customer tidak ditemukan")
            return
        }
        val ktp = s.alamatKtp
        if (ktp.alamatLengkap.isBlank() || ktp.rt.isBlank() || ktp.rw.isBlank() || ktp.kelurahan.isBlank() || ktp.kecamatan.isBlank() || ktp.kotaKabupaten.isBlank() || ktp.provinsi.isBlank() || ktp.kodePos.isBlank()) {
            _uiState.value = s.copy(errorMessage = "Semua field alamat KTP wajib diisi")
            return
        }

        val domisili = if (s.sameAsKtp) ktp else s.alamatDomisili
        if (!s.sameAsKtp && (domisili.alamatLengkap.isBlank() || domisili.rt.isBlank() || domisili.rw.isBlank() || domisili.kelurahan.isBlank() || domisili.kecamatan.isBlank() || domisili.kotaKabupaten.isBlank() || domisili.provinsi.isBlank() || domisili.kodePos.isBlank())) {
            _uiState.value = s.copy(errorMessage = "Semua field alamat domisili wajib diisi")
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            try {
                val req = RegisterStep3Request(
                    alamatKtp = AlamatCustomerDto(
                        alamatLengkap = ktp.alamatLengkap.trim(),
                        rt = ktp.rt.trim(),
                        rw = ktp.rw.trim(),
                        kelurahan = ktp.kelurahan.trim(),
                        kecamatan = ktp.kecamatan.trim(),
                        kotaKabupaten = ktp.kotaKabupaten.trim(),
                        provinsi = ktp.provinsi.trim(),
                        kodePos = ktp.kodePos.trim()
                    ),
                    alamatDomisili = AlamatCustomerDto(
                        alamatLengkap = domisili.alamatLengkap.trim(),
                        rt = domisili.rt.trim(),
                        rw = domisili.rw.trim(),
                        kelurahan = domisili.kelurahan.trim(),
                        kecamatan = domisili.kecamatan.trim(),
                        kotaKabupaten = domisili.kotaKabupaten.trim(),
                        provinsi = domisili.provinsi.trim(),
                        kodePos = domisili.kodePos.trim()
                    )
                )
                val response = apiService.registerStep3(custId, req)
                if (response.isSuccessful && response.body()?.isSuccess == true) {
                    _uiState.value = _uiState.value.copy(isLoading = false, currentStep = 4, successMessage = "Step 3 berhasil")
                } else {
                    val errorMsg = ApiClient.parseError(response)
                    _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = errorMsg)
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = "Koneksi gagal. Cek backend Anda.")
            }
        }
    }

    // -----------------------------------------------------------
    // Step 4 Form Updates & Upload
    // -----------------------------------------------------------
    fun onKtpUriSelected(uri: Uri?) { _uiState.value = _uiState.value.copy(ktpUri = uri, errorMessage = null) }
    fun onSelfieUriSelected(uri: Uri?) { _uiState.value = _uiState.value.copy(selfieUri = uri, errorMessage = null) }

    fun submitStep4() {
        val s = _uiState.value
        val custId = s.customerId
        if (custId.isNullOrBlank()) {
            _uiState.value = s.copy(errorMessage = "ID Customer tidak ditemukan")
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            try {
                val context = getApplication<Application>().applicationContext

                val ktpPart: MultipartBody.Part? = s.ktpUri?.let { uri ->
                    val bytes = context.contentResolver.openInputStream(uri)?.readBytes()
                    bytes?.let {
                        val body = it.toRequestBody("image/jpeg".toMediaTypeOrNull())
                        MultipartBody.Part.createFormData("ktp", "ktp_${custId}.jpg", body)
                    }
                }

                val selfiePart: MultipartBody.Part? = s.selfieUri?.let { uri ->
                    val bytes = context.contentResolver.openInputStream(uri)?.readBytes()
                    bytes?.let {
                        val body = it.toRequestBody("image/jpeg".toMediaTypeOrNull())
                        MultipartBody.Part.createFormData("selfie", "selfie_${custId}.jpg", body)
                    }
                }

                val response = apiService.registerStep4(custId, ktpPart, selfiePart)
                if (response.isSuccessful && response.body()?.isSuccess == true) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isRegistrationComplete = true,
                        successMessage = "Pendaftaran Berhasil! Akun Anda siap digunakan."
                    )
                } else {
                    val errorMsg = ApiClient.parseError(response)
                    _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = errorMsg)
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = "Koneksi gagal saat upload file")
            }
        }
    }

    fun goToPreviousStep() {
        val current = _uiState.value.currentStep
        if (current > 1) {
            _uiState.value = _uiState.value.copy(currentStep = current - 1, errorMessage = null)
        }
    }
}
