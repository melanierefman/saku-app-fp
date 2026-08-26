package com.bcafinance.backend_saku.features.marketing.controller;

import com.bcafinance.backend_saku.core.dto.ApiResponse;
import com.bcafinance.backend_saku.features.marketing.dto.MarketingDashboardStatsResponse;
import com.bcafinance.backend_saku.features.marketing.service.MarketingReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/marketing/dashboard")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('MARKETING', 'ADMIN', 'BRANCHMANAGER', 'BACKOFFICE')")
public class MarketingDashboardController {

    private final MarketingReviewService marketingReviewService;

    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<MarketingDashboardStatsResponse>> getDashboardStats() {
        return ResponseEntity.ok(ApiResponse.success(marketingReviewService.getDashboardStats()));
    }
}
