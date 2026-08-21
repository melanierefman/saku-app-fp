package com.bcafinance.backend_saku.features.customer.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record RegisterStep2Request(
        @NotBlank @Size(min = 16, max = 16) String nik,
        @NotBlank String namaLengkap,
        @NotBlank String namaRekening,
        @NotBlank String namaBank,
        @NotBlank String noRekening,
        @NotBlank @Size(max = 100) String pekerjaan,
        @NotBlank @Size(max = 150) String tempatKerja,
        @NotBlank String statusPekerjaan,
        @NotNull @Positive BigDecimal pendapatan,
        @NotNull @Min(0) Integer lamaBekerjaBulan,
        @NotNull @Min(0) Integer lamaJadiNasabahBulan,
        @NotNull @DecimalMin("0") BigDecimal totalCicilanLainnya) {
}
