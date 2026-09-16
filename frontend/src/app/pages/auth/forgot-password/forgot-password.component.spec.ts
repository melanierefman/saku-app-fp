import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { provideRouter, Router } from '@angular/router';
import { of, throwError } from 'rxjs';
import { ForgotPasswordComponent } from './forgot-password.component';
import { AuthService } from '../../../core/services/auth/auth.service';
import { ToastService } from '../../../shared/components/toast/toast.service';

describe('ForgotPasswordComponent', () => {
  let component: ForgotPasswordComponent;
  let fixture: ComponentFixture<ForgotPasswordComponent>;
  let authServiceSpy: {
    requestOtp: ReturnType<typeof vi.fn>;
    resetPassword: ReturnType<typeof vi.fn>;
  };
  let toastServiceSpy: { success: ReturnType<typeof vi.fn>; error: ReturnType<typeof vi.fn> };
  let router: Router;

  beforeEach(async () => {
    authServiceSpy = {
      requestOtp: vi.fn(),
      resetPassword: vi.fn(),
    };
    toastServiceSpy = {
      success: vi.fn(),
      error: vi.fn(),
    };

    await TestBed.configureTestingModule({
      imports: [ForgotPasswordComponent],
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

    fixture = TestBed.createComponent(ForgotPasswordComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create forgot password component', () => {
    expect(component).toBeTruthy();
    expect(component.step).toBe(1);
  });

  it('should validate email format in step 1', () => {
    component.email = '';
    component.onRequestOtp();
    expect(component.emailError).toBe('Email wajib diisi');

    component.email = 'invalid-email';
    component.onRequestOtp();
    expect(component.emailError).toBe('Format email tidak valid');
  });

  it('should advance to step 2 when OTP request succeeds', () => {
    authServiceSpy.requestOtp.mockReturnValue(
      of({
        statusCode: 200,
        message: 'OTP sent',
        data: { message: 'OTP dikirim ke email' },
      })
    );

    component.email = 'customer@example.com';
    component.onRequestOtp();

    expect(authServiceSpy.requestOtp).toHaveBeenCalledWith('customer@example.com');
    expect(component.step).toBe(2);
    expect(toastServiceSpy.success).toHaveBeenCalledWith('Kode OTP telah dikirim ke email Anda.');
  });

  it('should show error when OTP request fails', () => {
    authServiceSpy.requestOtp.mockReturnValue(
      throwError(() => ({
        error: { message: 'Email tidak ditemukan' },
      }))
    );

    component.email = 'unknown@example.com';
    component.onRequestOtp();

    expect(component.emailError).toBe('Email tidak ditemukan');
    expect(toastServiceSpy.error).toHaveBeenCalledWith('Email tidak ditemukan');
    expect(component.step).toBe(1);
  });
});
