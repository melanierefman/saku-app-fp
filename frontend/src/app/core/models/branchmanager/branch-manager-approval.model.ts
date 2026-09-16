import { AlamatInfo, DokumenPinjamanItem } from '../superadmin/monitoring-pengajuan.model';

export interface BranchManagerPengajuanItemResponse {
  pengajuanId?: string;
  id?: string;
  nomorPengajuan?: string;
  noPengajuan?: string;
  customerId?: string;
  namaCustomer?: string;
  customer?: string;
  nama?: string;
  email?: string;
  noHp?: string;
  jumlahPinjaman?: number;
  jumlah?: number;
  nominalPinjaman?: number;
  nominal?: number;
  tenorBulan?: number;
  tenor?: number;
  cabangId?: string;
  branchId?: string;
  namaCabang?: string;
  cabang?: string | { id?: string; nama?: string; namaCabang?: string };
  cabangNama?: string;
  kotaCabang?: string;
  tanggalPengajuan?: string;
  createdDate?: string;
  statusPengajuan?: string;
  status?: string;
  hasilReviewMarketing?: string;
  catatanMarketing?: string;
  tanggalReviewMarketing?: string;
  hasilPersetujuanTerakhir?: string;
  catatanPersetujuanTerakhir?: string;
  tanggalPersetujuanTerakhir?: string;
  tanggalPersetujuan?: string;
  tanggalPersetujuanBM?: string;
  skorKredit?: number;
  skor?: number;
  statusScoring?: string;
}

export interface ReviewMarketingHistoryItem {
  id?: string;
  pengajuanId?: string;
  nomorPengajuan?: string;
  hasilReview?: string;
  status?: string;
  catatan?: string;
  reviewerId?: string;
  namaReviewer?: string;
  reviewerNama?: string;
  tanggalReview?: string;
  statusPengajuan?: string;
}

export interface PersetujuanHistoryItem {
  id?: string;
  pengajuanId?: string;
  nomorPengajuan?: string;
  hasilPersetujuan?: string;
  status?: string;
  catatan?: string;
  approverId?: string;
  namaApprover?: string;
  tanggalPersetujuan?: string;
  statusPengajuan?: string;
}

export interface PlafondTierOption {
  id?: string;
  nama?: string;
  minPendapatan?: number;
  plafondMaksimal?: number;
  minSkor?: number;
  maxSkor?: number;
  bunga?: number;
  biayaAdmin?: number;
  status?: boolean;
}

export interface BranchManagerPengajuanDetailResponse {
  pengajuanId?: string;
  id?: string;
  nomorPengajuan?: string;
  noPengajuan?: string;
  tanggalPengajuan?: string;
  createdDate?: string;
  statusPengajuan?: string;
  status?: string;
  catatanPengajuan?: string;

  // Customer Profile
  customerId?: string;
  namaLengkap?: string;
  customer?: string;
  email?: string;
  nik?: string;
  noHp?: string;
  pekerjaan?: string;
  tempatKerja?: string;
  statusPekerjaan?: string;
  pendapatan?: number;
  penghasilanBulanan?: number;
  namaBank?: string;
  noRekening?: string;
  namaRekening?: string;
  alamatKtp?: AlamatInfo;
  alamatDomisili?: AlamatInfo;

  // Documents
  fotoSelfie?: string;
  fotoKtp?: string;
  fotoSlipGaji?: string;
  fotoRekeningKoran?: string;
  fotoNpwp?: string;
  dokumenPinjamanList?: DokumenPinjamanItem[];

  // Scoring
  statusPekerjaanScoring?: string;
  penghasilanBulananScoring?: number;
  lamaBekerjaBulan?: number;
  cicilanBerjalan?: number;
  skor?: number;
  skorKredit?: number;
  statusScoring?: string;
  keputusanSistem?: string;
  plafonNama?: string;
  plafonMaksimal?: number;
  dbrPercentage?: number;
  dbr?: number;
  isAmbigu?: boolean;
  notesAmbigu?: string[];
  ringkasanScoring?: string;
  ringkasanAnalisis?: string;
  rekomendasiAksi?: string;
  rekomendasiTierId?: string;
  rekomendasiTierNama?: string;
  rekomendasiBunga?: number;
  rekomendasiBiayaAdmin?: number;
  rekomendasiAlasan?: string;
  availablePlafondTiers?: PlafondTierOption[];

  // Facility Details
  jumlahPinjaman?: number;
  jumlah?: number;
  tenorBulan?: number;
  tenor?: number;
  tujuanPinjaman?: string;
  bunga?: number;
  biayaAdmin?: number;
  estimasiCicilan?: number;

  // Branch
  branchId?: string;
  namaCabang?: string;
  kotaCabang?: string;

  // Marketing Review Result
  marketingReviewerId?: string;
  namaMarketingReviewer?: string;
  hasilReviewMarketing?: string;
  catatanMarketing?: string;
  tanggalReviewMarketing?: string;
  reviewMarketingHistory?: ReviewMarketingHistoryItem[];

  // BM Approval History
  hasilPersetujuanBM?: string;
  catatanBM?: string;
  tanggalPersetujuanBM?: string;
  persetujuanHistory?: PersetujuanHistoryItem[];
}

export interface PersetujuanPinjamanRequest {
  hasilPersetujuan: 'DISETUJUI' | 'DITOLAK' | string;
  catatan?: string;
  penyesuaianTierId?: string;
  kategoriAlasan?: string;
  adjustedJumlahPinjaman?: number;
  adjustedBunga?: number;
  adjustedBiayaAdmin?: number;
}

export interface PersetujuanPinjamanResponse {
  pengajuanId: string;
  nomorPengajuan: string;
  statusPengajuan: string;
  hasilPersetujuan: string;
  catatanPersetujuan?: string;
  tanggalPersetujuan: string;
}
