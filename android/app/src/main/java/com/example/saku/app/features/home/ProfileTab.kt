package com.example.saku.app.features.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.composables.icons.lucide.Bell
import com.composables.icons.lucide.Briefcase
import com.composables.icons.lucide.Calculator
import com.composables.icons.lucide.ChevronRight
import com.composables.icons.lucide.CreditCard
import com.composables.icons.lucide.IdCard
import com.composables.icons.lucide.LogOut
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Palette
import com.composables.icons.lucide.ShieldCheck
import com.composables.icons.lucide.User
import com.example.saku.app.core.data.UserSession
import com.example.saku.app.core.network.dto.CustomerProfileDto
import com.example.saku.app.core.ui.components.Badge
import com.example.saku.app.core.ui.components.BadgeSize
import com.example.saku.app.core.ui.components.BadgeVariant
import com.example.saku.app.core.ui.components.Button
import com.example.saku.app.core.ui.components.ButtonSize
import com.example.saku.app.core.ui.components.ButtonVariant
import com.example.saku.app.ui.theme.Background
import com.example.saku.app.ui.theme.Border
import com.example.saku.app.ui.theme.Primary
import com.example.saku.app.ui.theme.Primary0
import com.example.saku.app.ui.theme.Surface
import com.example.saku.app.ui.theme.TextMuted
import com.example.saku.app.ui.theme.TextPrimary
import com.example.saku.app.ui.theme.TextSecondary

@Composable
fun ProfileTabContent(
    customerProfile: CustomerProfileDto?,
    userSession: UserSession?,
    onLogoutClick: () -> Unit,
    onNotificationClick: () -> Unit,
    onSimulasiClick: () -> Unit,
    onSandboxClick: (() -> Unit)?,
) {
    val displayName = customerProfile?.nama ?: (userSession?.nama ?: (userSession?.username ?: "Nasabah SAKU"))
    val displayEmail = customerProfile?.email ?: (userSession?.email ?: "-")
    val displayPhone = customerProfile?.noHp ?: (userSession?.noHp ?: "-")
    val isKycVerified = customerProfile?.isKycVerified ?: false
    val initials = displayName.trim().take(2).uppercase()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Background),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Header Tab Profil
        item {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Profil Nasabah",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    letterSpacing = (-0.5).sp
                )
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = "Kelola informasi akun & data diri Anda",
                    fontSize = 13.sp,
                    color = TextSecondary
                )
            }
        }

        // 2. User Hero Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Surface),
                border = BorderStroke(1.dp, Border)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(Primary0),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = initials,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Primary
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = displayName,
                            fontSize = 15.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = displayEmail,
                            fontSize = 12.sp,
                            color = TextSecondary
                        )
                        Spacer(modifier = Modifier.height(6.dp))

                        Badge(
                            text = if (isKycVerified) "KYC Terverifikasi" else "Menunggu Verifikasi KYC",
                            variant = if (isKycVerified) BadgeVariant.Success else BadgeVariant.Warning,
                            size = BadgeSize.SM
                        )
                    }
                }
            }
        }

        // 3. Section: Informasi Identitas
        item {
            ProfileInfoSectionCard(
                title = "Informasi Identitas & KTP",
                icon = Lucide.IdCard,
                items = listOf(
                    "NIK" to (customerProfile?.nik ?: "-"),
                    "Nama Lengkap" to displayName,
                    "Email" to displayEmail,
                    "No. Handphone" to displayPhone
                )
            )
        }

        // 4. Section: Informasi Rekening Bank
        item {
            ProfileInfoSectionCard(
                title = "Rekening Bank Pencairan",
                icon = Lucide.CreditCard,
                items = listOf(
                    "Nama Bank" to (customerProfile?.namaBank ?: "BCA (Bank Central Asia)"),
                    "Nomor Rekening" to (customerProfile?.noRekening ?: "-"),
                    "Atas Nama" to (customerProfile?.namaRekening ?: displayName)
                )
            )
        }

        // 5. Section: Informasi Pekerjaan
        item {
            ProfileInfoSectionCard(
                title = "Informasi Pekerjaan",
                icon = Lucide.Briefcase,
                items = listOf(
                    "Profesi / Pekerjaan" to (customerProfile?.pekerjaan ?: "-"),
                    "Tempat Kerja" to (customerProfile?.tempatKerja ?: "-"),
                    "Status Karyawan" to (customerProfile?.statusPekerjaan ?: "-")
                )
            )
        }

        // 6. Section: Pengaturan & Menu Cepat
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Surface),
                border = BorderStroke(1.dp, Border)
            ) {
                Column(modifier = Modifier.padding(vertical = 6.dp)) {
                    ProfileMenuRow(
                        icon = Lucide.Bell,
                        title = "Pemberitahuan & Notifikasi",
                        subtitle = "Lihat pembaruan status dan pesan sistem",
                        onClick = onNotificationClick
                    )

                    ProfileMenuRow(
                        icon = Lucide.Calculator,
                        title = "Simulasi Pinjaman SAKU",
                        subtitle = "Hitung estimasi angsuran & bunga pinjaman",
                        onClick = onSimulasiClick
                    )

                    if (onSandboxClick != null) {
                        ProfileMenuRow(
                            icon = Lucide.Palette,
                            title = "Developer UI Sandbox",
                            subtitle = "Katalog komponen desain sistem Compose",
                            onClick = onSandboxClick
                        )
                    }
                }
            }
        }

        // 7. Tombol Logout
        item {
            Spacer(modifier = Modifier.height(4.dp))
            Button(
                text = "Keluar dari Akun",
                onClick = onLogoutClick,
                variant = ButtonVariant.Error,
                size = ButtonSize.LG,
                fullWidth = true,
                leadingIcon = Lucide.LogOut
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun ProfileInfoSectionCard(
    title: String,
    icon: ImageVector,
    items: List<Pair<String, String>>,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Surface),
        border = BorderStroke(1.dp, Border)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Primary0),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = Primary,
                        modifier = Modifier.size(16.dp)
                    )
                }
                Text(
                    text = title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            items.forEachIndexed { index, (label, value) ->
                if (index > 0) {
                    Spacer(modifier = Modifier.height(10.dp))
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = label, fontSize = 12.sp, color = TextMuted)
                    Text(
                        text = value,
                        fontSize = 12.5.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary
                    )
                }
            }
        }
    }
}

@Composable
private fun ProfileMenuRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 18.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(34.dp)
                .clip(CircleShape)
                .background(Primary0),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Primary,
                modifier = Modifier.size(17.dp)
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Text(
                text = subtitle,
                fontSize = 11.5.sp,
                color = TextSecondary
            )
        }
        Icon(
            imageVector = Lucide.ChevronRight,
            contentDescription = null,
            tint = TextMuted,
            modifier = Modifier.size(18.dp)
        )
    }
}
