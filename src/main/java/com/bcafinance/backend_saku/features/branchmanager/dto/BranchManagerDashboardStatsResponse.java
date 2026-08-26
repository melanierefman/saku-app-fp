package com.bcafinance.backend_saku.features.branchmanager.dto;

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
    private long totalPengajuanCabang;
    private long menungguPersetujuan;
    private long disetujuiBM;
    private long ditolakBM;
    private BigDecimal totalNominalDiajukan;
    private BigDecimal totalNominalDisetujui;
    private double approvalRateBM;
    private BigDecimal rataRataNominalPinjaman;

    // Charts
    private Map<String, Long> tenorDistribution;
    private Map<String, Long> statusPersetujuanDistribution;
    private List<MonthlyTrendItem> monthlyApprovalTrends;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class MonthlyTrendItem {
        private String month;
        private long countDisetujui;
        private BigDecimal totalNominalDisetujui;
    }
}
