package com.bcafinance.backend_saku.features.customer.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RegisterStep1Request(
        @NotBlank(message = "Email tidak boleh kosong")
        @Email(message = "Format email tidak valid")
        String email,

        @NotBlank(message = "Username tidak boleh kosong")
        @Size(min = 4, max = 50, message = "Username minimal 4 dan maksimal 50 karakter")
        String username,

        @NotBlank(message = "Nomor HP tidak boleh kosong")
        @Pattern(regexp = "^08[0-9]{8,14}$", message = "Format nomor HP tidak valid (harus diawali 08 dan 10-16 digit)")
        String noHp,

        @NotBlank(message = "Password tidak boleh kosong")
        @Size(min = 8, message = "Password minimal 8 karakter")
        String password,

        @NotBlank(message = "Konfirmasi password tidak boleh kosong")
        @Size(min = 8, message = "Konfirmasi password minimal 8 karakter")
        String confirmPassword,

        String otpCode) {

    public RegisterStep1Request(String email, String username, String noHp, String password, String confirmPassword) {
        this(email, username, noHp, password, confirmPassword, null);
    }
}


