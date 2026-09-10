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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.composables.icons.lucide.ChevronRight
import com.composables.icons.lucide.Lucide
import com.example.saku.app.ui.theme.Border
import com.example.saku.app.ui.theme.Error
import com.example.saku.app.ui.theme.Error0
import com.example.saku.app.ui.theme.Neutral0
import com.example.saku.app.ui.theme.Neutral60
import com.example.saku.app.ui.theme.Primary
import com.example.saku.app.ui.theme.Primary0
import com.example.saku.app.ui.theme.Surface
import com.example.saku.app.ui.theme.TextMuted
import com.example.saku.app.ui.theme.TextPrimary
import com.example.saku.app.ui.theme.TextSecondary

enum class MenuTrailingType {
    Chevron,
    Badge,
    Value,
    Switch,
    None
}

data class MenuItemData(
    val id: String,
    val title: String,
    val icon: ImageVector,
    val subtitle: String? = null,
    val trailingType: MenuTrailingType = MenuTrailingType.Chevron,
    val badgeText: String? = null,
    val badgeVariant: BadgeVariant = BadgeVariant.Success,
    val valueText: String? = null,
    val switchChecked: Boolean = false,
    val onSwitchChange: ((Boolean) -> Unit)? = null,
    val isDestructive: Boolean = false,
    val iconTint: Color? = null,
    val iconBackground: Color? = null
)

@Composable
fun MenuListItem(
    title: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    trailingType: MenuTrailingType = MenuTrailingType.Chevron,
    badgeText: String? = null,
    badgeVariant: BadgeVariant = BadgeVariant.Success,
    valueText: String? = null,
    switchChecked: Boolean = false,
    onSwitchChange: ((Boolean) -> Unit)? = null,
    isDestructive: Boolean = false,
    iconTint: Color? = null,
    iconBackground: Color? = null,
    onClick: (() -> Unit)? = null
) {
    val finalIconTint = when {
        isDestructive -> Error
        iconTint != null -> iconTint
        else -> TextPrimary
    }

    val finalIconBg = when {
        isDestructive -> Error0
        iconBackground != null -> iconBackground
        else -> Neutral0
    }

    val clickModifier = if (onClick != null && trailingType != MenuTrailingType.Switch) {
        Modifier.clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = ripple(color = if (isDestructive) Error.copy(alpha = 0.1f) else Primary.copy(alpha = 0.1f)),
            onClick = onClick
        )
    } else Modifier

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .then(clickModifier)
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Leading Icon with soft container
        Box(
            modifier = Modifier
                .size(38.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(finalIconBg),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = finalIconTint,
                modifier = Modifier.size(18.dp)
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        // Title & Subtitle Column (with tight line height and spacing)
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = title,
                fontSize = 13.5.sp,
                fontWeight = FontWeight.SemiBold,
                color = if (isDestructive) Error else TextPrimary,
                lineHeight = 17.sp
            )
            if (!subtitle.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    fontSize = 11.5.sp,
                    color = TextSecondary,
                    lineHeight = 15.sp
                )
            }
        }

        Spacer(modifier = Modifier.width(8.dp))

        // Trailing Content
        when (trailingType) {
            MenuTrailingType.Chevron -> {
                Icon(
                    imageVector = Lucide.ChevronRight,
                    contentDescription = null,
                    tint = TextMuted,
                    modifier = Modifier.size(18.dp)
                )
            }

            MenuTrailingType.Badge -> {
                if (badgeText != null) {
                    Badge(
                        text = badgeText,
                        variant = badgeVariant,
                        size = BadgeSize.SM,
                        modifier = Modifier.wrapContentWidth()
                    )
                }
            }

            MenuTrailingType.Value -> {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (valueText != null) {
                        Text(
                            text = valueText,
                            fontSize = 12.5.sp,
                            color = TextSecondary,
                            lineHeight = 16.sp
                        )
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = Lucide.ChevronRight,
                        contentDescription = null,
                        tint = TextMuted,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            MenuTrailingType.Switch -> {
                Switch(
                    checked = switchChecked,
                    onCheckedChange = { onSwitchChange?.invoke(it) }
                )
            }

            MenuTrailingType.None -> {}
        }
    }
}

@Composable
fun MenuGroupCard(
    items: List<MenuItemData>,
    modifier: Modifier = Modifier,
    headerTitle: String? = null,
    onItemClick: ((MenuItemData) -> Unit)? = null
) {
    Column(modifier = modifier.fillMaxWidth()) {
        if (!headerTitle.isNullOrBlank()) {
            Text(
                text = headerTitle,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = TextSecondary,
                lineHeight = 16.sp,
                modifier = Modifier.padding(horizontal = 4.dp, vertical = 6.dp)
            )
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Surface),
            border = BorderStroke(1.dp, Border)
        ) {
            Column(modifier = Modifier.padding(vertical = 4.dp)) {
                items.forEachIndexed { index, item ->
                    MenuListItem(
                        title = item.title,
                        icon = item.icon,
                        subtitle = item.subtitle,
                        trailingType = item.trailingType,
                        badgeText = item.badgeText,
                        badgeVariant = item.badgeVariant,
                        valueText = item.valueText,
                        switchChecked = item.switchChecked,
                        onSwitchChange = item.onSwitchChange,
                        isDestructive = item.isDestructive,
                        iconTint = item.iconTint,
                        iconBackground = item.iconBackground,
                        onClick = if (onItemClick != null) {
                            { onItemClick(item) }
                        } else null
                    )

                    if (index < items.lastIndex) {
                        HorizontalDivider(
                            color = Border,
                            thickness = 0.8.dp,
                            modifier = Modifier.padding(horizontal = 14.dp)
                        )
                    }
                }
            }
        }
    }
}
