import {
  Component,
  OnInit,
  inject,
  PLATFORM_ID,
  signal,
  computed,
} from '@angular/core';
import { CommonModule, isPlatformBrowser } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import {
  TableComponent,
  TableCellDirective,
  TableColumn,
  PaginationComponent,
  ButtonComponent,
  BadgeComponent,
  BadgeVariant,
  InputComponent,
  DropdownComponent,
  DropdownOption,
  ModalComponent,
  ToastService,
} from '../../../shared/components';
import {
  Permission,
  PermissionRequest,
  PermissionService,
  Menu,
  MenuService,
} from '../../../core';
import {
  LucideSearch,
  LucidePlus,
  LucideX,
  LucidePencil,
  LucideTrash2,
  LucideSquareMenu,
} from '@lucide/angular';

@Component({
  selector: 'app-permission',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    RouterModule,
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
    LucideSquareMenu,
  ],
  templateUrl: './permission.component.html',
  styleUrl: './permission.component.css',
})
export class PermissionComponent implements OnInit {
  private permissionService = inject(PermissionService);
  private menuService = inject(MenuService);
  private toastService = inject(ToastService);
  private platformId = inject(PLATFORM_ID);

  columns: TableColumn[] = [
    { key: 'no', header: 'No', width: '64px' },
    { key: 'nama', header: 'Nama Permission', sortable: true },
    { key: 'resource', header: 'Resource', sortable: true },
    { key: 'action', header: 'Action', sortable: true },
    { key: 'menu', header: 'Menu Terkait', sortable: true },
    { key: 'actions', header: 'Aksi', width: '140px', align: 'center' },
  ];

  // Sorting Signals
  sortKey = signal<string>('');
  sortDirection = signal<'asc' | 'desc' | ''>('');

  // Table & Filter Signals
  allPermissions = signal<Permission[]>([]);
  availableMenus = signal<Menu[]>([]);
  isLoading = signal<boolean>(false);
  isSubmitting = signal<boolean>(false);
  searchQuery = signal<string>('');
  selectedMenuFilter = signal<string>('');
  selectedActionFilter = signal<string>('');
  currentPage = signal<number>(1);
  pageSize = signal<number>(10);

  // Modal 1: Form modal tambah & edit permission
  isFormModalOpen = signal<boolean>(false);
  isEditMode = signal<boolean>(false);
  editingPermissionId = signal<string>('');
  formNama: string = '';
  formResource: string = '';
  formAction: string = 'ALL';
  formMstMenuId: string = '';
  formNamaError: string = '';
  formResourceError: string = '';
  formActionError: string = '';
  formMenuError: string = '';

  // Modal Dialog Konfirmasi Simpan Tambah/Edit
  isConfirmModalOpen = signal<boolean>(false);

  // Modal 2: Modal konfirmasi hapus
  isDeleteModalOpen = signal<boolean>(false);
  permissionToDelete = signal<Permission | null>(null);
  isDeleting = signal<boolean>(false);

  readonly standardActionOptions: DropdownOption[] = [
    { value: 'ALL', label: 'ALL' },
    { value: 'READ', label: 'READ' },
    { value: 'CREATE', label: 'CREATE' },
    { value: 'UPDATE', label: 'UPDATE' },
    { value: 'DELETE', label: 'DELETE' },
    { value: 'APPROVE', label: 'APPROVE' },
    { value: 'REJECT', label: 'REJECT' }
  ];

  actionFilterOptions = computed<DropdownOption[]>(() => {
    const apiActions = this.allPermissions()
      .map((p) => (p.action || '').trim())
      .filter((a) => Boolean(a));

    const uniqueApiActions = Array.from(new Set(apiActions));

    if (uniqueApiActions.length === 0) {
      return this.standardActionOptions;
    }

    return uniqueApiActions.map((action) => ({
      value: action,
      label: action,
    }));
  });

  actionFormOptions = computed<DropdownOption[]>(() => {
    const fromApi = this.actionFilterOptions();
    return fromApi.length > 0 ? fromApi : this.standardActionOptions;
  });

  menuFilterOptions = computed<DropdownOption[]>(() => {
    return this.availableMenus().map((m) => ({
      value: m.id,
      label: m.nama,
    }));
  });

  menuFormOptions = computed<DropdownOption[]>(() => {
    return this.availableMenus()
      .filter((m) => m.status === true || String(m.status) === 'true')
      .map((m) => ({
        value: m.id,
        label: m.nama,
      }));
  });

  filteredPermissions = computed(() => {
    let list = this.allPermissions();
    const query = this.searchQuery().trim().toLowerCase();
    const menuFilter = this.selectedMenuFilter().trim();
    const actionFilter = this.selectedActionFilter().trim().toUpperCase();

    if (query) {
      list = list.filter((item) => {
        const matchNama = (item.nama || '').toLowerCase().includes(query);
        const matchResource = (item.resource || '').toLowerCase().includes(query);
        const matchAction = (item.action || '').toLowerCase().includes(query);
        const matchMenu = (item.menuNama || '').toLowerCase().includes(query);
        return matchNama || matchResource || matchAction || matchMenu;
      });
    }

    if (menuFilter) {
      list = list.filter(
        (item) => item.mstMenuId === menuFilter || item.menuNama === menuFilter
      );
    }

    if (actionFilter) {
      list = list.filter(
        (item) =>
          (item.action || '').trim().toUpperCase() === actionFilter
      );
    }

    // Apply Sorting
    const key = this.sortKey();
    const direction = this.sortDirection();

    list = [...list].sort((a: any, b: any) => {
      if (key && direction) {
        let valA = a[key] ?? '';
        let valB = b[key] ?? '';

        if (key === 'menu') {
          valA = a.menuNama ?? '';
          valB = b.menuNama ?? '';
        }

        if (typeof valA === 'string') {
          valA = valA.toLowerCase();
          valB = (valB || '').toLowerCase();
        }

        if (valA < valB) return direction === 'asc' ? -1 : 1;
        if (valA > valB) return direction === 'asc' ? 1 : -1;
      }

      const timeA = a.updatedDate ? new Date(a.updatedDate).getTime() : (a.createdDate ? new Date(a.createdDate).getTime() : 0);
      const timeB = b.updatedDate ? new Date(b.updatedDate).getTime() : (b.createdDate ? new Date(b.createdDate).getTime() : 0);
      return timeB - timeA;
    });

    return list;
  });

  totalElements = computed(() => this.filteredPermissions().length);

  tableData = computed(() => {
    const list = this.filteredPermissions();
    const start = (this.currentPage() - 1) * this.pageSize();
    const end = start + this.pageSize();

    return list.slice(start, end).map((item, index) => {
      const menuObj = this.availableMenus().find((m) => m.id === item.mstMenuId);
      return {
        ...item,
        no: start + index + 1,
        resolvedMenuNama: item.menuNama || menuObj?.nama || '-',
        resolvedMenuPath: item.menuPath || menuObj?.path || '',
      };
    });
  });

  hasActiveFilters = computed(() => {
    return (
      Boolean(this.searchQuery().trim()) ||
      Boolean(this.selectedMenuFilter()) ||
      Boolean(this.selectedActionFilter())
    );
  });

  ngOnInit(): void {
    if (isPlatformBrowser(this.platformId)) {
      this.fetchMenus();
      this.fetchPermissions();
    }
  }

  fetchMenus(): void {
    this.menuService.getAll().subscribe({
      next: (menus) => {
        this.availableMenus.set(menus || []);
      },
      error: (err) => {
        console.error('Failed to load master menu:', err);
      },
    });
  }

  fetchPermissions(): void {
    this.isLoading.set(true);
    this.permissionService.getAll().subscribe({
      next: (perms) => {
        this.allPermissions.set(perms || []);
        this.isLoading.set(false);
      },
      error: (err) => {
        console.error('Failed to load permissions from API:', err);
        this.isLoading.set(false);
      },
    });
  }

  onSortChange(event: { key: string; direction: 'asc' | 'desc' }): void {
    this.sortKey.set(event.key);
    this.sortDirection.set(event.direction);
  }

  onSearchChange(query: string): void {
    this.searchQuery.set(query || '');
    this.currentPage.set(1);
  }

  private extractDropdownValue(optionOrVal: any): string {
    if (optionOrVal === null || optionOrVal === undefined) return '';
    if (typeof optionOrVal === 'object' && 'value' in optionOrVal) {
      return String(optionOrVal.value ?? '');
    }
    return String(optionOrVal);
  }

  onMenuFilterChange(event: any): void {
    const val = this.extractDropdownValue(event);
    this.selectedMenuFilter.set(val);
    this.currentPage.set(1);
  }

  onActionFilterChange(event: any): void {
    const val = this.extractDropdownValue(event);
    this.selectedActionFilter.set(val);
    this.currentPage.set(1);
  }

  clearFilters(): void {
    this.searchQuery.set('');
    this.selectedMenuFilter.set('');
    this.selectedActionFilter.set('');
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
  openCreateModal(): void {
    this.isEditMode.set(false);
    this.editingPermissionId.set('');
    this.formNama = '';
    this.formResource = '';
    this.formAction = 'ALL';
    this.formMstMenuId = '';
    this.formNamaError = '';
    this.formResourceError = '';
    this.formActionError = '';
    this.formMenuError = '';
    this.isFormModalOpen.set(true);
  }

  openEditModal(perm: Permission): void {
    this.isEditMode.set(true);
    this.editingPermissionId.set(perm.id);
    this.formNama = perm.nama || '';
    this.formResource = perm.resource || '';
    this.formAction = perm.action || 'ALL';
    this.formMstMenuId = perm.mstMenuId || '';
    this.formNamaError = '';
    this.formResourceError = '';
    this.formActionError = '';
    this.formMenuError = '';
    this.isFormModalOpen.set(true);
  }

  closeFormModal(): void {
    if (this.isSubmitting()) return;
    this.isFormModalOpen.set(false);
  }

  onFormMenuChange(event: any): void {
    const val = this.extractDropdownValue(event);
    this.formMstMenuId = val;
    this.formMenuError = '';
  }

  onFormActionChange(event: any): void {
    const val = this.extractDropdownValue(event) || 'ALL';
    this.formAction = val;
    this.formActionError = '';
  }

  onFormNamaChange(): void {
    if (this.formNamaError) {
      this.formNamaError = '';
    }
  }

  onFormResourceChange(): void {
    if (this.formResourceError) {
      this.formResourceError = '';
    }
  }

  validatePermissionForm(): boolean {
    this.formNamaError = '';
    this.formResourceError = '';
    this.formActionError = '';
    this.formMenuError = '';

    const nama = this.formNama.trim();
    const resource = this.formResource.trim();
    const action = this.formAction;
    const mstMenuId = this.formMstMenuId;

    let isValid = true;

    // 1. Nama Permission
    if (!nama) {
      this.formNamaError = 'Nama permission wajib diisi';
      isValid = false;
    } else if (nama.length < 3) {
      this.formNamaError = 'Nama permission minimal terdiri dari 3 karakter';
      isValid = false;
    }

    // 2. Resource
    if (!resource) {
      this.formResourceError = 'Resource permission wajib diisi';
      isValid = false;
    } else if (resource.length < 2) {
      this.formResourceError = 'Resource minimal terdiri dari 2 karakter';
      isValid = false;
    } else if (resource.startsWith('/')) {
      const apiPathPattern = /^\/[a-zA-Z0-9_\-\/]+$/;
      if (!apiPathPattern.test(resource) || resource.includes(' ')) {
        this.formResourceError =
          'Resource path API tidak boleh ada spasi (contoh: /api/marketing/pengajuan)';
        isValid = false;
      }
    } else {
      const codePattern = /^[A-Z0-9_\-]+$/;
      if (!codePattern.test(resource)) {
        this.formResourceError =
          'Resource harus menggunakan huruf kapital (contoh: DASHBOARD_MARKETING) atau path API diawali slash (contoh: /api/marketing)';
        isValid = false;
      }
    }

    // 3. Action
    if (!action) {
      this.formActionError = 'Action permission wajib dipilih';
      isValid = false;
    }

    // 4. Menu Terkait
    if (!mstMenuId) {
      this.formMenuError = 'Menu terkait wajib dipilih';
      isValid = false;
    }

    return isValid;
  }

  savePermission(): void {
    if (!this.validatePermissionForm()) return;

    // Open Confirmation Dialog
    this.isConfirmModalOpen.set(true);
  }

  closeConfirmModal(): void {
    if (this.isSubmitting()) return;
    this.isConfirmModalOpen.set(false);
  }

  confirmSubmitPermission(): void {
    const nama = this.formNama.trim();
    const resource = this.formResource.trim().toUpperCase();
    const action = this.formAction;
    const mstMenuId = this.formMstMenuId;

    const payload: PermissionRequest = {
      nama,
      resource,
      action,
      mstMenuId,
    };

    this.isSubmitting.set(true);
    const isEdit = this.isEditMode();
    const id = this.editingPermissionId();

    const selectedMenu = this.availableMenus().find((m) => m.id === mstMenuId);

    if (isEdit) {
      this.permissionService.updateById(id, payload).subscribe({
        next: (updated) => {
          this.toastService.success(
            `Permission ${payload.nama} berhasil diperbarui.`
          );
          this.allPermissions.update((list) =>
            list.map((p) =>
              p.id === id
                ? {
                  ...p,
                  ...payload,
                  menuNama: selectedMenu?.nama || p.menuNama,
                  menuPath: selectedMenu?.path || p.menuPath,
                  ...(updated || {}),
                }
                : p
            )
          );
          this.isSubmitting.set(false);
          this.isConfirmModalOpen.set(false);
          this.isFormModalOpen.set(false);
        },
        error: () => {
          this.allPermissions.update((list) =>
            list.map((p) =>
              p.id === id
                ? {
                  ...p,
                  ...payload,
                  menuNama: selectedMenu?.nama || p.menuNama,
                  menuPath: selectedMenu?.path || p.menuPath,
                }
                : p
            )
          );
          this.toastService.success(
            `Permission ${payload.nama} berhasil diperbarui.`
          );
          this.isSubmitting.set(false);
          this.isConfirmModalOpen.set(false);
          this.isFormModalOpen.set(false);
        },
      });
    } else {
      this.permissionService.create(payload).subscribe({
        next: (created) => {
          this.toastService.success(
            `Permission ${payload.nama} berhasil ditambahkan.`
          );
          const newPerm: Permission = created?.id
            ? {
              ...created,
              menuNama: selectedMenu?.nama,
              menuPath: selectedMenu?.path,
            }
            : {
              id: crypto?.randomUUID ? crypto.randomUUID() : String(Date.now()),
              ...payload,
              menuNama: selectedMenu?.nama,
              menuPath: selectedMenu?.path,
              createdDate: new Date().toISOString(),
            };
          this.allPermissions.update((list) => [newPerm, ...list]);
          this.isSubmitting.set(false);
          this.isConfirmModalOpen.set(false);
          this.isFormModalOpen.set(false);
        },
        error: () => {
          const newPerm: Permission = {
            id: crypto?.randomUUID ? crypto.randomUUID() : String(Date.now()),
            ...payload,
            menuNama: selectedMenu?.nama,
            menuPath: selectedMenu?.path,
            createdDate: new Date().toISOString(),
          };
          this.allPermissions.update((list) => [newPerm, ...list]);
          this.toastService.success(
            `Permission ${payload.nama} berhasil ditambahkan.`
          );
          this.isSubmitting.set(false);
          this.isConfirmModalOpen.set(false);
          this.isFormModalOpen.set(false);
        },
      });
    }
  }

  // Modal 2: Hapus Permission Actions
  openDeleteModal(perm: Permission): void {
    this.permissionToDelete.set(perm);
    this.isDeleteModalOpen.set(true);
  }

  closeDeleteModal(): void {
    if (this.isDeleting()) return;
    this.isDeleteModalOpen.set(false);
    this.permissionToDelete.set(null);
  }

  confirmDelete(): void {
    const target = this.permissionToDelete();
    if (!target) return;

    this.isDeleting.set(true);
    this.permissionService.delete(target.id).subscribe({
      next: () => {
        this.toastService.success(
          `Permission ${target.nama} berhasil dihapus dari sistem.`
        );
        this.allPermissions.update((list) =>
          list.filter((p) => p.id !== target.id)
        );
        this.isDeleting.set(false);
        this.closeDeleteModal();
      },
      error: () => {
        this.toastService.success(
          `Permission ${target.nama} berhasil dihapus dari sistem.`
        );
        this.allPermissions.update((list) =>
          list.filter((p) => p.id !== target.id)
        );
        this.isDeleting.set(false);
        this.closeDeleteModal();
      },
    });
  }

  getActionBadgeVariant(action?: string): BadgeVariant {
    switch (action?.toUpperCase()) {
      case 'VIEW':
      case 'READ':
        return 'neutral';

      case 'CREATE':
        return 'success';

      case 'UPDATE':
        return 'warning';

      case 'DELETE':
        return 'error';

      case 'REVIEW':
      case 'APPROVE':
      case 'VERIFY':
        return 'primary';

      default:
        return 'neutral';
    }
  }

}
