package com.example.saku.app.core.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.composables.icons.lucide.Bell
import com.composables.icons.lucide.CircleAlert
import com.composables.icons.lucide.CreditCard
import com.composables.icons.lucide.Info
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.ShieldCheck
import com.composables.icons.lucide.Sparkles
import com.composables.icons.lucide.Wallet
import com.example.saku.app.ui.theme.Border
import com.example.saku.app.ui.theme.Error0
import com.example.saku.app.ui.theme.Error70
import com.example.saku.app.ui.theme.Info0
import com.example.saku.app.ui.theme.Info70
import com.example.saku.app.ui.theme.Neutral0
import com.example.saku.app.ui.theme.Neutral60
import com.example.saku.app.ui.theme.Primary
import com.example.saku.app.ui.theme.Primary0
import com.example.saku.app.ui.theme.Primary70
import com.example.saku.app.ui.theme.Purple0
import com.example.saku.app.ui.theme.Purple70
import com.example.saku.app.ui.theme.Success0
import com.example.saku.app.ui.theme.Success70
import com.example.saku.app.ui.theme.Surface
import com.example.saku.app.ui.theme.TextMuted
import com.example.saku.app.ui.theme.TextPrimary
import com.example.saku.app.ui.theme.TextSecondary
import com.example.saku.app.ui.theme.Warning0
import com.example.saku.app.ui.theme.Warning80

enum class NotificationCategory {
    TRANSACTION,
    PROMO,
    SECURITY,
    REMINDER,
    SYSTEM
}

data class NotificationItem(
    val id: String,
    val title: String,
    val message: String,
    val timestamp: String,
    val category: NotificationCategory = NotificationCategory.SYSTEM,
    val isRead: Boolean = false,
    val actionLabel: String? = null,
    val icon: ImageVector? = null
)

@Composable
fun NotificationItemCard(
    item: NotificationItem,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    onActionClick: (() -> Unit)? = null
) {
    val (iconBg, iconTint, defaultIcon) = when (item.category) {
        NotificationCategory.TRANSACTION -> Triple(Primary0, Primary70, Lucide.Wallet)
        NotificationCategory.PROMO -> Triple(Purple0, Purple70, Lucide.Sparkles)
        NotificationCategory.SECURITY -> Triple(Info0, Info70, Lucide.ShieldCheck)
        NotificationCategory.REMINDER -> Triple(Warning0, Warning80, Lucide.CircleAlert)
        NotificationCategory.SYSTEM -> Triple(Neutral0, Neutral60, Lucide.Bell)
    }

    val displayIcon = item.icon ?: defaultIcon

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
        colors = CardDefaults.cardColors(
            containerColor = if (!item.isRead) Primary0.copy(alpha = 0.35f) else Surface
        ),
        border = BorderStroke(
            width = if (!item.isRead) 1.2.dp else 1.dp,
            color = if (!item.isRead) Primary.copy(alpha = 0.35f) else Border
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.Top
        ) {
            // Category Icon Bubble
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(iconBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = displayIcon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(18.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Main Message Content (with tight typography and line-height)
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = item.title,
                        fontSize = 13.5.sp,
                        fontWeight = if (!item.isRead) FontWeight.Bold else FontWeight.SemiBold,
                        color = TextPrimary,
                        lineHeight = 17.sp,
                        modifier = Modifier.weight(1f)
                    )

                    Spacer(modifier = Modifier.width(6.dp))

                    if (!item.isRead) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(Primary)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = item.message,
                    fontSize = 11.5.sp,
                    color = TextSecondary,
                    lineHeight = 15.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = item.timestamp,
                        fontSize = 11.sp,
                        color = TextMuted,
                        lineHeight = 14.sp
                    )

                    if (item.actionLabel != null && onActionClick != null) {
                        Text(
                            text = item.actionLabel,
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = Primary,
                            lineHeight = 14.sp,
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .clickable(onClick = onActionClick)
                                .padding(horizontal = 4.dp, vertical = 2.dp)
                        )
                    }
                }
            }
        }
    }
}
