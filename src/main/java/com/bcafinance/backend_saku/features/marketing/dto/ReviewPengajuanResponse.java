package com.bcafinance.backend_saku.features.marketing.dto;

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
public class ReviewPengajuanResponse {

    private UUID id;
    private UUID pengajuanId;
    private String nomorPengajuan;
    private String hasilReview;
    private String catatan;
    private UUID reviewerId;
    private String namaReviewer;
    private LocalDateTime tanggalReview;
    private String statusPengajuan;
}
