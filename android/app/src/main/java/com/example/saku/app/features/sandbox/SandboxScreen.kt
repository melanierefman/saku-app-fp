package com.example.saku.app.features.sandbox

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.composables.icons.lucide.ArrowLeft
import com.composables.icons.lucide.ArrowRight
import com.composables.icons.lucide.Bell
import com.composables.icons.lucide.Briefcase
import com.composables.icons.lucide.Building
import com.composables.icons.lucide.Calendar
import com.composables.icons.lucide.Camera
import com.composables.icons.lucide.Check
import com.composables.icons.lucide.ChevronRight
import com.composables.icons.lucide.CircleAlert
import com.composables.icons.lucide.CircleCheck
import com.composables.icons.lucide.Clock
import com.composables.icons.lucide.CreditCard
import com.composables.icons.lucide.DollarSign
import com.composables.icons.lucide.Eye
import com.composables.icons.lucide.EyeOff
import com.composables.icons.lucide.FileText
import com.composables.icons.lucide.Heart
import com.composables.icons.lucide.House
import com.composables.icons.lucide.IdCard
import com.composables.icons.lucide.ImagePlus
import com.composables.icons.lucide.Info
import com.composables.icons.lucide.Key
import com.composables.icons.lucide.Landmark
import com.composables.icons.lucide.Lock
import com.composables.icons.lucide.LogOut
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Mail
import com.composables.icons.lucide.MapPin
import com.composables.icons.lucide.Palette
import com.composables.icons.lucide.Phone
import com.composables.icons.lucide.Plus
import com.composables.icons.lucide.Receipt
import com.composables.icons.lucide.RotateCw
import com.composables.icons.lucide.Search
import com.composables.icons.lucide.Send
import com.composables.icons.lucide.Settings
import com.composables.icons.lucide.ShieldCheck
import com.composables.icons.lucide.Sparkles
import com.composables.icons.lucide.Trash2
import com.composables.icons.lucide.TrendingUp
import com.composables.icons.lucide.User
import com.composables.icons.lucide.Users
import com.composables.icons.lucide.Wallet
import com.composables.icons.lucide.WifiOff
import com.example.saku.app.core.ui.components.Badge
import com.example.saku.app.core.ui.components.BadgeShape
import com.example.saku.app.core.ui.components.BadgeSize
import com.example.saku.app.core.ui.components.BadgeVariant
import com.example.saku.app.core.ui.components.BottomNavBar
import com.example.saku.app.core.ui.components.BottomNavItem
import com.example.saku.app.core.ui.components.Button
import com.example.saku.app.core.ui.components.ButtonShape
import com.example.saku.app.core.ui.components.ButtonSize
import com.example.saku.app.core.ui.components.ButtonVariant
import com.example.saku.app.core.ui.components.Checkbox
import com.example.saku.app.core.ui.components.CheckboxWithLabel
import com.example.saku.app.core.ui.components.ConfirmationDialog
import com.example.saku.app.core.ui.components.CurrencyField
import com.example.saku.app.core.ui.components.DialogType
import com.example.saku.app.core.ui.components.DocumentUploadCard
import com.example.saku.app.core.ui.components.DropdownField
import com.example.saku.app.core.ui.components.DropdownOption
import com.example.saku.app.core.ui.components.EmptyStateView
import com.example.saku.app.core.ui.components.ErrorCategory
import com.example.saku.app.core.ui.components.ErrorStateView
import com.example.saku.app.core.ui.components.HistoryGroupCard
import com.example.saku.app.core.ui.components.HistoryItem
import com.example.saku.app.core.ui.components.HistoryListItemCard
import com.example.saku.app.core.ui.components.HistoryType
import com.example.saku.app.core.ui.components.HorizontalTimeline
import com.example.saku.app.core.ui.components.IconButton
import com.example.saku.app.core.ui.components.IconButtonShape
import com.example.saku.app.core.ui.components.IconButtonSize
import com.example.saku.app.core.ui.components.IconButtonVariant
import com.example.saku.app.core.ui.components.InfoSummaryCard
import com.example.saku.app.core.ui.components.LoadingDialog
import com.example.saku.app.core.ui.components.LoanBreakdownItem
import com.example.saku.app.core.ui.components.LoanDetailCard
import com.example.saku.app.core.ui.components.MenuGroupCard
import com.example.saku.app.core.ui.components.MenuItemData
import com.example.saku.app.core.ui.components.MenuTrailingType
import com.example.saku.app.core.ui.components.NotificationCategory
import com.example.saku.app.core.ui.components.NotificationItem
import com.example.saku.app.core.ui.components.NotificationItemCard
import com.example.saku.app.core.ui.components.OtpInputField
import com.example.saku.app.core.ui.components.OtpResendSection
import com.example.saku.app.core.ui.components.PasswordField
import com.example.saku.app.core.ui.components.RadioButton
import com.example.saku.app.core.ui.components.RadioButtonWithLabel
import com.example.saku.app.core.ui.components.ResultDetailItem
import com.example.saku.app.core.ui.components.ResultStateView
import com.example.saku.app.core.ui.components.ResultType
import com.example.saku.app.core.ui.components.ShimmerCard
import com.example.saku.app.core.ui.components.ShimmerListItem
import com.example.saku.app.core.ui.components.StatTrendInfo
import com.example.saku.app.core.ui.components.StatusTimelineCard
import com.example.saku.app.core.ui.components.StepProgressBar
import com.example.saku.app.core.ui.components.SummaryCardVariant
import com.example.saku.app.core.ui.components.Switch
import com.example.saku.app.core.ui.components.SwitchWithLabel
import com.example.saku.app.core.ui.components.TabItem
import com.example.saku.app.core.ui.components.TabRow
import com.example.saku.app.core.ui.components.TabVariant
import com.example.saku.app.core.ui.components.TextField
import com.example.saku.app.core.ui.components.TimelineStepItem
import com.example.saku.app.core.ui.components.TimelineStepState
import com.example.saku.app.core.ui.components.TopBar
import com.example.saku.app.core.ui.components.UploadStatus
import com.example.saku.app.core.ui.components.formatRupiah
import com.example.saku.app.ui.theme.Background
import com.example.saku.app.ui.theme.Border
import com.example.saku.app.ui.theme.Error
import com.example.saku.app.ui.theme.Info
import com.example.saku.app.ui.theme.Neutral
import com.example.saku.app.ui.theme.Neutral0
import com.example.saku.app.ui.theme.Orange
import com.example.saku.app.ui.theme.Primary
import com.example.saku.app.ui.theme.Primary0
import com.example.saku.app.ui.theme.Primary10
import com.example.saku.app.ui.theme.Primary60
import com.example.saku.app.ui.theme.Primary90
import com.example.saku.app.ui.theme.Purple
import com.example.saku.app.ui.theme.Success
import com.example.saku.app.ui.theme.Surface
import com.example.saku.app.ui.theme.TextMuted
import com.example.saku.app.ui.theme.TextPrimary
import com.example.saku.app.ui.theme.TextSecondary
import com.example.saku.app.ui.theme.Typography
import com.example.saku.app.ui.theme.Warning
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

enum class SandboxTab(val title: String) {
    ALL("Semua"),
    BUTTON("Button"),
    INPUT("Form & Input"),
    SELECTION("Selection"),
    BADGE("Badge & Progress"),
    NAVIGATION("Navigation"),
    CARD("Fintech Cards"),
    FEEDBACK("Feedback & Dialogs"),
    TOKENS("Tokens & Icons")
}

@Composable
fun SandboxScreen(
    onNavigateBack: () -> Unit
) {
    var selectedTab by remember { mutableStateOf(SandboxTab.ALL) }
    var actionToastMsg by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            TopBar(
                title = "UI Sandbox (SAKU)",
                subtitle = "Design System & Reusable Components",
                onBackClick = onNavigateBack,
                actions = {
                    IconButton(
                        icon = Lucide.Info,
                        contentDescription = "Info",
                        onClick = {
                            actionToastMsg = "SAKU Design System: Semua komponen siap digunakan!"
                        },
                        variant = IconButtonVariant.Ghost
                    )
                }
            )
        },
        containerColor = Background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Filter Tabs Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                SandboxTab.entries.forEach { tab ->
                    FilterChip(
                        selected = selectedTab == tab,
                        onClick = { selectedTab = tab },
                        label = { Text(tab.title, maxLines = 1, softWrap = false) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Primary,
                            selectedLabelColor = Color.White,
                            containerColor = Surface,
                            labelColor = TextSecondary
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            borderColor = Border,
                            selectedBorderColor = Primary,
                            enabled = true,
                            selected = selectedTab == tab
                        )
                    )
                }
            }

            AnimatedVisibility(visible = actionToastMsg != null) {
                actionToastMsg?.let { msg ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 4.dp),
                        colors = CardDefaults.cardColors(containerColor = Primary0),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = msg, fontSize = 13.sp, color = Primary60, fontWeight = FontWeight.Medium)
                            Text(
                                text = "Tutup",
                                fontSize = 12.sp,
                                color = Primary90,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(Primary10)
                                    .clickable { actionToastMsg = null }
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }

            // Scrollable Content
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // 1. BUTTON & ICON BUTTON
                if (selectedTab == SandboxTab.ALL || selectedTab == SandboxTab.BUTTON) {
                    ButtonShowcaseSection(onToast = { actionToastMsg = it })
                }

                // 2. FORM & INPUT (Basic TextFields + Specialized: Currency, Dropdown, OTP)
                if (selectedTab == SandboxTab.ALL || selectedTab == SandboxTab.INPUT) {
                    InputShowcaseSection()
                    Phase2ShowcaseSection(onToast = { actionToastMsg = it })
                }

                // 3. SELECTION (Checkbox, Radio, Switch)
                if (selectedTab == SandboxTab.ALL || selectedTab == SandboxTab.SELECTION) {
                    SelectionShowcaseSection()
                }

                // 4. BADGE & STEP PROGRESS
                if (selectedTab == SandboxTab.ALL || selectedTab == SandboxTab.BADGE) {
                    BadgeShowcaseSection(onToast = { actionToastMsg = it })
                }

                // 5. NAVIGATION & TABS
                if (selectedTab == SandboxTab.ALL || selectedTab == SandboxTab.NAVIGATION) {
                    Phase3ShowcaseSection(onToast = { actionToastMsg = it })
                }

                // 6. FINTECH CARDS & DATA DISPLAY
                if (selectedTab == SandboxTab.ALL || selectedTab == SandboxTab.CARD) {
                    Phase4ShowcaseSection(onToast = { actionToastMsg = it })
                }

                // 7. FEEDBACK, DIALOGS & SYSTEM STATES
                if (selectedTab == SandboxTab.ALL || selectedTab == SandboxTab.FEEDBACK) {
                    Phase5ShowcaseSection(onToast = { actionToastMsg = it })
                }

                // 8. TOKENS & ICONS
                if (selectedTab == SandboxTab.ALL || selectedTab == SandboxTab.TOKENS) {
                    LucideIconsShowcaseSection()
                    DesignTokensSection()
                }

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

// 0. PHASE 5 FEEDBACK & SYSTEM STATES SHOWCASE SECTION (NEW)
@Composable
private fun Phase5ShowcaseSection(onToast: (String) -> Unit) {
    // 1. Result State
    var selectedResultType by remember { mutableStateOf(ResultType.SUCCESS) }

    // 2. Empty State
    var selectedEmptyCategory by remember { mutableStateOf("loans") }

    // 3. Error State
    var selectedErrorCategory by remember { mutableStateOf(ErrorCategory.NETWORK_ERROR) }
    var isRetrying by remember { mutableStateOf(false) }

    // 4. Modal Dialogs
    var showLoadingDialog by remember { mutableStateOf(false) }
    var showConfirmDialog by remember { mutableStateOf(false) }
    var confirmDialogType by remember { mutableStateOf(DialogType.WARNING) }
    var confirmDialogTitle by remember { mutableStateOf("Batalkan Pengajuan?") }
    var confirmDialogMessage by remember { mutableStateOf("Data pengajuan pinjaman yang sudah diisi akan hilang dan tidak dapat dikembalikan.") }

    // Confirmation Dialog
    ConfirmationDialog(
        visible = showConfirmDialog,
        title = confirmDialogTitle,
        message = confirmDialogMessage,
        type = confirmDialogType,
        onConfirm = {
            showConfirmDialog = false
            onToast("Aksi Dikonfirmasi!")
        },
        onDismiss = {
            showConfirmDialog = false
            onToast("Dibatalkan")
        }
    )

    // Loading Dialog
    LoadingDialog(
        visible = showLoadingDialog,
        message = "Memproses Pengajuan Pinjaman...",
        onDismissRequest = { showLoadingDialog = false }
    )

    ShowcaseCard(
        title = "Feedback, Dialogs & System States",
        subtitle = "ResultStateView (Success/Processing/Failed), EmptyStateView, ErrorStateView, Shimmer Skeletons, & Modal Dialogs"
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(22.dp)) {

            // 1. RESULT STATE VIEW PREVIEW
            Text(
                text = "1. ResultStateView (Hasil Transaksi & Pengajuan)",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = Primary
            )

            // Result Type Selector Chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(
                    Triple(ResultType.SUCCESS, "Sukses", Lucide.CircleCheck),
                    Triple(ResultType.PROCESSING, "Diproses", Lucide.Clock),
                    Triple(ResultType.FAILED, "Gagal", Lucide.CircleAlert)
                ).forEach { (type, label, icon) ->
                    FilterChip(
                        selected = selectedResultType == type,
                        onClick = { selectedResultType = type },
                        label = { Text(label, maxLines = 1, softWrap = false) },
                        leadingIcon = {
                            Icon(
                                imageVector = icon,
                                contentDescription = null,
                                modifier = Modifier.size(15.dp),
                                tint = if (selectedResultType == type) Color.White else Primary
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Primary,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Surface),
                border = BorderStroke(1.dp, Border)
            ) {
                when (selectedResultType) {
                    ResultType.SUCCESS -> {
                        ResultStateView(
                            title = "Pengajuan Pinjaman Berhasil",
                            description = "Pengajuan pinjaman kilat Rp 5.000.000 telah disetujui dan siap dicairkan.",
                            type = ResultType.SUCCESS,
                            amount = "Rp 5.000.000",
                            amountLabel = "Total Pencairan Bersih",
                            referenceId = "SK-TRX-8829103",
                            details = listOf(
                                ResultDetailItem("Rekening Tujuan", "Bank Transfer (•••• 8821)"),
                                ResultDetailItem("Tenor Pinjaman", "12 Bulan"),
                                ResultDetailItem("Angsuran per Bulan", "Rp 476.000 / bln", isHighlighted = true),
                                ResultDetailItem("Biaya Administrasi", "Gratis")
                            ),
                            primaryButtonText = "Lihat Status Pinjaman",
                            onPrimaryClick = { onToast("Buka halaman Status Pinjaman") },
                            secondaryButtonText = "Unduh Bukti PDF",
                            onSecondaryClick = { onToast("Mengunduh Bukti Transaksi PDF...") },
                            onCopyReference = { onToast("No. Referensi $it disalin ke clipboard!") }
                        )
                    }

                    ResultType.PROCESSING -> {
                        ResultStateView(
                            title = "Pembayaran Sedang Diproses",
                            description = "Sistem perbankan sedang memverifikasi transaksi pembayaran angsuran kamu.",
                            type = ResultType.PROCESSING,
                            amount = "Rp 476.000",
                            amountLabel = "Nominal Pembayaran Angsuran",
                            referenceId = "SK-PAY-9921004",
                            details = listOf(
                                ResultDetailItem("Metode Pembayaran", "SAKU Virtual Account"),
                                ResultDetailItem("Waktu Transaksi", "10 Sep 2026, 14:30 WIB"),
                                ResultDetailItem("Estimasi Selesai", "Maks. 5 - 10 Menit", isHighlighted = true)
                            ),
                            primaryButtonText = "Cek Status Berkala",
                            onPrimaryClick = { onToast("Memperbarui status transaksi...") },
                            secondaryButtonText = "Kembali ke Beranda",
                            onSecondaryClick = { onToast("Kembali ke Beranda") },
                            onCopyReference = { onToast("No. Referensi $it disalin!") }
                        )
                    }

                    ResultType.FAILED -> {
                        ResultStateView(
                            title = "Transaksi Tidak Berhasil",
                            description = "Saldo rekening asal tidak mencukupi atau batas waktu pembayaran telah kedaluwarsa.",
                            type = ResultType.FAILED,
                            amount = "Rp 476.000",
                            referenceId = "SK-ERR-0012948",
                            details = listOf(
                                ResultDetailItem("Kode Error", "INSUFFICIENT_FUNDS_402"),
                                ResultDetailItem("Waktu Transaksi", "10 Sep 2026, 14:35 WIB")
                            ),
                            primaryButtonText = "Ulangi Pembayaran",
                            onPrimaryClick = { onToast("Buka metode pembayaran ulang") },
                            secondaryButtonText = "Hubungi Bantuan CS",
                            onSecondaryClick = { onToast("Menghubungkan ke Customer Service SAKU...") },
                            onCopyReference = { onToast("No. Referensi $it disalin!") }
                        )
                    }
                }
            }

            HorizontalDivider(color = Border, thickness = 1.dp)

            // 2. EMPTY STATE VIEW PREVIEWS
            Text(
                text = "2. EmptyStateView (Ilustrasi Status Kosong)",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = Primary
            )

            // Empty Category Chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(
                    Triple("loans", "Riwayat Kosong", Lucide.Wallet),
                    Triple("search", "Pencarian Kosong", Lucide.Search),
                    Triple("notif", "Notifikasi Kosong", Lucide.Bell)
                ).forEach { (cat, label, icon) ->
                    FilterChip(
                        selected = selectedEmptyCategory == cat,
                        onClick = { selectedEmptyCategory = cat },
                        label = { Text(label, maxLines = 1, softWrap = false) },
                        leadingIcon = {
                            Icon(
                                imageVector = icon,
                                contentDescription = null,
                                modifier = Modifier.size(15.dp),
                                tint = if (selectedEmptyCategory == cat) Color.White else Primary
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Primary,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Surface),
                border = BorderStroke(1.dp, Border)
            ) {
                when (selectedEmptyCategory) {
                    "loans" -> {
                        EmptyStateView(
                            icon = Lucide.Wallet,
                            title = "Belum Ada Pinjaman Aktif",
                            description = "Dapatkan dana tunai kilat hingga Rp 25.000.000 dengan bunga super ringan dan proses 5 menit.",
                            actionButtonText = "Ajukan Pinjaman Sekarang",
                            onActionClick = { onToast("Buka formulir pengajuan pinjaman") }
                        )
                    }
                    "search" -> {
                        EmptyStateView(
                            icon = Lucide.Search,
                            title = "Hasil Pencarian Tidak Ditemukan",
                            description = "Tidak ada riwayat transaksi atau dokumen yang cocok dengan kata kunci pencarian kamu.",
                            actionButtonText = "Reset Pencarian",
                            onActionClick = { onToast("Pencarian di-reset") }
                        )
                    }
                    "notif" -> {
                        EmptyStateView(
                            icon = Lucide.Bell,
                            title = "Tidak Ada Notifikasi Baru",
                            description = "Semua update tagihan, promo eksklusif, dan info keamanan akun akan muncul di sini.",
                            actionButtonText = "Segarkan",
                            onActionClick = { onToast("Memeriksa notifikasi terbaru...") }
                        )
                    }
                }
            }

            HorizontalDivider(color = Border, thickness = 1.dp)

            // 3. ERROR STATE VIEW PREVIEWS
            Text(
                text = "3. ErrorStateView (Penanganan Kesalahan Jaringan & Sistem)",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = Primary
            )

            // Error Category Chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(
                    Triple(ErrorCategory.NETWORK_ERROR, "No Internet", Lucide.WifiOff),
                    Triple(ErrorCategory.SERVER_ERROR, "Server 503", Lucide.CircleAlert),
                    Triple(ErrorCategory.SECURITY_ERROR, "Sesi Berakhir", Lucide.Lock)
                ).forEach { (cat, label, icon) ->
                    FilterChip(
                        selected = selectedErrorCategory == cat,
                        onClick = { selectedErrorCategory = cat },
                        label = { Text(label, maxLines = 1, softWrap = false) },
                        leadingIcon = {
                            Icon(
                                imageVector = icon,
                                contentDescription = null,
                                modifier = Modifier.size(15.dp),
                                tint = if (selectedErrorCategory == cat) Color.White else Primary
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Primary,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Surface),
                border = BorderStroke(1.dp, Border)
            ) {
                when (selectedErrorCategory) {
                    ErrorCategory.NETWORK_ERROR -> {
                        ErrorStateView(
                            category = ErrorCategory.NETWORK_ERROR,
                            title = "Koneksi Internet Terputus",
                            description = "Pastikan perangkat terhubung ke jaringan Wi-Fi atau data seluler yang stabil lalu coba kembali.",
                            errorCode = "ERR_NO_INTERNET",
                            isRetrying = isRetrying,
                            onRetryClick = {
                                isRetrying = true
                                onToast("Memeriksa koneksi internet...")
                            },
                            helpButtonText = "Bantuan Koneksi",
                            onHelpClick = { onToast("Buka panduan pemecahan masalah koneksi") }
                        )
                    }

                    ErrorCategory.SERVER_ERROR -> {
                        ErrorStateView(
                            category = ErrorCategory.SERVER_ERROR,
                            title = "Layanan Sedang Dalam Pemeliharaan",
                            description = "Server SAKU sedang ditingkatkan untuk pengalaman yang lebih cepat. Mohon tunggu beberapa saat.",
                            errorCode = "HTTP 503 • SERVICE_UNAVAILABLE",
                            isRetrying = isRetrying,
                            onRetryClick = {
                                isRetrying = true
                                onToast("Menghubungi server kembali...")
                            },
                            helpButtonText = "Hubungi CS WhatsApp",
                            onHelpClick = { onToast("Buka CS SAKU") }
                        )
                    }

                    ErrorCategory.SECURITY_ERROR -> {
                        ErrorStateView(
                            category = ErrorCategory.SECURITY_ERROR,
                            title = "Sesi Akun Telah Berakhir",
                            description = "Demi alasan keamanan, sesi login kamu telah kedaluwarsa. Silakan masukkan PIN untuk masuk kembali.",
                            errorCode = "AUTH_TOKEN_EXPIRED",
                            retryButtonText = "Login Ulang",
                            onRetryClick = { onToast("Buka halaman Login / PIN") }
                        )
                    }

                    else -> {}
                }
            }

            HorizontalDivider(color = Border, thickness = 1.dp)

            // 4. SHIMMER SKELETON LOADERS
            Text(
                text = "4. Shimmer Skeleton Placeholder (Loading Konten API)",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = Primary
            )

            Text(
                text = "Skeleton Card Placeholder",
                fontSize = 12.5.sp,
                fontWeight = FontWeight.Medium,
                color = TextSecondary
            )

            ShimmerCard()

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Skeleton List Item Placeholder",
                fontSize = 12.5.sp,
                fontWeight = FontWeight.Medium,
                color = TextSecondary
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Surface),
                border = BorderStroke(1.dp, Border)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    ShimmerListItem()
                    HorizontalDivider(color = Border, thickness = 0.8.dp, modifier = Modifier.padding(vertical = 4.dp))
                    ShimmerListItem()
                }
            }

            HorizontalDivider(color = Border, thickness = 1.dp)

            // 5. MODAL DIALOGS & OVERLAYS
            Text(
                text = "5. Modal Dialogs & Fullscreen Loaders",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = Primary
            )

            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Button(
                    text = "Buka Loading Spinner Dialog",
                    onClick = {
                        showLoadingDialog = true
                    },
                    variant = ButtonVariant.Secondary,
                    size = ButtonSize.MD,
                    fullWidth = true
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        text = "Dialog Peringatan",
                        onClick = {
                            confirmDialogType = DialogType.WARNING
                            confirmDialogTitle = "Batalkan Pengajuan?"
                            confirmDialogMessage = "Formulir yang sudah kamu isi akan dihapus dan tidak dapat dipulihkan kembali."
                            showConfirmDialog = true
                        },
                        variant = ButtonVariant.Warning,
                        size = ButtonSize.SM,
                        modifier = Modifier.weight(1f)
                    )

                    Button(
                        text = "Dialog Hapus Akun",
                        onClick = {
                            confirmDialogType = DialogType.DESTRUCTIVE
                            confirmDialogTitle = "Hapus Dokumen KYC?"
                            confirmDialogMessage = "Foto E-KTP yang telah diunggah akan dihapus permanen dari sistem kami."
                            showConfirmDialog = true
                        },
                        variant = ButtonVariant.Error,
                        size = ButtonSize.SM,
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        text = "Dialog Konfirmasi",
                        onClick = {
                            confirmDialogType = DialogType.INFO
                            confirmDialogTitle = "Kirim Pengajuan Sekarang?"
                            confirmDialogMessage = "Pastikan nominal pinjaman Rp 5.000.000 dan rekening bank tujuan sudah sesuai."
                            showConfirmDialog = true
                        },
                        variant = ButtonVariant.Primary,
                        size = ButtonSize.SM,
                        modifier = Modifier.weight(1f)
                    )

                    Button(
                        text = "Dialog Sukses",
                        onClick = {
                            confirmDialogType = DialogType.SUCCESS
                            confirmDialogTitle = "Verifikasi PIN Berhasil"
                            confirmDialogMessage = "PIN transaksi kamu telah berhasil diperbarui dan siap digunakan."
                            showConfirmDialog = true
                        },
                        variant = ButtonVariant.Success,
                        size = ButtonSize.SM,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

// 0. PHASE 4 FINTECH CARDS & DATA DISPLAY SHOWCASE SECTION
@Composable
private fun Phase4ShowcaseSection(onToast: (String) -> Unit) {
    // State for interactive Document Upload demo
    var ktpUploadStatus by remember { mutableStateOf(UploadStatus.UPLOADED) }
    var npwpUploadStatus by remember { mutableStateOf(UploadStatus.EMPTY) }
    var slipGajiUploadStatus by remember { mutableStateOf(UploadStatus.UPLOADING) }
    var uploadProgress by remember { mutableFloatStateOf(0.65f) }

    // State for Notification demo
    val notifications = remember {
        mutableStateListOf(
            NotificationItem(
                id = "1",
                title = "Pengajuan Pinjaman Disetujui",
                message = "Pinjaman Kilat Rp 5.000.000 telah disetujui. Dana siap dicairkan ke rekening bank kamu.",
                timestamp = "10 menit yang lalu",
                category = NotificationCategory.TRANSACTION,
                isRead = false,
                actionLabel = "Cairkan Sekarang"
            ),
            NotificationItem(
                id = "2",
                title = "Promo Bunga Spesial 0.5%",
                message = "Gunakan kode promo SAKUMERDEKA untuk menikmati bunga pinjaman super ringan bulan ini.",
                timestamp = "2 jam yang lalu",
                category = NotificationCategory.PROMO,
                isRead = false
            ),
            NotificationItem(
                id = "3",
                title = "Pengingat Jatuh Tempo",
                message = "Angsuran ke-3 sebesar Rp 476.000 akan jatuh tempo pada 25 September 2026.",
                timestamp = "Kemarin, 10:30",
                category = NotificationCategory.REMINDER,
                isRead = true,
                actionLabel = "Bayar Sekarang"
            ),
            NotificationItem(
                id = "4",
                title = "Verifikasi Keamanan Berhasil",
                message = "Perangkat baru Xiaomi 14 berhasil login ke akun SAKU kamu.",
                timestamp = "2 hari yang lalu",
                category = NotificationCategory.SECURITY,
                isRead = true
            )
        )
    }

    // State for Menu Switches
    var biometricEnabled by remember { mutableStateOf(true) }
    var pushNotificationEnabled by remember { mutableStateOf(true) }

    // Timeline Steps Data
    val loanTrackingSteps = remember {
        listOf(
            TimelineStepItem(
                title = "Pengajuan Terkirim",
                description = "Formulir pinjaman Rp 5.000.000 berhasil dikirim",
                timestamp = "10 Sep, 09:15",
                state = TimelineStepState.COMPLETED
            ),
            TimelineStepItem(
                title = "Verifikasi Dokumen KTP",
                description = "Dokumen identitas valid dan terverifikasi otomatis oleh sistem Dukcapil",
                timestamp = "10 Sep, 09:20",
                state = TimelineStepState.COMPLETED
            ),
            TimelineStepItem(
                title = "Analisis & Approval Kredit",
                description = "Sedang dalam tahap evaluasi risiko dan penentuan limit pinjaman akhir",
                timestamp = "10 Sep, 09:45",
                state = TimelineStepState.ACTIVE,
                remark = "Estimasi proses verifikasi analis: 10 - 30 Menit"
            ),
            TimelineStepItem(
                title = "Pencairan ke Rekening Bank",
                description = "Dana akan otomatis ditransfer ke rekening bank terdaftar",
                state = TimelineStepState.PENDING
            )
        )
    }

    val horizontalSteps = remember {
        listOf(
            TimelineStepItem("Formulir", state = TimelineStepState.COMPLETED),
            TimelineStepItem("Dokumen", state = TimelineStepState.COMPLETED),
            TimelineStepItem("Verifikasi", state = TimelineStepState.ACTIVE),
            TimelineStepItem("Pencairan", state = TimelineStepState.PENDING)
        )
    }

    val historyItems = remember {
        listOf(
            HistoryItem(
                id = "TRX-001",
                title = "Pencairan Pinjaman Kilat",
                subtitle = "Rekening Bank (•••• 8821) • 10 Sep 2026, 09:30",
                date = "10 Sep 2026",
                amount = "Rp 5.000.000",
                isIncome = true,
                statusText = "Berhasil",
                statusVariant = BadgeVariant.Success,
                type = HistoryType.DISBURSEMENT
            ),
            HistoryItem(
                id = "TRX-002",
                title = "Pembayaran Angsuran ke-2",
                subtitle = "SAKU Virtual Account • 08 Sep 2026, 14:15",
                date = "08 Sep 2026",
                amount = "Rp 476.000",
                isIncome = false,
                statusText = "Berhasil",
                statusVariant = BadgeVariant.Success,
                type = HistoryType.PAYMENT
            ),
            HistoryItem(
                id = "TRX-003",
                title = "Top Up SAKU Saldo",
                subtitle = "QRIS Instant • 05 Sep 2026, 11:20",
                date = "05 Sep 2026",
                amount = "Rp 250.000",
                isIncome = true,
                statusText = "Berhasil",
                statusVariant = BadgeVariant.Success,
                type = HistoryType.TOPUP
            ),
            HistoryItem(
                id = "TRX-004",
                title = "Biaya Layanan & Administrasi",
                subtitle = "Sistem SAKU • 01 Sep 2026, 00:00",
                date = "01 Sep 2026",
                amount = "Rp 15.000",
                isIncome = false,
                statusText = "Berhasil",
                statusVariant = BadgeVariant.Neutral,
                type = HistoryType.FEE
            )
        )
    }

    ShowcaseCard(
        title = "Fintech Cards & Data Display",
        subtitle = "InfoSummaryCard (Gradient & Metrics), LoanDetailCard, DocumentUploadCard, StatusTimeline, History & Notification Cards"
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(22.dp)) {

            // 1. STAT SUMMARY & HERO GRADIENT CARDS
            Text(
                text = "1. InfoSummaryCard (Hero Gradient & Metric Cards)",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = Primary
            )

            // Hero Gradient Card
            InfoSummaryCard(
                title = "Total Limit Pinjaman Tersedia",
                value = "Rp 25.000.000",
                subtitle = "Limit aktif dapat dicairkan kapan saja ke rekening bank",
                variant = SummaryCardVariant.GradientPrimary,
                icon = Lucide.Wallet,
                actionButtonText = "Tarik Dana Cepat",
                onActionClick = { onToast("Klik Tarik Dana Cepat") }
            )

            // 2-Column Metric Cards
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                InfoSummaryCard(
                    title = "Pinjaman Aktif",
                    value = "Rp 8.500.000",
                    variant = SummaryCardVariant.Primary,
                    icon = Lucide.TrendingUp,
                    badgeText = "1 Aktif",
                    badgeVariant = BadgeVariant.Primary,
                    modifier = Modifier.weight(1f),
                    onClick = { onToast("Detail Pinjaman Aktif") }
                )

                InfoSummaryCard(
                    title = "Sisa Tagihan",
                    value = "Rp 476.000",
                    variant = SummaryCardVariant.Success,
                    icon = Lucide.CreditCard,
                    trend = StatTrendInfo("+12.5% Lunas", isPositive = true),
                    modifier = Modifier.weight(1f),
                    onClick = { onToast("Detail Sisa Tagihan") }
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                InfoSummaryCard(
                    title = "Jatuh Tempo",
                    value = "25 Sep 2026",
                    subtitle = "5 hari tersisa",
                    variant = SummaryCardVariant.Warning,
                    icon = Lucide.Calendar,
                    modifier = Modifier.weight(1f),
                    onClick = { onToast("Pengingat Jatuh Tempo") }
                )

                InfoSummaryCard(
                    title = "Skor Kredit",
                    value = "780 / 850",
                    variant = SummaryCardVariant.Info,
                    icon = Lucide.ShieldCheck,
                    badgeText = "Sangat Baik",
                    badgeVariant = BadgeVariant.Success,
                    modifier = Modifier.weight(1f),
                    onClick = { onToast("Detail Skor Kredit") }
                )
            }

            HorizontalDivider(color = Border, thickness = 1.dp)

            // 2. LOAN DETAIL CARD
            Text(
                text = "2. LoanDetailCard (Rincian Pinjaman & Aksi Pembayaran)",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = Primary
            )

            LoanDetailCard(
                loanId = "Pinjaman Kilat #SK-9921",
                date = "Diajukan pada 10 Sep 2026",
                loanAmount = "Rp 5.000.000",
                statusText = "Sedang Berjalan",
                statusVariant = BadgeVariant.Success,
                tenor = "12 Bulan",
                monthlyInstallment = "Rp 476.000 / bln",
                dueDate = "25 Sep 2026",
                breakdownItems = listOf(
                    LoanBreakdownItem("Pokok Pinjaman", "Rp 5.000.000"),
                    LoanBreakdownItem("Bunga Pinjaman (0.8%)", "Rp 40.000 / bln"),
                    LoanBreakdownItem("Biaya Layanan & Asuransi", "Gratis")
                ),
                primaryButtonText = "Bayar Tagihan",
                onPrimaryClick = { onToast("Buka halaman Pembayaran Tagihan") },
                secondaryButtonText = "Jadwal Angsuran",
                onSecondaryClick = { onToast("Lihat Rincian Jadwal Angsuran") }
            )

            HorizontalDivider(color = Border, thickness = 1.dp)

            // 3. DOCUMENT UPLOAD CARD
            Text(
                text = "3. DocumentUploadCard (Upload Dokumen KYC & Verifikasi)",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = Primary
            )

            // KTP - Uploaded State
            DocumentUploadCard(
                title = "Foto E-KTP Asli",
                description = "Format JPG atau PNG (Maks 5MB)",
                status = ktpUploadStatus,
                icon = Lucide.IdCard,
                fileName = "e-ktp_melanie_verified.jpg",
                fileSize = "2.4 MB",
                statusBadgeText = "Terverifikasi",
                statusBadgeVariant = BadgeVariant.Success,
                onUploadClick = {
                    ktpUploadStatus = UploadStatus.UPLOADED
                    onToast("Unggah ulang E-KTP")
                },
                onPreviewClick = { onToast("Pratinjau Foto E-KTP") },
                onDeleteClick = {
                    ktpUploadStatus = UploadStatus.EMPTY
                    onToast("E-KTP dihapus")
                }
            )

            // Slip Gaji - Uploading State
            DocumentUploadCard(
                title = "Slip Gaji / Bukti Penghasilan",
                description = "Format PDF, JPG, atau PNG (Maks 5MB)",
                status = slipGajiUploadStatus,
                icon = Lucide.FileText,
                fileName = "slip_gaji_agustus_2026.pdf",
                fileSize = "1.8 MB",
                uploadProgress = uploadProgress,
                onUploadClick = {
                    slipGajiUploadStatus = UploadStatus.UPLOADED
                    onToast("Slip gaji berhasil diunggah!")
                },
                onDeleteClick = {
                    slipGajiUploadStatus = UploadStatus.EMPTY
                    onToast("Upload slip gaji dibatalkan")
                }
            )

            // NPWP - Empty State
            DocumentUploadCard(
                title = "NPWP / Bukti Pajak (Opsional)",
                description = "Tingkatkan limit pinjaman hingga Rp 50 Juta",
                status = npwpUploadStatus,
                icon = Lucide.ImagePlus,
                onUploadClick = {
                    npwpUploadStatus = UploadStatus.UPLOADED
                    onToast("NPWP berhasil dipilih & diunggah!")
                }
            )

            // Error Demo Card
            DocumentUploadCard(
                title = "Foto Selfie dengan KTP",
                description = "Wajah dan KTP harus terlihat jelas dan tidak buram",
                status = UploadStatus.ERROR,
                icon = Lucide.Camera,
                fileName = "selfie_ktp_blur.jpg",
                errorMessage = "Foto terlalu gelap atau buram. Silakan ambil ulang dengan pencahayaan cukup.",
                onUploadClick = { onToast("Ambil ulang foto selfie") },
                onDeleteClick = { onToast("Hapus foto") }
            )

            HorizontalDivider(color = Border, thickness = 1.dp)

            // 4. STATUS TIMELINE CARD
            Text(
                text = "4. StatusTimelineCard & Horizontal Step Tracker",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = Primary
            )

            // Horizontal Step Tracker preview
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Surface),
                border = androidx.compose.foundation.BorderStroke(1.dp, Border)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Ringkasan Tahapan Pengajuan",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    HorizontalTimeline(steps = horizontalSteps)
                }
            }

            // Full Vertical Timeline Card
            StatusTimelineCard(
                title = "Status Pengajuan Pinjaman",
                referenceId = "No. Pengajuan: #SK-8829-JKT",
                statusBadgeText = "Dalam Proses",
                statusBadgeVariant = BadgeVariant.Warning,
                steps = loanTrackingSteps,
                actionButtonText = "Hubungi Customer Service",
                onActionClick = { onToast("Hubungi CS SAKU via WhatsApp/Live Chat") }
            )

            HorizontalDivider(color = Border, thickness = 1.dp)

            // 5. TRANSACTION HISTORY LIST CARDS
            Text(
                text = "5. HistoryListItemCard & HistoryGroupCard",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = Primary
            )

            HistoryGroupCard(
                title = "Riwayat Transaksi Terakhir",
                items = historyItems,
                onItemClick = { item ->
                    onToast("Detail Transaksi: ${item.title} (${item.amount})")
                }
            )

            HorizontalDivider(color = Border, thickness = 1.dp)

            // 6. NOTIFICATION ITEM CARDS
            Text(
                text = "6. NotificationItemCard (Notifikasi & Pengingat)",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = Primary
            )

            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                notifications.forEach { item ->
                    NotificationItemCard(
                        item = item,
                        onClick = {
                            val index = notifications.indexOf(item)
                            if (index >= 0) {
                                notifications[index] = item.copy(isRead = true)
                            }
                            onToast("Dibaca: ${item.title}")
                        },
                        onActionClick = {
                            onToast("Aksi: ${item.actionLabel ?: "Detail"}")
                        }
                    )
                }
            }

            HorizontalDivider(color = Border, thickness = 1.dp)

            // 7. MENU & SETTINGS LIST ITEM CARDS
            Text(
                text = "7. MenuListItemCard & MenuGroupCard (Profil & Pengaturan)",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = Primary
            )

            MenuGroupCard(
                headerTitle = "PENGATURAN AKUN & KEAMANAN",
                items = listOf(
                    MenuItemData(
                        id = "profile",
                        title = "Informasi Profil & Kontak",
                        subtitle = "Nama, No. HP, dan Email terdaftar",
                        icon = Lucide.User,
                        trailingType = MenuTrailingType.Value,
                        valueText = "melanie@saku.id"
                    ),
                    MenuItemData(
                        id = "kyc",
                        title = "Status Verifikasi Identitas (KYC)",
                        subtitle = "E-KTP dan data kependudukan",
                        icon = Lucide.ShieldCheck,
                        trailingType = MenuTrailingType.Badge,
                        badgeText = "Terverifikasi",
                        badgeVariant = BadgeVariant.Success
                    ),
                    MenuItemData(
                        id = "biometric",
                        title = "Login Biometrik / Sidik Jari",
                        subtitle = "Masuk cepat tanpa memasukkan PIN",
                        icon = Lucide.Lock,
                        trailingType = MenuTrailingType.Switch,
                        switchChecked = biometricEnabled,
                        onSwitchChange = {
                            biometricEnabled = it
                            onToast("Biometrik: ${if (it) "Aktif" else "Nonaktif"}")
                        }
                    ),
                    MenuItemData(
                        id = "notification",
                        title = "Notifikasi & Pengingat Tagihan",
                        subtitle = "Push notifikasi jatuh tempo pinjaman",
                        icon = Lucide.Bell,
                        trailingType = MenuTrailingType.Switch,
                        switchChecked = pushNotificationEnabled,
                        onSwitchChange = {
                            pushNotificationEnabled = it
                            onToast("Notifikasi: ${if (it) "Aktif" else "Nonaktif"}")
                        }
                    ),
                    MenuItemData(
                        id = "logout",
                        title = "Keluar dari Akun",
                        subtitle = "Sesi akun di perangkat ini akan diakhiri",
                        icon = Lucide.LogOut,
                        trailingType = MenuTrailingType.Chevron,
                        isDestructive = true
                    )
                ),
                onItemClick = { item ->
                    onToast("Menu dipilih: ${item.title}")
                }
            )
        }
    }
}

// 0. PHASE 3 NAVIGATION & TABS SHOWCASE SECTION
@Composable
private fun Phase3ShowcaseSection(onToast: (String) -> Unit) {
    // Bottom Nav State
    var activeNavRoute by remember { mutableStateOf("home") }
    val navItems = remember {
        listOf(
            BottomNavItem("home", "Beranda", Lucide.House),
            BottomNavItem("loan", "Pinjaman", Lucide.Wallet),
            BottomNavItem("apply", "Ajukan", Lucide.Plus, isCenterAction = true),
            BottomNavItem("history", "Riwayat", Lucide.Receipt),
            BottomNavItem("profile", "Profil", Lucide.User, badgeCount = 2)
        )
    }

    // TabRow State
    var selectedSegmentedKey by remember { mutableStateOf("all") }
    var selectedUnderlineKey by remember { mutableStateOf("active") }
    var selectedPillKey by remember { mutableStateOf("personal") }
    var selectedFolderKey by remember { mutableStateOf("step1") }

    val loanHistoryTabs = remember {
        listOf(
            TabItem("all", "Semua", count = 18),
            TabItem("pending", "Menunggu", count = 3),
            TabItem("approved", "Disetujui", count = 12),
            TabItem("rejected", "Ditolak", count = 3)
        )
    }

    val loanCategoryTabs = remember {
        listOf(
            TabItem("active", "Pinjaman Aktif", count = 2, icon = Lucide.TrendingUp),
            TabItem("history", "Riwayat Pelunasan", count = 10, icon = Lucide.Receipt),
            TabItem("simulation", "Simulasi Kredit", icon = Lucide.CreditCard)
        )
    }

    val loanTypePills = remember {
        listOf(
            TabItem("personal", "Pinjaman Personal", count = 5),
            TabItem("umkm", "Modal Usaha UMKM", count = 2),
            TabItem("paylater", "SAKU Paylater"),
            TabItem("promo", "Promo Khusus", badge = "Diskon")
        )
    }

    val folderTabs = remember {
        listOf(
            TabItem("step1", "Dokumen KTP"),
            TabItem("step2", "Slip Gaji"),
            TabItem("step3", "Rekening Koran")
        )
    }

    ShowcaseCard(
        title = "Navigation & Containers",
        subtitle = "BottomNavBar dengan Action Center, TabRow (Segmented, Underline, Pill, Folder), & TopBar Enhanced"
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {

            // 1. Bottom Navigation Bar Preview
            Text(
                text = "1. BottomNavBar (Menu Navigasi Bawah Fintech)",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = Primary
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Background),
                border = androidx.compose.foundation.BorderStroke(1.dp, Border)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = "Rute Aktif: ${navItems.find { it.route == activeNavRoute }?.title}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = Primary60,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    BottomNavBar(
                        items = navItems,
                        currentRoute = activeNavRoute,
                        onItemClick = { item ->
                            activeNavRoute = item.route
                            onToast("Navigasi ke: ${item.title}")
                        }
                    )
                }
            }

            HorizontalDivider(color = Border, thickness = 1.dp)

            // 2. TabRow - Segmented Variant
            Text(
                text = "2. TabRow - Varian Segmented (Kontainer Rounded)",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = Primary
            )

            TabRow(
                items = loanHistoryTabs,
                selectedKey = selectedSegmentedKey,
                onTabSelected = { item ->
                    selectedSegmentedKey = item.key
                    onToast("Tab Segmented: ${item.label}")
                },
                variant = TabVariant.Segmented,
                isScrollable = true
            )

            HorizontalDivider(color = Border, thickness = 1.dp)

            // 3. TabRow - Underline Variant
            Text(
                text = "3. TabRow - Varian Underline (Garis Indikator Oranye)",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = Primary
            )

            TabRow(
                items = loanCategoryTabs,
                selectedKey = selectedUnderlineKey,
                onTabSelected = { item ->
                    selectedUnderlineKey = item.key
                    onToast("Tab Underline: ${item.label}")
                },
                variant = TabVariant.Underline,
                isScrollable = true
            )

            HorizontalDivider(color = Border, thickness = 1.dp)

            // 4. TabRow - Pill Variant
            Text(
                text = "4. TabRow - Varian Pill (Chip Kapsul Mandiri)",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = Primary
            )

            TabRow(
                items = loanTypePills,
                selectedKey = selectedPillKey,
                onTabSelected = { item ->
                    selectedPillKey = item.key
                    onToast("Tab Pill: ${item.label}")
                },
                variant = TabVariant.Pill,
                isScrollable = true
            )

            HorizontalDivider(color = Border, thickness = 1.dp)

            // 5. TabRow - Folder Variant
            Text(
                text = "5. TabRow - Varian Folder (Rounded-Top Headers)",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = Primary
            )

            TabRow(
                items = folderTabs,
                selectedKey = selectedFolderKey,
                onTabSelected = { item ->
                    selectedFolderKey = item.key
                    onToast("Tab Folder: ${item.label}")
                },
                variant = TabVariant.Folder,
                isScrollable = true
            )

            HorizontalDivider(color = Border, thickness = 1.dp)

            // 6. TopBar Enhanced (Title + Subtitle + Action Slots)
            Text(
                text = "6. TopBar Enhanced (Title, Subtitle & Action Slots)",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = Primary
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Background),
                border = androidx.compose.foundation.BorderStroke(1.dp, Border)
            ) {
                TopBar(
                    title = "Detail Pengajuan Pinjaman",
                    subtitle = "ID Pengajuan #SAKU-2026-891",
                    onBackClick = { onToast("Kembali ke Dashboard") },
                    actions = {
                        IconButton(
                            icon = Lucide.Bell,
                            contentDescription = "Notifikasi",
                            onClick = { onToast("Notifikasi TopBar diklik") },
                            variant = IconButtonVariant.Ghost
                        )
                        IconButton(
                            icon = Lucide.Settings,
                            contentDescription = "Pengaturan",
                            onClick = { onToast("Pengaturan TopBar diklik") },
                            variant = IconButtonVariant.Ghost
                        )
                    }
                )
            }
        }
    }
}

// PHASE 2 SPECIALIZED SHOWCASE SECTION
@Composable
private fun Phase2ShowcaseSection(onToast: (String) -> Unit) {
    var loanAmount by remember { mutableStateOf<Long?>(5_000_000L) }

    val tenorOptions = remember {
        listOf(
            DropdownOption("3", "3 Bulan", "Cicilan Rp 1.760.000/bln", badge = "Populer", leadingIcon = Lucide.Calendar),
            DropdownOption("6", "6 Bulan", "Cicilan Rp 920.000/bln", leadingIcon = Lucide.Calendar),
            DropdownOption("12", "12 Bulan", "Cicilan Rp 490.000/bln", badge = "Bunga Rendah", leadingIcon = Lucide.Calendar),
            DropdownOption("24", "24 Bulan", "Cicilan Rp 280.000/bln", leadingIcon = Lucide.Calendar)
        )
    }
    var selectedTenor by remember { mutableStateOf<DropdownOption?>(tenorOptions[2]) }

    val provinceOptions = remember {
        listOf(
            DropdownOption("dki", "DKI Jakarta", "Jakarta Pusat, Barat, Selatan, Timur, Utara", leadingIcon = Lucide.Building),
            DropdownOption("jabar", "Jawa Barat", "Bandung, Bekasi, Bogor, Depok, dll", leadingIcon = Lucide.MapPin),
            DropdownOption("jateng", "Jawa Tengah", "Semarang, Solo, Magelang, dll", leadingIcon = Lucide.MapPin),
            DropdownOption("jatim", "Jawa Timur", "Surabaya, Malang, Sidoarjo, dll", leadingIcon = Lucide.MapPin),
            DropdownOption("banten", "Banten", "Tangerang, Serang, Cilegon, dll", leadingIcon = Lucide.MapPin),
            DropdownOption("bali", "Bali", "Denpasar, Badung, Gianyar, dll", leadingIcon = Lucide.MapPin)
        )
    }
    var selectedProvince by remember { mutableStateOf<DropdownOption?>(provinceOptions[0]) }

    var otpCode by remember { mutableStateOf("123") }
    var isPinMasked by remember { mutableStateOf(false) }
    var countdownSecs by remember { mutableIntStateOf(45) }

    var currentStep by remember { mutableIntStateOf(2) }
    val stepTitles = listOf("Data Diri", "Informasi Pekerjaan", "Upload Dokumen", "Konfirmasi")

    ShowcaseCard(
        title = "Specialized Inputs & Progress",
        subtitle = "CurrencyField, DropdownField (Bottom Sheet), OtpInputField & StepProgressBar"
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {

            // 1. Step Progress Bar
            Text(
                text = "1. StepProgressBar (Alur Pengajuan & Registrasi)",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = Primary
            )

            StepProgressBar(
                currentStep = currentStep,
                totalSteps = 4,
                stepTitle = stepTitles[currentStep - 1],
                stepLabels = stepTitles
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    text = "Langkah Sebelumnya",
                    onClick = { if (currentStep > 1) currentStep-- },
                    enabled = currentStep > 1,
                    variant = ButtonVariant.Outline,
                    size = ButtonSize.SM,
                    modifier = Modifier.weight(1f)
                )
                Button(
                    text = if (currentStep == 4) "Selesai" else "Langkah Selanjutnya",
                    onClick = { if (currentStep < 4) currentStep++ else onToast("Alur 4 Langkah Selesai!") },
                    variant = ButtonVariant.Primary,
                    size = ButtonSize.SM,
                    modifier = Modifier.weight(1f)
                )
            }

            HorizontalDivider(color = Border, thickness = 1.dp)

            // 2. Currency Field
            Text(
                text = "2. CurrencyField (Auto Format Rupiah & Quick Add)",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = Primary
            )

            CurrencyField(
                amount = loanAmount,
                onAmountChange = { loanAmount = it },
                label = "Nominal Pinjaman Diajukan",
                required = true,
                maxAmount = 20_000_000L,
                helperText = "Nominal terpilih: ${loanAmount?.let { formatRupiah(it) } ?: "Rp 0"}"
            )

            HorizontalDivider(color = Border, thickness = 1.dp)

            // 3. Dropdown Fields
            Text(
                text = "3. DropdownField (Searchable Bottom Sheet)",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = Primary
            )

            DropdownField(
                options = tenorOptions,
                selectedOption = selectedTenor,
                onOptionSelect = { selectedTenor = it },
                label = "Pilih Tenor Pinjaman",
                required = true,
                leadingIcon = Lucide.Calendar
            )

            DropdownField(
                options = provinceOptions,
                selectedOption = selectedProvince,
                onOptionSelect = { selectedProvince = it },
                label = "Provinsi Domisili",
                placeholder = "Cari provinsi...",
                required = true,
                searchable = true,
                clearable = true,
                leadingIcon = Lucide.MapPin
            )

            HorizontalDivider(color = Border, thickness = 1.dp)

            // 4. OTP / PIN Input Field
            Text(
                text = "4. OtpInputField (Verifikasi Kode 6-Digit)",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = Primary
            )

            SwitchWithLabel(
                checked = isPinMasked,
                onCheckedChange = { isPinMasked = it },
                label = "Samarkan Angka (PIN Masking)",
                description = "Ubah tampilan angka menjadi titik bulat keamanan"
            )

            OtpInputField(
                otpValue = otpCode,
                onOtpChange = { otpCode = it },
                otpLength = 6,
                isMasked = isPinMasked,
                onOtpComplete = { completedCode ->
                    onToast("Kode OTP Terisi Lengkap: $completedCode")
                }
            )

            OtpResendSection(
                countdownSeconds = countdownSecs,
                onResendClick = {
                    countdownSecs = 60
                    onToast("Kode OTP baru telah dikirim ke WhatsApp!")
                }
            )
        }
    }
}

// 1. BUTTON & ICON BUTTON SHOWCASE
@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ButtonShowcaseSection(onToast: (String) -> Unit) {
    var buttonClickCount by remember { mutableIntStateOf(0) }
    var selectedVariant by remember { mutableStateOf(ButtonVariant.Primary) }
    var selectedSize by remember { mutableStateOf(ButtonSize.LG) }
    var selectedShape by remember { mutableStateOf(ButtonShape.Rounded) }
    var isInteractiveLoading by remember { mutableStateOf(false) }
    var isInteractiveEnabled by remember { mutableStateOf(true) }
    var hasInteractiveLeadingIcon by remember { mutableStateOf(true) }
    var hasInteractiveTrailingIcon by remember { mutableStateOf(false) }
    var customButtonText by remember { mutableStateOf("Lanjutkan Pengajuan") }

    ShowcaseCard(
        title = "Button & IconButton",
        subtitle = "Tombol aksi utama, sekunder, outline, icon-only dengan variasi ukuran & status"
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {

            Text(
                text = "Koleksi Varian Button (1:1 Frontend)",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary
            )

            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Button(
                    text = "Primary Button",
                    onClick = { onToast("Primary Button diklik") },
                    variant = ButtonVariant.Primary,
                    leadingIcon = Lucide.Send
                )

                Button(
                    text = "Secondary Button",
                    onClick = { onToast("Secondary Button diklik") },
                    variant = ButtonVariant.Secondary,
                    leadingIcon = Lucide.Plus
                )

                Button(
                    text = "White Button",
                    onClick = { onToast("White Button diklik") },
                    variant = ButtonVariant.White,
                    leadingIcon = Lucide.Sparkles
                )

                Button(
                    text = "Neutral Button",
                    onClick = { onToast("Neutral Button diklik") },
                    variant = ButtonVariant.Neutral,
                    leadingIcon = Lucide.Wallet
                )

                Button(
                    text = "Outline Button",
                    onClick = { onToast("Outline Button diklik") },
                    variant = ButtonVariant.Outline,
                    leadingIcon = Lucide.Heart
                )

                Button(
                    text = "Cancel Button",
                    onClick = { onToast("Cancel Button diklik") },
                    variant = ButtonVariant.Cancel,
                    leadingIcon = Lucide.RotateCw
                )

                Button(
                    text = "Success Button",
                    onClick = { onToast("Success Button diklik") },
                    variant = ButtonVariant.Success,
                    leadingIcon = Lucide.CircleCheck
                )

                Button(
                    text = "Warning Button",
                    onClick = { onToast("Warning Button diklik") },
                    variant = ButtonVariant.Warning,
                    leadingIcon = Lucide.CircleAlert
                )

                Button(
                    text = "Error / Delete Button",
                    onClick = { onToast("Error Button diklik") },
                    variant = ButtonVariant.Error,
                    leadingIcon = Lucide.Trash2
                )

                Button(
                    text = "Ghost Button (Text Only)",
                    onClick = { onToast("Ghost Button diklik") },
                    variant = ButtonVariant.Ghost,
                    leadingIcon = Lucide.ChevronRight
                )
            }

            HorizontalDivider(color = Border, thickness = 1.dp)

            Text(
                text = "Ukuran Button (LG: 52dp, MD: 44dp, SM: 36dp)",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    text = "Large (52dp)",
                    onClick = { onToast("Button LG diklik") },
                    size = ButtonSize.LG,
                    modifier = Modifier.weight(1f)
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    text = "Medium (44dp)",
                    onClick = { onToast("Button MD diklik") },
                    size = ButtonSize.MD,
                    variant = ButtonVariant.Secondary,
                    modifier = Modifier.weight(1f)
                )
                Button(
                    text = "Small (36dp)",
                    onClick = { onToast("Button SM diklik") },
                    size = ButtonSize.SM,
                    variant = ButtonVariant.Outline,
                    modifier = Modifier.weight(1f)
                )
            }

            HorizontalDivider(color = Border, thickness = 1.dp)

            Text(
                text = "IconButton (Action Buttons & Notification Badge)",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    icon = Lucide.Bell,
                    contentDescription = "Notifikasi",
                    onClick = { onToast("IconButton Notifikasi dengan badge 5 diklik") },
                    variant = IconButtonVariant.Secondary,
                    size = IconButtonSize.MD,
                    badgeCount = 5
                )

                IconButton(
                    icon = Lucide.Plus,
                    contentDescription = "Tambah",
                    onClick = { onToast("IconButton Primary diklik") },
                    variant = IconButtonVariant.Primary,
                    size = IconButtonSize.MD,
                    shape = IconButtonShape.Circle
                )

                IconButton(
                    icon = Lucide.Search,
                    contentDescription = "Cari",
                    onClick = { onToast("IconButton Outline diklik") },
                    variant = IconButtonVariant.Outline,
                    size = IconButtonSize.MD
                )

                IconButton(
                    icon = Lucide.Settings,
                    contentDescription = "Pengaturan",
                    onClick = { onToast("IconButton Ghost diklik") },
                    variant = IconButtonVariant.Ghost,
                    size = IconButtonSize.MD
                )

                IconButton(
                    icon = Lucide.User,
                    contentDescription = "Profil",
                    onClick = { onToast("IconButton Neutral diklik") },
                    variant = IconButtonVariant.Neutral,
                    size = IconButtonSize.MD,
                    shape = IconButtonShape.Circle
                )
            }

            HorizontalDivider(color = Border, thickness = 1.dp)

            Text(
                text = "Interactive Button Playground",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = Primary
            )

            TextField(
                value = customButtonText,
                onValueChange = { customButtonText = it },
                label = "Teks Tombol"
            )

            Text(text = "Pilih Varian:", fontSize = 12.sp, fontWeight = FontWeight.Medium, color = TextPrimary)
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                ButtonVariant.entries.forEach { variant ->
                    FilterChip(
                        selected = selectedVariant == variant,
                        onClick = { selectedVariant = variant },
                        label = { Text(variant.name, fontSize = 11.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Primary,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = "Ukuran:", fontSize = 12.sp, fontWeight = FontWeight.Medium, color = TextPrimary)
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        ButtonSize.entries.forEach { size ->
                            FilterChip(
                                selected = selectedSize == size,
                                onClick = { selectedSize = size },
                                label = { Text(size.name, fontSize = 11.sp) }
                            )
                        }
                    }
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = "Bentuk:", fontSize = 12.sp, fontWeight = FontWeight.Medium, color = TextPrimary)
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        ButtonShape.entries.forEach { shape ->
                            FilterChip(
                                selected = selectedShape == shape,
                                onClick = { selectedShape = shape },
                                label = { Text(shape.name, fontSize = 11.sp) }
                            )
                        }
                    }
                }
            }

            SwitchWithLabel(
                checked = isInteractiveEnabled,
                onCheckedChange = { isInteractiveEnabled = it },
                label = "Status Aktif (Enabled)"
            )

            SwitchWithLabel(
                checked = isInteractiveLoading,
                onCheckedChange = { isInteractiveLoading = it },
                label = "Status Loading (Spinner)"
            )

            SwitchWithLabel(
                checked = hasInteractiveLeadingIcon,
                onCheckedChange = { hasInteractiveLeadingIcon = it },
                label = "Leading Icon (Kiri)"
            )

            SwitchWithLabel(
                checked = hasInteractiveTrailingIcon,
                onCheckedChange = { hasInteractiveTrailingIcon = it },
                label = "Trailing Icon (Kanan)"
            )

            Button(
                text = customButtonText.ifBlank { "Klik Saya" },
                onClick = {
                    buttonClickCount++
                    onToast("Tombol diklik! Total: $buttonClickCount")
                },
                variant = selectedVariant,
                size = selectedSize,
                shape = selectedShape,
                enabled = isInteractiveEnabled,
                isLoading = isInteractiveLoading,
                leadingIcon = if (hasInteractiveLeadingIcon) Lucide.Send else null,
                trailingIcon = if (hasInteractiveTrailingIcon) Lucide.ChevronRight else null
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Neutral0),
                shape = RoundedCornerShape(8.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Total Klik: $buttonClickCount kali",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextPrimary
                    )
                    if (buttonClickCount > 0) {
                        Button(
                            text = "Reset",
                            onClick = { buttonClickCount = 0 },
                            variant = ButtonVariant.Outline,
                            size = ButtonSize.SM,
                            fullWidth = false
                        )
                    }
                }
            }
        }
    }
}

// 2. FORM INPUT SHOWCASE
@Composable
private fun InputShowcaseSection() {
    var textInput by remember { mutableStateOf("Melanie Refman") }
    var emailInput by remember { mutableStateOf("melanie@example.com") }
    var passwordInput by remember { mutableStateOf("SecretPassword123") }
    var phoneInput by remember { mutableStateOf("081234567890") }
    var errorInput by remember { mutableStateOf("invalid-format") }
    var isErrorActive by remember { mutableStateOf(true) }

    ShowcaseCard(
        title = "Form Input & PasswordField",
        subtitle = "Komponen input dengan prefix/suffix, label asterisk required, pesan validasi, dan toggle password"
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {

            TextField(
                value = textInput,
                onValueChange = { textInput = it },
                label = "Nama Lengkap Sesuai KTP",
                required = true,
                placeholder = "Masukkan nama lengkap",
                leadingIcon = Lucide.User,
                helperText = "Gunakan nama asli untuk verifikasi identitas"
            )

            TextField(
                value = emailInput,
                onValueChange = { emailInput = it },
                label = "Alamat Email",
                required = true,
                placeholder = "contoh@email.com",
                leadingIcon = Lucide.Mail
            )

            PasswordField(
                value = passwordInput,
                onValueChange = { passwordInput = it },
                label = "Kata Sandi Akun",
                required = true,
                placeholder = "Minimal 8 karakter",
                helperText = "Gunakan kombinasi huruf, angka, dan simbol"
            )

            TextField(
                value = phoneInput,
                onValueChange = { phoneInput = it },
                label = "Nomor Telepon / WhatsApp",
                required = true,
                placeholder = "8xxxxxxxxxx",
                prefixText = "+62",
                leadingIcon = Lucide.Phone
            )

            HorizontalDivider(color = Border, thickness = 1.dp)

            Text(
                text = "Status Validasi & Pesan Error",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary
            )

            SwitchWithLabel(
                checked = isErrorActive,
                onCheckedChange = { isErrorActive = it },
                label = "Aktifkan State Error Validasi"
            )

            TextField(
                value = errorInput,
                onValueChange = { errorInput = it },
                label = "Format Email dengan Validasi",
                placeholder = "contoh@domain.com",
                leadingIcon = Lucide.Mail,
                errorMessage = if (isErrorActive) "Alamat email tidak valid. Pastikan mengandung @ dan domain." else null
            )

            TextField(
                value = "1234567890123456 (Terkunci)",
                onValueChange = {},
                label = "Nomor KTP (Read-Only / Disabled)",
                enabled = false,
                leadingIcon = Lucide.IdCard,
                helperText = "KTP terverifikasi dan tidak dapat diubah"
            )
        }
    }
}

// 3. SELECTION COMPONENTS SHOWCASE (Checkbox, RadioButton, Switch)
@Composable
private fun SelectionShowcaseSection() {
    var termsChecked by remember { mutableStateOf(true) }
    var privacyChecked by remember { mutableStateOf(false) }
    var indeterminateChecked by remember { mutableStateOf(false) }
    var isIndeterminate by remember { mutableStateOf(true) }
    var showErrorCheckbox by remember { mutableStateOf(true) }

    var selectedLoanPurpose by remember { mutableStateOf("modal_usaha") }

    var pushNotifEnabled by remember { mutableStateOf(true) }
    var biometricsEnabled by remember { mutableStateOf(false) }

    ShowcaseCard(
        title = "Selection Controls (Checkbox, Radio & Switch)",
        subtitle = "Elemen pemilih dengan border abu-abu bersih (bukan hitam) dan aksen oranye aktif"
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {

            Text(
                text = "Checkbox & CheckboxWithLabel",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = Primary
            )

            CheckboxWithLabel(
                checked = termsChecked,
                onCheckedChange = { termsChecked = it },
                label = "Saya menyetujui Syarat dan Ketentuan SAKU",
                description = "Dengan menyetujui, Anda tunduk pada regulasi dan kebijakan privasi yang berlaku."
            )

            CheckboxWithLabel(
                checked = privacyChecked,
                onCheckedChange = { privacyChecked = it },
                label = "Izinkan akses data kredit perbankan",
                description = "Digunakan semata-mata untuk proses scoring pengajuan pinjaman."
            )

            CheckboxWithLabel(
                checked = indeterminateChecked,
                onCheckedChange = {
                    indeterminateChecked = it
                    isIndeterminate = false
                },
                indeterminate = isIndeterminate,
                label = "Pilih Semua Dokumen Persyaratan",
                description = "Status indeterminate aktif jika sebagian dokumen belum dipilih."
            )

            CheckboxWithLabel(
                checked = !showErrorCheckbox,
                onCheckedChange = { showErrorCheckbox = !it },
                label = "Konfirmasi Kebenaran Data",
                description = "Wajib dicentang sebelum melanjutkan pengajuan",
                errorMessage = if (showErrorCheckbox) "Anda wajib mencentang persetujuan ini" else null
            )

            HorizontalDivider(color = Border, thickness = 1.dp)

            Text(
                text = "RadioButton (Pilihan Tunggal)",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = Primary
            )

            Text(
                text = "Pilih Tujuan Pinjaman:",
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = TextSecondary
            )

            RadioButtonWithLabel(
                selected = selectedLoanPurpose == "modal_usaha",
                onClick = { selectedLoanPurpose = "modal_usaha" },
                label = "Modal Usaha / UMKM",
                description = "Untuk pengembangan bisnis, belanja stok barang, atau modal kerja"
            )

            RadioButtonWithLabel(
                selected = selectedLoanPurpose == "kebutuhan_pribadi",
                onClick = { selectedLoanPurpose = "kebutuhan_pribadi" },
                label = "Kebutuhan Konsumtif / Pribadi",
                description = "Biaya pendidikan, kesehatan, renovasi rumah, atau dana darurat"
            )

            RadioButtonWithLabel(
                selected = selectedLoanPurpose == "investasi",
                onClick = { selectedLoanPurpose = "investasi" },
                label = "Investasi & Pembelian Alat",
                description = "Pembelian mesin produksi atau alat operasional usaha"
            )

            HorizontalDivider(color = Border, thickness = 1.dp)

            Text(
                text = "Switch Toggle (Pengaturan & Keamanan)",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = Primary
            )

            SwitchWithLabel(
                checked = pushNotifEnabled,
                onCheckedChange = { pushNotifEnabled = it },
                label = "Notifikasi Status Pinjaman",
                description = "Dapatkan pemberitahuan langsung saat status pinjaman disetujui atau dicairkan"
            )

            SwitchWithLabel(
                checked = biometricsEnabled,
                onCheckedChange = { biometricsEnabled = it },
                label = "Masuk Menggunakan Biometrik / Sidik Jari",
                description = "Masuk lebih cepat dan aman tanpa perlu mengetik kata sandi"
            )
        }
    }
}

// 4. BADGE SHOWCASE
@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun BadgeShowcaseSection(onToast: (String) -> Unit) {
    val removableBadges = remember {
        mutableStateListOf("Pinjaman Kilat", "Bunga 0.8%", "Tenor 12 Bln", "Verifikasi Instan")
    }

    ShowcaseCard(
        title = "Badge / StatusBadge Component",
        subtitle = "Label status fintech dengan variasi warna soft, solid, outline, pulsing dot, dan removable chips"
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {

            Text(
                text = "Soft Tint Badges (Fintech Status)",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary
            )

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Badge(text = "Primary Soft", variant = BadgeVariant.Primary)
                Badge(text = "Berhasil Disetujui", variant = BadgeVariant.Success, dot = true)
                Badge(text = "Menunggu Verifikasi", variant = BadgeVariant.Warning, dotPulse = true)
                Badge(text = "Pengajuan Ditolak", variant = BadgeVariant.Error, dot = true)
                Badge(text = "Dalam Proses", variant = BadgeVariant.Info, dotPulse = true)
                Badge(text = "Netral", variant = BadgeVariant.Neutral)
                Badge(text = "Premium", variant = BadgeVariant.Purple, leadingIcon = Lucide.Sparkles)
                Badge(text = "Kredit Kilat", variant = BadgeVariant.Orange, leadingIcon = Lucide.TrendingUp)
            }

            HorizontalDivider(color = Border, thickness = 1.dp)

            Text(
                text = "Solid & Outline Badges",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary
            )

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Badge(text = "Solid Primary", variant = BadgeVariant.SolidPrimary)
                Badge(text = "Solid Success", variant = BadgeVariant.SolidSuccess)
                Badge(text = "Solid Warning", variant = BadgeVariant.SolidWarning)
                Badge(text = "Solid Error", variant = BadgeVariant.SolidError)
                Badge(text = "Solid Neutral", variant = BadgeVariant.SolidNeutral)
                Badge(text = "Outline Badge", variant = BadgeVariant.Outline)
            }

            HorizontalDivider(color = Border, thickness = 1.dp)

            Text(
                text = "Ukuran & Bentuk (Pill vs Rounded)",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Badge(text = "Large (13sp)", size = BadgeSize.LG, variant = BadgeVariant.Primary, shape = BadgeShape.Pill)
                Badge(text = "Medium (12sp)", size = BadgeSize.MD, variant = BadgeVariant.Success, shape = BadgeShape.Rounded)
                Badge(text = "Small (11sp)", size = BadgeSize.SM, variant = BadgeVariant.Warning, shape = BadgeShape.Pill)
            }

            HorizontalDivider(color = Border, thickness = 1.dp)

            Text(
                text = "Removable Filter Badges (Ketuk X untuk hapus)",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary
            )

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                removableBadges.forEach { item ->
                    Badge(
                        text = item,
                        variant = BadgeVariant.Primary,
                        removable = true,
                        onRemove = {
                            removableBadges.remove(item)
                            onToast("Filter '$item' dihapus")
                        }
                    )
                }
            }

            if (removableBadges.isEmpty()) {
                Button(
                    text = "Reset Filter Badges",
                    onClick = {
                        removableBadges.addAll(listOf("Pinjaman Kilat", "Bunga 0.8%", "Tenor 12 Bln", "Verifikasi Instan"))
                    },
                    variant = ButtonVariant.Secondary,
                    size = ButtonSize.SM,
                    fullWidth = false
                )
            }
        }
    }
}

// 6. LUCIDE ICONS SHOWCASE
data class LucideIconItem(
    val name: String,
    val vector: ImageVector,
    val category: String
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun LucideIconsShowcaseSection() {
    val iconsList = remember {
        listOf(
            LucideIconItem("Wallet", Lucide.Wallet, "Keuangan & Transaksi"),
            LucideIconItem("CreditCard", Lucide.CreditCard, "Keuangan & Transaksi"),
            LucideIconItem("Landmark", Lucide.Landmark, "Keuangan & Transaksi"),
            LucideIconItem("TrendingUp", Lucide.TrendingUp, "Keuangan & Transaksi"),
            LucideIconItem("Receipt", Lucide.Receipt, "Keuangan & Transaksi"),
            LucideIconItem("DollarSign", Lucide.DollarSign, "Keuangan & Transaksi"),

            LucideIconItem("User", Lucide.User, "Akun & Keamanan"),
            LucideIconItem("Users", Lucide.Users, "Akun & Keamanan"),
            LucideIconItem("Lock", Lucide.Lock, "Akun & Keamanan"),
            LucideIconItem("Key", Lucide.Key, "Akun & Keamanan"),
            LucideIconItem("ShieldCheck", Lucide.ShieldCheck, "Akun & Keamanan"),
            LucideIconItem("IdCard", Lucide.IdCard, "Akun & Keamanan"),
            LucideIconItem("Eye", Lucide.Eye, "Akun & Keamanan"),
            LucideIconItem("EyeOff", Lucide.EyeOff, "Akun & Keamanan"),
            LucideIconItem("LogOut", Lucide.LogOut, "Akun & Keamanan"),

            LucideIconItem("Mail", Lucide.Mail, "Komunikasi & Status"),
            LucideIconItem("Phone", Lucide.Phone, "Komunikasi & Status"),
            LucideIconItem("Bell", Lucide.Bell, "Komunikasi & Status"),
            LucideIconItem("Send", Lucide.Send, "Komunikasi & Status"),
            LucideIconItem("Info", Lucide.Info, "Komunikasi & Status"),
            LucideIconItem("CircleCheck", Lucide.CircleCheck, "Komunikasi & Status"),
            LucideIconItem("CircleAlert", Lucide.CircleAlert, "Komunikasi & Status"),

            LucideIconItem("ArrowLeft", Lucide.ArrowLeft, "Aksi & Navigasi"),
            LucideIconItem("ArrowRight", Lucide.ArrowRight, "Aksi & Navigasi"),
            LucideIconItem("ChevronRight", Lucide.ChevronRight, "Aksi & Navigasi"),
            LucideIconItem("Plus", Lucide.Plus, "Aksi & Navigasi"),
            LucideIconItem("Check", Lucide.Check, "Aksi & Navigasi"),
            LucideIconItem("Search", Lucide.Search, "Aksi & Navigasi"),
            LucideIconItem("Settings", Lucide.Settings, "Aksi & Navigasi"),
            LucideIconItem("Palette", Lucide.Palette, "Aksi & Navigasi"),
            LucideIconItem("House", Lucide.House, "Aksi & Navigasi"),
            LucideIconItem("Briefcase", Lucide.Briefcase, "Aksi & Navigasi"),
            LucideIconItem("ImagePlus", Lucide.ImagePlus, "Aksi & Navigasi"),
            LucideIconItem("RotateCw", Lucide.RotateCw, "Aksi & Navigasi"),
            LucideIconItem("Heart", Lucide.Heart, "Aksi & Navigasi"),
            LucideIconItem("Sparkles", Lucide.Sparkles, "Aksi & Navigasi")
        )
    }

    var selectedIconItem by remember { mutableStateOf(iconsList.first()) }
    var selectedSizeDp by remember { mutableIntStateOf(28) }
    var selectedColor by remember { mutableStateOf(Primary) }
    var selectedColorName by remember { mutableStateOf("Primary") }

    ShowcaseCard(
        title = "Lucide Icons",
        subtitle = "Gaya stroke 2px monoline modern khas Fintech — 1:1 identik dengan frontend web"
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Neutral0),
                border = androidx.compose.foundation.BorderStroke(1.dp, Border)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Interactive Icon Preview",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Primary
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Box(
                            modifier = Modifier
                                .size(72.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Surface)
                                .border(1.dp, Border, RoundedCornerShape(12.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = selectedIconItem.vector,
                                contentDescription = selectedIconItem.name,
                                tint = selectedColor,
                                modifier = Modifier.size(selectedSizeDp.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Lucide.${selectedIconItem.name}",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Text(
                                text = "Kategori: ${selectedIconItem.category}",
                                fontSize = 12.sp,
                                color = TextSecondary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Ukuran: ${selectedSizeDp}dp | Warna: $selectedColorName",
                                fontSize = 11.sp,
                                color = Primary60,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(text = "Pilih Ukuran:", fontSize = 12.sp, fontWeight = FontWeight.Medium, color = TextPrimary)
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf(18, 24, 28, 36, 48).forEach { size ->
                            FilterChip(
                                selected = selectedSizeDp == size,
                                onClick = { selectedSizeDp = size },
                                label = { Text("${size}dp") },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = Primary,
                                    selectedLabelColor = Color.White
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(text = "Pilih Warna Tint:", fontSize = 12.sp, fontWeight = FontWeight.Medium, color = TextPrimary)
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf(
                            Triple("Primary", Primary, Primary),
                            Triple("TextPrimary", TextPrimary, TextPrimary),
                            Triple("Success", Success, Success),
                            Triple("Error", Error, Error),
                            Triple("Warning", Warning, Warning)
                        ).forEach { (name, color, _) ->
                            Box(
                                modifier = Modifier
                                    .size(30.dp)
                                    .clip(CircleShape)
                                    .background(color)
                                    .border(
                                        width = if (selectedColor == color) 2.5.dp else 1.dp,
                                        color = if (selectedColor == color) Neutral else Color.Transparent,
                                        shape = CircleShape
                                    )
                                    .clickable {
                                        selectedColor = color
                                        selectedColorName = name
                                    }
                            )
                        }
                    }
                }
            }

            HorizontalDivider(color = Border, thickness = 1.dp)

            val categories = iconsList.map { it.category }.distinct()
            categories.forEach { cat ->
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = cat,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Primary
                    )

                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        iconsList.filter { it.category == cat }.forEach { item ->
                            val isSelected = selectedIconItem.name == item.name
                            Column(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (isSelected) Primary0 else Surface)
                                    .border(
                                        width = if (isSelected) 1.5.dp else 1.dp,
                                        color = if (isSelected) Primary else Border,
                                        shape = RoundedCornerShape(10.dp)
                                    )
                                    .clickable { selectedIconItem = item }
                                    .padding(horizontal = 10.dp, vertical = 8.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    imageVector = item.vector,
                                    contentDescription = item.name,
                                    tint = if (isSelected) Primary else TextPrimary,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = item.name,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) Primary else TextSecondary
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// 7. DESIGN TOKENS SHOWCASE
@Composable
private fun DesignTokensSection() {
    ShowcaseCard(
        title = "Design Tokens",
        subtitle = "Palet Warna dan Skala Tipografi Sistem SAKU"
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Text(
                text = "Palet Warna Utama & Semantik",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ColorChipItem(color = Primary, name = "Primary", hex = "#FF792E", modifier = Modifier.weight(1f))
                ColorChipItem(color = Primary0, name = "Primary 0", hex = "#FFF4EE", modifier = Modifier.weight(1f))
                ColorChipItem(color = Neutral, name = "Neutral", hex = "#252525", modifier = Modifier.weight(1f))
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ColorChipItem(color = Success, name = "Success", hex = "#48BB78", modifier = Modifier.weight(1f))
                ColorChipItem(color = Warning, name = "Warning", hex = "#F59E0B", modifier = Modifier.weight(1f))
                ColorChipItem(color = Error, name = "Error", hex = "#E53E3E", modifier = Modifier.weight(1f))
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ColorChipItem(color = Info, name = "Info (Blue)", hex = "#3B82F6", modifier = Modifier.weight(1f))
                ColorChipItem(color = Purple, name = "Purple", hex = "#9333EA", modifier = Modifier.weight(1f))
                ColorChipItem(color = Orange, name = "Orange", hex = "#EA580C", modifier = Modifier.weight(1f))
            }

            HorizontalDivider(color = Border, thickness = 1.dp)

            Text(
                text = "Tipografi (Overused Grotesk)",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary
            )

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(text = "Headline Large (24sp Bold)", style = Typography.headlineLarge, color = TextPrimary)
                Text(text = "Title Large (18sp SemiBold)", style = Typography.titleLarge, color = TextPrimary)
                Text(text = "Title Medium (16sp Medium)", style = Typography.titleMedium, color = TextPrimary)
                Text(text = "Body Large (16sp Regular)", style = Typography.bodyLarge, color = TextSecondary)
                Text(text = "Body Medium (14sp Regular)", style = Typography.bodyMedium, color = TextSecondary)
                Text(text = "Label Large (14sp SemiBold)", style = Typography.labelLarge, color = Primary)
            }
        }
    }
}

// Showcase Container Card
@Composable
private fun ShowcaseCard(
    title: String,
    subtitle: String,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, Border)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {
            Text(
                text = title,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Text(
                text = subtitle,
                fontSize = 13.sp,
                color = TextSecondary
            )

            Spacer(modifier = Modifier.height(16.dp))

            content()
        }
    }
}

@Composable
private fun ColorChipItem(
    color: Color,
    name: String,
    hex: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(Neutral0)
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(color)
                .border(1.dp, Border, CircleShape)
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(text = name, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
        Text(text = hex, fontSize = 10.sp, color = TextMuted)
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun SandboxScreenPreview() {
    SandboxScreen(onNavigateBack = {})
}