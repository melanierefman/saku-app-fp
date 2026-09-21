package com.example.saku.app.core.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.saku.app.MainActivity
import com.example.saku.app.R
import com.example.saku.app.core.network.ApiClient
import com.example.saku.app.core.network.dto.FcmTokenRequestDto
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SakuFirebaseMessagingService : FirebaseMessagingService() {

    companion object {
        private const val TAG = "SakuFCM"
        const val CHANNEL_ID = "saku_loan_channel"
        const val CHANNEL_NAME = "Notifikasi SAKU"
        const val CHANNEL_DESC = "Pemberitahuan status pengajuan dan tagihan pinjaman SAKU"

        const val EXTRA_NOTIFICATION_TYPE = "EXTRA_NOTIFICATION_TYPE"
        const val EXTRA_LOAN_ID = "EXTRA_LOAN_ID"
        const val EXTRA_TARGET_ROUTE = "EXTRA_TARGET_ROUTE"
        const val EXTRA_NOTIF_ID = "EXTRA_NOTIF_ID"

        fun extractTargetRoute(intent: Intent?): String? {
            if (intent == null) return null
            val route = intent.getStringExtra(EXTRA_TARGET_ROUTE)
                ?: intent.getStringExtra("targetRoute")
            if (!route.isNullOrBlank()) return route

            val loanId = intent.getStringExtra(EXTRA_LOAN_ID)
                ?: intent.getStringExtra("pengajuanId")
            if (!loanId.isNullOrBlank()) return "loan_detail/$loanId"

            val type = intent.getStringExtra(EXTRA_NOTIFICATION_TYPE)
                ?: intent.getStringExtra("type")
            if (type?.contains("KYC", ignoreCase = true) == true || type?.contains("VERIFIKASI", ignoreCase = true) == true) {
                return "kyc_pending"
            }
            return null
        }

        fun extractNotifId(intent: Intent?): String? {
            if (intent == null) return null
            return intent.getStringExtra(EXTRA_NOTIF_ID)
                ?: intent.getStringExtra("notifId")
                ?: intent.getStringExtra("notificationId")
        }

        fun extractLoanId(intent: Intent?): String? {
            if (intent == null) return null
            return intent.getStringExtra(EXTRA_LOAN_ID)
                ?: intent.getStringExtra("pengajuanId")
        }

        fun createNotificationChannel(context: Context) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channel = NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_HIGH
                ).apply {
                    description = CHANNEL_DESC
                    enableLights(true)
                    enableVibration(true)
                }
                val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.createNotificationChannel(channel)
            }
        }
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d(TAG, "New FCM Token received: $token")
        sendTokenToServer(token)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        Log.d(TAG, "Message received from: ${remoteMessage.from}")

        val title = remoteMessage.notification?.title
            ?: remoteMessage.data["title"]
            ?: remoteMessage.data["judul"]
            ?: "SAKU - Pinjaman Digital"

        val message = remoteMessage.notification?.body
            ?: remoteMessage.data["body"]
            ?: remoteMessage.data["pesan"]
            ?: "Ada informasi baru mengenai pengajuan pinjaman Anda."

        val type = remoteMessage.data["type"] ?: "INFO"
        val loanId = remoteMessage.data["pengajuanId"]
        val notifId = remoteMessage.data["notifId"] ?: remoteMessage.data["notificationId"]
        val targetRoute = remoteMessage.data["targetRoute"] ?: if (!loanId.isNullOrBlank()) {
            "loan_detail/$loanId"
        } else if (type.contains("KYC", ignoreCase = true) || type.contains("VERIFIKASI", ignoreCase = true)) {
            "kyc_pending"
        } else {
            null
        }

        showNotification(title, message, type, loanId, targetRoute, notifId)
    }

    private fun showNotification(
        title: String,
        message: String,
        type: String? = null,
        loanId: String? = null,
        targetRoute: String? = null,
        notifId: String? = null
    ) {
        createNotificationChannel(applicationContext)

        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra(EXTRA_NOTIFICATION_TYPE, type)
            putExtra(EXTRA_LOAN_ID, loanId)
            putExtra(EXTRA_TARGET_ROUTE, targetRoute)
            putExtra(EXTRA_NOTIF_ID, notifId)
            // Also duplicate plain keys to support direct FCM data payload matching
            putExtra("type", type)
            putExtra("pengajuanId", loanId)
            putExtra("targetRoute", targetRoute)
            putExtra("notifId", notifId)
        }

        val pendingIntent = PendingIntent.getActivity(
            this,
            System.currentTimeMillis().toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notificationBuilder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification_saku)
            .setColor(0xFFFF7A00.toInt()) // SAKU Primary Orange Accent
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setContentIntent(pendingIntent)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notificationId = (System.currentTimeMillis() % 100000).toInt()
        notificationManager.notify(notificationId, notificationBuilder.build())
    }

    private fun sendTokenToServer(token: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val apiService = ApiClient.getCustomerApiService(applicationContext)
                val response = apiService.updateFcmToken(FcmTokenRequestDto(token))
                if (response.isSuccessful) {
                    Log.d(TAG, "FCM token successfully registered to backend")
                } else {
                    Log.w(TAG, "Backend returned error registering FCM token: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error sending FCM token to backend: ${e.message}")
            }
        }
    }
}
