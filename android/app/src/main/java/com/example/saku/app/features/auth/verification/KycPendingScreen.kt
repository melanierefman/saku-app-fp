package com.example.saku.app.features.auth.verification

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.composables.icons.lucide.ArrowLeft
import com.composables.icons.lucide.Bell
import com.composables.icons.lucide.Briefcase
import com.composables.icons.lucide.Camera
import com.composables.icons.lucide.Check
import com.composables.icons.lucide.ChevronRight
import com.composables.icons.lucide.CircleAlert
import com.composables.icons.lucide.CircleCheck
import com.composables.icons.lucide.Clock
import com.composables.icons.lucide.Headphones
import com.composables.icons.lucide.IdCard
import com.composables.icons.lucide.Image as LucideImage
import com.composables.icons.lucide.Landmark
import com.composables.icons.lucide.LogOut
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.MapPin
import com.composables.icons.lucide.RefreshCw
import com.composables.icons.lucide.ShieldAlert
import com.composables.icons.lucide.Trash2
import com.composables.icons.lucide.User
import com.example.saku.app.R
import com.example.saku.app.core.network.ApiConstants
import com.example.saku.app.core.network.dto.CustomerProfileDto
import com.example.saku.app.core.ui.components.Badge
import com.example.saku.app.core.ui.components.BadgeSize
import com.example.saku.app.core.ui.components.BadgeVariant
import com.example.saku.app.core.ui.components.Button
import com.example.saku.app.core.ui.components.ButtonSize
import com.example.saku.app.core.ui.components.ButtonVariant
import com.example.saku.app.core.ui.components.ConfirmationDialog
import com.example.saku.app.core.ui.components.DialogType
import com.example.saku.app.core.util.ImageCompressorHelper
import com.example.saku.app.ui.theme.Background
import com.example.saku.app.ui.theme.Border
import com.example.saku.app.ui.theme.Error
import com.example.saku.app.ui.theme.Error0
import com.example.saku.app.ui.theme.Neutral0
import com.example.saku.app.ui.theme.Neutral10
import com.example.saku.app.ui.theme.Primary
import com.example.saku.app.ui.theme.Primary0
import com.example.saku.app.ui.theme.Success
import com.example.saku.app.ui.theme.Success0
import com.example.saku.app.ui.theme.Surface
import com.example.saku.app.ui.theme.TextMuted
import com.example.saku.app.ui.theme.TextPrimary
import com.example.saku.app.ui.theme.TextSecondary
import com.example.saku.app.ui.theme.Warning
import com.example.saku.app.ui.theme.Warning0
import com.example.saku.app.ui.theme.Warning20
import com.example.saku.app.ui.theme.Warning70
import com.example.saku.app.ui.theme.Warning80
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import java.text.NumberFormat
import java.util.Locale
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

enum class KycSubPage {
    MAIN,
    DATA_AKUN,
    DATA_DIRI_KEUANGAN,
    INFORMASI_ALAMAT,
    REVISI_DOKUMEN
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KycPendingScreen(
    onNavigateToHome: () -> Unit,
    onNavigateToLogin: () -> Unit,
    onNavigateToNotifications: () -> Unit = {},
    viewModel: KycPendingViewModel = koinViewModel()
) {
    val context = LocalContext.current
    val statusState by viewModel.statusState.collectAsState()
    val profile by viewModel.profile.collectAsState()
    val unreadCount by viewModel.unreadNotifikasiCount.collectAsState()
    val isSubmittingRevision by viewModel.isSubmittingRevision.collectAsState()
    val ktpBitmap by viewModel.ktpBitmap.collectAsState()
    val ktpUri by viewModel.ktpUri.collectAsState()
    val selfieBitmap by viewModel.selfieBitmap.collectAsState()
    val selfieUri by viewModel.selfieUri.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    var currentSubPage by remember { mutableStateOf(KycSubPage.MAIN) }
    var showPendingInfoDialog by remember { mutableStateOf(false) }
    var showApprovedDialog by remember { mutableStateOf(false) }
    var showLogoutDialog by remember { mutableStateOf(false) }
    var countdownSeconds by remember { mutableStateOf(3) }

    val rawStatus = (profile?.statusVerifikasi ?: "").uppercase()
    val isRejected = rawStatus.contains("REJECTED") || rawStatus.contains("DITOLAK")
    val isRevisionNeeded = !isRejected && (rawStatus.contains("REVISI") || rawStatus.contains("REVISION"))

    // Handle back button on sub-pages to navigate back to MAIN view
    BackHandler(enabled = currentSubPage != KycSubPage.MAIN) {
        currentSubPage = KycSubPage.MAIN
    }

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.fetchUnreadNotificationCount()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    LaunchedEffect(Unit) {
        com.example.saku.app.MainActivity.syncFcmToken(context)
    }

    LaunchedEffect(statusState) {
        when (statusState) {
            is KycStatusState.Verified -> {
                showApprovedDialog = true
                countdownSeconds = 3
            }
            is KycStatusState.StillPending -> {
                showPendingInfoDialog = true
            }
            is KycStatusState.Error -> {
                val errorMsg = (statusState as KycStatusState.Error).message
                scope.launch {
                    snackbarHostState.showSnackbar(errorMsg)
                }
            }
            else -> {}
        }
    }

    // Auto-redirect timer when KYC is approved
    LaunchedEffect(showApprovedDialog) {
        if (showApprovedDialog) {
            while (countdownSeconds > 0) {
                delay(1000)
                countdownSeconds -= 1
            }
            showApprovedDialog = false
            viewModel.logout { onNavigateToLogin() }
        }
    }

    // Modal Sukses Akun Terverifikasi
    if (showApprovedDialog) {
        AlertDialog(
            onDismissRequest = { /* Modal wajib konfirmasi ke Login */ },
            confirmButton = {
                Button(
                    text = "Masuk Sekarang (Login)",
                    onClick = {
                        showApprovedDialog = false
                        viewModel.logout { onNavigateToLogin() }
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            },
            icon = {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(Success0),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Lucide.CircleCheck,
                        contentDescription = null,
                        tint = Success,
                        modifier = Modifier.size(34.dp)
                    )
                }
            },
            title = {
                Text(
                    text = "Akun Berhasil Diverifikasi! 🎉",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = TextPrimary,
                    textAlign = TextAlign.Center
                )
            },
            text = {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Akun Anda telah disetujui. Silakan login untuk mulai mengajukan pinjaman.",
                        color = TextSecondary,
                        fontSize = 13.5.sp,
                        lineHeight = 19.sp,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Neutral0)
                            .padding(horizontal = 14.dp, vertical = 7.dp)
                    ) {
                        Text(
                            text = "Otomatis dialihkan ke Login dalam $countdownSeconds detik...",
                            color = Primary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            },
            containerColor = Surface,
            shape = RoundedCornerShape(20.dp)
        )
    }

    if (showPendingInfoDialog) {
        AlertDialog(
            onDismissRequest = {
                showPendingInfoDialog = false
                viewModel.resetState()
            },
            confirmButton = {
                Button(
                    text = "Mengerti",
                    onClick = {
                        showPendingInfoDialog = false
                        viewModel.resetState()
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            },
            icon = {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(Warning0),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Lucide.Clock,
                        contentDescription = null,
                        tint = Warning,
                        modifier = Modifier.size(28.dp)
                    )
                }
            },
            title = {
                Text(
                    text = "Verifikasi Sedang Berjalan",
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp,
                    color = TextPrimary,
                    textAlign = TextAlign.Center
                )
            },
            text = {
                Text(
                    text = "Dokumen Anda sedang dalam proses pemeriksaan. Estimasi maksimal 1 jam kerja.",
                    color = TextSecondary,
                    fontSize = 13.5.sp,
                    lineHeight = 19.sp,
                    textAlign = TextAlign.Center
                )
            },
            containerColor = Surface,
            shape = RoundedCornerShape(20.dp)
        )
    }

    // Logout Confirmation Dialog
    ConfirmationDialog(
        visible = showLogoutDialog,
        title = "Keluar dari Akun?",
        message = "Apakah Anda yakin ingin keluar dari akun Anda?",
        confirmButtonText = "Keluar",
        dismissButtonText = "Batal",
        type = DialogType.DESTRUCTIVE,
        icon = Lucide.LogOut,
        onConfirm = {
            showLogoutDialog = false
            viewModel.logout { onNavigateToLogin() }
        },
        onDismiss = { showLogoutDialog = false }
    )

    // Main Full-Page Router
    AnimatedContent(
        targetState = currentSubPage,
        transitionSpec = {
            if (targetState != KycSubPage.MAIN) {
                slideInHorizontally { width -> width } togetherWith slideOutHorizontally { width -> -width }
            } else {
                slideInHorizontally { width -> -width } togetherWith slideOutHorizontally { width -> width }
            }
        },
        label = "KycPageNavigation"
    ) { page ->
        when (page) {
            KycSubPage.MAIN -> {
                KycMainVerificationScreen(
                    profile = profile,
                    isRevisionNeeded = isRevisionNeeded,
                    isRejected = isRejected,
                    unreadCount = unreadCount,
                    statusState = statusState,
                    snackbarHostState = snackbarHostState,
                    onNavigateToSubPage = { currentSubPage = it },
                    onNavigateToNotifications = onNavigateToNotifications,
                    onCheckStatus = { viewModel.checkVerificationStatus(silent = false) },
                    onLogout = { showLogoutDialog = true }
                )
            }
            KycSubPage.DATA_AKUN -> {
                KycDataAkunPage(
                    profile = profile,
                    onBackClick = { currentSubPage = KycSubPage.MAIN }
                )
            }
            KycSubPage.DATA_DIRI_KEUANGAN -> {
                KycDataDiriKeuanganPage(
                    profile = profile,
                    onBackClick = { currentSubPage = KycSubPage.MAIN }
                )
            }
            KycSubPage.INFORMASI_ALAMAT -> {
                KycInformasiAlamatPage(
                    profile = profile,
                    onBackClick = { currentSubPage = KycSubPage.MAIN }
                )
            }
            KycSubPage.REVISI_DOKUMEN -> {
                KycRevisiDokumenPage(
                    profile = profile,
                    isRevisionNeeded = isRevisionNeeded,
                    ktpBitmap = ktpBitmap,
                    ktpUri = ktpUri,
                    selfieBitmap = selfieBitmap,
                    selfieUri = selfieUri,
                    isSubmitting = isSubmittingRevision,
                    onKtpSelected = viewModel::setKtpPhoto,
                    onSelfieSelected = viewModel::setSelfiePhoto,
                    onSubmitRevision = {
                        viewModel.submitRevision(context) {
                            currentSubPage = KycSubPage.MAIN
                        }
                    },
                    onBackClick = { currentSubPage = KycSubPage.MAIN }
                )
            }
        }
    }
}
// 1. REUSABLE TOP BAR COMPONENT (Loan Detail Screen style)
@Composable
private fun KycDetailTopBar(
    title: String,
    subtitle: String? = null,
    onBackClick: () -> Unit,
    badgeText: String? = null,
    badgeVariant: BadgeVariant = BadgeVariant.Neutral
) {
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
                onClick = onBackClick,
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
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                if (!subtitle.isNullOrBlank()) {
                    Text(
                        text = subtitle,
                        fontSize = 11.5.sp,
                        color = TextMuted
                    )
                }
            }
            if (!badgeText.isNullOrBlank()) {
                Spacer(modifier = Modifier.width(8.dp))
                Badge(
                    text = badgeText,
                    variant = badgeVariant,
                    size = BadgeSize.SM
                )
            }
        }
    }
}
// 2. MAIN STATUS VERIFIKASI SCREEN
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun KycMainVerificationScreen(
    profile: CustomerProfileDto?,
    isRevisionNeeded: Boolean,
    isRejected: Boolean,
    unreadCount: Long = 0L,
    statusState: KycStatusState,
    snackbarHostState: SnackbarHostState,
    onNavigateToSubPage: (KycSubPage) -> Unit,
    onNavigateToNotifications: () -> Unit,
    onCheckStatus: () -> Unit,
    onLogout: () -> Unit
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = Color.White,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Status Verifikasi",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 17.sp,
                        color = TextPrimary
                    )
                },
                actions = {
                    IconButton(onClick = onNavigateToNotifications) {
                        Box(contentAlignment = Alignment.TopEnd) {
                            Icon(
                                imageVector = Lucide.Bell,
                                contentDescription = "Notifikasi",
                                tint = TextSecondary,
                                modifier = Modifier.size(20.dp)
                            )
                            if (unreadCount > 0) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(Error)
                                )
                            }
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(scrollState)
                .padding(horizontal = 20.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            if (isRejected) {
                // Header Icon for REJECTED
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(Error0)
                        .border(4.dp, Color(0xFFFFD2D2), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Lucide.ShieldAlert,
                        contentDescription = null,
                        tint = Error,
                        modifier = Modifier.size(40.dp)
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                Text(
                    text = "Pendaftaran Belum Disetujui",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    letterSpacing = (-0.3).sp,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Mohon maaf, pengajuan pendaftaran akun Anda saat ini belum memenuhi kriteria kelayakan layanan SAKU.",
                    fontSize = 13.sp,
                    color = TextSecondary,
                    lineHeight = 18.5.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 12.dp)
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Information Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Surface),
                    border = BorderStroke(1.dp, Border)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Lucide.Headphones,
                                contentDescription = null,
                                tint = Primary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Layanan Bantuan Nasabah",
                                fontSize = 14.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Untuk pertanyaan lebih lanjut seputar kriteria dan kebijakan pendaftaran, silakan hubungi tim Customer Care BCA Finance SAKU.",
                            fontSize = 12.5.sp,
                            color = TextSecondary,
                            lineHeight = 17.5.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    text = "Hubungi Customer Care",
                    onClick = {
                        try {
                            val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
                                data = Uri.parse("mailto:support@bcafinance.co.id")
                                putExtra(Intent.EXTRA_SUBJECT, "Pertanyaan Verifikasi Akun SAKU")
                            }
                            context.startActivity(Intent.createChooser(emailIntent, "Hubungi Dukungan SAKU"))
                        } catch (e: Exception) {
                            Toast.makeText(context, "Email: support@bcafinance.co.id", Toast.LENGTH_LONG).show()
                        }
                    },
                    variant = ButtonVariant.Primary,
                    size = ButtonSize.LG,
                    fullWidth = true,
                    leadingIcon = Lucide.Headphones
                )

                Spacer(modifier = Modifier.height(10.dp))

                TextButton(
                    onClick = onLogout,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Keluar ke Halaman Login",
                        color = TextSecondary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

            } else {
                // Central SAKU Orange Wallet Logo Circle
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(Primary0)
                        .border(4.dp, Color(0xFFFFE3D1), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.saku_logo),
                        contentDescription = "Logo SAKU",
                        modifier = Modifier
                            .size(62.dp)
                            .clip(CircleShape)
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Title: Menunggu Verifikasi / Perlu Revisi Dokumen
                Text(
                    text = if (isRevisionNeeded) "Perlu Revisi Dokumen" else "Menunggu Verifikasi",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isRevisionNeeded) Error else TextPrimary,
                    letterSpacing = (-0.3).sp,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Subtitle
                Text(
                    text = if (isRevisionNeeded) {
                        "Terdapat perbaikan yang diperlukan pada dokumen identitas Anda."
                    } else {
                        "Akun Anda sedang dalam proses verifikasi. Estimasi maksimal 1 jam kerja."
                    },
                    fontSize = 13.sp,
                    color = TextSecondary,
                    lineHeight = 18.5.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 12.dp)
                )

                Spacer(modifier = Modifier.height(20.dp))

                // 4-Step Verification Interactive Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Surface),
                    border = BorderStroke(1.dp, Border)
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        // Item 1: Data Akun Baru
                        VerificationInteractiveItemRow(
                            title = "Data Akun Nasabah",
                            subtitle = "Email, username, no handphone",
                            iconType = VerificationIconType.SUCCESS,
                            onClick = { onNavigateToSubPage(KycSubPage.DATA_AKUN) }
                        )

                        HorizontalDivider(color = Border.copy(alpha = 0.6f), thickness = 1.dp)

                        // Item 2: Data Diri & Keuangan
                        VerificationInteractiveItemRow(
                            title = "Data Diri & Keuangan",
                            subtitle = "Identitas NIK, pekerjaan & info rekening",
                            iconType = VerificationIconType.SUCCESS,
                            onClick = { onNavigateToSubPage(KycSubPage.DATA_DIRI_KEUANGAN) }
                        )

                        HorizontalDivider(color = Border.copy(alpha = 0.6f), thickness = 1.dp)

                        // Item 3: Informasi Alamat
                        VerificationInteractiveItemRow(
                            title = "Informasi Alamat",
                            subtitle = "Alamat sesuai e-KTP dan tempat tinggal",
                            iconType = VerificationIconType.SUCCESS,
                            onClick = { onNavigateToSubPage(KycSubPage.INFORMASI_ALAMAT) }
                        )

                        HorizontalDivider(color = Border.copy(alpha = 0.6f), thickness = 1.dp)

                        // Item 4: Verifikasi Identitas
                        VerificationInteractiveItemRow(
                            title = if (isRevisionNeeded) "Revisi Dokumen Identitas" else "Verifikasi Identitas",
                            subtitle = if (isRevisionNeeded) {
                                "Perlu revisi berkas foto e-KTP / Selfie"
                            } else {
                                "Dokumen foto e-KTP dan selfie terdaftar"
                            },
                            iconType = if (isRevisionNeeded) VerificationIconType.REVISION else VerificationIconType.PENDING,
                            onClick = { onNavigateToSubPage(KycSubPage.REVISI_DOKUMEN) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Action Buttons (Non-sticky, directly below the steps card)
                Button(
                    text = "Cek Status Verifikasi",
                    onClick = onCheckStatus,
                    isLoading = statusState is KycStatusState.Checking,
                    variant = ButtonVariant.Primary,
                    size = ButtonSize.LG,
                    fullWidth = true,
                    leadingIcon = Lucide.RefreshCw
                )

                Spacer(modifier = Modifier.height(10.dp))

                TextButton(
                    onClick = onLogout,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Keluar ke Halaman Login",
                        color = TextSecondary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}
// 3. PAGE 1: DATA AKUN NASABAH
@Composable
private fun KycDataAkunPage(
    profile: CustomerProfileDto?,
    onBackClick: () -> Unit
) {
    Scaffold(
        containerColor = Background,
        topBar = {
            KycDetailTopBar(
                title = "Data Akun Nasabah",
                subtitle = "Informasi akun yang terdaftar",
                onBackClick = onBackClick
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 18.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Account Details Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Surface),
                border = BorderStroke(1.dp, Border)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(imageVector = Lucide.User, contentDescription = null, tint = Primary, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Informasi Akun",
                            fontSize = 14.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    }
                    Spacer(modifier = Modifier.height(14.dp))

                    DetailFieldItem(label = "Alamat Email", value = profile?.email ?: "-")
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = Border.copy(alpha = 0.6f))
                    DetailFieldItem(label = "Username", value = profile?.username ?: "-")
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = Border.copy(alpha = 0.6f))
                    DetailFieldItem(label = "Nomor Handphone", value = profile?.noHp ?: "-")
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = Border.copy(alpha = 0.6f))
                    DetailFieldItem(label = "Status Registrasi", value = "Menunggu Verifikasi", isHighlight = true)
                    if (!profile?.createdDate.isNullOrBlank()) {
                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = Border.copy(alpha = 0.6f))
                        DetailFieldItem(label = "Tanggal Pendaftaran", value = formatIndoDate(profile?.createdDate))
                    }
                }
            }
        }
    }
}
// 4. PAGE 2: DATA DIRI & KEUANGAN
@Composable
private fun KycDataDiriKeuanganPage(
    profile: CustomerProfileDto?,
    onBackClick: () -> Unit
) {
    val formatRupiah: (Double?) -> String = { amount ->
        if (amount == null) "Rp 0"
        else "Rp " + NumberFormat.getNumberInstance(Locale("id", "ID")).format(amount)
    }

    Scaffold(
        containerColor = Background,
        topBar = {
            KycDetailTopBar(
                title = "Data Diri & Keuangan",
                subtitle = "Identitas pribadi, pekerjaan & rekening",
                onBackClick = onBackClick
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 18.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Section 1: Identitas Pribadi
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Surface),
                border = BorderStroke(1.dp, Border)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(imageVector = Lucide.IdCard, contentDescription = null, tint = Primary, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Identitas Pribadi",
                            fontSize = 14.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    }
                    Spacer(modifier = Modifier.height(14.dp))

                    DetailFieldItem(label = "NIK e-KTP", value = profile?.nik ?: "-")
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = Border.copy(alpha = 0.6f))
                    DetailFieldItem(label = "Nama Lengkap", value = profile?.nama ?: "-")
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = Border.copy(alpha = 0.6f))
                    DetailFieldItem(label = "Nama Ibu Kandung", value = profile?.namaIbuKandung ?: "-")
                }
            }

            // Section 2: Pekerjaan & Finansial
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Surface),
                border = BorderStroke(1.dp, Border)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(imageVector = Lucide.Briefcase, contentDescription = null, tint = Primary, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Pekerjaan & Finansial",
                            fontSize = 14.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    }
                    Spacer(modifier = Modifier.height(14.dp))

                    DetailFieldItem(label = "Profesi / Pekerjaan", value = profile?.pekerjaan ?: "-")
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = Border.copy(alpha = 0.6f))
                    DetailFieldItem(label = "Tempat Bekerja", value = profile?.tempatKerja ?: "-")
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = Border.copy(alpha = 0.6f))
                    DetailFieldItem(label = "Status Pekerjaan", value = formatStatusPekerjaan(profile?.statusPekerjaan))
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = Border.copy(alpha = 0.6f))
                    DetailFieldItem(label = "Penghasilan Bulanan", value = formatRupiah(profile?.penghasilanBulanan), isHighlight = true)
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = Border.copy(alpha = 0.6f))
                    DetailFieldItem(label = "Lama Bekerja", value = "${profile?.lamaBekerjaBulan ?: 0} Bulan")
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = Border.copy(alpha = 0.6f))
                    DetailFieldItem(label = "Total Cicilan Lain", value = formatRupiah(profile?.totalCicilanLainBulanan))
                }
            }

            // Section 3: Rekening Pencairan
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Surface),
                border = BorderStroke(1.dp, Border)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(imageVector = Lucide.Landmark, contentDescription = null, tint = Primary, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Rekening Bank Pencairan",
                            fontSize = 14.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    }
                    Spacer(modifier = Modifier.height(14.dp))

                    DetailFieldItem(label = "Nama Bank", value = profile?.namaBank ?: "-")
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = Border.copy(alpha = 0.6f))
                    DetailFieldItem(label = "Nomor Rekening", value = profile?.noRekening ?: "-")
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = Border.copy(alpha = 0.6f))
                    DetailFieldItem(label = "Nama Pemilik Rekening", value = profile?.namaRekening ?: "-")
                }
            }
        }
    }
}
// 5. PAGE 3: INFORMASI ALAMAT
@Composable
private fun KycInformasiAlamatPage(
    profile: CustomerProfileDto?,
    onBackClick: () -> Unit
) {
    Scaffold(
        containerColor = Background,
        topBar = {
            KycDetailTopBar(
                title = "Informasi Alamat",
                subtitle = "Alamat e-KTP dan tempat tinggal domisili",
                onBackClick = onBackClick
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 18.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Section 1: Alamat e-KTP
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Surface),
                border = BorderStroke(1.dp, Border)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(imageVector = Lucide.MapPin, contentDescription = null, tint = Primary, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Alamat Sesuai e-KTP",
                            fontSize = 14.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    }
                    Spacer(modifier = Modifier.height(14.dp))

                    DetailFieldItem(
                        label = "Alamat Lengkap",
                        value = profile?.alamatKtp?.formattedAddress ?: profile?.alamatKtp?.alamatLengkap ?: "-"
                    )
                    if (profile?.alamatKtp != null) {
                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = Border.copy(alpha = 0.6f))
                        DetailFieldItem(label = "RT / RW", value = "RT ${profile.alamatKtp.rt ?: '-'} / RW ${profile.alamatKtp.rw ?: '-'}")
                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = Border.copy(alpha = 0.6f))
                        DetailFieldItem(label = "Kelurahan / Kecamatan", value = "${profile.alamatKtp.kelurahan ?: '-'}, ${profile.alamatKtp.kecamatan ?: '-'}")
                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = Border.copy(alpha = 0.6f))
                        DetailFieldItem(label = "Kota / Kabupaten", value = profile.alamatKtp.kotaKabupaten ?: "-")
                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = Border.copy(alpha = 0.6f))
                        DetailFieldItem(label = "Provinsi", value = profile.alamatKtp.provinsi ?: "-")
                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = Border.copy(alpha = 0.6f))
                        DetailFieldItem(label = "Kode Pos", value = profile.alamatKtp.kodePos ?: "-")
                    }
                }
            }

            // Section 2: Alamat Domisili
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Surface),
                border = BorderStroke(1.dp, Border)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(imageVector = Lucide.MapPin, contentDescription = null, tint = Primary, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Alamat Domisili Saat Ini",
                            fontSize = 14.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    }
                    Spacer(modifier = Modifier.height(14.dp))

                    DetailFieldItem(
                        label = "Alamat Tempat Tinggal",
                        value = profile?.alamatDomisili?.formattedAddress ?: profile?.alamatDomisili?.alamatLengkap ?: profile?.alamatKtp?.formattedAddress ?: "-"
                    )
                }
            }
        }
    }
}
// 6. PAGE 4: REVISI DOKUMEN IDENTITAS (Full Page Screen)
@Composable
private fun KycRevisiDokumenPage(
    profile: CustomerProfileDto?,
    isRevisionNeeded: Boolean,
    ktpBitmap: Bitmap?,
    ktpUri: Uri?,
    selfieBitmap: Bitmap?,
    selfieUri: Uri?,
    isSubmitting: Boolean,
    onKtpSelected: (Uri?, Bitmap?) -> Unit,
    onSelfieSelected: (Uri?, Bitmap?) -> Unit,
    onSubmitRevision: () -> Unit,
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    var tempCameraUri by remember { mutableStateOf<Uri?>(null) }
    var currentCameraTarget by remember { mutableStateOf<String>("") }
    var showConfirmDialog by remember { mutableStateOf(false) }

    val note = (profile?.catatanVerifikasi ?: "").lowercase()
    val mentionsKtp = note.contains("ktp") || note.contains("nik") || note.contains("identitas")
    val mentionsSelfie = note.contains("selfie") || note.contains("wajah") || note.contains("liveness") || note.contains("muka")

    val (isKtpTargeted, isSelfieTargeted) = when {
        isRevisionNeeded && mentionsKtp && !mentionsSelfie -> Pair(true, false)
        isRevisionNeeded && mentionsSelfie && !mentionsKtp -> Pair(false, true)
        isRevisionNeeded -> Pair(true, true)
        else -> Pair(false, false)
    }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            if (currentCameraTarget == "ktp") {
                onKtpSelected(uri, null)
            } else if (currentCameraTarget == "selfie") {
                onSelfieSelected(uri, null)
            }
        }
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success && tempCameraUri != null) {
            if (currentCameraTarget == "ktp") {
                onKtpSelected(tempCameraUri, null)
            } else if (currentCameraTarget == "selfie") {
                onSelfieSelected(tempCameraUri, null)
            }
        }
    }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            val target = currentCameraTarget
            try {
                val uri = ImageCompressorHelper.createTempPictureUri(context, prefix = "${target}_revisi_")
                tempCameraUri = uri
                cameraLauncher.launch(uri)
            } catch (e: Exception) {
                galleryLauncher.launch("image/*")
            }
        } else {
            Toast.makeText(context, "Izin kamera diperlukan untuk mengambil foto", Toast.LENGTH_LONG).show()
        }
    }

    val launchNativeCamera: (String) -> Unit = { target ->
        currentCameraTarget = target
        val hasPermission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED

        if (hasPermission) {
            try {
                val uri = ImageCompressorHelper.createTempPictureUri(context, prefix = "${target}_revisi_")
                tempCameraUri = uri
                cameraLauncher.launch(uri)
            } catch (e: Exception) {
                galleryLauncher.launch("image/*")
            }
        } else {
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    val launchGallery: (String) -> Unit = { target ->
        currentCameraTarget = target
        galleryLauncher.launch("image/*")
    }

    val isFormReadyToSubmit = when {
        isKtpTargeted && isSelfieTargeted -> {
            (ktpUri != null || ktpBitmap != null) || (selfieUri != null || selfieBitmap != null)
        }
        isKtpTargeted -> {
            ktpUri != null || ktpBitmap != null
        }
        isSelfieTargeted -> {
            selfieUri != null || selfieBitmap != null
        }
        else -> (ktpUri != null || ktpBitmap != null || selfieUri != null || selfieBitmap != null)
    }

    Scaffold(
        containerColor = Background,
        topBar = {
            KycDetailTopBar(
                title = if (isRevisionNeeded) "Revisi Dokumen Identitas" else "Verifikasi Identitas",
                subtitle = if (isRevisionNeeded) "Perbarui dokumen sesuai catatan verifikator" else "Dokumen identitas terdaftar",
                onBackClick = onBackClick,
                badgeText = if (isRevisionNeeded) "Perlu Revisi" else "Terunggah",
                badgeVariant = if (isRevisionNeeded) BadgeVariant.Warning else BadgeVariant.Success
            )
        },
        bottomBar = {
            if (isRevisionNeeded) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding(),
                    shadowElevation = 8.dp,
                    color = Surface,
                    border = BorderStroke(1.dp, Border)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Button(
                            text = "Kirim Dokumen Revisi",
                            onClick = { showConfirmDialog = true },
                            isLoading = isSubmitting,
                            enabled = isFormReadyToSubmit && !isSubmitting,
                            modifier = Modifier.fillMaxWidth(),
                            size = ButtonSize.LG
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 18.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Verifier Note Alert Card
            if (isRevisionNeeded && !profile?.catatanVerifikasi.isNullOrBlank()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Warning0),
                    border = BorderStroke(1.dp, Warning20)
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(
                            imageVector = Lucide.CircleAlert,
                            contentDescription = null,
                            tint = Warning,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Catatan Tim Verifikator:",
                                fontSize = 12.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = Warning80
                            )
                            Spacer(modifier = Modifier.height(3.dp))
                            Text(
                                text = "\"${profile?.catatanVerifikasi}\"",
                                fontSize = 13.sp,
                                color = Warning70,
                                lineHeight = 18.sp
                            )
                        }
                    }
                }
            }

            // 1. KTP Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Surface),
                border = BorderStroke(1.dp, Border)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Lucide.IdCard, contentDescription = null, tint = Primary, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "1. Foto Fisik e-KTP",
                                fontSize = 14.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        }

                        if (isRevisionNeeded) {
                            if (isKtpTargeted) {
                                Badge(text = "Perlu Diunggah Ulang", variant = BadgeVariant.Error, size = BadgeSize.SM)
                            } else {
                                Badge(text = "Sudah Sesuai", variant = BadgeVariant.Success, size = BadgeSize.SM)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    DocumentUploadOrPreviewBox(
                        title = "Foto e-KTP",
                        isEditable = isRevisionNeeded && isKtpTargeted,
                        existingUrl = profile?.fotoKtp,
                        newUri = ktpUri,
                        newBitmap = ktpBitmap,
                        onGalleryClick = { launchGallery("ktp") },
                        onCameraClick = { launchNativeCamera("ktp") },
                        onClearClick = { onKtpSelected(null, null) }
                    )
                }
            }

            // 2. Selfie Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Surface),
                border = BorderStroke(1.dp, Border)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Lucide.Camera, contentDescription = null, tint = Primary, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "2. Foto Selfie Wajah",
                                fontSize = 14.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        }

                        if (isRevisionNeeded) {
                            if (isSelfieTargeted) {
                                Badge(text = "Perlu Diunggah Ulang", variant = BadgeVariant.Error, size = BadgeSize.SM)
                            } else {
                                Badge(text = "Sudah Sesuai", variant = BadgeVariant.Success, size = BadgeSize.SM)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    DocumentUploadOrPreviewBox(
                        title = "Foto Selfie",
                        isEditable = isRevisionNeeded && isSelfieTargeted,
                        existingUrl = profile?.fotoSelfie,
                        newUri = selfieUri,
                        newBitmap = selfieBitmap,
                        onGalleryClick = { launchGallery("selfie") },
                        onCameraClick = { launchNativeCamera("selfie") },
                        onClearClick = { onSelfieSelected(null, null) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
        }
    }

    ConfirmationDialog(
        visible = showConfirmDialog,
        title = "Kirim Dokumen Revisi?",
        message = "Pastikan foto dokumen sudah jelas dan tidak buram sebelum dikirim.",
        confirmButtonText = "Ya, Kirim Revisi",
        dismissButtonText = "Periksa Kembali",
        type = DialogType.INFO,
        isLoading = isSubmitting,
        onConfirm = {
            showConfirmDialog = false
            onSubmitRevision()
        },
        onDismiss = {
            if (!isSubmitting) {
                showConfirmDialog = false
            }
        }
    )
}
// 7. HELPER COMPONENTS & UTILITIES
@Composable
private fun DocumentUploadOrPreviewBox(
    title: String,
    isEditable: Boolean,
    existingUrl: String?,
    newUri: Uri?,
    newBitmap: Bitmap?,
    onGalleryClick: () -> Unit,
    onCameraClick: () -> Unit,
    onClearClick: () -> Unit
) {
    val context = LocalContext.current
    val hasNewImage = newUri != null || newBitmap != null
    val hasExisting = !existingUrl.isNullOrBlank()

    var isImageLoading by remember { mutableStateOf(false) }
    var isImageError by remember { mutableStateOf(false) }

    Column {
        if (hasNewImage || hasExisting) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(190.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(Neutral10)
                    .border(1.dp, Border, RoundedCornerShape(14.dp)),
                contentAlignment = Alignment.Center
            ) {
                when {
                    newUri != null -> {
                        AsyncImage(
                            model = newUri,
                            contentDescription = title,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                    newBitmap != null -> {
                        AsyncImage(
                            model = newBitmap,
                            contentDescription = title,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                    hasExisting -> {
                        val cleanUrl = resolveMediaUrl(existingUrl)
                        AsyncImage(
                            model = ImageRequest.Builder(context)
                                .data(cleanUrl)
                                .crossfade(true)
                                .build(),
                            contentDescription = title,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize(),
                            onLoading = {
                                isImageLoading = true
                                isImageError = false
                            },
                            onSuccess = {
                                isImageLoading = false
                                isImageError = false
                            },
                            onError = {
                                isImageLoading = false
                                isImageError = true
                            }
                        )

                        if (isImageLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(32.dp),
                                color = Primary,
                                strokeWidth = 2.5.dp
                            )
                        }

                        if (isImageError) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center,
                                modifier = Modifier.padding(12.dp)
                            ) {
                                Icon(
                                    imageVector = Lucide.LucideImage,
                                    contentDescription = null,
                                    tint = TextMuted,
                                    modifier = Modifier.size(36.dp)
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "Dokumen $title Terdaftar",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = TextPrimary,
                                    textAlign = TextAlign.Center
                                )
                                Text(
                                    text = existingUrl?.substringAfterLast('/') ?: "",
                                    fontSize = 10.5.sp,
                                    color = TextMuted,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }

                if (isEditable && hasNewImage) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(10.dp)
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(Color.Black.copy(alpha = 0.65f))
                            .clickable { onClearClick() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Lucide.Trash2,
                            contentDescription = "Hapus",
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        } else {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(Background)
                    .border(1.dp, Border, RoundedCornerShape(14.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Belum ada dokumen yang dipilih",
                    fontSize = 12.5.sp,
                    color = TextMuted
                )
            }
        }

        if (isEditable) {
            Spacer(modifier = Modifier.height(12.dp))
            Row(modifier = Modifier.fillMaxWidth()) {
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .clickable { onGalleryClick() },
                    color = Surface,
                    border = BorderStroke(1.dp, Border),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(vertical = 10.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Lucide.LucideImage,
                            contentDescription = null,
                            tint = Primary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Pilih Galeri",
                            fontSize = 12.5.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TextPrimary
                        )
                    }
                }

                Spacer(modifier = Modifier.width(10.dp))

                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .clickable { onCameraClick() },
                    color = Surface,
                    border = BorderStroke(1.dp, Border),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(vertical = 10.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Lucide.Camera,
                            contentDescription = null,
                            tint = Primary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Kamera HP",
                            fontSize = 12.5.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TextPrimary
                        )
                    }
                }
            }
        }
    }
}

enum class VerificationIconType {
    SUCCESS,
    PENDING,
    REVISION
}

@Composable
private fun VerificationInteractiveItemRow(
    title: String,
    subtitle: String,
    iconType: VerificationIconType,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 18.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Status Icon Circle
        when (iconType) {
            VerificationIconType.SUCCESS -> {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(Success0),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Lucide.Check,
                        contentDescription = "Selesai",
                        tint = Success,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
            VerificationIconType.PENDING -> {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(Warning0),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(Warning)
                    )
                }
            }
            VerificationIconType.REVISION -> {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(Error0),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Lucide.CircleAlert,
                        contentDescription = "Perlu Revisi",
                        tint = Error,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.width(14.dp))

        // Title & Description
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 14.5.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(3.dp))
            Text(
                text = subtitle,
                fontSize = 12.sp,
                color = if (iconType == VerificationIconType.REVISION) Error else TextSecondary,
                lineHeight = 16.sp,
                fontWeight = if (iconType == VerificationIconType.REVISION) FontWeight.SemiBold else FontWeight.Normal
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        // Chevron Right
        Icon(
            imageVector = Lucide.ChevronRight,
            contentDescription = null,
            tint = TextMuted,
            modifier = Modifier.size(18.dp)
        )
    }
}

@Composable
private fun DetailFieldItem(
    label: String,
    value: String,
    isHighlight: Boolean = false
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp)
    ) {
        Text(
            text = label,
            fontSize = 11.5.sp,
            color = TextMuted,
            fontWeight = FontWeight.Medium
        )
        Spacer(modifier = Modifier.height(3.dp))
        Text(
            text = value,
            fontSize = if (isHighlight) 13.5.sp else 13.sp,
            fontWeight = if (isHighlight) FontWeight.Bold else FontWeight.SemiBold,
            color = if (isHighlight) Primary else TextPrimary,
            lineHeight = 18.5.sp
        )
    }
}

private fun resolveMediaUrl(path: String?): String {
    if (path.isNullOrBlank()) return ""
    if (path.startsWith("http://") || path.startsWith("https://")) return path
    val baseServer = ApiConstants.BASE_URL
        .removeSuffix("/api/")
        .removeSuffix("/api")
        .trimEnd('/')
    val clean = path.replace("\\", "/").trimStart('/')
    val fullPath = if (clean.startsWith("uploads/")) clean else "uploads/$clean"
    return "$baseServer/$fullPath"
}

private fun formatStatusPekerjaan(status: String?): String {
    return com.example.saku.app.core.util.EnumLabelUtils.formatStatusPekerjaan(status)
}

private fun formatIndoDate(dateStr: String?): String {
    if (dateStr.isNullOrBlank()) return "-"
    return try {
        val clean = dateStr.substringBefore("T")
        val parts = clean.split("-")
        if (parts.size == 3) {
            val year = parts[0]
            val monthNum = parts[1].toIntOrNull() ?: 1
            val day = parts[2].toIntOrNull() ?: 1
            val monthNames = listOf("Januari", "Februari", "Maret", "April", "Mei", "Juni", "Juli", "Agustus", "September", "Oktober", "November", "Desember")
            val monthName = monthNames.getOrElse(monthNum - 1) { "Bulan" }
            "$day $monthName $year"
        } else {
            dateStr
        }
    } catch (e: Exception) {
        dateStr
    }
}