package com.example.saku.app

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.content.Intent
import androidx.compose.runtime.mutableStateOf
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.saku.app.core.network.ApiClient
import com.example.saku.app.core.network.dto.FcmTokenRequestDto
import com.example.saku.app.core.notification.SakuFirebaseMessagingService
import com.example.saku.app.navigation.AppNavHost
import com.example.saku.app.ui.theme.SAKUAppTheme
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private val pendingRouteState = mutableStateOf<String?>(null)

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            Log.d("MainActivity", "POST_NOTIFICATIONS permission granted")
        } else {
            Log.w("MainActivity", "POST_NOTIFICATIONS permission denied")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // 1. Create Notification Channel
        SakuFirebaseMessagingService.createNotificationChannel(this)

        // 2. Request Notification Permission for Android 13+
        askNotificationPermission()

        // 3. Sync FCM Token with backend
        syncFcmToken()

        // 4. Handle incoming notification deep link
        handleNotificationIntent(intent)

        setContent {
            SAKUAppTheme {
                AppNavHost(
                    pendingRoute = pendingRouteState.value,
                    onRouteHandled = { pendingRouteState.value = null }
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()
        syncFcmToken(applicationContext)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleNotificationIntent(intent)
    }

    private fun handleNotificationIntent(intent: Intent?) {
        if (intent == null) return
        val targetRoute = SakuFirebaseMessagingService.extractTargetRoute(intent)
        if (!targetRoute.isNullOrBlank()) {
            Log.d("MainActivity", "🔔 Target route from notification intent: $targetRoute")
            pendingRouteState.value = targetRoute
        }

        val notifId = SakuFirebaseMessagingService.extractNotifId(intent)
        val loanId = SakuFirebaseMessagingService.extractLoanId(intent)

        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val apiService = ApiClient.getCustomerApiService(applicationContext)
                if (!notifId.isNullOrBlank()) {
                    apiService.markNotificationRead(notifId)
                    Log.d("MainActivity", "🔔 Auto-marked notification $notifId as read from push intent")
                } else if (!loanId.isNullOrBlank()) {
                    val res = apiService.getNotifications()
                    if (res.isSuccessful) {
                        res.body()?.data?.filter { !it.isNotificationRead && it.pengajuanPinjamanId == loanId }
                            ?.forEach { unreadItem ->
                                unreadItem.id?.let { apiService.markNotificationRead(it) }
                            }
                        Log.d("MainActivity", "🔔 Auto-marked unread notifications for loan $loanId as read")
                    }
                }
            } catch (e: Exception) {
                Log.w("MainActivity", "Could not auto-mark notification as read: ${e.message}")
            }
        }
    }

    private fun askNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) !=
                PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    companion object {
        fun syncFcmToken(context: android.content.Context) {
            try {
                FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val token = task.result
                        Log.d("MainActivity", "Fetched FCM Token: $token")
                        kotlinx.coroutines.CoroutineScope(Dispatchers.IO).launch {
                            try {
                                val tokenManager = com.example.saku.app.core.data.TokenManager.getInstance(context)
                                val accessToken = tokenManager.getAccessTokenSync()
                                if (!accessToken.isNullOrBlank()) {
                                    val apiService = ApiClient.getCustomerApiService(context)
                                    apiService.updateFcmToken(FcmTokenRequestDto(token))
                                    Log.d("MainActivity", "Synced FCM token to backend successfully")
                                }
                            } catch (e: Exception) {
                                Log.d("MainActivity", "Could not sync FCM token: ${e.message}")
                            }
                        }
                    } else {
                        Log.w("MainActivity", "Fetching FCM registration token failed", task.exception)
                    }
                }
            } catch (e: Exception) {
                Log.e("MainActivity", "Error initializing FCM token fetch: ${e.message}")
            }
        }
    }

    private fun syncFcmToken() {
        syncFcmToken(applicationContext)
    }
}