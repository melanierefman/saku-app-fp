package com.example.saku.app.features.history

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.composables.icons.lucide.ArrowLeft
import com.composables.icons.lucide.FileText
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.RefreshCw
import com.example.saku.app.core.network.dto.LoanApplicationItemDto
import com.example.saku.app.core.ui.components.Badge
import com.example.saku.app.core.ui.components.BadgeSize
import com.example.saku.app.core.ui.components.BadgeVariant
import com.example.saku.app.core.ui.components.Button
import com.example.saku.app.core.ui.components.ButtonSize
import com.example.saku.app.core.ui.components.ButtonVariant
import com.example.saku.app.ui.theme.Background
import com.example.saku.app.ui.theme.Border
import com.example.saku.app.ui.theme.Neutral0
import com.example.saku.app.ui.theme.Primary
import com.example.saku.app.ui.theme.Primary0
import com.example.saku.app.ui.theme.Surface
import com.example.saku.app.ui.theme.TextMuted
import com.example.saku.app.ui.theme.TextPrimary
import com.example.saku.app.ui.theme.TextSecondary
import com.example.saku.app.ui.theme.Warning0
import java.text.NumberFormat

@Composable
fun HistoryScreen(
    myLoans: List<LoanApplicationItemDto>,
    selectedFilter: String,
    onFilterSelect: (String) -> Unit,
    currencyFormatter: NumberFormat,
    isAjukanEnabled: Boolean = true,
    onAjukanClick: () -> Unit,
    onDetailClick: (LoanApplicationItemDto) -> Unit = {},
    onRefresh: () -> Unit,
    onBackClick: () -> Unit = {},
) {
    val listState = rememberLazyListState()

    val filters = listOf(
        "SEMUA" to "Semua",
        "DALAM_PROSES" to "Dalam Proses",
        "DISETUJUI" to "Disetujui",
        "DICAIRKAN" to "Dicairkan",
        "DITOLAK" to "Ditolak",
    )

    val filteredList = myLoans.filter { loan ->
        val status = (loan.statusPengajuan ?: "PENDING").uppercase()
        when (selectedFilter) {
            "DALAM_PROSES" -> status in listOf(
                "PENDING", "SUBMITTED", "MENUNGGU_REVIEW", "VERIFIKASI_MARKETING",
                "SELESAI_DIREVIEW", "MENUNGGU_PERSETUJUAN", "DISETUJUI_MARKETING",
                "MENUNGGU_DOKUMEN", "PERLU_REVISI", "REVISI"
            )
            "DISETUJUI" -> status in listOf("APPROVED", "DISETUJUI", "PENGAJUAN_DISETUJUI", "MENUNGGU_PENCAIRAN", "DISETUJUI_BM")
            "DICAIRKAN" -> status in listOf("DISBURSED", "DICAIRKAN", "LUNAS", "PAID")
            "DITOLAK" -> status in listOf("REJECTED", "DITOLAK", "PENGAJUAN_DITOLAK", "DITOLAK_MARKETING", "DITOLAK_BM", "REJECT", "BATAL", "CANCELLED")
            else -> true
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
    ) {
        // Top Bar: Header putih dengan tombol back, judul, dan tombol refresh
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = Surface,
            border = BorderStroke(1.dp, Border),
            shadowElevation = 1.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
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

                Text(
                    text = "Aktivitas & Riwayat",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )

                Spacer(modifier = Modifier.weight(1f))

                IconButton(
                    onClick = onRefresh,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Lucide.RefreshCw,
                        contentDescription = "Segarkan",
                        tint = TextPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }

        // Body: Filter bar dan daftar kartu riwayat pinjaman
        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(top = 16.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // 1. Filter Chips Bar
            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(7.dp),
                    contentPadding = PaddingValues(horizontal = 18.dp, vertical = 2.dp)
                ) {
                    items(filters) { (key, label) ->
                        val isSelected = selectedFilter == key
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isSelected) Primary else Surface)
                                .border(1.dp, if (isSelected) Primary else Border, RoundedCornerShape(10.dp))
                                .clickable { onFilterSelect(key) }
                                .padding(horizontal = 13.dp, vertical = 7.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = label,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) Color.White else TextPrimary
                            )
                        }
                    }
                }
            }

            // 2. Daftar Riwayat Pinjaman
            if (filteredList.isEmpty()) {
                item {
                    Box(modifier = Modifier.padding(horizontal = 18.dp)) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(18.dp),
                            colors = CardDefaults.cardColors(containerColor = Surface),
                            border = BorderStroke(1.dp, Border)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 28.dp, horizontal = 20.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(48.dp)
                                        .clip(CircleShape)
                                        .background(Primary0),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Lucide.FileText,
                                        contentDescription = null,
                                        tint = Primary,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.height(10.dp))
                                Text(
                                    text = "Belum Ada Pengajuan",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Pengajuan pinjaman Anda akan otomatis tercatat dan dapat dipantau langsung di sini.",
                                    fontSize = 11.5.sp,
                                    color = TextSecondary,
                                    textAlign = TextAlign.Center,
                                    lineHeight = 16.sp
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Button(
                                    text = if (isAjukanEnabled) "Ajukan Pinjaman Baru" else "Limit Tidak Mencukupi (Rp 0)",
                                    onClick = onAjukanClick,
                                    enabled = isAjukanEnabled,
                                    variant = ButtonVariant.Primary,
                                    size = ButtonSize.MD,
                                    fullWidth = true
                                )
                            }
                        }
                    }
                }
            } else {
                items(filteredList, key = { it.id ?: it.hashCode().toString() }) { loan ->
                    Box(modifier = Modifier.padding(horizontal = 18.dp)) {
                        HistoryLoanCard(
                            loan = loan,
                            currencyFormatter = currencyFormatter,
                            onClick = { onDetailClick(loan) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun HistoryLoanCard(
    loan: LoanApplicationItemDto,
    currencyFormatter: NumberFormat,
    onClick: () -> Unit = {}
) {
    val status = loan.statusPengajuan ?: "PENDING"
    val (badgeVariant, badgeText) = when (status.uppercase()) {
        "DICAIRKAN", "DISBURSED" -> Pair(BadgeVariant.Success, "Dicairkan")
        "APPROVED", "DISETUJUI", "PENGAJUAN_DISETUJUI" -> Pair(BadgeVariant.Success, "Disetujui")
        "MENUNGGU_PENCAIRAN" -> Pair(BadgeVariant.Success, "Menunggu Pencairan")
        "SELESAI_DIREVIEW", "MENUNGGU_PERSETUJUAN", "DISETUJUI_MARKETING" -> Pair(BadgeVariant.Info, "Menunggu Persetujuan")
        "VERIFIKASI_MARKETING", "MENUNGGU_REVIEW" -> Pair(BadgeVariant.Info, "Sedang Ditinjau")
        "REJECTED", "DITOLAK", "PENGAJUAN_DITOLAK", "DITOLAK_MARKETING", "DITOLAK_BM", "REJECT", "BATAL", "CANCELLED" -> Pair(BadgeVariant.Error, "Ditolak")
        "PAID", "LUNAS" -> Pair(BadgeVariant.Neutral, "Lunas")
        "PERLU_REVISI", "REVISI" -> Pair(BadgeVariant.Warning, "Revisi Dokumen")
        else -> Pair(BadgeVariant.Info, "Dalam Proses")
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Surface),
        border = BorderStroke(1.dp, Border)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Top Row: Nomor Pengajuan & Status Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = loan.nomorPengajuan ?: "No. Pengajuan",
                        fontSize = 13.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(1.dp))
                    // Text(
                    //     text = loan.createdDate?.take(10) ?: "Baru saja",
                    //     fontSize = 11.sp,
                    //     color = TextMuted
                    // )
                    Text(
                        text = formatIndoDate(loan.createdDate),
                        fontSize = 11.sp,
                        color = TextMuted
                    )
                }

                Badge(
                    text = badgeText,
                    variant = badgeVariant,
                    size = BadgeSize.SM
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Grid Details Box
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(Primary0)
                    .padding(10.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(text = "Nominal Pinjaman", fontSize = 10.5.sp, color = TextMuted)
                    Text(
                        text = "Rp ${currencyFormatter.format(loan.jumlahPinjaman ?: 0.0)}",
                        fontSize = 13.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(text = "Tenor Pinjaman", fontSize = 10.5.sp, color = TextMuted)
                    Text(
                        text = "${loan.tenorBulan ?: 0} Bulan",
                        fontSize = 13.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }
            }

            if ((loan.estimasiAngsuranBulanan ?: 0.0) > 0.0) {
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "Angsuran Bulanan", fontSize = 11.sp, color = TextSecondary)
                    Text(
                        text = "Rp ${currencyFormatter.format(loan.estimasiAngsuranBulanan ?: 0.0)} / bln",
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary
                    )
                }
            }

            val isRevisionStatus = loan.statusPengajuan?.uppercase() in listOf("PERLU_REVISI", "REVISI", "REVISI_DOKUMEN", "BUTUH_REVISI")
            if (isRevisionStatus && !loan.catatanReview.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Catatan: ${loan.catatanReview}",
                    fontSize = 10.5.sp,
                    color = TextMuted,
                    lineHeight = 14.sp
                )
            }
        }
    }
}

private fun formatIndoDate(dateStr: String?): String {
    if (dateStr.isNullOrBlank()) return "Baru saja"
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
