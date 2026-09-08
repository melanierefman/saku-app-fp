package com.bcafinance.backend_saku.features.branchmanager.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
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
public class BranchManagerPengajuanItemResponse {

    private UUID pengajuanId;
    private String noPengajuan;
    private UUID customerId;
    private String customer;
    private String email;
    private String noHp;
    private LocalDateTime tanggalPengajuan;
    private BigDecimal jumlah;
    private Integer tenor;
    private String cabang;
    private String status; // MENUNGGU_PERSETUJUAN, PENGAJUAN_DISETUJUI, PENGAJUAN_DITOLAK

    // Info Review Marketing
    private String namaMarketingReviewer;
    private String catatanMarketing;
    private LocalDateTime tanggalReviewMarketing;

    // Info Persetujuan BM Terakhir
    private String hasilPersetujuanTerakhir;
    private String catatanPersetujuanTerakhir;
    private LocalDateTime tanggalPersetujuanTerakhir;
}
