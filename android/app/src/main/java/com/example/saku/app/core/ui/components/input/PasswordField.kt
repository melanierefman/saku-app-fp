package com.example.saku.app.core.ui.components

import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import com.composables.icons.lucide.Lock
import com.composables.icons.lucide.Lucide

@Composable
fun PasswordField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String = "Kata Sandi",
    required: Boolean = false,
    placeholder: String = "Masukkan kata sandi",
    helperText: String? = null,
    errorMessage: String? = null,
    leadingIcon: ImageVector? = Lucide.Lock,
    enabled: Boolean = true,
    imeAction: ImeAction = ImeAction.Done,
    onImeAction: () -> Unit = {}
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        label = label,
        required = required,
        placeholder = placeholder,
        helperText = helperText,
        errorMessage = errorMessage,
        leadingIcon = leadingIcon,
        isPassword = true,
        enabled = enabled,
        singleLine = true,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Password,
            imeAction = imeAction
        ),
        keyboardActions = KeyboardActions(
            onDone = { onImeAction() },
            onNext = { onImeAction() },
            onGo = { onImeAction() }
        )
    )
}
