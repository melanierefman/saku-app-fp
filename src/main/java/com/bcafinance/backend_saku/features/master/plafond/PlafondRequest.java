package com.bcafinance.backend_saku.features.master.plafond;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PlafondRequest {

    @NotBlank
    @Size(max = 50)
    private String nama;

    @NotNull
    @DecimalMin("0")
    @Digits(integer = 13, fraction = 2)
    private BigDecimal minPendapatan;

    @NotNull
    @DecimalMin("0")
    @Digits(integer = 13, fraction = 2)
    private BigDecimal plafondMaksimal;

    @NotNull
    @DecimalMin("0")
    @Digits(integer = 13, fraction = 2)
    private BigDecimal minPlafond;

    @NotNull
    @DecimalMin("0")
    @Digits(integer = 13, fraction = 2)
    private BigDecimal maxPlafond;

    @NotNull
    @Min(0)
    @Max(100)
    private Integer minSkor;

    @NotNull
    @Min(0)
    @Max(100)
    private Integer maxSkor;

    @NotNull
    @DecimalMin("0")
    @Digits(integer = 1, fraction = 2)
    private BigDecimal bunga;

    @NotNull
    @DecimalMin("0")
    @Digits(integer = 13, fraction = 2)
    private BigDecimal biayaAdmin;

    @NotNull
    private Boolean status;
}
