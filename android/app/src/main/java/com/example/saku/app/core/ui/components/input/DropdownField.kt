package com.example.saku.app.core.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton as M3IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.composables.icons.lucide.Check
import com.composables.icons.lucide.ChevronDown
import com.composables.icons.lucide.CircleAlert
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Search
import com.composables.icons.lucide.X
import com.example.saku.app.ui.theme.Border
import com.example.saku.app.ui.theme.Error
import com.example.saku.app.ui.theme.Neutral0
import com.example.saku.app.ui.theme.Neutral10
import com.example.saku.app.ui.theme.Neutral40
import com.example.saku.app.ui.theme.Neutral60
import com.example.saku.app.ui.theme.Primary
import com.example.saku.app.ui.theme.Primary0
import com.example.saku.app.ui.theme.Primary60
import com.example.saku.app.ui.theme.Surface
import com.example.saku.app.ui.theme.TextMuted
import com.example.saku.app.ui.theme.TextPrimary
import com.example.saku.app.ui.theme.TextSecondary
import kotlinx.coroutines.launch

data class DropdownOption(
    val value: String,
    val label: String,
    val description: String? = null,
    val badge: String? = null,
    val leadingIcon: ImageVector? = null,
    val disabled: Boolean = false
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropdownField(
    options: List<DropdownOption>,
    selectedOption: DropdownOption?,
    onOptionSelect: (DropdownOption?) -> Unit,
    modifier: Modifier = Modifier,
    label: String? = null,
    required: Boolean = false,
    placeholder: String = "Pilih opsi...",
    helperText: String? = null,
    errorMessage: String? = null,
    searchable: Boolean = true,
    clearable: Boolean = false,
    leadingIcon: ImageVector? = null,
    enabled: Boolean = true
) {
    var isSheetOpen by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val coroutineScope = rememberCoroutineScope()

    val isError = !errorMessage.isNullOrBlank()
    val hasValue = selectedOption != null

    val chevronRotation by animateFloatAsState(
        targetValue = if (isSheetOpen) 180f else 0f,
        label = "dropdown_chevron"
    )

    val filteredOptions = remember(searchQuery, options) {
        if (searchQuery.isBlank()) options
        else options.filter {
            it.label.contains(searchQuery, ignoreCase = true) ||
                    (it.description?.contains(searchQuery, ignoreCase = true) == true)
        }
    }

    Column(modifier = modifier.fillMaxWidth()) {
        // Label
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

        // Trigger Box
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(
                    when {
                        !enabled -> Color(0xFFF9FAFB)
                        hasValue -> Surface
                        else -> Color(0xFFF9FAFB)
                    }
                )
                .border(
                    width = 1.dp,
                    color = when {
                        !enabled -> Border.copy(alpha = 0.6f)
                        isError -> Error
                        isSheetOpen -> Primary
                        else -> Border
                    },
                    shape = RoundedCornerShape(12.dp)
                )
                .clickable(
                    enabled = enabled,
                    interactionSource = remember { MutableInteractionSource() },
                    indication = ripple(color = Primary.copy(alpha = 0.1f)),
                    onClick = {
                        searchQuery = ""
                        isSheetOpen = true
                    }
                )
                .padding(horizontal = 14.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    val iconToShow = selectedOption?.leadingIcon ?: leadingIcon
                    if (iconToShow != null) {
                        Icon(
                            imageVector = iconToShow,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp),
                            tint = if (isError) Error else Neutral40
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                    }

                    if (selectedOption != null) {
                        Text(
                            text = selectedOption.label,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = if (enabled) TextPrimary else TextMuted
                        )
                        if (!selectedOption.badge.isNullOrBlank()) {
                            Spacer(modifier = Modifier.width(8.dp))
                            Badge(
                                text = selectedOption.badge,
                                variant = BadgeVariant.Primary,
                                size = BadgeSize.SM
                            )
                        }
                    } else {
                        Text(
                            text = placeholder,
                            fontSize = 14.sp,
                            color = TextMuted
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (clearable && selectedOption != null && enabled) {
                        M3IconButton(
                            onClick = { onOptionSelect(null) },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                imageVector = Lucide.X,
                                contentDescription = "Clear",
                                modifier = Modifier.size(14.dp),
                                tint = Neutral40
                            )
                        }
                        Spacer(modifier = Modifier.width(2.dp))
                    }

                    Icon(
                        imageVector = Lucide.ChevronDown,
                        contentDescription = "Open",
                        modifier = Modifier
                            .size(18.dp)
                            .rotate(chevronRotation),
                        tint = if (isSheetOpen) Primary else Neutral40
                    )
                }
            }
        }

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
    }

    // Modal Bottom Sheet for Option Picker
    if (isSheetOpen) {
        ModalBottomSheet(
            onDismissRequest = { isSheetOpen = false },
            sheetState = sheetState,
            containerColor = Surface,
            shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
            dragHandle = {
                Box(
                    modifier = Modifier
                        .padding(vertical = 12.dp)
                        .size(width = 36.dp, height = 4.dp)
                        .clip(CircleShape)
                        .background(Neutral10)
                )
            }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .padding(bottom = 32.dp)
            ) {
                // Sheet Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = label ?: "Pilih Opsi",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    M3IconButton(
                        onClick = {
                            coroutineScope.launch {
                                sheetState.hide()
                                isSheetOpen = false
                            }
                        },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Lucide.X,
                            contentDescription = "Close",
                            modifier = Modifier.size(18.dp),
                            tint = Neutral40
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Search Box
                if (searchable) {
                    TextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = "Cari opsi...",
                        leadingIcon = Lucide.Search,
                        trailingIcon = if (searchQuery.isNotEmpty()) Lucide.X else null,
                        onTrailingIconClick = { searchQuery = "" }
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }

                HorizontalDivider(color = Border, thickness = 1.dp)
                Spacer(modifier = Modifier.height(8.dp))

                // Options List
                if (filteredOptions.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Tidak ada opsi yang sesuai",
                            fontSize = 13.sp,
                            color = TextMuted
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 350.dp)
                    ) {
                        items(filteredOptions) { option ->
                            val isSelected = selectedOption?.value == option.value
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (isSelected) Primary0 else Color.Transparent)
                                    .clickable(
                                        enabled = !option.disabled,
                                        interactionSource = remember { MutableInteractionSource() },
                                        indication = ripple(color = Primary.copy(alpha = 0.1f)),
                                        onClick = {
                                            onOptionSelect(option)
                                            coroutineScope.launch {
                                                sheetState.hide()
                                                isSheetOpen = false
                                            }
                                        }
                                    )
                                    .padding(horizontal = 12.dp, vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    if (option.leadingIcon != null) {
                                        Icon(
                                            imageVector = option.leadingIcon,
                                            contentDescription = null,
                                            modifier = Modifier.size(18.dp),
                                            tint = if (isSelected) Primary else Neutral40
                                        )
                                        Spacer(modifier = Modifier.width(10.dp))
                                    }

                                    Column {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(
                                                text = option.label,
                                                fontSize = 14.sp,
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                                color = if (isSelected) Primary60 else TextPrimary
                                            )
                                            if (!option.badge.isNullOrBlank()) {
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Badge(
                                                    text = option.badge,
                                                    variant = if (isSelected) BadgeVariant.SolidPrimary else BadgeVariant.Neutral,
                                                    size = BadgeSize.SM
                                                )
                                            }
                                        }

                                        if (!option.description.isNullOrBlank()) {
                                            Spacer(modifier = Modifier.height(2.dp))
                                            Text(
                                                text = option.description,
                                                fontSize = 12.sp,
                                                color = if (isSelected) Primary60.copy(alpha = 0.8f) else TextSecondary
                                            )
                                        }
                                    }
                                }

                                if (isSelected) {
                                    Icon(
                                        imageVector = Lucide.Check,
                                        contentDescription = "Selected",
                                        tint = Primary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
