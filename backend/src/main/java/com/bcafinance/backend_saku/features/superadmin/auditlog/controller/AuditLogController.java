package com.bcafinance.backend_saku.features.superadmin.auditlog.controller;

import com.bcafinance.backend_saku.core.dto.ApiResponse;
import com.bcafinance.backend_saku.features.superadmin.auditlog.dto.AuditLogPageResponse;
import com.bcafinance.backend_saku.features.superadmin.auditlog.service.AuditLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping({"/api/superadmin/audit-log", "/api/master/audit-log", "/api/audit-log"})
@RequiredArgsConstructor
@PreAuthorize("hasRole('SUPERADMIN')")
@Tag(name = "Superadmin - Audit Log", description = "Pemantauan jejak audit aktivitas dan perubahan data dalam sistem SAKU App")
public class AuditLogController {

    private final AuditLogService auditLogService;

    // Ambil daftar catatan riwayat audit log aktivitas sistem (paginated)
    @Operation(summary = "Daftar log aktivitas sistem", description = "Mengambil catatan log audit aktivitas pengguna dengan filter aksi, entitas, ID karyawan, kata kunci, dan pagination")
    @GetMapping
    public ResponseEntity<ApiResponse<AuditLogPageResponse>> getAuditLogs(
            @RequestParam(required = false) String action,
            @RequestParam(required = false) String entity,
            @RequestParam(required = false) UUID karyawanId,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        AuditLogPageResponse response = auditLogService.getAuditLogs(
                action, entity, karyawanId, keyword, page, size);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
