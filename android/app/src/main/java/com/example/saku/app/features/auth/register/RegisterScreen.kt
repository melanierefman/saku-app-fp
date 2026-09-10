package com.example.saku.app.features.auth.register

import android.graphics.Bitmap
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.core.content.FileProvider
import java.io.File
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.composables.icons.lucide.ArrowLeft
import com.composables.icons.lucide.Camera
import com.composables.icons.lucide.CircleAlert
import com.composables.icons.lucide.CircleCheck
import com.composables.icons.lucide.CreditCard
import com.composables.icons.lucide.IdCard
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Mail
import com.composables.icons.lucide.Phone
import com.composables.icons.lucide.ScanText
import com.composables.icons.lucide.ShieldCheck
import com.composables.icons.lucide.User
import com.example.saku.app.R
import com.example.saku.app.core.ui.components.Button
import com.example.saku.app.core.ui.components.ButtonVariant
import com.example.saku.app.core.ui.components.CameraCaptureMode
import com.example.saku.app.core.ui.components.CameraFramingCaptureDialog
import com.example.saku.app.core.ui.components.CheckboxWithLabel
import com.example.saku.app.core.ui.components.CurrencyField
import com.example.saku.app.core.ui.components.DocumentUploadCard
import com.example.saku.app.core.ui.components.DropdownField
import com.example.saku.app.core.ui.components.DropdownOption
import com.example.saku.app.core.ui.components.OtpInputField
import com.example.saku.app.core.ui.components.PasswordField
import com.example.saku.app.core.ui.components.TextField
import com.example.saku.app.core.ui.components.UploadStatus
import com.example.saku.app.ui.theme.Border
import com.example.saku.app.ui.theme.Error
import com.example.saku.app.ui.theme.Neutral0
import com.example.saku.app.ui.theme.Primary
import com.example.saku.app.ui.theme.Primary0
import com.example.saku.app.ui.theme.Primary60
import com.example.saku.app.ui.theme.Success
import com.example.saku.app.ui.theme.Success0
import com.example.saku.app.ui.theme.Surface
import com.example.saku.app.ui.theme.TextMuted
import com.example.saku.app.ui.theme.TextPrimary
import com.example.saku.app.ui.theme.TextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    onNavigateBack: () -> Unit,
    onNavigateToLogin: () -> Unit,
    viewModel: RegisterViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    // Dialog Sukses Registrasi SAKU
    if (uiState.isRegistrationComplete) {
        AlertDialog(
            onDismissRequest = { /* Modal tidak bisa di-dismiss selain tombol */ },
            confirmButton = {
                Button(
                    text = "Masuk ke Akun SAKU",
                    onClick = onNavigateToLogin,
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
                        modifier = Modifier.size(36.dp)
                    )
                }
            },
            title = {
                Text(
                    text = "Pendaftaran Berhasil!",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = TextPrimary
                )
            },
            text = {
                Text(
                    text = "Selamat! Akun nasabah SAKU Anda telah berhasil dibuat dan berkas KYC Anda telah diteruskan ke tim verifikasi. Silakan masuk menggunakan akun baru Anda.",
                    color = TextSecondary,
                    fontSize = 14.sp,
                    lineHeight = 20.sp
                )
            },
            containerColor = Surface,
            shape = RoundedCornerShape(20.dp)
        )
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // Background SAKU Mesh Gradient
        Image(
            painter = painterResource(id = R.drawable.bg_card_saku),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            text = "Daftar Akun SAKU",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = Color.White
                        )
                    },
                    navigationIcon = {
                        Box(
                            modifier = Modifier
                                .padding(start = 12.dp)
                                .size(38.dp)
                                .shadow(4.dp, CircleShape)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.95f))
                                .clickable {
                                    if (uiState.currentStep > 1) {
                                        viewModel.goToPreviousStep()
                                    } else {
                                        onNavigateBack()
                                    }
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Lucide.ArrowLeft,
                                contentDescription = "Kembali",
                                tint = Primary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    },
                    actions = {
                        // Empty balance spacer so the title is optically perfectly centered
                        Spacer(modifier = Modifier.size(50.dp))
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
                )
            },
            containerColor = Color.Transparent
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .imePadding()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                // Card Container enclosing Header Progress & Form
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(
                            elevation = 16.dp,
                            shape = RoundedCornerShape(24.dp),
                            ambientColor = Color(0x33000000),
                            spotColor = Color(0x33000000)
                        ),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Surface.copy(alpha = 0.98f)),
                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.8f))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 24.dp)
                    ) {
                        // 1. Header Card with Step Progress (Sesuai Referensi Gambar)
                        RegisterCardHeader(currentStep = uiState.currentStep)

                        Spacer(modifier = Modifier.height(20.dp))

                        // Error Banner
                        uiState.errorMessage?.let { error ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 16.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFFEE2E2)),
                                shape = RoundedCornerShape(12.dp),
                                border = BorderStroke(1.dp, Color(0xFFFCA5A5))
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Lucide.CircleAlert,
                                        contentDescription = null,
                                        tint = Error,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = error,
                                        color = Error,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }

                        // Success Banner
                        uiState.successMessage?.let { success ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 16.dp),
                                colors = CardDefaults.cardColors(containerColor = Success0),
                                shape = RoundedCornerShape(12.dp),
                                border = BorderStroke(1.dp, Color(0xFF86EFAC))
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Lucide.CircleCheck,
                                        contentDescription = null,
                                        tint = Success,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = success,
                                        color = Success,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }

                        // Form Berdasarkan Step
                        when (uiState.currentStep) {
                            1 -> Step1EmailOtpForm(viewModel = viewModel, uiState = uiState)
                            2 -> Step2KtpOcrForm(viewModel = viewModel, uiState = uiState)
                            3 -> Step3PersonalFinancialForm(viewModel = viewModel, uiState = uiState)
                            4 -> Step4LivenessSelfieForm(viewModel = viewModel, uiState = uiState)
                            5 -> Step5TncPasswordForm(viewModel = viewModel, uiState = uiState)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

/**
 * Header Card dengan Judul, Subtitle, Info Langkah & Linear Progress Bar Oranye
 * (Sesuai dengan gambar referensi desain)
 */
@Composable
private fun RegisterCardHeader(currentStep: Int) {
    val totalSteps = 5
    val percent = ((currentStep.toFloat() / totalSteps.toFloat()) * 100).toInt()
    val animatedProgress by animateFloatAsState(
        targetValue = currentStep.toFloat() / totalSteps.toFloat(),
        animationSpec = tween(durationMillis = 400),
        label = "register_header_progress"
    )

    val (title, subtitle) = when (currentStep) {
        1 -> "Buat Akun Baru" to "Lengkapi data diri Anda untuk memulai."
        2 -> "Scan & Verifikasi e-KTP" to "Foto atau unggah e-KTP Anda untuk pengisian data otomatis."
        3 -> "Data Diri & Keuangan" to "Data ini diperlukan untuk proses penilaian kredit. Pastikan data yang dimasukkan sesuai dengan dokumen resmi."
        4 -> "Verifikasi Identitas" to "Pastikan foto wajah Anda terlihat jelas untuk mempercepat proses verifikasi oleh tim SAKU."
        5 -> "Syarat & Ketentuan & Kata Sandi" to "Pastikan Anda membaca ketentuan layanan SAKU dan buat kata sandi akun Anda."
        else -> "Pendaftaran SAKU" to "Lengkapi langkah pendaftaran akun Anda."
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        // Judul Besar
        Text(
            text = title,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )

        Spacer(modifier = Modifier.height(6.dp))

        // Subtitle Deskripsi
        Text(
            text = subtitle,
            fontSize = 13.sp,
            color = TextSecondary,
            lineHeight = 18.sp
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Info Langkah & Persentase
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Langkah $currentStep dari $totalSteps",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextSecondary
            )
            Text(
                text = "$percent%",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Primary
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Linear Progress Bar Oranye SAKU
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp))
                .background(Color(0xFFFFEAD8))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(animatedProgress)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(3.dp))
                    .background(
                        Brush.horizontalGradient(
                            listOf(Primary, Primary60)
                        )
                    )
            )
        }
    }
}

/**
 * Section Title Header dengan aksen warna Oranye SAKU
 */
@Composable
private fun SectionHeader(title: String, modifier: Modifier = Modifier) {
    Column(modifier = modifier.fillMaxWidth().padding(top = 16.dp, bottom = 12.dp)) {
        Text(
            text = title,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = Primary
        )
        Spacer(modifier = Modifier.height(4.dp))
        HorizontalDivider(color = Color(0xFFFFEAD8), thickness = 1.dp)
    }
}

// Step 1: Buat Akun Baru (Verifikasi Email OTP)
@Composable
private fun Step1EmailOtpForm(viewModel: RegisterViewModel, uiState: RegisterUiState) {
    Column(modifier = Modifier.fillMaxWidth()) {
        TextField(
            value = uiState.email,
            onValueChange = viewModel::onEmailChange,
            label = "Email",
            placeholder = "Masukkan email aktif Anda",
            leadingIcon = Lucide.Mail,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            required = true,
            enabled = !uiState.isOtpVerified
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (!uiState.isOtpSent) {
            Button(
                text = "Kirim Kode OTP",
                onClick = viewModel::sendOtp,
                isLoading = uiState.isLoading,
                enabled = uiState.email.isNotBlank() && !uiState.isLoading,
                modifier = Modifier.fillMaxWidth()
            )
        } else {
            SectionHeader(title = "Kode Verifikasi Email")

            Text(
                text = "Masukkan 6 digit kode OTP yang telah dikirimkan ke email Anda:",
                fontSize = 13.sp,
                color = TextSecondary
            )

            Spacer(modifier = Modifier.height(12.dp))

            OtpInputField(
                otpValue = uiState.otpCode,
                onOtpChange = viewModel::onOtpCodeChange,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (uiState.otpCountdown > 0) {
                    Text(
                        text = "Kirim ulang kode dalam ${uiState.otpCountdown}s",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                } else {
                    Text(
                        text = "Belum menerima kode? ",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                    Text(
                        text = "Kirim Ulang",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Primary,
                        modifier = Modifier.clickable { viewModel.sendOtp() }
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                text = "Verifikasi & Lanjut",
                onClick = viewModel::verifyOtp,
                isLoading = uiState.isLoading,
                enabled = uiState.otpCode.length == 6 && !uiState.isLoading,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

// Step 2: Scan & Verifikasi e-KTP (OCR)
@Composable
private fun Step2KtpOcrForm(viewModel: RegisterViewModel, uiState: RegisterUiState) {
    val context = LocalContext.current
    var isKtpCameraOpen by remember { mutableStateOf(false) }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            viewModel.onKtpImageSelected(uri)
        }
    }

    if (isKtpCameraOpen) {
        CameraFramingCaptureDialog(
            mode = CameraCaptureMode.KTP,
            onDismissRequest = { isKtpCameraOpen = false },
            onImageCaptured = { bitmap ->
                isKtpCameraOpen = false
                viewModel.onKtpBitmapCaptured(bitmap)
            },
            onPickGalleryRequested = {
                galleryLauncher.launch("image/*")
            }
        )
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        // Document Upload Card
        DocumentUploadCard(
            title = "Foto e-KTP Asli",
            description = "Posisikan e-KTP di dalam bingkai pemandu oranye agar data terisi secara otomatis & akurat.",
            status = when {
                uiState.isOcrProcessing -> UploadStatus.UPLOADING
                uiState.ktpBitmap != null || uiState.ktpUri != null -> UploadStatus.UPLOADED
                else -> UploadStatus.EMPTY
            },
            icon = Lucide.IdCard,
            fileName = if (uiState.ktpBitmap != null) "ktp_camera_scan.jpg" else uiState.ktpUri?.lastPathSegment,
            statusBadgeText = if (uiState.isOcrProcessing) "Memproses OCR..." else if (uiState.ktpBitmap != null || uiState.ktpUri != null) "Tersimpan" else null,
            onUploadClick = { isKtpCameraOpen = true },
            onDeleteClick = { viewModel.onKtpImageSelected(null) }
        )

        // OCR Scanning Indicator / Banner
        if (uiState.isOcrProcessing) {
            Spacer(modifier = Modifier.height(12.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Primary0),
                shape = RoundedCornerShape(10.dp)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = Primary,
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Sedang memindai e-KTP dengan OCR otomatis...",
                        fontSize = 13.sp,
                        color = Primary,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        uiState.ocrSuccessMessage?.let { msg ->
            Spacer(modifier = Modifier.height(12.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Success0),
                shape = RoundedCornerShape(10.dp)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Lucide.ScanText,
                        contentDescription = null,
                        tint = Success,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = msg,
                        fontSize = 13.sp,
                        color = Success,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        // Preview Image if available
        if (uiState.ktpBitmap != null) {
            Spacer(modifier = Modifier.height(12.dp))
            Image(
                bitmap = uiState.ktpBitmap!!.asImageBitmap(),
                contentDescription = "Preview e-KTP",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .border(1.dp, Border, RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop
            )
        } else if (uiState.ktpUri != null) {
            Spacer(modifier = Modifier.height(12.dp))
            AsyncImage(
                model = uiState.ktpUri,
                contentDescription = "Preview e-KTP",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .border(1.dp, Border, RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop
            )
        }

        SectionHeader(title = "Data Sesuai e-KTP")

        TextField(
            value = uiState.nik,
            onValueChange = viewModel::onNikChange,
            label = "NIK (Nomor Induk Kependudukan)",
            placeholder = "16 digit NIK e-KTP",
            leadingIcon = Lucide.IdCard,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            required = true
        )

        Spacer(modifier = Modifier.height(12.dp))

        TextField(
            value = uiState.namaLengkap,
            onValueChange = viewModel::onNamaLengkapChange,
            label = "Nama Lengkap",
            placeholder = "Nama lengkap sesuai e-KTP",
            leadingIcon = Lucide.User,
            required = true
        )

        SectionHeader(title = "Alamat Sesuai KTP")

        TextField(
            value = uiState.alamatKtp.alamatLengkap,
            onValueChange = { viewModel.onAlamatKtpChange(uiState.alamatKtp.copy(alamatLengkap = it)) },
            label = "Alamat Lengkap (Jalan / Gang / No)",
            placeholder = "Contoh: Jl. Sudirman No. 45",
            required = true
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row(modifier = Modifier.fillMaxWidth()) {
            TextField(
                value = uiState.alamatKtp.rt,
                onValueChange = { viewModel.onAlamatKtpChange(uiState.alamatKtp.copy(rt = it)) },
                label = "RT",
                placeholder = "001",
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.weight(1f),
                required = true
            )
            Spacer(modifier = Modifier.width(12.dp))
            TextField(
                value = uiState.alamatKtp.rw,
                onValueChange = { viewModel.onAlamatKtpChange(uiState.alamatKtp.copy(rw = it)) },
                label = "RW",
                placeholder = "002",
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.weight(1f),
                required = true
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(modifier = Modifier.fillMaxWidth()) {
            TextField(
                value = uiState.alamatKtp.kelurahan,
                onValueChange = { viewModel.onAlamatKtpChange(uiState.alamatKtp.copy(kelurahan = it)) },
                label = "Kelurahan / Desa",
                placeholder = "Kelurahan",
                modifier = Modifier.weight(1f),
                required = true
            )
            Spacer(modifier = Modifier.width(12.dp))
            TextField(
                value = uiState.alamatKtp.kecamatan,
                onValueChange = { viewModel.onAlamatKtpChange(uiState.alamatKtp.copy(kecamatan = it)) },
                label = "Kecamatan",
                placeholder = "Kecamatan",
                modifier = Modifier.weight(1f),
                required = true
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(modifier = Modifier.fillMaxWidth()) {
            TextField(
                value = uiState.alamatKtp.kotaKabupaten,
                onValueChange = { viewModel.onAlamatKtpChange(uiState.alamatKtp.copy(kotaKabupaten = it)) },
                label = "Kota / Kabupaten",
                placeholder = "Kota / Kab",
                modifier = Modifier.weight(1f),
                required = true
            )
            Spacer(modifier = Modifier.width(12.dp))
            TextField(
                value = uiState.alamatKtp.provinsi,
                onValueChange = { viewModel.onAlamatKtpChange(uiState.alamatKtp.copy(provinsi = it)) },
                label = "Provinsi",
                placeholder = "Provinsi",
                modifier = Modifier.weight(1f),
                required = true
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        TextField(
            value = uiState.alamatKtp.kodePos,
            onValueChange = { viewModel.onAlamatKtpChange(uiState.alamatKtp.copy(kodePos = it)) },
            label = "Kode Pos",
            placeholder = "12345",
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            text = "Lanjut ke Data Diri",
            onClick = viewModel::submitStep1Ktp,
            isLoading = uiState.isLoading,
            enabled = uiState.nik.isNotBlank() && uiState.namaLengkap.isNotBlank() && !uiState.isLoading,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

// Step 3: Data Diri, Keuangan, Rekening & Domisili
@Composable
private fun Step3PersonalFinancialForm(viewModel: RegisterViewModel, uiState: RegisterUiState) {
    val bankOptions = listOf(
        DropdownOption(value = "BCA", label = "BCA (Bank Central Asia)"),
        DropdownOption(value = "MANDIRI", label = "Bank Mandiri"),
        DropdownOption(value = "BRI", label = "BRI (Bank Rakyat Indonesia)"),
        DropdownOption(value = "BNI", label = "BNI (Bank Negara Indonesia)"),
        DropdownOption(value = "CIMB", label = "CIMB Niaga"),
        DropdownOption(value = "PERMATA", label = "Bank Permata"),
        DropdownOption(value = "DANAMON", label = "Bank Danamon"),
        DropdownOption(value = "BSI", label = "BSI (Bank Syariah Indonesia)")
    )

    val jobStatusOptions = listOf(
        DropdownOption(value = "KARYAWAN_TETAP", label = "Karyawan Tetap"),
        DropdownOption(value = "KARYAWAN_KONTRAK", label = "Karyawan Kontrak"),
        DropdownOption(value = "WIRAUSAHA", label = "Wirausaha / Pemilik Usaha"),
        DropdownOption(value = "PROFESIONAL", label = "Profesional / Freelancer"),
        DropdownOption(value = "PNS_BUMN", label = "PNS / Pegawai BUMN"),
        DropdownOption(value = "IBU_RUMAH_TANGGA", label = "Ibu Rumah Tangga"),
        DropdownOption(value = "LAINNYA", label = "Lainnya")
    )

    Column(modifier = Modifier.fillMaxWidth()) {
        SectionHeader(title = "Informasi Pribadi")

        TextField(
            value = uiState.noHp,
            onValueChange = viewModel::onNoHpChange,
            label = "Nomor Handphone (WhatsApp)",
            placeholder = "081234567890",
            leadingIcon = Lucide.Phone,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            required = true
        )

        Spacer(modifier = Modifier.height(12.dp))

        TextField(
            value = uiState.namaIbuKandung,
            onValueChange = viewModel::onNamaIbuKandungChange,
            label = "Nama Gadis Ibu Kandung",
            placeholder = "Nama ibu kandung untuk verifikasi keamanan",
            leadingIcon = Lucide.User,
            required = true
        )

        SectionHeader(title = "Rekening Pencairan")

        DropdownField(
            options = bankOptions,
            selectedOption = bankOptions.find { it.value.equals(uiState.namaBank, ignoreCase = true) || it.label.startsWith(uiState.namaBank, ignoreCase = true) },
            onOptionSelect = { opt -> opt?.let { viewModel.onNamaBankChange(it.value) } },
            label = "Nama Bank",
            placeholder = "Pilih Bank Pencairan",
            leadingIcon = Lucide.CreditCard,
            required = true
        )

        Spacer(modifier = Modifier.height(12.dp))

        TextField(
            value = uiState.noRekening,
            onValueChange = viewModel::onNoRekeningChange,
            label = "Nomor Rekening",
            placeholder = "Contoh: 1234567890",
            leadingIcon = Lucide.CreditCard,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            required = true
        )

        Spacer(modifier = Modifier.height(12.dp))

        TextField(
            value = uiState.namaRekening,
            onValueChange = viewModel::onNamaRekeningChange,
            label = "Nama Pemilik Rekening",
            placeholder = "Sesuai pada buku tabungan / rekening bank",
            leadingIcon = Lucide.User,
            required = true
        )

        SectionHeader(title = "Pekerjaan & Finansial")

        TextField(
            value = uiState.pekerjaan,
            onValueChange = viewModel::onPekerjaanChange,
            label = "Profesi / Pekerjaan",
            placeholder = "Contoh: Software Engineer / Staff Keuangan",
            required = true
        )

        Spacer(modifier = Modifier.height(12.dp))

        TextField(
            value = uiState.tempatKerja,
            onValueChange = viewModel::onTempatKerjaChange,
            label = "Nama Perusahaan / Tempat Bekerja",
            placeholder = "Contoh: PT BCA Finance",
            required = true
        )

        Spacer(modifier = Modifier.height(12.dp))

        DropdownField(
            options = jobStatusOptions,
            selectedOption = jobStatusOptions.find { it.value.equals(uiState.statusPekerjaan, ignoreCase = true) },
            onOptionSelect = { opt -> opt?.let { viewModel.onStatusPekerjaanChange(it.value) } },
            label = "Status Pekerjaan",
            placeholder = "Pilih status pekerjaan",
            required = true
        )

        Spacer(modifier = Modifier.height(12.dp))

        CurrencyField(
            amount = uiState.pendapatan.toLongOrNull(),
            onAmountChange = { viewModel.onPendapatanChange(it?.toString() ?: "") },
            label = "Pendapatan Bersih Bulanan",
            placeholder = "0",
            required = true,
            quickAmounts = emptyList()
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row(modifier = Modifier.fillMaxWidth()) {
            TextField(
                value = uiState.lamaBekerjaBulan,
                onValueChange = viewModel::onLamaBekerjaChange,
                label = "Lama Bekerja (Bulan)",
                placeholder = "Contoh: 24",
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.weight(1f),
                required = true
            )
            Spacer(modifier = Modifier.width(12.dp))
            CurrencyField(
                amount = uiState.totalCicilanLainnya.toLongOrNull(),
                onAmountChange = { viewModel.onCicilanLainnyaChange(it?.toString() ?: "0") },
                label = "Total Cicilan Lain (Jika Ada)",
                placeholder = "0",
                quickAmounts = emptyList(),
                modifier = Modifier.weight(1.3f)
            )
        }

        SectionHeader(title = "Alamat Domisili")

        CheckboxWithLabel(
            checked = uiState.sameAsKtp,
            onCheckedChange = viewModel::onSameAsKtpToggle,
            label = "Alamat domisili saat ini sama dengan e-KTP",
            modifier = Modifier.padding(vertical = 4.dp)
        )

        AnimatedVisibility(visible = !uiState.sameAsKtp) {
            Column(modifier = Modifier.fillMaxWidth().padding(top = 8.dp)) {
                TextField(
                    value = uiState.alamatDomisili.alamatLengkap,
                    onValueChange = { viewModel.onAlamatDomisiliChange(uiState.alamatDomisili.copy(alamatLengkap = it)) },
                    label = "Alamat Domisili Lengkap",
                    placeholder = "Jl. Domisili No. 123",
                    required = true
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(modifier = Modifier.fillMaxWidth()) {
                    TextField(
                        value = uiState.alamatDomisili.rt,
                        onValueChange = { viewModel.onAlamatDomisiliChange(uiState.alamatDomisili.copy(rt = it)) },
                        label = "RT",
                        placeholder = "001",
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f),
                        required = true
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    TextField(
                        value = uiState.alamatDomisili.rw,
                        onValueChange = { viewModel.onAlamatDomisiliChange(uiState.alamatDomisili.copy(rw = it)) },
                        label = "RW",
                        placeholder = "002",
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f),
                        required = true
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(modifier = Modifier.fillMaxWidth()) {
                    TextField(
                        value = uiState.alamatDomisili.kelurahan,
                        onValueChange = { viewModel.onAlamatDomisiliChange(uiState.alamatDomisili.copy(kelurahan = it)) },
                        label = "Kelurahan",
                        placeholder = "Kelurahan",
                        modifier = Modifier.weight(1f),
                        required = true
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    TextField(
                        value = uiState.alamatDomisili.kecamatan,
                        onValueChange = { viewModel.onAlamatDomisiliChange(uiState.alamatDomisili.copy(kecamatan = it)) },
                        label = "Kecamatan",
                        placeholder = "Kecamatan",
                        modifier = Modifier.weight(1f),
                        required = true
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(modifier = Modifier.fillMaxWidth()) {
                    TextField(
                        value = uiState.alamatDomisili.kotaKabupaten,
                        onValueChange = { viewModel.onAlamatDomisiliChange(uiState.alamatDomisili.copy(kotaKabupaten = it)) },
                        label = "Kota / Kab",
                        placeholder = "Kota / Kab",
                        modifier = Modifier.weight(1f),
                        required = true
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    TextField(
                        value = uiState.alamatDomisili.provinsi,
                        onValueChange = { viewModel.onAlamatDomisiliChange(uiState.alamatDomisili.copy(provinsi = it)) },
                        label = "Provinsi",
                        placeholder = "Provinsi",
                        modifier = Modifier.weight(1f),
                        required = true
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            text = "Lanjut ke Verifikasi Wajah",
            onClick = viewModel::submitStep2Personal,
            isLoading = uiState.isLoading,
            enabled = uiState.noHp.isNotBlank() && uiState.noRekening.isNotBlank() && uiState.pekerjaan.isNotBlank() && !uiState.isLoading,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

// Step 4: Verifikasi Identitas (Liveness Selfie)
@Composable
private fun Step4LivenessSelfieForm(viewModel: RegisterViewModel, uiState: RegisterUiState) {
    val context = LocalContext.current
    var isSelfieCameraOpen by remember { mutableStateOf(false) }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            viewModel.onSelfieUriSelected(uri)
        }
    }

    if (isSelfieCameraOpen) {
        CameraFramingCaptureDialog(
            mode = CameraCaptureMode.SELFIE,
            onDismissRequest = { isSelfieCameraOpen = false },
            onImageCaptured = { bitmap ->
                isSelfieCameraOpen = false
                viewModel.onSelfieBitmapCaptured(bitmap)
            },
            onPickGalleryRequested = {
                galleryLauncher.launch("image/*")
            }
        )
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        // Panduan Foto Wajah
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Primary0),
            shape = RoundedCornerShape(14.dp)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Lucide.ShieldCheck,
                        contentDescription = null,
                        tint = Primary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Panduan Verifikasi Wajah",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = Primary
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "• Posisikan wajah Anda tepat di dalam lingkaran oval kamera.\n• Hindari memakai kacamata hitam, topi, atau masker.\n• Pastikan cahaya ruangan terang dan tidak membelakangi lampu.",
                    fontSize = 12.sp,
                    color = TextPrimary,
                    lineHeight = 18.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Document Upload Card
        DocumentUploadCard(
            title = "Foto Selfie Wajah",
            description = "Gunakan kamera panduan untuk verifikasi wajah langsung.",
            status = if (uiState.selfieBitmap != null || uiState.selfieUri != null) UploadStatus.UPLOADED else UploadStatus.EMPTY,
            icon = Lucide.Camera,
            fileName = if (uiState.selfieBitmap != null) "selfie_camera_scan.jpg" else uiState.selfieUri?.lastPathSegment,
            statusBadgeText = if (uiState.selfieBitmap != null || uiState.selfieUri != null) "Foto Tersimpan" else null,
            onUploadClick = { isSelfieCameraOpen = true },
            onDeleteClick = { viewModel.onSelfieUriSelected(null) }
        )

        // Preview Image with Oval Frame
        if (uiState.selfieBitmap != null) {
            Spacer(modifier = Modifier.height(16.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .border(2.dp, Primary, RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    bitmap = uiState.selfieBitmap!!.asImageBitmap(),
                    contentDescription = "Preview Selfie",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }
        } else if (uiState.selfieUri != null) {
            Spacer(modifier = Modifier.height(16.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .border(2.dp, Primary, RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = uiState.selfieUri,
                    contentDescription = "Preview Selfie",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            text = "Kirim & Lanjut",
            onClick = viewModel::submitStep3Liveness,
            isLoading = uiState.isLoading,
            enabled = (uiState.selfieBitmap != null || uiState.selfieUri != null) && !uiState.isLoading,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

// Step 5: Syarat & Ketentuan & Buat Kata Sandi (Email Only)
@Composable
private fun Step5TncPasswordForm(viewModel: RegisterViewModel, uiState: RegisterUiState) {
    Column(modifier = Modifier.fillMaxWidth()) {
        SectionHeader(title = "Syarat & Ketentuan Layanan")

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp),
            colors = CardDefaults.cardColors(containerColor = Neutral0),
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, Border)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(12.dp)
            ) {
                Text(
                    text = "SYARAT & KETENTUAN SAKU (BCA FINANCE)",
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "1. Pendaftaran akun SAKU diperuntukkan bagi nasabah yang memenuhi kriteria kelayakan pembiayaan BCA Finance.\n\n" +
                            "2. Nasabah menjamin keaslian dan kebenaran seluruh dokumen e-KTP, data rekening, dan foto identitas yang diunggah.\n\n" +
                            "3. SAKU berhak melakukan penilaian credit scoring otomatis dan verifikasi lanjutan demi keamanan transaksi.\n\n" +
                            "4. Data pribadi nasabah dilindungi sesuai Kebijakan Privasi dan regulasi Otoritas Jasa Keuangan (OJK).",
                    fontSize = 11.sp,
                    color = TextSecondary,
                    lineHeight = 16.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        CheckboxWithLabel(
            checked = uiState.isTncAgreed,
            onCheckedChange = viewModel::onTncAgreedToggle,
            label = "Saya telah membaca, memahami, dan menyetujui Syarat & Ketentuan serta Kebijakan Privasi SAKU.",
            modifier = Modifier.padding(vertical = 4.dp)
        )

        SectionHeader(title = "Keamanan Akun")

        PasswordField(
            value = uiState.password,
            onValueChange = viewModel::onPasswordChange,
            label = "Kata Sandi",
            placeholder = "Minimal 8 karakter",
            required = true,
            helperText = "Gunakan kombinasi huruf dan angka"
        )

        Spacer(modifier = Modifier.height(12.dp))

        PasswordField(
            value = uiState.confirmPassword,
            onValueChange = viewModel::onConfirmPasswordChange,
            label = "Konfirmasi Kata Sandi",
            placeholder = "Ulangi kata sandi Anda",
            required = true
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            text = "Selesaikan Pendaftaran",
            onClick = viewModel::submitStep4And5,
            isLoading = uiState.isLoading,
            enabled = uiState.isTncAgreed && uiState.password.length >= 8 && uiState.confirmPassword.isNotBlank() && !uiState.isLoading,
            modifier = Modifier.fillMaxWidth()
        )
    }
}
