package com.bcafinance.backend_saku.features.branchmanager.controller;

import com.bcafinance.backend_saku.core.dto.ApiResponse;
import com.bcafinance.backend_saku.features.branchmanager.dto.BranchManagerPengajuanDetailResponse;
import com.bcafinance.backend_saku.features.branchmanager.dto.BranchManagerPengajuanItemResponse;
import com.bcafinance.backend_saku.features.branchmanager.dto.PersetujuanPinjamanRequest;
import com.bcafinance.backend_saku.features.branchmanager.dto.PersetujuanPinjamanResponse;
import com.bcafinance.backend_saku.core.security.AppUser;
import com.bcafinance.backend_saku.features.branchmanager.service.BranchManagerPersetujuanService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bcafinance.backend_saku.core.dto.PageResponse;

@RestController
@RequestMapping({"/api/branch-manager/persetujuan", "/api/bm/persetujuan", "/api/branchmanager/persetujuan"})
@RequiredArgsConstructor
public class BranchManagerPersetujuanController {

    private final BranchManagerPersetujuanService branchManagerPersetujuanService;

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<BranchManagerPengajuanItemResponse>>> findAll(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size,
            @RequestParam(name = "search", required = false) String search,
            @RequestParam(name = "status", required = false) String status,
            @AuthenticationPrincipal AppUser karyawan) {
        UUID karyawanId = karyawan != null ? karyawan.getIdKaryawan() : null;
        return ResponseEntity.ok(ApiResponse.success(
                branchManagerPersetujuanService.findAllPaginated(page, size, search, status, karyawanId)));
    }

    @GetMapping("/all")
    public ResponseEntity<ApiResponse<List<BranchManagerPengajuanItemResponse>>> findAllList(
            @RequestParam(required = false) String status,
            @AuthenticationPrincipal AppUser karyawan) {
        UUID karyawanId = karyawan != null ? karyawan.getIdKaryawan() : null;
        return ResponseEntity.ok(ApiResponse.success(branchManagerPersetujuanService.findAll(status, karyawanId)));
    }



    @GetMapping("/{pengajuanId}")
    public ResponseEntity<ApiResponse<BranchManagerPengajuanDetailResponse>> getDetail(
            @PathVariable UUID pengajuanId) {
        return ResponseEntity.ok(ApiResponse.success(branchManagerPersetujuanService.getDetail(pengajuanId)));
    }

    @PutMapping({"/{pengajuanId}", "/{pengajuanId}/approve"})
    public ResponseEntity<ApiResponse<PersetujuanPinjamanResponse>> persetujuan(
            @PathVariable UUID pengajuanId,
            @Valid @RequestBody PersetujuanPinjamanRequest request,
            @AuthenticationPrincipal AppUser karyawan) {
        return ResponseEntity.ok(ApiResponse.success(
                branchManagerPersetujuanService.persetujuan(pengajuanId, karyawan.getIdKaryawan(), request)));
    }
}
