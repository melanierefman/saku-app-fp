package com.bcafinance.backend_saku.features.customer.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public record RegisterStep3Request(
        @NotNull @Valid AlamatCustomer alamatKtp,
        @NotNull @Valid AlamatCustomer alamatDomisili) {
}
