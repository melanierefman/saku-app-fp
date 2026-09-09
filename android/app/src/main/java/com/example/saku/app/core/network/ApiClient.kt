package com.example.saku.app.core.network

import android.content.Context
import com.example.saku.app.core.data.TokenManager
import com.example.saku.app.core.network.api.AuthApiService
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ApiClient {

    private val gson: Gson = GsonBuilder()
        .setLenient()
        .create()

    private fun createOkHttpClient(tokenManager: TokenManager): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        return OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(tokenManager))
            .addInterceptor(loggingInterceptor)
            .connectTimeout(ApiConstants.CONNECT_TIMEOUT, TimeUnit.SECONDS)
            .readTimeout(ApiConstants.READ_TIMEOUT, TimeUnit.SECONDS)
            .writeTimeout(ApiConstants.WRITE_TIMEOUT, TimeUnit.SECONDS)
            .build()
    }

    fun getRetrofit(context: Context): Retrofit {
        val tokenManager = TokenManager.getInstance(context)
        return Retrofit.Builder()
            .baseUrl(ApiConstants.BASE_URL)
            .client(createOkHttpClient(tokenManager))
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    fun getAuthApiService(context: Context): AuthApiService {
        return getRetrofit(context).create(AuthApiService::class.java)
    }

    /**
     * Helper to parse error body when API returns HTTP 4xx/5xx
     */
    fun <T> parseError(response: Response<T>): String {
        return try {
            val errorBody = response.errorBody()?.string()
            if (!errorBody.isNullOrBlank()) {
                val errorResponse = gson.fromJson(errorBody, ApiResponse::class.java)
                errorResponse.message ?: "Terjadi kesalahan pada server (${response.code()})"
            } else {
                "Terjadi kesalahan pada server (${response.code()})"
            }
        } catch (e: Exception) {
            "Gagal menghubungi server (${response.code()})"
        }
    }
}
