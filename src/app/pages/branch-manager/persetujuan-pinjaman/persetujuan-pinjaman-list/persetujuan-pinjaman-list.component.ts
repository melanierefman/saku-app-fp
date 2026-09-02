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
import { Router, RouterModule } from '@angular/router';
import {
  BreadcrumbsComponent,
  BreadcrumbItem,
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
} from '../../../../core';
import {
  LucideSearch,
  LucideX,
  LucideEye,
} from '@lucide/angular';

@Component({
  selector: 'app-persetujuan-pinjaman-list',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    RouterModule,
    BreadcrumbsComponent,
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
  templateUrl: './persetujuan-pinjaman-list.component.html',
  styleUrl: './persetujuan-pinjaman-list.component.css',
})
export class PersetujuanPinjamanListComponent implements OnInit {
  private bmService = inject(BranchManagerApprovalService);
  private toastService = inject(ToastService);
  private router = inject(Router);
  private platformId = inject(PLATFORM_ID);
  private cdr = inject(ChangeDetectorRef);

  // Breadcrumbs
  readonly breadcrumbs: BreadcrumbItem[] = [
    { label: 'Dashboard', url: '/dashboard' },
    { label: 'Persetujuan Pinjaman', active: true },
  ];

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
    { key: 'customer', header: 'Customer', sortable: true, minWidth: '180px' },
    { key: 'jumlah', header: 'Jumlah Pinjaman', sortable: true, minWidth: '160px' },
    { key: 'tenor', header: 'Tenor', sortable: true, width: '100px' },
    { key: 'cabang', header: 'Cabang', sortable: true, minWidth: '180px' },
    { key: 'tanggalReviewMarketing', header: 'Tanggal Review', sortable: true, width: '170px' },
    { key: 'tanggalPersetujuan', header: 'Tanggal Persetujuan', sortable: true, width: '170px' },
    { key: 'status', header: 'Status Persetujuan', sortable: true, width: '180px' },
    { key: 'actions', header: 'Aksi', sortable: false, width: '80px', align: 'center' },
  ];

  readonly statusOptions: DropdownOption[] = [
    { value: 'MENUNGGU_PERSETUJUAN', label: 'Menunggu Persetujuan' },
    { value: 'DISETUJUI', label: 'Disetujui' },
    { value: 'DITOLAK', label: 'Ditolak' },
  ];

  // Signals
  items = signal<BranchManagerPengajuanItemResponse[]>([]);
  isLoading = signal<boolean>(false);
  totalElements = signal<number>(0);
  totalPages = signal<number>(1);
  currentPage = signal<number>(0);
  pageSize = signal<number>(10);

  // Filters & Sorting
  searchQuery = signal<string>('');
  selectedStatus = signal<string>('');
  selectedTanggalReview = signal<Date | null>(null);
  selectedTanggalPersetujuan = signal<Date | null>(null);
  sortKey = signal<string>('tanggalReviewMarketing');
  sortDirection = signal<'asc' | 'desc'>('desc');

  ngOnInit(): void {
    if (isPlatformBrowser(this.platformId)) {
      this.loadData();
    }
  }

  loadData(): void {
    this.isLoading.set(true);

    const tglReviewYMD = this.formatDateToYMD(this.selectedTanggalReview());
    const tglPersetujuanYMD = this.formatDateToYMD(this.selectedTanggalPersetujuan());

    const params = {
      page: this.currentPage(),
      size: this.pageSize(),
      search: this.searchQuery() || undefined,
      status: this.selectedStatus() || undefined,
      tanggalReviewMarketing: tglReviewYMD,
      tanggalPersetujuan: tglPersetujuanYMD,
    };

    this.bmService.findAllPaginated(params).subscribe({
      next: (res) => {
        let contentList = res?.content || [];

        // Client-side date filter refinement
        if (tglReviewYMD) {
          contentList = contentList.filter((item: any) => {
            const itemDate =
              item.tanggalReviewMarketing ||
              item.tanggalReviewTerakhir ||
              item.tanggalReview;
            if (!itemDate) return false;
            return itemDate.startsWith(tglReviewYMD);
          });
        }

        if (tglPersetujuanYMD) {
          contentList = contentList.filter((item: any) => {
            const itemDate =
              item.tanggalPersetujuan ||
              item.tanggalPersetujuanBM ||
              item.tanggalPersetujuanTerakhir;
            if (!itemDate) return false;
            return itemDate.startsWith(tglPersetujuanYMD);
          });
        }

        // Client sort
        const field = this.sortKey();
        const dir = this.sortDirection();
        if (field && contentList.length > 0) {
          contentList = [...contentList].sort((a: any, b: any) => {
            const valA = a[field] ?? '';
            const valB = b[field] ?? '';
            let cmp = 0;
            if (typeof valA === 'number' && typeof valB === 'number') {
              cmp = valA - valB;
            } else {
              cmp = String(valA).localeCompare(String(valB));
            }
            return dir === 'asc' ? cmp : -cmp;
          });
        }

        this.items.set(contentList);
        this.totalElements.set(res?.totalElements ?? contentList.length);
        this.totalPages.set(res?.totalPages ?? 1);
        this.isLoading.set(false);
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error('Failed to load BM persetujuan applications:', err);
        this.items.set([]);
        this.totalElements.set(0);
        this.totalPages.set(1);
        this.isLoading.set(false);
        this.toastService.error('Gagal memuat daftar persetujuan pinjaman');
        this.cdr.detectChanges();
      },
    });
  }

  // Filter & Search Handlers
  onSearchChange(query: string): void {
    this.searchQuery.set(query);
    this.currentPage.set(0);
    this.loadData();
  }

  onStatusChange(event: DropdownOption | null | string): void {
    if (!event) {
      this.selectedStatus.set('');
    } else if (typeof event === 'object' && 'value' in event) {
      this.selectedStatus.set(event.value || '');
    } else {
      this.selectedStatus.set(String(event));
    }
    this.currentPage.set(0);
    this.loadData();
  }

  onTanggalReviewChange(date: any): void {
    this.selectedTanggalReview.set(
      date instanceof Date ? date : date ? new Date(date) : null
    );
    this.currentPage.set(0);
    this.loadData();
  }

  onTanggalPersetujuanChange(date: any): void {
    this.selectedTanggalPersetujuan.set(
      date instanceof Date ? date : date ? new Date(date) : null
    );
    this.currentPage.set(0);
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
      this.selectedTanggalReview() ||
      this.selectedTanggalPersetujuan()
    );
  }

  clearFilters(): void {
    this.searchQuery.set('');
    this.selectedStatus.set('');
    this.selectedTanggalReview.set(null);
    this.selectedTanggalPersetujuan.set(null);
    this.currentPage.set(0);
    this.loadData();
  }

  onSortChange(event: { key: string; direction: 'asc' | 'desc' }): void {
    this.sortKey.set(event.key);
    this.sortDirection.set(event.direction);
    this.loadData();
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
}
