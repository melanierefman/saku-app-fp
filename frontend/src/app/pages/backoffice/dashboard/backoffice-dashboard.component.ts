import {
  Component,
  OnInit,
  signal,
  inject,
  ChangeDetectorRef,
  PLATFORM_ID,
  computed,
} from '@angular/core';
import { CommonModule, isPlatformBrowser } from '@angular/common';
import { RouterModule, Router } from '@angular/router';
import {
  BackofficeDashboardService,
  BackofficeDashboardStats,
  DailyDisbursementItem,
  BackOfficeVerifikasiCustomerService,
  VerifikasiCustomerItem,
  BackOfficePencairanService,
  PencairanItem,
} from '../../../core';
import {
  BadgeComponent,
  BadgeVariant,
  ToastService,
  SkeletonComponent,
} from '../../../shared/components';
import {
  LucideCheckCircle2,
  LucideRefreshCw,
  LucideArrowRight,
  LucideActivity,
  LucideBanknote,
  LucideCalendarCheck,
  LucideUserCheck,
  LucideWallet,
  LucideReceipt,
  LucideCreditCard,
  LucideEye,
} from '@lucide/angular';

export interface BankItemDisplay {
  bankName: string;
  count: number;
  pct: number;
  colorClass: string;
  bgClass: string;
  borderClass: string;
}

@Component({
  selector: 'app-backoffice-dashboard',
  standalone: true,
  imports: [
    CommonModule,
    RouterModule,
    BadgeComponent,
    SkeletonComponent,
    LucideCheckCircle2,
    LucideRefreshCw,
    LucideArrowRight,
    LucideActivity,
    LucideBanknote,
    LucideCalendarCheck,
    LucideUserCheck,
    LucideWallet,
    LucideReceipt,
    LucideCreditCard,
    LucideEye,
  ],
  templateUrl: './backoffice-dashboard.component.html',
  styleUrl: './backoffice-dashboard.component.css',
})
export class BackofficeDashboardComponent implements OnInit {
  private dashboardService = inject(BackofficeDashboardService);
  private kycService = inject(BackOfficeVerifikasiCustomerService);
  private pencairanService = inject(BackOfficePencairanService);
  private toastService = inject(ToastService);
  private router = inject(Router);
  private platformId = inject(PLATFORM_ID);
  private cdr = inject(ChangeDetectorRef);

  // Loading signals
  isLoadingStats = signal<boolean>(true);
  isLoadingKyc = signal<boolean>(true);
  isLoadingPencairan = signal<boolean>(true);

  // Data signals
  stats = signal<BackofficeDashboardStats | null>(null);
  recentKyc = signal<VerifikasiCustomerItem[]>([]);
  recentPencairan = signal<PencairanItem[]>([]);

  // Computed Weekly Disbursements
  totalWeeklyDisbursementCount = computed<number>(() => {
    const list: DailyDisbursementItem[] = this.stats()?.weeklyDisbursements || [];
    return list.reduce((acc, curr) => acc + (curr.count || 0), 0);
  });

  totalWeeklyDisbursementNominal = computed<number>(() => {
    const list: DailyDisbursementItem[] = this.stats()?.weeklyDisbursements || [];
    return list.reduce((acc, curr) => acc + (curr.totalNominal || 0), 0);
  });

  maxWeeklyDisbursementCount = computed<number>(() => {
    const list: DailyDisbursementItem[] = this.stats()?.weeklyDisbursements || [];
    if (list.length === 0) return 1;
    const max = Math.max(...list.map((item) => item.count || 0));
    return max > 0 ? max : 1;
  });

  // Bank Distribution Computation
  bankBreakdown = computed<{ items: BankItemDisplay[]; total: number }>(() => {
    const dist: Record<string, number> = this.stats()?.bankDistribution || {};
    const entries = Object.entries(dist);
    const total = entries.reduce(
      (acc, [, val]) => acc + (typeof val === 'number' ? val : Number(val) || 0),
      0
    );

    const palette = [
      { colorClass: 'bg-blue-600', bgClass: 'bg-blue-50/60', borderClass: 'border-blue-200/60' }, // BCA style
      { colorClass: 'bg-warning', bgClass: 'bg-warning-0/60', borderClass: 'border-warning-20/60' }, // Mandiri style
      { colorClass: 'bg-blue-700', bgClass: 'bg-sky-50/60', borderClass: 'border-sky-200/60' }, // BRI style
      { colorClass: 'bg-success', bgClass: 'bg-success-0/60', borderClass: 'border-success-20/60' }, // BNI / Syariah style
      { colorClass: 'bg-primary', bgClass: 'bg-primary-0/40', borderClass: 'border-primary-20/60' },
      { colorClass: 'bg-error', bgClass: 'bg-error-0/40', borderClass: 'border-error-20/60' },
    ];

    const items: BankItemDisplay[] = entries.map(([bankName, val], idx) => {
      const count = typeof val === 'number' ? val : Number(val) || 0;
      const pct = total > 0 ? Math.round((count / total) * 100) : 0;
      const theme = palette[idx % palette.length];
      return {
        bankName,
        count,
        pct,
        colorClass: theme.colorClass,
        bgClass: theme.bgClass,
        borderClass: theme.borderClass,
      };
    });

    return { items, total };
  });

  ngOnInit(): void {
    if (isPlatformBrowser(this.platformId)) {
      this.loadAllData();
    }
  }

  loadAllData(): void {
    this.loadStats();
    this.loadRecentKyc();
    this.loadRecentPencairan();
  }

  loadStats(): void {
    this.isLoadingStats.set(true);
    this.dashboardService.getDashboardStats().subscribe({
      next: (res: any) => {
        if (res?.data) {
          this.stats.set(res.data);
        } else if (
          res &&
          typeof res === 'object' &&
          ('menungguVerifikasiKyc' in res || 'totalNominalDicairkan' in res)
        ) {
          this.stats.set(res);
        }
        this.isLoadingStats.set(false);
        this.cdr.markForCheck();
      },
      error: (err) => {
        console.warn('Gagal memuat statistik Dashboard Backoffice:', err);
        this.isLoadingStats.set(false);
        this.cdr.markForCheck();
      },
    });
  }

  loadRecentKyc(): void {
    this.isLoadingKyc.set(true);
    this.kycService
      .findAllPaginated({
        page: 0,
        size: 5,
        status: 'PENDING',
      })
      .subscribe({
        next: (res: any) => {
          const list = Array.isArray(res?.content)
            ? res.content
            : Array.isArray(res?.data?.content)
            ? res.data.content
            : Array.isArray(res?.data)
            ? res.data
            : Array.isArray(res)
            ? res
            : [];
          this.recentKyc.set(list.slice(0, 5));
          this.isLoadingKyc.set(false);
          this.cdr.markForCheck();
        },
        error: (err) => {
          console.warn('Gagal memuat antrean verifikasi KYC Backoffice:', err);
          this.isLoadingKyc.set(false);
          this.cdr.markForCheck();
        },
      });
  }

  loadRecentPencairan(): void {
    this.isLoadingPencairan.set(true);
    this.pencairanService
      .findAllPaginated({
        page: 0,
        size: 5,
        status: 'MENUNGGU_PENCAIRAN',
      })
      .subscribe({
        next: (res: any) => {
          const list = Array.isArray(res?.content)
            ? res.content
            : Array.isArray(res?.data?.content)
            ? res.data.content
            : Array.isArray(res?.data)
            ? res.data
            : Array.isArray(res)
            ? res
            : [];
          this.recentPencairan.set(list.slice(0, 5));
          this.isLoadingPencairan.set(false);
          this.cdr.markForCheck();
        },
        error: (err) => {
          console.warn('Gagal memuat antrean pencairan dana Backoffice:', err);
          this.isLoadingPencairan.set(false);
          this.cdr.markForCheck();
        },
      });
  }

  navigateToKycList(): void {
    this.router.navigate(['/verifikasi-customer'], { queryParams: { status: 'PENDING' } });
  }

  navigateToKycDetail(id?: string): void {
    if (!id) return;
    this.router.navigate(['/verifikasi-customer/detail', id]);
  }

  navigateToPencairanList(): void {
    this.router.navigate(['/pencairan'], { queryParams: { status: 'MENUNGGU_PENCAIRAN' } });
  }

  navigateToPencairanDetail(id?: string): void {
    if (!id) return;
    this.router.navigate(['/pencairan/detail', id]);
  }

  formatCurrency(val?: number): string {
    if (val === undefined || val === null || isNaN(val)) return 'Rp 0';
    return new Intl.NumberFormat('id-ID', {
      style: 'currency',
      currency: 'IDR',
      maximumFractionDigits: 0,
    }).format(val);
  }

  formatDate(dateStr?: string): string {
    if (!dateStr) return '-';
    try {
      const d = new Date(dateStr);
      if (isNaN(d.getTime())) return dateStr;
      return new Intl.DateTimeFormat('id-ID', {
        day: 'numeric',
        month: 'short',
        year: 'numeric',
      }).format(d);
    } catch {
      return dateStr;
    }
  }

  getBarHeightPercent(count: number): number {
    const max = this.maxWeeklyDisbursementCount();
    if (max === 0 || count === 0) return 6;
    const pct = Math.round((count / max) * 100);
    return Math.max(pct, 10);
  }

  // KYC Helpers matching verifikasi-customer-list
  getTanggalRegister(row: any): string {
    const raw =
      row?.tanggalRegister ||
      row?.tanggalRegistrasi ||
      row?.tanggalDaftar ||
      row?.createdDate ||
      row?.createdAt ||
      row?.registrationDate ||
      row?.registeredAt ||
      row?.tglRegister ||
      row?.tglRegistrasi ||
      row?.tanggalPengajuan;
    return this.formatDate(raw);
  }

  getKycStatusBadgeVariant(status?: string): BadgeVariant {
    const s = (status || '').toUpperCase();
    if (s === 'APPROVED' || s === 'DISETUJUI' || s === 'VERIFIED') {
      return 'success';
    }
    if (s === 'REJECTED' || s === 'DITOLAK') {
      return 'error';
    }
    if (s === 'PERLU_REVISI' || s === 'REVISI') {
      return 'warning';
    }
    return 'warning';
  }

  getKycStatusLabel(status?: string): string {
    const s = (status || '').toUpperCase();
    switch (s) {
      case 'APPROVED':
      case 'DISETUJUI':
      case 'VERIFIED':
        return 'Disetujui';
      case 'REJECTED':
      case 'DITOLAK':
        return 'Ditolak';
      case 'PERLU_REVISI':
      case 'REVISI':
        return 'Perlu Revisi';
      case 'PENDING':
      case 'MENUNGGU_VERIFIKASI':
      default:
        return 'Menunggu Verifikasi';
    }
  }

  // Pencairan Helpers matching pencairan-list
  formatNomorPengajuan(no?: string): string {
    if (!no) return '-';
    return no;
  }

  getPencairanStatusBadgeVariant(status?: string): BadgeVariant {
    const s = (status || '').toUpperCase();
    if (s === 'DICAIRKAN' || s === 'BERHASIL' || s === 'CAIR' || s === 'DISBURSED') {
      return 'success';
    }
    return 'warning';
  }

  getPencairanStatusLabel(status?: string): string {
    const s = (status || '').toUpperCase();
    switch (s) {
      case 'DICAIRKAN':
      case 'BERHASIL':
      case 'CAIR':
      case 'DISBURSED':
        return 'Telah Cair';
      case 'MENUNGGU_PENCAIRAN':
      default:
        return 'Menunggu Pencairan';
    }
  }
}
