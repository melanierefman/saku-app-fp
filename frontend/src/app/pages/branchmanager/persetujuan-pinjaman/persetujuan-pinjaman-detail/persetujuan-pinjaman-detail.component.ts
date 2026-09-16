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
  SkeletonComponent,
} from '../../../../shared/components';
import {
  BranchManagerApprovalService,
  BranchManagerPengajuanDetailResponse,
  PersetujuanPinjamanRequest,
  DokumenPinjamanItem,
  ReviewMarketingHistoryItem,
  PersetujuanHistoryItem,
} from '../../../../core';
import {
  LucideFileText,
  LucideExternalLink,
  LucideUser,
  LucideBanknote,
  LucideSend,
  LucideAlertCircle,
  LucideFileCheck,
  LucideHistory,
  LucideShieldCheck,
  LucideMessageSquare,
  LucideInfo,
  LucideBriefcase,
  LucideArrowLeft,
} from '@lucide/angular';
import { environment } from '../../../../../environments/environment';
import { formatDate as formatDateHelper } from '../../../../shared/utils/date.util';

@Component({
  selector: 'app-persetujuan-pinjaman-detail',
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
    LucideSend,
    LucideAlertCircle,
    LucideFileCheck,
    LucideHistory,
    LucideShieldCheck,
    LucideMessageSquare,
    LucideInfo,
    LucideBriefcase,
    LucideArrowLeft,
  ],
  templateUrl: './persetujuan-pinjaman-detail.component.html',
  styleUrl: './persetujuan-pinjaman-detail.component.css',
})
export class PersetujuanPinjamanDetailComponent implements OnInit {
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private bmService = inject(BranchManagerApprovalService);
  private toastService = inject(ToastService);
  private cdr = inject(ChangeDetectorRef);
  private platformId = inject(PLATFORM_ID);

  goBack(): void {
    this.router.navigate(['/persetujuan-pinjaman']);
  }

  // Signals
  detail = signal<BranchManagerPengajuanDetailResponse | null>(null);
  isLoading = signal<boolean>(true);
  isSubmitting = signal<boolean>(false);
  isConfirmModalOpen = signal<boolean>(false);
  pengajuanId = signal<string>('');
  photoError = signal<boolean>(false);

  // Form Signals
  selectedKeputusan = signal<string>('');
  selectedTierId = signal<string>('');
  selectedKategoriAlasan = signal<string>('');
  catatanPersetujuan = signal<string>('');

  readonly keputusanOptions: DropdownOption[] = [
    { value: 'DISETUJUI', label: 'Setujui Pengajuan' },
    { value: 'DITOLAK', label: 'Tolak Pengajuan' },
  ];

  readonly presetReasons: Record<string, DropdownOption[]> = {
    DISETUJUI: [
      {
        value: 'Data keuangan, slip gaji, dan credit scoring memenuhi seluruh kriteria kelayakan pembiayaan.',
        label: 'Kriteria Kelayakan Terpenuhi Lengkap',
      },
      {
        value: 'Disetujui sesuai dengan tier, limit plafon, dan suku bunga yang telah ditetapkan sistem.',
        label: 'Sesuai Rekomendasi & Tier Sistem',
      },
      {
        value: 'Kapasitas bayar (DBR) sehat dan stabilitas kepegawaian terverifikasi dengan baik.',
        label: 'Kapasitas Bayar & DBR Sehat',
      },
      {
        value: 'Dokumen identitas, rekening koran, dan data keuangan valid tanpa catatan anomali.',
        label: 'Dokumen & Mutasi Valid',
      },
      { value: 'LAINNYA', label: 'Lainnya (Tulis catatan khusus)...' },
    ],
    DITOLAK: [
      {
        value: 'Rasio beban hutang (DBR) terlalu tinggi, risiko gagal bayar tergolong tinggi.',
        label: 'Rasio Beban Hutang Terlalu Tinggi',
      },
      {
        value: 'Kapasitas penghasilan tidak mencukupi untuk pembayaran angsuran bulanan yang diajukan.',
        label: 'Kapasitas Penghasilan Tidak Mencukupi',
      },
      {
        value: 'Dokumen identitas / bukti penghasilan tidak valid atau gagal verifikasi keaslian.',
        label: 'Dokumen Gagal Verifikasi / Tidak Valid',
      },
      {
        value: 'Status pekerjaan dan riwayat kredit belum memenuhi kriteria minimal pembiayaan.',
        label: 'Profil Pekerjaan & Kredit Tidak Memenuhi Syarat',
      },
      { value: 'LAINNYA', label: 'Lainnya (Tulis catatan khusus)...' },
    ],
  };

  get tierOptions(): DropdownOption[] {
    const tiers = this.detail()?.availablePlafondTiers || [];
    return tiers.map((t) => ({
      value: t.id || '',
      label: `${t.nama || 'Tier'} (Bunga ${t.bunga || 5}% / Admin ${this.formatCurrency(t.biayaAdmin || 250000)})`,
    }));
  }

  get kategoriAlasanOptions(): DropdownOption[] {
    const k = this.selectedKeputusan();
    return this.presetReasons[k] || [];
  }

  ngOnInit(): void {
    if (isPlatformBrowser(this.platformId)) {
      const id = this.route.snapshot.paramMap.get('id');
      if (id) {
        this.pengajuanId.set(id);
        this.loadDetail(id);
      } else {
        this.toastService.error('ID Pengajuan Pinjaman tidak valid');
        this.router.navigate(['/persetujuan-pinjaman']);
      }
    }
  }

  loadDetail(id: string): void {
    this.isLoading.set(true);
    this.bmService.getDetail(id).subscribe({
      next: (res) => {
        this.detail.set(res);
        this.isLoading.set(false);

        // Pre-fill existing approval if already reviewed
        const existingDecision = res?.hasilPersetujuanBM || '';
        if (existingDecision) {
          this.selectedKeputusan.set(existingDecision.toUpperCase());
        }
        if (res?.catatanBM) {
          this.catatanPersetujuan.set(res.catatanBM);
        }
      },
      error: (err) => {
        this.isLoading.set(false);
        this.toastService.error('Gagal memuat detail pengajuan pinjaman');
        console.error('Error loading BM pengajuan detail:', err);
      },
    });
  }

  onKeputusanChange(event: DropdownOption | null | string): void {
    let val = '';
    if (event && typeof event === 'object' && 'value' in event) {
      val = event.value || '';
    } else if (event) {
      val = String(event);
    }

    this.selectedKeputusan.set(val);
    this.selectedKategoriAlasan.set('');

    // Pre-populate default preset if available
    const presets = this.presetReasons[val];
    if (presets && presets.length > 0 && presets[0].value !== 'LAINNYA') {
      this.selectedKategoriAlasan.set(presets[0].value || '');
      this.catatanPersetujuan.set(presets[0].value || '');
    } else {
      this.catatanPersetujuan.set('');
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
      this.catatanPersetujuan.set('');
    } else if (val) {
      this.catatanPersetujuan.set(val);
    }
  }

  openConfirmModal(): void {
    const keputusan = this.selectedKeputusan();
    if (!keputusan) {
      this.toastService.warning('Silakan pilih Keputusan Persetujuan terlebih dahulu');
      return;
    }
    if (!this.catatanPersetujuan().trim()) {
      this.toastService.warning('Catatan pertimbangan persetujuan / penolakan wajib diisi');
      return;
    }
    this.isConfirmModalOpen.set(true);
  }

  closeConfirmModal(): void {
    this.isConfirmModalOpen.set(false);
  }

  confirmSubmitPersetujuan(): void {
    const id = this.pengajuanId();
    const keputusan = this.selectedKeputusan();

    const payload: PersetujuanPinjamanRequest = {
      hasilPersetujuan: keputusan === 'DITOLAK' ? 'DITOLAK' : 'DISETUJUI',
      catatan: this.catatanPersetujuan().trim() || undefined,
      kategoriAlasan: this.selectedKategoriAlasan() || undefined,
    };

    this.isSubmitting.set(true);
    this.bmService.persetujuan(id, payload).subscribe({
      next: () => {
        this.isSubmitting.set(false);
        this.isConfirmModalOpen.set(false);
        this.toastService.success('Keputusan persetujuan pinjaman berhasil disimpan');
        this.loadDetail(id);
      },
      error: (err) => {
        console.error('Failed to submit BM persetujuan:', err);
        this.isSubmitting.set(false);
        const errMsg =
          err?.error?.message ||
          err?.message ||
          'Terjadi kesalahan saat memproses keputusan';
        this.toastService.error(errMsg);
        this.cdr.detectChanges();
      },
    });
  }

  // Document URL Opener
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

  getCustomerName(): string {
    const d = this.detail();
    return d?.namaLengkap || d?.customer || 'Customer';
  }

  getFotoSelfieUrl(): string | null {
    const d = this.detail();
    const path = d?.fotoSelfie;
    if (!path) return null;
    if (path.startsWith('http://') || path.startsWith('https://')) return path;
    const cleanPath = path.replace(/^\/+/, '');
    const baseUrl = environment.apiUrl.replace(/\/api\/?$/, '');
    return `${baseUrl}/uploads/${cleanPath}`;
  }

  onPhotoError(): void {
    this.photoError.set(true);
  }

  getDokumenList(): { type: string; label: string; fileUrl?: string }[] {
    const d = this.detail();
    if (!d) return [];

    const list: { type: string; label: string; fileUrl?: string }[] = [];

    if (d.fotoKtp) list.push({ type: 'KTP', label: 'Foto KTP', fileUrl: d.fotoKtp });
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
        return 'Foto KTP';
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

  getStatusPekerjaanLabel(): string {
    const d = this.detail();
    const s = d?.statusPekerjaan || d?.statusPekerjaanScoring || 'KARYAWAN_TETAP';
    switch (s.toUpperCase()) {
      case 'KARYAWAN_TETAP':
        return 'Karyawan Tetap';
      case 'KARYAWAN_KONTRAK':
        return 'Karyawan Kontrak';
      case 'WIRASWASTA':
        return 'Wiraswasta';
      case 'PROFESIONAL':
        return 'Profesional';
      default:
        return s;
    }
  }

  getSkor(): number {
    return this.detail()?.skor ?? this.detail()?.skorKredit ?? 0;
  }

  getPlafonMaksimal(): number {
    return this.detail()?.plafonMaksimal ?? 0;
  }

  getPlafonNama(): string {
    return this.detail()?.plafonNama || 'Tier Standar';
  }

  getDbrPercentage(): number {
    const d = this.detail();
    const val = d?.dbrPercentage ?? d?.dbr;
    if (val !== undefined && val !== null && !isNaN(Number(val)) && Number(val) > 0) {
      const num = Number(val);
      return num <= 1 ? parseFloat((num * 100).toFixed(1)) : parseFloat(num.toFixed(1));
    }
    const cicilan = this.getCicilanBerjalan();
    const pendapatan = this.getPendapatanBulanan();
    if (pendapatan > 0 && cicilan > 0) {
      return parseFloat(((cicilan / pendapatan) * 100).toFixed(1));
    }
    return 0;
  }

  getDbrColorClass(): string {
    const dbr = this.getDbrPercentage();
    if (dbr <= 30) return 'text-success-60';
    if (dbr <= 40) return 'text-warning-60';
    return 'text-error-60';
  }

  getCicilanBerjalan(): number {
    return this.detail()?.cicilanBerjalan ?? 0;
  }

  getPendapatanBulanan(): number {
    const d = this.detail();
    return d?.penghasilanBulananScoring ?? d?.penghasilanBulanan ?? d?.pendapatan ?? 0;
  }

  getLamaBekerja(): number {
    return this.detail()?.lamaBekerjaBulan ?? 0;
  }

  getKeputusanSistemLabel(): string {
    const d = this.detail();
    const skor = this.getSkor();
    const status = (d?.statusScoring || '').toUpperCase();
    if (d?.keputusanSistem) return d.keputusanSistem;
    if (status === 'APPROVED' || skor >= 75) return 'LAYAK (APPROVED)';
    if (status === 'REVIEW' || (skor >= 60 && skor < 75)) return 'PERLU REVIEW (REVIEW)';
    return 'TIDAK LAYAK (REJECTED)';
  }

  getRingkasanAnalisis(): string {
    const d = this.detail();
    return d?.ringkasanScoring || d?.ringkasanAnalisis || '';
  }

  isAmbigu(): boolean {
    const d = this.detail();
    return !!d?.isAmbigu || (Array.isArray(d?.notesAmbigu) && d.notesAmbigu.length > 0);
  }

  getNotesAmbigu(): string[] {
    return this.detail()?.notesAmbigu || [];
  }

  getScoreColorClass(): string {
    const s = this.getSkor();
    const status = (this.detail()?.statusScoring || '').toUpperCase();
    if (status === 'REJECTED' || s < 60) return 'text-error-60';
    if (status === 'REVIEW' || (s >= 60 && s < 75)) return 'text-warning-60';
    return 'text-success-60';
  }

  getScoreBadgeClass(): string {
    const s = this.detail()?.skor ?? this.detail()?.skorKredit ?? 0;
    const status = (this.detail()?.statusScoring || '').toUpperCase();
    if (status === 'REJECTED' || s < 60) {
      return 'bg-error-0 text-error-70 border border-error-20';
    }
    if (status === 'REVIEW' || (s >= 60 && s < 75)) {
      return 'bg-warning-0 text-warning-80 border border-warning-20';
    }
    return 'bg-success-0 text-success-70 border border-success-20';
  }

  isPending(): boolean {
    const d = this.detail();
    const h = (d?.hasilPersetujuanBM || '').toUpperCase();
    const s = (d?.statusPengajuan || d?.status || '').toUpperCase();
    if (h === 'DISETUJUI' || h === 'DITOLAK' || s === 'DISETUJUI' || s === 'DITOLAK' || s === 'DICAIRKAN' || s === 'DISBURSED' || s === 'LUNAS') {
      return false;
    }
    return true;
  }

  getStatusDisplayLabel(): string {
    const d = this.detail();
    const h = (d?.hasilPersetujuanBM || '').toUpperCase();
    const s = (d?.statusPengajuan || d?.status || '').toUpperCase();

    if (h === 'DISETUJUI' || s === 'DISETUJUI' || s === 'DICAIRKAN' || s === 'DISBURSED') {
      return 'Disetujui BM';
    }
    if (h === 'DITOLAK' || s === 'DITOLAK') {
      return 'Ditolak BM';
    }
    return 'Menunggu Persetujuan BM';
  }

  getStatusPillClass(): string {
    const label = this.getStatusDisplayLabel();
    if (label === 'Disetujui BM') {
      return 'bg-success-0 border border-success-20 text-success-70';
    }
    if (label === 'Ditolak BM') {
      return 'bg-error-0 border border-error-20 text-error-70';
    }
    return 'bg-warning-0 border border-warning-20 text-warning-80';
  }

  getCatatanBM(): string {
    const d = this.detail();
    if (d?.catatanBM) return d.catatanBM;
    if (d?.persetujuanHistory && d.persetujuanHistory.length > 0) {
      return d.persetujuanHistory[0].catatan || '';
    }
    return '';
  }

  getNamaApprover(): string {
    const d = this.detail();
    if (d?.persetujuanHistory && d.persetujuanHistory.length > 0) {
      return d.persetujuanHistory[0].namaApprover || 'Branch Manager';
    }
    return 'Branch Manager';
  }

  getTanggalPersetujuanBM(): string {
    const d = this.detail();
    return d?.tanggalPersetujuanBM || (d?.persetujuanHistory && d.persetujuanHistory[0]?.tanggalPersetujuan) || '';
  }

  getReviewMarketingHistory(): ReviewMarketingHistoryItem[] {
    return this.detail()?.reviewMarketingHistory || [];
  }

  getPersetujuanHistory(): PersetujuanHistoryItem[] {
    return this.detail()?.persetujuanHistory || [];
  }

  formatCurrency(val: number | null | undefined): string {
    if (val === null || val === undefined || isNaN(val)) return 'Rp 0';
    return 'Rp ' + new Intl.NumberFormat('id-ID').format(val);
  }

  formatDate(dateStr?: string | null): string {
    return formatDateHelper(dateStr);
  }

  formatPercent(val?: number | null): string {
    if (val === null || val === undefined) return '0%';
    const p = val <= 1 ? val * 100 : val;
    return `${p.toFixed(1)}%`;
  }
}
