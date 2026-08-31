import type { Permission } from './permission.model';
export type { Permission };

export interface Role {
  id: string;
  nama: string;
  code?: string;
  deskripsi?: string;
  status?: boolean;
  totalPermissions?: number;
  permissions?: Permission[] | string[];
  createdDate?: string;
  updatedDate?: string;
}

export interface RoleDetailResponse {
  id: string;
  nama: string;
  status: boolean;
  totalPermissions?: number;
  permissions?: Permission[] | string[];
  createdDate?: string;
  updatedDate?: string;
}

export interface RoleRequest {
  nama: string;
  status: boolean;
  deskripsi?: string;
}

export interface AssignPermissionsRequest {
  permissionIds: string[];
}

export function formatRoleName(role?: string | null): string {
  if (!role) return '-';
  const raw = role.trim();
  const clean = raw.toUpperCase().replace(/[\s\-_]+/g, '');

  switch (clean) {
    case 'BACKOFFICE':
      return 'Back Office';
    case 'BRANCHMANAGER':
      return 'Branch Manager';
    case 'SUPERADMIN':
      return 'Superadmin';
    case 'MARKETING':
      return 'Marketing'
    default:
      return raw
        .replace(/[_\-]+/g, ' ')
        .split(' ')
        .filter(Boolean)
        .map((w) => w.charAt(0).toUpperCase() + w.slice(1).toLowerCase())
        .join(' ');
  }
}
