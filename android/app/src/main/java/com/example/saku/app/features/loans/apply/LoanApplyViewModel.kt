package com.example.saku.app.features.loans.apply

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.saku.app.core.data.repository.CustomerRepository
import com.example.saku.app.core.data.repository.LoanRepository
import com.example.saku.app.core.network.ApiResult
import com.example.saku.app.core.network.dto.CustomerProfileDto
import com.example.saku.app.core.network.dto.LoanApplicationItemDto
import com.example.saku.app.core.network.dto.PengajuanPinjamanRequestDto
import com.example.saku.app.core.network.dto.PengajuanStepResponseDto
import com.example.saku.app.core.util.ImageCompressorHelper
import com.google.gson.Gson
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import kotlin.math.roundToLong

data class LoanApplyUiState(
    val currentStep: Int = 1, // 1: Nominal & Tenor, 2: Upload Dokumen, 3: Ringkasan & Submit, 4: Sukses
    val availablePlafond: Double = 50_000_000.0,
    val totalPlafond: Double = 50_000_000.0,

    // Step 1: Input Nominal & Tenor
    val jumlahPinjaman: Double = 5_000_000.0,
    val tenorBulan: Int = 6,
    val tujuanPinjaman: String = "Modal Usaha",
    val customTujuan: String = "",

    // Dynamic Rate & Calculation from BE
    val sukuBungaPersen: Double = 1.5,
    val biayaAdmin: Double = 50_000.0,
    val bungaBulanan: Double = 75_000.0,
    val estimasiCicilanBulanan: Long = 908_333L,
    val totalPengembalian: Long = 5_500_000L,
    val tierName: String = "Reguler",

    // Bank Account Information
    val namaBank: String = "BCA",
    val noRekening: String = "",
    val namaRekening: String = "",

    val isFromSimulation: Boolean = false,

    // Step 1 Output
    val pengajuanId: String? = null,
    val nomorPengajuan: String? = null,
    val step1Response: PengajuanStepResponseDto? = null,

    // Step 2: Documents
    val slipGajiUri: Uri? = null,
    val slipGajiBitmap: Bitmap? = null,
    val rekeningKoranUri: Uri? = null,
    val rekeningKoranBitmap: Bitmap? = null,
    val npwpUri: Uri? = null,
    val npwpBitmap: Bitmap? = null,

    // Step 3: Confirmation & Agreement
    val isTncAgreed: Boolean = false,

    // Status
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null,
    val submittedLoanResult: LoanApplicationItemDto? = null
) {
    val effectiveTujuan: String
        get() = if (tujuanPinjaman == "Lainnya" && customTujuan.isNotBlank()) customTujuan.trim() else tujuanPinjaman

    val pokokPerBulan: Double
        get() = if (tenorBulan > 0) jumlahPinjaman / tenorBulan else 0.0
}

class LoanApplyViewModel(
    private val customerRepository: CustomerRepository,
    private val loanRepository: LoanRepository
) : ViewModel() {

    private val gson = Gson()

    private val _uiState = MutableStateFlow(LoanApplyUiState())
    val uiState: StateFlow<LoanApplyUiState> = _uiState.asStateFlow()

    init {
        loadCustomerProfile()
        recalculateInstallment(_uiState.value.jumlahPinjaman, _uiState.value.tenorBulan)
    }

    fun initSimulationData(amount: Double?, tenor: Int?) {
        val currentAvail = _uiState.value.availablePlafond.coerceAtLeast(500_000.0)
        val validAmount = if (amount != null && amount >= 500_000.0) amount.coerceIn(500_000.0, currentAvail) else _uiState.value.jumlahPinjaman
        val validTenor = if (tenor != null && tenor > 0) tenor else _uiState.value.tenorBulan

        _uiState.value = _uiState.value.copy(
            jumlahPinjaman = validAmount,
            tenorBulan = validTenor,
            tujuanPinjaman = "",
            isFromSimulation = true,
            errorMessage = null
        )
        recalculateInstallment(validAmount, validTenor)
    }

    private fun loadCustomerProfile() {
        viewModelScope.launch {
            try {
                // 1. Ambil dari cache dulu
                val cached = customerRepository.getCachedProfileJsonSync()
                if (!cached.isNullOrBlank()) {
                    try {
                        val prof = gson.fromJson(cached, CustomerProfileDto::class.java)
                        prof?.let { applyProfileData(it) }
                    } catch (e: Exception) {
                        // ignore
                    }
                }

                // 2. Fetch API profil terbaru
                when (val res = customerRepository.getProfile()) {
                    is ApiResult.Success -> {
                        applyProfileData(res.data)
                    }
                    else -> {}
                }
            } catch (e: Exception) {
                // Keep default state
            }
        }
    }

    private fun applyProfileData(p: CustomerProfileDto) {
        val avail = p.availablePlafond ?: (p.totalPlafond ?: 50_000_000.0)
        val isSim = _uiState.value.isFromSimulation
        val amount = if (isSim) _uiState.value.jumlahPinjaman.coerceIn(500_000.0, avail.coerceAtLeast(500_000.0)) else (if (avail >= 5_000_000.0) 5_000_000.0 else avail.coerceAtLeast(500_000.0))
        val tenor = _uiState.value.tenorBulan
        val rawBunga = p.sukuBunga
        val normalizedBunga = if (rawBunga != null) (if (rawBunga < 0.05 && rawBunga > 0.0) rawBunga * 100 else rawBunga) else null
        val admin = p.biayaAdmin

        _uiState.value = _uiState.value.copy(
            availablePlafond = avail,
            totalPlafond = p.totalPlafond ?: 50_000_000.0,
            jumlahPinjaman = amount,
            namaBank = p.namaBank ?: "BCA",
            noRekening = p.noRekening ?: "",
            namaRekening = p.namaRekening ?: (p.nama ?: ""),
            sukuBungaPersen = normalizedBunga ?: _uiState.value.sukuBungaPersen,
            biayaAdmin = admin ?: _uiState.value.biayaAdmin,
            tierName = p.tierPlafond ?: _uiState.value.tierName
        )
        recalculateInstallment(amount, tenor)
    }

    // Step 1: Input handlers
    fun setJumlahPinjaman(amount: Double) {
        val clamped = amount.coerceIn(500_000.0, _uiState.value.availablePlafond.coerceAtLeast(500_000.0))
        _uiState.value = _uiState.value.copy(jumlahPinjaman = clamped, errorMessage = null)
        recalculateInstallment(clamped, _uiState.value.tenorBulan)
    }

    fun setTenorBulan(months: Int) {
        _uiState.value = _uiState.value.copy(tenorBulan = months, errorMessage = null)
        recalculateInstallment(_uiState.value.jumlahPinjaman, months)
    }

    private fun recalculateInstallment(amount: Double, tenor: Int) {
        val rate = _uiState.value.sukuBungaPersen
        val bungaBln = amount * (rate / 100.0)
        val pokokBln = if (tenor > 0) amount / tenor else 0.0
        val cicilanBln = (pokokBln + bungaBln).roundToLong()
        val totalBunga = (bungaBln * tenor).roundToLong()
        val totalBayar = (amount + totalBunga).roundToLong()

        _uiState.value = _uiState.value.copy(
            bungaBulanan = bungaBln,
            estimasiCicilanBulanan = cicilanBln,
            totalPengembalian = totalBayar
        )
    }

    fun setTujuanPinjaman(tujuan: String) {
        _uiState.value = _uiState.value.copy(tujuanPinjaman = tujuan, errorMessage = null)
    }

    fun setCustomTujuan(tujuan: String) {
        _uiState.value = _uiState.value.copy(customTujuan = tujuan, errorMessage = null)
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }

    // Step 1: Proceed to Step 2 (Local validation only - No BE request yet)
    fun submitStep1() {
        proceedToStep2()
    }

    fun proceedToStep2() {
        val s = _uiState.value
        if (s.jumlahPinjaman < 500_000.0) {
            _uiState.value = s.copy(errorMessage = "Jumlah pinjaman minimal Rp 500.000")
            return
        }
        if (s.jumlahPinjaman > s.availablePlafond) {
            _uiState.value = s.copy(errorMessage = "Jumlah pinjaman melebihi sisa plafond yang tersedia")
            return
        }
        if (s.effectiveTujuan.isBlank()) {
            _uiState.value = s.copy(errorMessage = "Silakan pilih atau isi tujuan pinjaman Anda")
            return
        }

        _uiState.value = s.copy(currentStep = 2, errorMessage = null)
    }

    // Step 2: Document handlers
    fun setSlipGaji(uri: Uri?, bitmap: Bitmap?) {
        _uiState.value = _uiState.value.copy(slipGajiUri = uri, slipGajiBitmap = bitmap, errorMessage = null)
    }

    fun setRekeningKoran(uri: Uri?, bitmap: Bitmap?) {
        _uiState.value = _uiState.value.copy(rekeningKoranUri = uri, rekeningKoranBitmap = bitmap, errorMessage = null)
    }

    fun setNpwp(uri: Uri?, bitmap: Bitmap?) {
        _uiState.value = _uiState.value.copy(npwpUri = uri, npwpBitmap = bitmap, errorMessage = null)
    }

    fun proceedToStep3Summary() {
        val s = _uiState.value
        val hasSlipGaji = s.slipGajiUri != null || s.slipGajiBitmap != null
        val hasRekKoran = s.rekeningKoranUri != null || s.rekeningKoranBitmap != null

        if (!hasSlipGaji) {
            _uiState.value = s.copy(errorMessage = "Dokumen Slip Gaji / Bukti Penghasilan wajib diunggah")
            return
        }
        if (!hasRekKoran) {
            _uiState.value = s.copy(errorMessage = "Dokumen Rekening Koran 3 Bulan Terakhir wajib diunggah")
            return
        }

        _uiState.value = s.copy(currentStep = 3, errorMessage = null)
    }

    // Step 3: T&C Agreement & Final Submit to Backend
    fun setTncAgreed(agreed: Boolean) {
        _uiState.value = _uiState.value.copy(isTncAgreed = agreed, errorMessage = null)
    }

    fun submitFinalApplication(context: Context) {
        val s = _uiState.value
        if (!s.isTncAgreed) {
            _uiState.value = s.copy(errorMessage = "Anda harus menyetujui Syarat & Ketentuan Pengajuan Pinjaman")
            return
        }

        val hasSlipGaji = s.slipGajiUri != null || s.slipGajiBitmap != null
        val hasRekKoran = s.rekeningKoranUri != null || s.rekeningKoranBitmap != null
        if (!hasSlipGaji || !hasRekKoran) {
            _uiState.value = s.copy(errorMessage = "Dokumen pendukung (Slip Gaji & Rekening Koran) wajib diunggah")
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            try {
                // 1. Simpan Data Pinjaman (Step 1) ke Backend Server
                val req = PengajuanPinjamanRequestDto(
                    jumlahPinjaman = s.jumlahPinjaman,
                    tenorBulan = s.tenorBulan,
                    tujuanPinjaman = s.effectiveTujuan
                )
                val step1Res = loanRepository.submitLoanStep1(req)
                if (step1Res !is ApiResult.Success) {
                    val errorMsg = if (step1Res is ApiResult.Error) step1Res.message else "Gagal membuat pengajuan pinjaman di server"
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = errorMsg.ifBlank { "Gagal membuat pengajuan pinjaman di server" }
                    )
                    return@launch
                }

                val step1Data = step1Res.data
                val pengajuanId = step1Data.pengajuanId ?: step1Data.data?.id
                val nomorPengajuan = step1Data.nomorPengajuan ?: step1Data.data?.nomorPengajuan

                if (pengajuanId.isNullOrBlank()) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = "Gagal memperoleh ID Pengajuan dari server"
                    )
                    return@launch
                }

                // 2. Prepare Multipart Parts (Supports PDF and Images)
                val slipPart = createDocumentPart(context, "slipGaji", "slip_gaji", pengajuanId, s.slipGajiUri, s.slipGajiBitmap)
                val rekKoranPart = createDocumentPart(context, "rekeningKoran", "rekening_koran", pengajuanId, s.rekeningKoranUri, s.rekeningKoranBitmap)
                val npwpPart = createDocumentPart(context, "npwp", "npwp", pengajuanId, s.npwpUri, s.npwpBitmap)

                // 3. Unggah Dokumen Pendukung (Step 2) ke Backend Server
                val step2Res = loanRepository.submitLoanStep2(
                    pengajuanId = pengajuanId,
                    slipGaji = slipPart,
                    rekeningKoran = rekKoranPart,
                    npwp = npwpPart
                )

                when (step2Res) {
                    is ApiResult.Success -> {
                        val step2Data = step2Res.data
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            currentStep = 4, // Success Receipt
                            pengajuanId = pengajuanId,
                            nomorPengajuan = step2Data.nomorPengajuan ?: (step2Data.data?.nomorPengajuan ?: nomorPengajuan),
                            submittedLoanResult = step2Data.data,
                            successMessage = "Pengajuan pinjaman berhasil dikirim!"
                        )
                    }
                    is ApiResult.Error -> {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            errorMessage = step2Res.message.ifBlank { "Gagal mengunggah dokumen pengajuan" }
                        )
                    }
                    else -> {
                        _uiState.value = _uiState.value.copy(isLoading = false)
                    }
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Gagal memproses pengajuan pinjaman: ${e.localizedMessage ?: "Terjadi kesalahan"}"
                )
            }
        }
    }

    private fun createDocumentPart(
        context: Context,
        partName: String,
        prefix: String,
        pengajuanId: String,
        uri: Uri?,
        bitmap: Bitmap?
    ): MultipartBody.Part? {
        return when {
            uri != null -> {
                val mimeType = context.contentResolver.getType(uri) ?: "application/octet-stream"
                val isPdf = mimeType.contains("pdf", ignoreCase = true) || uri.toString().contains(".pdf", ignoreCase = true)
                val extension = if (isPdf) ".pdf" else ".jpg"
                val bytes = if (isPdf) {
                    try {
                        context.contentResolver.openInputStream(uri)?.use { it.readBytes() }
                    } catch (e: Exception) {
                        null
                    }
                } else {
                    ImageCompressorHelper.compressImageUri(context, uri, maxDimension = 1920, quality = 82)
                }

                bytes?.let {
                    val reqBody = it.toRequestBody(mimeType.toMediaTypeOrNull())
                    MultipartBody.Part.createFormData(partName, "${prefix}_${pengajuanId}$extension", reqBody)
                }
            }
            bitmap != null -> {
                val bytes = ImageCompressorHelper.compressBitmap(bitmap, maxDimension = 1920, quality = 82)
                val reqBody = bytes.toRequestBody("image/jpeg".toMediaTypeOrNull())
                MultipartBody.Part.createFormData(partName, "${prefix}_${pengajuanId}.jpg", reqBody)
            }
            else -> null
        }
    }

    fun goToPreviousStep() {
        val s = _uiState.value
        if (s.currentStep > 1 && s.currentStep < 4) {
            _uiState.value = s.copy(currentStep = s.currentStep - 1, errorMessage = null)
        }
    }
}