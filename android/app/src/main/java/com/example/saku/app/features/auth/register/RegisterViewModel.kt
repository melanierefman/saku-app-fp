package com.example.saku.app.features.auth.register

import android.app.Application
import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.saku.app.core.data.TokenManager
import com.example.saku.app.core.data.repository.AuthRepository
import com.example.saku.app.core.data.repository.AuthRepositoryImpl
import com.example.saku.app.core.network.ApiClient
import com.example.saku.app.core.network.ApiResult
import com.example.saku.app.core.network.dto.AlamatCustomerDto
import com.example.saku.app.core.network.dto.RegisterStep1KtpRequestDto
import com.example.saku.app.core.network.dto.RegisterStep2PersonalRequestDto
import com.example.saku.app.core.network.dto.RegisterStep5CompleteRequest
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

data class RegisterUiState(
    val currentStep: Int = 0, // 0: Email OTP (Pra-Step), 1: Data Pribadi & Identitas, 2: Pekerjaan & Rekening, 3: Alamat KTP & Domisili, 4: Upload KYC, 5: S&K, 6: Kredensial
    val customerId: String? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null,
    val isRegistrationComplete: Boolean = false,

    // Pra-Step: Email & OTP Verification
    val email: String = "",
    val otpCode: String = "",
    val isOtpSent: Boolean = false,
    val isOtpVerified: Boolean = false,
    val otpCountdown: Int = 0,

    // Step 1: Data Pribadi & Identitas (Manual Input)
    val nik: String = "",
    val namaLengkap: String = "",
    val noHp: String = "",

    // Step 2: Data Pekerjaan & Rekening
    val pekerjaan: String = "",
    val tempatKerja: String = "",
    val statusPekerjaan: String = "KARYAWAN_TETAP",
    val pendapatan: String = "",
    val lamaBekerjaBulan: String = "",
    val totalCicilanLainnya: String = "0",
    val namaBank: String = "BCA",
    val noRekening: String = "",
    val namaRekening: String = "",
    val namaIbuKandung: String = "",

    // Step 3: Alamat KTP & Domisili
    val alamatKtp: AlamatFormState = AlamatFormState(),
    val sameAsKtp: Boolean = true,
    val alamatDomisili: AlamatFormState = AlamatFormState(),

    // Step 4: Upload Dokumen KYC (Foto e-KTP & Selfie)
    val ktpUri: Uri? = null,
    val ktpBitmap: Bitmap? = null,
    val selfieUri: Uri? = null,
    val selfieBitmap: Bitmap? = null,

    // Step 5: Buat Kredensial (Kata Sandi)
    val password: String = "",
    val confirmPassword: String = "",

    // Step 6: Syarat & Ketentuan (Langkah Terakhir)
    val isTncAgreed: Boolean = false,
    val showConfirmationModal: Boolean = false
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

class RegisterViewModel(
    private val authRepository: AuthRepository,
    application: Application
) : AndroidViewModel(application) {

    private val gson = Gson()
    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

    private var countdownJob: Job? = null

    // Pra-Step: Email & OTP Handling
    fun onEmailChange(v: String) { _uiState.value = _uiState.value.copy(email = v, errorMessage = null) }
    fun onOtpCodeChange(v: String) {
        val filtered = v.filter { it.isDigit() }.take(6)
        _uiState.value = _uiState.value.copy(otpCode = filtered, errorMessage = null)
    }

    fun sendOtp() {
        val email = _uiState.value.email.trim()
        if (email.isBlank() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _uiState.value = _uiState.value.copy(errorMessage = "Format email tidak valid")
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null, successMessage = null)
            when (val result = authRepository.sendOtp(email, "REGISTRATION")) {
                is ApiResult.Success -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isOtpSent = true,
                        successMessage = "Kode OTP 6-digit telah dikirim ke $email"
                    )
                    startCountdown()
                }
                is ApiResult.Error -> {
                    _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = result.message)
                }
                else -> {
                    _uiState.value = _uiState.value.copy(isLoading = false)
                }
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
            when (val result = authRepository.verifyOtp(email, otp, "REGISTRATION")) {
                is ApiResult.Success -> {
                    val custId = result.data.customerId
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isOtpVerified = true,
                        customerId = custId,
                        currentStep = 1,
                        successMessage = "Email berhasil diverifikasi! Silakan lengkapi data identitas."
                    )
                }
                is ApiResult.Error -> {
                    _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = result.message)
                }
                else -> {
                    _uiState.value = _uiState.value.copy(isLoading = false)
                }
            }
        }
    }

    // Step 1: Data Pribadi & Identitas (Manual Input)
    fun onNikChange(v: String) {
        val filtered = v.filter { it.isDigit() }.take(16)
        _uiState.value = _uiState.value.copy(nik = filtered, errorMessage = null)
    }
    fun onNamaLengkapChange(v: String) { _uiState.value = _uiState.value.copy(namaLengkap = v, errorMessage = null) }
    fun onNoHpChange(v: String) {
        val filtered = v.filter { it.isDigit() || it == '+' }.take(16)
        _uiState.value = _uiState.value.copy(noHp = filtered, errorMessage = null)
    }

    fun submitStep1Personal() {
        val s = _uiState.value
        if (s.nik.length != 16) {
            _uiState.value = s.copy(errorMessage = "NIK harus berjumlah 16 digit angka")
            return
        }
        if (s.namaLengkap.trim().isBlank()) {
            _uiState.value = s.copy(errorMessage = "Nama lengkap sesuai e-KTP wajib diisi")
            return
        }
        if (s.noHp.trim().length < 9) {
            _uiState.value = s.copy(errorMessage = "Nomor handphone tidak valid (minimal 9 digit)")
            return
        }

        // Lanjut ke Step 2 (Data Pekerjaan & Rekening)
        _uiState.value = s.copy(currentStep = 2, errorMessage = null, successMessage = null)
    }

    // Step 2: Data Pekerjaan & Rekening
    fun onPekerjaanChange(v: String) { _uiState.value = _uiState.value.copy(pekerjaan = v, errorMessage = null) }
    fun onTempatKerjaChange(v: String) { _uiState.value = _uiState.value.copy(tempatKerja = v, errorMessage = null) }
    fun onStatusPekerjaanChange(v: String) { _uiState.value = _uiState.value.copy(statusPekerjaan = v, errorMessage = null) }
    fun onPendapatanChange(v: String) { _uiState.value = _uiState.value.copy(pendapatan = v, errorMessage = null) }
    fun onLamaBekerjaChange(v: String) {
        val filtered = v.filter { it.isDigit() }.take(4)
        _uiState.value = _uiState.value.copy(lamaBekerjaBulan = filtered, errorMessage = null)
    }
    fun onCicilanLainnyaChange(v: String) { _uiState.value = _uiState.value.copy(totalCicilanLainnya = v, errorMessage = null) }
    fun onNamaBankChange(v: String) { _uiState.value = _uiState.value.copy(namaBank = v, errorMessage = null) }
    fun onNoRekeningChange(v: String) {
        val filtered = v.filter { it.isDigit() }.take(20)
        _uiState.value = _uiState.value.copy(noRekening = filtered, errorMessage = null)
    }
    fun onNamaRekeningChange(v: String) { _uiState.value = _uiState.value.copy(namaRekening = v, errorMessage = null) }
    fun onNamaIbuKandungChange(v: String) { _uiState.value = _uiState.value.copy(namaIbuKandung = v, errorMessage = null) }

    fun submitStep2WorkAndBank() {
        val s = _uiState.value
        if (s.pekerjaan.trim().isBlank()) {
            _uiState.value = s.copy(errorMessage = "Profesi / Pekerjaan wajib diisi")
            return
        }
        if (s.tempatKerja.trim().isBlank()) {
            _uiState.value = s.copy(errorMessage = "Nama perusahaan / tempat bekerja wajib diisi")
            return
        }
        if (s.pendapatan.trim().isBlank() || s.pendapatan == "0") {
            _uiState.value = s.copy(errorMessage = "Pendapatan bersih bulanan wajib diisi")
            return
        }
        if (s.lamaBekerjaBulan.trim().isBlank()) {
            _uiState.value = s.copy(errorMessage = "Lama bekerja wajib diisi")
            return
        }
        if (s.namaBank.trim().isBlank()) {
            _uiState.value = s.copy(errorMessage = "Bank pencairan wajib dipilih")
            return
        }
        if (s.noRekening.trim().isBlank()) {
            _uiState.value = s.copy(errorMessage = "Nomor rekening wajib diisi")
            return
        }
        if (s.namaRekening.trim().isBlank()) {
            _uiState.value = s.copy(errorMessage = "Nama pemilik rekening wajib diisi")
            return
        }
        if (s.namaIbuKandung.trim().isBlank()) {
            _uiState.value = s.copy(errorMessage = "Nama gadis ibu kandung wajib diisi")
            return
        }

        // Lanjut ke Step 3 (Alamat KTP & Domisili)
        _uiState.value = s.copy(currentStep = 3, errorMessage = null, successMessage = null)
    }

    // Step 3: Alamat KTP & Domisili
    fun onAlamatKtpChange(alamat: AlamatFormState) { _uiState.value = _uiState.value.copy(alamatKtp = alamat, errorMessage = null) }
    fun onSameAsKtpToggle(same: Boolean) { _uiState.value = _uiState.value.copy(sameAsKtp = same, errorMessage = null) }
    fun onAlamatDomisiliChange(alamat: AlamatFormState) { _uiState.value = _uiState.value.copy(alamatDomisili = alamat, errorMessage = null) }

    fun submitStep3Address() {
        val s = _uiState.value
        val custId = s.customerId
        if (custId.isNullOrBlank()) {
            _uiState.value = s.copy(errorMessage = "ID Customer tidak ditemukan. Mohon ulangi verifikasi email.")
            return
        }

        val ktp = s.alamatKtp
        if (ktp.alamatLengkap.isBlank() || ktp.rt.isBlank() || ktp.rw.isBlank() || ktp.kelurahan.isBlank() || ktp.kecamatan.isBlank() || ktp.kotaKabupaten.isBlank() || ktp.provinsi.isBlank() || ktp.kodePos.isBlank()) {
            _uiState.value = s.copy(errorMessage = "Seluruh field alamat e-KTP dan kode pos wajib diisi")
            return
        }

        val domisili = if (s.sameAsKtp) ktp else s.alamatDomisili
        if (!s.sameAsKtp && (domisili.alamatLengkap.isBlank() || domisili.rt.isBlank() || domisili.rw.isBlank() || domisili.kelurahan.isBlank() || domisili.kecamatan.isBlank() || domisili.kotaKabupaten.isBlank() || domisili.provinsi.isBlank() || domisili.kodePos.isBlank())) {
            _uiState.value = s.copy(errorMessage = "Seluruh field alamat domisili dan kode pos wajib diisi jika berbeda dengan e-KTP")
            return
        }

        val pendapatanVal = s.pendapatan.replace(".", "").replace(",", "").toDoubleOrNull() ?: 0.0
        val lamaBekerjaVal = s.lamaBekerjaBulan.toIntOrNull() ?: 0
        val cicilanVal = s.totalCicilanLainnya.replace(".", "").replace(",", "").toDoubleOrNull() ?: 0.0

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null, successMessage = null)
            try {
                // 1. Simpan Data e-KTP & Cek Keunikan NIK di Backend
                val ktpDto = RegisterStep1KtpRequestDto(
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

                val resKtp = authRepository.registerStep1KtpJson(custId, ktpDto)
                if (resKtp is ApiResult.Error) {
                    _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = resKtp.message)
                    return@launch
                }

                // 2. Simpan Data Pribadi, Pekerjaan, Rekening, Domisili & Cek Keunikan No HP di Backend
                val personalDto = RegisterStep2PersonalRequestDto(
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

                val resPersonal = authRepository.registerStep2Personal(custId, personalDto)
                if (resPersonal is ApiResult.Error) {
                    _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = resPersonal.message)
                    return@launch
                }

                // Sukses verifikasi backend, lanjut ke Step 4 (Upload Dokumen KYC)
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    currentStep = 4,
                    successMessage = "Data pribadi & alamat tersimpan. Lanjut ke upload dokumen KYC."
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = "Koneksi backend bermasalah. Pastikan server aktif.")
            }
        }
    }

    // Step 4: Upload Dokumen KYC (Foto e-KTP & Foto Selfie) — Tanpa OCR Otomatis
    fun onKtpImageSelected(uri: Uri?) {
        _uiState.value = _uiState.value.copy(ktpUri = uri, ktpBitmap = null, errorMessage = null)
    }

    fun onKtpBitmapCaptured(bitmap: Bitmap?) {
        _uiState.value = _uiState.value.copy(ktpBitmap = bitmap, ktpUri = null, errorMessage = null)
    }

    fun onSelfieUriSelected(uri: Uri?) {
        _uiState.value = _uiState.value.copy(selfieUri = uri, selfieBitmap = null, errorMessage = null)
    }

    fun onSelfieBitmapCaptured(bitmap: Bitmap?) {
        _uiState.value = _uiState.value.copy(selfieBitmap = bitmap, selfieUri = null, errorMessage = null)
    }

    fun submitStep4Documents() {
        val s = _uiState.value
        val custId = s.customerId
        if (custId.isNullOrBlank()) {
            _uiState.value = s.copy(errorMessage = "ID Customer tidak ditemukan")
            return
        }
        if (s.ktpUri == null && s.ktpBitmap == null) {
            _uiState.value = s.copy(errorMessage = "Foto fisik e-KTP wajib diunggah")
            return
        }
        if (s.selfieUri == null && s.selfieBitmap == null) {
            _uiState.value = s.copy(errorMessage = "Foto selfie wajah wajib diunggah")
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null, successMessage = null)
            try {
                val context = getApplication<Application>().applicationContext

                // 1. Prepare Berkas Foto e-KTP
                val ktpBytes: ByteArray? = when {
                    s.ktpUri != null -> com.example.saku.app.core.util.ImageCompressorHelper.compressImageUri(context, s.ktpUri, maxDimension = 1920, quality = 82)
                    s.ktpBitmap != null -> com.example.saku.app.core.util.ImageCompressorHelper.compressBitmap(s.ktpBitmap, maxDimension = 1920, quality = 82)
                    else -> null
                }

                val ktpPart: MultipartBody.Part? = ktpBytes?.let {
                    val reqFile = it.toRequestBody("image/jpeg".toMediaTypeOrNull())
                    MultipartBody.Part.createFormData("ktp", "ktp_${custId}.jpg", reqFile)
                }

                // 2. Prepare Berkas Foto Selfie
                val selfieBytes: ByteArray? = when {
                    s.selfieUri != null -> com.example.saku.app.core.util.ImageCompressorHelper.compressImageUri(context, s.selfieUri, maxDimension = 1440, quality = 80)
                    s.selfieBitmap != null -> com.example.saku.app.core.util.ImageCompressorHelper.compressBitmap(s.selfieBitmap, maxDimension = 1440, quality = 80)
                    else -> null
                }

                val selfiePart: MultipartBody.Part? = selfieBytes?.let {
                    val selfieReqFile = it.toRequestBody("image/jpeg".toMediaTypeOrNull())
                    MultipartBody.Part.createFormData("selfie", "selfie_${custId}.jpg", selfieReqFile)
                }

                // 3. Upload Dokumen KYC (KTP & Selfie) via Register Step 4 Endpoint
                val uploadRes = authRepository.registerStep4(custId, ktpPart, selfiePart)
                if (uploadRes is ApiResult.Error) {
                    _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = uploadRes.message)
                    return@launch
                }

                // Sukses unggah dokumen KYC, lanjut ke Step 5 (Buat Kata Sandi)
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    currentStep = 5,
                    successMessage = "Dokumen KYC berhasil diunggah! Lanjut membuat kata sandi akun Anda."
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = "Gagal mengunggah dokumen. Periksa koneksi backend Anda.")
            }
        }
    }

    // Step 5: Buat Kredensial (Kata Sandi)
    fun onPasswordChange(v: String) { _uiState.value = _uiState.value.copy(password = v, errorMessage = null) }
    fun onConfirmPasswordChange(v: String) { _uiState.value = _uiState.value.copy(confirmPassword = v, errorMessage = null) }

    fun submitStep5Credentials() {
        val s = _uiState.value
        if (s.password.length < 8) {
            _uiState.value = s.copy(errorMessage = "Kata sandi minimal 8 karakter")
            return
        }
        if (s.password != s.confirmPassword) {
            _uiState.value = s.copy(errorMessage = "Konfirmasi kata sandi tidak sesuai")
            return
        }
        _uiState.value = s.copy(
            currentStep = 6,
            errorMessage = null,
            successMessage = "Kata sandi tersimpan. Langkah terakhir: pelajari dan setujui Syarat & Ketentuan."
        )
    }

    // Step 6: Syarat & Ketentuan (Langkah Terakhir Pendaftaran)
    fun onTncAgreedToggle(agreed: Boolean) { _uiState.value = _uiState.value.copy(isTncAgreed = agreed, errorMessage = null) }

    fun openConfirmationModal() {
        val s = _uiState.value
        if (!s.isTncAgreed) {
            _uiState.value = s.copy(errorMessage = "Anda harus menyetujui Syarat & Ketentuan SAKU untuk melanjutkan")
            return
        }
        _uiState.value = s.copy(showConfirmationModal = true, errorMessage = null)
    }

    fun dismissConfirmationModal() {
        _uiState.value = _uiState.value.copy(showConfirmationModal = false)
    }

    fun submitStep6FinalRegistration() {
        val s = _uiState.value
        val custId = s.customerId
        if (custId.isNullOrBlank()) {
            _uiState.value = s.copy(errorMessage = "ID Customer tidak ditemukan", showConfirmationModal = false)
            return
        }
        if (!s.isTncAgreed) {
            _uiState.value = s.copy(errorMessage = "Anda harus menyetujui Syarat & Ketentuan SAKU untuk melanjutkan", showConfirmationModal = false)
            return
        }
        if (s.password.length < 8 || s.password != s.confirmPassword) {
            _uiState.value = s.copy(errorMessage = "Kata sandi tidak valid. Silakan kembali ke langkah sebelumnya.", showConfirmationModal = false)
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, showConfirmationModal = false, errorMessage = null, successMessage = null)
            
            // 1. Submit T&C acceptance
            try {
                authRepository.registerStep4Tnc(custId)
            } catch (e: Exception) {
                // Log and continue
            }

            // 2. Complete registration with password
            val completeReq = RegisterStep5CompleteRequest(
                password = s.password,
                confirmPassword = s.confirmPassword
            )
            when (val completeRes = authRepository.registerStep5Complete(custId, completeReq)) {
                is ApiResult.Success -> {
                    // Otomatis login agar session token tersimpan untuk halaman Status Verifikasi
                    authRepository.login(s.email.trim(), s.password)
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isRegistrationComplete = true,
                        successMessage = "Pendaftaran Berhasil! Akun Anda sedang dalam proses verifikasi tim Backoffice SAKU."
                    )
                }
                is ApiResult.Error -> {
                    _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = completeRes.message)
                }
                else -> {
                    _uiState.value = _uiState.value.copy(isLoading = false)
                }
            }
        }
    }

    fun goToPreviousStep() {
        val current = _uiState.value.currentStep
        if (current > 0) {
            _uiState.value = _uiState.value.copy(currentStep = current - 1, errorMessage = null, successMessage = null)
        }
    }
}
