import { Component, inject, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import {
  LucideBell,
  LucideCircleHelp,
  LucideChevronDown,
  LucideUserRound,
  LucideLogOut,
} from '@lucide/angular';
import { AuthStore } from '../../../core/store/auth.store';
import { AuthService } from '../../../core/services/auth.service';
import { formatRoleName } from '../../../core';
import { ModalComponent } from '../modal/modal.component';

@Component({
  selector: 'app-header',
  standalone: true,
  imports: [
    CommonModule,
    RouterLink,
    ModalComponent,
    LucideBell,
    LucideCircleHelp,
    LucideChevronDown,
    LucideUserRound,
    LucideLogOut,
  ],
  templateUrl: './header.component.html',
  styleUrl: './header.component.css',
})
export class HeaderComponent {
  private authStore = inject(AuthStore);
  private authService = inject(AuthService);
  private cdr = inject(ChangeDetectorRef);

  readonly currentUser = this.authStore.currentUser;
  readonly userRole = this.authStore.userRole;

  showUserDropdown = false;
  showLogoutModal = false;

  get userName(): string {
    return this.currentUser()?.nama || this.currentUser()?.username || 'Karyawan SAKU';
  }

  get userRoleDisplay(): string {
    return formatRoleName(this.userRole() || 'Karyawan');
  }

  get userInitials(): string {
    const name = this.userName;
    const parts = name.trim().split(' ');
    if (parts.length >= 2) {
      return (parts[0][0] + parts[1][0]).toUpperCase();
    }
    return name.substring(0, 2).toUpperCase();
  }

  toggleUserDropdown(): void {
    this.showUserDropdown = !this.showUserDropdown;
    this.cdr.detectChanges();
  }

  closeUserDropdown(): void {
    this.showUserDropdown = false;
    this.cdr.detectChanges();
  }

  openLogoutModal(): void {
    this.showUserDropdown = false;
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
