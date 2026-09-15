package com.example.saku.app.features.loans.apply

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
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
import androidx.lifecycle.viewmodel.compose.viewModel
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
import com.example.saku.app.core.ui.components.Badge
import com.example.saku.app.core.ui.components.BadgeSize
import com.example.saku.app.core.ui.components.BadgeVariant
import com.example.saku.app.core.ui.components.Button
import com.example.saku.app.core.ui.components.ButtonSize
import com.example.saku.app.core.ui.components.ButtonVariant
import com.example.saku.app.core.ui.components.CameraCaptureMode
import com.example.saku.app.core.ui.components.CameraFramingCaptureDialog
import com.example.saku.app.core.ui.components.TextField
import com.example.saku.app.ui.theme.Background
import com.example.saku.app.ui.theme.Border
import com.example.saku.app.ui.theme.Error
import com.example.saku.app.ui.theme.Error0
import com.example.saku.app.ui.theme.Error20
import com.example.saku.app.ui.theme.Error70
import com.example.saku.app.ui.theme.Neutral10
import com.example.saku.app.ui.theme.Neutral20
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
    viewModel: LoanApplyViewModel = viewModel()
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

    // Camera Capture State for Step 2
    var activeCameraDocType by remember { mutableStateOf<String?>(null) }

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
                    onSubmitFinal = { viewModel.submitFinalApplication(context) },
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
                        onCaptureSlipGaji = { activeCameraDocType = "SLIP_GAJI" },
                        onPickSlipGaji = { slipGajiPickerLauncher.launch("*/*") },
                        onRemoveSlipGaji = { viewModel.setSlipGaji(null, null) },
                        onCaptureRekKoran = { activeCameraDocType = "REK_KORAN" },
                        onPickRekKoran = { rekKoranPickerLauncher.launch("*/*") },
                        onRemoveRekKoran = { viewModel.setRekeningKoran(null, null) },
                        onCaptureNpwp = { activeCameraDocType = "NPWP" },
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

    // Camera Framing Dialog for In-App Photo Capture
    if (activeCameraDocType != null) {
        CameraFramingCaptureDialog(
            mode = CameraCaptureMode.KTP, // Uses rectangular document frame cutout
            onDismissRequest = { activeCameraDocType = null },
            onImageCaptured = { bitmap: Bitmap ->
                when (activeCameraDocType) {
                    "SLIP_GAJI" -> viewModel.setSlipGaji(null, bitmap)
                    "REK_KORAN" -> viewModel.setRekeningKoran(null, bitmap)
                    "NPWP" -> viewModel.setNpwp(null, bitmap)
                }
                activeCameraDocType = null
            }
        )
    }
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
                            1 -> "Langkah 1 dari 3: Nominal & Tenor"
                            2 -> "Langkah 2 dari 3: Unggah Dokumen"
                            3 -> "Langkah 3 dari 3: Ringkasan & Konfirmasi"
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

// Step 1: Nominal & Tenor View
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

    val candidateAmounts = listOf(1_000_000.0, 2_000_000.0, 5_000_000.0, 10_000_000.0, 20_000_000.0, 30_000_000.0, 50_000_000.0)
    val quickAmounts = (candidateAmounts.filter { it < maxAmount && it >= minAmount } + maxAmount).distinct().sorted()
    val tenorOptions = listOf(3, 6, 9, 12, 18, 24, 36)
    val tujuanOptions = listOf("Modal Usaha", "Renovasi Rumah", "Pendidikan", "Keperluan Medis", "Elektronik", "Lainnya")

    var amountInputText by remember(uiState.jumlahPinjaman) {
        mutableStateOf(if (uiState.jumlahPinjaman > 0) currencyFormatter.format(uiState.jumlahPinjaman.toLong()) else "")
    }

    LazyColumn(
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

        // 1. Unified Card: Sisa Plafond Tersedia & Rekening Pencairan
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Surface),
                border = BorderStroke(1.dp, Border)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // Top: Sisa Plafond Tersedia & Suku Bunga
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Sisa Plafond Tersedia",
                                fontSize = 11.5.sp,
                                color = TextMuted
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Rp ${currencyFormatter.format(uiState.availablePlafond)}",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        }
                        Badge(
                            text = "Bunga ${uiState.sukuBungaPersen}%/bln",
                            variant = BadgeVariant.Success,
                            size = BadgeSize.SM
                        )
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = Border)

                    // Bottom: Rekening Pencairan
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
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
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Rekening Pencairan",
                                fontSize = 11.sp,
                                color = TextMuted
                            )
                            Text(
                                text = "${uiState.namaBank} • ${if (uiState.noRekening.length > 4) "•••• " + uiState.noRekening.takeLast(4) else uiState.noRekening}",
                                fontSize = 13.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Text(
                                text = "a.n. ${uiState.namaRekening}",
                                fontSize = 11.5.sp,
                                color = TextSecondary
                            )
                        }
                        Badge(text = "Terverifikasi", variant = BadgeVariant.Success, size = BadgeSize.SM)
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

        // 2. Jumlah Pinjaman Card (Direct Input + Stepper + Slider + LazyRow Chips)
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Surface),
                border = BorderStroke(1.dp, Border)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "Jumlah Pinjaman",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    // Stepper Box with Direct Editable TextField
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(Background)
                            .border(1.dp, Border, RoundedCornerShape(14.dp))
                            .padding(vertical = 12.dp, horizontal = 12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(
                                onClick = {
                                    val newVal = (uiState.jumlahPinjaman - stepAmount).coerceIn(minAmount, maxAmount)
                                    onAmountChange(newVal)
                                },
                                enabled = uiState.jumlahPinjaman > minAmount,
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (uiState.jumlahPinjaman > minAmount) Surface else Background)
                                    .border(1.dp, Border, RoundedCornerShape(10.dp))
                            ) {
                                Text(
                                    text = "−",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (uiState.jumlahPinjaman > minAmount) TextPrimary else TextMuted
                                )
                            }

                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(horizontal = 8.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Text(
                                        text = "Rp ",
                                        fontSize = 22.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = TextPrimary
                                    )
                                    BasicTextField(
                                        value = amountInputText,
                                        onValueChange = { input ->
                                            val digits = input.filter { it.isDigit() }
                                            if (digits.length <= 11) {
                                                val parsed = digits.toDoubleOrNull() ?: 0.0
                                                val clamped = if (parsed > maxAmount) maxAmount else parsed
                                                amountInputText = if (digits.isNotEmpty()) currencyFormatter.format(clamped.toLong()) else ""
                                                if (clamped >= minAmount) {
                                                    onAmountChange(clamped)
                                                }
                                            }
                                        },
                                        textStyle = TextStyle(
                                            fontSize = 22.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = TextPrimary,
                                            textAlign = TextAlign.Start
                                        ),
                                        keyboardOptions = KeyboardOptions(
                                            keyboardType = KeyboardType.Number,
                                            imeAction = ImeAction.Done
                                        ),
                                        singleLine = true,
                                        cursorBrush = SolidColor(Primary)
                                    )
                                }
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "Ketik nominal atau geser slider",
                                    fontSize = 11.sp,
                                    color = TextMuted
                                )
                            }

                            IconButton(
                                onClick = {
                                    val newVal = (uiState.jumlahPinjaman + stepAmount).coerceIn(minAmount, maxAmount)
                                    onAmountChange(newVal)
                                },
                                enabled = uiState.jumlahPinjaman < maxAmount,
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (uiState.jumlahPinjaman < maxAmount) Surface else Background)
                                    .border(1.dp, Border, RoundedCornerShape(10.dp))
                            ) {
                                Text(
                                    text = "+",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (uiState.jumlahPinjaman < maxAmount) TextPrimary else TextMuted
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    val currentVal = uiState.jumlahPinjaman.toFloat().coerceIn(minAmount.toFloat(), maxAmount.toFloat())
                    Slider(
                        value = currentVal,
                        onValueChange = { newVal ->
                            val rounded = (Math.round(newVal / 100_000.0) * 100_000.0).coerceIn(minAmount, maxAmount)
                            onAmountChange(rounded)
                        },
                        valueRange = minAmount.toFloat()..maxAmount.toFloat(),
                        colors = SliderDefaults.colors(
                            thumbColor = Primary,
                            activeTrackColor = Primary,
                            inactiveTrackColor = Neutral20
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Min: Rp ${currencyFormatter.format(minAmount)}", fontSize = 11.sp, color = TextMuted)
                        Text(text = "Maks: Rp ${currencyFormatter.format(uiState.availablePlafond)}", fontSize = 11.sp, color = TextMuted)
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "Pilihan Cepat",
                        fontSize = 12.sp,
                        color = TextSecondary,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(quickAmounts) { amt ->
                            val isSelected = uiState.jumlahPinjaman == amt
                            val isMax = amt == uiState.availablePlafond
                            val label = if (isMax) "Maksimal" else "Rp ${currencyFormatter.format(amt)}"

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (isSelected) Primary0 else Surface)
                                    .border(
                                        width = if (isSelected) 1.5.dp else 1.dp,
                                        color = if (isSelected) Primary else Border,
                                        shape = RoundedCornerShape(10.dp)
                                    )
                                    .clickable { onAmountChange(amt) }
                                    .padding(horizontal = 14.dp, vertical = 9.dp)
                            ) {
                                Text(
                                    text = label,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) Primary else TextPrimary
                                )
                            }
                        }
                    }
                }
            }
        }

        // 3. Card: Pilihan Tenor
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Surface),
                border = BorderStroke(1.dp, Border)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Jangka Waktu (Tenor)",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = "${uiState.tenorBulan} Bulan",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Primary
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(tenorOptions) { months ->
                            val isSelected = uiState.tenorBulan == months
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(if (isSelected) Primary0 else Surface)
                                    .border(
                                        width = if (isSelected) 1.5.dp else 1.dp,
                                        color = if (isSelected) Primary else Border,
                                        shape = RoundedCornerShape(12.dp)
                                    )
                                    .clickable { onTenorChange(months) }
                                    .padding(horizontal = 16.dp, vertical = 10.dp)
                            ) {
                                Text(
                                    text = "$months Bulan",
                                    fontSize = 13.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) Primary else TextPrimary
                                )
                            }
                        }
                    }
                }
            }
        }

        // 4. Card: Tujuan Penggunaan Dana
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Surface),
                border = BorderStroke(
                    1.dp,
                    if (uiState.isFromSimulation && uiState.effectiveTujuan.isBlank()) Primary.copy(alpha = 0.6f) else Border
                )
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Tujuan Penggunaan Dana",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Text(text = " *", color = Error, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        }
                        Badge(
                            text = if (uiState.effectiveTujuan.isNotBlank()) "Sudah Dipilih" else "Wajib Dipilih",
                            variant = if (uiState.effectiveTujuan.isNotBlank()) BadgeVariant.Success else BadgeVariant.Warning,
                            size = BadgeSize.SM
                        )
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
                                        .background(if (isSelected) Primary0 else Surface)
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
                                        color = if (isSelected) Primary else TextPrimary,
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
                                        .background(if (isSelected) Primary0 else Surface)
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
                                        color = if (isSelected) Primary else TextPrimary,
                                        textAlign = TextAlign.Center,
                                        maxLines = 2,
                                        lineHeight = 14.sp
                                    )
                                }
                            }
                        }
                    }

                    if (uiState.tujuanPinjaman == "Lainnya") {
                        Spacer(modifier = Modifier.height(12.dp))
                        TextField(
                            value = uiState.customTujuan,
                            onValueChange = onCustomTujuanChange,
                            placeholder = "Tuliskan keperluan pinjaman Anda...",
                            label = "Detail Keperluan"
                        )
                    }
                }
            }
        }

        // 5. Card: Rincian Estimasi Pembiayaan
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Surface),
                border = BorderStroke(1.dp, Border)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Lucide.FileCheck,
                            contentDescription = null,
                            tint = Primary,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = "Rincian Estimasi Pembiayaan",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Hero Monthly Box
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(Primary0)
                            .border(1.dp, Primary.copy(alpha = 0.25f), RoundedCornerShape(14.dp))
                            .padding(16.dp)
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Estimasi Cicilan Bulanan",
                                fontSize = 12.sp,
                                color = TextSecondary
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Rp ${currencyFormatter.format(uiState.estimasiCicilanBulanan)}",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Primary
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "per bulan selama ${uiState.tenorBulan} bulan",
                                fontSize = 11.5.sp,
                                color = TextMuted
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    CostBreakdownRow(label = "Pokok Pinjaman", value = "Rp ${currencyFormatter.format(uiState.jumlahPinjaman)}")
                    CostBreakdownRow(label = "Biaya Admin", value = "Rp ${currencyFormatter.format(uiState.biayaAdmin)}")
                    CostBreakdownRow(label = "Suku Bunga", value = "${uiState.sukuBungaPersen}% / bulan flat")
                    CostBreakdownRow(label = "Tenor Pinjaman", value = "${uiState.tenorBulan} Bulan")
                }
            }
        }
    }
}

@Composable
private fun CostBreakdownRow(label: String, value: String, isBold: Boolean = false) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = 12.5.sp,
            color = if (isBold) TextPrimary else TextSecondary,
            fontWeight = if (isBold) FontWeight.SemiBold else FontWeight.Normal
        )
        Text(
            text = value,
            fontSize = 12.5.sp,
            fontWeight = if (isBold) FontWeight.Bold else FontWeight.SemiBold,
            color = TextPrimary
        )
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
                colors = CardDefaults.cardColors(containerColor = Primary0),
                border = BorderStroke(1.dp, Primary20)
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
                        tint = Primary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Unggah dokumen pendukung untuk proses verifikasi kilat. Pastikan foto jelas, terbaca, dan tidak terpotong.",
                        fontSize = 12.5.sp,
                        color = Primary80,
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
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Ringkasan Pengajuan",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Badge(
                            text = "Tahap Akhir",
                            variant = BadgeVariant.Primary,
                            size = BadgeSize.SM
                        )
                    }

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
                        text = "Saya menyatakan data yang diajukan adalah benar dan menyetujui Syarat & Ketentuan Pembiayaan SAKU BCA Finance serta regulasi OJK & AFPI.",
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
                text = "Permohonan pinjaman Anda telah masuk ke sistem dan sedang ditinjau oleh tim verifikasi BCA Finance.",
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

                    NextStepRow(stepNum = "1", title = "Verifikasi Dokumen", desc = "Tim verifikasi memeriksa kelengkapan slip gaji & mutasi rekening.")
                    NextStepRow(stepNum = "2", title = "Persetujuan Cabang", desc = "Kepala cabang menyetujui rekomendasi kredit.")
                    NextStepRow(stepNum = "3", title = "Pencairan Dana Instan", desc = "Dana pinjaman langsung ditransfer ke rekening BCA Anda.")
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
                color = if (isHighlight) Primary else TextPrimary
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

                Badge(
                    text = if (hasFile) "Terunggah" else (if (isRequired) "Wajib" else "Opsional"),
                    variant = if (hasFile) BadgeVariant.Success else (if (isRequired) BadgeVariant.Error else BadgeVariant.Neutral),
                    size = BadgeSize.SM
                )
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
                                    .size(40.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isPdf) Error0 else Primary0),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Lucide.FileText,
                                    contentDescription = null,
                                    tint = if (isPdf) Error else Primary,
                                    modifier = Modifier.size(20.dp)
                                )
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
                                    .size(36.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Surface),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Lucide.FileText,
                                    contentDescription = null,
                                    tint = Primary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Pilih File Dokumen",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Primary70
                                )
                                Spacer(modifier = Modifier.height(1.dp))
                                Text(
                                    text = "Format PDF, JPG, atau PNG (Maks. 5 MB)",
                                    fontSize = 11.sp,
                                    color = TextMuted
                                )
                            }
                        }
                    }

                    // 2. Secondary CTA: Ambil Foto / Scan Fisik
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(Surface)
                            .border(1.dp, Border, RoundedCornerShape(10.dp))
                            .clickable { onCameraClick() }
                            .padding(vertical = 10.dp, horizontal = 14.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Lucide.Camera,
                                contentDescription = null,
                                tint = TextSecondary,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "Atau ambil foto / scan langsung dengan kamera",
                                fontSize = 11.5.sp,
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
            .shadow(12.dp),
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
