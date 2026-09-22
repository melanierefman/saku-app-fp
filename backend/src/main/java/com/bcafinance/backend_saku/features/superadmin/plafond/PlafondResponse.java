package com.bcafinance.backend_saku.features.superadmin.plafond;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PlafondResponse implements Serializable {

    @Schema(description = "ID Tier Plafond", example = "pl123e45-6789-4bc5-a123-456789abcdef")
    private UUID id;

    @Schema(description = "Nama Tier Plafond", example = "Plafond Gold")
    private String nama;

    @Schema(description = "Batas Minimum Pendapatan (Rp)", example = "8000000")
    private BigDecimal minPendapatan;

    @Schema(description = "Batas Maksimal Plafond (Rp)", example = "25000000")
    private BigDecimal plafondMaksimal;

    @Schema(description = "Batas Minimum Nominal Pinjaman (Rp)", example = "5000000")
    private BigDecimal minPlafond;

    @Schema(description = "Batas Maksimum Nominal Pinjaman (Rp)", example = "25000000")
    private BigDecimal maxPlafond;

    @Schema(description = "Batas Minimum Skor Kredit", example = "650")
    private Integer minSkor;

    @Schema(description = "Batas Maksimum Skor Kredit", example = "850")
    private Integer maxSkor;

    @Schema(description = "Suku Bunga Bulanan (%)", example = "1.25")
    private BigDecimal bunga;

    @Schema(description = "Biaya Administrasi (Rp)", example = "50000")
    private BigDecimal biayaAdmin;

    @Schema(description = "Status Keaktifan Aturan", example = "true")
    private Boolean status;

    @Schema(description = "Waktu Dibuat", example = "2026-09-01T08:00:00")
    private LocalDateTime createdDate;

    @Schema(description = "Waktu Terakhir Diperbarui", example = "2026-09-22T08:00:00")
    private LocalDateTime updatedDate;
}
