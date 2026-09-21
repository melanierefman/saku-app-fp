package com.example.saku.app.features.profile.password

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.composables.icons.lucide.ArrowLeft
import com.composables.icons.lucide.CircleCheck
import com.composables.icons.lucide.KeyRound
import com.composables.icons.lucide.Lock
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.ShieldCheck
import com.example.saku.app.core.ui.components.Button
import com.example.saku.app.core.ui.components.ButtonSize
import com.example.saku.app.core.ui.components.ButtonVariant
import com.example.saku.app.core.ui.components.ConfirmationDialog
import com.example.saku.app.core.ui.components.DialogType
import com.example.saku.app.core.ui.components.PasswordField
import com.example.saku.app.features.home.HomeViewModel
import com.example.saku.app.ui.theme.Background
import com.example.saku.app.ui.theme.Border
import com.example.saku.app.ui.theme.Error
import com.example.saku.app.ui.theme.Neutral10
import com.example.saku.app.ui.theme.Primary
import com.example.saku.app.ui.theme.Primary0
import com.example.saku.app.ui.theme.Success
import com.example.saku.app.ui.theme.Surface
import com.example.saku.app.ui.theme.TextMuted
import com.example.saku.app.ui.theme.TextPrimary
import org.koin.androidx.compose.koinViewModel
import com.example.saku.app.ui.theme.TextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChangePasswordScreen(
    onNavigateBack: () -> Unit,
    viewModel: HomeViewModel = koinViewModel()
) {
    val context = LocalContext.current
    val isUpdating by viewModel.isProfileUpdating.collectAsState()

    var oldPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var showConfirmDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Surface,
                border = BorderStroke(1.dp, Border),
                shadowElevation = 1.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Lucide.ArrowLeft,
                            contentDescription = "Kembali",
                            tint = TextPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = "Ganti Kata Sandi",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }
            }
        },
        containerColor = Background
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Security Tips Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Primary0),
                    border = BorderStroke(1.dp, Primary.copy(alpha = 0.2f))
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(Primary),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Lucide.ShieldCheck,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Tips Keamanan Kata Sandi",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Primary
                            )
                            Text(
                                text = "Gunakan kombinasi minimal 8 karakter dengan huruf dan angka.",
                                fontSize = 11.5.sp,
                                color = TextSecondary,
                                lineHeight = 15.sp
                            )
                        }
                    }
                }
            }

            // Input Form Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = Surface),
                    border = BorderStroke(1.dp, Border)
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        PasswordField(
                            value = oldPassword,
                            onValueChange = { oldPassword = it; errorMessage = null },
                            label = "Kata Sandi Lama",
                            placeholder = "Masukkan kata sandi saat ini",
                            required = true
                        )

                        PasswordField(
                            value = newPassword,
                            onValueChange = { newPassword = it; errorMessage = null },
                            label = "Kata Sandi Baru",
                            placeholder = "Minimal 8 karakter",
                            required = true
                        )

                        PasswordField(
                            value = confirmPassword,
                            onValueChange = { confirmPassword = it; errorMessage = null },
                            label = "Konfirmasi Kata Sandi Baru",
                            placeholder = "Ulangi kata sandi baru",
                            required = true
                        )

                        // Password checklist guidelines
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            PasswordCriteriaRow(
                                title = "Minimal 8 karakter",
                                isMet = newPassword.length >= 8
                            )
                            PasswordCriteriaRow(
                                title = "Konfirmasi kata sandi cocok",
                                isMet = newPassword.isNotBlank() && newPassword == confirmPassword
                            )
                        }

                        if (errorMessage != null) {
                            Text(
                                text = errorMessage!!,
                                fontSize = 12.sp,
                                color = Error
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Button(
                            text = "Simpan Kata Sandi Baru",
                            onClick = {
                                if (oldPassword.isBlank()) {
                                    errorMessage = "Kata sandi lama wajib diisi"
                                    return@Button
                                }
                                if (newPassword.length < 8) {
                                    errorMessage = "Kata sandi baru minimal 8 karakter"
                                    return@Button
                                }
                                if (newPassword != confirmPassword) {
                                    errorMessage = "Konfirmasi kata sandi tidak cocok"
                                    return@Button
                                }

                                showConfirmDialog = true
                            },
                            isLoading = isUpdating,
                            variant = ButtonVariant.Primary,
                            size = ButtonSize.LG,
                            fullWidth = true
                        )
                    }
                }
            }
        }
    }

    // Confirmation Dialog
    ConfirmationDialog(
        visible = showConfirmDialog,
        title = "Ganti Kata Sandi?",
        message = "Simpan kata sandi baru untuk akun Anda?",
        type = DialogType.INFO,
        icon = Lucide.KeyRound,
        confirmButtonText = "Ya, Ganti",
        dismissButtonText = "Batal",
        confirmButtonVariant = ButtonVariant.Primary,
        isLoading = isUpdating,
        onConfirm = {
            viewModel.changePassword(
                oldPass = oldPassword,
                newPass = newPassword,
                confirmPass = confirmPassword,
                onSuccess = { msg ->
                    showConfirmDialog = false
                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                    onNavigateBack()
                },
                onError = { err ->
                    showConfirmDialog = false
                    errorMessage = err
                }
            )
        },
        onDismiss = {
            if (!isUpdating) {
                showConfirmDialog = false
            }
        }
    )
}

@Composable
private fun PasswordCriteriaRow(title: String, isMet: Boolean) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Icon(
            imageVector = Lucide.CircleCheck,
            contentDescription = null,
            tint = if (isMet) Success else TextMuted,
            modifier = Modifier.size(13.dp)
        )
        Text(
            text = title,
            fontSize = 11.5.sp,
            color = if (isMet) Success else TextMuted,
            fontWeight = if (isMet) FontWeight.Medium else FontWeight.Normal
        )
    }
}
