package com.example.saku.app.features.home

import androidx.compose.foundation.layout.heightIn
import androidx.compose.material3.CircularProgressIndicator
import com.example.saku.app.core.network.dto.NotifikasiItemDto
import com.example.saku.app.core.network.dto.SimulasiPinjamanResponseDto
import com.example.saku.app.features.history.HistoryScreen
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.TrendingUp
import androidx.compose.material.icons.rounded.Calculate
import androidx.compose.material.icons.rounded.History
import androidx.compose.material.icons.rounded.Speed
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import kotlinx.coroutines.delay
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.saku.app.ui.theme.OverusedGrotesk
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.platform.LocalContext
import com.composables.icons.lucide.Bell
import com.composables.icons.lucide.ChevronRight
import com.composables.icons.lucide.CircleCheck
import com.composables.icons.lucide.CreditCard
import com.composables.icons.lucide.Eye
import com.composables.icons.lucide.EyeOff
import com.composables.icons.lucide.House
import com.composables.icons.lucide.Info
import com.composables.icons.lucide.LogOut
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Plus
import com.composables.icons.lucide.Receipt
import com.composables.icons.lucide.ShieldCheck
import com.composables.icons.lucide.TrendingUp
import com.composables.icons.lucide.User
import com.composables.icons.lucide.Wallet
import com.example.saku.app.R
import com.example.saku.app.core.network.dto.AngsuranItemDto
import com.example.saku.app.core.network.dto.LoanApplicationItemDto
import com.example.saku.app.core.ui.components.Badge
import com.example.saku.app.core.ui.components.BadgeSize
import com.example.saku.app.core.ui.components.BadgeVariant
import com.example.saku.app.core.ui.components.BottomNavBar
import com.example.saku.app.core.ui.components.BottomNavItem
import com.example.saku.app.core.ui.components.Button
import com.example.saku.app.core.ui.components.ButtonSize
import com.example.saku.app.core.ui.components.ButtonVariant
import com.example.saku.app.core.ui.components.ConfirmationDialog
import com.example.saku.app.core.ui.components.DialogType
import com.example.saku.app.ui.theme.Background
import com.example.saku.app.ui.theme.Border
import com.example.saku.app.ui.theme.Error
import com.example.saku.app.ui.theme.Neutral20
import com.example.saku.app.ui.theme.Primary
import com.example.saku.app.ui.theme.Primary0
import com.example.saku.app.ui.theme.SAKUAppTheme
import com.example.saku.app.ui.theme.Success
import com.example.saku.app.ui.theme.Surface
import com.example.saku.app.ui.theme.TextMuted
import com.example.saku.app.ui.theme.TextPrimary
import com.example.saku.app.ui.theme.TextSecondary
import java.text.NumberFormat
import java.util.Locale

import com.example.saku.app.features.loans.payment.PaymentInfoBottomSheet

import com.example.saku.app.ui.theme.Neutral
import org.koin.androidx.compose.koinViewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToLogin: () -> Unit,
    onNavigateToRegister: () -> Unit = {},
    onNavigateToSandbox: (() -> Unit)? = null,
    onNavigateToApplyLoan: () -> Unit = {},
    onNavigateToLoanDetail: (String) -> Unit = {},
    onNavigateToNotifications: () -> Unit = {},
    onNavigateToEditProfile: (String) -> Unit = {},
    onNavigateToChangePassword: () -> Unit = {},
    onNavigateToLoanSimulation: () -> Unit = {},
    viewModel: HomeViewModel = koinViewModel(),
) {
    val context = LocalContext.current
    val isLoggedIn by viewModel.isLoggedIn.collectAsState()
    val userSession by viewModel.userSession.collectAsState()
    val customerProfile by viewModel.customerProfile.collectAsState()
    val myLoans by viewModel.myLoans.collectAsState()
    val isBalanceVisible by viewModel.isBalanceVisible.collectAsState()
    val showLogoutDialog by viewModel.showLogoutDialog.collectAsState()
    val unreadNotifikasiCount by viewModel.unreadNotifikasiCount.collectAsState()
    val currentNavRoute by viewModel.currentNavRoute.collectAsState()

    val selectedHistoryFilter by viewModel.selectedHistoryFilter.collectAsState()

    var showPaymentSheet by remember { mutableStateOf(false) }
    var selectedLoanForPayment by remember { mutableStateOf<LoanApplicationItemDto?>(null) }
    var selectedAngsuranForPayment by remember { mutableStateOf<AngsuranItemDto?>(null) }
    var showCreditScoreDialog by remember { mutableStateOf(false) }
    var showUpgradeLimitDialog by remember { mutableStateOf(false) }

    val currencyFormatter = remember {
        NumberFormat.getNumberInstance(Locale.forLanguageTag("id-ID"))
    }

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.fetchUnreadNotificationCount()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    // Refresh data saat HomeScreen aktif / tab berpindah
    LaunchedEffect(currentNavRoute, isLoggedIn) {
        if (isLoggedIn) {
            viewModel.fetchDashboardData()
            com.example.saku.app.MainActivity.syncFcmToken(context)
        }
    }

    val totalPlafond = customerProfile?.totalPlafond ?: 50_000_000.0
    val usedPlafond = customerProfile?.usedPlafond ?: 0.0
    val availablePlafond = customerProfile?.availablePlafond ?: (totalPlafond - usedPlafond)
    val displayName =
        customerProfile?.nama ?: (userSession?.nama ?: (userSession?.username ?: "Nasabah SAKU"))

    val activeLoans = remember(myLoans) {
        myLoans.filter { loan ->
            val s = (loan.statusPengajuan ?: "").uppercase()
            s !in listOf("DITOLAK", "PENGAJUAN_DITOLAK", "REJECTED", "DITOLAK_MARKETING", "DITOLAK_BM", "REJECT", "BATAL", "CANCELLED", "PAID", "LUNAS")
        }
    }

    val inProgressLoans = remember(myLoans) {
        myLoans.filter { loan ->
            val s = (loan.statusPengajuan ?: "").uppercase()
            s !in listOf("DITOLAK", "PENGAJUAN_DITOLAK", "REJECTED", "DITOLAK_MARKETING", "DITOLAK_BM", "REJECT", "BATAL", "CANCELLED", "PAID", "LUNAS", "DICAIRKAN", "DISBURSED")
        }
    }
    val inProcessAmount = remember(inProgressLoans) {
        inProgressLoans.sumOf { it.jumlahPinjaman ?: 0.0 }
    }
    val hasDisbursedLoan = remember(myLoans) {
        myLoans.any {
            val s = (it.statusPengajuan ?: "").uppercase()
            s in listOf("DICAIRKAN", "DISBURSED")
        }
    }

    val handleAjukanClick: () -> Unit = {
        if (inProgressLoans.isNotEmpty()) {
            // Arahkan ke layar Simulasi Pinjaman agar nasabah tetap bisa mengecek estimasi cicilan
            onNavigateToLoanSimulation()
        } else if (availablePlafond >= 500_000.0) {
            onNavigateToApplyLoan()
        } else {
            // Jika plafond belum mencukupi, arahkan juga ke simulasi pinjaman
            onNavigateToLoanSimulation()
        }
    }

    val navItems =
        listOf(
            BottomNavItem(route = "home", title = "Beranda", icon = Lucide.House),
            BottomNavItem(route = "loans", title = "Pinjaman", icon = Lucide.Wallet),
            BottomNavItem(route = "apply", title = "Ajukan", icon = Lucide.Plus, isCenterAction = true),
            BottomNavItem(route = "bills", title = "Tagihan", icon = Lucide.Receipt),
            BottomNavItem(route = "profile", title = "Profil", icon = Lucide.User),
        )

    Scaffold(
        bottomBar = {
            if (isLoggedIn) {
                BottomNavBar(
                    items = navItems,
                    currentRoute = currentNavRoute,
                    onItemClick = { item ->
                        if (item.route == "apply") {
                            handleAjukanClick()
                        } else {
                            viewModel.setNavRoute(item.route)
                        }
                    },
                )
            }
        },
        containerColor = Background,
    ) { innerPadding ->
        val tabOrder = remember { listOf("home", "loans", "bills", "history", "profile") }
        AnimatedContent(
            targetState = currentNavRoute,
            transitionSpec = {
                val initialIndex = tabOrder.indexOf(initialState).let { if (it == -1) 0 else it }
                val targetIndex = tabOrder.indexOf(targetState).let { if (it == -1) 0 else it }

                if (targetIndex >= initialIndex) {
                    (slideInHorizontally(
                        initialOffsetX = { width -> (width * 0.15f).toInt() },
                        animationSpec = tween(durationMillis = 240, easing = FastOutSlowInEasing)
                    ) + fadeIn(animationSpec = tween(durationMillis = 200))).togetherWith(
                        slideOutHorizontally(
                            targetOffsetX = { width -> (-width * 0.15f).toInt() },
                            animationSpec = tween(durationMillis = 240, easing = FastOutSlowInEasing)
                        ) + fadeOut(animationSpec = tween(durationMillis = 180))
                    )
                } else {
                    (slideInHorizontally(
                        initialOffsetX = { width -> (-width * 0.15f).toInt() },
                        animationSpec = tween(durationMillis = 240, easing = FastOutSlowInEasing)
                    ) + fadeIn(animationSpec = tween(durationMillis = 200))).togetherWith(
                        slideOutHorizontally(
                            targetOffsetX = { width -> (width * 0.15f).toInt() },
                            animationSpec = tween(durationMillis = 240, easing = FastOutSlowInEasing)
                        ) + fadeOut(animationSpec = tween(durationMillis = 180))
                    )
                }
            },
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            label = "tab_content_transition"
        ) { route ->
            when (route) {
                "home" -> {
                    HomeTabContent(
                        displayName = displayName,
                        isLoggedIn = isLoggedIn,
                        sukuBunga = customerProfile?.sukuBunga,
                        availablePlafond = availablePlafond,
                        totalPlafond = totalPlafond,
                        usedPlafond = usedPlafond,
                        isBalanceVisible = isBalanceVisible,
                        unreadCount = unreadNotifikasiCount,
                        activeLoan = activeLoans.firstOrNull(),
                        activeLoansCount = activeLoans.size,
                        inProcessAmount = inProcessAmount,
                        hasInProcessLoan = inProgressLoans.isNotEmpty(),
                        hasDisbursedLoan = hasDisbursedLoan,
                        currencyFormatter = currencyFormatter,
                        onToggleVisibility = viewModel::toggleBalanceVisibility,
                        onAjukanClick = handleAjukanClick,
                        onBayarClick = {
                            val active = activeLoans.firstOrNull()
                            if (active != null) {
                                selectedLoanForPayment = active
                                selectedAngsuranForPayment = active.listAngsuran?.firstOrNull { it.statusBayar != "LUNAS" && it.statusBayar != "PAID" }
                                showPaymentSheet = true
                            } else {
                                viewModel.setNavRoute("bills")
                            }
                        },
                        onDetailLoanClick = { loanId -> onNavigateToLoanDetail(loanId) },
                        onSimulasiClick = onNavigateToLoanSimulation,
                        onCreditScoreClick = { showCreditScoreDialog = true },
                        onUpgradeLimitClick = { showUpgradeLimitDialog = true },
                        onRiwayatClick = { viewModel.setNavRoute("history") },
                        onNotificationClick = onNavigateToNotifications,
                        onSandboxClick = onNavigateToSandbox,
                        onLogoutClick = { viewModel.setLogoutDialogVisible(true) },
                        onNavigateToLogin = onNavigateToLogin,
                        onNavigateToRegister = onNavigateToRegister,
                    )
                }
                "loans" -> {
                    LoansTabContent(
                        customerProfile = customerProfile,
                        myLoans = myLoans,
                        availablePlafond = availablePlafond,
                        totalPlafond = totalPlafond,
                        usedPlafond = usedPlafond,
                        currencyFormatter = currencyFormatter,
                        onAjukanClick = handleAjukanClick,
                        onSimulasiClick = onNavigateToLoanSimulation,
                        onPayClick = { loan ->
                            selectedLoanForPayment = loan
                            selectedAngsuranForPayment = loan.listAngsuran?.firstOrNull { it.statusBayar != "LUNAS" && it.statusBayar != "PAID" }
                            showPaymentSheet = true
                        },
                        onDetailClick = { loan ->
                            loan.id?.let { onNavigateToLoanDetail(it) }
                        },
                    )
                }
                "bills" -> {
                    BillsTabContent(
                        myLoans = myLoans,
                        customerProfile = customerProfile,
                        currencyFormatter = currencyFormatter,
                        isAjukanEnabled = availablePlafond >= 500_000.0 && inProgressLoans.isEmpty(),
                        onPayClick = { loan, angsuran ->
                            selectedLoanForPayment = loan
                            selectedAngsuranForPayment = angsuran
                            showPaymentSheet = true
                        },
                        onDetailClick = { loan ->
                            loan.id?.let { onNavigateToLoanDetail(it) }
                        },
                        onAjukanClick = handleAjukanClick,
                        onRefresh = viewModel::fetchDashboardData,
                    )
                }
                "history" -> {
                    HistoryScreen(
                        myLoans = myLoans,
                        selectedFilter = selectedHistoryFilter,
                        onFilterSelect = viewModel::setHistoryFilter,
                        currencyFormatter = currencyFormatter,
                        isAjukanEnabled = availablePlafond >= 500_000.0 && inProgressLoans.isEmpty(),
                        onAjukanClick = handleAjukanClick,
                        onDetailClick = { loan ->
                            loan.id?.let { onNavigateToLoanDetail(it) }
                        },
                        onRefresh = viewModel::fetchDashboardData,
                        onBackClick = { viewModel.setNavRoute("home") },
                    )
                }
                "profile" -> {
                    ProfileTabContent(
                        customerProfile = customerProfile,
                        userSession = userSession,
                        onLogoutClick = { viewModel.setLogoutDialogVisible(true) },
                        onNotificationClick = onNavigateToNotifications,
                        onSimulasiClick = onNavigateToLoanSimulation,
                        onEditProfileClick = onNavigateToEditProfile,
                        onChangePasswordClick = onNavigateToChangePassword,
                        onSandboxClick = onNavigateToSandbox,
                    )
                }
            }
        }
    }

    // Logout Confirmation Dialog
    ConfirmationDialog(
        visible = showLogoutDialog,
        title = "Keluar dari Akun?",
        message = "Apakah Anda yakin ingin keluar dari akun Anda?",
        confirmButtonText = "Keluar",
        dismissButtonText = "Batal",
        type = DialogType.DESTRUCTIVE,
        icon = Lucide.LogOut,
        onConfirm = {
            viewModel.logout()
        },
        onDismiss = { viewModel.setLogoutDialogVisible(false) },
    )

    // Payment Info Bottom Sheet
    if (showPaymentSheet) {
        PaymentInfoBottomSheet(
            loan = selectedLoanForPayment ?: myLoans.firstOrNull(),
            angsuran = selectedAngsuranForPayment,
            onDismiss = {
                showPaymentSheet = false
                selectedLoanForPayment = null
                selectedAngsuranForPayment = null
            }
        )
    }

    // Credit Score Detail Dialog
    if (showCreditScoreDialog) {
        CreditScoreDetailDialog(
            skorKredit = customerProfile?.skorKredit ?: 81,
            tierName = customerProfile?.tierPlafond ?: "Tier Reguler",
            onDismiss = { showCreditScoreDialog = false },
            onPanduanUpgradeClick = {
                showCreditScoreDialog = false
                showUpgradeLimitDialog = true
            }
        )
    }

    // Upgrade Limit Guide Dialog
    if (showUpgradeLimitDialog) {
        UpgradeLimitGuideDialog(
            currentPlafond = totalPlafond,
            tierName = customerProfile?.tierPlafond ?: "Tier Reguler",
            currencyFormatter = currencyFormatter,
            onDismiss = { showUpgradeLimitDialog = false },
            onEditProfileClick = {
                showUpgradeLimitDialog = false
                onNavigateToEditProfile("pekerjaan")
            }
        )
    }
}

// Beranda Tab Content
@Composable
private fun HomeTabContent(
    displayName: String,
    isLoggedIn: Boolean = true,
    sukuBunga: Double? = null,
    availablePlafond: Double,
    totalPlafond: Double,
    usedPlafond: Double,
    isBalanceVisible: Boolean,
    unreadCount: Long = 0L,
    activeLoan: LoanApplicationItemDto?,
    activeLoansCount: Int,
    inProcessAmount: Double = 0.0,
    hasInProcessLoan: Boolean = false,
    hasDisbursedLoan: Boolean = false,
    currencyFormatter: NumberFormat,
    onToggleVisibility: () -> Unit,
    onAjukanClick: () -> Unit,
    onBayarClick: () -> Unit,
    onDetailLoanClick: (String) -> Unit,
    onSimulasiClick: () -> Unit,
    onCreditScoreClick: () -> Unit,
    onUpgradeLimitClick: () -> Unit,
    onRiwayatClick: () -> Unit,
    onNotificationClick: () -> Unit,
    onSandboxClick: (() -> Unit)?,
    onLogoutClick: () -> Unit,
    onNavigateToLogin: () -> Unit = {},
    onNavigateToRegister: () -> Unit = {},
) {
    val listState = rememberLazyListState()
    var isScrolled by remember { mutableStateOf(false) }
    val isAjukanEnabled = availablePlafond >= 500_000.0

    // Hysteresis scroll detection to avoid viewport resize oscillation / bouncing
    LaunchedEffect(listState) {
        snapshotFlow {
            Pair(listState.firstVisibleItemIndex, listState.firstVisibleItemScrollOffset)
        }.collect { (index, offset) ->
            if (index > 0 || offset > 45) {
                if (!isScrolled) isScrolled = true
            } else if (index == 0 && offset <= 5) {
                if (isScrolled) isScrolled = false
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
    ) {
        // 1. HEADER ATAS (SAKU Logo + Action Icons + Collapsible "HALO, NAMA LENGKAP" / "SELAMAT DATANG DI SAKU")
        HomeHeaderSection(
            displayName = displayName,
            unreadCount = unreadCount,
            isScrolled = isScrolled,
            isLoggedIn = isLoggedIn,
            onNotificationClick = onNotificationClick,
            onSandboxClick = onSandboxClick,
            onLogoutClick = onLogoutClick,
            onNavigateToLogin = onNavigateToLogin,
            onNavigateToRegister = onNavigateToRegister,
        )

        // 2. HERO CARD PLAFOND SAKU (Sticky di atas, Box Hitam mengecil saat di-scroll)
        Box(modifier = Modifier.padding(horizontal = 20.dp, vertical = 2.dp)) {
            PlafondMeshHeroCard(
                availablePlafond = availablePlafond,
                totalPlafond = totalPlafond,
                usedPlafond = usedPlafond,
                isBalanceVisible = isBalanceVisible,
                sukuBunga = sukuBunga,
                inProcessAmount = inProcessAmount,
                hasInProcessLoan = hasInProcessLoan,
                hasDisbursedLoan = hasDisbursedLoan,
                isScrolled = isScrolled,
                isLoggedIn = isLoggedIn,
                onToggleVisibility = onToggleVisibility,
                onSimulasiClick = onSimulasiClick,
                currencyFormatter = currencyFormatter,
            )
        }

        // 3. KONTEN YANG DI-SCROLL KE BAWAH (Menu Utama, Tagihan, Promo, Tips)
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentPadding = PaddingValues(bottom = 32.dp),
        ) {
            if (isLoggedIn) {
                // SECTION "MENU UTAMA" (Simulasi, Cek Skor, Naikkan Limit, Riwayat)
                item {
                    Spacer(modifier = Modifier.height(14.dp))
                    MenuUtamaSection(
                        onSimulasiClick = onSimulasiClick,
                        onCreditScoreClick = onCreditScoreClick,
                        onUpgradeLimitClick = onUpgradeLimitClick,
                        onRiwayatClick = onRiwayatClick,
                    )
                }

                // INFO TAGIHAN / PINJAMAN AKTIF CARD
                item {
                    Spacer(modifier = Modifier.height(18.dp))
                    Box(modifier = Modifier.padding(horizontal = 20.dp)) {
                        TagihanPinjamanAktifCard(
                            activeLoan = activeLoan,
                            activeLoansCount = activeLoansCount,
                            currencyFormatter = currencyFormatter,
                            onPayClick = onBayarClick,
                            onDetailClick = { activeLoan?.id?.let { onDetailLoanClick(it) } ?: onBayarClick() },
                        )
                    }
                }
            } else {
                // SECTION: KEUNGGULAN SAKU (Value Proposition)
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    KeunggulanSakuSection()
                }

                // SECTION: 3 LANGKAH MUDAH PENGAJUAN (How it Works)
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    LangkahPengajuanSection(onRegisterClick = onNavigateToRegister)
                }
            }

            // BANNER PROMO FULL WIDTH
            item {
                Spacer(modifier = Modifier.height(if (isLoggedIn) 20.dp else 16.dp))
                FullWidthPromoBannerSection(
                    onSimulasiClick = onSimulasiClick,
                    onAjukanClick = if (isLoggedIn) onAjukanClick else onNavigateToRegister,
                )
            }

            // TIPS & LITERASI KEUANGAN
            item {
                Spacer(modifier = Modifier.height(22.dp))
                TipsLiterasiKeuanganSection(
                    onArticleClick = onSimulasiClick,
                )
            }
        }
    }
}

// 1. Top Bar ("Halo, Nama Lengkap" / "Masuk" & "Daftar")
@Composable
private fun HomeHeaderSection(
    displayName: String,
    unreadCount: Long = 0L,
    isScrolled: Boolean = false,
    isLoggedIn: Boolean = true,
    onNotificationClick: () -> Unit,
    onSandboxClick: (() -> Unit)?,
    onLogoutClick: () -> Unit,
    onNavigateToLogin: () -> Unit = {},
    onNavigateToRegister: () -> Unit = {},
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 20.dp, end = 20.dp, top = 8.dp, bottom = if (isScrolled) 2.dp else 4.dp)
            .animateContentSize()
    ) {
        // Top Row: Logo & Action Buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // SAKU Branding
            Text(
                text = "SAKU",
                fontSize = 20.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Primary,
                letterSpacing = 0.5.sp,
            )

            if (isLoggedIn) {
                // Action Icons (Notifikasi, Logout) - Bare Outline Icons
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(2.dp),
                ) {
                    // Notification Bell with Red Badge
                    IconButton(
                        onClick = onNotificationClick,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Box(contentAlignment = Alignment.TopEnd) {
                            Icon(
                                imageVector = Lucide.Bell,
                                contentDescription = "Notifikasi",
                                tint = TextPrimary,
                                modifier = Modifier.size(22.dp),
                            )
                            if (unreadCount > 0) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(Error)
                                )
                            }
                        }
                    }

                    // Logout Button
                    IconButton(
                        onClick = onLogoutClick,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Lucide.LogOut,
                            contentDescription = "Logout",
                            tint = TextPrimary,
                            modifier = Modifier.size(22.dp),
                        )
                    }
                }
            } else {
                // Tombol Masuk (Ghost/Text) dan Daftar (Filled Primary Compact)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    // 1. Tombol "Masuk" (Teks polos / Ghost button)
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .clickable(onClick = onNavigateToLogin)
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Masuk",
                            fontSize = 13.5.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Primary,
                            style = TextStyle(
                                fontFamily = OverusedGrotesk,
                                platformStyle = PlatformTextStyle(includeFontPadding = false),
                            )
                        )
                    }

                    // 2. Tombol "Daftar" (Filled Primary Compact)
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Primary)
                            .clickable(onClick = onNavigateToRegister)
                            .padding(horizontal = 14.dp, vertical = 6.5.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Daftar",
                            fontSize = 13.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            style = TextStyle(
                                fontFamily = OverusedGrotesk,
                                platformStyle = PlatformTextStyle(includeFontPadding = false),
                            )
                        )
                    }
                }
            }
        }

        // myBCA style "HALO, NAMA LENGKAP" (Dihide saat user scroll ke bawah)
        AnimatedVisibility(
            visible = !isScrolled,
            enter = expandVertically(tween(200)) + fadeIn(tween(200)),
            exit = shrinkVertically(tween(200)) + fadeOut(tween(200)),
        ) {
            Column {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = if (isLoggedIn) "HALO, ${displayName.uppercase()}" else "SELAMAT DATANG DI SAKU",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextSecondary,
                    letterSpacing = 0.5.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}

// 2. Hero Card Plafond SAKU
@Composable
private fun PlafondMeshHeroCard(
    availablePlafond: Double,
    totalPlafond: Double,
    usedPlafond: Double,
    isBalanceVisible: Boolean,
    sukuBunga: Double? = null,
    inProcessAmount: Double = 0.0,
    hasInProcessLoan: Boolean = false,
    hasDisbursedLoan: Boolean = false,
    isScrolled: Boolean = false,
    isLoggedIn: Boolean = true,
    onToggleVisibility: () -> Unit,
    onSimulasiClick: () -> Unit = {},
    currencyFormatter: NumberFormat,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = if (isScrolled) 3.dp else 5.dp,
                shape = RoundedCornerShape(20.dp),
                ambientColor = Primary,
                spotColor = Primary
            ),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
        ) {
            // Background Image
            Image(
                painter = painterResource(id = R.drawable.bg_card_saku),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                alignment = Alignment.CenterEnd,
                modifier = Modifier
                    .matchParentSize()
                    .graphicsLayer(
                        scaleX = 1.45f,
                        scaleY = 1.45f,
                        transformOrigin = TransformOrigin(0.88f, 0.5f),
                    ),
            )

            // Content Overlay
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp, vertical = if (isScrolled) 11.dp else 14.dp)
            ) {
                if (isLoggedIn) {
                    // Top Row: Plafon Pinjaman Anda + Eye Toggle
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.clickable(onClick = onToggleVisibility),
                        ) {
                            Text(
                                text = "Plafon Pinjaman Anda",
                                fontSize = 13.sp,
                                lineHeight = 15.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color.White.copy(alpha = 0.95f),
                                style = TextStyle(
                                    fontFamily = OverusedGrotesk,
                                    platformStyle = PlatformTextStyle(includeFontPadding = false),
                                    lineHeightStyle = LineHeightStyle(
                                        alignment = LineHeightStyle.Alignment.Center,
                                        trim = LineHeightStyle.Trim.Both
                                    )
                                )
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Icon(
                                imageVector = if (isBalanceVisible) Lucide.Eye else Lucide.EyeOff,
                                contentDescription = "Toggle saldo",
                                tint = Color.White.copy(alpha = 0.95f),
                                modifier = Modifier.size(15.dp),
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(if (isScrolled) 3.dp else 6.dp))

                    // Main Large Amount
                    Text(
                        text = if (isBalanceVisible) "Rp${currencyFormatter.format(availablePlafond)}" else "Rp ••••••••••",
                        fontSize = if (isScrolled) 22.sp else 26.sp,
                        lineHeight = if (isScrolled) 24.sp else 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        style = TextStyle(
                            fontFamily = OverusedGrotesk,
                            platformStyle = PlatformTextStyle(includeFontPadding = false),
                            lineHeightStyle = LineHeightStyle(
                                alignment = LineHeightStyle.Alignment.Center,
                                trim = LineHeightStyle.Trim.Both
                            )
                        )
                    )

                    Spacer(modifier = Modifier.height(3.dp))

                    Text(
                        text = "Tersedia untuk pengajuan",
                        fontSize = 11.sp,
                        lineHeight = 13.sp,
                        color = Color.White.copy(alpha = 0.9f),
                        style = TextStyle(
                            fontFamily = OverusedGrotesk,
                            platformStyle = PlatformTextStyle(includeFontPadding = false),
                            lineHeightStyle = LineHeightStyle(
                                alignment = LineHeightStyle.Alignment.Center,
                                trim = LineHeightStyle.Trim.Both
                            )
                        )
                    )

                    // Indikator Informative jika ada pengajuan yang sedang diproses
                    if (hasInProcessLoan && inProcessAmount > 0) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color.Black.copy(alpha = 0.28f))
                                .border(0.5.dp, Color.White.copy(alpha = 0.15f), RoundedCornerShape(8.dp))
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFFFD166))
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isBalanceVisible) "Rp ${currencyFormatter.format(inProcessAmount)} sedang dalam proses pengajuan" else "Sedang dalam proses pengajuan",
                                fontSize = 10.5.sp,
                                lineHeight = 13.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color.White.copy(alpha = 0.95f),
                                style = TextStyle(
                                    fontFamily = OverusedGrotesk,
                                    platformStyle = PlatformTextStyle(includeFontPadding = false),
                                )
                            )
                        }
                    }

                    // Sub-limit dark container (Total Plafond, Plafond Terpakai, Bunga)
                    // DIHIDE KETIKA DI-SCROLL KE BAWAH (Persis OVO di Gambar 2)
                    AnimatedVisibility(
                        visible = !isScrolled,
                        enter = expandVertically(tween(200)) + fadeIn(tween(200)),
                        exit = shrinkVertically(tween(200)) + fadeOut(tween(200)),
                    ) {
                        Column {
                            Spacer(modifier = Modifier.height(12.dp))
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color.Black.copy(alpha = 0.35f))
                                    .border(1.dp, Color.White.copy(alpha = 0.15f), RoundedCornerShape(12.dp))
                                    .padding(horizontal = 14.dp, vertical = 9.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                SubLimitMetricItem(
                                    label = "Total Plafond",
                                    value = if (isBalanceVisible) "Rp ${currencyFormatter.format(totalPlafond)}" else "Rp ••••••"
                                )
                                Box(
                                    modifier = Modifier
                                        .height(22.dp)
                                        .width(1.dp)
                                        .background(Color.White.copy(alpha = 0.22f))
                                )

                                SubLimitMetricItem(
                                    label = "Plafond Terpakai",
                                    value = if (isBalanceVisible) "Rp ${currencyFormatter.format(usedPlafond)}" else "Rp ••••••"
                                )

                                Box(
                                    modifier = Modifier
                                        .height(22.dp)
                                        .width(1.dp)
                                        .background(Color.White.copy(alpha = 0.22f))
                                )

                                SubLimitMetricItem(
                                    label = "Bunga",
                                    value = if (sukuBunga != null && sukuBunga > 0) {
                                        val pct = if (sukuBunga <= 1.0) sukuBunga * 100 else sukuBunga
                                        val formatted = if (pct % 1.0 == 0.0) "${pct.toLong()}%" else "${pct.toString().replace('.', ',')}%"
                                        "$formatted / bln"
                                    } else {
                                        "1,25% / bln"
                                    }
                                )
                            }
                        }
                    }
                } else {
                    // Mode Belum Login (Guest Mode)
                    Text(
                        text = "Plafon Pinjaman Hingga",
                        fontSize = 13.sp,
                        lineHeight = 15.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White.copy(alpha = 0.95f),
                        style = TextStyle(
                            fontFamily = OverusedGrotesk,
                            platformStyle = PlatformTextStyle(includeFontPadding = false),
                        )
                    )

                    Spacer(modifier = Modifier.height(if (isScrolled) 3.dp else 6.dp))

                    Text(
                        text = "Rp150.000.000",
                        fontSize = if (isScrolled) 22.sp else 26.sp,
                        lineHeight = if (isScrolled) 24.sp else 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        style = TextStyle(
                            fontFamily = OverusedGrotesk,
                            platformStyle = PlatformTextStyle(includeFontPadding = false),
                        )
                    )

                    Spacer(modifier = Modifier.height(3.dp))

                    Text(
                        text = "Bunga ringan mulai dari 0,75% / bln",
                        fontSize = 11.5.sp,
                        lineHeight = 14.sp,
                        color = Color.White.copy(alpha = 0.9f),
                        style = TextStyle(
                            fontFamily = OverusedGrotesk,
                            platformStyle = PlatformTextStyle(includeFontPadding = false),
                        )
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Tombol Putih Simulasi Pinjaman
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.White)
                            .clickable(onClick = onSimulasiClick)
                            .padding(vertical = 10.dp, horizontal = 16.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Calculate,
                            contentDescription = "Simulasi Pinjaman",
                            tint = Primary,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Simulasi Pinjaman",
                            fontSize = 13.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = Primary,
                            style = TextStyle(
                                fontFamily = OverusedGrotesk,
                                platformStyle = PlatformTextStyle(includeFontPadding = false),
                            )
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SubLimitMetricItem(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = label,
            fontSize = 10.5.sp,
            lineHeight = 12.sp,
            fontWeight = FontWeight.Medium,
            color = Color.White.copy(alpha = 0.9f),
            style = TextStyle(
                fontFamily = OverusedGrotesk,
                platformStyle = PlatformTextStyle(includeFontPadding = false),
                lineHeightStyle = LineHeightStyle(
                    alignment = LineHeightStyle.Alignment.Center,
                    trim = LineHeightStyle.Trim.Both
                )
            )
        )
        Spacer(modifier = Modifier.height(3.5.dp))
        Text(
            text = value,
            fontSize = 12.5.sp,
            lineHeight = 15.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            style = TextStyle(
                fontFamily = OverusedGrotesk,
                platformStyle = PlatformTextStyle(includeFontPadding = false),
                lineHeightStyle = LineHeightStyle(
                    alignment = LineHeightStyle.Alignment.Center,
                    trim = LineHeightStyle.Trim.Both
                )
            )
        )
    }
}

// 2.A Keunggulan SAKU (Khusus Mode Belum Login)
@Composable
private fun KeunggulanSakuSection() {
    Box(modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp)) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = Surface),
            border = BorderStroke(1.dp, Border),
        ) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(16.dp)
            ) {
                Text(
                    text = "Keunggulan SAKU",
                    fontSize = 14.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Solusi Aman Keuangan Untukmu",
                    fontSize = 11.5.sp,
                    fontWeight = FontWeight.Medium,
                    color = TextSecondary,
                    modifier = Modifier.padding(bottom = 12.dp),
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    KeunggulanItem(
                        icon = Lucide.CreditCard,
                        title = "Plafon Terukur",
                        desc = "Sesuai Kemampuan",
                        modifier = Modifier.weight(1f)
                    )
                    KeunggulanItem(
                        icon = Icons.AutoMirrored.Rounded.TrendingUp,
                        title = "Bunga Ringan",
                        desc = "Mulai 0,75%/bln",
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    KeunggulanItem(
                        icon = Icons.Rounded.Speed,
                        title = "Pencairan Cepat",
                        desc = "Cukup e-KTP",
                        modifier = Modifier.weight(1f)
                    )
                    KeunggulanItem(
                        icon = Lucide.ShieldCheck,
                        title = "Aman Terpercaya",
                        desc = "Data Terenkripsi",
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
private fun KeunggulanItem(
    icon: ImageVector,
    title: String,
    desc: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.padding(horizontal = 2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(Primary0),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = Primary,
                modifier = Modifier.size(20.dp)
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(
                text = title,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = desc,
                fontSize = 10.5.sp,
                color = TextSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

// 2.B 3 Langkah Mudah Pengajuan (Khusus Mode Belum Login)
@Composable
private fun LangkahPengajuanSection(
    onRegisterClick: () -> Unit = {}
) {
    Box(modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp)) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = Surface),
            border = BorderStroke(1.dp, Border),
        ) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(16.dp)
            ) {
                Text(
                    text = "3 Langkah Mudah Pengajuan",
                    fontSize = 14.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                )

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    LangkahItem(
                        stepNumber = "1",
                        title = "Daftar Akun",
                        desc = "Data & e-KTP",
                        modifier = Modifier.weight(1f)
                    )
                    Box(
                        modifier = Modifier
                            .padding(top = 14.dp)
                            .width(16.dp)
                            .height(1.dp)
                            .background(Border)
                    )
                    LangkahItem(
                        stepNumber = "2",
                        title = "Cek Plafon",
                        desc = "Limit otomatis",
                        modifier = Modifier.weight(1f)
                    )
                    Box(
                        modifier = Modifier
                            .padding(top = 14.dp)
                            .width(16.dp)
                            .height(1.dp)
                            .background(Border)
                    )
                    LangkahItem(
                        stepNumber = "3",
                        title = "Cairkan Dana",
                        desc = "Transfer instan",
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
private fun LangkahItem(
    stepNumber: String,
    title: String,
    desc: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(horizontal = 2.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(28.dp)
                .clip(CircleShape)
                .background(Primary),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = stepNumber,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = title,
            fontSize = 11.5.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = desc,
            fontSize = 10.sp,
            color = TextSecondary,
            textAlign = TextAlign.Center,
            lineHeight = 13.sp
        )
    }
}

// 3. Akses Cepat
@Composable
private fun MenuUtamaSection(
    onSimulasiClick: () -> Unit,
    onCreditScoreClick: () -> Unit,
    onUpgradeLimitClick: () -> Unit,
    onRiwayatClick: () -> Unit,
) {
    Box(modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp)) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = Surface),
            border = BorderStroke(1.dp, Border),
        ) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 14.dp)
            ) {
                Text(
                    text = "Akses Cepat",
                    fontSize = 14.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    modifier = Modifier.padding(start = 4.dp, bottom = 12.dp),
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    ActionItemColumn(
                        title = "Simulasi",
                        icon = Icons.Rounded.Calculate,
                        containerColor = Primary0,
                        onClick = onSimulasiClick,
                    )
                    ActionItemColumn(
                        title = "Cek Skor",
                        icon = Icons.Rounded.Speed,
                        containerColor = Primary0,
                        onClick = onCreditScoreClick,
                    )
                    ActionItemColumn(
                        title = "Naikkan Limit",
                        icon = Icons.AutoMirrored.Rounded.TrendingUp,
                        containerColor = Primary0,
                        onClick = onUpgradeLimitClick,
                    )
                    ActionItemColumn(
                        title = "Riwayat",
                        icon = Icons.Rounded.History,
                        containerColor = Primary0,
                        onClick = onRiwayatClick,
                    )
                }
            }
        }
    }
}

@Composable
private fun ActionItemColumn(
    title: String,
    icon: ImageVector,
    containerColor: Color = Primary0,
    iconColor: Color = Primary,
    enabled: Boolean = true,
    onClick: () -> Unit,
) {
    val effectiveContainerColor = if (enabled) containerColor else Color(0xFFF1F5F9)
    val effectiveIconColor = if (enabled) iconColor else Color(0xFF94A3B8)
    val effectiveTextColor = if (enabled) TextPrimary else TextMuted

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier =
            Modifier.clip(RoundedCornerShape(14.dp))
                .then(if (enabled) Modifier.clickable(onClick = onClick) else Modifier)
                .padding(horizontal = 6.dp, vertical = 4.dp),
    ) {
        Box(
            modifier = Modifier
                .size(52.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(effectiveContainerColor),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = effectiveIconColor,
                modifier = Modifier.size(32.dp),
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(text = title, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = effectiveTextColor)
    }
}

// 4. Info Tagihan / Pinjaman Aktif
@Composable
private fun TagihanPinjamanAktifCard(
    activeLoan: LoanApplicationItemDto?,
    activeLoansCount: Int,
    currencyFormatter: NumberFormat,
    onPayClick: () -> Unit,
    onDetailClick: () -> Unit,
) {
    val rawStatus = (activeLoan?.statusPengajuan ?: "PENDING").uppercase()
    val isDisbursed = rawStatus in listOf("DICAIRKAN", "DISBURSED")
    val isPending = !isDisbursed

    Box(modifier = Modifier.fillMaxWidth()) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = Surface),
            border = BorderStroke(1.dp, Border),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                // 1. Header: Title & Status Badge
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Tagihan & Pinjaman Aktif",
                        fontSize = 14.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        modifier = Modifier.weight(1f, fill = false)
                    )
                    if (activeLoan != null) {
                        Spacer(modifier = Modifier.width(8.dp))
                        val (bVariant, bText) = when (rawStatus) {
                            "DICAIRKAN", "DISBURSED" -> BadgeVariant.Primary to "Dicairkan"
                            "APPROVED", "DISETUJUI", "PENGAJUAN_DISETUJUI" -> BadgeVariant.Success to "Disetujui"
                            "MENUNGGU_PENCAIRAN" -> BadgeVariant.Success to "Menunggu Pencairan"
                            "SELESAI_DIREVIEW", "MENUNGGU_PERSETUJUAN", "DISETUJUI_MARKETING" -> BadgeVariant.Info to "Menunggu Persetujuan"
                            "VERIFIKASI_MARKETING", "MENUNGGU_REVIEW" -> BadgeVariant.Info to "Sedang Ditinjau"
                            "PERLU_REVISI", "REVISI" -> BadgeVariant.Warning to "Revisi Dokumen"
                            else -> BadgeVariant.Info to "Dalam Proses"
                        }
                        Badge(text = bText, variant = bVariant, size = BadgeSize.SM)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                if (activeLoan == null) {
                    // Empty state
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFFF0FDF4))
                            .border(1.dp, Color(0xFFBBF7D0), RoundedCornerShape(12.dp))
                            .padding(horizontal = 12.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(
                            imageVector = Lucide.CircleCheck,
                            contentDescription = null,
                            tint = Success,
                            modifier = Modifier.size(18.dp),
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Tidak ada tagihan aktif.",
                            fontSize = 12.sp,
                            color = Color(0xFF15803D),
                            fontWeight = FontWeight.Medium,
                        )
                    }
                } else {
                    val rawAmount = activeLoan.estimasiAngsuranBulanan ?: (activeLoan.jumlahPinjaman ?: 0.0)
                    val roundedAmount = Math.round(rawAmount)

                    // 2. Middle Section: Amount & Action Button aligned horizontally
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = if (isPending) "Estimasi Angsuran" else "Total Tagihan Bulan Ini",
                            fontSize = 12.sp,
                            lineHeight = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = TextSecondary,
                            style = TextStyle(
                                fontFamily = OverusedGrotesk,
                                platformStyle = PlatformTextStyle(includeFontPadding = false),
                                lineHeightStyle = LineHeightStyle(
                                    alignment = LineHeightStyle.Alignment.Center,
                                    trim = LineHeightStyle.Trim.Both
                                )
                            )
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Row(verticalAlignment = Alignment.Bottom) {
                                Text(
                                    text = "Rp ${currencyFormatter.format(roundedAmount)}",
                                    fontSize = 22.sp,
                                    lineHeight = 24.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Neutral,
                                    style = TextStyle(
                                        fontFamily = OverusedGrotesk,
                                        platformStyle = PlatformTextStyle(includeFontPadding = false),
                                        lineHeightStyle = LineHeightStyle(
                                            alignment = LineHeightStyle.Alignment.Center,
                                            trim = LineHeightStyle.Trim.Both
                                        )
                                    )
                                )
                                Text(
                                    text = " / bln",
                                    fontSize = 12.sp,
                                    lineHeight = 14.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = TextSecondary,
                                    modifier = Modifier.padding(bottom = 1.dp, start = 2.dp),
                                    style = TextStyle(
                                        fontFamily = OverusedGrotesk,
                                        platformStyle = PlatformTextStyle(includeFontPadding = false),
                                        lineHeightStyle = LineHeightStyle(
                                            alignment = LineHeightStyle.Alignment.Center,
                                            trim = LineHeightStyle.Trim.Both
                                        )
                                    )
                                )
                            }

                            Button(
                                text = if (isPending) "Cek Status" else "Bayar Sekarang",
                                size = ButtonSize.SM,
                                fullWidth = false,
                                onClick = if (isPending) onDetailClick else onPayClick,
                            )
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Diajukan pada ${formatDisplayDate(activeLoan.createdDate)}",
                            fontSize = 11.sp,
                            lineHeight = 13.sp,
                            color = TextMuted,
                            style = TextStyle(
                                fontFamily = OverusedGrotesk,
                                platformStyle = PlatformTextStyle(includeFontPadding = false),
                                lineHeightStyle = LineHeightStyle(
                                    alignment = LineHeightStyle.Alignment.Center,
                                    trim = LineHeightStyle.Trim.Both
                                )
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // 3. Footer Divider & Link
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(Border)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(onClick = onDetailClick),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = "Lihat Rincian Pinjaman",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TextSecondary,
                        )
                        Icon(
                            imageVector = Lucide.ChevronRight,
                            contentDescription = null,
                            tint = TextSecondary,
                            modifier = Modifier.size(15.dp),
                        )
                    }
                }
            }
        }
    }
}

private fun formatDisplayDate(dateStr: String?): String {
    if (dateStr.isNullOrBlank()) return "Hari ini"
    return try {
        val clean = dateStr.substringBefore("T")
        val parts = clean.split("-")
        if (parts.size == 3) {
            val year = parts[0]
            val monthNum = parts[1].toIntOrNull() ?: 1
            val day = parts[2].toIntOrNull() ?: 1
            val monthNames = listOf("Januari", "Februari", "Maret", "April", "Mei", "Juni", "Juli", "Agustus", "September", "Oktober", "November", "Desember")
            val monthName = monthNames.getOrElse(monthNum - 1) { "Bulan" }
            "$day $monthName $year"
        } else {
            dateStr
        }
    } catch (e: Exception) {
        dateStr
    }
}

// 5. Banner Promo Carousel
private data class PromoBannerData(
    val imageRes: Int,
    val contentDescription: String,
    val action: () -> Unit,
)

@Composable
private fun FullWidthPromoBannerSection(
    onSimulasiClick: () -> Unit,
    onAjukanClick: () -> Unit,
) {
    val promoList = listOf(
        PromoBannerData(
            imageRes = R.drawable.banner_promo_1,
            contentDescription = "Bunga Mulai Dari 3% Per Bulan - Cek Simulasi Sekarang!",
            action = onSimulasiClick,
        ),
        PromoBannerData(
            imageRes = R.drawable.banner_promo_2,
            contentDescription = "Butuh Dana? Cair Cepat Tanpa Ribet - Ajukan Sekarang!",
            action = onAjukanClick,
        ),
        PromoBannerData(
            imageRes = R.drawable.banner_promo_3,
            contentDescription = "Butuh Dana Buat Wujudkan Mobil Impianmu? - Ajukan Pinjaman!",
            action = onAjukanClick,
        ),
    )

    val pagerState = rememberPagerState(pageCount = { promoList.size })

    // Auto-slide banner otomatis setiap 4 detik
    LaunchedEffect(pagerState) {
        while (true) {
            delay(4000L)
            if (!pagerState.isScrollInProgress) {
                val nextPage = (pagerState.currentPage + 1) % promoList.size
                pagerState.animateScrollToPage(
                    page = nextPage,
                    animationSpec = tween(durationMillis = 650, easing = FastOutSlowInEasing)
                )
            }
        }
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "Promo & Penawaran",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
            )
            Text(
                text = "Lihat Semua",
                fontSize = 12.5.sp,
                fontWeight = FontWeight.SemiBold,
                color = Primary,
                modifier = Modifier.clickable(onClick = onSimulasiClick),
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Full Size Carousel Banner - Aspect Ratio 1024:324
        HorizontalPager(
            state = pagerState,
            contentPadding = PaddingValues(horizontal = 20.dp),
            pageSpacing = 12.dp,
            modifier = Modifier.fillMaxWidth(),
        ) { page ->
            val item = promoList[page]
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1024f / 324f)
                    .shadow(
                        elevation = 4.dp,
                        shape = RoundedCornerShape(16.dp),
                        ambientColor = Primary.copy(alpha = 0.25f),
                        spotColor = Primary.copy(alpha = 0.25f),
                    )
                    .clickable(onClick = item.action),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Surface),
                border = BorderStroke(0.5.dp, Border.copy(alpha = 0.6f))
            ) {
                Image(
                    painter = painterResource(id = item.imageRes),
                    contentDescription = item.contentDescription,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }
        }
    }
}

// 6. Edukasi & Tips Keuangan
@Composable
private fun TipsLiterasiKeuanganSection(onArticleClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
    ) {
        Text(
            text = "Edukasi & Tips Keuangan",
            fontSize = 15.5.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary,
        )
//        Spacer(modifier = Modifier.height(2.dp))
//        Text(
//            text = "Kelola finansial cerdas & wujudkan impian Anda",
//            fontSize = 12.sp,
//            color = TextSecondary,
//        )

        Spacer(modifier = Modifier.height(12.dp))

        // Article Card 1 - 5 Strategi (Banner 4)
        FinancialArticleItem(
            imageRes = R.drawable.banner_promo_4,
            title = "5 Strategi Kelola Arus Kas Pinjaman Usaha",
            desc = "Pisahkan rekening bisnis & pribadi untuk menjaga likuiditas operasional usaha.",
            tag = "3 Menit Baca • Tips UMKM",
            onClick = onArticleClick,
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Article Card 2 - Menjaga Riwayat (Banner 5)
        FinancialArticleItem(
            imageRes = R.drawable.banner_promo_5,
            title = "Menjaga Riwayat Kredit Prima Bebas Denda",
            desc = "Bayar cicilan sebelum jatuh tempo untuk meningkatkan limit pinjaman berkala.",
            tag = "2 Menit Baca • Finansial Sehat",
            onClick = onArticleClick,
        )
    }
}

@Composable
private fun FinancialArticleItem(
    imageRes: Int,
    title: String,
    desc: String,
    tag: String,
    onClick: () -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Surface),
        border = BorderStroke(1.dp, Border),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Image(
                painter = painterResource(id = imageRes),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(65.dp)
                    .clip(RoundedCornerShape(13.dp))
            )

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 13.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = desc,
                    fontSize = 11.5.sp,
                    lineHeight = 15.sp,
                    color = TextSecondary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = tag,
                    fontSize = 10.5.sp,
                    fontWeight = FontWeight.Medium,
                    color = TextMuted,
                )
            }

            Spacer(modifier = Modifier.width(6.dp))

            Icon(
                imageVector = Lucide.ChevronRight,
                contentDescription = null,
                tint = TextMuted,
                modifier = Modifier.size(16.dp),
            )
        }
    }
}

// Dialog: Interactive Loan Simulation
@Composable
private fun LoanSimulationDialog(
    amount: Double,
    tenorMonths: Int,
    onAmountChange: (Double) -> Unit,
    onTenorChange: (Int) -> Unit,
    simulasiResult: SimulasiPinjamanResponseDto?,
    monthlyInstallment: Long,
    currencyFormatter: NumberFormat,
    isAjukanEnabled: Boolean = true,
    onDismiss: () -> Unit,
    onApply: () -> Unit,
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = Surface),
            border = BorderStroke(1.dp, Border),
        ) {
            Column(
                modifier = Modifier.padding(22.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "Simulasi Pinjaman SAKU",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                    )
                    Box(
                        modifier =
                            Modifier.size(32.dp)
                                .clip(CircleShape)
                                .background(Primary0)
                                .clickable(onClick = onDismiss),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = "✕",
                            color = Primary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                Text(text = "Nominal Pinjaman", fontSize = 12.5.sp, color = TextSecondary)
                Text(
                    text = "Rp ${currencyFormatter.format(amount)}",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Primary,
                )

                Spacer(modifier = Modifier.height(8.dp))

                Slider(
                    value = amount.toFloat(),
                    onValueChange = { onAmountChange(it.toDouble()) },
                    valueRange = 1_000_000f..50_000_000f,
                    steps = 97,
                    colors =
                        SliderDefaults.colors(
                            thumbColor = Primary,
                            activeTrackColor = Primary,
                            inactiveTrackColor = Neutral20,
                        ),
                    modifier = Modifier.fillMaxWidth(),
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(text = "Rp 1 Juta", fontSize = 11.sp, color = TextMuted)
                    Text(text = "Rp 50 Juta", fontSize = 11.sp, color = TextMuted)
                }

                Spacer(modifier = Modifier.height(18.dp))

                Text(text = "Pilih Jangka Waktu (Tenor)", fontSize = 12.5.sp, color = TextSecondary)

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    listOf(3, 6, 9, 12).forEach { months ->
                        val isSelected = tenorMonths == months
                        Box(
                            modifier =
                                Modifier.weight(1f)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (isSelected) Primary else Background)
                                    .border(
                                        1.dp,
                                        if (isSelected) Primary else Border,
                                        RoundedCornerShape(10.dp),
                                    )
                                    .clickable { onTenorChange(months) }
                                    .padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                text = "$months Bln",
                                fontSize = 12.5.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) Color.White else TextPrimary,
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Breakdown Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Background),
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(14.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Text(
                            text = "Estimasi Angsuran Bulanan",
                            fontSize = 12.sp,
                            color = TextSecondary,
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Rp ${currencyFormatter.format(monthlyInstallment)} / bulan",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary,
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        val tierName = simulasiResult?.estimasiTierPlafond ?: "Standar"
                        val rawBunga = simulasiResult?.sukuBungaPersen ?: 1.5
                        val sukuBunga = if (rawBunga <= 1.0 && rawBunga > 0.0) rawBunga * 100 else rawBunga
                        Text(
                            text = "Bunga $sukuBunga% flat/bln • Tier Plafond: $tierName",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = Primary,
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                Button(
                    text = if (isAjukanEnabled) "Ajukan Pinjaman Ini" else "Limit Tidak Mencukupi (Rp 0)",
                    onClick = onApply,
                    enabled = isAjukanEnabled,
                    variant = ButtonVariant.Primary,
                    size = ButtonSize.LG,
                    fullWidth = true,
                )
            }
        }
    }
}

// Dialog: Notification Center
@Composable
private fun NotificationCenterDialog(
    notifications: List<NotifikasiItemDto>,
    isLoading: Boolean,
    unreadCount: Long,
    onMarkAsRead: (String) -> Unit,
    onMarkAllAsRead: () -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = Surface),
            border = BorderStroke(1.dp, Border),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Pemberitahuan",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary,
                        )
                        if (unreadCount > 0) {
                            Badge(
                                text = "$unreadCount baru",
                                variant = BadgeVariant.Error,
                                size = BadgeSize.SM
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .size(30.dp)
                            .clip(CircleShape)
                            .background(Background)
                            .clickable(onClick = onDismiss),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = "✕",
                            color = TextSecondary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                        )
                    }
                }

                if (unreadCount > 0) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Text(
                            text = "Tandai semua sudah dibaca",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Primary,
                            modifier = Modifier
                                .clickable(onClick = onMarkAllAsRead)
                                .padding(vertical = 4.dp, horizontal = 2.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                if (isLoading) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            color = Primary,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                } else if (notifications.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(46.dp)
                                    .clip(CircleShape)
                                    .background(Primary0),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Lucide.Bell,
                                    contentDescription = null,
                                    tint = Primary,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                            Text(
                                text = "Belum Ada Notifikasi",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = TextPrimary
                            )
                            Text(
                                text = "Semua pembaruan status pinjaman akan muncul di sini.",
                                fontSize = 11.5.sp,
                                color = TextSecondary,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 380.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(notifications) { notif ->
                            NotificationItemRow(
                                item = notif,
                                onClick = {
                                    notif.id?.let { onMarkAsRead(it) }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun NotificationItemRow(
    item: NotifikasiItemDto,
    onClick: () -> Unit
) {
    val isRead = item.isNotificationRead
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(if (isRead) Background else Primary0.copy(alpha = 0.6f))
            .border(
                width = 1.dp,
                color = if (isRead) Border else Primary.copy(alpha = 0.25f),
                shape = RoundedCornerShape(12.dp)
            )
            .clickable(onClick = onClick)
            .padding(12.dp),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(34.dp)
                .clip(CircleShape)
                .background(if (isRead) Surface else Primary),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Lucide.Bell,
                contentDescription = null,
                tint = if (isRead) Primary else Color.White,
                modifier = Modifier.size(16.dp),
            )
        }

        Spacer(modifier = Modifier.width(10.dp))

        Column(modifier = Modifier.weight(1f)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = item.judul ?: "Pemberitahuan SAKU",
                    fontSize = 13.sp,
                    fontWeight = if (isRead) FontWeight.SemiBold else FontWeight.Bold,
                    color = TextPrimary
                )
                if (!isRead) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(Primary)
                    )
                }
            }

            Spacer(modifier = Modifier.height(3.dp))
            Text(
                text = item.pesan ?: "-",
                fontSize = 11.5.sp,
                color = TextSecondary,
                lineHeight = 15.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = formatNotificationTime(item.createdDate),
                fontSize = 10.sp,
                color = TextMuted
            )
        }
    }
}

private fun formatNotificationTime(rawDate: String?): String {
    if (rawDate.isNullOrBlank()) return "Baru saja"
    return try {
        rawDate.replace("T", " ").take(16)
    } catch (e: Exception) {
        rawDate
    }
}

// Dialog: Credit Score Detail
@Composable
private fun CreditScoreDetailDialog(
    skorKredit: Int = 81,
    tierName: String = "Tier Reguler",
    onDismiss: () -> Unit,
    onPanduanUpgradeClick: () -> Unit
) {
    val tierVariant = when {
        tierName.contains("Platinum", ignoreCase = true) -> BadgeVariant.Primary
        tierName.contains("Prioritas", ignoreCase = true) -> BadgeVariant.Success
        tierName.contains("Reguler", ignoreCase = true) -> BadgeVariant.Warning
        tierName.contains("Starter", ignoreCase = true) -> BadgeVariant.Neutral
        else -> BadgeVariant.Neutral
    }

    val scoreBadgeText = when {
        skorKredit >= 75 -> "Sangat Baik"
        skorKredit >= 60 -> "Cukup Baik"
        else -> "Perlu Peningkatan"
    }

    val scoreBadgeVariant = when {
        skorKredit >= 75 -> BadgeVariant.Success
        skorKredit >= 60 -> BadgeVariant.Warning
        else -> BadgeVariant.Error
    }

    val scoreDesc = when {
        skorKredit >= 75 -> "Kolektibilitas lancar. Anda memenuhi kualifikasi untuk proses pencairan instan."
        skorKredit >= 60 -> "Kolektibilitas baik. Anda memenuhi syarat pinjaman dengan verifikasi berkas standar."
        else -> "Tingkatkan skor Anda dengan melengkapi data profil dan pembayaran cicilan tepat waktu."
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = Surface),
            border = BorderStroke(1.dp, Border),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Analisis Skor Kredit",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )

                    Box(
                        modifier = Modifier
                            .size(30.dp)
                            .clip(CircleShape)
                            .background(Background)
                            .clickable(onClick = onDismiss),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "✕",
                            color = TextSecondary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Score Hero Section (Clean No-BG / Direct on Dialog Surface)
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Skor Kelayakan Finansial",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "$skorKredit",
                        fontSize = 38.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = TextPrimary,
                        letterSpacing = (-0.5).sp,
                        style = TextStyle(
                            fontFamily = OverusedGrotesk,
                            platformStyle = PlatformTextStyle(includeFontPadding = false),
                        )
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Badge(
                            text = scoreBadgeText,
                            variant = scoreBadgeVariant,
                            size = BadgeSize.SM
                        )
                        Badge(
                            text = tierName,
                            variant = tierVariant,
                            size = BadgeSize.SM
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = scoreDesc,
                        fontSize = 11.5.sp,
                        color = TextSecondary,
                        textAlign = TextAlign.Center,
                        lineHeight = 15.sp
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "Faktor Penilaian Kredit",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )

                Spacer(modifier = Modifier.height(8.dp))

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(Background)
                        .border(1.dp, Border, RoundedCornerShape(14.dp))
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    CreditFactorRow(
                        title = "Riwayat Pembayaran",
                        desc = "100% Tepat Waktu",
                        badge = "Optimal",
                        badgeVariant = BadgeVariant.Success
                    )
                    HorizontalDivider(color = Border, thickness = 0.6.dp)
                    CreditFactorRow(
                        title = "Penggunaan Limit",
                        desc = "Rasio Terkontrol & Sehat",
                        badge = "Baik",
                        badgeVariant = BadgeVariant.Success
                    )
                    HorizontalDivider(color = Border, thickness = 0.6.dp)
                    CreditFactorRow(
                        title = "Kelengkapan KYC",
                        desc = "KTP & Rekening Terverifikasi",
                        badge = "Lengkap",
                        badgeVariant = BadgeVariant.Primary
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    text = "Panduan Kenaikan Limit",
                    onClick = onPanduanUpgradeClick,
                    variant = ButtonVariant.Primary,
                    size = ButtonSize.MD,
                    fullWidth = true
                )
            }
        }
    }
}

@Composable
private fun CreditFactorRow(
    title: String,
    desc: String,
    badge: String,
    badgeVariant: BadgeVariant
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary
            )
            Text(
                text = desc,
                fontSize = 11.sp,
                color = TextSecondary
            )
        }
        Badge(text = badge, variant = badgeVariant, size = BadgeSize.SM)
    }
}

// Dialog: Upgrade Limit Guide
@Composable
private fun UpgradeLimitGuideDialog(
    currentPlafond: Double,
    tierName: String = "Tier Reguler",
    currencyFormatter: NumberFormat,
    onDismiss: () -> Unit,
    onEditProfileClick: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = Surface),
            border = BorderStroke(1.dp, Border),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Kenaikan Limit Plafond",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )

                    Box(
                        modifier = Modifier
                            .size(30.dp)
                            .clip(CircleShape)
                            .background(Background)
                            .clickable(onClick = onDismiss),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "✕",
                            color = TextSecondary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Current vs Max Limit Card (Light Clean Container with balanced 2 columns)
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
                    border = BorderStroke(1.dp, Border)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Left Column: Limit Saat Ini
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Limit Saat Ini",
                                fontSize = 11.sp,
                                color = TextSecondary
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Rp ${currencyFormatter.format(currentPlafond)}",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = tierName,
                                fontSize = 10.5.sp,
                                color = TextSecondary,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        // Vertical Divider
                        Box(
                            modifier = Modifier
                                .height(40.dp)
                                .width(1.dp)
                                .background(Border)
                        )

                        // Right Column: Potensi Limit Maksimal
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .padding(start = 12.dp),
                            horizontalAlignment = Alignment.End
                        ) {
                            Text(
                                text = "Potensi Maksimal",
                                fontSize = 11.sp,
                                color = Primary,
                                fontWeight = FontWeight.SemiBold
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "s/d Rp 150.000.000",
                                fontSize = 14.5.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Primary
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Tier Platinum",
                                fontSize = 10.5.sp,
                                color = TextSecondary,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "3 Langkah Menaikkan Limit Anda",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )

                Spacer(modifier = Modifier.height(10.dp))

                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    UpgradeStepRow(
                        step = "1",
                        title = "Bayar Cicilan Tepat Waktu",
                        desc = "Lakukan pembayaran angsuran sebelum jatuh tempo selama 3 siklus berturut-turut."
                    )
                    UpgradeStepRow(
                        step = "2",
                        title = "Lengkapi Data Finansial",
                        desc = "Isi data pekerjaan, penghasilan bulanan, dan NPWP di menu Edit Profil."
                    )
                    UpgradeStepRow(
                        step = "3",
                        title = "Evaluasi Sistem Otomatis",
                        desc = "Sistem SAKU akan mengevaluasi kenaikan limit Anda secara berkala setiap 30 hari."
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                Button(
                    text = "Lengkapi Data Profil",
                    onClick = onEditProfileClick,
                    variant = ButtonVariant.Primary,
                    size = ButtonSize.MD,
                    fullWidth = true
                )
            }
        }
    }
}

@Composable
private fun UpgradeStepRow(
    step: String,
    title: String,
    desc: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(CircleShape)
                .background(Primary0),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = step,
                fontSize = 11.5.sp,
                fontWeight = FontWeight.Bold,
                color = Primary
            )
        }
        Spacer(modifier = Modifier.width(10.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 12.5.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = desc,
                fontSize = 11.sp,
                color = TextSecondary,
                lineHeight = 15.sp
            )
        }
    }
}

@Preview(name = "Card Plafon Pinjaman - Saldo Terlihat", showBackground = true)
@Composable
fun PlafondMeshHeroCardPreview() {
    SAKUAppTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            PlafondMeshHeroCard(
                availablePlafond = 45_000_000.0,
                totalPlafond = 50_000_000.0,
                usedPlafond = 5_000_000.0,
                isBalanceVisible = true,
                onToggleVisibility = {},
                currencyFormatter = NumberFormat.getNumberInstance(Locale.forLanguageTag("id-ID")),
            )
        }
    }
}

@Preview(name = "Card Plafon Pinjaman - Saldo Tersembunyi", showBackground = true)
@Composable
fun PlafondMeshHeroCardHiddenPreview() {
    SAKUAppTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            PlafondMeshHeroCard(
                availablePlafond = 45_000_000.0,
                totalPlafond = 50_000_000.0,
                usedPlafond = 5_000_000.0,
                isBalanceVisible = false,
                onToggleVisibility = {},
                currencyFormatter = NumberFormat.getNumberInstance(Locale.forLanguageTag("id-ID")),
            )
        }
    }
}

@Preview(name = "Card Plafon Pinjaman - Sedang Proses Pengajuan", showBackground = true)
@Composable
fun PlafondMeshHeroCardInProcessPreview() {
    SAKUAppTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            PlafondMeshHeroCard(
                availablePlafond = 6_000_000.0,
                totalPlafond = 10_500_000.0,
                usedPlafond = 4_500_000.0,
                isBalanceVisible = true,
                sukuBunga = 0.06,
                inProcessAmount = 4_500_000.0,
                hasInProcessLoan = true,
                hasDisbursedLoan = false,
                onToggleVisibility = {},
                currencyFormatter = NumberFormat.getNumberInstance(Locale.forLanguageTag("id-ID")),
            )
        }
    }
}

@Preview(name = "Tagihan Card - Dalam Review", showBackground = true)
@Composable
fun TagihanPinjamanAktifCardReviewPreview() {
    SAKUAppTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            TagihanPinjamanAktifCard(
                activeLoan = LoanApplicationItemDto(
                    id = "1",
                    nomorPengajuan = "PJ-20260827-0C85AB",
                    jumlahPinjaman = 4_500_000.0,
                    tenorBulan = 12,
                    bunga = 0.05,
                    estimasiAngsuranBulanan = 375_187.5,
                    statusPengajuan = "PENDING",
                    createdDate = "2026-08-27T10:30:00"
                ),
                activeLoansCount = 1,
                currencyFormatter = NumberFormat.getNumberInstance(Locale.forLanguageTag("id-ID")),
                onPayClick = {},
                onDetailClick = {},
            )
        }
    }
}

@Preview(name = "Tagihan Card - Belum Dibayar", showBackground = true)
@Composable
fun TagihanPinjamanAktifCardActivePreview() {
    SAKUAppTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            TagihanPinjamanAktifCard(
                activeLoan = LoanApplicationItemDto(
                    id = "2",
                    nomorPengajuan = "PJ-20260901-7F2A1C",
                    jumlahPinjaman = 5_000_000.0,
                    tenorBulan = 6,
                    bunga = 1.5,
                    estimasiAngsuranBulanan = 882_833.0,
                    statusPengajuan = "APPROVED",
                    createdDate = "2026-09-01T08:15:00"
                ),
                activeLoansCount = 1,
                currencyFormatter = NumberFormat.getNumberInstance(Locale.forLanguageTag("id-ID")),
                onPayClick = {},
                onDetailClick = {},
            )
        }
    }
}

@Preview(name = "Tagihan Card - Semua Lunas (Empty)", showBackground = true)
@Composable
fun TagihanPinjamanAktifCardEmptyPreview() {
    SAKUAppTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            TagihanPinjamanAktifCard(
                activeLoan = null,
                activeLoansCount = 0,
                currencyFormatter = NumberFormat.getNumberInstance(Locale.forLanguageTag("id-ID")),
                onPayClick = {},
                onDetailClick = {},
            )
        }
    }
}

@Preview(name = "Full HomeScreen", showBackground = true, showSystemUi = true)
@Composable
fun HomeScreenPreview() {
    SAKUAppTheme {
        HomeScreen(
            onNavigateToLogin = {},
            onNavigateToSandbox = {},
        )
    }
}