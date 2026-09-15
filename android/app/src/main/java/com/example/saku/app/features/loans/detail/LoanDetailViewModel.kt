package com.example.saku.app.features.loans.detail

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.saku.app.core.data.repository.LoanRepository
import com.example.saku.app.core.data.repository.LoanRepositoryImpl
import com.example.saku.app.core.network.ApiClient
import com.example.saku.app.core.network.ApiResult
import com.example.saku.app.core.network.dto.AngsuranItemDto
import com.example.saku.app.core.network.dto.LoanApplicationItemDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class LoanDetailUiState(
    val loan: LoanApplicationItemDto? = null,
    val angsuranList: List<AngsuranItemDto> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val showPaymentSheet: Boolean = false,
    val selectedAngsuranForPayment: AngsuranItemDto? = null
)

class LoanDetailViewModel @JvmOverloads constructor(
    application: Application,
    private val loanRepository: LoanRepository = LoanRepositoryImpl(
        ApiClient.getCustomerApiService(application)
    )
) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(LoanDetailUiState())
    val uiState: StateFlow<LoanDetailUiState> = _uiState.asStateFlow()

    fun fetchLoanDetail(loanId: String) {
        if (loanId.isBlank()) return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            try {
                // 1. Fetch Loan by ID
                val loanRes = loanRepository.getLoanById(loanId)
                val loanData = if (loanRes is ApiResult.Success) loanRes.data else null

                // 2. Fetch Jadwal Angsuran
                val angsuranRes = loanRepository.getJadwalAngsuran(loanId)
                val angsuranData = if (angsuranRes is ApiResult.Success) angsuranRes.data else emptyList()

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    loan = loanData,
                    angsuranList = angsuranData
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Gagal memuat detail pinjaman: ${e.localizedMessage ?: "Terjadi kesalahan"}"
                )
            }
        }
    }

    fun openPaymentSheet(angsuran: AngsuranItemDto? = null) {
        _uiState.value = _uiState.value.copy(
            showPaymentSheet = true,
            selectedAngsuranForPayment = angsuran ?: _uiState.value.angsuranList.firstOrNull { it.statusBayar != "LUNAS" }
        )
    }

    fun closePaymentSheet() {
        _uiState.value = _uiState.value.copy(
            showPaymentSheet = false,
            selectedAngsuranForPayment = null
        )
    }
}
