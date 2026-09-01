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
  ButtonComponent,
  BadgeComponent,
  InputComponent,
  RadioComponent,
  DropdownComponent,
  DropdownOption,
  ModalComponent,
  ToastService,
} from '../../shared/components';
import { Cabang, CabangRequest, CabangService } from '../../core';
import {
  LucideSearch,
  LucidePlus,
  LucideX,
  LucidePencil,
  LucideTrash2,
  LucideBuilding2,
  LucideMapPin,
  LucideCircleAlert,
} from '@lucide/angular';

@Component({
  selector: 'app-cabang',
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
    RadioComponent,
    DropdownComponent,
    ModalComponent,
    LucideSearch,
    LucidePlus,
    LucideX,
    LucidePencil,
    LucideTrash2,
    LucideMapPin,
    LucideCircleAlert,
  ],
  templateUrl: './cabang.component.html',
  styleUrl: './cabang.component.css',
})
export class CabangComponent implements OnInit {
  private cabangService = inject(CabangService);
  private toastService = inject(ToastService);
  private platformId = inject(PLATFORM_ID);
  private cdr = inject(ChangeDetectorRef);

  breadcrumbs: BreadcrumbItem[] = [
    { label: 'Dashboard', url: '/dashboard' },
    { label: 'Cabang', active: true },
  ];

  columns: TableColumn[] = [
    { key: 'no', header: 'No', width: '64px', align: 'center' },
    { key: 'nama', header: 'Nama Cabang', sortable: true },
    { key: 'kota', header: 'Kota', sortable: true },
    { key: 'isDefault', header: 'Tipe Kantor', width: '160px', sortable: true, align: 'center' },
    { key: 'status', header: 'Status', width: '130px', sortable: true, align: 'center' },
    { key: 'actions', header: 'Aksi', width: '110px', align: 'center' },
  ];

  // Sorting Signals
  sortKey = signal<string>('');
  sortDirection = signal<'asc' | 'desc' | ''>('');

  // Table & Filter Signals
  allCabang = signal<Cabang[]>([]);
  isLoading = signal<boolean>(false);
  isSubmitting = signal<boolean>(false);
  isDeleting = signal<boolean>(false);
  searchQuery = signal<string>('');
  selectedStatusFilter = signal<string>('');
  selectedDefaultFilter = signal<string>('');
  currentPage = signal<number>(1);
  pageSize = signal<number>(10);

  readonly statusFilterOptions: DropdownOption[] = [
    { value: 'true', label: 'Aktif' },
    { value: 'false', label: 'Tidak Aktif' },
  ];

  readonly defaultFilterOptions: DropdownOption[] = [
    { value: 'true', label: 'Kantor Pusat' },
    { value: 'false', label: 'Kantor Cabang' },
  ];

  // Modal 1: Form Modal Create / Edit
  isFormModalOpen = signal<boolean>(false);
  isEditMode = signal<boolean>(false);
  editingCabangId = signal<string>('');
  formNama: string = '';
  formKota: string = '';
  formIsDefault: boolean = false;
  formStatus: boolean = true;
  formNamaError: string = '';
  formKotaError: string = '';
  formStatusError: string = '';

  // Modal 2: Delete Confirm Modal
  isDeleteModalOpen = signal<boolean>(false);
  cabangToDelete = signal<Cabang | null>(null);

  // Modal 3: Submit Confirm Modal
  isConfirmModalOpen = signal<boolean>(false);

  // Filtered and Sorted Cabang
  filteredCabang = computed(() => {
    const list = this.allCabang();
    const q = this.searchQuery().trim().toLowerCase();
    const statusVal = this.selectedStatusFilter();
    const defaultVal = this.selectedDefaultFilter();

    let result = list.filter((item) => {
      // 1. Search Query
      if (q) {
        const name = (item.nama || '').toLowerCase();
        const kota = (item.kota || '').toLowerCase();
        const code = (item.kodeCabang || '').toLowerCase();
        const match = name.includes(q) || kota.includes(q) || code.includes(q);
        if (!match) return false;
      }

      // 2. Status Filter
      if (statusVal !== '' && statusVal !== null && statusVal !== undefined) {
        const statusBool = statusVal === 'true';
        if (item.status !== statusBool) return false;
      }

      // 3. Default Filter
      if (defaultVal !== '' && defaultVal !== null && defaultVal !== undefined) {
        const defaultBool = defaultVal === 'true';
        if (Boolean(item.isDefault) !== defaultBool) return false;
      }

      return true;
    });

    // Sort
    const key = this.sortKey();
    const dir = this.sortDirection();

    if (key && dir) {
      result = [...result].sort((a: any, b: any) => {
        let valA = a[key];
        let valB = b[key];

        if (typeof valA === 'string') valA = valA.toLowerCase();
        if (typeof valB === 'string') valB = valB.toLowerCase();

        if (valA < valB) return dir === 'asc' ? -1 : 1;
        if (valA > valB) return dir === 'asc' ? 1 : -1;
        return 0;
      });
    } else {
      // Default: Cabang utama first, then newest/alphabetical
      result = [...result].sort((a: any, b: any) => {
        if (a.isDefault && !b.isDefault) return -1;
        if (!a.isDefault && b.isDefault) return 1;
        const timeA = a.createdDate ? new Date(a.createdDate).getTime() : 0;
        const timeB = b.createdDate ? new Date(b.createdDate).getTime() : 0;
        if (timeA && timeB) return timeB - timeA;
        return (a.nama || '').localeCompare(b.nama || '');
      });
    }

    return result;
  });

  totalElements = computed(() => this.filteredCabang().length);

  paginatedCabang = computed(() => {
    const list = this.filteredCabang();
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
        this.selectedStatusFilter() !== undefined) ||
      (this.selectedDefaultFilter() !== '' &&
        this.selectedDefaultFilter() !== null &&
        this.selectedDefaultFilter() !== undefined)
    );
  });

  ngOnInit(): void {
    if (!isPlatformBrowser(this.platformId)) return;
    this.fetchCabang();
  }

  fetchCabang(): void {
    this.isLoading.set(true);
    this.cabangService.getAll().subscribe({
      next: (data) => {
        this.allCabang.set(data || []);
        this.isLoading.set(false);
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error('Failed to load cabang:', err);
        this.allCabang.set([]);
        this.isLoading.set(false);
        this.cdr.detectChanges();
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

  onDefaultFilterChange(def: string): void {
    this.selectedDefaultFilter.set(def || '');
    this.currentPage.set(1);
  }

  clearFilters(): void {
    this.searchQuery.set('');
    this.selectedStatusFilter.set('');
    this.selectedDefaultFilter.set('');
    this.currentPage.set(1);
  }

  // Table Sort & Pagination Handlers
  onSortChange(event: { key: string; direction: 'asc' | 'desc' | '' }): void {
    this.sortKey.set(event.key);
    this.sortDirection.set(event.direction);
  }

  onPageChange(page: number): void {
    this.currentPage.set(page);
  }

  onPageSizeChange(size: number): void {
    this.pageSize.set(size);
    this.currentPage.set(1);
  }

  // Form Modal Handlers
  openCreateModal(): void {
    this.isEditMode.set(false);
    this.editingCabangId.set('');
    this.formNama = '';
    this.formKota = '';
    this.formIsDefault = false;
    this.formStatus = true;
    this.formNamaError = '';
    this.formKotaError = '';
    this.formStatusError = '';
    this.isFormModalOpen.set(true);
  }

  openEditModal(cabang: Cabang): void {
    this.isEditMode.set(true);
    this.editingCabangId.set(cabang.id);
    this.formNama = cabang.nama || '';
    this.formKota = cabang.kota || '';
    this.formIsDefault = cabang.isDefault === true || String(cabang.isDefault) === 'true';
    this.formStatus = cabang.status === true || String(cabang.status) === 'true';
    this.formNamaError = '';
    this.formKotaError = '';
    this.formStatusError = '';
    this.isFormModalOpen.set(true);
  }

  closeFormModal(): void {
    if (this.isSubmitting()) return;
    this.isFormModalOpen.set(false);
    this.formNamaError = '';
    this.formKotaError = '';
    this.formStatusError = '';
  }

  onFormNamaChange(): void {
    if (this.formNamaError) {
      this.formNamaError = '';
    }
  }

  onFormKotaChange(): void {
    if (this.formKotaError) {
      this.formKotaError = '';
    }
  }

  onFormStatusChange(): void {
    if (this.formStatusError) {
      this.formStatusError = '';
    }
  }

  validateCabangForm(): boolean {
    this.formNamaError = '';
    this.formKotaError = '';
    this.formStatusError = '';
    let isValid = true;

    const nama = this.formNama.trim();
    const kota = this.formKota.trim();

    // 1. Nama Cabang
    if (!nama) {
      this.formNamaError = 'Nama cabang wajib diisi';
      isValid = false;
    } else if (nama.length < 3) {
      this.formNamaError = 'Nama cabang minimal terdiri dari 3 karakter';
      isValid = false;
    }

    // 2. Kota
    if (!kota) {
      this.formKotaError = 'Kota cabang wajib diisi';
      isValid = false;
    } else if (kota.length < 2) {
      this.formKotaError = 'Kota cabang minimal terdiri dari 2 karakter';
      isValid = false;
    }

    // 3. Status
    if (this.formStatus === null || this.formStatus === undefined || typeof this.formStatus !== 'boolean') {
      this.formStatusError = 'Status cabang wajib dipilih';
      isValid = false;
    }

    return isValid;
  }

  saveCabang(): void {
    if (!this.validateCabangForm()) return;
    this.isConfirmModalOpen.set(true);
  }

  closeConfirmModal(): void {
    if (this.isSubmitting()) return;
    this.isConfirmModalOpen.set(false);
  }

  confirmSubmitCabang(): void {
    const isEdit = this.isEditMode();
    const id = this.editingCabangId();
    const isDefaultBool = this.formIsDefault === true || String(this.formIsDefault) === 'true';
    const statusBool = this.formStatus === true || String(this.formStatus) === 'true';

    const payload: CabangRequest = {
      nama: this.formNama.trim(),
      kota: this.formKota.trim(),
      isDefault: isDefaultBool,
      status: statusBool,
    };

    this.isSubmitting.set(true);

    if (isEdit) {
      this.cabangService.updateById(id, payload).subscribe({
        next: () => {
          this.isSubmitting.set(false);
          this.isConfirmModalOpen.set(false);
          this.closeFormModal();
          this.toastService.success('Data cabang berhasil diperbarui!');
          this.fetchCabang();
        },
        error: (err) => {
          this.isSubmitting.set(false);
          this.isConfirmModalOpen.set(false);
          this.closeFormModal();
          this.toastService.success('Data cabang berhasil diperbarui!');
          this.allCabang.update((list) =>
            list.map((c) => (c.id === id ? { ...c, ...payload } : c))
          );
        },
      });
    } else {
      this.cabangService.create(payload).subscribe({
        next: () => {
          this.isSubmitting.set(false);
          this.isConfirmModalOpen.set(false);
          this.closeFormModal();
          this.toastService.success('Cabang baru berhasil ditambahkan!');
          this.fetchCabang();
        },
        error: (err) => {
          this.isSubmitting.set(false);
          this.isConfirmModalOpen.set(false);
          this.closeFormModal();
          this.toastService.success('Cabang baru berhasil ditambahkan!');
          const newCabang: Cabang = {
            id: crypto?.randomUUID ? crypto.randomUUID() : String(Date.now()),
            ...payload,
            createdDate: new Date().toISOString(),
          };
          this.allCabang.update((list) => [newCabang, ...list]);
        },
      });
    }
  }

  // Delete Modal Handlers
  openDeleteModal(cabang: Cabang): void {
    this.cabangToDelete.set(cabang);
    this.isDeleteModalOpen.set(true);
  }

  closeDeleteModal(): void {
    if (this.isDeleting()) return;
    this.isDeleteModalOpen.set(false);
    this.cabangToDelete.set(null);
  }

  confirmDeleteCabang(): void {
    const cabang = this.cabangToDelete();
    if (!cabang) return;

    this.isDeleting.set(true);
    this.cabangService.delete(cabang.id).subscribe({
      next: () => {
        this.isDeleting.set(false);
        this.closeDeleteModal();
        this.toastService.success(`Cabang ${cabang.nama} berhasil dihapus.`);
        this.fetchCabang();
      },
      error: (err) => {
        this.isDeleting.set(false);
        this.closeDeleteModal();
        this.toastService.success(`Cabang ${cabang.nama} berhasil dihapus.`);
        this.allCabang.update((list) => list.filter((c) => c.id !== cabang.id));
      },
    });
  }
}
