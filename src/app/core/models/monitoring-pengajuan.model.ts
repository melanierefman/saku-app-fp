export interface MarketingReviewStage {
  status?: string;
  catatan?: string;
  reviewer?: string;
  tanggal?: string | null;
}

export interface BranchManagerReviewStage {
  status?: string;
  catatan?: string;
  approver?: string;
  tanggal?: string | null;
}

export interface BackofficeReviewStage {
  status?: string;
  catatan?: string;
  disburser?: string;
  tanggal?: string | null;
}

export interface RiwayatPengajuanItem {
  role?: string;
  status?: string;
  tanggal?: string | null;
}

export interface ReviewHistoryItem {
  tahap?: string;
  reviewerNama?: string;
  reviewer?: string;
  role?: string;
  status?: string;
  catatan?: string;
  tanggal?: string | null;
  createdDate?: string;
}

export interface DokumenPengajuanItem {
  id?: string;
  jenisDokumen?: string;
  namaDokumen?: string;
  fileUrl?: string;
  statusVerifikasi?: string;
}

export interface NasabahDetail {
  id?: string;
  namaLengkap?: string;
  nama?: string;
  nik?: string;
  noHp?: string;
  email?: string;
  alamat?: string;
  pendapatanBulanan?: number;
  pekerjaan?: string;
  tempatLahir?: string;
  tanggalLahir?: string;
  jenisKelamin?: string;
  lamaBekerja?: number;
  lamaKerja?: number;
  masaKerja?: number;
  lamaBekerjaBulan?: number;
  skorKredit?: number;
  skor?: number;
  dbr?: number;
  rekomendasiPlafond?: string;
}

export interface PinjamanDetail {
  nominal?: number;
  jumlah?: number;
  tenor?: number;
  bunga?: number;
  angsuranBulanan?: number;
  biayaAdmin?: number;
  tujuanPinjaman?: string;
}

export interface ScoringDetail {
  skorKredit?: number;
  skor?: number;
  rekomendasiPlafond?: string;
  maxLimit?: number;
  dbr?: number;
  lamaBekerja?: number;
  lamaKerja?: number;
  masaKerja?: number;
  catatanScoring?: string;
}

export interface MarketingPengajuanItemResponse {
  pengajuanId?: string;
  id?: string;
  noPengajuan?: string;
  nomorPengajuan?: string;
  customerId?: string;
  customer?: any;
  namaNasabah?: string;
  nama?: string;
  email?: string;
  noHp?: string;
  nik?: string;
  tanggalPengajuan?: string;
  createdDate?: string;
  jumlah?: number;
  nominalPinjaman?: number;
  nominal?: number;
  tenor?: number;
  bunga?: number;
  cabang?: any;
  cabangNama?: string;
  status?: string;
  hasilReviewTerakhir?: string;
  catatanReviewTerakhir?: string;
  tanggalReviewTerakhir?: string;
  skorKredit?: number;
  skor?: number;
  updatedDate?: string;

  // Stages & Riwayat
  marketing?: MarketingReviewStage;
  branchManager?: BranchManagerReviewStage;
  backoffice?: BackofficeReviewStage;
  riwayat?: RiwayatPengajuanItem[];
}

export interface MarketingPengajuanDetailResponse {
  id?: string;
  pengajuanId?: string;
  noPengajuan?: string;
  nomorPengajuan?: string;
  status?: string;
  tanggalPengajuan?: string;
  createdDate?: string;
  updatedDate?: string;
  
  // Data Nasabah / Customer
  customerId?: string;
  customer?: any;
  namaNasabah?: string;
  nama?: string;
  nik?: string;
  email?: string;
  noHp?: string;
  alamat?: string;
  pekerjaan?: string;
  pendapatanBulanan?: number;
  tempatLahir?: string;
  tanggalLahir?: string;
  jenisKelamin?: string;
  agama?: string;
  statusPernikahan?: string;
  lamaBekerja?: number;
  lamaKerja?: number;
  masaKerja?: number;
  skorKredit?: number;
  skor?: number;
  dbr?: number;
  rekomendasiPlafond?: string;
  
  nasabah?: NasabahDetail;
  pinjaman?: PinjamanDetail;
  scoring?: ScoringDetail;

  // Data Pinjaman
  jumlah?: number;
  nominalPinjaman?: number;
  nominal?: number;
  tenor?: number;
  bunga?: number;
  angsuranBulanan?: number;
  biayaAdmin?: number;
  tujuanPinjaman?: string;

  // Cabang
  cabang?: any;
  cabangNama?: string;

  // Review Status
  hasilReviewTerakhir?: string;
  catatanReviewTerakhir?: string;
  tanggalReviewTerakhir?: string;

  // Stages & Riwayat
  marketing?: MarketingReviewStage;
  branchManager?: BranchManagerReviewStage;
  backoffice?: BackofficeReviewStage;
  riwayat?: RiwayatPengajuanItem[];
  reviews?: ReviewHistoryItem[];
  reviewHistory?: ReviewHistoryItem[];
  dokumen?: DokumenPengajuanItem[];
  documents?: DokumenPengajuanItem[];
}
