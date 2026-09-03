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
  ModalComponent,
  ToastService,
} from '../../../shared/components';
import { AuditLog, AuditLogService } from '../../../core';
import {
  LucideSearch,
  LucideX,
  LucideEye,
  LucideUser,
  LucideLayers,
  LucideTerminal,
} from '@lucide/angular';

@Component({
  selector: 'app-audit-log',
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
    ModalComponent,
    LucideSearch,
    LucideX,
    LucideEye,
    LucideUser,
    LucideLayers,
    LucideTerminal,
  ],
  templateUrl: './audit-log.component.html',
  styleUrl: './audit-log.component.css',
})
export class AuditLogComponent implements OnInit {
  private auditLogService = inject(AuditLogService);
  private toastService = inject(ToastService);
  private platformId = inject(PLATFORM_ID);
  private cdr = inject(ChangeDetectorRef);

  columns: TableColumn[] = [
    { key: 'no', header: 'No', width: '60px', align: 'center' },
    { key: 'timestamp', header: 'Waktu Aktivitas', sortable: true },
    { key: 'user', header: 'Pengguna & Role', sortable: true },
    { key: 'action', header: 'Aksi', sortable: true, align: 'center' },
    { key: 'entity', header: 'Entitas', sortable: true },
    {
      key: 'description',
      header: 'Deskripsi Aktivitas',
      width: '400px',
      cellClass: 'whitespace-normal',
    },
    { key: 'actions', header: 'Detail', width: '80px', align: 'center' },
  ];

  // Sorting Signals
  sortKey = signal<string>('');
  sortDirection = signal<'asc' | 'desc' | ''>('');

  // Table Data & Loading Signals
  allLogs = signal<AuditLog[]>([]);
  isLoading = signal<boolean>(false);

  // Filters
  searchKeyword = signal<string>('');
  selectedActionFilter = signal<string>('');
  selectedEntityFilter = signal<string>('');
  currentPage = signal<number>(1);
  pageSize = signal<number>(10);

  // Detail Modal
  isDetailModalOpen = signal<boolean>(false);
  selectedLog = signal<AuditLog | null>(null);

  readonly actionOptions: DropdownOption[] = [
    { value: 'LOGIN', label: 'LOGIN' },
    { value: 'LOGOUT', label: 'LOGOUT' },
    { value: 'CREATE', label: 'CREATE' },
    { value: 'UPDATE', label: 'UPDATE' },
    { value: 'DELETE', label: 'DELETE' },
    { value: 'APPROVE', label: 'APPROVE' },
    { value: 'REJECT', label: 'REJECT' },
  ];

  readonly entityOptions: DropdownOption[] = [
    { value: 'AUTH', label: 'AUTH (Autentikasi)' },
    { value: 'KARYAWAN', label: 'KARYAWAN' },
    { value: 'ROLE', label: 'ROLE' },
    { value: 'CABANG', label: 'CABANG' },
    { value: 'PLAFOND', label: 'PLAFOND' },
    { value: 'PENGAJUAN', label: 'PENGAJUAN' },
    { value: 'MENU', label: 'MENU' },
    { value: 'PERMISSION', label: 'PERMISSION' },
  ];

  filteredLogs = computed(() => {
    const list = this.allLogs();
    const q = this.searchKeyword().trim().toLowerCase();
    const actionVal = this.selectedActionFilter();
    const entityVal = this.selectedEntityFilter();

    let result = list.filter((log) => {
      // 1. Search keyword
      if (q) {
        const userNama = (log.karyawan?.nama || log.karyawanNama || '').toLowerCase();
        const username = (log.karyawan?.username || log.username || '').toLowerCase();
        const email = (log.karyawan?.email || '').toLowerCase();
        const cabang = (log.karyawan?.cabang || '').toLowerCase();
        const ent = (log.entity || '').toLowerCase();
        const desc = (log.description || '').toLowerCase();
        if (
          !userNama.includes(q) &&
          !username.includes(q) &&
          !email.includes(q) &&
          !cabang.includes(q) &&
          !ent.includes(q) &&
          !desc.includes(q)
        ) {
          return false;
        }
      }

      // 2. Action Filter
      if (actionVal) {
        if ((log.action || '').toUpperCase() !== actionVal.toUpperCase()) {
          return false;
        }
      }

      // 3. Entity Filter
      if (entityVal) {
        if ((log.entity || '').toUpperCase() !== entityVal.toUpperCase()) {
          return false;
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

        if (key === 'user') {
          valA = a.karyawan?.nama || a.username || a.karyawanNama || '';
          valB = b.karyawan?.nama || b.username || b.karyawanNama || '';
        } else if (key === 'timestamp') {
          valA = new Date(a.createdDate || a.timestamp || 0).getTime();
          valB = new Date(b.createdDate || b.timestamp || 0).getTime();
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

  totalElements = computed(() => this.filteredLogs().length);

  paginatedLogs = computed(() => {
    const list = this.filteredLogs();
    const page = this.currentPage();
    const size = this.pageSize();
    const start = (page - 1) * size;
    return list.slice(start, start + size);
  });

  hasActiveFilters = computed(() => {
    return !!(
      this.searchKeyword().trim() ||
      this.selectedActionFilter() ||
      this.selectedEntityFilter()
    );
  });

  ngOnInit(): void {
    if (!isPlatformBrowser(this.platformId)) return;
    this.fetchAuditLogs();
  }

  fetchAuditLogs(): void {
    this.isLoading.set(true);
    this.auditLogService
      .getAuditLogs({
        page: 0,
        size: 100,
        keyword: this.searchKeyword(),
        action: this.selectedActionFilter(),
        entity: this.selectedEntityFilter(),
      })
      .subscribe({
        next: (res) => {
          const logs = res?.logs || res?.content || [];
          this.allLogs.set(logs);
          this.isLoading.set(false);
          this.cdr.detectChanges();
        },
        error: (err) => {
          console.error('Failed to load audit logs:', err);
          this.allLogs.set([]);
          this.isLoading.set(false);
          this.cdr.detectChanges();
        },
      });
  }

  // Filter Handlers
  onSearchChange(keyword: string): void {
    this.searchKeyword.set(keyword || '');
    this.currentPage.set(1);
  }

  onActionFilterChange(action: string): void {
    this.selectedActionFilter.set(action || '');
    this.currentPage.set(1);
  }

  onEntityFilterChange(entity: string): void {
    this.selectedEntityFilter.set(entity || '');
    this.currentPage.set(1);
  }

  clearFilters(): void {
    this.searchKeyword.set('');
    this.selectedActionFilter.set('');
    this.selectedEntityFilter.set('');
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
  openDetail(log: AuditLog): void {
    this.selectedLog.set(log);
    this.isDetailModalOpen.set(true);
  }

  closeDetailModal(): void {
    this.isDetailModalOpen.set(false);
    this.selectedLog.set(null);
  }

  // Formatters & Helpers
  formatTimestamp(timestamp?: string): string {
    if (!timestamp) return '-';
    try {
      const d = new Date(timestamp);
      if (isNaN(d.getTime())) return timestamp;
      return d.toLocaleString('id-ID', {
        day: '2-digit',
        month: 'short',
        year: 'numeric',
        hour: '2-digit',
        minute: '2-digit',
        second: '2-digit',
      });
    } catch {
      return timestamp;
    }
  }

  getActionBadgeVariant(action?: string): BadgeVariant {
    const a = (action || '').toUpperCase();
    if (a === 'CREATE' || a === 'APPROVE') return 'success';
    if (a === 'UPDATE') return 'warning';
    if (a === 'DELETE' || a === 'REJECT') return 'error';
    if (a === 'LOGIN' || a === 'LOGOUT') return 'primary';
    return 'neutral';
  }

  formatRole(role?: string): string {
    if (!role) return '-';
    const r = role.toUpperCase().replace(/[_\s-]+/g, '');
    switch (r) {
      case 'SUPERADMIN':
        return 'Super Admin';
      case 'BRANCHMANAGER':
        return 'Branch Manager';
      case 'MARKETING':
        return 'Marketing';
      case 'BACKOFFICE':
        return 'Backoffice';
      case 'CUSTOMER':
        return 'Customer';
      default:
        return role
          .replace(/[_-]/g, ' ')
          .toLowerCase()
          .replace(/\b\w/g, (c) => c.toUpperCase());
    }
  }

  getDetailsJson(details: any): string {
    if (!details) return '-';
    if (typeof details === 'string') {
      try {
        const parsed = JSON.parse(details);
        return JSON.stringify(parsed, null, 2);
      } catch {
        return details;
      }
    }
    return JSON.stringify(details, null, 2);
  }
}
