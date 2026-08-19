package com.bcafinance.backend_saku.dto.register;

import jakarta.validation.constraints.NotBlank;

public record AlamatCustomer(
        @NotBlank String alamatLengkap,
        @NotBlank String rt,
        @NotBlank String rw,
        @NotBlank String kelurahan,
        @NotBlank String kecamatan,
        @NotBlank String kotaKabupaten,
        @NotBlank String provinsi,
        @NotBlank String kodePos) {
}
