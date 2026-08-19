package com.bcafinance.backend_saku.dto;

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

    // Analisis Ambigu Scoring
    private Boolean isAmbigu;
    private List<String> indikatorAmbigu;
    private String ringkasanAnalisis;

    // Rincian Penilaian Tiap Faktor
    private ScoringBreakdown breakdown;
}
