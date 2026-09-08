package com.bcafinance.backend_saku.features.backoffice.dto;

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
public class BackofficeDashboardStatsResponse {

    // KPI Cards
    private long menungguVerifikasiKyc;
    private long siapDicairkan;
    private long totalTransaksiPencairan;
    private BigDecimal totalNominalDicairkan;
    private BigDecimal totalBiayaAdmin;
    private long totalAngsuranAktif;

    // Charts
    private Map<String, Long> bankDistribution;
    private Map<String, Long> kycDistribution;
    private List<DailyDisbursementItem> weeklyDisbursements;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class DailyDisbursementItem {
        private String date;
        private String day;
        private long count;
        private BigDecimal totalNominal;
    }
}
