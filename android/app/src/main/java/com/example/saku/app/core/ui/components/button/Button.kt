package com.example.saku.app.core.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button as M3Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.composables.icons.lucide.Lucide
import com.example.saku.app.ui.theme.Border
import com.example.saku.app.ui.theme.Error
import com.example.saku.app.ui.theme.Error60
import com.example.saku.app.ui.theme.Error70
import com.example.saku.app.ui.theme.Neutral0
import com.example.saku.app.ui.theme.Neutral10
import com.example.saku.app.ui.theme.Neutral20
import com.example.saku.app.ui.theme.Neutral40
import com.example.saku.app.ui.theme.Neutral50
import com.example.saku.app.ui.theme.Neutral60
import com.example.saku.app.ui.theme.Neutral70
import com.example.saku.app.ui.theme.Primary
import com.example.saku.app.ui.theme.Primary0
import com.example.saku.app.ui.theme.Primary10
import com.example.saku.app.ui.theme.Primary20
import com.example.saku.app.ui.theme.Primary60
import com.example.saku.app.ui.theme.Primary70
import com.example.saku.app.ui.theme.Success
import com.example.saku.app.ui.theme.Success60
import com.example.saku.app.ui.theme.Success70
import com.example.saku.app.ui.theme.Warning
import com.example.saku.app.ui.theme.Warning60
import com.example.saku.app.ui.theme.Warning70

enum class ButtonVariant {
    Primary,
    Secondary,
    White,
    Neutral,
    Outline,
    Cancel,
    Ghost,
    Success,
    Warning,
    Error
}

enum class ButtonSize(
    val height: Dp,
    val fontSize: TextUnit,
    val horizontalPadding: Dp,
    val iconSize: Dp,
    val gap: Dp
) {
    SM(height = 36.dp, fontSize = 12.sp, horizontalPadding = 12.dp, iconSize = 16.dp, gap = 6.dp),
    MD(height = 44.dp, fontSize = 14.sp, horizontalPadding = 16.dp, iconSize = 18.dp, gap = 8.dp),
    LG(height = 52.dp, fontSize = 15.sp, horizontalPadding = 20.dp, iconSize = 20.dp, gap = 8.dp)
}

enum class ButtonShape {
    Rounded, // 12.dp
    Pill,    // CircleShape
    Square   // 4.dp
}

@Composable
fun Button(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isLoading: Boolean = false,
    variant: ButtonVariant = ButtonVariant.Primary,
    size: ButtonSize = ButtonSize.LG,
    shape: ButtonShape = ButtonShape.Rounded,
    fullWidth: Boolean = true,
    leadingIcon: ImageVector? = null,
    trailingIcon: ImageVector? = null
) {
    val cornerShape: Shape = when (shape) {
        ButtonShape.Rounded -> RoundedCornerShape(12.dp)
        ButtonShape.Pill -> CircleShape
        ButtonShape.Square -> RoundedCornerShape(4.dp)
    }

    val baseModifier = if (fullWidth) {
        modifier
            .fillMaxWidth()
            .height(size.height)
    } else {
        modifier.height(size.height)
    }

    val (containerColor, contentColor, borderStroke) = getButtonColorsAndBorder(variant, enabled)

    M3Button(
        onClick = onClick,
        modifier = baseModifier,
        enabled = enabled && !isLoading,
        shape = cornerShape,
        border = borderStroke,
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = contentColor,
            disabledContainerColor = containerColor.copy(alpha = 0.55f),
            disabledContentColor = contentColor.copy(alpha = 0.6f)
        ),
        contentPadding = PaddingValues(horizontal = size.horizontalPadding, vertical = 0.dp),
        elevation = if (variant == ButtonVariant.Ghost || !enabled) null else ButtonDefaults.buttonElevation(
            defaultElevation = 0.5.dp,
            pressedElevation = 0.dp
        )
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(size.iconSize),
                color = contentColor,
                strokeWidth = 2.dp
            )
            Spacer(modifier = Modifier.width(size.gap))
        } else if (leadingIcon != null) {
            Icon(
                imageVector = leadingIcon,
                contentDescription = null,
                modifier = Modifier.size(size.iconSize),
                tint = contentColor
            )
            Spacer(modifier = Modifier.width(size.gap))
        }

        Text(
            text = text,
            fontWeight = FontWeight.SemiBold,
            fontSize = size.fontSize,
            color = contentColor
        )

        if (!isLoading && trailingIcon != null) {
            Spacer(modifier = Modifier.width(size.gap))
            Icon(
                imageVector = trailingIcon,
                contentDescription = null,
                modifier = Modifier.size(size.iconSize),
                tint = contentColor
            )
        }
    }
}

private data class ButtonStyleProps(
    val containerColor: Color,
    val contentColor: Color,
    val border: BorderStroke?
)

private fun getButtonColorsAndBorder(
    variant: ButtonVariant,
    enabled: Boolean
): ButtonStyleProps {
    return when (variant) {
        ButtonVariant.Primary -> ButtonStyleProps(
            containerColor = Primary,
            contentColor = Color.White,
            border = null
        )
        ButtonVariant.Secondary -> ButtonStyleProps(
            containerColor = Primary0,
            contentColor = Primary60,
            border = BorderStroke(1.dp, if (enabled) Primary20 else Primary20.copy(alpha = 0.5f))
        )
        ButtonVariant.White -> ButtonStyleProps(
            containerColor = Color.White,
            contentColor = Primary,
            border = BorderStroke(1.dp, if (enabled) Border else Border.copy(alpha = 0.5f))
        )
        ButtonVariant.Neutral -> ButtonStyleProps(
            containerColor = Neutral50,
            contentColor = Color.White,
            border = null
        )
        ButtonVariant.Outline -> ButtonStyleProps(
            containerColor = Color.White,
            contentColor = Neutral50,
            border = BorderStroke(1.dp, if (enabled) Neutral20 else Neutral10)
        )
        ButtonVariant.Cancel -> ButtonStyleProps(
            containerColor = Color.White,
            contentColor = Neutral40,
            border = BorderStroke(1.dp, if (enabled) Border else Border.copy(alpha = 0.5f))
        )
        ButtonVariant.Ghost -> ButtonStyleProps(
            containerColor = Color.Transparent,
            contentColor = Neutral50,
            border = null
        )
        ButtonVariant.Success -> ButtonStyleProps(
            containerColor = Success,
            contentColor = Color.White,
            border = null
        )
        ButtonVariant.Warning -> ButtonStyleProps(
            containerColor = Warning,
            contentColor = Color.White,
            border = null
        )
        ButtonVariant.Error -> ButtonStyleProps(
            containerColor = Error,
            contentColor = Color.White,
            border = null
        )
    }
}
