import { AlamatDetail } from '../backoffice/verifikasi-customer.model';

export interface CustomerPinjamanHistoryItemResponse {
  pengajuanId: string;
  nomorPengajuan: string;
  tanggalPengajuan: string;
  jumlahPinjaman: number;
  tenorBulan: number;
  statusPengajuan: string;
  tujuanPinjaman: string;
  namaCabang: string;
}

export interface MarketingCustomerItemResponse {
  customerId: string;
  namaCustomer: string;
  nik: string;
  email: string;
  noHp: string;
  kota: string;
  provinsi: string;
  totalPlafond: number;
  usedPlafond: number;
  availablePlafond: number;
  tierPlafond: string;
  totalPengajuan: number;
  statusAkun: boolean;
  tanggalVerifikasi?: string;
  createdDate: string;
}

export interface MarketingCustomerDetailResponse {
  customerId: string;
  namaCustomer: string;
  nik: string;
  email: string;
  noHp: string;
  namaIbuKandung?: string;
  namaBank?: string;
  noRekening?: string;
  namaRekening?: string;
  alamatKtp?: AlamatDetail;
  alamatDomisili?: AlamatDetail;
  fotoKtp?: string;
  fotoSelfie?: string;
  pekerjaan?: string;
  tempatKerja?: string;
  pendapatan?: number;
  skor?: number;
  statusScoring?: string;
  tierPlafond?: string;
  totalPlafond?: number;
  usedPlafond?: number;
  availablePlafond?: number;
  statusAkun?: boolean;
  tanggalVerifikasi?: string;
  createdDate?: string;
  riwayatPinjaman?: CustomerPinjamanHistoryItemResponse[];
}
