package com.example.saku.app.core.network

import com.google.gson.annotations.SerializedName

data class ApiResponse<T>(
    @SerializedName("statusCode")
    val statusCode: Int? = null,

    @SerializedName("message")
    val message: String? = null,

    @SerializedName("data")
    val data: T? = null,

    @SerializedName("errors")
    val errors: Any? = null
) {
    val isSuccess: Boolean
        get() = (statusCode ?: 200) in 200..299
}
