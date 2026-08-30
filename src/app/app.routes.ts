import { Routes } from '@angular/router';
import { LandingPageComponent } from './pages/landing-page/landing-page.component';
import { SandboxComponent } from './pages/sandbox/sandbox.component';
import { LoginComponent } from './pages/auth/login/login.component';
import { ForgotPasswordComponent } from './pages/auth/forgot-password/forgot-password.component';
import { DashboardComponent } from './pages/dashboard/dashboard.component';
import { FeaturePlaceholderComponent } from './pages/feature-placeholder/feature-placeholder.component';
import { ProfileComponent } from './pages/profile/profile.component';
import { KaryawanListComponent } from './pages/karyawan/karyawan-list/karyawan-list.component';
import { KaryawanFormComponent } from './pages/karyawan/karyawan-form/karyawan-form.component';
import { MainLayoutComponent } from './shared/layouts/main-layout/main-layout.component';
import { authGuard, guestGuard } from './core/guards/auth.guard';

export const routes: Routes = [
  // Public Landing Page
  { path: '', component: LandingPageComponent, pathMatch: 'full' },
  { path: 'landing-page', component: LandingPageComponent },
  { path: 'landing', redirectTo: '', pathMatch: 'full' },

  // Public Auth
  { path: 'login', component: LoginComponent, canActivate: [guestGuard] },
  { path: 'auth/login', redirectTo: 'login', pathMatch: 'full' },
  { path: 'forgot-password', component: ForgotPasswordComponent },
  { path: 'auth/forgot-password', redirectTo: 'forgot-password', pathMatch: 'full' },

  // Public Component Sandbox (No Login Required)
  { path: 'sandbox', component: SandboxComponent },

  // Protected Portal Routes wrapped in MainLayoutComponent
  {
    path: '',
    component: MainLayoutComponent,
    canActivate: [authGuard],
    children: [
      { path: 'dashboard', component: DashboardComponent },
      { path: 'beranda', redirectTo: 'dashboard', pathMatch: 'full' },

      // Marketing Routes
      {
        path: 'pengajuan-pinjaman',
        component: FeaturePlaceholderComponent,
        data: { title: 'Pengajuan Pinjaman' },
      },

      // Branch Manager Routes
      {
        path: 'persetujuan-pinjaman',
        component: FeaturePlaceholderComponent,
        data: { title: 'Persetujuan Pinjaman' },
      },

      // Backoffice Routes
      {
        path: 'verifikasi-customer',
        component: FeaturePlaceholderComponent,
        data: { title: 'Verifikasi Customer' },
      },
      {
        path: 'pencairan',
        component: FeaturePlaceholderComponent,
        data: { title: 'Pencairan' },
      },

      // Superadmin / RBAC Routes
      {
        path: 'rbac/role-access',
        component: FeaturePlaceholderComponent,
        data: { title: 'Role Access' },
      },
      {
        path: 'rbac/role',
        component: FeaturePlaceholderComponent,
        data: { title: 'Role' },
      },
      {
        path: 'rbac/permission',
        component: FeaturePlaceholderComponent,
        data: { title: 'Permission' },
      },
      {
        path: 'rbac/menu',
        component: FeaturePlaceholderComponent,
        data: { title: 'Menu' },
      },

      // Superadmin / Master Data Routes
      {
        path: 'master/karyawan',
        component: KaryawanListComponent,
      },
      {
        path: 'master/karyawan/tambah',
        component: KaryawanFormComponent,
      },
      {
        path: 'master/karyawan/edit/:id',
        component: KaryawanFormComponent,
      },
      {
        path: 'master/cabang',
        component: FeaturePlaceholderComponent,
        data: { title: 'Master Cabang' },
      },
      {
        path: 'master/plafond',
        component: FeaturePlaceholderComponent,
        data: { title: 'Master Plafond' },
      },

      // Superadmin / Monitoring Routes
      {
        path: 'monitoring/pengajuan',
        component: FeaturePlaceholderComponent,
        data: { title: 'Monitoring Pengajuan' },
      },
      {
        path: 'monitoring/audit-log',
        component: FeaturePlaceholderComponent,
        data: { title: 'Audit Log' },
      },

      // Profile
      { path: 'profile', component: ProfileComponent },
    ],
  },

  { path: '**', redirectTo: '' },
];

