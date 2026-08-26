package com.bcafinance.backend_saku.features.branchmanager.controller;

import com.bcafinance.backend_saku.core.dto.ApiResponse;
import com.bcafinance.backend_saku.features.branchmanager.dto.BranchManagerDashboardStatsResponse;
import com.bcafinance.backend_saku.features.branchmanager.service.BranchManagerPersetujuanService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/branchmanager/dashboard")
@RequiredArgsConstructor
@PreAuthorize("hasRole('BRANCHMANAGER')")
public class BranchManagerDashboardController {


    private final BranchManagerPersetujuanService branchManagerPersetujuanService;

    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<BranchManagerDashboardStatsResponse>> getDashboardStats() {
        return ResponseEntity.ok(ApiResponse.success(branchManagerPersetujuanService.getDashboardStats()));
    }
}
