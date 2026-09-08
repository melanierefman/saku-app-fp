# SAKU App

---

## 📁 Struktur Direktori

```text
saku-app-fp/
├── .github/
│   └── workflows/
│       ├── backend-ci.yml       # CI Pipeline untuk Backend
│       └── frontend-ci.yml      # CI Pipeline untuk Frontend
├── backend/                     # Layanan Backend (Spring Boot 4.x / Java 21)
│   ├── database/
│   │   └── docker-compose.yaml  # Docker Compose untuk Database PostgreSQL
│   ├── src/
│   ├── pom.xml
│   └── ...
├── frontend/                    # Aplikasi Frontend (Angular 22 / Tailwind CSS)
│   ├── src/
│   ├── package.json
│   └── ...
├── .gitignore
└── README.md
```

---

## ⚙️ Prasyarat (Prerequisites)

Pastikan dependensi berikut sudah terpasang di komputer Anda:
- **Java JDK 21** (misalnya Temurin / OpenJDK 21)
- **Node.js**: v20.x atau v22.x LTS & **npm**
- **Docker Desktop** (untuk database PostgreSQL)
- **Git**

---

## 🚀 Panduan Memulai Cepat (Quick Start)

### 1. Setup & Jalankan Database (PostgreSQL)

1. Buat file `.env` di dalam folder `backend/`:
   ```bash
   cp backend/.env.example backend/.env   # atau buat manual file backend/.env
   ```
   Pastikan variabel `DB_NAME`, `DB_USERNAME`, `DB_PASSWORD`, dan `DB_URL` sudah sesuai.

2. Jalankan container database PostgreSQL via Docker Compose:
   ```bash
   cd backend/database
   docker compose --env-file ../.env up -d
   ```
   Database PostgreSQL akan berjalan di port `5433` (atau sesuai konfigurasi di `docker-compose.yaml`).

---

### 2. Menjalankan Backend (Spring Boot)

1. Buka terminal baru dan masuk ke folder `backend`:
   ```bash
   cd backend
   ```

2. Jalankan aplikasi Spring Boot:
   - **Windows (PowerShell/CMD):**
     ```powershell
     .\mvnw.cmd spring-boot:run
     ```
   - **Linux / macOS:**
     ```bash
     ./mvnw spring-boot:run
     ```
   Backend akan aktif di `http://localhost:8080`.

---

### 3. Menjalankan Frontend (Angular)

1. Buka terminal baru dan masuk ke folder `frontend`:
   ```bash
   cd frontend
   ```

2. Install dependensi package:
   ```bash
   npm install
   ```

3. Jalankan server development:
   ```bash
   npm start
   ```
   Akses antarmuka web di `http://localhost:4200/`.

---

## 🔄 CI / CD Pipeline (GitHub Actions)

Repositori ini menggunakan GitHub Actions yang otomatis berjalan saat ada `push` atau `pull request` ke branch `main` dan `dev`:
- **`backend-ci.yml`**: Menjalankan PostgreSQL & Redis service, build Maven, dan unit test otomatis jika ada perubahan pada folder `backend/**`.
- **`frontend-ci.yml`**: Menjalankan validasi dependensi, build production Angular, dan menyimpan build artifact jika ada perubahan pada folder `frontend/**`.

---

## 🌿 Branching Strategy

- **`main`**: Branch produksi stabil.
- **`dev`**: Branch pengembangan utama (staging / development).
