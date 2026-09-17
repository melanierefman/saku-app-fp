package com.example.saku.app.core.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.saku.app.ui.theme.Border
import com.example.saku.app.ui.theme.Error
import com.example.saku.app.ui.theme.Neutral10
import com.example.saku.app.ui.theme.Neutral20
import com.example.saku.app.ui.theme.Neutral40
import com.example.saku.app.ui.theme.Neutral50
import com.example.saku.app.ui.theme.Primary
import com.example.saku.app.ui.theme.Primary0
import com.example.saku.app.ui.theme.Primary20
import com.example.saku.app.ui.theme.Primary60
import com.example.saku.app.ui.theme.Surface

enum class IconButtonVariant {
    Primary,
    Secondary,
    Outline,
    Ghost,
    White,
    Neutral
}

enum class IconButtonSize(val boxSize: Dp, val iconSize: Dp) {
    SM(boxSize = 32.dp, iconSize = 16.dp),
    MD(boxSize = 40.dp, iconSize = 20.dp),
    LG(boxSize = 48.dp, iconSize = 24.dp)
}

enum class IconButtonShape {
    Circle,
    Rounded // 10.dp
}

@Composable
fun IconButton(
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
    enabled: Boolean = true,
    variant: IconButtonVariant = IconButtonVariant.Ghost,
    size: IconButtonSize = IconButtonSize.MD,
    shape: IconButtonShape = IconButtonShape.Rounded,
    badgeCount: Int? = null
) {
    val cornerShape: Shape = when (shape) {
        IconButtonShape.Circle -> CircleShape
        IconButtonShape.Rounded -> RoundedCornerShape(10.dp)
    }

    val (containerColor, contentColor, borderStroke) = when (variant) {
        IconButtonVariant.Primary -> Triple(Primary, Color.White, null)
        IconButtonVariant.Secondary -> Triple(
            Primary0,
            Primary60,
            BorderStroke(1.dp, if (enabled) Primary20 else Primary20.copy(alpha = 0.5f))
        )
        IconButtonVariant.Outline -> Triple(
            Surface,
            Neutral50,
            BorderStroke(1.dp, if (enabled) Neutral20 else Neutral10)
        )
        IconButtonVariant.Ghost -> Triple(Color.Transparent, Neutral50, null)
        IconButtonVariant.White -> Triple(
            Color.White,
            Primary,
            BorderStroke(1.dp, if (enabled) Border else Border.copy(alpha = 0.5f))
        )
        IconButtonVariant.Neutral -> Triple(Neutral50, Color.White, null)
    }

    Box(
        modifier = modifier
            .size(size.boxSize)
            .clip(cornerShape)
            .background(if (enabled) containerColor else containerColor.copy(alpha = 0.5f))
            .then(
                if (borderStroke != null) {
                    Modifier.border(borderStroke, cornerShape)
                } else Modifier
            )
            .clickable(
                enabled = enabled,
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(bounded = true, color = contentColor.copy(alpha = 0.2f)),
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = if (enabled) contentColor else contentColor.copy(alpha = 0.5f),
            modifier = Modifier.size(size.iconSize)
        )

        // Notification Badge Overlay
        if (badgeCount != null && badgeCount > 0) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = 2.dp, y = (-2).dp)
                    .clip(CircleShape)
                    .background(Error)
                    .padding(horizontal = 4.dp, vertical = 1.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (badgeCount > 99) "99+" else badgeCount.toString(),
                    color = Color.White,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    lineHeight = 10.sp
                )
            }
        }
    }
}
