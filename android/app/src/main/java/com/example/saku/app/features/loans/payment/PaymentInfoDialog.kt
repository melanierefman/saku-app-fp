package com.example.saku.app.features.loans.payment

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.composables.icons.lucide.Check
import com.composables.icons.lucide.ChevronDown
import com.composables.icons.lucide.ChevronUp
import com.composables.icons.lucide.Copy
import com.composables.icons.lucide.CreditCard
import com.composables.icons.lucide.Info
import com.composables.icons.lucide.Landmark
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.QrCode
import com.composables.icons.lucide.ShieldCheck
import com.composables.icons.lucide.Smartphone
import com.composables.icons.lucide.X
import com.example.saku.app.core.network.dto.AngsuranItemDto
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
import com.example.saku.app.ui.theme.Neutral10
import com.example.saku.app.ui.theme.Neutral20
import com.example.saku.app.ui.theme.Primary
import com.example.saku.app.ui.theme.Primary0
import com.example.saku.app.ui.theme.Primary60
import com.example.saku.app.ui.theme.Surface
import com.example.saku.app.ui.theme.TextMuted
import com.example.saku.app.ui.theme.TextPrimary
import com.example.saku.app.ui.theme.TextSecondary
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentInfoBottomSheet(
    loan: LoanApplicationItemDto?,
    angsuran: AngsuranItemDto?,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val currencyFormatter = remember { NumberFormat.getNumberInstance(Locale.forLanguageTag("id-ID")) }

    val vaNumber = "80123" + (loan?.nomorPengajuan?.filter { it.isDigit() }?.takeLast(10) ?: "0812345678")
    val nominalTagihan = angsuran?.jumlahAngsuran ?: (loan?.estimasiAngsuranBulanan ?: 0.0)
    val cicilanKe = angsuran?.cicilanKe ?: 1
    val totalTenor = loan?.tenorBulan ?: 12

    var selectedTab by remember { mutableStateOf("VA") } // "VA" or "QRIS"
    var expandedGuide by remember { mutableStateOf("MBCA") } // "MBCA", "KLIKBCA", "ATM"

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Surface,
        dragHandle = null
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp)
        ) {
            // Header Bottom Sheet
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Pembayaran Angsuran",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = "Cicilan Ke-$cicilanKe dari $totalTenor Bulan",
                        fontSize = 12.sp,
                        color = TextMuted
                    )
                }

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFF1F5F9))
                ) {
                    Icon(
                        imageVector = Lucide.X,
                        contentDescription = "Tutup",
                        tint = TextSecondary,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            HorizontalDivider(color = Border)

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Total Tagihan Hero Box
                item {
                    Spacer(modifier = Modifier.height(6.dp))
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = Primary0),
                        border = BorderStroke(1.dp, Primary.copy(alpha = 0.2f))
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(18.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Total Tagihan Bulan Ini",
                                fontSize = 12.5.sp,
                                color = TextMuted
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Rp ${currencyFormatter.format(nominalTagihan)}",
                                fontSize = 26.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Primary,
                                letterSpacing = (-0.5).sp
                            )
                            angsuran?.jatuhTempo?.let { jt ->
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Jatuh Tempo: $jt",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Error
                                )
                            }
                        }
                    }
                }

                // Payment Method Selector Tabs (Virtual Account vs QRIS)
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFFF1F5F9))
                            .padding(4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (selectedTab == "VA") Surface else Color.Transparent)
                                .clickable { selectedTab = "VA" }
                                .padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Lucide.Landmark,
                                    contentDescription = null,
                                    tint = if (selectedTab == "VA") Primary else TextMuted,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "BCA Virtual Account",
                                    fontSize = 12.5.sp,
                                    fontWeight = if (selectedTab == "VA") FontWeight.Bold else FontWeight.Medium,
                                    color = if (selectedTab == "VA") TextPrimary else TextMuted
                                )
                            }
                        }

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (selectedTab == "QRIS") Surface else Color.Transparent)
                                .clickable { selectedTab = "QRIS" }
                                .padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Lucide.QrCode,
                                    contentDescription = null,
                                    tint = if (selectedTab == "QRIS") Primary else TextMuted,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "QRIS SAKU",
                                    fontSize = 12.5.sp,
                                    fontWeight = if (selectedTab == "QRIS") FontWeight.Bold else FontWeight.Medium,
                                    color = if (selectedTab == "QRIS") TextPrimary else TextMuted
                                )
                            }
                        }
                    }
                }

                // Tab Content: VA vs QRIS
                if (selectedTab == "VA") {
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
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = "Nomor BCA Virtual Account",
                                            fontSize = 12.sp,
                                            color = TextMuted
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = vaNumber,
                                            fontSize = 17.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = TextPrimary,
                                            letterSpacing = 0.5.sp
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(10.dp))

                                    Button(
                                        text = "Salin VA",
                                        onClick = {
                                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                            val clip = ClipData.newPlainText("BCA Virtual Account", vaNumber)
                                            clipboard.setPrimaryClip(clip)
                                            Toast.makeText(context, "Nomor Virtual Account disalin ke clipboard", Toast.LENGTH_SHORT).show()
                                        },
                                        variant = ButtonVariant.Primary,
                                        size = ButtonSize.SM,
                                        fullWidth = false
                                    )
                                }

                                Spacer(modifier = Modifier.height(10.dp))
                                Text(
                                    text = "Nama Akun: SAKU BCA FINANCE - ${loan?.namaCustomer ?: "NASABAH"}",
                                    fontSize = 12.sp,
                                    color = TextSecondary
                                )
                            }
                        }
                    }
                } else {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(18.dp),
                            colors = CardDefaults.cardColors(containerColor = Surface),
                            border = BorderStroke(1.dp, Border)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(20.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(160.dp)
                                        .clip(RoundedCornerShape(14.dp))
                                        .background(Color.White)
                                        .border(1.dp, Border, RoundedCornerShape(14.dp))
                                        .padding(16.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Lucide.QrCode,
                                        contentDescription = "QR Code",
                                        tint = Primary,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                }
                                Spacer(modifier = Modifier.height(10.dp))
                                Text(
                                    text = "Pindai kode QRIS dengan BCA Mobile / Sakuku / e-Wallet apa saja",
                                    fontSize = 12.sp,
                                    color = TextSecondary,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }

                // Step-by-Step Payment Instructions (Accordion)
                item {
                    Text(
                        text = "Petunjuk Cara Pembayaran",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }

                item {
                    PaymentAccordionItem(
                        title = "BCA Mobile (m-BCA)",
                        isExpanded = expandedGuide == "MBCA",
                        onToggle = { expandedGuide = if (expandedGuide == "MBCA") "" else "MBCA" },
                        steps = listOf(
                            "1. Buka aplikasi BCA Mobile dan pilih menu m-Transfer.",
                            "2. Pilih menu BCA Virtual Account.",
                            "3. Masukkan nomor Virtual Account: $vaNumber.",
                            "4. Pastikan nominal tagihan dan nama nasabah sesuai.",
                            "5. Masukkan PIN m-BCA Anda dan simpan bukti pembayaran."
                        )
                    )
                }

                item {
                    PaymentAccordionItem(
                        title = "KlikBCA Individual",
                        isExpanded = expandedGuide == "KLIKBCA",
                        onToggle = { expandedGuide = if (expandedGuide == "KLIKBCA") "" else "KLIKBCA" },
                        steps = listOf(
                            "1. Login ke akun KlikBCA Individual Anda.",
                            "2. Pilih menu Transfer Dana -> Transfer ke BCA Virtual Account.",
                            "3. Masukkan nomor Virtual Account: $vaNumber.",
                            "4. Masukkan respon KeyBCA Appli 1 dan klik Kirim."
                        )
                    )
                }

                item {
                    PaymentAccordionItem(
                        title = "ATM BCA",
                        isExpanded = expandedGuide == "ATM",
                        onToggle = { expandedGuide = if (expandedGuide == "ATM") "" else "ATM" },
                        steps = listOf(
                            "1. Masukkan Kartu ATM BCA & PIN Anda.",
                            "2. Pilih menu Transaksi Lainnya -> Transfer -> Ke Rekening BCA Virtual Account.",
                            "3. Masukkan nomor Virtual Account: $vaNumber.",
                            "4. Periksa rincian pembayaran pada layar ATM lalu tekan YA."
                        )
                    )
                }

                // Done Button
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        text = "Saya Sudah Melakukan Pembayaran",
                        onClick = {
                            Toast.makeText(context, "Status pembayaran akan diverifikasi otomatis dalam 1-5 menit.", Toast.LENGTH_LONG).show()
                            onDismiss()
                        },
                        modifier = Modifier.fillMaxWidth(),
                        size = ButtonSize.LG
                    )
                }
            }
        }
    }
}

@Composable
private fun PaymentAccordionItem(
    title: String,
    isExpanded: Boolean,
    onToggle: () -> Unit,
    steps: List<String>
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Surface),
        border = BorderStroke(1.dp, Border)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onToggle() },
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    fontSize = 13.5.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary
                )
                Icon(
                    imageVector = if (isExpanded) Lucide.ChevronUp else Lucide.ChevronDown,
                    contentDescription = null,
                    tint = TextSecondary,
                    modifier = Modifier.size(18.dp)
                )
            }

            if (isExpanded) {
                Spacer(modifier = Modifier.height(10.dp))
                HorizontalDivider(color = Border)
                Spacer(modifier = Modifier.height(10.dp))
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    steps.forEach { step ->
                        Text(
                            text = step,
                            fontSize = 12.sp,
                            color = TextSecondary,
                            lineHeight = 16.sp
                        )
                    }
                }
            }
        }
    }
}
