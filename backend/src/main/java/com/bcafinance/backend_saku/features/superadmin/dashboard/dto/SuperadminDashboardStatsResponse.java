package com.bcafinance.backend_saku.features.superadmin.dashboard.dto;

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
    private long totalPengajuan;
    private BigDecimal totalNominalDiajukan;
    private BigDecimal totalNominalDicairkan;
    private long totalCustomer;
    private long totalCabang;
    private long totalKaryawan;
    private double approvalRateNasional;

    // 2. Status Pipeline Funnel
    private long menungguReviewMarketing;
    private long menungguPersetujuanBM;
    private long menungguPencairan;
    private long telahDicairkan;
    private long ditolak;

    // 3. Distribusi & Visual Data
    private Map<String, Long> statusDistribution;
    private List<BranchPerformanceItem> branchPerformance;
    private List<MonthlyLoanTrendItem> monthlyTrends;

    // 4. Quick Recent Summaries
    private List<RecentPengajuanItem> recentPengajuan;
    private List<RecentAuditLogItem> recentAuditLogs;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class BranchPerformanceItem {
        private String branchId;
        private String branchName;
        private String kota;
        private long totalPengajuan;
        private BigDecimal totalNominal;
        private long totalDisetujui;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class MonthlyLoanTrendItem {
        private String month;
        private long totalPengajuan;
        private BigDecimal totalNominalDiajukan;
        private BigDecimal totalNominalDicairkan;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class RecentPengajuanItem {
        private String id;
        private String nomorPengajuan;
        private String namaCustomer;
        private String namaCabang;
        private BigDecimal jumlahPinjaman;
        private Integer tenorBulan;
        private String statusPengajuan;
        private String statusPengajuanLabel;
        private String tanggalPengajuan;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class RecentAuditLogItem {
        private String id;
        private String username;
        private String role;
        private String action;
        private String entity;
        private String deskripsi;
        private String timestamp;
    }
}
