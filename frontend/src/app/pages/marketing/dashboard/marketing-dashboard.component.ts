import {
  Component,
  OnInit,
  OnDestroy,
  signal,
  inject,
  ChangeDetectorRef,
  PLATFORM_ID,
  computed,
} from '@angular/core';
import { CommonModule, isPlatformBrowser } from '@angular/common';
import { RouterModule, Router } from '@angular/router';
import { forkJoin, of, map, catchError, Subject, takeUntil } from 'rxjs';
import {
  MarketingDashboardService,
  MarketingDashboardStats,
  DailyTrendItem,
  MarketingLoanService,
  MarketingPengajuanItemResponse,
  RealTimeService,
} from '../../../core';
import {
  BadgeComponent,
  BadgeVariant,
  ToastService,
  SkeletonComponent,
} from '../../../shared/components';
import {
  LucideFileText,
  LucideCheckCircle2,
  LucideXCircle,
  LucideTrendingUp,
  LucideRefreshCw,
  LucideArrowRight,
  LucideActivity,
  LucideAward,
  LucideEye,
} from '@lucide/angular';

@Component({
  selector: 'app-marketing-dashboard',
  standalone: true,
  imports: [
    CommonModule,
    RouterModule,
    BadgeComponent,
    SkeletonComponent,
    LucideFileText,
    LucideCheckCircle2,
    LucideXCircle,
    LucideTrendingUp,
    LucideRefreshCw,
    LucideArrowRight,
    LucideActivity,
    LucideAward,
    LucideEye,
  ],
  templateUrl: './marketing-dashboard.component.html',
  styleUrl: './marketing-dashboard.component.css',
})
export class MarketingDashboardComponent implements OnInit, OnDestroy {
  private dashboardService = inject(MarketingDashboardService);
  private loanService = inject(MarketingLoanService);
  private realtimeService = inject(RealTimeService);
  private toastService = inject(ToastService);
  private router = inject(Router);
  private platformId = inject(PLATFORM_ID);
  private cdr = inject(ChangeDetectorRef);
  private destroy$ = new Subject<void>();

  // Loading signals
  isLoadingStats = signal<boolean>(true);
  isLoadingWorklist = signal<boolean>(true);

  // Data signals
  stats = signal<MarketingDashboardStats | null>(null);
  recentPengajuan = signal<MarketingPengajuanItemResponse[]>([]);

  // Computed helper for total weekly count & total nominal
  totalWeeklyCount = computed<number>(() => {
    const trends: DailyTrendItem[] = this.stats()?.weeklyTrends || [];
    return trends.reduce((acc: number, curr: DailyTrendItem) => acc + (curr.count || 0), 0);
  });

  totalWeeklyNominal = computed<number>(() => {
    const trends: DailyTrendItem[] = this.stats()?.weeklyTrends || [];
    return trends.reduce((acc: number, curr: DailyTrendItem) => acc + (curr.totalNominal || 0), 0);
  });

  maxWeeklyCount = computed<number>(() => {
    const trends: DailyTrendItem[] = this.stats()?.weeklyTrends || [];
    if (trends.length === 0) return 1;
    const max = Math.max(...trends.map((t: DailyTrendItem) => t.count || 0));
    return max > 0 ? max : 1;
  });

  // Scoring Distribution breakdown computation
  scoringBreakdown = computed(() => {
    const dist: Record<string, number> = this.stats()?.scoringDistribution || {};
    let high = 0;
    let medium = 0;
    let low = 0;

    for (const [key, val] of Object.entries(dist)) {
      const upperKey = key.toUpperCase();
      const countVal = typeof val === 'number' ? val : Number(val) || 0;
      if (upperKey.includes('TINGGI') || upperKey.includes('HIGH') || upperKey.includes('>= 75')) {
        high += countVal;
      } else if (
        upperKey.includes('SEDANG') ||
        upperKey.includes('MEDIUM') ||
        upperKey.includes('60')
      ) {
        medium += countVal;
      } else {
        low += countVal;
      }
    }

    const total = high + medium + low;
    const highPct = total > 0 ? Math.round((high / total) * 100) : 0;
    const medPct = total > 0 ? Math.round((medium / total) * 100) : 0;
    const lowPct = total > 0 ? Math.max(0, 100 - highPct - medPct) : 0;

    return {
      high,
      medium,
      low,
      total,
      highPct,
      medPct,
      lowPct,
    };
  });

  ngOnInit(): void {
    if (isPlatformBrowser(this.platformId)) {
      this.loadAllData();

      this.realtimeService.loanMarketingUpdates$
        .pipe(takeUntil(this.destroy$))
        .subscribe(() => {
          this.loadAllData();
        });
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
        if (res.data) {
          this.stats.set(res.data);
        }
        this.isLoadingStats.set(false);
        this.cdr.markForCheck();
      },
      error: (err: any) => {
        console.error('Failed to load marketing dashboard stats:', err);
        this.toastService.error(
          err?.error?.message || 'Gagal memuat statistik dashboard marketing.'
        );
        this.isLoadingStats.set(false);
        this.cdr.markForCheck();
      },
    });
  }

  loadRecentWorklist(): void {
    this.isLoadingWorklist.set(true);
    this.loanService
      .findAllPaginated({
        page: 0,
        size: 5,
        status: 'MENUNGGU_REVIEW',
      })
      .subscribe({
        next: (res: any) => {
          const rawItems: MarketingPengajuanItemResponse[] = res?.content || [];
          this.recentPengajuan.set(rawItems);
          this.isLoadingWorklist.set(false);
          this.cdr.markForCheck();

          // If any items are missing skorKredit, fetch their individual detail to get real score from backend
          if (rawItems.length > 0) {
            const needsScoreEnrichment = rawItems.some((it) => this.getScore(it) === 0);
            if (needsScoreEnrichment) {
              const enrichObservables = rawItems.map((item) => {
                const id = item.id || item.pengajuanId;
                if (!id) return of(item);
                if (this.getScore(item) > 0) return of(item);

                return this.loanService.getDetail(id).pipe(
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
          }
        },
        error: (err: any) => {
          console.warn('Failed to load recent worklist pengajuan:', err);
          this.recentPengajuan.set([]);
          this.isLoadingWorklist.set(false);
          this.cdr.markForCheck();
        },
      });
  }

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

  formatDate(dateStr?: string | null): string {
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

  getStatusBadgeVariant(status?: string, hasilReview?: string): BadgeVariant {
    const label = this.getStatusLabel(status, hasilReview);
    if (label === 'Disetujui Marketing') return 'success';
    if (label === 'Ditolak Marketing') return 'error';
    if (label === 'Perlu Revisi' || label === 'Menunggu Review') return 'warning';
    return 'neutral';
  }

  getStatusLabel(status?: string, hasilReview?: string): string {
    const s = (status || '').toUpperCase();
    const h = (hasilReview || '').toUpperCase();

    if (
      h === 'DISETUJUI' ||
      s === 'DISETUJUI' ||
      s === 'SELESAI_DIREVIEW' ||
      s === 'DICAIRKAN' ||
      s === 'DISBURSED' ||
      s.includes('BM') ||
      s.includes('CAIR') ||
      s.includes('BACKOFFICE')
    ) {
      return 'Disetujui Marketing';
    }
    if (h === 'DITOLAK' || s === 'DITOLAK') {
      return 'Ditolak Marketing';
    }
    if (
      h === 'DOKUMEN_DIREVISI' ||
      s === 'DOKUMEN_DIREVISI' ||
      h === 'PERLU_REVISI' ||
      s === 'PERLU_REVISI'
    ) {
      return 'Perlu Revisi';
    }
    if (s === 'MENUNGGU_REVIEW' || s === 'PENDING') {
      return 'Menunggu Review';
    }
    return 'Menunggu Review';
  }

  getBarHeightPercent(count: number): number {
    const max = this.maxWeeklyCount();
    if (max <= 0) return 10;
    const pct = Math.round((count / max) * 100);
    return Math.max(pct, 12); // Minimum visible height
  }

  navigateToDetail(id?: string): void {
    if (id) {
      this.router.navigate(['/pengajuan-pinjaman/detail', id]);
    }
  }

  navigateToList(status?: string): void {
    if (status) {
      this.router.navigate(['/pengajuan-pinjaman'], {
        queryParams: { status },
      });
    } else {
      this.router.navigate(['/pengajuan-pinjaman']);
    }
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }
}
