package com.example.saku.app.features.loans.detail

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.composables.icons.lucide.ArrowLeft
import com.composables.icons.lucide.Building
import com.composables.icons.lucide.Calendar
import com.composables.icons.lucide.Check
import com.composables.icons.lucide.CircleAlert
import com.composables.icons.lucide.CircleCheck
import com.composables.icons.lucide.Clock
import com.composables.icons.lucide.Copy
import com.composables.icons.lucide.CreditCard
import com.composables.icons.lucide.FileText
import com.composables.icons.lucide.Info
import com.composables.icons.lucide.Landmark
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.ShieldAlert
import com.composables.icons.lucide.ShieldCheck
import com.composables.icons.lucide.UserCheck
import com.composables.icons.lucide.Wallet
import com.example.saku.app.core.network.dto.AngsuranItemDto
import com.example.saku.app.core.network.dto.LoanApplicationItemDto
import com.example.saku.app.core.ui.components.Badge
import com.example.saku.app.core.ui.components.BadgeSize
import com.example.saku.app.core.ui.components.BadgeVariant
import com.example.saku.app.core.ui.components.Button
import com.example.saku.app.core.ui.components.ButtonSize
import com.example.saku.app.core.ui.components.ButtonVariant
import com.example.saku.app.features.loans.payment.PaymentInfoBottomSheet
import com.example.saku.app.ui.theme.Background
import com.example.saku.app.ui.theme.Border
import com.example.saku.app.ui.theme.Error
import com.example.saku.app.ui.theme.Error0
import com.example.saku.app.ui.theme.Error70
import com.example.saku.app.ui.theme.Error80
import com.example.saku.app.ui.theme.Neutral10
import com.example.saku.app.ui.theme.Neutral20
import com.example.saku.app.ui.theme.Primary
import com.example.saku.app.ui.theme.Primary0
import com.example.saku.app.ui.theme.Primary20
import com.example.saku.app.ui.theme.Primary70
import com.example.saku.app.ui.theme.Primary80
import com.example.saku.app.ui.theme.Surface
import com.example.saku.app.ui.theme.TextMuted
import com.example.saku.app.ui.theme.TextPrimary
import com.example.saku.app.ui.theme.TextSecondary
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoanDetailScreen(
    loanId: String,
    onNavigateBack: () -> Unit,
    viewModel: LoanDetailViewModel = viewModel()
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    val currencyFormatter = remember { NumberFormat.getNumberInstance(Locale.forLanguageTag("id-ID")) }

    LaunchedEffect(loanId) {
        viewModel.fetchLoanDetail(loanId)
    }

    val loan = uiState.loan
    val rawStatus = (loan?.statusPengajuan ?: "PENDING").uppercase()
    val isDisbursed = rawStatus in listOf("DICAIRKAN", "DISBURSED", "APPROVED", "DISETUJUI")
    val hasUnpaidAngsuran = uiState.angsuranList.any { it.statusBayar != "LUNAS" }

    Scaffold(
        topBar = {
            LoanDetailTopBar(
                nomorPengajuan = loan?.nomorPengajuan ?: "Detail Pinjaman",
                status = rawStatus,
                onBackClick = onNavigateBack
            )
        },
        bottomBar = {
            if (isDisbursed && hasUnpaidAngsuran) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(12.dp),
                    color = Surface
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 14.dp)
                    ) {
                        Button(
                            text = "Bayar Angsuran Sekarang",
                            onClick = { viewModel.openPaymentSheet() },
                            modifier = Modifier.fillMaxWidth(),
                            size = ButtonSize.LG
                        )
                    }
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
            } else if (loan != null) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Background),
                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 18.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // 1. Status Tracking Timeline Card
                    item {
                        LoanTrackingTimelineCard(status = rawStatus)
                    }

                    // 2. Review Note Alert (if any)
                    loan.catatanReview?.takeIf { it.isNotBlank() }?.let { note ->
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
                                    Column {
                                        Text(
                                            text = "Catatan Review Verifikator",
                                            fontSize = 12.5.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Primary80
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = note,
                                            fontSize = 12.sp,
                                            color = Primary70,
                                            lineHeight = 16.sp
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // 3. Loan Financial Specifications Card
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
                                        text = "Rincian Pembiayaan",
                                        fontSize = 14.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary
                                    )
                                    IconButton(
                                        onClick = {
                                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                            val clip = ClipData.newPlainText("No Pengajuan", loan.nomorPengajuan)
                                            clipboard.setPrimaryClip(clip)
                                            Toast.makeText(context, "Nomor pengajuan disalin", Toast.LENGTH_SHORT).show()
                                        },
                                        modifier = Modifier.size(28.dp)
                                    ) {
                                        Icon(
                                            imageVector = Lucide.Copy,
                                            contentDescription = "Salin",
                                            tint = TextMuted,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(14.dp))

                                DetailRow(label = "Nomor Pengajuan", value = loan.nomorPengajuan ?: "-")
                                DetailRow(label = "Nominal Pinjaman", value = "Rp ${currencyFormatter.format(loan.jumlahPinjaman ?: 0.0)}")
                                DetailRow(label = "Tenor Pinjaman", value = "${loan.tenorBulan ?: 0} Bulan")
                                val rawBunga = loan.bunga ?: 1.5
                                val displayBunga = if (rawBunga <= 1.0 && rawBunga > 0.0) rawBunga * 100 else rawBunga
                                DetailRow(label = "Suku Bunga", value = "$displayBunga% flat / bulan")
                                DetailRow(label = "Biaya Administrasi", value = "Rp ${currencyFormatter.format(loan.biayaAdmin ?: 0.0)}")
                                DetailRow(label = "Tujuan Pinjaman", value = loan.tujuanPinjaman ?: "-")
                                DetailRow(label = "Cabang Pengelola", value = loan.namaCabang ?: "BCA Finance Pusat")

                                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = Border)

                                DetailRow(
                                    label = "Estimasi Angsuran Bulanan",
                                    value = "Rp ${currencyFormatter.format(loan.estimasiAngsuranBulanan ?: 0.0)} / bln",
                                    isHighlight = true
                                )
                            }
                        }
                    }

                    // 4. Jadwal Angsuran Card (Uniform with Rincian Pembiayaan Card)
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
                                        text = "Jadwal Angsuran",
                                        fontSize = 14.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary
                                    )
                                    if (uiState.angsuranList.isNotEmpty()) {
                                        val lunasCount = uiState.angsuranList.count { it.statusBayar == "LUNAS" }
                                        Text(
                                            text = "$lunasCount/${uiState.angsuranList.size} Lunas",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = TextMuted
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(14.dp))

                                if (uiState.angsuranList.isEmpty()) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 12.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Icon(
                                            imageVector = Lucide.Calendar,
                                            contentDescription = null,
                                            tint = TextMuted,
                                            modifier = Modifier.size(32.dp)
                                        )
                                        Spacer(modifier = Modifier.height(10.dp))
                                        Text(
                                            text = "Jadwal Angsuran Belum Terbit",
                                            fontSize = 13.5.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = TextPrimary
                                        )
                                        Spacer(modifier = Modifier.height(3.dp))
                                        Text(
                                            text = "Tabel angsuran akan aktif otomatis setelah pinjaman disetujui & dicairkan.",
                                            fontSize = 12.sp,
                                            color = TextSecondary,
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                } else {
                                    uiState.angsuranList.forEachIndexed { index, item ->
                                        if (index > 0) {
                                            HorizontalDivider(
                                                modifier = Modifier.padding(vertical = 12.dp),
                                                color = Border.copy(alpha = 0.6f)
                                            )
                                        }
                                        AngsuranItemRow(
                                            angsuran = item,
                                            currencyFormatter = currencyFormatter,
                                            onPayClick = { viewModel.openPaymentSheet(item) }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Payment Bottom Sheet
    if (uiState.showPaymentSheet) {
        PaymentInfoBottomSheet(
            loan = loan,
            angsuran = uiState.selectedAngsuranForPayment,
            onDismiss = { viewModel.closePaymentSheet() }
        )
    }
}

// Top Bar
@Composable
private fun LoanDetailTopBar(
    nomorPengajuan: String,
    status: String,
    onBackClick: () -> Unit
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
                    text = "Detail Pinjaman",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Text(
                    text = nomorPengajuan,
                    fontSize = 11.5.sp,
                    color = TextMuted
                )
            }

            val (badgeText, badgeVariant) = when (status) {
                "DICAIRKAN", "DISBURSED" -> "Dicairkan" to BadgeVariant.Success
                "DISETUJUI", "APPROVED", "PENGAJUAN_DISETUJUI" -> "Disetujui BM" to BadgeVariant.Success
                "MENUNGGU_PENCAIRAN" -> "Menunggu Pencairan" to BadgeVariant.Success
                "SELESAI_DIREVIEW", "MENUNGGU_PERSETUJUAN", "DISETUJUI_MARKETING" -> "Disetujui Marketing" to BadgeVariant.Primary
                "VERIFIKASI_MARKETING", "MENUNGGU_REVIEW" -> "Review Marketing" to BadgeVariant.Primary
                "DITOLAK", "REJECTED", "PENGAJUAN_DITOLAK", "DITOLAK_MARKETING", "DITOLAK_BM", "REJECT", "BATAL", "CANCELLED" -> "Ditolak" to BadgeVariant.Error
                "PAID", "LUNAS" -> "Lunas" to BadgeVariant.Success
                "PERLU_REVISI", "REVISI" -> "Perlu Revisi" to BadgeVariant.Warning
                else -> "Dalam Proses" to BadgeVariant.Primary
            }
            Badge(text = badgeText, variant = badgeVariant, size = BadgeSize.SM)
        }
    }
}

// Status Timeline Card
@Composable
private fun LoanTrackingTimelineCard(status: String) {
    val s = status.uppercase()
    val isRejected = s in listOf("DITOLAK", "REJECTED", "PENGAJUAN_DITOLAK", "DITOLAK_MARKETING", "DITOLAK_BM", "REJECT", "BATAL", "CANCELLED")

    // Step 1: Pengajuan Terkirim (Centang jika form berhasil diajukan dan bukan ditolak awal)
    val step1Passed = !isRejected && s !in listOf("DRAFT")
    val step1Current = false

    // Step 2: Verifikasi Marketing & Dokumen (Centang jika Marketing sudah ACC / status lanjut ke tahap berikutnya)
    val step2Passed = !isRejected && s in listOf(
        "SELESAI_DIREVIEW", "MENUNGGU_PERSETUJUAN", "DISETUJUI_MARKETING",
        "PENGAJUAN_DISETUJUI", "APPROVED", "DISETUJUI", "MENUNGGU_PENCAIRAN",
        "DICAIRKAN", "DISBURSED", "LUNAS", "PAID"
    )
    val step2Current = !isRejected && s in listOf("PENDING", "SUBMITTED", "MENUNGGU_REVIEW", "VERIFIKASI_MARKETING", "MENUNGGU_DOKUMEN", "PERLU_REVISI", "REVISI")

    // Step 3: Persetujuan Branch Manager (Centang jika BM sudah ACC atau dana dicairkan)
    val step3Passed = !isRejected && s in listOf(
        "PENGAJUAN_DISETUJUI", "APPROVED", "DISETUJUI", "MENUNGGU_PENCAIRAN",
        "DICAIRKAN", "DISBURSED", "LUNAS", "PAID"
    )
    val step3Current = !isRejected && s in listOf("SELESAI_DIREVIEW", "MENUNGGU_PERSETUJUAN", "DISETUJUI_MARKETING")

    // Step 4: Pencairan Dana ke Rekening (Centang jika uang sudah ditransfer / dicairkan)
    val step4Passed = !isRejected && s in listOf("DICAIRKAN", "DISBURSED", "LUNAS", "PAID")
    val step4Current = !isRejected && s in listOf("PENGAJUAN_DISETUJUI", "APPROVED", "DISETUJUI", "MENUNGGU_PENCAIRAN")

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
                    text = "Status Pengajuan",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                if (isRejected) {
                    Badge(text = "Ditolak", variant = BadgeVariant.Error, size = BadgeSize.SM)
                }
            }

            if (isRejected) {
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
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
                        text = "Pengajuan pinjaman tidak disetujui / ditolak.",
                        fontSize = 12.sp,
                        color = Error80,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            TimelineStepItem(
                step = 1,
                title = "Pengajuan Terkirim",
                desc = "Data formulir dan berkas dokumen diterima sistem",
                isPassed = step1Passed,
                isCurrent = step1Current,
                isLast = false
            )

            TimelineStepItem(
                step = 2,
                title = "Verifikasi Marketing & Dokumen",
                desc = if (step2Passed) "Berkas dokumen telah diverifikasi & disetujui tim marketing" else "Pemeriksaan kelengkapan berkas oleh tim operasional",
                isPassed = step2Passed,
                isCurrent = step2Current,
                isLast = false
            )

            TimelineStepItem(
                step = 3,
                title = "Persetujuan Branch Manager",
                desc = if (step3Passed) "Plafond pinjaman telah disetujui oleh Branch Manager" else "Persetujuan final plafon kredit oleh kepala cabang",
                isPassed = step3Passed,
                isCurrent = step3Current,
                isLast = false
            )

            TimelineStepItem(
                step = 4,
                title = "Pencairan Dana ke Rekening",
                desc = if (step4Passed) "Dana pinjaman telah berhasil ditransfer ke rekening bank Anda" else "Dana pinjaman ditransfer ke rekening BCA Anda",
                isPassed = step4Passed,
                isCurrent = step4Current,
                isLast = true
            )
        }
    }
}

@Composable
private fun TimelineStepItem(
    step: Int,
    title: String,
    desc: String,
    isPassed: Boolean,
    isCurrent: Boolean,
    isLast: Boolean
) {
    Row(modifier = Modifier.fillMaxWidth()) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(
                        when {
                            isPassed -> Primary
                            isCurrent -> Primary.copy(alpha = 0.12f)
                            else -> Neutral20
                        }
                    )
                    .then(
                        if (isCurrent && !isPassed) Modifier.border(1.5.dp, Primary, CircleShape) else Modifier
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (isPassed) {
                    Icon(
                        imageVector = Lucide.Check,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(14.dp)
                    )
                } else {
                    Text(
                        text = "$step",
                        fontSize = 11.sp,
                        color = if (isCurrent) Primary else TextMuted,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            if (!isLast) {
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .height(34.dp)
                        .background(if (isPassed) Primary else Neutral20)
                )
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.padding(bottom = if (isLast) 0.dp else 12.dp)) {
            Text(
                text = title,
                fontSize = 13.sp,
                fontWeight = if (isCurrent || isPassed) FontWeight.Bold else FontWeight.Medium,
                color = if (isCurrent || isPassed) TextPrimary else TextMuted
            )
            Spacer(modifier = Modifier.height(1.dp))
            Text(
                text = desc,
                fontSize = 11.5.sp,
                color = if (isCurrent) TextSecondary else TextMuted,
                lineHeight = 15.sp
            )
        }
    }
}

// Angsuran Item Row
@Composable
private fun AngsuranItemRow(
    angsuran: AngsuranItemDto,
    currencyFormatter: NumberFormat,
    onPayClick: () -> Unit
) {
    val isLunas = angsuran.statusBayar == "LUNAS"

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Surface),
        border = BorderStroke(1.dp, Border)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Cicilan Ke-${angsuran.cicilanKe ?: 1}",
                    fontSize = 13.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Jatuh Tempo: ${angsuran.jatuhTempo ?: "-"}",
                    fontSize = 11.5.sp,
                    color = TextMuted
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Rp ${currencyFormatter.format(angsuran.jumlahAngsuran ?: 0.0)}",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = if (isLunas) TextSecondary else Primary
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            if (isLunas) {
                Badge(text = "Lunas", variant = BadgeVariant.Success, size = BadgeSize.SM)
            } else {
                Button(
                    text = "Bayar",
                    onClick = onPayClick,
                    variant = ButtonVariant.Primary,
                    size = ButtonSize.SM,
                    fullWidth = false
                )
            }
        }
    }
}

@Composable
private fun DetailRow(
    label: String,
    value: String,
    isHighlight: Boolean = false
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = label,
            fontSize = if (isHighlight) 13.sp else 12.sp,
            color = if (isHighlight) TextPrimary else TextMuted,
            fontWeight = if (isHighlight) FontWeight.SemiBold else FontWeight.Normal,
            modifier = Modifier.weight(0.38f)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = value,
            fontSize = if (isHighlight) 14.sp else 12.5.sp,
            fontWeight = if (isHighlight) FontWeight.ExtraBold else FontWeight.SemiBold,
            color = if (isHighlight) Primary else TextPrimary,
            textAlign = TextAlign.End,
            modifier = Modifier.weight(0.62f),
            lineHeight = 16.5.sp
        )
    }
}
