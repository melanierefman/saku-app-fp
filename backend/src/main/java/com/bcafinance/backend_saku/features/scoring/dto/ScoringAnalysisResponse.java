package com.bcafinance.backend_saku.features.scoring.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
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
public class ScoringAnalysisResponse {

    // Ringkasan Skor
    private Integer skor;
    private String statusScoring;
    private String keputusanSistem;
    private BigDecimal dbr;
    private Double dbrPercentage;

    // Evaluasi Plafond
    private UUID matchedPlafondId;
    private String matchedPlafondNama;
    private BigDecimal matchedMinPendapatan;
    private BigDecimal matchedPlafondMaksimal;
    private BigDecimal estimasiPlafondDisetujui;
    private BigDecimal totalPlafond;
    private BigDecimal usedPlafond;
    private BigDecimal availablePlafond;

    // Analisis Ambigu Scoring
    private Boolean isAmbigu;
    private List<String> indikatorAmbigu;
    private String ringkasanAnalisis;

    // Rekomendasi Aksi Sistem (untuk BM / Marketing)
    private String rekomendasiAksi;
    private UUID rekomendasiTierId;
    private String rekomendasiTierNama;
    private BigDecimal rekomendasiBunga;
    private BigDecimal rekomendasiBiayaAdmin;
    private String rekomendasiAlasan;

    // Rincian Penilaian Tiap Faktor
    private ScoringBreakdown breakdown;
}
