package com.example.saku.app.features.loans.apply

import android.Manifest
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import coil.compose.AsyncImage
import com.composables.icons.lucide.ArrowLeft
import com.composables.icons.lucide.Camera
import com.composables.icons.lucide.CircleAlert
import com.composables.icons.lucide.CircleCheck
import com.composables.icons.lucide.Copy
import com.composables.icons.lucide.FileCheck
import com.composables.icons.lucide.FileText
import com.composables.icons.lucide.Image as ImageIcon
import com.composables.icons.lucide.Info
import com.composables.icons.lucide.Landmark
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.RotateCw
import com.composables.icons.lucide.Trash2
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import com.example.saku.app.R
import com.example.saku.app.core.ui.components.Badge
import com.example.saku.app.core.ui.components.BadgeSize
import com.example.saku.app.core.ui.components.BadgeVariant
import com.example.saku.app.core.ui.components.Button
import com.example.saku.app.core.ui.components.ButtonSize
import com.example.saku.app.core.ui.components.ButtonVariant
import com.example.saku.app.core.ui.components.CameraCaptureMode
import com.example.saku.app.core.ui.components.ConfirmationDialog
import com.example.saku.app.core.ui.components.DialogType
import com.example.saku.app.core.ui.components.TextField
import com.example.saku.app.core.util.ImageCompressorHelper
import com.example.saku.app.ui.theme.Background
import com.example.saku.app.ui.theme.Border
import com.example.saku.app.ui.theme.Error
import com.example.saku.app.ui.theme.Error0
import com.example.saku.app.ui.theme.Error20
import com.example.saku.app.ui.theme.Error70
import com.example.saku.app.ui.theme.Neutral0
import com.example.saku.app.ui.theme.Neutral10
import com.example.saku.app.ui.theme.Neutral20
import com.example.saku.app.ui.theme.OverusedGrotesk
import com.example.saku.app.ui.theme.Primary
import com.example.saku.app.ui.theme.Primary0
import com.example.saku.app.ui.theme.Primary20
import com.example.saku.app.ui.theme.Primary70
import com.example.saku.app.ui.theme.Primary80
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
import java.text.NumberFormat

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoanApplyScreen(
    initialAmount: Double? = null,
    initialTenor: Int? = null,
    onNavigateBack: () -> Unit,
    onNavigateToHome: () -> Unit,
    onNavigateToDetail: (String) -> Unit,
    viewModel: LoanApplyViewModel = koinViewModel()
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    val currencyFormatter = remember { NumberFormat.getNumberInstance(Locale.forLanguageTag("id-ID")) }

    LaunchedEffect(initialAmount, initialTenor) {
        if (initialAmount != null || initialTenor != null) {
            viewModel.initSimulationData(initialAmount, initialTenor)
        }
    }

    // Intercept back button when in wizard steps
    BackHandler {
        if (uiState.currentStep in 2..3) {
            viewModel.goToPreviousStep()
        } else if (uiState.currentStep == 4) {
            onNavigateToHome()
        } else {
            onNavigateBack()
        }
    }

    // Native Camera Launcher for Step 2
    var pendingCameraUri by remember { mutableStateOf<Uri?>(null) }
    var pendingCameraDocType by remember { mutableStateOf<String?>(null) }

    val nativeCameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success: Boolean ->
        if (success && pendingCameraUri != null) {
            val uri = pendingCameraUri!!
            when (pendingCameraDocType) {
                "SLIP_GAJI" -> viewModel.setSlipGaji(uri, null)
                "REK_KORAN" -> viewModel.setRekeningKoran(uri, null)
                "NPWP" -> viewModel.setNpwp(uri, null)
            }
        }
        pendingCameraUri = null
        pendingCameraDocType = null
    }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            val docType = pendingCameraDocType
            if (docType != null) {
                try {
                    val prefix = when (docType) {
                        "SLIP_GAJI" -> "slip_gaji_"
                        "REK_KORAN" -> "rek_koran_"
                        else -> "npwp_"
                    }
                    val tempUri = ImageCompressorHelper.createTempPictureUri(context, prefix)
                    pendingCameraUri = tempUri
                    nativeCameraLauncher.launch(tempUri)
                } catch (e: Exception) {
                    Toast.makeText(context, "Gagal membuka kamera: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            Toast.makeText(context, "Izin kamera diperlukan untuk mengambil foto dokumen", Toast.LENGTH_LONG).show()
            pendingCameraDocType = null
        }
    }

    val launchNativeCamera = { docType: String ->
        pendingCameraDocType = docType
        val hasPermission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED

        if (hasPermission) {
            try {
                val prefix = when (docType) {
                    "SLIP_GAJI" -> "slip_gaji_"
                    "REK_KORAN" -> "rek_koran_"
                    else -> "npwp_"
                }
                val tempUri = ImageCompressorHelper.createTempPictureUri(context, prefix)
                pendingCameraUri = tempUri
                nativeCameraLauncher.launch(tempUri)
            } catch (e: Exception) {
                Toast.makeText(context, "Gagal membuka kamera: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        } else {
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    // Modal Konfirmasi Pengajuan Pinjaman State for Step 3
    var showSubmitConfirmDialog by remember { mutableStateOf(false) }

    // Gallery Picker Launchers for Step 2
    val slipGajiPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            viewModel.setSlipGaji(uri, null)
        }
    }

    val rekKoranPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            viewModel.setRekeningKoran(uri, null)
        }
    }

    val npwpPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            viewModel.setNpwp(uri, null)
        }
    }

    Scaffold(
        topBar = {
            if (uiState.currentStep < 4) {
                ApplyTopBar(
                    currentStep = uiState.currentStep,
                    onBackClick = {
                        if (uiState.currentStep in 2..3) viewModel.goToPreviousStep() else onNavigateBack()
                    }
                )
            }
        },
        bottomBar = {
            if (uiState.currentStep < 4) {
                val isStep1Valid = uiState.availablePlafond >= 500_000.0 &&
                    uiState.jumlahPinjaman >= 500_000.0 &&
                    uiState.jumlahPinjaman <= uiState.availablePlafond &&
                    uiState.effectiveTujuan.isNotBlank()
                ApplyBottomActionBar(
                    currentStep = uiState.currentStep,
                    isLoading = uiState.isLoading,
                    isTncAgreed = uiState.isTncAgreed,
                    isStep1Valid = isStep1Valid,
                    step1HasPurpose = uiState.effectiveTujuan.isNotBlank(),
                    isPlafondSufficient = uiState.availablePlafond >= 500_000.0,
                    onNextStep1 = { viewModel.submitStep1() },
                    onNextStep2 = { viewModel.proceedToStep3Summary() },
                    onSubmitFinal = { showSubmitConfirmDialog = true },
                    onPrevious = { viewModel.goToPreviousStep() }
                )
            }
        },
        containerColor = Background
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Crossfade(
                targetState = uiState.currentStep,
                animationSpec = tween(250),
                label = "apply_step_crossfade"
            ) { step ->
                when (step) {
                    1 -> Step1NominalTenorView(
                        uiState = uiState,
                        currencyFormatter = currencyFormatter,
                        onAmountChange = viewModel::setJumlahPinjaman,
                        onTenorChange = viewModel::setTenorBulan,
                        onTujuanChange = viewModel::setTujuanPinjaman,
                        onCustomTujuanChange = viewModel::setCustomTujuan
                    )
                    2 -> Step2UploadDokumenView(
                        uiState = uiState,
                        onCaptureSlipGaji = { launchNativeCamera("SLIP_GAJI") },
                        onPickSlipGaji = { slipGajiPickerLauncher.launch("*/*") },
                        onRemoveSlipGaji = { viewModel.setSlipGaji(null, null) },
                        onCaptureRekKoran = { launchNativeCamera("REK_KORAN") },
                        onPickRekKoran = { rekKoranPickerLauncher.launch("*/*") },
                        onRemoveRekKoran = { viewModel.setRekeningKoran(null, null) },
                        onCaptureNpwp = { launchNativeCamera("NPWP") },
                        onPickNpwp = { npwpPickerLauncher.launch("*/*") },
                        onRemoveNpwp = { viewModel.setNpwp(null, null) }
                    )
                    3 -> Step3SummarySubmitView(
                        uiState = uiState,
                        currencyFormatter = currencyFormatter,
                        onTncToggle = viewModel::setTncAgreed
                    )
                    4 -> Step4SuccessReceiptView(
                        uiState = uiState,
                        currencyFormatter = currencyFormatter,
                        onNavigateToHome = onNavigateToHome,
                        onNavigateToDetail = {
                            val id = uiState.pengajuanId ?: uiState.submittedLoanResult?.id
                            if (!id.isNullOrBlank()) onNavigateToDetail(id) else onNavigateToHome()
                        }
                    )
                }
            }

            // Error Banner Snack
            uiState.errorMessage?.let { error ->
                Box(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(16.dp)
                        .fillMaxWidth()
                ) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Error0),
                        border = BorderStroke(1.dp, Error20),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Lucide.CircleAlert,
                                contentDescription = null,
                                tint = Error,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = error,
                                color = Error70,
                                fontSize = 13.sp,
                                modifier = Modifier.weight(1f)
                            )
                            IconButton(
                                onClick = { viewModel.clearError() },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Text("✕", color = Error70, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }

    // Modal Konfirmasi Sebelum Ajukan Pinjaman
    ConfirmationDialog(
        visible = showSubmitConfirmDialog,
        title = "Konfirmasi Pengajuan",
        message = "Ajukan pinjaman sebesar Rp ${currencyFormatter.format(uiState.jumlahPinjaman)} dengan tenor ${uiState.tenorBulan} bulan?",
        confirmButtonText = "Ya, Ajukan",
        dismissButtonText = "Periksa Kembali",
        type = DialogType.INFO,
        icon = Lucide.FileCheck,
        isLoading = uiState.isLoading,
        onConfirm = {
            showSubmitConfirmDialog = false
            viewModel.submitFinalApplication(context)
        },
        onDismiss = {
            if (!uiState.isLoading) {
                showSubmitConfirmDialog = false
            }
        }
    )
}

// Top Bar & Stepper Header
@Composable
private fun ApplyTopBar(
    currentStep: Int,
    onBackClick: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Surface,
        border = BorderStroke(1.dp, Border),
        shadowElevation = 1.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 20.dp, vertical = 12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
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
                        text = "Ajukan Pinjaman",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = when (currentStep) {
                            1 -> "Langkah 1: Nominal & Tenor"
                            2 -> "Langkah 2: Unggah Dokumen"
                            3 -> "Langkah 3: Ringkasan & Konfirmasi"
                            else -> "Pengajuan Selesai"
                        },
                        fontSize = 12.sp,
                        color = TextMuted
                    )
                }

                Badge(
                    text = "Tahap $currentStep/3",
                    variant = BadgeVariant.Neutral,
                    size = BadgeSize.SM
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Multi-Step Progress Indicator
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                for (i in 1..3) {
                    val isFilled = i <= currentStep
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(4.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(if (isFilled) Primary else Neutral20)
                    )
                }
            }
        }
    }
}

// Step 1: Nominal & Tenor View (Diselaraskan Penuh dengan Desain Simulasi & Beranda)
@Composable
private fun Step1NominalTenorView(
    uiState: LoanApplyUiState,
    currencyFormatter: NumberFormat,
    onAmountChange: (Double) -> Unit,
    onTenorChange: (Int) -> Unit,
    onTujuanChange: (String) -> Unit,
    onCustomTujuanChange: (String) -> Unit
) {
    val minAmount = 500_000.0
    val maxAmount = uiState.availablePlafond.coerceAtLeast(minAmount)
    val stepAmount = 500_000.0

    val quickAmounts = listOf(1_000_000.0, 5_000_000.0, 10_000_000.0, 25_000_000.0, maxAmount)
        .filter { it <= maxAmount && it >= minAmount }.distinct().sorted()
    val tenorOptions = listOf(3, 6, 9, 12, 18, 24, 36)
    val minTenor = 3
    val maxTenor = 36
    val tujuanOptions = listOf("Modal Usaha", "Renovasi Rumah", "Pendidikan", "Keperluan Medis", "Elektronik", "Lainnya")

    val totalBunga = (uiState.bungaBulanan * uiState.tenorBulan).toLong()

    var amountInputText by remember(uiState.jumlahPinjaman) {
        mutableStateOf(if (uiState.jumlahPinjaman > 0) currencyFormatter.format(uiState.jumlahPinjaman.toLong()) else "")
    }

    val coroutineScope = rememberCoroutineScope()
    val listState = rememberLazyListState()
    val bringIntoViewRequester = remember { BringIntoViewRequester() }
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(uiState.tujuanPinjaman) {
        if (uiState.tujuanPinjaman == "Lainnya") {
            delay(250)
            bringIntoViewRequester.bringIntoView()
            try {
                focusRequester.requestFocus()
            } catch (e: Exception) {
                // ignore
            }
        }
    }

    LazyColumn(
        state = listState,
        modifier = Modifier
            .fillMaxSize()
            .background(Background),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 18.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Banner Notifikasi Otomatis dari Simulasi
        if (uiState.isFromSimulation) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Primary0),
                    border = BorderStroke(1.dp, Primary20)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(Primary),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Lucide.CircleCheck,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Data Simulasi Terisi Otomatis",
                                fontSize = 13.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = Primary80
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Nominal Rp ${currencyFormatter.format(uiState.jumlahPinjaman)} & tenor ${uiState.tenorBulan} bulan siap diajukan. Silakan pilih Tujuan Penggunaan Dana di bawah untuk lanjut ke upload dokumen.",
                                fontSize = 11.5.sp,
                                color = Primary70,
                                lineHeight = 16.sp
                            )
                        }
                    }
                }
            }
        }

        // 1. Plafond Header Card dengan Motif SAKU
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(
                        elevation = 8.dp,
                        shape = RoundedCornerShape(20.dp),
                        ambientColor = Color(0x14000000),
                        spotColor = Color(0x20000000)
                    ),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Primary),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.2f))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                ) {
                    // Background Image Card SAKU
                    Image(
                        painter = painterResource(id = R.drawable.bg_card_saku),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .matchParentSize()
                            .graphicsLayer {
                                scaleX = 1.35f
                                scaleY = 1.35f
                                transformOrigin = TransformOrigin(0.85f, 0.5f)
                            }
                    )

                    // Content Plafond Info
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 18.dp)
                    ) {
                        Text(
                            text = "Sisa Limit Plafon Tersedia",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.White.copy(alpha = 0.88f)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Rp ${currencyFormatter.format(uiState.availablePlafond)}",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White,
                            letterSpacing = (-0.5).sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Suku bunga ${String.format(Locale.US, "%.1f", uiState.sukuBungaPersen)}% per bulan",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.White.copy(alpha = 0.92f)
                        )
                    }
                }
            }
        }

        // Warning jika limit tidak mencukupi
        if (uiState.availablePlafond < 500_000.0) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Error0),
                    border = BorderStroke(1.dp, Error20)
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Lucide.CircleAlert,
                            contentDescription = null,
                            tint = Error,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Sisa limit plafon Anda saat ini adalah Rp 0 (atau di bawah batas minimum Rp 500.000). Anda tidak dapat mengajukan pinjaman baru saat ini.",
                            fontSize = 12.sp,
                            color = Error70,
                            lineHeight = 16.sp
                        )
                    }
                }
            }
        }

        // 2. Main Floating Form Card: Nominal, Tenor, Tujuan & Rincian Estimasi (Identik dengan Simulasi)
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(
                        elevation = 8.dp,
                        shape = RoundedCornerShape(20.dp),
                        ambientColor = Color(0x14000000),
                        spotColor = Color(0x20000000)
                    ),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Surface),
                border = BorderStroke(1.dp, Border)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .animateContentSize()
                        .padding(20.dp)
                ) {
                    // --- SECTION 1: NOMINAL YANG DIAJUKAN ---
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Nominal yang Diajukan",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = TextPrimary
                            )
                            Text(text = " *", color = Error, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Rp ",
                                fontSize = 26.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = TextPrimary,
                                letterSpacing = (-0.5).sp
                            )
                            BasicTextField(
                                value = amountInputText,
                                onValueChange = { input ->
                                    val digits = input.filter { it.isDigit() }
                                    if (digits.length <= 11) {
                                        val parsed = digits.toDoubleOrNull() ?: 0.0
                                        val clamped = if (parsed > maxAmount) maxAmount else parsed
                                        amountInputText = if (clamped > 0) currencyFormatter.format(clamped.toLong()) else ""
                                        if (clamped >= minAmount) {
                                            onAmountChange(clamped)
                                        } else if (clamped == 0.0) {
                                            onAmountChange(minAmount)
                                        }
                                    }
                                },
                                textStyle = TextStyle(
                                    fontFamily = OverusedGrotesk,
                                    fontSize = 26.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = TextPrimary,
                                    letterSpacing = (-0.5).sp
                                ),
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Number,
                                    imeAction = ImeAction.Done
                                ),
                                singleLine = true,
                                cursorBrush = SolidColor(Primary),
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Ketuk untuk edit",
                            fontSize = 11.sp,
                            color = TextMuted
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Slider 1: Jumlah Pinjaman
                    val currentVal = uiState.jumlahPinjaman.toFloat().coerceIn(minAmount.toFloat(), maxAmount.toFloat())
                    Slider(
                        value = currentVal,
                        onValueChange = { newVal ->
                            val rounded = (Math.round(newVal / stepAmount) * stepAmount).coerceIn(minAmount, maxAmount)
                            onAmountChange(rounded)
                        },
                        valueRange = minAmount.toFloat()..maxAmount.toFloat(),
                        colors = SliderDefaults.colors(
                            thumbColor = Primary,
                            activeTrackColor = Primary,
                            inactiveTrackColor = Color(0xFFEBEBEB)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Rp ${currencyFormatter.format(minAmount)}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = TextMuted
                        )
                        Text(
                            text = "Rp ${currencyFormatter.format(maxAmount)}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = TextMuted
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Quick Selection Chips (Nominal)
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(quickAmounts) { amt ->
                            val isSelected = uiState.jumlahPinjaman == amt
                            val isMax = amt == maxAmount
                            val label = if (isMax) "Maksimal" else "Rp ${currencyFormatter.format(amt / 1_000_000)} Jt"

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (isSelected) Primary0 else Neutral0)
                                    .border(
                                        width = if (isSelected) 1.5.dp else 1.dp,
                                        color = if (isSelected) Primary else Border,
                                        shape = RoundedCornerShape(10.dp)
                                    )
                                    .clickable { onAmountChange(amt) }
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = label,
                                    fontSize = 11.5.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = TextPrimary
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // --- SECTION 2: TENOR PINJAMAN (DILENGKAPI SLIDER & CHIPS) ---
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Tenor Pinjaman",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TextPrimary
                        )
                        Text(text = " *", color = Error, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    // Slider 2: Tenor Pinjaman (Scroll/Geser)
                    val currentTenorFloat = uiState.tenorBulan.toFloat().coerceIn(minTenor.toFloat(), maxTenor.toFloat())
                    Slider(
                        value = currentTenorFloat,
                        onValueChange = { newVal ->
                            val closest = tenorOptions.minByOrNull { Math.abs(it - newVal) } ?: 6
                            onTenorChange(closest)
                        },
                        valueRange = minTenor.toFloat()..maxTenor.toFloat(),
                        steps = 0,
                        colors = SliderDefaults.colors(
                            thumbColor = Primary,
                            activeTrackColor = Primary,
                            inactiveTrackColor = Color(0xFFEBEBEB)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "$minTenor Bulan",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = TextMuted
                        )
                        Text(
                            text = "$maxTenor Bulan",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = TextMuted
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Quick Tenor Chips
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(tenorOptions) { months ->
                            val isSelected = uiState.tenorBulan == months
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (isSelected) Primary0 else Neutral0)
                                    .border(
                                        width = if (isSelected) 1.5.dp else 1.dp,
                                        color = if (isSelected) Primary else Border,
                                        shape = RoundedCornerShape(10.dp)
                                    )
                                    .clickable { onTenorChange(months) }
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = "$months Bulan",
                                    fontSize = 11.5.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = TextPrimary
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // --- SECTION 3: TUJUAN PENGGUNAAN DANA ---
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Tujuan Penggunaan Dana",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TextPrimary
                        )
                        Text(text = " *", color = Error, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                    }

                    if (uiState.effectiveTujuan.isBlank()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Pilih salah satu keperluan di bawah untuk melanjutkan ke upload dokumen",
                            fontSize = 11.5.sp,
                            color = Primary70,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            tujuanOptions.take(3).forEach { opt ->
                                val isSelected = uiState.tujuanPinjaman == opt
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(44.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(if (isSelected) Primary0 else Neutral0)
                                        .border(
                                            width = if (isSelected) 1.5.dp else 1.dp,
                                            color = if (isSelected) Primary else Border,
                                            shape = RoundedCornerShape(10.dp)
                                        )
                                        .clickable { onTujuanChange(opt) }
                                        .padding(horizontal = 4.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = opt,
                                        fontSize = 11.5.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        color = TextPrimary,
                                        textAlign = TextAlign.Center,
                                        maxLines = 2,
                                        lineHeight = 14.sp
                                    )
                                }
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            tujuanOptions.drop(3).forEach { opt ->
                                val isSelected = uiState.tujuanPinjaman == opt
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(44.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(if (isSelected) Primary0 else Neutral0)
                                        .border(
                                            width = if (isSelected) 1.5.dp else 1.dp,
                                            color = if (isSelected) Primary else Border,
                                            shape = RoundedCornerShape(10.dp)
                                        )
                                        .clickable { onTujuanChange(opt) }
                                        .padding(horizontal = 4.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = opt,
                                        fontSize = 11.5.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        color = TextPrimary,
                                        textAlign = TextAlign.Center,
                                        maxLines = 2,
                                        lineHeight = 14.sp
                                    )
                                }
                            }
                        }
                    }

                    AnimatedVisibility(
                        visible = uiState.tujuanPinjaman == "Lainnya",
                        enter = fadeIn() + expandVertically(),
                        exit = fadeOut() + shrinkVertically()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 12.dp)
                                .bringIntoViewRequester(bringIntoViewRequester)
                        ) {
                            TextField(
                                value = uiState.customTujuan,
                                onValueChange = onCustomTujuanChange,
                                placeholder = "Tuliskan keperluan pinjaman Anda...",
                                label = "Detail Keperluan",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .focusRequester(focusRequester)
                                    .onFocusEvent { focusState ->
                                        if (focusState.isFocused) {
                                            coroutineScope.launch {
                                                delay(250)
                                                bringIntoViewRequester.bringIntoView()
                                            }
                                        }
                                    }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(22.dp))

                    // Subtle Divider
                    HorizontalDivider(
                        color = Border,
                        thickness = 1.dp
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    // --- SECTION 4: RINCIAN ESTIMASI ANGSURAN (IDENTIK DENGAN SIMULASI) ---
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        BreakdownRow(
                            label = "Cicilan Bulanan (Estimasi):",
                            value = "Rp ${currencyFormatter.format(uiState.estimasiCicilanBulanan)} / bln",
                            isHighlight = true
                        )

                        BreakdownRow(
                            label = "Total Estimasi Bunga:",
                            value = "Rp ${currencyFormatter.format(totalBunga)}"
                        )

                        BreakdownRow(
                            label = "Biaya Administrasi:",
                            value = "Rp ${currencyFormatter.format(uiState.biayaAdmin)}"
                        )

                        BreakdownRow(
                            label = "Total Pengembalian:",
                            value = "Rp ${currencyFormatter.format(uiState.totalPengembalian)}",
                            isBold = true
                        )
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // Disclaimer Note in Warning Style
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Warning0)
                            .border(1.dp, Warning.copy(alpha = 0.35f), RoundedCornerShape(12.dp))
                            .padding(12.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.Top,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Lucide.CircleAlert,
                                contentDescription = null,
                                tint = Warning80,
                                modifier = Modifier
                                    .size(16.dp)
                                    .offset(y = 1.dp)
                            )
                            Column {
                                Text(
                                    text = "Disclaimer",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Warning80
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "Perhitungan di atas bersifat estimasi indikatif dan dapat disesuaikan berdasarkan skor profil kredit nasabah.",
                                    fontSize = 11.sp,
                                    color = Warning80.copy(alpha = 0.9f),
                                    lineHeight = 15.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun BreakdownRow(
    label: String,
    value: String,
    isHighlight: Boolean = false,
    isBold: Boolean = false
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = if (isHighlight) 13.5.sp else 12.5.sp,
            color = if (isHighlight || isBold) TextPrimary else TextSecondary,
            fontWeight = if (isHighlight || isBold) FontWeight.SemiBold else FontWeight.Normal
        )
        Text(
            text = value,
            fontSize = if (isHighlight) 14.5.sp else 12.5.sp,
            fontWeight = if (isHighlight || isBold) FontWeight.Bold else FontWeight.SemiBold,
            color = if (isHighlight) Primary else TextPrimary
        )
    }
}

@Composable
private fun CostBreakdownRow(
    label: String,
    value: String,
    isBold: Boolean = false,
    isBadge: Boolean = false,
    isHighlight: Boolean = false
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.5.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = if (isHighlight) 13.sp else 12.5.sp,
            color = if (isHighlight || isBold) TextPrimary else TextSecondary,
            fontWeight = if (isHighlight || isBold) FontWeight.Bold else FontWeight.Medium
        )
        if (isBadge) {
            Badge(text = value, variant = BadgeVariant.Success, size = BadgeSize.SM)
        } else {
            Text(
                text = value,
                fontSize = if (isHighlight) 14.sp else 12.5.sp,
                fontWeight = if (isHighlight || isBold) FontWeight.Bold else FontWeight.SemiBold,
                color = if (isHighlight) Primary else TextPrimary
            )
        }
    }
}

// Step 2: Upload Dokumen Pelengkap View
@Composable
private fun Step2UploadDokumenView(
    uiState: LoanApplyUiState,
    onCaptureSlipGaji: () -> Unit,
    onPickSlipGaji: () -> Unit,
    onRemoveSlipGaji: () -> Unit,
    onCaptureRekKoran: () -> Unit,
    onPickRekKoran: () -> Unit,
    onRemoveRekKoran: () -> Unit,
    onCaptureNpwp: () -> Unit,
    onPickNpwp: () -> Unit,
    onRemoveNpwp: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Background),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 18.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Info Banner
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Warning0),
                border = BorderStroke(1.dp, Warning20)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(
                        imageVector = Lucide.Info,
                        contentDescription = null,
                        tint = Warning,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Unggah dokumen pendukung untuk proses verifikasi kilat. Pastikan foto jelas, terbaca, dan tidak terpotong.",
                        fontSize = 12.5.sp,
                        color = Warning80,
                        lineHeight = 17.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        // 1. Slip Gaji / Bukti Penghasilan (Wajib)
        item {
            DocumentUploadBox(
                title = "Slip Gaji / Bukti Penghasilan",
                subtitle = "Slip gaji 1 bulan terakhir atau surat keterangan penghasilan",
                isRequired = true,
                uri = uiState.slipGajiUri,
                bitmap = uiState.slipGajiBitmap,
                onDocumentClick = onPickSlipGaji,
                onCameraClick = onCaptureSlipGaji,
                onRemoveClick = onRemoveSlipGaji
            )
        }

        // 2. Rekening Koran (Wajib)
        item {
            DocumentUploadBox(
                title = "Rekening Koran 3 Bulan Terakhir",
                subtitle = "Mutasi rekening bank aktif penerimaan gaji / usaha",
                isRequired = true,
                uri = uiState.rekeningKoranUri,
                bitmap = uiState.rekeningKoranBitmap,
                onDocumentClick = onPickRekKoran,
                onCameraClick = onCaptureRekKoran,
                onRemoveClick = onRemoveRekKoran
            )
        }

        // 3. NPWP (Opsional)
        item {
            DocumentUploadBox(
                title = "Kartu NPWP",
                subtitle = "Wajib jika pengajuan pinjaman di atas Rp 50.000.000",
                isRequired = false,
                uri = uiState.npwpUri,
                bitmap = uiState.npwpBitmap,
                onDocumentClick = onPickNpwp,
                onCameraClick = onCaptureNpwp,
                onRemoveClick = onRemoveNpwp
            )
        }
    }
}

// Step 3: Ringkasan & Submit View
@Composable
private fun Step3SummarySubmitView(
    uiState: LoanApplyUiState,
    currencyFormatter: NumberFormat,
    onTncToggle: (Boolean) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Background),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 18.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Summary Header Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Surface),
                border = BorderStroke(1.dp, Border)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "Ringkasan Pengajuan",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )

                    CostBreakdownRow(label = "Status", value = "Siap Diajukan", isBadge = true)
                    CostBreakdownRow(label = "Nominal Pinjaman", value = "Rp ${currencyFormatter.format(uiState.jumlahPinjaman)}")
                    CostBreakdownRow(label = "Tenor", value = "${uiState.tenorBulan} Bulan")
                    CostBreakdownRow(label = "Tujuan Penggunaan", value = uiState.effectiveTujuan)
                    CostBreakdownRow(label = "Suku Bunga", value = "${uiState.sukuBungaPersen}% flat / bulan")
                    CostBreakdownRow(label = "Biaya Administrasi", value = "Rp ${currencyFormatter.format(uiState.biayaAdmin)}")

                    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = Border)

                    CostBreakdownRow(
                        label = "Estimasi Cicilan per Bulan",
                        value = "Rp ${currencyFormatter.format(uiState.estimasiCicilanBulanan)}",
                        isHighlight = true
                    )
                    CostBreakdownRow(
                        label = "Total Estimasi Pengembalian",
                        value = "Rp ${currencyFormatter.format(uiState.totalPengembalian)}"
                    )
                }
            }
        }

        // Disbursement Destination Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Surface),
                border = BorderStroke(1.dp, Border)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Rekening Penerima Dana",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Primary0),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Lucide.Landmark,
                                contentDescription = null,
                                tint = Primary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "${uiState.namaBank} - ${uiState.noRekening}",
                                fontSize = 13.5.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = TextPrimary
                            )
                            Text(
                                text = "a.n. ${uiState.namaRekening}",
                                fontSize = 12.sp,
                                color = TextSecondary
                            )
                        }
                    }
                }
            }
        }

        // Uploaded Documents Status
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Surface),
                border = BorderStroke(1.dp, Border)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Dokumen Pendukung",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "1. Slip Gaji / Bukti Penghasilan", fontSize = 12.5.sp, color = TextSecondary)
                        Badge(text = "Terlampir", variant = BadgeVariant.Success, size = BadgeSize.SM)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "2. Rekening Koran 3 Bulan", fontSize = 12.5.sp, color = TextSecondary)
                        Badge(text = "Terlampir", variant = BadgeVariant.Success, size = BadgeSize.SM)
                    }
                    if (uiState.npwpUri != null || uiState.npwpBitmap != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "3. Kartu NPWP", fontSize = 12.5.sp, color = TextSecondary)
                            Badge(text = "Terlampir", variant = BadgeVariant.Success, size = BadgeSize.SM)
                        }
                    }
                }
            }
        }

        // Terms & Conditions Checkbox
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Surface),
                border = BorderStroke(1.dp, Border)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onTncToggle(!uiState.isTncAgreed) }
                        .padding(14.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Checkbox(
                        checked = uiState.isTncAgreed,
                        onCheckedChange = { onTncToggle(it) },
                        colors = CheckboxDefaults.colors(
                            checkedColor = Primary,
                            checkmarkColor = Color.White
                        )
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Saya menyatakan data yang diajukan adalah benar dan menyetujui Syarat & Ketentuan Pembiayaan SAKU (PT SAKU) serta regulasi yang berlaku.",
                        fontSize = 12.sp,
                        color = TextPrimary,
                        lineHeight = 16.sp
                    )
                }
            }
        }
    }
}

// Step 4: Success Receipt View
@Composable
private fun Step4SuccessReceiptView(
    uiState: LoanApplyUiState,
    currencyFormatter: NumberFormat,
    onNavigateToHome: () -> Unit,
    onNavigateToDetail: () -> Unit
) {
    val context = LocalContext.current
    val todayDate = remember { SimpleDateFormat("dd MMMM yyyy, HH:mm", Locale.forLanguageTag("id-ID")).format(Date()) }
    val nomor = uiState.nomorPengajuan ?: (uiState.submittedLoanResult?.nomorPengajuan ?: "PJ-${System.currentTimeMillis().toString().takeLast(8)}")

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Background),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        // Success Animation Icon
        item {
            Spacer(modifier = Modifier.height(10.dp))
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(Success0),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Lucide.CircleCheck,
                    contentDescription = null,
                    tint = Success,
                    modifier = Modifier.size(46.dp)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "Pengajuan Berhasil Dikirim!",
                fontSize = 21.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Permohonan pinjaman Anda telah masuk ke sistem dan sedang ditinjau oleh tim verifikasi PT SAKU.",
                fontSize = 13.sp,
                color = TextSecondary,
                textAlign = TextAlign.Center,
                lineHeight = 18.sp,
                modifier = Modifier.padding(horizontal = 12.dp)
            )
        }

        // Receipt Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Surface),
                border = BorderStroke(1.dp, Border)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    // Header Nomor Pengajuan with Copy
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(text = "Nomor Pengajuan", fontSize = 11.sp, color = TextMuted)
                            Text(
                                text = nomor,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = Primary
                            )
                        }

                        IconButton(
                            onClick = {
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                val clip = ClipData.newPlainText("Nomor Pengajuan", nomor)
                                clipboard.setPrimaryClip(clip)
                                Toast.makeText(context, "Nomor pengajuan berhasil disalin", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier
                                .size(34.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Primary0)
                        ) {
                            Icon(
                                imageVector = Lucide.Copy,
                                contentDescription = "Salin",
                                tint = Primary,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 14.dp), color = Border)

                    CostBreakdownRow(label = "Status", value = "Menunggu Review", isBadge = true)
                    CostBreakdownRow(label = "Waktu Pengajuan", value = todayDate)
                    CostBreakdownRow(label = "Nominal Diajukan", value = "Rp ${currencyFormatter.format(uiState.jumlahPinjaman)}")
                    CostBreakdownRow(label = "Tenor", value = "${uiState.tenorBulan} Bulan")
                    CostBreakdownRow(label = "Estimasi Cicilan", value = "Rp ${currencyFormatter.format(uiState.estimasiCicilanBulanan)} / bln")
                    CostBreakdownRow(label = "Rekening Pencairan", value = "${uiState.namaBank} (•••• ${uiState.noRekening.takeLast(4)})")
                }
            }
        }

        // Process Timeline Info
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Surface),
                border = BorderStroke(1.dp, Border)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "Tahap Selanjutnya",
                        fontSize = 13.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    NextStepRow(stepNum = "1", title = "Verifikasi Dokumen", desc = "Pemeriksaan kelengkapan berkas dan data pengajuan Anda.")
                    NextStepRow(stepNum = "2", title = "Persetujuan Pinjaman", desc = "Proses evaluasi dan persetujuan pengajuan pinjaman.")
                    NextStepRow(stepNum = "3", title = "Pencairan Dana", desc = "Dana pinjaman langsung ditransfer ke rekening bank terdaftar Anda.")
                }
            }
        }

        // Action Buttons
        item {
            Spacer(modifier = Modifier.height(6.dp))
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    text = "Pantau Status Pengajuan ->",
                    onClick = onNavigateToDetail,
                    modifier = Modifier.fillMaxWidth(),
                    size = ButtonSize.LG
                )

                Button(
                    text = "Kembali ke Beranda",
                    onClick = onNavigateToHome,
                    variant = ButtonVariant.Outline,
                    modifier = Modifier.fillMaxWidth(),
                    size = ButtonSize.LG
                )
            }
        }
    }
}

// Helper Components
@Composable
private fun CostBreakdownRow(
    label: String,
    value: String,
    isHighlight: Boolean = false,
    isBadge: Boolean = false
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = if (isHighlight) 13.sp else 12.5.sp,
            fontWeight = if (isHighlight) FontWeight.SemiBold else FontWeight.Normal,
            color = if (isHighlight) TextPrimary else TextMuted
        )
        if (isBadge) {
            Badge(text = value, variant = BadgeVariant.Warning, size = BadgeSize.SM)
        } else {
            Text(
                text = value,
                fontSize = if (isHighlight) 14.sp else 13.sp,
                fontWeight = if (isHighlight) FontWeight.Bold else FontWeight.SemiBold,
                color = TextPrimary
            )
        }
    }
}

private fun getFileNameFromUri(context: Context, uri: Uri?): String {
    if (uri == null) return ""
    return try {
        var name = ""
        val cursor = context.contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val index = it.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME)
                if (index != -1) {
                    name = it.getString(index)
                }
            }
        }
        if (name.isBlank()) {
            uri.lastPathSegment ?: "dokumen_terlampir"
        } else {
            name
        }
    } catch (e: Exception) {
        uri.lastPathSegment ?: "dokumen_terlampir"
    }
}

@Composable
private fun DocumentUploadBox(
    title: String,
    subtitle: String,
    isRequired: Boolean,
    uri: Uri?,
    bitmap: Bitmap?,
    onDocumentClick: () -> Unit,
    onCameraClick: () -> Unit,
    onRemoveClick: () -> Unit
) {
    val context = LocalContext.current
    val hasFile = uri != null || bitmap != null
    val fileName = remember(uri, bitmap) {
        if (bitmap != null) {
            "Scan_Foto_${title.take(12).replace(" ", "_")}.jpg"
        } else if (uri != null) {
            getFileNameFromUri(context, uri)
        } else {
            ""
        }
    }
    val isPdf = remember(uri, fileName) {
        fileName.endsWith(".pdf", ignoreCase = true) || (uri != null && context.contentResolver.getType(uri)?.contains("pdf", ignoreCase = true) == true)
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Surface),
        border = BorderStroke(1.dp, if (hasFile) Primary20 else Border)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header Info & Status Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = title,
                            fontSize = 13.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        if (isRequired) {
                            Text(text = " *", color = Error, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(text = subtitle, fontSize = 11.5.sp, color = TextMuted)
                }

                if (hasFile) {
                    Badge(
                        text = "Terunggah",
                        variant = BadgeVariant.Success,
                        size = BadgeSize.SM
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            if (hasFile) {
                // Attached File View
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Background)
                        .border(1.dp, Border, RoundedCornerShape(12.dp))
                        .padding(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(Primary),
                                contentAlignment = Alignment.Center
                            ) {
                                if (bitmap != null) {
                                    Image(
                                        bitmap = bitmap.asImageBitmap(),
                                        contentDescription = null,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                } else if (uri != null && !isPdf) {
                                    AsyncImage(
                                        model = uri,
                                        contentDescription = null,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                } else {
                                    Icon(
                                        imageVector = Lucide.FileText,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = fileName.ifBlank { "dokumen_terlampir" },
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = TextPrimary,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = if (isPdf) "Dokumen PDF • Siap diverifikasi" else "Berkas Gambar • Siap diverifikasi",
                                    fontSize = 11.sp,
                                    color = TextSecondary
                                )
                            }
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(
                                onClick = onDocumentClick,
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    imageVector = Lucide.RotateCw,
                                    contentDescription = "Ganti Dokumen",
                                    tint = Primary,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                            IconButton(
                                onClick = onRemoveClick,
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    imageVector = Lucide.Trash2,
                                    contentDescription = "Hapus Dokumen",
                                    tint = Error,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }
            } else {
                // Empty state: Document picker primary CTA + Camera scan secondary
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // 1. Primary CTA: Pilih File Dokumen (PDF, JPG, PNG)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Primary0)
                            .border(1.dp, Primary20, RoundedCornerShape(12.dp))
                            .clickable { onDocumentClick() }
                            .padding(vertical = 14.dp, horizontal = 14.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(Primary),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Lucide.FileText,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Pilih File Dokumen",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "Format PDF, JPG, atau PNG (Maks. 5 MB)",
                                    fontSize = 11.5.sp,
                                    color = TextSecondary
                                )
                            }
                        }
                    }

                    // 2. Secondary CTA: Ambil Foto / Scan Fisik
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(Neutral0)
                            .border(1.dp, Border, RoundedCornerShape(10.dp))
                            .clickable { onCameraClick() }
                            .padding(vertical = 10.dp, horizontal = 14.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(Surface),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Lucide.Camera,
                                    contentDescription = null,
                                    tint = Primary,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "Atau ambil foto / scan langsung dengan kamera",
                                fontSize = 12.sp,
                                color = TextSecondary,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun NextStepRow(stepNum: String, title: String, desc: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(CircleShape)
                .background(Primary0),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = stepNum,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Primary
            )
        }
        Spacer(modifier = Modifier.width(10.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
            Text(text = desc, fontSize = 11.5.sp, color = TextMuted, lineHeight = 15.sp)
        }
    }
}

// Bottom Action Bar
@Composable
private fun ApplyBottomActionBar(
    currentStep: Int,
    isLoading: Boolean,
    isTncAgreed: Boolean,
    isStep1Valid: Boolean = true,
    step1HasPurpose: Boolean = true,
    isPlafondSufficient: Boolean = true,
    onNextStep1: () -> Unit,
    onNextStep2: () -> Unit,
    onSubmitFinal: () -> Unit,
    onPrevious: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(12.dp)
            .navigationBarsPadding()
            .imePadding(),
        color = Surface
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (currentStep in 2..3) {
                Button(
                    text = "Kembali",
                    onClick = onPrevious,
                    variant = ButtonVariant.Outline,
                    size = ButtonSize.LG,
                    modifier = Modifier.weight(0.8f)
                )
            }

            val nextButtonText = when (currentStep) {
                1 -> {
                    if (!isPlafondSufficient) {
                        "Limit Tidak Mencukupi (Rp 0)"
                    } else if (!step1HasPurpose) {
                        "Pilih Tujuan Penggunaan Dana"
                    } else {
                        "Lanjut ke Upload Dokumen →"
                    }
                }
                2 -> "Lanjut ke Ringkasan →"
                3 -> "Ajukan Pinjaman"
                else -> "Lanjut"
            }

            val isButtonEnabled = when (currentStep) {
                1 -> isStep1Valid && !isLoading
                2 -> !isLoading
                3 -> isTncAgreed && !isLoading
                else -> !isLoading
            }

            Button(
                text = nextButtonText,
                onClick = {
                    when (currentStep) {
                        1 -> onNextStep1()
                        2 -> onNextStep2()
                        3 -> onSubmitFinal()
                    }
                },
                enabled = isButtonEnabled,
                isLoading = isLoading,
                size = ButtonSize.LG,
                modifier = Modifier.weight(1.2f)
            )
        }
    }
}
