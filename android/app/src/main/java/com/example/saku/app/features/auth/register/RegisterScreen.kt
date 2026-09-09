package com.example.saku.app.features.auth.register

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import com.composables.icons.lucide.ArrowLeft
import com.composables.icons.lucide.Briefcase
import com.composables.icons.lucide.Check
import com.composables.icons.lucide.CircleCheck
import com.composables.icons.lucide.House
import com.composables.icons.lucide.IdCard
import com.composables.icons.lucide.ImagePlus
import com.composables.icons.lucide.Lock
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Mail
import com.composables.icons.lucide.Phone
import com.composables.icons.lucide.User
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.saku.app.core.ui.components.Button
import com.example.saku.app.core.ui.components.ButtonVariant
import com.example.saku.app.core.ui.components.TextField
import com.example.saku.app.ui.theme.Background
import com.example.saku.app.ui.theme.Border
import com.example.saku.app.ui.theme.Error
import com.example.saku.app.ui.theme.Neutral10
import com.example.saku.app.ui.theme.Neutral20
import com.example.saku.app.ui.theme.Neutral40
import com.example.saku.app.ui.theme.Neutral60
import com.example.saku.app.ui.theme.Primary
import com.example.saku.app.ui.theme.Primary0
import com.example.saku.app.ui.theme.Success
import com.example.saku.app.ui.theme.Success0
import com.example.saku.app.ui.theme.Surface
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

    // Success Dialog
    if (uiState.isRegistrationComplete) {
        AlertDialog(
            onDismissRequest = { /* Prevent dismiss without action */ },
            confirmButton = {
                Button(
                    text = "Masuk Sekarang",
                    onClick = onNavigateToLogin
                )
            },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Lucide.CircleCheck,
                        contentDescription = null,
                        tint = Success,
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "Pendaftaran Berhasil!", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                }
            },
            text = {
                Text(
                    text = "Selamat! Akun nasabah SAKU Anda telah berhasil dibuat. Silahkan masuk menggunakan akun baru Anda.",
                    color = TextSecondary,
                    fontSize = 14.sp
                )
            },
            containerColor = Surface,
            shape = RoundedCornerShape(16.dp)
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Daftar Akun SAKU",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = TextPrimary
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        if (uiState.currentStep > 1) {
                            viewModel.goToPreviousStep()
                        } else {
                            onNavigateBack()
                        }
                    }) {
                        Icon(
                            imageVector = Lucide.ArrowLeft,
                            contentDescription = "Kembali",
                            tint = TextPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Background)
            )
        },
        containerColor = Background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 8.dp)
        ) {
            // Step Progress Indicator
            StepProgressBar(currentStep = uiState.currentStep)

            Spacer(modifier = Modifier.height(20.dp))

            // Error banner
            uiState.errorMessage?.let { error ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFEE2E2)),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(
                        text = error,
                        color = Error,
                        fontSize = 13.sp,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }

            // Success banner
            uiState.successMessage?.let { success ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    colors = CardDefaults.cardColors(containerColor = Success0),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(
                        text = success,
                        color = Success,
                        fontSize = 13.sp,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }

            // Step Forms
            when (uiState.currentStep) {
                1 -> Step1AccountForm(viewModel = viewModel, uiState = uiState)
                2 -> Step2PersonalForm(viewModel = viewModel, uiState = uiState)
                3 -> Step3AddressForm(viewModel = viewModel, uiState = uiState)
                4 -> Step4KycForm(viewModel = viewModel, uiState = uiState)
            }

            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}

@Composable
fun StepProgressBar(currentStep: Int) {
    val steps = listOf("Akun", "Pribadi", "Alamat", "Dokumen")

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        steps.forEachIndexed { index, title ->
            val stepNumber = index + 1
            val isCompleted = stepNumber < currentStep
            val isCurrent = stepNumber == currentStep

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(
                            when {
                                isCompleted -> Success
                                isCurrent -> Primary
                                else -> Neutral20
                            }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (isCompleted) {
                        Icon(
                            imageVector = Lucide.Check,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    } else {
                        Text(
                            text = "$stepNumber",
                            color = if (isCurrent) Color.White else Neutral60,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = title,
                    fontSize = 11.sp,
                    fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal,
                    color = if (isCurrent) Primary else TextSecondary
                )
            }
        }
    }
}

// Step 1: Account Form
@Composable
fun Step1AccountForm(viewModel: RegisterViewModel, uiState: RegisterUiState) {
    Column {
        Text(
            text = "Informasi Akun",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )
        Text(
            text = "Buat kredensial akun nasabah SAKU Anda",
            fontSize = 13.sp,
            color = TextSecondary
        )

        Spacer(modifier = Modifier.height(20.dp))

        TextField(
            value = uiState.email,
            onValueChange = viewModel::onEmailChange,
            label = "Alamat Email",
            placeholder = "nama@email.com",
            leadingIcon = Lucide.Mail,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
        )

        Spacer(modifier = Modifier.height(12.dp))

        // OTP Request row
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            TextField(
                value = uiState.otpCode,
                onValueChange = viewModel::onOtpCodeChange,
                label = "Kode OTP (Email)",
                placeholder = "6 digit kode",
                modifier = Modifier.weight(1f),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            Spacer(modifier = Modifier.width(8.dp))

            Button(
                text = if (uiState.otpCountdown > 0) "${uiState.otpCountdown}s" else if (uiState.isOtpSent) "Kirim Ulang" else "Kirim OTP",
                onClick = { viewModel.sendOtp() },
                variant = ButtonVariant.Secondary,
                enabled = uiState.otpCountdown == 0 && !uiState.isLoading,
                modifier = Modifier.padding(top = 18.dp)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        TextField(
            value = uiState.username,
            onValueChange = viewModel::onUsernameChange,
            label = "Username",
            placeholder = "contoh: budi_santoso",
            leadingIcon = Lucide.User
        )

        Spacer(modifier = Modifier.height(12.dp))

        TextField(
            value = uiState.noHp,
            onValueChange = viewModel::onNoHpChange,
            label = "Nomor Handphone",
            placeholder = "081234567890",
            leadingIcon = Lucide.Phone,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
        )

        Spacer(modifier = Modifier.height(12.dp))

        TextField(
            value = uiState.password,
            onValueChange = viewModel::onPasswordChange,
            label = "Password",
            placeholder = "Minimal 8 karakter",
            isPassword = true,
            leadingIcon = Lucide.Lock
        )

        Spacer(modifier = Modifier.height(12.dp))

        TextField(
            value = uiState.confirmPassword,
            onValueChange = viewModel::onConfirmPasswordChange,
            label = "Konfirmasi Password",
            placeholder = "Ulangi password",
            isPassword = true,
            leadingIcon = Lucide.Lock
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            text = "Lanjut ke Data Pribadi",
            onClick = { viewModel.submitStep1() },
            isLoading = uiState.isLoading
        )
    }
}

// Step 2: Personal & Financial Form
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Step2PersonalForm(viewModel: RegisterViewModel, uiState: RegisterUiState) {
    var statusExpanded by remember { mutableStateOf(false) }
    val statusOptions = listOf("TETAP", "KONTRAK", "WIRASWASTA", "PROFESIONAL", "LAINNYA")

    Column {
        Text(
            text = "Data Pribadi & Keuangan",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )
        Text(
            text = "Lengkapi data identitas dan pekerjaan untuk evaluasi plafond",
            fontSize = 13.sp,
            color = TextSecondary
        )

        Spacer(modifier = Modifier.height(20.dp))

        TextField(
            value = uiState.nik,
            onValueChange = { if (it.length <= 16) viewModel.onNikChange(it) },
            label = "Nomor Induk Kependudukan (NIK)",
            placeholder = "16 digit NIK sesuai KTP",
            leadingIcon = Lucide.IdCard,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )

        Spacer(modifier = Modifier.height(12.dp))

        TextField(
            value = uiState.namaLengkap,
            onValueChange = viewModel::onNamaLengkapChange,
            label = "Nama Lengkap Sesuai KTP",
            placeholder = "Contoh: Budi Santoso",
            leadingIcon = Lucide.User
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            TextField(
                value = uiState.namaBank,
                onValueChange = viewModel::onNamaBankChange,
                label = "Nama Bank",
                placeholder = "BCA / Mandiri / BRI",
                modifier = Modifier.weight(1f)
            )
            TextField(
                value = uiState.noRekening,
                onValueChange = viewModel::onNoRekeningChange,
                label = "Nomor Rekening",
                placeholder = "1234567890",
                modifier = Modifier.weight(1f),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        TextField(
            value = uiState.namaRekening,
            onValueChange = viewModel::onNamaRekeningChange,
            label = "Nama Pemilik Rekening",
            placeholder = "Nama pada buku tabungan"
        )

        Spacer(modifier = Modifier.height(12.dp))

        TextField(
            value = uiState.pekerjaan,
            onValueChange = viewModel::onPekerjaanChange,
            label = "Profesi / Pekerjaan",
            placeholder = "Contoh: Staff IT / Supervisor",
            leadingIcon = Lucide.Briefcase
        )

        Spacer(modifier = Modifier.height(12.dp))

        TextField(
            value = uiState.tempatKerja,
            onValueChange = viewModel::onTempatKerjaChange,
            label = "Nama Perusahaan / Tempat Kerja",
            placeholder = "PT Contoh Digital Indonesia"
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Status Pekerjaan Dropdown
        ExposedDropdownMenuBox(
            expanded = statusExpanded,
            onExpandedChange = { statusExpanded = it }
        ) {
            OutlinedTextField(
                value = uiState.statusPekerjaan,
                onValueChange = {},
                readOnly = true,
                label = { Text("Status Pekerjaan") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = statusExpanded) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Primary,
                    unfocusedBorderColor = Border,
                    focusedContainerColor = Surface,
                    unfocusedContainerColor = Surface
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable, true)
            )
            ExposedDropdownMenu(
                expanded = statusExpanded,
                onDismissRequest = { statusExpanded = false }
            ) {
                statusOptions.forEach { opt ->
                    DropdownMenuItem(
                        text = { Text(opt) },
                        onClick = {
                            viewModel.onStatusPekerjaanChange(opt)
                            statusExpanded = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            TextField(
                value = uiState.pendapatan,
                onValueChange = viewModel::onPendapatanChange,
                label = "Pendapatan/Bulan (Rp)",
                placeholder = "10000000",
                modifier = Modifier.weight(1.2f),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
            TextField(
                value = uiState.lamaBekerjaBulan,
                onValueChange = viewModel::onLamaBekerjaChange,
                label = "Masa Kerja (Bulan)",
                placeholder = "24",
                modifier = Modifier.weight(0.8f),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        TextField(
            value = uiState.totalCicilanLainnya,
            onValueChange = viewModel::onCicilanLainnyaChange,
            label = "Total Cicilan Lain/Bulan (Rp)",
            placeholder = "0 jika tidak ada",
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Button(
                text = "Kembali",
                onClick = { viewModel.goToPreviousStep() },
                variant = ButtonVariant.Outline,
                modifier = Modifier.weight(1f)
            )
            Button(
                text = "Lanjut ke Alamat",
                onClick = { viewModel.submitStep2() },
                isLoading = uiState.isLoading,
                modifier = Modifier.weight(1.5f)
            )
        }
    }
}

// Step 3: Address Form
@Composable
fun Step3AddressForm(viewModel: RegisterViewModel, uiState: RegisterUiState) {
    Column {
        Text(
            text = "Informasi Alamat",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )
        Text(
            text = "Alamat KTP dan domisili tempat tinggal Anda saat ini",
            fontSize = 13.sp,
            color = TextSecondary
        )

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Alamat Sesuai KTP",
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold,
            color = Primary
        )

        Spacer(modifier = Modifier.height(8.dp))

        AlamatInputs(
            form = uiState.alamatKtp,
            onFormChange = viewModel::onAlamatKtpChange
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Same as KTP Checkbox
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .clickable { viewModel.onSameAsKtpToggle(!uiState.sameAsKtp) }
                .padding(vertical = 4.dp)
        ) {
            Checkbox(
                checked = uiState.sameAsKtp,
                onCheckedChange = viewModel::onSameAsKtpToggle,
                colors = CheckboxDefaults.colors(checkedColor = Primary)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "Alamat domisili saat ini sama dengan alamat KTP",
                fontSize = 13.sp,
                color = TextPrimary
            )
        }

        if (!uiState.sameAsKtp) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Alamat Domisili",
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = Primary
            )
            Spacer(modifier = Modifier.height(8.dp))
            AlamatInputs(
                form = uiState.alamatDomisili,
                onFormChange = viewModel::onAlamatDomisiliChange
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Button(
                text = "Kembali",
                onClick = { viewModel.goToPreviousStep() },
                variant = ButtonVariant.Outline,
                modifier = Modifier.weight(1f)
            )
            Button(
                text = "Lanjut ke Dokumen",
                onClick = { viewModel.submitStep3() },
                isLoading = uiState.isLoading,
                modifier = Modifier.weight(1.5f)
            )
        }
    }
}

@Composable
fun AlamatInputs(
    form: AlamatFormState,
    onFormChange: (AlamatFormState) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        TextField(
            value = form.alamatLengkap,
            onValueChange = { onFormChange(form.copy(alamatLengkap = it)) },
            label = "Alamat Lengkap (Jalan / No. Rumah)",
            placeholder = "Jl. Sudirman No. 123",
            leadingIcon = Lucide.House
        )

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            TextField(
                value = form.rt,
                onValueChange = { onFormChange(form.copy(rt = it)) },
                label = "RT",
                placeholder = "001",
                modifier = Modifier.weight(1f),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
            TextField(
                value = form.rw,
                onValueChange = { onFormChange(form.copy(rw = it)) },
                label = "RW",
                placeholder = "005",
                modifier = Modifier.weight(1f),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
            TextField(
                value = form.kodePos,
                onValueChange = { onFormChange(form.copy(kodePos = it)) },
                label = "Kode Pos",
                placeholder = "12345",
                modifier = Modifier.weight(1.2f),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
        }

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            TextField(
                value = form.kelurahan,
                onValueChange = { onFormChange(form.copy(kelurahan = it)) },
                label = "Kelurahan / Desa",
                placeholder = "Menteng",
                modifier = Modifier.weight(1f)
            )
            TextField(
                value = form.kecamatan,
                onValueChange = { onFormChange(form.copy(kecamatan = it)) },
                label = "Kecamatan",
                placeholder = "Menteng",
                modifier = Modifier.weight(1f)
            )
        }

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            TextField(
                value = form.kotaKabupaten,
                onValueChange = { onFormChange(form.copy(kotaKabupaten = it)) },
                label = "Kota / Kabupaten",
                placeholder = "Jakarta Pusat",
                modifier = Modifier.weight(1f)
            )
            TextField(
                value = form.provinsi,
                onValueChange = { onFormChange(form.copy(provinsi = it)) },
                label = "Provinsi",
                placeholder = "DKI Jakarta",
                modifier = Modifier.weight(1f)
            )
        }
    }
}

// Step 4: KYC Document Upload Form
@Composable
fun Step4KycForm(viewModel: RegisterViewModel, uiState: RegisterUiState) {
    val ktpPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        viewModel.onKtpUriSelected(uri)
    }

    val selfiePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        viewModel.onSelfieUriSelected(uri)
    }

    Column {
        Text(
            text = "Upload Dokumen & KYC",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )
        Text(
            text = "Unggah foto KTP dan selfie untuk proses verifikasi identitas (KYC)",
            fontSize = 13.sp,
            color = TextSecondary
        )

        Spacer(modifier = Modifier.height(20.dp))

        // KTP Upload Card
        UploadBox(
            title = "Foto e-KTP",
            subtitle = "Pastikan e-KTP terbaca jelas dan tidak buram",
            selectedUri = uiState.ktpUri,
            onPickClick = { ktpPickerLauncher.launch("image/*") }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Selfie Upload Card
        UploadBox(
            title = "Foto Selfie Wajah",
            subtitle = "Wajah menghadap lurus ke kamera, pencahayaan terang",
            selectedUri = uiState.selfieUri,
            onPickClick = { selfiePickerLauncher.launch("image/*") }
        )

        Spacer(modifier = Modifier.height(24.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Button(
                text = "Kembali",
                onClick = { viewModel.goToPreviousStep() },
                variant = ButtonVariant.Outline,
                modifier = Modifier.weight(1f)
            )
            Button(
                text = "Selesaikan Pendaftaran",
                onClick = { viewModel.submitStep4() },
                isLoading = uiState.isLoading,
                modifier = Modifier.weight(1.5f)
            )
        }
    }
}

@Composable
fun UploadBox(
    title: String,
    subtitle: String,
    selectedUri: Uri?,
    onPickClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .border(1.5.dp, if (selectedUri != null) Success else Border, RoundedCornerShape(16.dp))
            .clickable { onPickClick() },
        colors = CardDefaults.cardColors(containerColor = Surface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (selectedUri != null) {
                AsyncImage(
                    model = selectedUri,
                    contentDescription = title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                        .clip(RoundedCornerShape(12.dp))
                )
                Spacer(modifier = Modifier.height(10.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Lucide.CircleCheck,
                        contentDescription = null,
                        tint = Success,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Foto berhasil dipilih (Ketuk untuk ganti)",
                        fontSize = 12.sp,
                        color = Success,
                        fontWeight = FontWeight.Medium
                    )
                }
            } else {
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(CircleShape)
                        .background(Primary0),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Lucide.ImagePlus,
                        contentDescription = null,
                        tint = Primary,
                        modifier = Modifier.size(28.dp)
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = subtitle,
                    fontSize = 12.sp,
                    color = TextSecondary
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Pilih dari Galeri / Kamera",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Primary
                )
            }
        }
    }
}
