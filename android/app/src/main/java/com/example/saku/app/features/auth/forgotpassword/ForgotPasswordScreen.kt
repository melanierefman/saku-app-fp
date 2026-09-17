package com.example.saku.app.features.auth.forgotpassword

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
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
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.koin.androidx.compose.koinViewModel
import com.composables.icons.lucide.ArrowLeft
import com.composables.icons.lucide.CircleAlert
import com.composables.icons.lucide.CircleCheck
import com.composables.icons.lucide.Lucide
import com.example.saku.app.core.network.ApiResult
import com.example.saku.app.core.ui.components.Button
import com.example.saku.app.core.ui.components.ButtonSize
import com.example.saku.app.core.ui.components.ButtonVariant
import com.example.saku.app.core.ui.components.OtpInputField
import com.example.saku.app.core.ui.components.PasswordField
import com.example.saku.app.core.ui.components.TextField
import com.example.saku.app.ui.theme.Error
import com.example.saku.app.ui.theme.Error0
import com.example.saku.app.ui.theme.Error60
import com.example.saku.app.ui.theme.Primary
import com.example.saku.app.ui.theme.Success
import com.example.saku.app.ui.theme.Success0
import com.example.saku.app.ui.theme.TextMuted
import com.example.saku.app.ui.theme.TextPrimary
import com.example.saku.app.ui.theme.TextSecondary
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForgotPasswordScreen(
    onNavigateBack: () -> Unit,
    onNavigateToLogin: () -> Unit,
    viewModel: ForgotPasswordViewModel = koinViewModel()
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

    // Countdown state for OTP resend (58s)
    var countdownSecs by remember { mutableIntStateOf(58) }
    LaunchedEffect(step) {
        scrollState.animateScrollTo(0)
        if (step == ForgotPasswordStep.VERIFY_OTP) {
            countdownSecs = 58
            while (countdownSecs > 0) {
                delay(1000)
                countdownSecs--
            }
        }
    }

    Scaffold(
        containerColor = Color.White,
        topBar = {
            if (step != ForgotPasswordStep.SUCCESS) {
                CenterAlignedTopAppBar(
                    title = { },
                    navigationIcon = {
                        IconButton(
                            onClick = {
                                if (step == ForgotPasswordStep.VERIFY_OTP || step == ForgotPasswordStep.RESET_PASSWORD) {
                                    viewModel.changeEmail()
                                } else {
                                    onNavigateBack()
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Lucide.ArrowLeft,
                                contentDescription = "Kembali",
                                tint = TextPrimary,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
                )
            }
        },
        bottomBar = {
            // Sticky Bottom CTA Bar
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .imePadding()
                    .navigationBarsPadding()
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                when (step) {
                    ForgotPasswordStep.REQUEST_OTP -> {
                        Button(
                            text = "Kirim Kode OTP",
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
                    ForgotPasswordStep.VERIFY_OTP -> {
                        // Resend OTP Countdown helper above the button
                        Row(
                            modifier = Modifier.padding(bottom = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "Belum dapat kode? ",
                                fontSize = 13.sp,
                                color = TextSecondary
                            )
                            if (countdownSecs > 0) {
                                Text(
                                    text = "Kirim ulang dalam ${countdownSecs}s",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Primary
                                )
                            } else {
                                Text(
                                    text = "Kirim Ulang",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Primary,
                                    modifier = Modifier.clickable {
                                        viewModel.sendOtp()
                                        countdownSecs = 58
                                    }
                                )
                            }
                        }

                        Button(
                            text = "Verifikasi OTP",
                            isLoading = actionState is ApiResult.Loading,
                            enabled = otpCode.length == 6,
                            onClick = {
                                focusManager.clearFocus()
                                viewModel.verifyOtp()
                            },
                            variant = ButtonVariant.Primary,
                            size = ButtonSize.LG,
                            fullWidth = true
                        )
                    }
                    ForgotPasswordStep.RESET_PASSWORD -> {
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
                    ForgotPasswordStep.SUCCESS -> {
                        Button(
                            text = "Masuk Sekarang",
                            onClick = onNavigateToLogin,
                            variant = ButtonVariant.Primary,
                            size = ButtonSize.LG,
                            fullWidth = true
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(scrollState)
                .padding(horizontal = 24.dp, vertical = 12.dp)
        ) {
            // Error Alert Banner
            if (actionState is ApiResult.Error) {
                val errorMsg = (actionState as ApiResult.Error).message
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 20.dp),
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
                            text = errorMsg,
                            fontSize = 13.sp,
                            color = Error60,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            when (step) {
                // STEP 1: REQUEST OTP
                ForgotPasswordStep.REQUEST_OTP -> {
                    Text(
                        text = "Lupa Kata Sandi?",
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        letterSpacing = (-0.5).sp
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Masukkan alamat email Anda yang terdaftar untuk menerima kode verifikasi OTP.",
                        fontSize = 14.sp,
                        color = TextSecondary,
                        lineHeight = 20.sp
                    )

                    Spacer(modifier = Modifier.height(28.dp))

                    TextField(
                        value = email,
                        onValueChange = viewModel::onEmailChange,
                        label = "Email Terdaftar",
                        placeholder = "nama@email.com",
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
                }

                // STEP 2: VERIFIKASI OTP
                ForgotPasswordStep.VERIFY_OTP -> {
                    Text(
                        text = "Verifikasi Kode OTP",
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        letterSpacing = (-0.5).sp
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Masukkan 6 digit kode verifikasi yang kami kirimkan ke email $email.",
                        fontSize = 14.sp,
                        color = TextSecondary,
                        lineHeight = 20.sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Ganti Email link
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .clickable { viewModel.changeEmail() }
                            .padding(vertical = 4.dp)
                    ) {
                        Text(
                            text = "Salah email? Ganti Email",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Primary
                        )
                    }

                    Spacer(modifier = Modifier.height(28.dp))

                    // 6-Digit OTP Field
                    OtpInputField(
                        otpValue = otpCode,
                        onOtpChange = viewModel::onOtpChange,
                        otpLength = 6,
                        autoFocus = true,
                        isError = otpError != null,
                        errorMessage = otpError,
                        onOtpComplete = {
                            viewModel.verifyOtp()
                        }
                    )
                }

                // STEP 3: BUAT PASSWORD BARU
                ForgotPasswordStep.RESET_PASSWORD -> {
                    Text(
                        text = "Buat Password Baru",
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        letterSpacing = (-0.5).sp
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Kata sandi baru harus memiliki minimal 6 karakter demi keamanan akun Anda.",
                        fontSize = 14.sp,
                        color = TextSecondary,
                        lineHeight = 20.sp
                    )

                    Spacer(modifier = Modifier.height(28.dp))

                    PasswordField(
                        value = newPassword,
                        onValueChange = viewModel::onNewPasswordChange,
                        label = "Password Baru",
                        placeholder = "Minimal 6 karakter",
                        errorMessage = passwordError,
                        imeAction = ImeAction.Next,
                        onImeAction = {
                            focusManager.moveFocus(FocusDirection.Down)
                        }
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    PasswordField(
                        value = confirmPassword,
                        onValueChange = viewModel::onConfirmPasswordChange,
                        label = "Konfirmasi Password Baru",
                        placeholder = "Ulangi password baru",
                        errorMessage = confirmPasswordError,
                        imeAction = ImeAction.Done,
                        onImeAction = {
                            focusManager.clearFocus()
                            viewModel.resetPassword()
                        }
                    )
                }

                // STEP 4: SUCCESS
                ForgotPasswordStep.SUCCESS -> {
                    Spacer(modifier = Modifier.height(32.dp))

                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(72.dp)
                                .clip(CircleShape)
                                .background(Success0),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Lucide.CircleCheck,
                                contentDescription = null,
                                tint = Success,
                                modifier = Modifier.size(38.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        Text(
                            text = "Password Berhasil Diubah",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Kata sandi akun SAKU Anda telah berhasil diperbarui. Silakan masuk menggunakan kata sandi baru Anda.",
                            fontSize = 14.sp,
                            color = TextSecondary,
                            textAlign = TextAlign.Center,
                            lineHeight = 20.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
