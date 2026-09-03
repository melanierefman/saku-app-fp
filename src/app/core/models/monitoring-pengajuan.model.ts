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
  id?: string;
  tahap?: string;
  reviewerNama?: string;
  reviewer?: string;
  role?: string;
  status?: string;
  hasilReview?: string;
  catatan?: string;
  tanggal?: string | null;
  tanggalReview?: string | null;
  createdDate?: string;
}

export interface DokumenPengajuanItem {
  id?: string;
  jenisDokumen?: string;
  namaDokumen?: string;
  fileUrl?: string;
  statusVerifikasi?: string;
}

export interface AlamatInfo {
  jenisAlamat?: string;
  alamatLengkap?: string;
  rt?: string;
  rw?: string;
  kelurahan?: string;
  kecamatan?: string;
  kotaKabupaten?: string;
  provinsi?: string;
  kodePos?: string;
  formattedAddress?: string;
}

export interface DokumenPinjamanItem {
  id?: string;
  docType?: string;
  jenisDokumen?: string;
  namaDokumen?: string;
  fileUrl?: string;
  createdDate?: string;
}

export interface BreakdownInfo {
  dbrDetail?: string;
  pendapatanDetail?: string;
  lamaBekerjaDetail?: string;
  statusPekerjaanDetail?: string;
  lamaCustomerDetail?: string;
}

export interface CustomerDetail {
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
  namaCustomer?: string;
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
  nomorPengajuan?: string;
  noPengajuan?: string;
  tanggalPengajuan?: string;
  createdDate?: string;
  updatedDate?: string;
  statusPengajuan?: string;
  status?: string;
  catatanPengajuan?: string;

  // Data Customer
  customerId?: string;
  customer?: any;
  namaLengkap?: string;
  namaCustomer?: string;
  nama?: string;
  nik?: string;
  email?: string;
  noHp?: string;
  pekerjaan?: string;
  tempatKerja?: string;
  statusPekerjaan?: string;
  pendapatan?: number;
  penghasilanBulanan?: number;
  pendapatanBulanan?: number;
  namaBank?: string;
  noRekening?: string;
  namaRekening?: string;
  alamat?: string;
  alamatKtp?: AlamatInfo;
  alamatDomisili?: AlamatInfo;
  tempatLahir?: string;
  tanggalLahir?: string;
  jenisKelamin?: string;
  agama?: string;
  statusPernikahan?: string;

  // Dokumen & Lampiran
  dokumenList?: DokumenPinjamanItem[];
  dokumenPinjamanList?: DokumenPinjamanItem[];
  documents?: DokumenPengajuanItem[];
  dokumen?: any;
  fotoKtp?: string;
  fotoKtpUrl?: string;
  fotoSelfie?: string;
  fotoSelfieUrl?: string;
  slipGaji?: string;
  slipGajiUrl?: string;
  rekeningKoran?: string;
  rekeningKoranUrl?: string;
  npwp?: string;
  npwpUrl?: string;
  dokumenLainnya?: string;
  dokumenLainnyaUrl?: string;

  // Scoring AI & Plafon
  skorKredit?: number;
  skor?: number;
  statusScoring?: string;
  keputusanSistem?: string;
  scoringStatusPekerjaan?: string;
  scoringPenghasilan?: number;
  lamaBekerjaBulan?: number;
  lamaKerja?: number;
  lamaBekerja?: number;
  masaKerja?: number;
  cicilanBerjalan?: number;
  dbr?: number;
  dbrPercentage?: number;
  plafonNama?: string;
  rekomendasiPlafond?: string;
  plafonMaksimal?: number;
  estimasiPlafondDisetujui?: number;
  isAmbigu?: boolean;
  notesAmbigu?: string[];
  ringkasanAnalisis?: string;
  breakdown?: BreakdownInfo;

  customerDetail?: CustomerDetail;
  pinjaman?: PinjamanDetail;
  scoring?: ScoringDetail;

  // Data Pinjaman
  jumlahPinjaman?: number;
  jumlah?: number;
  nominalPinjaman?: number;
  nominal?: number;
  tenorBulan?: number;
  tenor?: number;
  bunga?: number;
  angsuranBulanan?: number;
  biayaAdmin?: number;
  estimasiCicilan?: number;
  tujuanPinjaman?: string;
  tujuan?: string;
  keperluan?: string;

  // Cabang
  branchId?: string;
  namaCabang?: string;
  cabang?: any;
  cabangNama?: string;
  kotaCabang?: string;

  // Review Status
  latestReview?: any;
  reviewHistory?: ReviewHistoryItem[];
  reviews?: ReviewHistoryItem[];
  hasilReviewTerakhir?: string;
  catatanReviewTerakhir?: string;
  tanggalReviewTerakhir?: string;

  // Stages & Riwayat
  marketing?: MarketingReviewStage;
  branchManager?: BranchManagerReviewStage;
  backoffice?: BackofficeReviewStage;
  riwayat?: RiwayatPengajuanItem[];
}
