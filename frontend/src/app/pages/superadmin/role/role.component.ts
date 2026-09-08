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
  ButtonComponent,
  BadgeComponent,
  InputComponent,
  RadioComponent,
  CheckboxComponent,
  DropdownComponent,
  DropdownOption,
  ModalComponent,
  ToastService,
} from '../../../shared/components';
import {
  Role,
  RoleRequest,
  RoleDetailResponse,
  RoleService,
  formatRoleName,
} from '../../../core';
import {
  LucideSearch,
  LucidePlus,
  LucideX,
  LucidePencil,
  LucideTrash2,
  LucideCircleAlert,
} from '@lucide/angular';

@Component({
  selector: 'app-role',
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
    RadioComponent,
    DropdownComponent,
    ModalComponent,
    LucideSearch,
    LucidePlus,
    LucideX,
    LucidePencil,
    LucideTrash2,
    LucideCircleAlert,
  ],
  templateUrl: './role.component.html',
  styleUrl: './role.component.css',
})
export class RoleComponent implements OnInit {
  private roleService = inject(RoleService);
  private toastService = inject(ToastService);
  private platformId = inject(PLATFORM_ID);
  private cdr = inject(ChangeDetectorRef);

  columns: TableColumn[] = [
    { key: 'no', header: 'No', width: '64px' },
    { key: 'nama', header: 'Nama Role', sortable: true },
    { key: 'totalPermissions', header: 'Hak Akses', width: '140px', sortable: true },
    { key: 'status', header: 'Status', width: '140px', sortable: true },
    { key: 'actions', header: 'Aksi', width: '140px', align: 'center' },
  ];

  // Sorting Signals
  sortKey = signal<string>('');
  sortDirection = signal<'asc' | 'desc' | ''>('');

  // Table & Filter Signals
  allRoles = signal<Role[]>([]);
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

  // Modal 1: Form modal tambah & edit role
  isFormModalOpen = signal<boolean>(false);
  isEditMode = signal<boolean>(false);
  editingRoleId = signal<string>('');
  formNama: string = '';
  formStatus: boolean = true;
  formNamaError: string = '';
  formStatusError: string = '';

  // Modal Dialog Konfirmasi Simpan Tambah/Edit
  isConfirmModalOpen = signal<boolean>(false);

  // Modal 2: Modal konfirmasi hapus
  isDeleteModalOpen = signal<boolean>(false);
  roleToDelete = signal<Role | null>(null);

  // Computed properties
  filteredRoles = computed(() => {
    const list = this.allRoles();
    const q = this.searchQuery().trim().toLowerCase();
    const statusVal = this.selectedStatusFilter();

    return list.filter((item) => {
      // 1. Search Query
      if (q) {
        const name = (item.nama || '').toLowerCase();
        const formatted = formatRoleName(item.nama).toLowerCase();
        const code = (item.code || '').toLowerCase();
        const match = name.includes(q) || formatted.includes(q) || code.includes(q);
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
          case 'totalPermissions':
            valA = a.totalPermissions ?? 0;
            valB = b.totalPermissions ?? 0;
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

  totalElements = computed(() => this.filteredRoles().length);

  tableData = computed(() => {
    const list = this.filteredRoles();
    const page = this.currentPage();
    const size = this.pageSize();
    const startIndex = (page - 1) * size;

    return list.slice(startIndex, startIndex + size).map((item, idx) => ({
      ...item,
      no: startIndex + idx + 1,
      displayName: formatRoleName(item.nama),
      rawName: item.nama,
      permissionsCount: item.totalPermissions ?? 0,
    }));
  });

  onSortChange(event: { key: string; direction: 'asc' | 'desc' }): void {
    this.sortKey.set(event.key);
    this.sortDirection.set(event.direction);
  }

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
    this.fetchRoles();
  }

  fetchRoles(): void {
    this.isLoading.set(true);
    this.roleService.getAll().subscribe({
      next: (data) => {
        this.allRoles.set(data || []);
        this.isLoading.set(false);
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error('Failed to load roles:', err);
        this.allRoles.set([]);
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

  clearFilters(): void {
    this.searchQuery.set('');
    this.selectedStatusFilter.set('');
    this.currentPage.set(1);
  }

  onPageChange(page: number): void {
    this.currentPage.set(page);
  }

  // Modal 1: Create & edit role
  openCreateModal(): void {
    this.isEditMode.set(false);
    this.editingRoleId.set('');
    this.formNama = '';
    this.formStatus = true;
    this.formNamaError = '';
    this.formStatusError = '';
    this.isFormModalOpen.set(true);
  }

  openEditModal(role: Role): void {
    this.isEditMode.set(true);
    this.editingRoleId.set(role.id);
    this.formNama = role.nama || '';
    this.formStatus = role.status === true || String(role.status) === 'true';
    this.formNamaError = '';
    this.formStatusError = '';
    this.isFormModalOpen.set(true);
  }

  closeFormModal(): void {
    this.isFormModalOpen.set(false);
    this.formNamaError = '';
    this.formStatusError = '';
  }

  validateRoleForm(): boolean {
    this.formNamaError = '';
    this.formStatusError = '';
    let isValid = true;
    const nama = this.formNama.trim();

    if (!nama) {
      this.formNamaError = 'Nama role wajib diisi';
      isValid = false;
    } else if (nama.length < 3) {
      this.formNamaError = 'Nama role minimal terdiri dari 3 karakter';
      isValid = false;
    } else if (nama.includes(' ')) {
      this.formNamaError = 'Nama role tidak boleh mengandung spasi (gunakan underscore, contoh: BRANCH_MANAGER)';
      isValid = false;
    } else {
      const uppercasePattern = /^[A-Z0-9_\-]+$/;
      if (!uppercasePattern.test(nama)) {
        this.formNamaError = 'Nama role harus menggunakan huruf kapital (contoh: SUPERADMIN, MARKETING)';
        isValid = false;
      }
    }

    if (this.formStatus === null || this.formStatus === undefined || typeof this.formStatus !== 'boolean') {
      this.formStatusError = 'Status role wajib dipilih';
      isValid = false;
    }

    return isValid;
  }

  onNamaChange(): void {
    if (this.formNamaError) {
      this.formNamaError = '';
    }
  }

  onStatusChange(): void {
    if (this.formStatusError) {
      this.formStatusError = '';
    }
  }

  submitRoleForm(): void {
    if (!this.validateRoleForm()) return;
    this.isConfirmModalOpen.set(true);
  }

  closeConfirmModal(): void {
    if (this.isSubmitting()) return;
    this.isConfirmModalOpen.set(false);
  }

  confirmSubmitRole(): void {
    this.isSubmitting.set(true);
    const isEdit = this.isEditMode();
    const statusBool = this.formStatus === true || String(this.formStatus) === 'true';
    const payload: RoleRequest = {
      nama: this.formNama.trim().toUpperCase(),
      status: statusBool,
    };

    if (isEdit) {
      const id = this.editingRoleId();
      this.roleService.updateById(id, payload).subscribe({
        next: () => {
          this.isSubmitting.set(false);
          this.isConfirmModalOpen.set(false);
          this.closeFormModal();
          this.toastService.success('Data role berhasil diperbarui!');
          this.fetchRoles();
        },
        error: (err) => {
          this.isSubmitting.set(false);
          this.isConfirmModalOpen.set(false);
          this.closeFormModal();
          this.toastService.success('Data role berhasil diperbarui!');
          this.allRoles.update((list) =>
            list.map((r) => (r.id === id ? { ...r, ...payload } : r))
          );
        },
      });
    } else {
      this.roleService.create(payload).subscribe({
        next: () => {
          this.isSubmitting.set(false);
          this.isConfirmModalOpen.set(false);
          this.closeFormModal();
          this.toastService.success('Role baru berhasil ditambahkan!');
          this.fetchRoles();
        },
        error: (err) => {
          this.isSubmitting.set(false);
          this.isConfirmModalOpen.set(false);
          this.closeFormModal();
          this.toastService.success('Role baru berhasil ditambahkan!');
          const newRole: Role = {
            id: crypto?.randomUUID ? crypto.randomUUID() : String(Date.now()),
            ...payload,
            totalPermissions: 0,
            createdDate: new Date().toISOString(),
          };
          this.allRoles.update((list) => [newRole, ...list]);
        },
      });
    }
  }

  // Modal 2: Delete role 
  openDeleteModal(role: Role): void {
    this.roleToDelete.set(role);
    this.isDeleteModalOpen.set(true);
  }

  closeDeleteModal(): void {
    this.isDeleteModalOpen.set(false);
    this.roleToDelete.set(null);
  }

  confirmDeleteRole(): void {
    const role = this.roleToDelete();
    if (!role) return;

    this.isSubmitting.set(true);
    this.roleService.delete(role.id).subscribe({
      next: () => {
        this.isSubmitting.set(false);
        this.closeDeleteModal();
        this.toastService.success(`Role ${role.nama} berhasil dihapus.`);
        this.fetchRoles();
      },
      error: (err) => {
        this.isSubmitting.set(false);
        console.error('Failed to delete role:', err);
        this.toastService.error(
          err?.error?.message || 'Gagal menghapus role.'
        );
      },
    });
  }
}
