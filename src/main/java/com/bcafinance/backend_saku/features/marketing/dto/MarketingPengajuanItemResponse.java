package com.bcafinance.backend_saku.features.marketing.dto;

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
public class MarketingPengajuanItemResponse {

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
    private String status;
    private String hasilReviewTerakhir;
    private String catatanReviewTerakhir;
    private LocalDateTime tanggalReviewTerakhir;
}
