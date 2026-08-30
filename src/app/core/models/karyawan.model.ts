export interface PageResponse<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  currentPage: number;
  pageSize: number;
  isFirst?: boolean;
  isLast?: boolean;
}

export interface Karyawan {
  id: string;
  nama: string;
  username: string;
  email: string;
  status: boolean;
  mstRoleId: string;
  mstBranchId: string;
  roleNama?: string;
  cabangNama?: string;
  createdDate?: string;
  updatedDate?: string;
  roleName?: string;
  branchName?: string;
}

export interface KaryawanQueryParams {
  page?: number;
  size?: number;
  search?: string;
  roleId?: string;
  branchId?: string;
  status?: boolean | string;
}

export interface KaryawanCreateRequest {
  nama: string;
  username: string;
  email: string;
  password: string;
  status: boolean;
  mstRoleId: string;
  mstBranchId: string;
}

export interface KaryawanUpdateRequest {
  id?: string;
  nama: string;
  username: string;
  email: string;
  password?: string;
  status: boolean;
  mstRoleId: string;
  mstBranchId: string;
}
