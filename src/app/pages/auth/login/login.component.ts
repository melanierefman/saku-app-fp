import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { InputComponent, ButtonComponent } from '../../../shared/components';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink, InputComponent, ButtonComponent],
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

  constructor(private router: Router) {}

  togglePasswordVisibility(): void {
    this.showPassword = !this.showPassword;
  }

  onForgotPassword(): void {
    // Navigate or trigger forgot password modal / notification
  }

  onLogin(): void {
    this.emailError = '';
    this.passwordError = '';
    this.errorMessage = '';

    if (!this.emailOrUsername.trim()) {
      this.emailError = 'Email atau username wajib diisi';
    }

    if (!this.password.trim()) {
      this.passwordError = 'Password wajib diisi';
    }

    if (this.emailError || this.passwordError) {
      return;
    }

    this.isLoading = true;

    // Simulate login process
    setTimeout(() => {
      this.isLoading = false;
      // Navigate to portal dashboard / sandbox
      this.router.navigate(['/sandbox']);
    }, 1000);
  }
}
