package com.example.saku.app.features.splash

import android.app.Activity
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.saku.app.R
import com.example.saku.app.core.data.TokenManager
import com.example.saku.app.core.security.RootCheckResult
import com.example.saku.app.core.security.RootDetectionHelper
import com.example.saku.app.core.ui.components.feedback.RootWarningDialog
import com.example.saku.app.ui.theme.Primary
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

@Composable
fun SplashScreen(
    onNavigateToOnboarding: () -> Unit,
    onNavigateToLogin: () -> Unit,
    onNavigateToHome: () -> Unit,
    onNavigateToKycPending: () -> Unit
) {
    val context = LocalContext.current
    val tokenManager = remember { TokenManager.getInstance(context) }
    val scope = rememberCoroutineScope()
    val scale = remember { Animatable(0.6f) }
    var rootCheckResult by remember { mutableStateOf<RootCheckResult?>(null) }
    var showRootWarning by remember { mutableStateOf(false) }

    val proceedNavigation: suspend () -> Unit = {
        val isLoggedIn = tokenManager.isLoggedInFlow.first()
        if (isLoggedIn) {
            val userSession = tokenManager.userSessionFlow.first()
            if (userSession?.isKycVerified == true) {
                onNavigateToHome()
            } else {
                onNavigateToKycPending()
            }
        } else {
            val isOnboardingDone = tokenManager.isOnboardingCompletedFlow.first()
            if (isOnboardingDone) {
                onNavigateToHome()
            } else {
                onNavigateToOnboarding()
            }
        }
    }

    LaunchedEffect(key1 = true) {
        scale.animateTo(
            targetValue = 1f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            )
        )
        delay(1000)

        // Run Security & Root Detection Check
        val result = RootDetectionHelper.checkDeviceSecurity(context)
        if (result.isRooted) {
            rootCheckResult = result
            showRootWarning = true
        } else {
            proceedNavigation()
        }
    }

    if (showRootWarning && rootCheckResult != null) {
        RootWarningDialog(
            reasons = rootCheckResult!!.reasons,
            onDismiss = {
                showRootWarning = false
                scope.launch {
                    proceedNavigation()
                }
            },
            onExitApp = {
                (context as? Activity)?.finishAffinity()
            }
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.scale(scale.value)
        ) {
            Image(
                painter = painterResource(id = R.drawable.saku_logo),
                contentDescription = "Logo SAKU",
                modifier = Modifier.size(96.dp)
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "SAKU",
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Primary,
                letterSpacing = 0.5.sp
            )
        }
    }
}