# SAKU Web Portal Frontend

> Portal Web Manajemen & Operasional Internal **SAKU** berbasis **Angular 22** dan **Tailwind CSS**.

Portal ini digunakan oleh tim operasional internal (Marketing, Branch Manager, Back Office, dan Super Admin) untuk mengelola seluruh siklus pembiayaan pinjaman digital secara real-time, cepat, dan aman.

---

## 📖 Dokumentasi API Terkait (Scalar API Reference)

Seluruh interaksi API antara Web Portal dan Backend mengacu pada dokumentasi **Scalar API Reference**:

- 🌐 **Production Scalar API**: [https://saku.morpkhai.web.id/scalar](https://saku.morpkhai.web.id/scalar)
- 💻 **Localhost Scalar API**: [http://localhost:8080/scalar](http://localhost:8080/scalar)

---

## 🛠️ Teknologi & Arsitektur Frontend

| Komponen | Spesifikasi / Library |
| :--- | :--- |
| **Framework & Versi** | Angular 22 (Standalone Components Architecture) |
| **Bahasa Pemrograman** | TypeScript 5.x |
| **Styling & UI** | Tailwind CSS 3.x, Custom Design System & Glassmorphism |
| **Ikonografi** | Lucide Angular Icons & FontAwesome |
| **Manajemen State & HTTP** | RxJS, Angular HttpClient Interceptors (JWT Bearer Auth & Global Error Handling) |
| **Notifikasi Real-Time** | Server-Sent Events (SSE) Client Service |
| **Unit Test Runner** | Vitest (`ng test` / `vitest`) |
| **Build & Tooling** | Angular CLI 22, Vite Builder |

---

## 📁 Struktur Direktori Frontend

```text
frontend/
├── src/
│   ├── app/
│   │   ├── core/                  # Singleton Services, Guards, Interceptors, Models
│   │   │   ├── guards/            # AuthGuard, RoleGuard (RBAC Protection)
│   │   │   ├── interceptors/      # AuthInterceptor (JWT Injection), ErrorInterceptor
│   │   │   ├── services/          # AuthService, SseService, ApiService, NotificationService
│   │   │   └── models/            # Type definitions & response DTO interfaces
│   │   ├── pages/                 # Halaman utama aplikasi berbasis role
│   │   │   ├── auth/              # Halaman Login Karyawan & Nasabah
│   │   │   ├── landing-page/      # Landing Page Publik SAKU (SEO & Simulasi Pinjaman)
│   │   │   ├── marketing/         # Dashboard & Review Pengajuan Pinjaman Cabang
│   │   │   ├── branchmanager/     # Dashboard & Persetujuan (Approval) Pinjaman BM
│   │   │   ├── backoffice/        # Verifikasi Dokumen KYC & Proses Pencairan Dana
│   │   │   └── superadmin/        # Master Role, Permission, Menu, Karyawan, Cabang, Plafond, Audit Log
│   │   └── shared/                # Reusable UI Components (Navbar, Sidebar, Modal, Table, Badge, Footer)
│   ├── environments/
│   │   ├── environment.ts         # Konfigurasi development (API: http://localhost:8080)
│   │   └── environment.prod.ts    # Konfigurasi production (API: https://saku.morpkhai.web.id)
│   ├── index.html                 # HTML Entry Point dengan Meta Tag SEO & Social Graph
│   └── styles.css                 # Global styles & import Tailwind directives
├── package.json
├── tailwind.config.js
├── tsconfig.json
└── angular.json
```

---

## 👥 Modul & Fitur Berdasarkan Role

### 1. 🌐 Landing Page Publik (`/`)
- Informasi nilai utama SAKU (*Responsible Lending* & Anti-Overindebtedness).
- Kalkulator simulasi pinjaman interaktif (pilihan plafon, tenor, dan estimasi bunga transparan).
- Navigasi cepat dan Call-to-Action download mobile app.

### 2. 📋 Modul Marketing (`/marketing`)
- **Dashboard Marketing**: Ringkasan total pengajuan masuk, perlu review, dan disetujui.
- **Review Pengajuan**: Verifikasi kelayakan debitur, riwayat scoring, pengecekan slip gaji & rekening koran.
- **Aksi Review**: Teruskan ke Branch Manager (Setujui), Minta Revisi Dokumen, atau Tolak Pengajuan.

### 3. ⚖️ Modul Branch Manager (`/branchmanager`)
- **Dashboard Branch Manager**: Matriks persetujuan pinjaman cabang & statistik portofolio.
- **Persetujuan Pinjaman**: Evaluasi pinjaman bernilai besar yang memerlukan otorisasi pimpinan cabang.
- **Aksi Keputusan**: Approve / Reject disertai catatan telaah manajerial.

### 4. 💳 Modul Back Office (`/backoffice`)
- **Verifikasi KYC**: Pengecekan keabsahan foto e-KTP dan foto selfie nasabah baru.
- **Pencairan Dana (Disbursement)**: Validasi rekening penerima dan eksekusi transfer pencairan ke rekening BCA nasabah.

### 5. ⚙️ Modul Super Admin (`/superadmin`)
- **Master RBAC**: Manajemen Role, Hak Akses Permission, dan Menu Navigasi dinamis.
- **Master Data**: Pengelolaan Karyawan Internal, Kantor Cabang, dan Produk Plafond (Tier Bronze s.d Platinum).
- **Monitoring & Audit**: Monitoring status seluruh pengajuan nasional dan pelacakan audit log aktivitas staf.

---

## ⚙️ Konfigurasi Environment

File konfigurasi API backend terletak pada folder `src/environments/`:

- **Development (`environment.ts`)**:
  ```typescript
  export const environment = {
    production: false,
    apiUrl: 'http://localhost:8080/api',
    baseUrl: 'http://localhost:8080'
  };
  ```

- **Production (`environment.prod.ts`)**:
  ```typescript
  export const environment = {
    production: true,
    apiUrl: 'https://saku.morpkhai.web.id/api',
    baseUrl: 'https://saku.morpkhai.web.id'
  };
  ```

---

## 🚀 Panduan Menjalankan Frontend

### 1. Instalasi Dependensi
```bash
cd frontend
npm install
```

### 2. Menjalankan Server Development
```bash
npm start
# atau:
ng serve --port 4200
```
Buka browser di: **`http://localhost:4200/`**.

### 3. Build untuk Production
```bash
npm run build
# Output bundle akan dibuat di folder: dist/
```

### 4. Menjalankan Unit Testing
```bash
npm test
```

---

## 🔑 Kredensial Akun Pengujian Demo

| Role | Username | Password | Modul Utama |
| :--- | :--- | :--- | :--- |
| **Super Admin** | `superadmin` | `Admin123#` | `/superadmin/dashboard` |
| **Back Office** | `backoffice` | `Admin123#` | `/backoffice/dashboard` |
| **Marketing (Pusat)** | `marketing` | `Admin123#` | `/marketing/dashboard` |
| **Branch Manager (Pusat)** | `bm` | `Admin123#` | `/branchmanager/dashboard` |
