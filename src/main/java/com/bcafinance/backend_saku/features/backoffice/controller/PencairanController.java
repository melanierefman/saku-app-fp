package com.bcafinance.backend_saku.features.backoffice.controller;

import com.bcafinance.backend_saku.core.dto.ApiResponse;
import com.bcafinance.backend_saku.core.security.AppUser;
import com.bcafinance.backend_saku.features.backoffice.dto.PencairanDetailResponse;
import com.bcafinance.backend_saku.features.backoffice.dto.PencairanItemResponse;
import com.bcafinance.backend_saku.features.backoffice.dto.PencairanRequest;
import com.bcafinance.backend_saku.features.backoffice.dto.PencairanResponse;
import com.bcafinance.backend_saku.features.backoffice.service.PencairanService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/backoffice/pencairan")
@RequiredArgsConstructor
public class PencairanController {

    private final PencairanService pencairanService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<PencairanItemResponse>>> findAll(
            @RequestParam(required = false) String status) {
        return ResponseEntity.ok(ApiResponse.success(pencairanService.findAll(status)));
    }

    @GetMapping("/pending")
    public ResponseEntity<ApiResponse<List<PencairanItemResponse>>> findPending() {
        return ResponseEntity.ok(ApiResponse.success(pencairanService.findAll("MENUNGGU_PENCAIRAN")));
    }

    @GetMapping("/{pengajuanId}")
    public ResponseEntity<ApiResponse<PencairanDetailResponse>> getDetail(
            @PathVariable UUID pengajuanId) {
        return ResponseEntity.ok(ApiResponse.success(pencairanService.getDetail(pengajuanId)));
    }

    @PostMapping({"/{pengajuanId}", "/{pengajuanId}/cairkan"})
    public ResponseEntity<ApiResponse<PencairanResponse>> cairkan(
            @PathVariable UUID pengajuanId,
            @RequestBody(required = false) PencairanRequest request,
            @AuthenticationPrincipal AppUser karyawan) {
        return ResponseEntity.ok(ApiResponse.success(
                pencairanService.cairkanPinjaman(pengajuanId, karyawan.getIdKaryawan(), request)));
    }

    @PutMapping("/{pengajuanId}")
    public ResponseEntity<ApiResponse<PencairanResponse>> cairkanPut(
            @PathVariable UUID pengajuanId,
            @RequestBody(required = false) PencairanRequest request,
            @AuthenticationPrincipal AppUser karyawan) {
        return ResponseEntity.ok(ApiResponse.success(
                pencairanService.cairkanPinjaman(pengajuanId, karyawan.getIdKaryawan(), request)));
    }

    @GetMapping("/{pengajuanId}/angsuran")
    public ResponseEntity<ApiResponse<List<com.bcafinance.backend_saku.features.backoffice.dto.AngsuranItemResponse>>> getJadwalAngsuran(
            @PathVariable UUID pengajuanId) {
        return ResponseEntity.ok(ApiResponse.success(pencairanService.getJadwalAngsuran(pengajuanId)));
    }
}

