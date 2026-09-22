package com.example.saku.app.core.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.composables.icons.lucide.CircleAlert
import com.composables.icons.lucide.Lucide
import com.example.saku.app.ui.theme.Border
import com.example.saku.app.ui.theme.Error
import com.example.saku.app.ui.theme.Neutral0
import com.example.saku.app.ui.theme.Primary
import com.example.saku.app.ui.theme.Primary0
import com.example.saku.app.ui.theme.Primary60
import com.example.saku.app.ui.theme.Surface
import com.example.saku.app.ui.theme.TextMuted
import com.example.saku.app.ui.theme.TextPrimary
import androidx.compose.runtime.getValue

@Composable
fun OtpInputField(
    otpValue: String,
    onOtpChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    otpLength: Int = 6,
    isMasked: Boolean = false,
    isError: Boolean = false,
    errorMessage: String? = null,
    enabled: Boolean = true,
    autoFocus: Boolean = true,
    focusRequester: FocusRequester = remember { FocusRequester() },
    boxWidth: Dp = 46.dp,
    boxHeight: Dp = 54.dp,
    onOtpComplete: ((String) -> Unit)? = null
) {
    LaunchedEffect(autoFocus, enabled) {
        if (autoFocus && enabled) {
            kotlinx.coroutines.delay(150)
            try {
                focusRequester.requestFocus()
            } catch (e: Exception) {
                // Ignore focus request error if not attached yet
            }
        }
    }

    LaunchedEffect(otpValue) {
        if (otpValue.length == otpLength) {
            onOtpComplete?.invoke(otpValue)
        }
    }

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Invisible core input overlaid with visual individual boxes
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            BasicTextField(
                value = otpValue,
                onValueChange = { input ->
                    val digitsOnly = input.filter { it.isDigit() }.take(otpLength)
                    onOtpChange(digitsOnly)
                },
                modifier = Modifier
                    .focusRequester(focusRequester)
                    .size(1.dp), // Tiny background anchor
                enabled = enabled,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword)
            )

            // Visual Boxes Row
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                for (index in 0 until otpLength) {
                    val isCurrentFocused = enabled && (otpValue.length == index || (index == otpLength - 1 && otpValue.length == otpLength))
                    val digitChar = otpValue.getOrNull(index)
                    val isFilled = digitChar != null

                    val borderColor by animateColorAsState(
                        targetValue = when {
                            !enabled -> Border.copy(alpha = 0.5f)
                            isError -> Error
                            isCurrentFocused -> Primary
                            isFilled -> Primary60
                            else -> Border
                        },
                        animationSpec = tween(durationMillis = 150),
                        label = "otp_border_$index"
                    )

                    val bgColor by animateColorAsState(
                        targetValue = when {
                            !enabled -> Neutral0
                            isCurrentFocused -> Surface
                            isFilled -> Primary0
                            else -> Color(0xFFF9FAFB)
                        },
                        animationSpec = tween(durationMillis = 150),
                        label = "otp_bg_$index"
                    )

                    Box(
                        modifier = Modifier
                            .size(width = boxWidth, height = boxHeight)
                            .clip(RoundedCornerShape(12.dp))
                            .background(bgColor)
                            .border(
                                width = if (isCurrentFocused || isError) 2.dp else 1.dp,
                                color = borderColor,
                                shape = RoundedCornerShape(12.dp)
                            )
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null,
                                onClick = {
                                    focusRequester.requestFocus()
                                }
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isFilled) {
                            if (isMasked) {
                                Box(
                                    modifier = Modifier
                                        .size(10.dp)
                                        .clip(CircleShape)
                                        .background(Primary)
                                )
                            } else {
                                Text(
                                    text = digitChar.toString(),
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isError) Error else TextPrimary
                                )
                            }
                        } else if (isCurrentFocused) {
                            // Blinking subtle dot or cursor indicator
                            Box(
                                modifier = Modifier
                                    .size(width = 2.dp, height = 20.dp)
                                    .clip(RoundedCornerShape(1.dp))
                                    .background(Primary)
                            )
                        }
                    }
                }
            }
        }

        // Error message
        if (isError && !errorMessage.isNullOrBlank()) {
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Lucide.CircleAlert,
                    contentDescription = "Error",
                    modifier = Modifier.size(14.dp),
                    tint = Error
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = errorMessage,
                    fontSize = 12.sp,
                    color = Error,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
fun OtpResendSection(
    countdownSeconds: Int,
    onResendClick: () -> Unit,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false
) {
    val canResend = countdownSeconds <= 0 && !isLoading

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (canResend) {
            Text(
                text = "Belum menerima kode? ",
                fontSize = 13.sp,
                color = TextMuted
            )
            Text(
                text = "Kirim Ulang",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = Primary,
                modifier = Modifier
                    .clickable(onClick = onResendClick)
                    .padding(4.dp)
            )
        } else {
            val minutes = countdownSeconds / 60
            val seconds = countdownSeconds % 60
            val formattedTime = String.format("%02d:%02d", minutes, seconds)

            Text(
                text = "Kirim ulang kode dalam ",
                fontSize = 13.sp,
                color = TextMuted
            )
            Text(
                text = formattedTime,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = Primary
            )
        }
    }
}