# SAKU: Solusi Aman Keuangan Untukmu

> **"Pinjam Tenang, Sesuai Kemampuan."**

**SAKU** adalah platform pinjaman dana tunai digital (*smart digital cash loan*) yang mengedepankan prinsip **Responsible Lending (Pinjaman Bertanggung Jawab)**. Didukung oleh mesin evaluasi **Intelligent Credit Scoring**, SAKU memastikan setiap nasabah mendapatkan rekomendasi plafon dan skema cicilan yang benar-benar terukur sesuai kemampuan finansialnya — menghadirkan rasa tenang (*peace of mind*) tanpa kekhawatiran bunga tersembunyi ataupun jeratan utang berlebih (*anti-overindebtedness*).

Ekosistem SAKU menghubungkan nasabah peminjam (*mobile client* Android) dengan portal operasional cabang terintegrasi (Marketing, Branch Manager, Back Office, dan Super Admin) dalam satu alur pembiayaan yang aman, transparan, dan cepat.

---

## 📖 Dokumentasi API Interaktif (Scalar API Reference)

Layanan RESTful API backend SAKU dilengkapi dengan antarmuka dokumentasi modern dan interaktif menggunakan **Scalar API Reference**:

- 🌐 **Dokumentasi Scalar API (Production)**: [https://saku.morpkhai.web.id/scalar](https://saku.morpkhai.web.id/scalar)
- 💻 **Dokumentasi Scalar API (Lokal)**: [http://localhost:8080/scalar](http://localhost:8080/scalar) *(atau `/docs`)*
- 📄 **OpenAPI 3.0 Raw Spec**: [https://saku.morpkhai.web.id/v3/api-docs](https://saku.morpkhai.web.id/v3/api-docs)

> **Fitur Scalar Docs**: Tampilan modern dark/light mode, pencarian endpoint instan, live API testing/request builder, schema type definitions, dan response example.

---

## 💡 Nilai Utama & Keunggulan SAKU

1. **Limit Pintar & Terukur**: Plafon pinjaman dihitung secara adil berdasarkan profil pendapatan, rasio beban cicilan (*Debt Burden Ratio*), dan skor kredit nasabah.
2. **Transparansi Penuh**: Simulasi nominal, suku bunga flat transparan (0.75% - 1.50% / bulan), dan jadwal angsuran ditampilkan jelas sebelum pengajuan.
3. **Alur Verifikasi Cepat & Berjenjang**: Verifikasi identitas KYC (*e-KTP & selfie liveness*), review marketing cabang, approval branch manager, hingga pencairan dana otomatis ke rekening bank.
4. **Keamanan & RBAC Ketat**: Perlindungan berlapis dengan JWT Auth, BCrypt password hashing, Spring Security RBAC, dan audit trail aktivitas staf.

---

## 🏗️ Arsitektur & Komponen Ekosistem

| Komponen | Platform / Framework | Direktori | Deskripsi & Fitur Utama |
| :--- | :--- | :--- | :--- |
| **Backend REST API** | Spring Boot 4.x / Java 21 | [`/backend`](./backend/README.md) | REST API, Spring Security, JWT, PostgreSQL 16, Redis 7, Scalar Docs, SSE Notifications |
| **Web Portal Admin** | Angular 22 / TypeScript | [`/frontend`](./frontend/README.md) | Portal Backoffice, Review Marketing, Approval BM, Master RBAC & Plafond Superadmin |
| **Mobile App Nasabah** | Android Jetpack Compose | `/android` | Registrasi KYC, Pengajuan Pinjaman, Simulasi Angsuran, Biometric Auth, FCM Notifikasi |
| **DevOps & CI/CD** | GitHub Actions & Docker | `/.github/workflows` | Automated Maven Testing, SCP Deployment, Nginx Reverse Proxy, Auto-SSL Certbot |

---

## 📁 Struktur Repositori

```text
saku-app-fp/
├── .github/workflows/
│   ├── backend-ci.yml         # CI/CD Pipeline Backend (Maven Test, SCP & Docker Auto-Deploy)
│   └── frontend-ci.yml        # CI Pipeline Frontend (Angular Lint, Test & Build)
├── android/                   # Aplikasi Mobile Nasabah (Android Jetpack Compose & Kotlin)
├── backend/                   # Layanan Backend RESTful API (Spring Boot 4 & Java 21)
│   ├── database/              # SQL Seeder (30 Customer, 18 Loan), Migrasi, & Dummy Generator
│   ├── nginx/                 # Konfigurasi Reverse Proxy & Auto-SSL
│   └── src/main/java/...      # Controllers, Services, Security, Entities, DTOs
├── frontend/                  # Web Portal Staf Internal (Angular 22 & Tailwind CSS)
│   └── src/app/pages/         # Halaman Marketing, BM, Backoffice, Superadmin, Landing Page
└── README.md                  # Dokumentasi Utama Proyek
```

---

## 🔑 Akun Demo Pengujian (Demo Accounts)

Seluruh akun telah disiapkan melalui database seeder:

| Role | Username | Email | Password | Cabang / Akses |
| :--- | :--- | :--- | :--- | :--- |
| **Super Admin** | `superadmin` | `superadmin@saku.com` | `Admin123#` | Nasional (Full Access & RBAC) |
| **Back Office** | `backoffice` | `backoffice@saku.com` | `Admin123#` | Nasional (KYC Verification & Pencairan) |
| **Marketing (Pusat)** | `marketing` | `marketing@saku.com` | `Admin123#` | Kantor Pusat Jakarta |
| **Marketing (Surabaya)** | `marketingsby` | `marketingsby@saku.com` | `Admin123#` | Cabang Surabaya |
| **Marketing (Bandung)** | `marketingbdg` | `marketingbdg@saku.com` | `Admin123#` | Cabang Bandung |
| **Branch Manager (Pusat)** | `bm` | `bm@saku.com` | `Admin123#` | Kantor Pusat Jakarta |
| **Branch Manager (Surabaya)**| `bmsby` | `bmsby@saku.com` | `Admin123#` | Cabang Surabaya |
| **Branch Manager (Bandung)** | `bmbdg` | `bmbdg@saku.com` | `Admin123#` | Cabang Bandung |
| **Nasabah (Customer)** | `melanie` | `melanie@saku.com` | `Customer123#` | Nasabah Tier Reguler (Silver) |
| **Nasabah (Customer)** | `bagus_sby` | `bagus.sby@saku.com` | `Customer123#` | Nasabah Tier Platinum (Surabaya) |
| **Nasabah (Customer)** | `siti_bandung`| `siti.bandung@saku.com`| `Customer123#` | Nasabah Tier Starter (Bandung) |

---

## 🚀 Panduan Menjalankan Seluruh Proyek (Quick Start)

### 1. Menjalankan Backend API
Lihat panduan lengkap di [backend/README.md](./backend/README.md).
```bash
cd backend
.\mvnw.cmd spring-boot:run   # Windows
./mvnw spring-boot:run       # Linux / macOS
```
> Server aktif di: `http://localhost:8080` (Dokumentasi API: `http://localhost:8080/scalar`)

### 2. Menjalankan Web Portal Frontend
Lihat panduan lengkap di [frontend/README.md](./frontend/README.md).
```bash
cd frontend
npm install
npm start
```
> Web portal aktif di: `http://localhost:4200`

### 3. Menjalankan Mobile App Android
Buka folder `android/` di **Android Studio**, lakukan **Gradle Sync**, dan jalankan pada emulator atau perangkat fisik Android.

---

## 📄 Lisensi & Tim Pengembang
Dikembangkan untuk Final Project.
