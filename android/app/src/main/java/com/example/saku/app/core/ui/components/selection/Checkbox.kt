package com.example.saku.app.core.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.composables.icons.lucide.Check
import com.composables.icons.lucide.CircleAlert
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Minus
import com.example.saku.app.ui.theme.Border
import com.example.saku.app.ui.theme.Error
import com.example.saku.app.ui.theme.Neutral0
import com.example.saku.app.ui.theme.Neutral20
import com.example.saku.app.ui.theme.Neutral30
import com.example.saku.app.ui.theme.Neutral40
import com.example.saku.app.ui.theme.Primary
import com.example.saku.app.ui.theme.Surface
import com.example.saku.app.ui.theme.TextMuted
import com.example.saku.app.ui.theme.TextPrimary
import com.example.saku.app.ui.theme.TextSecondary

@Composable
fun Checkbox(
    checked: Boolean,
    onCheckedChange: ((Boolean) -> Unit)?,
    modifier: Modifier = Modifier,
    indeterminate: Boolean = false,
    enabled: Boolean = true,
    isError: Boolean = false,
    size: Dp = 20.dp
) {
    val shape = RoundedCornerShape(6.dp)

    val isFilled = checked || indeterminate
    val backgroundColor by animateColorAsState(
        targetValue = when {
            !enabled -> Neutral0
            isFilled -> Primary
            else -> Surface
        },
        animationSpec = tween(durationMillis = 150),
        label = "checkbox_bg"
    )

    val borderColor by animateColorAsState(
        targetValue = when {
            !enabled -> Neutral20
            isError -> Error
            isFilled -> Primary
            else -> Neutral30
        },
        animationSpec = tween(durationMillis = 150),
        label = "checkbox_border"
    )

    val clickModifier = if (onCheckedChange != null && enabled) {
        Modifier.clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = ripple(bounded = false, radius = size),
            onClick = { onCheckedChange(!checked) }
        )
    } else Modifier

    Box(
        modifier = modifier
            .size(size)
            .clip(shape)
            .background(backgroundColor)
            .border(width = 1.5.dp, color = borderColor, shape = shape)
            .then(clickModifier),
        contentAlignment = Alignment.Center
    ) {
        if (indeterminate) {
            Icon(
                imageVector = Lucide.Minus,
                contentDescription = "Indeterminate",
                tint = if (enabled) Color.White else TextMuted,
                modifier = Modifier.size(size * 0.7f)
            )
        } else if (checked) {
            Icon(
                imageVector = Lucide.Check,
                contentDescription = "Checked",
                tint = if (enabled) Color.White else TextMuted,
                modifier = Modifier.size(size * 0.75f)
            )
        }
    }
}

@Composable
fun CheckboxWithLabel(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    description: String? = null,
    errorMessage: String? = null,
    indeterminate: Boolean = false,
    enabled: Boolean = true
) {
    val isError = !errorMessage.isNullOrBlank()

    Column(modifier = modifier) {
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .clickable(
                    enabled = enabled,
                    interactionSource = remember { MutableInteractionSource() },
                    indication = ripple(color = Primary.copy(alpha = 0.1f)),
                    onClick = { onCheckedChange(!checked) }
                )
                .padding(vertical = 4.dp),
            verticalAlignment = if (description != null) Alignment.Top else Alignment.CenterVertically
        ) {
            Box(modifier = Modifier.padding(top = if (description != null) 2.dp else 0.dp)) {
                Checkbox(
                    checked = checked,
                    onCheckedChange = null, // handled by Row click
                    indeterminate = indeterminate,
                    enabled = enabled,
                    isError = isError
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column {
                Text(
                    text = label,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = when {
                        !enabled -> TextMuted
                        isError -> Error
                        else -> TextPrimary
                    }
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

        if (isError && !errorMessage.isNullOrBlank()) {
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(start = 30.dp)
            ) {
                Icon(
                    imageVector = Lucide.CircleAlert,
                    contentDescription = "Error",
                    tint = Error,
                    modifier = Modifier.size(12.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = errorMessage,
                    fontSize = 12.sp,
                    color = Error
                )
            }
        }
    }
}
