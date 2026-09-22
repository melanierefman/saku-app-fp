package com.example.saku.app.core.network

import com.example.saku.app.core.data.TokenManager
import com.example.saku.app.core.network.dto.AuthResponse
import com.example.saku.app.core.network.dto.RefreshTokenRequest
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import okhttp3.Route
import java.util.concurrent.TimeUnit

class TokenAuthenticator(
    private val tokenManager: TokenManager
) : Authenticator {

    private val gson = Gson()

    override fun authenticate(route: Route?, response: Response): Request? {
        val path = response.request.url.encodedPath
        // Jangan intercept jika request adalah endpoint publik atau auth
        val isPublicAuth = path.contains("/auth/") || path.contains("/public/")
        if (isPublicAuth) {
            return null
        }

        // Batasi retry maksimal 2 kali untuk mencegah infinite loop
        if (responseCount(response) >= 2) {
            return null
        }

        synchronized(this) {
            val currentToken = runBlocking { tokenManager.getAccessTokenSync() }
            val requestToken = response.request.header("Authorization")?.removePrefix("Bearer ")?.trim()

            // Jika token sudah diperbarui oleh thread / request lain, langsung gunakan token baru
            if (currentToken != null && currentToken != requestToken) {
                return response.request.newBuilder()
                    .header("Authorization", "Bearer $currentToken")
                    .build()
            }

            val refreshToken = runBlocking { tokenManager.getRefreshTokenSync() }
            if (refreshToken.isNullOrBlank()) {
                return null
            }

            val refreshClient = OkHttpClient.Builder()
                .connectTimeout(ApiConstants.CONNECT_TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(ApiConstants.READ_TIMEOUT, TimeUnit.SECONDS)
                .build()

            val refreshBody = gson.toJson(RefreshTokenRequest(refreshToken))
                .toRequestBody("application/json; charset=utf-8".toMediaType())

            val refreshRequest = Request.Builder()
                .url(ApiConstants.BASE_URL + "auth/customer/refresh-token")
                .post(refreshBody)
                .addHeader("Accept", "application/json")
                .build()

            try {
                val refreshResponse = refreshClient.newCall(refreshRequest).execute()
                if (refreshResponse.isSuccessful) {
                    val responseBody = refreshResponse.body?.string()
                    if (!responseBody.isNullOrBlank()) {
                        val type = object : TypeToken<ApiResponse<AuthResponse>>() {}.type
                        val apiResponse: ApiResponse<AuthResponse> = gson.fromJson(responseBody, type)
                        val authData = apiResponse.data

                        if (authData != null && !authData.accessToken.isNullOrBlank()) {
                            runBlocking {
                                tokenManager.updateTokensOnly(
                                    accessToken = authData.accessToken,
                                    refreshToken = authData.refreshToken ?: refreshToken
                                )
                            }

                            return response.request.newBuilder()
                                .header("Authorization", "Bearer ${authData.accessToken}")
                                .build()
                        }
                    }
                } else {
                    // Refresh token tidak valid / kedaluwarsa -> bersihkan sesi
                    runBlocking {
                        tokenManager.clearSession()
                    }
                }
            } catch (e: Exception) {
                android.util.Log.e("TokenAuthenticator", "Token refresh request failed: ${e.message}")
            }

            return null
        }
    }

    private fun responseCount(response: Response): Int {
        var count = 1
        var prior = response.priorResponse
        while (prior != null) {
            count++
            prior = prior.priorResponse
        }
        return count
    }
}