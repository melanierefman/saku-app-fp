package com.example.saku.app.features.auth.login

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusDirection
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
import com.composables.icons.lucide.CircleAlert
import com.composables.icons.lucide.Lucide
import com.example.saku.app.R
import com.example.saku.app.core.network.ApiResult
import com.example.saku.app.core.ui.components.Button
import com.example.saku.app.core.ui.components.ButtonSize
import com.example.saku.app.core.ui.components.ButtonVariant
import com.example.saku.app.core.ui.components.PasswordField
import com.example.saku.app.core.ui.components.TextField
import com.example.saku.app.ui.theme.Border
import com.example.saku.app.ui.theme.Error
import com.example.saku.app.ui.theme.Error0
import com.example.saku.app.ui.theme.Error60
import com.example.saku.app.ui.theme.Neutral60
import com.example.saku.app.ui.theme.Primary
import com.example.saku.app.ui.theme.Primary0
import com.example.saku.app.ui.theme.Surface
import com.example.saku.app.ui.theme.TextPrimary
import com.example.saku.app.ui.theme.TextSecondary

@Composable
fun LoginScreen(
    onNavigateToHome: () -> Unit,
    onNavigateToRegister: () -> Unit,
    onNavigateToForgotPassword: () -> Unit,
    onNavigateToSandbox: (() -> Unit)? = null,
    viewModel: LoginViewModel = viewModel()
) {
    val username by viewModel.username.collectAsState()
    val password by viewModel.password.collectAsState()
    val usernameError by viewModel.usernameError.collectAsState()
    val passwordError by viewModel.passwordError.collectAsState()
    val loginState by viewModel.loginState.collectAsState()

    val focusManager = LocalFocusManager.current
    val snackbarHostState = remember { SnackbarHostState() }
    val scrollState = rememberScrollState()

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
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
                val minScreenHeight = maxHeight
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(scrollState)
                        .padding(horizontal = 24.dp, vertical = 20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = (minScreenHeight - 40.dp).coerceAtLeast(0.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            // Card Container (White with rounded corners & glassmorphic border)
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
                        // SAKU Official Logo
                        Image(
                            painter = painterResource(id = R.drawable.saku_logo),
                            contentDescription = "Logo SAKU",
                            modifier = Modifier
                                .size(56.dp)
                                .clip(RoundedCornerShape(16.dp))
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        // Title
                        Text(
                            text = "Selamat Datang",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        // Subtitle
                        Text(
                            text = "Kelola keuangan Anda dengan SAKU.",
                            fontSize = 12.5.sp,
                            fontWeight = FontWeight.Normal,
                            color = TextSecondary,
                            textAlign = TextAlign.Center,
                            lineHeight = 16.sp
                        )

                        Spacer(modifier = Modifier.height(22.dp))

                        // Error Alert Banner
                        if (loginState is ApiResult.Error) {
                            val errorMsg = (loginState as ApiResult.Error).message
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

                        // Email / Username Field
                        TextField(
                            value = username,
                            onValueChange = viewModel::onUsernameChange,
                            label = "Email/username",
                            placeholder = "Masukkan email/username",
                            isError = usernameError != null,
                            errorMessage = usernameError,
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Email,
                                imeAction = ImeAction.Next
                            ),
                            keyboardActions = KeyboardActions(
                                onNext = { focusManager.moveFocus(FocusDirection.Down) }
                            )
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        // Password Header Row
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Password",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium,
                                color = if (passwordError != null) Error else Neutral60
                            )
                            Text(
                                text = "Lupa Password?",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Primary,
                                modifier = Modifier
                                    .clickable { onNavigateToForgotPassword() }
                                    .padding(vertical = 2.dp)
                            )
                        }

                        // Password Field
                        PasswordField(
                            value = password,
                            onValueChange = viewModel::onPasswordChange,
                            label = "",
                            placeholder = "Masukkan password",
                            errorMessage = passwordError,
                            leadingIcon = null,
                            imeAction = ImeAction.Done,
                            onImeAction = {
                                focusManager.clearFocus()
                                viewModel.login(onSuccess = onNavigateToHome)
                            }
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        // Submit Button "Masuk"
                        Button(
                            text = "Masuk",
                            isLoading = loginState is ApiResult.Loading,
                            onClick = {
                                focusManager.clearFocus()
                                viewModel.login(onSuccess = onNavigateToHome)
                            },
                            variant = ButtonVariant.Primary,
                            size = ButtonSize.LG,
                            fullWidth = true
                        )

                        Spacer(modifier = Modifier.height(18.dp))

                        // Footer Text: "Belum punya akun? Daftar Sekarang"
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "Belum punya akun? ",
                                fontSize = 12.5.sp,
                                color = TextSecondary
                            )
                            Text(
                                text = "Daftar Sekarang",
                                fontSize = 12.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = Primary,
                                modifier = Modifier.clickable { onNavigateToRegister() }
                            )
                        }
                    }
                }

                // UI Sandbox Dev Launcher
                if (onNavigateToSandbox != null) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "🛠️ UI Component Sandbox",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White.copy(alpha = 0.85f),
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { onNavigateToSandbox() }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }
        }
    }
}
}
}
}


