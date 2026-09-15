package com.example.saku.app.features.simulation

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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
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
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.composables.icons.lucide.ArrowLeft
import com.composables.icons.lucide.Calculator
import com.composables.icons.lucide.Lucide
import com.example.saku.app.core.ui.components.Badge
import com.example.saku.app.core.ui.components.BadgeSize
import com.example.saku.app.core.ui.components.BadgeVariant
import com.example.saku.app.core.ui.components.Button
import com.example.saku.app.core.ui.components.ButtonSize
import com.example.saku.app.core.ui.components.ButtonVariant
import com.example.saku.app.features.home.HomeViewModel
import com.example.saku.app.ui.theme.Background
import com.example.saku.app.ui.theme.Border
import com.example.saku.app.ui.theme.Neutral20
import com.example.saku.app.ui.theme.Primary
import com.example.saku.app.ui.theme.Primary0
import com.example.saku.app.ui.theme.Surface
import com.example.saku.app.ui.theme.TextMuted
import com.example.saku.app.ui.theme.TextPrimary
import com.example.saku.app.ui.theme.TextSecondary
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoanSimulationScreen(
    onNavigateBack: () -> Unit,
    onNavigateToApplyLoan: (Double, Int) -> Unit = { _, _ -> },
    viewModel: HomeViewModel = viewModel()
) {
    val customerProfile by viewModel.customerProfile.collectAsState()
    val simAmount by viewModel.simAmount.collectAsState()
    val simTenorMonths by viewModel.simTenorMonths.collectAsState()
    val simulasiResult by viewModel.simulasiResult.collectAsState()
    val currencyFormatter = remember { NumberFormat.getNumberInstance(Locale.forLanguageTag("id-ID")) }

    val minPlafond = 500_000.0
    val maxPlafond = (customerProfile?.availablePlafond ?: customerProfile?.totalPlafond ?: 50_000_000.0)
        .coerceAtLeast(minPlafond)

    LaunchedEffect(maxPlafond) {
        if (simAmount > maxPlafond) {
            viewModel.updateSimAmount(maxPlafond)
        } else {
            viewModel.updateSimAmount(simAmount.coerceIn(minPlafond, maxPlafond))
        }
    }

    val monthlyInstallment = viewModel.calculateMonthlyInstallment(simAmount, simTenorMonths)

    val candidateAmounts = listOf(1_000_000.0, 2_000_000.0, 5_000_000.0, 10_000_000.0, 20_000_000.0, 30_000_000.0, 50_000_000.0)
    val quickAmounts = (candidateAmounts.filter { it < maxPlafond && it >= minPlafond } + maxPlafond).distinct().sorted()
    val tenors = listOf(3, 6, 9, 12)

    val profileBunga = customerProfile?.sukuBunga
    val defaultBunga = if (profileBunga != null && profileBunga > 0) profileBunga else (simulasiResult?.sukuBungaPersen ?: 5.0)
    val sukuBunga = if (defaultBunga <= 1.0 && defaultBunga > 0.0) defaultBunga * 100 else defaultBunga
    val biayaAdmin = customerProfile?.biayaAdmin ?: (simulasiResult?.biayaAdmin ?: 250_000.0)
    val isApplyEnabled = (customerProfile?.availablePlafond ?: 50_000_000.0) >= minPlafond
    val totalPengembalian = simulasiResult?.totalPembayaran ?: ((monthlyInstallment * simTenorMonths).toDouble() + biayaAdmin)

    Scaffold(
        topBar = {
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
                        onClick = onNavigateBack,
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
                        text = "Simulasi Pinjaman SAKU",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }
            }
        },
        containerColor = Background
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 18.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. Plafond Info Card (Selaras dengan Form Pengajuan)
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Surface),
                    border = BorderStroke(1.dp, Border)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Sisa Plafond Tersedia",
                                fontSize = 12.sp,
                                color = TextMuted
                            )
                            Spacer(modifier = Modifier.height(3.dp))
                            Text(
                                text = "Rp ${currencyFormatter.format(maxPlafond)}",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        }
                        val tierBadgeText = customerProfile?.tierPlafond ?: "Bunga ${String.format(Locale.US, "%.1f", sukuBunga)}%/bln"
                        Badge(
                            text = tierBadgeText,
                            variant = BadgeVariant.Success,
                            size = BadgeSize.SM
                        )
                    }
                }
            }

            // 2. Section: Jumlah Pinjaman & Input Bebas / Slider
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = Surface),
                    border = BorderStroke(1.dp, Border)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Text(
                            text = "Jumlah Pinjaman",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        val stepAmount = 500_000.0

                        // Amount Input Box with Stepper & Direct Editable Input
                        var amountInputText by remember(simAmount) {
                            mutableStateOf(if (simAmount > 0) currencyFormatter.format(simAmount.toLong()) else "")
                        }

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .background(Background)
                                .border(1.dp, Border, RoundedCornerShape(14.dp))
                                .padding(vertical = 12.dp, horizontal = 12.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Stepper Decrement Button
                                IconButton(
                                    onClick = {
                                        val newVal = (simAmount - stepAmount).coerceIn(minPlafond, maxPlafond)
                                        viewModel.updateSimAmount(newVal)
                                    },
                                    enabled = simAmount > minPlafond,
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(if (simAmount > minPlafond) Surface else Background)
                                        .border(1.dp, Border, RoundedCornerShape(10.dp))
                                ) {
                                    Text(
                                        text = "−",
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (simAmount > minPlafond) TextPrimary else TextMuted
                                    )
                                }

                                // Central Direct Editable Amount Display
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier
                                        .weight(1f)
                                        .padding(horizontal = 8.dp)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.Center
                                    ) {
                                        Text(
                                            text = "Rp ",
                                            fontSize = 22.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = TextPrimary
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
                                                    }
                                                }
                                            },
                                            textStyle = TextStyle(
                                                fontSize = 22.sp,
                                                fontWeight = FontWeight.ExtraBold,
                                                color = TextPrimary,
                                                textAlign = TextAlign.Start
                                            ),
                                            keyboardOptions = KeyboardOptions(
                                                keyboardType = KeyboardType.Number,
                                                imeAction = ImeAction.Done
                                            ),
                                            singleLine = true,
                                            cursorBrush = SolidColor(Primary)
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = "Ketik nominal atau geser slider",
                                        fontSize = 11.sp,
                                        color = TextMuted
                                    )
                                }

                                // Stepper Increment Button
                                IconButton(
                                    onClick = {
                                        val newVal = (simAmount + stepAmount).coerceIn(minPlafond, maxPlafond)
                                        viewModel.updateSimAmount(newVal)
                                    },
                                    enabled = simAmount < maxPlafond,
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(if (simAmount < maxPlafond) Surface else Background)
                                        .border(1.dp, Border, RoundedCornerShape(10.dp))
                                ) {
                                    Text(
                                        text = "+",
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (simAmount < maxPlafond) TextPrimary else TextMuted
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Smooth Continuous / Step Slider
                        val currentSliderVal = simAmount.toFloat().coerceIn(minPlafond.toFloat(), maxPlafond.toFloat())
                        Slider(
                            value = currentSliderVal,
                            onValueChange = { newVal ->
                                val rounded = (Math.round(newVal / 100_000.0) * 100_000.0).coerceIn(minPlafond, maxPlafond)
                                viewModel.updateSimAmount(rounded)
                            },
                            valueRange = minPlafond.toFloat()..maxPlafond.toFloat(),
                            colors = SliderDefaults.colors(
                                thumbColor = Primary,
                                activeTrackColor = Primary,
                                inactiveTrackColor = Neutral20
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "Min: Rp ${currencyFormatter.format(minPlafond)}", fontSize = 11.sp, color = TextMuted)
                            Text(text = "Maks: Rp ${currencyFormatter.format(maxPlafond)}", fontSize = 11.sp, color = TextMuted)
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Pilihan Cepat (Scrollable LazyRow agar tidak terpotong)
                        Text(
                            text = "Pilihan Cepat",
                            fontSize = 12.sp,
                            color = TextSecondary,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(quickAmounts) { amt ->
                                val isSelected = simAmount == amt
                                val isMax = amt == maxPlafond
                                val label = if (isMax) "Maksimal" else "Rp ${currencyFormatter.format(amt)}"

                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(if (isSelected) Primary0 else Surface)
                                        .border(
                                            width = if (isSelected) 1.5.dp else 1.dp,
                                            color = if (isSelected) Primary else Border,
                                            shape = RoundedCornerShape(10.dp)
                                        )
                                        .clickable { viewModel.updateSimAmount(amt) }
                                        .padding(horizontal = 14.dp, vertical = 9.dp)
                                ) {
                                    Text(
                                        text = label,
                                        fontSize = 12.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isSelected) Primary else TextPrimary
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // 3. Section: Pilihan Tenor
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
                            Text(
                                text = "Jangka Waktu (Tenor)",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Text(
                                text = "$simTenorMonths Bulan",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Primary
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(tenors) { months ->
                                val isSelected = simTenorMonths == months
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(if (isSelected) Primary0 else Surface)
                                        .border(
                                            width = if (isSelected) 1.5.dp else 1.dp,
                                            color = if (isSelected) Primary else Border,
                                            shape = RoundedCornerShape(12.dp)
                                        )
                                        .clickable { viewModel.updateSimTenor(months) }
                                        .padding(horizontal = 16.dp, vertical = 10.dp)
                                ) {
                                    Text(
                                        text = "$months Bulan",
                                        fontSize = 13.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isSelected) Primary else TextPrimary
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // 4. Section: Rincian Estimasi Angsuran
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Surface),
                    border = BorderStroke(1.dp, Border)
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Lucide.Calculator,
                                contentDescription = null,
                                tint = Primary,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "Rincian Estimasi Angsuran",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        }

                        // Hero Monthly Installment Box
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .background(Primary0)
                                .border(1.dp, Primary.copy(alpha = 0.25f), RoundedCornerShape(14.dp))
                                .padding(16.dp)
                        ) {
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "Total Cicilan Bulanan",
                                    fontSize = 12.sp,
                                    color = TextSecondary
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "Rp ${currencyFormatter.format(monthlyInstallment)}",
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Primary
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "per bulan selama $simTenorMonths bulan",
                                    fontSize = 11.5.sp,
                                    color = TextMuted
                                )
                            }
                        }

                        // Breakdown Items
                        SimulasiRow(label = "Pokok Pinjaman", value = "Rp ${currencyFormatter.format(simAmount)}")
                        SimulasiRow(label = "Suku Bunga", value = "${String.format(Locale.US, "%.1f", sukuBunga)}% Flat / Bulan")
                        SimulasiRow(label = "Biaya Administrasi", value = "Rp ${currencyFormatter.format(biayaAdmin)}")
                        SimulasiRow(label = "Total Estimasi Pengembalian", value = "Rp ${currencyFormatter.format(totalPengembalian)}", isBold = true)

                        Spacer(modifier = Modifier.height(6.dp))

                        Button(
                            text = if (isApplyEnabled) "Ajukan Pinjaman Sekarang" else "Limit Tidak Mencukupi (Rp 0)",
                            onClick = { onNavigateToApplyLoan(simAmount, simTenorMonths) },
                            enabled = isApplyEnabled,
                            variant = ButtonVariant.Primary,
                            size = ButtonSize.LG,
                            fullWidth = true
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SimulasiRow(label: String, value: String, isBold: Boolean = false) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, fontSize = 12.5.sp, color = if (isBold) TextPrimary else TextSecondary, fontWeight = if (isBold) FontWeight.SemiBold else FontWeight.Normal)
        Text(text = value, fontSize = 12.5.sp, fontWeight = if (isBold) FontWeight.Bold else FontWeight.SemiBold, color = TextPrimary)
    }
}
