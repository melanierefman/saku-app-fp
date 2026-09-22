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
  ModalComponent,
  ToastService,
} from '../../../../shared/components';
import {
  PencairanService,
  PencairanItem,
  PencairanRequest,
  RealTimeService,
} from '../../../../core';
import {
  LucideSearch,
  LucideX,
  LucideEye,
} from '@lucide/angular';

@Component({
  selector: 'app-pencairan-list',
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
    ModalComponent,
    LucideSearch,
    LucideX,
    LucideEye,
  ],
  templateUrl: './pencairan-list.component.html',
  styleUrl: './pencairan-list.component.css',
})
export class PencairanListComponent implements OnInit, OnDestroy {
  private pencairanService = inject(PencairanService);
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
      key: 'noPengajuan',
      header: 'No. Pengajuan',
      sortable: true,
      minWidth: '170px',
      sticky: 'left',
      headerClass: 'border-r border-[#E5E7EB]',
      cellClass: 'border-r border-[#E5E7EB]',
    },
    { key: 'namaCustomer', header: 'Nama Customer', sortable: true, minWidth: '180px' },
    { key: 'jumlahPinjaman', header: 'Jumlah Pinjaman', sortable: true, width: '160px', align: 'right' },
    { key: 'biayaAdmin', header: 'Biaya Admin', sortable: true, width: '130px', align: 'right' },
    { key: 'jumlahPencairan', header: 'Total Cair Bersih', sortable: true, width: '170px', align: 'right' },
    { key: 'tenorBulan', header: 'Tenor', sortable: true, width: '100px', align: 'center' },
    { key: 'tanggalDisetujuiBM', header: 'Tanggal Persetujuan', sortable: true, width: '180px' },
    { key: 'tanggalPencairan', header: 'Tanggal Pencairan', sortable: true, width: '180px' },
    { key: 'statusPencairan', header: 'Status Pencairan', sortable: true, width: '170px' },
    { key: 'actions', header: 'Aksi', sortable: false, width: '80px', align: 'center' },
  ];

  readonly statusOptions: DropdownOption[] = [
    { value: '', label: 'Semua Status' },
    { value: 'MENUNGGU_PENCAIRAN', label: 'Menunggu Pencairan (Belum Cair)' },
    { value: 'DICAIRKAN', label: 'Sudah Dicairkan (Telah Cair)' },
  ];

  // Signals
  items = signal<PencairanItem[]>([]);
  isLoading = signal<boolean>(false);
  totalElements = signal<number>(0);
  totalPages = signal<number>(1);
  currentPage = signal<number>(1);
  pageSize = signal<number>(10);

  // Filters & Sorting
  searchQuery = signal<string>('');
  selectedStatus = signal<string>('');
  selectedTanggalDisetujui = signal<Date | null>(null);
  selectedTanggalPencairan = signal<Date | null>(null);
  sortKey = signal<string>('noPengajuan');
  sortDirection = signal<'asc' | 'desc'>('desc');

  // Quick Disburse Modal
  isQuickCairModalOpen = signal<boolean>(false);
  isSubmittingQuick = signal<boolean>(false);
  selectedItemForDisburse = signal<PencairanItem | null>(null);
  quickCatatan = signal<string>('');

  private searchDebounceTimer?: any;

  ngOnInit(): void {
    if (isPlatformBrowser(this.platformId)) {
      this.route.queryParams.subscribe((params) => {
        const statusParam = params['status'] ?? '';
        this.selectedStatus.set(statusParam);
        this.currentPage.set(1);
        this.loadData();
      });

      this.realtimeService.loanPencairanUpdates$
        .pipe(takeUntil(this.destroy$))
        .subscribe((event) => {
          this.toastService.info(
            event.message || 'Terdapat pinjaman baru yang siap dicairkan.'
          );
          this.loadData();
        });
    }
  }

  loadData(): void {
    this.isLoading.set(true);

    const tglDisetujuiYMD = this.formatDateToYMD(this.selectedTanggalDisetujui());
    const tglPencairanYMD = this.formatDateToYMD(this.selectedTanggalPencairan());
    const apiPage = Math.max(0, this.currentPage() - 1);

    const params = {
      page: apiPage,
      size: this.pageSize(),
      search: this.searchQuery().trim() || undefined,
      status: this.selectedStatus() || undefined,
      tanggalDisetujuiBM: tglDisetujuiYMD,
      tanggalPencairan: tglPencairanYMD,
    };

    this.pencairanService.findAllPaginated(params).subscribe({
      next: (res) => {
        let contentList = res?.content || [];
        const total = res?.totalElements ?? contentList.length;

        // Client-side fallback filter
        if (tglDisetujuiYMD) {
          contentList = contentList.filter((item) => {
            const itemDate = item.tanggalDisetujuiBM;
            return itemDate ? itemDate.startsWith(tglDisetujuiYMD) : false;
          });
        }

        if (tglPencairanYMD) {
          contentList = contentList.filter((item) => {
            const itemDate = item.tanggalPencairan;
            return itemDate ? itemDate.startsWith(tglPencairanYMD) : false;
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
          this.totalPages.set(res?.totalPages ?? Math.max(1, Math.ceil(total / this.pageSize())));
          this.items.set(contentList);
        }

        // Client sort if needed
        const field = this.sortKey();
        const dir = this.sortDirection();
        if (field && this.items().length > 0) {
          const sorted = [...this.items()].sort((a: any, b: any) => {
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
          this.items.set(sorted);
        }

        this.isLoading.set(false);
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error('Failed to load pencairan list:', err);
        this.items.set([]);
        this.totalElements.set(0);
        this.totalPages.set(1);
        this.isLoading.set(false);
        this.cdr.detectChanges();
      },
    });
  }

  // Quick Filter Tabs
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

  onTanggalDisetujuiChange(date: any): void {
    this.selectedTanggalDisetujui.set(
      date instanceof Date ? date : date ? new Date(date) : null
    );
    this.currentPage.set(1);
    this.loadData();
  }

  onTanggalPencairanChange(date: any): void {
    this.selectedTanggalPencairan.set(
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
      this.selectedTanggalDisetujui() ||
      this.selectedTanggalPencairan()
    );
  }

  clearFilters(): void {
    this.searchQuery.set('');
    this.selectedStatus.set('');
    this.selectedTanggalDisetujui.set(null);
    this.selectedTanggalPencairan.set(null);
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

  navigateToDetail(row: PencairanItem): void {
    const id = row.pengajuanId || row.id;
    if (id) {
      this.router.navigate(['/pencairan/detail', id]);
    }
  }

  // Quick Cairkan Actions from List
  openQuickCairModal(row: PencairanItem, event?: MouseEvent): void {
    if (event) {
      event.stopPropagation();
    }
    this.selectedItemForDisburse.set(row);
    this.quickCatatan.set(
      `Dana sebesar ${this.formatCurrency(row.jumlahPencairan || row.jumlahPencairanBersih)} berhasil ditransfer ke rekening ${row.namaBank} an. ${row.namaRekening || row.namaCustomer}`
    );
    this.isQuickCairModalOpen.set(true);
  }

  closeQuickCairModal(): void {
    this.isQuickCairModalOpen.set(false);
    this.selectedItemForDisburse.set(null);
    this.quickCatatan.set('');
  }

  confirmQuickCair(): void {
    const item = this.selectedItemForDisburse();
    if (!item) return;

    const id = item.pengajuanId || item.id;
    if (!id) {
      this.toastService.error('ID Pengajuan tidak ditemukan');
      return;
    }

    const payload: PencairanRequest = {
      catatan: this.quickCatatan().trim() || 'Pencairan dana telah berhasil ditransfer ke rekening customer',
      jumlahPencairan: item.jumlahPencairan || item.jumlahPencairanBersih,
    };

    this.isSubmittingQuick.set(true);

    this.pencairanService.cairkan(id, payload).subscribe({
      next: () => {
        this.isSubmittingQuick.set(false);
        this.closeQuickCairModal();
        this.toastService.success(
          `Pinjaman ${this.formatNomorPengajuan(item.noPengajuan)} berhasil dicairkan!`
        );
        this.loadData();
      },
      error: (err) => {
        this.isSubmittingQuick.set(false);
        console.error('Failed to disburse loan:', err);
        const errMsg =
          err?.error?.message || err?.message || 'Gagal memproses pencairan dana';
        this.toastService.error(errMsg);
        this.cdr.detectChanges();
      },
    });
  }

  isAlreadyDisbursed(status?: string): boolean {
    const s = (status || '').toUpperCase();
    return s === 'DICAIRKAN' || s === 'BERHASIL' || s === 'CAIR' || s === 'DISBURSED';
  }

  // UI Formatters
  getStatusBadgeVariant(status?: string): BadgeVariant {
    const s = (status || '').toUpperCase();
    if (s === 'DICAIRKAN' || s === 'BERHASIL' || s === 'CAIR' || s === 'DISBURSED') {
      return 'success';
    }
    return 'warning';
  }

  getStatusLabel(status?: string): string {
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

  formatCurrency(val?: number | null): string {
    if (val === null || val === undefined || isNaN(val)) return 'Rp 0';
    return 'Rp ' + new Intl.NumberFormat('id-ID').format(val);
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

  formatNomorPengajuan(no?: string): string {
    if (!no) return '-';
    if (no.toUpperCase()) return no;
    return `${no}`;
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }
}
