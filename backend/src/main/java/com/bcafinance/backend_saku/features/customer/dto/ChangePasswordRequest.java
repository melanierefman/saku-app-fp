package com.bcafinance.backend_saku.features.customer.dto;

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
public class ChangePasswordRequest {

    @NotBlank(message = "Password lama tidak boleh kosong")
    private String oldPassword;

    @NotBlank(message = "Password baru tidak boleh kosong")
    @Size(min = 8, message = "Password baru minimal 8 karakter")
    private String newPassword;

    @NotBlank(message = "Konfirmasi password baru tidak boleh kosong")
    @Size(min = 8, message = "Konfirmasi password baru minimal 8 karakter")
    private String confirmPassword;
}
