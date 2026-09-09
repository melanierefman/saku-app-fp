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

        @Volatile
        private var INSTANCE: TokenManager? = null

        fun getInstance(context: Context): TokenManager {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: TokenManager(context.applicationContext).also { INSTANCE = it }
            }
        }
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
            preferences[KEY_USER_ID] = id
            preferences[KEY_USERNAME] = username
            preferences[KEY_NAMA] = nama
            preferences[KEY_EMAIL] = email
            preferences[KEY_NO_HP] = noHp
            preferences[KEY_ROLE] = role
            preferences[KEY_IS_KYC_VERIFIED] = isKycVerified
        }
    }

    suspend fun getAccessTokenSync(): String? {
        return accessTokenFlow.first()
    }

    suspend fun clearSession() {
        context.dataStore.edit { preferences ->
            preferences.clear()
        }
    }
}
