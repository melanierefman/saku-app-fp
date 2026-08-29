import { Routes } from '@angular/router';
import { LandingPageComponent } from './pages/landing-page/landing-page.component';
import { SandboxComponent } from './pages/sandbox/sandbox.component';

export const routes: Routes = [
  { path: '', component: LandingPageComponent, pathMatch: 'full' },
  { path: 'landing-page', component: LandingPageComponent },
  { path: 'landing', redirectTo: '', pathMatch: 'full' },
  { path: 'sandbox', component: SandboxComponent },
  { path: '**', redirectTo: '' },
];

