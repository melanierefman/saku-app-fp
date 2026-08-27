import { Routes } from '@angular/router';
import { SandboxComponent } from './pages/sandbox/sandbox.component';

export const routes: Routes = [
  { path: '', redirectTo: 'sandbox', pathMatch: 'full' },
  { path: 'sandbox', component: SandboxComponent },
  { path: '**', redirectTo: 'sandbox' },
];

