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
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.composables.icons.lucide.ArrowLeft
import com.composables.icons.lucide.Briefcase
import com.composables.icons.lucide.CreditCard
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.MapPin
import com.composables.icons.lucide.User
import com.example.saku.app.core.ui.components.Button
import com.example.saku.app.core.ui.components.ButtonSize
import com.example.saku.app.core.ui.components.ButtonVariant
import com.example.saku.app.core.ui.components.ConfirmationDialog
import com.example.saku.app.core.ui.components.CurrencyField
import com.example.saku.app.core.ui.components.DialogType
import com.example.saku.app.core.ui.components.DropdownField
import com.example.saku.app.core.ui.components.DropdownOption
import com.example.saku.app.core.ui.components.TextField
import com.example.saku.app.ui.theme.Background
import com.example.saku.app.ui.theme.Border
import com.example.saku.app.ui.theme.Primary
import com.example.saku.app.ui.theme.Surface
import com.example.saku.app.ui.theme.TextPrimary
import com.example.saku.app.ui.theme.TextSecondary
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    initialTab: String = "REKENING",
    onNavigateBack: () -> Unit,
    viewModel: EditProfileViewModel = koinViewModel()
) {
    val context = LocalContext.current
    val customerProfile by viewModel.customerProfile.collectAsState()
    val isUpdating by viewModel.isProfileUpdating.collectAsState()

    // Wilayah Cascading States
    val provinces by viewModel.provinces.collectAsState()
    val regencies by viewModel.regencies.collectAsState()
    val districts by viewModel.districts.collectAsState()
    val villages by viewModel.villages.collectAsState()

    val selectedProvince by viewModel.selectedProvince.collectAsState()
    val selectedRegency by viewModel.selectedRegency.collectAsState()
    val selectedDistrict by viewModel.selectedDistrict.collectAsState()
    val selectedVillage by viewModel.selectedVillage.collectAsState()

    val isLoadingWilayah by viewModel.isLoadingWilayah.collectAsState()
    val isLoadingRegencies by viewModel.isLoadingRegencies.collectAsState()
    val isLoadingDistricts by viewModel.isLoadingDistricts.collectAsState()
    val isLoadingVillages by viewModel.isLoadingVillages.collectAsState()

    var activeTab by remember(initialTab) {
        val normalized = initialTab.trim().uppercase()
        mutableStateOf(if (normalized in listOf("REKENING", "DOMISILI", "PEKERJAAN")) normalized else "REKENING")
    }

    val currentTab = when (activeTab.trim().uppercase()) {
        "DOMISILI" -> "DOMISILI"
        "PEKERJAAN" -> "PEKERJAAN"
        else -> "REKENING"
    }

    // Opsi Pilihan Bank (Sama dengan Register)
    val bankOptions = listOf(
        DropdownOption(value = "BCA", label = "BCA (Bank Central Asia)"),
        DropdownOption(value = "MANDIRI", label = "Bank Mandiri"),
        DropdownOption(value = "BRI", label = "BRI (Bank Rakyat Indonesia)"),
        DropdownOption(value = "BNI", label = "BNI (Bank Negara Indonesia)"),
        DropdownOption(value = "CIMB", label = "CIMB Niaga"),
        DropdownOption(value = "PERMATA", label = "Bank Permata"),
        DropdownOption(value = "DANAMON", label = "Bank Danamon"),
        DropdownOption(value = "BSI", label = "BSI (Bank Syariah Indonesia)")
    )

    // Opsi Status Pekerjaan (Sama dengan Register)
    val jobStatusOptions = listOf(
        DropdownOption(value = "KARYAWAN_TETAP", label = "Karyawan Tetap"),
        DropdownOption(value = "KARYAWAN_KONTRAK", label = "Karyawan Kontrak"),
        DropdownOption(value = "WIRAUSAHA", label = "Wirausaha / Pemilik Usaha"),
        DropdownOption(value = "PROFESIONAL", label = "Profesional / Freelancer"),
        DropdownOption(value = "PNS_BUMN", label = "PNS / Pegawai BUMN"),
        DropdownOption(value = "IBU_RUMAH_TANGGA", label = "Ibu Rumah Tangga"),
        DropdownOption(value = "LAINNYA", label = "Lainnya")
    )

    // Rekening Form States
    var namaBank by remember(customerProfile) { mutableStateOf(customerProfile?.namaBank ?: "BCA") }
    var noRekening by remember(customerProfile) { mutableStateOf(customerProfile?.noRekening ?: "") }
    var namaRekening by remember(customerProfile) { mutableStateOf(customerProfile?.namaRekening ?: (customerProfile?.nama ?: "")) }

    // Domisili Form States
    var alamat by remember(customerProfile) { mutableStateOf(customerProfile?.alamatDomisili?.alamatLengkap ?: "") }
    var rt by remember(customerProfile) { mutableStateOf(customerProfile?.alamatDomisili?.rt ?: "") }
    var rw by remember(customerProfile) { mutableStateOf(customerProfile?.alamatDomisili?.rw ?: "") }
    var kodePos by remember(customerProfile) { mutableStateOf(customerProfile?.alamatDomisili?.kodePos ?: "") }

    // Pekerjaan Form States
    var pekerjaan by remember(customerProfile) { mutableStateOf(customerProfile?.pekerjaan ?: "") }
    var tempatKerja by remember(customerProfile) { mutableStateOf(customerProfile?.tempatKerja ?: "") }
    var statusPekerjaan by remember(customerProfile) { mutableStateOf(customerProfile?.statusPekerjaan ?: "KARYAWAN_TETAP") }
    var penghasilan by remember(customerProfile) { mutableStateOf(customerProfile?.penghasilanBulanan?.toLong()?.toString() ?: "") }
    var lamaBekerja by remember(customerProfile) { mutableStateOf(customerProfile?.lamaBekerjaBulan?.toString() ?: "") }
    var cicilanLain by remember(customerProfile) { mutableStateOf(customerProfile?.totalCicilanLainBulanan?.toLong()?.toString() ?: "0") }

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
                .padding(innerPadding)
                .imePadding(),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 14.dp, bottom = 48.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Tab Selector: Rekening | Domisili | Pekerjaan
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
                // ==================== TAB REKENING ====================
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

                                val selectedBankOption = bankOptions.find {
                                    it.value.equals(namaBank, ignoreCase = true) ||
                                    it.label.startsWith(namaBank, ignoreCase = true)
                                } ?: DropdownOption(value = namaBank, label = namaBank)

                                DropdownField(
                                    options = bankOptions,
                                    selectedOption = selectedBankOption,
                                    onOptionSelect = { opt -> opt?.let { namaBank = it.value } },
                                    label = "Nama Bank",
                                    placeholder = "Pilih Bank Pencairan",
                                    leadingIcon = Lucide.CreditCard,
                                    required = true
                                )

                                TextField(
                                    value = noRekening,
                                    onValueChange = { noRekening = it },
                                    label = "Nomor Rekening",
                                    placeholder = "Contoh: 1234567890",
                                    leadingIcon = Lucide.CreditCard,
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    required = true
                                )

                                TextField(
                                    value = namaRekening,
                                    onValueChange = { namaRekening = it },
                                    label = "Nama Pemilik Rekening",
                                    placeholder = "Sesuai nama di buku tabungan",
                                    leadingIcon = Lucide.User,
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

                // ==================== TAB DOMISILI ====================
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

                                // 1. Provinsi (API Cascading)
                                DropdownField(
                                    options = provinces,
                                    selectedOption = selectedProvince,
                                    onOptionSelect = { viewModel.onProvinceSelected(it) },
                                    label = "Provinsi Domisili",
                                    placeholder = if (isLoadingWilayah) "Memuat daftar provinsi..." else "Pilih Provinsi...",
                                    required = true,
                                    searchable = true,
                                    isLoading = isLoadingWilayah,
                                    leadingIcon = Lucide.MapPin
                                )

                                // 2. Kota / Kabupaten (API Cascading)
                                DropdownField(
                                    options = regencies,
                                    selectedOption = selectedRegency,
                                    onOptionSelect = { viewModel.onRegencySelected(it) },
                                    label = "Kota / Kabupaten Domisili",
                                    placeholder = if (selectedProvince == null) "Pilih provinsi terlebih dahulu" else if (isLoadingRegencies) "Memuat kota/kabupaten..." else "Pilih Kota / Kabupaten...",
                                    required = true,
                                    searchable = true,
                                    enabled = selectedProvince != null,
                                    isLoading = isLoadingRegencies,
                                    leadingIcon = Lucide.MapPin
                                )

                                // 3. Kecamatan (API Cascading)
                                DropdownField(
                                    options = districts,
                                    selectedOption = selectedDistrict,
                                    onOptionSelect = { viewModel.onDistrictSelected(it) },
                                    label = "Kecamatan Domisili",
                                    placeholder = if (selectedRegency == null) "Pilih kota/kabupaten terlebih dahulu" else if (isLoadingDistricts) "Memuat kecamatan..." else "Pilih Kecamatan...",
                                    required = true,
                                    searchable = true,
                                    enabled = selectedRegency != null,
                                    isLoading = isLoadingDistricts,
                                    leadingIcon = Lucide.MapPin
                                )

                                // 4. Kelurahan / Desa (API Cascading)
                                DropdownField(
                                    options = villages,
                                    selectedOption = selectedVillage,
                                    onOptionSelect = { viewModel.onVillageSelected(it) },
                                    label = "Kelurahan / Desa Domisili",
                                    placeholder = if (selectedDistrict == null) "Pilih kecamatan terlebih dahulu" else if (isLoadingVillages) "Memuat kelurahan/desa..." else "Pilih Kelurahan / Desa...",
                                    required = true,
                                    searchable = true,
                                    enabled = selectedDistrict != null,
                                    isLoading = isLoadingVillages,
                                    leadingIcon = Lucide.MapPin
                                )

                                // 5. Alamat Lengkap
                                TextField(
                                    value = alamat,
                                    onValueChange = { alamat = it },
                                    label = "Alamat Lengkap",
                                    placeholder = "Nama jalan, nomor rumah",
                                    required = true
                                )

                                // 6. RT & RW
                                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                    TextField(
                                        value = rt,
                                        onValueChange = { rt = it },
                                        label = "RT",
                                        placeholder = "001",
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                        modifier = Modifier.weight(1f),
                                        required = true
                                    )
                                    TextField(
                                        value = rw,
                                        onValueChange = { rw = it },
                                        label = "RW",
                                        placeholder = "002",
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                        modifier = Modifier.weight(1f),
                                        required = true
                                    )
                                }

                                // 7. Kode Pos
                                TextField(
                                    value = kodePos,
                                    onValueChange = { kodePos = it },
                                    label = "Kode Pos",
                                    placeholder = "Contoh: 12345",
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    required = true
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                Button(
                                    text = "Simpan Perubahan Domisili",
                                    onClick = {
                                        val provVal = selectedProvince?.label ?: customerProfile?.alamatDomisili?.provinsi ?: ""
                                        val kotaVal = selectedRegency?.label ?: customerProfile?.alamatDomisili?.kotaKabupaten ?: ""
                                        val kecVal = selectedDistrict?.label ?: customerProfile?.alamatDomisili?.kecamatan ?: ""
                                        val kelVal = selectedVillage?.label ?: customerProfile?.alamatDomisili?.kelurahan ?: ""

                                        if (provVal.isBlank() || kotaVal.isBlank() || kecVal.isBlank() || kelVal.isBlank() || alamat.isBlank() || rt.isBlank() || rw.isBlank() || kodePos.isBlank()) {
                                            Toast.makeText(context, "Semua data alamat domisili wajib diisi lengkap", Toast.LENGTH_SHORT).show()
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

                // ==================== TAB PEKERJAAN ====================
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

                                val selectedJobStatusOption = jobStatusOptions.find {
                                    it.value.equals(statusPekerjaan, ignoreCase = true) ||
                                    it.label.equals(statusPekerjaan, ignoreCase = true)
                                } ?: DropdownOption(value = statusPekerjaan, label = statusPekerjaan)

                                DropdownField(
                                    options = jobStatusOptions,
                                    selectedOption = selectedJobStatusOption,
                                    onOptionSelect = { opt -> opt?.let { statusPekerjaan = it.value } },
                                    label = "Status Pekerjaan",
                                    placeholder = "Pilih status pekerjaan",
                                    required = true
                                )

                                CurrencyField(
                                    amount = penghasilan.toLongOrNull(),
                                    onAmountChange = { penghasilan = it?.toString() ?: "" },
                                    label = "Penghasilan Bersih Bulanan",
                                    placeholder = "0",
                                    required = true,
                                    quickAmounts = emptyList()
                                )

                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                    TextField(
                                        value = lamaBekerja,
                                        onValueChange = { lamaBekerja = it },
                                        label = "Lama Kerja (Bulan)",
                                        placeholder = "24",
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                        modifier = Modifier.weight(1f)
                                    )

                                    CurrencyField(
                                        amount = cicilanLain.toLongOrNull(),
                                        onAmountChange = { cicilanLain = it?.toString() ?: "0" },
                                        label = "Cicilan Lain / Bulan",
                                        placeholder = "0",
                                        quickAmounts = emptyList(),
                                        modifier = Modifier.weight(1.3f)
                                    )
                                }

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

            item {
                Spacer(modifier = Modifier.height(24.dp).navigationBarsPadding())
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
        "DOMISILI" -> "Pastikan data alamat domisili sudah sesuai."
        "PEKERJAAN" -> "Pastikan informasi pekerjaan dan penghasilan sudah sesuai."
        else -> "Pastikan data rekening bank aktif dan sesuai nama Anda."
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
                    val provVal = selectedProvince?.label ?: customerProfile?.alamatDomisili?.provinsi ?: ""
                    val kotaVal = selectedRegency?.label ?: customerProfile?.alamatDomisili?.kotaKabupaten ?: ""
                    val kecVal = selectedDistrict?.label ?: customerProfile?.alamatDomisili?.kecamatan ?: ""
                    val kelVal = selectedVillage?.label ?: customerProfile?.alamatDomisili?.kelurahan ?: ""

                    viewModel.updateDomisili(
                        alamat = alamat,
                        rt = rt,
                        rw = rw,
                        kelurahan = kelVal,
                        kecamatan = kecVal,
                        kota = kotaVal,
                        provinsi = provVal,
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
                        lamaBekerjaBulan = lamaBekerja.toIntOrNull(),
                        totalCicilanLainBulanan = cicilanLain.toDoubleOrNull(),
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