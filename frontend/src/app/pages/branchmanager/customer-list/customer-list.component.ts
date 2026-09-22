import {
  Component,
  OnInit,
  signal,
  inject,
  ChangeDetectorRef,
} from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
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
  ToastService,
  CardComponent,
} from '../../../shared/components';
import {
  BranchManagerCustomerService,
  BranchManagerCustomerItemResponse,
  BranchManagerCustomerDetailResponse,
} from '../../../core';
import {
  LucideSearch,
  LucideX,
  LucideEye,
  LucideUsers,
  LucideShieldCheck,
  LucideWallet,
  LucideCreditCard,
  LucideMapPin,
  LucideBriefcase,
  LucideFileText,
  LucideExternalLink,
  LucideUser,
  LucideReceipt,
} from '@lucide/angular';

@Component({
  selector: 'app-bm-customer-list',
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
    CardComponent,
    LucideSearch,
    LucideX,
    LucideEye,
    LucideShieldCheck,
    LucideWallet,
    LucideCreditCard,
    LucideMapPin,
    LucideBriefcase,
    LucideFileText,
    LucideExternalLink,
    LucideUser,
  ],
  templateUrl: './customer-list.component.html',
  styleUrl: './customer-list.component.css',
})
export class BranchManagerCustomerListComponent implements OnInit {
  private customerService = inject(BranchManagerCustomerService);
  private toastService = inject(ToastService);
  private cdr = inject(ChangeDetectorRef);

  // Table Data State
  customers = signal<BranchManagerCustomerItemResponse[]>([]);
  isLoading = signal<boolean>(false);
  totalItems = signal<number>(0);
  currentPage = signal<number>(1);
  pageSize = signal<number>(10);

  // Filters
  searchQuery = signal<string>('');
  selectedTier = signal<string>('');

  tierOptions: DropdownOption[] = [
    { label: 'Semua Tier', value: '' },
    { label: 'Tier Bronze', value: 'Bronze' },
    { label: 'Tier Silver', value: 'Silver' },
    { label: 'Tier Gold', value: 'Gold' },
    { label: 'Tier Platinum', value: 'Platinum' },
    { label: 'Tier Reguler', value: 'Reguler' },
    { label: 'Tier Prioritas', value: 'Prioritas' },
  ];

  // Table Columns
  columns: TableColumn[] = [
    { key: 'customer', header: 'Nasabah', sortable: true, width: '220px', minWidth: '180px' },
    { key: 'domisili', header: 'Domisili', sortable: true },
    { key: 'tierPlafond', header: 'Tier Plafond', sortable: true },
    { key: 'plafond', header: 'Plafond Aktif', sortable: true },
    { key: 'availablePlafond', header: 'Sisa Plafond', sortable: true },
    { key: 'totalPengajuan', header: 'Pengajuan', sortable: true },
    { key: 'statusAkun', header: 'Status Akun', sortable: false },
    { key: 'actions', header: 'Aksi', sortable: false, align: 'center', width: '80px' },
  ];

  // Modal / Detail State
  isModalOpen = signal<boolean>(false);
  isLoadingDetail = signal<boolean>(false);
  selectedCustomerDetail = signal<BranchManagerCustomerDetailResponse | null>(null);
  activeDetailTab = signal<'profil' | 'plafond' | 'pinjaman'>('profil');

  // Stats
  totalVerifiedCustomers = signal<number>(0);
  totalActivePlafond = signal<number>(0);
  avgPlafond = signal<number>(0);

  ngOnInit(): void {
    this.loadCustomers();
  }

  loadCustomers(): void {
    this.isLoading.set(true);
    const params = {
      page: this.currentPage() - 1,
      size: this.pageSize(),
      search: this.searchQuery(),
      tier: this.selectedTier(),
    };

    this.customerService.findAllPaginated(params).subscribe({
      next: (res) => {
        let content = res.content || [];
        if (this.selectedTier()) {
          const t = this.selectedTier().toLowerCase();
          content = content.filter((c) =>
            (c.tierPlafond || '').toLowerCase().includes(t)
          );
        }

        this.customers.set(content);
        this.totalItems.set(res.totalElements || content.length);

        // Compute Stats
        const verifiedCount = res.totalElements || content.length;
        this.totalVerifiedCustomers.set(verifiedCount);

        const totalPlafondSum = content.reduce((sum, c) => sum + (c.totalPlafond || 0), 0);
        this.totalActivePlafond.set(totalPlafondSum);

        const avg = content.length > 0 ? Math.round(totalPlafondSum / content.length) : 0;
        this.avgPlafond.set(avg);

        this.isLoading.set(false);
        this.cdr.markForCheck();
      },
      error: (err: any) => {
        console.error('Error loading branch manager customers:', err);
        this.toastService.show('Gagal memuat daftar nasabah cabang', 'error');
        this.isLoading.set(false);
        this.cdr.markForCheck();
      },
    });
  }

  onSearchChange(value: string): void {
    this.searchQuery.set(value);
    this.currentPage.set(1);
    this.loadCustomers();
  }

  onTierChange(event: any): void {
    const val = typeof event === 'object' && event !== null && 'value' in event ? event.value : event;
    this.selectedTier.set(val ? String(val) : '');
    this.currentPage.set(1);
    this.loadCustomers();
  }

  clearFilters(): void {
    this.searchQuery.set('');
    this.selectedTier.set('');
    this.currentPage.set(1);
    this.loadCustomers();
  }

  hasActiveFilters(): boolean {
    return !!this.searchQuery() || !!this.selectedTier();
  }

  onPageChange(page: number): void {
    this.currentPage.set(page);
    this.loadCustomers();
  }

  onPageSizeChange(size: number): void {
    this.pageSize.set(size);
    this.currentPage.set(1);
    this.loadCustomers();
  }

  viewCustomerDetail(customer: BranchManagerCustomerItemResponse): void {
    this.isModalOpen.set(true);
    this.isLoadingDetail.set(true);
    this.activeDetailTab.set('profil');

    this.customerService.getDetail(customer.customerId).subscribe({
      next: (detail) => {
        this.selectedCustomerDetail.set(detail);
        this.isLoadingDetail.set(false);
        this.cdr.markForCheck();
      },
      error: (err: any) => {
        console.error('Error loading customer detail:', err);
        this.toastService.show('Gagal memuat rincian data nasabah', 'error');
        this.isLoadingDetail.set(false);
        this.cdr.markForCheck();
      },
    });
  }

  closeModal(): void {
    this.isModalOpen.set(false);
    this.selectedCustomerDetail.set(null);
  }

  getFotoUrl(path?: string | null): string {
    return this.customerService.getFileUrl(path);
  }

  openInNewTab(path?: string | null): void {
    if (!path) return;
    const url = this.getFotoUrl(path);
    if (url) {
      window.open(url, '_blank', 'noopener,noreferrer');
    }
  }

  formatCurrency(value: number | undefined | null): string {
    if (value === undefined || value === null) return 'Rp 0';
    return new Intl.NumberFormat('id-ID', {
      style: 'currency',
      currency: 'IDR',
      maximumFractionDigits: 0,
    }).format(value);
  }

  getTierBadgeClass(tier?: string): string {
    const t = (tier || '').toLowerCase();
    if (t.includes('bronze') || t.includes('starter') || t.includes('tier 1')) {
      return 'bg-[#FFEDD5] text-[#9A3412]';
    } else if (t.includes('silver') || t.includes('reguler') || t.includes('tier 2')) {
      return 'bg-slate-100 text-slate-700';
    } else if (t.includes('gold') || t.includes('prioritas') || t.includes('tier 3')) {
      return 'bg-amber-100 text-amber-800';
    } else if (t.includes('platinum') || t.includes('tier 4')) {
      return 'bg-purple-100 text-purple-700';
    }
    return 'bg-neutral-100 text-neutral-700';
  }

  getTierBadgeVariant(tier?: string): BadgeVariant {
    switch ((tier || '').toLowerCase()) {
      case 'platinum':
      case 'prioritas':
        return 'purple';
      case 'gold':
        return 'warning';
      case 'silver':
        return 'info';
      default:
        return 'neutral';
    }
  }

  getLoanStatusBadge(status: string | undefined): { variant: BadgeVariant; label: string } {
    switch ((status || '').toUpperCase()) {
      case 'MENUNGGU_REVIEW':
      case 'MENUNGGU_PERSETUJUAN':
      case 'PENDING':
        return { variant: 'warning', label: 'Menunggu' };
      case 'DISETUJUI':
      case 'APPROVED':
      case 'SELESAI':
      case 'DICAIRKAN':
        return { variant: 'success', label: 'Disetujui' };
      case 'DITOLAK':
      case 'REJECTED':
        return { variant: 'error', label: 'Ditolak' };
      case 'DOKUMEN_DIREVISI':
      case 'PERLU_REVISI':
        return { variant: 'info', label: 'Revisi' };
      default:
        return { variant: 'neutral', label: status || '-' };
    }
  }
}
