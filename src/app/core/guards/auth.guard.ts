import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthStore } from '../store/auth.store';
import { ToastService } from '../../shared/components/toast/toast.service';

export const authGuard: CanActivateFn = () => {
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
  const authStore = inject(AuthStore);
  const router = inject(Router);

  if (!authStore.isAuthenticated()) {
    return true;
  }

  return router.createUrlTree(['/dashboard']);
};
