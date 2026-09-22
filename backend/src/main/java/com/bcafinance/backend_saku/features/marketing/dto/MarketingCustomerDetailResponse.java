package com.bcafinance.backend_saku.features.marketing.dto;

import com.bcafinance.backend_saku.core.dto.AlamatDetailResponse;
import com.bcafinance.backend_saku.core.dto.CustomerPinjamanHistoryItemResponse;
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
public class MarketingCustomerDetailResponse {
    private UUID customerId;
    private String namaCustomer;
    private String nik;
    private String email;
    private String noHp;
    private String namaIbuKandung;
    private String namaBank;
    private String noRekening;
    private String namaRekening;
    private AlamatDetailResponse alamatKtp;
    private AlamatDetailResponse alamatDomisili;
    private String fotoKtp;
    private String fotoSelfie;
    private String pekerjaan;
    private String tempatKerja;
    private BigDecimal pendapatan;
    private Integer skor;
    private String statusScoring;
    private String tierPlafond;
    private BigDecimal totalPlafond;
    private BigDecimal usedPlafond;
    private BigDecimal availablePlafond;
    private Boolean statusAkun;
    private LocalDateTime tanggalVerifikasi;
    private LocalDateTime createdDate;
    private List<CustomerPinjamanHistoryItemResponse> riwayatPinjaman;
}
