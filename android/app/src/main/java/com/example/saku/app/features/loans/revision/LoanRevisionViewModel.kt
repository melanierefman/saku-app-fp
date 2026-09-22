package com.example.saku.app.features.loans.revision

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.saku.app.core.data.repository.LoanRepository
import com.example.saku.app.core.network.ApiResult
import com.example.saku.app.core.network.dto.LoanApplicationItemDto
import com.example.saku.app.core.util.ImageCompressorHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody

data class LoanRevisionUiState(
    val loan: LoanApplicationItemDto? = null,
    val isLoading: Boolean = false,
    val isSubmitting: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null,

    // Document targeting based on Marketing catatanReview
    val needsSlipGaji: Boolean = true,
    val needsRekeningKoran: Boolean = true,
    val needsNpwp: Boolean = false,

    // File states
    val slipGajiUri: Uri? = null,
    val slipGajiBitmap: Bitmap? = null,
    val rekeningKoranUri: Uri? = null,
    val rekeningKoranBitmap: Bitmap? = null,
    val npwpUri: Uri? = null,
    val npwpBitmap: Bitmap? = null
) {
    val canSubmit: Boolean
        get() = (slipGajiUri != null || slipGajiBitmap != null) ||
                (rekeningKoranUri != null || rekeningKoranBitmap != null) ||
                (npwpUri != null || npwpBitmap != null)
}

class LoanRevisionViewModel(
    private val loanRepository: LoanRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoanRevisionUiState())
    val uiState: StateFlow<LoanRevisionUiState> = _uiState.asStateFlow()

    fun loadLoan(loanId: String) {
        if (loanId.isBlank()) return
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            when (val res = loanRepository.getLoanById(loanId)) {
                is ApiResult.Success -> {
                    val loan = res.data
                    val note = loan.catatanReview.orEmpty().lowercase()
                    val mentionsSlip = note.contains("slip") || note.contains("gaji") || note.contains("penghasilan")
                    val mentionsRekKoran = note.contains("rekening") || note.contains("koran") || note.contains("mutasi")
                    val mentionsNpwp = note.contains("npwp") || note.contains("pajak")

                    val (targetSlip, targetRek, targetNpwp) = when {
                        mentionsSlip && !mentionsRekKoran && !mentionsNpwp -> Triple(true, false, false)
                        mentionsRekKoran && !mentionsSlip && !mentionsNpwp -> Triple(false, true, false)
                        mentionsNpwp && !mentionsSlip && !mentionsRekKoran -> Triple(false, false, true)
                        mentionsSlip && mentionsRekKoran && !mentionsNpwp -> Triple(true, true, false)
                        mentionsSlip && mentionsNpwp && !mentionsRekKoran -> Triple(true, false, true)
                        mentionsRekKoran && mentionsNpwp && !mentionsSlip -> Triple(false, true, true)
                        else -> Triple(true, true, false)
                    }

                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        loan = loan,
                        needsSlipGaji = targetSlip,
                        needsRekeningKoran = targetRek,
                        needsNpwp = targetNpwp
                    )
                }
                is ApiResult.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = res.message.ifBlank { "Gagal memuat data pengajuan pinjaman" }
                    )
                }
                else -> {
                    _uiState.value = _uiState.value.copy(isLoading = false)
                }
            }
        }
    }

    fun setSlipGaji(uri: Uri?, bitmap: Bitmap?) {
        _uiState.value = _uiState.value.copy(
            slipGajiUri = uri,
            slipGajiBitmap = bitmap,
            errorMessage = null
        )
    }

    fun setRekeningKoran(uri: Uri?, bitmap: Bitmap?) {
        _uiState.value = _uiState.value.copy(
            rekeningKoranUri = uri,
            rekeningKoranBitmap = bitmap,
            errorMessage = null
        )
    }

    fun setNpwp(uri: Uri?, bitmap: Bitmap?) {
        _uiState.value = _uiState.value.copy(
            npwpUri = uri,
            npwpBitmap = bitmap,
            errorMessage = null
        )
    }

    fun submitRevision(context: Context) {
        val s = _uiState.value
        val loan = s.loan
        val loanId = loan?.id
        if (loanId.isNullOrBlank()) {
            _uiState.value = s.copy(errorMessage = "Data pengajuan tidak valid")
            return
        }

        if (!s.canSubmit) {
            _uiState.value = s.copy(errorMessage = "Pilih minimal satu berkas revisi untuk diunggah")
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSubmitting = true, errorMessage = null)
            try {
                val slipPart = createDocumentPart(context, "slipGaji", "slip_gaji", loanId, s.slipGajiUri, s.slipGajiBitmap)
                val rekKoranPart = createDocumentPart(context, "rekeningKoran", "rekening_koran", loanId, s.rekeningKoranUri, s.rekeningKoranBitmap)
                val npwpPart = createDocumentPart(context, "npwp", "npwp", loanId, s.npwpUri, s.npwpBitmap)

                when (val res = loanRepository.submitLoanStep2(loanId, slipPart, rekKoranPart, npwpPart)) {
                    is ApiResult.Success -> {
                        _uiState.value = _uiState.value.copy(
                            isSubmitting = false,
                            isSuccess = true,
                            successMessage = res.data.message ?: "Dokumen revisi berhasil diunggah ulang!"
                        )
                    }
                    is ApiResult.Error -> {
                        _uiState.value = _uiState.value.copy(
                            isSubmitting = false,
                            errorMessage = res.message.ifBlank { "Gagal mengirim dokumen revisi" }
                        )
                    }
                    else -> {
                        _uiState.value = _uiState.value.copy(isSubmitting = false)
                    }
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isSubmitting = false,
                    errorMessage = "Terjadi kesalahan: ${e.localizedMessage ?: "Gagal memproses berkas"}"
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
                    MultipartBody.Part.createFormData(partName, "${prefix}_revisi_${pengajuanId}$extension", reqBody)
                }
            }
            bitmap != null -> {
                val bytes = ImageCompressorHelper.compressBitmap(bitmap, maxDimension = 1920, quality = 82)
                val reqBody = bytes.toRequestBody("image/jpeg".toMediaTypeOrNull())
                MultipartBody.Part.createFormData(partName, "${prefix}_revisi_${pengajuanId}.jpg", reqBody)
            }
            else -> null
        }
    }
}