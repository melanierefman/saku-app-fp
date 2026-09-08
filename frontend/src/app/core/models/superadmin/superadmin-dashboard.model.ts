export interface BranchPerformanceItem {
  branchId: string;
  branchName: string;
  kota: string;
  totalPengajuan: number;
  totalNominal: number;
  totalDisetujui: number;
}

export interface MonthlyLoanTrendItem {
  month: string;
  totalPengajuan: number;
  totalNominalDiajukan: number;
  totalNominalDicairkan: number;
}

export interface RecentPengajuanItem {
  id: string;
  nomorPengajuan: string;
  namaCustomer: string;
  namaCabang: string;
  jumlahPinjaman: number;
  tenorBulan: number;
  statusPengajuan: string;
  statusPengajuanLabel?: string;
  statusLabel?: string;
  tanggalPengajuan: string;
}

export interface RecentAuditLogItem {
  id: string;
  username: string;
  role: string;
  action: string;
  entity: string;
  deskripsi: string;
  timestamp: string;
}

export interface SuperadminDashboardStats {
  // 1. KPI Cards Nasional
  totalPengajuan: number;
  totalNominalDiajukan: number;
  totalNominalDicairkan: number;
  totalCustomer: number;
  totalCabang: number;
  totalKaryawan: number;
  approvalRateNasional: number;

  // 2. Pipeline Status Funnel
  menungguReviewMarketing: number;
  menungguPersetujuanBM: number;
  menungguPencairan: number;
  telahDicairkan: number;
  ditolak: number;

  // 3. Visual Charts & Analytics
  statusDistribution: Record<string, number>;
  branchPerformance: BranchPerformanceItem[];
  monthlyTrends: MonthlyLoanTrendItem[];

  // 4. Ringkasan Aktivitas Terbaru
  recentPengajuan: RecentPengajuanItem[];
  recentAuditLogs: RecentAuditLogItem[];
}
