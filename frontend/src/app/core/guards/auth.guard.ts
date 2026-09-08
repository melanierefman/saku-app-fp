import { inject, PLATFORM_ID } from '@angular/core';
import { isPlatformBrowser } from '@angular/common';
import { CanActivateFn, Router } from '@angular/router';
import { AuthStore } from '../store/auth.store';
import { ToastService } from '../../shared/components/toast/toast.service';

export const authGuard: CanActivateFn = () => {
  const platformId = inject(PLATFORM_ID);
  if (!isPlatformBrowser(platformId)) {
    return true;
  }

  const authStore = inject(AuthStore);
  const router = inject(Router);
  const toastService = inject(ToastService);

  if (authStore.isAuthenticated()) {
    return true;
  }

  toastService.warning('Silakan masuk terlebih dahulu untuk mengakses halaman ini.');
  return router.createUrlTree(['/login']);
};

export const guestGuard: CanActivateFn = () => {
  const platformId = inject(PLATFORM_ID);
  if (!isPlatformBrowser(platformId)) {
    return true;
  }

  const authStore = inject(AuthStore);
  const router = inject(Router);

  if (!authStore.isAuthenticated()) {
    return true;
  }

  return router.createUrlTree(['/dashboard']);
};
