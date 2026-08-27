package com.bcafinance.backend_saku.features.public_api.dto;

import java.io.Serializable;
import java.math.BigDecimal;
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
public class SimulasiPinjamanResponse implements Serializable {

    private BigDecimal jumlahPinjaman;
    private Integer tenorBulan;
    private BigDecimal sukuBungaPersen;
    private BigDecimal cicilanPokokBulanan;
    private BigDecimal bungaBulanan;
    private BigDecimal totalCicilanBulanan;
    private BigDecimal biayaAdmin;
    private BigDecimal totalPembayaran;
    private String estimasiTierPlafond;
}
