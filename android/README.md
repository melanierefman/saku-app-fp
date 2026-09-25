# SAKU Android Customer Application

> Aplikasi mobile resmi **SAKU** untuk nasabah berbasis **Android (Kotlin)** dan **Jetpack Compose**.

Aplikasi ini digunakan oleh calon nasabah dan debitur untuk melakukan registrasi akun digital, verifikasi identitas (e-KTP & Selfie KYC), simulasi pinjaman transparan, pengajuan pembiayaan *paperless*, pemantauan status persetujuan secara *real-time*, hingga penerimaan notifikasi pencairan dana.

---

## 📖 Dokumentasi API Terkait (Scalar API Reference)

Seluruh komunikasi data antara aplikasi Android dan server backend SAKU mengacu pada spesifikasi **Scalar API Reference**:

- 🌐 **Production Scalar API**: [https://saku.morpkhai.web.id/scalar](https://saku.morpkhai.web.id/scalar)
- 💻 **Localhost Scalar API**: [http://localhost:8080/scalar](http://localhost:8080/scalar)

---

## 🛠️ Teknologi & Arsitektur Mobile

| Komponen | Spesifikasi / Library |
| :--- | :--- |
| **Bahasa Pemrograman** | Kotlin 2.x |
| **SDK Targets** | Min SDK 29 (Android 10), Target SDK 37 (Android 15) |
| **UI Toolkit & Design** | Jetpack Compose, Material 3, SAKU Custom Design System (Orange Theme) |
| **Pola Arsitektur** | MVVM (Model-View-ViewModel) + Repository Pattern |
| **Networking & HTTP** | Retrofit 2, OkHttp 4, Gson Converter |
| **Autentikasi & Sesi** | JWT Bearer Token, `AuthInterceptor`, `TokenAuthenticator` (Auto Refresh Token) |
| **Image Loading** | Coil (`AsyncImage`) untuk KTP, Selfie, dan Dokumen Pinjaman |
| **Ikonografi** | Lucide Icons for Compose (`com.composables.icons.lucide`) |
| **Notifikasi Push** | Firebase Cloud Messaging (FCM Client SDK) |
| **Asinkronus & State** | Kotlin Coroutines, `StateFlow`, `SharedFlow`, `collectAsStateWithLifecycle` |

---

## 📁 Struktur Direktori Android

```text
android/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/example/saku/app/
│   │   │   │   ├── core/                        # Modul Inti & Infrastructure
│   │   │   │   │   ├── data/                    # Repository Implementations & Data Sources
│   │   │   │   │   ├── network/                 # Retrofit Client, API Services, DTOs, Interceptors
│   │   │   │   │   │   ├── AuthInterceptor.kt    # Injeksi JWT Token ke Header Request
│   │   │   │   │   │   └── TokenAuthenticator.kt # Penanganan Auto-Refresh Token saat 401 Unauthorized
│   │   │   │   │   ├── ui/                      # Design Tokens, Color Palettes, Reusable UI Components
│   │   │   │   │   └── util/                    # Helper (Currency Formatter, Date Parser, Image Utils)
│   │   │   │   ├── features/                    # Modul Fitur Aplikasi (UI + ViewModel)
│   │   │   │   │   ├── auth/                    # Login, Reset Password, & Multistep KYC Registration (Step 0..6)
│   │   │   │   │   │   ├── login/               # Layar Login Nasabah
│   │   │   │   │   │   ├── register/            # Wizard Registrasi 6 Langkah (Personal, Pekerjaan, Alamat, KYC)
│   │   │   │   │   │   ├── forgotpassword/      # Form Pemulihan Kata Sandi via Email OTP
│   │   │   │   │   │   └── verification/        # Status Screen Verifikasi KYC (Pending, Verified, Rejected)
│   │   │   │   │   ├── home/                    # Dashboard Utama (Beranda, Tab Pinjaman, Tagihan, Profil)
│   │   │   │   │   ├── loans/                   # Siklus Pengajuan & Tracking Pinjaman
│   │   │   │   │   │   ├── apply/               # Wizard Pengajuan 3 Langkah (Simulasi, Upload Dokumen, Summary)
│   │   │   │   │   │   └── detail/              # Tracking Berjenjang Status Pengajuan & Bukti Pencairan
│   │   │   │   │   ├── simulation/              # Kalkulator Interaktif Simulasi Angsuran & Tenor
│   │   │   │   │   ├── notification/            # Pusat Notifikasi FCM & In-App Alerts
│   │   │   │   │   ├── history/                 # Arsip Riwayat Transaksi Pinjaman
│   │   │   │   │   └── profile/                 # Edit Data Diri & Ganti Password Nasabah
│   │   │   │   ├── navigation/                  # AppNavHost & Definisi Rute Navigasi Compose
│   │   │   │   └── MainActivity.kt              # Single Activity Entry Point
│   │   │   ├── res/                             # Drawable Resources, Vector Icons, & App Config
│   │   │   └── AndroidManifest.xml              # Izin Aplikasi (Camera, Internet, File Access, Push Notification)
│   │   └── androidTest/                         # Integration & Repository Android Unit Tests
│   └── build.gradle.kts                         # Konfigurasi Module Build, Dependencies, & Signing Config
├── build.gradle.kts                             # Root Build Gradle Script
└── gradle.properties                            # Gradle Performance & JVM Settings
```

---

## 📱 Alur Penggunaan Utama (*User Journey*)

1. **Registrasi & KYC Verifikasi (Paperless Onboarding)**
   - Calon nasabah memverifikasi email via OTP 6-digit.
   - Pengisian data pribadi, pekerjaan, rekening penerima, dan alamat KTP/domisili.
   - Pengunggahan foto e-KTP dan foto *selfie* memegang KTP.
   - Penentuan kata sandi akun dan persetujuan Syarat & Ketentuan.
   - Akun masuk ke status *Menunggu Verifikasi* tim Back Office (estimasi 1 jam kerja).

2. **Simulasi & Pengajuan Pinjaman (*Loan Application*)**
   - Nasabah melihat sisa limit plafond kredit hasil *scoring*.
   - Simulasi nominal pinjaman dan durasi tenor dengan perhitungan *Loan Health Score* (rasio kemampuan bayar).
   - Pengunggahan dokumen persyaratan (Slip Gaji / Rekening Koran / NPWP).
   - Peninjauan ringkasan rincian biaya admin dan estimasi dana cair bersih, kemudian pengajuan dikirim.

3. **Pelacakan Berjenjang & Notifikasi (*Real-time Tracking*)**
   - Nasabah memantau tahapan persetujuan secara transparan (*Submitted* $\rightarrow$ *Marketing Review* $\rightarrow$ *Branch Manager Approval* $\rightarrow$ *Back Office Disbursement*).
   - Penerimaan *Push Notification* otomatis saat status pengajuan atau verifikasi diperbarui.

---

## 🚀 Panduan Membuka & Menjalankan Proyek

### Prasyarat
- **Android Studio** (Disarankan versi Ladybug / 2024.2.1 atau lebih baru).
- **JDK 17** atau **JDK 21**.
- Device Android Fisik atau Emulator (Android 10.0+ / API 29+).

### Langkah Menjalankan Aplikasi
1. Buka folder `android/` di Android Studio.
2. Pastikan file `keystore.properties` atau konfigurasi API URL pada `ApiConstants.kt` sudah mengarah ke backend yang aktif:
   - **Localhost Emulator**: `http://10.0.2.2:8080/api/`
   - **Production Backend**: `https://saku.morpkhai.web.id/api/`
3. Lakukan **Sync Project with Gradle Files**.
4. Pilih target Emulator / HP Android terhubung, lalu klik tombol **Run** (`Shift + F10`).