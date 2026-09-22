package com.example.saku.app.features.profile.edit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.saku.app.core.data.repository.CustomerRepository
import com.example.saku.app.core.data.repository.WilayahRepository
import com.example.saku.app.core.data.repository.WilayahRepositoryImpl
import com.example.saku.app.core.network.ApiResult
import com.example.saku.app.core.network.dto.CustomerProfileDto
import com.example.saku.app.core.network.dto.UpdateDomisiliRequestDto
import com.example.saku.app.core.network.dto.UpdatePekerjaanRequestDto
import com.example.saku.app.core.network.dto.UpdateRekeningRequestDto
import com.example.saku.app.core.ui.components.DropdownOption
import com.google.gson.Gson
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class EditProfileViewModel(
    private val customerRepository: CustomerRepository,
    private val wilayahRepository: WilayahRepository
) : ViewModel() {

    private val gson = Gson()

    private val _customerProfile = MutableStateFlow<CustomerProfileDto?>(null)
    val customerProfile: StateFlow<CustomerProfileDto?> = _customerProfile.asStateFlow()

    private val _isProfileUpdating = MutableStateFlow(false)
    val isProfileUpdating: StateFlow<Boolean> = _isProfileUpdating.asStateFlow()

    // Wilayah Cascading Options
    private val _provinces = MutableStateFlow<List<DropdownOption>>(
        WilayahRepositoryImpl.DEFAULT_PROVINCES.map { DropdownOption(it.code, it.name) }
    )
    val provinces: StateFlow<List<DropdownOption>> = _provinces.asStateFlow()

    private val _regencies = MutableStateFlow<List<DropdownOption>>(emptyList())
    val regencies: StateFlow<List<DropdownOption>> = _regencies.asStateFlow()

    private val _districts = MutableStateFlow<List<DropdownOption>>(emptyList())
    val districts: StateFlow<List<DropdownOption>> = _districts.asStateFlow()

    private val _villages = MutableStateFlow<List<DropdownOption>>(emptyList())
    val villages: StateFlow<List<DropdownOption>> = _villages.asStateFlow()

    // Selected Wilayah Options
    private val _selectedProvince = MutableStateFlow<DropdownOption?>(null)
    val selectedProvince: StateFlow<DropdownOption?> = _selectedProvince.asStateFlow()

    private val _selectedRegency = MutableStateFlow<DropdownOption?>(null)
    val selectedRegency: StateFlow<DropdownOption?> = _selectedRegency.asStateFlow()

    private val _selectedDistrict = MutableStateFlow<DropdownOption?>(null)
    val selectedDistrict: StateFlow<DropdownOption?> = _selectedDistrict.asStateFlow()

    private val _selectedVillage = MutableStateFlow<DropdownOption?>(null)
    val selectedVillage: StateFlow<DropdownOption?> = _selectedVillage.asStateFlow()

    // Wilayah Loading States
    private val _isLoadingWilayah = MutableStateFlow(false)
    val isLoadingWilayah: StateFlow<Boolean> = _isLoadingWilayah.asStateFlow()

    private val _isLoadingRegencies = MutableStateFlow(false)
    val isLoadingRegencies: StateFlow<Boolean> = _isLoadingRegencies.asStateFlow()

    private val _isLoadingDistricts = MutableStateFlow(false)
    val isLoadingDistricts: StateFlow<Boolean> = _isLoadingDistricts.asStateFlow()

    private val _isLoadingVillages = MutableStateFlow(false)
    val isLoadingVillages: StateFlow<Boolean> = _isLoadingVillages.asStateFlow()

    init {
        loadInitialData()
    }

    fun loadInitialData() {
        viewModelScope.launch {
            // 1. Load cached profile immediately
            val cachedJson = customerRepository.getCachedProfileJsonSync()
            if (!cachedJson.isNullOrBlank() && _customerProfile.value == null) {
                try {
                    val cached = gson.fromJson(cachedJson, CustomerProfileDto::class.java)
                    _customerProfile.value = cached
                } catch (e: Exception) {
                    // ignore parse error
                }
            }

            // 2. Fetch fresh profile from network
            when (val res = customerRepository.getProfile()) {
                is ApiResult.Success -> {
                    val profile = res.data
                    _customerProfile.value = profile
                    customerRepository.saveCachedProfileJson(gson.toJson(profile))
                    prefillWilayahFromProfile(profile)
                }
                else -> {
                    prefillWilayahFromProfile(_customerProfile.value)
                }
            }

            // 3. Load provinces from WilayahRepository
            loadProvinces()
        }
    }

    private fun loadProvinces() {
        viewModelScope.launch {
            _isLoadingWilayah.value = true
            when (val res = wilayahRepository.getProvinces()) {
                is ApiResult.Success -> {
                    val opts = res.data.map { DropdownOption(it.code, it.name) }
                    _provinces.value = opts
                    prefillWilayahFromProfile(_customerProfile.value)
                }
                else -> {
                    prefillWilayahFromProfile(_customerProfile.value)
                }
            }
            _isLoadingWilayah.value = false
        }
    }

    private fun prefillWilayahFromProfile(profile: CustomerProfileDto?) {
        val dom = profile?.alamatDomisili ?: return
        val provName = dom.provinsi?.trim() ?: return
        if (provName.isBlank()) return

        // If already selected and different, do not overwrite user's in-progress selection
        if (_selectedProvince.value != null && _selectedProvince.value?.label != provName) return

        val provOpt = _provinces.value.find {
            it.label.equals(provName, ignoreCase = true) ||
            provName.contains(it.label, ignoreCase = true) ||
            it.label.contains(provName, ignoreCase = true)
        } ?: DropdownOption(value = "", label = provName)

        _selectedProvince.value = provOpt

        if (provOpt.value.isNotBlank()) {
            viewModelScope.launch {
                _isLoadingRegencies.value = true
                when (val res = wilayahRepository.getRegencies(provOpt.value)) {
                    is ApiResult.Success -> {
                        val regOpts = res.data.map { DropdownOption(it.code, it.name) }
                        _regencies.value = regOpts

                        val cityName = dom.kotaKabupaten?.trim() ?: ""
                        if (cityName.isNotBlank()) {
                            val regOpt = regOpts.find {
                                it.label.equals(cityName, ignoreCase = true) ||
                                cityName.contains(it.label, ignoreCase = true) ||
                                it.label.contains(cityName, ignoreCase = true)
                            } ?: DropdownOption(value = "", label = cityName)

                            _selectedRegency.value = regOpt

                            if (regOpt.value.isNotBlank()) {
                                _isLoadingDistricts.value = true
                                when (val distRes = wilayahRepository.getDistricts(regOpt.value)) {
                                    is ApiResult.Success -> {
                                        val distOpts = distRes.data.map { DropdownOption(it.code, it.name) }
                                        _districts.value = distOpts

                                        val kecName = dom.kecamatan?.trim() ?: ""
                                        if (kecName.isNotBlank()) {
                                            val distOpt = distOpts.find {
                                                it.label.equals(kecName, ignoreCase = true) ||
                                                kecName.contains(it.label, ignoreCase = true) ||
                                                it.label.contains(kecName, ignoreCase = true)
                                            } ?: DropdownOption(value = "", label = kecName)

                                            _selectedDistrict.value = distOpt

                                            if (distOpt.value.isNotBlank()) {
                                                _isLoadingVillages.value = true
                                                when (val vilRes = wilayahRepository.getVillages(distOpt.value)) {
                                                    is ApiResult.Success -> {
                                                        val vilOpts = vilRes.data.map { DropdownOption(it.code, it.name) }
                                                        _villages.value = vilOpts

                                                        val kelName = dom.kelurahan?.trim() ?: ""
                                                        if (kelName.isNotBlank()) {
                                                            val vilOpt = vilOpts.find {
                                                                it.label.equals(kelName, ignoreCase = true) ||
                                                                kelName.contains(it.label, ignoreCase = true) ||
                                                                it.label.contains(kelName, ignoreCase = true)
                                                            } ?: DropdownOption(value = "", label = kelName)
                                                            _selectedVillage.value = vilOpt
                                                        }
                                                    }
                                                    else -> {}
                                                }
                                                _isLoadingVillages.value = false
                                            }
                                        }
                                    }
                                    else -> {}
                                }
                                _isLoadingDistricts.value = false
                            }
                        }
                    }
                    else -> {}
                }
                _isLoadingRegencies.value = false
            }
        }
    }

    fun onProvinceSelected(option: DropdownOption?) {
        _selectedProvince.value = option
        _selectedRegency.value = null
        _selectedDistrict.value = null
        _selectedVillage.value = null
        _regencies.value = emptyList()
        _districts.value = emptyList()
        _villages.value = emptyList()

        if (option != null && option.value.isNotBlank()) {
            viewModelScope.launch {
                _isLoadingRegencies.value = true
                when (val res = wilayahRepository.getRegencies(option.value)) {
                    is ApiResult.Success -> {
                        _regencies.value = res.data.map { DropdownOption(it.code, it.name) }
                    }
                    else -> {}
                }
                _isLoadingRegencies.value = false
            }
        }
    }

    fun onRegencySelected(option: DropdownOption?) {
        _selectedRegency.value = option
        _selectedDistrict.value = null
        _selectedVillage.value = null
        _districts.value = emptyList()
        _villages.value = emptyList()

        if (option != null && option.value.isNotBlank()) {
            viewModelScope.launch {
                _isLoadingDistricts.value = true
                when (val res = wilayahRepository.getDistricts(option.value)) {
                    is ApiResult.Success -> {
                        _districts.value = res.data.map { DropdownOption(it.code, it.name) }
                    }
                    else -> {}
                }
                _isLoadingDistricts.value = false
            }
        }
    }

    fun onDistrictSelected(option: DropdownOption?) {
        _selectedDistrict.value = option
        _selectedVillage.value = null
        _villages.value = emptyList()

        if (option != null && option.value.isNotBlank()) {
            viewModelScope.launch {
                _isLoadingVillages.value = true
                when (val res = wilayahRepository.getVillages(option.value)) {
                    is ApiResult.Success -> {
                        _villages.value = res.data.map { DropdownOption(it.code, it.name) }
                    }
                    else -> {}
                }
                _isLoadingVillages.value = false
            }
        }
    }

    fun onVillageSelected(option: DropdownOption?) {
        _selectedVillage.value = option
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
                        onSuccess("Informasi rekening bank berhasil diperbarui")
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

    fun updatePekerjaan(
        pekerjaan: String,
        tempatKerja: String?,
        statusPekerjaan: String?,
        penghasilanBulanan: Double?,
        lamaBekerjaBulan: Int? = null,
        totalCicilanLainBulanan: Double? = null,
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
                    penghasilanBulanan = penghasilanBulanan,
                    lamaBekerjaBulan = lamaBekerjaBulan,
                    totalCicilanLainBulanan = totalCicilanLainBulanan
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
}
