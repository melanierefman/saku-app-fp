export interface AngsuranItem {
  id?: string;
  cicilanKe: number;
  jumlahAngsuran: number;
  jatuhTempo: string; // YYYY-MM-DD
  statusBayar: 'BELUM_BAYAR' | 'LUNAS' | string;
  tanggalBayar?: string | null;
}

export interface PencairanItem {
  pengajuanId: string;
  id?: string;
  noPengajuan: string;
  nomorPengajuan?: string;
  customerId: string;
  namaCustomer: string;
  namaLengkap?: string;
  nik: string;
  email: string;
  noHp: string;
  namaBank: string;
  noRekening: string;
  namaRekening: string;
  jumlahPinjaman: number;
  biayaAdmin: number;
  jumlahPencairan: number;
  jumlahPencairanBersih?: number;
  tenorBulan: number;
  bunga: number;
  namaCabang: string;
  statusPengajuan: string;
  tanggalDisetujuiBM?: string;
  statusPencairan: 'MENUNGGU_PENCAIRAN' | 'DICAIRKAN' | 'BERHASIL' | string;
  tanggalPencairan?: string;
  namaPetugasBackoffice?: string;
}

export interface PencairanDetail {
  pengajuanId: string;
  id?: string;
  nomorPengajuan: string;
  noPengajuan?: string;
  tanggalPengajuan: string;
  statusPengajuan: string;
  catatanPengajuan?: string;

  // Customer
  customerId: string;
  namaLengkap: string;
  namaCustomer?: string;
  nik: string;
  email: string;
  noHp: string;
  alamatKtp?: any;
  alamatDomisili?: any;

  // Rekening Pencairan
  namaBank: string;
  noRekening: string;
  namaRekening: string;

  // Finansial & Kalkulasi
  jumlahPinjaman: number;
  tenorBulan: number;
  tujuanPinjaman: string;
  bunga: number;
  biayaAdmin: number;
  jumlahPencairanBersih: number;
  estimasiAngsuranBulanan: number;
  namaCabang: string;
  kotaCabang?: string;

  // Dokumen & Foto
  fotoSelfie?: string;
  fotoKtp?: string;
  fotoSlipGaji?: string;
  fotoRekeningKoran?: string;
  fotoNpwp?: string;
  dokumenPinjamanList?: Array<{
    id?: string;
    docType?: string;
    jenisDokumen?: string;
    fileUrl?: string;
  }>;

  // Review & Approval Sebelumnya
  reviewMarketingTerakhir?: {
    hasilReview: string;
    catatan?: string;
    namaReviewer: string;
    tanggalReview: string;
  };
  persetujuanBMTerakhir?: {
    hasilPersetujuan: string;
    catatan?: string;
    namaApprover: string;
    tanggalPersetujuan: string;
  };

  // Status Pencairan
  pencairanId?: string;
  statusPencairan: string;
  tanggalPencairan?: string;
  namaPetugasBackoffice?: string;
  catatanPencairan?: string;
  listAngsuran?: AngsuranItem[];
}

export interface PencairanRequest {
  catatan?: string;
  jumlahPencairan?: number;
}

export interface PencairanResponse {
  pencairanId?: string;
  pengajuanId: string;
  statusPencairan: string;
  tanggalPencairan: string;
  catatan?: string;
  listAngsuran?: AngsuranItem[];
}
