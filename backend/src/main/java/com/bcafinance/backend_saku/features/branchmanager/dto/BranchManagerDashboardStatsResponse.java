package com.bcafinance.backend_saku.features.branchmanager.dto;

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
public class BranchManagerDashboardStatsResponse {

    // KPI Cards
    @Schema(description = "Total seluruh pengajuan pada cabang ini", example = "35")
    private long totalPengajuanCabang;

    @Schema(description = "Jumlah pengajuan menunggu persetujuan Branch Manager", example = "4")
    private long menungguPersetujuan;

    @Schema(description = "Jumlah pengajuan yang disetujui Branch Manager", example = "28")
    private long disetujuiBM;

    @Schema(description = "Jumlah pengajuan yang ditolak Branch Manager", example = "3")
    private long ditolakBM;

    @Schema(description = "Total nominal diajukan (Rp)", example = "750000000")
    private BigDecimal totalNominalDiajukan;

    @Schema(description = "Total nominal disetujui (Rp)", example = "620000000")
    private BigDecimal totalNominalDisetujui;

    @Schema(description = "Persentase approval rate Branch Manager (%)", example = "90.3")
    private double approvalRateBM;

    @Schema(description = "Rata-rata nominal per pinjaman (Rp)", example = "21400000")
    private BigDecimal rataRataNominalPinjaman;

    // Charts
    @Schema(description = "Distribusi pengajuan berdasarkan tenor pinjaman")
    private Map<String, Long> tenorDistribution;

    @Schema(description = "Distribusi pengajuan berdasarkan status persetujuan")
    private Map<String, Long> statusPersetujuanDistribution;

    @Schema(description = "Tren persetujuan pinjaman bulanan")
    private List<MonthlyTrendItem> monthlyApprovalTrends;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class MonthlyTrendItem {
        @Schema(description = "Bulan", example = "September 2026")
        private String month;

        @Schema(description = "Jumlah pengajuan disetujui", example = "18")
        private long countDisetujui;

        @Schema(description = "Total nominal disetujui", example = "380000000")
        private BigDecimal totalNominalDisetujui;
    }
}
