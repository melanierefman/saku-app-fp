package com.example.saku.app.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.composables.icons.lucide.Inbox
import com.composables.icons.lucide.Lucide
import com.example.saku.app.ui.theme.Neutral0
import com.example.saku.app.ui.theme.Neutral40
import com.example.saku.app.ui.theme.Primary
import com.example.saku.app.ui.theme.Primary0
import com.example.saku.app.ui.theme.TextPrimary
import com.example.saku.app.ui.theme.TextSecondary

@Composable
fun EmptyStateView(
    title: String,
    description: String,
    modifier: Modifier = Modifier,
    icon: ImageVector = Lucide.Inbox,
    iconContainerSize: Dp = 64.dp,
    iconSize: Dp = 28.dp,
    iconBackgroundColor: Color = Primary0,
    iconTint: Color = Primary,
    actionButtonText: String? = null,
    onActionClick: (() -> Unit)? = null,
    secondaryButtonText: String? = null,
    onSecondaryClick: (() -> Unit)? = null
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Icon Bubble
        Box(
            modifier = Modifier
                .size(iconContainerSize)
                .clip(CircleShape)
                .background(iconBackgroundColor),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(iconSize)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Title
        Text(
            text = title,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary,
            textAlign = TextAlign.Center,
            lineHeight = 22.sp
        )

        Spacer(modifier = Modifier.height(4.dp))

        // Description with tight line height
        Text(
            text = description,
            fontSize = 12.5.sp,
            color = TextSecondary,
            textAlign = TextAlign.Center,
            lineHeight = 17.sp
        )

        // Action Buttons
        if (actionButtonText != null && onActionClick != null) {
            Spacer(modifier = Modifier.height(20.dp))
            Button(
                text = actionButtonText,
                onClick = onActionClick,
                variant = ButtonVariant.Primary,
                size = ButtonSize.SM,
                fullWidth = false
            )
        }

        if (secondaryButtonText != null && onSecondaryClick != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                text = secondaryButtonText,
                onClick = onSecondaryClick,
                variant = ButtonVariant.Ghost,
                size = ButtonSize.SM,
                fullWidth = false
            )
        }
    }
}
