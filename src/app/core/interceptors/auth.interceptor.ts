import { HttpInterceptorFn, HttpErrorResponse } from '@angular/common/http';
import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { catchError, throwError } from 'rxjs';
import { TokenService } from '../services/token.service';
import { ToastService } from '../../shared/components/toast/toast.service';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const tokenService = inject(TokenService);
  const router = inject(Router);
  const toastService = inject(ToastService);

  const token = tokenService.getAccessToken();

  const isAuthRequest = req.url.includes('/auth/');

  let authReq = req;
  if (token && !isAuthRequest) {
    authReq = req.clone({
      setHeaders: {
        Authorization: `Bearer ${token}`,
      },
    });
  }

  return next(authReq).pipe(
    catchError((error: HttpErrorResponse) => {
      if (error.status === 401 && !isAuthRequest) {
        tokenService.clearSession();
        toastService.error('Sesi Anda telah berakhir. Silakan masuk kembali.');
        router.navigate(['/login']);
      }
      return throwError(() => error);
    })
  );
};
