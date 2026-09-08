package com.bcafinance.backend_saku.features.customer.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdatePekerjaanRequest {

    @NotBlank(message = "Pekerjaan tidak boleh kosong")
    @Size(max = 100, message = "Pekerjaan maksimal 100 karakter")
    private String pekerjaan;

    @NotBlank(message = "Tempat kerja tidak boleh kosong")
    @Size(max = 150, message = "Tempat kerja maksimal 150 karakter")
    private String tempatKerja;

    @NotBlank(message = "Status pekerjaan tidak boleh kosong")
    @Size(max = 50, message = "Status pekerjaan maksimal 50 karakter")
    private String statusPekerjaan;

    @NotNull(message = "Penghasilan bulanan tidak boleh kosong")
    @DecimalMin(value = "0.0", message = "Penghasilan bulanan tidak boleh negatif")
    private BigDecimal penghasilanBulanan;

    @NotNull(message = "Lama bekerja tidak boleh kosong")
    @Min(value = 0, message = "Lama bekerja tidak boleh negatif")
    private Integer lamaBekerjaBulan;

    @DecimalMin(value = "0.0", message = "Total cicilan lain tidak boleh negatif")
    private BigDecimal totalCicilanLainBulanan;
}
