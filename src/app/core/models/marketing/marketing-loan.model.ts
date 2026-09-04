export interface ReviewPengajuanRequest {
  hasilReview: 'DISETUJUI' | 'PERLU_REVISI' | 'DITOLAK' | string;
  catatan: string;
}

export interface ReviewPengajuanResponse {
  pengajuanId?: string;
  id?: string;
  status?: string;
  hasilReview?: string;
  catatan?: string;
  tanggalReview?: string;
}
