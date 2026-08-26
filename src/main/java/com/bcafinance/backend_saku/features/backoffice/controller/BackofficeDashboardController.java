package com.bcafinance.backend_saku.features.backoffice.controller;

import com.bcafinance.backend_saku.core.dto.ApiResponse;
import com.bcafinance.backend_saku.features.backoffice.dto.BackofficeDashboardStatsResponse;
import com.bcafinance.backend_saku.features.backoffice.service.PencairanService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/backoffice/dashboard")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('BACKOFFICE', 'ADMIN')")
public class BackofficeDashboardController {

    private final PencairanService pencairanService;

    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<BackofficeDashboardStatsResponse>> getDashboardStats() {
        return ResponseEntity.ok(ApiResponse.success(pencairanService.getDashboardStats()));
    }
}
