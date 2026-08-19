package com.bcafinance.backend_saku.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PlafondCalculationRequest {

    @NotNull
    @DecimalMin("0")
    private BigDecimal pendapatan;

    @NotNull
    private Double skorAkhir;
}
