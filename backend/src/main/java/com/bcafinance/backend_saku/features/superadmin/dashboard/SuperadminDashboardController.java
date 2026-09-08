package com.bcafinance.backend_saku.features.superadmin.dashboard;

import com.bcafinance.backend_saku.core.dto.ApiResponse;
import com.bcafinance.backend_saku.features.superadmin.dashboard.dto.SuperadminDashboardStatsResponse;
import com.bcafinance.backend_saku.features.superadmin.dashboard.service.SuperadminDashboardService;
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
public class SuperadminDashboardController {

    private final SuperadminDashboardService dashboardService;

    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<SuperadminDashboardStatsResponse>> getDashboardStats() {
        return ResponseEntity.ok(ApiResponse.success(dashboardService.getDashboardStats()));
    }
}
