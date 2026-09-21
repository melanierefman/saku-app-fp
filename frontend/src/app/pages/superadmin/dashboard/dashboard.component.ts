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
import { AuthStore } from '../../../core/store/auth.store';
import {
  SuperadminDashboardService,
  SuperadminDashboardStats,
  BranchPerformanceItem,
  MonthlyLoanTrendItem,
  RecentPengajuanItem,
  RecentAuditLogItem,
} from '../../../core';
import {
  BadgeComponent,
  BadgeVariant,
  ToastService,
  SkeletonComponent,
} from '../../../shared/components';
import { MarketingDashboardComponent } from '../../marketing/dashboard/marketing-dashboard.component';
import { BranchManagerDashboardComponent } from '../../branchmanager/dashboard/branch-manager-dashboard.component';
import { BackofficeDashboardComponent } from '../../backoffice/dashboard/backoffice-dashboard.component';
import {
  LucideFileText,
  LucideBanknote,
  LucideCheckCircle2,
  LucideTrendingUp,
  LucideUsers,
  LucideBuilding2,
  LucideRefreshCw,
  LucideArrowRight,
  LucideHistory,
  LucideClock,
  LucideActivity,
  LucideUserCheck,
  LucideXCircle,
} from '@lucide/angular';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [
    CommonModule,
    RouterModule,
    BadgeComponent,
    SkeletonComponent,
    MarketingDashboardComponent,
    BranchManagerDashboardComponent,
    BackofficeDashboardComponent,
    LucideFileText,
    LucideBanknote,
    LucideCheckCircle2,
    LucideTrendingUp,
    LucideUsers,
    LucideBuilding2,
    LucideRefreshCw,
    LucideArrowRight,
    LucideHistory,
    LucideClock,
    LucideActivity,
    LucideUserCheck,
    LucideXCircle,
  ],
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.css',
})
export class DashboardComponent implements OnInit {
  private authStore = inject(AuthStore);
  private dashboardService = inject(SuperadminDashboardService);
  private toastService = inject(ToastService);
  private platformId = inject(PLATFORM_ID);
  private cdr = inject(ChangeDetectorRef);
  private router = inject(Router);

  readonly currentUser = this.authStore.currentUser;
  readonly userRole = this.authStore.userRole;

  navigateToList(status?: string): void {
    if (status) {
      this.router.navigate(['/superadmin/monitoring/pengajuan'], { queryParams: { status } });
    } else {
      this.router.navigate(['/superadmin/monitoring/pengajuan']);
    }
  }

  navigateToCustomer(): void {
    this.router.navigate(['/superadmin/customer']);
  }

  navigateToCabang(): void {
    this.router.navigate(['/superadmin/cabang']);
  }

  navigateToPlafond(): void {
    this.router.navigate(['/superadmin/plafond']);
  }

  // Signals
  isLoading = signal<boolean>(true);
  stats = signal<SuperadminDashboardStats | null>(null);

  get userName(): string {
    return this.currentUser()?.nama || this.currentUser()?.username || 'Pengguna SAKU';
  }

  get userRoleDisplay(): string {
    const role = this.userRole() || 'KARYAWAN';
    return role.replace(/^ROLE_/, '').replace(/_/g, ' ').toUpperCase();
  }

  get isSuperAdmin(): boolean {
    const role = (this.userRole() || '').toUpperCase();
    return role.includes('SUPERADMIN') || role.includes('ADMIN');
  }

  get isMarketing(): boolean {
    const role = (this.userRole() || '').toUpperCase();
    return role.includes('MARKETING');
  }

  get isBranchManager(): boolean {
    const role = (this.userRole() || '').toUpperCase();
    return role.includes('BRANCH') || role.includes('BM') || role.includes('BRANCHMANAGER');
  }

  get isBackoffice(): boolean {
    const role = (this.userRole() || '').toUpperCase();
    return role.includes('BACKOFFICE') || role.includes('BO');
  }

  // Monthly trends helper max value for bar scale calculation
  maxMonthlyNominal = computed<number>(() => {
    const trends = this.stats()?.monthlyTrends || [];
    if (trends.length === 0) return 1;
    let max = 0;
    for (const item of trends) {
      if (item.totalNominalDiajukan > max) max = item.totalNominalDiajukan;
      if (item.totalNominalDicairkan > max) max = item.totalNominalDicairkan;
    }
    return max > 0 ? max : 1;
  });

  ngOnInit(): void {
    if (isPlatformBrowser(this.platformId)) {
      if (this.isSuperAdmin) {
        this.loadDashboardData();
      } else {
        this.isLoading.set(false);
      }
    }
  }

  loadDashboardData(): void {
    this.isLoading.set(true);
    this.dashboardService.getDashboardStats().subscribe({
      next: (res) => {
        if (res.data) {
          this.stats.set(res.data);
        }
        this.isLoading.set(false);
        this.cdr.markForCheck();
      },
      error: (err) => {
        console.error('Failed to load superadmin dashboard stats:', err);
        this.toastService.error(
          err?.error?.message || 'Gagal memuat data statistik dashboard.'
        );
        this.isLoading.set(false);
        this.cdr.markForCheck();
      },
    });
  }

  // Format IDR Currency
  formatCurrency(value?: number | null): string {
    if (value === undefined || value === null || isNaN(value)) {
      return 'Rp 0';
    }
    return new Intl.NumberFormat('id-ID', {
      style: 'currency',
      currency: 'IDR',
      minimumFractionDigits: 0,
      maximumFractionDigits: 0,
    }).format(value);
  }

  // Format Number (e.g. 1.250 or 85.5)
  formatNumber(value?: number | null, fractionDigits: number = 0): string {
    if (value === undefined || value === null || isNaN(value)) {
      return '0';
    }
    return new Intl.NumberFormat('id-ID', {
      minimumFractionDigits: fractionDigits,
      maximumFractionDigits: fractionDigits,
    }).format(value);
  }

  // Format Large Currency for concise display (e.g. Rp 1.5 M, Rp 450 Jt)
  formatCompactCurrency(value?: number | null): string {
    if (!value || isNaN(value)) return 'Rp 0';
    if (value >= 1_000_000_000) {
      return `Rp ${(value / 1_000_000_000).toFixed(1)} M`;
    }
    if (value >= 1_000_000) {
      return `Rp ${(value / 1_000_000).toFixed(1)} Jt`;
    }
    return this.formatCurrency(value);
  }

  // Format Date & Time
  formatDateTime(dateStr?: string | null): string {
    if (!dateStr) return '-';
    try {
      const date = new Date(dateStr);
      if (isNaN(date.getTime())) return dateStr;
      return new Intl.DateTimeFormat('id-ID', {
        day: '2-digit',
        month: 'short',
        year: 'numeric',
        hour: '2-digit',
        minute: '2-digit',
      }).format(date);
    } catch {
      return dateStr;
    }
  }

  formatDateOnly(dateStr?: string | null): string {
    if (!dateStr) return '-';
    try {
      const date = new Date(dateStr);
      if (isNaN(date.getTime())) return dateStr;
      return new Intl.DateTimeFormat('id-ID', {
        day: '2-digit',
        month: 'short',
        year: 'numeric',
      }).format(date);
    } catch {
      return dateStr;
    }
  }

  // Calculate percentage for pipeline bar
  getFunnelPercentage(count?: number | null): number {
    const total = this.stats()?.totalPengajuan || 0;
    if (!total || !count || count <= 0) return 0;
    return Math.min(100, Math.round((count / total) * 100));
  }

  // Status mapping for recent pengajuan
  getStatusBadgeVariant(status?: string): BadgeVariant {
    const s = (status || '').toUpperCase().trim().replace(/\s+/g, '_');
    switch (s) {
      // 1. Marketing
      case 'MENUNGGU_REVIEW_MARKETING':
      case 'MENUNGGU_REVIEW':
      case 'PENDING':
        return 'primary';
      case 'PERLU_REVISI':
      case 'DOKUMEN_DIREVISI':
        return 'warning';
      case 'SELESAI_DIREVIEW':
        return 'info';

      // 2. Branch Manager (BM)
      case 'MENUNGGU_PERSETUJUAN_BM':
      case 'MENUNGGU_PERSETUJUAN':
        return 'warning';
      case 'DISETUJUI':
      case 'PENGAJUAN_DISETUJUI':
      case 'APPROVED':
        return 'success';

      // 3. Backoffice / Pencairan
      case 'MENUNGGU_PENCAIRAN':
        return 'cyan';
      case 'DICAIRKAN':
      case 'TELAH_DICAIRKAN':
      case 'DISBURSED':
      case 'BERHASIL':
      case 'CAIR':
      case 'SELESAI':
        return 'success';

      // 4. Ditolak
      case 'DITOLAK_MARKETING':
      case 'DITOLAK_BM':
      case 'DITOLAK':
      case 'PENGAJUAN_DITOLAK':
      case 'REJECTED':
      case 'GAGAL':
        return 'error';

      default:
        return 'neutral';
    }
  }

  getStatusLabel(status?: string, customLabel?: string): string {
    if (customLabel && customLabel.trim()) return customLabel.trim();
    if (!status) return '-';
    const s = status.toUpperCase().trim().replace(/\s+/g, '_');
    switch (s) {
      case 'MENUNGGU_REVIEW_MARKETING':
      case 'MENUNGGU_REVIEW':
      case 'PENDING':
        return 'Menunggu Review Marketing';
      case 'PERLU_REVISI':
        return 'Perlu Revisi Dokumen';
      case 'DOKUMEN_DIREVISI':
        return 'Revisi Diajukan';
      case 'SELESAI_DIREVIEW':
        return 'Selesai Review';
      case 'MENUNGGU_PERSETUJUAN_BM':
      case 'MENUNGGU_PERSETUJUAN':
        return 'Menunggu Persetujuan BM';
      case 'MENUNGGU_PENCAIRAN':
        return 'Menunggu Pencairan';
      case 'DICAIRKAN':
      case 'TELAH_DICAIRKAN':
      case 'DISBURSED':
      case 'BERHASIL':
        return 'Dana Dicairkan';
      case 'DISETUJUI':
      case 'PENGAJUAN_DISETUJUI':
      case 'APPROVED':
        return 'Pengajuan Disetujui';
      case 'DITOLAK_MARKETING':
        return 'Ditolak Marketing';
      case 'DITOLAK_BM':
        return 'Ditolak Branch Manager';
      case 'DITOLAK':
      case 'PENGAJUAN_DITOLAK':
      case 'REJECTED':
        return 'Ditolak';
      default:
        return status
          .replace(/_/g, ' ')
          .toLowerCase()
          .replace(/\b\w/g, (char) => char.toUpperCase());
    }
  }

  // Action badge mapping for audit logs
  getActionBadgeVariant(action?: string): BadgeVariant {
    switch (action?.toUpperCase()) {
      case 'CREATE':
        return 'success';
      case 'UPDATE':
        return 'warning';
      case 'DELETE':
        return 'error';
      case 'LOGIN':
      case 'AUTH':
        return 'primary';
      case 'APPROVE':
      case 'CAIRKAN':
        return 'success';
      case 'REJECT':
        return 'error';
      default:
        return 'neutral';
    }
  }
}
