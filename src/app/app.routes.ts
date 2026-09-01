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
import { RoleAccessComponent } from './pages/role-access/role-access.component';
import { RoleComponent } from './pages/role/role.component';
import { MenuComponent } from './pages/menu/menu.component';
import { PermissionComponent } from './pages/permission/permission.component';
import { CabangComponent } from './pages/cabang/cabang.component';
import { PlafondListComponent } from './pages/plafond/plafond-list/plafond-list.component';
import { PlafondFormComponent } from './pages/plafond/plafond-form/plafond-form.component';
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
        component: RoleAccessComponent,
      },
      {
        path: 'master/role-access',
        redirectTo: 'rbac/role-access',
        pathMatch: 'full',
      },
      {
        path: 'rbac/role',
        component: RoleComponent,
      },
      {
        path: 'master/role',
        redirectTo: 'rbac/role',
        pathMatch: 'full',
      },
      {
        path: 'rbac/permission',
        component: PermissionComponent,
      },
      {
        path: 'master/permission',
        redirectTo: 'rbac/permission',
        pathMatch: 'full',
      },
      {
        path: 'rbac/menu',
        component: MenuComponent,
      },
      {
        path: 'master/menu',
        redirectTo: 'rbac/menu',
        pathMatch: 'full',
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
        component: CabangComponent,
      },
      {
        path: 'cabang',
        redirectTo: 'master/cabang',
        pathMatch: 'full',
      },
      {
        path: 'master/plafond',
        component: PlafondListComponent,
      },
      {
        path: 'master/plafond/tambah',
        component: PlafondFormComponent,
      },
      {
        path: 'master/plafond/edit/:id',
        component: PlafondFormComponent,
      },
      {
        path: 'plafond',
        redirectTo: 'master/plafond',
        pathMatch: 'full',
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

