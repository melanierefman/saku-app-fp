package com.example.saku.app.features.auth.forgotpassword

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import com.composables.icons.lucide.CircleAlert
import com.composables.icons.lucide.CircleCheck
import com.composables.icons.lucide.Key
import com.composables.icons.lucide.Lock
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Mail
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.saku.app.core.network.ApiResult
import com.example.saku.app.core.ui.components.Button
import com.example.saku.app.core.ui.components.TextField
import com.example.saku.app.core.ui.components.TopBar
import com.example.saku.app.ui.theme.Background
import com.example.saku.app.ui.theme.Border
import com.example.saku.app.ui.theme.Error0
import com.example.saku.app.ui.theme.Error60
import com.example.saku.app.ui.theme.Success
import com.example.saku.app.ui.theme.Surface
import com.example.saku.app.ui.theme.TextPrimary
import com.example.saku.app.ui.theme.TextSecondary

@Composable
fun ForgotPasswordScreen(
    onNavigateBack: () -> Unit,
    onNavigateToLogin: () -> Unit,
    viewModel: ForgotPasswordViewModel = viewModel()
) {
    val step by viewModel.currentStep.collectAsState()
    val email by viewModel.email.collectAsState()
    val otpCode by viewModel.otpCode.collectAsState()
    val newPassword by viewModel.newPassword.collectAsState()
    val confirmPassword by viewModel.confirmPassword.collectAsState()

    val emailError by viewModel.emailError.collectAsState()
    val otpError by viewModel.otpError.collectAsState()
    val passwordError by viewModel.passwordError.collectAsState()
    val confirmPasswordError by viewModel.confirmPasswordError.collectAsState()
    val actionState by viewModel.actionState.collectAsState()

    Scaffold(
        topBar = {
            TopBar(
                title = "Lupa Password",
                onBackClick = onNavigateBack
            )
        },
        containerColor = Background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            when (step) {
                ForgotPasswordStep.REQUEST_OTP -> {
                    Text(
                        text = "Atur Ulang Password",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Masukkan email akun SAKU Anda. Kami akan mengirimkan kode OTP untuk verifikasi.",
                        fontSize = 14.sp,
                        color = TextSecondary
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    if (actionState is ApiResult.Error) {
                        ErrorCard(message = (actionState as ApiResult.Error).message)
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Surface),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Border)
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            TextField(
                                value = email,
                                onValueChange = viewModel::onEmailChange,
                                label = "Email Terdaftar",
                                placeholder = "contoh@email.com",
                                leadingIcon = Lucide.Mail,
                                isError = emailError != null,
                                errorMessage = emailError,
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Email,
                                    imeAction = ImeAction.Done
                                )
                            )

                            Spacer(modifier = Modifier.height(20.dp))

                            Button(
                                text = "Kirim Kode OTP",
                                isLoading = actionState is ApiResult.Loading,
                                onClick = viewModel::sendOtp
                            )
                        }
                    }
                }

                ForgotPasswordStep.RESET_PASSWORD -> {
                    Text(
                        text = "Verifikasi & Buat Password Baru",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Kode OTP telah dikirim ke $email. Silakan masukkan kode dan buat password baru.",
                        fontSize = 14.sp,
                        color = TextSecondary
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    if (actionState is ApiResult.Error) {
                        ErrorCard(message = (actionState as ApiResult.Error).message)
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Surface),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Border)
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            TextField(
                                value = otpCode,
                                onValueChange = viewModel::onOtpChange,
                                label = "Kode OTP (6 Digit)",
                                placeholder = "123456",
                                leadingIcon = Lucide.Key,
                                isError = otpError != null,
                                errorMessage = otpError,
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Number,
                                    imeAction = ImeAction.Next
                                )
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            TextField(
                                value = newPassword,
                                onValueChange = viewModel::onNewPasswordChange,
                                label = "Password Baru",
                                placeholder = "Minimal 6 karakter",
                                leadingIcon = Lucide.Lock,
                                isPassword = true,
                                isError = passwordError != null,
                                errorMessage = passwordError,
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Password,
                                    imeAction = ImeAction.Next
                                )
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            TextField(
                                value = confirmPassword,
                                onValueChange = viewModel::onConfirmPasswordChange,
                                label = "Konfirmasi Password Baru",
                                placeholder = "Ulangi password baru",
                                leadingIcon = Lucide.Lock,
                                isPassword = true,
                                isError = confirmPasswordError != null,
                                errorMessage = confirmPasswordError,
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Password,
                                    imeAction = ImeAction.Done
                                )
                            )

                            Spacer(modifier = Modifier.height(20.dp))

                            Button(
                                text = "Simpan Password Baru",
                                isLoading = actionState is ApiResult.Loading,
                                onClick = viewModel::resetPassword
                            )
                        }
                    }
                }

                ForgotPasswordStep.SUCCESS -> {
                    Spacer(modifier = Modifier.height(40.dp))

                    Icon(
                        imageVector = Lucide.CircleCheck,
                        contentDescription = null,
                        tint = Success,
                        modifier = Modifier.size(72.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Password Berhasil Diubah!",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Silakan login menggunakan password baru Anda.",
                        fontSize = 14.sp,
                        color = TextSecondary
                    )

                    Spacer(modifier = Modifier.height(28.dp))

                    Button(
                        text = "Kembali ke Login",
                        onClick = onNavigateToLogin
                    )
                }
            }
        }
    }
}

@Composable
private fun ErrorCard(message: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Error0)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Lucide.CircleAlert,
                contentDescription = null,
                tint = Error60,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = message,
                fontSize = 13.sp,
                color = Error60,
                fontWeight = FontWeight.Medium
            )
        }
    }
}
