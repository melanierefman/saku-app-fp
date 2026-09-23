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
  TableComponent,
  TableCellDirective,
  TableColumn,
  PaginationComponent,
  BadgeComponent,
  BadgeVariant,
  InputComponent,
  DropdownComponent,
  DropdownOption,
  DatePickerComponent,
  ModalComponent,
  ToastService,
  SkeletonComponent,
} from '../../../../shared/components';
import {
  MarketingPengajuanItemResponse,
  MarketingPengajuanDetailResponse,
  RiwayatPengajuanItem,
  ReviewHistoryItem,
  DokumenPengajuanItem,
  MonitoringPengajuanService,
  Cabang,
  CabangService,
} from '../../../../core';
import {
  LucideSearch,
  LucideX,
  LucideEye,
  LucideFileText,
} from '@lucide/angular';
import { formatDate as formatDateHelper, sortTableData } from '../../../../shared/utils';

@Component({
  selector: 'app-monitoring-pengajuan',
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
    SkeletonComponent,
    LucideSearch,
    LucideX,
    LucideEye,
    LucideFileText,
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

  columns: TableColumn[] = [
    { key: 'no', header: 'No', width: '60px', align: 'center' },
    { key: 'noPengajuan', header: 'No. Pengajuan', sortable: true },
    { key: 'customer', header: 'Nama Customer', sortable: true },
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
  selectedTanggalPengajuan = signal<Date | null>(null);
  currentPage = signal<number>(1);
  pageSize = signal<number>(10);

  // Detail Modal
  isDetailModalOpen = signal<boolean>(false);
  selectedDetail = signal<MarketingPengajuanDetailResponse | null>(null);
  isLoadingDetail = signal<boolean>(false);

  readonly statusOptions: DropdownOption[] = [
    { value: 'MENUNGGU_REVIEW_MARKETING', label: 'Menunggu Review Marketing' },
    { value: 'PERLU_REVISI', label: 'Perlu Revisi Dokumen' },
    { value: 'DOKUMEN_DIREVISI', label: 'Revisi Diajukan' },
    { value: 'MENUNGGU_PERSETUJUAN_BM', label: 'Menunggu Persetujuan BM' },
    { value: 'MENUNGGU_PENCAIRAN', label: 'Menunggu Pencairan' },
    { value: 'DICAIRKAN', label: 'Dana Dicairkan' },
    { value: 'DITOLAK_MARKETING', label: 'Ditolak Marketing' },
    { value: 'DITOLAK_BM', label: 'Ditolak Branch Manager' },
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
    const tglYMD = this.formatDateToYMD(this.selectedTanggalPengajuan());

    let result = list.filter((item) => {
      // 1. Search Query
      if (q) {
        const no = (item.noPengajuan || item.nomorPengajuan || '').toLowerCase();
        const nama = (item.customer || item.namaCustomer || item.nama || '').toLowerCase();
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

      // 4. Tanggal Pengajuan Filter
      if (tglYMD) {
        const tgl = item.tanggalPengajuan || item.createdDate || '';
        if (!tgl.startsWith(tglYMD)) {
          const parsed = new Date(tgl);
          if (isNaN(parsed.getTime()) || this.formatDateToYMD(parsed) !== tglYMD) {
            return false;
          }
        }
      }

      return true;
    });

    // Sorting
    const key = this.sortKey();
    const dir = this.sortDirection();

    if (key && dir) {
      result = sortTableData<MarketingPengajuanItemResponse>(
        result,
        key,
        dir,
        {
          noPengajuan: (item) => item.noPengajuan || (item as any).nomorPengajuan || '',
          customer: (item) => item.customer || (item as any).namaCustomer || (item as any).nama || '',
          cabang: (item) => this.getCabangName(item),
          jumlahTenor: (item) => item.jumlah || (item as any).nominalPinjaman || (item as any).nominal || 0,
          tanggalPengajuan: (item) => item.tanggalPengajuan || (item as any).createdDate,
          status: (item) => item.status,
        }
      );
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
      this.selectedBranchFilter() ||
      this.selectedTanggalPengajuan()
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

  onTanggalPengajuanChange(date: any): void {
    this.selectedTanggalPengajuan.set(
      date instanceof Date ? date : date ? new Date(date) : null
    );
    this.currentPage.set(1);
  }

  private formatDateToYMD(date: Date | null): string | undefined {
    if (!date) return undefined;
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    return `${year}-${month}-${day}`;
  }

  clearFilters(): void {
    this.searchQuery.set('');
    this.selectedStatusFilter.set('');
    this.selectedBranchFilter.set('');
    this.selectedTanggalPengajuan.set(null);
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
              ...item,
              ...detail,
              // Merge fallback values if some fields are not provided in detail endpoint
              customer: detail.customer || item.customer || item.namaCustomer || item.nama,
              email: detail.email || item.email,
              noHp: detail.noHp || item.noHp,
              nik: detail.nik || item.nik,
              noPengajuan: detail.noPengajuan || item.noPengajuan || item.nomorPengajuan,
              jumlah: detail.jumlah ?? item.jumlah ?? item.nominalPinjaman ?? item.nominal ?? 0,
              tenor: detail.tenor ?? item.tenor ?? 12,
              cabang: detail.cabang || item.cabang,
              hasilReviewTerakhir: detail.hasilReviewTerakhir || item.hasilReviewTerakhir,
              tanggalReviewTerakhir: detail.tanggalReviewTerakhir || item.tanggalReviewTerakhir,
              tanggalPengajuan: detail.tanggalPengajuan || item.tanggalPengajuan || item.createdDate,
              status: detail.status || item.status,
              marketing: detail.marketing || item.marketing,
              branchManager: detail.branchManager || item.branchManager,
              backoffice: detail.backoffice || item.backoffice,
              riwayat: detail.riwayat || item.riwayat,
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
      customer: item.customer || item.namaCustomer || item.nama,
      email: item.email,
      noHp: item.noHp,
      nik: item.nik,
      jumlah: item.jumlah || item.nominalPinjaman || item.nominal || 0,
      tenor: item.tenor || 12,
      cabang: this.getCabangName(item),
      marketing: item.marketing,
      branchManager: item.branchManager,
      backoffice: item.backoffice,
      riwayat: item.riwayat,
    };
  }

  getRiwayatList(): RiwayatPengajuanItem[] {
    const d = this.selectedDetail();
    if (!d) return [];

    if (Array.isArray(d.riwayat) && d.riwayat.length > 0) {
      return d.riwayat;
    }

    const list: RiwayatPengajuanItem[] = [];
    if (d.marketing) {
      list.push({
        role: 'MARKETING',
        status: d.marketing.status || 'MENUNGGU_REVIEW',
        tanggal: d.marketing.tanggal,
      });
    }
    if (d.branchManager) {
      list.push({
        role: 'BRANCH_MANAGER',
        status: d.branchManager.status || '-',
        tanggal: d.branchManager.tanggal,
      });
    }
    if (d.backoffice) {
      list.push({
        role: 'BACKOFFICE',
        status: d.backoffice.status || '-',
        tanggal: d.backoffice.tanggal,
      });
    }
    return list;
  }

  getRoleLabel(role?: string): string {
    const r = (role || '').toUpperCase();
    if (r === 'MARKETING') return 'Marketing';
    if (r === 'BRANCH_MANAGER' || r === 'BM') return 'Branch Manager';
    if (r === 'BACKOFFICE' || r === 'BACK_OFFICE') return 'Back Office';
    return role || 'Petugas';
  }

  getRoleStepNumber(role?: string): number {
    const r = (role || '').toUpperCase();
    if (r === 'MARKETING') return 1;
    if (r === 'BRANCH_MANAGER' || r === 'BM') return 2;
    if (r === 'BACKOFFICE' || r === 'BACK_OFFICE') return 3;
    return 1;
  }

  getStageBadgeVariant(status?: string | null): BadgeVariant {
    const s = (status || '').toUpperCase().trim().replace(/\s+/g, '_');
    switch (s) {
      // 1. Marketing
      case 'MENUNGGU_REVIEW_MARKETING':
      case 'MENUNGGU_REVIEW':
      case 'PENDING':
        return 'primary';
      case 'PERLU_REVISI':
        return 'orange';
      case 'DOKUMEN_DIREVISI':
        return 'warning';
      case 'SELESAI_DIREVIEW':
        return 'info';

      // 2. Branch Manager (BM)
      case 'MENUNGGU_PERSETUJUAN_BM':
      case 'MENUNGGU_PERSETUJUAN':
        return 'purple';
      case 'DISETUJUI':
      case 'PENGAJUAN_DISETUJUI':
      case 'APPROVED':
        return 'primary';

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
      case 'GAGAL':
      case 'REJECTED':
      case 'PENGAJUAN_DITOLAK':
        return 'error';

      default:
        return 'neutral';
    }
  }

  getStageLabel(status?: string | null): string {
    return this.formatHumanReadableStatus(status);
  }

  formatHumanReadableStatus(status?: string | null): string {
    if (!status || status === '-' || status.trim() === '') return 'Belum Diproses';
    const s = status.toUpperCase().trim();
    if (s === 'MENUNGGU_REVIEW') return 'Menunggu Review';
    if (s === 'SELESAI_DIREVIEW') return 'Selesai Direview';
    if (s === 'DOKUMEN_DIREVISI') return 'Dokumen Direvisi';
    if (s === 'PERLU_REVISI') return 'Perlu Revisi';
    if (s === 'MENUNGGU_PERSETUJUAN') return 'Menunggu Persetujuan';
    if (s === 'DISETUJUI' || s === 'PENGAJUAN_DISETUJUI') return 'Disetujui BM';
    if (s === 'DICAIRKAN') return 'Dicairkan';
    if (s === 'DITOLAK' || s === 'PENGAJUAN_DITOLAK') return 'Ditolak';
    if (s === 'DISBURSED') return 'Telah Cair';
    if (s === 'BERHASIL') return 'Berhasil Cair';
    if (s === 'PENDING') return 'Menunggu Review';
    if (s === 'KARYAWAN_TETAP') return 'Karyawan Tetap';
    if (s === 'KARYAWAN_KONTRAK') return 'Karyawan Kontrak';
    if (s === 'WIRASWASTA' || s === 'WIRAUSAHA' || s === 'PENGUSAHA') return 'Wiraswasta';
    if (s === 'PROFESIONAL') return 'Profesional';
    if (s === 'PNS' || s === 'PNS_BUMN' || s === 'PEGAWAI_NEGERI') return 'PNS / Pegawai BUMN';
    if (s === 'IBU_RUMAH_TANGGA') return 'Ibu Rumah Tangga';

    return s
      .split('_')
      .map((word) => word.charAt(0).toUpperCase() + word.slice(1).toLowerCase())
      .join(' ');
  }

  // Detail Modal Data Getters (Support both flat and nested objects)
  getDetailCustomerName(): string {
    const d = this.selectedDetail();
    if (!d) return '-';
    if (typeof d.customer === 'object' && d.customer?.nama) return d.customer.nama;
    if (typeof d.customer === 'object' && d.customer?.namaLengkap) return d.customer.namaLengkap;
    if (typeof d.customer === 'string' && d.customer) return d.customer;
    if (d.namaLengkap) return d.namaLengkap;
    if (d.namaCustomer) return d.namaCustomer;
    if (d.nama) return d.nama;
    return '-';
  }

  getDetailNik(): string {
    const d = this.selectedDetail();
    if (!d) return '-';
    if (typeof d.customer === 'object' && d.customer?.nik) return d.customer.nik;
    if (d.nik) return d.nik;
    return '-';
  }

  getDetailNoHp(): string {
    const d = this.selectedDetail();
    if (!d) return '-';
    if (typeof d.customer === 'object' && d.customer?.noHp) return d.customer.noHp;
    if (d.noHp) return d.noHp;
    return '-';
  }

  getDetailEmail(): string {
    const d = this.selectedDetail();
    if (!d) return '-';
    if (typeof d.customer === 'object' && d.customer?.email) return d.customer.email;
    if (d.email) return d.email;
    return '-';
  }

  getDetailAlamat(): string {
    const d = this.selectedDetail();
    if (!d) return '-';
    if (typeof d.customer === 'object' && d.customer?.alamat) return d.customer.alamat;
    if (d.alamat) return d.alamat;
    return '-';
  }

  getDetailPekerjaan(): string {
    const d = this.selectedDetail();
    if (!d) return 'Karyawan Swasta';
    if (typeof d.customer === 'object' && d.customer?.pekerjaan) return d.customer.pekerjaan;
    if (d.pekerjaan) return d.pekerjaan;
    return 'Karyawan Swasta';
  }

  getDetailPendapatan(): number | null {
    const d = this.selectedDetail();
    if (!d) return null;
    if (typeof d.customer === 'object' && d.customer?.pendapatanBulanan)
      return d.customer.pendapatanBulanan;
    if (d.pendapatanBulanan) return d.pendapatanBulanan;
    return null;
  }

  getDetailCabang(): string {
    const d = this.selectedDetail();
    if (!d) return 'Kantor Pusat';
    if (typeof d.cabang === 'object' && d.cabang?.nama) return d.cabang.nama;
    if (typeof d.cabang === 'string' && d.cabang) return d.cabang;
    if (d.cabangNama) return d.cabangNama;
    if (typeof d.cabang === 'string') return d.cabang;
    return 'Kantor Pusat';
  }

  getDetailTujuan(): string {
    const d = this.selectedDetail();
    if (!d) return '-';
    return d.tujuanPinjaman || d.tujuan || d.keperluan || 'Modal Usaha / Konsumtif';
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
    const val =
      d.scoring?.skorKredit ??
      d.scoring?.skor ??
      d.skorKredit ??
      d.skor ??
      d.customer?.skorKredit ??
      d.customer?.skor ??
      null;

    if (val !== null && val !== undefined && !isNaN(Number(val))) {
      return Number(val);
    }

    if (d.catatanReviewTerakhir) {
      const match = d.catatanReviewTerakhir.match(/skor\s+kredit\s+(\d+)/i);
      if (match && match[1]) {
        return Number(match[1]);
      }
    }

    return null;
  }

  getDetailRekomendasiPlafond(): string {
    const d = this.selectedDetail();
    if (!d) return '';
    const val =
      d.scoring?.rekomendasiPlafond ??
      d.rekomendasiPlafond ??
      d.customer?.rekomendasiPlafond ??
      '';

    if (val) return String(val);

    if (d.catatanReviewTerakhir) {
      const match = d.catatanReviewTerakhir.match(/(Plafond\s+Tier\s+\d+(?:\s*-\s*[A-Za-z]+)?)/i);
      if (match && match[1]) {
        return match[1];
      }
    }

    return '';
  }

  getDetailDbr(): string | null {
    const d = this.selectedDetail();
    if (!d) return null;
    const val =
      d.scoring?.dbr ??
      d.dbr ??
      d.customer?.dbr ??
      null;

    if (val !== null && val !== undefined && !isNaN(Number(val))) {
      const num = Number(val);
      if (num > 0 && num < 1) {
        return (num * 100).toFixed(2).replace('.', ',');
      }
      return String(num);
    }

    if (d.catatanReviewTerakhir) {
      const match = d.catatanReviewTerakhir.match(/DBR\s+(?:rendah\s+)?\(?([\d.,]+)%?\)?/i);
      if (match && match[1]) {
        return match[1].replace('%', '');
      }
    }

    return null;
  }

  getDetailLamaBekerja(): number | null {
    const d = this.selectedDetail();
    if (!d) return null;
    const val =
      d.customer?.lamaBekerja ??
      d.customer?.lamaKerja ??
      d.customer?.masaKerja ??
      d.customer?.lamaBekerjaBulan ??
      d.scoring?.lamaBekerja ??
      d.scoring?.lamaKerja ??
      d.scoring?.masaKerja ??
      d.lamaBekerja ??
      d.lamaKerja ??
      d.masaKerja ??
      null;

    if (val !== null && val !== undefined && !isNaN(Number(val))) {
      return Number(val);
    }

    if (d.catatanReviewTerakhir) {
      const match = d.catatanReviewTerakhir.match(/lama\s+bekerja\s+(\d+)\s*bulan/i);
      if (match && match[1]) {
        return Number(match[1]);
      }
    }

    return null;
  }

  getDetailReviews(): ReviewHistoryItem[] {
    const d = this.selectedDetail();
    if (!d) return [];
    const list = d.reviews || d.reviewHistory || [];
    return [...list].sort((a, b) => {
      const timeA = new Date(a.tanggalReview || a.tanggal || a.createdDate || '').getTime() || 0;
      const timeB = new Date(b.tanggalReview || b.tanggal || b.createdDate || '').getTime() || 0;
      return timeA - timeB;
    });
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
    return formatDateHelper(dateStr);
  }

  getStatusBadgeVariant(status?: string, hasilReview?: string): BadgeVariant {
    const raw = status || hasilReview;
    const s = (raw || '').toUpperCase().trim().replace(/\s+/g, '_');
    switch (s) {
      // 1. Marketing
      case 'MENUNGGU_REVIEW_MARKETING':
      case 'MENUNGGU_REVIEW':
      case 'PENDING':
        return 'primary';
      case 'PERLU_REVISI':
        return 'orange';
      case 'DOKUMEN_DIREVISI':
        return 'warning';
      case 'SELESAI_DIREVIEW':
        return 'info';

      // 2. Branch Manager (BM)
      case 'MENUNGGU_PERSETUJUAN_BM':
      case 'MENUNGGU_PERSETUJUAN':
        return 'purple';
      case 'DISETUJUI':
      case 'PENGAJUAN_DISETUJUI':
      case 'APPROVED':
        return 'primary';

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
}
