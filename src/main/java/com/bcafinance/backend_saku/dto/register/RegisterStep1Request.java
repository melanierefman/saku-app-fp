package com.bcafinance.backend_saku.dto.register;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RegisterStep1Request(
        @Email @NotBlank String email,
        @NotBlank @Size(min = 4, max = 50) String username,
        @NotBlank @Pattern(regexp = "^08[0-9]{8,14}$") String noHp,
        @NotBlank @Size(min = 8) String password) {
}
