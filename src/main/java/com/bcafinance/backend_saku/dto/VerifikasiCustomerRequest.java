package com.bcafinance.backend_saku.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class VerifikasiCustomerRequest {

    @NotBlank(message = "Status verifikasi tidak boleh kosong")
    @Pattern(regexp = "^(?i)(APPROVED|REJECTED|PERLU_REVISI|DISETUJUI|DITOLAK|REVISI)$", message = "Status verifikasi harus APPROVED, REJECTED, atau PERLU_REVISI")
    private String statusVerifikasi;

    @NotBlank(message = "Catatan verifikasi tidak boleh kosong")
    @Size(max = 500, message = "Catatan verifikasi maksimal 500 karakter")
    private String catatanVerifikasi;
}

