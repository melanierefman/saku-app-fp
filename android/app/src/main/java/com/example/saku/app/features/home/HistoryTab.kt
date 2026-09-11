package com.example.saku.app.features.home

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.composables.icons.lucide.Calendar
import com.composables.icons.lucide.DollarSign
import com.composables.icons.lucide.FileText
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Receipt
import com.example.saku.app.core.network.dto.LoanApplicationItemDto
import com.example.saku.app.core.ui.components.Badge
import com.example.saku.app.core.ui.components.BadgeSize
import com.example.saku.app.core.ui.components.BadgeVariant
import com.example.saku.app.core.ui.components.Button
import com.example.saku.app.core.ui.components.ButtonSize
import com.example.saku.app.core.ui.components.ButtonVariant
import com.example.saku.app.ui.theme.Background
import com.example.saku.app.ui.theme.Border
import com.example.saku.app.ui.theme.Primary
import com.example.saku.app.ui.theme.Primary0
import com.example.saku.app.ui.theme.Surface
import com.example.saku.app.ui.theme.TextMuted
import com.example.saku.app.ui.theme.TextPrimary
import com.example.saku.app.ui.theme.TextSecondary
import java.text.NumberFormat

@Composable
fun HistoryTabContent(
    myLoans: List<LoanApplicationItemDto>,
    selectedFilter: String,
    onFilterSelect: (String) -> Unit,
    currencyFormatter: NumberFormat,
    onAjukanClick: () -> Unit,
    onRefresh: () -> Unit,
) {
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
            "DALAM_PROSES" -> status in listOf("PENDING", "SUBMITTED", "MENUNGGU_REVIEW", "VERIFIKASI_MARKETING", "MENUNGGU_PERSETUJUAN")
            "DISETUJUI" -> status in listOf("APPROVED", "DISETUJUI")
            "DICAIRKAN" -> status in listOf("DISBURSED", "DICAIRKAN")
            "DITOLAK" -> status in listOf("REJECTED", "DITOLAK")
            else -> true
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Background),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Header Tab Riwayat
        item {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Riwayat Pengajuan",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    letterSpacing = (-0.5).sp
                )
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = "Daftar seluruh berkas pengajuan pinjaman & statusnya",
                    fontSize = 13.sp,
                    color = TextSecondary
                )
            }
        }

        // 2. Filter Chips Horizontal
        item {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(vertical = 4.dp)
            ) {
                items(filters) { (key, label) ->
                    val isSelected = selectedFilter == key
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isSelected) Primary else Surface)
                            .border(1.dp, if (isSelected) Primary else Border, RoundedCornerShape(10.dp))
                            .clickable { onFilterSelect(key) }
                            .padding(horizontal = 14.dp, vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = label,
                            fontSize = 12.5.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) Color.White else TextPrimary
                        )
                    }
                }
            }
        }

        // 3. List Item Riwayat atau Empty State
        if (filteredList.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Surface),
                    border = BorderStroke(1.dp, Border)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .clip(CircleShape)
                                .background(Primary0),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Lucide.Receipt,
                                contentDescription = null,
                                tint = Primary,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(14.dp))
                        Text(
                            text = "Belum Ada Riwayat",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Pengajuan pinjaman Anda akan otomatis tercatat dan dapat dipantau langsung di sini.",
                            fontSize = 12.5.sp,
                            color = TextSecondary,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                            lineHeight = 17.sp
                        )
                        Spacer(modifier = Modifier.height(18.dp))
                        Button(
                            text = "Ajukan Pinjaman Baru ->",
                            onClick = onAjukanClick,
                            variant = ButtonVariant.Primary,
                            size = ButtonSize.SM
                        )
                    }
                }
            }
        } else {
            items(filteredList) { loan ->
                HistoryLoanCard(loan = loan, currencyFormatter = currencyFormatter)
            }
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun HistoryLoanCard(
    loan: LoanApplicationItemDto,
    currencyFormatter: NumberFormat,
) {
    val status = loan.statusPengajuan ?: "PENDING"
    val (badgeVariant, badgeText) = when (status.uppercase()) {
        "APPROVED", "DISETUJUI" -> Pair(BadgeVariant.Success, "Disetujui")
        "DISBURSED", "DICAIRKAN" -> Pair(BadgeVariant.Success, "Dicairkan")
        "REJECTED", "DITOLAK" -> Pair(BadgeVariant.Error, "Ditolak")
        "PAID", "LUNAS" -> Pair(BadgeVariant.Success, "Lunas")
        else -> Pair(BadgeVariant.Primary, "Dalam Review")
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Surface),
        border = BorderStroke(1.dp, Border)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            // Top Row: Nomor Pengajuan & Status Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Primary0),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Lucide.FileText,
                            contentDescription = null,
                            tint = Primary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = loan.nomorPengajuan ?: "No. Pengajuan",
                            fontSize = 13.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = loan.createdDate?.take(10) ?: "Baru saja",
                            fontSize = 11.sp,
                            color = TextMuted
                        )
                    }
                }

                Badge(
                    text = badgeText,
                    variant = badgeVariant,
                    size = BadgeSize.SM
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Grid Details Box
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Background)
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(text = "Nominal Pinjaman", fontSize = 11.sp, color = TextMuted)
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Rp ${currencyFormatter.format(loan.jumlahPinjaman ?: 0.0)}",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Primary
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(text = "Tenor Pinjaman", fontSize = 11.sp, color = TextMuted)
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "${loan.tenorBulan ?: 0} Bulan",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }
            }

            if ((loan.estimasiAngsuranBulanan ?: 0.0) > 0.0) {
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "Angsuran Bulanan", fontSize = 11.5.sp, color = TextSecondary)
                    Text(
                        text = "Rp ${currencyFormatter.format(loan.estimasiAngsuranBulanan ?: 0.0)} / bln",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary
                    )
                }
            }

            if (!loan.catatanReview.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Catatan: ${loan.catatanReview}",
                    fontSize = 11.sp,
                    color = TextMuted,
                    lineHeight = 15.sp
                )
            }
        }
    }
}
