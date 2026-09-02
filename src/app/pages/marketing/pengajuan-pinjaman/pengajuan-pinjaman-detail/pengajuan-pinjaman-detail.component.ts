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
  BreadcrumbsComponent,
  BreadcrumbItem,
  DropdownComponent,
  DropdownOption,
  ToastService,
} from '../../../../shared/components';
import {
  MarketingLoanService,
  MarketingPengajuanDetailResponse,
  ReviewPengajuanRequest,
  DokumenPinjamanItem,
  ReviewHistoryItem,
} from '../../../../core';
import {
  LucideFileText,
  LucideExternalLink,
  LucideUser,
  LucideBanknote,
  LucideShieldCheck,
  LucideClipboardCheck,
  LucideClock,
  LucideAlertTriangle,
} from '@lucide/angular';
import { environment } from '../../../../../environments/environment';

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
    BreadcrumbsComponent,
    DropdownComponent,
    LucideFileText,
    LucideExternalLink,
    LucideUser,
    LucideBanknote,
    LucideShieldCheck,
    LucideClipboardCheck,
    LucideClock,
    LucideAlertTriangle,
  ],
  templateUrl: './pengajuan-pinjaman-detail.component.html',
  styleUrl: './pengajuan-pinjaman-detail.component.css',
})
export class PengajuanPinjamanDetailComponent implements OnInit {
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private marketingService = inject(MarketingLoanService);
  private toastService = inject(ToastService);
  private platformId = inject(PLATFORM_ID);
  private cdr = inject(ChangeDetectorRef);

  pengajuanId: string = '';
  isLoading = signal<boolean>(true);
  detail = signal<MarketingPengajuanDetailResponse | null>(null);

  // Review Form
  selectedReviewStatus = signal<string>('');
  catatanReview = signal<string>('');
  isSubmitting = signal<boolean>(false);

  // Photo error fallback handling
  photoError = signal<boolean>(false);

  onPhotoError(): void {
    this.photoError.set(true);
  }

  readonly reviewStatusOptions: DropdownOption[] = [
    { value: 'DISETUJUI', label: 'Disetujui' },
    { value: 'PERLU_REVISI', label: 'Perlu Revisi' },
    { value: 'DITOLAK', label: 'Ditolak' },
  ];

  readonly breadcrumbs = computed<BreadcrumbItem[]>(() => [
    { label: 'Dashboard', url: '/dashboard' },
    { label: 'Daftar Pengajuan Pinjaman', url: '/pengajuan-pinjaman' },
    {
      label: `No. ${this.getFormattedNomorPengajuan()}`,
      active: true,
    },
  ]);

  ngOnInit(): void {
    this.pengajuanId = this.route.snapshot.paramMap.get('id') || '';
    if (isPlatformBrowser(this.platformId) && this.pengajuanId) {
      this.loadDetail();
    }
  }

  loadDetail(): void {
    this.isLoading.set(true);
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
    if (!event) {
      this.selectedReviewStatus.set('');
    } else if (typeof event === 'object' && 'value' in event) {
      this.selectedReviewStatus.set(event.value || '');
    } else {
      this.selectedReviewStatus.set(String(event));
    }
  }

  submitReview(): void {
    const decision = this.selectedReviewStatus();
    const note = this.catatanReview().trim();

    if (!decision) {
      this.toastService.warning('Silakan pilih Status Review terlebih dahulu');
      return;
    }

    if (decision === 'DITOLAK' && !note) {
      this.toastService.warning('Catatan wajib diisi jika menolak pengajuan');
      return;
    }

    const payload: ReviewPengajuanRequest = {
      hasilReview: decision,
      catatan: note || 'Review pengajuan telah diselesaikan oleh Marketing.',
    };

    this.isSubmitting.set(true);

    this.marketingService.review(this.pengajuanId, payload).subscribe({
      next: () => {
        this.isSubmitting.set(false);
        this.toastService.success(
          `Keputusan review berhasil disimpan: ${this.getDecisionLabel(decision)}`
        );
        this.router.navigate(['/pengajuan-pinjaman']);
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
    return d?.namaLengkap || d?.namaNasabah || d?.nama || d?.customer?.nama || '-';
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
    const s = d?.statusPekerjaan || d?.scoringStatusPekerjaan || 'KARYAWAN_TETAP';
    if (s === 'KARYAWAN_TETAP') return 'Karyawan Tetap';
    if (s === 'KARYAWAN_KONTRAK') return 'Karyawan Kontrak';
    if (s === 'WIRASWASTA') return 'Wiraswasta';
    if (s === 'PROFESIONAL') return 'Profesional';
    return s;
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

  getLamaMenjadiNasabah(): number {
    const d = this.detail();
    return d?.lamaJadiNasabahBulan ?? 0;
  }

  getCicilanBerjalan(): number {
    const d = this.detail();
    return d?.cicilanBerjalan ?? 800000;
  }

  getSkor(): number {
    const d = this.detail();
    return d?.skor ?? d?.skorKredit ?? 72;
  }

  getScoreColorClass(): string {
    const s = this.getSkor();
    const status = (this.detail()?.statusScoring || '').toUpperCase();
    if (status === 'REJECTED' || s < 60) return 'text-red-600';
    if (status === 'REVIEW' || (s >= 60 && s < 75)) return 'text-amber-600';
    return 'text-emerald-600';
  }

  getScoreBadgeClass(): string {
    const s = this.getSkor();
    const status = (this.detail()?.statusScoring || '').toUpperCase();
    if (status === 'REJECTED' || s < 60) {
      return 'bg-red-50 text-red-700 border border-red-200';
    }
    if (status === 'REVIEW' || (s >= 60 && s < 75)) {
      return 'bg-amber-50 text-amber-800 border border-amber-200';
    }
    return 'bg-emerald-50 text-emerald-700 border border-emerald-200';
  }

  getPlafonNama(): string {
    const d = this.detail();
    return d?.plafonNama || 'Plafond Tier 1 - Starter';
  }

  getPlafonMaksimal(): number {
    const d = this.detail();
    return (
      d?.plafonMaksimal ??
      d?.estimasiPlafondDisetujui ??
      15000000
    );
  }

  getDbrPercentage(): string {
    const d = this.detail();
    if (d?.dbrPercentage !== undefined && d?.dbrPercentage !== null) {
      return String(d.dbrPercentage).replace('.', ',');
    }
    if (d?.dbr !== undefined && d?.dbr !== null) {
      const num = Number(d.dbr);
      const pct = num < 1 ? num * 100 : num;
      return pct.toFixed(2).replace('.', ',');
    }
    return '17,78';
  }

  isAmbigu(): boolean {
    const d = this.detail();
    if (d?.isAmbigu !== undefined && d?.isAmbigu !== null) {
      return Boolean(d.isAmbigu);
    }
    return !!(d?.notesAmbigu && d.notesAmbigu.length > 0);
  }

  getNotesAmbigu(): string[] {
    const d = this.detail();
    return d?.notesAmbigu || [];
  }

  getRingkasanAnalisis(): string {
    const d = this.detail();
    return (
      d?.ringkasanAnalisis ||
      'Sistem mendeteksi adanya inkonsistensi/faktor ambigu antara skor kredit, pendapatan, atau rasio cicilan. Disarankan memeriksa kelengkapan dokumen dan kemampuan bayar nasabah.'
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
      return d.reviewHistory;
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
    return `${baseHost}/${cleanPath}`;
  }

  getFotoSelfieUrl(): string {
    const d = this.detail();
    return this.getFileUrl(d?.fotoSelfie);
  }

  getDocuments(): DisplayDocItem[] {
    const d = this.detail();
    const list: DisplayDocItem[] = [];

    if (d?.fotoKtp) {
      list.push({
        name: 'KTP_Customer.jpg',
        type: 'KTP',
        url: this.resolveUrl(d.fotoKtp),
        isImage: true,
      });
    }

    if (d?.slipGaji) {
      list.push({
        name: 'Slip_Gaji.pdf',
        type: 'Slip Gaji',
        url: this.resolveUrl(d.slipGaji),
        isImage: false,
      });
    }

    if (d?.rekeningKoran) {
      list.push({
        name: 'Rekening_Koran.pdf',
        type: 'Rekening Koran',
        url: this.resolveUrl(d.rekeningKoran),
        isImage: false,
      });
    }

    if (d?.npwp) {
      list.push({
        name: 'NPWP.pdf',
        type: 'NPWP',
        url: this.resolveUrl(d.npwp),
        isImage: false,
      });
    }

    if (Array.isArray(d?.dokumenPinjamanList)) {
      d.dokumenPinjamanList.forEach((item: DokumenPinjamanItem) => {
        if (item.fileUrl) {
          const docName = this.formatDocTypeName(item.docType);
          const resolved = this.resolveUrl(item.fileUrl);
          if (!list.some((existing) => existing.url === resolved)) {
            list.push({
              id: item.id,
              name: docName,
              type: item.docType || 'Dokumen',
              url: resolved,
              isImage: !!item.fileUrl && (item.fileUrl.endsWith('.jpg') || item.fileUrl.endsWith('.png') || item.fileUrl.endsWith('.jpeg')),
            });
          }
        }
      });
    }

    if (list.length === 0) {
      list.push(
        { name: 'KTP_Customer.jpg', type: 'KTP', url: '', isImage: true },
        { name: 'Slip_Gaji_Bulan_Lalu.pdf', type: 'Slip Gaji', url: '', isImage: false },
        { name: 'Rekening_Koran.pdf', type: 'Rekening Koran', url: '', isImage: false }
      );
    }

    return list;
  }

  private resolveUrl(path?: string): string {
    return this.getFileUrl(path);
  }

  private formatDocTypeName(type?: string): string {
    if (!type) return 'Dokumen_Pendukung.pdf';
    switch (type.toUpperCase()) {
      case 'SLIP_GAJI':
        return 'Slip_Gaji.pdf';
      case 'REKENING_KORAN':
        return 'Rekening_Koran.pdf';
      case 'NPWP':
        return 'NPWP.pdf';
      case 'KTP':
        return 'KTP_Customer.jpg';
      case 'KK':
        return 'Kartu_Keluarga.jpg';
      default:
        return `${type.toLowerCase()}.pdf`;
    }
  }

  formatCurrency(val: number | null | undefined): string {
    if (val === null || val === undefined || isNaN(val)) return 'Rp 0';
    return 'Rp ' + new Intl.NumberFormat('id-ID').format(val);
  }

  formatDate(dateStr?: string | null): string {
    if (!dateStr) return '-';
    try {
      const d = new Date(dateStr);
      if (isNaN(d.getTime())) return dateStr;
      return d.toLocaleDateString('id-ID', {
        day: '2-digit',
        month: 'short',
        hour: '2-digit',
        minute: '2-digit',
      });
    } catch {
      return dateStr;
    }
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
      return 'bg-emerald-50 border border-emerald-200 text-emerald-700';
    }
    if (label === 'Ditolak Marketing') {
      return 'bg-red-50 border border-red-200 text-red-700';
    }
    if (label === 'Perlu Revisi') {
      return 'bg-amber-50 border border-amber-200 text-amber-700';
    }
    return 'bg-[#FEF9C3] border border-[#FDE047] text-[#854D0E]';
  }
}
