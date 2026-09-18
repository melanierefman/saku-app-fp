import {
  Component,
  OnInit,
  signal,
  inject,
  ChangeDetectorRef,
  PLATFORM_ID,
  computed,
} from '@angular/core';
import { CommonModule, isPlatformBrowser } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import {
  DropdownComponent,
  DropdownOption,
  ModalComponent,
  ToastService,
  CardComponent,
  SkeletonComponent,
} from '../../../../shared/components';
import {
  VerifikasiCustomerService,
  VerifikasiCustomerDetail,
  VerifikasiCustomerRequest,
} from '../../../../core';
import {
  LucideExternalLink,
  LucideUser,
  LucideSend,
  LucideCircleAlert,
  LucideClock,
  LucideShieldCheck,
  LucideBriefcase,
  LucideCreditCard,
  LucideArrowLeft,
  LucideCheckCircle2,
  LucideXCircle,
  LucideAlertTriangle,
  LucideRotateCcw,
} from '@lucide/angular';
import { environment } from '../../../../../environments/environment';
import { formatDate as formatDateHelper } from '../../../../shared/utils/date.util';

@Component({
  selector: 'app-verifikasi-customer-detail',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    RouterModule,
    DropdownComponent,
    ModalComponent,
    CardComponent,
    SkeletonComponent,
    LucideExternalLink,
    LucideUser,
    LucideSend,
    LucideCircleAlert,
    LucideClock,
    LucideShieldCheck,
    LucideBriefcase,
    LucideCreditCard,
    LucideArrowLeft,
    LucideCheckCircle2,
    LucideXCircle,
    LucideAlertTriangle,
    LucideRotateCcw,
  ],
  templateUrl: './verifikasi-customer-detail.component.html',
  styleUrl: './verifikasi-customer-detail.component.css',
})
export class VerifikasiCustomerDetailComponent implements OnInit {
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private verifikasiService = inject(VerifikasiCustomerService);
  private toastService = inject(ToastService);
  private cdr = inject(ChangeDetectorRef);
  private platformId = inject(PLATFORM_ID);

  goBack(): void {
    this.router.navigate(['/verifikasi-customer']);
  }

  // Signals
  customerId = signal<string>('');
  detail = signal<VerifikasiCustomerDetail | null>(null);
  isLoading = signal<boolean>(true);
  isSubmitting = signal<boolean>(false);
  isConfirmModalOpen = signal<boolean>(false);
  isSuccessModalOpen = signal<boolean>(false);
  lastSubmittedStatus = signal<string>('');

  // Form Signals
  selectedStatusVerifikasi = signal<string>('');
  selectedKategoriAlasan = signal<string>('');
  catatanVerifikasi = signal<string>('');

  // Image Preview Lightbox Modal
  isImageModalOpen = signal<boolean>(false);
  previewImageUrl = signal<string>('');
  previewImageTitle = signal<string>('');

  // Fallback photo error signals
  selfieError = signal<boolean>(false);
  ktpError = signal<boolean>(false);

  readonly statusVerifikasiOptions: DropdownOption[] = [
    { value: 'APPROVED', label: 'Disetujui (APPROVED)' },
    { value: 'PERLU_REVISI', label: 'Perlu Revisi (PERLU_REVISI)' },
    { value: 'REJECTED', label: 'Ditolak (REJECTED)' },
  ];

  readonly presetReasons: Record<string, DropdownOption[]> = {
    APPROVED: [
      {
        value:
          'Dokumen fisik e-KTP dan foto selfie liveness lengkap, jelas, dan sesuai dengan data identitas kependudukan.',
        label: 'Dokumen KTP & Selfie Valid (Lolos Verifikasi)',
      },
      {
        value:
          'Data identitas, pekerjaan, rekening, dan foto KYC nasabah terverifikasi valid.',
        label: 'Seluruh Data & KYC Terverifikasi Lengkap',
      },
      { value: 'LAINNYA', label: 'Lainnya (Tulis catatan khusus)...' },
    ],
    PERLU_REVISI: [
      {
        value:
          'Foto fisik e-KTP buram atau nomor NIK tidak terbaca jelas. Mohon unggah ulang foto fisik e-KTP asli.',
        label: 'Foto e-KTP Buram / Tidak Terbaca Jelas',
      },
      {
        value:
          'Foto selfie / liveness buram, terpotong, atau wajah tidak terlihat jelas. Mohon unggah ulang foto selfie wajah terbaru.',
        label: 'Foto Selfie Buram / Wajah Terpotong',
      },
      {
        value:
          'Data NIK atau Nama Lengkap yang diinput tidak sesuai dengan fisik e-KTP. Mohon unggah ulang foto e-KTP yang valid.',
        label: 'Data Input Tidak Sesuai Fisik e-KTP',
      },
      {
        value:
          'Dokumen foto bukan fisik e-KTP asli (fotokopi / foto dari layar monitor). Mohon unggah foto fisik e-KTP asli.',
        label: 'Bukan Fisik e-KTP Asli (Fotokopi / Layar)',
      },
      { value: 'LAINNYA', label: 'Lainnya (Tulis catatan khusus)...' },
    ],
    REJECTED: [
      {
        value: 'Dokumen e-KTP terindikasi palsu / manipulasi digital.',
        label: 'Dokumen Terindikasi Manipulasi / Palsu',
      },
      {
        value:
          'Wajah pada foto selfie liveness tidak sesuai / berbeda orang dengan foto fisik e-KTP.',
        label: 'Wajah Selfie Berbeda dengan Foto e-KTP',
      },
      {
        value:
          'Data pendaftaran nasabah tidak memenuhi kriteria dan regulasi verifikasi identitas SAKU.',
        label: 'Tidak Memenuhi Kriteria Verifikasi SAKU',
      },
      { value: 'LAINNYA', label: 'Lainnya (Tulis catatan khusus)...' },
    ],
  };

  get kategoriAlasanOptions(): DropdownOption[] {
    const s = this.selectedStatusVerifikasi();
    return this.presetReasons[s] || [];
  }

  ngOnInit(): void {
    if (isPlatformBrowser(this.platformId)) {
      this.route.paramMap.subscribe((params) => {
        const id = params.get('id');
        if (id) {
          this.customerId.set(id);
          this.loadDetail(id);
        } else {
          this.isLoading.set(false);
          this.toastService.error('ID Customer tidak valid');
          this.router.navigate(['/verifikasi-customer']);
        }
      });
    }
  }

  loadDetail(id: string): void {
    this.isLoading.set(true);
    this.verifikasiService.getDetail(id).subscribe({
      next: (res) => {
        if (res) {
          this.detail.set(res);
          // Pre-populate if already verified
          if (res.statusVerifikasi && res.statusVerifikasi !== 'PENDING') {
            this.selectedStatusVerifikasi.set(res.statusVerifikasi);
          }
          if (res.catatanVerifikasi) {
            this.catatanVerifikasi.set(res.catatanVerifikasi);
          }
        } else {
          this.toastService.error('Data verifikasi customer tidak ditemukan');
        }
        this.isLoading.set(false);
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error('Failed to load customer detail:', err);
        this.isLoading.set(false);
        this.toastService.error('Gagal memuat detail data customer');
        this.cdr.detectChanges();
      },
    });
  }

  onStatusChange(event: DropdownOption | null | string): void {
    let val = '';
    if (event && typeof event === 'object' && 'value' in event) {
      val = event.value || '';
    } else if (event) {
      val = String(event);
    }
    this.selectedStatusVerifikasi.set(val);
    this.selectedKategoriAlasan.set('');

    const presets = this.presetReasons[val];
    if (presets && presets.length > 0 && presets[0].value !== 'LAINNYA') {
      this.selectedKategoriAlasan.set(presets[0].value || '');
      this.catatanVerifikasi.set(presets[0].value || '');
    } else {
      this.catatanVerifikasi.set('');
    }
  }

  onKategoriAlasanChange(event: DropdownOption | null | string): void {
    let val = '';
    if (event && typeof event === 'object' && 'value' in event) {
      val = event.value || '';
    } else if (event) {
      val = String(event);
    }
    this.selectedKategoriAlasan.set(val);
    if (val === 'LAINNYA') {
      this.catatanVerifikasi.set('');
    } else if (val) {
      this.catatanVerifikasi.set(val);
    }
  }

  openConfirmModal(): void {
    const status = this.selectedStatusVerifikasi();
    const catatan = this.catatanVerifikasi().trim();

    if (!status) {
      this.toastService.warning('Silakan pilih Status Verifikasi terlebih dahulu');
      return;
    }

    if (!catatan) {
      this.toastService.warning('Catatan verifikasi wajib diisi');
      return;
    }

    this.isConfirmModalOpen.set(true);
  }

  closeConfirmModal(): void {
    this.isConfirmModalOpen.set(false);
  }

  confirmSubmitVerifikasi(): void {
    const id = this.customerId();
    const status = this.selectedStatusVerifikasi();
    const catatan = this.catatanVerifikasi().trim();

    const payload: VerifikasiCustomerRequest = {
      statusVerifikasi: status,
      catatanVerifikasi: catatan,
    };

    this.isSubmitting.set(true);

    this.verifikasiService.verifikasi(id, payload).subscribe({
      next: () => {
        this.isSubmitting.set(false);
        this.isConfirmModalOpen.set(false);
        this.lastSubmittedStatus.set(status);
        this.isSuccessModalOpen.set(true);
        this.toastService.success(
          `Keputusan verifikasi berhasil disimpan: ${this.getStatusDisplayLabel(status)}`
        );
        this.loadDetail(id);
      },
      error: (err) => {
        this.isSubmitting.set(false);
        console.error('Failed to submit verification:', err);
        const errMsg =
          err?.error?.message || err?.message || 'Terjadi kesalahan saat memproses verifikasi';
        this.toastService.error(errMsg);
        this.cdr.detectChanges();
      },
    });
  }

  closeSuccessModal(): void {
    this.isSuccessModalOpen.set(false);
  }

  navigateToList(): void {
    this.isSuccessModalOpen.set(false);
    this.router.navigate(['/verifikasi-customer']);
  }

  // Image Preview Helpers
  openImagePreview(path?: string | null, title?: string): void {
    if (!path) {
      this.toastService.warning('File gambar tidak tersedia');
      return;
    }
    const fullUrl = this.verifikasiService.getFileUrl(path);
    this.previewImageUrl.set(fullUrl);
    this.previewImageTitle.set(title || 'Preview Dokumen');
    this.isImageModalOpen.set(true);
  }

  closeImageModal(): void {
    this.isImageModalOpen.set(false);
    this.previewImageUrl.set('');
  }

  openDocument(fileUrl?: string, docType?: string): void {
    if (!fileUrl) {
      this.toastService.warning(`Dokumen ${docType || ''} tidak tersedia`);
      return;
    }

    if (fileUrl.startsWith('http://') || fileUrl.startsWith('https://')) {
      window.open(fileUrl, '_blank', 'noopener,noreferrer');
      return;
    }

    const fullUrl = this.verifikasiService.getFileUrl(fileUrl);
    window.open(fullUrl, '_blank', 'noopener,noreferrer');
  }

  getDokumenList(): { type: string; label: string; fileUrl?: string }[] {
    const d = this.detail();
    if (!d) return [];

    const list: { type: string; label: string; fileUrl?: string }[] = [];
    if (d.fotoKtp) list.push({ type: 'KTP', label: 'Foto e-KTP', fileUrl: d.fotoKtp });
    if (d.fotoSelfie) list.push({ type: 'SELFIE', label: 'Foto Selfie', fileUrl: d.fotoSelfie });

    return list;
  }

  openInNewTab(path?: string | null): void {
    if (!path) return;
    const url = this.verifikasiService.getFileUrl(path);
    window.open(url, '_blank', 'noopener,noreferrer');
  }

  getFotoUrl(path?: string | null): string {
    return this.verifikasiService.getFileUrl(path);
  }

  isPending(): boolean {
    const s = (this.detail()?.statusVerifikasi || 'PENDING').toUpperCase();
    return s === 'PENDING' || s === 'MENUNGGU_VERIFIKASI' || s === 'MENUNGGU';
  }

  // UI Formatters
  getStatusDisplayLabel(status?: string): string {
    const s = (status || this.detail()?.statusVerifikasi || 'PENDING').toUpperCase();
    switch (s) {
      case 'APPROVED':
      case 'DISETUJUI':
      case 'VERIFIED':
        return 'Disetujui (APPROVED)';
      case 'REJECTED':
      case 'DITOLAK':
        return 'Ditolak (REJECTED)';
      case 'PERLU_REVISI':
      case 'REVISI':
        return 'Perlu Revisi (PERLU_REVISI)';
      case 'PENDING':
      default:
        return 'Menunggu Verifikasi';
    }
  }

  getStatusPillClass(status?: string): string {
    const s = (status || this.detail()?.statusVerifikasi || 'PENDING').toUpperCase();
    if (s === 'APPROVED' || s === 'DISETUJUI' || s === 'VERIFIED') {
      return 'bg-success-0 border border-success-20 text-success-70';
    }
    if (s === 'REJECTED' || s === 'DITOLAK') {
      return 'bg-error-0 border border-error-20 text-error-70';
    }
    if (s === 'PERLU_REVISI' || s === 'REVISI') {
      return 'bg-warning-0 border border-warning-20 text-warning-80';
    }
    return 'bg-warning-0 border border-warning-20 text-warning-80';
  }

  formatCurrency(val?: number | null): string {
    if (val === null || val === undefined || isNaN(val)) return 'Rp 0';
    return 'Rp ' + new Intl.NumberFormat('id-ID').format(val);
  }

  getTanggalRegister(row?: any): string {
    if (!row) return '-';
    const val =
      row.tanggalRegister ||
      row.tanggalRegistrasi ||
      row.tanggalDaftar ||
      row.createdDate ||
      row.createdAt ||
      row.registrationDate ||
      row.registeredAt ||
      row.tglRegister ||
      row.tglRegistrasi ||
      row.tglDaftar ||
      row.tanggalPengajuan ||
      row.tanggalVerifikasi;
    return this.formatDate(val);
  }

  formatDate(dateStr?: string | null): string {
    return formatDateHelper(dateStr);
  }

  formatStatusPekerjaan(s?: string): string {
    if (!s) return '-';
    switch (s.toUpperCase()) {
      case 'KARYAWAN_TETAP':
        return 'Karyawan Tetap';
      case 'KARYAWAN_KONTRAK':
        return 'Karyawan Kontrak';
      case 'WIRAUSAHA':
      case 'PENGUSAHA':
        return 'Wirausaha / Pengusaha';
      case 'PROFESIONAL':
        return 'Profesional';
      case 'PNS':
        return 'Pegawai Negeri Sipil (PNS)';
      default:
        return s.replace(/_/g, ' ');
    }
  }

  isVerifikasiApproved(): boolean {
    const label = this.getStatusDisplayLabel();
    return label === 'Disetujui (APPROVED)';
  }

  isVerifikasiRejected(): boolean {
    const label = this.getStatusDisplayLabel();
    return label === 'Ditolak (REJECTED)';
  }

  isVerifikasiRevision(): boolean {
    const label = this.getStatusDisplayLabel();
    return label === 'Perlu Revisi (PERLU_REVISI)';
  }

  formatDateTime(dateStr?: string | null): string {
    if (!dateStr) return '-';
    try {
      const d = new Date(dateStr);
      if (isNaN(d.getTime())) return String(dateStr);
      return new Intl.DateTimeFormat('id-ID', {
        day: 'numeric',
        month: 'short',
        year: 'numeric',
        hour: '2-digit',
        minute: '2-digit',
      }).format(d);
    } catch {
      return String(dateStr);
    }
  }
}
