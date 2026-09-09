import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { provideRouter, Router } from '@angular/router';
import { of, throwError } from 'rxjs';
import { LoginComponent } from './login.component';
import { AuthService } from '../../../core/services/auth/auth.service';
import { ToastService } from '../../../shared/components/toast/toast.service';

describe('LoginComponent', () => {
  let component: LoginComponent;
  let fixture: ComponentFixture<LoginComponent>;
  let authServiceSpy: { loginKaryawan: ReturnType<typeof vi.fn> };
  let toastServiceSpy: { success: ReturnType<typeof vi.fn>; error: ReturnType<typeof vi.fn> };
  let router: Router;

  beforeEach(async () => {
    authServiceSpy = {
      loginKaryawan: vi.fn(),
    };
    toastServiceSpy = {
      success: vi.fn(),
      error: vi.fn(),
    };

    await TestBed.configureTestingModule({
      imports: [LoginComponent],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        provideRouter([]),
        { provide: AuthService, useValue: authServiceSpy },
        { provide: ToastService, useValue: toastServiceSpy },
      ],
    }).compileComponents();

    router = TestBed.inject(Router);
    vi.spyOn(router, 'navigate').mockResolvedValue(true);

    fixture = TestBed.createComponent(LoginComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create login component', () => {
    expect(component).toBeTruthy();
  });

  it('should show validation error when fields are empty', () => {
    component.emailOrUsername = '';
    component.password = '';
    component.onLogin();

    expect(component.emailError).toBe('Email atau username wajib diisi');
    expect(component.passwordError).toBe('Password wajib diisi');
    expect(authServiceSpy.loginKaryawan).not.toHaveBeenCalled();
  });

  it('should call authService and navigate to dashboard on success', () => {
    const mockResponse = {
      statusCode: 200,
      message: 'Success',
      data: {
        tokenType: 'Bearer',
        accessToken: 'mock-access',
        refreshToken: 'mock-refresh',
        expiresIn: 900,
        user: {
          id: '123',
          username: 'marketing_user',
          nama: 'Marketing User',
          email: 'marketing@bca.co.id',
          role: 'MARKETING',
          tipe: 'KARYAWAN',
          status: true,
        },
      },
    };

    authServiceSpy.loginKaryawan.mockReturnValue(of(mockResponse));

    component.emailOrUsername = 'marketing_user';
    component.password = 'password123';
    component.onLogin();

    expect(authServiceSpy.loginKaryawan).toHaveBeenCalledWith({
      identifier: 'marketing_user',
      password: 'password123',
    });
    expect(toastServiceSpy.success).toHaveBeenCalledWith('Selamat datang kembali, Marketing User!');
    expect(router.navigate).toHaveBeenCalledWith(['/dashboard']);
    expect(component.isLoading).toBe(false);
  });

  it('should show error message and toast on login failure', () => {
    authServiceSpy.loginKaryawan.mockReturnValue(
      throwError(() => ({
        error: { message: 'Username/email atau password salah' },
      }))
    );

    component.emailOrUsername = 'marketing_user';
    component.password = 'wrong_password';
    component.onLogin();

    expect(component.errorMessage).toBe('Username/email atau password salah');
    expect(toastServiceSpy.error).toHaveBeenCalledWith('Username/email atau password salah');
    expect(component.isLoading).toBe(false);
  });

  it('should toggle password visibility', () => {
    expect(component.showPassword).toBe(false);
    component.togglePasswordVisibility();
    expect(component.showPassword).toBe(true);
  });
});
