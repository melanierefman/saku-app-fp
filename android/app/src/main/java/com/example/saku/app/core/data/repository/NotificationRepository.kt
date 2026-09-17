package com.example.saku.app.core.data.repository

import com.example.saku.app.core.network.ApiResult
import com.example.saku.app.core.network.dto.NotifikasiItemDto
import com.example.saku.app.core.network.dto.UnreadNotifikasiCountDto

interface NotificationRepository {
    suspend fun getNotifications(status: String? = null): ApiResult<List<NotifikasiItemDto>>
    suspend fun getUnreadCount(): ApiResult<UnreadNotifikasiCountDto>
    suspend fun getNotificationDetail(id: String): ApiResult<NotifikasiItemDto>
    suspend fun markNotificationRead(id: String): ApiResult<NotifikasiItemDto>
    suspend fun markAllNotificationsRead(): ApiResult<String>
}
