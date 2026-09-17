package com.example.saku.app.features.profile.edit

import android.widget.Toast
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.composables.icons.lucide.ArrowLeft
import com.composables.icons.lucide.Briefcase
import com.composables.icons.lucide.CreditCard
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.MapPin
import com.example.saku.app.core.ui.components.Button
import com.example.saku.app.core.ui.components.ButtonSize
import com.example.saku.app.core.ui.components.ButtonVariant
import com.example.saku.app.core.ui.components.ConfirmationDialog
import com.example.saku.app.core.ui.components.DialogType
import com.example.saku.app.core.ui.components.TextField
import com.example.saku.app.features.home.HomeViewModel
import com.example.saku.app.ui.theme.Background
import com.example.saku.app.ui.theme.Border
import com.example.saku.app.ui.theme.Neutral10
import com.example.saku.app.ui.theme.Primary
import com.example.saku.app.ui.theme.Primary0
import com.example.saku.app.ui.theme.Surface
import com.example.saku.app.ui.theme.TextPrimary
import com.example.saku.app.ui.theme.TextSecondary
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    initialTab: String = "REKENING",
    onNavigateBack: () -> Unit,
    viewModel: HomeViewModel = koinViewModel()
) {
    val context = LocalContext.current
    val customerProfile by viewModel.customerProfile.collectAsState()
    val isUpdating by viewModel.isProfileUpdating.collectAsState()

    var activeTab by remember(initialTab) {
        val normalized = initialTab.trim().uppercase()
        mutableStateOf(if (normalized in listOf("REKENING", "DOMISILI", "PEKERJAAN")) normalized else "REKENING")
    }

    val currentTab = when (activeTab.trim().uppercase()) {
        "DOMISILI" -> "DOMISILI"
        "PEKERJAAN" -> "PEKERJAAN"
        else -> "REKENING"
    }

    // Rekening Form States
    var namaBank by remember(customerProfile) { mutableStateOf(customerProfile?.namaBank ?: "BCA") }
    var noRekening by remember(customerProfile) { mutableStateOf(customerProfile?.noRekening ?: "") }
    var namaRekening by remember(customerProfile) { mutableStateOf(customerProfile?.namaRekening ?: (customerProfile?.nama ?: "")) }

    // Domisili Form States
    var alamat by remember(customerProfile) { mutableStateOf(customerProfile?.alamatDomisili?.alamatLengkap ?: "") }
    var rt by remember(customerProfile) { mutableStateOf(customerProfile?.alamatDomisili?.rt ?: "") }
    var rw by remember(customerProfile) { mutableStateOf(customerProfile?.alamatDomisili?.rw ?: "") }
    var kelurahan by remember(customerProfile) { mutableStateOf(customerProfile?.alamatDomisili?.kelurahan ?: "") }
    var kecamatan by remember(customerProfile) { mutableStateOf(customerProfile?.alamatDomisili?.kecamatan ?: "") }
    var kota by remember(customerProfile) { mutableStateOf(customerProfile?.alamatDomisili?.kotaKabupaten ?: "") }
    var provinsi by remember(customerProfile) { mutableStateOf(customerProfile?.alamatDomisili?.provinsi ?: "") }
    var kodePos by remember(customerProfile) { mutableStateOf(customerProfile?.alamatDomisili?.kodePos ?: "") }

    // Pekerjaan Form States
    var pekerjaan by remember(customerProfile) { mutableStateOf(customerProfile?.pekerjaan ?: "") }
    var tempatKerja by remember(customerProfile) { mutableStateOf(customerProfile?.tempatKerja ?: "") }
    var statusPekerjaan by remember(customerProfile) { mutableStateOf(customerProfile?.statusPekerjaan ?: "") }
    var penghasilan by remember(customerProfile) { mutableStateOf(customerProfile?.penghasilanBulanan?.toLong()?.toString() ?: "") }

    var showConfirmDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Surface,
                border = BorderStroke(1.dp, Border),
                shadowElevation = 1.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Lucide.ArrowLeft,
                            contentDescription = "Kembali",
                            tint = TextPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = "Edit Data Nasabah",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }
            }
        },
        containerColor = Background
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Tab Selector: Rekening | Domisili | Pekerjaan (Di dalam body halaman)
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFF1F5F9))
                        .padding(4.dp)
                ) {
                    ProfileTabPill(
                        title = "Rekening",
                        icon = Lucide.CreditCard,
                        isSelected = currentTab == "REKENING",
                        onClick = { activeTab = "REKENING" },
                        modifier = Modifier.weight(1f)
                    )
                    ProfileTabPill(
                        title = "Domisili",
                        icon = Lucide.MapPin,
                        isSelected = currentTab == "DOMISILI",
                        onClick = { activeTab = "DOMISILI" },
                        modifier = Modifier.weight(1f)
                    )
                    ProfileTabPill(
                        title = "Pekerjaan",
                        icon = Lucide.Briefcase,
                        isSelected = currentTab == "PEKERJAAN",
                        onClick = { activeTab = "PEKERJAAN" },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            when (currentTab) {
                "REKENING" -> {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(18.dp),
                            colors = CardDefaults.cardColors(containerColor = Surface),
                            border = BorderStroke(1.dp, Border)
                        ) {
                            Column(
                                modifier = Modifier.padding(18.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Text(
                                    text = "Informasi Rekening Bank Pencairan",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                                Text(
                                    text = "Dana pinjaman SAKU yang disetujui akan ditransfer langsung ke rekening ini.",
                                    fontSize = 12.sp,
                                    color = TextSecondary
                                )

                                Spacer(modifier = Modifier.height(4.dp))

                                TextField(
                                    value = namaBank,
                                    onValueChange = { namaBank = it },
                                    label = "Nama Bank",
                                    placeholder = "Contoh: BCA, Bank Mandiri, BRI",
                                    required = true
                                )

                                TextField(
                                    value = noRekening,
                                    onValueChange = { noRekening = it },
                                    label = "Nomor Rekening",
                                    placeholder = "Masukkan nomor rekening",
                                    required = true
                                )

                                TextField(
                                    value = namaRekening,
                                    onValueChange = { namaRekening = it },
                                    label = "Nama Pemilik Rekening",
                                    placeholder = "Sesuai nama di buku tabungan",
                                    required = true
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                Button(
                                    text = "Simpan Perubahan Rekening",
                                    onClick = {
                                        if (namaBank.isBlank() || noRekening.isBlank() || namaRekening.isBlank()) {
                                            Toast.makeText(context, "Semua data rekening wajib diisi", Toast.LENGTH_SHORT).show()
                                            return@Button
                                        }
                                        showConfirmDialog = true
                                    },
                                    isLoading = isUpdating,
                                    variant = ButtonVariant.Primary,
                                    size = ButtonSize.LG,
                                    fullWidth = true
                                )
                            }
                        }
                    }
                }

                "DOMISILI" -> {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(18.dp),
                            colors = CardDefaults.cardColors(containerColor = Surface),
                            border = BorderStroke(1.dp, Border)
                        ) {
                            Column(
                                modifier = Modifier.padding(18.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Text(
                                    text = "Alamat Domisili Tempat Tinggal",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                                Text(
                                    text = "Pastikan alamat domisili tempat tinggal Anda saat ini sudah sesuai.",
                                    fontSize = 12.sp,
                                    color = TextSecondary
                                )

                                Spacer(modifier = Modifier.height(4.dp))

                                TextField(
                                    value = alamat,
                                    onValueChange = { alamat = it },
                                    label = "Alamat Lengkap",
                                    placeholder = "Nama jalan, nomor rumah, RT/RW",
                                    required = true
                                )

                                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                    TextField(
                                        value = rt,
                                        onValueChange = { rt = it },
                                        label = "RT",
                                        placeholder = "001",
                                        modifier = Modifier.weight(1f),
                                        required = true
                                    )
                                    TextField(
                                        value = rw,
                                        onValueChange = { rw = it },
                                        label = "RW",
                                        placeholder = "002",
                                        modifier = Modifier.weight(1f),
                                        required = true
                                    )
                                }

                                TextField(
                                    value = kelurahan,
                                    onValueChange = { kelurahan = it },
                                    label = "Kelurahan / Desa",
                                    placeholder = "Nama kelurahan",
                                    required = true
                                )

                                TextField(
                                    value = kecamatan,
                                    onValueChange = { kecamatan = it },
                                    label = "Kecamatan",
                                    placeholder = "Nama kecamatan",
                                    required = true
                                )

                                TextField(
                                    value = kota,
                                    onValueChange = { kota = it },
                                    label = "Kota / Kabupaten",
                                    placeholder = "Nama kota",
                                    required = true
                                )

                                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                    TextField(
                                        value = provinsi,
                                        onValueChange = { provinsi = it },
                                        label = "Provinsi",
                                        placeholder = "DKI Jakarta",
                                        modifier = Modifier.weight(1.2f),
                                        required = true
                                    )
                                    TextField(
                                        value = kodePos,
                                        onValueChange = { kodePos = it },
                                        label = "Kode Pos",
                                        placeholder = "12345",
                                        modifier = Modifier.weight(0.8f),
                                        required = true
                                    )
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                Button(
                                    text = "Simpan Perubahan Domisili",
                                    onClick = {
                                        if (alamat.isBlank() || rt.isBlank() || rw.isBlank() || kelurahan.isBlank() || kecamatan.isBlank() || kota.isBlank() || provinsi.isBlank() || kodePos.isBlank()) {
                                            Toast.makeText(context, "Semua data alamat domisili wajib diisi", Toast.LENGTH_SHORT).show()
                                            return@Button
                                        }
                                        showConfirmDialog = true
                                    },
                                    isLoading = isUpdating,
                                    variant = ButtonVariant.Primary,
                                    size = ButtonSize.LG,
                                    fullWidth = true
                                )
                            }
                        }
                    }
                }

                "PEKERJAAN" -> {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(18.dp),
                            colors = CardDefaults.cardColors(containerColor = Surface),
                            border = BorderStroke(1.dp, Border)
                        ) {
                            Column(
                                modifier = Modifier.padding(18.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Text(
                                    text = "Informasi Pekerjaan & Finansial",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                                Text(
                                    text = "Data pekerjaan digunakan dalam penilaian kelayakan limit plafond pinjaman.",
                                    fontSize = 12.sp,
                                    color = TextSecondary
                                )

                                Spacer(modifier = Modifier.height(4.dp))

                                TextField(
                                    value = pekerjaan,
                                    onValueChange = { pekerjaan = it },
                                    label = "Profesi / Pekerjaan",
                                    placeholder = "Contoh: Karyawan Swasta, PNS, Wiraswasta",
                                    required = true
                                )

                                TextField(
                                    value = tempatKerja,
                                    onValueChange = { tempatKerja = it },
                                    label = "Nama Perusahaan / Tempat Kerja",
                                    placeholder = "PT Nama Perusahaan",
                                    required = true
                                )

                                TextField(
                                    value = statusPekerjaan,
                                    onValueChange = { statusPekerjaan = it },
                                    label = "Status Karyawan",
                                    placeholder = "Karyawan Tetap / Kontrak / Profesional",
                                    required = true
                                )

                                TextField(
                                    value = penghasilan,
                                    onValueChange = { penghasilan = it },
                                    label = "Penghasilan Bersih Bulanan (Rp)",
                                    placeholder = "Contoh: 7500000",
                                    required = true
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                Button(
                                    text = "Simpan Perubahan Pekerjaan",
                                    onClick = {
                                        if (pekerjaan.isBlank() || tempatKerja.isBlank() || statusPekerjaan.isBlank() || penghasilan.isBlank()) {
                                            Toast.makeText(context, "Semua data pekerjaan & finansial wajib diisi", Toast.LENGTH_SHORT).show()
                                            return@Button
                                        }
                                        showConfirmDialog = true
                                    },
                                    isLoading = isUpdating,
                                    variant = ButtonVariant.Primary,
                                    size = ButtonSize.LG,
                                    fullWidth = true
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Modal Confirmation Dialog for Edit Profile Actions
    val dialogTitle = when (currentTab) {
        "DOMISILI" -> "Simpan Perubahan Domisili?"
        "PEKERJAAN" -> "Simpan Perubahan Pekerjaan?"
        else -> "Simpan Perubahan Rekening?"
    }
    val dialogMessage = when (currentTab) {
        "DOMISILI" -> "Pastikan seluruh data alamat domisili tempat tinggal Anda sudah lengkap dan sesuai data terkini."
        "PEKERJAAN" -> "Pastikan informasi profesi, tempat kerja, dan penghasilan bulanan sudah sesuai dengan data sebenarnya."
        else -> "Pastikan nomor rekening dan nama pemilik rekening pencairan sudah benar dan aktif atas nama Anda."
    }
    val dialogIcon = when (currentTab) {
        "DOMISILI" -> Lucide.MapPin
        "PEKERJAAN" -> Lucide.Briefcase
        else -> Lucide.CreditCard
    }

    ConfirmationDialog(
        visible = showConfirmDialog,
        title = dialogTitle,
        message = dialogMessage,
        type = DialogType.INFO,
        icon = dialogIcon,
        confirmButtonText = "Ya, Simpan",
        dismissButtonText = "Batal",
        confirmButtonVariant = ButtonVariant.Primary,
        isLoading = isUpdating,
        onConfirm = {
            when (currentTab) {
                "DOMISILI" -> {
                    viewModel.updateDomisili(
                        alamat = alamat,
                        rt = rt,
                        rw = rw,
                        kelurahan = kelurahan,
                        kecamatan = kecamatan,
                        kota = kota,
                        provinsi = provinsi,
                        kodePos = kodePos,
                        onSuccess = { msg ->
                            showConfirmDialog = false
                            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                        },
                        onError = { err ->
                            showConfirmDialog = false
                            Toast.makeText(context, err, Toast.LENGTH_LONG).show()
                        }
                    )
                }
                "PEKERJAAN" -> {
                    viewModel.updatePekerjaan(
                        pekerjaan = pekerjaan,
                        tempatKerja = tempatKerja,
                        statusPekerjaan = statusPekerjaan,
                        penghasilanBulanan = penghasilan.toDoubleOrNull(),
                        onSuccess = { msg ->
                            showConfirmDialog = false
                            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                        },
                        onError = { err ->
                            showConfirmDialog = false
                            Toast.makeText(context, err, Toast.LENGTH_LONG).show()
                        }
                    )
                }
                else -> {
                    viewModel.updateRekening(
                        namaBank = namaBank,
                        noRekening = noRekening,
                        namaRekening = namaRekening,
                        onSuccess = { msg ->
                            showConfirmDialog = false
                            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                        },
                        onError = { err ->
                            showConfirmDialog = false
                            Toast.makeText(context, err, Toast.LENGTH_LONG).show()
                        }
                    )
                }
            }
        },
        onDismiss = {
            if (!isUpdating) {
                showConfirmDialog = false
            }
        }
    )
}

@Composable
private fun ProfileTabPill(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(if (isSelected) Surface else Color.Transparent)
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isSelected) Primary else TextSecondary,
                modifier = Modifier.size(15.dp)
            )
            Text(
                text = title,
                fontSize = 12.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = if (isSelected) TextPrimary else TextSecondary
            )
        }
    }
}
