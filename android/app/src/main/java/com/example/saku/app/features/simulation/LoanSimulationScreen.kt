package com.example.saku.app.features.simulation

import android.widget.Toast
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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.composables.icons.lucide.ArrowLeft
import com.composables.icons.lucide.CircleAlert
import com.composables.icons.lucide.Lucide
import com.example.saku.app.R
import com.example.saku.app.core.ui.components.Button
import com.example.saku.app.core.ui.components.ButtonSize
import com.example.saku.app.core.ui.components.ButtonVariant
import com.example.saku.app.core.ui.components.Checkbox
import com.example.saku.app.features.home.HomeViewModel
import com.example.saku.app.ui.theme.Background
import com.example.saku.app.ui.theme.Border
import com.example.saku.app.ui.theme.Neutral0
import com.example.saku.app.ui.theme.OverusedGrotesk
import com.example.saku.app.ui.theme.Primary
import com.example.saku.app.ui.theme.Primary0
import com.example.saku.app.ui.theme.Surface
import com.example.saku.app.ui.theme.TextMuted
import com.example.saku.app.ui.theme.TextPrimary
import com.example.saku.app.ui.theme.TextSecondary
import com.example.saku.app.ui.theme.Warning
import com.example.saku.app.ui.theme.Warning0
import com.example.saku.app.ui.theme.Warning80
import java.text.NumberFormat
import java.util.Locale
import org.koin.androidx.compose.koinViewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoanSimulationScreen(
    onNavigateBack: () -> Unit,
    onNavigateToApplyLoan: (Double, Int) -> Unit = { _, _ -> },
    viewModel: HomeViewModel = koinViewModel()
) {
    val customerProfile by viewModel.customerProfile.collectAsState()
    val myLoans by viewModel.myLoans.collectAsState()
    val simAmount by viewModel.simAmount.collectAsState()
    val simTenorMonths by viewModel.simTenorMonths.collectAsState()
    val simulasiResult by viewModel.simulasiResult.collectAsState()
    val context = androidx.compose.ui.platform.LocalContext.current
    val currencyFormatter = remember { NumberFormat.getNumberInstance(Locale.forLanguageTag("id-ID")) }

    val hasInProcessLoan = remember(myLoans) {
        myLoans.any {
            val s = (it.statusPengajuan ?: "").uppercase()
            s !in listOf("DITOLAK", "PENGAJUAN_DITOLAK", "REJECTED", "DITOLAK_MARKETING", "DITOLAK_BM", "REJECT", "BATAL", "CANCELLED", "PAID", "LUNAS", "DICAIRKAN", "DISBURSED")
        }
    }

    val minPlafond = 500_000.0
    val maxPlafond = (customerProfile?.availablePlafond ?: customerProfile?.totalPlafond ?: 50_000_000.0)
        .coerceAtLeast(minPlafond)

    LaunchedEffect(maxPlafond) {
        if (simAmount > maxPlafond) {
            viewModel.updateSimAmount(maxPlafond)
        } else if (simAmount < minPlafond) {
            viewModel.updateSimAmount(25_000_000.0.coerceIn(minPlafond, maxPlafond))
        }
    }

    val profileBunga = customerProfile?.sukuBunga
    val defaultBunga = if (profileBunga != null && profileBunga > 0) profileBunga else (simulasiResult?.sukuBungaPersen ?: 1.25)
    val sukuBunga = if (defaultBunga <= 1.0 && defaultBunga > 0.0) defaultBunga * 100 else defaultBunga
    val formattedBunga = if (sukuBunga % 1.0 == 0.0) {
        "${sukuBunga.toLong()}%"
    } else {
        "${sukuBunga.toString().replace('.', ',')}%"
    }
    val biayaAdmin = customerProfile?.biayaAdmin ?: (simulasiResult?.biayaAdmin ?: 250_000.0)
    val isApplyEnabled = (customerProfile?.availablePlafond ?: 50_000_000.0) >= minPlafond && !hasInProcessLoan

    // Perhitungan Cicilan Bulanan, Total Bunga & Total Pengembalian (Konsisten & Presisi)
    val pokokBulanan = if (simTenorMonths > 0) simAmount / simTenorMonths else 0.0
    val bungaBulanan = simAmount * (sukuBunga / 100.0)
    val monthlyInstallment = (pokokBulanan + bungaBulanan).toLong()
    val totalBunga = (bungaBulanan * simTenorMonths).toLong()
    val totalPengembalian = (simAmount + totalBunga).toLong()
    val danaBersihCair = (simAmount - biayaAdmin).coerceAtLeast(0.0).toLong()

    val availableTenors = listOf(3, 6, 9, 12, 18, 24, 36)
    val quickAmounts = listOf(1_000_000.0, 5_000_000.0, 10_000_000.0, 25_000_000.0, maxPlafond)
        .filter { it <= maxPlafond && it >= minPlafond }.distinct().sorted()

    var isAgreedToTerms by remember { mutableStateOf(true) }
    var amountInputText by remember(simAmount) {
        mutableStateOf(if (simAmount > 0) currencyFormatter.format(simAmount.toLong()) else "")
    }
    val scrollState = rememberScrollState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
    ) {
        // --- 1. HEADER SECTION WITH bg_card_saku (Plafond Card Theme) ---
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(230.dp)
                .background(Primary)
        ) {
            // Background Image matching Homescreen Plafond Card with Zoom
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
                        transformOrigin = TransformOrigin(0.88f, 0.5f)
                    )
            )

            // Hero Limit Info
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(top = 56.dp, start = 20.dp, end = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Maksimal Limit Hingga",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.White.copy(alpha = 0.88f)
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Rp ${currencyFormatter.format(maxPlafond)}",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White,
                    letterSpacing = (-0.5).sp
                )

                val formattedBunga = if (sukuBunga % 1.0 == 0.0) {
                    "${sukuBunga.toLong()}%"
                } else {
                    "${sukuBunga.toString().replace('.', ',')}%"
                }

                Text(
                    text = "Suku bunga $formattedBunga per bulan",
                    fontSize = 12.5.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.White.copy(alpha = 0.92f)
                )
            }
        }

        // --- 2. MAIN SCROLLABLE CONTENT WITH FLOATING CARD ---
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .statusBarsPadding()
                .padding(top = 170.dp) // Perfectly balanced overlap over header
                .padding(horizontal = 18.dp)
                .navigationBarsPadding()
        ) {
            // FLOATING WHITE CARD
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(
                        elevation = 12.dp,
                        shape = RoundedCornerShape(24.dp),
                        ambientColor = Color(0x14000000),
                        spotColor = Color(0x24000000)
                    ),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Surface),
                border = BorderStroke(1.dp, Border)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(22.dp)
                ) {
                    // --- SECTION 1: NOMINAL YANG DIAJUKAN (DAPAT DIKETIK) ---
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "Nominal yang Diajukan",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Rp ",
                                fontSize = 26.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = TextPrimary,
                                letterSpacing = (-0.5).sp
                            )
                            BasicTextField(
                                value = amountInputText,
                                onValueChange = { input ->
                                    val digits = input.filter { it.isDigit() }
                                    if (digits.length <= 11) {
                                        val parsed = digits.toDoubleOrNull() ?: 0.0
                                        val clamped = if (parsed > maxPlafond) maxPlafond else parsed
                                        amountInputText = if (clamped > 0) currencyFormatter.format(clamped.toLong()) else ""
                                        if (clamped >= minPlafond) {
                                            viewModel.updateSimAmount(clamped)
                                        } else if (clamped == 0.0) {
                                            viewModel.updateSimAmount(minPlafond)
                                        }
                                    }
                                },
                                textStyle = TextStyle(
                                    fontFamily = OverusedGrotesk,
                                    fontSize = 26.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = TextPrimary,
                                    letterSpacing = (-0.5).sp
                                ),
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Number,
                                    imeAction = ImeAction.Done
                                ),
                                singleLine = true,
                                cursorBrush = SolidColor(Primary),
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Ketuk untuk edit",
                            fontSize = 11.sp,
                            color = TextMuted
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Slider 1: Jumlah Pinjaman
                    val sliderAmountVal = simAmount.toFloat().coerceIn(minPlafond.toFloat(), maxPlafond.toFloat())
                    Slider(
                        value = sliderAmountVal,
                        onValueChange = { newVal ->
                            val step = 500_000.0
                            val rounded = (Math.round(newVal / step) * step).coerceIn(minPlafond, maxPlafond)
                            viewModel.updateSimAmount(rounded)
                        },
                        valueRange = minPlafond.toFloat()..maxPlafond.toFloat(),
                        colors = SliderDefaults.colors(
                            thumbColor = Primary,
                            activeTrackColor = Primary,
                            inactiveTrackColor = Color(0xFFEBEBEB)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Rp ${currencyFormatter.format(minPlafond)}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = TextMuted
                        )
                        Text(
                            text = "Rp ${currencyFormatter.format(maxPlafond)}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = TextMuted
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Quick Selection Chips (Nominal)
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(quickAmounts) { amt ->
                            val isSelected = simAmount == amt
                            val label = if (amt == maxPlafond) "Maksimal" else "Rp ${currencyFormatter.format(amt / 1_000_000)} Jt"

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (isSelected) Primary0 else Neutral0)
                                    .border(
                                        width = if (isSelected) 1.5.dp else 1.dp,
                                        color = if (isSelected) Primary else Border,
                                        shape = RoundedCornerShape(10.dp)
                                    )
                                    .clickable { viewModel.updateSimAmount(amt) }
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = label,
                                    fontSize = 11.5.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = TextPrimary
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // --- SECTION 2: TENOR PINJAMAN ---
                    Text(
                        text = "Tenor Pinjaman",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Slider 2: Tenor Pinjaman
                    val minTenor = 3
                    val maxTenor = 36
                    Slider(
                        value = simTenorMonths.toFloat().coerceIn(minTenor.toFloat(), maxTenor.toFloat()),
                        onValueChange = { newVal ->
                            val closest = availableTenors.minByOrNull { Math.abs(it - newVal.toInt()) } ?: newVal.toInt()
                            viewModel.updateSimTenor(closest)
                        },
                        valueRange = minTenor.toFloat()..maxTenor.toFloat(),
                        steps = 0,
                        colors = SliderDefaults.colors(
                            thumbColor = Primary,
                            activeTrackColor = Primary,
                            inactiveTrackColor = Color(0xFFEBEBEB)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "3 Bulan",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = TextMuted
                        )
                        Text(
                            text = "36 Bulan",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = TextMuted
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Quick Tenor Chips
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(availableTenors) { months ->
                            val isSelected = simTenorMonths == months
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (isSelected) Primary0 else Neutral0)
                                    .border(
                                        width = if (isSelected) 1.5.dp else 1.dp,
                                        color = if (isSelected) Primary else Border,
                                        shape = RoundedCornerShape(10.dp)
                                    )
                                    .clickable { viewModel.updateSimTenor(months) }
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = "$months Bulan",
                                    fontSize = 11.5.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = TextPrimary
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(22.dp))

                    // Subtle Divider
                    HorizontalDivider(
                        color = Border,
                        thickness = 1.dp
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    // --- SECTION 3: RINCIAN ESTIMASI ANGSURAN ---
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        BreakdownRow(
                            label = "Cicilan Bulanan (Estimasi):",
                            value = "Rp ${currencyFormatter.format(monthlyInstallment)} / bln",
                            isHighlight = true
                        )

                        BreakdownRow(
                            label = "Nominal Pinjaman:",
                            value = "Rp ${currencyFormatter.format(simAmount.toLong())}"
                        )

                        BreakdownRow(
                            label = "Biaya Administrasi (Potong Awal):",
                            value = "- Rp ${currencyFormatter.format(biayaAdmin)}"
                        )

                        BreakdownRow(
                            label = "Dana Bersih Cair:",
                            value = "Rp ${currencyFormatter.format(danaBersihCair)}",
                            isValueBold = true
                        )

                        BreakdownRow(
                            label = "Tenor Pinjaman:",
                            value = "$simTenorMonths Bulan"
                        )

                        BreakdownRow(
                            label = "Total Estimasi Bunga ($formattedBunga/bln):",
                            value = "Rp ${currencyFormatter.format(totalBunga)}"
                        )

                        BreakdownRow(
                            label = "Total Pengembalian (Cicilan):",
                            value = "Rp ${currencyFormatter.format(totalPengembalian)}",
                            isBold = true
                        )
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // Disclaimer Note in Warning Style
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Warning0)
                            .border(1.dp, Warning.copy(alpha = 0.35f), RoundedCornerShape(12.dp))
                            .padding(12.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.Top,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Lucide.CircleAlert,
                                contentDescription = null,
                                tint = Warning80,
                                modifier = Modifier
                                    .size(16.dp)
                                    .offset(y = 1.dp)
                            )
                            Column {
                                Text(
                                    text = "Disclaimer",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Warning80
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "Perhitungan di atas bersifat estimasi indikatif dan dapat disesuaikan berdasarkan skor profil kredit nasabah.",
                                    fontSize = 11.sp,
                                    color = Warning80.copy(alpha = 0.9f),
                                    lineHeight = 15.sp
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // --- 3. AGREEMENT CHECKBOX & TERMS ---
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { isAgreedToTerms = !isAgreedToTerms }
                    .padding(vertical = 4.dp, horizontal = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = isAgreedToTerms,
                    onCheckedChange = { isAgreedToTerms = it }
                )

                Spacer(modifier = Modifier.width(10.dp))

                val agreementText = buildAnnotatedString {
                    append("Saya telah membaca dan menyetujui ")
                    withStyle(style = SpanStyle(color = Primary, fontWeight = FontWeight.Bold)) {
                        append("Syarat & Ketentuan")
                    }
                    append(" serta ")
                    withStyle(style = SpanStyle(color = Primary, fontWeight = FontWeight.Bold)) {
                        append("Kebijakan Privasi")
                    }
                    append(".")
                }

                Text(
                    text = agreementText,
                    fontSize = 12.sp,
                    color = TextSecondary,
                    lineHeight = 16.sp
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // --- 4. APPLY NOW CTA BUTTON ---
            val buttonText = when {
                hasInProcessLoan -> "Sedang Ada Pengajuan Berjalan"
                !isApplyEnabled -> "Plafond Belum Tersedia"
                else -> "Ajukan Sekarang"
            }

            Button(
                text = buttonText,
                onClick = {
                    if (hasInProcessLoan) {
                        val inProg = myLoans.firstOrNull {
                            val s = (it.statusPengajuan ?: "").uppercase()
                            s !in listOf("DITOLAK", "PENGAJUAN_DITOLAK", "REJECTED", "DITOLAK_MARKETING", "DITOLAK_BM", "REJECT", "BATAL", "CANCELLED", "PAID", "LUNAS", "DICAIRKAN", "DISBURSED")
                        }
                        Toast.makeText(
                            context,
                            "Anda memiliki pengajuan (${inProg?.nomorPengajuan ?: "berjalan"}) yang sedang diproses review.",
                            Toast.LENGTH_LONG
                        ).show()
                    } else if (isAgreedToTerms && isApplyEnabled) {
                        onNavigateToApplyLoan(simAmount, simTenorMonths)
                    }
                },
                enabled = isAgreedToTerms,
                variant = if (hasInProcessLoan) ButtonVariant.Outline else ButtonVariant.Primary,
                size = ButtonSize.LG,
                fullWidth = true
            )

            Spacer(modifier = Modifier.height(30.dp))
        }

        // --- 3. TOP NAV BAR ---
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 12.dp, vertical = 6.dp),
            contentAlignment = Alignment.Center
        ) {
            IconButton(
                onClick = onNavigateBack,
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .size(40.dp)
            ) {
                Icon(
                    imageVector = Lucide.ArrowLeft,
                    contentDescription = "Kembali",
                    tint = Color.White,
                    modifier = Modifier.size(22.dp)
                )
            }

            Text(
                text = "Simulasi Pinjaman",
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun BreakdownRow(
    label: String,
    value: String,
    isHighlight: Boolean = false,
    isBold: Boolean = false,
    isValueBold: Boolean = false
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = if (isHighlight) 13.5.sp else 12.5.sp,
            color = if (isHighlight || isBold) TextPrimary else TextSecondary,
            fontWeight = if (isHighlight || isBold) FontWeight.SemiBold else FontWeight.Normal,
            maxLines = 1
        )
        Text(
            text = value,
            fontSize = if (isHighlight) 14.5.sp else 12.5.sp,
            fontWeight = if (isHighlight || isBold || isValueBold) FontWeight.Bold else FontWeight.SemiBold,
            color = if (isHighlight) Primary else TextPrimary,
            maxLines = 1
        )
    }
}