package com.bcafinance.backend_saku.dto.register;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public record RegisterStep3Request(
        @NotNull @Valid AlamatCustomer alamatKtp,
        @NotNull @Valid AlamatCustomer alamatDomisili) {
}
