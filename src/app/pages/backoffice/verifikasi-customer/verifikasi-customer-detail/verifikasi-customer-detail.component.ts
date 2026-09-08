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

  // Form Signals
  selectedStatusVerifikasi = signal<string>('');
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
    if (!event) {
      this.selectedStatusVerifikasi.set('');
    } else if (typeof event === 'object' && 'value' in event) {
      this.selectedStatusVerifikasi.set(event.value || '');
    } else {
      this.selectedStatusVerifikasi.set(String(event));
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
}
