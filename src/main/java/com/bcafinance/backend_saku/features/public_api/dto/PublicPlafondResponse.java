package com.bcafinance.backend_saku.features.public_api.dto;

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
public class PublicPlafondResponse {

    private UUID id;
    private String nama;
    private BigDecimal minPlafond;
    private BigDecimal maxPlafond;
    private BigDecimal bunga;
    private BigDecimal biayaAdmin;
    private Integer minSkor;
    private Integer maxSkor;
}
