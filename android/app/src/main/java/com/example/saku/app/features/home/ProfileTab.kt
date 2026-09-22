package com.example.saku.app.features.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import android.widget.Toast
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.composables.icons.lucide.Briefcase
import com.composables.icons.lucide.ChevronRight
import com.composables.icons.lucide.CreditCard
import com.composables.icons.lucide.Headphones
import com.composables.icons.lucide.IdCard
import com.composables.icons.lucide.KeyRound
import com.composables.icons.lucide.LogOut
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.MapPin
import com.composables.icons.lucide.Pencil
import com.composables.icons.lucide.ShieldCheck
import com.example.saku.app.R
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
import com.example.saku.app.ui.theme.Primary60
import com.example.saku.app.ui.theme.Primary70
import com.example.saku.app.ui.theme.Surface
import com.example.saku.app.ui.theme.TextMuted
import com.example.saku.app.ui.theme.TextPrimary
import com.example.saku.app.ui.theme.TextSecondary
import java.text.NumberFormat
import java.util.Locale

@Composable
fun ProfileTabContent(
    customerProfile: CustomerProfileDto?,
    userSession: UserSession?,
    onLogoutClick: () -> Unit,
    onNotificationClick: () -> Unit,
    onSimulasiClick: () -> Unit,
    onEditProfileClick: (String) -> Unit,
    onChangePasswordClick: () -> Unit,
    onSandboxClick: (() -> Unit)?,
) {
    val listState = rememberLazyListState()
    val currencyFormatter = remember { NumberFormat.getNumberInstance(Locale.forLanguageTag("id-ID")) }

    val displayName = customerProfile?.nama ?: (userSession?.nama ?: (userSession?.username ?: "Nasabah SAKU"))
    val displayEmail = customerProfile?.email ?: (userSession?.email ?: "-")
    val displayPhone = customerProfile?.noHp ?: (userSession?.noHp ?: "-")
    val initials = displayName.trim().take(2).uppercase()

    val penghasilanFormatted = customerProfile?.penghasilanBulanan?.let {
        "Rp ${currencyFormatter.format(it)}"
    } ?: "-"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.linearGradient(
                    listOf(
                        Primary,
                        Primary60,
                        Primary70
                    )
                )
            )
    )

    {
        // 1. TALL ORANGE HEADER (Avatar, Nama, Email, dan Level Plafond)
        Box(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            // Background Card Wave Pattern
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
                        transformOrigin = TransformOrigin(0.85f, 0.5f)
                    )
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 18.dp, end = 18.dp, top = 8.dp, bottom = 18.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Top Action Bar
                Box(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Profil Saya",
                        fontSize = 17.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        letterSpacing = 0.3.sp,
                        modifier = Modifier.align(Alignment.Center)
                    )

                    // Quick Edit Button in Top Right
                    Row(
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.White)
                            .clickable { onEditProfileClick("REKENING") }
                            .padding(horizontal = 10.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Lucide.Pencil,
                            contentDescription = "Edit Profil",
                            tint = Primary,
                            modifier = Modifier.size(12.dp)
                        )
                        Text(
                            text = "Edit",
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Circular Avatar Inside Orange Header
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .shadow(6.dp, CircleShape)
                        .clip(CircleShape)
                        .background(Color.White)
                        .padding(2.5.dp)
                        .clip(CircleShape)
                        .background(Primary0),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = initials,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Primary
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Nama Nasabah in White
                Text(
                    text = displayName,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(2.dp))

                // Email in Translucent White
                Text(
                    text = displayEmail,
                    fontSize = 12.sp,
                    color = Color.White.copy(alpha = 0.88f)
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Level / Tier Plafond Badge langsung dari Backend (tierPlafond)
                val rawTierName = customerProfile?.tierPlafond?.takeIf { it.isNotBlank() } ?: "Tier Reguler"
                val tierVariant = when {
                    rawTierName.contains("Platinum", ignoreCase = true) -> BadgeVariant.Primary
                    rawTierName.contains("Prioritas", ignoreCase = true) -> BadgeVariant.Success
                    rawTierName.contains("Reguler", ignoreCase = true) -> BadgeVariant.Warning
                    rawTierName.contains("Starter", ignoreCase = true) -> BadgeVariant.Neutral
                    else -> BadgeVariant.Neutral
                }

                Badge(
                    text = rawTierName,
                    variant = tierVariant,
                    size = BadgeSize.SM
                )
            }
        }

        // 2. WHITE SHEET BODY WITH ROUNDED TOP CORNERS
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                .background(Background)
        ) {
            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(top = 16.dp, bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // 1. SECTION: INFORMASI DATA DIRI (CLEAN & SIMPLE UNIFIED CARD)
                item {
                    Box(modifier = Modifier.padding(horizontal = 18.dp)) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Surface),
                            border = BorderStroke(1.dp, Border)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = "Informasi Akun & Data Diri",
                                    fontSize = 13.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )

                                Spacer(modifier = Modifier.height(14.dp))

                                // Item 1: Identitas KTP
                                ProfileInfoSimpleItem(
                                    icon = Lucide.IdCard,
                                    title = "Identitas KTP",
                                    primaryValue = "NIK: ${customerProfile?.nik ?: "-"}",
                                    secondaryValue = "No. Handphone: $displayPhone"
                                )

                                HorizontalDivider(
                                    modifier = Modifier.padding(vertical = 12.dp),
                                    color = Border.copy(alpha = 0.5f),
                                    thickness = 0.8.dp
                                )

                                // Item 2: Rekening Pencairan
                                val bankName = customerProfile?.namaBank ?: "BCA"
                                val rekeningNo = customerProfile?.noRekening ?: "-"
                                val atasNama = customerProfile?.namaRekening ?: displayName
                                ProfileInfoSimpleItem(
                                    icon = Lucide.CreditCard,
                                    title = "Rekening Pencairan",
                                    primaryValue = "$bankName • $rekeningNo",
                                    secondaryValue = "a/n $atasNama",
                                    onEditClick = { onEditProfileClick("REKENING") }
                                )

                                HorizontalDivider(
                                    modifier = Modifier.padding(vertical = 12.dp),
                                    color = Border.copy(alpha = 0.5f),
                                    thickness = 0.8.dp
                                )

                                // Item 3: Alamat Domisili
                                val domisili = customerProfile?.alamatDomisili
                                val alamatPrimary = domisili?.alamatLengkap ?: "-"
                                val alamatSecondary = if (domisili != null && (domisili.kotaKabupaten != null || domisili.provinsi != null)) {
                                    val rtRw = if (!domisili.rt.isNullOrBlank() || !domisili.rw.isNullOrBlank()) "RT ${domisili.rt ?: "-"}/RW ${domisili.rw ?: "-"}, " else ""
                                    val kota = domisili.kotaKabupaten ?: ""
                                    val prov = if (!domisili.provinsi.isNullOrBlank()) ", ${domisili.provinsi}" else ""
                                    val pos = if (!domisili.kodePos.isNullOrBlank()) " ${domisili.kodePos}" else ""
                                    "$rtRw$kota$prov$pos".trim().removePrefix(",").trim()
                                } else null

                                ProfileInfoSimpleItem(
                                    icon = Lucide.MapPin,
                                    title = "Alamat Domisili",
                                    primaryValue = alamatPrimary,
                                    secondaryValue = alamatSecondary,
                                    onEditClick = { onEditProfileClick("DOMISILI") }
                                )

                                HorizontalDivider(
                                    modifier = Modifier.padding(vertical = 12.dp),
                                    color = Border.copy(alpha = 0.5f),
                                    thickness = 0.8.dp
                                )

                                // Item 4: Pekerjaan & Penghasilan
                                val pekerjaan = customerProfile?.pekerjaan ?: "-"
                                val tempatKerja = customerProfile?.tempatKerja
                                val pekerjaanPrimary = if (!tempatKerja.isNullOrBlank()) "$pekerjaan • $tempatKerja" else pekerjaan

                                ProfileInfoSimpleItem(
                                    icon = Lucide.Briefcase,
                                    title = "Informasi Pekerjaan",
                                    primaryValue = pekerjaanPrimary,
                                    secondaryValue = "Penghasilan: $penghasilanFormatted",
                                    onEditClick = { onEditProfileClick("PEKERJAAN") }
                                )
                            }
                        }
                    }
                }

                // 2. SECTION: KEAMANAN & MENU PENGATURAN
                item {
                    val context = LocalContext.current
                    Box(modifier = Modifier.padding(horizontal = 18.dp)) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Surface),
                            border = BorderStroke(1.dp, Border)
                        ) {
                            Column(modifier = Modifier.padding(vertical = 4.dp)) {
                                ProfileMenuRow(
                                    icon = Lucide.KeyRound,
                                    title = "Ganti Kata Sandi",
                                    subtitle = "Perbarui kata sandi akun SAKU Anda",
                                    onClick = onChangePasswordClick
                                )

                                HorizontalDivider(
                                    modifier = Modifier.padding(horizontal = 16.dp),
                                    color = Border.copy(alpha = 0.5f),
                                    thickness = 0.8.dp
                                )

                                ProfileMenuRow(
                                    icon = Lucide.Headphones,
                                    title = "Pusat Bantuan & Layanan CS",
                                    subtitle = "Hubungi Halo SAKU (1500888) & CS SAKU",
                                    onClick = {
                                        Toast.makeText(context, "Layanan Halo SAKU: 1500888 atau email halosaku@saku.co.id", Toast.LENGTH_LONG).show()
                                    }
                                )

                                HorizontalDivider(
                                    modifier = Modifier.padding(horizontal = 16.dp),
                                    color = Border.copy(alpha = 0.5f),
                                    thickness = 0.8.dp
                                )

                                ProfileMenuRow(
                                    icon = Lucide.ShieldCheck,
                                    title = "Syarat & Ketentuan",
                                    subtitle = "Kebijakan privasi & regulasi berizin",
                                    onClick = {
                                        Toast.makeText(context, "SAKU berizin dan diawasi sesuai hukum yang berlaku.", Toast.LENGTH_SHORT).show()
                                    }
                                )
                            }
                        }
                    }
                }

                // 3. TOMBOL LOGOUT & APP VERSION FOOTER
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 18.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Button(
                            text = "Keluar dari Akun",
                            onClick = onLogoutClick,
                            variant = ButtonVariant.Error,
                            size = ButtonSize.LG,
                            fullWidth = true,
                            leadingIcon = Lucide.LogOut
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "SAKU Mobile App v1.0.0\nPT SAKU • 2026",
                            fontSize = 11.5.sp,
                            color = TextMuted,
                            textAlign = TextAlign.Center,
                            lineHeight = 16.sp
                        )

                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun ProfileInfoSimpleItem(
    icon: ImageVector,
    title: String,
    primaryValue: String,
    secondaryValue: String? = null,
    onEditClick: (() -> Unit)? = null,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        // Bare black icon with no outer fill/container
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = TextPrimary,
            modifier = Modifier
                .padding(top = 2.dp)
                .size(18.dp)
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    fontSize = 11.5.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextSecondary
                )

                if (onEditClick != null) {
                    Text(
                        text = "Ubah",
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = Primary,
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .clickable(onClick = onEditClick)
                            .padding(horizontal = 4.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = primaryValue,
                fontSize = 12.5.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary
            )

            if (!secondaryValue.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(1.5.dp))
                Text(
                    text = secondaryValue,
                    fontSize = 11.5.sp,
                    color = TextMuted
                )
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
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Bare black icon with no outer container/fill
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = TextPrimary,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary
            )
            if (subtitle.isNotBlank()) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    fontSize = 11.sp,
                    color = TextSecondary
                )
            }
        }
        Icon(
            imageVector = Lucide.ChevronRight,
            contentDescription = null,
            tint = TextMuted,
            modifier = Modifier.size(16.dp)
        )
    }
}
