package com.example.saku.app.core.ui.components

import androidx.compose.foundation.background
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.composables.icons.lucide.CircleAlert
import com.composables.icons.lucide.Headphones
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.RotateCw
import com.composables.icons.lucide.WifiOff
import com.example.saku.app.ui.theme.Error
import com.example.saku.app.ui.theme.Error0
import com.example.saku.app.ui.theme.Error70
import com.example.saku.app.ui.theme.TextPrimary
import com.example.saku.app.ui.theme.TextSecondary

enum class ErrorCategory {
    NETWORK_ERROR,
    SERVER_ERROR,
    SECURITY_ERROR,
    GENERIC
}

@Composable
fun ErrorStateView(
    title: String,
    description: String,
    modifier: Modifier = Modifier,
    category: ErrorCategory = ErrorCategory.GENERIC,
    errorCode: String? = null,
    icon: ImageVector? = null,
    retryButtonText: String = "Coba Lagi",
    onRetryClick: (() -> Unit)? = null,
    isRetrying: Boolean = false,
    helpButtonText: String? = "Hubungi Bantuan",
    onHelpClick: (() -> Unit)? = null
) {
    val defaultIcon = when (category) {
        ErrorCategory.NETWORK_ERROR -> Lucide.WifiOff
        ErrorCategory.SERVER_ERROR -> Lucide.CircleAlert
        ErrorCategory.SECURITY_ERROR -> Lucide.CircleAlert
        ErrorCategory.GENERIC -> Lucide.CircleAlert
    }

    val displayIcon = icon ?: defaultIcon

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Error Bubble
        Box(
            modifier = Modifier
                .size(72.dp)
                .clip(CircleShape)
                .background(Error0),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = displayIcon,
                contentDescription = null,
                tint = Error,
                modifier = Modifier.size(32.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Optional Error Code Badge
        if (!errorCode.isNullOrBlank()) {
            Badge(
                text = errorCode,
                variant = BadgeVariant.Error,
                size = BadgeSize.SM,
                shape = BadgeShape.Pill
            )
            Spacer(modifier = Modifier.height(10.dp))
        }

        // Title
        Text(
            text = title,
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary,
            textAlign = TextAlign.Center,
            lineHeight = 22.sp
        )

        Spacer(modifier = Modifier.height(4.dp))

        // Description
        Text(
            text = description,
            fontSize = 12.5.sp,
            color = TextSecondary,
            textAlign = TextAlign.Center,
            lineHeight = 17.sp
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Action Buttons
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (onRetryClick != null) {
                Button(
                    text = retryButtonText,
                    onClick = onRetryClick,
                    isLoading = isRetrying,
                    variant = ButtonVariant.Primary,
                    size = ButtonSize.SM,
                    leadingIcon = Lucide.RotateCw,
                    fullWidth = false
                )
            }

            if (helpButtonText != null && onHelpClick != null) {
                Button(
                    text = helpButtonText,
                    onClick = onHelpClick,
                    variant = ButtonVariant.Ghost,
                    size = ButtonSize.SM,
                    leadingIcon = Lucide.Headphones,
                    fullWidth = false
                )
            }
        }
    }
}
