package com.example.saku.app.navigation

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.saku.app.features.auth.forgotpassword.ForgotPasswordScreen
import com.example.saku.app.features.auth.login.LoginScreen
import com.example.saku.app.features.auth.register.RegisterScreen
import com.example.saku.app.features.auth.verification.KycPendingScreen
import com.example.saku.app.features.home.HomeScreen
import com.example.saku.app.features.loans.apply.LoanApplyScreen
import com.example.saku.app.features.loans.detail.LoanDetailScreen
import com.example.saku.app.features.loans.revision.LoanRevisionScreen
import com.example.saku.app.features.notification.NotificationScreen
import com.example.saku.app.features.onboarding.OnboardingScreen
import com.example.saku.app.features.profile.edit.EditProfileScreen
import com.example.saku.app.features.profile.password.ChangePasswordScreen
import com.example.saku.app.features.sandbox.SandboxScreen
import com.example.saku.app.features.simulation.LoanSimulationScreen
import com.example.saku.app.features.splash.SplashScreen

@Composable
fun AppNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    pendingRoute: String? = null,
    onRouteHandled: () -> Unit = {}
) {
    // Handle deep-link when app is already in a destination past Splash/Auth
    LaunchedEffect(pendingRoute) {
        if (!pendingRoute.isNullOrBlank()) {
            val currentRoute = navController.currentDestination?.route
            if (currentRoute != null &&
                currentRoute != Screen.Splash.route &&
                currentRoute != Screen.Login.route &&
                currentRoute != Screen.Register.route &&
                currentRoute != Screen.Onboarding.route
            ) {
                try {
                    navController.navigate(pendingRoute)
                } catch (e: Exception) {
                    Log.e("AppNavHost", "Failed to navigate to pendingRoute $pendingRoute: ${e.message}")
                }
                onRouteHandled()
            }
        }
    }

    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route,
        modifier = modifier,
        enterTransition = { fadeIn(animationSpec = tween(220, easing = FastOutSlowInEasing)) },
        exitTransition = { fadeOut(animationSpec = tween(180, easing = FastOutSlowInEasing)) },
        popEnterTransition = { fadeIn(animationSpec = tween(220, easing = FastOutSlowInEasing)) },
        popExitTransition = { fadeOut(animationSpec = tween(180, easing = FastOutSlowInEasing)) }
    ) {
        composable(Screen.Splash.route) {
            SplashScreen(
                onNavigateToOnboarding = {
                    navController.navigate(Screen.Onboarding.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                },
                onNavigateToLogin = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                },
                onNavigateToHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                    if (!pendingRoute.isNullOrBlank()) {
                        try {
                            navController.navigate(pendingRoute)
                        } catch (e: Exception) {
                            Log.e("AppNavHost", "Failed to navigate to route $pendingRoute from Splash: ${e.message}")
                        }
                        onRouteHandled()
                    }
                },
                onNavigateToKycPending = {
                    val target = if (!pendingRoute.isNullOrBlank() && pendingRoute.contains("kyc")) pendingRoute else Screen.KycPending.route
                    navController.navigate(target) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                    onRouteHandled()
                }
            )
        }

        composable(Screen.Onboarding.route) {
            OnboardingScreen(
                onNavigateToLogin = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Onboarding.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Login.route) {
            LoginScreen(
                onNavigateToHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onNavigateToRegister = {
                    navController.navigate(Screen.Register.route)
                },
                onNavigateToForgotPassword = {
                    navController.navigate(Screen.ForgotPassword.route)
                },
                onNavigateToKycPending = {
                    navController.navigate(Screen.KycPending.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onNavigateToSandbox = {
                    navController.navigate(Screen.Sandbox.route)
                }
            )
        }

        composable(Screen.Register.route) {
            RegisterScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToLogin = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Register.route) { inclusive = true }
                    }
                },
                onNavigateToKycPending = {
                    navController.navigate(Screen.KycPending.route) {
                        popUpTo(Screen.Register.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.KycPending.route) {
            KycPendingScreen(
                onNavigateToHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.KycPending.route) { inclusive = true }
                    }
                },
                onNavigateToLogin = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.KycPending.route) { inclusive = true }
                    }
                },
                onNavigateToNotifications = {
                    navController.navigate(Screen.Notifications.route)
                }
            )
        }

        composable(Screen.ForgotPassword.route) {
            ForgotPasswordScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToLogin = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.ForgotPassword.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Home.route) {
            HomeScreen(
                onNavigateToLogin = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                },
                onNavigateToSandbox = {
                    navController.navigate(Screen.Sandbox.route)
                },
                onNavigateToApplyLoan = {
                    navController.navigate(Screen.LoanApply.route)
                },
                onNavigateToLoanDetail = { loanId ->
                    navController.navigate(Screen.LoanDetail.createRoute(loanId))
                },
                onNavigateToNotifications = {
                    navController.navigate(Screen.Notifications.route)
                },
                onNavigateToEditProfile = { tab ->
                    navController.navigate(Screen.EditProfile.createRoute(tab))
                },
                onNavigateToChangePassword = {
                    navController.navigate(Screen.ChangePassword.route)
                },
                onNavigateToLoanSimulation = {
                    navController.navigate(Screen.LoanSimulation.route)
                }
            )
        }

        composable(
            route = Screen.LoanApply.route,
            arguments = listOf(
                navArgument("amount") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                },
                navArgument("tenor") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                }
            )
        ) { backStackEntry ->
            val initialAmount = backStackEntry.arguments?.getString("amount")?.toDoubleOrNull()
            val initialTenor = backStackEntry.arguments?.getString("tenor")?.toIntOrNull()
            LoanApplyScreen(
                initialAmount = initialAmount,
                initialTenor = initialTenor,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                },
                onNavigateToDetail = { loanId ->
                    navController.navigate(Screen.LoanDetail.createRoute(loanId)) {
                        popUpTo(Screen.Home.route)
                    }
                }
            )
        }

        composable(
            route = Screen.LoanDetail.route,
            arguments = listOf(
                navArgument("loanId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val loanId = backStackEntry.arguments?.getString("loanId") ?: ""
            LoanDetailScreen(
                loanId = loanId,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToRevision = { id ->
                    navController.navigate(Screen.LoanRevision.createRoute(id))
                }
            )
        }

        composable(
            route = Screen.LoanRevision.route,
            arguments = listOf(
                navArgument("loanId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val loanId = backStackEntry.arguments?.getString("loanId") ?: ""
            LoanRevisionScreen(
                loanId = loanId,
                onNavigateBack = { navController.popBackStack() },
                onRevisionSuccess = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.Notifications.route) {
            NotificationScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToLoanDetail = { loanId ->
                    navController.navigate(Screen.LoanDetail.createRoute(loanId))
                },
                onNavigateToKycPending = {
                    navController.navigate(Screen.KycPending.route)
                }
            )
        }

        composable(
            route = Screen.EditProfile.route,
            arguments = listOf(
                navArgument("tab") {
                    type = NavType.StringType
                    defaultValue = "REKENING"
                }
            )
        ) { backStackEntry ->
            val tab = backStackEntry.arguments?.getString("tab") ?: "REKENING"
            EditProfileScreen(
                initialTab = tab,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.ChangePassword.route) {
            ChangePasswordScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.LoanSimulation.route) {
            LoanSimulationScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToApplyLoan = { amount, tenor ->
                    navController.navigate(Screen.LoanApply.createRoute(amount, tenor))
                }
            )
        }

        composable(Screen.Sandbox.route) {
            SandboxScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
