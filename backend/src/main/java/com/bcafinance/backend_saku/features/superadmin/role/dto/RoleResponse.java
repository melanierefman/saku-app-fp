package com.bcafinance.backend_saku.features.superadmin.role.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
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
public class RoleResponse {

    @Schema(description = "ID Role", example = "ro123e45-6789-4bc5-a123-456789abcdef")
    private UUID id;

    @Schema(description = "Nama Role Pengguna", example = "SUPERADMIN")
    private String nama;

    @Schema(description = "Status Keaktifan Role", example = "true")
    private Boolean status;

    @Schema(description = "Jumlah Izin / Permission yang Terhubung", example = "24")
    private Integer totalPermissions;

    @Schema(description = "Waktu Dibuat", example = "2026-09-01T08:00:00")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdDate;

    @Schema(description = "Waktu Terakhir Diperbarui", example = "2026-09-22T08:00:00")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updatedDate;
}
