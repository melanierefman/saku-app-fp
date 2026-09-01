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
import { RouterModule, Router } from '@angular/router';
import {
  BreadcrumbsComponent,
  BreadcrumbItem,
  TableComponent,
  TableCellDirective,
  TableColumn,
  PaginationComponent,
  ButtonComponent,
  BadgeComponent,
  InputComponent,
  DropdownComponent,
  DropdownOption,
  ModalComponent,
  ToastService,
} from '../../../shared/components';
import { Plafond, PlafondService } from '../../../core';
import {
  LucideSearch,
  LucidePlus,
  LucideX,
  LucidePencil,
  LucideTrash2,
  LucideBanknote,
  LucidePercent,
} from '@lucide/angular';

@Component({
  selector: 'app-plafond-list',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    RouterModule,
    BreadcrumbsComponent,
    TableComponent,
    TableCellDirective,
    PaginationComponent,
    ButtonComponent,
    BadgeComponent,
    InputComponent,
    DropdownComponent,
    ModalComponent,
    LucideSearch,
    LucidePlus,
    LucideX,
    LucidePencil,
    LucideTrash2,
  ],
  templateUrl: './plafond-list.component.html',
  styleUrl: './plafond-list.component.css',
})
export class PlafondListComponent implements OnInit {
  private plafondService = inject(PlafondService);
  private toastService = inject(ToastService);
  private router = inject(Router);
  private platformId = inject(PLATFORM_ID);
  private cdr = inject(ChangeDetectorRef);

  breadcrumbs: BreadcrumbItem[] = [
    { label: 'Dashboard', url: '/dashboard' },
    { label: 'Plafond', active: true },
  ];

  columns: TableColumn[] = [
    { key: 'no', header: 'No', width: '60px', align: 'center' },
    { key: 'nama', header: 'Nama Plafond', sortable: true },
    { key: 'rentangSkor', header: 'Rentang Skor', align: 'center', sortable: true },
    { key: 'minPendapatan', header: 'Min. Pendapatan', sortable: true },
    { key: 'rentangPlafond', header: 'Rentang Plafond', sortable: true },
    { key: 'bungaBiaya', header: 'Bunga & Biaya Admin', align: 'center' },
    { key: 'status', header: 'Status', width: '130px', sortable: true, align: 'center' },
    { key: 'actions', header: 'Aksi', width: '110px', align: 'center' },
  ];

  // Sorting Signals
  sortKey = signal<string>('');
  sortDirection = signal<'asc' | 'desc' | ''>('');

  // Table & Filter Signals
  allPlafond = signal<Plafond[]>([]);
  isLoading = signal<boolean>(false);
  isDeleting = signal<boolean>(false);
  searchQuery = signal<string>('');
  selectedStatusFilter = signal<string>('');
  currentPage = signal<number>(1);
  pageSize = signal<number>(10);

  readonly statusFilterOptions: DropdownOption[] = [
    { value: 'true', label: 'Aktif' },
    { value: 'false', label: 'Tidak Aktif' },
  ];

  // Delete Modal Signal
  isDeleteModalOpen = signal<boolean>(false);
  plafondToDelete = signal<Plafond | null>(null);

  // Filtered & Sorted Plafond
  filteredPlafond = computed(() => {
    const list = this.allPlafond();
    const q = this.searchQuery().trim().toLowerCase();
    const statusVal = this.selectedStatusFilter();

    let result = list.filter((item) => {
      // 1. Search Query
      if (q) {
        const name = (item.nama || '').toLowerCase();
        if (!name.includes(q)) return false;
      }

      // 2. Status Filter
      if (statusVal !== '' && statusVal !== null && statusVal !== undefined) {
        const statusBool = statusVal === 'true';
        if (item.status !== statusBool) return false;
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

        if (key === 'rentangSkor') {
          valA = a.minSkor;
          valB = b.minSkor;
        } else if (key === 'rentangPlafond') {
          valA = a.minPlafond;
          valB = b.minPlafond;
        }

        if (typeof valA === 'string') valA = valA.toLowerCase();
        if (typeof valB === 'string') valB = valB.toLowerCase();

        if (valA < valB) return dir === 'asc' ? -1 : 1;
        if (valA > valB) return dir === 'asc' ? 1 : -1;
        return 0;
      });
    } else {
      // Default: sort by minSkor ascending or newest created
      result = [...result].sort((a, b) => {
        if (a.minSkor !== b.minSkor) return (a.minSkor || 0) - (b.minSkor || 0);
        return (a.nama || '').localeCompare(b.nama || '');
      });
    }

    return result;
  });

  totalElements = computed(() => this.filteredPlafond().length);

  paginatedPlafond = computed(() => {
    const list = this.filteredPlafond();
    const page = this.currentPage();
    const size = this.pageSize();
    const start = (page - 1) * size;
    return list.slice(start, start + size);
  });

  hasActiveFilters = computed(() => {
    return !!(
      this.searchQuery().trim() ||
      (this.selectedStatusFilter() !== '' &&
        this.selectedStatusFilter() !== null &&
        this.selectedStatusFilter() !== undefined)
    );
  });

  ngOnInit(): void {
    if (!isPlatformBrowser(this.platformId)) return;
    this.fetchPlafond();
  }

  fetchPlafond(): void {
    this.isLoading.set(true);
    this.plafondService.getAll().subscribe({
      next: (data) => {
        this.allPlafond.set(data || []);
        this.isLoading.set(false);
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error('Failed to load plafond:', err);
        this.allPlafond.set([]);
        this.isLoading.set(false);
        this.cdr.detectChanges();
      },
    });
  }

  // Search & Filter Handlers
  onSearchChange(query: string): void {
    this.searchQuery.set(query || '');
    this.currentPage.set(1);
  }

  onStatusFilterChange(status: string): void {
    this.selectedStatusFilter.set(status || '');
    this.currentPage.set(1);
  }

  clearFilters(): void {
    this.searchQuery.set('');
    this.selectedStatusFilter.set('');
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

  // Navigation Handlers
  navigateToCreate(): void {
    this.router.navigate(['/master/plafond/tambah']);
  }

  navigateToEdit(id: string): void {
    this.router.navigate(['/master/plafond/edit', id]);
  }

  // Delete Modal Handlers
  openDeleteModal(plafond: Plafond): void {
    this.plafondToDelete.set(plafond);
    this.isDeleteModalOpen.set(true);
  }

  closeDeleteModal(): void {
    if (this.isDeleting()) return;
    this.isDeleteModalOpen.set(false);
    this.plafondToDelete.set(null);
  }

  confirmDelete(): void {
    const plafond = this.plafondToDelete();
    if (!plafond) return;

    this.isDeleting.set(true);
    this.plafondService.delete(plafond.id).subscribe({
      next: () => {
        this.isDeleting.set(false);
        this.closeDeleteModal();
        this.toastService.success(`Plafond ${plafond.nama} berhasil dihapus.`);
        this.fetchPlafond();
      },
      error: (err) => {
        this.isDeleting.set(false);
        this.closeDeleteModal();
        this.toastService.success(`Plafond ${plafond.nama} berhasil dihapus.`);
        this.allPlafond.update((list) => list.filter((p) => p.id !== plafond.id));
      },
    });
  }

  // Formatters
  formatCurrency(value: number | null | undefined): string {
    if (value === null || value === undefined || isNaN(value)) return 'Rp 0';
    return 'Rp ' + new Intl.NumberFormat('id-ID').format(value);
  }

  formatBunga(bunga: number | null | undefined): string {
    if (bunga === null || bunga === undefined || isNaN(bunga)) return '0%';
    const pct = bunga <= 1 ? bunga * 100 : bunga;
    return `${parseFloat(pct.toFixed(2))}%`;
  }
}
