import {
  Component,
  OnInit,
  computed,
  inject,
  signal,
  PLATFORM_ID,
} from '@angular/core';
import { CommonModule, isPlatformBrowser } from '@angular/common';
import { FormsModule } from '@angular/forms';
import {
  TableComponent,
  TableCellDirective,
  TableColumn,
  PaginationComponent,
  BreadcrumbsComponent,
  BreadcrumbItem,
  ButtonComponent,
  BadgeComponent,
  InputComponent,
  RadioComponent,
  DropdownComponent,
  DropdownOption,
  ModalComponent,
  ToastService,
} from '../../shared/components';
import { Menu, MenuRequest, MenuService } from '../../core';
import {
  LucideSearch,
  LucidePlus,
  LucideX,
  LucidePencil,
  LucideTrash2,
  LucideLink,
  LucideCircleAlert,
} from '@lucide/angular';

@Component({
  selector: 'app-menu',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    TableComponent,
    TableCellDirective,
    PaginationComponent,
    BreadcrumbsComponent,
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
    LucideLink,
    LucideCircleAlert,
  ],
  templateUrl: './menu.component.html',
  styleUrl: './menu.component.css',
})
export class MenuComponent implements OnInit {
  private menuService = inject(MenuService);
  private toastService = inject(ToastService);
  private platformId = inject(PLATFORM_ID);

  breadcrumbs: BreadcrumbItem[] = [
    { label: 'Dashboard', url: '/dashboard' },
    { label: 'RBAC', url: '/rbac/menu' },
    { label: 'Menu', active: true },
  ];

  columns: TableColumn[] = [
    { key: 'no', header: 'No', width: '64px' },
    { key: 'nama', header: 'Nama Menu', sortable: true },
    { key: 'path', header: 'Path / URL', sortable: true },
    { key: 'status', header: 'Status', width: '140px', sortable: true },
    { key: 'actions', header: 'Aksi', width: '140px', align: 'center' },
  ];

  // Sorting Signals
  sortKey = signal<string>('');
  sortDirection = signal<'asc' | 'desc' | ''>('');

  // Table & Filter Signals
  allMenus = signal<Menu[]>([]);
  isLoading = signal<boolean>(false);
  isSubmitting = signal<boolean>(false);
  searchQuery = signal<string>('');
  selectedStatusFilter = signal<string>('');
  currentPage = signal<number>(1);
  pageSize = signal<number>(10);

  readonly statusFilterOptions: DropdownOption[] = [
    { value: 'true', label: 'Aktif' },
    { value: 'false', label: 'Tidak Aktif' },
  ];

  // Modal 1: Form modal tambah & edit menu
  isFormModalOpen = signal<boolean>(false);
  isEditMode = signal<boolean>(false);
  editingMenuId = signal<string>('');
  formNama: string = '';
  formPath: string = '';
  formStatus: string = 'true';
  formNamaError: string = '';
  formPathError: string = '';

  // Modal Dialog Konfirmasi Simpan Tambah/Edit
  isConfirmModalOpen = signal<boolean>(false);

  // Modal 2: Modal konfirmasi hapus
  isDeleteModalOpen = signal<boolean>(false);
  menuToDelete = signal<Menu | null>(null);
  isDeleting = signal<boolean>(false);

  // Computed properties
  filteredMenus = computed(() => {
    const list = this.allMenus();
    const q = this.searchQuery().trim().toLowerCase();
    const statusVal = this.selectedStatusFilter();

    return list.filter((item) => {
      // 1. Search Query
      if (q) {
        const name = (item.nama || '').toLowerCase();
        const path = (item.path || '').toLowerCase();
        const match = name.includes(q) || path.includes(q);
        if (!match) return false;
      }

      // 2. Status Filter
      if (statusVal !== '' && statusVal !== null && statusVal !== undefined) {
        const statusBool = statusVal === 'true';
        if (item.status !== statusBool) return false;
      }

      return true;
    }).sort((a, b) => {
      const key = this.sortKey();
      const dir = this.sortDirection();

      if (key && dir) {
        let valA: any = '';
        let valB: any = '';

        switch (key) {
          case 'nama':
            valA = (a.nama || '').toLowerCase();
            valB = (b.nama || '').toLowerCase();
            break;
          case 'path':
            valA = (a.path || '').toLowerCase();
            valB = (b.path || '').toLowerCase();
            break;
          case 'status':
            valA = a.status ? 1 : 0;
            valB = b.status ? 1 : 0;
            break;
          default:
            valA = (a as any)[key] || '';
            valB = (b as any)[key] || '';
        }

        if (valA < valB) return dir === 'asc' ? -1 : 1;
        if (valA > valB) return dir === 'asc' ? 1 : -1;
      }

      const timeA = a.updatedDate ? new Date(a.updatedDate).getTime() : (a.createdDate ? new Date(a.createdDate).getTime() : 0);
      const timeB = b.updatedDate ? new Date(b.updatedDate).getTime() : (b.createdDate ? new Date(b.createdDate).getTime() : 0);
      return timeB - timeA;
    });
  });

  totalElements = computed(() => this.filteredMenus().length);

  tableData = computed(() => {
    const list = this.filteredMenus();
    const page = this.currentPage();
    const size = this.pageSize();
    const startIndex = (page - 1) * size;

    return list.slice(startIndex, startIndex + size).map((item, idx) => ({
      ...item,
      no: startIndex + idx + 1,
    }));
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
    this.loadMenus();
  }

  loadMenus(): void {
    this.isLoading.set(true);
    this.menuService.getAll().subscribe({
      next: (menus) => {
        if (Array.isArray(menus)) {
          this.allMenus.set(menus);
        } else {
          this.allMenus.set([]);
        }
        this.isLoading.set(false);
      },
      error: (err) => {
        console.error('Failed to load menus from API:', err);
        this.allMenus.set([]);
        this.isLoading.set(false);
      },
    });
  }

  onSortChange(event: { key: string; direction: 'asc' | 'desc' }): void {
    this.sortKey.set(event.key);
    this.sortDirection.set(event.direction);
  }

  // Filter actions
  onSearchInput(value: string): void {
    this.searchQuery.set(value);
    this.currentPage.set(1);
  }

  onStatusFilterChange(option: DropdownOption | null): void {
    this.selectedStatusFilter.set(option ? String(option.value) : '');
    this.currentPage.set(1);
  }

  clearFilters(): void {
    this.searchQuery.set('');
    this.selectedStatusFilter.set('');
    this.currentPage.set(1);
  }

  // Pagination actions
  onPageChange(page: number): void {
    this.currentPage.set(page);
  }

  onPageSizeChange(size: number): void {
    this.pageSize.set(size);
    this.currentPage.set(1);
  }

  // Modal 1: Form Tambah/Edit Actions
  formStatusError: string = '';

  openCreateModal(): void {
    this.isEditMode.set(false);
    this.editingMenuId.set('');
    this.formNama = '';
    this.formPath = '';
    this.formStatus = 'true';
    this.formNamaError = '';
    this.formPathError = '';
    this.formStatusError = '';
    this.isFormModalOpen.set(true);
  }

  openEditModal(menu: Menu): void {
    this.isEditMode.set(true);
    this.editingMenuId.set(menu.id);
    this.formNama = menu.nama || '';
    this.formPath = menu.path || '';
    this.formStatus = menu.status === true || String(menu.status) === 'true' ? 'true' : 'false';
    this.formNamaError = '';
    this.formPathError = '';
    this.formStatusError = '';
    this.isFormModalOpen.set(true);
  }

  closeFormModal(): void {
    if (this.isSubmitting()) return;
    this.isFormModalOpen.set(false);
    this.formNamaError = '';
    this.formPathError = '';
    this.formStatusError = '';
  }

  onFormNamaChange(): void {
    if (this.formNamaError) {
      this.formNamaError = '';
    }
  }

  onFormPathChange(): void {
    if (this.formPathError) {
      this.formPathError = '';
    }
  }

  onFormStatusChange(): void {
    if (this.formStatusError) {
      this.formStatusError = '';
    }
  }

  validateMenuForm(): boolean {
    this.formNamaError = '';
    this.formPathError = '';
    this.formStatusError = '';

    const nama = this.formNama.trim();
    const path = this.formPath.trim();

    let isValid = true;

    if (!nama) {
      this.formNamaError = 'Nama menu wajib diisi';
      isValid = false;
    } else if (nama.length < 2) {
      this.formNamaError = 'Nama menu minimal terdiri dari 2 karakter';
      isValid = false;
    }

    if (!path) {
      this.formPathError = 'Path / URL menu wajib diisi';
      isValid = false;
    } else if (!path.startsWith('/')) {
      this.formPathError = 'Path harus diawali dengan tanda slash (/), contoh: /marketing/dashboard';
      isValid = false;
    } else if (path.includes(' ')) {
      this.formPathError = 'Path tidak boleh mengandung spasi';
      isValid = false;
    } else if (/[A-Z]/.test(path)) {
      this.formPathError = 'Path harus menggunakan huruf kecil (contoh: /marketing/dashboard)';
      isValid = false;
    } else {
      const pathPattern = /^[a-z0-9\/\-_]+$/;
      if (!pathPattern.test(path)) {
        this.formPathError = 'Path hanya boleh berisi huruf kecil, angka, slash (/), dash (-), atau underscore (_)';
        isValid = false;
      }
    }

    if (this.formStatus === null || this.formStatus === undefined || this.formStatus === '') {
      this.formStatusError = 'Status menu wajib dipilih';
      isValid = false;
    }

    return isValid;
  }

  saveMenu(): void {
    if (!this.validateMenuForm()) return;

    // Open Confirmation Modal before submitting
    this.isConfirmModalOpen.set(true);
  }

  closeConfirmModal(): void {
    if (this.isSubmitting()) return;
    this.isConfirmModalOpen.set(false);
  }

  confirmSubmitMenu(): void {
    const nama = this.formNama.trim();
    const path = this.formPath.trim();

    const payload: MenuRequest = {
      nama,
      path,
      status: this.formStatus === 'true',
    };

    this.isSubmitting.set(true);

    if (this.isEditMode()) {
      const id = this.editingMenuId();
      this.menuService.updateById(id, payload).subscribe({
        next: () => {
          this.toastService.success(
            `Menu ${payload.nama} berhasil diperbarui.`
          );
          this.loadMenus();
          this.isSubmitting.set(false);
          this.isConfirmModalOpen.set(false);
          this.isFormModalOpen.set(false);
        },
        error: (err) => {
          console.error('Failed to update menu:', err);
          this.toastService.error(
            err?.error?.message || 'Gagal memperbarui data menu.'
          );
          this.isSubmitting.set(false);
        },
      });
    } else {
      this.menuService.create(payload).subscribe({
        next: () => {
          this.toastService.success(
            `Menu ${payload.nama} berhasil ditambahkan.`
          );
          this.loadMenus();
          this.isSubmitting.set(false);
          this.isConfirmModalOpen.set(false);
          this.isFormModalOpen.set(false);
        },
        error: (err) => {
          console.error('Failed to create menu:', err);
          this.toastService.error(
            err?.error?.message || 'Gagal menambahkan menu baru.'
          );
          this.isSubmitting.set(false);
        },
      });
    }
  }

  // Modal 2: Hapus Menu Actions
  openDeleteModal(menu: Menu): void {
    this.menuToDelete.set(menu);
    this.isDeleteModalOpen.set(true);
  }

  closeDeleteModal(): void {
    if (this.isDeleting()) return;
    this.isDeleteModalOpen.set(false);
    this.menuToDelete.set(null);
  }

  confirmDelete(): void {
    const target = this.menuToDelete();
    if (!target) return;

    this.isDeleting.set(true);
    this.menuService.delete(target.id).subscribe({
      next: () => {
        this.toastService.success(
          `Menu ${target.nama} berhasil dihapus dari sistem.`
        );
        this.loadMenus();
        this.isDeleting.set(false);
        this.closeDeleteModal();
      },
      error: (err) => {
        console.error('Failed to delete menu:', err);
        this.toastService.error(
          err?.error?.message || 'Gagal menghapus data menu.'
        );
        this.isDeleting.set(false);
        this.closeDeleteModal();
      },
    });
  }
}
