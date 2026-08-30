import { Injectable, signal, computed, inject } from '@angular/core';
import { UserProfile, LoginResponseData } from '../models/auth.models';
import { TokenService } from '../services/token.service';

@Injectable({
  providedIn: 'root',
})
export class AuthStore {
  private tokenService = inject(TokenService);

  // State Signals
  private readonly _currentUser = signal<UserProfile | null>(
    this.tokenService.getUser()
  );
  private readonly _accessToken = signal<string | null>(
    this.tokenService.getAccessToken()
  );

  // Readonly Public Selectors
  readonly currentUser = this._currentUser.asReadonly();
  readonly accessToken = this._accessToken.asReadonly();
  readonly isAuthenticated = computed(() => !!this._accessToken() && !!this._currentUser());
  readonly userRole = computed(() => this._currentUser()?.role || null);
  readonly userCabang = computed(() => this._currentUser()?.cabang || null);
  readonly permissions = computed(() => this._currentUser()?.permissions || []);

  // Actions
  setAuth(data: LoginResponseData): void {
    this.tokenService.setSession(data.accessToken, data.refreshToken, data.user);
    this._accessToken.set(data.accessToken);
    this._currentUser.set(data.user);
  }

  updateUser(user: UserProfile): void {
    this.tokenService.setUser(user);
    this._currentUser.set(user);
  }

  clearAuth(): void {
    this.tokenService.clearSession();
    this._accessToken.set(null);
    this._currentUser.set(null);
  }

  hasPermission(permission: string): boolean {
    return this.permissions().includes(permission);
  }

  hasRole(role: string): boolean {
    return this.userRole()?.toUpperCase() === role.toUpperCase();
  }
}
