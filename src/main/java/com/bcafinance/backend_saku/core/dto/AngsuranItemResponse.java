package com.bcafinance.backend_saku.core.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
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
public class AngsuranItemResponse {

    private UUID id;
    private Integer cicilanKe;
    private BigDecimal jumlahAngsuran;
    private LocalDate jatuhTempo;
    private String statusBayar;
}
