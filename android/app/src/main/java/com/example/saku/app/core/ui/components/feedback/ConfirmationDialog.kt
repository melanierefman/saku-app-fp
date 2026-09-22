package com.example.saku.app.core.ui.components

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.composables.icons.lucide.CircleAlert
import com.composables.icons.lucide.CircleCheck
import com.composables.icons.lucide.Info
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Trash2
import com.example.saku.app.ui.theme.Border
import com.example.saku.app.ui.theme.Error
import com.example.saku.app.ui.theme.Error0
import com.example.saku.app.ui.theme.Info
import com.example.saku.app.ui.theme.Primary
import com.example.saku.app.ui.theme.Primary0
import com.example.saku.app.ui.theme.Success
import com.example.saku.app.ui.theme.Success0
import com.example.saku.app.ui.theme.Surface
import com.example.saku.app.ui.theme.TextPrimary
import com.example.saku.app.ui.theme.TextSecondary
import com.example.saku.app.ui.theme.Warning
import com.example.saku.app.ui.theme.Warning0

enum class DialogType {
    WARNING,
    DESTRUCTIVE,
    INFO,
    SUCCESS
}

@Composable
fun ConfirmationDialog(
    visible: Boolean,
    title: String,
    message: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    type: DialogType = DialogType.WARNING,
    icon: ImageVector? = null,
    confirmButtonText: String = "Konfirmasi",
    dismissButtonText: String = "Batal",
    confirmButtonVariant: ButtonVariant? = null,
    isLoading: Boolean = false
) {
    if (visible) {
        val (bubbleBg, iconTint, defaultIcon, defaultConfirmVariant) = when (type) {
            DialogType.WARNING -> Tuple4(Warning0, Warning, Lucide.CircleAlert, ButtonVariant.Warning)
            DialogType.DESTRUCTIVE -> Tuple4(Error0, Error, Lucide.Trash2, ButtonVariant.Error)
            DialogType.INFO -> Tuple4(Primary0, Primary, Lucide.Info, ButtonVariant.Primary)
            DialogType.SUCCESS -> Tuple4(Success0, Success, Lucide.CircleCheck, ButtonVariant.Success)
        }

        val displayIcon = icon ?: defaultIcon
        val finalConfirmVariant = confirmButtonVariant ?: defaultConfirmVariant

        Dialog(
            onDismissRequest = onDismiss,
            properties = DialogProperties(dismissOnBackPress = true, dismissOnClickOutside = true)
        ) {
            Card(
                modifier = modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Surface),
                border = BorderStroke(1.dp, Border)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Header Icon Bubble
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(CircleShape)
                            .background(bubbleBg),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = displayIcon,
                            contentDescription = null,
                            tint = iconTint,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

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

                    // Message
                    Text(
                        text = message,
                        fontSize = 12.5.sp,
                        color = TextSecondary,
                        textAlign = TextAlign.Center,
                        lineHeight = 17.sp
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // Action Buttons Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            text = dismissButtonText,
                            onClick = onDismiss,
                            variant = ButtonVariant.Outline,
                            size = ButtonSize.SM,
                            modifier = Modifier.weight(1f)
                        )

                        Button(
                            text = confirmButtonText,
                            onClick = onConfirm,
                            variant = finalConfirmVariant,
                            size = ButtonSize.SM,
                            isLoading = isLoading,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }
}

private data class Tuple4<A, B, C, D>(
    val a: A,
    val b: B,
    val c: C,
    val d: D
)