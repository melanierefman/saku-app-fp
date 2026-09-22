package com.example.saku.app.core.network.dto

import com.google.gson.annotations.SerializedName

data class WilayahIdResponse<T>(
    @SerializedName("data") val data: List<T>? = null
)

data class WilayahItemDto(
    @SerializedName("code") val code: String,
    @SerializedName("name") val name: String
)