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
  SkeletonComponent,
} from '../../../../shared/components';
import {
  MarketingLoanService,
  MarketingPengajuanDetailResponse,
  ReviewPengajuanRequest,
  DokumenPinjamanItem,
  ReviewHistoryItem,
  RealTimeService,
} from '../../../../core';
import {
  LucideFileText,
  LucideExternalLink,
  LucideUser,
  LucideBanknote,
  LucideShieldCheck,
  LucideClipboardCheck,
  LucideClock,
  LucideInfo,
  LucideSend,
  LucideBriefcase,
  LucideArrowLeft,
  LucideCheckCircle2,
  LucideXCircle,
  LucideAlertTriangle,
} from '@lucide/angular';
import { environment } from '../../../../../environments/environment';
import { formatDate as formatDateHelper } from '../../../../shared/utils/date.util';

export interface DisplayDocItem {
  id?: string;
  name: string;
  type: string;
  url: string;
  isImage?: boolean;
}

@Component({
  selector: 'app-pengajuan-pinjaman-detail',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    RouterModule,
    DropdownComponent,
    ModalComponent,
    SkeletonComponent,
    LucideFileText,
    LucideExternalLink,
    LucideUser,
    LucideBanknote,
    LucideShieldCheck,
    LucideClipboardCheck,
    LucideClock,
    LucideInfo,
    LucideSend,
    LucideBriefcase,
    LucideArrowLeft,
    LucideCheckCircle2,
    LucideXCircle,
    LucideAlertTriangle,
  ],
  templateUrl: './pengajuan-pinjaman-detail.component.html',
  styleUrl: './pengajuan-pinjaman-detail.component.css',
})
export class PengajuanPinjamanDetailComponent implements OnInit, OnDestroy {
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private marketingService = inject(MarketingLoanService);
  private realtimeService = inject(RealTimeService);
  private toastService = inject(ToastService);
  private platformId = inject(PLATFORM_ID);
  private cdr = inject(ChangeDetectorRef);
  private destroy$ = new Subject<void>();

  goBack(): void {
    this.router.navigate(['/pengajuan-pinjaman']);
  }

  pengajuanId: string = '';
  isLoading = signal<boolean>(true);
  detail = signal<MarketingPengajuanDetailResponse | null>(null);

  // Review Form
  selectedReviewStatus = signal<string>('');
  selectedKategoriAlasan = signal<string>('');
  catatanReview = signal<string>('');
  isSubmitting = signal<boolean>(false);
  isConfirmModalOpen = signal<boolean>(false);

  // Photo error fallback handling
  photoError = signal<boolean>(false);
  cacheBuster = signal<number>(Date.now());

  onPhotoError(): void {
    this.photoError.set(true);
  }

  readonly reviewStatusOptions: DropdownOption[] = [
    { value: 'DISETUJUI', label: 'Disetujui' },
    { value: 'PERLU_REVISI', label: 'Perlu Revisi Dokumen' },
    { value: 'DITOLAK', label: 'Tolak Pengajuan' },
  ];

  readonly presetReasons: Record<string, DropdownOption[]> = {
    DISETUJUI: [
      {
        value: 'Dokumen lengkap dan valid. Pendapatan, slip gaji, dan mutasi rekening sesuai kemampuan bayar customer.',
        label: 'Dokumen & Kapasitas Bayar Valid (Lolos)',
      },
      {
        value: 'Data pekerjaan terverifikasi valid, status karyawan tetap dengan masa kerja stabil.',
        label: 'Data Pekerjaan Stabil & Terverifikasi',
      },
      {
        value: 'Credit scoring dan rasio DBR sehat, direkomendasikan lanjut ke persetujuan Branch Manager.',
        label: 'Direkomendasikan Lanjut ke Branch Manager',
      },
      { value: 'LAINNYA', label: 'Lainnya (Tulis catatan khusus)...' },
    ],
    PERLU_REVISI: [
      {
        value: 'Rekening koran wajib memuat mutasi 3 bulan terakhir. Berkas yang diunggah belum genap 3 bulan. Mohon unggah ulang rekening koran 3 bulan lengkap.',
        label: 'Rekening Koran Kurang dari 3 Bulan',
      },
      {
        value: 'Berkas rekening koran terpotong, buram, atau rincian transaksi tidak terbaca jelas. Mohon unggah ulang dokumen asli/PDF resmi.',
        label: 'Rekening Koran Buram / Terpotong',
      },
      {
        value: 'Slip gaji tidak jelas, terpotong, atau tidak memuat stempel/pengesahan resmi HRD perusahaan. Mohon unggah slip gaji resmi.',
        label: 'Slip Gaji Buram / Tanpa Pengesahan HRD',
      },
      {
        value: 'Nominal penghasilan pada slip gaji tidak sesuai dengan data penghasilan yang diinput pada formulir pengajuan. Mohon unggah bukti penghasilan yang valid.',
        label: 'Nominal Slip Gaji Tidak Sesuai Input',
      },
      {
        value: 'Foto kartu NPWP buram, terpotong, atau nomor NPWP tidak terbaca jelas. Mohon unggah ulang foto fisik kartu NPWP yang jelas.',
        label: 'Foto Kartu NPWP Buram / Nomor Tidak Terbaca',
      },
      {
        value: 'Berkas dokumen salah input atau tertukar pada kolom unggahan (misal slip gaji dan rekening koran tertukar). Mohon unggah berkas pada kolom yang sesuai.',
        label: 'Dokumen Tertukar / Salah Kolom Unggah',
      },
      { value: 'LAINNYA', label: 'Lainnya (Tulis catatan khusus)...' },
    ],
    DITOLAK: [
      {
        value: 'Dokumen keuangan atau bukti penghasilan tidak valid / terindikasi manipulasi.',
        label: 'Dokumen Keuangan Tidak Valid / Manipulasi',
      },
      {
        value: 'Penghasilan bulanan di bawah kriteria minimum pembiayaan atau DBR melampaui batas toleransi.',
        label: 'Penghasilan Tidak Memenuhi Standar Minimal',
      },
      {
        value: 'Profil pekerjaan dan masa kerja tidak memenuhi kriteria kelayakan kredit.',
        label: 'Profil Pekerjaan Tidak Memenuhi Syarat',
      },
      { value: 'LAINNYA', label: 'Lainnya (Tulis catatan khusus)...' },
    ],
  };

  get kategoriAlasanOptions(): DropdownOption[] {
    const s = this.selectedReviewStatus();
    return this.presetReasons[s] || [];
  }

  private lastActionTimestamp = 0;

  ngOnInit(): void {
    this.pengajuanId = this.route.snapshot.paramMap.get('id') || '';
    if (isPlatformBrowser(this.platformId) && this.pengajuanId) {
      this.loadDetail();

      this.realtimeService.events$
        .pipe(takeUntil(this.destroy$))
        .subscribe((event) => {
          // Abaikan event dari hasil review sendiri atau jika baru saja melakukan aksi lokal
          if (event.eventType === 'LOAN_REVIEWED' || event.eventType === 'LOAN_READY_FOR_BM') {
            return;
          }
          if (Date.now() - this.lastActionTimestamp < 3500) {
            return;
          }

          const loanId = (this.pengajuanId || '').toLowerCase();
          const refId = (event.referenceId || '').toLowerCase();
          const noPengajuan = (this.detail()?.nomorPengajuan || '').toLowerCase();
          const evtNoPengajuan = (event.nomorPengajuan || '').toLowerCase();
          const custId = (this.detail()?.customerId || '').toLowerCase();

          const isMatch =
            (!!refId && (refId === loanId || refId === custId)) ||
            (!!evtNoPengajuan && !!noPengajuan && evtNoPengajuan === noPengajuan);

          if (isMatch) {
            this.toastService.info(
              event.message || 'Terdapat pembaruan data/berkas nasabah untuk pengajuan ini. Data diperbarui otomatis.'
            );
            this.photoError.set(false);
            this.cacheBuster.set(Date.now());
            if (event.eventType === 'LOAN_REVISED' || event.eventType === 'LOAN_SUBMITTED' || event.eventType === 'KYC_REVISED') {
              this.selectedReviewStatus.set('');
              this.selectedKategoriAlasan.set('');
              this.catatanReview.set('');
            }
            this.loadDetail();
          }
        });
    }
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  loadDetail(): void {
    this.isLoading.set(true);
    this.photoError.set(false);
    this.cacheBuster.set(Date.now());

    this.marketingService.getDetail(this.pengajuanId).subscribe({
      next: (res) => {
        this.detail.set(res);
        this.isLoading.set(false);
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error('Failed to fetch detail loan application:', err);
        this.isLoading.set(false);
        this.toastService.error('Gagal mengambil data detail pengajuan');
        this.cdr.detectChanges();
      },
    });
  }

  navigateBack(): void {
    this.router.navigate(['/pengajuan-pinjaman']);
  }

  onReviewStatusChange(event: DropdownOption | null | string): void {
    let val = '';
    if (event && typeof event === 'object' && 'value' in event) {
      val = event.value || '';
    } else if (event) {
      val = String(event);
    }
    this.selectedReviewStatus.set(val);
    this.selectedKategoriAlasan.set('');

    const presets = this.presetReasons[val];
    if (presets && presets.length > 0 && presets[0].value !== 'LAINNYA') {
      this.selectedKategoriAlasan.set(presets[0].value || '');
      this.catatanReview.set(presets[0].value || '');
    } else {
      this.catatanReview.set('');
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
      this.catatanReview.set('');
    } else if (val) {
      this.catatanReview.set(val);
    }
  }

  openConfirmModal(): void {
    const decision = this.selectedReviewStatus();
    const note = this.catatanReview().trim();

    if (!decision) {
      this.toastService.warning('Silakan pilih Status Review terlebih dahulu');
      return;
    }

    if (!note) {
      this.toastService.warning('Catatan pertimbangan review wajib diisi');
      return;
    }

    this.isConfirmModalOpen.set(true);
  }

  closeConfirmModal(): void {
    this.isConfirmModalOpen.set(false);
  }

  confirmSubmitReview(): void {
    const decision = this.selectedReviewStatus();
    const note = this.catatanReview().trim();

    const payload: ReviewPengajuanRequest = {
      hasilReview: decision,
      catatan: note || 'Review pengajuan telah diselesaikan oleh Marketing.',
      kategoriAlasan: this.selectedKategoriAlasan() || undefined,
    };

    this.isSubmitting.set(true);
    this.lastActionTimestamp = Date.now();

    this.marketingService.review(this.pengajuanId, payload).subscribe({
      next: () => {
        this.lastActionTimestamp = Date.now();
        this.isSubmitting.set(false);
        this.isConfirmModalOpen.set(false);
        this.toastService.success(
          `Keputusan review berhasil disimpan: ${this.getDecisionLabel(decision)}`
        );
        this.loadDetail();
      },
      error: (err) => {
        this.isSubmitting.set(false);
        console.error('Error submitting review:', err);
        const errMsg =
          err?.error?.message || err?.message || 'Gagal menyimpan keputusan review';
        this.toastService.error(errMsg);
      },
    });
  }

  // Getters for Template
  getFormattedNomorPengajuan(): string {
    const d = this.detail();
    const no = d?.nomorPengajuan || d?.noPengajuan || 'PJ-202608-000123';
    return no.replace(/-/g, ' – ');
  }

  getCustomerName(): string {
    const d = this.detail();
    return d?.namaLengkap || d?.namaCustomer || d?.nama || d?.customer?.nama || '-';
  }

  getNik(): string {
    const d = this.detail();
    return d?.nik || d?.customer?.nik || '-';
  }

  getNoHp(): string {
    const d = this.detail();
    return d?.noHp || d?.customer?.noHp || '-';
  }

  getEmail(): string {
    const d = this.detail();
    return d?.email || d?.customer?.email || '-';
  }

  getNamaIbuKandung(): string {
    const d = this.detail();
    return d?.namaIbuKandung || (d as any)?.customer?.namaIbuKandung || '-';
  }

  getPekerjaan(): string {
    const d = this.detail();
    return (
      d?.pekerjaan ||
      d?.scoringStatusPekerjaan ||
      d?.statusPekerjaan ||
      'Staff Administrasi'
    );
  }

  getTempatKerja(): string {
    const d = this.detail();
    return d?.tempatKerja || 'PT Sinar Jaya';
  }

  getStatusPekerjaanLabel(): string {
    const d = this.detail();
    const s = (d?.statusPekerjaan || d?.scoringStatusPekerjaan || '').toUpperCase();
    if (s === 'KARYAWAN_TETAP') return 'Karyawan Tetap';
    if (s === 'KARYAWAN_KONTRAK') return 'Karyawan Kontrak';
    if (s === 'WIRASWASTA' || s === 'WIRAUSAHA' || s === 'PENGUSAHA') return 'Wiraswasta';
    if (s === 'PROFESIONAL') return 'Profesional';
    if (s === 'PNS' || s === 'PNS_BUMN' || s === 'PEGAWAI_NEGERI') return 'PNS / Pegawai BUMN';
    if (s === 'IBU_RUMAH_TANGGA') return 'Ibu Rumah Tangga';
    return s ? s.replace(/_/g, ' ') : '-';
  }

  getPendapatanBulanan(): number {
    const d = this.detail();
    return (
      d?.penghasilanBulanan ??
      d?.pendapatan ??
      d?.scoringPenghasilan ??
      4500000
    );
  }

  getNamaBank(): string {
    const d = this.detail();
    return d?.namaBank || 'BCA';
  }

  getNoRekening(): string {
    const d = this.detail();
    return d?.noRekening || '6234567890';
  }

  getNamaRekening(): string {
    const d = this.detail();
    return d?.namaRekening || this.getCustomerName();
  }

  getAlamatKtp(): string {
    const d = this.detail();
    if (d?.alamatKtp) {
      if (typeof d.alamatKtp === 'object' && d.alamatKtp.formattedAddress) {
        return d.alamatKtp.formattedAddress;
      }
      if (typeof d.alamatKtp === 'string') return d.alamatKtp;
    }
    return d?.alamat || 'Jl. Tebet Raya No. 30, RT 006/RW 008, Kel. Tebet Timur, Kec. Tebet, Jakarta Selatan, DKI Jakarta 12820';
  }

  getAlamatDomisili(): string {
    const d = this.detail();
    if (d?.alamatDomisili) {
      if (typeof d.alamatDomisili === 'object' && d.alamatDomisili.formattedAddress) {
        return d.alamatDomisili.formattedAddress;
      }
      if (typeof d.alamatDomisili === 'string') return d.alamatDomisili;
    }
    return this.getAlamatKtp();
  }

  getLamaBekerja(): number {
    const d = this.detail();
    return d?.lamaBekerjaBulan ?? d?.lamaBekerja ?? d?.lamaKerja ?? 20;
  }

  getCicilanBerjalan(): number {
    const d = this.detail();
    return d?.cicilanBerjalan ?? 800000;
  }

  getSkor(): number {
    const d = this.detail();
    const val =
      d?.skorKredit ??
      d?.skor ??
      (d as any)?.scoring?.skorKredit ??
      (d as any)?.scoring?.skor ??
      (d as any)?.scoring?.score ??
      (d as any)?.scoring?.totalSkor ??
      (d as any)?.creditScore ??
      (d as any)?.score ??
      (d as any)?.nilaiSkor ??
      (d as any)?.customer?.skorKredit ??
      (d as any)?.customer?.skor ??
      null;

    if (val !== null && val !== undefined && !isNaN(Number(val))) {
      return Number(val);
    }
    return 0;
  }

  getScoreColorClass(): string {
    const s = this.getSkor();
    const status = (this.detail()?.statusScoring || '').toUpperCase();
    if (status === 'REJECTED' || s < 60) return 'text-error-60';
    if (status === 'REVIEW' || (s >= 60 && s < 75)) return 'text-warning-60';
    return 'text-success-60';
  }

  getScoreBadgeClass(): string {
    const s = this.getSkor();
    const status = (this.detail()?.statusScoring || '').toUpperCase();
    if (status === 'REJECTED' || s < 60) {
      return 'bg-error-0 text-error-70 border border-error-20';
    }
    if (status === 'REVIEW' || (s >= 60 && s < 75)) {
      return 'bg-warning-0 text-warning-80 border border-warning-20';
    }
    return 'bg-success-0 text-success-70 border border-success-20';
  }

  getPlafonNama(): string {
    const d = this.detail();
    return d?.plafonNama || 'Plafond Tier 1 - Starter';
  }

  getPlafonMaksimal(): number {
    const d = this.detail();
    return (
      d?.estimasiPlafondDisetujui ??
      d?.totalPlafond ??
      d?.plafonMaksimal ??
      15000000
    );
  }

  getDbrPercentage(): string {
    const d = this.detail();
    let num = 0;
    if (d?.dbrPercentage !== undefined && d?.dbrPercentage !== null && !isNaN(Number(d.dbrPercentage)) && Number(d.dbrPercentage) > 0) {
      num = Number(d.dbrPercentage);
    } else if (d?.dbr !== undefined && d?.dbr !== null && !isNaN(Number(d.dbr)) && Number(d.dbr) > 0) {
      const raw = Number(d.dbr);
      num = raw <= 1 ? raw * 100 : raw;
    } else {
      const cicilan = this.getCicilanBerjalan();
      const pendapatan = this.getPendapatanBulanan();
      if (pendapatan > 0 && cicilan > 0) {
        num = (cicilan / pendapatan) * 100;
      }
    }
    return num % 1 === 0 ? num.toFixed(0) : num.toFixed(1).replace('.', ',');
  }

  getDbrFormulaDetail(): string {
    const cicilan = this.getCicilanBerjalan();
    const pendapatan = this.getPendapatanBulanan();
    if (pendapatan > 0) {
      return `${this.formatCurrency(cicilan)} / ${this.formatCurrency(pendapatan)}`;
    }
    return '';
  }

  getDbrStatusLabel(): string {
    const pctStr = this.getDbrPercentage().replace(',', '.');
    const num = parseFloat(pctStr) || 0;
    if (num <= 30) return 'Kondisi Finansial Sehat';
    if (num <= 40) return 'Kondisi Finansial Wajar';
    if (num <= 50) return 'Perlu Diwaspadai';
    return 'Beban Utang Tinggi';
  }

  getDbrColorClass(): string {
    const pctStr = this.getDbrPercentage().replace(',', '.');
    const num = parseFloat(pctStr) || 0;
    if (num <= 30) return 'text-success-60';
    if (num <= 40) return 'text-warning-60';
    return 'text-error-60';
  }

  getKeputusanSistemLabel(): string {
    const d = this.detail();
    const raw = (d?.keputusanSistem || d?.statusScoring || '').toUpperCase();
    const skor = this.getSkor();

    if (raw.includes('LAYAK') && !raw.includes('TIDAK')) return 'Layak';
    if (raw.includes('TIDAK') || raw.includes('REJECT')) return 'Tidak Layak';
    if (raw.includes('REVIEW')) return 'Perlu Review';
    if (raw.includes('APPROV') || raw.includes('SETUJU')) return 'Layak';

    if (skor >= 75) return 'Layak';
    if (skor >= 60) return 'Perlu Review';
    return 'Tidak Layak';
  }

  isAmbigu(): boolean {
    const d = this.detail();
    if (d?.isAmbigu !== undefined && d?.isAmbigu !== null) {
      return Boolean(d.isAmbigu);
    }
    return false;
  }

  getNotesAmbigu(): string[] {
    const d = this.detail();
    return d?.notesAmbigu || [];
  }

  getRingkasanAnalisis(): string {
    const d = this.detail();
    return (
      d?.ringkasanAnalisis ||
      'Sistem mendeteksi adanya inkonsistensi/faktor ambigu antara skor kredit, pendapatan, atau rasio cicilan. Disarankan memeriksa kelengkapan dokumen dan kemampuan bayar customer.'
    );
  }

  getJumlahPinjaman(): number {
    const d = this.detail();
    return d?.jumlahPinjaman ?? d?.jumlah ?? d?.nominalPinjaman ?? 12000000;
  }

  getTenor(): number {
    const d = this.detail();
    return d?.tenorBulan ?? d?.tenor ?? 24;
  }

  getTujuanPinjaman(): string {
    const d = this.detail();
    return d?.tujuanPinjaman || 'Modal usaha sampingan';
  }

  getBungaLabel(): string {
    const d = this.detail();
    const b = d?.bunga;
    if (b !== undefined && b !== null) {
      const pct = b < 1 ? (b * 100).toFixed(1) : String(b);
      return `${pct.replace('.', ',')}%`;
    }
    return '6%';
  }

  getBiayaAdmin(): number {
    const d = this.detail();
    return d?.biayaAdmin ?? 100000;
  }

  getTanggalPengajuan(): string {
    const d = this.detail();
    const t = d?.tanggalPengajuan || d?.createdDate;
    return t ? this.formatDate(t) : '-';
  }

  getTanggalReviewTerakhir(): string {
    const d = this.detail();
    const t =
      d?.latestReview?.tanggalReview ||
      d?.tanggalReviewTerakhir ||
      d?.latestReview?.tanggal;
    return t ? this.formatDate(t) : '-';
  }

  getReviewHistory(): ReviewHistoryItem[] {
    const d = this.detail();
    if (d?.reviewHistory && Array.isArray(d.reviewHistory) && d.reviewHistory.length > 0) {
      return [...d.reviewHistory].sort((a, b) => {
        const timeA = new Date(a.tanggalReview || a.tanggal || a.createdDate || '').getTime() || 0;
        const timeB = new Date(b.tanggalReview || b.tanggal || b.createdDate || '').getTime() || 0;
        return timeA - timeB;
      });
    }
    return [];
  }

  getEstimasiCicilan(): number {
    const d = this.detail();
    return d?.estimasiCicilan ?? d?.angsuranBulanan ?? 500600;
  }

  getCabangName(): string {
    const d = this.detail();
    return d?.namaCabang || (typeof d?.cabang === 'object' ? d?.cabang?.nama : d?.cabang) || 'Kantor Pusat (Jakarta)';
  }

  getFileUrl(path: string | null | undefined): string {
    if (!path) return '';
    const baseHost = environment.apiUrl.replace(/\/api\/?$/, '');

    // Jika sudah berupa URL lengkap
    if (path.startsWith('http://') || path.startsWith('https://')) {
      return path
        .replace('/api/pinjaman/', '/uploads/pinjaman/')
        .replace('/api/ktp/', '/uploads/ktp/')
        .replace('/api/selfie/', '/uploads/selfie/');
    }

    // Bersihkan path
    let cleanPath = path.replace(/\\/g, '/');
    if (cleanPath.startsWith('/')) {
      cleanPath = cleanPath.substring(1);
    }
    // Jika path belum ada awalan 'uploads/'
    if (!cleanPath.startsWith('uploads/')) {
      cleanPath = `uploads/${cleanPath}`;
    }
    const resolvedUrl = `${baseHost}/${cleanPath}`;
    const sep = resolvedUrl.includes('?') ? '&' : '?';
    return `${resolvedUrl}${sep}_t=${this.cacheBuster()}`;
  }

  getFotoSelfieUrl(): string {
    const d = this.detail();
    return this.getFileUrl(d?.fotoSelfie);
  }

  getDokumenList(): { type: string; label: string; fileUrl?: string }[] {
    const d = this.detail();
    if (!d) return [];

    const list: { type: string; label: string; fileUrl?: string }[] = [];

    if (d.fotoKtp) list.push({ type: 'KTP', label: 'Foto KTP', fileUrl: d.fotoKtp });
    if (d.slipGaji)
      list.push({ type: 'SLIP_GAJI', label: 'Slip Gaji', fileUrl: d.slipGaji });
    if (d.rekeningKoran)
      list.push({ type: 'REKENING_KORAN', label: 'Rekening Koran', fileUrl: d.rekeningKoran });
    if (d.npwp)
      list.push({ type: 'NPWP', label: 'NPWP', fileUrl: d.npwp });

    const docs = d.dokumenPinjamanList || d.dokumenList;
    if (docs && Array.isArray(docs)) {
      docs.forEach((doc) => {
        const type = doc.docType || doc.jenisDokumen || 'DOKUMEN';
        if (type.toUpperCase() !== 'SELFIE' && !list.some((existing) => existing.type === type)) {
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
        return 'Foto KTP';
      case 'SLIP_GAJI':
        return 'Slip Gaji';
      case 'REKENING_KORAN':
        return 'Rekening Koran';
      case 'NPWP':
        return 'NPWP';
      case 'KK':
        return 'Kartu Keluarga';
      default:
        return type.replace(/_/g, ' ');
    }
  }

  getDocuments(): { type: string; label: string; fileUrl?: string }[] {
    return this.getDokumenList();
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

    const cleanPath = fileUrl.replace(/^\/+/, '');
    const baseUrl = environment.apiUrl.replace(/\/api\/?$/, '');
    const absoluteUrl = `${baseUrl}/uploads/${cleanPath}`;
    window.open(absoluteUrl, '_blank', 'noopener,noreferrer');
  }

  formatCurrency(val: number | null | undefined): string {
    if (val === null || val === undefined || isNaN(val)) return 'Rp 0';
    return 'Rp ' + new Intl.NumberFormat('id-ID').format(val);
  }

  formatDate(dateStr?: string | null): string {
    return formatDateHelper(dateStr);
  }

  getDecisionLabel(decision: string): string {
    switch (decision) {
      case 'DISETUJUI':
        return 'Disetujui';
      case 'PERLU_REVISI':
        return 'Perlu Revisi';
      case 'DITOLAK':
        return 'Ditolak';
      default:
        return decision;
    }
  }

  isPending(): boolean {
    const d = this.detail();
    const s = (d?.statusPengajuan || d?.status || 'MENUNGGU_REVIEW').toUpperCase();
    return s === 'MENUNGGU_REVIEW' || s === 'PENDING' || s === 'MENUNGGU_REVIEW_MARKETING';
  }

  getCatatanReview(): string {
    const d = this.detail();
    return d?.catatanReviewTerakhir || d?.latestReview?.catatan || d?.catatanPengajuan || '';
  }

  getNamaReviewer(): string {
    const d = this.detail();
    return d?.latestReview?.namaReviewer || d?.latestReview?.reviewerNama || 'Marketing SAKU';
  }

  getTanggalReview(): string {
    const d = this.detail();
    return d?.tanggalReviewTerakhir || d?.latestReview?.tanggalReview || d?.tanggalPengajuan || '';
  }

  getStatusDisplayLabel(): string {
    const d = this.detail();
    const h = (
      d?.latestReview?.hasilReview ||
      d?.latestReview?.status ||
      d?.hasilReviewTerakhir ||
      ''
    ).toUpperCase();
    const s = (d?.statusPengajuan || d?.status || 'MENUNGGU_REVIEW').toUpperCase();

    if (
      h === 'DISETUJUI' ||
      s === 'DISETUJUI' ||
      s === 'SELESAI_DIREVIEW' ||
      s === 'DICAIRKAN' ||
      s === 'DISBURSED' ||
      s.includes('BM') ||
      s.includes('CAIR') ||
      s.includes('BACKOFFICE')
    ) {
      return 'Disetujui Marketing';
    }
    if (h === 'DITOLAK' || s === 'DITOLAK') {
      return 'Ditolak Marketing';
    }
    if (
      s === 'PERLU_REVISI' ||
      s === 'DOKUMEN_DIREVISI' ||
      h === 'PERLU_REVISI' ||
      h === 'DOKUMEN_DIREVISI'
    ) {
      return 'Perlu Revisi';
    }
    if (s === 'MENUNGGU_REVIEW' || s === 'PENDING') {
      return 'Menunggu Review';
    }
    return 'Menunggu Review';
  }

  getStatusPillClass(): string {
    const label = this.getStatusDisplayLabel();
    if (label === 'Disetujui Marketing') {
      return 'bg-success-0 border border-success-20 text-success-70';
    }
    if (label === 'Ditolak Marketing') {
      return 'bg-error-0 border border-error-20 text-error-70';
    }
    if (label === 'Perlu Revisi') {
      return 'bg-warning-0 border border-warning-20 text-warning-80';
    }
    return 'bg-warning-0 border border-warning-20 text-warning-80';
  }

  isReviewApproved(): boolean {
    return this.getStatusDisplayLabel() === 'Disetujui Marketing';
  }

  isReviewRejected(): boolean {
    return this.getStatusDisplayLabel() === 'Ditolak Marketing';
  }

  isReviewRevision(): boolean {
    return this.getStatusDisplayLabel() === 'Perlu Revisi';
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
