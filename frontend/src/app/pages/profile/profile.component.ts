import { Component, OnInit, inject, ChangeDetectorRef, signal, computed, PLATFORM_ID } from '@angular/core';
import { CommonModule, isPlatformBrowser } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AuthStore } from '../../core/store/auth.store';
import {
  CardComponent,
  InputComponent,
  ButtonComponent,
  BadgeComponent,
  ModalComponent,
  ToastService,
  SkeletonComponent,
} from '../../shared/components';
import { AuthService } from '../../core/services/auth/auth.service';
import { formatRoleName } from '../../core';
import {
  LucideLock,
  LucideKey,
  LucideEye,
  LucideEyeOff,
  LucideCircleAlert,
  LucideCheckCircle2,
  LucideUser,
} from '@lucide/angular';

@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    CardComponent,
    InputComponent,
    ButtonComponent,
    BadgeComponent,
    ModalComponent,
    SkeletonComponent,
    LucideLock,
    LucideKey,
    LucideEye,
    LucideEyeOff,
    LucideCircleAlert,
    LucideCheckCircle2,
    LucideUser,
  ],
  templateUrl: './profile.component.html',
  styleUrl: './profile.component.css',
})
export class ProfileComponent implements OnInit {
  private authStore = inject(AuthStore);
  private authService = inject(AuthService);
  private toastService = inject(ToastService);
  private cdr = inject(ChangeDetectorRef);
  private platformId = inject(PLATFORM_ID);

  readonly user = this.authStore.currentUser;
  isLoading = signal<boolean>(true);
  showLogoutModal = false;
  showChangePasswordModal = false;

  // Change Password Form Signals
  oldPassword = signal<string>('');
  newPassword = signal<string>('');
  confirmPassword = signal<string>('');

  oldPasswordError = signal<string | null>(null);
  newPasswordError = signal<string | null>(null);
  confirmPasswordError = signal<string | null>(null);

  showOldPassword = signal<boolean>(false);
  showNewPassword = signal<boolean>(false);
  showConfirmPassword = signal<boolean>(false);

  isSubmitting = signal<boolean>(false);
  errorMessage = signal<string | null>(null);
  successMessage = signal<string | null>(null);

  ngOnInit(): void {
    if (isPlatformBrowser(this.platformId)) {
      setTimeout(() => {
        this.isLoading.set(false);
        this.cdr.markForCheck();
      }, 300);
    } else {
      this.isLoading.set(false);
    }
  }

  get roleDisplay(): string {
    const role = formatRoleName(this.user()?.role || 'Karyawan');
    const branch = this.user()?.cabang;
    if (branch && branch.trim()) {
      return `${role} (${branch.trim()})`;
    }
    return role;
  }

  get userInitials(): string {
    const name = this.user()?.nama || this.user()?.username || 'K';
    const parts = name.trim().split(' ');
    if (parts.length >= 2) {
      return (parts[0][0] + parts[1][0]).toUpperCase();
    }
    return name.substring(0, 2).toUpperCase();
  }

  onOldPasswordChange(val: string): void {
    this.oldPassword.set(val || '');
    this.oldPasswordError.set(null);
    this.errorMessage.set(null);
  }

  onNewPasswordChange(val: string): void {
    this.newPassword.set(val || '');
    this.newPasswordError.set(null);
    this.errorMessage.set(null);
  }

  onConfirmPasswordChange(val: string): void {
    this.confirmPassword.set(val || '');
    this.confirmPasswordError.set(null);
    this.errorMessage.set(null);
  }

  onChangePassword(): void {
    this.openChangePasswordModal();
  }

  openChangePasswordModal(): void {
    // Reset field errors
    this.oldPasswordError.set(null);
    this.newPasswordError.set(null);
    this.confirmPasswordError.set(null);
    this.errorMessage.set(null);
    this.successMessage.set(null);

    let hasError = false;

    if (!this.oldPassword().trim()) {
      this.oldPasswordError.set('Password saat ini wajib diisi');
      hasError = true;
    }

    if (!this.newPassword()) {
      this.newPasswordError.set('Password baru wajib diisi');
      hasError = true;
    } else if (this.newPassword().length < 8) {
      this.newPasswordError.set('Password baru minimal 8 karakter');
      hasError = true;
    } else if (this.oldPassword() && this.oldPassword() === this.newPassword()) {
      this.newPasswordError.set('Password baru tidak boleh sama dengan password saat ini');
      hasError = true;
    }

    if (!this.confirmPassword()) {
      this.confirmPasswordError.set('Konfirmasi password wajib diisi');
      hasError = true;
    } else if (this.newPassword() && this.newPassword() !== this.confirmPassword()) {
      this.confirmPasswordError.set('Password baru dan konfirmasi password tidak cocok');
      hasError = true;
    }

    if (hasError) {
      return;
    }

    this.showChangePasswordModal = true;
    this.cdr.detectChanges();
  }

  onConfirmChangePassword(): void {
    this.showChangePasswordModal = false;
    this.isSubmitting.set(true);
    this.cdr.detectChanges();

    this.authService
      .changePassword({
        oldPassword: this.oldPassword(),
        newPassword: this.newPassword(),
        confirmPassword: this.confirmPassword(),
      })
      .subscribe({
        next: (response) => {
          this.isSubmitting.set(false);
          const msg = response?.message || 'Password berhasil diubah';
          this.successMessage.set(msg);
          this.toastService.success(msg);
          this.resetPasswordForm();
          this.cdr.detectChanges();
        },
        error: (error) => {
          this.isSubmitting.set(false);
          const msg =
            error?.error?.message ||
            error?.message ||
            'Gagal mengubah password. Pastikan data yang dimasukkan benar.';
          this.errorMessage.set(msg);
          this.toastService.error(msg);
          this.cdr.detectChanges();
        },
      });
  }

  resetPasswordForm(): void {
    this.oldPassword.set('');
    this.newPassword.set('');
    this.confirmPassword.set('');
    this.oldPasswordError.set(null);
    this.newPasswordError.set(null);
    this.confirmPasswordError.set(null);
    this.showOldPassword.set(false);
    this.showNewPassword.set(false);
    this.showConfirmPassword.set(false);
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
