package com.example.saku.app.core.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.X
import com.example.saku.app.ui.theme.Border
import com.example.saku.app.ui.theme.Cyan
import com.example.saku.app.ui.theme.Cyan0
import com.example.saku.app.ui.theme.Cyan20
import com.example.saku.app.ui.theme.Cyan70
import com.example.saku.app.ui.theme.Error
import com.example.saku.app.ui.theme.Error0
import com.example.saku.app.ui.theme.Error20
import com.example.saku.app.ui.theme.Error70
import com.example.saku.app.ui.theme.Indigo
import com.example.saku.app.ui.theme.Indigo0
import com.example.saku.app.ui.theme.Indigo20
import com.example.saku.app.ui.theme.Indigo70
import com.example.saku.app.ui.theme.Info
import com.example.saku.app.ui.theme.Info0
import com.example.saku.app.ui.theme.Info20
import com.example.saku.app.ui.theme.Info70
import com.example.saku.app.ui.theme.Neutral0
import com.example.saku.app.ui.theme.Neutral10
import com.example.saku.app.ui.theme.Neutral20
import com.example.saku.app.ui.theme.Neutral40
import com.example.saku.app.ui.theme.Neutral50
import com.example.saku.app.ui.theme.Orange
import com.example.saku.app.ui.theme.Orange0
import com.example.saku.app.ui.theme.Orange20
import com.example.saku.app.ui.theme.Orange70
import com.example.saku.app.ui.theme.Primary
import com.example.saku.app.ui.theme.Primary0
import com.example.saku.app.ui.theme.Primary20
import com.example.saku.app.ui.theme.Primary70
import com.example.saku.app.ui.theme.Purple
import com.example.saku.app.ui.theme.Purple0
import com.example.saku.app.ui.theme.Purple20
import com.example.saku.app.ui.theme.Purple70
import com.example.saku.app.ui.theme.Success
import com.example.saku.app.ui.theme.Success0
import com.example.saku.app.ui.theme.Success20
import com.example.saku.app.ui.theme.Success70
import com.example.saku.app.ui.theme.Warning
import com.example.saku.app.ui.theme.Warning0
import com.example.saku.app.ui.theme.Warning20
import com.example.saku.app.ui.theme.Warning80

enum class BadgeVariant {
    Primary,
    SolidPrimary,
    Neutral,
    SolidNeutral,
    Success,
    SolidSuccess,
    Warning,
    SolidWarning,
    Error,
    SolidError,
    Info,
    Purple,
    Indigo,
    Cyan,
    Orange,
    Outline
}

enum class BadgeSize(
    val fontSize: TextUnit,
    val paddingValues: PaddingValues,
    val dotSize: Dp,
    val iconSize: Dp
) {
    SM(fontSize = 11.sp, paddingValues = PaddingValues(horizontal = 7.dp, vertical = 2.dp), dotSize = 5.dp, iconSize = 12.dp),
    MD(fontSize = 12.sp, paddingValues = PaddingValues(horizontal = 9.dp, vertical = 3.dp), dotSize = 6.dp, iconSize = 14.dp),
    LG(fontSize = 13.sp, paddingValues = PaddingValues(horizontal = 12.dp, vertical = 4.dp), dotSize = 7.dp, iconSize = 16.dp)
}

enum class BadgeShape {
    Pill,    // CircleShape / Full rounded
    Rounded  // 6.dp
}

@Composable
fun Badge(
    text: String,
    modifier: Modifier = Modifier,
    variant: BadgeVariant = BadgeVariant.Neutral,
    size: BadgeSize = BadgeSize.MD,
    shape: BadgeShape = BadgeShape.Pill,
    dot: Boolean = false,
    dotPulse: Boolean = false,
    leadingIcon: ImageVector? = null,
    removable: Boolean = false,
    onRemove: (() -> Unit)? = null,
    onClick: (() -> Unit)? = null
) {
    val cornerShape: Shape = when (shape) {
        BadgeShape.Pill -> CircleShape
        BadgeShape.Rounded -> RoundedCornerShape(6.dp)
    }

    val props = getBadgeProps(variant)

    val clickModifier = if (onClick != null) {
        Modifier.clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = ripple(bounded = true, color = props.textColor.copy(alpha = 0.2f)),
            onClick = onClick
        )
    } else Modifier

    Row(
        modifier = modifier
            .clip(cornerShape)
            .background(props.backgroundColor)
            .then(
                if (props.borderColor != null) {
                    Modifier.border(1.dp, props.borderColor, cornerShape)
                } else Modifier
            )
            .then(clickModifier)
            .padding(size.paddingValues),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Dot indicator
        if (dot || dotPulse) {
            if (dotPulse) {
                val infiniteTransition = rememberInfiniteTransition(label = "badge_pulse")
                val pulseScale by infiniteTransition.animateFloat(
                    initialValue = 1f,
                    targetValue = 1.45f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(durationMillis = 900, easing = FastOutSlowInEasing),
                        repeatMode = RepeatMode.Reverse
                    ),
                    label = "badge_pulse_scale"
                )

                Box(
                    modifier = Modifier
                        .size(size.dotSize)
                        .scale(pulseScale)
                        .clip(CircleShape)
                        .background(props.dotColor)
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(size.dotSize)
                        .clip(CircleShape)
                        .background(props.dotColor)
                )
            }
            Spacer(modifier = Modifier.width(5.dp))
        }

        // Leading Icon
        if (leadingIcon != null) {
            Icon(
                imageVector = leadingIcon,
                contentDescription = null,
                tint = props.textColor,
                modifier = Modifier.size(size.iconSize)
            )
            Spacer(modifier = Modifier.width(4.dp))
        }

        // Text
        Text(
            text = text,
            fontSize = size.fontSize,
            fontWeight = FontWeight.SemiBold,
            color = props.textColor,
            lineHeight = size.fontSize
        )

        // Removable X Icon
        if (removable && onRemove != null) {
            Spacer(modifier = Modifier.width(4.dp))
            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = ripple(bounded = true, radius = 8.dp),
                        onClick = onRemove
                    )
                    .padding(1.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Lucide.X,
                    contentDescription = "Remove",
                    tint = props.textColor.copy(alpha = 0.8f),
                    modifier = Modifier.size(size.iconSize)
                )
            }
        }
    }
}

private data class BadgeProps(
    val backgroundColor: Color,
    val textColor: Color,
    val borderColor: Color?,
    val dotColor: Color
)

private fun getBadgeProps(variant: BadgeVariant): BadgeProps {
    return when (variant) {
        BadgeVariant.Primary -> BadgeProps(
            backgroundColor = Primary0,
            textColor = Primary70,
            borderColor = Primary20,
            dotColor = Primary
        )
        BadgeVariant.SolidPrimary -> BadgeProps(
            backgroundColor = Primary,
            textColor = Color.White,
            borderColor = null,
            dotColor = Color.White
        )
        BadgeVariant.Neutral -> BadgeProps(
            backgroundColor = Neutral0,
            textColor = Neutral50,
            borderColor = Neutral10,
            dotColor = Neutral40
        )
        BadgeVariant.SolidNeutral -> BadgeProps(
            backgroundColor = Neutral50,
            textColor = Color.White,
            borderColor = null,
            dotColor = Color.White
        )
        BadgeVariant.Success -> BadgeProps(
            backgroundColor = Success0,
            textColor = Success70,
            borderColor = Success20,
            dotColor = Success
        )
        BadgeVariant.SolidSuccess -> BadgeProps(
            backgroundColor = Success,
            textColor = Color.White,
            borderColor = null,
            dotColor = Color.White
        )
        BadgeVariant.Warning -> BadgeProps(
            backgroundColor = Warning0,
            textColor = Warning80,
            borderColor = Warning20,
            dotColor = Warning
        )
        BadgeVariant.SolidWarning -> BadgeProps(
            backgroundColor = Warning,
            textColor = Color.White,
            borderColor = null,
            dotColor = Color.White
        )
        BadgeVariant.Error -> BadgeProps(
            backgroundColor = Error0,
            textColor = Error70,
            borderColor = Error20,
            dotColor = Error
        )
        BadgeVariant.SolidError -> BadgeProps(
            backgroundColor = Error,
            textColor = Color.White,
            borderColor = null,
            dotColor = Color.White
        )
        BadgeVariant.Info -> BadgeProps(
            backgroundColor = Info0,
            textColor = Info70,
            borderColor = Info20,
            dotColor = Info
        )
        BadgeVariant.Purple -> BadgeProps(
            backgroundColor = Purple0,
            textColor = Purple70,
            borderColor = Purple20,
            dotColor = Purple
        )
        BadgeVariant.Indigo -> BadgeProps(
            backgroundColor = Indigo0,
            textColor = Indigo70,
            borderColor = Indigo20,
            dotColor = Indigo
        )
        BadgeVariant.Cyan -> BadgeProps(
            backgroundColor = Cyan0,
            textColor = Cyan70,
            borderColor = Cyan20,
            dotColor = Cyan
        )
        BadgeVariant.Orange -> BadgeProps(
            backgroundColor = Orange0,
            textColor = Orange70,
            borderColor = Orange20,
            dotColor = Orange
        )
        BadgeVariant.Outline -> BadgeProps(
            backgroundColor = Color.White,
            textColor = Neutral50,
            borderColor = Border,
            dotColor = Neutral40
        )
    }
}
