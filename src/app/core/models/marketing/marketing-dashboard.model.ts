export interface DailyTrendItem {
  date: string;         // Format: "YYYY-MM-DD"
  day: string;          // Format: "Sen", "Sel", "Rab", dll.
  count: number;        // Jumlah pengajuan pada hari tersebut
  totalNominal: number; // Total nominal pinjaman pada hari tersebut (Rp)
}

export interface MarketingDashboardStats {
  // 1. KPI Cards
  totalPengajuan: number;
  menungguReview: number;
  perluRevisi: number;
  disetujuiMarketing: number;
  ditolakMarketing: number;
  approvalRate: number; // Persentase kelolosan (contoh: 87.5)

  // 2. Charts & Analytics
  scoringDistribution: Record<string, number>; // "SKOR_TINGGI (>= 75)", "SKOR_SEDANG (60 - 74)", "SKOR_RENDAH (< 60)"
  statusDistribution: Record<string, number>;  // "MENUNGGU_REVIEW", "PERLU_REVISI", "SELESAI_DIREVIEW", "PENGAJUAN_DITOLAK", "DICAIRKAN"
  weeklyTrends: DailyTrendItem[];              // 7 hari terakhir
}
