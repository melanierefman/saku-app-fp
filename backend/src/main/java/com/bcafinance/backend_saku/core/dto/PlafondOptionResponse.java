package com.bcafinance.backend_saku.core.dto;

import java.math.BigDecimal;
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
public class PlafondOptionResponse {
    private UUID id;
    private String nama;
    private BigDecimal minPendapatan;
    private BigDecimal plafondMaksimal;
    private Integer minSkor;
    private Integer maxSkor;
    private BigDecimal bunga;
    private BigDecimal biayaAdmin;
    private Boolean status;
}
