export interface MarketingPengajuanItemResponse {
  pengajuanId?: string;
  id?: string;
  noPengajuan?: string;
  nomorPengajuan?: string;
  customerId?: string;
  customer?: string;
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
  cabang?: string | { id?: string; nama?: string; kota?: string };
  cabangNama?: string;
  status?: string;
  hasilReviewTerakhir?: string;
  catatanReviewTerakhir?: string;
  tanggalReviewTerakhir?: string;
  skorKredit?: number;
  skor?: number;
  updatedDate?: string;
}

export interface ReviewHistoryItem {
  tahap?: string;
  reviewerNama?: string;
  reviewer?: string;
  role?: string;
  status?: string;
  catatan?: string;
  tanggal?: string;
  createdDate?: string;
}

export interface DokumenPengajuanItem {
  id?: string;
  jenisDokumen?: string;
  namaDokumen?: string;
  fileUrl?: string;
  statusVerifikasi?: string;
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
  
  // Data Nasabah / Customer (Support flat & nested)
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
  
  nasabah?: {
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
  };

  // Data Pinjaman (Support flat & nested)
  jumlah?: number;
  nominalPinjaman?: number;
  nominal?: number;
  tenor?: number;
  bunga?: number;
  angsuranBulanan?: number;
  biayaAdmin?: number;
  tujuanPinjaman?: string;

  pinjaman?: {
    nominal?: number;
    jumlah?: number;
    tenor?: number;
    bunga?: number;
    angsuranBulanan?: number;
    biayaAdmin?: number;
    tujuanPinjaman?: string;
  };

  // Scoring & Plafond (Support flat & nested)
  skorKredit?: number;
  skor?: number;
  rekomendasiPlafond?: string;
  maxLimit?: number;
  dbr?: number;
  lamaBekerja?: number;
  catatanScoring?: string;

  scoring?: {
    skorKredit?: number;
    skor?: number;
    rekomendasiPlafond?: string;
    maxLimit?: number;
    dbr?: number;
    lamaBekerja?: number;
    catatanScoring?: string;
  };

  // Cabang
  cabang?: any;
  cabangNama?: string;

  // Review & Approval Status
  hasilReviewTerakhir?: string;
  catatanReviewTerakhir?: string;
  tanggalReviewTerakhir?: string;
  reviews?: ReviewHistoryItem[];
  reviewHistory?: ReviewHistoryItem[];
  dokumen?: DokumenPengajuanItem[];
  documents?: DokumenPengajuanItem[];
}
