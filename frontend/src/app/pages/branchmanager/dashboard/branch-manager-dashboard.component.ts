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
import { forkJoin, of, map, catchError } from 'rxjs';
import {
  BranchManagerDashboardService,
  BranchManagerDashboardStats,
  MonthlyTrendItem,
  BranchManagerApprovalService,
  BranchManagerPengajuanItemResponse,
} from '../../../core';
import {
  BadgeComponent,
  BadgeVariant,
  ToastService,
  SkeletonComponent,
} from '../../../shared/components';
import {
  LucideFileText,
  LucideClock,
  LucideCheckCircle2,
  LucideXCircle,
  LucideTrendingUp,
  LucideRefreshCw,
  LucideArrowRight,
  LucideActivity,
  LucideBanknote,
  LucideCoins,
  LucidePieChart,
  LucideEye,
} from '@lucide/angular';

export interface TenorItemDisplay {
  label: string;
  count: number;
  pct: number;
  colorClass: string;
  bgClass: string;
  borderClass: string;
}

@Component({
  selector: 'app-branch-manager-dashboard',
  standalone: true,
  imports: [
    CommonModule,
    RouterModule,
    BadgeComponent,
    SkeletonComponent,
    LucideFileText,
    LucideClock,
    LucideCheckCircle2,
    LucideXCircle,
    LucideTrendingUp,
    LucideRefreshCw,
    LucideArrowRight,
    LucideActivity,
    LucideBanknote,
    LucideCoins,
    LucidePieChart,
    LucideEye,
  ],
  templateUrl: './branch-manager-dashboard.component.html',
  styleUrl: './branch-manager-dashboard.component.css',
})
export class BranchManagerDashboardComponent implements OnInit {
  private dashboardService = inject(BranchManagerDashboardService);
  private approvalService = inject(BranchManagerApprovalService);
  private toastService = inject(ToastService);
  private router = inject(Router);
  private platformId = inject(PLATFORM_ID);
  private cdr = inject(ChangeDetectorRef);

  // Loading signals
  isLoadingStats = signal<boolean>(true);
  isLoadingWorklist = signal<boolean>(true);

  // Data signals
  stats = signal<BranchManagerDashboardStats | null>(null);
  recentPengajuan = signal<BranchManagerPengajuanItemResponse[]>([]);

  // Computed helper for total 6-month count & total nominal
  totalMonthlyCountDisetujui = computed<number>(() => {
    const trends: MonthlyTrendItem[] = this.stats()?.monthlyApprovalTrends || [];
    return trends.reduce((acc: number, curr: MonthlyTrendItem) => acc + (curr.countDisetujui || 0), 0);
  });

  totalMonthlyNominalDisetujui = computed<number>(() => {
    const trends: MonthlyTrendItem[] = this.stats()?.monthlyApprovalTrends || [];
    return trends.reduce((acc: number, curr: MonthlyTrendItem) => acc + (curr.totalNominalDisetujui || 0), 0);
  });

  maxMonthlyCount = computed<number>(() => {
    const trends: MonthlyTrendItem[] = this.stats()?.monthlyApprovalTrends || [];
    if (trends.length === 0) return 1;
    const max = Math.max(...trends.map((t: MonthlyTrendItem) => t.countDisetujui || 0));
    return max > 0 ? max : 1;
  });

  // Tenor Distribution computation
  tenorBreakdown = computed<{ items: TenorItemDisplay[]; total: number }>(() => {
    const dist: Record<string, number> = this.stats()?.tenorDistribution || {};
    const entries = Object.entries(dist);
    const total = entries.reduce((acc, [, val]) => acc + (typeof val === 'number' ? val : Number(val) || 0), 0);

    const palette = [
      { colorClass: 'bg-primary', bgClass: 'bg-primary-0/40', borderClass: 'border-primary-20/60' },
      { colorClass: 'bg-indigo-500', bgClass: 'bg-indigo-50/60', borderClass: 'border-indigo-200/60' },
      { colorClass: 'bg-success', bgClass: 'bg-success-0/40', borderClass: 'border-success-20/60' },
      { colorClass: 'bg-warning', bgClass: 'bg-warning-0/40', borderClass: 'border-warning-20/60' },
      { colorClass: 'bg-sky-500', bgClass: 'bg-sky-50/60', borderClass: 'border-sky-200/60' },
      { colorClass: 'bg-error', bgClass: 'bg-error-0/40', borderClass: 'border-error-20/60' },
    ];

    const items: TenorItemDisplay[] = entries.map(([label, val], idx) => {
      const count = typeof val === 'number' ? val : Number(val) || 0;
      const pct = total > 0 ? Math.round((count / total) * 100) : 0;
      const theme = palette[idx % palette.length];
      return {
        label: label.includes('Bulan') ? label : `${label} Bulan`,
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
    this.loadRecentWorklist();
  }

  loadStats(): void {
    this.isLoadingStats.set(true);
    this.dashboardService.getDashboardStats().subscribe({
      next: (res: any) => {
        if (res?.data) {
          this.stats.set(res.data);
        } else if (res && typeof res === 'object' && ('menungguPersetujuan' in res || 'totalPengajuanCabang' in res)) {
          this.stats.set(res);
        }
        this.isLoadingStats.set(false);
        this.cdr.markForCheck();
      },
      error: (err) => {
        console.warn('Gagal memuat statistik Dashboard Branch Manager:', err);
        this.isLoadingStats.set(false);
        this.cdr.markForCheck();
      },
    });
  }

  loadRecentWorklist(): void {
    this.isLoadingWorklist.set(true);
    this.approvalService
      .findAllPaginated({
        page: 0,
        size: 5,
        status: 'MENUNGGU_PERSETUJUAN',
      })
      .subscribe({
        next: (pageRes: any) => {
          const list: BranchManagerPengajuanItemResponse[] = Array.isArray(pageRes?.content)
            ? pageRes.content
            : Array.isArray(pageRes?.data?.content)
            ? pageRes.data.content
            : Array.isArray(pageRes?.data)
            ? pageRes.data
            : Array.isArray(pageRes)
            ? pageRes
            : [];
          const top5 = list.slice(0, 5);
          this.recentPengajuan.set(top5);
          this.isLoadingWorklist.set(false);
          this.cdr.markForCheck();

          // Enrich scores if missing
          if (top5.length > 0 && top5.some((it) => this.getScore(it) === 0)) {
            const enrichObservables = top5.map((item) => {
              const id = item.pengajuanId || item.id;
              if (!id || this.getScore(item) > 0) return of(item);

              return this.approvalService.getDetail(id).pipe(
                map((detail) => {
                  if (!detail) return item;
                  const realScore =
                    detail.skorKredit ??
                    detail.skor ??
                    (detail as any).scoring?.skorKredit ??
                    (detail as any).scoring?.skor ??
                    (detail as any).customer?.skorKredit ??
                    (detail as any).customer?.skor;

                  return {
                    ...item,
                    skorKredit: realScore !== undefined && realScore !== null ? Number(realScore) : item.skorKredit,
                    skor: realScore !== undefined && realScore !== null ? Number(realScore) : item.skor,
                    statusScoring: detail.statusScoring || (item as any).statusScoring,
                  };
                }),
                catchError(() => of(item))
              );
            });

            forkJoin(enrichObservables).subscribe({
              next: (enrichedList) => {
                this.recentPengajuan.set(enrichedList);
                this.cdr.markForCheck();
              },
              error: () => {},
            });
          }
        },
        error: (err) => {
          console.warn('Gagal memuat antrean prioritas persetujuan BM:', err);
          this.isLoadingWorklist.set(false);
          this.cdr.markForCheck();
        },
      });
  }

  navigateToList(status?: string): void {
    if (status) {
      this.router.navigate(['/persetujuan-pinjaman'], { queryParams: { status } });
    } else {
      this.router.navigate(['/persetujuan-pinjaman']);
    }
  }

  navigateToDetail(id?: string): void {
    if (!id) return;
    this.router.navigate(['/persetujuan-pinjaman/detail', id]);
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
    const max = this.maxMonthlyCount();
    if (max === 0 || count === 0) return 6;
    const pct = Math.round((count / max) * 100);
    return Math.max(pct, 10);
  }

  getMarketingStatusBadgeVariant(status?: string): BadgeVariant {
    const s = (status || '').toUpperCase();
    if (s.includes('DISETUJUI') || s.includes('APPROV') || s.includes('LOLOS') || s.includes('REKOMENDASI')) {
      return 'success';
    }
    if (s.includes('TOLAK') || s.includes('REJECT') || s.includes('TIDAK')) {
      return 'error';
    }
    if (s.includes('REVISI') || s.includes('PERBAIKAN')) {
      return 'warning';
    }
    return 'neutral';
  }

  getMarketingStatusLabel(status?: string): string {
    const s = (status || '').toUpperCase();
    if (s.includes('DISETUJUI') || s.includes('APPROV') || s.includes('LOLOS') || s.includes('REKOMENDASI')) {
      return 'Direkomendasikan';
    }
    if (s.includes('TOLAK') || s.includes('REJECT')) {
      return 'Tidak Direkomendasikan';
    }
    if (s.includes('REVISI')) {
      return 'Perlu Revisi';
    }
    return status || 'Belum Direview';
  }

  getScore(item: any): number {
    const val =
      item?.skorKredit ??
      item?.skor ??
      item?.scoring?.skorKredit ??
      item?.scoring?.skor ??
      item?.scoring?.score ??
      item?.scoring?.totalSkor ??
      item?.creditScore ??
      item?.score ??
      item?.nilaiSkor ??
      item?.customer?.skorKredit ??
      item?.customer?.skor ??
      item?.customer?.creditScore ??
      null;

    if (val !== null && val !== undefined && !isNaN(Number(val))) {
      return Number(val);
    }
    return 0;
  }

  getScoreBadgeVariant(score: number): BadgeVariant {
    if (score >= 75) return 'success';
    if (score >= 60) return 'warning';
    if (score > 0) return 'error';
    return 'neutral';
  }

  getScoreLabel(score: number): string {
    if (score >= 75) return 'Tinggi';
    if (score >= 60) return 'Sedang';
    if (score > 0) return 'Rendah';
    return 'Belum Dinilai';
  }

  getStatusBadgeVariant(status?: string, hasilPersetujuan?: string): BadgeVariant {
    const label = this.getStatusLabel(status, hasilPersetujuan);
    if (label === 'Disetujui BM') return 'success';
    if (label === 'Ditolak BM') return 'error';
    if (label === 'Menunggu Persetujuan BM') return 'warning';
    return 'neutral';
  }

  getStatusLabel(status?: string, hasilPersetujuan?: string): string {
    const s = (status || '').toUpperCase();
    const h = (hasilPersetujuan || '').toUpperCase();

    if (
      h === 'DISETUJUI' ||
      s === 'DISETUJUI' ||
      s === 'DICAIRKAN' ||
      s === 'DISBURSED' ||
      s === 'BERHASIL' ||
      s.includes('CAIR') ||
      s.includes('BACKOFFICE')
    ) {
      return 'Disetujui BM';
    }
    if (h === 'DITOLAK' || s === 'DITOLAK') {
      return 'Ditolak BM';
    }
    return 'Menunggu Persetujuan BM';
  }
}
