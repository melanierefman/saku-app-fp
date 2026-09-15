package com.example.saku.app.features.home

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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.composables.icons.lucide.Calendar
import com.composables.icons.lucide.CircleAlert
import com.composables.icons.lucide.CircleCheck
import com.composables.icons.lucide.Clock
import com.composables.icons.lucide.CreditCard
import com.composables.icons.lucide.FileText
import com.composables.icons.lucide.Info
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Receipt
import com.composables.icons.lucide.RefreshCw
import com.example.saku.app.R
import com.example.saku.app.core.network.dto.AngsuranItemDto
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
import com.example.saku.app.ui.theme.Error
import com.example.saku.app.ui.theme.OverusedGrotesk
import com.example.saku.app.ui.theme.Primary
import com.example.saku.app.ui.theme.Primary0
import com.example.saku.app.ui.theme.Primary60
import com.example.saku.app.ui.theme.Primary70
import com.example.saku.app.ui.theme.Success
import com.example.saku.app.ui.theme.Surface
import com.example.saku.app.ui.theme.TextMuted
import com.example.saku.app.ui.theme.TextPrimary
import com.example.saku.app.ui.theme.TextSecondary
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class BillInstallmentItem(
    val loan: LoanApplicationItemDto,
    val angsuran: AngsuranItemDto,
    val cicilanKe: Int,
    val totalTenor: Int,
    val nominal: Double,
    val rawJatuhTempo: String,
    val formattedJatuhTempo: String,
    val isPaid: Boolean,
    val isOverdue: Boolean,
    val isDueSoon: Boolean,
    val daysRemaining: Long
)

@Composable
fun BillsTabContent(
    myLoans: List<LoanApplicationItemDto>,
    customerProfile: CustomerProfileDto? = null,
    currencyFormatter: NumberFormat,
    isAjukanEnabled: Boolean = true,
    onPayClick: (LoanApplicationItemDto, AngsuranItemDto?) -> Unit,
    onDetailClick: (LoanApplicationItemDto) -> Unit,
    onAjukanClick: () -> Unit,
    onRefresh: () -> Unit,
) {
    val listState = rememberLazyListState()
    var selectedFilter by remember { mutableStateOf("UNPAID") } // "UNPAID" or "PAID"

    // Flatten all active loans into individual installment bills
    val allBills = remember(myLoans) {
        val list = mutableListOf<BillInstallmentItem>()
        val today = Date()
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

        val disbursedLoans = myLoans.filter { loan ->
            val status = (loan.statusPengajuan ?: "").uppercase()
            status in listOf("DICAIRKAN", "DISBURSED", "LUNAS", "PAID", "APPROVED", "DISETUJUI")
        }

        for (loan in disbursedLoans) {
            val angsuranList = loan.listAngsuran
            val totalTenor = loan.tenorBulan ?: 12
            val monthlyEst = loan.estimasiAngsuranBulanan ?: (loan.jumlahPinjaman?.div(totalTenor) ?: 0.0)

            if (!angsuranList.isNullOrEmpty()) {
                for (angsuran in angsuranList) {
                    val rawDate = angsuran.jatuhTempo ?: ""
                    val (formattedDate, daysLeft, isOverdue, isDueSoon) = parseBillDate(rawDate, today, sdf)
                    val status = (angsuran.statusBayar ?: "BELUM_BAYAR").uppercase()
                    val isPaid = status in listOf("LUNAS", "PAID", "SUDAH_BAYAR")

                    list.add(
                        BillInstallmentItem(
                            loan = loan,
                            angsuran = angsuran,
                            cicilanKe = angsuran.cicilanKe ?: 1,
                            totalTenor = totalTenor,
                            nominal = angsuran.jumlahAngsuran ?: monthlyEst,
                            rawJatuhTempo = rawDate,
                            formattedJatuhTempo = formattedDate,
                            isPaid = isPaid,
                            isOverdue = isOverdue && !isPaid,
                            isDueSoon = isDueSoon && !isPaid,
                            daysRemaining = daysLeft
                        )
                    )
                }
            } else {
                // Synthesize installments if backend list is empty but loan is active
                val rawDate = loan.createdDate?.substringBefore("T") ?: "2026-09-30"
                val (formattedDate, daysLeft, isOverdue, isDueSoon) = parseBillDate(rawDate, today, sdf)
                val isLoanPaid = (loan.statusPengajuan ?: "").uppercase() in listOf("LUNAS", "PAID")

                list.add(
                    BillInstallmentItem(
                        loan = loan,
                        angsuran = AngsuranItemDto(
                            id = loan.id,
                            cicilanKe = 1,
                            jumlahAngsuran = monthlyEst,
                            jatuhTempo = rawDate,
                            statusBayar = if (isLoanPaid) "LUNAS" else "BELUM_BAYAR"
                        ),
                        cicilanKe = 1,
                        totalTenor = totalTenor,
                        nominal = monthlyEst,
                        rawJatuhTempo = rawDate,
                        formattedJatuhTempo = formattedDate,
                        isPaid = isLoanPaid,
                        isOverdue = isOverdue && !isLoanPaid,
                        isDueSoon = isDueSoon && !isLoanPaid,
                        daysRemaining = daysLeft
                    )
                )
            }
        }
        list
    }

    // Split into Unpaid (sorted by nearest due date ASC) and Paid (sorted by due date DESC)
    val unpaidBills = remember(allBills) {
        allBills.filter { !it.isPaid }.sortedWith(
            compareBy<BillInstallmentItem> { it.rawJatuhTempo }.thenBy { it.cicilanKe }
        )
    }

    val paidBills = remember(allBills) {
        allBills.filter { it.isPaid }.sortedWith(
            compareByDescending<BillInstallmentItem> { it.rawJatuhTempo }.thenByDescending { it.cicilanKe }
        )
    }

    val tabs = listOf(
        "UNPAID" to "Perlu Dibayar",
        "PAID" to "Sudah Lunas"
    )

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
        // 1. TOP HEADER WITH SAKU BRANDING & REFRESH ACTION (Identical with HistoryTab)
        Box(
            modifier = Modifier.fillMaxWidth()
        ) {
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

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 18.dp, end = 18.dp, top = 12.dp, bottom = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Daftar Tagihan",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        letterSpacing = 0.3.sp
                    )
                }

                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.White)
                        .clickable { onRefresh() }
                        .padding(horizontal = 10.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Lucide.RefreshCw,
                        contentDescription = "Muat Ulang",
                        tint = Primary,
                        modifier = Modifier.size(12.dp)
                    )
                    Text(
                        text = "Segarkan",
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }
            }
        }

        // 2. WHITE SHEET BODY WITH ROUNDED TOP CORNERS (Clean Tab First & List Layout)
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
                // 1. TABS SEGMENTED CONTROL PENUH (2 Tab Segmented Control)
                item {
                    Box(modifier = Modifier.padding(horizontal = 18.dp)) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFFF1F5F9))
                                .padding(4.dp)
                        ) {
                            val unpaidCount = unpaidBills.size
                            val paidCount = paidBills.size

                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(9.dp))
                                    .background(if (selectedFilter == "UNPAID") Surface else Color.Transparent)
                                    .clickable { selectedFilter = "UNPAID" }
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "Perlu Dibayar ($unpaidCount)",
                                    fontSize = 12.sp,
                                    fontWeight = if (selectedFilter == "UNPAID") FontWeight.Bold else FontWeight.Medium,
                                    color = if (selectedFilter == "UNPAID") Primary else TextSecondary
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(9.dp))
                                    .background(if (selectedFilter == "PAID") Surface else Color.Transparent)
                                    .clickable { selectedFilter = "PAID" }
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "Sudah Lunas ($paidCount)",
                                    fontSize = 12.sp,
                                    fontWeight = if (selectedFilter == "PAID") FontWeight.Bold else FontWeight.Medium,
                                    color = if (selectedFilter == "PAID") Primary else TextSecondary
                                )
                            }
                        }
                    }
                }

                // 2. LIST TAGIHAN DI BAWAHNYA
                if (selectedFilter == "UNPAID") {
                    if (unpaidBills.isEmpty()) {
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
                                                imageVector = Lucide.CircleCheck,
                                                contentDescription = null,
                                                tint = Primary,
                                                modifier = Modifier.size(24.dp)
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(10.dp))
                                        Text(
                                            text = "Tidak Ada Tagihan Aktif",
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = TextPrimary
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = "Semua angsuran pinjaman Anda telah lunas atau belum ada tagihan aktif yang perlu dibayar saat ini.",
                                            fontSize = 11.5.sp,
                                            color = TextSecondary,
                                            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
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
                        itemsIndexed(unpaidBills, key = { _, item -> "${item.loan.id ?: ""}_${item.cicilanKe}" }) { index, billItem ->
                            Box(modifier = Modifier.padding(horizontal = 18.dp)) {
                                CleanBillItemCard(
                                    item = billItem,
                                    isTopUrgent = index == 0, // List paling atas menggunakan button Primary, sisanya White / Secondary
                                    currencyFormatter = currencyFormatter,
                                    onPayClick = { onPayClick(billItem.loan, billItem.angsuran) },
                                    onDetailClick = { onDetailClick(billItem.loan) }
                                )
                            }
                        }
                    }
                } else {
                    // TAB SUDAH LUNAS
                    if (paidBills.isEmpty()) {
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
                                            text = "Belum Ada Riwayat Lunas",
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = TextPrimary
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = "Riwayat pembayaran cicilan yang telah terverifikasi lunas akan muncul di sini.",
                                            fontSize = 11.5.sp,
                                            color = TextSecondary,
                                            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                            lineHeight = 16.sp
                                        )
                                    }
                                }
                            }
                        }
                    } else {
                        itemsIndexed(paidBills, key = { _, item -> "${item.loan.id ?: ""}_${item.cicilanKe}" }) { _, billItem ->
                            Box(modifier = Modifier.padding(horizontal = 18.dp)) {
                                CleanBillItemCard(
                                    item = billItem,
                                    isTopUrgent = false,
                                    currencyFormatter = currencyFormatter,
                                    onPayClick = { onPayClick(billItem.loan, billItem.angsuran) },
                                    onDetailClick = { onDetailClick(billItem.loan) }
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
private fun CleanBillItemCard(
    item: BillInstallmentItem,
    isTopUrgent: Boolean,
    currencyFormatter: NumberFormat,
    onPayClick: () -> Unit,
    onDetailClick: () -> Unit
) {
    val badgeVariant = when {
        item.isPaid -> BadgeVariant.Success
        item.isOverdue -> BadgeVariant.Error
        item.daysRemaining == 0L -> BadgeVariant.Error
        item.daysRemaining in 1..7 -> BadgeVariant.Warning
        else -> BadgeVariant.Neutral
    }

    val badgeText = when {
        item.isPaid -> "Lunas"
        item.isOverdue -> "Lewat ${Math.abs(item.daysRemaining)} Hari"
        item.daysRemaining == 0L -> "Jatuh Tempo Hari Ini"
        item.daysRemaining == 1L -> "1 Hari Lagi"
        item.daysRemaining in 2..7 -> "${item.daysRemaining} Hari Lagi"
        else -> "Belum Dibayar"
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Surface),
        border = BorderStroke(1.dp, if (isTopUrgent) Primary.copy(alpha = 0.5f) else Border)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Top Row: Loan Title / Cicilan & Status Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Icon tagihan dikomen agar lebih clean
                    /*
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(RoundedCornerShape(7.dp))
                            .background(if (isTopUrgent) Primary else Primary0),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (item.isPaid) Lucide.CircleCheck else Lucide.Receipt,
                            contentDescription = null,
                            tint = if (isTopUrgent) Color.White else Primary,
                            modifier = Modifier.size(15.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    */
                    Column {
                        Text(
                            text = "Cicilan Ke-${item.cicilanKe} dari ${item.totalTenor} Bln",
                            fontSize = 13.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.height(1.dp))
                        Text(
                            text = item.loan.nomorPengajuan ?: "No. Pengajuan",
                            fontSize = 11.sp,
                            color = TextMuted
                        )
                    }
                }

                Badge(text = badgeText, variant = badgeVariant, size = BadgeSize.SM)
            }

            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(color = Border, thickness = 0.7.dp)
            Spacer(modifier = Modifier.height(10.dp))

            // Amount & Due Date
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Nominal Tagihan",
                        fontSize = 11.sp,
                        color = TextSecondary
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Rp ${currencyFormatter.format(Math.round(item.nominal))}",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = if (item.isPaid) TextPrimary else (if (isTopUrgent) Primary else TextPrimary),
                        style = TextStyle(
                            fontFamily = OverusedGrotesk,
                            platformStyle = PlatformTextStyle(includeFontPadding = false),
                        )
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = if (item.isPaid) "Status Bayar" else "Jatuh Tempo",
                        fontSize = 11.sp,
                        color = TextSecondary
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = if (item.isOverdue) Lucide.Clock else Lucide.Calendar,
                            contentDescription = null,
                            tint = if (item.isOverdue) Error else TextSecondary,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (item.isPaid) "Terverifikasi" else item.formattedJatuhTempo,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (item.isOverdue) Error else TextPrimary
                        )
                    }
                }
            }

            // Urgency & Due Date Helper Note (Hanya muncul jika lewat jatuh tempo atau < 15 hari)
            if (!item.isPaid && (item.isOverdue || item.daysRemaining < 15L)) {
                Spacer(modifier = Modifier.height(10.dp))
                val noteBg = when {
                    item.isOverdue -> Color(0xFFFEF2F2)
                    item.daysRemaining <= 1L -> Color(0xFFFFF7ED)
                    item.daysRemaining <= 3L -> Color(0xFFFFFBEB)
                    else -> Primary0.copy(alpha = 0.5f)
                }
                val noteBorder = when {
                    item.isOverdue -> Color(0xFFFECACA)
                    item.daysRemaining <= 1L -> Color(0xFFFED7AA)
                    item.daysRemaining <= 3L -> Color(0xFFFDE68A)
                    else -> Primary.copy(alpha = 0.15f)
                }
                val noteTextColor = when {
                    item.isOverdue -> Color(0xFF991B1B)
                    item.daysRemaining <= 1L -> Color(0xFF9A3412)
                    item.daysRemaining <= 3L -> Color(0xFF92400E)
                    else -> Primary
                }
                val noteIconTint = when {
                    item.isOverdue -> Error
                    item.daysRemaining <= 1L -> Color(0xFFEA580C)
                    item.daysRemaining <= 3L -> Color(0xFFD97706)
                    else -> Primary
                }
                val noteMessage = when {
                    item.isOverdue -> "Tagihan telah lewat jatuh tempo ${Math.abs(item.daysRemaining)} hari. Harap segera bayar agar terhindar dari denda."
                    item.daysRemaining == 0L -> "Jatuh tempo hari ini! Segera lakukan pembayaran sebelum pukul 23:59 WIB."
                    item.daysRemaining == 1L -> "Jatuh tempo besok. Pastikan saldo Anda mencukupi untuk pembayaran."
                    else -> "Jatuh tempo dalam ${item.daysRemaining} hari lagi (${item.formattedJatuhTempo})."
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(noteBg)
                        .border(0.8.dp, noteBorder, RoundedCornerShape(8.dp))
                        .padding(horizontal = 10.dp, vertical = 6.5.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (item.isOverdue) Lucide.CircleAlert else (if (item.daysRemaining <= 3L) Lucide.Clock else Lucide.Info),
                        contentDescription = null,
                        tint = noteIconTint,
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = noteMessage,
                        fontSize = 10.5.sp,
                        color = noteTextColor,
                        lineHeight = 14.5.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Action Buttons
            if (!item.isPaid) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Secondary Button: Rincian Pinjaman
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFFF8FAFC))
                            .border(1.dp, Border, RoundedCornerShape(10.dp))
                            .clickable(onClick = onDetailClick)
                            .padding(vertical = 8.5.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Rincian",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TextPrimary
                        )
                    }

                    // Payment Button:
                    // - Index 0 (Top urgent): Solid Primary Orange
                    // - Index > 0 (Remaining items): White / Outline variant
                    if (isTopUrgent) {
                        Box(
                            modifier = Modifier
                                .weight(1.2f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(Primary)
                                .clickable(onClick = onPayClick)
                                .padding(vertical = 8.5.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Lucide.CreditCard,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(5.dp))
                                Text(
                                    text = "Bayar Sekarang",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }
                    } else {
                        // White / Outline variant for remaining list items
                        Box(
                            modifier = Modifier
                                .weight(1.2f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color.White)
                                .border(1.dp, Primary, RoundedCornerShape(10.dp))
                                .clickable(onClick = onPayClick)
                                .padding(vertical = 8.5.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Lucide.CreditCard,
                                    contentDescription = null,
                                    tint = Primary,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(5.dp))
                                Text(
                                    text = "Bayar Tagihan",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Primary
                                )
                            }
                        }
                    }
                }
            } else {
                // Paid Item Action Button
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFFF8FAFC))
                        .border(1.dp, Border, RoundedCornerShape(10.dp))
                        .clickable(onClick = onDetailClick)
                        .padding(vertical = 8.5.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Lihat Rincian Pinjaman",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary
                    )
                }
            }
        }
    }
}

private data class BillDateResult(
    val formatted: String,
    val daysLeft: Long,
    val isOverdue: Boolean,
    val isDueSoon: Boolean
)

private fun parseBillDate(dateStr: String?, today: Date, sdf: SimpleDateFormat): BillDateResult {
    if (dateStr.isNullOrBlank()) {
        return BillDateResult("Tanggal tidak tersedia", 0L, isOverdue = false, isDueSoon = false)
    }

    return try {
        val clean = dateStr.substringBefore("T").trim()
        val parts = clean.split("-")
        val monthNames = listOf("Jan", "Feb", "Mar", "Apr", "Mei", "Jun", "Jul", "Ags", "Sep", "Okt", "Nov", "Des")

        val formattedText = if (parts.size == 3) {
            val year = parts[0]
            val monthNum = parts[1].toIntOrNull() ?: 1
            val day = parts[2]
            "$day ${monthNames.getOrElse(monthNum - 1) { "Bln" }} $year"
        } else {
            clean
        }

        val parsedDate = sdf.parse(clean)
        if (parsedDate != null) {
            val calToday = java.util.Calendar.getInstance().apply {
                time = today
                set(java.util.Calendar.HOUR_OF_DAY, 0)
                set(java.util.Calendar.MINUTE, 0)
                set(java.util.Calendar.SECOND, 0)
                set(java.util.Calendar.MILLISECOND, 0)
            }
            val calDue = java.util.Calendar.getInstance().apply {
                time = parsedDate
                set(java.util.Calendar.HOUR_OF_DAY, 0)
                set(java.util.Calendar.MINUTE, 0)
                set(java.util.Calendar.SECOND, 0)
                set(java.util.Calendar.MILLISECOND, 0)
            }
            val diffMs = calDue.timeInMillis - calToday.timeInMillis
            val diffDays = diffMs / (1000L * 60 * 60 * 24)
            val isOverdue = diffDays < 0
            val isDueSoon = diffDays in 0..7
            BillDateResult(formattedText, diffDays, isOverdue, isDueSoon)
        } else {
            BillDateResult(formattedText, 0L, isOverdue = false, isDueSoon = false)
        }
    } catch (e: Exception) {
        BillDateResult(dateStr, 0L, isOverdue = false, isDueSoon = false)
    }
}
