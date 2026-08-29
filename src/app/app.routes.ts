import { Routes } from '@angular/router';
import { LandingPageComponent } from './pages/landing-page/landing-page.component';
import { SandboxComponent } from './pages/sandbox/sandbox.component';
import { LoginComponent } from './pages/auth/login/login.component';
import { ForgotPasswordComponent } from './pages/auth/forgot-password/forgot-password.component';

export const routes: Routes = [
  { path: '', component: LandingPageComponent, pathMatch: 'full' },
  { path: 'landing-page', component: LandingPageComponent },
  { path: 'landing', redirectTo: '', pathMatch: 'full' },
  { path: 'login', component: LoginComponent },
  { path: 'auth/login', redirectTo: 'login', pathMatch: 'full' },
  { path: 'forgot-password', component: ForgotPasswordComponent },
  { path: 'auth/forgot-password', redirectTo: 'forgot-password', pathMatch: 'full' },
  { path: 'sandbox', component: SandboxComponent },
  { path: '**', redirectTo: '' },
];

