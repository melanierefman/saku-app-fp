import {
  HttpInterceptorFn,
  HttpErrorResponse,
  HttpRequest,
  HttpHandlerFn,
} from '@angular/common/http';
import { inject } from '@angular/core';
import { Router } from '@angular/router';
import {
  catchError,
  throwError,
  switchMap,
  filter,
  take,
  BehaviorSubject,
} from 'rxjs';
import { TokenService } from '../services/auth/token.service';
import { AuthService } from '../services/auth/auth.service';
import { ToastService } from '../../shared/components/toast/toast.service';

let isRefreshing = false;
const refreshTokenSubject: BehaviorSubject<string | null> =
  new BehaviorSubject<string | null>(null);

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const tokenService = inject(TokenService);
  const authService = inject(AuthService);
  const router = inject(Router);
  const toastService = inject(ToastService);

  const token = tokenService.getAccessToken();
  const isAuthRequest = req.url.includes('/auth/');

  let authReq = req;
  if (token && !isAuthRequest) {
    authReq = addToken(req, token);
  }

  return next(authReq).pipe(
    catchError((error: HttpErrorResponse) => {
      // If 401 on non-auth requests -> refresh token
      if (error.status === 401 && !isAuthRequest) {
        const refreshToken = tokenService.getRefreshToken();
        if (refreshToken) {
          return handle401Error(
            req,
            next,
            authService,
            tokenService,
            router,
            toastService
          );
        } else {
          tokenService.clearSession();
          toastService.error('Sesi Anda telah berakhir. Silakan masuk kembali.');
          router.navigate(['/login']);
        }
      }
      return throwError(() => error);
    })
  );
};

function addToken(request: HttpRequest<any>, token: string): HttpRequest<any> {
  return request.clone({
    setHeaders: {
      Authorization: `Bearer ${token}`,
    },
  });
}

function handle401Error(
  req: HttpRequest<any>,
  next: HttpHandlerFn,
  authService: AuthService,
  tokenService: TokenService,
  router: Router,
  toastService: ToastService
) {
  if (!isRefreshing) {
    isRefreshing = true;
    refreshTokenSubject.next(null);

    return authService.refreshToken().pipe(
      switchMap((res) => {
        isRefreshing = false;
        const newToken =
          res?.data?.accessToken || tokenService.getAccessToken();
        if (newToken) {
          refreshTokenSubject.next(newToken);
          return next(addToken(req, newToken));
        }
        refreshTokenSubject.next('FAILED');
        tokenService.clearSession();
        toastService.error('Sesi Anda telah berakhir. Silakan masuk kembali.');
        router.navigate(['/login']);
        return throwError(
          () => new Error('Refresh token returned no access token')
        );
      }),
      catchError((err) => {
        isRefreshing = false;
        refreshTokenSubject.next('FAILED');
        tokenService.clearSession();
        toastService.error('Sesi Anda telah berakhir. Silakan masuk kembali.');
        router.navigate(['/login']);
        return throwError(() => err);
      })
    );
  } else {
    return refreshTokenSubject.pipe(
      filter((token) => token !== null),
      take(1),
      switchMap((token) => {
        if (token && token !== 'FAILED') {
          return next(addToken(req, token));
        }
        return throwError(() => new Error('Token refresh failed'));
      })
    );
  }
}
