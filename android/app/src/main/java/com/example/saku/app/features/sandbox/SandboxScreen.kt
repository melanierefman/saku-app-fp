package com.example.saku.app.features.sandbox

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.composables.icons.lucide.ArrowLeft
import com.composables.icons.lucide.ArrowRight
import com.composables.icons.lucide.Bell
import com.composables.icons.lucide.Briefcase
import com.composables.icons.lucide.Check
import com.composables.icons.lucide.ChevronRight
import com.composables.icons.lucide.CircleAlert
import com.composables.icons.lucide.CircleCheck
import com.composables.icons.lucide.CreditCard
import com.composables.icons.lucide.DollarSign
import com.composables.icons.lucide.Eye
import com.composables.icons.lucide.EyeOff
import com.composables.icons.lucide.Heart
import com.composables.icons.lucide.House
import com.composables.icons.lucide.IdCard
import com.composables.icons.lucide.ImagePlus
import com.composables.icons.lucide.Info
import com.composables.icons.lucide.Key
import com.composables.icons.lucide.Landmark
import com.composables.icons.lucide.Lock
import com.composables.icons.lucide.LogOut
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Mail
import com.composables.icons.lucide.Palette
import com.composables.icons.lucide.Phone
import com.composables.icons.lucide.Plus
import com.composables.icons.lucide.Receipt
import com.composables.icons.lucide.RotateCw
import com.composables.icons.lucide.Search
import com.composables.icons.lucide.Send
import com.composables.icons.lucide.Settings
import com.composables.icons.lucide.ShieldCheck
import com.composables.icons.lucide.Sparkles
import com.composables.icons.lucide.Trash2
import com.composables.icons.lucide.TrendingUp
import com.composables.icons.lucide.User
import com.composables.icons.lucide.Users
import com.composables.icons.lucide.Wallet
import com.example.saku.app.core.ui.components.Badge
import com.example.saku.app.core.ui.components.BadgeShape
import com.example.saku.app.core.ui.components.BadgeSize
import com.example.saku.app.core.ui.components.BadgeVariant
import com.example.saku.app.core.ui.components.Button
import com.example.saku.app.core.ui.components.ButtonShape
import com.example.saku.app.core.ui.components.ButtonSize
import com.example.saku.app.core.ui.components.ButtonVariant
import com.example.saku.app.core.ui.components.Checkbox
import com.example.saku.app.core.ui.components.CheckboxWithLabel
import com.example.saku.app.core.ui.components.IconButton
import com.example.saku.app.core.ui.components.IconButtonShape
import com.example.saku.app.core.ui.components.IconButtonSize
import com.example.saku.app.core.ui.components.IconButtonVariant
import com.example.saku.app.core.ui.components.PasswordField
import com.example.saku.app.core.ui.components.TextField
import com.example.saku.app.core.ui.components.TopBar
import com.example.saku.app.ui.theme.Background
import com.example.saku.app.ui.theme.Border
import com.example.saku.app.ui.theme.Cyan
import com.example.saku.app.ui.theme.Error
import com.example.saku.app.ui.theme.Error0
import com.example.saku.app.ui.theme.Indigo
import com.example.saku.app.ui.theme.Info
import com.example.saku.app.ui.theme.Neutral
import com.example.saku.app.ui.theme.Neutral0
import com.example.saku.app.ui.theme.Neutral10
import com.example.saku.app.ui.theme.Neutral20
import com.example.saku.app.ui.theme.Neutral40
import com.example.saku.app.ui.theme.Neutral50
import com.example.saku.app.ui.theme.Neutral60
import com.example.saku.app.ui.theme.Orange
import com.example.saku.app.ui.theme.Primary
import com.example.saku.app.ui.theme.Primary0
import com.example.saku.app.ui.theme.Primary10
import com.example.saku.app.ui.theme.Primary60
import com.example.saku.app.ui.theme.Primary90
import com.example.saku.app.ui.theme.Purple
import com.example.saku.app.ui.theme.Success
import com.example.saku.app.ui.theme.Success0
import com.example.saku.app.ui.theme.Surface
import com.example.saku.app.ui.theme.TextMuted
import com.example.saku.app.ui.theme.TextPrimary
import com.example.saku.app.ui.theme.TextSecondary
import com.example.saku.app.ui.theme.Typography
import com.example.saku.app.ui.theme.Warning
import com.example.saku.app.ui.theme.Warning0

enum class SandboxTab(val title: String) {
    ALL("Semua"),
    BUTTON("Button & Icon"),
    INPUT("Form Input"),
    CHECKBOX("Checkbox"),
    BADGE("Badge"),
    TOP_BAR("TopBar"),
    LUCIDE("Lucide Icons"),
    TOKENS("Tokens")
}

@Composable
fun SandboxScreen(
    onNavigateBack: () -> Unit
) {
    var selectedTab by remember { mutableStateOf(SandboxTab.ALL) }
    var actionToastMsg by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            TopBar(
                title = "UI Sandbox (Tahap 1)",
                onBackClick = onNavigateBack,
                actions = {
                    IconButton(
                        icon = Lucide.Info,
                        contentDescription = "Info",
                        onClick = {
                            actionToastMsg = "SAKU Design System: Phase 1 Atomic Components Active"
                        },
                        variant = IconButtonVariant.Ghost
                    )
                }
            )
        },
        containerColor = Background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Filter Tabs Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                SandboxTab.entries.forEach { tab ->
                    FilterChip(
                        selected = selectedTab == tab,
                        onClick = { selectedTab = tab },
                        label = { Text(tab.title) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Primary,
                            selectedLabelColor = Color.White,
                            containerColor = Surface,
                            labelColor = TextSecondary
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            borderColor = Border,
                            selectedBorderColor = Primary,
                            enabled = true,
                            selected = selectedTab == tab
                        )
                    )
                }
            }

            AnimatedVisibility(visible = actionToastMsg != null) {
                actionToastMsg?.let { msg ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 4.dp),
                        colors = CardDefaults.cardColors(containerColor = Primary0),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = msg, fontSize = 13.sp, color = Primary60, fontWeight = FontWeight.Medium)
                            Text(
                                text = "Tutup",
                                fontSize = 12.sp,
                                color = Primary90,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(Primary10)
                                    .clickable { actionToastMsg = null }
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }

            // Scrollable Content
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                if (selectedTab == SandboxTab.ALL || selectedTab == SandboxTab.BUTTON) {
                    ButtonShowcaseSection(onToast = { actionToastMsg = it })
                }

                if (selectedTab == SandboxTab.ALL || selectedTab == SandboxTab.INPUT) {
                    InputShowcaseSection()
                }

                if (selectedTab == SandboxTab.ALL || selectedTab == SandboxTab.CHECKBOX) {
                    CheckboxShowcaseSection()
                }

                if (selectedTab == SandboxTab.ALL || selectedTab == SandboxTab.BADGE) {
                    BadgeShowcaseSection(onToast = { actionToastMsg = it })
                }

                if (selectedTab == SandboxTab.ALL || selectedTab == SandboxTab.TOP_BAR) {
                    TopBarShowcaseSection(onToast = { actionToastMsg = it })
                }

                if (selectedTab == SandboxTab.ALL || selectedTab == SandboxTab.LUCIDE) {
                    LucideIconsShowcaseSection()
                }

                if (selectedTab == SandboxTab.ALL || selectedTab == SandboxTab.TOKENS) {
                    DesignTokensSection()
                }

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

// 1. BUTTON & ICON BUTTON SHOWCASE
@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ButtonShowcaseSection(onToast: (String) -> Unit) {
    var buttonClickCount by remember { mutableIntStateOf(0) }
    var selectedVariant by remember { mutableStateOf(ButtonVariant.Primary) }
    var selectedSize by remember { mutableStateOf(ButtonSize.LG) }
    var selectedShape by remember { mutableStateOf(ButtonShape.Rounded) }
    var isInteractiveLoading by remember { mutableStateOf(false) }
    var isInteractiveEnabled by remember { mutableStateOf(true) }
    var hasInteractiveLeadingIcon by remember { mutableStateOf(true) }
    var hasInteractiveTrailingIcon by remember { mutableStateOf(false) }
    var customButtonText by remember { mutableStateOf("Lanjutkan Pengajuan") }

    ShowcaseCard(
        title = "Button & IconButton",
        subtitle = "Tombol aksi utama, sekunder, outline, icon-only dengan variasi ukuran & status"
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {

            // Static Variants Grid
            Text(
                text = "Koleksi Varian Button (1:1 Frontend)",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary
            )

            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Button(
                    text = "Primary Button",
                    onClick = { onToast("Primary Button diklik") },
                    variant = ButtonVariant.Primary,
                    leadingIcon = Lucide.Send
                )

                Button(
                    text = "Secondary Button",
                    onClick = { onToast("Secondary Button diklik") },
                    variant = ButtonVariant.Secondary,
                    leadingIcon = Lucide.Plus
                )

                Button(
                    text = "White Button",
                    onClick = { onToast("White Button diklik") },
                    variant = ButtonVariant.White,
                    leadingIcon = Lucide.Sparkles
                )

                Button(
                    text = "Neutral Button",
                    onClick = { onToast("Neutral Button diklik") },
                    variant = ButtonVariant.Neutral,
                    leadingIcon = Lucide.Wallet
                )

                Button(
                    text = "Outline Button",
                    onClick = { onToast("Outline Button diklik") },
                    variant = ButtonVariant.Outline,
                    leadingIcon = Lucide.Heart
                )

                Button(
                    text = "Cancel Button",
                    onClick = { onToast("Cancel Button diklik") },
                    variant = ButtonVariant.Cancel,
                    leadingIcon = Lucide.RotateCw
                )

                Button(
                    text = "Success Button",
                    onClick = { onToast("Success Button diklik") },
                    variant = ButtonVariant.Success,
                    leadingIcon = Lucide.CircleCheck
                )

                Button(
                    text = "Warning Button",
                    onClick = { onToast("Warning Button diklik") },
                    variant = ButtonVariant.Warning,
                    leadingIcon = Lucide.CircleAlert
                )

                Button(
                    text = "Error / Delete Button",
                    onClick = { onToast("Error Button diklik") },
                    variant = ButtonVariant.Error,
                    leadingIcon = Lucide.Trash2
                )

                Button(
                    text = "Ghost Button (Text Only)",
                    onClick = { onToast("Ghost Button diklik") },
                    variant = ButtonVariant.Ghost,
                    leadingIcon = Lucide.ChevronRight
                )
            }

            HorizontalDivider(color = Border, thickness = 1.dp)

            // Ukuran Button
            Text(
                text = "Ukuran Button (LG: 52dp, MD: 44dp, SM: 36dp)",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    text = "Large (52dp)",
                    onClick = { onToast("Button LG diklik") },
                    size = ButtonSize.LG,
                    modifier = Modifier.weight(1f)
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    text = "Medium (44dp)",
                    onClick = { onToast("Button MD diklik") },
                    size = ButtonSize.MD,
                    variant = ButtonVariant.Secondary,
                    modifier = Modifier.weight(1f)
                )
                Button(
                    text = "Small (36dp)",
                    onClick = { onToast("Button SM diklik") },
                    size = ButtonSize.SM,
                    variant = ButtonVariant.Outline,
                    modifier = Modifier.weight(1f)
                )
            }

            HorizontalDivider(color = Border, thickness = 1.dp)

            // Icon Button Section
            Text(
                text = "IconButton (Action Buttons & Notification Badge)",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    icon = Lucide.Bell,
                    contentDescription = "Notifikasi",
                    onClick = { onToast("IconButton Notifikasi dengan badge 5 diklik") },
                    variant = IconButtonVariant.Secondary,
                    size = IconButtonSize.MD,
                    badgeCount = 5
                )

                IconButton(
                    icon = Lucide.Plus,
                    contentDescription = "Tambah",
                    onClick = { onToast("IconButton Primary diklik") },
                    variant = IconButtonVariant.Primary,
                    size = IconButtonSize.MD,
                    shape = IconButtonShape.Circle
                )

                IconButton(
                    icon = Lucide.Search,
                    contentDescription = "Cari",
                    onClick = { onToast("IconButton Outline diklik") },
                    variant = IconButtonVariant.Outline,
                    size = IconButtonSize.MD
                )

                IconButton(
                    icon = Lucide.Settings,
                    contentDescription = "Pengaturan",
                    onClick = { onToast("IconButton Ghost diklik") },
                    variant = IconButtonVariant.Ghost,
                    size = IconButtonSize.MD
                )

                IconButton(
                    icon = Lucide.User,
                    contentDescription = "Profil",
                    onClick = { onToast("IconButton Neutral diklik") },
                    variant = IconButtonVariant.Neutral,
                    size = IconButtonSize.MD,
                    shape = IconButtonShape.Circle
                )
            }

            HorizontalDivider(color = Border, thickness = 1.dp)

            // Live Interactive Playground
            Text(
                text = "Interactive Button Playground",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = Primary
            )

            TextField(
                value = customButtonText,
                onValueChange = { customButtonText = it },
                label = "Teks Tombol"
            )

            // Varian Picker Chips
            Text(text = "Pilih Varian:", fontSize = 12.sp, fontWeight = FontWeight.Medium, color = TextPrimary)
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                ButtonVariant.entries.forEach { variant ->
                    FilterChip(
                        selected = selectedVariant == variant,
                        onClick = { selectedVariant = variant },
                        label = { Text(variant.name, fontSize = 11.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Primary,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }

            // Size & Shape Picker
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = "Ukuran:", fontSize = 12.sp, fontWeight = FontWeight.Medium, color = TextPrimary)
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        ButtonSize.entries.forEach { size ->
                            FilterChip(
                                selected = selectedSize == size,
                                onClick = { selectedSize = size },
                                label = { Text(size.name, fontSize = 11.sp) }
                            )
                        }
                    }
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = "Bentuk:", fontSize = 12.sp, fontWeight = FontWeight.Medium, color = TextPrimary)
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        ButtonShape.entries.forEach { shape ->
                            FilterChip(
                                selected = selectedShape == shape,
                                onClick = { selectedShape = shape },
                                label = { Text(shape.name, fontSize = 11.sp) }
                            )
                        }
                    }
                }
            }

            // Toggle Switches
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Status Aktif (Enabled)", fontSize = 13.sp, color = TextPrimary)
                Switch(
                    checked = isInteractiveEnabled,
                    onCheckedChange = { isInteractiveEnabled = it },
                    colors = SwitchDefaults.colors(checkedThumbColor = Primary, checkedTrackColor = Primary0)
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Status Loading (Spinner)", fontSize = 13.sp, color = TextPrimary)
                Switch(
                    checked = isInteractiveLoading,
                    onCheckedChange = { isInteractiveLoading = it },
                    colors = SwitchDefaults.colors(checkedThumbColor = Primary, checkedTrackColor = Primary0)
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Leading Icon (Kiri)", fontSize = 13.sp, color = TextPrimary)
                Switch(
                    checked = hasInteractiveLeadingIcon,
                    onCheckedChange = { hasInteractiveLeadingIcon = it },
                    colors = SwitchDefaults.colors(checkedThumbColor = Primary, checkedTrackColor = Primary0)
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Trailing Icon (Kanan)", fontSize = 13.sp, color = TextPrimary)
                Switch(
                    checked = hasInteractiveTrailingIcon,
                    onCheckedChange = { hasInteractiveTrailingIcon = it },
                    colors = SwitchDefaults.colors(checkedThumbColor = Primary, checkedTrackColor = Primary0)
                )
            }

            // Interactive Button Preview
            Button(
                text = customButtonText.ifBlank { "Klik Saya" },
                onClick = {
                    buttonClickCount++
                    onToast("Tombol diklik! Total: $buttonClickCount")
                },
                variant = selectedVariant,
                size = selectedSize,
                shape = selectedShape,
                enabled = isInteractiveEnabled,
                isLoading = isInteractiveLoading,
                leadingIcon = if (hasInteractiveLeadingIcon) Lucide.Send else null,
                trailingIcon = if (hasInteractiveTrailingIcon) Lucide.ChevronRight else null
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Neutral0),
                shape = RoundedCornerShape(8.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Total Klik: $buttonClickCount kali",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextPrimary
                    )
                    if (buttonClickCount > 0) {
                        Button(
                            text = "Reset",
                            onClick = { buttonClickCount = 0 },
                            variant = ButtonVariant.Outline,
                            size = ButtonSize.SM,
                            fullWidth = false
                        )
                    }
                }
            }
        }
    }
}

// 2. FORM INPUT & PASSWORD SHOWCASE
@Composable
private fun InputShowcaseSection() {
    var textInput by remember { mutableStateOf("Melanie Refman") }
    var emailInput by remember { mutableStateOf("melanie@example.com") }
    var passwordInput by remember { mutableStateOf("SecretPassword123") }
    var phoneInput by remember { mutableStateOf("081234567890") }
    var rupiahInput by remember { mutableStateOf("5.000.000") }
    var errorInput by remember { mutableStateOf("invalid-format") }
    var isErrorActive by remember { mutableStateOf(true) }

    ShowcaseCard(
        title = "Form Input & PasswordField",
        subtitle = "Komponen input dengan prefix/suffix, label asterisk required, pesan validasi, dan toggle password"
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {

            // Basic Text Field
            TextField(
                value = textInput,
                onValueChange = { textInput = it },
                label = "Nama Lengkap Sesuai KTP",
                required = true,
                placeholder = "Masukkan nama lengkap",
                leadingIcon = Lucide.User,
                helperText = "Gunakan nama asli untuk verifikasi identitas"
            )

            // Email Input
            TextField(
                value = emailInput,
                onValueChange = { emailInput = it },
                label = "Alamat Email",
                required = true,
                placeholder = "contoh@email.com",
                leadingIcon = Lucide.Mail
            )

            // Password Field Component
            PasswordField(
                value = passwordInput,
                onValueChange = { passwordInput = it },
                label = "Kata Sandi Akun",
                required = true,
                placeholder = "Minimal 8 karakter",
                helperText = "Gunakan kombinasi huruf, angka, dan simbol"
            )

            // Currency with Prefix & Suffix
            TextField(
                value = rupiahInput,
                onValueChange = { rupiahInput = it },
                label = "Nominal Pengajuan Pinjaman",
                required = true,
                placeholder = "0",
                prefixText = "Rp",
                suffixText = ",00",
                leadingIcon = Lucide.Wallet,
                helperText = "Maksimum pinjaman hingga Rp 20.000.000"
            )

            // Phone Input
            TextField(
                value = phoneInput,
                onValueChange = { phoneInput = it },
                label = "Nomor Telepon / WhatsApp",
                required = true,
                placeholder = "8xxxxxxxxxx",
                prefixText = "+62",
                leadingIcon = Lucide.Phone
            )

            HorizontalDivider(color = Border, thickness = 1.dp)

            // Error & Validation Controls
            Text(
                text = "Status Validasi & Pesan Error",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Aktifkan State Error", fontSize = 13.sp, color = TextPrimary)
                Switch(
                    checked = isErrorActive,
                    onCheckedChange = { isErrorActive = it },
                    colors = SwitchDefaults.colors(checkedThumbColor = Error, checkedTrackColor = Error0)
                )
            }

            TextField(
                value = errorInput,
                onValueChange = { errorInput = it },
                label = "Format Email dengan Validasi",
                placeholder = "contoh@domain.com",
                leadingIcon = Lucide.Mail,
                errorMessage = if (isErrorActive) "Alamat email tidak valid. Pastikan mengandung @ dan domain." else null
            )

            TextField(
                value = "1234567890123456 (Terkunci)",
                onValueChange = {},
                label = "Nomor KTP (Read-Only / Disabled)",
                enabled = false,
                leadingIcon = Lucide.IdCard,
                helperText = "KTP terverifikasi dan tidak dapat diubah"
            )
        }
    }
}

// 3. CHECKBOX SHOWCASE
@Composable
private fun CheckboxShowcaseSection() {
    var termsChecked by remember { mutableStateOf(true) }
    var privacyChecked by remember { mutableStateOf(false) }
    var promoChecked by remember { mutableStateOf(false) }
    var indeterminateChecked by remember { mutableStateOf(false) }
    var isIndeterminate by remember { mutableStateOf(true) }
    var showErrorCheckbox by remember { mutableStateOf(true) }

    ShowcaseCard(
        title = "Checkbox & CheckboxWithLabel",
        subtitle = "Kotak centang mandiri, row dengan label dan deskripsi, status indeterminate, dan error"
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {

            Text(
                text = "Checkbox Dengan Label & Deskripsi",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary
            )

            CheckboxWithLabel(
                checked = termsChecked,
                onCheckedChange = { termsChecked = it },
                label = "Saya menyetujui Syarat dan Ketentuan SAKU",
                description = "Dengan menyetujui, Anda tunduk pada regulasi OJK dan kebijakan privasi yang berlaku."
            )

            CheckboxWithLabel(
                checked = privacyChecked,
                onCheckedChange = { privacyChecked = it },
                label = "Izinkan akses data kredit perbankan",
                description = "Digunakan semata-mata untuk proses scoring pengajuan pinjaman."
            )

            CheckboxWithLabel(
                checked = promoChecked,
                onCheckedChange = { promoChecked = it },
                label = "Kirim notifikasi promo dan bunga khusus via WhatsApp"
            )

            HorizontalDivider(color = Border, thickness = 1.dp)

            Text(
                text = "Status Khusus: Indeterminate, Error, Disabled",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary
            )

            CheckboxWithLabel(
                checked = indeterminateChecked,
                onCheckedChange = {
                    indeterminateChecked = it
                    isIndeterminate = false
                },
                indeterminate = isIndeterminate,
                label = "Pilih Semua Dokumen Persyaratan",
                description = "Status indeterminate aktif jika sebagian dokumen belum dipilih."
            )

            CheckboxWithLabel(
                checked = !showErrorCheckbox,
                onCheckedChange = { showErrorCheckbox = !it },
                label = "Konfirmasi Kebenaran Data",
                description = "Wajib dicentang sebelum melanjutkan pengajuan",
                errorMessage = if (showErrorCheckbox) "Anda wajib mencentang persetujuan ini" else null
            )

            CheckboxWithLabel(
                checked = true,
                onCheckedChange = {},
                enabled = false,
                label = "Perjanjian Kredit Baku (Terkunci)",
                description = "Dokumen legal yang tidak dapat diubah oleh pemohon"
            )
        }
    }
}

// 4. BADGE SHOWCASE
@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun BadgeShowcaseSection(onToast: (String) -> Unit) {
    val removableBadges = remember {
        mutableStateListOf("Pinjaman Kilat", "Bunga 0.8%", "Tenor 12 Bln", "Verifikasi Instan")
    }

    ShowcaseCard(
        title = "Badge / StatusBadge Component",
        subtitle = "Label status fintech dengan variasi warna soft, solid, outline, pulsing dot, dan removable chips"
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {

            Text(
                text = "Soft Tint Badges (Fintech Status)",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary
            )

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Badge(text = "Primary Soft", variant = BadgeVariant.Primary)
                Badge(text = "Berhasil Disetujui", variant = BadgeVariant.Success, dot = true)
                Badge(text = "Menunggu Verifikasi", variant = BadgeVariant.Warning, dotPulse = true)
                Badge(text = "Pengajuan Ditolak", variant = BadgeVariant.Error, dot = true)
                Badge(text = "Dalam Proses", variant = BadgeVariant.Info, dotPulse = true)
                Badge(text = "Netral", variant = BadgeVariant.Neutral)
                Badge(text = "Premium", variant = BadgeVariant.Purple, leadingIcon = Lucide.Sparkles)
                Badge(text = "Kredit Kilat", variant = BadgeVariant.Orange, leadingIcon = Lucide.TrendingUp)
            }

            HorizontalDivider(color = Border, thickness = 1.dp)

            Text(
                text = "Solid & Outline Badges",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary
            )

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Badge(text = "Solid Primary", variant = BadgeVariant.SolidPrimary)
                Badge(text = "Solid Success", variant = BadgeVariant.SolidSuccess)
                Badge(text = "Solid Warning", variant = BadgeVariant.SolidWarning)
                Badge(text = "Solid Error", variant = BadgeVariant.SolidError)
                Badge(text = "Solid Neutral", variant = BadgeVariant.SolidNeutral)
                Badge(text = "Outline Badge", variant = BadgeVariant.Outline)
            }

            HorizontalDivider(color = Border, thickness = 1.dp)

            Text(
                text = "Ukuran & Bentuk (Pill vs Rounded)",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Badge(text = "Large (13sp)", size = BadgeSize.LG, variant = BadgeVariant.Primary, shape = BadgeShape.Pill)
                Badge(text = "Medium (12sp)", size = BadgeSize.MD, variant = BadgeVariant.Success, shape = BadgeShape.Rounded)
                Badge(text = "Small (11sp)", size = BadgeSize.SM, variant = BadgeVariant.Warning, shape = BadgeShape.Pill)
            }

            HorizontalDivider(color = Border, thickness = 1.dp)

            // Removable Filter Chips
            Text(
                text = "Removable Filter Badges (Ketuk X untuk hapus)",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary
            )

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                removableBadges.forEach { item ->
                    Badge(
                        text = item,
                        variant = BadgeVariant.Primary,
                        removable = true,
                        onRemove = {
                            removableBadges.remove(item)
                            onToast("Filter '$item' dihapus")
                        }
                    )
                }
            }

            if (removableBadges.isEmpty()) {
                Button(
                    text = "Reset Filter Badges",
                    onClick = {
                        removableBadges.addAll(listOf("Pinjaman Kilat", "Bunga 0.8%", "Tenor 12 Bln", "Verifikasi Instan"))
                    },
                    variant = ButtonVariant.Secondary,
                    size = ButtonSize.SM,
                    fullWidth = false
                )
            }
        }
    }
}

// 5. TOPBAR SHOWCASE
@Composable
private fun TopBarShowcaseSection(onToast: (String) -> Unit) {
    var previewTitle by remember { mutableStateOf("Detail Pengajuan Pinjaman") }

    ShowcaseCard(
        title = "TopBar Component",
        subtitle = "App Bar dengan judul rata tengah, navigasi kembali, dan action icon"
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            TextField(
                value = previewTitle,
                onValueChange = { previewTitle = it },
                label = "Ubah Judul TopBar"
            )

            Text(
                text = "Preview 1: Dengan Navigasi Kembali & Action Icons",
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextSecondary
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Background),
                border = androidx.compose.foundation.BorderStroke(1.dp, Border)
            ) {
                TopBar(
                    title = previewTitle,
                    onBackClick = { onToast("Navigasi Kembali diklik") },
                    actions = {
                        IconButton(
                            icon = Lucide.Bell,
                            contentDescription = "Notifikasi",
                            onClick = { onToast("Icon Notifikasi TopBar diklik") },
                            variant = IconButtonVariant.Ghost
                        )
                        IconButton(
                            icon = Lucide.Settings,
                            contentDescription = "Pengaturan",
                            onClick = { onToast("Icon Pengaturan TopBar diklik") },
                            variant = IconButtonVariant.Ghost
                        )
                    }
                )
            }

            Text(
                text = "Preview 2: Header Utama Dashboard (Tanpa Tombol Back)",
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextSecondary
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Background),
                border = androidx.compose.foundation.BorderStroke(1.dp, Border)
            ) {
                TopBar(
                    title = "Dashboard SAKU",
                    onBackClick = null,
                    actions = {
                        IconButton(
                            icon = Lucide.Search,
                            contentDescription = "Cari",
                            onClick = { onToast("Icon Cari diklik") },
                            variant = IconButtonVariant.Ghost
                        )
                    }
                )
            }
        }
    }
}

// 6. LUCIDE ICONS SHOWCASE
data class LucideIconItem(
    val name: String,
    val vector: ImageVector,
    val category: String
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun LucideIconsShowcaseSection() {
    val iconsList = remember {
        listOf(
            // Keuangan & Fintech
            LucideIconItem("Wallet", Lucide.Wallet, "Keuangan & Transaksi"),
            LucideIconItem("CreditCard", Lucide.CreditCard, "Keuangan & Transaksi"),
            LucideIconItem("Landmark", Lucide.Landmark, "Keuangan & Transaksi"),
            LucideIconItem("TrendingUp", Lucide.TrendingUp, "Keuangan & Transaksi"),
            LucideIconItem("Receipt", Lucide.Receipt, "Keuangan & Transaksi"),
            LucideIconItem("DollarSign", Lucide.DollarSign, "Keuangan & Transaksi"),

            // User & Security
            LucideIconItem("User", Lucide.User, "Akun & Keamanan"),
            LucideIconItem("Users", Lucide.Users, "Akun & Keamanan"),
            LucideIconItem("Lock", Lucide.Lock, "Akun & Keamanan"),
            LucideIconItem("Key", Lucide.Key, "Akun & Keamanan"),
            LucideIconItem("ShieldCheck", Lucide.ShieldCheck, "Akun & Keamanan"),
            LucideIconItem("IdCard", Lucide.IdCard, "Akun & Keamanan"),
            LucideIconItem("Eye", Lucide.Eye, "Akun & Keamanan"),
            LucideIconItem("EyeOff", Lucide.EyeOff, "Akun & Keamanan"),
            LucideIconItem("LogOut", Lucide.LogOut, "Akun & Keamanan"),

            // Komunikasi & Notifikasi
            LucideIconItem("Mail", Lucide.Mail, "Komunikasi & Status"),
            LucideIconItem("Phone", Lucide.Phone, "Komunikasi & Status"),
            LucideIconItem("Bell", Lucide.Bell, "Komunikasi & Status"),
            LucideIconItem("Send", Lucide.Send, "Komunikasi & Status"),
            LucideIconItem("Info", Lucide.Info, "Komunikasi & Status"),
            LucideIconItem("CircleCheck", Lucide.CircleCheck, "Komunikasi & Status"),
            LucideIconItem("CircleAlert", Lucide.CircleAlert, "Komunikasi & Status"),

            // Aksi & UI
            LucideIconItem("ArrowLeft", Lucide.ArrowLeft, "Aksi & Navigasi"),
            LucideIconItem("ArrowRight", Lucide.ArrowRight, "Aksi & Navigasi"),
            LucideIconItem("ChevronRight", Lucide.ChevronRight, "Aksi & Navigasi"),
            LucideIconItem("Plus", Lucide.Plus, "Aksi & Navigasi"),
            LucideIconItem("Check", Lucide.Check, "Aksi & Navigasi"),
            LucideIconItem("Search", Lucide.Search, "Aksi & Navigasi"),
            LucideIconItem("Settings", Lucide.Settings, "Aksi & Navigasi"),
            LucideIconItem("Palette", Lucide.Palette, "Aksi & Navigasi"),
            LucideIconItem("House", Lucide.House, "Aksi & Navigasi"),
            LucideIconItem("Briefcase", Lucide.Briefcase, "Aksi & Navigasi"),
            LucideIconItem("ImagePlus", Lucide.ImagePlus, "Aksi & Navigasi"),
            LucideIconItem("RotateCw", Lucide.RotateCw, "Aksi & Navigasi"),
            LucideIconItem("Heart", Lucide.Heart, "Aksi & Navigasi"),
            LucideIconItem("Sparkles", Lucide.Sparkles, "Aksi & Navigasi")
        )
    }

    var selectedIconItem by remember { mutableStateOf(iconsList.first()) }
    var selectedSizeDp by remember { mutableIntStateOf(28) }
    var selectedColor by remember { mutableStateOf(Primary) }
    var selectedColorName by remember { mutableStateOf("Primary") }

    ShowcaseCard(
        title = "Lucide Icons",
        subtitle = "Gaya stroke 2px monoline modern khas Fintech — 1:1 identik dengan frontend web"
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {

            // Interactive Live Playground Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Neutral0),
                border = androidx.compose.foundation.BorderStroke(1.dp, Border)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Interactive Icon Preview",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Primary
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Box(
                            modifier = Modifier
                                .size(72.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Surface)
                                .border(1.dp, Border, RoundedCornerShape(12.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = selectedIconItem.vector,
                                contentDescription = selectedIconItem.name,
                                tint = selectedColor,
                                modifier = Modifier.size(selectedSizeDp.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Lucide.${selectedIconItem.name}",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Text(
                                text = "Kategori: ${selectedIconItem.category}",
                                fontSize = 12.sp,
                                color = TextSecondary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Ukuran: ${selectedSizeDp}dp | Warna: $selectedColorName",
                                fontSize = 11.sp,
                                color = Primary60,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(text = "Pilih Ukuran:", fontSize = 12.sp, fontWeight = FontWeight.Medium, color = TextPrimary)
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf(18, 24, 28, 36, 48).forEach { size ->
                            FilterChip(
                                selected = selectedSizeDp == size,
                                onClick = { selectedSizeDp = size },
                                label = { Text("${size}dp") },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = Primary,
                                    selectedLabelColor = Color.White
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(text = "Pilih Warna Tint:", fontSize = 12.sp, fontWeight = FontWeight.Medium, color = TextPrimary)
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf(
                            Triple("Primary", Primary, Primary),
                            Triple("TextPrimary", TextPrimary, TextPrimary),
                            Triple("Success", Success, Success),
                            Triple("Error", Error, Error),
                            Triple("Warning", Warning, Warning)
                        ).forEach { (name, color, _) ->
                            Box(
                                modifier = Modifier
                                    .size(30.dp)
                                    .clip(CircleShape)
                                    .background(color)
                                    .border(
                                        width = if (selectedColor == color) 2.5.dp else 1.dp,
                                        color = if (selectedColor == color) Neutral else Color.Transparent,
                                        shape = CircleShape
                                    )
                                    .clickable {
                                        selectedColor = color
                                        selectedColorName = name
                                    }
                            )
                        }
                    }
                }
            }

            HorizontalDivider(color = Border, thickness = 1.dp)

            // Categorized Icons Gallery
            val categories = iconsList.map { it.category }.distinct()
            categories.forEach { cat ->
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = cat,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Primary
                    )

                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        iconsList.filter { it.category == cat }.forEach { item ->
                            val isSelected = selectedIconItem.name == item.name
                            Column(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (isSelected) Primary0 else Surface)
                                    .border(
                                        width = if (isSelected) 1.5.dp else 1.dp,
                                        color = if (isSelected) Primary else Border,
                                        shape = RoundedCornerShape(10.dp)
                                    )
                                    .clickable { selectedIconItem = item }
                                    .padding(horizontal = 10.dp, vertical = 8.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    imageVector = item.vector,
                                    contentDescription = item.name,
                                    tint = if (isSelected) Primary else TextPrimary,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = item.name,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) Primary else TextSecondary
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// 7. DESIGN TOKENS SHOWCASE
@Composable
private fun DesignTokensSection() {
    ShowcaseCard(
        title = "Design Tokens",
        subtitle = "Palet Warna dan Skala Tipografi Sistem SAKU"
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Text(
                text = "Palet Warna Utama & Semantik",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ColorChipItem(color = Primary, name = "Primary", hex = "#FF792E", modifier = Modifier.weight(1f))
                ColorChipItem(color = Primary0, name = "Primary 0", hex = "#FFF4EE", modifier = Modifier.weight(1f))
                ColorChipItem(color = Neutral, name = "Neutral", hex = "#252525", modifier = Modifier.weight(1f))
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ColorChipItem(color = Success, name = "Success", hex = "#48BB78", modifier = Modifier.weight(1f))
                ColorChipItem(color = Warning, name = "Warning", hex = "#F59E0B", modifier = Modifier.weight(1f))
                ColorChipItem(color = Error, name = "Error", hex = "#E53E3E", modifier = Modifier.weight(1f))
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ColorChipItem(color = Info, name = "Info (Blue)", hex = "#3B82F6", modifier = Modifier.weight(1f))
                ColorChipItem(color = Purple, name = "Purple", hex = "#9333EA", modifier = Modifier.weight(1f))
                ColorChipItem(color = Orange, name = "Orange", hex = "#EA580C", modifier = Modifier.weight(1f))
            }

            HorizontalDivider(color = Border, thickness = 1.dp)

            Text(
                text = "Tipografi (Overused Grotesk)",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary
            )

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(text = "Headline Large (24sp Bold)", style = Typography.headlineLarge, color = TextPrimary)
                Text(text = "Title Large (18sp SemiBold)", style = Typography.titleLarge, color = TextPrimary)
                Text(text = "Title Medium (16sp Medium)", style = Typography.titleMedium, color = TextPrimary)
                Text(text = "Body Large (16sp Regular)", style = Typography.bodyLarge, color = TextSecondary)
                Text(text = "Body Medium (14sp Regular)", style = Typography.bodyMedium, color = TextSecondary)
                Text(text = "Label Large (14sp SemiBold)", style = Typography.labelLarge, color = Primary)
            }
        }
    }
}

// Showcase Container Card
@Composable
private fun ShowcaseCard(
    title: String,
    subtitle: String,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, Border)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {
            Text(
                text = title,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Text(
                text = subtitle,
                fontSize = 13.sp,
                color = TextSecondary
            )

            Spacer(modifier = Modifier.height(16.dp))

            content()
        }
    }
}

@Composable
private fun ColorChipItem(
    color: Color,
    name: String,
    hex: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(Neutral0)
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(color)
                .border(1.dp, Border, CircleShape)
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(text = name, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
        Text(text = hex, fontSize = 10.sp, color = TextMuted)
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun SandboxScreenPreview() {
    SandboxScreen(onNavigateBack = {})
}
