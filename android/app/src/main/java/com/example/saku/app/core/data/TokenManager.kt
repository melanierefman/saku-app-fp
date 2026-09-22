package com.example.saku.app.core.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.io.IOException

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "saku_customer_prefs")

class TokenManager(private val context: Context) {

    companion object {
        private val KEY_ACCESS_TOKEN = stringPreferencesKey("access_token")
        private val KEY_REFRESH_TOKEN = stringPreferencesKey("refresh_token")
        private val KEY_USER_ID = stringPreferencesKey("user_id")
        private val KEY_USERNAME = stringPreferencesKey("username")
        private val KEY_NAMA = stringPreferencesKey("nama")
        private val KEY_EMAIL = stringPreferencesKey("email")
        private val KEY_NO_HP = stringPreferencesKey("no_hp")
        private val KEY_ROLE = stringPreferencesKey("role")
        private val KEY_IS_KYC_VERIFIED = booleanPreferencesKey("is_kyc_verified")
        private val KEY_IS_ONBOARDING_COMPLETED = booleanPreferencesKey("is_onboarding_completed")
        private val KEY_CACHED_PROFILE_JSON = stringPreferencesKey("cached_profile_json")

        @Volatile
        private var INSTANCE: TokenManager? = null

        fun getInstance(context: Context): TokenManager {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: TokenManager(context.applicationContext).also { INSTANCE = it }
            }
        }
    }

    val isOnboardingCompletedFlow: Flow<Boolean> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) emit(emptyPreferences()) else throw exception
        }
        .map { preferences ->
            preferences[KEY_IS_ONBOARDING_COMPLETED] ?: false
        }

    val accessTokenFlow: Flow<String?> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) emit(emptyPreferences()) else throw exception
        }
        .map { preferences ->
            preferences[KEY_ACCESS_TOKEN]
        }

    val refreshTokenFlow: Flow<String?> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) emit(emptyPreferences()) else throw exception
        }
        .map { preferences ->
            preferences[KEY_REFRESH_TOKEN]
        }

    val userSessionFlow: Flow<UserSession?> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) emit(emptyPreferences()) else throw exception
        }
        .map { preferences ->
            val accessToken = preferences[KEY_ACCESS_TOKEN] ?: return@map null
            UserSession(
                id = preferences[KEY_USER_ID] ?: "",
                username = preferences[KEY_USERNAME] ?: "",
                nama = preferences[KEY_NAMA] ?: "",
                email = preferences[KEY_EMAIL] ?: "",
                noHp = preferences[KEY_NO_HP] ?: "",
                role = preferences[KEY_ROLE] ?: "CUSTOMER",
                isKycVerified = preferences[KEY_IS_KYC_VERIFIED] ?: false,
                accessToken = accessToken,
                refreshToken = preferences[KEY_REFRESH_TOKEN] ?: ""
            )
        }

    val isLoggedInFlow: Flow<Boolean> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) emit(emptyPreferences()) else throw exception
        }
        .map { preferences ->
            !preferences[KEY_ACCESS_TOKEN].isNullOrBlank()
        }

    val cachedProfileJsonFlow: Flow<String?> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) emit(emptyPreferences()) else throw exception
        }
        .map { preferences ->
            preferences[KEY_CACHED_PROFILE_JSON]
        }

    suspend fun saveAuthTokens(
        accessToken: String,
        refreshToken: String,
        id: String = "",
        username: String = "",
        nama: String = "",
        email: String = "",
        noHp: String = "",
        role: String = "CUSTOMER",
        isKycVerified: Boolean = false
    ) {
        context.dataStore.edit { preferences ->
            preferences[KEY_ACCESS_TOKEN] = accessToken
            preferences[KEY_REFRESH_TOKEN] = refreshToken
            if (id.isNotBlank()) preferences[KEY_USER_ID] = id
            if (username.isNotBlank()) preferences[KEY_USERNAME] = username
            if (nama.isNotBlank()) preferences[KEY_NAMA] = nama
            if (email.isNotBlank()) preferences[KEY_EMAIL] = email
            if (noHp.isNotBlank()) preferences[KEY_NO_HP] = noHp
            if (role.isNotBlank()) preferences[KEY_ROLE] = role
            preferences[KEY_IS_KYC_VERIFIED] = isKycVerified
        }
    }

    suspend fun updateTokensOnly(accessToken: String, refreshToken: String = "") {
        context.dataStore.edit { preferences ->
            preferences[KEY_ACCESS_TOKEN] = accessToken
            if (refreshToken.isNotBlank()) {
                preferences[KEY_REFRESH_TOKEN] = refreshToken
            }
        }
    }

    suspend fun updateProfileData(
        nama: String? = null,
        email: String? = null,
        noHp: String? = null,
        isKycVerified: Boolean? = null
    ) {
        context.dataStore.edit { preferences ->
            nama?.let { if (it.isNotBlank()) preferences[KEY_NAMA] = it }
            email?.let { if (it.isNotBlank()) preferences[KEY_EMAIL] = it }
            noHp?.let { if (it.isNotBlank()) preferences[KEY_NO_HP] = it }
            isKycVerified?.let { preferences[KEY_IS_KYC_VERIFIED] = it }
        }
    }

    suspend fun saveCachedProfileJson(profileJson: String) {
        context.dataStore.edit { preferences ->
            preferences[KEY_CACHED_PROFILE_JSON] = profileJson
        }
    }

    suspend fun getAccessTokenSync(): String? {
        return accessTokenFlow.first()
    }

    suspend fun getRefreshTokenSync(): String? {
        return refreshTokenFlow.first()
    }

    suspend fun getCachedProfileJsonSync(): String? {
        return cachedProfileJsonFlow.first()
    }

    suspend fun setOnboardingCompleted(completed: Boolean = true) {
        context.dataStore.edit { preferences ->
            preferences[KEY_IS_ONBOARDING_COMPLETED] = completed
        }
    }

    suspend fun clearSession() {
        context.dataStore.edit { preferences ->
            val wasOnboardingCompleted = preferences[KEY_IS_ONBOARDING_COMPLETED] ?: true
            preferences.clear()
            preferences[KEY_IS_ONBOARDING_COMPLETED] = wasOnboardingCompleted
        }
    }
}