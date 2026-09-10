package com.example.saku.app.core.network.api

import com.example.saku.app.core.network.ApiResponse
import com.example.saku.app.core.network.dto.CustomerProfileDto
import com.example.saku.app.core.network.dto.LoanApplicationItemDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface CustomerApiService {

    @GET("customer/profile")
    suspend fun getProfile(): Response<ApiResponse<CustomerProfileDto>>

    @GET("customer/pengajuan-pinjaman/my")
    suspend fun getMyLoans(): Response<ApiResponse<List<LoanApplicationItemDto>>>

    @GET("customer/pengajuan-pinjaman/{id}")
    suspend fun getLoanById(
        @Path("id") id: String
    ): Response<ApiResponse<LoanApplicationItemDto>>
}
