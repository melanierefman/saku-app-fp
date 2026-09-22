import { CustomerPinjamanHistoryItemResponse } from '../marketing/marketing-customer.model';
import { AlamatDetail } from '../backoffice/verifikasi-customer.model';

export interface BranchManagerCustomerItemResponse {
  customerId: string;
  namaCustomer: string;
  nik: string;
  email: string;
  noHp: string;
  kota: string;
  provinsi: string;
  namaCabang: string;
  totalPlafond: number;
  usedPlafond: number;
  availablePlafond: number;
  tierPlafond: string;
  totalPengajuan: number;
  statusAkun: boolean;
  tanggalVerifikasi?: string;
  createdDate: string;
}

export interface BranchManagerCustomerDetailResponse {
  customerId: string;
  namaCustomer: string;
  nik: string;
  email: string;
  noHp: string;
  namaIbuKandung?: string;
  namaBank?: string;
  noRekening?: string;
  namaRekening?: string;
  namaCabang?: string;
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
