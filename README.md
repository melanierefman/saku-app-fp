# SAKU App (Sistem Aplikasi Kredit Usaha)

SAKU adalah platform pengajuan dan persetujuan pembiayaan multiguna digital terintegrasi yang menghubungkan nasabah peminjam (*mobile client*), tim marketing, branch manager, dan tim backoffice dalam satu ekosistem yang aman, transparan, dan cepat.

---

## 🏗️ Arsitektur & Teknologi

Aplikasi SAKU dibangun dengan arsitektur modern multi-platform:

| Komponen | Platform / Framework | Bahasa / Tool | Fitur Utama |
| :--- | :--- | :--- | :--- |
| **Mobile App** | Android (Jetpack Compose) | Kotlin 2.x | Material 3, Biometric/Auth, Room DB, FCM Push Notifications, Root/Security Detection |
| **Backend API** | Spring Boot 4.x | Java 21 | Spring Security, JWT Auth, Spring Data JPA (Hibernate), PostgreSQL 16, Redis 7 (Caching & OTP) |
| **Web Portal** | Angular 22 | TypeScript / Tailwind CSS | Dashboard Monitoring, Review Marketing, Persetujuan BM, Eksekusi Pencairan, Verifikasi KYC |
| **DevOps / CI-CD** | Docker & GitHub Actions | Docker Compose, Bash | Automated Maven Testing, SCP File Transfer, Zero-downtime Container Deployment |

---

## 📁 Struktur Direktori Repositori

```text
saku-app-fp/
├── .github/
│   └── workflows/
│       ├── backend-ci.yml         # CI/CD Pipeline (Build, Test & Auto-deploy SSH/Docker)
│       └── frontend-ci.yml        # CI Pipeline (Build, Lint & Test Angular)
├── android/                       # Aplikasi Mobile Nasabah (Android Jetpack Compose)
│   ├── app/
│   │   ├── src/main/java/...      # UI Components, Screens, ViewModels, Room DB & Services
│   │   └── build.gradle.kts
│   ├── gradle/
│   └── build.gradle.kts
├── backend/                       # Layanan RESTful API Backend (Spring Boot & Java 21)
│   ├── Dockerfile                 # Multi-stage production container build
│   ├── docker-compose.yml         # Orkestrasi container backend & persistensi storage
│   ├── database/
│   │   ├── docker-compose.yaml    # Database PostgreSQL lokal (Development)
│   │   └── seeder.sql             # SQL Data Seeder (Schema: mel_saku)
│   ├── src/main/java/...          # Controllers, Services, Repositories, Entities & Security
│   ├── src/main/resources/        # application.properties & Firebase credentials
│   ├── pom.xml
│   └── .env.production.example
├── frontend/                      # Web Portal Backoffice & Manajemen (Angular 22)
│   ├── src/app/pages/             # Modul Marketing, Branch Manager, Backoffice, Superadmin
│   ├── src/app/core/              # Guards, Interceptors, Services, Models
│   ├── package.json
│   └── angular.json
├── .gitignore
└── README.md
```

---

## ⚙️ Prasyarat Lingkungan (Prerequisites)

Pastikan perangkat Anda telah memasang dependensi berikut:
- **Java JDK 21** (misalnya Eclipse Temurin / OpenJDK 21)
- **Node.js**: v20.x atau v22.x LTS & **npm**
- **Android Studio** (Ladybug / Iguana atau terbaru) + Android SDK 35
- **Docker Engine & Docker Compose**
- **Git**

---

## 🚀 Panduan Memulai Cepat (Quick Start)

### 1. Menjalankan Backend (Spring Boot)

#### Opsi A: Menggunakan Database Lokal (Docker)
1. Jalankan PostgreSQL lokal:
   ```bash
   cd backend/database
   docker compose up -d
   ```
2. Siapkan file `.env` di folder `backend/` (salin dari `.env.example`):
   ```properties
   DB_URL=jdbc:postgresql://localhost:5433/db_saku
   DB_USERNAME=your_local_db_user
   DB_PASSWORD=your_local_db_password
   REDIS_HOST=localhost
   REDIS_PORT=6379
   JWT_SECRET=your_secure_256bit_jwt_secret_here
   ```

#### Opsi B: Menggunakan Database Server Remote
Konfigurasikan `.env` mengarah ke host database dan Redis yang telah disediakan:
```properties
DB_URL=jdbc:postgresql://<DB_HOST>:<DB_PORT>/<DB_NAME>?currentSchema=<SCHEMA_NAME>
DB_USERNAME=<DB_USERNAME>
DB_PASSWORD=<DB_PASSWORD>
DB_SCHEMA=<SCHEMA_NAME>
REDIS_HOST=<REDIS_HOST>
REDIS_PORT=6379
REDIS_USERNAME=<REDIS_USERNAME>
REDIS_PASSWORD=<REDIS_PASSWORD>
REDIS_KEY_PREFIX=<KEY_PREFIX>
```

#### Menjalankan Server API:
- **Windows (PowerShell):**
  ```powershell
  cd backend
  .\mvnw.cmd spring-boot:run
  ```
- **Linux / macOS:**
  ```bash
  cd backend
  ./mvnw spring-boot:run
  ```
Backend aktif di `http://localhost:8080`.

---

### 2. Mengisi Data Awal (Database Seeder)

Setelah backend menyala pertama kali dan struktur tabel otomatis terbentuk di database, jalankan script seeder untuk mengisi data master role, karyawan, cabang, plafond, dan akun uji coba:

- **Via DBeaver / pgAdmin**: Buka koneksi database Anda, lalu buka dan eksekusi file [`backend/database/seeder.sql`](file:///c:/Users/melan/OneDrive/Documents/GitHub/saku-app-fp/backend/database/seeder.sql).
- **Via Terminal (psql / Docker)**:
  ```powershell
  docker exec -i saku-postgres psql -h <DB_HOST> -U <DB_USER> -d <DB_NAME> < backend\database\seeder.sql
  ```

#### Kredensial Pengguna Bawaan (Default Credentials):
| Role | Username | Password | Akses Halaman |
| :--- | :--- | :--- | :--- |
| **Super Admin** | `superadmin` | `password` | `/dashboard`, `/rbac/*`, `/master/*` |
| **Marketing** | `marketing` | `password` | `/pengajuan-pinjaman` (Review & Scoring) |
| **Branch Manager** | `bm` | `password` | `/persetujuan-pinjaman` (Persetujuan Plafond) |
| **Back Office** | `backoffice` | `password` | `/verifikasi-customer`, `/pencairan` |

---

### 3. Menjalankan Web Portal Frontend (Angular)

1. Masuk ke folder `frontend`:
   ```bash
   cd frontend
   ```
2. Pasang dependensi:
   ```bash
   npm install
   ```
3. Jalankan server development:
   ```bash
   npm start
   ```
4. Buka browser di: **`http://localhost:4200/`**.

---

### 4. Menjalankan Aplikasi Mobile (Android)

1. Buka **Android Studio**.
2. Pilih menu **File &rarr; Open**, arahkan ke subfolder **`android/`**.
3. Tunggu proses **Gradle Sync** selesai secara otomatis.
4. Hubungkan HP Android fisik (dengan *USB Debugging* aktif) atau gunakan Android Virtual Device (AVD Emulator).
5. Klik tombol hijau **Run 'app' (Shift + F10)**.

---

## 🔄 CI / CD Pipeline & Deployment Otomatis

Repositori ini telah dikonfigurasi dengan pipeline **GitHub Actions** yang beroperasi secara otomatis:

### 1. Pipeline Backend (`backend-ci.yml`)
- **Tahap CI (Integrasi)**:
  - Berjalan pada setiap push / pull-request ke branch `dev` dan `main`.
  - Menginisialisasi service container PostgreSQL & Redis di runner Ubuntu.
  - Menjalankan unit test dan compile Maven (`./mvnw clean test -B`).
- **Tahap CD (Deployment Produksi)**:
  - Otomatis terpicu saat ada push / merge ke branch **`main`**.
  - Mengambil konfigurasi rahasia dari GitHub Secrets (`PROD_ENV_FILE`, `SERVER_SSH_KEY`, `SERVER_HOST`, `SERVER_USER`).
  - Mentransfer berkas backend dan file `.env` ke target server VPS produksi via protokol SCP aman.
  - Membangun dan menyalakan kontainer aplikasi secara otomatis melalui Docker Compose (`docker compose up -d --build`).

### 2. Pipeline Frontend (`frontend-ci.yml`)
- Memvalidasi ketergantungan paket Node.js (`npm ci`).
- Menjalankan unit testing Angular.
- Melakukan kompilasi build production bundle (`npm run build --configuration production`).

---

## 🌿 Strategi Percabangan Git (Branching Strategy)

- **`main`**: Branch produksi stabil. Setiap perubahan yang di-push ke branch ini akan langsung diuji dan di-deploy otomatis ke server VPS produksi.
- **`dev`**: Branch pengembangan aktif utama untuk integrasi fitur sebelum dirilis ke produksi.
- **`feat/*`**: Branch fitur individual untuk pengerjaan modul tertentu sebelum digabungkan ke branch `dev`.
