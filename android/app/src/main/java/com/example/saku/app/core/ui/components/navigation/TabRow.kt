package com.example.saku.app.core.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.saku.app.ui.theme.Border
import com.example.saku.app.ui.theme.Neutral0
import com.example.saku.app.ui.theme.Neutral10
import com.example.saku.app.ui.theme.Neutral40
import com.example.saku.app.ui.theme.Neutral60
import com.example.saku.app.ui.theme.Primary
import com.example.saku.app.ui.theme.Primary0
import com.example.saku.app.ui.theme.Primary60
import com.example.saku.app.ui.theme.Surface
import com.example.saku.app.ui.theme.TextMuted
import com.example.saku.app.ui.theme.TextSecondary
import androidx.compose.runtime.getValue

enum class TabVariant {
    Segmented,
    Underline,
    Pill,
    Folder
}

data class TabItem(
    val key: String,
    val label: String,
    val count: Int? = null,
    val badge: String? = null,
    val icon: ImageVector? = null,
    val disabled: Boolean = false
)

@Composable
fun TabRow(
    items: List<TabItem>,
    selectedKey: String,
    onTabSelected: (TabItem) -> Unit,
    modifier: Modifier = Modifier,
    variant: TabVariant = TabVariant.Segmented,
    isScrollable: Boolean = false
) {
    when (variant) {
        TabVariant.Segmented -> SegmentedTabRow(
            items = items,
            selectedKey = selectedKey,
            onTabSelected = onTabSelected,
            modifier = modifier,
            isScrollable = isScrollable
        )
        TabVariant.Underline -> UnderlineTabRow(
            items = items,
            selectedKey = selectedKey,
            onTabSelected = onTabSelected,
            modifier = modifier,
            isScrollable = isScrollable
        )
        TabVariant.Pill -> PillTabRow(
            items = items,
            selectedKey = selectedKey,
            onTabSelected = onTabSelected,
            modifier = modifier,
            isScrollable = isScrollable
        )
        TabVariant.Folder -> FolderTabRow(
            items = items,
            selectedKey = selectedKey,
            onTabSelected = onTabSelected,
            modifier = modifier,
            isScrollable = isScrollable
        )
    }
}

// 1. Segmented Tabs
@Composable
private fun SegmentedTabRow(
    items: List<TabItem>,
    selectedKey: String,
    onTabSelected: (TabItem) -> Unit,
    modifier: Modifier = Modifier,
    isScrollable: Boolean = false
) {
    val scrollModifier = if (isScrollable) Modifier.horizontalScroll(rememberScrollState()) else Modifier.fillMaxWidth()

    Box(
        modifier = modifier
            .then(scrollModifier)
            .clip(RoundedCornerShape(12.dp))
            .background(Neutral0)
            .border(1.dp, Border, RoundedCornerShape(12.dp))
            .padding(4.dp)
    ) {
        Row(
            modifier = if (!isScrollable) Modifier.fillMaxWidth() else Modifier,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            items.forEach { item ->
                val isSelected = item.key == selectedKey
                val itemModifier = if (!isScrollable) Modifier.weight(1f) else Modifier

                val bgColor by animateColorAsState(
                    targetValue = if (isSelected) Surface else Color.Transparent,
                    label = "segmented_bg"
                )

                val textColor by animateColorAsState(
                    targetValue = when {
                        item.disabled -> TextMuted
                        isSelected -> Primary
                        else -> TextSecondary
                    },
                    label = "segmented_text"
                )

                Box(
                    modifier = itemModifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(bgColor)
                        .then(
                            if (isSelected) {
                                Modifier.shadow(elevation = 1.dp, shape = RoundedCornerShape(8.dp))
                            } else Modifier
                        )
                        .clickable(
                            enabled = !item.disabled,
                            interactionSource = remember { MutableInteractionSource() },
                            indication = ripple(color = Primary.copy(alpha = 0.1f)),
                            onClick = { onTabSelected(item) }
                        )
                        .padding(horizontal = 14.dp, vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        if (item.icon != null) {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = textColor
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                        }

                        Text(
                            text = item.label,
                            fontSize = 13.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = textColor,
                            maxLines = 1,
                            softWrap = false
                        )

                        if (item.count != null) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .background(if (isSelected) Primary0 else Neutral10)
                                    .padding(horizontal = 6.dp, vertical = 1.dp)
                            ) {
                                Text(
                                    text = item.count.toString(),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) Primary60 else Neutral40
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// 2. Underline Tabs
@Composable
private fun UnderlineTabRow(
    items: List<TabItem>,
    selectedKey: String,
    onTabSelected: (TabItem) -> Unit,
    modifier: Modifier = Modifier,
    isScrollable: Boolean = false
) {
    val scrollModifier = if (isScrollable) Modifier.horizontalScroll(rememberScrollState()) else Modifier.fillMaxWidth()

    Column(modifier = modifier.then(scrollModifier)) {
        Row(
            modifier = if (!isScrollable) Modifier.fillMaxWidth() else Modifier,
            horizontalArrangement = if (isScrollable) Arrangement.spacedBy(20.dp) else Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            items.forEach { item ->
                val isSelected = item.key == selectedKey
                val itemModifier = if (!isScrollable) Modifier.weight(1f) else Modifier

                Column(
                    modifier = itemModifier
                        .clickable(
                            enabled = !item.disabled,
                            interactionSource = remember { MutableInteractionSource() },
                            indication = ripple(bounded = true, color = Primary.copy(alpha = 0.1f)),
                            onClick = { onTabSelected(item) }
                        )
                        .padding(horizontal = 8.dp, vertical = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        if (item.icon != null) {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = if (isSelected) Primary else Neutral40
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                        }

                        Text(
                            text = item.label,
                            fontSize = 13.5.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = when {
                                item.disabled -> TextMuted
                                isSelected -> Primary
                                else -> TextSecondary
                            },
                            maxLines = 1,
                            softWrap = false
                        )

                        if (item.count != null) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Badge(
                                text = item.count.toString(),
                                variant = if (isSelected) BadgeVariant.Primary else BadgeVariant.Neutral,
                                size = BadgeSize.SM
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Active Underline
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(if (!isScrollable) 0.8f else 1f)
                            .height(2.5.dp)
                            .clip(RoundedCornerShape(1.5.dp))
                            .background(if (isSelected) Primary else Color.Transparent)
                    )
                }
            }
        }
        HorizontalDivider(color = Border, thickness = 1.dp)
    }
}

// 3. Pill Tabs
@Composable
private fun PillTabRow(
    items: List<TabItem>,
    selectedKey: String,
    onTabSelected: (TabItem) -> Unit,
    modifier: Modifier = Modifier,
    isScrollable: Boolean = true
) {
    val scrollModifier = if (isScrollable) Modifier.horizontalScroll(rememberScrollState()) else Modifier.fillMaxWidth()

    Row(
        modifier = modifier.then(scrollModifier),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        items.forEach { item ->
            val isSelected = item.key == selectedKey

            val bgColor by animateColorAsState(
                targetValue = if (isSelected) Primary else Neutral0,
                label = "pill_bg"
            )

            val textColor by animateColorAsState(
                targetValue = when {
                    item.disabled -> TextMuted
                    isSelected -> Color.White
                    else -> Neutral60
                },
                label = "pill_text"
            )

            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(bgColor)
                    .border(
                        width = 1.dp,
                        color = if (isSelected) Primary else Border,
                        shape = CircleShape
                    )
                    .clickable(
                        enabled = !item.disabled,
                        interactionSource = remember { MutableInteractionSource() },
                        indication = ripple(color = Color.White.copy(alpha = 0.2f)),
                        onClick = { onTabSelected(item) }
                    )
                    .padding(horizontal = 14.dp, vertical = 6.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (item.icon != null) {
                        Icon(
                            imageVector = item.icon,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp),
                            tint = textColor
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                    }

                    Text(
                        text = item.label,
                        fontSize = 12.5.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        color = textColor,
                        maxLines = 1,
                        softWrap = false
                    )

                    if (item.count != null) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(if (isSelected) Color.White.copy(alpha = 0.25f) else Neutral10)
                                .padding(horizontal = 6.dp, vertical = 1.dp)
                        ) {
                            Text(
                                text = item.count.toString(),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) Color.White else Neutral40
                            )
                        }
                    }
                }
            }
        }
    }
}

// 4. Folder Tabs
@Composable
private fun FolderTabRow(
    items: List<TabItem>,
    selectedKey: String,
    onTabSelected: (TabItem) -> Unit,
    modifier: Modifier = Modifier,
    isScrollable: Boolean = false
) {
    val scrollModifier = if (isScrollable) Modifier.horizontalScroll(rememberScrollState()) else Modifier.fillMaxWidth()

    Column(modifier = modifier.then(scrollModifier)) {
        Row(
            modifier = if (!isScrollable) Modifier.fillMaxWidth() else Modifier,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            items.forEach { item ->
                val isSelected = item.key == selectedKey
                val itemModifier = if (!isScrollable) Modifier.weight(1f) else Modifier

                Box(
                    modifier = itemModifier
                        .clip(RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp))
                        .background(if (isSelected) Neutral0 else Color.Transparent)
                        .clickable(
                            enabled = !item.disabled,
                            interactionSource = remember { MutableInteractionSource() },
                            indication = ripple(color = Primary.copy(alpha = 0.1f)),
                            onClick = { onTabSelected(item) }
                        )
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = item.label,
                        fontSize = 13.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        color = if (isSelected) Primary else TextSecondary,
                        maxLines = 1,
                        softWrap = false
                    )
                }
            }
        }
        HorizontalDivider(color = Border, thickness = 1.dp)
    }
}