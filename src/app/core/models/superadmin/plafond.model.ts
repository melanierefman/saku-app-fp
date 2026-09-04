export interface Plafond {
  id: string;
  nama: string;
  minSkor: number;
  maxSkor: number;
  minPendapatan: number;
  minPlafond: number;
  maxPlafond: number;
  plafondMaksimal?: number;
  bunga: number;
  biayaAdmin: number;
  status: boolean;
  createdDate?: string;
  updatedDate?: string;
}

export interface PlafondRequest {
  nama: string;
  minSkor: number;
  maxSkor: number;
  minPendapatan: number;
  minPlafond: number;
  maxPlafond: number;
  plafondMaksimal?: number;
  bunga: number;
  biayaAdmin: number;
  status: boolean;
}
