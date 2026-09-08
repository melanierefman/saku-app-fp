import { Component, inject, OnInit, signal, computed } from '@angular/core';
import { CommonModule, Location } from '@angular/common';
import { Router, ActivatedRoute, RouterModule } from '@angular/router';
import { AuthStore } from '../../core/store/auth.store';
import { ButtonComponent, BadgeComponent } from '../../shared/components';
import {
  LucideFileQuestion,
  LucideShieldAlert,
  LucideServerCrash,
  LucideArrowLeft,
  LucideHome,
  LucideRotateCw,
  LucideCircleHelp,
} from '@lucide/angular';

export type ErrorType = '404' | '403' | '500';

interface ErrorConfig {
  code: string;
  badge: string;
  badgeVariant: 'error' | 'warning' | 'primary' | 'neutral';
  title: string;
  description: string;
  primaryActionLabel: string;
  secondaryActionLabel?: string;
  iconName: string;
}

@Component({
  selector: 'app-error-page',
  standalone: true,
  imports: [
    CommonModule,
    RouterModule,
    ButtonComponent,
    BadgeComponent,
    LucideFileQuestion,
    LucideShieldAlert,
    LucideServerCrash,
    LucideArrowLeft,
    LucideHome,
    LucideRotateCw,
    LucideCircleHelp,
  ],
  templateUrl: './error-page.component.html',
  styleUrl: './error-page.component.css',
})
export class ErrorPageComponent implements OnInit {
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private location = inject(Location);
  private authStore = inject(AuthStore);

  errorType = signal<ErrorType>('404');

  readonly configs: Record<ErrorType, ErrorConfig> = {
    '404': {
      code: '404',
      badge: 'Halaman Tidak Ditemukan',
      badgeVariant: 'warning',
      title: 'Oops! Halaman yang Anda cari tidak tersedia',
      description:
        'Halaman yang Anda tuju mungkin telah dipindahkan, dihapus, atau alamat URL yang Anda masukkan tidak valid.',
      primaryActionLabel: 'Kembali ke Beranda',
      secondaryActionLabel: 'Halaman Sebelumnya',
      iconName: '404',
    },
    '403': {
      code: '403',
      badge: 'Akses Dibatasi',
      badgeVariant: 'error',
      title: 'Akses Ditolak (Forbidden)',
      description:
        'Akun Anda tidak memiliki hak akses atau kewenangan untuk membuka halaman ini. Silakan hubungi Superadmin jika Anda membutuhkan izin akses.',
      primaryActionLabel: 'Kembali ke Dashboard',
      secondaryActionLabel: 'Halaman Sebelumnya',
      iconName: '403',
    },
    '500': {
      code: '500',
      badge: 'Gangguan Sistem',
      badgeVariant: 'error',
      title: 'Terjadi Kesalahan pada Server',
      description:
        'Layanan kami sedang mengalami kendala internal atau dalam proses pemeliharaan. Tim teknis sedang menangani masalah ini.',
      primaryActionLabel: 'Muat Ulang Halaman',
      secondaryActionLabel: 'Kembali ke Beranda',
      iconName: '500',
    },
  };

  currentConfig = computed(() => this.configs[this.errorType()]);

  get userDashboardRoute(): string {
    if (!this.authStore.isAuthenticated()) {
      return '/';
    }
    const role = this.authStore.userRole()?.toUpperCase();
    switch (role) {
      case 'SUPERADMIN':
      case 'ADMIN':
        return '/dashboard';
      case 'MARKETING':
        return '/marketing/dashboard';
      case 'BRANCH_MANAGER':
      case 'BRANCHMANAGER':
      case 'BM':
        return '/branchmanager/dashboard';
      case 'BACKOFFICE':
      case 'BACK_OFFICE':
      case 'BO':
        return '/backoffice/dashboard';
      default:
        return '/dashboard';
    }
  }

  ngOnInit(): void {
    // Detect error type from route data or query param
    this.route.data.subscribe((data) => {
      if (data && data['type']) {
        const t = String(data['type']) as ErrorType;
        if (this.configs[t]) {
          this.errorType.set(t);
        }
      }
    });

    this.route.queryParams.subscribe((params) => {
      if (params && params['code']) {
        const c = String(params['code']) as ErrorType;
        if (this.configs[c]) {
          this.errorType.set(c);
        }
      }
    });
  }

  onPrimaryAction(): void {
    if (this.errorType() === '500') {
      window.location.reload();
      return;
    }
    this.router.navigate([this.userDashboardRoute]);
  }

  onSecondaryAction(): void {
    if (this.errorType() === '500') {
      this.router.navigate([this.userDashboardRoute]);
      return;
    }
    // Go back in history if possible
    this.location.back();
  }
}
