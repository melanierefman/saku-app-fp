package com.example.saku.app.core.data.repository

import com.example.saku.app.core.database.dao.NotificationDao
import com.example.saku.app.core.database.entity.NotificationEntity
import com.example.saku.app.core.network.ApiClient
import com.example.saku.app.core.network.ApiResult
import com.example.saku.app.core.network.api.CustomerApiService
import com.example.saku.app.core.network.dto.NotifikasiItemDto
import com.example.saku.app.core.network.dto.UnreadNotifikasiCountDto
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationRepositoryImpl @Inject constructor(
    private val customerApiService: CustomerApiService,
    private val notificationDao: NotificationDao
) : NotificationRepository {

    override suspend fun getNotifications(status: String?): ApiResult<List<NotifikasiItemDto>> {
        return try {
            val response = customerApiService.getNotifications(status)
            if (response.isSuccessful && response.body()?.data != null) {
                val list = response.body()!!.data ?: emptyList()
                if (list.isNotEmpty()) {
                    notificationDao.insertNotifications(list.map { NotificationEntity.fromDto(it) })
                }
                ApiResult.Success(list, response.body()?.message)
            } else {
                val cached = notificationDao.getAllNotifications()
                if (cached.isNotEmpty()) {
                    ApiResult.Success(cached.map { it.toDto() }, "Menampilkan notifikasi tersimpan")
                } else {
                    ApiResult.Error(ApiClient.parseError(response), response.code())
                }
            }
        } catch (e: Exception) {
            val cached = notificationDao.getAllNotifications()
            if (cached.isNotEmpty()) {
                ApiResult.Success(cached.map { it.toDto() }, "Offline mode - notifikasi lokal")
            } else {
                ApiResult.Error(e.localizedMessage ?: "Gagal memuat notifikasi")
            }
        }
    }

    override suspend fun getUnreadCount(): ApiResult<UnreadNotifikasiCountDto> {
        return try {
            val response = customerApiService.getUnreadCount()
            if (response.isSuccessful && response.body()?.data != null) {
                ApiResult.Success(response.body()!!.data!!, response.body()?.message)
            } else {
                ApiResult.Error(ApiClient.parseError(response), response.code())
            }
        } catch (e: Exception) {
            ApiResult.Error(e.localizedMessage ?: "Gagal memuat jumlah notifikasi belum dibaca")
        }
    }

    override suspend fun getNotificationDetail(id: String): ApiResult<NotifikasiItemDto> {
        return try {
            val response = customerApiService.getNotificationDetail(id)
            if (response.isSuccessful && response.body()?.data != null) {
                val item = response.body()!!.data!!
                notificationDao.insertNotifications(listOf(NotificationEntity.fromDto(item)))
                ApiResult.Success(item, response.body()?.message)
            } else {
                ApiResult.Error(ApiClient.parseError(response), response.code())
            }
        } catch (e: Exception) {
            ApiResult.Error(e.localizedMessage ?: "Gagal memuat detail notifikasi")
        }
    }

    override suspend fun markNotificationRead(id: String): ApiResult<NotifikasiItemDto> {
        return try {
            notificationDao.markAsRead(id)
            val response = customerApiService.markNotificationRead(id)
            if (response.isSuccessful && response.body()?.data != null) {
                ApiResult.Success(response.body()!!.data!!, response.body()?.message)
            } else {
                ApiResult.Error(ApiClient.parseError(response), response.code())
            }
        } catch (e: Exception) {
            ApiResult.Error(e.localizedMessage ?: "Gagal memperbarui status notifikasi")
        }
    }

    override suspend fun markAllNotificationsRead(): ApiResult<String> {
        return try {
            notificationDao.markAllAsRead()
            val response = customerApiService.markAllNotificationsRead()
            if (response.isSuccessful) {
                ApiResult.Success(response.body()?.data ?: "Semua notifikasi ditandai dibaca", response.body()?.message)
            } else {
                ApiResult.Error(ApiClient.parseError(response), response.code())
            }
        } catch (e: Exception) {
            ApiResult.Error(e.localizedMessage ?: "Gagal menandai semua notifikasi")
        }
    }
}
