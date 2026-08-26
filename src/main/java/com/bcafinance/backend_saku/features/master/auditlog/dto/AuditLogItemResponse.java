package com.bcafinance.backend_saku.features.master.auditlog.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
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
public class AuditLogItemResponse {

    private UUID id;
    private String action;
    private String entity;
    private Integer entityId;
    private String description;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdDate;

    private KaryawanAuditInfo karyawan;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class KaryawanAuditInfo {
        private UUID id;
        private String nama;
        private String email;
        private String username;
        private String role;
        private String cabang;
    }
}
