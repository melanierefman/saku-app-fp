import {
  Component,
  OnInit,
  OnDestroy,
  signal,
  inject,
  ChangeDetectorRef,
  PLATFORM_ID,
} from '@angular/core';
import { CommonModule, isPlatformBrowser } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterModule, ActivatedRoute } from '@angular/router';
import { forkJoin, of, map, catchError, switchMap, Observable, Subject, takeUntil } from 'rxjs';
import {
  TableComponent,
  TableColumn,
  TableCellDirective,
  PaginationComponent,
  BadgeComponent,
  BadgeVariant,
  InputComponent,
  DropdownComponent,
  DropdownOption,
  DatePickerComponent,
  ToastService,
} from '../../../../shared/components';
import {
  BranchManagerApprovalService,
  BranchManagerPengajuanItemResponse,
  RealTimeService,
  BranchManagerDashboardService,
  BranchManagerDashboardStats,
} from '../../../../core';
import {
  LucideSearch,
  LucideX,
  LucideEye,
  LucideClock,
  LucideAlertCircle,
  LucideCheckCircle2,
  LucideXCircle,
} from '@lucide/angular';

@Component({
  selector: 'app-persetujuan-pinjaman-list',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    RouterModule,
    TableComponent,
    TableCellDirective,
    PaginationComponent,
    BadgeComponent,
    InputComponent,
    DropdownComponent,
    DatePickerComponent,
    LucideSearch,
    LucideX,
    LucideEye,
    LucideClock,
    LucideAlertCircle,
    LucideCheckCircle2,
    LucideXCircle,
  ],
  templateUrl: './persetujuan-pinjaman-list.component.html',
  styleUrl: './persetujuan-pinjaman-list.component.css',
})
export class PersetujuanPinjamanListComponent implements OnInit, OnDestroy {
  private bmService = inject(BranchManagerApprovalService);
  private dashboardService = inject(BranchManagerDashboardService);
  private realtimeService = inject(RealTimeService);
  private toastService = inject(ToastService);
  private router = inject(Router);
  private route = inject(ActivatedRoute);
  private platformId = inject(PLATFORM_ID);
  private cdr = inject(ChangeDetectorRef);
  private destroy$ = new Subject<void>();

  // Stats Signal
  stats = signal<BranchManagerDashboardStats | null>(null);

  // Table Configuration
  readonly columns: TableColumn[] = [
    {
      key: 'noPengajuan',
      header: 'No. Pengajuan',
      sortable: true,
      width: '180px',
      sticky: 'left',
      headerClass: 'border-r border-[#E5E7EB]',
      cellClass: 'border-r border-[#E5E7EB]',
    },
    { key: 'customer', header: 'Nama Customer', sortable: true, minWidth: '180px' },
    { key: 'jumlah', header: 'Jumlah Pinjaman', sortable: true, minWidth: '160px' },
    { key: 'tenor', header: 'Tenor', sortable: true, width: '100px' },
    { key: 'cabang', header: 'Cabang', sortable: true, minWidth: '160px' },
    { key: 'skorKelayakan', header: 'Skor Kelayakan', sortable: true, width: '160px' },
    { key: 'tanggalReviewMarketing', header: 'Tanggal Review Marketing', sortable: true, width: '180px' },
    { key: 'tanggalPersetujuan', header: 'Tanggal Persetujuan', sortable: true, width: '180px' },
    { key: 'status', header: 'Status Keputusan', sortable: true, width: '170px' },
    { key: 'actions', header: 'Aksi', sortable: false, width: '80px', align: 'center' },
  ];

  readonly statusOptions: DropdownOption[] = [
    { value: 'MENUNGGU_PERSETUJUAN', label: 'Menunggu Persetujuan' },
    { value: 'DISETUJUI', label: 'Disetujui' },
    { value: 'DITOLAK', label: 'Ditolak' },
    { value: 'PERLU_REVISI', label: 'Perlu Revisi' },
  ];

  readonly skorOptions: DropdownOption[] = [
    { value: 'TINGGI', label: 'Tinggi (≥ 75)' },
    { value: 'SEDANG', label: 'Sedang (60 - 74)' },
    { value: 'RENDAH', label: 'Rendah (< 60)' },
    { value: 'BELUM_DINILAI', label: 'Belum Dinilai' },
  ];

  // Signals
  items = signal<BranchManagerPengajuanItemResponse[]>([]);
  isLoading = signal<boolean>(false);
  totalElements = signal<number>(0);
  totalPages = signal<number>(1);
  currentPage = signal<number>(1);
  pageSize = signal<number>(10);

  // Filters & Sorting
  searchQuery = signal<string>('');
  selectedStatus = signal<string>('');
  selectedSkor = signal<string>('');
  selectedTanggalReview = signal<Date | null>(null);
  selectedTanggalPersetujuan = signal<Date | null>(null);
  sortKey = signal<string>('tanggalReviewMarketing');
  sortDirection = signal<'asc' | 'desc'>('desc');
  private searchDebounceTimer?: any;

  ngOnInit(): void {
    if (isPlatformBrowser(this.platformId)) {
      this.loadStats();

      this.route.queryParams.subscribe((params) => {
        const statusParam = params['status'] ?? '';
        this.selectedStatus.set(statusParam);
        this.currentPage.set(1);
        this.loadData();
      });

      this.realtimeService.loanBmUpdates$
        .pipe(takeUntil(this.destroy$))
        .subscribe((event) => {
          this.toastService.info(
            event.message || 'Terdapat pengajuan pinjaman yang menunggu persetujuan Anda.'
          );
          this.loadStats();
          this.loadData();
        });
    }
  }

  loadStats(): void {
    this.dashboardService.getDashboardStats().subscribe({
      next: (res) => {
        if (res.data) {
          this.stats.set(res.data);
          this.cdr.markForCheck();
        }
      },
      error: (err) => {
        console.error('Error fetching BM loan stats:', err);
      },
    });
  }

  toggleStatusFilter(status: string): void {
    if (this.selectedStatus() === status) {
      this.selectedStatus.set('');
    } else {
      this.selectedStatus.set(status);
    }
    this.currentPage.set(1);
    this.loadData();
  }

  loadData(): void {
    this.isLoading.set(true);

    const tglReviewYMD = this.formatDateToYMD(this.selectedTanggalReview());
    const tglPersetujuanYMD = this.formatDateToYMD(this.selectedTanggalPersetujuan());
    const apiPage = Math.max(0, this.currentPage() - 1);

    const params = {
      page: apiPage,
      size: this.pageSize(),
      search: this.searchQuery().trim() || undefined,
      status: this.selectedStatus() || undefined,
      tanggalReviewMarketing: tglReviewYMD,
      tanggalPersetujuan: tglPersetujuanYMD,
    };

    this.bmService.findAllPaginated(params).subscribe({
      next: (res) => {
        let contentList = res?.content || [];
        const total = res?.totalElements ?? contentList.length;

        // Client-side date filter refinement
        if (tglReviewYMD) {
          contentList = contentList.filter((item: any) => {
            const itemDate =
              item.tanggalReviewMarketing ||
              item.tanggalReviewTerakhir ||
              item.tanggalReview;
            return itemDate && itemDate.startsWith(tglReviewYMD);
          });
        }

        if (tglPersetujuanYMD) {
          contentList = contentList.filter((item: any) => {
            const itemDate =
              item.tanggalPersetujuan ||
              item.tanggalPersetujuanBM ||
              item.tanggalPersetujuanTerakhir;
            return itemDate && itemDate.startsWith(tglPersetujuanYMD);
          });
        }

        // Client-side Skor filter refinement
        const skorFilter = this.selectedSkor();
        if (skorFilter) {
          contentList = contentList.filter((item: any) => {
            const s = this.getScore(item);
            if (skorFilter === 'TINGGI') return s >= 75;
            if (skorFilter === 'SEDANG') return s >= 60 && s < 75;
            if (skorFilter === 'RENDAH') return s > 0 && s < 60;
            if (skorFilter === 'BELUM_DINILAI') return s === 0;
            return true;
          });
        }

        // If backend returned unpaginated full list, slice client-side
        if (contentList.length > this.pageSize()) {
          const startIndex = (this.currentPage() - 1) * this.pageSize();
          const pagedList = contentList.slice(startIndex, startIndex + this.pageSize());
          this.totalElements.set(contentList.length);
          this.totalPages.set(Math.ceil(contentList.length / this.pageSize()));
          this.items.set(pagedList);
        } else {
          this.totalElements.set(total);
          this.totalPages.set(Math.max(1, Math.ceil(total / this.pageSize())));
          this.items.set(contentList);
        }

        this.applySorting();
        this.isLoading.set(false);
        this.cdr.detectChanges();

        // Background non-blocking enrichment for visible page items
        this.enrichVisibleScoresAsync();
      },
      error: (err) => {
        console.error('Failed to load BM persetujuan list:', err);
        this.items.set([]);
        this.totalElements.set(0);
        this.totalPages.set(1);
        this.isLoading.set(false);
        this.toastService.error('Gagal memuat daftar persetujuan pinjaman');
        this.cdr.detectChanges();
      },
    });
  }

  private enrichVisibleScoresAsync(): void {
    const currentList = this.items();
    const itemsNeedingScore = currentList.filter(
      (item) => (item.pengajuanId || item.id) && this.getScore(item) === 0
    );

    if (itemsNeedingScore.length === 0) return;

    const observables = itemsNeedingScore.map((item) => {
      const id = (item.pengajuanId || item.id)!;
      return this.bmService.getDetail(id).pipe(
        map((detail) => {
          if (!detail) return null;
          const realScore =
            detail.skorKredit ??
            detail.skor ??
            (detail as any).scoring?.skorKredit ??
            (detail as any).scoring?.skor ??
            (detail as any).customer?.skorKredit ??
            (detail as any).customer?.skor;

          return {
            id,
            skorKredit: realScore !== undefined && realScore !== null ? Number(realScore) : undefined,
            statusScoring: detail.statusScoring || (item as any).statusScoring,
          };
        }),
        catchError(() => of(null))
      );
    });

    forkJoin(observables).subscribe({
      next: (results) => {
        const scoreMap = new Map<string, any>();
        results.forEach((r) => {
          if (r && r.id) scoreMap.set(r.id, r);
        });

        if (scoreMap.size > 0) {
          const updated = this.items().map((item) => {
            const id = item.pengajuanId || item.id;
            if (id && scoreMap.has(id)) {
              const res = scoreMap.get(id);
              return {
                ...item,
                skorKredit: res.skorKredit ?? item.skorKredit,
                skor: res.skorKredit ?? item.skor,
                statusScoring: res.statusScoring || item.statusScoring,
              };
            }
            return item;
          });

          this.items.set(updated);
          this.applySorting();
          this.cdr.detectChanges();
        }
      },
      error: () => {},
    });
  }

  private applySorting(): void {
    const field = this.sortKey();
    const dir = this.sortDirection();
    if (field && this.items().length > 0) {
      const sorted = [...this.items()].sort((a: any, b: any) => {
        let valA = a[field] ?? '';
        let valB = b[field] ?? '';

        if (field === 'skorKelayakan' || field === 'skorKredit' || field === 'skor') {
          valA = this.getScore(a);
          valB = this.getScore(b);
        }

        let cmp = 0;
        if (typeof valA === 'number' && typeof valB === 'number') {
          cmp = valA - valB;
        } else {
          cmp = String(valA).localeCompare(String(valB));
        }
        return dir === 'asc' ? cmp : -cmp;
      });
      this.items.set(sorted);
    }
  }

  // Filter & Search Handlers
  onSearchChange(query: string): void {
    this.searchQuery.set(query);
    if (this.searchDebounceTimer) {
      clearTimeout(this.searchDebounceTimer);
    }
    this.searchDebounceTimer = setTimeout(() => {
      this.currentPage.set(1);
      this.loadData();
    }, 350);
  }

  onStatusChange(event: DropdownOption | null | string): void {
    if (!event) {
      this.selectedStatus.set('');
    } else if (typeof event === 'object' && 'value' in event) {
      this.selectedStatus.set(event.value || '');
    } else {
      this.selectedStatus.set(String(event));
    }
    this.currentPage.set(1);
    this.loadData();
  }

  onSkorChange(event: DropdownOption | null | string): void {
    if (!event) {
      this.selectedSkor.set('');
    } else if (typeof event === 'object' && 'value' in event) {
      this.selectedSkor.set(event.value || '');
    } else {
      this.selectedSkor.set(String(event));
    }
    this.currentPage.set(1);
    this.loadData();
  }

  onTanggalReviewChange(date: any): void {
    this.selectedTanggalReview.set(
      date instanceof Date ? date : date ? new Date(date) : null
    );
    this.currentPage.set(1);
    this.loadData();
  }

  onTanggalPersetujuanChange(date: any): void {
    this.selectedTanggalPersetujuan.set(
      date instanceof Date ? date : date ? new Date(date) : null
    );
    this.currentPage.set(1);
    this.loadData();
  }

  private formatDateToYMD(date: Date | null): string | undefined {
    if (!date) return undefined;
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    return `${year}-${month}-${day}`;
  }

  hasActiveFilters(): boolean {
    return !!(
      this.searchQuery() ||
      this.selectedStatus() ||
      this.selectedSkor() ||
      this.selectedTanggalReview() ||
      this.selectedTanggalPersetujuan()
    );
  }

  clearFilters(): void {
    this.searchQuery.set('');
    this.selectedStatus.set('');
    this.selectedSkor.set('');
    this.selectedTanggalReview.set(null);
    this.selectedTanggalPersetujuan.set(null);
    this.currentPage.set(1);
    this.loadData();
  }

  onSortChange(event: { key: string; direction: 'asc' | 'desc' }): void {
    this.sortKey.set(event.key);
    this.sortDirection.set(event.direction);
    this.applySorting();
  }

  onPageChange(page: number): void {
    this.currentPage.set(page);
    this.loadData();
  }

  navigateToDetail(row: BranchManagerPengajuanItemResponse): void {
    const id = row.pengajuanId || row.id;
    if (id) {
      this.router.navigate(['/persetujuan-pinjaman/detail', id]);
    }
  }

  // Formatters & UI Helpers
  getCabangName(row: BranchManagerPengajuanItemResponse): string {
    if (!row) return '-';
    if (typeof row.cabang === 'object' && row.cabang?.nama) return row.cabang.nama;
    if (typeof row.cabang === 'string') return row.cabang;
    if (row.namaCabang) return row.namaCabang;
    if (row.cabangNama) return row.cabangNama;
    return 'Kantor Cabang';
  }

  formatCurrency(value: number | null | undefined): string {
    if (value === null || value === undefined || isNaN(value)) return 'Rp 0';
    return 'Rp ' + new Intl.NumberFormat('id-ID').format(value);
  }

  formatDate(dateStr: string | null | undefined): string {
    if (!dateStr) return '-';
    try {
      const date = new Date(dateStr);
      if (isNaN(date.getTime())) return dateStr;
      return date.toLocaleDateString('id-ID', {
        day: '2-digit',
        month: 'short',
        year: 'numeric',
        hour: '2-digit',
        minute: '2-digit',
      });
    } catch {
      return dateStr;
    }
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
      s === 'BERHASIL'
    ) {
      return 'Disetujui BM';
    }
    if (h === 'DITOLAK' || s === 'DITOLAK') {
      return 'Ditolak BM';
    }
    return 'Menunggu Persetujuan BM';
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

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }
}
