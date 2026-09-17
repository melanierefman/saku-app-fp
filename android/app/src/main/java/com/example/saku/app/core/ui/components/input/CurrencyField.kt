package com.example.saku.app.core.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton as M3IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.composables.icons.lucide.CircleAlert
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Wallet
import com.composables.icons.lucide.X
import com.example.saku.app.ui.theme.Border
import com.example.saku.app.ui.theme.Error
import com.example.saku.app.ui.theme.Neutral0
import com.example.saku.app.ui.theme.Neutral40
import com.example.saku.app.ui.theme.Neutral60
import com.example.saku.app.ui.theme.Primary
import com.example.saku.app.ui.theme.Primary0
import com.example.saku.app.ui.theme.Primary20
import com.example.saku.app.ui.theme.Primary60
import com.example.saku.app.ui.theme.Surface
import com.example.saku.app.ui.theme.TextMuted
import com.example.saku.app.ui.theme.TextPrimary
import java.text.NumberFormat
import java.util.Locale

class CurrencyVisualTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val originalText = text.text
        if (originalText.isEmpty()) {
            return TransformedText(text, OffsetMapping.Identity)
        }

        val digitsOnly = originalText.filter { it.isDigit() }
        if (digitsOnly.isEmpty()) {
            return TransformedText(text, OffsetMapping.Identity)
        }

        val parsed = digitsOnly.toLongOrNull() ?: 0L
        val formatted = NumberFormat.getNumberInstance(Locale.forLanguageTag("id-ID")).format(parsed)

        val offsetMapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                if (offset <= 0) return 0
                val digitsBefore = originalText.take(offset).count { it.isDigit() }
                var count = 0
                for (i in formatted.indices) {
                    if (formatted[i].isDigit()) count++
                    if (count == digitsBefore) return i + 1
                }
                return formatted.length
            }

            override fun transformedToOriginal(offset: Int): Int {
                if (offset <= 0) return 0
                val digitsBefore = formatted.take(offset).count { it.isDigit() }
                var count = 0
                for (i in originalText.indices) {
                    if (originalText[i].isDigit()) count++
                    if (count == digitsBefore) return i + 1
                }
                return originalText.length
            }
        }

        return TransformedText(AnnotatedString(formatted), offsetMapping)
    }
}

fun formatRupiah(amount: Long): String {
    return "Rp " + NumberFormat.getNumberInstance(Locale.forLanguageTag("id-ID")).format(amount)
}

fun formatQuickLabel(amount: Long): String {
    return when {
        amount >= 1_000_000L -> {
            val jt = amount.toDouble() / 1_000_000.0
            "+${if (jt % 1.0 == 0.0) jt.toInt() else jt} Jt"
        }
        amount >= 1_000L -> "+${amount / 1_000L} rb"
        else -> "+$amount"
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CurrencyField(
    amount: Long?,
    onAmountChange: (Long?) -> Unit,
    modifier: Modifier = Modifier,
    label: String = "Nominal Pinjaman",
    required: Boolean = false,
    placeholder: String = "0",
    helperText: String? = null,
    errorMessage: String? = null,
    minAmount: Long? = null,
    maxAmount: Long? = null,
    quickAmounts: List<Long> = listOf(500_000L, 1_000_000L, 2_000_000L, 5_000_000L),
    leadingIcon: ImageVector? = Lucide.Wallet,
    enabled: Boolean = true,
    readOnly: Boolean = false
) {
    val rawString = amount?.toString() ?: ""
    val isError = !errorMessage.isNullOrBlank()

    Column(modifier = modifier.fillMaxWidth()) {
        // Label
        if (label.isNotBlank()) {
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
            value = rawString,
            onValueChange = { input ->
                val digitsOnly = input.filter { it.isDigit() }
                if (digitsOnly.isEmpty()) {
                    onAmountChange(null)
                } else {
                    var parsed = digitsOnly.toLongOrNull() ?: 0L
                    if (maxAmount != null && parsed > maxAmount) {
                        parsed = maxAmount
                    }
                    onAmountChange(parsed)
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = enabled,
            readOnly = readOnly,
            singleLine = true,
            textStyle = TextStyle(
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = if (enabled) TextPrimary else TextMuted
            ),
            placeholder = {
                Text(
                    text = placeholder,
                    fontSize = 15.sp,
                    color = TextMuted
                )
            },
            leadingIcon = {
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
                        Spacer(modifier = Modifier.width(6.dp))
                    }
                    Text(
                        text = "Rp",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (enabled) Primary else Neutral40
                    )
                }
            },
            trailingIcon = if (amount != null && amount > 0L && enabled && !readOnly) {
                {
                    M3IconButton(
                        onClick = { onAmountChange(null) },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Lucide.X,
                            contentDescription = "Hapus",
                            modifier = Modifier.size(16.dp),
                            tint = Neutral40
                        )
                    }
                }
            } else null,
            visualTransformation = CurrencyVisualTransformation(),
            isError = isError,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Primary,
                unfocusedBorderColor = Border,
                errorBorderColor = Error,
                focusedContainerColor = Surface,
                unfocusedContainerColor = if (amount != null && amount > 0L) Surface else Color(0xFFF9FAFB),
                disabledContainerColor = Color(0xFFF9FAFB),
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary,
                cursorColor = Primary
            )
        )

        // Error or Helper Text
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

        // Quick Amount Chips
        if (quickAmounts.isNotEmpty() && enabled && !readOnly) {
            Spacer(modifier = Modifier.height(8.dp))
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                quickAmounts.forEach { quickVal ->
                    val isSelected = amount == quickVal
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) Primary0 else Neutral0)
                            .border(
                                width = 1.dp,
                                color = if (isSelected) Primary else Border,
                                shape = RoundedCornerShape(8.dp)
                            )
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = ripple(color = Primary.copy(alpha = 0.2f)),
                                onClick = {
                                    val nextAmount = (amount ?: 0L) + quickVal
                                    val finalAmount = if (maxAmount != null && nextAmount > maxAmount) maxAmount else nextAmount
                                    onAmountChange(finalAmount)
                                }
                            )
                            .padding(horizontal = 10.dp, vertical = 5.dp)
                    ) {
                        Text(
                            text = formatQuickLabel(quickVal),
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) Primary60 else Neutral60
                        )
                    }
                }
            }
        }
    }
}
