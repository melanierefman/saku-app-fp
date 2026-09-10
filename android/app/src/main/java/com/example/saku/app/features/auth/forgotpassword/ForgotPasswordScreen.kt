package com.example.saku.app.features.auth.forgotpassword

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.composables.icons.lucide.ArrowLeft
import com.composables.icons.lucide.CircleAlert
import com.composables.icons.lucide.Key
import com.composables.icons.lucide.Lock
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.ShieldCheck
import com.example.saku.app.R
import com.example.saku.app.core.network.ApiResult
import com.example.saku.app.core.ui.components.Button
import com.example.saku.app.core.ui.components.ButtonSize
import com.example.saku.app.core.ui.components.ButtonVariant
import com.example.saku.app.core.ui.components.OtpInputField
import com.example.saku.app.core.ui.components.PasswordField
import com.example.saku.app.core.ui.components.ResultStateView
import com.example.saku.app.core.ui.components.ResultType
import com.example.saku.app.core.ui.components.TextField
import com.example.saku.app.ui.theme.Border
import com.example.saku.app.ui.theme.Error0
import com.example.saku.app.ui.theme.Error60
import com.example.saku.app.ui.theme.Primary
import com.example.saku.app.ui.theme.Primary0
import com.example.saku.app.ui.theme.Surface
import com.example.saku.app.ui.theme.TextMuted
import com.example.saku.app.ui.theme.TextPrimary
import com.example.saku.app.ui.theme.TextSecondary
import kotlinx.coroutines.delay

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

    val focusManager = LocalFocusManager.current
    val scrollState = rememberScrollState()

    // Countdown state for OTP resend (58s demo matching design screenshot)
    var countdownSecs by remember { mutableIntStateOf(58) }
    LaunchedEffect(step) {
        if (step == ForgotPasswordStep.VERIFY_OTP) {
            countdownSecs = 58
            while (countdownSecs > 0) {
                delay(1000)
                countdownSecs--
            }
        }
    }

    Scaffold(
        containerColor = Color.Transparent
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // SAKU Mesh Gradient Background
            Image(
                painter = painterResource(id = R.drawable.bg_card_saku),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxSize()
                    .imePadding()
            ) {
                val totalScreenHeight = maxHeight
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(scrollState)
                        .padding(horizontal = 24.dp, vertical = 20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Top Bar with Circular Back Button & Centered "SAKU" Logo
                    if (step != ForgotPasswordStep.SUCCESS) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Circular White Back Button
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .shadow(4.dp, CircleShape)
                                    .clip(CircleShape)
                                    .background(Color.White.copy(alpha = 0.95f))
                                    .clickable {
                                        if (step == ForgotPasswordStep.VERIFY_OTP || step == ForgotPasswordStep.RESET_PASSWORD) {
                                            viewModel.changeEmail()
                                        } else {
                                            onNavigateBack()
                                        }
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Lucide.ArrowLeft,
                                    contentDescription = "Kembali",
                                    tint = Primary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }

                            // SAKU Brand Center Text
                            Text(
                                text = "SAKU",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )

                            // Dummy Spacer for symmetry
                            Spacer(modifier = Modifier.size(38.dp))
                        }
                    }

                    // Centered Scrollable Area
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = (if (step != ForgotPasswordStep.SUCCESS) totalScreenHeight - 40.dp - 50.dp else totalScreenHeight - 40.dp).coerceAtLeast(0.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Error Alert Banner
                    if (actionState is ApiResult.Error) {
                        val errorMsg = (actionState as ApiResult.Error).message
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 16.dp),
                            shape = RoundedCornerShape(10.dp),
                            colors = CardDefaults.cardColors(containerColor = Error0)
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Lucide.CircleAlert,
                                    contentDescription = null,
                                    tint = Error60,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = errorMsg,
                                    fontSize = 12.sp,
                                    color = Error60,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }

            when (step) {
                // SCREEN 1: LUPA KATA SANDI (Request OTP)
                ForgotPasswordStep.REQUEST_OTP -> {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(
                                elevation = 12.dp,
                                shape = RoundedCornerShape(24.dp),
                                ambientColor = Color(0x33000000),
                                spotColor = Color(0x33000000)
                            ),
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = Surface.copy(alpha = 0.98f)),
                        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.8f))
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 24.dp, vertical = 28.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            // Orange Key Icon Badge
                            Box(
                                modifier = Modifier
                                    .size(54.dp)
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(Primary),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Lucide.Key,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(26.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // Title
                            Text(
                                text = "Lupa Kata Sandi?",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary,
                                textAlign = TextAlign.Center
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            // Subtitle
                            Text(
                                text = "Masukkan email Anda untuk menerima instruksi reset kata sandi.",
                                fontSize = 12.5.sp,
                                fontWeight = FontWeight.Normal,
                                color = TextSecondary,
                                textAlign = TextAlign.Center,
                                lineHeight = 16.sp
                            )

                            Spacer(modifier = Modifier.height(24.dp))

                            // Email Field
                            TextField(
                                value = email,
                                onValueChange = viewModel::onEmailChange,
                                label = "Email",
                                placeholder = "Masukkan email",
                                isError = emailError != null,
                                errorMessage = emailError,
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Email,
                                    imeAction = ImeAction.Done
                                ),
                                keyboardActions = KeyboardActions(
                                    onDone = {
                                        focusManager.clearFocus()
                                        viewModel.sendOtp()
                                    }
                                )
                            )

                            Spacer(modifier = Modifier.height(24.dp))

                            // Submit Button "Kirim"
                            Button(
                                text = "Kirim",
                                isLoading = actionState is ApiResult.Loading,
                                onClick = {
                                    focusManager.clearFocus()
                                    viewModel.sendOtp()
                                },
                                variant = ButtonVariant.Primary,
                                size = ButtonSize.LG,
                                fullWidth = true
                            )
                        }
                    }
                }

                // SCREEN 2: VERIFIKASI KODE OTP
                ForgotPasswordStep.VERIFY_OTP -> {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(
                                elevation = 12.dp,
                                shape = RoundedCornerShape(24.dp),
                                ambientColor = Color(0x33000000),
                                spotColor = Color(0x33000000)
                            ),
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = Surface.copy(alpha = 0.98f)),
                        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.8f))
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 24.dp, vertical = 28.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            // Orange Shield Icon Badge
                            Box(
                                modifier = Modifier
                                    .size(54.dp)
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(Primary),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Lucide.ShieldCheck,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(26.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // Title
                            Text(
                                text = "Verifikasi Kode OTP",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary,
                                textAlign = TextAlign.Center
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            // Subtitle
                            Text(
                                text = "Kami telah mengirimkan kode 6 digit ke nomor/email anda.",
                                fontSize = 12.5.sp,
                                fontWeight = FontWeight.Normal,
                                color = TextSecondary,
                                textAlign = TextAlign.Center,
                                lineHeight = 16.sp
                            )

                            Spacer(modifier = Modifier.height(24.dp))

                            // 6-Digit OTP Field
                            OtpInputField(
                                otpValue = otpCode,
                                onOtpChange = viewModel::onOtpChange,
                                otpLength = 6,
                                onOtpComplete = {
                                    viewModel.verifyOtp()
                                }
                            )

                            if (otpError != null) {
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = otpError ?: "",
                                    fontSize = 12.sp,
                                    color = Error60,
                                    fontWeight = FontWeight.Medium
                                )
                            }

                            Spacer(modifier = Modifier.height(18.dp))

                            // Resend Section (Belum menerima kode? Kirim ulang dalam 00:58)
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(2.dp)
                            ) {
                                Text(
                                    text = "Belum menerima kode?",
                                    fontSize = 12.sp,
                                    color = TextSecondary
                                )
                                if (countdownSecs > 0) {
                                    Text(
                                        text = "Kirim ulang dalam 00:${if (countdownSecs < 10) "0$countdownSecs" else countdownSecs}",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Primary
                                    )
                                } else {
                                    Text(
                                        text = "Kirim Ulang Kode",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Primary,
                                        modifier = Modifier.clickable {
                                            viewModel.sendOtp()
                                        }
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(22.dp))

                            // Action Button "Verifikasi"
                            Button(
                                text = "Verifikasi",
                                isLoading = actionState is ApiResult.Loading,
                                onClick = {
                                    viewModel.verifyOtp()
                                },
                                variant = ButtonVariant.Primary,
                                size = ButtonSize.LG,
                                fullWidth = true
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            // Secondary Link "← Ganti Email"
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .clickable { viewModel.changeEmail() }
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Icon(
                                    imageVector = Lucide.ArrowLeft,
                                    contentDescription = null,
                                    tint = TextSecondary,
                                    modifier = Modifier.size(13.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Ganti Email",
                                    fontSize = 12.5.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = TextSecondary
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Bottom Security Note (Outside Card)
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Lucide.Lock,
                            contentDescription = null,
                            tint = Color.White.copy(alpha = 0.85f),
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Enkripsi end-to-end. Kode OTP bersifat rahasia.",
                            fontSize = 11.5.sp,
                            color = Color.White.copy(alpha = 0.85f)
                        )
                    }
                }

                // SCREEN 3: BUAT PASSWORD BARU
                ForgotPasswordStep.RESET_PASSWORD -> {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(
                                elevation = 12.dp,
                                shape = RoundedCornerShape(24.dp),
                                ambientColor = Color(0x33000000),
                                spotColor = Color(0x33000000)
                            ),
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = Surface.copy(alpha = 0.98f)),
                        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.8f))
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 24.dp, vertical = 28.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(54.dp)
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(Primary),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Lucide.Lock,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(26.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            Text(
                                text = "Buat Password Baru",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary,
                                textAlign = TextAlign.Center
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = "Kata sandi baru harus minimal 6 karakter.",
                                fontSize = 12.5.sp,
                                color = TextSecondary,
                                textAlign = TextAlign.Center
                            )

                            Spacer(modifier = Modifier.height(24.dp))

                            PasswordField(
                                value = newPassword,
                                onValueChange = viewModel::onNewPasswordChange,
                                label = "Password Baru",
                                placeholder = "Masukkan password baru",
                                errorMessage = passwordError,
                                imeAction = ImeAction.Next
                            )

                            Spacer(modifier = Modifier.height(14.dp))

                            PasswordField(
                                value = confirmPassword,
                                onValueChange = viewModel::onConfirmPasswordChange,
                                label = "Konfirmasi Password",
                                placeholder = "Ulangi password baru",
                                errorMessage = confirmPasswordError,
                                imeAction = ImeAction.Done,
                                onImeAction = {
                                    focusManager.clearFocus()
                                    viewModel.resetPassword()
                                }
                            )

                            Spacer(modifier = Modifier.height(24.dp))

                            Button(
                                text = "Simpan Password Baru",
                                isLoading = actionState is ApiResult.Loading,
                                onClick = {
                                    focusManager.clearFocus()
                                    viewModel.resetPassword()
                                },
                                variant = ButtonVariant.Primary,
                                size = ButtonSize.LG,
                                fullWidth = true
                            )
                        }
                    }
                }

                // SCREEN 4: SUKSES RESET PASSWORD
                ForgotPasswordStep.SUCCESS -> {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(
                                elevation = 12.dp,
                                shape = RoundedCornerShape(24.dp),
                                ambientColor = Color(0x33000000),
                                spotColor = Color(0x33000000)
                            ),
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = Surface.copy(alpha = 0.98f)),
                        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.8f))
                    ) {
                        ResultStateView(
                            title = "Password Berhasil Diubah",
                            description = "Kata sandi akun SAKU Anda telah berhasil diperbarui. Silakan masuk menggunakan kata sandi baru.",
                            type = ResultType.SUCCESS,
                            primaryButtonText = "Masuk Sekarang",
                            onPrimaryClick = onNavigateToLogin
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
}



