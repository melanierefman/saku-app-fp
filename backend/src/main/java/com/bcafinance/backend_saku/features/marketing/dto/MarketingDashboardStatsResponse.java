package com.bcafinance.backend_saku.features.marketing.dto;

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
public class MarketingDashboardStatsResponse {

    // KPI Cards
    @Schema(description = "Total seluruh pengajuan masuk", example = "48")
    private long totalPengajuan;

    @Schema(description = "Jumlah pengajuan menunggu review", example = "8")
    private long menungguReview;

    @Schema(description = "Jumlah pengajuan yang meminta perbaikan berkas/revisi", example = "3")
    private long perluRevisi;

    @Schema(description = "Jumlah pengajuan yang direkomendasikan/disetujui marketing", example = "32")
    private long disetujuiMarketing;

    @Schema(description = "Jumlah pengajuan yang ditolak marketing", example = "5")
    private long ditolakMarketing;

    @Schema(description = "Persentase approval rate marketing (%)", example = "86.5")
    private double approvalRate;

    // Chart Data
    @Schema(description = "Distribusi pengajuan berdasarkan hasil credit scoring")
    private Map<String, Long> scoringDistribution;

    @Schema(description = "Distribusi pengajuan berdasarkan status")
    private Map<String, Long> statusDistribution;

    @Schema(description = "Tren harian pengajuan dalam seminggu terakhir")
    private List<DailyTrendItem> weeklyTrends;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class DailyTrendItem {
        @Schema(description = "Tanggal", example = "2026-09-22")
        private String date;

        @Schema(description = "Nama Hari", example = "Selasa")
        private String day;

        @Schema(description = "Jumlah pengajuan masuk", example = "7")
        private long count;

        @Schema(description = "Total nominal diajukan", example = "105000000")
        private BigDecimal totalNominal;
    }
}
