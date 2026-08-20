package com.bcafinance.backend_saku.controller;

import com.bcafinance.backend_saku.dto.ApiResponse;
import com.bcafinance.backend_saku.dto.BranchManagerPengajuanDetailResponse;
import com.bcafinance.backend_saku.dto.BranchManagerPengajuanItemResponse;
import com.bcafinance.backend_saku.dto.PersetujuanPinjamanRequest;
import com.bcafinance.backend_saku.dto.PersetujuanPinjamanResponse;
import com.bcafinance.backend_saku.security.AppUser;
import com.bcafinance.backend_saku.service.BranchManagerPersetujuanService;
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

@RestController
@RequestMapping({"/api/branch-manager/persetujuan", "/api/bm/persetujuan"})
@RequiredArgsConstructor
public class BranchManagerPersetujuanController {

    private final BranchManagerPersetujuanService branchManagerPersetujuanService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<BranchManagerPengajuanItemResponse>>> findAll(
            @RequestParam(required = false) String status) {
        return ResponseEntity.ok(ApiResponse.success(branchManagerPersetujuanService.findAll(status)));
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
