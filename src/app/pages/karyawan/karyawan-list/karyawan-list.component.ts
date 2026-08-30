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
  ToastService,
} from '../../../shared/components';
import {
  Karyawan,
  KaryawanService,
  Role,
  RoleService,
  Cabang,
  CabangService,
  formatRoleName,
} from '../../../core';
import {
  LucideSearch,
  LucidePlus,
  LucideX,
} from '@lucide/angular';

@Component({
  selector: 'app-karyawan-list',
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
    LucideSearch,
    LucidePlus,
    LucideX,
  ],
  templateUrl: './karyawan-list.component.html',
  styleUrl: './karyawan-list.component.css',
})
export class KaryawanListComponent implements OnInit {
  private router = inject(Router);
  private karyawanService = inject(KaryawanService);
  private roleService = inject(RoleService);
  private cabangService = inject(CabangService);
  private toastService = inject(ToastService);
  private platformId = inject(PLATFORM_ID);

  breadcrumbs: BreadcrumbItem[] = [
    { label: 'Dashboard', url: '/dashboard' },
    { label: 'Daftar Karyawan', active: true },
  ];

  columns: TableColumn[] = [
    { key: 'no', header: 'No', width: '64px' },
    { key: 'nama', header: 'Karyawan' },
    { key: 'username', header: 'Username' },
    { key: 'email', header: 'Email' },
    { key: 'role', header: 'Role' },
    { key: 'cabang', header: 'Cabang' },
    { key: 'status', header: 'Status' },
  ];

  // Reactive State Signals (Clean API data)
  allKaryawanList = signal<Karyawan[]>([]);
  roles = signal<Role[]>([]);
  branches = signal<Cabang[]>([]);
  isLoading = signal<boolean>(false);

  // Filter State Signals
  searchQuery = signal<string>('');
  selectedRoleFilter = signal<string>('');
  selectedBranchFilter = signal<string>('');
  selectedStatusFilter = signal<string>('');

  // Pagination State Signals
  currentPage = signal<number>(1);
  pageSize = signal<number>(10);

  // Dropdown options
  roleFilterOptions = signal<DropdownOption[]>([]);
  branchFilterOptions = signal<DropdownOption[]>([]);
  readonly statusFilterOptions: DropdownOption[] = [
    { value: 'true', label: 'Aktif' },
    { value: 'false', label: 'Tidak Aktif' },
  ];

  // Reactive Computed: Filtered List
  filteredKaryawanList = computed(() => {
    const list = this.allKaryawanList();
    const q = this.searchQuery().trim().toLowerCase();
    const roleId = this.selectedRoleFilter();
    const branchId = this.selectedBranchFilter();
    const statusVal = this.selectedStatusFilter();
    const currentRoles = this.roles();
    const currentBranches = this.branches();

    return list.filter((item) => {
      // 1. Text Search
      if (q) {
        const name = (item.nama || '').toLowerCase();
        const username = (item.username || '').toLowerCase();
        const email = (item.email || '').toLowerCase();
        const role = (item.roleNama || item.roleName || this.resolveRoleName(item.mstRoleId) || '').toLowerCase();
        const branch = (item.cabangNama || item.branchName || this.resolveBranchName(item.mstBranchId) || '').toLowerCase();

        const match =
          name.includes(q) ||
          username.includes(q) ||
          email.includes(q) ||
          role.includes(q) ||
          branch.includes(q);

        if (!match) return false;
      }

      // 2. Role Filter
      if (roleId) {
        const targetRole = currentRoles.find((r) => r.id === roleId);
        const targetRoleName = targetRole ? targetRole.nama.toLowerCase() : '';
        const itemRole = (item.roleNama || '').toLowerCase();
        const matchesRole =
          item.mstRoleId === roleId ||
          (itemRole !== '' && itemRole === targetRoleName);
        if (!matchesRole) return false;
      }

      // 3. Branch Filter
      if (branchId) {
        const targetBranch = currentBranches.find((b) => b.id === branchId);
        const targetBranchName = targetBranch ? targetBranch.nama.toLowerCase() : '';
        const itemBranch = (item.cabangNama || '').toLowerCase();
        const matchesBranch =
          item.mstBranchId === branchId ||
          (itemBranch !== '' && itemBranch === targetBranchName);
        if (!matchesBranch) return false;
      }

      // 4. Status Filter
      if (statusVal !== '' && statusVal !== null && statusVal !== undefined) {
        const statusBool = statusVal === 'true';
        if (item.status !== statusBool) return false;
      }

      return true;
    }).sort((a, b) => {
      const timeA = a.updatedDate ? new Date(a.updatedDate).getTime() : (a.createdDate ? new Date(a.createdDate).getTime() : 0);
      const timeB = b.updatedDate ? new Date(b.updatedDate).getTime() : (b.createdDate ? new Date(b.createdDate).getTime() : 0);
      return timeB - timeA;
    });
  });

  // Reactive Computed: Total Elements Count
  totalElements = computed(() => this.filteredKaryawanList().length);

  // Reactive Computed: Table Rows for Current Page
  tableData = computed(() => {
    const list = this.filteredKaryawanList();
    const page = this.currentPage();
    const size = this.pageSize();
    const startIndex = (page - 1) * size;

    return list.slice(startIndex, startIndex + size).map((item, idx) => ({
      ...item,
      no: startIndex + idx + 1,
      roleName: formatRoleName(
        item.roleNama || item.roleName || this.resolveRoleName(item.mstRoleId)
      ),
      branchName:
        item.cabangNama ||
        item.branchName ||
        this.resolveBranchName(item.mstBranchId),
    }));
  });

  // Reactive Computed: Has Active Filters
  hasActiveFilters = computed(() => {
    return !!(
      this.searchQuery().trim() ||
      this.selectedRoleFilter() ||
      this.selectedBranchFilter() ||
      (this.selectedStatusFilter() !== '' &&
        this.selectedStatusFilter() !== null &&
        this.selectedStatusFilter() !== undefined)
    );
  });

  ngOnInit(): void {
    if (!isPlatformBrowser(this.platformId)) return;

    this.loadRoles();
    this.loadBranches();
    this.fetchKaryawan();
  }

  loadRoles(): void {
    this.roleService.getAll().subscribe({
      next: (roles) => {
        const list = roles || [];
        this.roles.set(list);
        this.roleFilterOptions.set(
          list.map((r) => ({
            value: r.id,
            label: formatRoleName(r.nama),
          }))
        );
      },
      error: (err) => {
        console.error('Failed to load roles from API:', err);
      },
    });
  }

  loadBranches(): void {
    this.cabangService.getAll().subscribe({
      next: (branches) => {
        const list = branches || [];
        this.branches.set(list);
        this.branchFilterOptions.set(
          list.map((b) => ({
            value: b.id,
            label: b.nama,
          }))
        );
      },
      error: (err) => {
        console.error('Failed to load branches from API:', err);
      },
    });
  }

  resolveRoleName(roleId: string): string {
    const match = this.roles().find((r) => r.id === roleId);
    if (match) return formatRoleName(match.nama);
    return formatRoleName(roleId);
  }

  resolveBranchName(branchId: string): string {
    const match = this.branches().find((b) => b.id === branchId);
    if (match) return match.nama;
    return branchId || '-';
  }

  fetchKaryawan(): void {
    this.isLoading.set(true);

    this.karyawanService.getAll().subscribe({
      next: (data) => {
        this.allKaryawanList.set(data || []);
        this.isLoading.set(false);
      },
      error: (err) => {
        console.error('Failed to load karyawan list from API:', err);
        this.allKaryawanList.set([]);
        this.isLoading.set(false);
      },
    });
  }

  onSearchChange(query: string): void {
    this.searchQuery.set(query || '');
    this.currentPage.set(1);
  }

  onRoleFilterChange(roleId: string): void {
    this.selectedRoleFilter.set(roleId || '');
    this.currentPage.set(1);
  }

  onBranchFilterChange(branchId: string): void {
    this.selectedBranchFilter.set(branchId || '');
    this.currentPage.set(1);
  }

  onStatusFilterChange(status: string): void {
    this.selectedStatusFilter.set(status || '');
    this.currentPage.set(1);
  }

  clearFilters(): void {
    this.searchQuery.set('');
    this.selectedRoleFilter.set('');
    this.selectedBranchFilter.set('');
    this.selectedStatusFilter.set('');
    this.currentPage.set(1);
  }

  onPageChange(page: number): void {
    this.currentPage.set(page);
  }

  navigateToCreate(): void {
    this.router.navigate(['/master/karyawan/tambah']);
  }

  navigateToEdit(id: string): void {
    this.router.navigate(['/master/karyawan/edit', id]);
  }
}
