package com.bcafinance.backend_saku.features.branchmanager.controller;

import com.bcafinance.backend_saku.core.dto.ApiResponse;
import com.bcafinance.backend_saku.core.dto.PageResponse;
import com.bcafinance.backend_saku.core.security.AppUser;
import com.bcafinance.backend_saku.features.branchmanager.dto.BranchManagerCustomerDetailResponse;
import com.bcafinance.backend_saku.features.branchmanager.dto.BranchManagerCustomerItemResponse;
import com.bcafinance.backend_saku.features.branchmanager.service.BranchManagerCustomerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping({"/api/branch-manager/customers", "/api/bm/customers", "/api/branchmanager/customers"})
@RequiredArgsConstructor
@Tag(name = "Branch Manager - Daftar Nasabah", description = "Supervisi dan monitoring portofolio nasabah terverifikasi untuk Branch Manager")
public class BranchManagerCustomerController {

    private final BranchManagerCustomerService branchManagerCustomerService;

    @Operation(summary = "Daftar nasabah BM", description = "Mengambil daftar nasabah terverifikasi di bawah supervisi cabang BM")
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<BranchManagerCustomerItemResponse>>> findAll(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size,
            @RequestParam(name = "search", required = false) String search,
            @RequestParam(name = "tier", required = false) String tier,
            @AuthenticationPrincipal AppUser karyawan) {
        UUID karyawanId = karyawan != null ? karyawan.getIdKaryawan() : null;
        return ResponseEntity.ok(ApiResponse.success(
                branchManagerCustomerService.findAllPaginated(page, size, search, tier, karyawanId)));
    }

    @Operation(summary = "Detail nasabah BM", description = "Mengambil rincian data nasabah, sisa plafond, dan performa histori pinjaman nasabah")
    @GetMapping("/{customerId}")
    public ResponseEntity<ApiResponse<BranchManagerCustomerDetailResponse>> getDetail(
            @PathVariable UUID customerId) {
        return ResponseEntity.ok(ApiResponse.success(
                branchManagerCustomerService.getDetail(customerId)));
    }
}
