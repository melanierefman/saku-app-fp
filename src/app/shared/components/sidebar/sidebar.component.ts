import { Component, EventEmitter, Input, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';

export interface NavItem {
  id: string;
  label: string;
  icon: 'grid' | 'user-key' | 'user-cog' | 'user-check' | 'menu' | 'users' | 'building' | 'banknote' | 'receipt' | 'history' | string;
  url?: string;
  badge?: string | number;
}

export interface NavGroup {
  title?: string;
  items: NavItem[];
}

export const DEFAULT_SAKU_MENU_GROUPS: NavGroup[] = [
  {
    items: [
      {
        id: 'beranda',
        label: 'Beranda',
        icon: 'grid',
        url: '/dashboard',
      },
    ],
  },
  {
    title: 'RBAC',
    items: [
      {
        id: 'role-access',
        label: 'Role Access',
        icon: 'user-key',
        url: '/rbac/role-access',
      },
      {
        id: 'role',
        label: 'Role',
        icon: 'user-cog',
        url: '/rbac/role',
      },
      {
        id: 'permission',
        label: 'Permission',
        icon: 'user-check',
        url: '/rbac/permission',
      },
      {
        id: 'menu',
        label: 'Menu',
        icon: 'menu',
        url: '/rbac/menu',
      },
    ],
  },
  {
    title: 'MASTER DATA',
    items: [
      {
        id: 'karyawan',
        label: 'Karyawan',
        icon: 'users',
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
        icon: 'receipt',
        url: '/monitoring/pengajuan',
      },
      {
        id: 'audit-log',
        label: 'Audit Log',
        icon: 'history',
        url: '/monitoring/audit-log',
      },
    ],
  },
];

@Component({
  selector: 'app-sidebar',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './sidebar.component.html',
  styleUrl: './sidebar.component.css',
})
export class SidebarComponent {
  @Input() logoSrc: string = 'saku-logo.png';
  @Input() brandTitle: string = 'SAKU';
  @Input() brandSubtitle: string = 'BACK OFFICE PORTAL';
  @Input() activeId: string = 'role';
  @Input() collapsed: boolean = false;
  @Input() menuGroups: NavGroup[] = DEFAULT_SAKU_MENU_GROUPS;

  @Output() itemSelect = new EventEmitter<NavItem>();

  onSelect(item: NavItem, event: MouseEvent): void {
    this.activeId = item.id;
    this.itemSelect.emit(item);
  }

  isActive(item: NavItem): boolean {
    return this.activeId === item.id;
  }
}
