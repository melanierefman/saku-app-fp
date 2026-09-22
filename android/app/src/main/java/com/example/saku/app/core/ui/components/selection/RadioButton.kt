package com.example.saku.app.core.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.saku.app.ui.theme.Neutral0
import com.example.saku.app.ui.theme.Neutral20
import com.example.saku.app.ui.theme.Neutral30
import com.example.saku.app.ui.theme.Primary
import com.example.saku.app.ui.theme.Surface
import com.example.saku.app.ui.theme.TextMuted
import com.example.saku.app.ui.theme.TextPrimary
import com.example.saku.app.ui.theme.TextSecondary
import androidx.compose.runtime.getValue

@Composable
fun RadioButton(
    selected: Boolean,
    onClick: (() -> Unit)?,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    size: Dp = 20.dp
) {
    val borderColor by animateColorAsState(
        targetValue = when {
            !enabled -> Neutral20
            selected -> Primary
            else -> Neutral30
        },
        animationSpec = tween(durationMillis = 150),
        label = "radio_border"
    )

    val bgColor by animateColorAsState(
        targetValue = when {
            !enabled -> Neutral0
            else -> Surface
        },
        animationSpec = tween(durationMillis = 150),
        label = "radio_bg"
    )

    val clickModifier = if (onClick != null && enabled) {
        Modifier.clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = ripple(bounded = false, radius = size),
            onClick = onClick
        )
    } else Modifier

    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(bgColor)
            .border(
                width = if (selected) 2.dp else 1.5.dp,
                color = borderColor,
                shape = CircleShape
            )
            .then(clickModifier),
        contentAlignment = Alignment.Center
    ) {
        if (selected) {
            Box(
                modifier = Modifier
                    .size(size * 0.5f)
                    .clip(CircleShape)
                    .background(if (enabled) Primary else TextMuted)
            )
        }
    }
}

@Composable
fun RadioButtonWithLabel(
    selected: Boolean,
    onClick: () -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    description: String? = null,
    enabled: Boolean = true
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .clickable(
                enabled = enabled,
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(color = Primary.copy(alpha = 0.1f)),
                onClick = onClick
            )
            .padding(vertical = 4.dp),
        verticalAlignment = if (description != null) Alignment.Top else Alignment.CenterVertically
    ) {
        Box(modifier = Modifier.padding(top = if (description != null) 2.dp else 0.dp)) {
            RadioButton(
                selected = selected,
                onClick = null, // handled by parent Row
                enabled = enabled
            )
        }

        Spacer(modifier = Modifier.width(10.dp))

        Column {
            Text(
                text = label,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = if (enabled) TextPrimary else TextMuted
            )

            if (!description.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = description,
                    fontSize = 12.sp,
                    color = if (enabled) TextSecondary else TextMuted,
                    lineHeight = 16.sp
                )
            }
        }
    }
}