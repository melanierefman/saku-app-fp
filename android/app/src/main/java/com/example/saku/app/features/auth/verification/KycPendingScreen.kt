package com.example.saku.app.features.auth.verification

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.example.saku.app.core.ui.components.ConfirmationDialog
import com.example.saku.app.core.ui.components.DialogType
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.composables.icons.lucide.ArrowLeft
import com.composables.icons.lucide.Briefcase
import com.composables.icons.lucide.Camera
import com.composables.icons.lucide.Check
import com.composables.icons.lucide.ChevronRight
import com.composables.icons.lucide.CircleAlert
import com.composables.icons.lucide.CircleCheck
import com.composables.icons.lucide.Clock
import com.composables.icons.lucide.CreditCard
import com.composables.icons.lucide.ExternalLink
import com.composables.icons.lucide.FileText
import com.composables.icons.lucide.IdCard
import com.composables.icons.lucide.Image as LucideImage
import com.composables.icons.lucide.Info
import com.composables.icons.lucide.Lock
import com.composables.icons.lucide.LogOut
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Mail
import com.composables.icons.lucide.MapPin
import com.composables.icons.lucide.Phone
import com.composables.icons.lucide.RefreshCw
import com.composables.icons.lucide.ShieldAlert
import com.composables.icons.lucide.ShieldCheck
import com.composables.icons.lucide.Trash2
import com.composables.icons.lucide.User
import com.composables.icons.lucide.X
import com.example.saku.app.R
import com.example.saku.app.core.network.ApiConstants
import com.example.saku.app.core.network.dto.AlamatDetailDto
import com.example.saku.app.core.network.dto.CustomerProfileDto
import com.example.saku.app.core.ui.components.Button
import com.example.saku.app.core.ui.components.ButtonSize
import com.example.saku.app.core.ui.components.ButtonVariant
import com.example.saku.app.core.util.ImageCompressorHelper
import com.example.saku.app.ui.theme.Border
import com.example.saku.app.ui.theme.Error
import com.example.saku.app.ui.theme.Error0
import com.example.saku.app.ui.theme.Neutral0
import com.example.saku.app.ui.theme.Neutral10
import com.example.saku.app.ui.theme.Neutral60
import com.example.saku.app.ui.theme.Primary
import com.example.saku.app.ui.theme.Primary0
import com.example.saku.app.ui.theme.Primary60
import com.example.saku.app.ui.theme.Success
import com.example.saku.app.ui.theme.Success0
import com.example.saku.app.ui.theme.TextMuted
import com.example.saku.app.ui.theme.TextPrimary
import com.example.saku.app.ui.theme.TextSecondary
import com.example.saku.app.ui.theme.Warning0
import com.example.saku.app.ui.theme.Warning60
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import java.text.NumberFormat
import java.util.Locale

enum class ActiveModalSheet {
    NONE,
    DATA_AKUN,
    DATA_DIRI_KEUANGAN,
    INFORMASI_ALAMAT,
    VERIFIKASI_IDENTITAS
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KycPendingScreen(
    onNavigateToHome: () -> Unit,
    onNavigateToLogin: () -> Unit,
    viewModel: KycPendingViewModel = koinViewModel()
) {
    val context = LocalContext.current
    val statusState by viewModel.statusState.collectAsState()
    val profile by viewModel.profile.collectAsState()
    val isSubmittingRevision by viewModel.isSubmittingRevision.collectAsState()

    val ktpBitmap by viewModel.ktpBitmap.collectAsState()
    val ktpUri by viewModel.ktpUri.collectAsState()
    val selfieBitmap by viewModel.selfieBitmap.collectAsState()
    val selfieUri by viewModel.selfieUri.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()

    var activeSheet by remember { mutableStateOf(ActiveModalSheet.NONE) }
    var showPendingInfoDialog by remember { mutableStateOf(false) }
    var showApprovedDialog by remember { mutableStateOf(false) }
    var countdownSeconds by remember { mutableStateOf(3) }

    val rawStatus = (profile?.statusVerifikasi ?: "").uppercase()
    val isRevisionNeeded = rawStatus.contains("REVISI") || rawStatus.contains("REVISION") || rawStatus.contains("REJECTED") || rawStatus.contains("DITOLAK")

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

    // Timer countdown 3 detik untuk auto-redirect ke halaman Login saat akun berhasil diverifikasi
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

    // Modal Sukses Akun Terverifikasi (Auto redirect ke Login dalam 3 detik / Tombol Manual)
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
                        .background(Color(0xFFDCFCE7)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Lucide.CircleCheck,
                        contentDescription = null,
                        tint = Color(0xFF16A34A),
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
                        text = "Selamat! Pendaftaran akun dan dokumen identitas Anda telah disetujui oleh tim Backoffice SAKU. Silakan login kembali untuk mulai mengajukan pinjaman.",
                        color = TextSecondary,
                        fontSize = 13.5.sp,
                        lineHeight = 19.sp,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFFF3F4F6))
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
            containerColor = Color.White,
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
                        .background(Color(0xFFFEF3C7)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Lucide.Clock,
                        contentDescription = null,
                        tint = Color(0xFFD97706),
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
                    text = "Dokumen dan data Anda saat ini sedang diperiksa secara manual oleh tim Backoffice SAKU. Estimasi pemeriksaan maksimal 1 jam pada hari kerja.",
                    color = TextSecondary,
                    fontSize = 13.5.sp,
                    lineHeight = 19.sp,
                    textAlign = TextAlign.Center
                )
            },
            containerColor = Color.White,
            shape = RoundedCornerShape(20.dp)
        )
    }

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
                    IconButton(
                        onClick = {
                            viewModel.logout(onSuccess = onNavigateToLogin)
                        }
                    ) {
                        Icon(
                            imageVector = Lucide.LogOut,
                            contentDescription = "Keluar",
                            tint = TextSecondary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        bottomBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .navigationBarsPadding()
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Primary Action: Refresh Status
                Button(
                    text = "Cek Status Verifikasi",
                    onClick = {
                        viewModel.checkVerificationStatus(
                            silent = false,
                        )
                    },
                    isLoading = statusState is KycStatusState.Checking,
                    variant = ButtonVariant.Primary,
                    size = ButtonSize.LG,
                    fullWidth = true,
                    leadingIcon = Lucide.RefreshCw
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Secondary Action: Logout
                TextButton(
                    onClick = {
                        viewModel.logout(onSuccess = onNavigateToLogin)
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Keluar ke Halaman Login",
                        color = TextSecondary,
                        fontSize = 13.5.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(scrollState)
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(8.dp))

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
                    "Tim Backoffice meminta perbaikan pada dokumen identitas Anda. Klik pada Verifikasi Identitas di bawah untuk mengunggah ulang dokumen."
                } else {
                    "Akun Anda sedang dalam proses verifikasi oleh tim Backoffice SAKU. Estimasi pemeriksaan maksimal 1 jam pada hari kerja."
                },
                fontSize = 13.5.sp,
                color = TextSecondary,
                lineHeight = 19.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            // Warning Banner if Revision Needed
            if (isRevisionNeeded && !profile?.catatanVerifikasi.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(16.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Warning0),
                    border = BorderStroke(1.dp, Color(0xFFFCD34D))
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(
                            imageVector = Lucide.CircleAlert,
                            contentDescription = null,
                            tint = Color(0xFFD97706),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Catatan Tim Verifikator Backoffice:",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF92400E)
                            )
                            Spacer(modifier = Modifier.height(3.dp))
                            Text(
                                text = "\"${profile?.catatanVerifikasi}\"",
                                fontSize = 13.sp,
                                color = Color(0xFF78350F),
                                lineHeight = 18.sp
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 4-Step Verification Status Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.2.dp, if (isRevisionNeeded) Color(0xFFFCD34D) else Color(0xFFFFDFC7))
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    // Item 1: Data Akun Baru (Read-only preview)
                    VerificationInteractiveItemRow(
                        title = "Data Akun Baru",
                        subtitle = "Data sudah lengkap",
                        iconType = VerificationIconType.SUCCESS,
                        onClick = { activeSheet = ActiveModalSheet.DATA_AKUN }
                    )

                    HorizontalDivider(
                        color = Color(0xFFFFF1E6),
                        thickness = 1.dp
                    )

                    // Item 2: Data Diri & Keuangan (Read-only preview)
                    VerificationInteractiveItemRow(
                        title = "Data Diri & Keuangan",
                        subtitle = "Data sudah lengkap",
                        iconType = VerificationIconType.SUCCESS,
                        onClick = { activeSheet = ActiveModalSheet.DATA_DIRI_KEUANGAN }
                    )

                    HorizontalDivider(
                        color = Color(0xFFFFF1E6),
                        thickness = 1.dp
                    )

                    // Item 3: Informasi Alamat (Read-only preview)
                    VerificationInteractiveItemRow(
                        title = "Informasi Alamat",
                        subtitle = "Data sudah lengkap",
                        iconType = VerificationIconType.SUCCESS,
                        onClick = { activeSheet = ActiveModalSheet.INFORMASI_ALAMAT }
                    )

                    HorizontalDivider(
                        color = Color(0xFFFFF1E6),
                        thickness = 1.dp
                    )

                    // Item 4: Verifikasi Identitas (Interactive Revision if needed, or Preview)
                    VerificationInteractiveItemRow(
                        title = "Verifikasi Identitas",
                        subtitle = if (isRevisionNeeded) {
                            "Perlu revisi berkas foto KTP / Selfie (Klik untuk edit)"
                        } else {
                            "Dokumen foto identitas & selfie sudah diunggah"
                        },
                        iconType = if (isRevisionNeeded) VerificationIconType.REVISION else VerificationIconType.PENDING,
                        onClick = { activeSheet = ActiveModalSheet.VERIFIKASI_IDENTITAS }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    // Modal Bottom Sheets for Data Preview & Revision
    when (activeSheet) {
        ActiveModalSheet.DATA_AKUN -> {
            DataAkunPreviewSheet(
                profile = profile,
                onDismiss = { activeSheet = ActiveModalSheet.NONE }
            )
        }
        ActiveModalSheet.DATA_DIRI_KEUANGAN -> {
            DataDiriKeuanganPreviewSheet(
                profile = profile,
                onDismiss = { activeSheet = ActiveModalSheet.NONE }
            )
        }
        ActiveModalSheet.INFORMASI_ALAMAT -> {
            InformasiAlamatPreviewSheet(
                profile = profile,
                onDismiss = { activeSheet = ActiveModalSheet.NONE }
            )
        }
        ActiveModalSheet.VERIFIKASI_IDENTITAS -> {
            VerifikasiIdentitasSheet(
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
                        activeSheet = ActiveModalSheet.NONE
                    }
                },
                onDismiss = { activeSheet = ActiveModalSheet.NONE }
            )
        }
        ActiveModalSheet.NONE -> {}
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
                        .background(Color(0xFFDCFCE7)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Lucide.Check,
                        contentDescription = "Selesai",
                        tint = Color(0xFF16A34A),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
            VerificationIconType.PENDING -> {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFFEF9C3)),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFCA8A04))
                    )
                }
            }
            VerificationIconType.REVISION -> {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFFEE2E2)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Lucide.CircleAlert,
                        contentDescription = "Perlu Revisi",
                        tint = Color(0xFFDC2626),
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
                color = if (iconType == VerificationIconType.REVISION) Color(0xFFDC2626) else TextSecondary,
                lineHeight = 16.sp,
                fontWeight = if (iconType == VerificationIconType.REVISION) FontWeight.SemiBold else FontWeight.Normal
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        // Chevron Right
        Icon(
            imageVector = Lucide.ChevronRight,
            contentDescription = null,
            tint = Color(0xFF9CA3AF),
            modifier = Modifier.size(18.dp)
        )
    }
}

// 1. DATA AKUN PREVIEW SHEET
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DataAkunPreviewSheet(
    profile: CustomerProfileDto?,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = Color.White
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 32.dp)
                .verticalScroll(rememberScrollState())
        ) {
            SheetHeader(
                title = "Data Akun Baru",
                subtitle = "Informasi akun nasabah terdaftar (Hanya Baca)",
                icon = Lucide.User
            )

            Spacer(modifier = Modifier.height(20.dp))

            PreviewDetailCard {
                PreviewFieldItem(label = "Alamat Email", value = profile?.email ?: "-")
                PreviewFieldItem(label = "Username", value = profile?.username ?: "-")
                PreviewFieldItem(label = "Nomor Handphone", value = profile?.noHp ?: "-")
                PreviewFieldItem(label = "Status Registrasi", value = "Selesai Terverifikasi OTP")
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                text = "Tutup",
                onClick = onDismiss,
                variant = ButtonVariant.Secondary,
                fullWidth = true
            )
        }
    }
}

// 2. DATA DIRI & KEUANGAN PREVIEW SHEET
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DataDiriKeuanganPreviewSheet(
    profile: CustomerProfileDto?,
    onDismiss: () -> Unit
) {
    val formatRupiah: (Double?) -> String = { amount ->
        if (amount == null) "Rp 0"
        else "Rp " + NumberFormat.getNumberInstance(Locale("id", "ID")).format(amount)
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = Color.White
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 32.dp)
                .verticalScroll(rememberScrollState())
        ) {
            SheetHeader(
                title = "Data Diri & Keuangan",
                subtitle = "Informasi identitas, pekerjaan, dan rekening (Hanya Baca)",
                icon = Lucide.CreditCard
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text(text = "Identitas Pribadi", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Primary)
            Spacer(modifier = Modifier.height(8.dp))
            PreviewDetailCard {
                PreviewFieldItem(label = "NIK e-KTP", value = profile?.nik ?: "-")
                PreviewFieldItem(label = "Nama Lengkap", value = profile?.nama ?: "-")
                PreviewFieldItem(label = "Nama Ibu Kandung", value = profile?.namaIbuKandung ?: "-")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(text = "Pekerjaan & Finansial", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Primary)
            Spacer(modifier = Modifier.height(8.dp))
            PreviewDetailCard {
                PreviewFieldItem(label = "Profesi / Pekerjaan", value = profile?.pekerjaan ?: "-")
                PreviewFieldItem(label = "Tempat Bekerja", value = profile?.tempatKerja ?: "-")
                PreviewFieldItem(label = "Status Pekerjaan", value = formatStatusPekerjaan(profile?.statusPekerjaan))
                PreviewFieldItem(label = "Penghasilan Bulanan", value = formatRupiah(profile?.penghasilanBulanan))
                PreviewFieldItem(label = "Lama Bekerja", value = "${profile?.lamaBekerjaBulan ?: 0} Bulan")
                PreviewFieldItem(label = "Cicilan Lainnya", value = formatRupiah(profile?.totalCicilanLainBulanan))
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(text = "Rekening Pencairan", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Primary)
            Spacer(modifier = Modifier.height(8.dp))
            PreviewDetailCard {
                PreviewFieldItem(label = "Bank Pencairan", value = profile?.namaBank ?: "-")
                PreviewFieldItem(label = "Nomor Rekening", value = profile?.noRekening ?: "-")
                PreviewFieldItem(label = "Nama Pemilik Rekening", value = profile?.namaRekening ?: "-")
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                text = "Tutup",
                onClick = onDismiss,
                variant = ButtonVariant.Secondary,
                fullWidth = true
            )
        }
    }
}

// 3. INFORMASI ALAMAT PREVIEW SHEET
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun InformasiAlamatPreviewSheet(
    profile: CustomerProfileDto?,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = Color.White
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 32.dp)
                .verticalScroll(rememberScrollState())
        ) {
            SheetHeader(
                title = "Informasi Alamat",
                subtitle = "Alamat e-KTP dan tempat tinggal domisili (Hanya Baca)",
                icon = Lucide.MapPin
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text(text = "Alamat Sesuai e-KTP", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Primary)
            Spacer(modifier = Modifier.height(8.dp))
            PreviewDetailCard {
                PreviewFieldItem(
                    label = "Alamat Lengkap",
                    value = profile?.alamatKtp?.formattedAddress ?: profile?.alamatKtp?.alamatLengkap ?: "-"
                )
                if (profile?.alamatKtp != null) {
                    PreviewFieldItem(label = "RT / RW", value = "RT ${profile.alamatKtp.rt ?: '-'} / RW ${profile.alamatKtp.rw ?: '-'}")
                    PreviewFieldItem(label = "Kelurahan / Kecamatan", value = "${profile.alamatKtp.kelurahan ?: '-'}, ${profile.alamatKtp.kecamatan ?: '-'}")
                    PreviewFieldItem(label = "Kota / Provinsi", value = "${profile.alamatKtp.kotaKabupaten ?: '-'}, ${profile.alamatKtp.provinsi ?: '-'}")
                    PreviewFieldItem(label = "Kode Pos", value = profile.alamatKtp.kodePos ?: "-")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(text = "Alamat Domisili Saat Ini", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Primary)
            Spacer(modifier = Modifier.height(8.dp))
            PreviewDetailCard {
                PreviewFieldItem(
                    label = "Alamat Lengkap",
                    value = profile?.alamatDomisili?.formattedAddress ?: profile?.alamatDomisili?.alamatLengkap ?: profile?.alamatKtp?.formattedAddress ?: "-"
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                text = "Tutup",
                onClick = onDismiss,
                variant = ButtonVariant.Secondary,
                fullWidth = true
            )
        }
    }
}

// Helper to resolve absolute media URL from Backend API
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

// 4. VERIFIKASI IDENTITAS & REVISI SHEET
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun VerifikasiIdentitasSheet(
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
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    var tempCameraUri by remember { mutableStateOf<Uri?>(null) }
    var currentCameraTarget by remember { mutableStateOf<String>("") }

    // Parse catatan revisi dari Backoffice untuk menentukan dokumen spesifik yang perlu diunggah ulang
    val note = (profile?.catatanVerifikasi ?: "").lowercase()
    val mentionsKtp = note.contains("ktp") || note.contains("nik") || note.contains("identitas")
    val mentionsSelfie = note.contains("selfie") || note.contains("wajah") || note.contains("liveness") || note.contains("muka")

    val (isKtpTargeted, isSelfieTargeted) = when {
        isRevisionNeeded && mentionsKtp && !mentionsSelfie -> Pair(true, false)
        isRevisionNeeded && mentionsSelfie && !mentionsKtp -> Pair(false, true)
        isRevisionNeeded -> Pair(true, true) // Bila keduanya disebut atau catatan umum
        else -> Pair(false, false)
    }

    var showConfirmDialog by remember { mutableStateOf(false) }

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
            if (target != null) {
                try {
                    val uri = ImageCompressorHelper.createTempPictureUri(context, prefix = "${target}_revisi_")
                    tempCameraUri = uri
                    cameraLauncher.launch(uri)
                } catch (e: Exception) {
                    galleryLauncher.launch("image/*")
                }
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

    // Logic enable tombol submit revisi berdasarkan dokumen yang diminta
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

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = Color.White
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 32.dp)
                .verticalScroll(rememberScrollState())
        ) {
            SheetHeader(
                title = if (isRevisionNeeded) "Revisi Dokumen Identitas" else "Verifikasi Identitas",
                subtitle = if (isRevisionNeeded) "Perbarui dokumen foto sesuai catatan revisi tim Backoffice di bawah ini" else "Dokumen foto identitas dan selfie yang telah Anda daftarkan (Hanya Baca)",
                icon = if (isRevisionNeeded) Lucide.CircleAlert else Lucide.ShieldCheck
            )

            if (isRevisionNeeded && !profile?.catatanVerifikasi.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(16.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Warning0),
                    border = BorderStroke(1.dp, Color(0xFFFCD34D))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "Catatan Verifikator Backoffice:",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF92400E)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "\"${profile?.catatanVerifikasi}\"",
                            fontSize = 13.sp,
                            color = Color(0xFF78350F),
                            lineHeight = 18.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 1. KTP UPLOAD / PREVIEW CARD
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "1. Foto Fisik e-KTP", fontSize = 13.5.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                if (isRevisionNeeded) {
                    if (isKtpTargeted) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color(0xFFFEE2E2))
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = "Perlu Diunggah Ulang",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFDC2626)
                            )
                        }
                    } else {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color(0xFFDCFCE7))
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = "Sudah Sesuai",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF16A34A)
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))

            DocumentUploadOrPreviewCard(
                title = "Foto e-KTP",
                isEditable = isRevisionNeeded && isKtpTargeted,
                existingUrl = profile?.fotoKtp,
                newUri = ktpUri,
                newBitmap = ktpBitmap,
                onGalleryClick = { launchGallery("ktp") },
                onCameraClick = { launchNativeCamera("ktp") },
                onClearClick = { onKtpSelected(null, null) }
            )

            Spacer(modifier = Modifier.height(20.dp))

            // 2. SELFIE UPLOAD / PREVIEW CARD
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "2. Foto Selfie Wajah", fontSize = 13.5.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                if (isRevisionNeeded) {
                    if (isSelfieTargeted) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color(0xFFFEE2E2))
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = "Perlu Diunggah Ulang",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFDC2626)
                            )
                        }
                    } else {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color(0xFFDCFCE7))
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = "Sudah Sesuai",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF16A34A)
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))

            DocumentUploadOrPreviewCard(
                title = "Foto Selfie",
                isEditable = isRevisionNeeded && isSelfieTargeted,
                existingUrl = profile?.fotoSelfie,
                newUri = selfieUri,
                newBitmap = selfieBitmap,
                onGalleryClick = { launchGallery("selfie") },
                onCameraClick = { launchNativeCamera("selfie") },
                onClearClick = { onSelfieSelected(null, null) }
            )

            Spacer(modifier = Modifier.height(24.dp))

            if (isRevisionNeeded) {
                Button(
                    text = "Kirim Ulang Dokumen Revisi",
                    onClick = { showConfirmDialog = true },
                    isLoading = isSubmitting,
                    enabled = isFormReadyToSubmit && !isSubmitting,
                    variant = ButtonVariant.Primary,
                    size = ButtonSize.LG,
                    fullWidth = true
                )
            } else {
                Button(
                    text = "Tutup",
                    onClick = onDismiss,
                    variant = ButtonVariant.Secondary,
                    fullWidth = true
                )
            }
        }
    }

    ConfirmationDialog(
        visible = showConfirmDialog,
        title = "Kirim Dokumen Revisi?",
        message = "Pastikan foto dokumen fisik yang Anda unggah sudah jelas, tidak buram, dan sesuai instruksi. Dokumen akan langsung diproses ulang oleh tim verifikasi SAKU.",
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

@Composable
private fun DocumentUploadOrPreviewCard(
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

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFBFBFC)),
        border = BorderStroke(1.dp, Color(0xFFE5E7EB))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            if (hasNewImage || hasExisting) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFFF3F4F6))
                        .border(1.dp, Color(0xFFE5E7EB), RoundedCornerShape(10.dp)),
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
                                    modifier = Modifier.size(28.dp),
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
                                        tint = Color(0xFF9CA3AF),
                                        modifier = Modifier.size(32.dp)
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
                                        color = TextSecondary,
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
                                .padding(8.dp)
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(Color.Black.copy(alpha = 0.6f))
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
                        .height(120.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFFF9FAFB))
                        .border(1.dp, Color(0xFFE5E7EB), RoundedCornerShape(10.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Belum ada dokumen yang dipilih",
                        fontSize = 12.sp,
                        color = TextMuted
                    )
                }
            }

            if (isEditable) {
                Spacer(modifier = Modifier.height(12.dp))
                Row(modifier = Modifier.fillMaxWidth()) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.White)
                            .border(1.dp, Color(0xFFE5E7EB), RoundedCornerShape(8.dp))
                            .clickable { onGalleryClick() }
                            .padding(vertical = 9.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Lucide.LucideImage,
                                contentDescription = null,
                                tint = Primary,
                                modifier = Modifier.size(15.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Pilih Galeri",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = TextPrimary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.White)
                            .border(1.dp, Color(0xFFE5E7EB), RoundedCornerShape(8.dp))
                            .clickable { onCameraClick() }
                            .padding(vertical = 9.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Lucide.Camera,
                                contentDescription = null,
                                tint = Primary,
                                modifier = Modifier.size(15.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Kamera HP",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = TextPrimary
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SheetHeader(
    title: String,
    subtitle: String,
    icon: ImageVector
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(Primary0),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Primary,
                modifier = Modifier.size(22.dp)
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                fontSize = 12.sp,
                color = TextSecondary,
                lineHeight = 16.sp
            )
        }
    }
}

@Composable
private fun PreviewDetailCard(content: @Composable () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFBFBFC)),
        border = BorderStroke(1.dp, Color(0xFFE5E7EB))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 8.dp)
        ) {
            content()
        }
    }
}

@Composable
private fun PreviewFieldItem(label: String, value: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
    ) {
        Text(
            text = label,
            fontSize = 11.5.sp,
            color = TextSecondary,
            fontWeight = FontWeight.Medium
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = value,
            fontSize = 13.sp,
            color = TextPrimary,
            fontWeight = FontWeight.SemiBold
        )
    }
}

private fun formatStatusPekerjaan(status: String?): String {
    return when (status?.uppercase()) {
        "KARYAWAN_TETAP" -> "Karyawan Tetap"
        "KARYAWAN_KONTRAK" -> "Karyawan Kontrak"
        "WIRAUSAHA", "PENGUSAHA" -> "Wirausaha / Pengusaha"
        "PROFESIONAL" -> "Profesional"
        "PNS", "PNS_BUMN" -> "PNS / Pegawai BUMN"
        "IBU_RUMAH_TANGGA" -> "Ibu Rumah Tangga"
        else -> status ?: "-"
    }
}
