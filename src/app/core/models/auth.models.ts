export interface ApiResponse<T> {
  data: T;
  message: string;
  statusCode: number;
  errors?: any;
}

export interface LoginKaryawanRequest {
  identifier?: string;
  username?: string;
  email?: string;
  password: string;
}

export interface UserProfile {
  id: string;
  username: string;
  nama: string;
  email: string;
  role: string; // e.g. 'MARKETING', 'BRANCH_MANAGER', 'BACKOFFICE', 'ADMIN'
  tipe: string; // e.g. 'KARYAWAN'
  status: boolean;
  cabang?: string;
  permissions: string[];
}

export interface LoginResponseData {
  tokenType: string;
  accessToken: string;
  refreshToken: string;
  expiresIn: number;
  user: UserProfile;
}

export type AuthResponse = ApiResponse<LoginResponseData>;
