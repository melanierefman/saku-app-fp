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
          import('./pages/dashboard/dashboard.component').then((m) => m.DashboardComponent),
      },
      { path: 'beranda', redirectTo: 'dashboard', pathMatch: 'full' },

      // Marketing Routes
      {
        path: 'pengajuan-pinjaman',
        loadComponent: () =>
          import('./pages/feature-placeholder/feature-placeholder.component').then(
            (m) => m.FeaturePlaceholderComponent
          ),
        data: { title: 'Pengajuan Pinjaman' },
      },

      // Branch Manager Routes
      {
        path: 'persetujuan-pinjaman',
        loadComponent: () =>
          import('./pages/feature-placeholder/feature-placeholder.component').then(
            (m) => m.FeaturePlaceholderComponent
          ),
        data: { title: 'Persetujuan Pinjaman' },
      },

      // Backoffice Routes
      {
        path: 'verifikasi-customer',
        loadComponent: () =>
          import('./pages/feature-placeholder/feature-placeholder.component').then(
            (m) => m.FeaturePlaceholderComponent
          ),
        data: { title: 'Verifikasi Customer' },
      },
      {
        path: 'pencairan',
        loadComponent: () =>
          import('./pages/feature-placeholder/feature-placeholder.component').then(
            (m) => m.FeaturePlaceholderComponent
          ),
        data: { title: 'Pencairan' },
      },

      // Superadmin / RBAC Routes
      {
        path: 'rbac/role-access',
        loadComponent: () =>
          import('./pages/role-access/role-access.component').then((m) => m.RoleAccessComponent),
      },
      {
        path: 'master/role-access',
        redirectTo: 'rbac/role-access',
        pathMatch: 'full',
      },
      {
        path: 'rbac/role',
        loadComponent: () =>
          import('./pages/role/role.component').then((m) => m.RoleComponent),
      },
      {
        path: 'master/role',
        redirectTo: 'rbac/role',
        pathMatch: 'full',
      },
      {
        path: 'rbac/permission',
        loadComponent: () =>
          import('./pages/permission/permission.component').then((m) => m.PermissionComponent),
      },
      {
        path: 'master/permission',
        redirectTo: 'rbac/permission',
        pathMatch: 'full',
      },
      {
        path: 'rbac/menu',
        loadComponent: () =>
          import('./pages/menu/menu.component').then((m) => m.MenuComponent),
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
          import('./pages/karyawan/karyawan-list/karyawan-list.component').then(
            (m) => m.KaryawanListComponent
          ),
      },
      {
        path: 'master/karyawan/tambah',
        loadComponent: () =>
          import('./pages/karyawan/karyawan-form/karyawan-form.component').then(
            (m) => m.KaryawanFormComponent
          ),
      },
      {
        path: 'master/karyawan/edit/:id',
        loadComponent: () =>
          import('./pages/karyawan/karyawan-form/karyawan-form.component').then(
            (m) => m.KaryawanFormComponent
          ),
      },
      {
        path: 'master/cabang',
        loadComponent: () =>
          import('./pages/cabang/cabang.component').then((m) => m.CabangComponent),
      },
      {
        path: 'cabang',
        redirectTo: 'master/cabang',
        pathMatch: 'full',
      },
      {
        path: 'master/plafond',
        loadComponent: () =>
          import('./pages/plafond/plafond-list/plafond-list.component').then(
            (m) => m.PlafondListComponent
          ),
      },
      {
        path: 'master/plafond/tambah',
        loadComponent: () =>
          import('./pages/plafond/plafond-form/plafond-form.component').then(
            (m) => m.PlafondFormComponent
          ),
      },
      {
        path: 'master/plafond/edit/:id',
        loadComponent: () =>
          import('./pages/plafond/plafond-form/plafond-form.component').then(
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
          import('./pages/monitoring/monitoring-pengajuan/monitoring-pengajuan.component').then(
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
        path: 'monitoring/audit-log',
        loadComponent: () =>
          import('./pages/monitoring/audit-log/audit-log.component').then(
            (m) => m.AuditLogComponent
          ),
        data: { title: 'Audit Log' },
      },
      {
        path: 'master/audit-log',
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
