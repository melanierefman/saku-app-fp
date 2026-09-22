package com.bcafinance.backend_saku.features.branchmanager.controller;

import com.bcafinance.backend_saku.core.dto.ApiResponse;
import com.bcafinance.backend_saku.core.security.AppUser;
import com.bcafinance.backend_saku.features.branchmanager.dto.BranchManagerDashboardStatsResponse;
import com.bcafinance.backend_saku.features.branchmanager.service.BranchManagerPersetujuanService;
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
@RequestMapping({"/api/branchmanager/dashboard", "/api/branch-manager/dashboard", "/api/bm/dashboard"})
@RequiredArgsConstructor
@PreAuthorize("hasRole('BRANCHMANAGER')")
@Tag(name = "Branch Manager - Dashboard", description = "Statistik metrik keputusan approval, volume kredit, dan performa cabang Branch Manager")
public class BranchManagerDashboardController {

    private final BranchManagerPersetujuanService branchManagerPersetujuanService;

    // Ambil ringkasan statistik keputusan persetujuan pinjaman Branch Manager
    @Operation(summary = "Statistik dashboard BM", description = "Mengambil ringkasan statistik jumlah pengajuan butuh approval, disetujui, ditolak, dan total nominal disetujui")
    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<BranchManagerDashboardStatsResponse>> getDashboardStats(
            @AuthenticationPrincipal AppUser karyawan) {
        UUID karyawanId = karyawan != null ? karyawan.getIdKaryawan() : null;
        return ResponseEntity.ok(ApiResponse.success(branchManagerPersetujuanService.getDashboardStats(karyawanId)));
    }
}
