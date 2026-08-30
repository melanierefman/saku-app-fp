import { Component, inject, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AuthStore } from '../../core/store/auth.store';
import { ButtonComponent, ModalComponent } from '../../shared/components';
import { AuthService } from '../../core/services/auth.service';

@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [CommonModule, ButtonComponent, ModalComponent],
  templateUrl: './profile.component.html',
  styleUrl: './profile.component.css',
})
export class ProfileComponent {
  private authStore = inject(AuthStore);
  private authService = inject(AuthService);
  private cdr = inject(ChangeDetectorRef);

  readonly user = this.authStore.currentUser;
  showLogoutModal = false;

  get roleDisplay(): string {
    return (this.user()?.role || 'KARYAWAN').replace(/_/g, ' ').toUpperCase();
  }

  get userInitials(): string {
    const name = this.user()?.nama || this.user()?.username || 'K';
    const parts = name.trim().split(' ');
    if (parts.length >= 2) {
      return (parts[0][0] + parts[1][0]).toUpperCase();
    }
    return name.substring(0, 2).toUpperCase();
  }

  openLogoutModal(): void {
    this.showLogoutModal = true;
    this.cdr.detectChanges();
  }

  onLogout(): void {
    this.openLogoutModal();
  }

  onConfirmLogout(): void {
    this.showLogoutModal = false;
    this.cdr.detectChanges();
    this.authService.logout(true);
  }
}
