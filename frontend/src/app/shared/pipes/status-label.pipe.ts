import { Pipe, PipeTransform } from '@angular/core';

/**
 * Pipe untuk mengubah status enum backend menjadi teks bahasa Indonesia yang ramah pengguna.
 * Contoh:
 * {{ 'MENUNGGU_REVIEW' | statusLabel }} -> "Menunggu Review"
 * {{ 'DISETUJUI_BM' | statusLabel }} -> "Disetujui BM"
 */
@Pipe({
  name: 'statusLabel',
  standalone: true,
})
export class StatusLabelPipe implements PipeTransform {
  transform(status?: string | null): string {
    if (!status) return '-';
    const s = String(status).trim().toUpperCase();

    switch (s) {
      // Loan Application Statuses
      case 'DIAJUKAN':
        return 'Diajukan';
      case 'MENUNGGU_REVIEW':
      case 'PENDING':
        return 'Menunggu Review';
      case 'DISETUJUI_MARKETING':
      case 'SELESAI_DIREVIEW':
        return 'Disetujui Marketing';
      case 'DITOLAK_MARKETING':
        return 'Ditolak Marketing';
      case 'MENUNGGU_PERSETUJUAN_BM':
      case 'MENUNGGU_BM':
        return 'Menunggu BM';
      case 'DISETUJUI_BM':
      case 'APPROVED_BM':
        return 'Disetujui BM';
      case 'DITOLAK_BM':
      case 'REJECTED_BM':
        return 'Ditolak BM';
      case 'MENUNGGU_PENCAIRAN':
      case 'READY_TO_DISBURSE':
        return 'Siap Dicairkan';
      case 'DICAIRKAN':
      case 'DISBURSED':
        return 'Sudah Dicairkan';
      case 'DITOLAK':
      case 'REJECTED':
        return 'Ditolak';
      case 'DISETUJUI':
      case 'APPROVED':
        return 'Disetujui';
      case 'PERLU_REVISI':
      case 'DOKUMEN_DIREVISI':
        return 'Perlu Revisi';

      // KYC / Verification Statuses
      case 'VERIFIED':
      case 'TERVERIFIKASI':
        return 'Terverifikasi';
      case 'MENUNGGU_VERIFIKASI':
        return 'Menunggu Verifikasi';
      case 'BELUM_VERIFIKASI':
      case 'UNVERIFIED':
        return 'Belum Verifikasi';

      // Master & General Statuses
      case 'AKTIF':
      case 'ACTIVE':
        return 'Aktif';
      case 'NONAKTIF':
      case 'NON_AKTIF':
      case 'INACTIVE':
        return 'Nonaktif';
      case 'TERKUNCI':
      case 'LOCKED':
        return 'Terkunci';
      case 'TERSEDIA':
      case 'AVAILABLE':
        return 'Tersedia';
      case 'SELESAI':
      case 'COMPLETED':
      case 'SUCCESS':
        return 'Selesai';
      case 'GAGAL':
      case 'FAILED':
        return 'Gagal';

      default:
        // Fallback: capitalize snake_case
        return s
          .replace(/_/g, ' ')
          .toLowerCase()
          .split(' ')
          .map((w) => w.charAt(0).toUpperCase() + w.slice(1))
          .join(' ');
    }
  }
}

/**
 * Pipe untuk menentukan variant semantic badge / pill berdasarkan status.
 * Output: 'success' | 'warning' | 'error' | 'primary' | 'neutral'
 */
@Pipe({
  name: 'statusVariant',
  standalone: true,
})
export class StatusVariantPipe implements PipeTransform {
  transform(
    status?: string | null
  ): 'success' | 'warning' | 'error' | 'primary' | 'neutral' {
    if (!status) return 'neutral';
    const s = String(status).trim().toUpperCase();

    if (
      s.includes('SETUJU') ||
      s.includes('APPROV') ||
      s.includes('CAIR') ||
      s.includes('DISBURS') ||
      s.includes('VERIF') ||
      s === 'AKTIF' ||
      s === 'ACTIVE' ||
      s === 'SUCCESS' ||
      s === 'SELESAI'
    ) {
      return 'success';
    }

    if (
      s.includes('TOLAK') ||
      s.includes('REJECT') ||
      s.includes('GAGAL') ||
      s.includes('FAILED') ||
      s.includes('LOCK') ||
      s.includes('NONAKTIF')
    ) {
      return 'error';
    }

    if (
      s.includes('TUNGGU') ||
      s.includes('PENDING') ||
      s.includes('REVISI') ||
      s.includes('REVIEW')
    ) {
      return 'warning';
    }

    return 'neutral';
  }
}
