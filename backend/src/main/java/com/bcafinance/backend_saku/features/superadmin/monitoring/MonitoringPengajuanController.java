package com.bcafinance.backend_saku.features.superadmin.monitoring;

import com.bcafinance.backend_saku.core.dto.ApiResponse;
import com.bcafinance.backend_saku.core.dto.PageResponse;
import com.bcafinance.backend_saku.features.superadmin.monitoring.dto.MonitoringPengajuanDetailResponse;
import com.bcafinance.backend_saku.features.superadmin.monitoring.dto.MonitoringPengajuanItemResponse;
import com.bcafinance.backend_saku.features.superadmin.monitoring.service.MonitoringPengajuanService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping({"/api/superadmin/monitoring-pengajuan", "/api/master/monitoring-pengajuan", "/api/monitoring-pengajuan"})
@RequiredArgsConstructor
@PreAuthorize("hasRole('SUPERADMIN')")
@Tag(name = "Superadmin - Monitoring Pengajuan", description = "Pemantauan komprehensif seluruh siklus hidup pengajuan pinjaman nasabah")
public class MonitoringPengajuanController {

    private final MonitoringPengajuanService monitoringPengajuanService;

    // Ambil data monitoring pengajuan pinjaman end-to-end secara terpaginasi
    @Operation(summary = "Monitoring pengajuan pinjaman (paginasi)", description = "Mengambil daftar pemantauan seluruh pengajuan pinjaman dengan pagination, status, dan filter cabang")
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<MonitoringPengajuanItemResponse>>> findAllPaginated(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size,
            @RequestParam(name = "search", required = false) String search,
            @RequestParam(name = "status", required = false) String status,
            @RequestParam(name = "branchId", required = false) UUID branchId) {
        PageResponse<MonitoringPengajuanItemResponse> result = monitoringPengajuanService
                .findAllPaginated(page, size, search, status, branchId);
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    // Ambil seluruh data monitoring pengajuan pinjaman tanpa paginasi untuk kebutuhan ekspor
    @Operation(summary = "Ekspor data pengajuan pinjaman", description = "Mengambil seluruh data monitoring pengajuan tanpa paginasi untuk kebutuhan rekapitulasi/ekspor")
    @GetMapping("/all")
    public ResponseEntity<ApiResponse<List<MonitoringPengajuanItemResponse>>> findAll(
            @RequestParam(name = "status", required = false) String status,
            @RequestParam(name = "branchId", required = false) UUID branchId) {
        return ResponseEntity.ok(ApiResponse.success(monitoringPengajuanService.findAll(status, branchId)));
    }

    // Ambil detail riwayat lengkap pengajuan pinjaman end-to-end berdasarkan ID
    @Operation(summary = "Detail audit pengajuan pinjaman", description = "Mengambil audit jejak status lengkap, timeline approval, dan berkas pengajuan pinjaman")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<MonitoringPengajuanDetailResponse>> getDetail(
            @PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(monitoringPengajuanService.getDetail(id)));
    }
}
