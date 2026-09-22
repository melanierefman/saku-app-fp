package com.bcafinance.backend_saku.features.superadmin.dashboard;

import com.bcafinance.backend_saku.core.dto.ApiResponse;
import com.bcafinance.backend_saku.features.superadmin.dashboard.dto.SuperadminDashboardStatsResponse;
import com.bcafinance.backend_saku.features.superadmin.dashboard.service.SuperadminDashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping({"/api/master/dashboard", "/api/superadmin/dashboard", "/api/dashboard/superadmin"})
@RequiredArgsConstructor
@PreAuthorize("hasRole('SUPERADMIN')")
@Tag(name = "Superadmin - Dashboard", description = "Statistik keseluruhan metrik sistem, pengguna, cabang, dan volume pinjaman SAKU App")
public class SuperadminDashboardController {

    private final SuperadminDashboardService dashboardService;

    // Ambil ringkasan statistik metrik dashboard utama Superadmin
    @Operation(summary = "Statistik dashboard superadmin", description = "Mengambil seluruh ringkasan statistik metrik pengguna, cabang aktif, status pengajuan, dan total kredit cair")
    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<SuperadminDashboardStatsResponse>> getDashboardStats() {
        return ResponseEntity.ok(ApiResponse.success(dashboardService.getDashboardStats()));
    }
}
