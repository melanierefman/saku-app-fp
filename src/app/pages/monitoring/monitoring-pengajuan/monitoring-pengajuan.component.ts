import {
  Component,
  OnInit,
  inject,
  PLATFORM_ID,
  signal,
  computed,
  ChangeDetectorRef,
} from '@angular/core';
import { CommonModule, isPlatformBrowser } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import {
  BreadcrumbsComponent,
  BreadcrumbItem,
  TableComponent,
  TableCellDirective,
  TableColumn,
  PaginationComponent,
  BadgeComponent,
  BadgeVariant,
  InputComponent,
  DropdownComponent,
  DropdownOption,
  ModalComponent,
  ToastService,
} from '../../../shared/components';
import {
  MarketingPengajuanItemResponse,
  MarketingPengajuanDetailResponse,
  ReviewHistoryItem,
  DokumenPengajuanItem,
  MonitoringPengajuanService,
  Cabang,
  CabangService,
} from '../../../core';
import {
  LucideSearch,
  LucideX,
  LucideEye,
  LucideFileText,
  LucideUser,
  LucideDollarSign,
  LucideSparkles,
  LucideCheckCircle2,
  LucideClock,
} from '@lucide/angular';

@Component({
  selector: 'app-monitoring-pengajuan',
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
    ModalComponent,
    LucideSearch,
    LucideX,
    LucideEye,
    LucideFileText,
    LucideUser,
    LucideDollarSign,
    LucideSparkles,
    LucideCheckCircle2,
    LucideClock,
  ],
  templateUrl: './monitoring-pengajuan.component.html',
  styleUrl: './monitoring-pengajuan.component.css',
})
export class MonitoringPengajuanComponent implements OnInit {
  private monitoringService = inject(MonitoringPengajuanService);
  private cabangService = inject(CabangService);
  private toastService = inject(ToastService);
  private platformId = inject(PLATFORM_ID);
  private cdr = inject(ChangeDetectorRef);

  breadcrumbs: BreadcrumbItem[] = [
    { label: 'Dashboard', url: '/dashboard' },
    { label: 'Monitoring Pengajuan', active: true },
  ];

  columns: TableColumn[] = [
    { key: 'no', header: 'No', width: '60px', align: 'center' },
    { key: 'noPengajuan', header: 'No. Pengajuan', sortable: true },
    { key: 'customer', header: 'Customer', sortable: true },
    { key: 'cabang', header: 'Cabang', sortable: true },
    { key: 'jumlahTenor', header: 'Jumlah & Tenor', sortable: true },
    { key: 'tanggalPengajuan', header: 'Tgl Pengajuan', sortable: true },
    { key: 'status', header: 'Status', width: '160px', sortable: true, align: 'center' },
    { key: 'actions', header: 'Aksi', width: '80px', align: 'center' },
  ];

  // Sorting Signals
  sortKey = signal<string>('');
  sortDirection = signal<'asc' | 'desc' | ''>('');

  // Table Data & Loading
  allPengajuan = signal<MarketingPengajuanItemResponse[]>([]);
  isLoading = signal<boolean>(false);
  branches = signal<Cabang[]>([]);

  // Filters
  searchQuery = signal<string>('');
  selectedStatusFilter = signal<string>('');
  selectedBranchFilter = signal<string>('');
  currentPage = signal<number>(1);
  pageSize = signal<number>(10);

  // Detail Modal
  isDetailModalOpen = signal<boolean>(false);
  selectedDetail = signal<MarketingPengajuanDetailResponse | null>(null);
  isLoadingDetail = signal<boolean>(false);

  readonly statusOptions: DropdownOption[] = [
    { value: 'MENUNGGU_REVIEW', label: 'Menunggu Review' },
    { value: 'SELESAI_DIREVIEW', label: 'Selesai Direview' },
    { value: 'DOKUMEN_DIREVISI', label: 'Dokumen Direvisi' },
    { value: 'DISETUJUI', label: 'Disetujui' },
    { value: 'DITOLAK', label: 'Ditolak' },
    { value: 'DISBURSED', label: 'Telah Cair' },
  ];

  branchOptions = computed<DropdownOption[]>(() =>
    this.branches().map((b) => ({
      value: b.id,
      label: b.nama,
    }))
  );

  filteredPengajuan = computed(() => {
    const list = this.allPengajuan();
    const q = this.searchQuery().trim().toLowerCase();
    const statusVal = this.selectedStatusFilter();
    const branchVal = this.selectedBranchFilter();

    let result = list.filter((item) => {
      // 1. Search Query
      if (q) {
        const no = (item.noPengajuan || item.nomorPengajuan || '').toLowerCase();
        const nama = (item.customer || item.namaNasabah || item.nama || '').toLowerCase();
        const email = (item.email || '').toLowerCase();
        const noHp = (item.noHp || '').toLowerCase();
        const nik = (item.nik || '').toLowerCase();
        if (
          !no.includes(q) &&
          !nama.includes(q) &&
          !email.includes(q) &&
          !noHp.includes(q) &&
          !nik.includes(q)
        ) {
          return false;
        }
      }

      // 2. Status Filter
      if (statusVal) {
        const itemStatus = (item.status || '').toUpperCase();
        const itemHasil = (item.hasilReviewTerakhir || '').toUpperCase();
        if (!itemStatus.includes(statusVal) && !itemHasil.includes(statusVal)) {
          return false;
        }
      }

      // 3. Branch Filter
      if (branchVal) {
        const cId = typeof item.cabang === 'object' ? item.cabang?.id : '';
        const cNama = this.getCabangName(item).toLowerCase();
        const matchBranch = this.branches().find((b) => b.id === branchVal);
        if (matchBranch) {
          if (cId && cId !== branchVal) return false;
          if (cNama !== matchBranch.nama.toLowerCase()) return false;
        }
      }

      return true;
    });

    // Sorting
    const key = this.sortKey();
    const dir = this.sortDirection();

    if (key && dir) {
      result = [...result].sort((a: any, b: any) => {
        let valA = a[key];
        let valB = b[key];

        if (key === 'jumlahTenor') {
          valA = a.jumlah || a.nominalPinjaman || a.nominal || 0;
          valB = b.jumlah || b.nominalPinjaman || b.nominal || 0;
        } else if (key === 'cabang') {
          valA = this.getCabangName(a);
          valB = this.getCabangName(b);
        } else if (key === 'customer') {
          valA = a.customer || a.namaNasabah || a.nama || '';
          valB = b.customer || b.namaNasabah || b.nama || '';
        } else if (key === 'noPengajuan') {
          valA = a.noPengajuan || a.nomorPengajuan || '';
          valB = b.noPengajuan || b.nomorPengajuan || '';
        }

        if (typeof valA === 'string') valA = valA.toLowerCase();
        if (typeof valB === 'string') valB = valB.toLowerCase();

        if (valA < valB) return dir === 'asc' ? -1 : 1;
        if (valA > valB) return dir === 'asc' ? 1 : -1;
        return 0;
      });
    }

    return result;
  });

  totalElements = computed(() => this.filteredPengajuan().length);

  paginatedPengajuan = computed(() => {
    const list = this.filteredPengajuan();
    const page = this.currentPage();
    const size = this.pageSize();
    const start = (page - 1) * size;
    return list.slice(start, start + size);
  });

  hasActiveFilters = computed(() => {
    return !!(
      this.searchQuery().trim() ||
      this.selectedStatusFilter() ||
      this.selectedBranchFilter()
    );
  });

  ngOnInit(): void {
    if (!isPlatformBrowser(this.platformId)) return;
    this.fetchBranches();
    this.fetchPengajuan();
  }

  fetchBranches(): void {
    this.cabangService.getAll().subscribe({
      next: (branches) => {
        this.branches.set(branches || []);
      },
      error: (err) => console.error('Failed to fetch branches:', err),
    });
  }

  fetchPengajuan(): void {
    this.isLoading.set(true);
    this.monitoringService.findAllPaginated({ page: 0, size: 100 }).subscribe({
      next: (res) => {
        const items = res?.content || [];
        this.allPengajuan.set(items);
        this.isLoading.set(false);
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error('Failed to load pengajuan:', err);
        this.monitoringService.findAll().subscribe({
          next: (list) => {
            this.allPengajuan.set(list || []);
            this.isLoading.set(false);
            this.cdr.detectChanges();
          },
          error: () => {
            this.allPengajuan.set([]);
            this.isLoading.set(false);
            this.cdr.detectChanges();
          },
        });
      },
    });
  }

  // Filter & Search Handlers
  onSearchChange(query: string): void {
    this.searchQuery.set(query || '');
    this.currentPage.set(1);
  }

  onStatusFilterChange(status: string): void {
    this.selectedStatusFilter.set(status || '');
    this.currentPage.set(1);
  }

  onBranchFilterChange(branchId: string): void {
    this.selectedBranchFilter.set(branchId || '');
    this.currentPage.set(1);
  }

  clearFilters(): void {
    this.searchQuery.set('');
    this.selectedStatusFilter.set('');
    this.selectedBranchFilter.set('');
    this.currentPage.set(1);
  }

  // Table Sort & Pagination
  onSortChange(event: { key: string; direction: 'asc' | 'desc' | '' }): void {
    this.sortKey.set(event.key);
    this.sortDirection.set(event.direction);
  }

  onPageChange(page: number): void {
    this.currentPage.set(page);
  }

  // Detail Modal Handlers
  openDetail(item: MarketingPengajuanItemResponse): void {
    const id = item.pengajuanId || item.id || '';
    this.isLoadingDetail.set(true);
    this.isDetailModalOpen.set(true);

    if (id) {
      this.monitoringService.getDetail(id).subscribe({
        next: (detail) => {
          if (detail) {
            this.selectedDetail.set({
              ...detail,
              // Merge fallback values if some fields are not provided in detail endpoint
              customer: detail.customer || item.customer || item.namaNasabah || item.nama,
              email: detail.email || item.email,
              noHp: detail.noHp || item.noHp,
              noPengajuan: detail.noPengajuan || item.noPengajuan || item.nomorPengajuan,
              jumlah: detail.jumlah ?? item.jumlah ?? item.nominalPinjaman ?? item.nominal ?? 0,
              tenor: detail.tenor ?? item.tenor ?? 12,
              cabang: detail.cabang || item.cabang,
              hasilReviewTerakhir: detail.hasilReviewTerakhir || item.hasilReviewTerakhir,
              catatanReviewTerakhir: detail.catatanReviewTerakhir || item.catatanReviewTerakhir,
              tanggalReviewTerakhir: detail.tanggalReviewTerakhir || item.tanggalReviewTerakhir,
              tanggalPengajuan: detail.tanggalPengajuan || item.tanggalPengajuan || item.createdDate,
              status: detail.status || item.status,
            });
          } else {
            this.selectedDetail.set(this.constructFallbackDetail(item));
          }
          this.isLoadingDetail.set(false);
          this.cdr.detectChanges();
        },
        error: (err) => {
          console.warn('Could not fetch detail from server, using row data:', err);
          this.selectedDetail.set(this.constructFallbackDetail(item));
          this.isLoadingDetail.set(false);
          this.cdr.detectChanges();
        },
      });
    } else {
      this.selectedDetail.set(this.constructFallbackDetail(item));
      this.isLoadingDetail.set(false);
    }
  }

  closeDetailModal(): void {
    this.isDetailModalOpen.set(false);
    this.selectedDetail.set(null);
  }

  private constructFallbackDetail(
    item: MarketingPengajuanItemResponse
  ): MarketingPengajuanDetailResponse {
    return {
      pengajuanId: item.pengajuanId || item.id,
      noPengajuan: item.noPengajuan || item.nomorPengajuan,
      status: item.status || 'MENUNGGU_REVIEW',
      tanggalPengajuan: item.tanggalPengajuan || item.createdDate || '-',
      hasilReviewTerakhir: item.hasilReviewTerakhir,
      catatanReviewTerakhir: item.catatanReviewTerakhir,
      tanggalReviewTerakhir: item.tanggalReviewTerakhir,
      customer: item.customer || item.namaNasabah || item.nama,
      email: item.email,
      noHp: item.noHp,
      nik: item.nik,
      jumlah: item.jumlah || item.nominalPinjaman || item.nominal || 0,
      tenor: item.tenor || 12,
      cabang: this.getCabangName(item),
    };
  }

  // Detail Modal Data Getters (Support both flat and nested objects)
  getDetailCustomerName(): string {
    const d = this.selectedDetail();
    if (!d) return '-';
    if (typeof d.customer === 'object' && d.customer?.nama) return d.customer.nama;
    if (typeof d.customer === 'object' && d.customer?.namaLengkap) return d.customer.namaLengkap;
    if (typeof d.customer === 'string' && d.customer) return d.customer;
    if (d.nasabah?.namaLengkap) return d.nasabah.namaLengkap;
    if (d.nasabah?.nama) return d.nasabah.nama;
    if (d.namaNasabah) return d.namaNasabah;
    if (d.nama) return d.nama;
    return '-';
  }

  getDetailNik(): string {
    const d = this.selectedDetail();
    if (!d) return '-';
    if (typeof d.customer === 'object' && d.customer?.nik) return d.customer.nik;
    if (d.nik) return d.nik;
    if (d.nasabah?.nik) return d.nasabah.nik;
    return '-';
  }

  getDetailNoHp(): string {
    const d = this.selectedDetail();
    if (!d) return '-';
    if (typeof d.customer === 'object' && d.customer?.noHp) return d.customer.noHp;
    if (d.noHp) return d.noHp;
    if (d.nasabah?.noHp) return d.nasabah.noHp;
    return '-';
  }

  getDetailEmail(): string {
    const d = this.selectedDetail();
    if (!d) return '-';
    if (typeof d.customer === 'object' && d.customer?.email) return d.customer.email;
    if (d.email) return d.email;
    if (d.nasabah?.email) return d.nasabah.email;
    return '-';
  }

  getDetailAlamat(): string {
    const d = this.selectedDetail();
    if (!d) return '-';
    if (typeof d.customer === 'object' && d.customer?.alamat) return d.customer.alamat;
    if (d.alamat) return d.alamat;
    if (d.nasabah?.alamat) return d.nasabah.alamat;
    return '-';
  }

  getDetailPekerjaan(): string {
    const d = this.selectedDetail();
    if (!d) return 'Karyawan Swasta';
    if (typeof d.customer === 'object' && d.customer?.pekerjaan) return d.customer.pekerjaan;
    if (d.pekerjaan) return d.pekerjaan;
    if (d.nasabah?.pekerjaan) return d.nasabah.pekerjaan;
    return 'Karyawan Swasta';
  }

  getDetailPendapatan(): number | null {
    const d = this.selectedDetail();
    if (!d) return null;
    if (typeof d.customer === 'object' && d.customer?.pendapatanBulanan)
      return d.customer.pendapatanBulanan;
    if (d.pendapatanBulanan) return d.pendapatanBulanan;
    if (d.nasabah?.pendapatanBulanan) return d.nasabah.pendapatanBulanan;
    return null;
  }

  getDetailCabang(): string {
    const d = this.selectedDetail();
    if (!d) return 'Kantor Pusat';
    if (typeof d.cabang === 'object' && d.cabang?.nama) return d.cabang.nama;
    if (typeof d.cabang === 'string' && d.cabang) return d.cabang;
    if (d.cabangNama) return d.cabangNama;
    return 'Kantor Pusat';
  }

  getDetailJumlah(): number {
    const d = this.selectedDetail();
    if (!d) return 0;
    return d.jumlah ?? d.nominalPinjaman ?? d.nominal ?? d.pinjaman?.nominal ?? 0;
  }

  getDetailTenor(): number {
    const d = this.selectedDetail();
    if (!d) return 12;
    return d.tenor ?? d.pinjaman?.tenor ?? 12;
  }

  getDetailSkor(): number | null {
    const d = this.selectedDetail();
    if (!d) return null;
    return d.scoring?.skorKredit ?? d.scoring?.skor ?? d.skorKredit ?? d.skor ?? null;
  }

  getDetailRekomendasiPlafond(): string {
    const d = this.selectedDetail();
    if (!d) return '';
    return d.scoring?.rekomendasiPlafond ?? d.rekomendasiPlafond ?? '';
  }

  getDetailDbr(): number | null {
    const d = this.selectedDetail();
    if (!d) return null;
    return d.scoring?.dbr ?? d.dbr ?? null;
  }

  getDetailLamaBekerja(): number | null {
    const d = this.selectedDetail();
    if (!d) return null;
    return d.scoring?.lamaBekerja ?? d.lamaBekerja ?? null;
  }

  getDetailReviews(): ReviewHistoryItem[] {
    const d = this.selectedDetail();
    if (!d) return [];
    return d.reviews || d.reviewHistory || [];
  }

  getDetailDokumen(): DokumenPengajuanItem[] {
    const d = this.selectedDetail();
    if (!d) return [];
    return d.dokumen || d.documents || [];
  }

  // Helpers & Formatters
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
    const s = (status || '').toUpperCase();
    const h = (hasilReview || '').toUpperCase();

    if (h === 'DISETUJUI' || s === 'DISETUJUI' || s === 'DISBURSED') return 'success';
    if (h === 'DITOLAK' || s === 'DITOLAK') return 'error';
    if (s === 'SELESAI_DIREVIEW') return 'primary';
    if (s === 'MENUNGGU_REVIEW' || h === 'DOKUMEN_DIREVISI') return 'warning';
    return 'neutral';
  }

  getStatusLabel(status?: string, hasilReview?: string): string {
    const s = (status || '').toUpperCase();
    const h = (hasilReview || '').toUpperCase();

    if (h === 'DISETUJUI') return 'Disetujui';
    if (h === 'DITOLAK') return 'Ditolak';
    if (h === 'DOKUMEN_DIREVISI') return 'Dokumen Direvisi';
    if (s === 'SELESAI_DIREVIEW') return 'Selesai Direview';
    if (s === 'MENUNGGU_REVIEW') return 'Menunggu Review';
    if (s === 'DISBURSED') return 'Telah Cair';
    return status || 'Pending';
  }
}
