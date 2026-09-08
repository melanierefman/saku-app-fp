export interface DailyDisbursementItem {
  date: string;         // Format: "YYYY-MM-DD"
  day: string;          // Format: "Sen", "Sel", "Rab", dll.
  count: number;        // Jumlah transaksi pencairan pada hari tersebut
  totalNominal: number; // Total nominal dana yang dicairkan pada hari tersebut (Rp)
}

export interface BackofficeDashboardStats {
  // 1. KPI Cards Operasional & Finansial
  menungguVerifikasiKyc: number;   // Customer registrasi baru yang butuh verifikasi KYC (Actionable)
  siapDicairkan: number;           // Pinjaman disetujui BM yang siap ditransfer (Actionable)
  totalTransaksiPencairan: number; // Total transaksi pencairan yang telah sukses
  totalNominalDicairkan: number;   // Total dana pinjaman yang sudah dicairkan (Rp)
  totalBiayaAdmin: number;         // Total pendapatan biaya admin yang terkumpul (Rp)
  totalAngsuranAktif: number;      // Total jadwal cicilan aktif di sistem

  // 2. Charts & Analytics
  bankDistribution: Record<string, number>; // Sebaran bank rekening customer, contoh: {"BCA": 45, "MANDIRI": 12, "BRI": 8}
  kycDistribution: Record<string, number>;  // Contoh: {"TERVERIFIKASI": 120, "MENUNGGU_VERIFIKASI": 14}
  weeklyDisbursements: DailyDisbursementItem[]; // Tren pencairan 7 hari terakhir
}
