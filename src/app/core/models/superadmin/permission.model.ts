export type PermissionAction = 'READ' | 'CREATE' | 'UPDATE' | 'DELETE' | 'ALL' | 'APPROVE' | 'REJECT' | string;

export interface Permission {
  id: string;
  nama: string;
  resource?: string;
  action?: PermissionAction;
  mstMenuId?: string;
  menuNama?: string;
  menuPath?: string;
  createdDate?: string;
  updatedDate?: string;
  // Compatibility fields for role & permission assignment
  name?: string;
  code?: string;
  deskripsi?: string;
  category?: string;
  group?: string;
}

export interface PermissionRequest {
  nama: string;
  resource: string;
  action: PermissionAction;
  mstMenuId: string;
}
