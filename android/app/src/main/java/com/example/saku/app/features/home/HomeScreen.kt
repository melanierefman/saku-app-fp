package com.example.saku.app.features.home

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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import kotlinx.coroutines.delay
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.saku.app.ui.theme.OverusedGrotesk
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.composables.icons.lucide.Bell
import com.composables.icons.lucide.Calculator
import com.composables.icons.lucide.ChevronRight
import com.composables.icons.lucide.CircleCheck
import com.composables.icons.lucide.Clock
import com.composables.icons.lucide.CreditCard
import com.composables.icons.lucide.DollarSign
import com.composables.icons.lucide.Eye
import com.composables.icons.lucide.EyeOff
import com.composables.icons.lucide.FileText
import com.composables.icons.lucide.House
import com.composables.icons.lucide.Landmark
import com.composables.icons.lucide.LogOut
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Palette
import com.composables.icons.lucide.Receipt
import com.composables.icons.lucide.Sparkles
import com.composables.icons.lucide.TrendingUp
import com.composables.icons.lucide.User
import com.composables.icons.lucide.Wallet
import com.example.saku.app.R
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToLogin: () -> Unit,
    onNavigateToSandbox: (() -> Unit)? = null,
    viewModel: HomeViewModel = viewModel(),
) {
    val userSession by viewModel.userSession.collectAsState()
    val customerProfile by viewModel.customerProfile.collectAsState()
    val myLoans by viewModel.myLoans.collectAsState()
    val isBalanceVisible by viewModel.isBalanceVisible.collectAsState()
    val showLogoutDialog by viewModel.showLogoutDialog.collectAsState()
    val showSimulationDialog by viewModel.showSimulationDialog.collectAsState()
    val showNotificationDialog by viewModel.showNotificationDialog.collectAsState()
    val currentNavRoute by viewModel.currentNavRoute.collectAsState()
    val simAmount by viewModel.simAmount.collectAsState()
    val simTenorMonths by viewModel.simTenorMonths.collectAsState()

    val selectedHistoryFilter by viewModel.selectedHistoryFilter.collectAsState()

    val currencyFormatter = remember {
        NumberFormat.getNumberInstance(Locale.forLanguageTag("id-ID"))
    }

    // Refresh data saat HomeScreen aktif / dibuka ulang
    LaunchedEffect(Unit) {
        viewModel.fetchDashboardData()
    }

    val totalPlafond = customerProfile?.totalPlafond ?: 50_000_000.0
    val usedPlafond = customerProfile?.usedPlafond ?: 0.0
    val availablePlafond = customerProfile?.availablePlafond ?: (totalPlafond - usedPlafond)
    val displayName =
        customerProfile?.nama ?: (userSession?.nama ?: (userSession?.username ?: "Nasabah SAKU"))

    val navItems =
        listOf(
            BottomNavItem(route = "home", title = "Beranda", icon = Lucide.House),
            BottomNavItem(route = "loans", title = "Pinjaman", icon = Lucide.Wallet),
            BottomNavItem(route = "history", title = "Riwayat", icon = Lucide.Receipt),
            BottomNavItem(route = "profile", title = "Profil", icon = Lucide.User),
        )

    Scaffold(
        bottomBar = {
            BottomNavBar(
                items = navItems,
                currentRoute = currentNavRoute,
                onItemClick = { item -> viewModel.setNavRoute(item.route) },
            )
        },
        containerColor = Background,
    ) { innerPadding ->
        Crossfade(
            targetState = currentNavRoute,
            animationSpec = tween(220),
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) { route ->
            when (route) {
                "home" -> {
                    HomeTabContent(
                        displayName = displayName,
                        availablePlafond = availablePlafond,
                        totalPlafond = totalPlafond,
                        usedPlafond = usedPlafond,
                        isBalanceVisible = isBalanceVisible,
                        activeLoan = myLoans.firstOrNull(),
                        activeLoansCount = myLoans.size,
                        currencyFormatter = currencyFormatter,
                        onToggleVisibility = viewModel::toggleBalanceVisibility,
                        onAjukanClick = { viewModel.setSimulationDialogVisible(true) },
                        onBayarClick = { viewModel.setNavRoute("loans") },
                        onSimulasiClick = { viewModel.setSimulationDialogVisible(true) },
                        onRiwayatClick = { viewModel.setNavRoute("history") },
                        onNotificationClick = { viewModel.setNotificationDialogVisible(true) },
                        onSandboxClick = onNavigateToSandbox,
                        onLogoutClick = { viewModel.setLogoutDialogVisible(true) },
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
                        onAjukanClick = { viewModel.setSimulationDialogVisible(true) },
                        onSimulasiClick = { viewModel.setSimulationDialogVisible(true) },
                        onPayClick = { /* Pay handler */ },
                        onDetailClick = { /* Detail handler */ },
                    )
                }
                "history" -> {
                    HistoryTabContent(
                        myLoans = myLoans,
                        selectedFilter = selectedHistoryFilter,
                        onFilterSelect = viewModel::setHistoryFilter,
                        currencyFormatter = currencyFormatter,
                        onAjukanClick = { viewModel.setSimulationDialogVisible(true) },
                        onRefresh = viewModel::fetchDashboardData,
                    )
                }
                "profile" -> {
                    ProfileTabContent(
                        customerProfile = customerProfile,
                        userSession = userSession,
                        onLogoutClick = { viewModel.setLogoutDialogVisible(true) },
                        onNotificationClick = { viewModel.setNotificationDialogVisible(true) },
                        onSimulasiClick = { viewModel.setSimulationDialogVisible(true) },
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
        message = "Anda harus memasukkan email dan kata sandi kembali untuk masuk ke SAKU.",
        confirmButtonText = "Keluar",
        dismissButtonText = "Batal",
        type = DialogType.DESTRUCTIVE,
        icon = Lucide.LogOut,
        onConfirm = { viewModel.logout(onLoggedOut = onNavigateToLogin) },
        onDismiss = { viewModel.setLogoutDialogVisible(false) },
    )

    // Interactive Loan Simulation Dialog
    if (showSimulationDialog) {
        LoanSimulationDialog(
            amount = simAmount,
            tenorMonths = simTenorMonths,
            onAmountChange = viewModel::updateSimAmount,
            onTenorChange = viewModel::updateSimTenor,
            monthlyInstallment = viewModel.calculateMonthlyInstallment(simAmount, simTenorMonths),
            currencyFormatter = currencyFormatter,
            onDismiss = { viewModel.setSimulationDialogVisible(false) },
            onApply = {
                viewModel.setSimulationDialogVisible(false)
                viewModel.setNavRoute("loans")
            },
        )
    }

    // Notification Center Dialog
    if (showNotificationDialog) {
        NotificationCenterDialog(onDismiss = { viewModel.setNotificationDialogVisible(false) })
    }
}

// -------------------------------------------------------------
// BERANDA TAB CONTENT
// -------------------------------------------------------------
@Composable
private fun HomeTabContent(
    displayName: String,
    availablePlafond: Double,
    totalPlafond: Double,
    usedPlafond: Double,
    isBalanceVisible: Boolean,
    activeLoan: LoanApplicationItemDto?,
    activeLoansCount: Int,
    currencyFormatter: NumberFormat,
    onToggleVisibility: () -> Unit,
    onAjukanClick: () -> Unit,
    onBayarClick: () -> Unit,
    onSimulasiClick: () -> Unit,
    onRiwayatClick: () -> Unit,
    onNotificationClick: () -> Unit,
    onSandboxClick: (() -> Unit)?,
    onLogoutClick: () -> Unit,
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 28.dp),
    ) {
        // 1. Header Pengguna ("Halo, Nama Lengkap")
        item {
            HomeHeaderSection(
                displayName = displayName,
                onNotificationClick = onNotificationClick,
                onSandboxClick = onSandboxClick,
                onLogoutClick = onLogoutClick,
            )
        }

        // 2. HERO CARD PLAFOND SAKU (STANDALONE CARD SESUAI GAMBAR 1)
        item {
            Box(modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp)) {
                PlafondMeshHeroCard(
                    availablePlafond = availablePlafond,
                    totalPlafond = totalPlafond,
                    usedPlafond = usedPlafond,
                    isBalanceVisible = isBalanceVisible,
                    onToggleVisibility = onToggleVisibility,
                    currencyFormatter = currencyFormatter,
                )
            }
        }

        // 3. SECTION "MENU UTAMA" + QUICK ACTIONS (TITLE INSIDE CARD & CLEAN BORDER)
        item {
            Spacer(modifier = Modifier.height(16.dp))
            MenuUtamaSection(
                onAjukanClick = onAjukanClick,
                onBayarClick = onBayarClick,
                onSimulasiClick = onSimulasiClick,
                onRiwayatClick = onRiwayatClick,
            )
        }

        // 4. INFO TAGIHAN / PINJAMAN AKTIF CARD (CLEAN BORDER & BUTTON DI BAWAH KANAN)
        item {
            Spacer(modifier = Modifier.height(16.dp))
            Box(modifier = Modifier.padding(horizontal = 20.dp)) {
                TagihanPinjamanAktifCard(
                    activeLoan = activeLoan,
                    activeLoansCount = activeLoansCount,
                    currencyFormatter = currencyFormatter,
                    onPayClick = onBayarClick,
                    onDetailClick = onBayarClick,
                )
            }
        }

        // 5. BANNER PROMO FULL WIDTH DENGAN PAGE INDICATOR
        item {
            Spacer(modifier = Modifier.height(20.dp))
            FullWidthPromoBannerSection(
                onPromoItemClick = onAjukanClick,
            )
        }
    }
}

// -------------------------------------------------------------
// 1. TOP BAR ("HALO, NAMA LENGKAP")
// -------------------------------------------------------------
@Composable
private fun HomeHeaderSection(
    displayName: String,
    onNotificationClick: () -> Unit,
    onSandboxClick: (() -> Unit)?,
    onLogoutClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 10.dp)
    ) {
        // Top Row: Logo & Action Buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // SAKU Branding
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(id = R.drawable.saku_logo),
                    contentDescription = "Logo SAKU",
                    modifier = Modifier
                        .size(34.dp)
                        .clip(RoundedCornerShape(10.dp)),
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "SAKU",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Primary,
                    letterSpacing = 0.5.sp,
                )
            }

            // Action Icons (Sandbox, Notifikasi, Logout) - Bare Outline Icons (No Circle Fill)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(2.dp),
            ) {
                // Dev Sandbox Shortcut
                if (onSandboxClick != null) {
                    IconButton(
                        onClick = onSandboxClick,
                        modifier = Modifier.size(38.dp)
                    ) {
                        Icon(
                            imageVector = Lucide.Palette,
                            contentDescription = "UI Sandbox",
                            tint = TextPrimary,
                            modifier = Modifier.size(23.dp),
                        )
                    }
                }

                // Notification Bell with Red Dot
                IconButton(
                    onClick = onNotificationClick,
                    modifier = Modifier.size(38.dp)
                ) {
                    Box(contentAlignment = Alignment.TopEnd) {
                        Icon(
                            imageVector = Lucide.Bell,
                            contentDescription = "Notifikasi",
                            tint = TextPrimary,
                            modifier = Modifier.size(23.dp),
                        )
                        Box(
                            modifier = Modifier
                                .size(7.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFFF3B30))
                        )
                    }
                }

                // Logout Button
                IconButton(
                    onClick = onLogoutClick,
                    modifier = Modifier.size(38.dp)
                ) {
                    Icon(
                        imageVector = Lucide.LogOut,
                        contentDescription = "Logout",
                        tint = TextPrimary,
                        modifier = Modifier.size(23.dp),
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // myBCA style "HALO, NAMA LENGKAP"
        Text(
            text = "HALO, ${displayName.uppercase()}",
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = TextSecondary,
            letterSpacing = 0.5.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

// -------------------------------------------------------------
// 2. HERO CARD PLAFON PINJAMAN (ZOOMED & CONDONG KANAN SESUAI GAMBAR 1)
// -------------------------------------------------------------
@Composable
private fun PlafondMeshHeroCard(
    availablePlafond: Double,
    totalPlafond: Double,
    usedPlafond: Double,
    isBalanceVisible: Boolean,
    onToggleVisibility: () -> Unit,
    currencyFormatter: NumberFormat,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(elevation = 5.dp, shape = RoundedCornerShape(22.dp), ambientColor = Primary, spotColor = Primary),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(22.dp))
        ) {
            // Background Image (Zoomed In & Shifted to Right like Gambar 1)
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
                    .padding(horizontal = 18.dp, vertical = 14.dp)
            ) {
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
                            fontSize = 13.5.sp,
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
                            modifier = Modifier.size(16.dp),
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Main Large Amount
                Text(
                    text = if (isBalanceVisible) "Rp${currencyFormatter.format(availablePlafond)}" else "Rp ••••••••••",
                    fontSize = 26.sp,
                    lineHeight = 28.sp,
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

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Tersedia untuk pengajuan",
                    fontSize = 11.5.sp,
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

                Spacer(modifier = Modifier.height(12.dp))

                // Sub-limit dark container (Total Plafond, Plafond Terpakai, Bunga Mulai)
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
                        label = "Bunga Mulai",
                        value = "0.99% / bln"
                    )
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

// -------------------------------------------------------------
// 3. SECTION MENU UTAMA (TITLE INSIDE CARD & CLEAN BORDER)
// -------------------------------------------------------------
@Composable
private fun MenuUtamaSection(
    onAjukanClick: () -> Unit,
    onBayarClick: () -> Unit,
    onSimulasiClick: () -> Unit,
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
                    text = "Menu Utama",
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
                        title = "Ajukan",
                        icon = Lucide.Landmark,
                        onClick = onAjukanClick,
                    )
                    ActionItemColumn(
                        title = "Bayar",
                        icon = Lucide.CreditCard,
                        onClick = onBayarClick,
                    )
                    ActionItemColumn(
                        title = "Simulasi",
                        icon = Lucide.Calculator,
                        onClick = onSimulasiClick,
                    )
                    ActionItemColumn(
                        title = "Riwayat",
                        icon = Lucide.Clock,
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
    onClick: () -> Unit,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier =
            Modifier.clip(RoundedCornerShape(14.dp))
                .clickable(onClick = onClick)
                .padding(horizontal = 6.dp, vertical = 4.dp),
    ) {
        Box(
            modifier = Modifier
                .size(52.dp)
                .shadow(elevation = 3.dp, shape = RoundedCornerShape(16.dp), spotColor = Primary, ambientColor = Primary)
                .clip(RoundedCornerShape(16.dp))
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFFFF883A),
                            Primary,
                        )
                    )
                ),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = Color.White,
                modifier = Modifier.size(24.dp),
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(text = title, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
    }
}

// -------------------------------------------------------------
// 4. INFO TAGIHAN / PINJAMAN AKTIF (SEAMLESS CLEAN FINTECH CARD)
// -------------------------------------------------------------
@Composable
private fun TagihanPinjamanAktifCard(
    activeLoan: LoanApplicationItemDto?,
    activeLoansCount: Int,
    currencyFormatter: NumberFormat,
    onPayClick: () -> Unit,
    onDetailClick: () -> Unit,
) {
    val isPending = activeLoan?.statusPengajuan?.contains("PENDING", ignoreCase = true) == true

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
                // 1. Header: Title alone at top (Identical to Menu Utama)
                Text(
                    text = "Tagihan & Pinjaman Aktif",
                    fontSize = 14.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                )

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
                            text = "Tidak ada tagihan aktif. Semua tagihan telah lunas!",
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
                                    color = Primary,
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

// -------------------------------------------------------------
// 5. BANNER PROMO MENARIK (CAROUSEL PROMO)
// -------------------------------------------------------------
private data class PromoBannerData(
    val tag: String,
    val title: String,
    val desc: String,
    val cta: String,
    val gradientColors: List<Color>,
    val accentIcon: ImageVector,
)

@Composable
private fun FullWidthPromoBannerSection(onPromoItemClick: () -> Unit) {
    val promoList =
        listOf(
            PromoBannerData(
                tag = "PROMO SPESIAL",
                title = "Bunga Rendah Mulai 0.99% / bln",
                desc =
                    "Bebas biaya admin untuk pengajuan pinjaman pertama Anda di SAKU. Proses kilat cair 5 menit!",
                cta = "Ajukan Sekarang →",
                gradientColors = listOf(Color(0xFFFF792E), Color(0xFFFF5200), Color(0xFFD63B00)),
                accentIcon = Lucide.Sparkles,
            ),
            PromoBannerData(
                tag = "LIMIT EKSTRA",
                title = "Plafond s/d Rp 50.000.000",
                desc =
                    "Lengkapi data pekerjaan & rekening Anda untuk mendapatkan kenaikan limit instan.",
                cta = "Cek Plafond →",
                gradientColors = listOf(Color(0xFF4F46E5), Color(0xFF4338CA), Color(0xFF312E81)),
                accentIcon = Lucide.TrendingUp,
            ),
            PromoBannerData(
                tag = "CASHBACK",
                title = "Bonus Bayar Tepat Waktu",
                desc =
                    "Dapatkan cashback biaya admin & poin reward untuk setiap pembayaran angsuran tepat waktu.",
                cta = "Pelajari Promo →",
                gradientColors = listOf(Color(0xFF059669), Color(0xFF047857), Color(0xFF064E3B)),
                accentIcon = Lucide.CreditCard,
            ),
            PromoBannerData(
                tag = "SAKU USHA",
                title = "Modal Kerja UMKM s/d Rp 50 Juta",
                desc =
                    "Kembangkan usaha Anda dengan cicilan ringan hingga 12 bulan dan bunga bersahabat.",
                cta = "Ajukan Modal →",
                gradientColors = listOf(Color(0xFFEA580C), Color(0xFFC2410C), Color(0xFF9A3412)),
                accentIcon = Lucide.Wallet,
            ),
        )

    val pagerState = rememberPagerState(pageCount = { promoList.size })

    // Auto-slide banner otomatis setiap 3.5 detik
    LaunchedEffect(pagerState) {
        while (true) {
            delay(3500L)
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
                modifier = Modifier.clickable(onClick = onPromoItemClick),
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Full Size Carousel Banner
        HorizontalPager(
            state = pagerState,
            contentPadding = PaddingValues(horizontal = 20.dp),
            pageSpacing = 12.dp,
            modifier = Modifier.fillMaxWidth(),
        ) { page ->
            val item = promoList[page]
            Card(
                modifier =
                    Modifier.fillMaxWidth()
                        .height(172.dp)
                        .shadow(
                            elevation = 4.dp,
                            shape = RoundedCornerShape(18.dp),
                            ambientColor = item.gradientColors.first(),
                            spotColor = item.gradientColors.first(),
                        )
                        .clickable(onClick = onPromoItemClick),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent),
            ) {
                Box(
                    modifier =
                        Modifier.fillMaxSize()
                            .background(Brush.linearGradient(item.gradientColors))
                            .padding(horizontal = 18.dp, vertical = 16.dp)
                ) {
                    // Decorative Watermark Icon in background
                    Icon(
                        imageVector = item.accentIcon,
                        contentDescription = null,
                        tint = Color.White.copy(alpha = 0.12f),
                        modifier = Modifier.size(110.dp).align(Alignment.BottomEnd),
                    )

                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Column {
                            // Tag Pill
                            Box(
                                modifier =
                                    Modifier.clip(RoundedCornerShape(20.dp))
                                        .background(Color.White.copy(alpha = 0.22f))
                                        .padding(horizontal = 10.dp, vertical = 3.5.dp)
                            ) {
                                Text(
                                    text = item.tag,
                                    fontSize = 10.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    letterSpacing = 0.5.sp,
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // Main Headline
                            Text(
                                text = item.title,
                                fontSize = 17.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                lineHeight = 22.sp,
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            // Subtitle description
                            Text(
                                text = item.desc,
                                fontSize = 12.sp,
                                color = Color.White.copy(alpha = 0.92f),
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis,
                                lineHeight = 16.sp,
                            )
                        }

                        // Bottom CTA Pill Button
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End,
                        ) {
                            Box(
                                modifier =
                                    Modifier.clip(RoundedCornerShape(20.dp))
                                        .background(Color.White)
                                        .padding(horizontal = 12.dp, vertical = 6.dp),
                                contentAlignment = Alignment.Center,
                            ) {
                                Text(
                                    text = item.cta,
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = item.gradientColors.first(),
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Dot Page Indicators
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            repeat(promoList.size) { index ->
                val isSelected = pagerState.currentPage == index
                Box(
                    modifier =
                        Modifier.padding(horizontal = 3.5.dp)
                            .size(if (isSelected) 8.dp else 6.dp)
                            .clip(CircleShape)
                            .background(if (isSelected) Primary else Color(0xFFD1D5DB))
                )
            }
        }
    }
}

// -------------------------------------------------------------
// INTERACTIVE LOAN SIMULATION DIALOG
// -------------------------------------------------------------
@Composable
private fun LoanSimulationDialog(
    amount: Double,
    tenorMonths: Int,
    onAmountChange: (Double) -> Unit,
    onTenorChange: (Int) -> Unit,
    monthlyInstallment: Long,
    currencyFormatter: NumberFormat,
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

                Slider(
                    value = amount.toFloat(),
                    onValueChange = { onAmountChange((it / 500_000).toInt() * 500_000.0) },
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

                Spacer(modifier = Modifier.height(20.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Primary0),
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
                            color = Primary,
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Termasuk bunga flat 0.99% & biaya admin",
                            fontSize = 10.5.sp,
                            color = TextMuted,
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    text = "Ajukan Pinjaman Ini",
                    onClick = onApply,
                    variant = ButtonVariant.Primary,
                    size = ButtonSize.LG,
                    fullWidth = true,
                )
            }
        }
    }
}

// -------------------------------------------------------------
// NOTIFICATION CENTER DIALOG
// -------------------------------------------------------------
@Composable
private fun NotificationCenterDialog(onDismiss: () -> Unit) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Surface),
            border = BorderStroke(1.dp, Border),
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "Pemberitahuan",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                    )
                    Box(
                        modifier =
                            Modifier.size(30.dp)
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

                Spacer(modifier = Modifier.height(16.dp))

                NotificationItemRow(
                    title = "Selamat Datang di SAKU! 🎉",
                    desc = "Akun nasabah Anda telah aktif. Nikmati kemudahan transaksi & pinjaman.",
                    time = "Baru saja",
                )

                Spacer(modifier = Modifier.height(10.dp))

                NotificationItemRow(
                    title = "Plafond Tersedia Rp 50.000.000",
                    desc =
                        "Limit plafond Anda telah disiapkan. Ajukan pinjaman pertama kapan saja.",
                    time = "10 Menit lalu",
                )

                Spacer(modifier = Modifier.height(10.dp))

                NotificationItemRow(
                    title = "Promo Bunga Spesial 0.99%",
                    desc =
                        "Dapatkan suku bunga rendah untuk pengajuan pinjaman tenor s/d 12 bulan.",
                    time = "1 Jam lalu",
                )
            }
        }
    }
}

@Composable
private fun NotificationItemRow(title: String, desc: String, time: String) {
    Row(
        modifier =
            Modifier.fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(Background)
                .padding(12.dp)
    ) {
        Box(
            modifier = Modifier.size(32.dp).clip(CircleShape).background(Primary0),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Lucide.Bell,
                contentDescription = null,
                tint = Primary,
                modifier = Modifier.size(16.dp),
            )
        }

        Spacer(modifier = Modifier.width(10.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
            Text(text = desc, fontSize = 11.5.sp, color = TextSecondary, lineHeight = 15.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = time, fontSize = 10.sp, color = TextMuted)
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
                    bunga = 0.99,
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
