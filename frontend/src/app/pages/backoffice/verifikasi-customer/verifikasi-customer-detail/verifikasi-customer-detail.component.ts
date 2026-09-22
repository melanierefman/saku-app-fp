import {
  Component,
  OnInit,
  OnDestroy,
  signal,
  inject,
  ChangeDetectorRef,
  PLATFORM_ID,
  computed,
} from '@angular/core';
import { CommonModule, isPlatformBrowser } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { Subject, takeUntil } from 'rxjs';
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
  RealTimeService,
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
  ],
  templateUrl: './verifikasi-customer-detail.component.html',
  styleUrl: './verifikasi-customer-detail.component.css',
})
export class VerifikasiCustomerDetailComponent implements OnInit, OnDestroy {
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private verifikasiService = inject(VerifikasiCustomerService);
  private realtimeService = inject(RealTimeService);
  private toastService = inject(ToastService);
  private cdr = inject(ChangeDetectorRef);
  private platformId = inject(PLATFORM_ID);
  private destroy$ = new Subject<void>();

  goBack(): void {
    this.router.navigate(['/verifikasi-customer']);
  }

  // Signals
  customerId = signal<string>('');
  detail = signal<VerifikasiCustomerDetail | null>(null);
  isLoading = signal<boolean>(true);
  isSubmitting = signal<boolean>(false);
  isConfirmModalOpen = signal<boolean>(false);

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
  cacheBuster = signal<number>(Date.now());

  readonly statusVerifikasiOptions: DropdownOption[] = [
    { value: 'APPROVED', label: 'Disetujui' },
    { value: 'PERLU_REVISI', label: 'Perlu Revisi' },
    { value: 'REJECTED', label: 'Ditolak' },
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
          'Wajah pada foto selfie liveness tidak sesuai / berbeda orang dengan foto fisik e-KTP. Mohon unggah ulang foto selfie wajah Anda sendiri.',
        label: 'Wajah Selfie Berbeda dengan Foto e-KTP',
      },
      {
        value:
          'Dokumen foto bukan fisik e-KTP asli (fotokopi / foto dari layar monitor). Mohon unggah foto fisik e-KTP asli.',
        label: 'Bukan Fisik e-KTP Asli (Fotokopi / Layar)',
      },
      {
        value:
          'Data NIK atau Nama Lengkap yang diinput tidak sesuai dengan fisik e-KTP. Mohon unggah ulang foto e-KTP yang valid.',
        label: 'Data Input Tidak Sesuai Fisik e-KTP',
      },
      { value: 'LAINNYA', label: 'Lainnya (Tulis catatan khusus)...' },
    ],
    REJECTED: [
      {
        value: 'Identitas nasabah terindikasi pemalsuan, manipulasi digital, atau terdaftar dalam daftar hitam (blacklist).',
        label: 'Identitas Terindikasi Pemalsuan / Fraud / Blacklist',
      },
      {
        value:
          'Usia nasabah tidak memenuhi kriteria dan regulasi layanan SAKU (kurang dari 21 tahun atau lebih dari 60 tahun).',
        label: 'Usia Tidak Memenuhi Kriteria (< 21 atau > 60 Tahun)',
      },
      {
        value:
          'Domisili tempat tinggal atau wilayah kerja nasabah berada di luar jangkauan operasional layanan SAKU.',
        label: 'Wilayah Domisili di Luar Jangkauan Layanan',
      },
      {
        value:
          'Profil nasabah tidak memenuhi standar kelayakan kredit dan kriteria risiko SAKU.',
        label: 'Tidak Memenuhi Standar Kelayakan Kredit SAKU',
      },
      { value: 'LAINNYA', label: 'Lainnya (Tulis catatan internal)...' },
    ],
  };

  get kategoriAlasanOptions(): DropdownOption[] {
    const s = this.selectedStatusVerifikasi();
    return this.presetReasons[s] || [];
  }

  private lastActionTimestamp = 0;

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

      this.realtimeService.kycUpdates$
        .pipe(takeUntil(this.destroy$))
        .subscribe((event) => {
          // Abaikan jika dalam masa cooldown setelah submit verifikasi sendiri
          if (Date.now() - this.lastActionTimestamp < 3500) {
            return;
          }

          const currentId = (this.customerId() || '').toLowerCase();
          const refId = (event.referenceId || '').toLowerCase();
          const isMatch = !!refId && (refId === currentId || currentId.includes(refId) || refId.includes(currentId));

          if (isMatch) {
            this.toastService.info(
              event.message || 'Nasabah telah mengunggah revisi dokumen identitas. Data diperbarui otomatis.'
            );
            this.ktpError.set(false);
            this.selfieError.set(false);
            this.cacheBuster.set(Date.now());
            this.loadDetail(this.customerId());
          }
        });
    }
  }

  loadDetail(id: string): void {
    this.isLoading.set(true);
    this.ktpError.set(false);
    this.selfieError.set(false);
    this.cacheBuster.set(Date.now());

    this.verifikasiService.getDetail(id).subscribe({
      next: (res) => {
        if (res) {
          this.detail.set(res);
          // Pre-populate if already verified, or reset if pending/revised
          if (res.statusVerifikasi && res.statusVerifikasi !== 'PENDING') {
            this.selectedStatusVerifikasi.set(res.statusVerifikasi);
            if (res.catatanVerifikasi) {
              this.catatanVerifikasi.set(res.catatanVerifikasi);
            }
          } else {
            this.selectedStatusVerifikasi.set('');
            this.selectedKategoriAlasan.set('');
            this.catatanVerifikasi.set('');
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
    this.lastActionTimestamp = Date.now();

    this.verifikasiService.verifikasi(id, payload).subscribe({
      next: () => {
        this.lastActionTimestamp = Date.now();
        this.isSubmitting.set(false);
        this.isConfirmModalOpen.set(false);
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
    const url = this.getFotoUrl(path);
    window.open(url, '_blank', 'noopener,noreferrer');
  }

  getFotoUrl(path?: string | null): string {
    const rawUrl = this.verifikasiService.getFileUrl(path);
    if (!rawUrl) return '';
    const sep = rawUrl.includes('?') ? '&' : '?';
    return `${rawUrl}${sep}_t=${this.cacheBuster()}`;
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
        return 'Disetujui';
      case 'REJECTED':
      case 'DITOLAK':
        return 'Ditolak';
      case 'PERLU_REVISI':
      case 'REVISI':
        return 'Perlu Revisi';
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
    const s = (this.detail()?.statusVerifikasi || '').toUpperCase();
    return s === 'APPROVED' || s === 'DISETUJUI' || s === 'VERIFIED';
  }

  isVerifikasiRejected(): boolean {
    const s = (this.detail()?.statusVerifikasi || '').toUpperCase();
    return s === 'REJECTED' || s === 'DITOLAK';
  }

  isVerifikasiRevision(): boolean {
    const s = (this.detail()?.statusVerifikasi || '').toUpperCase();
    return s === 'PERLU_REVISI' || s === 'REVISI';
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

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }
}
