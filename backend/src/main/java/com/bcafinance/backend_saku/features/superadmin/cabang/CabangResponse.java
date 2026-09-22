package com.bcafinance.backend_saku.features.superadmin.cabang;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CabangResponse implements Serializable {

    @Schema(description = "ID Cabang", example = "cb123e45-6789-4bc5-a123-456789abcdef")
    private UUID id;

    @Schema(description = "Nama Kantor Cabang", example = "Cabang Jakarta Pusat")
    private String nama;

    @Schema(description = "Kota Lokasi Cabang", example = "Jakarta Pusat")
    private String kota;

    @Schema(description = "Apakah Cabang Utama / Default", example = "true")
    private Boolean isDefault;

    @Schema(description = "Status Keaktifan Cabang", example = "true")
    private Boolean status;
}
