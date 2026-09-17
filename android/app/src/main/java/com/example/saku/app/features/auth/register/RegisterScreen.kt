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
import androidx.compose.foundation.layout.navigationBarsPadding
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.saku.app.core.util.ImageCompressorHelper
import org.koin.androidx.compose.koinViewModel
import coil.compose.AsyncImage
import com.composables.icons.lucide.ArrowLeft
import com.composables.icons.lucide.Camera
import com.composables.icons.lucide.CircleAlert
import com.composables.icons.lucide.CircleCheck
import com.composables.icons.lucide.Check
import com.composables.icons.lucide.CreditCard
import com.composables.icons.lucide.Eye
import com.composables.icons.lucide.EyeOff
import com.composables.icons.lucide.FileText
import com.composables.icons.lucide.IdCard
import com.composables.icons.lucide.Image as LucideImage
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Mail
import com.composables.icons.lucide.Phone
import com.composables.icons.lucide.RefreshCw
import com.composables.icons.lucide.RotateCw
import com.composables.icons.lucide.ShieldCheck
import com.composables.icons.lucide.Trash2
import com.composables.icons.lucide.User
import com.composables.icons.lucide.X
import com.example.saku.app.R
import com.example.saku.app.core.network.dto.AlamatCustomerDto
import com.example.saku.app.core.ui.components.Badge
import com.example.saku.app.core.ui.components.BadgeSize
import com.example.saku.app.core.ui.components.BadgeVariant
import com.example.saku.app.core.ui.components.Button
import com.example.saku.app.core.ui.components.ButtonSize
import com.example.saku.app.core.ui.components.ButtonVariant
import com.example.saku.app.core.ui.components.Checkbox
import com.example.saku.app.core.ui.components.CheckboxWithLabel
import com.example.saku.app.core.ui.components.ConfirmationDialog
import com.example.saku.app.core.ui.components.CurrencyField
import com.example.saku.app.core.ui.components.DropdownField
import com.example.saku.app.core.ui.components.DropdownOption
import com.example.saku.app.core.ui.components.OtpInputField
import com.example.saku.app.core.ui.components.PasswordField
import com.example.saku.app.core.ui.components.StepProgressBar
import com.example.saku.app.core.ui.components.TextField
import com.example.saku.app.core.ui.components.UploadStatus
import com.example.saku.app.core.util.KtpOcrHelper
import com.example.saku.app.ui.theme.Border
import com.example.saku.app.ui.theme.Error
import com.example.saku.app.ui.theme.Error0
import com.example.saku.app.ui.theme.Error60
import com.example.saku.app.ui.theme.Neutral0
import com.example.saku.app.ui.theme.Neutral60
import com.example.saku.app.ui.theme.Primary
import com.example.saku.app.ui.theme.Primary0
import com.example.saku.app.ui.theme.Primary10
import com.example.saku.app.ui.theme.Primary20
import com.example.saku.app.ui.theme.Primary60
import com.example.saku.app.ui.theme.Success
import com.example.saku.app.ui.theme.Success0
import com.example.saku.app.ui.theme.Surface
import com.example.saku.app.ui.theme.TextMuted
import com.example.saku.app.ui.theme.TextPrimary
import com.example.saku.app.ui.theme.TextSecondary
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    onNavigateBack: () -> Unit,
    onNavigateToLogin: () -> Unit,
    onNavigateToKycPending: () -> Unit = onNavigateToLogin,
    viewModel: RegisterViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()
    val focusManager = LocalFocusManager.current

    // Auto-scroll ke paling atas setiap kali langkah pendaftaran berpindah
    LaunchedEffect(uiState.currentStep) {
        scrollState.animateScrollTo(0)
    }

    // Dialog Sukses Registrasi SAKU
    if (uiState.isRegistrationComplete) {
        AlertDialog(
            onDismissRequest = { /* Modal tidak bisa di-dismiss selain tombol */ },
            confirmButton = {
                Button(
                    text = "Lihat Status Verifikasi",
                    onClick = onNavigateToKycPending,
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
                    text = "Selamat! Akun nasabah SAKU Anda telah berhasil dibuat. Dokumen Anda sedang dalam antrean verifikasi manual tim Backoffice SAKU. Silakan masuk ke aplikasi.",
                    color = TextSecondary,
                    fontSize = 14.sp,
                    lineHeight = 20.sp
                )
            },
            containerColor = Color.White,
            shape = RoundedCornerShape(20.dp)
        )
    }

    // Modal Konfirmasi Sebelum Pendaftaran Final
    if (uiState.showConfirmationModal) {
        AlertDialog(
            onDismissRequest = { viewModel.dismissConfirmationModal() },
            shape = RoundedCornerShape(20.dp),
            containerColor = Color.White,
            icon = {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(Primary0),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Lucide.CircleCheck,
                        contentDescription = null,
                        tint = Primary,
                        modifier = Modifier.size(30.dp)
                    )
                }
            },
            title = {
                Text(
                    text = "Konfirmasi Pendaftaran",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = TextPrimary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Apakah seluruh data dan dokumen pendaftaran Anda sudah benar?",
                        fontSize = 13.sp,
                        color = TextSecondary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Neutral0),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, Border)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            ConfirmSummaryRow(label = "Nama Lengkap", value = uiState.namaLengkap)
                            ConfirmSummaryRow(label = "NIK", value = uiState.nik)
                            ConfirmSummaryRow(label = "Email", value = uiState.email)
                            ConfirmSummaryRow(label = "No. Handphone", value = uiState.noHp)
                        }
                    }

                    Text(
                        text = "Data pendaftaran Anda akan segera diverifikasi oleh tim Backoffice SAKU.",
                        fontSize = 11.5.sp,
                        color = TextMuted,
                        lineHeight = 15.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    text = "Ya, Daftarkan Sekarang",
                    onClick = { viewModel.submitStep6FinalRegistration() },
                    isLoading = uiState.isLoading,
                    modifier = Modifier.fillMaxWidth(),
                    variant = ButtonVariant.Primary
                )
            },
            dismissButton = {
                Button(
                    text = "Periksa Kembali",
                    onClick = { viewModel.dismissConfirmationModal() },
                    enabled = !uiState.isLoading,
                    modifier = Modifier.fillMaxWidth(),
                    variant = ButtonVariant.Outline
                )
            }
        )
    }

    Scaffold(
        containerColor = Color.White,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Daftar Akun SAKU",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 17.sp,
                        color = TextPrimary
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            if (uiState.currentStep > 0) {
                                viewModel.goToPreviousStep()
                            } else {
                                onNavigateBack()
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Lucide.ArrowLeft,
                            contentDescription = "Kembali",
                            tint = TextPrimary,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        bottomBar = {
            // Sticky Bottom Action Bar
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .imePadding()
                    .navigationBarsPadding()
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                when (uiState.currentStep) {
                    // Pra-Step (Email & OTP)
                    0 -> {
                        if (!uiState.isOtpSent) {
                            Button(
                                text = "Kirim Kode OTP",
                                onClick = {
                                    focusManager.clearFocus()
                                    viewModel.sendOtp()
                                },
                                isLoading = uiState.isLoading,
                                enabled = uiState.email.isNotBlank() && !uiState.isLoading,
                                variant = ButtonVariant.Primary,
                                size = ButtonSize.LG,
                                fullWidth = true
                            )
                        } else {
                            Button(
                                text = "Verifikasi OTP →",
                                onClick = {
                                    focusManager.clearFocus()
                                    viewModel.verifyOtp()
                                },
                                isLoading = uiState.isLoading,
                                enabled = uiState.otpCode.length == 6 && !uiState.isLoading,
                                variant = ButtonVariant.Primary,
                                size = ButtonSize.LG,
                                fullWidth = true
                            )
                        }
                    }
                    // Step 1: Data Pribadi
                    1 -> {
                        Button(
                            text = "Lanjut →",
                            onClick = {
                                focusManager.clearFocus()
                                viewModel.submitStep1Personal()
                            },
                            isLoading = uiState.isLoading,
                            enabled = uiState.nik.length == 16 && uiState.namaLengkap.isNotBlank() && uiState.noHp.isNotBlank() && !uiState.isLoading,
                            variant = ButtonVariant.Primary,
                            size = ButtonSize.LG,
                            fullWidth = true
                        )
                    }
                    // Step 2: Data Pekerjaan & Rekening
                    2 -> {
                        Button(
                            text = "Lanjut →",
                            onClick = {
                                focusManager.clearFocus()
                                viewModel.submitStep2WorkAndBank()
                            },
                            isLoading = uiState.isLoading,
                            enabled = uiState.pekerjaan.isNotBlank() && uiState.tempatKerja.isNotBlank() && uiState.noRekening.isNotBlank() && uiState.namaRekening.isNotBlank() && uiState.namaIbuKandung.isNotBlank() && !uiState.isLoading,
                            variant = ButtonVariant.Primary,
                            size = ButtonSize.LG,
                            fullWidth = true
                        )
                    }
                    // Step 3: Alamat KTP & Domisili
                    3 -> {
                        Button(
                            text = "Lanjut →",
                            onClick = {
                                focusManager.clearFocus()
                                viewModel.submitStep3Address()
                            },
                            isLoading = uiState.isLoading,
                            enabled = uiState.alamatKtp.alamatLengkap.isNotBlank() && uiState.alamatKtp.kotaKabupaten.isNotBlank() && !uiState.isLoading,
                            variant = ButtonVariant.Primary,
                            size = ButtonSize.LG,
                            fullWidth = true
                        )
                    }
                    // Step 4: Upload KYC
                    4 -> {
                        Button(
                            text = "Lanjut →",
                            onClick = {
                                focusManager.clearFocus()
                                viewModel.submitStep4Documents()
                            },
                            isLoading = uiState.isLoading,
                            enabled = (uiState.ktpBitmap != null || uiState.ktpUri != null) && (uiState.selfieBitmap != null || uiState.selfieUri != null) && !uiState.isLoading,
                            variant = ButtonVariant.Primary,
                            size = ButtonSize.LG,
                            fullWidth = true
                        )
                    }
                    // Step 5: Buat Kata Sandi
                    5 -> {
                        Button(
                            text = "Lanjut →",
                            onClick = {
                                focusManager.clearFocus()
                                viewModel.submitStep5Credentials()
                            },
                            isLoading = uiState.isLoading,
                            enabled = uiState.password.length >= 8 && uiState.confirmPassword.isNotBlank() && !uiState.isLoading,
                            variant = ButtonVariant.Primary,
                            size = ButtonSize.LG,
                            fullWidth = true
                        )
                    }
                    // Step 6: Syarat & Ketentuan (Langkah Terakhir)
                    6 -> {
                        Button(
                            text = "Daftar Akun SAKU",
                            onClick = {
                                focusManager.clearFocus()
                                viewModel.openConfirmationModal()
                            },
                            isLoading = uiState.isLoading,
                            enabled = uiState.isTncAgreed && !uiState.isLoading,
                            variant = ButtonVariant.Primary,
                            size = ButtonSize.LG,
                            fullWidth = true
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
                .verticalScroll(scrollState)
                .padding(horizontal = 24.dp, vertical = 12.dp)
        ) {
            // Step Progress Header
            RegisterStepProgressBar(currentStep = uiState.currentStep)

            Spacer(modifier = Modifier.height(20.dp))

            // Error Banner
            uiState.errorMessage?.let { error ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    colors = CardDefaults.cardColors(containerColor = Error0),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, Color(0xFFFCA5A5))
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
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Lucide.CircleCheck,
                            contentDescription = null,
                            tint = Success,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = success,
                            color = Success,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            // Form berdasarkan langkah aktif (Step 0..6)
            when (uiState.currentStep) {
                0 -> Step0EmailOtpForm(viewModel = viewModel, uiState = uiState)
                1 -> Step1PersonalForm(viewModel = viewModel, uiState = uiState)
                2 -> Step2JobAndBankForm(viewModel = viewModel, uiState = uiState)
                3 -> Step3AddressForm(viewModel = viewModel, uiState = uiState)
                4 -> Step4KycDocumentsForm(viewModel = viewModel, uiState = uiState)
                5 -> Step5CredentialsForm(viewModel = viewModel, uiState = uiState)
                6 -> Step6TncForm(viewModel = viewModel, uiState = uiState)
            }

            Spacer(modifier = Modifier.height(28.dp))
        }
    }
}

/**
 * Clean Header & Segmented Progress Bar
 */
@Composable
private fun RegisterStepProgressBar(currentStep: Int) {
    val totalSteps = 6
    val progressFloat = if (currentStep == 0) 0.05f else (currentStep.toFloat() / totalSteps.toFloat()).coerceIn(0f, 1f)
    val percent = if (currentStep == 0) 0 else ((currentStep.toFloat() / totalSteps.toFloat()) * 100).toInt()
    val animatedProgress by animateFloatAsState(
        targetValue = progressFloat,
        animationSpec = tween(durationMillis = 400),
        label = "register_header_progress"
    )

    val (title, subtitle) = when (currentStep) {
        0 -> "Verifikasi Email" to "Masukkan email aktif Anda untuk menerima kode OTP verifikasi akun."
        1 -> "Data Pribadi & Identitas" to "Masukkan NIK, nama lengkap sesuai e-KTP, dan nomor handphone Anda."
        2 -> "Data Pekerjaan & Rekening" to "Lengkapi informasi pekerjaan dan rekening bank untuk pencairan dana."
        3 -> "Alamat KTP & Domisili" to "Isi manual alamat lengkap sesuai e-KTP dan tempat tinggal saat ini."
        4 -> "Upload Dokumen KYC" to "Unggah foto fisik e-KTP dan foto selfie untuk verifikasi tim Backoffice."
        5 -> "Buat Kata Sandi" to "Buat kata sandi yang aman untuk mengakses akun SAKU Anda."
        6 -> "Syarat & Ketentuan" to "Pelajari dan setujui syarat & ketentuan layanan pembiayaan SAKU."
        else -> "Pendaftaran SAKU" to "Lengkapi formulir pendaftaran akun nasabah SAKU."
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = title,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary,
            letterSpacing = (-0.5).sp
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = subtitle,
            fontSize = 14.sp,
            color = TextSecondary,
            lineHeight = 20.sp
        )

        Spacer(modifier = Modifier.height(18.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = if (currentStep == 0) "Pra-Registrasi • Verifikasi Email" else "Langkah $currentStep dari $totalSteps",
                fontSize = 12.5.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextSecondary
            )
            Text(
                text = "$percent%",
                fontSize = 12.5.sp,
                fontWeight = FontWeight.Bold,
                color = Primary
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp))
                .background(Color(0xFFF3F4F6))
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
 * Section Title Header
 */
@Composable
private fun SectionHeader(title: String, modifier: Modifier = Modifier) {
    Column(modifier = modifier.fillMaxWidth().padding(top = 18.dp, bottom = 12.dp)) {
        Text(
            text = title,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = Primary
        )
        Spacer(modifier = Modifier.height(6.dp))
        HorizontalDivider(color = Color(0xFFF0F2F5), thickness = 1.dp)
    }
}

// Pra-Step: Verifikasi Email (OTP)
@Composable
private fun Step0EmailOtpForm(viewModel: RegisterViewModel, uiState: RegisterUiState) {
    val otpFocusRequester = remember { FocusRequester() }

    LaunchedEffect(uiState.isOtpSent) {
        if (uiState.isOtpSent) {
            delay(200)
            try {
                otpFocusRequester.requestFocus()
            } catch (e: Exception) {
                // Ignore if not yet laid out
            }
        }
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        TextField(
            value = uiState.email,
            onValueChange = viewModel::onEmailChange,
            label = "Alamat Email",
            placeholder = "nama@email.com",
            leadingIcon = Lucide.Mail,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            required = true,
            enabled = !uiState.isOtpVerified
        )

        if (uiState.isOtpSent) {
            Spacer(modifier = Modifier.height(20.dp))
            SectionHeader(title = "Kode Verifikasi Email")

            Text(
                text = "Masukkan 6 digit kode OTP yang telah dikirimkan ke email Anda:",
                fontSize = 13.sp,
                color = TextSecondary
            )

            Spacer(modifier = Modifier.height(14.dp))

            OtpInputField(
                otpValue = uiState.otpCode,
                onOtpChange = viewModel::onOtpCodeChange,
                focusRequester = otpFocusRequester,
                autoFocus = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (uiState.otpCountdown > 0) {
                    Text(
                        text = "Kirim ulang kode dalam ${uiState.otpCountdown}s",
                        fontSize = 13.sp,
                        color = TextSecondary
                    )
                } else {
                    Text(
                        text = "Belum menerima kode? ",
                        fontSize = 13.sp,
                        color = TextSecondary
                    )
                    Text(
                        text = "Kirim Ulang",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Primary,
                        modifier = Modifier.clickable { viewModel.sendOtp() }
                    )
                }
            }
        }
    }
}

// Step 1: Data Pribadi & Identitas (Manual Input)
@Composable
private fun Step1PersonalForm(viewModel: RegisterViewModel, uiState: RegisterUiState) {
    Column(modifier = Modifier.fillMaxWidth()) {
        SectionHeader(title = "Identitas Kependudukan")

        TextField(
            value = uiState.nik,
            onValueChange = viewModel::onNikChange,
            label = "NIK (Nomor Induk Kependudukan)",
            placeholder = "16 digit NIK sesuai e-KTP",
            leadingIcon = Lucide.IdCard,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            required = true,
            helperText = "Pastikan 16 digit angka sesuai fisik e-KTP"
        )

        Spacer(modifier = Modifier.height(14.dp))

        TextField(
            value = uiState.namaLengkap,
            onValueChange = viewModel::onNamaLengkapChange,
            label = "Nama Lengkap",
            placeholder = "Nama lengkap sesuai e-KTP",
            leadingIcon = Lucide.User,
            required = true
        )

        Spacer(modifier = Modifier.height(14.dp))

        TextField(
            value = uiState.noHp,
            onValueChange = viewModel::onNoHpChange,
            label = "Nomor Handphone (WhatsApp)",
            placeholder = "Contoh: 081234567890",
            leadingIcon = Lucide.Phone,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            required = true,
            helperText = "Nomor aktif yang dapat dihubungi"
        )
    }
}

// Step 2: Data Pekerjaan & Rekening
@Composable
private fun Step2JobAndBankForm(viewModel: RegisterViewModel, uiState: RegisterUiState) {
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
        SectionHeader(title = "Pekerjaan & Finansial")

        TextField(
            value = uiState.pekerjaan,
            onValueChange = viewModel::onPekerjaanChange,
            label = "Profesi / Pekerjaan",
            placeholder = "Contoh: Staff Operasional / Wiraswasta",
            required = true
        )

        Spacer(modifier = Modifier.height(14.dp))

        TextField(
            value = uiState.tempatKerja,
            onValueChange = viewModel::onTempatKerjaChange,
            label = "Nama Perusahaan / Tempat Bekerja",
            placeholder = "Contoh: PT SAKU Digital Indonesia",
            required = true
        )

        Spacer(modifier = Modifier.height(14.dp))

        DropdownField(
            options = jobStatusOptions,
            selectedOption = jobStatusOptions.find { it.value.equals(uiState.statusPekerjaan, ignoreCase = true) },
            onOptionSelect = { opt -> opt?.let { viewModel.onStatusPekerjaanChange(it.value) } },
            label = "Status Pekerjaan",
            placeholder = "Pilih status pekerjaan",
            required = true
        )

        Spacer(modifier = Modifier.height(14.dp))

        CurrencyField(
            amount = uiState.pendapatan.toLongOrNull(),
            onAmountChange = { viewModel.onPendapatanChange(it?.toString() ?: "") },
            label = "Pendapatan Bersih Bulanan",
            placeholder = "0",
            required = true,
            quickAmounts = emptyList()
        )

        Spacer(modifier = Modifier.height(14.dp))

        Row(modifier = Modifier.fillMaxWidth()) {
            TextField(
                value = uiState.lamaBekerjaBulan,
                onValueChange = viewModel::onLamaBekerjaChange,
                label = "Lama Kerja (Bulan)",
                placeholder = "24",
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.weight(1f),
                required = true
            )
            Spacer(modifier = Modifier.width(12.dp))
            CurrencyField(
                amount = uiState.totalCicilanLainnya.toLongOrNull(),
                onAmountChange = { viewModel.onCicilanLainnyaChange(it?.toString() ?: "0") },
                label = "Cicilan Lain (Bulan)",
                placeholder = "0",
                quickAmounts = emptyList(),
                modifier = Modifier.weight(1.3f)
            )
        }

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

        Spacer(modifier = Modifier.height(14.dp))

        TextField(
            value = uiState.noRekening,
            onValueChange = viewModel::onNoRekeningChange,
            label = "Nomor Rekening",
            placeholder = "Contoh: 1234567890",
            leadingIcon = Lucide.CreditCard,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            required = true
        )

        Spacer(modifier = Modifier.height(14.dp))

        TextField(
            value = uiState.namaRekening,
            onValueChange = viewModel::onNamaRekeningChange,
            label = "Nama Pemilik Rekening",
            placeholder = "Sesuai pada buku tabungan / rekening bank",
            leadingIcon = Lucide.User,
            required = true
        )

        Spacer(modifier = Modifier.height(14.dp))

        TextField(
            value = uiState.namaIbuKandung,
            onValueChange = viewModel::onNamaIbuKandungChange,
            label = "Nama Gadis Ibu Kandung",
            placeholder = "Nama ibu kandung untuk verifikasi keamanan",
            leadingIcon = Lucide.User,
            required = true
        )
    }
}

// Step 3: Alamat KTP & Domisili (Manual Input)
@Composable
private fun Step3AddressForm(viewModel: RegisterViewModel, uiState: RegisterUiState) {
    Column(modifier = Modifier.fillMaxWidth()) {
        SectionHeader(title = "Alamat Sesuai KTP")

        TextField(
            value = uiState.alamatKtp.alamatLengkap,
            onValueChange = { viewModel.onAlamatKtpChange(uiState.alamatKtp.copy(alamatLengkap = it)) },
            label = "Alamat Lengkap (Jalan / Gang / No)",
            placeholder = "Contoh: Jl. Sudirman No. 45",
            required = true
        )

        Spacer(modifier = Modifier.height(14.dp))

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

        Spacer(modifier = Modifier.height(14.dp))

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

        Spacer(modifier = Modifier.height(14.dp))

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

        Spacer(modifier = Modifier.height(14.dp))

        TextField(
            value = uiState.alamatKtp.kodePos,
            onValueChange = { viewModel.onAlamatKtpChange(uiState.alamatKtp.copy(kodePos = it)) },
            label = "Kode Pos",
            placeholder = "12345",
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            required = true
        )

        SectionHeader(title = "Alamat Domisili Saat Ini")

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

                Spacer(modifier = Modifier.height(14.dp))

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

                Spacer(modifier = Modifier.height(14.dp))

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

                Spacer(modifier = Modifier.height(14.dp))

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

                Spacer(modifier = Modifier.height(14.dp))

                TextField(
                    value = uiState.alamatDomisili.kodePos,
                    onValueChange = { viewModel.onAlamatDomisiliChange(uiState.alamatDomisili.copy(kodePos = it)) },
                    label = "Kode Pos Domisili",
                    placeholder = "12345",
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    required = true
                )
            }
        }
    }
}

// Step 4: Upload Dokumen KYC (Foto e-KTP & Foto Selfie)
@Composable
private fun Step4KycDocumentsForm(viewModel: RegisterViewModel, uiState: RegisterUiState) {
    val context = LocalContext.current
    var pendingCameraUri by remember { mutableStateOf<Uri?>(null) }
    var pendingCameraTarget by remember { mutableStateOf<String?>(null) }
    var activeGalleryTarget by remember { mutableStateOf<String?>(null) }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            when (activeGalleryTarget) {
                "ktp" -> viewModel.onKtpImageSelected(uri)
                "selfie" -> viewModel.onSelfieUriSelected(uri)
            }
        }
        activeGalleryTarget = null
    }

    val nativeCameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success: Boolean ->
        if (success && pendingCameraUri != null) {
            val uri = pendingCameraUri!!
            when (pendingCameraTarget) {
                "ktp" -> viewModel.onKtpImageSelected(uri)
                "selfie" -> viewModel.onSelfieUriSelected(uri)
            }
        }
        pendingCameraUri = null
        pendingCameraTarget = null
    }

    val launchCamera = { target: String ->
        val tempUri = ImageCompressorHelper.createTempPictureUri(context, if (target == "ktp") "ktp_" else "selfie_")
        pendingCameraUri = tempUri
        pendingCameraTarget = target
        nativeCameraLauncher.launch(tempUri)
    }

    val launchGallery = { target: String ->
        activeGalleryTarget = target
        galleryLauncher.launch("image/*")
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Foto Fisik e-KTP
        RegisterDocumentUploadBox(
            title = "Foto Fisik e-KTP Asli",
            subtitle = "Foto e-KTP asli secara jelas dan utuh tanpa pantulan cahaya",
            isRequired = true,
            hasFile = uiState.ktpUri != null || uiState.ktpBitmap != null,
            fileName = if (uiState.ktpBitmap != null) "ktp_foto.jpg" else (uiState.ktpUri?.lastPathSegment ?: "ktp_foto.jpg"),
            icon = Lucide.IdCard,
            onGalleryClick = { launchGallery("ktp") },
            onCameraClick = { launchCamera("ktp") },
            onRemoveClick = { viewModel.onKtpImageSelected(null) }
        )

        // 2. Foto Selfie Wajah
        RegisterDocumentUploadBox(
            title = "Foto Selfie Wajah",
            subtitle = "Foto wajah tampak depan dengan pencahayaan terang",
            isRequired = true,
            hasFile = uiState.selfieUri != null || uiState.selfieBitmap != null,
            fileName = if (uiState.selfieBitmap != null) "selfie_foto.jpg" else (uiState.selfieUri?.lastPathSegment ?: "selfie_foto.jpg"),
            icon = Lucide.Camera,
            onGalleryClick = { launchGallery("selfie") },
            onCameraClick = { launchCamera("selfie") },
            onRemoveClick = { viewModel.onSelfieUriSelected(null) }
        )
    }
}

@Composable
private fun RegisterDocumentUploadBox(
    title: String,
    subtitle: String,
    isRequired: Boolean = true,
    hasFile: Boolean,
    fileName: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector = Lucide.IdCard,
    onGalleryClick: () -> Unit,
    onCameraClick: () -> Unit,
    onRemoveClick: () -> Unit
) {
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
                        .background(Neutral0)
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
                                    .background(Primary0),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = icon,
                                    contentDescription = null,
                                    tint = Primary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = fileName.ifBlank { "dokumen_terlampir.jpg" },
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = TextPrimary,
                                    maxLines = 1,
                                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "Berkas Foto • Siap diverifikasi",
                                    fontSize = 11.sp,
                                    color = TextSecondary
                                )
                            }
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(
                                onClick = onGalleryClick,
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    imageVector = Lucide.RotateCw,
                                    contentDescription = "Ganti Foto",
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
                                    contentDescription = "Hapus Foto",
                                    tint = Error,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }
            } else {
                // Empty state: Gallery picker primary CTA + Camera scan secondary CTA
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // 1. Primary CTA: Pilih dari Galeri Foto
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Primary0)
                            .border(1.dp, Primary20, RoundedCornerShape(12.dp))
                            .clickable { onGalleryClick() }
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
                                    imageVector = Lucide.LucideImage,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Pilih dari Galeri Foto",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "Format JPG atau PNG (Maks. 5 MB)",
                                    fontSize = 11.5.sp,
                                    color = TextSecondary
                                )
                            }
                        }
                    }

                    // 2. Secondary CTA: Ambil Foto Kamera HP
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
                                text = "Atau ambil foto langsung dengan kamera HP",
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

// Step 5: Buat Kredensial Kata Sandi
@Composable
private fun Step5CredentialsForm(viewModel: RegisterViewModel, uiState: RegisterUiState) {
    Column(modifier = Modifier.fillMaxWidth()) {
        SectionHeader(title = "Keamanan Akun")

        PasswordField(
            value = uiState.password,
            onValueChange = viewModel::onPasswordChange,
            label = "Kata Sandi",
            placeholder = "Minimal 8 karakter",
            required = true,
            helperText = "Gunakan minimal 8 karakter dengan kombinasi huruf dan angka"
        )

        Spacer(modifier = Modifier.height(14.dp))

        PasswordField(
            value = uiState.confirmPassword,
            onValueChange = viewModel::onConfirmPasswordChange,
            label = "Konfirmasi Kata Sandi",
            placeholder = "Ulangi kata sandi Anda",
            required = true
        )
    }
}

// Step 6: Syarat & Ketentuan Layanan (Langkah Terakhir)
@Composable
private fun Step6TncForm(viewModel: RegisterViewModel, uiState: RegisterUiState) {
    Column(modifier = Modifier.fillMaxWidth()) {
        SectionHeader(title = "Syarat & Ketentuan Layanan")

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(280.dp),
            colors = CardDefaults.cardColors(containerColor = Neutral0),
            shape = RoundedCornerShape(14.dp),
            border = BorderStroke(1.dp, Border)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Lucide.FileText,
                        contentDescription = null,
                        tint = Primary,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = "KETENTUAN LAYANAN SAKU (PT SAKU)",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.5.sp,
                        color = TextPrimary
                    )
                }

                HorizontalDivider(color = Border)

                TncListItem(
                    number = "1",
                    title = "Kelayakan Pendaftaran Nasabah",
                    description = "Pendaftaran akun SAKU diperuntukkan bagi nasabah perorangan Warga Negara Indonesia (WNI) berusia minimal 21 tahun atau telah menikah, serta memiliki penghasilan tetap bulanan."
                )

                TncListItem(
                    number = "2",
                    title = "Keaslian Data & Dokumen Identitas (KYC)",
                    description = "Nasabah menjamin keaslian, keabsahan, keakuratan, dan keterbaruan seluruh data diri, pekerjaan, rekening bank, serta dokumen foto fisik e-KTP dan foto selfie yang diunggah ke dalam sistem SAKU."
                )

                TncListItem(
                    number = "3",
                    title = "Prosedur Verifikasi Tim Backoffice",
                    description = "Seluruh dokumen dan informasi yang didaftarkan akan melalui proses audit dan verifikasi manual oleh tim Backoffice SAKU sesuai regulasi perbankan dan otoritas keuangan yang berlaku."
                )

                TncListItem(
                    number = "4",
                    title = "Penilaian Kelayakan Kredit & Plafond",
                    description = "Nasabah memberikan kuasa dan persetujuan penuh kepada SAKU untuk melakukan analisis scoring risiko kredit dan penetapan limit plafond pembiayaan secara otomatis berdasarkan data yang diserahkan."
                )

                TncListItem(
                    number = "5",
                    title = "Perlindungan & Kerahasiaan Data Pribadi",
                    description = "SAKU berkomitmen melindungi seluruh kerahasiaan data pribadi nasabah dan tidak akan membagikannya kepada pihak ketiga tanpa persetujuan, sesuai ketentuan Undang-Undang Perlindungan Data Pribadi (UU PDP)."
                )

                TncListItem(
                    number = "6",
                    title = "Kewajiban Pengembalian Pembiayaan",
                    description = "Apabila pengajuan pinjaman disetujui dan dicairkan, nasabah wajib melakukan pembayaran angsuran tepat waktu sesuai jadwal jatuh tempo yang telah disepakati bersama."
                )

                TncListItem(
                    number = "7",
                    title = "Kekuatan Hukum Persetujuan Digital",
                    description = "Dengan mencentang persetujuan di bawah ini, nasabah menyatakan telah membaca, memahami, dan menyetujui seluruh ketentuan ini dengan kekuatan hukum yang sah dan mengikat."
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        CheckboxWithLabel(
            checked = uiState.isTncAgreed,
            onCheckedChange = viewModel::onTncAgreedToggle,
            label = "Saya telah membaca, memahami, dan menyetujui seluruh Syarat & Ketentuan serta Kebijakan Privasi SAKU.",
            modifier = Modifier.padding(vertical = 4.dp)
        )
    }
}

@Composable
private fun TncListItem(
    number: String,
    title: String,
    description: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(22.dp)
                .clip(CircleShape)
                .background(Primary0),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = number,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Primary
            )
        }

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 12.5.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(3.dp))
            Text(
                text = description,
                fontSize = 11.5.sp,
                color = TextSecondary,
                lineHeight = 16.5.sp
            )
        }
    }
}

@Composable
private fun ConfirmSummaryRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, fontSize = 12.sp, color = TextSecondary)
        Text(
            text = value.ifBlank { "-" },
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = TextPrimary
        )
    }
}
