package com.bcafinance.backend_saku.dto;

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
public class BranchManagerPengajuanDetailResponse {

    // 1. Info Utama Pengajuan
    private UUID pengajuanId;
    private String nomorPengajuan;
    private LocalDateTime tanggalPengajuan;
    private String statusPengajuan;
    private String catatanPengajuan;

    // 2. Data Customer
    private UUID customerId;
    private String namaLengkap;
    private String email;
    private String nik;
    private String noHp;
    private String pekerjaan;
    private String tempatKerja;
    private String statusPekerjaan;
    private BigDecimal pendapatan;
    private BigDecimal penghasilanBulanan;
    private String namaBank;
    private String noRekening;
    private String namaRekening;

    // Alamat
    private AlamatDetailResponse alamatKtp;
    private AlamatDetailResponse alamatDomisili;

    // 3. Dokumen Foto Identitas & Pinjaman
    private String fotoSelfie;
    private String fotoKtp;
    private String fotoSlipGaji;
    private String fotoRekeningKoran;
    private String fotoNpwp;
    private List<DokumenPinjamanResponse> dokumenPinjamanList;

    // 4. Hasil Scoring & Indikator Ambigu
    private String statusPekerjaanScoring;
    private BigDecimal penghasilanBulananScoring;
    private Integer lamaBekerjaBulan;
    private Integer lamaJadiNasabahBulan;
    private BigDecimal cicilanBerjalan;
    private Integer skor;
    private String statusScoring;
    private String plafonNama;
    private BigDecimal plafonMaksimal;
    private List<String> notesAmbigu;
    private String ringkasanScoring;

    // 5. Detail Pinjaman
    private BigDecimal jumlahPinjaman;
    private Integer tenorBulan;
    private String tujuanPinjaman;
    private BigDecimal bunga;
    private BigDecimal biayaAdmin;
    private BigDecimal estimasiCicilan;
    private UUID branchId;
    private String namaCabang;
    private String kotaCabang;

    // 6. Review & Rekomendasi Marketing
    private UUID marketingReviewerId;
    private String namaMarketingReviewer;
    private String hasilReviewMarketing;
    private String catatanMarketing;
    private LocalDateTime tanggalReviewMarketing;

    // 7. Riwayat
    private List<ReviewPengajuanResponse> reviewMarketingHistory;
    private List<PersetujuanPinjamanResponse> persetujuanHistory;
}
