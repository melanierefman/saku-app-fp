import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AuthStore } from '../../core/store/auth.store';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.css',
})
export class DashboardComponent {
  private authStore = inject(AuthStore);

  readonly currentUser = this.authStore.currentUser;
  readonly userRole = this.authStore.userRole;

  get userName(): string {
    return this.currentUser()?.nama || this.currentUser()?.username || 'Karyawan SAKU';
  }

  get userRoleDisplay(): string {
    const role = this.userRole() || 'KARYAWAN';
    return role.replace(/_/g, ' ').toUpperCase();
  }
}
