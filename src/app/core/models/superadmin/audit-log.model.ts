export interface AuditLogKaryawan {
  id?: string;
  nama?: string;
  email?: string;
  username?: string;
  role?: string;
  cabang?: string;
}

export interface AuditLog {
  id: string;
  action: string;
  entity?: string;
  entityId?: string | number;
  description?: string;
  createdDate?: string;
  timestamp?: string;
  karyawan?: AuditLogKaryawan;
  karyawanId?: string;
  karyawanNama?: string;
  username?: string;
  role?: string;
  ipAddress?: string;
  userAgent?: string;
  details?: string | Record<string, any>;
}

export interface AuditLogPageResponse {
  logs?: AuditLog[];
  content?: AuditLog[];
  totalItems?: number;
  totalElements?: number;
  totalPages: number;
  currentPage: number;
  pageSize: number;
}
