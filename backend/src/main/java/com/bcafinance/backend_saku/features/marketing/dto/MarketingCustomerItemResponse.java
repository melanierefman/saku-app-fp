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
public class MarketingCustomerItemResponse {
    private UUID customerId;
    private String namaCustomer;
    private String nik;
    private String email;
    private String noHp;
    private String kota;
    private String provinsi;
    private BigDecimal totalPlafond;
    private BigDecimal usedPlafond;
    private BigDecimal availablePlafond;
    private String tierPlafond;
    private Integer totalPengajuan;
    private Boolean statusAkun;
    private LocalDateTime tanggalVerifikasi;
    private LocalDateTime createdDate;
}
