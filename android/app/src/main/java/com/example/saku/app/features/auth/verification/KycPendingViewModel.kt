package com.example.saku.app.features.auth.verification

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.saku.app.core.data.TokenManager
import com.example.saku.app.core.data.repository.AuthRepository
import com.example.saku.app.core.data.repository.CustomerRepository
import com.example.saku.app.core.data.repository.NotificationRepository
import com.example.saku.app.core.network.ApiResult
import com.example.saku.app.core.network.dto.CustomerProfileDto
import com.example.saku.app.core.util.ImageCompressorHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody

sealed interface KycStatusState {
    object Idle : KycStatusState
    object Checking : KycStatusState
    object StillPending : KycStatusState
    object RevisionRequired : KycStatusState
    object Verified : KycStatusState
    data class Error(val message: String) : KycStatusState
}

class KycPendingViewModel(
    private val customerRepository: CustomerRepository,
    private val authRepository: AuthRepository,
    private val tokenManager: TokenManager,
    private val notificationRepository: NotificationRepository,
    application: Application
) : AndroidViewModel(application) {

    private val _statusState = MutableStateFlow<KycStatusState>(KycStatusState.Idle)
    val statusState: StateFlow<KycStatusState> = _statusState.asStateFlow()

    private val _profile = MutableStateFlow<CustomerProfileDto?>(null)
    val profile: StateFlow<CustomerProfileDto?> = _profile.asStateFlow()

    private val _unreadNotifikasiCount = MutableStateFlow(0L)
    val unreadNotifikasiCount: StateFlow<Long> = _unreadNotifikasiCount.asStateFlow()

    // Revision Upload State
    var ktpBitmap = MutableStateFlow<Bitmap?>(null)
        private set
    var ktpUri = MutableStateFlow<Uri?>(null)
        private set
    var selfieBitmap = MutableStateFlow<Bitmap?>(null)
        private set
    var selfieUri = MutableStateFlow<Uri?>(null)
        private set

    var isSubmittingRevision = MutableStateFlow(false)
        private set

    init {
        checkVerificationStatus(silent = true)
        fetchUnreadNotificationCount()
    }

    fun fetchUnreadNotificationCount() {
        viewModelScope.launch {
            try {
                when (val res = notificationRepository.getUnreadCount()) {
                    is ApiResult.Success -> {
                        _unreadNotifikasiCount.value = res.data.unreadCount
                    }
                    else -> {}
                }
            } catch (e: Exception) {
                // Ignore error
            }
        }
    }

    fun setKtpPhoto(uri: Uri?, bitmap: Bitmap?) {
        ktpUri.value = uri
        ktpBitmap.value = bitmap
    }

    fun setSelfiePhoto(uri: Uri?, bitmap: Bitmap?) {
        selfieUri.value = uri
        selfieBitmap.value = bitmap
    }

    fun checkVerificationStatus(silent: Boolean = false, onSuccess: (() -> Unit)? = null) {
        viewModelScope.launch {
            if (!silent) {
                _statusState.value = KycStatusState.Checking
            }
            fetchUnreadNotificationCount()

            val result = customerRepository.getProfile()
            if (result is ApiResult.Success) {
                val data = result.data
                _profile.value = data
                val isVerified = data.isKycVerified == true || data.status == true

                if (isVerified) {
                    tokenManager.updateProfileData(
                        nama = data.nama,
                        email = data.email,
                        noHp = data.noHp,
                        isKycVerified = true
                    )
                    _statusState.value = KycStatusState.Verified
                    onSuccess?.invoke()
                } else {
                    val status = (data.statusVerifikasi ?: "").uppercase()
                    if (status.contains("REVISI") || status.contains("REVISION") || status.contains("REJECTED") || status.contains("DITOLAK")) {
                        _statusState.value = KycStatusState.RevisionRequired
                    } else {
                        if (!silent) {
                            _statusState.value = KycStatusState.StillPending
                        } else {
                            _statusState.value = KycStatusState.Idle
                        }
                    }
                }
            } else if (result is ApiResult.Error) {
                if (!silent) {
                    _statusState.value = KycStatusState.Error(result.message)
                }
            }
        }
    }

    fun submitRevision(context: Context, onSuccess: () -> Unit) {
        val hasKtp = ktpUri.value != null || ktpBitmap.value != null
        val hasSelfie = selfieUri.value != null || selfieBitmap.value != null

        if (!hasKtp && !hasSelfie) {
            _statusState.value = KycStatusState.Error("Silakan pilih minimal satu dokumen KTP atau Selfie untuk diunggah ulang.")
            return
        }

        viewModelScope.launch {
            isSubmittingRevision.value = true

            val ktpBytes = when {
                ktpUri.value != null -> ImageCompressorHelper.compressImageUri(context, ktpUri.value!!, maxDimension = 1920, quality = 82)
                ktpBitmap.value != null -> ImageCompressorHelper.compressBitmap(ktpBitmap.value!!, maxDimension = 1920, quality = 82)
                else -> null
            }

            val ktpPart = ktpBytes?.let {
                val reqBody = it.toRequestBody("image/jpeg".toMediaTypeOrNull())
                MultipartBody.Part.createFormData("ktp", "ktp_revisi_${System.currentTimeMillis()}.jpg", reqBody)
            }

            val selfieBytes = when {
                selfieUri.value != null -> ImageCompressorHelper.compressImageUri(context, selfieUri.value!!, maxDimension = 1440, quality = 80)
                selfieBitmap.value != null -> ImageCompressorHelper.compressBitmap(selfieBitmap.value!!, maxDimension = 1440, quality = 80)
                else -> null
            }

            val selfiePart = selfieBytes?.let {
                val reqBody = it.toRequestBody("image/jpeg".toMediaTypeOrNull())
                MultipartBody.Part.createFormData("selfie", "selfie_revisi_${System.currentTimeMillis()}.jpg", reqBody)
            }

            val result = customerRepository.updateKycDocuments(ktpPart, selfiePart)
            isSubmittingRevision.value = false

            if (result is ApiResult.Success) {
                _profile.value = result.data
                ktpBitmap.value = null
                ktpUri.value = null
                selfieBitmap.value = null
                selfieUri.value = null
                _statusState.value = KycStatusState.StillPending
                onSuccess()
            } else if (result is ApiResult.Error) {
                _statusState.value = KycStatusState.Error(result.message)
            }
        }
    }

    fun logout(onSuccess: () -> Unit) {
        viewModelScope.launch {
            authRepository.logout()
            onSuccess()
        }
    }

    fun resetState() {
        _statusState.value = KycStatusState.Idle
    }
}