package com.example.saku.app.core.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.composables.icons.lucide.ArrowUpRight
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.TrendingUp
import com.composables.icons.lucide.Wallet
import com.example.saku.app.ui.theme.Border
import com.example.saku.app.ui.theme.Error
import com.example.saku.app.ui.theme.Error0
import com.example.saku.app.ui.theme.Error70
import com.example.saku.app.ui.theme.Info
import com.example.saku.app.ui.theme.Info0
import com.example.saku.app.ui.theme.Info70
import com.example.saku.app.ui.theme.Neutral0
import com.example.saku.app.ui.theme.Neutral40
import com.example.saku.app.ui.theme.Neutral60
import com.example.saku.app.ui.theme.Neutral70
import com.example.saku.app.ui.theme.Primary
import com.example.saku.app.ui.theme.Primary0
import com.example.saku.app.ui.theme.Primary60
import com.example.saku.app.ui.theme.Primary70
import com.example.saku.app.ui.theme.Success
import com.example.saku.app.ui.theme.Success0
import com.example.saku.app.ui.theme.Success70
import com.example.saku.app.ui.theme.Surface
import com.example.saku.app.ui.theme.TextMuted
import com.example.saku.app.ui.theme.TextPrimary
import com.example.saku.app.ui.theme.TextSecondary
import com.example.saku.app.ui.theme.Warning
import com.example.saku.app.ui.theme.Warning0
import com.example.saku.app.ui.theme.Warning80

enum class SummaryCardVariant {
    Primary,
    Success,
    Warning,
    Error,
    Info,
    Neutral,
    GradientPrimary
}

data class StatTrendInfo(
    val text: String,
    val isPositive: Boolean = true
)

@Composable
fun InfoSummaryCard(
    title: String,
    value: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    icon: ImageVector? = Lucide.Wallet,
    variant: SummaryCardVariant = SummaryCardVariant.Primary,
    trend: StatTrendInfo? = null,
    badgeText: String? = null,
    badgeVariant: BadgeVariant = BadgeVariant.Primary,
    actionButtonText: String? = null,
    onActionClick: (() -> Unit)? = null,
    onClick: (() -> Unit)? = null
) {
    val clickModifier = if (onClick != null) {
        Modifier.clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = ripple(color = Primary.copy(alpha = 0.1f)),
            onClick = onClick
        )
    } else Modifier

    if (variant == SummaryCardVariant.GradientPrimary) {
        // High-impact Gradient Hero Card
        Card(
            modifier = modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .then(clickModifier),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = Color.Transparent)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.linearGradient(
                            listOf(Primary, Primary60, Primary70)
                        )
                    )
                    .padding(20.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = title,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.White.copy(alpha = 0.85f),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f, fill = false)
                        )

                        if (icon != null) {
                            Spacer(modifier = Modifier.width(8.dp))
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(Color.White.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = icon,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = value,
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        letterSpacing = (-0.5).sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    if (!subtitle.isNullOrBlank()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = subtitle,
                            fontSize = 12.sp,
                            color = Color.White.copy(alpha = 0.8f),
                            lineHeight = 16.sp
                        )
                    }

                    if (actionButtonText != null && onActionClick != null) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            text = actionButtonText,
                            onClick = onActionClick,
                            variant = ButtonVariant.White,
                            size = ButtonSize.SM,
                            shape = ButtonShape.Rounded,
                            trailingIcon = Lucide.ArrowUpRight
                        )
                    }
                }
            }
        }
    } else {
        // Standard Structured Stat Card (Optimized for 2-column or full width)
        val (iconBg, iconTint) = when (variant) {
            SummaryCardVariant.Primary -> Pair(Primary0, Primary70)
            SummaryCardVariant.Success -> Pair(Success0, Success70)
            SummaryCardVariant.Warning -> Pair(Warning0, Warning80)
            SummaryCardVariant.Error -> Pair(Error0, Error70)
            SummaryCardVariant.Info -> Pair(Info0, Info70)
            SummaryCardVariant.Neutral -> Pair(Neutral0, Neutral70)
            else -> Pair(Primary0, Primary70)
        }

        Card(
            modifier = modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .then(clickModifier),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Surface),
            border = BorderStroke(1.dp, Border)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp)
            ) {
                // Top Row: Icon on left, Badge on right
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (icon != null) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(iconBg),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = icon,
                                contentDescription = null,
                                tint = iconTint,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    } else {
                        Spacer(modifier = Modifier.size(1.dp))
                    }

                    if (badgeText != null) {
                        Badge(
                            text = badgeText,
                            variant = badgeVariant,
                            size = BadgeSize.SM
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Title (Full width of the card)
                Text(
                    text = title,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = TextSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(3.dp))

                // Value (Full width, bold prominent text)
                Text(
                    text = value,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                // Subtitle or Trend Row
                if (trend != null || !subtitle.isNullOrBlank()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    if (trend != null) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(
                                imageVector = Lucide.TrendingUp,
                                contentDescription = null,
                                modifier = Modifier.size(13.dp),
                                tint = if (trend.isPositive) Success else Error
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = trend.text,
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = if (trend.isPositive) Success else Error,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    } else if (!subtitle.isNullOrBlank()) {
                        Text(
                            text = subtitle,
                            fontSize = 11.5.sp,
                            color = TextMuted,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                // Optional Action Button
                if (actionButtonText != null && onActionClick != null) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        text = actionButtonText,
                        onClick = onActionClick,
                        variant = ButtonVariant.Secondary,
                        size = ButtonSize.SM,
                        shape = ButtonShape.Rounded,
                        fullWidth = true
                    )
                }
            }
        }
    }
}
