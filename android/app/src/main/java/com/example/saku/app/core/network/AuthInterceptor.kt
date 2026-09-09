package com.example.saku.app.core.network

import com.example.saku.app.core.data.TokenManager
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(private val tokenManager: TokenManager) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        // Ambil token secara synchronous dari DataStore
        val accessToken = runBlocking {
            tokenManager.getAccessTokenSync()
        }

        val requestBuilder = originalRequest.newBuilder()
            .addHeader("Accept", "application/json")

        if (!accessToken.isNullOrBlank() && originalRequest.header("Authorization") == null) {
            requestBuilder.addHeader("Authorization", "Bearer $accessToken")
        }

        return chain.proceed(requestBuilder.build())
    }
}
