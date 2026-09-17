package com.bcafinance.backend_saku.features.branchmanager.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
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
public class PersetujuanPinjamanRequest {

    @NotBlank(message = "Hasil persetujuan tidak boleh kosong")
    @Pattern(regexp = "^(?i)(DISETUJUI|APPROVED|DISETUJUI_DENGAN_PENYESUAIAN|SETUJU_PENYESUAIAN|DITOLAK|REJECTED)$", message = "Hasil persetujuan harus DISETUJUI atau DITOLAK")
    @JsonAlias({"statusPersetujuan", "status", "hasil"})
    private String hasilPersetujuan;

    @NotBlank(message = "Catatan persetujuan tidak boleh kosong")
    @Size(max = 1000, message = "Catatan persetujuan maksimal 1000 karakter")
    @JsonAlias({"catatanPersetujuan", "notes"})
    private String catatan;

    @JsonAlias({"adjustedPlafondId", "tierId", "selectedTierId"})
    private java.util.UUID penyesuaianTierId;

    @JsonAlias({"alasanKategori", "presetReason", "jenisMasalah"})
    private String kategoriAlasan;

    @JsonAlias({"jumlahDisetujui", "adjustedPlafond"})
    private java.math.BigDecimal adjustedJumlahPinjaman;

    private java.math.BigDecimal adjustedBunga;
    private java.math.BigDecimal adjustedBiayaAdmin;
}
