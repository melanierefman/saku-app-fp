package com.example.saku.app.features.auth.register

import android.app.Application
import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.saku.app.core.data.repository.AuthRepository
import com.example.saku.app.core.data.repository.WilayahRepository
import com.example.saku.app.core.network.ApiResult
import com.example.saku.app.core.network.dto.AlamatCustomerDto
import com.example.saku.app.core.network.dto.RegisterStep1KtpRequestDto
import com.example.saku.app.core.network.dto.RegisterStep2PersonalRequestDto
import com.example.saku.app.core.network.dto.RegisterStep5CompleteRequest
import com.example.saku.app.core.ui.components.DropdownOption
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

    // Data Wilayah (API Publik)
    val provinces: List<DropdownOption> = com.example.saku.app.core.data.repository.WilayahRepositoryImpl.DEFAULT_PROVINCES.map {
        DropdownOption(value = it.code, label = it.name)
    },
    val isLoadingWilayah: Boolean = false,

    // KTP Wilayah Options & Selection
    val selectedKtpProvince: DropdownOption? = null,
    val ktpRegencies: List<DropdownOption> = emptyList(),
    val isLoadingKtpRegencies: Boolean = false,
    val selectedKtpRegency: DropdownOption? = null,
    val ktpDistricts: List<DropdownOption> = emptyList(),
    val isLoadingKtpDistricts: Boolean = false,
    val selectedKtpDistrict: DropdownOption? = null,
    val ktpVillages: List<DropdownOption> = emptyList(),
    val isLoadingKtpVillages: Boolean = false,
    val selectedKtpVillage: DropdownOption? = null,

    // Domisili Wilayah Options & Selection
    val selectedDomisiliProvince: DropdownOption? = null,
    val domisiliRegencies: List<DropdownOption> = emptyList(),
    val isLoadingDomisiliRegencies: Boolean = false,
    val selectedDomisiliRegency: DropdownOption? = null,
    val domisiliDistricts: List<DropdownOption> = emptyList(),
    val isLoadingDomisiliDistricts: Boolean = false,
    val selectedDomisiliDistrict: DropdownOption? = null,
    val domisiliVillages: List<DropdownOption> = emptyList(),
    val isLoadingDomisiliVillages: Boolean = false,
    val selectedDomisiliVillage: DropdownOption? = null,

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

    // Dialog & Confirmation Modals
    val showExitConfirmationModal: Boolean = false,
    val showStep3ConfirmationModal: Boolean = false,
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
    private val wilayahRepository: WilayahRepository,
    application: Application
) : AndroidViewModel(application) {

    private val gson = Gson()
    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

    private var countdownJob: Job? = null

    init {
        loadProvinces()
    }

    
    // API Publik Wilayah Indonesia Cascading
    
    fun loadProvinces() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoadingWilayah = true)
            when (val res = wilayahRepository.getProvinces()) {
                is ApiResult.Success -> {
                    val opts = res.data.map { DropdownOption(value = it.code, label = it.name) }
                    _uiState.value = _uiState.value.copy(
                        provinces = opts,
                        isLoadingWilayah = false
                    )
                }
                is ApiResult.Error -> {
                    _uiState.value = _uiState.value.copy(isLoadingWilayah = false)
                }
                else -> {
                    _uiState.value = _uiState.value.copy(isLoadingWilayah = false)
                }
            }
        }
    }

    // KTP Cascading Selection
    fun onKtpProvinceSelected(option: DropdownOption?) {
        _uiState.value = _uiState.value.copy(
            selectedKtpProvince = option,
            selectedKtpRegency = null,
            selectedKtpDistrict = null,
            selectedKtpVillage = null,
            ktpRegencies = emptyList(),
            ktpDistricts = emptyList(),
            ktpVillages = emptyList(),
            isLoadingKtpRegencies = option != null,
            alamatKtp = _uiState.value.alamatKtp.copy(
                provinsi = option?.label ?: "",
                kotaKabupaten = "",
                kecamatan = "",
                kelurahan = ""
            ),
            errorMessage = null
        )

        if (option != null) {
            viewModelScope.launch {
                when (val res = wilayahRepository.getRegencies(option.value)) {
                    is ApiResult.Success -> {
                        val regencies = res.data.map { DropdownOption(value = it.code, label = it.name) }
                        _uiState.value = _uiState.value.copy(
                            ktpRegencies = regencies,
                            isLoadingKtpRegencies = false
                        )
                    }
                    is ApiResult.Error -> {
                        _uiState.value = _uiState.value.copy(
                            isLoadingKtpRegencies = false,
                            errorMessage = res.message
                        )
                    }
                    else -> {
                        _uiState.value = _uiState.value.copy(isLoadingKtpRegencies = false)
                    }
                }
            }
        }
    }

    fun onKtpRegencySelected(option: DropdownOption?) {
        _uiState.value = _uiState.value.copy(
            selectedKtpRegency = option,
            selectedKtpDistrict = null,
            selectedKtpVillage = null,
            ktpDistricts = emptyList(),
            ktpVillages = emptyList(),
            isLoadingKtpDistricts = option != null,
            alamatKtp = _uiState.value.alamatKtp.copy(
                kotaKabupaten = option?.label ?: "",
                kecamatan = "",
                kelurahan = ""
            ),
            errorMessage = null
        )

        if (option != null) {
            viewModelScope.launch {
                when (val res = wilayahRepository.getDistricts(option.value)) {
                    is ApiResult.Success -> {
                        val districts = res.data.map { DropdownOption(value = it.code, label = it.name) }
                        _uiState.value = _uiState.value.copy(
                            ktpDistricts = districts,
                            isLoadingKtpDistricts = false
                        )
                    }
                    is ApiResult.Error -> {
                        _uiState.value = _uiState.value.copy(
                            isLoadingKtpDistricts = false,
                            errorMessage = res.message
                        )
                    }
                    else -> {
                        _uiState.value = _uiState.value.copy(isLoadingKtpDistricts = false)
                    }
                }
            }
        }
    }

    fun onKtpDistrictSelected(option: DropdownOption?) {
        _uiState.value = _uiState.value.copy(
            selectedKtpDistrict = option,
            selectedKtpVillage = null,
            ktpVillages = emptyList(),
            isLoadingKtpVillages = option != null,
            alamatKtp = _uiState.value.alamatKtp.copy(
                kecamatan = option?.label ?: "",
                kelurahan = ""
            ),
            errorMessage = null
        )

        if (option != null) {
            viewModelScope.launch {
                when (val res = wilayahRepository.getVillages(option.value)) {
                    is ApiResult.Success -> {
                        val villages = res.data.map { DropdownOption(value = it.code, label = it.name) }
                        _uiState.value = _uiState.value.copy(
                            ktpVillages = villages,
                            isLoadingKtpVillages = false
                        )
                    }
                    is ApiResult.Error -> {
                        _uiState.value = _uiState.value.copy(
                            isLoadingKtpVillages = false,
                            errorMessage = res.message
                        )
                    }
                    else -> {
                        _uiState.value = _uiState.value.copy(isLoadingKtpVillages = false)
                    }
                }
            }
        }
    }

    fun onKtpVillageSelected(option: DropdownOption?) {
        _uiState.value = _uiState.value.copy(
            selectedKtpVillage = option,
            alamatKtp = _uiState.value.alamatKtp.copy(
                kelurahan = option?.label ?: ""
            ),
            errorMessage = null
        )
    }

    // Domisili Cascading Selection
    fun onDomisiliProvinceSelected(option: DropdownOption?) {
        _uiState.value = _uiState.value.copy(
            selectedDomisiliProvince = option,
            selectedDomisiliRegency = null,
            selectedDomisiliDistrict = null,
            selectedDomisiliVillage = null,
            domisiliRegencies = emptyList(),
            domisiliDistricts = emptyList(),
            domisiliVillages = emptyList(),
            isLoadingDomisiliRegencies = option != null,
            alamatDomisili = _uiState.value.alamatDomisili.copy(
                provinsi = option?.label ?: "",
                kotaKabupaten = "",
                kecamatan = "",
                kelurahan = ""
            ),
            errorMessage = null
        )

        if (option != null) {
            viewModelScope.launch {
                when (val res = wilayahRepository.getRegencies(option.value)) {
                    is ApiResult.Success -> {
                        val regencies = res.data.map { DropdownOption(value = it.code, label = it.name) }
                        _uiState.value = _uiState.value.copy(
                            domisiliRegencies = regencies,
                            isLoadingDomisiliRegencies = false
                        )
                    }
                    is ApiResult.Error -> {
                        _uiState.value = _uiState.value.copy(
                            isLoadingDomisiliRegencies = false,
                            errorMessage = res.message
                        )
                    }
                    else -> {
                        _uiState.value = _uiState.value.copy(isLoadingDomisiliRegencies = false)
                    }
                }
            }
        }
    }

    fun onDomisiliRegencySelected(option: DropdownOption?) {
        _uiState.value = _uiState.value.copy(
            selectedDomisiliRegency = option,
            selectedDomisiliDistrict = null,
            selectedDomisiliVillage = null,
            domisiliDistricts = emptyList(),
            domisiliVillages = emptyList(),
            isLoadingDomisiliDistricts = option != null,
            alamatDomisili = _uiState.value.alamatDomisili.copy(
                kotaKabupaten = option?.label ?: "",
                kecamatan = "",
                kelurahan = ""
            ),
            errorMessage = null
        )

        if (option != null) {
            viewModelScope.launch {
                when (val res = wilayahRepository.getDistricts(option.value)) {
                    is ApiResult.Success -> {
                        val districts = res.data.map { DropdownOption(value = it.code, label = it.name) }
                        _uiState.value = _uiState.value.copy(
                            domisiliDistricts = districts,
                            isLoadingDomisiliDistricts = false
                        )
                    }
                    is ApiResult.Error -> {
                        _uiState.value = _uiState.value.copy(
                            isLoadingDomisiliDistricts = false,
                            errorMessage = res.message
                        )
                    }
                    else -> {
                        _uiState.value = _uiState.value.copy(isLoadingDomisiliDistricts = false)
                    }
                }
            }
        }
    }

    fun onDomisiliDistrictSelected(option: DropdownOption?) {
        _uiState.value = _uiState.value.copy(
            selectedDomisiliDistrict = option,
            selectedDomisiliVillage = null,
            domisiliVillages = emptyList(),
            isLoadingDomisiliVillages = option != null,
            alamatDomisili = _uiState.value.alamatDomisili.copy(
                kecamatan = option?.label ?: "",
                kelurahan = ""
            ),
            errorMessage = null
        )

        if (option != null) {
            viewModelScope.launch {
                when (val res = wilayahRepository.getVillages(option.value)) {
                    is ApiResult.Success -> {
                        val villages = res.data.map { DropdownOption(value = it.code, label = it.name) }
                        _uiState.value = _uiState.value.copy(
                            domisiliVillages = villages,
                            isLoadingDomisiliVillages = false
                        )
                    }
                    is ApiResult.Error -> {
                        _uiState.value = _uiState.value.copy(
                            isLoadingDomisiliVillages = false,
                            errorMessage = res.message
                        )
                    }
                    else -> {
                        _uiState.value = _uiState.value.copy(isLoadingDomisiliVillages = false)
                    }
                }
            }
        }
    }

    fun onDomisiliVillageSelected(option: DropdownOption?) {
        _uiState.value = _uiState.value.copy(
            selectedDomisiliVillage = option,
            alamatDomisili = _uiState.value.alamatDomisili.copy(
                kelurahan = option?.label ?: ""
            ),
            errorMessage = null
        )
    }

    
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
                        successMessage = "Email berhasil diverifikasi! Silakan lengkapi data pribadi Anda."
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

    fun resendOtp() {
        if (_uiState.value.otpCountdown > 0) return
        sendOtp()
    }

    
    // Step 1: Data Pribadi & Identitas
    
    fun onNikChange(v: String) {
        val filtered = v.filter { it.isDigit() }.take(16)
        _uiState.value = _uiState.value.copy(nik = filtered, errorMessage = null)
    }
    fun onNamaLengkapChange(v: String) { _uiState.value = _uiState.value.copy(namaLengkap = v, errorMessage = null) }
    fun onNoHpChange(v: String) {
        val filtered = v.filter { it.isDigit() || it == '+' }.take(14)
        _uiState.value = _uiState.value.copy(noHp = filtered, errorMessage = null)
    }

    fun submitStep1Personal() {
        val s = _uiState.value
        if (s.nik.length != 16) {
            _uiState.value = s.copy(errorMessage = "NIK harus berjumlah 16 digit angka")
            return
        }
        if (s.namaLengkap.trim().length < 3) {
            _uiState.value = s.copy(errorMessage = "Nama lengkap sesuai e-KTP minimal 3 karakter")
            return
        }
        val cleanHp = s.noHp.trim().removePrefix("+")
        if (!cleanHp.matches(Regex("^(08|628)[0-9]{8,12}$"))) {
            _uiState.value = s.copy(errorMessage = "Nomor handphone harus diawali 08 (10-14 digit angka)")
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            
            // 1. Validasi keunikan NIK langsung ke database backend
            when (val nikRes = authRepository.checkNik(s.nik.trim(), s.customerId)) {
                is ApiResult.Error -> {
                    _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = nikRes.message)
                    return@launch
                }
                else -> {}
            }

            // 2. Validasi keunikan No HP langsung ke database backend
            when (val phoneRes = authRepository.checkPhone(s.noHp.trim(), s.customerId)) {
                is ApiResult.Error -> {
                    _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = phoneRes.message)
                    return@launch
                }
                else -> {}
            }

            // Lolos validasi NIK & No HP, lanjut ke Step 2 (Data Pekerjaan & Rekening)
            _uiState.value = _uiState.value.copy(
                isLoading = false,
                currentStep = 2,
                errorMessage = null,
                successMessage = null
            )
        }
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
        val pend = s.pendapatan.filter { it.isDigit() }.toDoubleOrNull() ?: 0.0
        if (pend < 1_000_000.0) {
            _uiState.value = s.copy(errorMessage = "Pendapatan bersih bulanan minimal Rp 1.000.000")
            return
        }
        val cicilan = s.totalCicilanLainnya.filter { it.isDigit() }.toDoubleOrNull() ?: 0.0
        if (cicilan > pend) {
            _uiState.value = s.copy(errorMessage = "Total cicilan lain tidak boleh melebihi pendapatan bulanan")
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
        if (s.noRekening.trim().length < 8) {
            _uiState.value = s.copy(errorMessage = "Nomor rekening minimal 8 digit angka")
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

    fun openStep3ConfirmationModal() {
        val s = _uiState.value
        val ktp = s.alamatKtp
        if (ktp.provinsi.isBlank() || ktp.kotaKabupaten.isBlank() || ktp.kecamatan.isBlank() || ktp.kelurahan.isBlank() ||
            ktp.alamatLengkap.isBlank() || ktp.rt.isBlank() || ktp.rw.isBlank() || ktp.kodePos.isBlank()) {
            _uiState.value = s.copy(errorMessage = "Seluruh field alamat e-KTP wajib dilengkapi (Provinsi s/d Kode Pos)")
            return
        }

        val domisili = if (s.sameAsKtp) ktp else s.alamatDomisili
        if (!s.sameAsKtp && (domisili.provinsi.isBlank() || domisili.kotaKabupaten.isBlank() || domisili.kecamatan.isBlank() || domisili.kelurahan.isBlank() ||
            domisili.alamatLengkap.isBlank() || domisili.rt.isBlank() || domisili.rw.isBlank() || domisili.kodePos.isBlank())) {
            _uiState.value = s.copy(errorMessage = "Seluruh field alamat domisili wajib dilengkapi jika berbeda dengan e-KTP")
            return
        }

        _uiState.value = s.copy(showStep3ConfirmationModal = true, errorMessage = null)
    }

    fun dismissStep3ConfirmationModal() {
        _uiState.value = _uiState.value.copy(showStep3ConfirmationModal = false)
    }

    fun submitStep3Address() {
        val s = _uiState.value
        val custId = s.customerId
        if (custId.isNullOrBlank()) {
            _uiState.value = s.copy(errorMessage = "ID Customer tidak ditemukan. Mohon ulangi verifikasi email.", showStep3ConfirmationModal = false)
            return
        }

        val ktp = s.alamatKtp
        val domisili = if (s.sameAsKtp) ktp else s.alamatDomisili

        val pendapatanVal = s.pendapatan.replace(".", "").replace(",", "").toDoubleOrNull() ?: 0.0
        val lamaBekerjaVal = s.lamaBekerjaBulan.toIntOrNull() ?: 0
        val cicilanVal = s.totalCicilanLainnya.replace(".", "").replace(",", "").toDoubleOrNull() ?: 0.0

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, showStep3ConfirmationModal = false, errorMessage = null, successMessage = null)
            try {
                // 1. Simpan Data e-KTP di Backend
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

                // 2. Simpan Data Pribadi, Pekerjaan, Rekening, Domisili di Backend
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

    
    // Step 4: Upload Dokumen KYC
    
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

                // 3. Upload Dokumen KTP jika ada
                if (ktpPart != null) {
                    val ktpDto = RegisterStep1KtpRequestDto(
                        nik = s.nik.trim(),
                        namaLengkap = s.namaLengkap.trim(),
                        alamatKtp = AlamatCustomerDto(
                            alamatLengkap = s.alamatKtp.alamatLengkap.trim(),
                            rt = s.alamatKtp.rt.trim(),
                            rw = s.alamatKtp.rw.trim(),
                            kelurahan = s.alamatKtp.kelurahan.trim(),
                            kecamatan = s.alamatKtp.kecamatan.trim(),
                            kotaKabupaten = s.alamatKtp.kotaKabupaten.trim(),
                            provinsi = s.alamatKtp.provinsi.trim(),
                            kodePos = s.alamatKtp.kodePos.trim()
                        )
                    )
                    val jsonString = com.google.gson.Gson().toJson(ktpDto)
                    val dataPart = jsonString.toRequestBody("application/json".toMediaTypeOrNull())
                    val ktpRes = authRepository.registerStep1Ktp(custId, ktpPart, dataPart)
                    if (ktpRes is ApiResult.Error) {
                        _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = ktpRes.message)
                        return@launch
                    }
                }

                // 4. Upload Dokumen Selfie Wajah Liveness
                if (selfiePart != null) {
                    val selfieRes = authRepository.registerStep3Liveness(custId, selfiePart)
                    if (selfieRes is ApiResult.Error) {
                        _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = selfieRes.message)
                        return@launch
                    }
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

    
    // Back Navigation & Dialog Control
    
    fun onBackPress(onExit: () -> Unit) {
        val current = _uiState.value.currentStep
        if (current > 0) {
            _uiState.value = _uiState.value.copy(currentStep = current - 1, errorMessage = null, successMessage = null)
        } else {
            _uiState.value = _uiState.value.copy(showExitConfirmationModal = true)
        }
    }

    fun dismissExitModal() {
        _uiState.value = _uiState.value.copy(showExitConfirmationModal = false)
    }

    fun goToPreviousStep() {
        val current = _uiState.value.currentStep
        if (current > 0) {
            _uiState.value = _uiState.value.copy(currentStep = current - 1, errorMessage = null, successMessage = null)
        }
    }

    fun clearSuccessMessage() {
        _uiState.value = _uiState.value.copy(successMessage = null)
    }
}