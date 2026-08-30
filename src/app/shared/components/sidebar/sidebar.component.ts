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
      },
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

  get computedSubtitle(): string {
    if (this.brandSubtitle) return this.brandSubtitle;
    const role = this.authStore.userRole()?.toUpperCase();
    if (role === 'MARKETING') return 'MARKETING PORTAL';
    if (role === 'BRANCH_MANAGER' || role === 'BM') return 'BRANCH MANAGER PORTAL';
    if (role === 'BACKOFFICE' || role === 'BACK_OFFICE') return 'BACKOFFICE PORTAL';
    if (role === 'SUPERADMIN' || role === 'ADMIN') return 'SUPERADMIN PORTAL';
    return 'KARYAWAN PORTAL';
  }

  resolveMenuForRole(): NavGroup[] {
    const role = this.authStore.userRole()?.toUpperCase();
    switch (role) {
      case 'MARKETING':
        return MARKETING_MENU_GROUPS;
      case 'BRANCH_MANAGER':
      case 'BM':
        return BM_MENU_GROUPS;
      case 'BACKOFFICE':
      case 'BACK_OFFICE':
        return BACKOFFICE_MENU_GROUPS;
      case 'SUPERADMIN':
      case 'ADMIN':
        return SUPERADMIN_MENU_GROUPS;
      default:
        return MARKETING_MENU_GROUPS;
    }
  }

  onSelect(item: NavItem, event: MouseEvent): void {
    this.activeId = item.id;
    this.itemSelect.emit(item);
  }

  isActive(item: NavItem): boolean {
    if (item.url && this.router.url === item.url) {
      return true;
    }
    return this.activeId === item.id;
  }
}
