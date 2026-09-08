package com.bcafinance.backend_saku.features.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VerifyOtpRequest {

    @NotBlank(message = "Email tidak boleh kosong")
    @Email(message = "Format email tidak valid")
    private String email;

    @NotBlank(message = "Kode OTP tidak boleh kosong")
    @Size(min = 6, max = 6, message = "Kode OTP harus 6 digit")
    private String otpCode;

    @NotBlank(message = "Purpose tidak boleh kosong")
    private String purpose;
}
