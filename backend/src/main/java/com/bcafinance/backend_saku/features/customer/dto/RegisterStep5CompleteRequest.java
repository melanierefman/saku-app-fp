package com.bcafinance.backend_saku.features.customer.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterStep5CompleteRequest(
        @NotBlank(message = "Password wajib diisi")
        @Size(min = 8, message = "Password minimal 8 karakter")
        String password,

        @NotBlank(message = "Konfirmasi password wajib diisi")
        String confirmPassword
) {}
