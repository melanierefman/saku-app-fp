package com.example.saku.app.features.auth.register

import android.app.Application
import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.saku.app.core.network.ApiClient
import com.example.saku.app.core.network.dto.AlamatCustomerDto
import com.example.saku.app.core.network.dto.RegisterStep1KtpRequestDto
import com.example.saku.app.core.network.dto.RegisterStep2PersonalRequestDto
import com.example.saku.app.core.network.dto.RegisterStep4TncRequest
import com.example.saku.app.core.network.dto.RegisterStep5CompleteRequest
import com.example.saku.app.core.network.dto.SendOtpRequest
import com.example.saku.app.core.network.dto.VerifyOtpRequest
import com.example.saku.app.core.util.KtpOcrHelper
import com.google.gson.Gson
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.ByteArrayOutputStream

data class RegisterUiState(
    val currentStep: Int = 1, // 1: Email OTP, 2: KTP OCR, 3: Personal & Domisili, 4: Liveness Selfie, 5: TNC & Password
    val customerId: String? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null,
    val isRegistrationComplete: Boolean = false,

    // Step 1: Email & OTP Verification
    val email: String = "",
    val otpCode: String = "",
    val isOtpSent: Boolean = false,
    val isOtpVerified: Boolean = false,
    val otpCountdown: Int = 0,

    // Step 2: e-KTP & OCR
    val ktpUri: Uri? = null,
    val ktpBitmap: Bitmap? = null,
    val isOcrProcessing: Boolean = false,
    val ocrSuccessMessage: String? = null,
    val nik: String = "",
    val namaLengkap: String = "",
    val alamatKtp: AlamatFormState = AlamatFormState(),

    // Step 3: Data Pribadi, Rekening, Pekerjaan & Domisili
    val noHp: String = "",
    val namaIbuKandung: String = "",
    val namaBank: String = "BCA",
    val noRekening: String = "",
    val namaRekening: String = "",
    val pekerjaan: String = "",
    val tempatKerja: String = "",
    val statusPekerjaan: String = "KARYAWAN_TETAP",
    val pendapatan: String = "",
    val lamaBekerjaBulan: String = "",
    val totalCicilanLainnya: String = "0",
    val sameAsKtp: Boolean = true,
    val alamatDomisili: AlamatFormState = AlamatFormState(),

    // Step 4: Liveness Selfie
    val selfieUri: Uri? = null,
    val selfieBitmap: Bitmap? = null,

    // Step 5: T&C & Security (Password)
    val isTncAgreed: Boolean = false,
    val password: String = "",
    val confirmPassword: String = ""
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
    private val gson = Gson()
    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

    private var countdownJob: Job? = null

    // Step 1: Email & OTP Handling
    fun onEmailChange(v: String) { _uiState.value = _uiState.value.copy(email = v, errorMessage = null) }
    fun onOtpCodeChange(v: String) { _uiState.value = _uiState.value.copy(otpCode = v, errorMessage = null) }

    fun sendOtp() {
        val email = _uiState.value.email.trim()
        if (email.isBlank() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _uiState.value = _uiState.value.copy(errorMessage = "Format email tidak valid")
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null, successMessage = null)
            try {
                val response = apiService.sendOtp(SendOtpRequest(email = email, purpose = "REGISTRATION"))
                if (response.isSuccessful && response.body()?.isSuccess == true) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isOtpSent = true,
                        successMessage = "Kode OTP 6-digit telah dikirim ke $email"
                    )
                    startCountdown()
                } else {
                    val errorMsg = ApiClient.parseError(response)
                    _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = errorMsg)
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = "Koneksi jaringan bermasalah. Periksa koneksi backend Anda.")
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
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null, successMessage = null)
            try {
                val response = apiService.verifyOtp(VerifyOtpRequest(email = email, otpCode = otp, purpose = "REGISTRATION"))
                if (response.isSuccessful && response.body()?.isSuccess == true) {
                    val verifyData = response.body()?.data
                    val custId = verifyData?.customerId
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isOtpVerified = true,
                        customerId = custId,
                        currentStep = 2,
                        successMessage = "Email berhasil diverifikasi! Lanjut ke foto e-KTP"
                    )
                } else {
                    val errorMsg = ApiClient.parseError(response)
                    _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = errorMsg)
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = "Koneksi jaringan bermasalah")
            }
        }
    }

    // Step 2: e-KTP & OCR
    fun onNikChange(v: String) { _uiState.value = _uiState.value.copy(nik = v, errorMessage = null) }
    fun onNamaLengkapChange(v: String) { _uiState.value = _uiState.value.copy(namaLengkap = v, errorMessage = null) }
    fun onAlamatKtpChange(alamat: AlamatFormState) { _uiState.value = _uiState.value.copy(alamatKtp = alamat, errorMessage = null) }

    fun onKtpImageSelected(uri: Uri?) {
        _uiState.value = _uiState.value.copy(ktpUri = uri, ktpBitmap = null, errorMessage = null)
        if (uri != null) {
            runKtpOcr(uri = uri)
        }
    }

    fun onKtpBitmapCaptured(bitmap: Bitmap?) {
        _uiState.value = _uiState.value.copy(ktpBitmap = bitmap, ktpUri = null, errorMessage = null)
        if (bitmap != null) {
            runKtpOcr(bitmap = bitmap)
        }
    }

    private fun runKtpOcr(uri: Uri? = null, bitmap: Bitmap? = null) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isOcrProcessing = true, ocrSuccessMessage = null)
            try {
                val result = when {
                    bitmap != null -> KtpOcrHelper.recognizeTextFromBitmap(bitmap)
                    uri != null -> KtpOcrHelper.recognizeTextFromUri(getApplication(), uri)
                    else -> null
                }

                if (result != null) {
                    val currentKtp = _uiState.value.alamatKtp
                    val updatedKtp = currentKtp.copy(
                        alamatLengkap = result.alamat ?: currentKtp.alamatLengkap,
                        rt = result.rt ?: currentKtp.rt,
                        rw = result.rw ?: currentKtp.rw,
                        kelurahan = result.kelurahan ?: currentKtp.kelurahan,
                        kecamatan = result.kecamatan ?: currentKtp.kecamatan,
                        kotaKabupaten = result.kotaKabupaten ?: currentKtp.kotaKabupaten,
                        provinsi = result.provinsi ?: currentKtp.provinsi
                    )

                    val updatedDomisili = if (_uiState.value.sameAsKtp) updatedKtp else _uiState.value.alamatDomisili

                    _uiState.value = _uiState.value.copy(
                        isOcrProcessing = false,
                        nik = result.nik ?: _uiState.value.nik,
                        namaLengkap = result.nama ?: _uiState.value.namaLengkap,
                        alamatKtp = updatedKtp,
                        alamatDomisili = updatedDomisili,
                        pekerjaan = result.pekerjaan ?: _uiState.value.pekerjaan,
                        ocrSuccessMessage = if (!result.nik.isNullOrBlank() || !result.nama.isNullOrBlank() || !result.alamat.isNullOrBlank())
                            "Data e-KTP berhasil dipindai otomatis! Silakan periksa kembali."
                        else
                            "e-KTP berhasil diunggah. Mohon periksa kembali data di bawah."
                    )
                } else {
                    _uiState.value = _uiState.value.copy(isOcrProcessing = false)
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isOcrProcessing = false)
            }
        }
    }

    fun submitStep1Ktp() {
        val s = _uiState.value
        val custId = s.customerId
        if (custId.isNullOrBlank()) {
            _uiState.value = s.copy(errorMessage = "ID Customer tidak ditemukan. Mohon ulangi verifikasi email.")
            return
        }
        if (s.nik.length != 16) {
            _uiState.value = s.copy(errorMessage = "NIK harus berjumlah 16 digit angka")
            return
        }
        if (s.namaLengkap.isBlank()) {
            _uiState.value = s.copy(errorMessage = "Nama lengkap sesuai e-KTP wajib diisi")
            return
        }
        val ktp = s.alamatKtp
        if (ktp.alamatLengkap.isBlank() || ktp.rt.isBlank() || ktp.rw.isBlank() || ktp.kelurahan.isBlank() || ktp.kecamatan.isBlank() || ktp.kotaKabupaten.isBlank() || ktp.provinsi.isBlank()) {
            _uiState.value = s.copy(errorMessage = "Seluruh field alamat e-KTP wajib diisi")
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null, successMessage = null)
            try {
                val dataDto = RegisterStep1KtpRequestDto(
                    nik = s.nik.trim(),
                    namaLengkap = s.namaLengkap.trim(),
                    alamatKtp = AlamatCustomerDto(
                        alamatLengkap = ktp.alamatLengkap.trim(),
                        rt = ktp.rt.trim(),
                        rw = ktp.rw.trim(),
                        kelurahan = ktp.kelurahan.trim(),
                        kecamatan = ktp.kecamatan.trim(),
                        kotaKabupaten = ktp.kotaKabupaten.trim(),
                        provinsi = ktp.provinsi.trim(),
                        kodePos = ktp.kodePos.trim()
                    )
                )

                val context = getApplication<Application>().applicationContext
                val ktpBytes: ByteArray? = when {
                    s.ktpUri != null -> com.example.saku.app.core.util.ImageCompressorHelper.compressImageUri(context, s.ktpUri, maxDimension = 1920, quality = 82)
                    s.ktpBitmap != null -> com.example.saku.app.core.util.ImageCompressorHelper.compressBitmap(s.ktpBitmap, maxDimension = 1920, quality = 82)
                    else -> null
                }

                val ktpPart: MultipartBody.Part? = ktpBytes?.let {
                    val reqFile = it.toRequestBody("image/jpeg".toMediaTypeOrNull())
                    MultipartBody.Part.createFormData("ktp", "ktp_${custId}.jpg", reqFile)
                }

                val jsonStr = gson.toJson(dataDto)
                val dataPart = jsonStr.toRequestBody("application/json".toMediaTypeOrNull())

                val response = apiService.registerStep1Ktp(custId, ktpPart, dataPart)
                if (response.isSuccessful && response.body()?.isSuccess == true) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        currentStep = 3,
                        successMessage = "Data e-KTP berhasil disimpan. Lanjut ke data diri & keuangan."
                    )
                } else {
                    val errorMsg = ApiClient.parseError(response)
                    _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = errorMsg)
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = "Gagal mengirim data e-KTP. Periksa koneksi backend.")
            }
        }
    }

    // Step 3: Data Diri, Keuangan, Rekening & Domisili
    fun onNoHpChange(v: String) { _uiState.value = _uiState.value.copy(noHp = v, errorMessage = null) }
    fun onNamaIbuKandungChange(v: String) { _uiState.value = _uiState.value.copy(namaIbuKandung = v, errorMessage = null) }
    fun onNamaBankChange(v: String) { _uiState.value = _uiState.value.copy(namaBank = v, errorMessage = null) }
    fun onNoRekeningChange(v: String) { _uiState.value = _uiState.value.copy(noRekening = v, errorMessage = null) }
    fun onNamaRekeningChange(v: String) { _uiState.value = _uiState.value.copy(namaRekening = v, errorMessage = null) }
    fun onPekerjaanChange(v: String) { _uiState.value = _uiState.value.copy(pekerjaan = v, errorMessage = null) }
    fun onTempatKerjaChange(v: String) { _uiState.value = _uiState.value.copy(tempatKerja = v, errorMessage = null) }
    fun onStatusPekerjaanChange(v: String) { _uiState.value = _uiState.value.copy(statusPekerjaan = v, errorMessage = null) }
    fun onPendapatanChange(v: String) { _uiState.value = _uiState.value.copy(pendapatan = v, errorMessage = null) }
    fun onLamaBekerjaChange(v: String) { _uiState.value = _uiState.value.copy(lamaBekerjaBulan = v, errorMessage = null) }
    fun onCicilanLainnyaChange(v: String) { _uiState.value = _uiState.value.copy(totalCicilanLainnya = v, errorMessage = null) }
    fun onSameAsKtpToggle(same: Boolean) { _uiState.value = _uiState.value.copy(sameAsKtp = same, errorMessage = null) }
    fun onAlamatDomisiliChange(alamat: AlamatFormState) { _uiState.value = _uiState.value.copy(alamatDomisili = alamat, errorMessage = null) }

    fun submitStep2Personal() {
        val s = _uiState.value
        val custId = s.customerId
        if (custId.isNullOrBlank()) {
            _uiState.value = s.copy(errorMessage = "ID Customer tidak ditemukan")
            return
        }
        if (s.noHp.isBlank() || s.noRekening.isBlank() || s.namaRekening.isBlank() || s.pekerjaan.isBlank() || s.tempatKerja.isBlank()) {
            _uiState.value = s.copy(errorMessage = "Seluruh field data diri, pekerjaan & rekening wajib diisi")
            return
        }

        val domisili = if (s.sameAsKtp) s.alamatKtp else s.alamatDomisili
        if (!s.sameAsKtp && (domisili.alamatLengkap.isBlank() || domisili.rt.isBlank() || domisili.rw.isBlank() || domisili.kelurahan.isBlank() || domisili.kecamatan.isBlank() || domisili.kotaKabupaten.isBlank() || domisili.provinsi.isBlank())) {
            _uiState.value = s.copy(errorMessage = "Seluruh field alamat domisili wajib diisi jika berbeda dengan e-KTP")
            return
        }

        val pendapatanVal = s.pendapatan.replace(".", "").replace(",", "").toDoubleOrNull() ?: 0.0
        val lamaBekerjaVal = s.lamaBekerjaBulan.toIntOrNull() ?: 0
        val cicilanVal = s.totalCicilanLainnya.replace(".", "").replace(",", "").toDoubleOrNull() ?: 0.0

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null, successMessage = null)
            try {
                val req = RegisterStep2PersonalRequestDto(
                    noHp = s.noHp.trim(),
                    namaIbuKandung = if (s.namaIbuKandung.isNotBlank()) s.namaIbuKandung.trim() else null,
                    namaBank = s.namaBank.trim(),
                    noRekening = s.noRekening.trim(),
                    namaRekening = s.namaRekening.trim(),
                    pekerjaan = s.pekerjaan.trim(),
                    tempatKerja = s.tempatKerja.trim(),
                    statusPekerjaan = s.statusPekerjaan,
                    pendapatan = pendapatanVal,
                    lamaBekerjaBulan = lamaBekerjaVal,
                    totalCicilanLainnya = cicilanVal,
                    sameAsKtp = s.sameAsKtp,
                    alamatDomisili = if (s.sameAsKtp) null else AlamatCustomerDto(
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

                val response = apiService.registerStep2Personal(custId, req)
                if (response.isSuccessful && response.body()?.isSuccess == true) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        currentStep = 4,
                        successMessage = "Data pribadi & keuangan berhasil disimpan. Lanjut ke verifikasi wajah."
                    )
                } else {
                    val errorMsg = ApiClient.parseError(response)
                    _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = errorMsg)
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = "Koneksi gagal saat menyimpan data pribadi")
            }
        }
    }

    // Step 4: Liveness Selfie
    fun onSelfieUriSelected(uri: Uri?) {
        _uiState.value = _uiState.value.copy(selfieUri = uri, selfieBitmap = null, errorMessage = null)
    }

    fun onSelfieBitmapCaptured(bitmap: Bitmap?) {
        _uiState.value = _uiState.value.copy(selfieBitmap = bitmap, selfieUri = null, errorMessage = null)
    }

    fun submitStep3Liveness() {
        val s = _uiState.value
        val custId = s.customerId
        if (custId.isNullOrBlank()) {
            _uiState.value = s.copy(errorMessage = "ID Customer tidak ditemukan")
            return
        }
        if (s.selfieUri == null && s.selfieBitmap == null) {
            _uiState.value = s.copy(errorMessage = "Foto selfie verifikasi wajah wajib diambil")
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null, successMessage = null)
            try {
                val context = getApplication<Application>().applicationContext
                val selfieBytes: ByteArray? = when {
                    s.selfieUri != null -> com.example.saku.app.core.util.ImageCompressorHelper.compressImageUri(context, s.selfieUri, maxDimension = 1440, quality = 80)
                    s.selfieBitmap != null -> com.example.saku.app.core.util.ImageCompressorHelper.compressBitmap(s.selfieBitmap, maxDimension = 1440, quality = 80)
                    else -> null
                }

                if (selfieBytes == null) {
                    _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = "Gagal memproses file selfie")
                    return@launch
                }

                val reqFile = selfieBytes.toRequestBody("image/jpeg".toMediaTypeOrNull())
                val selfiePart = MultipartBody.Part.createFormData("selfie", "selfie_${custId}.jpg", reqFile)

                val response = apiService.registerStep3Liveness(custId, selfiePart)
                if (response.isSuccessful && response.body()?.isSuccess == true) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        currentStep = 5,
                        successMessage = "Verifikasi wajah berhasil! Lanjut ke Syarat & Ketentuan serta buat kata sandi."
                    )
                } else {
                    val errorMsg = ApiClient.parseError(response)
                    _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = errorMsg)
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = "Koneksi gagal saat mengunggah foto selfie")
            }
        }
    }

    // Step 5: T&C & Buat Password (Email Only)
    fun onTncAgreedToggle(agreed: Boolean) { _uiState.value = _uiState.value.copy(isTncAgreed = agreed, errorMessage = null) }
    fun onPasswordChange(v: String) { _uiState.value = _uiState.value.copy(password = v, errorMessage = null) }
    fun onConfirmPasswordChange(v: String) { _uiState.value = _uiState.value.copy(confirmPassword = v, errorMessage = null) }

    fun submitStep4And5() {
        val s = _uiState.value
        val custId = s.customerId
        if (custId.isNullOrBlank()) {
            _uiState.value = s.copy(errorMessage = "ID Customer tidak ditemukan")
            return
        }
        if (!s.isTncAgreed) {
            _uiState.value = s.copy(errorMessage = "Anda harus menyetujui Syarat & Ketentuan SAKU untuk melanjutkan")
            return
        }
        if (s.password.length < 8) {
            _uiState.value = s.copy(errorMessage = "Password minimal 8 karakter")
            return
        }
        if (s.password != s.confirmPassword) {
            _uiState.value = s.copy(errorMessage = "Konfirmasi password tidak sesuai")
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null, successMessage = null)
            try {
                // 1. Submit T&C
                val tncRes = apiService.registerStep4Tnc(custId)
                if (!tncRes.isSuccessful || tncRes.body()?.isSuccess != true) {
                    val errorMsg = ApiClient.parseError(tncRes)
                    _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = errorMsg)
                    return@launch
                }

                // 2. Submit Complete with Password
                val completeReq = RegisterStep5CompleteRequest(
                    password = s.password,
                    confirmPassword = s.confirmPassword
                )
                val completeRes = apiService.registerStep5Complete(custId, completeReq)
                if (completeRes.isSuccessful && completeRes.body()?.isSuccess == true) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isRegistrationComplete = true,
                        successMessage = "Pendaftaran Berhasil! Akun Anda sedang dalam proses verifikasi tim SAKU."
                    )
                } else {
                    val errorMsg = ApiClient.parseError(completeRes)
                    _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = errorMsg)
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = "Koneksi gagal saat menyelesaikan pendaftaran")
            }
        }
    }

    fun goToPreviousStep() {
        val current = _uiState.value.currentStep
        if (current > 1) {
            _uiState.value = _uiState.value.copy(currentStep = current - 1, errorMessage = null, successMessage = null)
        }
    }
}
