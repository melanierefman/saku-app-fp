import { Routes } from '@angular/router';
import { MainLayoutComponent } from './shared/layouts/main-layout/main-layout.component';
import { authGuard, guestGuard } from './core/guards/auth.guard';

export const routes: Routes = [
  // Public Landing Page
  {
    path: '',
    loadComponent: () =>
      import('./pages/landing-page/landing-page.component').then((m) => m.LandingPageComponent),
    pathMatch: 'full',
  },
  {
    path: 'landing-page',
    loadComponent: () =>
      import('./pages/landing-page/landing-page.component').then((m) => m.LandingPageComponent),
  },
  { path: 'landing', redirectTo: '', pathMatch: 'full' },

  // Public Auth
  {
    path: 'login',
    loadComponent: () =>
      import('./pages/auth/login/login.component').then((m) => m.LoginComponent),
    canActivate: [guestGuard],
  },
  { path: 'auth/login', redirectTo: 'login', pathMatch: 'full' },
  {
    path: 'forgot-password',
    loadComponent: () =>
      import('./pages/auth/forgot-password/forgot-password.component').then(
        (m) => m.ForgotPasswordComponent
      ),
  },
  { path: 'auth/forgot-password', redirectTo: 'forgot-password', pathMatch: 'full' },

  // Public Component Sandbox (No Login Required)
  {
    path: 'sandbox',
    loadComponent: () =>
      import('./pages/sandbox/sandbox.component').then((m) => m.SandboxComponent),
  },

  // Protected Portal Routes wrapped in MainLayoutComponent
  {
    path: '',
    component: MainLayoutComponent,
    canActivate: [authGuard],
    children: [
      {
        path: 'dashboard',
        loadComponent: () =>
          import('./pages/superadmin/dashboard/dashboard.component').then((m) => m.DashboardComponent),
      },
      { path: 'beranda', redirectTo: 'dashboard', pathMatch: 'full' },

      // Marketing Routes
      {
        path: 'pengajuan-pinjaman',
        loadComponent: () =>
          import(
            './pages/marketing/pengajuan-pinjaman/pengajuan-pinjaman-list/pengajuan-pinjaman-list.component'
          ).then((m) => m.PengajuanPinjamanListComponent),
        data: { title: 'Pengajuan Pinjaman' },
      },
      {
        path: 'pengajuan-pinjaman/detail/:id',
        loadComponent: () =>
          import(
            './pages/marketing/pengajuan-pinjaman/pengajuan-pinjaman-detail/pengajuan-pinjaman-detail.component'
          ).then((m) => m.PengajuanPinjamanDetailComponent),
        data: { title: 'Detail Pengajuan Pinjaman' },
      },
      {
        path: 'pengajuan-pinjaman/review/:id',
        redirectTo: 'pengajuan-pinjaman/detail/:id',
        pathMatch: 'full',
      },

      // Branch Manager Routes
      {
        path: 'persetujuan-pinjaman',
        loadComponent: () =>
          import(
            './pages/branchmanager/persetujuan-pinjaman/persetujuan-pinjaman-list/persetujuan-pinjaman-list.component'
          ).then((m) => m.PersetujuanPinjamanListComponent),
        data: { title: 'Persetujuan Pinjaman' },
      },
      {
        path: 'persetujuan-pinjaman/detail/:id',
        loadComponent: () =>
          import(
            './pages/branchmanager/persetujuan-pinjaman/persetujuan-pinjaman-detail/persetujuan-pinjaman-detail.component'
          ).then((m) => m.PersetujuanPinjamanDetailComponent),
        data: { title: 'Detail Persetujuan Pinjaman' },
      },
      {
        path: 'persetujuan-pinjaman/persetujuan/:id',
        redirectTo: 'persetujuan-pinjaman/detail/:id',
        pathMatch: 'full',
      },

      // Backoffice Routes
      {
        path: 'verifikasi-customer',
        loadComponent: () =>
          import(
            './pages/backoffice/verifikasi-customer/verifikasi-customer-list/verifikasi-customer-list.component'
          ).then((m) => m.VerifikasiCustomerListComponent),
        data: { title: 'Verifikasi Customer' },
      },
      {
        path: 'verifikasi-customer/detail/:id',
        loadComponent: () =>
          import(
            './pages/backoffice/verifikasi-customer/verifikasi-customer-detail/verifikasi-customer-detail.component'
          ).then((m) => m.VerifikasiCustomerDetailComponent),
        data: { title: 'Detail Verifikasi Customer' },
      },
      {
        path: 'verifikasi-customer/:id',
        redirectTo: 'verifikasi-customer/detail/:id',
        pathMatch: 'full',
      },
      {
        path: 'backoffice/verifikasi-customer',
        redirectTo: 'verifikasi-customer',
        pathMatch: 'full',
      },
      {
        path: 'backoffice/verifikasi-customer/detail/:id',
        redirectTo: 'verifikasi-customer/detail/:id',
        pathMatch: 'full',
      },
      {
        path: 'backoffice/verifikasi-customer/:id',
        redirectTo: 'verifikasi-customer/detail/:id',
        pathMatch: 'full',
      },
      // Backoffice Routes - Pencairan
      {
        path: 'pencairan',
        loadComponent: () =>
          import(
            './pages/backoffice/pencairan/pencairan-list/pencairan-list.component'
          ).then((m) => m.PencairanListComponent),
        data: { title: 'Pencairan Pinjaman' },
      },
      {
        path: 'pencairan/detail/:id',
        loadComponent: () =>
          import(
            './pages/backoffice/pencairan/pencairan-detail/pencairan-detail.component'
          ).then((m) => m.PencairanDetailComponent),
        data: { title: 'Detail Pencairan Pinjaman' },
      },
      {
        path: 'pencairan/:id',
        redirectTo: 'pencairan/detail/:id',
        pathMatch: 'full',
      },
      {
        path: 'backoffice/pencairan',
        redirectTo: 'pencairan',
        pathMatch: 'full',
      },
      {
        path: 'backoffice/pencairan/detail/:id',
        redirectTo: 'pencairan/detail/:id',
        pathMatch: 'full',
      },
      {
        path: 'backoffice/pencairan/:id',
        redirectTo: 'pencairan/detail/:id',
        pathMatch: 'full',
      },

      // Superadmin / RBAC Routes
      {
        path: 'rbac/role-access',
        loadComponent: () =>
          import('./pages/superadmin/role-access/role-access.component').then((m) => m.RoleAccessComponent),
      },
      {
        path: 'master/role-access',
        redirectTo: 'rbac/role-access',
        pathMatch: 'full',
      },
      {
        path: 'rbac/role',
        loadComponent: () =>
          import('./pages/superadmin/role/role.component').then((m) => m.RoleComponent),
      },
      {
        path: 'master/role',
        redirectTo: 'rbac/role',
        pathMatch: 'full',
      },
      {
        path: 'rbac/permission',
        loadComponent: () =>
          import('./pages/superadmin/permission/permission.component').then((m) => m.PermissionComponent),
      },
      {
        path: 'master/permission',
        redirectTo: 'rbac/permission',
        pathMatch: 'full',
      },
      {
        path: 'rbac/menu',
        loadComponent: () =>
          import('./pages/superadmin/menu/menu.component').then((m) => m.MenuComponent),
      },
      {
        path: 'master/menu',
        redirectTo: 'rbac/menu',
        pathMatch: 'full',
      },

      // Superadmin / Master Data Routes
      {
        path: 'master/karyawan',
        loadComponent: () =>
          import('./pages/superadmin/karyawan/karyawan-list/karyawan-list.component').then(
            (m) => m.KaryawanListComponent
          ),
      },
      {
        path: 'master/karyawan/tambah',
        loadComponent: () =>
          import('./pages/superadmin/karyawan/karyawan-form/karyawan-form.component').then(
            (m) => m.KaryawanFormComponent
          ),
      },
      {
        path: 'master/karyawan/edit/:id',
        loadComponent: () =>
          import('./pages/superadmin/karyawan/karyawan-form/karyawan-form.component').then(
            (m) => m.KaryawanFormComponent
          ),
      },
      {
        path: 'master/cabang',
        loadComponent: () =>
          import('./pages/superadmin/cabang/cabang.component').then((m) => m.CabangComponent),
      },
      {
        path: 'cabang',
        redirectTo: 'master/cabang',
        pathMatch: 'full',
      },
      {
        path: 'master/plafond',
        loadComponent: () =>
          import('./pages/superadmin/plafond/plafond-list/plafond-list.component').then(
            (m) => m.PlafondListComponent
          ),
      },
      {
        path: 'master/plafond/tambah',
        loadComponent: () =>
          import('./pages/superadmin/plafond/plafond-form/plafond-form.component').then(
            (m) => m.PlafondFormComponent
          ),
      },
      {
        path: 'master/plafond/edit/:id',
        loadComponent: () =>
          import('./pages/superadmin/plafond/plafond-form/plafond-form.component').then(
            (m) => m.PlafondFormComponent
          ),
      },
      {
        path: 'plafond',
        redirectTo: 'master/plafond',
        pathMatch: 'full',
      },

      // Superadmin / Monitoring Routes
      {
        path: 'monitoring/pengajuan',
        loadComponent: () =>
          import('./pages/superadmin/monitoring/monitoring-pengajuan/monitoring-pengajuan.component').then(
            (m) => m.MonitoringPengajuanComponent
          ),
        data: { title: 'Monitoring Pengajuan' },
      },
      {
        path: 'master/monitoring-pengajuan',
        redirectTo: 'monitoring/pengajuan',
        pathMatch: 'full',
      },
      {
        path: 'monitoring-pengajuan',
        redirectTo: 'monitoring/pengajuan',
        pathMatch: 'full',
      },
      {
        path: 'monitoring/audit-log',
        loadComponent: () =>
          import('./pages/superadmin/monitoring/audit-log/audit-log.component').then(
            (m) => m.AuditLogComponent
          ),
        data: { title: 'Audit Log' },
      },
      {
        path: 'master/audit-log',
        redirectTo: 'monitoring/audit-log',
        pathMatch: 'full',
      },
      {
        path: 'audit-log',
        redirectTo: 'monitoring/audit-log',
        pathMatch: 'full',
      },

      // Profile
      {
        path: 'profile',
        loadComponent: () =>
          import('./pages/profile/profile.component').then((m) => m.ProfileComponent),
      },
    ],
  },

  { path: '**', redirectTo: '' },
];
