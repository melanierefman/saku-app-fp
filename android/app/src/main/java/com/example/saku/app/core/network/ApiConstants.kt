package com.example.saku.app.core.network

object ApiConstants {
    // Menggunakan USB Reverse Proxy (adb reverse tcp:8080 tcp:8080)
    // Catatan: Jika menggunakan Emulator Android Studio, gunakan "http://10.0.2.2:8080/api/"
    // Catatan: Jika menggunakan Wi-Fi LAN langsung, gunakan IP laptop (contoh:
    // "http://192.168.1.25:8080/api/")
    // const val BASE_URL = "http://localhost:8080/api/"
    const val BASE_URL = "https://volleyball-scored-arizona-operator.trycloudflare.com/api/"

    // Timeout configurations (dalam detik)
    const val CONNECT_TIMEOUT = 30L
    const val READ_TIMEOUT = 30L
    const val WRITE_TIMEOUT = 30L
}
