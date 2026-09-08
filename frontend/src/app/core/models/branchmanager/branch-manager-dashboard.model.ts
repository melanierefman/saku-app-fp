export interface MonthlyTrendItem {
  month: string;                    // Format: "Aug 2026", "Sep 2026", dll.
  countDisetujui: number;           // Jumlah pengajuan yang disetujui BM pada bulan tersebut
  totalNominalDisetujui: number;    // Total nominal pinjaman yang disetujui (Rp)
}

export interface BranchManagerDashboardStats {
  // 1. KPI Cards Cabang
  totalPengajuanCabang: number;     // Total pengajuan yang masuk ke cabang
  menungguPersetujuan: number;      // Berkas menunggu keputusan BM (Actionable)
  disetujuiBM: number;              // Total disetujui BM
  ditolakBM: number;                // Total ditolak BM
  totalNominalDiajukan: number;     // Total nominal pinjaman diajukan (Rp)
  totalNominalDisetujui: number;    // Total nominal pinjaman disetujui (Rp)
  approvalRateBM: number;           // Persentase persetujuan BM (contoh: 91.5)
  rataRataNominalPinjaman: number;  // Rata-rata nominal pinjaman per customer (Rp)

  // 2. Charts & Analytics
  tenorDistribution: Record<string, number>;             // Contoh: {"12 Bulan": 24, "24 Bulan": 15, "36 Bulan": 8}
  statusPersetujuanDistribution: Record<string, number>; // Contoh: {"MENUNGGU_PERSETUJUAN": 5, "DISETUJUI": 42, "DITOLAK": 3}
  monthlyApprovalTrends: MonthlyTrendItem[];             // Tren persetujuan 6 bulan terakhir
}
