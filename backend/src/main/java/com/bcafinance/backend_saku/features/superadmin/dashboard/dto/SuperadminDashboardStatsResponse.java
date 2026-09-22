package com.bcafinance.backend_saku.features.superadmin.dashboard.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SuperadminDashboardStatsResponse {

    // 1. Executive Summary Cards
    @Schema(description = "Total seluruh pengajuan pinjaman", example = "125")
    private long totalPengajuan;

    @Schema(description = "Total nominal pinjaman yang diajukan (Rp)", example = "2500000000")
    private BigDecimal totalNominalDiajukan;

    @Schema(description = "Total nominal pinjaman yang telah dicairkan (Rp)", example = "1850000000")
    private BigDecimal totalNominalDicairkan;

    @Schema(description = "Total nasabah terdaftar", example = "84")
    private long totalCustomer;

    @Schema(description = "Total cabang operasional", example = "12")
    private long totalCabang;

    @Schema(description = "Total karyawan aktif", example = "45")
    private long totalKaryawan;

    @Schema(description = "Persentase approval rate nasional (%)", example = "85.5")
    private double approvalRateNasional;

    // 2. Status Pipeline Funnel
    @Schema(description = "Pengajuan menunggu review marketing", example = "8")
    private long menungguReviewMarketing;

    @Schema(description = "Pengajuan menunggu persetujuan Branch Manager", example = "4")
    private long menungguPersetujuanBM;

    @Schema(description = "Pengajuan disetujui menunggu pencairan", example = "6")
    private long menungguPencairan;

    @Schema(description = "Pengajuan yang telah dicairkan", example = "95")
    private long telahDicairkan;

    @Schema(description = "Pengajuan yang ditolak", example = "12")
    private long ditolak;

    // 3. Distribusi & Visual Data
    @Schema(description = "Distribusi jumlah pengajuan berdasarkan status")
    private Map<String, Long> statusDistribution;

    @Schema(description = "Daftar performa kantor cabang")
    private List<BranchPerformanceItem> branchPerformance;

    @Schema(description = "Tren pinjaman bulanan")
    private List<MonthlyLoanTrendItem> monthlyTrends;

    // 4. Quick Recent Summaries
    @Schema(description = "Daftar pengajuan terbaru")
    private List<RecentPengajuanItem> recentPengajuan;

    @Schema(description = "Daftar aktivitas audit log terbaru")
    private List<RecentAuditLogItem> recentAuditLogs;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class BranchPerformanceItem {
        @Schema(description = "ID Cabang", example = "cb123e45-6789-4bc5-a123-456789abcdef")
        private String branchId;

        @Schema(description = "Nama Cabang", example = "Cabang Jakarta Pusat")
        private String branchName;

        @Schema(description = "Kota", example = "Jakarta Pusat")
        private String kota;

        @Schema(description = "Total pengajuan", example = "35")
        private long totalPengajuan;

        @Schema(description = "Total nominal pengajuan", example = "750000000")
        private BigDecimal totalNominal;

        @Schema(description = "Total pengajuan disetujui", example = "30")
        private long totalDisetujui;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class MonthlyLoanTrendItem {
        @Schema(description = "Bulan", example = "September 2026")
        private String month;

        @Schema(description = "Total pengajuan pada bulan tersebut", example = "42")
        private long totalPengajuan;

        @Schema(description = "Total nominal diajukan", example = "850000000")
        private BigDecimal totalNominalDiajukan;

        @Schema(description = "Total nominal dicairkan", example = "650000000")
        private BigDecimal totalNominalDicairkan;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class RecentPengajuanItem {
        @Schema(description = "ID Pengajuan", example = "pj123e45-6789-4bc5-a123-456789abcdef")
        private String id;

        @Schema(description = "Nomor Pengajuan", example = "PJ-202609-0015")
        private String nomorPengajuan;

        @Schema(description = "Nama Nasabah", example = "Budi Santoso")
        private String namaCustomer;

        @Schema(description = "Nama Cabang", example = "Cabang Jakarta Pusat")
        private String namaCabang;

        @Schema(description = "Jumlah Pinjaman (Rp)", example = "15000000")
        private BigDecimal jumlahPinjaman;

        @Schema(description = "Tenor Pinjaman (Bulan)", example = "12")
        private Integer tenorBulan;

        @Schema(description = "Status Pengajuan", example = "MENUNGGU_REVIEW_MARKETING")
        private String statusPengajuan;

        @Schema(description = "Label Status Pengajuan", example = "Menunggu Review Marketing")
        private String statusPengajuanLabel;

        @Schema(description = "Tanggal Pengajuan", example = "2026-09-22T08:30:00")
        private String tanggalPengajuan;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class RecentAuditLogItem {
        @Schema(description = "ID Audit Log", example = "al123e45-6789-4bc5-a123-456789abcdef")
        private String id;

        @Schema(description = "Username pengguna", example = "admin.jkt")
        private String username;

        @Schema(description = "Role pengguna", example = "SUPERADMIN")
        private String role;

        @Schema(description = "Aksi yang dilakukan", example = "CREATE")
        private String action;

        @Schema(description = "Entitas data", example = "CABANG")
        private String entity;

        @Schema(description = "Deskripsi aktivitas", example = "Menambahkan cabang baru: Cabang Jakarta Pusat")
        private String deskripsi;

        @Schema(description = "Waktu aktivitas", example = "2026-09-22T09:15:20")
        private String timestamp;
    }
}
