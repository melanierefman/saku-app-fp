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
  ModalComponent,
  ToastService,
} from '../../../../shared/components';
import {
  PencairanService,
  PencairanDetail,
  AngsuranItem,
  PencairanRequest,
} from '../../../../core';
import {
  LucideExternalLink,
  LucideUser,
  LucideCreditCard,
  LucideSend,
  LucideAlertCircle,
  LucideClock,
  LucideShieldCheck,
  LucideBanknote,
  LucideCalendar,
  LucideCheckCircle2,
  LucideFileText,
  LucideArrowLeft,
} from '@lucide/angular';
import { environment } from '../../../../../environments/environment';
import { formatDate as formatDateHelper } from '../../../../shared/utils/date.util';

@Component({
  selector: 'app-pencairan-detail',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    RouterModule,
    ModalComponent,
    LucideExternalLink,
    LucideUser,
    LucideCreditCard,
    LucideSend,
    LucideAlertCircle,
    LucideClock,
    LucideShieldCheck,
    LucideBanknote,
    LucideCalendar,
    LucideCheckCircle2,
    LucideFileText,
    LucideArrowLeft,
  ],
  templateUrl: './pencairan-detail.component.html',
  styleUrl: './pencairan-detail.component.css',
})
export class PencairanDetailComponent implements OnInit {
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private pencairanService = inject(PencairanService);
  private toastService = inject(ToastService);
  private cdr = inject(ChangeDetectorRef);
  private platformId = inject(PLATFORM_ID);

  goBack(): void {
    this.router.navigate(['/pencairan']);
  }

  // Signals
  pengajuanId = signal<string>('');
  detail = signal<PencairanDetail | null>(null);
  angsuranList = signal<AngsuranItem[]>([]);
  isLoading = signal<boolean>(true);
  isSubmitting = signal<boolean>(false);
  isConfirmModalOpen = signal<boolean>(false);

  // Form Signals
  catatanPencairan = signal<string>('');

  // Image Preview Modal
  isImageModalOpen = signal<boolean>(false);
  previewImageUrl = signal<string>('');
  previewImageTitle = signal<string>('');

  // Photo error fallbacks
  selfieError = signal<boolean>(false);
  ktpError = signal<boolean>(false);

  ngOnInit(): void {
    if (isPlatformBrowser(this.platformId)) {
      this.route.paramMap.subscribe((params) => {
        const id = params.get('id');
        if (id) {
          this.pengajuanId.set(id);
          this.loadDetail(id);
        } else {
          this.isLoading.set(false);
          this.toastService.error('ID Pengajuan tidak valid');
          this.router.navigate(['/pencairan']);
        }
      });
    }
  }

  loadDetail(id: string): void {
    this.isLoading.set(true);
    this.pencairanService.getDetail(id).subscribe({
      next: (res) => {
        if (res) {
          this.detail.set(res);
          if (res.catatanPencairan) {
            this.catatanPencairan.set(res.catatanPencairan);
          }
          if (res.listAngsuran && Array.isArray(res.listAngsuran)) {
            this.angsuranList.set(res.listAngsuran);
          } else {
            // Fetch angsuran if already disbursed
            if (this.isAlreadyDisbursed(res.statusPencairan)) {
              this.loadAngsuran(id);
            }
          }
        } else {
          this.toastService.error('Data pencairan pinjaman tidak ditemukan');
        }
        this.isLoading.set(false);
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error('Failed to load pencairan detail:', err);
        this.isLoading.set(false);
        this.toastService.error('Gagal memuat detail pencairan');
        this.cdr.detectChanges();
      },
    });
  }

  loadAngsuran(id: string): void {
    this.pencairanService.getAngsuran(id).subscribe({
      next: (list) => {
        if (list && Array.isArray(list)) {
          this.angsuranList.set(list);
          this.cdr.detectChanges();
        }
      },
    });
  }

  isAlreadyDisbursed(status?: string): boolean {
    const s = (status || this.detail()?.statusPencairan || '').toUpperCase();
    return s === 'DICAIRKAN' || s === 'BERHASIL' || s === 'CAIR' || s === 'DISBURSED';
  }

  openConfirmModal(): void {
    if (this.isAlreadyDisbursed()) {
      this.toastService.warning('Pinjaman ini sudah dicairkan sebelumnya');
      return;
    }
    this.isConfirmModalOpen.set(true);
  }

  closeConfirmModal(): void {
    this.isConfirmModalOpen.set(false);
  }

  confirmSubmitPencairan(): void {
    const id = this.pengajuanId();
    const d = this.detail();
    const nominalCair = d?.jumlahPencairanBersih || Math.max(0, (d?.jumlahPinjaman || 0) - (d?.biayaAdmin || 0));

    const payload: PencairanRequest = {
      catatan: this.catatanPencairan().trim() || 'Pencairan dana telah berhasil ditransfer ke rekening customer',
      jumlahPencairan: nominalCair,
    };

    this.isSubmitting.set(true);

    this.pencairanService.cairkan(id, payload).subscribe({
      next: (res) => {
        this.isSubmitting.set(false);
        this.isConfirmModalOpen.set(false);
        this.toastService.success('Dana pinjaman berhasil dicairkan ke rekening customer!');
        if (res?.data?.listAngsuran && Array.isArray(res.data.listAngsuran)) {
          this.angsuranList.set(res.data.listAngsuran);
        }
        this.loadDetail(id);
      },
      error: (err) => {
        this.isSubmitting.set(false);
        console.error('Failed to disburse loan:', err);
        const errMsg =
          err?.error?.message || err?.message || 'Terjadi kesalahan saat memproses pencairan dana';
        this.toastService.error(errMsg);
        this.cdr.detectChanges();
      },
    });
  }

  // Document Helpers
  openDocument(fileUrl?: string, docType?: string): void {
    if (!fileUrl) {
      this.toastService.warning(`Dokumen ${docType || ''} tidak tersedia`);
      return;
    }

    if (fileUrl.startsWith('http://') || fileUrl.startsWith('https://')) {
      window.open(fileUrl, '_blank', 'noopener,noreferrer');
      return;
    }

    const fullUrl = this.pencairanService.getFileUrl(fileUrl);
    window.open(fullUrl, '_blank', 'noopener,noreferrer');
  }

  openImagePreview(path?: string | null, title?: string): void {
    if (!path) {
      this.toastService.warning('File gambar tidak tersedia');
      return;
    }
    const fullUrl = this.pencairanService.getFileUrl(path);
    this.previewImageUrl.set(fullUrl);
    this.previewImageTitle.set(title || 'Preview Dokumen');
    this.isImageModalOpen.set(true);
  }

  closeImageModal(): void {
    this.isImageModalOpen.set(false);
    this.previewImageUrl.set('');
  }

  getDokumenList(): { type: string; label: string; fileUrl?: string }[] {
    const d = this.detail();
    if (!d) return [];

    const list: { type: string; label: string; fileUrl?: string }[] = [];

    if (d.fotoKtp) list.push({ type: 'KTP', label: 'Foto e-KTP', fileUrl: d.fotoKtp });
    if (d.fotoSelfie) list.push({ type: 'SELFIE', label: 'Foto Selfie', fileUrl: d.fotoSelfie });
    if (d.fotoSlipGaji) list.push({ type: 'SLIP_GAJI', label: 'Slip Gaji', fileUrl: d.fotoSlipGaji });
    if (d.fotoRekeningKoran)
      list.push({ type: 'REKENING_KORAN', label: 'Rekening Koran', fileUrl: d.fotoRekeningKoran });
    if (d.fotoNpwp) list.push({ type: 'NPWP', label: 'NPWP', fileUrl: d.fotoNpwp });

    if (d.dokumenPinjamanList && Array.isArray(d.dokumenPinjamanList)) {
      d.dokumenPinjamanList.forEach((doc) => {
        const type = doc.docType || doc.jenisDokumen || 'DOKUMEN';
        if (!list.some((existing) => existing.type === type)) {
          list.push({
            type,
            label: this.formatDocLabel(type),
            fileUrl: doc.fileUrl,
          });
        }
      });
    }

    return list;
  }

  formatDocLabel(type: string): string {
    switch (type.toUpperCase()) {
      case 'KTP':
        return 'Foto e-KTP';
      case 'SELFIE':
        return 'Foto Selfie';
      case 'SLIP_GAJI':
        return 'Slip Gaji';
      case 'REKENING_KORAN':
        return 'Rekening Koran';
      case 'NPWP':
        return 'NPWP';
      default:
        return type.replace(/_/g, ' ');
    }
  }

  getFileUrl(path?: string | null): string {
    return this.pencairanService.getFileUrl(path);
  }

  // UI Formatters
  getFormattedNomorPengajuan(): string {
    const d = this.detail();
    const no = d?.nomorPengajuan || d?.noPengajuan || this.pengajuanId();
    if (!no) return '-';
    if (no.toUpperCase()) return no;
    return `${no}`;
  }

  getStatusPillClass(status?: string): string {
    const s = (status || this.detail()?.statusPencairan || '').toUpperCase();
    if (s === 'DICAIRKAN' || s === 'BERHASIL' || s === 'CAIR' || s === 'DISBURSED') {
      return 'bg-success-0 border border-success-20 text-success-70';
    }
    return 'bg-warning-0 border border-warning-20 text-warning-80';
  }

  getStatusDisplayLabel(status?: string): string {
    const s = (status || this.detail()?.statusPencairan || '').toUpperCase();
    if (s === 'DICAIRKAN' || s === 'BERHASIL' || s === 'CAIR' || s === 'DISBURSED') {
      return 'Telah Cair';
    }
    return 'Menunggu Pencairan';
  }

  formatCurrency(val?: number | null): string {
    if (val === null || val === undefined || isNaN(val)) return 'Rp 0';
    return 'Rp ' + new Intl.NumberFormat('id-ID').format(val);
  }

  formatDate(dateStr?: string | null): string {
    return formatDateHelper(dateStr, false);
  }

  formatDateTime(dateStr?: string | null): string {
    return formatDateHelper(dateStr, true);
  }
}
