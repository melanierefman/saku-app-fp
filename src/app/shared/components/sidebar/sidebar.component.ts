import {
  Component,
  EventEmitter,
  Input,
  Output,
  OnInit,
  inject,
} from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Router } from '@angular/router';
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
} from '@lucide/angular';
import { AuthStore } from '../../../core/store/auth.store';

export interface NavItem {
  id: string;
  label: string;
  icon:
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
  badge?: string | number;
}

export interface NavGroup {
  title?: string;
  items: NavItem[];
}

export const MARKETING_MENU_GROUPS: NavGroup[] = [
  {
    items: [
      { id: 'beranda', label: 'Beranda', icon: 'grid', url: '/dashboard' },
      {
        id: 'pengajuan-pinjaman',
        label: 'Pengajuan Pinjaman',
        icon: 'file-user',
        url: '/pengajuan-pinjaman',
      },
      { id: 'profile', label: 'Profil', icon: 'user-round', url: '/profile' },
    ],
  },
];

export const BM_MENU_GROUPS: NavGroup[] = [
  {
    items: [
      { id: 'beranda', label: 'Beranda', icon: 'grid', url: '/dashboard' },
      {
        id: 'persetujuan-pinjaman',
        label: 'Persetujuan Pinjaman',
        icon: 'file-user',
        url: '/persetujuan-pinjaman',
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
      },
      {
        id: 'pencairan',
        label: 'Pencairan',
        icon: 'banknote',
        url: '/pencairan',
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

  ngOnInit(): void {
    if (!this.menuGroups) {
      this.menuGroups = this.resolveMenuForRole();
    }
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
