package com.example.saku.app.core.data.repository

import com.example.saku.app.core.network.ApiResult
import com.example.saku.app.core.network.dto.NotifikasiItemDto
import com.example.saku.app.core.network.dto.UnreadNotifikasiCountDto

interface NotificationRepository {
    // Mengambil daftar notifikasi pengguna
    suspend fun getNotifications(status: String? = null): ApiResult<List<NotifikasiItemDto>>

    // Mengambil jumlah notifikasi yang belum dibaca
    suspend fun getUnreadCount(): ApiResult<UnreadNotifikasiCountDto>

    // Menandai satu notifikasi telah dibaca
    suspend fun markNotificationRead(id: String): ApiResult<NotifikasiItemDto>

    // Menandai semua notifikasi telah dibaca
    suspend fun markAllNotificationsRead(): ApiResult<String>
}