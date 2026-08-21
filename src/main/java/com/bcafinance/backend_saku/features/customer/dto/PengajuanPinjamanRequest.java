package com.bcafinance.backend_saku.features.customer.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PengajuanPinjamanRequest {

    @NotNull(message = "Jumlah pinjaman tidak boleh kosong")
    @DecimalMin(value = "500000.00", message = "Jumlah pinjaman minimal Rp 500.000")
    private BigDecimal jumlahPinjaman;

    @NotNull(message = "Tenor pinjaman tidak boleh kosong")
    @Min(value = 1, message = "Tenor minimal 1 bulan")
    @Max(value = 60, message = "Tenor maksimal 60 bulan")
    private Integer tenorBulan;

    @NotBlank(message = "Tujuan pinjaman tidak boleh kosong")
    @Size(max = 50, message = "Tujuan pinjaman maksimal 50 karakter")
    private String tujuanPinjaman;

    private UUID mstBranchId;
}
