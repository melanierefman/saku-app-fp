package com.example.saku.app.core.network.api

import com.example.saku.app.core.network.dto.WilayahIdResponse
import com.example.saku.app.core.network.dto.WilayahItemDto
import retrofit2.http.GET
import retrofit2.http.Path

/**
 * Public API Wilayah Indonesia (wilayah.id)
 * Base URL: https://wilayah.id/api/
 */
interface WilayahApiService {

    @GET("provinces.json")
    suspend fun getProvinces(): WilayahIdResponse<WilayahItemDto>

    @GET("regencies/{provinceCode}.json")
    suspend fun getRegencies(@Path("provinceCode") provinceCode: String): WilayahIdResponse<WilayahItemDto>

    @GET("districts/{regencyCode}.json")
    suspend fun getDistricts(@Path("regencyCode") regencyCode: String): WilayahIdResponse<WilayahItemDto>

    @GET("villages/{districtCode}.json")
    suspend fun getVillages(@Path("districtCode") districtCode: String): WilayahIdResponse<WilayahItemDto>
}