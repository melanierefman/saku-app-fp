# SAKU Backend Service

> Layanan RESTful API backend platform pinjaman dana tunai digital **SAKU** berbasis **Spring Boot 4.x** dan **Java 21**.

---

## 📖 Dokumentasi API Interaktif (Scalar API Reference)

Layanan backend SAKU dilengkapi antarmuka dokumentasi API modern dari **Scalar API Reference** yang dibangun di atas spesifikasi OpenAPI 3.0:

- 🌐 **Production Scalar Docs**: [https://saku.morpkhai.web.id/scalar](https://saku.morpkhai.web.id/scalar)
- 💻 **Localhost Scalar Docs**: [http://localhost:8080/scalar](http://localhost:8080/scalar) *(alias: `/docs`)*
- 📄 **OpenAPI 3.0 JSON Spec**: `http://localhost:8080/v3/api-docs`

> **Fitur Utama Scalar**:
> - Integrasi tema gelap modern (*dark mode*) dan tampilan responsif.
> - Client interaktif untuk mencoba eksekusi endpoint secara langsung (*Live Request & Response Testing*).
> - Dokumentasi lengkap DTO, validasi form, schema response, status HTTP, dan skema autentikasi Bearer JWT.

---

## 🛠️ Teknologi & Dependensi Utama

| Komponen | Spesifikasi / Library |
| :--- | :--- |
| **Bahasa & Framework** | Java 21 LTS, Spring Boot 4.x |
| **Keamanan (Security)** | Spring Security 6, JWT (JSON Web Token), BCrypt Password Hashing |
| **Persistensi Data** | Spring Data JPA, Hibernate, PostgreSQL 16 (Schema: `mel_saku`) |
| **In-Memory Cache & OTP** | Redis 7 (Spring Data Redis) |
| **Dokumentasi API** | SpringDoc OpenAPI 3.0 + Scalar API Reference CDN |
| **Notifikasi Real-Time** | Server-Sent Events (SSE) & Firebase Cloud Messaging (FCM Admin SDK) |
| **Build Tool & Container** | Apache Maven 3.9+, Docker Multi-Stage Build, Docker Compose |

---

## 📁 Struktur Direktori Backend

```text
backend/
├── database/
│   ├── seeder.sql                 # Data Seeder komprehensif (30 nasabah, 18 pinjaman, RBAC)
│   ├── generate-dummy-files.js    # Generator file dummy dokumen (KTP, Selfie, Slip Gaji, Rek Koran)
│   └── docker-compose.yaml        # PostgreSQL database service lokal
├── nginx/
│   ├── saku.conf                  # Konfigurasi reverse proxy HTTPS + WebSockets/SSE
│   └── saku-bootstrap.conf        # Bootstrap HTTP port 80 untuk sertifikat SSL Let's Encrypt
├── src/main/java/com/bcafinance/backend_saku/
│   ├── core/                      # Config (Security, OpenAPI, CORS, WebMvc), Filter, Exception
│   │   ├── config/                # SecurityConfig, OpenApiConfig, WebConfig, RedisConfig
│   │   ├── entity/                # Master & Transaksi JPA Entities (Customer, Loan, Role, dll)
│   │   ├── exception/             # GlobalExceptionHandler, SecurityExceptionHandler
│   │   ├── filter/                # JwtAuthFilter
│   │   └── realtime/              # SSE Emitter Service & Controller
│   └── features/                  # Modul fitur berbasis domain
│       ├── auth/                  # Autentikasi Karyawan & Customer, OTP Redis
│       ├── backoffice/            # Verifikasi KYC, Persetujuan Pencairan Dana
│       ├── branchmanager/         # Approval Pinjaman Berjenjang oleh BM
│       ├── customer/              # Registrasi Nasabah, Pengajuan Pinjaman, Profil, Notifikasi
│       ├── marketing/             # Review Dokumen & Kelayakan Pengajuan Pinjaman
│       ├── public_api/            # Endpoint publik, ScalarDocsController
│       ├── scoring/               # Intelligent Credit Scoring Engine
│       └── superadmin/            # Master RBAC, Karyawan, Cabang, Plafond, Audit Log
├── src/main/resources/
│   ├── application.properties     # Konfigurasi Spring Boot & variabel env
│   └── firebase-service-account.json # (Opsional) Kredensial FCM Service Account
├── Dockerfile                     # Multi-stage production container build
├── docker-compose.yml             # Service backend, PostgreSQL, dan Redis container
├── pom.xml                        # Konfigurasi Maven dependencies & plugins
└── .env.example                   # Template variabel lingkungan
```

---

## ⚙️ Konfigurasi Environment (`.env`)

Buat file `.env` di dalam folder `backend/` dengan menyalin contoh dari `.env.example`:

```properties
# Database PostgreSQL
DB_URL=jdbc:postgresql://localhost:5433/db_saku?currentSchema=mel_saku
DB_USERNAME=your_db_username
DB_PASSWORD=your_db_password
DB_NAME=db_saku
DB_SCHEMA=mel_saku

# In-Memory Cache & OTP Redis
REDIS_HOST=localhost
REDIS_PORT=6379
REDIS_PASSWORD=your_redis_password
REDIS_KEY_PREFIX=saku:

# Keamanan JWT (Gunakan 256-bit secret key)
JWT_SECRET=your_secure_256bit_jwt_secret_key_here
JWT_EXPIRATION_MS=86400000

# CORS & File Storage
APP_CORS_ALLOWED_ORIGINS=http://localhost:4200
APP_FILE_STORAGE_DIR=uploads
```

---

## 🚀 Panduan Menjalankan Layanan (Development)

### 1. Jalankan Database & Redis (Docker)
```bash
cd backend/database
docker compose up -d
```

### 2. Isi Data Awal & Dummy Uploads
1. Eksekusi file SQL seeder ke database:
   - Buka file [`database/seeder.sql`](./database/seeder.sql) via DBeaver / pgAdmin, atau:
   ```bash
   docker exec -i saku-postgres psql -U user_saku -d db_saku < database/seeder.sql
   ```
2. Buat file dummy dokumen:
   ```bash
   node database/generate-dummy-files.js
   ```

### 3. Jalankan Aplikasi Spring Boot
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

Layanan backend akan aktif di: **`http://localhost:8080`**.  
Buka **`http://localhost:8080/scalar`** di browser untuk melihat dokumentasi API interaktif.

---

## 🛡️ Role-Based Access Control (RBAC) Matrix

| Path Prefix | Hak Akses (*Role Required*) | Deskripsi |
| :--- | :--- | :--- |
| `/api/auth/**` | `Public (PermitAll)` | Login, Registrasi, Request/Verify OTP |
| `/api/public/**` | `Public (PermitAll)` | Health check, informasi produk |
| `/uploads/**` | `Public (PermitAll)` | Static resource dokumen (KTP, Selfie, Slip Gaji) |
| `/scalar`, `/docs`, `/v3/api-docs/**` | `Public (PermitAll)` | Scalar API Reference & OpenAPI JSON spec |
| `/api/customer/**` | `ROLE_CUSTOMER` | Profil nasabah, pengajuan pinjaman, jadwal angsuran |
| `/api/marketing/**` | `ROLE_MARKETING` | Review kelayakan pengajuan, verifikasi kelengkapan berkas |
| `/api/branch-manager/**` | `ROLE_BRANCHMANAGER` | Approval / penolakan pinjaman nominal besar oleh BM |
| `/api/backoffice/**` | `ROLE_BACKOFFICE` | Verifikasi e-KYC nasabah & proses pencairan dana |
| `/api/superadmin/**`, `/api/master/**` | `ROLE_SUPERADMIN` | Pengaturan Role, Menu, Permission, Karyawan, Cabang, Plafond, Audit Log |
| `/api/realtime/**` | `Authenticated (All Roles)` | Stream SSE Real-Time push event data perubahan status |

---

## 🧪 Pengujian Unit & Kompilasi (Testing)

Jalankan test suite menggunakan Maven wrapper:

```bash
# Menjalankan seluruh unit test
./mvnw clean test

# Kompilasi tanpa test
./mvnw clean compile -DskipTests
```
