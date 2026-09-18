package com.example.saku.app.features.loans.revision

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import androidx.compose.material3.AlertDialog
import com.composables.icons.lucide.ArrowLeft
import com.composables.icons.lucide.Camera
import com.composables.icons.lucide.CircleAlert
import com.composables.icons.lucide.CircleCheck
import com.composables.icons.lucide.FileText
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.RotateCw
import com.composables.icons.lucide.Trash2
import com.composables.icons.lucide.TriangleAlert
import com.example.saku.app.core.ui.components.Badge
import com.example.saku.app.core.ui.components.BadgeSize
import com.example.saku.app.core.ui.components.BadgeVariant
import com.example.saku.app.core.ui.components.Button
import com.example.saku.app.core.ui.components.ButtonSize
import com.example.saku.app.core.ui.components.ButtonVariant
import com.example.saku.app.core.ui.components.ConfirmationDialog
import com.example.saku.app.core.ui.components.DialogType
import com.example.saku.app.ui.theme.Background
import com.example.saku.app.ui.theme.Border
import com.example.saku.app.ui.theme.Error
import com.example.saku.app.ui.theme.Error0
import com.example.saku.app.ui.theme.Error80
import com.example.saku.app.ui.theme.Neutral0
import com.example.saku.app.ui.theme.Primary
import com.example.saku.app.ui.theme.Primary0
import com.example.saku.app.ui.theme.Primary20
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
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoanRevisionScreen(
    loanId: String,
    onNavigateBack: () -> Unit,
    onRevisionSuccess: () -> Unit,
    viewModel: LoanRevisionViewModel = koinViewModel()
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()

    var showConfirmDialog by remember { mutableStateOf(false) }
    var showSuccessDialog by remember { mutableStateOf(false) }

    LaunchedEffect(loanId) {
        viewModel.loadLoan(loanId)
    }

    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) {
            showSuccessDialog = true
        }
    }

    // Launchers for Slip Gaji
    val slipPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) viewModel.setSlipGaji(uri, null)
    }
    val slipCameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap: Bitmap? ->
        if (bitmap != null) viewModel.setSlipGaji(null, bitmap)
    }

    // Launchers for Rekening Koran
    val rekKoranPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) viewModel.setRekeningKoran(uri, null)
    }
    val rekKoranCameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap: Bitmap? ->
        if (bitmap != null) viewModel.setRekeningKoran(null, bitmap)
    }

    // Launchers for NPWP
    val npwpPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) viewModel.setNpwp(uri, null)
    }
    val npwpCameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap: Bitmap? ->
        if (bitmap != null) viewModel.setNpwp(null, bitmap)
    }

    val loan = uiState.loan

    Scaffold(
        topBar = {
            RevisionTopBar(
                nomorPengajuan = loan?.nomorPengajuan ?: "Pengajuan Pinjaman",
                onBackClick = onNavigateBack
            )
        },
        bottomBar = {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding(),
                shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
                colors = CardDefaults.cardColors(containerColor = Surface),
                border = BorderStroke(1.dp, Border)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Button(
                        text = if (uiState.isSubmitting) "Mengirim Dokumen..." else "Kirim Dokumen Revisi",
                        onClick = { showConfirmDialog = true },
                        enabled = uiState.canSubmit && !uiState.isSubmitting,
                        modifier = Modifier.fillMaxWidth(),
                        size = ButtonSize.LG
                    )
                }
            }
        },
        containerColor = Background
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (uiState.isLoading && loan == null) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Primary)
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Background),
                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 18.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // 1. Marketing Review Note Card (Warning Banner)
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(18.dp),
                            colors = CardDefaults.cardColors(containerColor = Warning0),
                            border = BorderStroke(1.dp, Warning20)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(32.dp)
                                            .clip(CircleShape)
                                            .background(Warning.copy(alpha = 0.15f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Lucide.TriangleAlert,
                                            contentDescription = null,
                                            tint = Warning,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            text = "Perlu Perbaikan Dokumen",
                                            fontSize = 13.5.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Warning80
                                        )
                                        Text(
                                            text = "Catatan Verifikator Marketing",
                                            fontSize = 11.5.sp,
                                            color = Warning70
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                val reviewNote = loan?.catatanReview?.takeIf { it.isNotBlank() }
                                    ?: "Mohon periksa kelengkapan dan kejelasan berkas dokumen keuangan Anda."

                                Text(
                                    text = reviewNote,
                                    fontSize = 12.5.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Warning80,
                                    lineHeight = 18.sp
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                Text(
                                    text = "Unggah berkas baru sesuai instruksi catatan di atas agar pengajuan pinjaman dapat diproses kembali.",
                                    fontSize = 11.5.sp,
                                    color = Warning70,
                                    lineHeight = 16.sp
                                )
                            }
                        }
                    }

                    // 2. Error alert if any
                    uiState.errorMessage?.let { err ->
                        item {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Error0)
                                    .padding(12.dp),
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
                                    text = err,
                                    fontSize = 12.sp,
                                    color = Error80,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }

                    // Section Heading
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Dokumen yang Perlu Diperbaiki",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            if (uiState.canSubmit) {
                                Badge(
                                    text = "Siap Dikirim",
                                    variant = BadgeVariant.Success,
                                    size = BadgeSize.SM
                                )
                            }
                        }
                    }

                    // 3. Document 1: Slip Gaji
                    if (uiState.needsSlipGaji) {
                        item {
                            RevisionDocumentUploadBox(
                                title = "Slip Gaji / Bukti Penghasilan",
                                subtitle = "Slip gaji 1 bulan terakhir dengan stempel/pengesahan resmi HRD",
                                isRequired = true,
                                uri = uiState.slipGajiUri,
                                bitmap = uiState.slipGajiBitmap,
                                onDocumentClick = { slipPickerLauncher.launch("*/*") },
                                onCameraClick = { slipCameraLauncher.launch(null) },
                                onRemoveClick = { viewModel.setSlipGaji(null, null) }
                            )
                        }
                    }

                    // 4. Document 2: Rekening Koran (3 Bulan Wajib)
                    if (uiState.needsRekeningKoran) {
                        item {
                            RevisionDocumentUploadBox(
                                title = "Rekening Koran 3 Bulan Terakhir",
                                subtitle = "Mutasi rekening bank aktif 3 bulan terakhir (bukan hanya 1-2 bulan)",
                                isRequired = true,
                                uri = uiState.rekeningKoranUri,
                                bitmap = uiState.rekeningKoranBitmap,
                                onDocumentClick = { rekKoranPickerLauncher.launch("*/*") },
                                onCameraClick = { rekKoranCameraLauncher.launch(null) },
                                onRemoveClick = { viewModel.setRekeningKoran(null, null) }
                            )
                        }
                    }

                    // 5. Document 3: Kartu NPWP
                    if (uiState.needsNpwp) {
                        item {
                            RevisionDocumentUploadBox(
                                title = "Kartu NPWP",
                                subtitle = "Foto fisik kartu NPWP jelas dan nomor NPWP dapat terbaca",
                                isRequired = true,
                                uri = uiState.npwpUri,
                                bitmap = uiState.npwpBitmap,
                                onDocumentClick = { npwpPickerLauncher.launch("*/*") },
                                onCameraClick = { npwpCameraLauncher.launch(null) },
                                onRemoveClick = { viewModel.setNpwp(null, null) }
                            )
                        }
                    }

                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }
        }
    }

    // Modal Konfirmasi Sebelum Kirim Revisi
    ConfirmationDialog(
        visible = showConfirmDialog,
        title = "Kirim Dokumen Revisi?",
        message = "Pastikan seluruh berkas yang Anda unggah sudah lengkap dan sesuai catatan perbaikan. Dokumen akan langsung ditinjau kembali oleh tim verifikator SAKU.",
        confirmButtonText = "Ya, Kirim Revisi",
        dismissButtonText = "Periksa Kembali",
        type = DialogType.INFO,
        isLoading = uiState.isSubmitting,
        onConfirm = {
            showConfirmDialog = false
            viewModel.submitRevision(context)
        },
        onDismiss = {
            if (!uiState.isSubmitting) {
                showConfirmDialog = false
            }
        }
    )

    // Modal Sukses Setelah Revisi Terkirim
    if (showSuccessDialog) {
        AlertDialog(
            onDismissRequest = { /* Modal ditutup lewat tombol */ },
            confirmButton = {
                Button(
                    text = "Kembali ke Detail Pinjaman",
                    onClick = {
                        showSuccessDialog = false
                        onRevisionSuccess()
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            },
            icon = {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(Success0),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Lucide.CircleCheck,
                        contentDescription = null,
                        tint = Success,
                        modifier = Modifier.size(32.dp)
                    )
                }
            },
            title = {
                Text(
                    text = "Dokumen Berhasil Dikirim!",
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp,
                    color = TextPrimary
                )
            },
            text = {
                Text(
                    text = "Berkas revisi Anda telah berhasil diunggah ulang dan sedang dalam antrean review tim SAKU. Anda akan mendapatkan update notifikasi segera.",
                    color = TextSecondary,
                    fontSize = 13.5.sp,
                    lineHeight = 19.sp
                )
            },
            containerColor = Color.White,
            shape = RoundedCornerShape(20.dp)
        )
    }
}

@Composable
private fun RevisionTopBar(
    nomorPengajuan: String,
    onBackClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(bottomStart = 20.dp, bottomEnd = 20.dp),
        colors = CardDefaults.cardColors(containerColor = Surface),
        border = BorderStroke(1.dp, Border)
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
            Column {
                Text(
                    text = "Revisi Dokumen Pinjaman",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Text(
                    text = nomorPengajuan,
                    fontSize = 12.sp,
                    color = TextSecondary
                )
            }
        }
    }
}

@Composable
private fun RevisionDocumentUploadBox(
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
            "Scan_${title.take(10).replace(" ", "_")}.jpg"
        } else if (uri != null) {
            getFileNameFromUri(context, uri)
        } else {
            ""
        }
    }
    val isPdf = remember(uri, fileName) {
        fileName.endsWith(".pdf", ignoreCase = true) ||
                (uri != null && context.contentResolver.getType(uri)?.contains("pdf", ignoreCase = true) == true)
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Surface),
        border = BorderStroke(1.dp, if (hasFile) Primary20 else Border)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
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
                    text = if (hasFile) "Terlampir" else (if (isRequired) "Perlu Revisi" else "Opsional"),
                    variant = if (hasFile) BadgeVariant.Success else (if (isRequired) BadgeVariant.Warning else BadgeVariant.Neutral),
                    size = BadgeSize.SM
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            if (hasFile) {
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
                                        tint = if (isPdf) Error else Primary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = fileName.ifBlank { "dokumen_revisi" },
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = TextPrimary,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = if (isPdf) "Dokumen PDF • Berkas perbaikan" else "Berkas Gambar • Berkas perbaikan",
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
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
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
                                    text = "Pilih File Dokumen Revisi",
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
                                text = "Atau ambil foto fisik langsung dengan kamera",
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
            uri.lastPathSegment ?: "dokumen_revisi"
        } else {
            name
        }
    } catch (e: Exception) {
        uri.lastPathSegment ?: "dokumen_revisi"
    }
}
