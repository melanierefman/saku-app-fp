package com.example.saku.app.navigation

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Onboarding : Screen("onboarding")
    object Login : Screen("login")
    object Register : Screen("register")
    object KycPending : Screen("kyc_pending")
    object ForgotPassword : Screen("forgot_password")
    object Home : Screen("home")
    object Sandbox : Screen("sandbox")
    object LoanApply : Screen("loan_apply?amount={amount}&tenor={tenor}") {
        fun createRoute(amount: Double? = null, tenor: Int? = null): String {
            return if (amount != null && tenor != null) {
                "loan_apply?amount=${amount.toLong()}&tenor=$tenor"
            } else if (amount != null) {
                "loan_apply?amount=${amount.toLong()}"
            } else if (tenor != null) {
                "loan_apply?tenor=$tenor"
            } else {
                "loan_apply"
            }
        }
    }
    object LoanDetail : Screen("loan_detail/{loanId}") {
        fun createRoute(loanId: String): String = "loan_detail/$loanId"
    }
    object Notifications : Screen("notifications")
    object EditProfile : Screen("edit_profile?tab={tab}") {
        fun createRoute(tab: String = "REKENING"): String = "edit_profile?tab=$tab"
    }
    object ChangePassword : Screen("change_password")
    object LoanRevision : Screen("loan_revision/{loanId}") {
        fun createRoute(loanId: String): String = "loan_revision/$loanId"
    }
    object LoanSimulation : Screen("loan_simulation")
}