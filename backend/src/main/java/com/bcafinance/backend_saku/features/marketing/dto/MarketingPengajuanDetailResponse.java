package com.bcafinance.backend_saku.features.marketing.dto;

import com.bcafinance.backend_saku.core.dto.AlamatDetailResponse;
import com.bcafinance.backend_saku.core.dto.DokumenPinjamanResponse;
import com.bcafinance.backend_saku.features.scoring.dto.ScoringBreakdown;
import java.math.BigDecimal;
import java.time.LocalDateTime;
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
public class MarketingPengajuanDetailResponse {

    // Pengajuan Info
    private UUID pengajuanId;
    private String nomorPengajuan;
    private LocalDateTime tanggalPengajuan;
    private String statusPengajuan;
    private String catatanPengajuan;

    // Data Customer
    private UUID customerId;
    private String namaLengkap;
    private String email;
    private String nik;
    private String noHp;
    private String namaIbuKandung;
    private String pekerjaan;
    private String tempatKerja;
    private String statusPekerjaan;
    private BigDecimal pendapatan;
    private BigDecimal penghasilanBulanan;
    private String namaBank;
    private String noRekening;
    private String namaRekening;
    private AlamatDetailResponse alamatKtp;
    private AlamatDetailResponse alamatDomisili;

    // Dokumen Identitas & Dokumen Pinjaman
    private String fotoSelfie;
    private String fotoKtp;
    private String slipGaji;
    private String rekeningKoran;
    private String npwp;
    private List<DokumenPinjamanResponse> dokumenPinjamanList;

    // Hasil Scoring & Indikator Ambigu
    private String scoringStatusPekerjaan;
    private BigDecimal scoringPenghasilan;
    private Integer lamaBekerjaBulan;
    private BigDecimal cicilanBerjalan;
    private Integer skor;
    private String statusScoring;
    private String keputusanSistem;
    private BigDecimal dbr;
    private Double dbrPercentage;
    private String plafonNama;
    private BigDecimal plafonMaksimal;
    private BigDecimal estimasiPlafondDisetujui;
    private BigDecimal totalPlafond;
    private Boolean isAmbigu;
    private List<String> notesAmbigu;
    private String ringkasanAnalisis;
    private String rekomendasiAksi;
    private UUID rekomendasiTierId;
    private String rekomendasiTierNama;
    private BigDecimal rekomendasiBunga;
    private BigDecimal rekomendasiBiayaAdmin;
    private String rekomendasiAlasan;
    private List<com.bcafinance.backend_saku.core.dto.PlafondOptionResponse> availablePlafondTiers;
    private ScoringBreakdown breakdown;

    // Detail Pinjaman
    private BigDecimal jumlahPinjaman;
    private Integer tenorBulan;
    private String tujuanPinjaman;
    private BigDecimal bunga;
    private BigDecimal biayaAdmin;
    private BigDecimal estimasiCicilan;
    private UUID branchId;
    private String namaCabang;
    private String kotaCabang;

    // Review & History
    private ReviewPengajuanResponse latestReview;
    private List<ReviewPengajuanResponse> reviewHistory;
}
