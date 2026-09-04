export interface Menu {
  id: string;
  nama: string;
  path: string;
  status: boolean;
  createdDate?: string;
  updatedDate?: string;
}

export interface MenuRequest {
  nama: string;
  path: string;
  status: boolean;
}
