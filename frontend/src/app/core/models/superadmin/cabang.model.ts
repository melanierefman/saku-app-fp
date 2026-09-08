export interface Cabang {
  id: string;
  nama: string;
  kota: string;
  isDefault?: boolean;
  status: boolean;
  kodeCabang?: string;
  alamat?: string;
  createdDate?: string;
  updatedDate?: string;
}

export interface CabangRequest {
  nama: string;
  kota: string;
  isDefault: boolean;
  status: boolean;
}
