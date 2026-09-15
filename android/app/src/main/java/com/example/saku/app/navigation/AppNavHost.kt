package com.example.saku.app.navigation

import androidx.compose.runtime.Composable
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
import com.example.saku.app.features.home.HomeScreen
import com.example.saku.app.features.loans.apply.LoanApplyScreen
import com.example.saku.app.features.loans.detail.LoanDetailScreen
import com.example.saku.app.features.notification.NotificationScreen
import com.example.saku.app.features.profile.edit.EditProfileScreen
import com.example.saku.app.features.profile.password.ChangePasswordScreen
import com.example.saku.app.features.sandbox.SandboxScreen
import com.example.saku.app.features.simulation.LoanSimulationScreen
import com.example.saku.app.features.splash.SplashScreen

@Composable
fun AppNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route,
        modifier = modifier
    ) {
        composable(Screen.Splash.route) {
            SplashScreen(
                onNavigateToLogin = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                },
                onNavigateToHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
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
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Notifications.route) {
            NotificationScreen(
                onNavigateBack = { navController.popBackStack() }
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
