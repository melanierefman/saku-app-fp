import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { Observable, tap } from 'rxjs';
import { environment } from '../../../../environments/environment';
import {
  AuthResponse,
  LoginKaryawanRequest,
} from '../../models/auth/auth.models';
import { AuthStore } from '../../store/auth.store';
import { TokenService } from './token.service';
import { ToastService } from '../../../shared/components/toast/toast.service';

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  private http = inject(HttpClient);
  private router = inject(Router);
  private authStore = inject(AuthStore);
  private tokenService = inject(TokenService);
  private toastService = inject(ToastService);

  private readonly API_URL = `${environment.apiUrl}/auth`;

  // Login Karyawan
  // POST /api/auth/karyawan/login
  loginKaryawan(credentials: LoginKaryawanRequest): Observable<AuthResponse> {
    const payload = {
      identifier: credentials.identifier || credentials.username || credentials.email,
      username: credentials.username || credentials.identifier,
      email: credentials.email || credentials.identifier,
      password: credentials.password,
    };

    return this.http
      .post<AuthResponse>(`${this.API_URL}/karyawan/login`, payload)
      .pipe(
        tap((response) => {
          if (response?.data) {
            this.authStore.setAuth(response.data);
          }
        })
      );
  }

  // Refresh Token Karyawan
  // POST /api/auth/karyawan/refresh-token
  refreshToken(): Observable<AuthResponse> {
    const refreshToken = this.tokenService.getRefreshToken();
    return this.http
      .post<AuthResponse>(`${this.API_URL}/karyawan/refresh-token`, { refreshToken })
      .pipe(
        tap((response) => {
          if (response?.data) {
            if (response.data.accessToken) {
              this.tokenService.setAccessToken(response.data.accessToken);
            }
            if (response.data.refreshToken) {
              this.tokenService.setRefreshToken(response.data.refreshToken);
            }
            if (response.data.user) {
              this.authStore.updateUser(response.data.user);
            }
          }
        })
      );
  }

  // Logout Karyawan
  // POST /api/auth/karyawan/logout
  logout(redirect: boolean = true): void {
    const refreshToken = this.tokenService.getRefreshToken();

    // Call backend logout endpoint (Bearer token automatically attached by interceptor)
    this.http
      .post(`${this.API_URL}/karyawan/logout`, { refreshToken })
      .subscribe({
        next: () => { },
        error: () => { },
      });

    this.authStore.clearAuth();
    this.toastService.info('Anda telah berhasil keluar.');
    if (redirect) {
      this.router.navigate(['/login']);
    }
  }

  // Request OTP Forgot Password
  requestOtp(email: string): Observable<any> {
    return this.http.post(`${this.API_URL}/forgot-password/request-otp`, { email });
  }

  // Verify OTP
  verifyOtp(email: string, otp: string): Observable<any> {
    return this.http.post(`${this.API_URL}/forgot-password/verify-otp`, { email, otp });
  }

  // Reset Password
  resetPassword(payload: { email: string; otp: string; newPassword: string }): Observable<any> {
    return this.http.post(`${this.API_URL}/forgot-password/reset`, payload);
  }
}
