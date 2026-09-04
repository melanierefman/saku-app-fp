export interface AlamatDetail {
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

export interface VerifikasiCustomerItem {
  customerId: string;
  id?: string;
  namaCustomer: string;
  namaLengkap?: string;
  nik: string;
  email: string;
  noHp: string;
  tanggalRegister?: string;
  tanggalRegistrasi?: string;
  tanggalDaftar?: string;
  tanggalPengajuan?: string;
  createdDate?: string;
  createdAt?: string;
  registrationDate?: string;
  registeredAt?: string;
  tglRegister?: string;
  tglRegistrasi?: string;
  statusVerifikasi: 'PENDING' | 'APPROVED' | 'PERLU_REVISI' | 'REJECTED' | string;
  catatanVerifikasi?: string | null;
  tanggalVerifikasi?: string | null;
}

export interface VerifikasiCustomerDetail {
  customerId: string;
  id?: string;
  namaLengkap: string;
  namaCustomer?: string;
  nik: string;
  email: string;
  noHp: string;
  username: string;

  // Pekerjaan & Keuangan
  pekerjaan?: string;
  tempatKerja?: string;
  statusPekerjaan?: string;
  pendapatan?: number;
  penghasilanBulanan?: number;

  // Rekening
  namaBank?: string;
  noRekening?: string;
  namaRekening?: string;

  // Alamat
  alamatKtp?: AlamatDetail;
  alamatDomisili?: AlamatDetail;

  // Dokumen Foto
  fotoSelfie?: string;
  fotoKtp?: string;

  // Status
  statusVerifikasi: 'PENDING' | 'APPROVED' | 'PERLU_REVISI' | 'REJECTED' | string;
  catatanVerifikasi?: string;
  tanggalRegister?: string;
  tanggalRegistrasi?: string;
  tanggalDaftar?: string;
  tanggalPengajuan?: string;
  createdDate?: string;
  createdAt?: string;
  registrationDate?: string;
  registeredAt?: string;
  tglRegister?: string;
  tglRegistrasi?: string;
  tanggalVerifikasi?: string;
  verifiedByKaryawanId?: string;
  namaVerifikator?: string;
}

export interface VerifikasiCustomerRequest {
  statusVerifikasi: 'APPROVED' | 'PERLU_REVISI' | 'REJECTED' | string;
  catatanVerifikasi: string;
}

export interface VerifikasiCustomerResponse {
  customerId: string;
  statusVerifikasi: string;
  catatanVerifikasi: string;
  tanggalVerifikasi: string;
}
