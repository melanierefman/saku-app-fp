package com.example.saku.app.core.network.dto

import com.google.gson.annotations.SerializedName

data class FcmTokenRequestDto(
    @SerializedName("fcmToken") val fcmToken: String
)
