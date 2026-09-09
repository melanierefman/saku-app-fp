package com.example.saku.app.core.network

object ApiConstants {
    // Gunakan 10.0.2.2 untuk Android Emulator menuju localhost laptop
    // Ganti dengan IP LAN (misal: "http://192.168.1.50:8080/api/") jika menjalankan di Real Device fisik
    const val BASE_URL = "http://10.0.2.2:8080/api/"

    // Timeout configurations (dalam detik)
    const val CONNECT_TIMEOUT = 30L
    const val READ_TIMEOUT = 30L
    const val WRITE_TIMEOUT = 30L
}
