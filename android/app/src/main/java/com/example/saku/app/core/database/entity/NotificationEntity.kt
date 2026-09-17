package com.example.saku.app.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.saku.app.core.network.dto.NotifikasiItemDto

@Entity(tableName = "notifications")
data class NotificationEntity(
    @PrimaryKey
    val id: String,
    val type: String?,
    val channel: String?,
    val judul: String?,
    val pesan: String?,
    val status: String?,
    val isRead: Boolean,
    val pengajuanPinjamanId: String?,
    val createdDate: String?,
    val lastUpdated: Long = System.currentTimeMillis()
) {
    fun toDto(): NotifikasiItemDto {
        return NotifikasiItemDto(
            id = id,
            type = type,
            channel = channel,
            judul = judul,
            pesan = pesan,
            status = status,
            isRead = isRead,
            pengajuanPinjamanId = pengajuanPinjamanId,
            createdDate = createdDate
        )
    }

    companion object {
        fun fromDto(dto: NotifikasiItemDto): NotificationEntity {
            return NotificationEntity(
                id = dto.id ?: java.util.UUID.randomUUID().toString(),
                type = dto.type,
                channel = dto.channel,
                judul = dto.judul,
                pesan = dto.pesan,
                status = dto.status,
                isRead = dto.isNotificationRead,
                pengajuanPinjamanId = dto.pengajuanPinjamanId,
                createdDate = dto.createdDate
            )
        }
    }
}
