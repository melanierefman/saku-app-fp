import {
  Component,
  OnInit,
  signal,
  inject,
  ChangeDetectorRef,
  PLATFORM_ID,
} from '@angular/core';
import { CommonModule, isPlatformBrowser } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterModule, ActivatedRoute } from '@angular/router';
import { forkJoin, of, map, catchError, switchMap, Observable } from 'rxjs';
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
  MarketingLoanService,
  MarketingPengajuanItemResponse,
} from '../../../../core';
import {
  LucideSearch,
  LucideX,
  LucideEye,
} from '@lucide/angular';

@Component({
  selector: 'app-pengajuan-pinjaman-list',
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
  ],
  templateUrl: './pengajuan-pinjaman-list.component.html',
  styleUrl: './pengajuan-pinjaman-list.component.css',
})
export class PengajuanPinjamanListComponent implements OnInit {
  private marketingService = inject(MarketingLoanService);
  private toastService = inject(ToastService);
  private router = inject(Router);
  private route = inject(ActivatedRoute);
  private platformId = inject(PLATFORM_ID);
  private cdr = inject(ChangeDetectorRef);

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
    { key: 'tanggalPengajuan', header: 'Tanggal Pengajuan', sortable: true, width: '160px' },
    { key: 'tanggalReviewTerakhir', header: 'Tanggal Review', sortable: true, width: '160px' },
    { key: 'status', header: 'Status', sortable: true, width: '160px' },
    { key: 'actions', header: 'Aksi', sortable: false, width: '80px', align: 'center' },
  ];

  readonly statusOptions: DropdownOption[] = [
    { value: 'MENUNGGU_REVIEW', label: 'Menunggu Review' },
    { value: 'DOKUMEN_DIREVISI', label: 'Dokumen Direvisi' },
    { value: 'SELESAI_DIREVIEW', label: 'Selesai Direview' },
    { value: 'DISETUJUI', label: 'Disetujui' },
    { value: 'DITOLAK', label: 'Ditolak' },
  ];

  readonly skorOptions: DropdownOption[] = [
    { value: 'TINGGI', label: 'Tinggi (≥ 75)' },
    { value: 'SEDANG', label: 'Sedang (60 - 74)' },
    { value: 'RENDAH', label: 'Rendah (< 60)' },
    { value: 'BELUM_DINILAI', label: 'Belum Dinilai' },
  ];

  // Signals
  items = signal<MarketingPengajuanItemResponse[]>([]);
  isLoading = signal<boolean>(false);
  totalElements = signal<number>(0);
  totalPages = signal<number>(1);
  currentPage = signal<number>(1);
  pageSize = signal<number>(10);

  // Filters & Sorting
  searchQuery = signal<string>('');
  selectedStatus = signal<string>('');
  selectedSkor = signal<string>('');
  selectedTanggalPengajuan = signal<Date | null>(null);
  selectedTanggalReview = signal<Date | null>(null);
  sortKey = signal<string>('tanggalPengajuan');
  sortDirection = signal<'asc' | 'desc'>('desc');

  private searchDebounceTimer?: any;

  ngOnInit(): void {
    if (isPlatformBrowser(this.platformId)) {
      this.route.queryParams.subscribe((params) => {
        const statusParam = params['status'] ?? '';
        this.selectedStatus.set(statusParam);
        this.currentPage.set(1);
        this.loadData();
      });
    }
  }

  loadData(): void {
    this.isLoading.set(true);

    const tglPengajuanYMD = this.formatDateToYMD(this.selectedTanggalPengajuan());
    const tglReviewYMD = this.formatDateToYMD(this.selectedTanggalReview());
    const apiPage = Math.max(0, this.currentPage() - 1);

    const params = {
      page: apiPage,
      size: this.pageSize(),
      search: this.searchQuery().trim() || undefined,
      status: this.selectedStatus() || undefined,
      tanggalPengajuan: tglPengajuanYMD,
      tanggalReview: tglReviewYMD,
    };

    this.marketingService.findAllPaginated(params).subscribe({
      next: (res) => {
        let contentList = res?.content || [];
        const total = res?.totalElements ?? contentList.length;

        // Client-side date filter refinement if API doesn't filter exact date
        if (tglPengajuanYMD) {
          contentList = contentList.filter((item: any) => {
            const itemDate = item.tanggalPengajuan || item.createdDate;
            return itemDate && itemDate.startsWith(tglPengajuanYMD);
          });
        }

        if (tglReviewYMD) {
          contentList = contentList.filter((item: any) => {
            const itemDate = item.tanggalReviewTerakhir || item.tanggalReview;
            return itemDate && itemDate.startsWith(tglReviewYMD);
          });
        }

        // Client-side Skor filter refinement (if already cached or present)
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
        console.error('Failed to load marketing loan applications:', err);
        this.items.set([]);
        this.totalElements.set(0);
        this.totalPages.set(1);
        this.isLoading.set(false);
        this.toastService.error('Gagal memuat daftar pengajuan pinjaman');
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
      return this.marketingService.getDetail(id).pipe(
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

  onTanggalPengajuanChange(date: any): void {
    this.selectedTanggalPengajuan.set(
      date instanceof Date ? date : date ? new Date(date) : null
    );
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
      this.selectedTanggalPengajuan() ||
      this.selectedTanggalReview()
    );
  }

  clearFilters(): void {
    this.searchQuery.set('');
    this.selectedStatus.set('');
    this.selectedSkor.set('');
    this.selectedTanggalPengajuan.set(null);
    this.selectedTanggalReview.set(null);
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

  navigateToDetail(row: MarketingPengajuanItemResponse): void {
    const id = row.pengajuanId || row.id;
    if (id) {
      this.router.navigate(['/pengajuan-pinjaman/detail', id]);
    }
  }

  // Formatters & UI Helpers
  getCabangName(row: MarketingPengajuanItemResponse): string {
    if (!row) return '-';
    if (typeof row.cabang === 'object' && row.cabang?.nama) return row.cabang.nama;
    if (typeof row.cabang === 'string') return row.cabang;
    if (row.cabangNama) return row.cabangNama;
    return 'Kantor Pusat';
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

  formatHumanReadableStatus(status?: string | null): string {
    if (!status || status === '-' || status.trim() === '') return 'Belum Diproses';
    const s = status.toUpperCase().trim();
    if (s === 'MENUNGGU_REVIEW') return 'Menunggu Review';
    if (s === 'SELESAI_DIREVIEW') return 'Selesai Direview';
    if (s === 'DOKUMEN_DIREVISI') return 'Dokumen Direvisi';
    if (s === 'PERLU_REVISI') return 'Perlu Revisi';
    if (s === 'MENUNGGU_PERSETUJUAN') return 'Menunggu Persetujuan';
    if (s === 'DISETUJUI') return 'Disetujui';
    if (s === 'DICAIRKAN') return 'Dicairkan';
    if (s === 'DITOLAK') return 'Ditolak';
    if (s === 'DISBURSED') return 'Telah Cair';
    if (s === 'BERHASIL') return 'Berhasil Cair';
    if (s === 'PENDING') return 'Menunggu';

    return s
      .split('_')
      .map((word) => word.charAt(0).toUpperCase() + word.slice(1).toLowerCase())
      .join(' ');
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
}
