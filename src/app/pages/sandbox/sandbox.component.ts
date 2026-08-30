import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import {
  ButtonComponent,
  BadgeComponent,
  InputComponent,
  InputNumberComponent,
  TextareaComponent,
  InputAmountComponent,
  CheckboxComponent,
  RadioComponent,
  DropdownComponent,
  DropdownOption,
  ModalComponent,
  ToastService,
  BreadcrumbsComponent,
  BreadcrumbItem,
  SidebarComponent,
  NavItem,
  DatePickerComponent,
  DateRangeValue,
  TableComponent,
  TableCellDirective,
  TableColumn,
  PaginationComponent,
} from '../../shared/components';

import {
  LucidePlus,
  LucideArrowRight,
  LucideSearch,
  LucideMail,
  LucideFilter,
  LucideLayout,
  LucideTrash2,
  LucideX,
  LucideCircleAlert,
  LucideCircleCheck,
  LucideCircleX,
  LucideTriangleAlert,
  LucideRefreshCw,
} from '@lucide/angular';

@Component({
  selector: 'app-sandbox',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    ButtonComponent,
    BadgeComponent,
    InputComponent,
    InputNumberComponent,
    TextareaComponent,
    InputAmountComponent,
    CheckboxComponent,
    RadioComponent,
    DropdownComponent,
    ModalComponent,
    BreadcrumbsComponent,
    SidebarComponent,
    DatePickerComponent,
    TableComponent,
    TableCellDirective,
    PaginationComponent,
    LucidePlus,
    LucideArrowRight,
    LucideSearch,
    LucideMail,
    LucideFilter,
    LucideLayout,
    LucideTrash2,
    LucideX,
    LucideCircleAlert,
    LucideCircleCheck,
    LucideCircleX,
    LucideTriangleAlert,
    LucideRefreshCw,
  ],
  templateUrl: './sandbox.component.html',
  styleUrl: './sandbox.component.css',
})
export class SandboxComponent {
  readonly toastService = inject(ToastService);

  // Date Picker Demo States
  demoSingleDate: Date | null = new Date();
  demoDateRange: DateRangeValue = {
    start: new Date(2026, 7, 1),
    end: new Date(2026, 7, 27),
  };
  demoFilterDateRange: DateRangeValue | null = null;

  // Breadcrumbs Demo Items
  breadcrumbItems: BreadcrumbItem[] = [
    { label: 'Dashboard', url: '/dashboard' },
    { label: 'Detail Verifikasi Customer', url: '/verification' },
    { label: 'No. PJ – 202608 – 000123', active: true },
  ];

  breadcrumbMasterItems: BreadcrumbItem[] = [
    { label: 'Beranda', url: '/dashboard' },
    { label: 'Master Data', url: '/master' },
    { label: 'Cabang', url: '/master/cabang' },
    { label: 'Tambah Cabang Baru', active: true },
  ];

  // Sidebar Demo Active Item
  sidebarActiveMenu: string = 'role';
  sidebarCollapsed: boolean = false;

  onSidebarNavSelect(item: NavItem): void {
    this.sidebarActiveMenu = item.id;
    this.toastService.info(`Navigasi ke menu: ${item.label}`);
  }

  toggleSidebarCollapse(): void {
    this.sidebarCollapsed = !this.sidebarCollapsed;
  }

  // Demo tag list untuk removable test
  tags: string[] = ['Angular', 'Tailwind', 'Saku Pay', 'Verified'];

  // Demo Form State untuk test ControlValueAccessor / Two-Way Binding
  demoText: string = 'PT Saku Digital Nusantara';
  demoSearch: string = '';
  demoNumber: string = '45';
  demoEmail: string = 'admin@saku.id';
  demoAmount: number = 2500000;
  demoTextarea: string =
    'Platform digital terpercaya untuk pengelolaan transaksi finansial cepat, efisien, dan aman.';

  // Plafond & Loan Setting Demo State
  minScore: number = 999;
  maxScore: number = 999;
  minPlafond: number | null = null;
  maxPlafond: number | null = null;
  bunga: number = 999;
  biayaAdmin: number | null = null;

  demoCheckbox1: boolean = true;
  demoCheckbox2: boolean = false;
  demoCheckboxIndeterminate: boolean = true;

  demoRole: string = 'superadmin';
  demoBranch: string = '';

  readonly roleOptions: DropdownOption[] = [
    { value: 'superadmin', label: 'Superadmin' },
    { value: 'marketing', label: 'Marketing' },
    { value: 'bm', label: 'Branch Manager (BM)' },
    { value: 'backoffice', label: 'Back Office' },
  ];

  readonly branchOptions: DropdownOption[] = [
    { value: 'JKT-01', label: 'Jakarta Pusat (Kantor Pusat)' },
    { value: 'BDG-01', label: 'Bandung - Dago' },
    { value: 'SBY-01', label: 'Surabaya - Gubeng' },
    { value: 'MDN-01', label: 'Medan - Merdeka' },
    { value: 'DPS-01', label: 'Denpasar - Sunset Road' },
  ];

  readonly cityOptions: DropdownOption[] = [
    { value: 'jakarta', label: 'Jakarta' },
    { value: 'bandung', label: 'Bandung' },
    { value: 'surabaya', label: 'Surabaya' },
    { value: 'medan', label: 'Medan' },
    { value: 'denpasar', label: 'Denpasar' },
  ];

  readonly statusFilterOptions: DropdownOption[] = [
    { value: 'success', label: 'Berhasil (Success)' },
    { value: 'pending', label: 'Menunggu (Pending)' },
    { value: 'failed', label: 'Gagal (Failed)' },
  ];

  selectedStatusFilter: string = '';

  // Modal State
  isFormModalOpen: boolean = false;
  isConfirmModalOpen: boolean = false;

  // Form Modal State
  modalCabangNama: string = '';
  modalCabangKota: string = '';
  modalCabangDefault: string = 'Ya';
  modalCabangStatus: string = 'Aktif';

  openFormModal(): void {
    this.isFormModalOpen = true;
  }

  closeFormModal(): void {
    this.isFormModalOpen = false;
  }

  submitFormModal(): void {
    this.isFormModalOpen = false;
    this.toastService.success('Cabang baru berhasil ditambahkan.');
  }

  openConfirmModal(): void {
    this.isConfirmModalOpen = true;
  }

  closeConfirmModal(): void {
    this.isConfirmModalOpen = false;
  }

  submitConfirmModal(): void {
    this.isConfirmModalOpen = false;
    this.toastService.error('Item has been deleted.');
  }

  // Trigger Toasts
  showSuccessToast(): void {
    this.toastService.success('Item moved successfully.');
  }

  showErrorToast(): void {
    this.toastService.error('Item has been deleted.');
  }

  showWarningToast(): void {
    this.toastService.warning('Improve password difficulty.');
  }

  removeTag(tagToRemove: string): void {
    this.tags = this.tags.filter((tag) => tag !== tagToRemove);
  }

  resetTags(): void {
    this.tags = ['Angular', 'Tailwind', 'Saku Pay', 'Verified'];
  }

  // ==========================================
  // Permission Table Demo States (Phase 4)
  // ==========================================
  tableSearchQuery: string = '';
  selectedResourceFilter: string = '';
  selectedActionFilter: string = '';
  tableCurrentPage: number = 1;
  tablePageSize: number = 10;
  isTableLoading: boolean = false;

  resourceFilterOptions: DropdownOption[] = [
    { value: 'DASHBOARD', label: 'DASHBOARD' },
    { value: 'CUSTOMER', label: 'CUSTOMER' },
    { value: 'PENGAJUAN', label: 'PENGAJUAN' },
    { value: 'REVIEW_PENGAJUAN', label: 'REVIEW_PENGAJUAN' },
    { value: 'ROLE', label: 'ROLE' },
    { value: 'USER', label: 'USER' },
  ];

  actionFilterOptions: DropdownOption[] = [
    { value: 'VIEW', label: 'VIEW' },
    { value: 'CREATE', label: 'CREATE' },
    { value: 'UPDATE', label: 'UPDATE' },
    { value: 'DELETE', label: 'DELETE' },
    { value: 'APPROVE', label: 'APPROVE' },
    { value: 'REJECT', label: 'REJECT' },
  ];

  permissionColumns: TableColumn[] = [
    { key: 'no', header: 'No', width: '64px' },
    { key: 'permission', header: 'Permission' },
    { key: 'resource', header: 'Resource' },
    { key: 'action', header: 'Action' },
    { key: 'menu', header: 'Menu' },
    { key: 'actions', header: 'Actions', width: '130px', align: 'right', tooltip: 'Informasi aksi permission' },
  ];

  allPermissions = [
    { id: 1, permission: 'View Dashboard', resource: 'DASHBOARD', action: 'VIEW', menu: 'Dashboard' },
    { id: 2, permission: 'View Customer', resource: 'CUSTOMER', action: 'VIEW', menu: 'Customer' },
    { id: 3, permission: 'Create Customer', resource: 'CUSTOMER', action: 'CREATE', menu: 'Customer' },
    { id: 4, permission: 'Update Customer', resource: 'CUSTOMER', action: 'UPDATE', menu: 'Customer' },
    { id: 5, permission: 'Delete Customer', resource: 'CUSTOMER', action: 'DELETE', menu: 'Customer' },
    { id: 6, permission: 'View Pengajuan', resource: 'PENGAJUAN', action: 'VIEW', menu: 'Pengajuan' },
    { id: 7, permission: 'Create Pengajuan', resource: 'PENGAJUAN', action: 'CREATE', menu: 'Pengajuan' },
    { id: 8, permission: 'Update Pengajuan', resource: 'PENGAJUAN', action: 'UPDATE', menu: 'Pengajuan' },
    { id: 9, permission: 'Delete Pengajuan', resource: 'PENGAJUAN', action: 'DELETE', menu: 'Pengajuan' },
    { id: 10, permission: 'View Pengajuan', resource: 'REVIEW_PENGAJUAN', action: 'VIEW', menu: 'Review Pengajuan' },
    { id: 11, permission: 'Approve Pengajuan', resource: 'REVIEW_PENGAJUAN', action: 'APPROVE', menu: 'Review Pengajuan' },
    { id: 12, permission: 'Reject Pengajuan', resource: 'REVIEW_PENGAJUAN', action: 'REJECT', menu: 'Review Pengajuan' },
    { id: 13, permission: 'View Role', resource: 'ROLE', action: 'VIEW', menu: 'Role Access' },
    { id: 14, permission: 'Create Role', resource: 'ROLE', action: 'CREATE', menu: 'Role Access' },
    { id: 15, permission: 'Update Role', resource: 'ROLE', action: 'UPDATE', menu: 'Role Access' },
    { id: 16, permission: 'Delete Role', resource: 'ROLE', action: 'DELETE', menu: 'Role Access' },
    { id: 17, permission: 'View User', resource: 'USER', action: 'VIEW', menu: 'User Management' },
    { id: 18, permission: 'Create User', resource: 'USER', action: 'CREATE', menu: 'User Management' },
    { id: 19, permission: 'Update User', resource: 'USER', action: 'UPDATE', menu: 'User Management' },
    { id: 20, permission: 'Delete User', resource: 'USER', action: 'DELETE', menu: 'User Management' },
    { id: 21, permission: 'Export Report', resource: 'DASHBOARD', action: 'VIEW', menu: 'Dashboard' },
    { id: 22, permission: 'Audit Log View', resource: 'AUDIT', action: 'VIEW', menu: 'Audit Log' },
    { id: 23, permission: 'Audit Log Export', resource: 'AUDIT', action: 'CREATE', menu: 'Audit Log' },
    { id: 24, permission: 'System Setting', resource: 'SETTING', action: 'UPDATE', menu: 'Pengaturan' },
    { id: 25, permission: 'View Master Cabang', resource: 'CABANG', action: 'VIEW', menu: 'Cabang' },
  ];

  get filteredPermissions() {
    return this.allPermissions.filter((item) => {
      const matchQuery =
        !this.tableSearchQuery.trim() ||
        item.permission.toLowerCase().includes(this.tableSearchQuery.toLowerCase()) ||
        item.resource.toLowerCase().includes(this.tableSearchQuery.toLowerCase()) ||
        item.action.toLowerCase().includes(this.tableSearchQuery.toLowerCase()) ||
        item.menu.toLowerCase().includes(this.tableSearchQuery.toLowerCase());

      const matchResource =
        !this.selectedResourceFilter || item.resource === this.selectedResourceFilter;
      const matchAction =
        !this.selectedActionFilter || item.action === this.selectedActionFilter;

      return matchQuery && matchResource && matchAction;
    });
  }

  get paginatedPermissions() {
    const startIndex = (this.tableCurrentPage - 1) * this.tablePageSize;
    return this.filteredPermissions
      .slice(startIndex, startIndex + this.tablePageSize)
      .map((item, idx) => ({
        ...item,
        no: startIndex + idx + 1,
      }));
  }

  onTableSearchChange(): void {
    this.tableCurrentPage = 1;
  }

  clearTableFilters(): void {
    this.tableSearchQuery = '';
    this.selectedResourceFilter = '';
    this.selectedActionFilter = '';
    this.tableCurrentPage = 1;
  }

  get hasActiveTableFilters(): boolean {
    return !!(this.tableSearchQuery.trim() || this.selectedResourceFilter || this.selectedActionFilter);
  }

  onTablePageChange(page: number): void {
    this.tableCurrentPage = page;
  }

  viewPermissionDetail(row: any): void {
    this.toastService.info(`Melihat detail permission: ${row.permission} (${row.resource})`);
  }

  simulateTableLoading(): void {
    this.isTableLoading = true;
    setTimeout(() => {
      this.isTableLoading = false;
    }, 1000);
  }
}

