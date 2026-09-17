package com.bcafinance.backend_saku.features.customer.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

public record RegisterStep2PersonalRequest(
        @NotBlank(message = "Nomor handphone wajib diisi")
        @Size(min = 10, max = 16, message = "Nomor handphone harus antara 10 - 16 digit")
        @Pattern(regexp = "^[0-9+]+$", message = "Nomor handphone hanya boleh berisi angka")
        String noHp,

        @NotBlank(message = "Nama pemilik rekening wajib diisi")
        String namaRekening,

        @NotBlank(message = "Nama bank wajib diisi")
        String namaBank,

        @NotBlank(message = "Nomor rekening wajib diisi")
        String noRekening,

        @NotBlank(message = "Profesi/pekerjaan wajib diisi")
        @Size(max = 100, message = "Pekerjaan maksimal 100 karakter")
        String pekerjaan,

        @NotBlank(message = "Tempat kerja wajib diisi")
        @Size(max = 150, message = "Tempat kerja maksimal 150 karakter")
        String tempatKerja,

        @NotBlank(message = "Status pekerjaan wajib diisi")
        String statusPekerjaan,

        @NotNull(message = "Pendapatan per bulan wajib diisi")
        @Positive(message = "Pendapatan harus lebih dari 0")
        BigDecimal pendapatan,

        @NotNull(message = "Masa kerja wajib diisi")
        @Min(value = 0, message = "Lama bekerja tidak boleh negatif")
        Integer lamaBekerjaBulan,

        @NotNull(message = "Total cicilan lain wajib diisi (0 jika tidak ada)")
        @DecimalMin(value = "0", message = "Total cicilan lain tidak boleh negatif")
        BigDecimal totalCicilanLainnya,

        @Valid
        AlamatCustomer alamatDomisili,

        Boolean sameAsKtp,

        String namaIbuKandung
) {}
