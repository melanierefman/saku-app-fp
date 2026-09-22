package com.example.saku.app.core.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.composables.icons.lucide.ArrowDownLeft
import com.composables.icons.lucide.ArrowUpRight
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Receipt
import com.composables.icons.lucide.RotateCw
import com.composables.icons.lucide.Wallet
import com.example.saku.app.ui.theme.Border
import com.example.saku.app.ui.theme.Info0
import com.example.saku.app.ui.theme.Info70
import com.example.saku.app.ui.theme.Primary
import com.example.saku.app.ui.theme.Primary0
import com.example.saku.app.ui.theme.Primary70
import com.example.saku.app.ui.theme.Purple0
import com.example.saku.app.ui.theme.Purple70
import com.example.saku.app.ui.theme.Success
import com.example.saku.app.ui.theme.Success0
import com.example.saku.app.ui.theme.Success70
import com.example.saku.app.ui.theme.Surface
import com.example.saku.app.ui.theme.TextPrimary
import com.example.saku.app.ui.theme.TextSecondary
import com.example.saku.app.ui.theme.Warning0
import com.example.saku.app.ui.theme.Warning80

enum class HistoryType {
    DISBURSEMENT, // Pencairan pinjaman (+)
    PAYMENT,      // Pembayaran angsuran (-)
    TOPUP,        // Top-up SAKU (+)
    FEE,          // Biaya admin / denda (-)
    REFUND        // Pengembalian dana (+)
}

data class HistoryItem(
    val id: String,
    val title: String,
    val date: String,
    val amount: String,
    val isIncome: Boolean,
    val statusText: String,
    val statusVariant: BadgeVariant = BadgeVariant.Success,
    val type: HistoryType = HistoryType.PAYMENT,
    val subtitle: String? = null,
    val icon: ImageVector? = null
)

@Composable
fun HistoryListItemCard(
    item: HistoryItem,
    modifier: Modifier = Modifier,
    cardStyle: Boolean = true,
    onClick: (() -> Unit)? = null
) {
    val (iconBg, iconTint, defaultIcon) = when (item.type) {
        HistoryType.DISBURSEMENT -> Triple(Success0, Success70, Lucide.ArrowDownLeft)
        HistoryType.PAYMENT -> Triple(Primary0, Primary70, Lucide.ArrowUpRight)
        HistoryType.TOPUP -> Triple(Info0, Info70, Lucide.Wallet)
        HistoryType.FEE -> Triple(Warning0, Warning80, Lucide.Receipt)
        HistoryType.REFUND -> Triple(Purple0, Purple70, Lucide.RotateCw)
    }

    val displayIcon = item.icon ?: defaultIcon

    val content = @Composable {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(if (cardStyle) 14.dp else 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon Bubble
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(iconBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = displayIcon,
                    contentDescription = item.title,
                    tint = iconTint,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Title & Subtitle Column (with tight typography)
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = item.title,
                    fontSize = 13.5.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = 17.sp
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = item.subtitle ?: item.date,
                    fontSize = 11.5.sp,
                    color = TextSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = 15.sp
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Amount & Status Badge Column
            Column(
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = if (item.isIncome) "+${item.amount}" else "-${item.amount}",
                    fontSize = 13.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (item.isIncome) Success70 else TextPrimary,
                    lineHeight = 17.sp
                )
                Spacer(modifier = Modifier.height(3.dp))
                Badge(
                    text = item.statusText,
                    variant = item.statusVariant,
                    size = BadgeSize.SM,
                    modifier = Modifier.wrapContentWidth()
                )
            }
        }
    }

    if (cardStyle) {
        Card(
            modifier = modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .then(
                    if (onClick != null) {
                        Modifier.clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = ripple(color = Primary.copy(alpha = 0.1f)),
                            onClick = onClick
                        )
                    } else Modifier
                ),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = Surface),
            border = BorderStroke(1.dp, Border)
        ) {
            content()
        }
    } else {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .then(
                    if (onClick != null) {
                        Modifier.clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = ripple(color = Primary.copy(alpha = 0.1f)),
                            onClick = onClick
                        )
                    } else Modifier
                )
        ) {
            content()
        }
    }
}

@Composable
fun HistoryGroupCard(
    title: String,
    items: List<HistoryItem>,
    modifier: Modifier = Modifier,
    onItemClick: ((HistoryItem) -> Unit)? = null
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Surface),
        border = BorderStroke(1.dp, Border)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = title,
                fontSize = 12.5.sp,
                fontWeight = FontWeight.Bold,
                color = TextSecondary,
                lineHeight = 16.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            items.forEachIndexed { index, item ->
                HistoryListItemCard(
                    item = item,
                    cardStyle = false,
                    onClick = if (onItemClick != null) {
                        { onItemClick(item) }
                    } else null
                )

                if (index < items.lastIndex) {
                    HorizontalDivider(
                        color = Border,
                        thickness = 0.8.dp,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }
            }
        }
    }
}