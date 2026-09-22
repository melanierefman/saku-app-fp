package com.example.saku.app.core.data.repository

import com.example.saku.app.core.network.ApiResult
import com.example.saku.app.core.network.dto.WilayahItemDto

interface WilayahRepository {
    suspend fun getProvinces(): ApiResult<List<WilayahItemDto>>
    suspend fun getRegencies(provinceCode: String): ApiResult<List<WilayahItemDto>>
    suspend fun getDistricts(regencyCode: String): ApiResult<List<WilayahItemDto>>
    suspend fun getVillages(districtCode: String): ApiResult<List<WilayahItemDto>>
}