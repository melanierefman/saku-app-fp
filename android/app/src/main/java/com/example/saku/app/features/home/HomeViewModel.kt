package com.example.saku.app.features.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.saku.app.core.data.UserSession
import com.example.saku.app.core.data.repository.AuthRepository
import com.example.saku.app.core.data.repository.CustomerRepository
import com.example.saku.app.core.data.repository.LoanRepository
import com.example.saku.app.core.data.repository.NotificationRepository
import com.example.saku.app.core.network.ApiResult
import com.example.saku.app.core.network.dto.ChangePasswordRequestDto
import com.example.saku.app.core.network.dto.CustomerProfileDto
import com.example.saku.app.core.network.dto.LoanApplicationItemDto
import com.example.saku.app.core.network.dto.NotifikasiItemDto
import com.example.saku.app.core.network.dto.PublicPlafondDto
import com.example.saku.app.core.network.dto.SimulasiPinjamanResponseDto
import com.example.saku.app.core.network.dto.UpdateDomisiliRequestDto
import com.example.saku.app.core.network.dto.UpdatePekerjaanRequestDto
import com.example.saku.app.core.network.dto.UpdateRekeningRequestDto
import com.google.gson.Gson
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.math.roundToLong

class HomeViewModel(
    private val customerRepository: CustomerRepository,
    private val loanRepository: LoanRepository,
    private val notificationRepository: NotificationRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val gson = Gson()

    val userSession: StateFlow<UserSession?> = authRepository.userSession
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

    // Simulation modal state & Server Calculation
    private val _showSimulationDialog = MutableStateFlow(false)
    val showSimulationDialog: StateFlow<Boolean> = _showSimulationDialog.asStateFlow()

    private val _simAmount = MutableStateFlow(10_000_000.0)
    val simAmount: StateFlow<Double> = _simAmount.asStateFlow()

    private val _simTenorMonths = MutableStateFlow(6)
    val simTenorMonths: StateFlow<Int> = _simTenorMonths.asStateFlow()

    private val _simulasiResult = MutableStateFlow<SimulasiPinjamanResponseDto?>(null)
    val simulasiResult: StateFlow<SimulasiPinjamanResponseDto?> = _simulasiResult.asStateFlow()

    private val _publicPlafonds = MutableStateFlow<List<PublicPlafondDto>>(emptyList())
    val publicPlafonds: StateFlow<List<PublicPlafondDto>> = _publicPlafonds.asStateFlow()

    // Logout dialog state
    private val _showLogoutDialog = MutableStateFlow(false)
    val showLogoutDialog: StateFlow<Boolean> = _showLogoutDialog.asStateFlow()

    // Notification states
    private val _showNotificationDialog = MutableStateFlow(false)
    val showNotificationDialog: StateFlow<Boolean> = _showNotificationDialog.asStateFlow()

    private val _unreadNotifikasiCount = MutableStateFlow(0L)
    val unreadNotifikasiCount: StateFlow<Long> = _unreadNotifikasiCount.asStateFlow()

    private val _notifikasiList = MutableStateFlow<List<NotifikasiItemDto>>(emptyList())
    val notifikasiList: StateFlow<List<NotifikasiItemDto>> = _notifikasiList.asStateFlow()

    private val _isNotifikasiLoading = MutableStateFlow(false)
    val isNotifikasiLoading: StateFlow<Boolean> = _isNotifikasiLoading.asStateFlow()

    // Bottom navigation active route
    private val _currentNavRoute = MutableStateFlow("home")
    val currentNavRoute: StateFlow<String> = _currentNavRoute.asStateFlow()

    // History filter state
    private val _selectedHistoryFilter = MutableStateFlow("SEMUA")
    val selectedHistoryFilter: StateFlow<String> = _selectedHistoryFilter.asStateFlow()

    // Profile update states
    private val _isProfileUpdating = MutableStateFlow(false)
    val isProfileUpdating: StateFlow<Boolean> = _isProfileUpdating.asStateFlow()

    fun setHistoryFilter(filter: String) {
        _selectedHistoryFilter.value = filter
    }

    init {
        // 1. Muat profil tersimpan (cache) segera agar data instan muncul tanpa flicker
        viewModelScope.launch {
            val cachedJson = customerRepository.getCachedProfileJsonSync()
            if (!cachedJson.isNullOrBlank() && _customerProfile.value == null) {
                try {
                    val cached = gson.fromJson(cachedJson, CustomerProfileDto::class.java)
                    _customerProfile.value = cached
                    val maxP = (cached?.availablePlafond ?: cached?.totalPlafond ?: 50_000_000.0).coerceAtLeast(500_000.0)
                    if (_simAmount.value > maxP) {
                        _simAmount.value = maxP
                    }
                } catch (e: Exception) {
                    // Ignore parse error
                }
            }
        }

        // 2. Tarik data terbaru dari backend
        fetchDashboardData()
        fetchUnreadNotificationCount()
        fetchPublicPlafonds()
    }

    fun fetchDashboardData() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // Fetch profile
                when (val profileResult = customerRepository.getProfile()) {
                    is ApiResult.Success -> {
                        val profile = profileResult.data
                        _customerProfile.value = profile
                        val maxP = (profile.availablePlafond ?: profile.totalPlafond ?: 50_000_000.0).coerceAtLeast(500_000.0)
                        if (_simAmount.value > maxP) {
                            _simAmount.value = maxP
                        }

                        // Simpan cache profil dan sinkronkan data profil
                        customerRepository.saveCachedProfileJson(gson.toJson(profile))
                        customerRepository.updateProfileData(
                            nama = profile.nama,
                            email = profile.email,
                            noHp = profile.noHp,
                            isKycVerified = profile.isKycVerified
                        )
                    }
                    else -> {}
                }

                // Fetch loans
                when (val loansResult = loanRepository.getMyLoans()) {
                    is ApiResult.Success -> {
                        _myLoans.value = loansResult.data
                    }
                    else -> {}
                }

                // Fetch unread notifications count
                fetchUnreadNotificationCount()
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

    // Notification Management
    fun setNotificationDialogVisible(visible: Boolean) {
        _showNotificationDialog.value = visible
        if (visible) {
            fetchNotifications()
        }
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

    fun fetchNotifications() {
        viewModelScope.launch {
            _isNotifikasiLoading.value = true
            try {
                when (val res = notificationRepository.getNotifications()) {
                    is ApiResult.Success -> {
                        _notifikasiList.value = res.data
                        fetchUnreadNotificationCount()
                    }
                    else -> {}
                }
            } catch (e: Exception) {
                android.util.Log.e("HomeViewModel", "Error fetching notifications: ${e.message}")
            } finally {
                _isNotifikasiLoading.value = false
            }
        }
    }

    fun markNotificationAsRead(id: String) {
        viewModelScope.launch {
            try {
                when (notificationRepository.markNotificationRead(id)) {
                    is ApiResult.Success -> {
                        _notifikasiList.value = _notifikasiList.value.map {
                            if (it.id == id) it.copy(isRead = true, status = "SUDAH_DIBACA") else it
                        }
                        _unreadNotifikasiCount.value = (_unreadNotifikasiCount.value - 1).coerceAtLeast(0L)
                    }
                    else -> {}
                }
            } catch (e: Exception) {
                android.util.Log.e("HomeViewModel", "Error marking notification read: ${e.message}")
            }
        }
    }

    fun markAllNotificationsAsRead() {
        viewModelScope.launch {
            try {
                when (notificationRepository.markAllNotificationsRead()) {
                    is ApiResult.Success -> {
                        _notifikasiList.value = _notifikasiList.value.map { it.copy(isRead = true, status = "SUDAH_DIBACA") }
                        _unreadNotifikasiCount.value = 0L
                    }
                    else -> {}
                }
            } catch (e: Exception) {
                android.util.Log.e("HomeViewModel", "Error marking all read: ${e.message}")
            }
        }
    }

    // Loan Simulation with Backend API
    fun setSimulationDialogVisible(visible: Boolean) {
        _showSimulationDialog.value = visible
        if (visible) {
            val maxP = (_customerProfile.value?.availablePlafond ?: _customerProfile.value?.totalPlafond ?: 50_000_000.0).coerceAtLeast(500_000.0)
            val clamped = _simAmount.value.coerceIn(500_000.0, maxP)
            _simAmount.value = clamped
            fetchSimulasiCalculation(clamped, _simTenorMonths.value)
        }
    }

    fun updateSimAmount(amount: Double) {
        val maxP = (_customerProfile.value?.availablePlafond ?: _customerProfile.value?.totalPlafond ?: 50_000_000.0).coerceAtLeast(500_000.0)
        val clamped = amount.coerceIn(500_000.0, maxP)
        _simAmount.value = clamped
        fetchSimulasiCalculation(clamped, _simTenorMonths.value)
    }

    fun updateSimTenor(months: Int) {
        _simTenorMonths.value = months
        fetchSimulasiCalculation(_simAmount.value, months)
    }

    private fun fetchSimulasiCalculation(amount: Double, tenor: Int) {
        viewModelScope.launch {
            try {
                when (val res = customerRepository.hitungSimulasi(amount, tenor)) {
                    is ApiResult.Success -> {
                        _simulasiResult.value = res.data
                    }
                    else -> {}
                }
            } catch (e: Exception) {
                // fallback local calculation if offline
            }
        }
    }

    private fun fetchPublicPlafonds() {
        viewModelScope.launch {
            try {
                when (val res = customerRepository.getPublicPlafonds()) {
                    is ApiResult.Success -> {
                        _publicPlafonds.value = res.data
                    }
                    else -> {}
                }
            } catch (e: Exception) {
                // ignore
            }
        }
    }

    fun calculateMonthlyInstallment(amount: Double, tenorMonths: Int): Long {
        if (tenorMonths <= 0) return 0L
        val profileRate = _customerProfile.value?.sukuBunga
        val rawRate = if (profileRate != null && profileRate > 0) profileRate else (_simulasiResult.value?.sukuBungaPersen ?: 5.0)
        val ratePct = if (rawRate <= 1.0 && rawRate > 0.0) rawRate * 100 else rawRate
        val principalPerMonth = amount / tenorMonths
        val interestPerMonth = amount * (ratePct / 100.0)
        return (principalPerMonth + interestPerMonth).roundToLong()
    }

    // Profile Management & Updates
    fun updateRekening(
        namaBank: String,
        noRekening: String,
        namaRekening: String,
        onSuccess: (String) -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            _isProfileUpdating.value = true
            try {
                val req = UpdateRekeningRequestDto(
                    namaBank = namaBank.trim(),
                    noRekening = noRekening.trim(),
                    namaRekening = namaRekening.trim()
                )
                when (val res = customerRepository.updateRekening(req)) {
                    is ApiResult.Success -> {
                        val updated = res.data
                        _customerProfile.value = updated
                        customerRepository.saveCachedProfileJson(gson.toJson(updated))
                        onSuccess("Rekening bank berhasil diperbarui")
                    }
                    is ApiResult.Error -> {
                        onError(res.message)
                    }
                    else -> {}
                }
            } catch (e: Exception) {
                onError(e.localizedMessage ?: "Gagal memperbarui rekening")
            } finally {
                _isProfileUpdating.value = false
            }
        }
    }

    fun updateDomisili(
        alamat: String,
        rt: String?,
        rw: String?,
        kelurahan: String?,
        kecamatan: String?,
        kota: String?,
        provinsi: String?,
        kodePos: String?,
        onSuccess: (String) -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            _isProfileUpdating.value = true
            try {
                val req = UpdateDomisiliRequestDto(
                    alamatLengkap = alamat.trim(),
                    rt = rt?.trim(),
                    rw = rw?.trim(),
                    kelurahan = kelurahan?.trim(),
                    kecamatan = kecamatan?.trim(),
                    kotaKabupaten = kota?.trim(),
                    provinsi = provinsi?.trim(),
                    kodePos = kodePos?.trim()
                )
                when (val res = customerRepository.updateDomisili(req)) {
                    is ApiResult.Success -> {
                        val updated = res.data
                        _customerProfile.value = updated
                        customerRepository.saveCachedProfileJson(gson.toJson(updated))
                        onSuccess("Alamat domisili berhasil diperbarui")
                    }
                    is ApiResult.Error -> {
                        onError(res.message)
                    }
                    else -> {}
                }
            } catch (e: Exception) {
                onError(e.localizedMessage ?: "Gagal memperbarui alamat domisili")
            } finally {
                _isProfileUpdating.value = false
            }
        }
    }

    fun updatePekerjaan(
        pekerjaan: String,
        tempatKerja: String?,
        statusPekerjaan: String?,
        penghasilanBulanan: Double?,
        onSuccess: (String) -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            _isProfileUpdating.value = true
            try {
                val req = UpdatePekerjaanRequestDto(
                    pekerjaan = pekerjaan.trim(),
                    tempatKerja = tempatKerja?.trim(),
                    statusPekerjaan = statusPekerjaan?.trim(),
                    penghasilanBulanan = penghasilanBulanan
                )
                when (val res = customerRepository.updatePekerjaan(req)) {
                    is ApiResult.Success -> {
                        val updated = res.data
                        _customerProfile.value = updated
                        customerRepository.saveCachedProfileJson(gson.toJson(updated))
                        onSuccess("Data pekerjaan berhasil diperbarui")
                    }
                    is ApiResult.Error -> {
                        onError(res.message)
                    }
                    else -> {}
                }
            } catch (e: Exception) {
                onError(e.localizedMessage ?: "Gagal memperbarui data pekerjaan")
            } finally {
                _isProfileUpdating.value = false
            }
        }
    }

    fun changePassword(
        oldPass: String,
        newPass: String,
        confirmPass: String,
        onSuccess: (String) -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            _isProfileUpdating.value = true
            try {
                val req = ChangePasswordRequestDto(
                    oldPassword = oldPass,
                    newPassword = newPass,
                    confirmPassword = confirmPass
                )
                when (val res = customerRepository.changePassword(req)) {
                    is ApiResult.Success -> {
                        val msg = res.message ?: "Kata sandi berhasil diubah"
                        onSuccess(msg)
                    }
                    is ApiResult.Error -> {
                        onError(res.message)
                    }
                    else -> {}
                }
            } catch (e: Exception) {
                onError(e.localizedMessage ?: "Gagal mengubah kata sandi")
            } finally {
                _isProfileUpdating.value = false
            }
        }
    }

    // Navigation & Logout
    fun setLogoutDialogVisible(visible: Boolean) {
        _showLogoutDialog.value = visible
    }

    fun setNavRoute(route: String) {
        _currentNavRoute.value = route
    }

    fun logout(onLoggedOut: () -> Unit) {
        viewModelScope.launch {
            _showLogoutDialog.value = false
            try {
                authRepository.logout()
            } catch (e: Exception) {
                // Ignore network error on logout
            } finally {
                onLoggedOut()
            }
        }
    }
}
