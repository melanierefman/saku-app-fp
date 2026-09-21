package com.example.saku.app.features.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.composables.icons.lucide.Calculator
import com.composables.icons.lucide.ChevronRight
import com.composables.icons.lucide.FileText
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Percent
import com.composables.icons.lucide.ShieldCheck
import com.composables.icons.lucide.Sparkles
import com.composables.icons.lucide.Wallet
import com.composables.icons.lucide.Zap
import com.example.saku.app.R
import com.example.saku.app.core.network.dto.CustomerProfileDto
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
import com.example.saku.app.ui.theme.Primary60
import com.example.saku.app.ui.theme.Primary70
import com.example.saku.app.ui.theme.Surface
import com.example.saku.app.ui.theme.TextMuted
import com.example.saku.app.ui.theme.TextPrimary
import com.example.saku.app.ui.theme.TextSecondary
import com.example.saku.app.ui.theme.Warning0
import java.text.NumberFormat

@Composable
fun LoansTabContent(
    customerProfile: CustomerProfileDto?,
    myLoans: List<LoanApplicationItemDto>,
    availablePlafond: Double,
    totalPlafond: Double,
    usedPlafond: Double,
    currencyFormatter: NumberFormat,
    onAjukanClick: () -> Unit,
    onSimulasiClick: () -> Unit,
    onPayClick: (LoanApplicationItemDto) -> Unit,
    onDetailClick: (LoanApplicationItemDto) -> Unit,
) {
    val listState = rememberLazyListState()
    val usedRatio = if (totalPlafond > 0) (usedPlafond / totalPlafond).toFloat().coerceIn(0f, 1f) else 0f

    val activeLoans = remember(myLoans) {
        myLoans.filter { loan ->
            val s = (loan.statusPengajuan ?: "").uppercase()
            s !in listOf("DITOLAK", "PENGAJUAN_DITOLAK", "REJECTED", "DITOLAK_MARKETING", "DITOLAK_BM", "REJECT", "BATAL", "CANCELLED", "PAID", "LUNAS")
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.linearGradient(
                    listOf(
                        Primary,
                        Primary60,
                        Primary70
                    )
                )
            )
    ) {
        // 1. ORANGE HEADER WITH bg_card_saku (Clean top bar)
        Box(
            modifier = Modifier.fillMaxWidth()
        ) {
            // Background Card Wave Pattern
            Image(
                painter = painterResource(id = R.drawable.bg_card_saku),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                alignment = Alignment.CenterEnd,
                modifier = Modifier
                    .matchParentSize()
                    .graphicsLayer(
                        scaleX = 1.45f,
                        scaleY = 1.45f,
                        transformOrigin = TransformOrigin(0.85f, 0.5f)
                    )
            )

            // Header Text & Quick Action Icon
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 18.dp, end = 18.dp, top = 12.dp, bottom = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Pinjaman SAKU",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        letterSpacing = 0.3.sp
                    )
                }
            }
        }

        // 2. WHITE SHEET BODY WITH ROUNDED TOP CORNERS (Lengkung kebalik / Top-rounded container)
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                .background(Background)
        ) {
            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(top = 16.dp, bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // 1. ALL-IN-ONE CARD: STATUS PLAFOND & AJUKAN PINJAMAN
                item {
                    Box(modifier = Modifier.padding(horizontal = 18.dp)) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(18.dp),
                            colors = CardDefaults.cardColors(containerColor = Surface),
                            border = BorderStroke(1.dp, Border)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                // Header: Title & Status Badge
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
//                                        Icon(
//                                            imageVector = Lucide.Wallet,
//                                            contentDescription = null,
//                                            tint = TextPrimary,
//                                            modifier = Modifier.size(18.dp)
//                                        )
                                        Text(
                                            text = "Sisa Plafond Pinjaman",
                                            fontSize = 13.5.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = TextPrimary
                                        )
                                    }

                                    val isAjukanEnabled = availablePlafond >= 500_000.0

                                    Badge(
                                        text = if (isAjukanEnabled) "Limit Aktif" else "Limit Habis",
                                        variant = if (isAjukanEnabled) BadgeVariant.Success else BadgeVariant.Neutral,
                                        size = BadgeSize.SM
                                    )
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                // Nominal Plafond Tersedia
                                Text(
                                    text = "Rp ${currencyFormatter.format(availablePlafond)}",
                                    fontSize = 25.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = TextPrimary,
                                    letterSpacing = (-0.5).sp
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                // Progress Bar Limit Terpakai
                                LinearProgressIndicator(
                                    progress = { usedRatio },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(6.dp)
                                        .clip(RoundedCornerShape(3.dp)),
                                    color = Primary,
                                    trackColor = Primary0
                                )

                                Spacer(modifier = Modifier.height(10.dp))

                                // Total Limit vs Limit Terpakai
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Text(text = "Total Limit:", fontSize = 11.5.sp, color = TextMuted)
                                        Text(
                                            text = "Rp ${currencyFormatter.format(totalPlafond)}",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = TextPrimary
                                        )
                                    }
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Text(text = "Terpakai:", fontSize = 11.5.sp, color = TextMuted)
                                        Text(
                                            text = "Rp ${currencyFormatter.format(usedPlafond)}",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = TextSecondary
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(14.dp))
                                HorizontalDivider(color = Border.copy(alpha = 0.6f), thickness = 0.8.dp)
                                Spacer(modifier = Modifier.height(14.dp))

                                // Button Ajukan Pinjaman
                                val isLimitAvailable = availablePlafond >= 500_000.0
                                Button(
                                    text = if (isLimitAvailable) "Ajukan Pinjaman Sekarang" else "Limit Tidak Mencukupi (Rp 0)",
                                    onClick = onAjukanClick,
                                    enabled = isLimitAvailable,
                                    variant = ButtonVariant.Primary,
                                    size = ButtonSize.MD,
                                    fullWidth = true
                                )
                            }
                        }
                    }
                }

                // 2. SECTION: PINJAMAN AKTIF & DALAM PROSES
                item {
                    Box(modifier = Modifier.padding(horizontal = 18.dp)) {
                        Text(
                            text = "Pinjaman Aktif (${activeLoans.size})",
                            fontSize = 14.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    }
                }

                if (activeLoans.isEmpty()) {
                    item {
                        Box(modifier = Modifier.padding(horizontal = 18.dp)) {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = Surface),
                                border = BorderStroke(1.dp, Border)
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(20.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(42.dp)
                                            .clip(CircleShape)
                                            .background(Primary0),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Lucide.FileText,
                                            contentDescription = null,
                                            tint = Primary,
                                            modifier = Modifier.size(22.dp)
                                        )
                                    }
                                    Text(
                                        text = "Tidak Ada Pinjaman Aktif",
                                        fontSize = 13.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary
                                    )
                                    Text(
                                        text = "Anda belum memiliki tagihan atau pengajuan pinjaman yang sedang berjalan.",
                                        fontSize = 11.5.sp,
                                        color = TextSecondary,
                                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                    )
                                }
                            }
                        }
                    }
                } else {
                    items(activeLoans, key = { it.id ?: it.hashCode().toString() }) { loan ->
                        Box(modifier = Modifier.padding(horizontal = 18.dp)) {
                            LoanItemCard(
                                loan = loan,
                                currencyFormatter = currencyFormatter,
                                onPayClick = { onPayClick(loan) },
                                onDetailClick = { onDetailClick(loan) }
                            )
                        }
                    }
                }

                // 4. FITUR & KEUNGGULAN SAKU (Dikomentari sesuai request)
                // item {
                //     Box(modifier = Modifier.padding(horizontal = 18.dp)) {
                //         Card(
                //             modifier = Modifier.fillMaxWidth(),
                //             shape = RoundedCornerShape(18.dp),
                //             colors = CardDefaults.cardColors(containerColor = Surface),
                //             border = BorderStroke(1.dp, Border)
                //         ) {
                //             Column(modifier = Modifier.padding(15.dp)) {
                //                 Text(
                //                     text = "Keunggulan Pinjaman SAKU",
                //                     fontSize = 13.5.sp,
                //                     fontWeight = FontWeight.Bold,
                //                     color = TextPrimary
                //                 )
                //
                //                 Spacer(modifier = Modifier.height(12.dp))
                //
                //                 LoanFeatureRow(
                //                     icon = Lucide.Percent,
                //                     title = "Suku Bunga Kompetitif Mulai 1.5% / Bulan",
                //                     desc = "Perhitungan transparan tanpa biaya siluman"
                //                 )
                //
                //                 Spacer(modifier = Modifier.height(10.dp))
                //
                //                 LoanFeatureRow(
                //                     icon = Lucide.Zap,
                //                     title = "Pencairan Cepat < 24 Jam",
                //                     desc = "Dana langsung ditransfer ke rekening bank Anda"
                //                 )
                //
                //                 Spacer(modifier = Modifier.height(10.dp))
                //
                //                 LoanFeatureRow(
                //                     icon = Lucide.ShieldCheck,
                //                     title = "Aman & Berizin Resmi",
                //                     desc = "Didukung teknologi enkripsi bank & berizin resmi"
                //                 )
                //             }
                //         }
                //     }
                // }
            }
        }
    }
}

@Composable
private fun LoanItemCard(
    loan: LoanApplicationItemDto,
    currencyFormatter: NumberFormat,
    onPayClick: () -> Unit,
    onDetailClick: () -> Unit,
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
            .clickable { onDetailClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Surface),
        border = BorderStroke(1.dp, Border)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = loan.nomorPengajuan ?: "No. Pengajuan",
                        fontSize = 12.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    // Text(
                    //     text = loan.createdDate?.take(10) ?: "Baru saja",
                    //     fontSize = 10.5.sp,
                    //     color = TextMuted
                    // )
                    Text(
                        text = formatIndoDate(loan.createdDate),
                        fontSize = 10.5.sp,
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

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(Primary0)
                    .padding(10.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(text = "Jumlah Pinjaman", fontSize = 10.5.sp, color = TextSecondary)
                    Text(
                        text = "Rp ${currencyFormatter.format(loan.jumlahPinjaman ?: 0.0)}",
                        fontSize = 13.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(text = "Tenor", fontSize = 10.5.sp, color = TextSecondary)
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
                    Text(text = "Estimasi Angsuran", fontSize = 11.sp, color = TextMuted)
                    Text(
                        text = "Rp ${currencyFormatter.format(loan.estimasiAngsuranBulanan ?: 0.0)} / bln",
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider(color = Border)
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Lihat Detail Pengajuan",
                    fontSize = 11.5.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextSecondary
                )
                Icon(
                    imageVector = Lucide.ChevronRight,
                    contentDescription = null,
                    tint = TextSecondary,
                    modifier = Modifier.size(15.dp)
                )
            }
        }
    }
}

@Composable
private fun LoanFeatureRow(
    icon: ImageVector,
    title: String,
    desc: String,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(30.dp)
                .clip(CircleShape)
                .background(Primary0),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Primary,
                modifier = Modifier.size(15.dp)
            )
        }
        Spacer(modifier = Modifier.width(10.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Text(
                text = desc,
                fontSize = 11.sp,
                color = TextSecondary
            )
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
