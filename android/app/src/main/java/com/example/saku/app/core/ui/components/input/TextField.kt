package com.example.saku.app.core.ui.components

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton as M3IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.composables.icons.lucide.CircleAlert
import com.composables.icons.lucide.Eye
import com.composables.icons.lucide.EyeOff
import com.composables.icons.lucide.Lucide
import com.example.saku.app.ui.theme.Border
import com.example.saku.app.ui.theme.Error
import com.example.saku.app.ui.theme.Neutral0
import com.example.saku.app.ui.theme.Neutral40
import com.example.saku.app.ui.theme.Neutral60
import com.example.saku.app.ui.theme.Primary
import com.example.saku.app.ui.theme.Surface
import com.example.saku.app.ui.theme.TextMuted
import com.example.saku.app.ui.theme.TextPrimary
import com.example.saku.app.ui.theme.TextSecondary

@Composable
fun TextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String? = null,
    required: Boolean = false,
    placeholder: String = "",
    helperText: String? = null,
    errorMessage: String? = null,
    leadingIcon: ImageVector? = null,
    trailingIcon: ImageVector? = null,
    onTrailingIconClick: (() -> Unit)? = null,
    prefixText: String? = null,
    suffixText: String? = null,
    isPassword: Boolean = false,
    isError: Boolean = !errorMessage.isNullOrBlank(),
    enabled: Boolean = true,
    readOnly: Boolean = false,
    singleLine: Boolean = true,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default
) {
    var passwordVisible by remember { mutableStateOf(false) }

    val effectiveVisualTransformation = when {
        isPassword && !passwordVisible -> PasswordVisualTransformation()
        else -> visualTransformation
    }

    Column(modifier = modifier.fillMaxWidth()) {
        // Label with Optional Required Asterisk
        if (!label.isNullOrBlank()) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 6.dp)
            ) {
                Text(
                    text = label,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = if (isError) Error else Neutral60
                )
                if (required) {
                    Text(
                        text = " *",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Error
                    )
                }
            }
        }

        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            enabled = enabled,
            readOnly = readOnly,
            singleLine = singleLine,
            textStyle = TextStyle(
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal,
                color = if (enabled) TextPrimary else TextMuted
            ),
            placeholder = {
                if (placeholder.isNotBlank()) {
                    Text(
                        text = placeholder,
                        fontSize = 14.sp,
                        color = TextMuted
                    )
                }
            },
            leadingIcon = if (leadingIcon != null || !prefixText.isNullOrBlank()) {
                {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(start = 12.dp)
                    ) {
                        if (leadingIcon != null) {
                            Icon(
                                imageVector = leadingIcon,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp),
                                tint = if (isError) Error else Neutral40
                            )
                        }
                        if (!prefixText.isNullOrBlank()) {
                            if (leadingIcon != null) Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = prefixText,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = Neutral40
                            )
                        }
                    }
                }
            } else null,
            trailingIcon = when {
                isPassword -> {
                    {
                        M3IconButton(
                            onClick = { passwordVisible = !passwordVisible },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector = if (passwordVisible) Lucide.EyeOff else Lucide.Eye,
                                contentDescription = if (passwordVisible) "Sembunyikan password" else "Lihat password",
                                modifier = Modifier.size(18.dp),
                                tint = Neutral40
                            )
                        }
                    }
                }
                trailingIcon != null -> {
                    {
                        if (onTrailingIconClick != null) {
                            M3IconButton(
                                onClick = onTrailingIconClick,
                                modifier = Modifier.size(36.dp)
                            ) {
                                Icon(
                                    imageVector = trailingIcon,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp),
                                    tint = Neutral40
                                )
                            }
                        } else {
                            Icon(
                                imageVector = trailingIcon,
                                contentDescription = null,
                                modifier = Modifier
                                    .padding(end = 12.dp)
                                    .size(18.dp),
                                tint = Neutral40
                            )
                        }
                    }
                }
                !suffixText.isNullOrBlank() -> {
                    {
                        Text(
                            text = suffixText,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = Neutral40,
                            modifier = Modifier.padding(end = 14.dp)
                        )
                    }
                }
                else -> null
            },
            visualTransformation = effectiveVisualTransformation,
            isError = isError,
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions,
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Primary,
                unfocusedBorderColor = Border,
                errorBorderColor = Error,
                disabledBorderColor = Border.copy(alpha = 0.6f),
                focusedContainerColor = Surface,
                unfocusedContainerColor = if (value.isNotBlank()) Surface else Color(0xFFF9FAFB),
                disabledContainerColor = Color(0xFFF9FAFB),
                errorContainerColor = Surface,
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary,
                disabledTextColor = TextMuted,
                cursorColor = Primary,
                errorCursorColor = Error
            )
        )

        // Error Message or Helper Text
        if (isError && !errorMessage.isNullOrBlank()) {
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                verticalAlignment = Alignment.Top,
                modifier = Modifier.padding(start = 2.dp)
            ) {
                Icon(
                    imageVector = Lucide.CircleAlert,
                    contentDescription = "Error",
                    modifier = Modifier
                        .size(14.dp)
                        .padding(top = 1.dp),
                    tint = Error
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = errorMessage,
                    fontSize = 12.sp,
                    color = Error,
                    lineHeight = 16.sp
                )
            }
        } else if (!helperText.isNullOrBlank()) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = helperText,
                fontSize = 12.sp,
                color = Neutral40,
                modifier = Modifier.padding(start = 2.dp)
            )
        }
    }
}
