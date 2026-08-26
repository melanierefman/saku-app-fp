package com.bcafinance.backend_saku.features.marketing.dto;

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
public class MarketingDashboardStatsResponse {

    // KPI Cards
    private long totalPengajuan;
    private long menungguReview;
    private long perluRevisi;
    private long disetujuiMarketing;
    private long ditolakMarketing;
    private double approvalRate;

    // Chart Data
    private Map<String, Long> scoringDistribution;
    private Map<String, Long> statusDistribution;
    private List<DailyTrendItem> weeklyTrends;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class DailyTrendItem {
        private String date;
        private String day;
        private long count;
        private BigDecimal totalNominal;
    }
}
