import { Component, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { InputComponent, ButtonComponent } from '../../../shared/components';
import { AuthService } from '../../../core/services/auth/auth.service';
import { ToastService } from '../../../shared/components/toast/toast.service';
import { LucideEye, LucideEyeOff } from '@lucide/angular';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    RouterLink,
    InputComponent,
    ButtonComponent,
    LucideEye,
    LucideEyeOff,
  ],
  templateUrl: './login.component.html',
  styleUrl: './login.component.css',
})
export class LoginComponent {
  emailOrUsername = '';
  password = '';
  showPassword = false;
  isLoading = false;
  errorMessage = '';
  emailError = '';
  passwordError = '';
  currentYear = new Date().getFullYear();

  constructor(
    private router: Router,
    private authService: AuthService,
    private toastService: ToastService,
    private cdr: ChangeDetectorRef
  ) {}

  togglePasswordVisibility(): void {
    this.showPassword = !this.showPassword;
  }

  onForgotPassword(): void {
    this.router.navigate(['/forgot-password']);
  }

  onLogin(): void {
    this.emailError = '';
    this.passwordError = '';
    this.errorMessage = '';

    const identifier = this.emailOrUsername.trim();
    const password = this.password.trim();

    if (!identifier) {
      this.emailError = 'Email atau username wajib diisi';
    }

    if (!password) {
      this.passwordError = 'Password wajib diisi';
    }

    if (this.emailError || this.passwordError) {
      return;
    }

    this.isLoading = true;

    this.authService
      .loginKaryawan({
        identifier: identifier,
        password: password,
      })
      .subscribe({
        next: (response) => {
          this.isLoading = false;
          const user = response?.data?.user;
          const userName = user?.nama || user?.username || 'Karyawan';
          this.toastService.success(`Selamat datang kembali, ${userName}!`);
          this.router.navigate(['/dashboard']);
          this.cdr.detectChanges();
        },
        error: (err) => {
          this.isLoading = false;
          const backendMessage =
            err?.error?.message ||
            err?.error?.error ||
            'Email/username atau password salah. Silakan coba lagi.';
          this.errorMessage = backendMessage;
          this.toastService.error(backendMessage);
          this.cdr.detectChanges();
        },
      });
  }
}
