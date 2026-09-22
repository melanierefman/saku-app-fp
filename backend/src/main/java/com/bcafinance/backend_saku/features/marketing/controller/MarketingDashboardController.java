package com.bcafinance.backend_saku.features.marketing.controller;

import com.bcafinance.backend_saku.core.dto.ApiResponse;
import com.bcafinance.backend_saku.core.security.AppUser;
import com.bcafinance.backend_saku.features.marketing.dto.MarketingDashboardStatsResponse;
import com.bcafinance.backend_saku.features.marketing.service.MarketingReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/marketing/dashboard")
@RequiredArgsConstructor
@PreAuthorize("hasRole('MARKETING')")
@Tag(name = "Marketing - Dashboard", description = "Statistik performa, antrean review, dan volume pinjaman staf Marketing")
public class MarketingDashboardController {

    private final MarketingReviewService marketingReviewService;

    // Ambil ringkasan statistik performa dan antrean pengajuan pinjaman Marketing
    @Operation(summary = "Statistik dashboard marketing", description = "Mengambil ringkasan statistik jumlah pengajuan pending, approved, rejected, dan metrik cabang")
    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<MarketingDashboardStatsResponse>> getDashboardStats(
            @AuthenticationPrincipal AppUser karyawan) {
        UUID karyawanId = karyawan != null ? karyawan.getIdKaryawan() : null;
        return ResponseEntity.ok(ApiResponse.success(marketingReviewService.getDashboardStats(karyawanId)));
    }
}
