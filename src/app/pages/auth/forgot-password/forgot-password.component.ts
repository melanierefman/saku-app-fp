import {
  Component,
  OnInit,
  OnDestroy,
  Inject,
  PLATFORM_ID,
  ChangeDetectorRef,
} from '@angular/core';
import { CommonModule, isPlatformBrowser } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import {
  InputComponent,
  ButtonComponent,
  ToastService,
} from '../../../shared/components';
import { AuthService } from '../../../core/services/auth.service';
import {
  LucideMail,
  LucideChevronLeft,
  LucideEye,
  LucideEyeOff,
} from '@lucide/angular';

@Component({
  selector: 'app-forgot-password',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    RouterLink,
    InputComponent,
    ButtonComponent,
    LucideMail,
    LucideChevronLeft,
    LucideEye,
    LucideEyeOff,
  ],
  templateUrl: './forgot-password.component.html',
  styleUrl: './forgot-password.component.css',
})
export class ForgotPasswordComponent implements OnInit, OnDestroy {
  // Step 1 = Input Email, Step 2 = OTP Verification, Step 3 = Input New Password
  step: 1 | 2 | 3 = 1;

  // Step 1 Data
  email = '';
  emailError = '';

  // Step 2 Data
  otpDigits: string[] = ['', '', '', '', '', ''];
  otpError = '';

  // Step 3 Data
  newPassword = '';
  newPasswordError = '';
  confirmPassword = '';
  confirmPasswordError = '';
  showNewPassword = false;
  showConfirmPassword = false;

  // States
  isLoading = false;
  countdown = 60;
  canResend = false;
  private resendTimer: any;
  private isBrowser: boolean;
  currentYear = new Date().getFullYear();

  constructor(
    private router: Router,
    private cdr: ChangeDetectorRef,
    private toastService: ToastService,
    private authService: AuthService,
    @Inject(PLATFORM_ID) platformId: Object
  ) {
    this.isBrowser = isPlatformBrowser(platformId);
  }

  ngOnInit(): void {}

  ngOnDestroy(): void {
    this.stopTimer();
  }

  // --- Step 1: Request OTP ---
  onRequestOtp(): void {
    this.emailError = '';

    if (!this.email.trim()) {
      this.emailError = 'Email wajib diisi';
      return;
    }

    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (!emailRegex.test(this.email.trim())) {
      this.emailError = 'Format email tidak valid';
      return;
    }

    this.isLoading = true;

    this.authService.requestOtp(this.email.trim()).subscribe({
      next: () => {
        this.isLoading = false;
        this.step = 2;
        this.startCountdown();
        this.toastService.success('Kode OTP telah dikirim ke email Anda.');
        this.cdr.detectChanges();
      },
      error: (err) => {
        this.isLoading = false;
        const msg = err?.error?.message || 'Gagal mengirim kode OTP. Silakan coba lagi.';
        this.emailError = msg;
        this.toastService.error(msg);
        this.cdr.detectChanges();
      },
    });
  }

  // --- Step 2: Resend OTP ---
  onResendOtp(): void {
    if (!this.canResend || this.isLoading) return;

    this.isLoading = true;
    this.otpError = '';
    this.otpDigits = ['', '', '', '', '', ''];

    this.authService.requestOtp(this.email.trim()).subscribe({
      next: () => {
        this.isLoading = false;
        this.startCountdown();
        this.toastService.success('Kode OTP baru telah dikirim ke email Anda.');
        this.cdr.detectChanges();
      },
      error: (err) => {
        this.isLoading = false;
        const msg = err?.error?.message || 'Gagal mengirim ulang kode OTP.';
        this.otpError = msg;
        this.toastService.error(msg);
        this.cdr.detectChanges();
      },
    });
  }

  private startCountdown(): void {
    this.stopTimer();
    this.countdown = 60;
    this.canResend = false;

    if (!this.isBrowser) return;

    this.resendTimer = setInterval(() => {
      this.countdown--;
      if (this.countdown <= 0) {
        this.canResend = true;
        this.stopTimer();
      }
      this.cdr.detectChanges();
    }, 1000);
  }

  private stopTimer(): void {
    if (this.resendTimer) {
      clearInterval(this.resendTimer);
      this.resendTimer = null;
    }
  }

  // --- Step 2: OTP Input Management ---
  onOtpInput(index: number, event: Event): void {
    const input = event.target as HTMLInputElement;
    const val = input.value.replace(/[^0-9]/g, '');

    this.otpDigits[index] = val ? val[val.length - 1] : '';
    this.otpError = '';

    if (val && index < 5) {
      const nextInput = document.getElementById(`otp-${index + 1}`);
      nextInput?.focus();
    }
  }

  onOtpKeyDown(index: number, event: KeyboardEvent): void {
    if (event.key === 'Backspace' && !this.otpDigits[index] && index > 0) {
      const prevInput = document.getElementById(`otp-${index - 1}`);
      prevInput?.focus();
    }
  }

  onOtpPaste(event: ClipboardEvent): void {
    event.preventDefault();
    const pasteData = event.clipboardData?.getData('text') || '';
    const digits = pasteData.replace(/[^0-9]/g, '').slice(0, 6);

    for (let i = 0; i < 6; i++) {
      this.otpDigits[i] = digits[i] || '';
    }

    this.otpError = '';
    const nextIdx = Math.min(digits.length, 5);
    const targetInput = document.getElementById(`otp-${nextIdx}`);
    targetInput?.focus();
  }

  get isOtpComplete(): boolean {
    return this.otpDigits.every((d) => d.trim().length === 1);
  }

  get otpValue(): string {
    return this.otpDigits.join('');
  }

  // --- Step 2: Verify OTP ---
  onVerifyOtp(): void {
    this.otpError = '';

    if (!this.isOtpComplete) {
      this.otpError = 'Masukkan 6 digit kode OTP yang lengkap';
      return;
    }

    this.isLoading = true;

    this.authService.verifyOtp(this.email.trim(), this.otpValue).subscribe({
      next: () => {
        this.isLoading = false;
        this.step = 3;
        this.cdr.detectChanges();
      },
      error: (err) => {
        this.isLoading = false;
        const msg = err?.error?.message || 'Kode OTP tidak valid atau telah kedaluwarsa.';
        this.otpError = msg;
        this.toastService.error(msg);
        this.cdr.detectChanges();
      },
    });
  }

  // --- Step 3: Submit Reset Password ---
  onResetPassword(): void {
    this.newPasswordError = '';
    this.confirmPasswordError = '';

    // Validate New Password
    if (!this.newPassword) {
      this.newPasswordError = 'Password baru wajib diisi';
    } else if (this.newPassword.length < 8) {
      this.newPasswordError = 'Password baru minimal 8 karakter';
    }

    // Validate Confirm Password
    if (!this.confirmPassword) {
      this.confirmPasswordError = 'Konfirmasi password baru wajib diisi';
    } else if (this.newPassword !== this.confirmPassword) {
      this.confirmPasswordError = 'Konfirmasi password tidak cocok';
    }

    if (this.newPasswordError || this.confirmPasswordError) {
      return;
    }

    this.isLoading = true;

    this.authService
      .resetPassword({
        email: this.email.trim(),
        otp: this.otpValue,
        newPassword: this.newPassword.trim(),
      })
      .subscribe({
        next: () => {
          this.isLoading = false;
          this.toastService.success('Password berhasil diubah! Silakan masuk kembali.');
          this.router.navigate(['/login']);
        },
        error: (err) => {
          this.isLoading = false;
          const msg = err?.error?.message || 'Gagal mengubah password. Silakan coba lagi.';
          this.newPasswordError = msg;
          this.toastService.error(msg);
          this.cdr.detectChanges();
        },
      });
  }

  // Helper to go back to step 1
  changeEmail(): void {
    this.step = 1;
    this.stopTimer();
    this.otpDigits = ['', '', '', '', '', ''];
    this.otpError = '';
    this.newPassword = '';
    this.confirmPassword = '';
    this.newPasswordError = '';
    this.confirmPasswordError = '';
  }
}
