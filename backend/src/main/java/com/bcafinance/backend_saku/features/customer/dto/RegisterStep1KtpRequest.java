package com.bcafinance.backend_saku.features.customer.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RegisterStep1KtpRequest(
        @NotBlank(message = "NIK wajib diisi")
        @Size(min = 16, max = 16, message = "NIK harus terdiri dari 16 digit")
        @Pattern(regexp = "^[0-9]{16}$", message = "NIK hanya boleh berisi angka")
        String nik,

        @NotBlank(message = "Nama lengkap sesuai KTP wajib diisi")
        String namaLengkap,

        @NotNull(message = "Alamat KTP wajib diisi")
        @Valid
        AlamatCustomer alamatKtp
) {}
