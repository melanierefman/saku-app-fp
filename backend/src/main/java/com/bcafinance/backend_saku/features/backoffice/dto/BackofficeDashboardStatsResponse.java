package com.bcafinance.backend_saku.features.backoffice.dto;

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
public class BackofficeDashboardStatsResponse {

    // KPI Cards
    @Schema(description = "Jumlah nasabah menunggu verifikasi dokumen KYC", example = "7")
    private long menungguVerifikasiKyc;

    @Schema(description = "Jumlah pengajuan siap dicairkan", example = "6")
    private long siapDicairkan;

    @Schema(description = "Total transaksi pencairan berhasil", example = "84")
    private long totalTransaksiPencairan;

    @Schema(description = "Total nominal pinjaman yang dicairkan (Rp)", example = "1650000000")
    private BigDecimal totalNominalDicairkan;

    @Schema(description = "Total perolehan biaya admin (Rp)", example = "4200000")
    private BigDecimal totalBiayaAdmin;

    @Schema(description = "Total angsuran aktif", example = "142")
    private long totalAngsuranAktif;

    // Charts
    @Schema(description = "Distribusi bank tujuan pencairan nasabah")
    private Map<String, Long> bankDistribution;

    @Schema(description = "Distribusi status verifikasi KYC")
    private Map<String, Long> kycDistribution;

    @Schema(description = "Tren pencairan harian dalam seminggu terakhir")
    private List<DailyDisbursementItem> weeklyDisbursements;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class DailyDisbursementItem {
        @Schema(description = "Tanggal", example = "2026-09-22")
        private String date;

        @Schema(description = "Nama Hari", example = "Selasa")
        private String day;

        @Schema(description = "Jumlah transaksi cair", example = "5")
        private long count;

        @Schema(description = "Total nominal dicairkan", example = "95000000")
        private BigDecimal totalNominal;
    }
}
