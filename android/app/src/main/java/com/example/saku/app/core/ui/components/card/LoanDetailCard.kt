package com.example.saku.app.core.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
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
import com.composables.icons.lucide.CreditCard
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Receipt
import com.example.saku.app.ui.theme.Border
import com.example.saku.app.ui.theme.Neutral0
import com.example.saku.app.ui.theme.Neutral40
import com.example.saku.app.ui.theme.Neutral60
import com.example.saku.app.ui.theme.Primary
import com.example.saku.app.ui.theme.Primary0
import com.example.saku.app.ui.theme.Primary60
import com.example.saku.app.ui.theme.Surface
import com.example.saku.app.ui.theme.TextMuted
import com.example.saku.app.ui.theme.TextPrimary
import com.example.saku.app.ui.theme.TextSecondary

data class LoanBreakdownItem(
    val label: String,
    val value: String
)

@Composable
fun LoanDetailCard(
    loanId: String,
    loanAmount: String,
    statusText: String,
    statusVariant: BadgeVariant,
    modifier: Modifier = Modifier,
    date: String? = null,
    tenor: String = "12 Bulan",
    monthlyInstallment: String = "Rp 476.000 / bln",
    dueDate: String = "25 Sep 2026",
    breakdownItems: List<LoanBreakdownItem>? = null,
    primaryButtonText: String? = "Bayar Tagihan",
    onPrimaryClick: (() -> Unit)? = null,
    secondaryButtonText: String? = "Rincian Cicilan",
    onSecondaryClick: (() -> Unit)? = null
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Surface),
        border = BorderStroke(1.dp, Border)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header Row: Loan ID + Status Badge (1-line)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f, fill = false)) {
                    Text(
                        text = loanId,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Primary60,
                        lineHeight = 19.sp
                    )
                    if (!date.isNullOrBlank()) {
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = date,
                            fontSize = 11.5.sp,
                            color = TextMuted,
                            lineHeight = 15.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                Badge(
                    text = statusText,
                    variant = statusVariant,
                    size = BadgeSize.SM,
                    dot = true,
                    modifier = Modifier.wrapContentWidth()
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Highlighted Total Loan Amount
            Text(
                text = "Total Nominal Pinjaman",
                fontSize = 12.sp,
                color = TextSecondary,
                lineHeight = 15.sp
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = loanAmount,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                lineHeight = 26.sp
            )

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = Border, thickness = 1.dp)
            Spacer(modifier = Modifier.height(12.dp))

            // 3-Column Quick Metrics
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = "Tenor", fontSize = 11.sp, color = TextMuted, lineHeight = 14.sp)
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(text = tenor, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary, lineHeight = 16.sp)
                }

                Column(modifier = Modifier.weight(1.3f)) {
                    Text(text = "Angsuran/Bln", fontSize = 11.sp, color = TextMuted, lineHeight = 14.sp)
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(text = monthlyInstallment, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = Primary60, lineHeight = 16.sp)
                }

                Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.End) {
                    Text(text = "Jatuh Tempo", fontSize = 11.sp, color = TextMuted, lineHeight = 14.sp)
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(text = dueDate, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary, lineHeight = 16.sp)
                }
            }

            // Optional extra breakdown items
            if (!breakdownItems.isNullOrEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(Neutral0)
                        .padding(10.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    breakdownItems.forEach { item ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = item.label, fontSize = 11.5.sp, color = Neutral60, lineHeight = 15.sp)
                            Text(text = item.value, fontSize = 11.5.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary, lineHeight = 15.sp)
                        }
                    }
                }
            }

            // Action Buttons Row
            if (primaryButtonText != null || secondaryButtonText != null) {
                Spacer(modifier = Modifier.height(14.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (secondaryButtonText != null && onSecondaryClick != null) {
                        Button(
                            text = secondaryButtonText,
                            onClick = onSecondaryClick,
                            variant = ButtonVariant.Outline,
                            size = ButtonSize.SM,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    if (primaryButtonText != null && onPrimaryClick != null) {
                        Button(
                            text = primaryButtonText,
                            onClick = onPrimaryClick,
                            variant = ButtonVariant.Primary,
                            size = ButtonSize.SM,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }
}
