package com.bcafinance.backend_saku.features.backoffice.controller;

import com.bcafinance.backend_saku.core.dto.ApiResponse;
import com.bcafinance.backend_saku.features.backoffice.dto.BackofficeDashboardStatsResponse;
import com.bcafinance.backend_saku.features.backoffice.service.PencairanService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping({"/api/backoffice/dashboard", "/api/bo/dashboard"})
@RequiredArgsConstructor
@PreAuthorize("hasRole('BACKOFFICE')")
@Tag(name = "Backoffice - Dashboard", description = "Statistik antrean verifikasi KYC dan rekapitulasi pencairan pinjaman Backoffice")
public class BackofficeDashboardController {

    private final PencairanService pencairanService;

    // Ambil ringkasan statistik antrean verifikasi customer & pencairan Backoffice
    @Operation(summary = "Statistik dashboard backoffice", description = "Mengambil statistik total verifikasi KYC pending, verifikasi disetujui, dan total dana dicairkan")
    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<BackofficeDashboardStatsResponse>> getDashboardStats() {
        return ResponseEntity.ok(ApiResponse.success(pencairanService.getDashboardStats()));
    }
}
