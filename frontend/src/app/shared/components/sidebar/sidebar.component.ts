import {
  Component,
  EventEmitter,
  Input,
  Output,
  OnInit,
  inject,
  signal,
} from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Router, NavigationEnd } from '@angular/router';
import { filter } from 'rxjs/operators';
import {
  LucideLayoutGrid,
  LucideUserRoundKey,
  LucideUserRoundCog,
  LucideShieldUser,
  LucideSquareMenu,
  LucideUsersRound,
  LucideBuilding2,
  LucideBanknote,
  LucideClipboardClock,
  LucideClipboardCheck,
  LucideFileUser,
  LucideUserRound,
  LucideChevronDown,
} from '@lucide/angular';
import { AuthStore } from '../../../core/store/auth.store';

export interface NavItem {
  id: string;
  label: string;
  icon?:
  | 'grid'
  | 'user-round-key'
  | 'user-round-cog'
  | 'user-shield'
  | 'square-menu'
  | 'menu'
  | 'users-round'
  | 'building'
  | 'banknote'
  | 'clipboard-clock'
  | 'clipboard-check'
  | 'file-user'
  | 'user-round'
  | string;
  url?: string;
  queryParams?: Record<string, any>;
  badge?: string | number;
  children?: NavItem[];
}

export interface NavGroup {
  title?: string;
  items: NavItem[];
}

export const MARKETING_MENU_GROUPS: NavGroup[] = [
  {
    items: [
      { id: 'beranda', label: 'Beranda', icon: 'grid', url: '/dashboard' },
      { id: 'daftar-nasabah', label: 'Daftar Nasabah', icon: 'users-round', url: '/marketing/customers' },
      {
        id: 'pengajuan-pinjaman',
        label: 'Pengajuan Pinjaman',
        icon: 'file-user',
        url: '/pengajuan-pinjaman',
        children: [
          { id: 'pengajuan-menunggu', label: 'Menunggu Review', url: '/pengajuan-pinjaman', queryParams: { status: 'MENUNGGU_REVIEW' } },
          { id: 'pengajuan-revisi', label: 'Dokumen Direvisi', url: '/pengajuan-pinjaman', queryParams: { status: 'DOKUMEN_DIREVISI' } },
          { id: 'pengajuan-disetujui', label: 'Pengajuan Disetujui', url: '/pengajuan-pinjaman', queryParams: { status: 'DISETUJUI' } },
          { id: 'pengajuan-ditolak', label: 'Pengajuan Ditolak', url: '/pengajuan-pinjaman', queryParams: { status: 'DITOLAK' } },
        ],
      },
      { id: 'profile', label: 'Profil', icon: 'user-round', url: '/profile' },
    ],
  },
];

export const BM_MENU_GROUPS: NavGroup[] = [
  {
    items: [
      { id: 'beranda', label: 'Beranda', icon: 'grid', url: '/dashboard' },
      { id: 'daftar-nasabah', label: 'Daftar Nasabah', icon: 'users-round', url: '/branch-manager/customers' },
      {
        id: 'persetujuan-pinjaman',
        label: 'Persetujuan Pinjaman',
        icon: 'file-user',
        url: '/persetujuan-pinjaman',
        children: [
          { id: 'persetujuan-menunggu', label: 'Menunggu Persetujuan', url: '/persetujuan-pinjaman', queryParams: { status: 'MENUNGGU_PERSETUJUAN' } },
          { id: 'persetujuan-disetujui', label: 'Pengajuan Disetujui', url: '/persetujuan-pinjaman', queryParams: { status: 'DISETUJUI' } },
          { id: 'persetujuan-ditolak', label: 'Pengajuan Ditolak', url: '/persetujuan-pinjaman', queryParams: { status: 'DITOLAK' } },
        ],
      },
      { id: 'profile', label: 'Profil', icon: 'user-round', url: '/profile' },
    ],
  },
];

export const BACKOFFICE_MENU_GROUPS: NavGroup[] = [
  {
    items: [
      { id: 'beranda', label: 'Beranda', icon: 'grid', url: '/dashboard' },
      {
        id: 'verifikasi-customer',
        label: 'Verifikasi Customer',
        icon: 'clipboard-check',
        url: '/verifikasi-customer',
        children: [
          { id: 'verif-pending', label: 'Pending Verifikasi', url: '/verifikasi-customer', queryParams: { status: 'PENDING' } },
          { id: 'verif-approved', label: 'Disetujui', url: '/verifikasi-customer', queryParams: { status: 'APPROVED' } },
          { id: 'verif-revisi', label: 'Perlu Revisi', url: '/verifikasi-customer', queryParams: { status: 'PERLU_REVISI' } },
          { id: 'verif-rejected', label: 'Ditolak', url: '/verifikasi-customer', queryParams: { status: 'REJECTED' } },
        ],
      },
      {
        id: 'pencairan',
        label: 'Pencairan',
        icon: 'banknote',
        url: '/pencairan',
        children: [
          { id: 'pencairan-menunggu', label: 'Menunggu Pencairan', url: '/pencairan', queryParams: { status: 'MENUNGGU_PENCAIRAN' } },
          { id: 'pencairan-dicairkan', label: 'Sudah Dicairkan', url: '/pencairan', queryParams: { status: 'DICAIRKAN' } },
        ],
      },
      { id: 'profile', label: 'Profil', icon: 'user-round', url: '/profile' },
    ],
  },
];

export const SUPERADMIN_MENU_GROUPS: NavGroup[] = [
  {
    items: [
      { id: 'dashboard', label: 'Dashboard', icon: 'grid', url: '/dashboard' },
    ],
  },
  {
    title: 'RBAC',
    items: [
      {
        id: 'role-access',
        label: 'Role Access',
        icon: 'user-round-key',
        url: '/rbac/role-access',
      },
      {
        id: 'role',
        label: 'Role',
        icon: 'user-round-key',
        url: '/rbac/role',
      },
      {
        id: 'permission',
        label: 'Permission',
        icon: 'user-round-cog',
        url: '/rbac/permission',
      },
      { id: 'menu', label: 'Menu', icon: 'square-menu', url: '/rbac/menu' },
    ],
  },
  {
    title: 'MASTER DATA',
    items: [
      {
        id: 'karyawan',
        label: 'Karyawan',
        icon: 'users-round',
        url: '/master/karyawan',
      },
      {
        id: 'cabang',
        label: 'Cabang',
        icon: 'building',
        url: '/master/cabang',
      },
      {
        id: 'plafond',
        label: 'Plafond',
        icon: 'banknote',
        url: '/master/plafond',
      },
    ],
  },
  {
    title: 'MONITORING',
    items: [
      {
        id: 'pengajuan',
        label: 'Pengajuan',
        icon: 'banknote',
        url: '/monitoring/pengajuan',
      },
      {
        id: 'audit-log',
        label: 'Audit Log',
        icon: 'clipboard-clock',
        url: '/monitoring/audit-log',
      }
    ],
  },
  {
    title: 'PROFIL',
    items: [
      { id: 'profile', label: 'Profil', icon: 'user-round', url: '/profile' },
    ],
  },
];

@Component({
  selector: 'app-sidebar',
  standalone: true,
  imports: [
    CommonModule,
    RouterModule,
    LucideLayoutGrid,
    LucideUserRoundKey,
    LucideUserRoundCog,
    LucideShieldUser,
    LucideSquareMenu,
    LucideUsersRound,
    LucideBuilding2,
    LucideBanknote,
    LucideClipboardClock,
    LucideClipboardCheck,
    LucideFileUser,
    LucideUserRound,
    LucideChevronDown,
  ],
  templateUrl: './sidebar.component.html',
  styleUrl: './sidebar.component.css',
})
export class SidebarComponent implements OnInit {
  private authStore = inject(AuthStore);
  private router = inject(Router);

  @Input() logoSrc: string = '/saku-logo.png';
  @Input() brandTitle: string = 'SAKU';
  @Input() brandSubtitle?: string;
  @Input() activeId: string = 'beranda';
  @Input() collapsed: boolean = false;
  @Input() menuGroups?: NavGroup[];

  @Output() itemSelect = new EventEmitter<NavItem>();

  expandedItems = signal<Set<string>>(new Set<string>());

  ngOnInit(): void {
    if (!this.menuGroups) {
      this.menuGroups = this.resolveMenuForRole();
    }
    this.autoExpandActiveParents();

    this.router.events
      .pipe(filter((event) => event instanceof NavigationEnd))
      .subscribe(() => {
        this.autoExpandActiveParents();
      });
  }

  get resolvedMenuGroups(): NavGroup[] {
    if (this.menuGroups && this.menuGroups.length > 0) {
      return this.menuGroups;
    }
    return this.resolveMenuForRole();
  }

  get computedSubtitle(): string {
    if (this.brandSubtitle) return this.brandSubtitle;
    const raw = this.authStore.userRole() || '';
    const clean = raw
      .toUpperCase()
      .replace(/^ROLE_/, '')
      .replace(/[\s\-_]+/g, '');

    if (clean === 'MARKETING') return 'MARKETING PORTAL';
    if (
      clean === 'BRANCHMANAGER' ||
      clean === 'BM' ||
      clean.includes('BRANCH') ||
      clean.includes('MANAGER')
    ) {
      return 'BRANCH MANAGER PORTAL';
    }
    if (
      clean === 'BACKOFFICE' ||
      clean === 'BO' ||
      clean.includes('BACKOFFICE')
    ) {
      return 'BACKOFFICE PORTAL';
    }
    if (
      clean === 'SUPERADMIN' ||
      clean === 'ADMIN' ||
      clean.includes('ADMIN')
    ) {
      return 'SUPERADMIN PORTAL';
    }
    return 'KARYAWAN PORTAL';
  }

  resolveMenuForRole(): NavGroup[] {
    const raw = this.authStore.userRole() || '';
    const clean = raw
      .toUpperCase()
      .replace(/^ROLE_/, '')
      .replace(/[\s\-_]+/g, '');

    if (clean === 'MARKETING') {
      return MARKETING_MENU_GROUPS;
    }
    if (
      clean === 'BRANCHMANAGER' ||
      clean === 'BM' ||
      clean.includes('BRANCH') ||
      clean.includes('MANAGER')
    ) {
      return BM_MENU_GROUPS;
    }
    if (
      clean === 'BACKOFFICE' ||
      clean === 'BO' ||
      clean.includes('BACKOFFICE')
    ) {
      return BACKOFFICE_MENU_GROUPS;
    }
    if (
      clean === 'SUPERADMIN' ||
      clean === 'ADMIN' ||
      clean.includes('ADMIN')
    ) {
      return SUPERADMIN_MENU_GROUPS;
    }
    return BM_MENU_GROUPS;
  }

  toggleExpand(itemId: string, event?: Event): void {
    if (event) {
      event.stopPropagation();
      event.preventDefault();
    }
    const current = new Set(this.expandedItems());
    if (current.has(itemId)) {
      current.delete(itemId);
    } else {
      current.add(itemId);
    }
    this.expandedItems.set(current);
  }

  isExpanded(itemId: string): boolean {
    return this.expandedItems().has(itemId);
  }

  hasChildren(item: NavItem): boolean {
    return !!(item.children && item.children.length > 0);
  }

  isChildActive(child: NavItem): boolean {
    if (!child.url) return false;
    const urlTree = this.router.parseUrl(this.router.url);
    const primaryPath =
      '/' +
      (urlTree.root.children['primary']
        ? urlTree.root.children['primary'].segments.map((s) => s.path).join('/')
        : '');

    if (primaryPath !== child.url && !primaryPath.startsWith(child.url + '/')) {
      return false;
    }

    if (child.queryParams !== undefined && child.queryParams !== null) {
      const targetStatus = child.queryParams['status'] ?? '';
      const currentStatus = urlTree.queryParams['status'] ?? '';
      return targetStatus === currentStatus;
    }

    return true;
  }

  onParentClick(item: NavItem, event: MouseEvent): void {
    if (item.url) {
      this.router.navigateByUrl(item.url);
      const current = new Set(this.expandedItems());
      current.add(item.id);
      this.expandedItems.set(current);
      this.onSelect(item, event);
    } else {
      this.toggleExpand(item.id, event);
    }
  }

  isParentOnlyActive(item: NavItem): boolean {
    if (!item.url) return false;
    const urlTree = this.router.parseUrl(this.router.url);
    const primaryPath =
      '/' +
      (urlTree.root.children['primary']
        ? urlTree.root.children['primary'].segments.map((s) => s.path).join('/')
        : '');

    if (primaryPath !== item.url) return false;

    if (this.hasChildren(item)) {
      const anyChildActive = item.children!.some((child) => this.isChildActive(child));
      return !anyChildActive;
    }
    return true;
  }

  isParentActive(item: NavItem): boolean {
    if (this.hasChildren(item)) {
      return item.children!.some((child) => this.isChildActive(child));
    }
    return this.isActive(item);
  }

  private autoExpandActiveParents(): void {
    const current = new Set(this.expandedItems());
    for (const group of this.resolvedMenuGroups || []) {
      for (const item of group.items) {
        if (this.hasChildren(item) && this.isParentActive(item)) {
          current.add(item.id);
        }
      }
    }
    this.expandedItems.set(current);
  }

  onSelect(item: NavItem, event: MouseEvent): void {
    this.activeId = item.id;
    this.itemSelect.emit(item);
  }

  isExactMatch(item: NavItem): boolean {
    if (!item.url) return true;
    return (
      item.url === '/dashboard' ||
      item.url === '/beranda' ||
      item.url === '/' ||
      item.url === '/profile'
    );
  }

  isActive(item: NavItem): boolean {
    if (!item.url) {
      return this.activeId === item.id;
    }
    const currentUrl = this.router.url.split('?')[0].split('#')[0];
    if (item.url === '/dashboard' || item.url === '/beranda' || item.url === '/') {
      return currentUrl === '/dashboard' || currentUrl === '/beranda';
    }
    return currentUrl === item.url || currentUrl.startsWith(item.url + '/');
  }
}
