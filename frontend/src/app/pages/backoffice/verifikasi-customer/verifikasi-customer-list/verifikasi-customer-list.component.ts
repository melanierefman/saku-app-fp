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
import { Subject, takeUntil } from 'rxjs';
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
  VerifikasiCustomerService,
  VerifikasiCustomerItem,
  RealTimeService,
} from '../../../../core';
import {
  LucideSearch,
  LucideX,
  LucideEye,
} from '@lucide/angular';

@Component({
  selector: 'app-verifikasi-customer-list',
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
  templateUrl: './verifikasi-customer-list.component.html',
  styleUrl: './verifikasi-customer-list.component.css',
})
export class VerifikasiCustomerListComponent implements OnInit, OnDestroy {
  private verifikasiService = inject(VerifikasiCustomerService);
  private realtimeService = inject(RealTimeService);
  private toastService = inject(ToastService);
  private router = inject(Router);
  private route = inject(ActivatedRoute);
  private platformId = inject(PLATFORM_ID);
  private cdr = inject(ChangeDetectorRef);
  private destroy$ = new Subject<void>();

  // Table Configuration
  readonly columns: TableColumn[] = [
    {
      key: 'namaCustomer',
      header: 'Nama Customer',
      sortable: true,
      minWidth: '180px',
      sticky: 'left',
      headerClass: 'border-r border-[#E5E7EB]',
      cellClass: 'border-r border-[#E5E7EB]',
    },
    { key: 'nik', header: 'NIK', sortable: true, width: '160px' },
    { key: 'email', header: 'Email', sortable: true, minWidth: '180px' },
    { key: 'noHp', header: 'No. Handphone', sortable: true, width: '150px' },
    { key: 'tanggalRegister', header: 'Tanggal Register', sortable: true, width: '180px' },
    { key: 'tanggalVerifikasi', header: 'Tanggal Verifikasi', sortable: true, width: '180px' },
    { key: 'statusVerifikasi', header: 'Status Verifikasi', sortable: true, width: '180px' },
    { key: 'actions', header: 'Aksi', sortable: false, width: '80px', align: 'center' },
  ];

  readonly statusOptions: DropdownOption[] = [
    { value: '', label: 'Semua Status' },
    { value: 'PENDING', label: 'Menunggu Verifikasi' },
    { value: 'APPROVED', label: 'Disetujui' },
    { value: 'PERLU_REVISI', label: 'Perlu Revisi' },
    { value: 'REJECTED', label: 'Ditolak' },
  ];

  // Signals
  items = signal<VerifikasiCustomerItem[]>([]);
  isLoading = signal<boolean>(false);
  totalElements = signal<number>(0);
  totalPages = signal<number>(1);
  currentPage = signal<number>(1);
  pageSize = signal<number>(10);

  // Filters & Sorting
  searchQuery = signal<string>('');
  selectedStatus = signal<string>('');
  selectedTanggalRegister = signal<Date | null>(null);
  selectedTanggalVerifikasi = signal<Date | null>(null);
  sortKey = signal<string>('tanggalRegister');
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

      this.realtimeService.kycUpdates$
        .pipe(takeUntil(this.destroy$))
        .subscribe((event) => {
          this.toastService.info(
            event.message || 'Terdapat data KYC nasabah baru/revisi.'
          );
          this.loadData();
        });
    }
  }

  loadData(): void {
    this.isLoading.set(true);

    const tglRegisterYMD = this.formatDateToYMD(this.selectedTanggalRegister());
    const tglVerifikasiYMD = this.formatDateToYMD(this.selectedTanggalVerifikasi());
    const apiPage = Math.max(0, this.currentPage() - 1);

    const params = {
      page: apiPage,
      size: this.pageSize(),
      search: this.searchQuery().trim() || undefined,
      status: this.selectedStatus() || undefined,
      tanggalRegister: tglRegisterYMD,
      tanggalVerifikasi: tglVerifikasiYMD,
    };

    this.verifikasiService.findAllPaginated(params).subscribe({
      next: (res) => {
        let contentList = res?.content || [];
        const total = res?.totalElements ?? contentList.length;

        // Client-side fallback filter
        if (tglRegisterYMD) {
          contentList = contentList.filter((item) => {
            const itemDate = this.extractRawRegisterDate(item);
            return itemDate ? itemDate.startsWith(tglRegisterYMD) : false;
          });
        }

        if (tglVerifikasiYMD) {
          contentList = contentList.filter((item) => {
            const itemDate = item.tanggalVerifikasi;
            return itemDate ? itemDate.startsWith(tglVerifikasiYMD) : false;
          });
        }

        // If backend returned unpaginated full list (content length > pageSize), slice client-side
        if (contentList.length > this.pageSize()) {
          const startIndex = (this.currentPage() - 1) * this.pageSize();
          const pagedList = contentList.slice(startIndex, startIndex + this.pageSize());
          this.totalElements.set(contentList.length);
          this.totalPages.set(Math.ceil(contentList.length / this.pageSize()));
          this.items.set(pagedList);
        } else {
          this.totalElements.set(total);
          this.totalPages.set(res?.totalPages ?? Math.max(1, Math.ceil(total / this.pageSize())));
          this.items.set(contentList);
        }

        // Client sort if needed
        const field = this.sortKey();
        const dir = this.sortDirection();
        if (field && this.items().length > 0) {
          const sorted = [...this.items()].sort((a: any, b: any) => {
            let valA = a[field] ?? '';
            let valB = b[field] ?? '';
            if (field === 'tanggalRegister') {
              valA = this.extractRawRegisterDate(a) || '';
              valB = this.extractRawRegisterDate(b) || '';
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

        this.isLoading.set(false);
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error('Failed to load verification customer list:', err);
        this.items.set([]);
        this.totalElements.set(0);
        this.totalPages.set(1);
        this.isLoading.set(false);
        this.cdr.detectChanges();
      },
    });
  }

  setQuickStatusFilter(status: string): void {
    this.selectedStatus.set(status);
    this.currentPage.set(1);
    this.loadData();
  }

  onSearchChange(query: string): void {
    this.searchQuery.set(query);
    if (this.searchDebounceTimer) {
      clearTimeout(this.searchDebounceTimer);
    }
    this.searchDebounceTimer = setTimeout(() => {
      this.currentPage.set(1);
      this.loadData();
    }, 400);
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

  onTanggalRegisterChange(date: any): void {
    this.selectedTanggalRegister.set(
      date instanceof Date ? date : date ? new Date(date) : null
    );
    this.currentPage.set(1);
    this.loadData();
  }

  onTanggalVerifikasiChange(date: any): void {
    this.selectedTanggalVerifikasi.set(
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
      this.selectedTanggalRegister() ||
      this.selectedTanggalVerifikasi()
    );
  }

  clearFilters(): void {
    this.searchQuery.set('');
    this.selectedStatus.set('');
    this.selectedTanggalRegister.set(null);
    this.selectedTanggalVerifikasi.set(null);
    this.currentPage.set(1);
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

  navigateToDetail(row: VerifikasiCustomerItem): void {
    const id = row.customerId || row.id;
    if (id) {
      this.router.navigate(['/verifikasi-customer/detail', id]);
    }
  }

  // UI Formatters
  getStatusBadgeVariant(status?: string): BadgeVariant {
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

  getStatusLabel(status?: string): string {
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

  extractRawRegisterDate(row?: any): string | undefined {
    if (!row) return undefined;
    return (
      row.tanggalRegister ||
      row.tanggalRegistrasi ||
      row.tanggalDaftar ||
      row.createdDate ||
      row.createdAt ||
      row.registrationDate ||
      row.registeredAt ||
      row.tglRegister ||
      row.tglRegistrasi ||
      row.tglDaftar ||
      row.tanggalPengajuan ||
      row.tanggalVerifikasi ||
      undefined
    );
  }

  getTanggalRegister(row?: any): string {
    const raw = this.extractRawRegisterDate(row);
    return this.formatDate(raw);
  }

  formatDate(dateStr?: string | null): string {
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

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }
}
