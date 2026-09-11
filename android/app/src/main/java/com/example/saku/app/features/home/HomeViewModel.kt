package com.example.saku.app.features.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.saku.app.core.data.TokenManager
import com.example.saku.app.core.data.UserSession
import com.example.saku.app.core.network.ApiClient
import com.example.saku.app.core.network.dto.CustomerProfileDto
import com.example.saku.app.core.network.dto.LoanApplicationItemDto
import com.google.gson.Gson
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.math.roundToLong

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val tokenManager = TokenManager.getInstance(application)
    private val customerApi = ApiClient.getCustomerApiService(application)
    private val gson = Gson()

    val userSession: StateFlow<UserSession?> = tokenManager.userSessionFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = null
        )

    private val _customerProfile = MutableStateFlow<CustomerProfileDto?>(null)
    val customerProfile: StateFlow<CustomerProfileDto?> = _customerProfile.asStateFlow()

    private val _myLoans = MutableStateFlow<List<LoanApplicationItemDto>>(emptyList())
    val myLoans: StateFlow<List<LoanApplicationItemDto>> = _myLoans.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    // Balance visibility toggle (eye icon)
    private val _isBalanceVisible = MutableStateFlow(true)
    val isBalanceVisible: StateFlow<Boolean> = _isBalanceVisible.asStateFlow()

    // Simulation modal state
    private val _showSimulationDialog = MutableStateFlow(false)
    val showSimulationDialog: StateFlow<Boolean> = _showSimulationDialog.asStateFlow()

    private val _simAmount = MutableStateFlow(10_000_000.0)
    val simAmount: StateFlow<Double> = _simAmount.asStateFlow()

    private val _simTenorMonths = MutableStateFlow(6)
    val simTenorMonths: StateFlow<Int> = _simTenorMonths.asStateFlow()

    // Logout dialog state
    private val _showLogoutDialog = MutableStateFlow(false)
    val showLogoutDialog: StateFlow<Boolean> = _showLogoutDialog.asStateFlow()

    // Notification dialog state
    private val _showNotificationDialog = MutableStateFlow(false)
    val showNotificationDialog: StateFlow<Boolean> = _showNotificationDialog.asStateFlow()

    // Bottom navigation active route
    private val _currentNavRoute = MutableStateFlow("home")
    val currentNavRoute: StateFlow<String> = _currentNavRoute.asStateFlow()

    // History filter state
    private val _selectedHistoryFilter = MutableStateFlow("SEMUA")
    val selectedHistoryFilter: StateFlow<String> = _selectedHistoryFilter.asStateFlow()

    fun setHistoryFilter(filter: String) {
        _selectedHistoryFilter.value = filter
    }

    init {
        // 1. Muat profil tersimpan (cache) segera agar data instan muncul tanpa flicker
        viewModelScope.launch {
            val cachedJson = tokenManager.getCachedProfileJsonSync()
            if (!cachedJson.isNullOrBlank() && _customerProfile.value == null) {
                try {
                    val cached = gson.fromJson(cachedJson, CustomerProfileDto::class.java)
                    _customerProfile.value = cached
                } catch (e: Exception) {
                    // Ignore parse error
                }
            }
        }

        // 2. Tarik data terbaru dari backend
        fetchDashboardData()
    }

    fun fetchDashboardData() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // Fetch profile
                android.util.Log.d("HomeViewModel", "Calling customerApi.getProfile()...")
                val profileResponse = customerApi.getProfile()
                if (profileResponse.isSuccessful && profileResponse.body()?.data != null) {
                    val profile = profileResponse.body()!!.data
                    _customerProfile.value = profile
                    android.util.Log.d("HomeViewModel", "Profile loaded: ${profile?.nama}, Plafond: ${profile?.availablePlafond}")

                    // Simpan cache profil dan sinkronkan data profil ke TokenManager
                    profile?.let {
                        tokenManager.saveCachedProfileJson(gson.toJson(it))
                        tokenManager.updateProfileData(
                            nama = it.nama,
                            email = it.email,
                            noHp = it.noHp,
                            isKycVerified = it.isKycVerified
                        )
                    }
                } else {
                    android.util.Log.w("HomeViewModel", "Profile API response error: code=${profileResponse.code()}")
                }

                // Fetch loans
                android.util.Log.d("HomeViewModel", "Calling customerApi.getMyLoans()...")
                val loansResponse = customerApi.getMyLoans()
                if (loansResponse.isSuccessful && loansResponse.body()?.data != null) {
                    val loans = loansResponse.body()!!.data ?: emptyList()
                    _myLoans.value = loans
                    android.util.Log.d("HomeViewModel", "Loans loaded: ${loans.size} active loans")
                } else {
                    android.util.Log.w("HomeViewModel", "MyLoans API response error: code=${loansResponse.code()}")
                }
            } catch (e: Exception) {
                android.util.Log.e("HomeViewModel", "Error fetching dashboard data: ${e.message}", e)
            } finally {
                _isLoading.value = false
                _isRefreshing.value = false
            }
        }
    }

    fun refreshDashboard() {
        _isRefreshing.value = true
        fetchDashboardData()
    }

    fun toggleBalanceVisibility() {
        _isBalanceVisible.value = !_isBalanceVisible.value
    }

    fun setSimulationDialogVisible(visible: Boolean) {
        _showSimulationDialog.value = visible
    }

    fun updateSimAmount(amount: Double) {
        _simAmount.value = amount
    }

    fun updateSimTenor(months: Int) {
        _simTenorMonths.value = months
    }

    /**
     * Menghitung estimasi angsuran bulanan (Pokok + Bunga flat 0.99% per bulan)
     */
    fun calculateMonthlyInstallment(amount: Double, tenorMonths: Int): Long {
        if (tenorMonths <= 0) return 0L
        val principalPerMonth = amount / tenorMonths
        val interestPerMonth = amount * 0.0099
        return (principalPerMonth + interestPerMonth).roundToLong()
    }

    fun setLogoutDialogVisible(visible: Boolean) {
        _showLogoutDialog.value = visible
    }

    fun setNotificationDialogVisible(visible: Boolean) {
        _showNotificationDialog.value = visible
    }

    fun setNavRoute(route: String) {
        _currentNavRoute.value = route
    }

    fun logout(onLoggedOut: () -> Unit) {
        viewModelScope.launch {
            _showLogoutDialog.value = false
            tokenManager.clearSession()
            onLoggedOut()
        }
    }
}
