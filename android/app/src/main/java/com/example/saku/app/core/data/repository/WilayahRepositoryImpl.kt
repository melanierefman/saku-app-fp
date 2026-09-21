package com.example.saku.app.core.data.repository

import android.util.Log
import com.example.saku.app.core.network.ApiResult
import com.example.saku.app.core.network.api.WilayahApiService
import com.example.saku.app.core.network.dto.WilayahItemDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.concurrent.ConcurrentHashMap

class WilayahRepositoryImpl(
    private val apiService: WilayahApiService
) : WilayahRepository {

    private var cachedProvinces: List<WilayahItemDto>? = null
    private val cachedRegencies = ConcurrentHashMap<String, List<WilayahItemDto>>()
    private val cachedDistricts = ConcurrentHashMap<String, List<WilayahItemDto>>()
    private val cachedVillages = ConcurrentHashMap<String, List<WilayahItemDto>>()

    companion object {
        private const val TAG = "WilayahRepo"
        val DEFAULT_PROVINCES = listOf(
            WilayahItemDto("11", "Aceh"),
            WilayahItemDto("12", "Sumatera Utara"),
            WilayahItemDto("13", "Sumatera Barat"),
            WilayahItemDto("14", "Riau"),
            WilayahItemDto("15", "Jambi"),
            WilayahItemDto("16", "Sumatera Selatan"),
            WilayahItemDto("17", "Bengkulu"),
            WilayahItemDto("18", "Lampung"),
            WilayahItemDto("19", "Kepulauan Bangka Belitung"),
            WilayahItemDto("21", "Kepulauan Riau"),
            WilayahItemDto("31", "DKI Jakarta"),
            WilayahItemDto("32", "Jawa Barat"),
            WilayahItemDto("33", "Jawa Tengah"),
            WilayahItemDto("34", "Daerah Istimewa Yogyakarta"),
            WilayahItemDto("35", "Jawa Timur"),
            WilayahItemDto("36", "Banten"),
            WilayahItemDto("51", "Bali"),
            WilayahItemDto("52", "Nusa Tenggara Barat"),
            WilayahItemDto("53", "Nusa Tenggara Timur"),
            WilayahItemDto("61", "Kalimantan Barat"),
            WilayahItemDto("62", "Kalimantan Tengah"),
            WilayahItemDto("63", "Kalimantan Selatan"),
            WilayahItemDto("64", "Kalimantan Timur"),
            WilayahItemDto("65", "Kalimantan Utara"),
            WilayahItemDto("71", "Sulawesi Utara"),
            WilayahItemDto("72", "Sulawesi Tengah"),
            WilayahItemDto("73", "Sulawesi Selatan"),
            WilayahItemDto("74", "Sulawesi Tenggara"),
            WilayahItemDto("75", "Gorontalo"),
            WilayahItemDto("76", "Sulawesi Barat"),
            WilayahItemDto("81", "Maluku"),
            WilayahItemDto("82", "Maluku Utara"),
            WilayahItemDto("91", "Papua"),
            WilayahItemDto("92", "Papua Barat"),
            WilayahItemDto("93", "Papua Selatan"),
            WilayahItemDto("94", "Papua Tengah"),
            WilayahItemDto("95", "Papua Pegunungan"),
            WilayahItemDto("96", "Papua Barat Daya")
        )
        fun resolveProvinceCode(input: String): String {
            val trimmed = input.trim()
            if (trimmed.all { it.isDigit() }) return trimmed
            val matched = DEFAULT_PROVINCES.find {
                it.name.equals(trimmed, ignoreCase = true) ||
                it.name.replace(" ", "").equals(trimmed.replace(" ", ""), ignoreCase = true)
            }
            return matched?.code ?: trimmed
        }
    }

    override suspend fun getProvinces(): ApiResult<List<WilayahItemDto>> = withContext(Dispatchers.IO) {
        try {
            cachedProvinces?.let { return@withContext ApiResult.Success(it) }
            val res = apiService.getProvinces()
            val list = res.data ?: emptyList()
            if (list.isNotEmpty()) {
                cachedProvinces = list
                ApiResult.Success(list)
            } else {
                cachedProvinces = DEFAULT_PROVINCES
                ApiResult.Success(DEFAULT_PROVINCES)
            }
        } catch (e: Exception) {
            Log.w(TAG, "getProvinces error: ${e.message}, using fallback")
            cachedProvinces = DEFAULT_PROVINCES
            ApiResult.Success(DEFAULT_PROVINCES)
        }
    }

    override suspend fun getRegencies(provinceCode: String): ApiResult<List<WilayahItemDto>> = withContext(Dispatchers.IO) {
        val targetCode = resolveProvinceCode(provinceCode)
        try {
            cachedRegencies[targetCode]?.let { return@withContext ApiResult.Success(it) }
            val res = apiService.getRegencies(targetCode)
            val list = res.data ?: emptyList()
            if (list.isNotEmpty()) {
                cachedRegencies[targetCode] = list
            }
            ApiResult.Success(list)
        } catch (e: Exception) {
            Log.e(TAG, "getRegencies for $targetCode (original: $provinceCode) error: ${e.message}")
            ApiResult.Error(e.message ?: "Gagal memuat daftar kota/kabupaten")
        }
    }

    override suspend fun getDistricts(regencyCode: String): ApiResult<List<WilayahItemDto>> = withContext(Dispatchers.IO) {
        val targetCode = regencyCode.trim()
        try {
            cachedDistricts[targetCode]?.let { return@withContext ApiResult.Success(it) }
            val res = apiService.getDistricts(targetCode)
            val list = res.data ?: emptyList()
            if (list.isNotEmpty()) {
                cachedDistricts[targetCode] = list
            }
            ApiResult.Success(list)
        } catch (e: Exception) {
            Log.e(TAG, "getDistricts for $targetCode error: ${e.message}")
            ApiResult.Error(e.message ?: "Gagal memuat daftar kecamatan")
        }
    }

    override suspend fun getVillages(districtCode: String): ApiResult<List<WilayahItemDto>> = withContext(Dispatchers.IO) {
        val targetCode = districtCode.trim()
        try {
            cachedVillages[targetCode]?.let { return@withContext ApiResult.Success(it) }
            val res = apiService.getVillages(targetCode)
            val list = res.data ?: emptyList()
            if (list.isNotEmpty()) {
                cachedVillages[targetCode] = list
            }
            ApiResult.Success(list)
        } catch (e: Exception) {
            Log.e(TAG, "getVillages for $targetCode error: ${e.message}")
            ApiResult.Error(e.message ?: "Gagal memuat daftar kelurahan/desa")
        }
    }
}
