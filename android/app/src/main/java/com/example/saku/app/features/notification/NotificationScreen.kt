package com.example.saku.app.features.notification

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.composables.icons.lucide.ArrowLeft
import com.composables.icons.lucide.Bell
import com.composables.icons.lucide.CheckCheck
import com.composables.icons.lucide.ChevronRight
import com.composables.icons.lucide.CircleCheck
import com.composables.icons.lucide.Clock
import com.composables.icons.lucide.Info
import com.composables.icons.lucide.Lucide
import com.example.saku.app.core.network.dto.NotifikasiItemDto
import com.example.saku.app.core.ui.components.ButtonVariant
import com.example.saku.app.core.ui.components.ConfirmationDialog
import com.example.saku.app.core.ui.components.DialogType
import com.example.saku.app.features.home.HomeViewModel
import com.example.saku.app.ui.theme.Background
import com.example.saku.app.ui.theme.Border
import com.example.saku.app.ui.theme.Primary
import com.example.saku.app.ui.theme.Primary0
import com.example.saku.app.ui.theme.Surface
import com.example.saku.app.ui.theme.TextMuted
import com.example.saku.app.ui.theme.TextPrimary
import com.example.saku.app.ui.theme.TextSecondary
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationScreen(
    onNavigateBack: () -> Unit,
    onNavigateToLoanDetail: (String) -> Unit = {},
    onNavigateToKycPending: () -> Unit = {},
    viewModel: HomeViewModel = koinViewModel()
) {
    val notifikasiList by viewModel.notifikasiList.collectAsState()
    val unreadCount by viewModel.unreadNotifikasiCount.collectAsState()
    val isLoading by viewModel.isNotifikasiLoading.collectAsState()

    var selectedTab by remember { mutableStateOf("SEMUA") } // "SEMUA" or "UNREAD"
    var showMarkAllReadDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.fetchNotifications()
    }

    val displayedList = if (selectedTab == "UNREAD") {
        notifikasiList.filter { !it.isNotificationRead }
    } else {
        notifikasiList
    }

    Scaffold(
        topBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Surface,
                border = BorderStroke(1.dp, Border),
                shadowElevation = 1.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Lucide.ArrowLeft,
                            contentDescription = "Kembali",
                            tint = TextPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = "Pemberitahuan",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        modifier = Modifier.weight(1f)
                    )

                    if (unreadCount > 0) {
                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .clickable { showMarkAllReadDialog = true }
                                .padding(horizontal = 8.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Lucide.CheckCheck,
                                contentDescription = null,
                                tint = Primary,
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = "Tandai Dibaca",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Primary
                            )
                        }
                    }
                }
            }
        },
        containerColor = Background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Tab Filter: Semua vs Belum Dibaca (Di luar header / dalam body)
            Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFF1F5F9))
                        .padding(4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(9.dp))
                            .background(if (selectedTab == "SEMUA") Surface else Color.Transparent)
                            .clickable { selectedTab = "SEMUA" }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Semua (${notifikasiList.size})",
                            fontSize = 12.5.sp,
                            fontWeight = if (selectedTab == "SEMUA") FontWeight.Bold else FontWeight.Medium,
                            color = if (selectedTab == "SEMUA") TextPrimary else TextSecondary
                        )
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(9.dp))
                            .background(if (selectedTab == "UNREAD") Surface else Color.Transparent)
                            .clickable { selectedTab = "UNREAD" }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Belum Dibaca ($unreadCount)",
                            fontSize = 12.5.sp,
                            fontWeight = if (selectedTab == "UNREAD") FontWeight.Bold else FontWeight.Medium,
                            color = if (selectedTab == "UNREAD") TextPrimary else TextSecondary
                        )
                    }
                }
            }

            if (isLoading && notifikasiList.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Primary, modifier = Modifier.size(36.dp))
                }
            } else if (displayedList.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .background(Primary0),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Lucide.Bell,
                                contentDescription = null,
                                tint = Primary,
                                modifier = Modifier.size(26.dp)
                            )
                        }
                        Text(
                            text = if (selectedTab == "UNREAD") "Tidak Ada Notifikasi Baru" else "Belum Ada Pemberitahuan",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = if (selectedTab == "UNREAD") "Semua pemberitahuan sudah Anda baca." else "Semua informasi dan pembaruan pengajuan pinjaman akan dikirimkan ke halaman ini.",
                            fontSize = 12.5.sp,
                            color = TextSecondary,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(displayedList, key = { it.id ?: it.hashCode().toString() }) { notif ->
                        NotificationPageCard(
                            item = notif,
                            onClick = {
                                notif.id?.let { viewModel.markNotificationAsRead(it) }
                                val loanId = notif.pengajuanPinjamanId
                                if (!loanId.isNullOrBlank()) {
                                    onNavigateToLoanDetail(loanId)
                                }
                            }
                        )
                    }
                }
            }
        }
    }

    // Confirmation Dialog for Mark All As Read
    ConfirmationDialog(
        visible = showMarkAllReadDialog,
        title = "Tandai Semua Dibaca?",
        message = "Semua pemberitahuan yang belum dibaca akan ditandai sebagai sudah dibaca.",
        type = DialogType.INFO,
        icon = Lucide.CheckCheck,
        confirmButtonText = "Ya, Tandai",
        dismissButtonText = "Batal",
        confirmButtonVariant = ButtonVariant.Primary,
        onConfirm = {
            showMarkAllReadDialog = false
            viewModel.markAllNotificationsAsRead()
        },
        onDismiss = {
            showMarkAllReadDialog = false
        }
    )
}

@Composable
private fun NotificationPageCard(
    item: NotifikasiItemDto,
    onClick: () -> Unit
) {
    val isRead = item.isNotificationRead
    val hasLoanLink = !item.pengajuanPinjamanId.isNullOrBlank()
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isRead) Surface else Surface
        ),
        border = BorderStroke(
            1.dp,
            if (isRead) Border else Primary.copy(alpha = 0.35f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(Primary0),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = when {
                        item.type?.contains("KYC", ignoreCase = true) == true || item.type?.contains("VERIFIKASI", ignoreCase = true) == true -> Lucide.CircleCheck
                        item.type?.contains("PENGAJUAN", ignoreCase = true) == true -> Lucide.Clock
                        item.type?.contains("PEMBAYARAN", ignoreCase = true) == true -> Lucide.CircleCheck
                        item.type?.contains("WELCOME", ignoreCase = true) == true -> Lucide.CircleCheck
                        else -> Lucide.Info
                    },
                    contentDescription = null,
                    tint = Primary,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = item.judul ?: "Pemberitahuan",
                        fontSize = 13.5.sp,
                        fontWeight = if (isRead) FontWeight.SemiBold else FontWeight.Bold,
                        color = TextPrimary,
                        modifier = Modifier.weight(1f)
                    )

                    if (!isRead) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .size(7.dp)
                                .clip(CircleShape)
                                .background(Primary)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = item.pesan ?: "",
                    fontSize = 12.sp,
                    color = TextSecondary,
                    lineHeight = 17.sp
                )

                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Lucide.Clock,
                            contentDescription = null,
                            tint = TextMuted,
                            modifier = Modifier.size(11.dp)
                        )
                        Text(
                            text = formatTime(item.createdDate),
                            fontSize = 10.5.sp,
                            color = TextMuted
                        )
                    }

                    if (hasLoanLink) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            Text(
                                text = "Lihat Detail",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Primary
                            )
                            Icon(
                                imageVector = Lucide.ChevronRight,
                                contentDescription = null,
                                tint = Primary,
                                modifier = Modifier.size(13.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

private fun formatTime(rawDate: String?): String {
    if (rawDate.isNullOrBlank()) return "Baru saja"
    return try {
        rawDate.replace("T", " ").take(16)
    } catch (e: Exception) {
        rawDate
    }
}
