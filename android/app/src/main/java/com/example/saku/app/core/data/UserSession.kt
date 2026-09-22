package com.example.saku.app.core.data

data class UserSession(
    val id: String = "",
    val username: String = "",
    val nama: String = "",
    val email: String = "",
    val noHp: String = "",
    val role: String = "CUSTOMER",
    val isKycVerified: Boolean = false,
    val accessToken: String = "",
    val refreshToken: String = ""
)