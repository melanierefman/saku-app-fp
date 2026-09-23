package com.example.saku.app.core.network

object ApiConstants {
    // Production Backend via Nginx Reverse Proxy (SSL HTTPS)
//    const val BASE_URL = "https://saku.morpkhai.web.id/api/"

    const val BASE_URL = "http://localhost:8080/api/"

    // Timeout configurations (dalam detik)
    const val CONNECT_TIMEOUT = 30L
    const val READ_TIMEOUT = 30L
    const val WRITE_TIMEOUT = 30L
}