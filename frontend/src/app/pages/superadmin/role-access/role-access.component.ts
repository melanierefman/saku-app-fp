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
  ButtonComponent,
  CheckboxComponent,
  DropdownComponent,
  DropdownOption,
  ModalComponent,
  TableComponent,
  TableCellDirective,
  TableColumn,
  ToastService,
} from '../../../shared/components';
import {
  Role,
  RoleService,
  RoleDetailResponse,
  Menu,
  MenuService,
  Permission,
  PermissionService,
  formatRoleName,
} from '../../../core';

export interface ActionColumn {
  key: string;
  label: string;
  aliases: string[];
}

@Component({
  selector: 'app-role-access',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    RouterModule,
    ButtonComponent,
    CheckboxComponent,
    DropdownComponent,
    ModalComponent,
    TableComponent,
    TableCellDirective,
  ],
  templateUrl: './role-access.component.html',
  styleUrl: './role-access.component.css',
})
export class RoleAccessComponent implements OnInit {
  private roleService = inject(RoleService);
  private menuService = inject(MenuService);
  private permissionService = inject(PermissionService);
  private toastService = inject(ToastService);
  private platformId = inject(PLATFORM_ID);

  // Table Columns Definition matching app-table
  columns: TableColumn[] = [
    { key: 'no', header: 'No', width: '64px', align: 'center' },
    { key: 'nama', header: 'Menu' },
    { key: 'view', header: 'View', align: 'center' },
    { key: 'create', header: 'Create', align: 'center' },
    { key: 'update', header: 'Update', align: 'center' },
    { key: 'delete', header: 'Delete', align: 'center' },
    { key: 'review', header: 'Review', align: 'center' },
    { key: 'approve', header: 'Approve', align: 'center' },
    { key: 'verify', header: 'Verify', align: 'center' },
  ];

  // Action Columns Definition
  readonly actionColumns: ActionColumn[] = [
    { key: 'view', label: 'View', aliases: ['VIEW', 'READ'] },
    { key: 'create', label: 'Create', aliases: ['CREATE', 'ADD'] },
    { key: 'update', label: 'Update', aliases: ['UPDATE', 'EDIT'] },
    { key: 'delete', label: 'Delete', aliases: ['DELETE', 'REMOVE'] },
    { key: 'review', label: 'Review', aliases: ['REVIEW'] },
    { key: 'approve', label: 'Approve', aliases: ['APPROVE', 'ALL'] },
    { key: 'verify', label: 'Verify', aliases: ['VERIFY', 'REJECT'] },
  ];

  // State Signals
  allRoles = signal<Role[]>([]);
  allMenus = signal<Menu[]>([]);
  allPermissions = signal<Permission[]>([]);
  selectedRoleId = signal<string>('');
  activePermissionIds = signal<Set<string>>(new Set());

  isLoading = signal<boolean>(false);
  isLoadingPermissions = signal<boolean>(false);
  isSubmitting = signal<boolean>(false);
  isConfirmModalOpen = signal<boolean>(false);

  roleOptions = computed<DropdownOption[]>(() => {
    return this.allRoles().map((r) => ({
      value: r.id,
      label: formatRoleName(r.nama),
    }));
  });

  selectedRole = computed<Role | undefined>(() => {
    const id = this.selectedRoleId();
    return this.allRoles().find((r) => r.id === id);
  });

  selectedRoleName = computed<string>(() => {
    const role = this.selectedRole();
    return role ? formatRoleName(role.nama) : 'Role';
  });

  totalActivePermissionsCount = computed<number>(() => {
    return this.activePermissionIds().size;
  });

  tableData = computed(() => {
    return this.allMenus().map((item, idx) => ({
      ...item,
      no: idx + 1,
    }));
  });

  ngOnInit(): void {
    if (isPlatformBrowser(this.platformId)) {
      this.loadInitialData();
    }
  }

  loadInitialData(): void {
    this.isLoading.set(true);

    // 1. Fetch Menus
    this.menuService.getAll().subscribe({
      next: (menus) => {
        this.allMenus.set(Array.isArray(menus) ? menus : []);
      },
      error: (err) => {
        console.error('Failed to load menus:', err);
      },
    });

    // 2. Fetch Master Permissions Catalog
    this.permissionService.getAll().subscribe({
      next: (perms) => {
        this.allPermissions.set(Array.isArray(perms) ? perms : []);
      },
      error: (err) => {
        console.error('Failed to load permissions catalog:', err);
      },
    });

    // 3. Fetch Roles
    this.roleService.getAll().subscribe({
      next: (roles) => {
        const roleList = Array.isArray(roles) ? roles : [];
        this.allRoles.set(roleList);
        this.isLoading.set(false);

        // Auto select first role if none is selected
        if (roleList.length > 0 && !this.selectedRoleId()) {
          this.selectRole(roleList[0].id);
        }
      },
      error: (err) => {
        console.error('Failed to load roles:', err);
        this.isLoading.set(false);
      },
    });
  }

  onRoleDropdownChange(event: any): void {
    let roleId = '';
    if (event && typeof event === 'object' && 'value' in event) {
      roleId = event.value;
    } else if (typeof event === 'string') {
      roleId = event;
    }

    if (roleId) {
      this.selectRole(roleId);
    } else {
      this.selectedRoleId.set('');
      this.activePermissionIds.set(new Set());
    }
  }

  selectRole(roleId: string): void {
    this.selectedRoleId.set(roleId);
    if (!roleId) {
      this.activePermissionIds.set(new Set());
      return;
    }

    this.isLoadingPermissions.set(true);
    this.roleService.getById(roleId).subscribe({
      next: (detail) => {
        const permissionIds = new Set<string>();

        if (detail && detail.permissions && Array.isArray(detail.permissions)) {
          detail.permissions.forEach((item: any) => {
            if (typeof item === 'string') {
              permissionIds.add(item);
            } else if (item && typeof item === 'object' && item.id) {
              permissionIds.add(item.id);
            }
          });
        }

        this.activePermissionIds.set(permissionIds);
        this.isLoadingPermissions.set(false);
      },
      error: (err) => {
        console.error(`Failed to load permissions for role ${roleId}:`, err);
        this.activePermissionIds.set(new Set());
        this.isLoadingPermissions.set(false);
      },
    });
  }

  // Find action permission
  findPermission(menuId: string, actionKey: string): Permission | undefined {
    const colDef = this.actionColumns.find((c) => c.key === actionKey);
    if (!colDef) return undefined;
    const aliases = colDef.aliases;

    return this.allPermissions().find((p) => {
      const matchMenu = p.mstMenuId === menuId;
      const permAction = (p.action || '').trim().toUpperCase();
      return matchMenu && aliases.includes(permAction);
    });
  }

  // Check action permission
  isPermissionChecked(menuId: string, actionKey: string): boolean {
    const perm = this.findPermission(menuId, actionKey);
    if (!perm) return false;
    return this.activePermissionIds().has(perm.id);
  }

  // Toggle action permission
  togglePermission(menuId: string, actionKey: string): void {
    const perm = this.findPermission(menuId, actionKey);
    if (!perm) return;

    this.activePermissionIds.update((set) => {
      const newSet = new Set(set);
      if (newSet.has(perm.id)) {
        newSet.delete(perm.id);
      } else {
        newSet.add(perm.id);
      }
      return newSet;
    });
  }

  /**
   * Check if all permissions for this menu are checked
   */
  isAllCheckedForMenu(menuId: string): boolean {
    const permsForMenu = this.allPermissions().filter((p) => p.mstMenuId === menuId);
    if (permsForMenu.length === 0) return false;
    return permsForMenu.every((p) => this.activePermissionIds().has(p.id));
  }

  /**
   * Toggle all permissions for a specific menu
   */
  toggleAllForMenu(menuId: string): void {
    const permsForMenu = this.allPermissions().filter((p) => p.mstMenuId === menuId);
    if (permsForMenu.length === 0) return;

    const allChecked = this.isAllCheckedForMenu(menuId);
    this.activePermissionIds.update((set) => {
      const newSet = new Set(set);
      if (allChecked) {
        permsForMenu.forEach((p) => newSet.delete(p.id));
      } else {
        permsForMenu.forEach((p) => newSet.add(p.id));
      }
      return newSet;
    });
  }

  // Confirmation & Save Actions
  openSaveConfirmModal(): void {
    if (!this.selectedRoleId()) {
      this.toastService.error('Silakan pilih role terlebih dahulu.');
      return;
    }
    this.isConfirmModalOpen.set(true);
  }

  closeSaveConfirmModal(): void {
    if (this.isSubmitting()) return;
    this.isConfirmModalOpen.set(false);
  }

  closeConfirmModal(): void {
    this.closeSaveConfirmModal();
  }

  confirmSaveAccess(): void {
    const roleId = this.selectedRoleId();
    if (!roleId) return;

    const permissionIds = Array.from(this.activePermissionIds());
    this.isSubmitting.set(true);

    this.roleService.assignPermissions(roleId, permissionIds).subscribe({
      next: () => {
        this.toastService.success(
          `Hak akses untuk role ${this.selectedRoleName()} berhasil disimpan.`
        );
        this.isSubmitting.set(false);
        this.isConfirmModalOpen.set(false);
      },
      error: (err) => {
        console.error('Failed to save role permissions:', err);
        this.toastService.error(
          err?.error?.message || 'Gagal menyimpan hak akses role.'
        );
        this.isSubmitting.set(false);
        this.isConfirmModalOpen.set(false);
      },
    });
  }
}
