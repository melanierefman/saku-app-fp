package com.bcafinance.backend_saku.controller;

import com.bcafinance.backend_saku.dto.ApiResponse;
import com.bcafinance.backend_saku.dto.PengajuanPinjamanRequest;
import com.bcafinance.backend_saku.dto.PengajuanPinjamanResponse;
import com.bcafinance.backend_saku.dto.PengajuanStepResponse;
import com.bcafinance.backend_saku.security.AppUser;
import com.bcafinance.backend_saku.service.PengajuanPinjamanService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/customer/pengajuan-pinjaman")
@RequiredArgsConstructor
public class PengajuanPinjamanController {

    private final PengajuanPinjamanService pengajuanService;

    @PostMapping("/step1")
    public ResponseEntity<ApiResponse<PengajuanStepResponse>> step1(
            @Valid @RequestBody PengajuanPinjamanRequest request,
            @AuthenticationPrincipal AppUser customer) {
        return ResponseEntity.ok(ApiResponse.success(
                pengajuanService.step1(customer.getIdKaryawan(), request)));
    }

    @PostMapping(value = "/step2/{pengajuanId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<PengajuanStepResponse>> step2(
            @PathVariable UUID pengajuanId,
            @RequestPart("slipGaji") MultipartFile slipGaji,
            @RequestPart("rekeningKoran") MultipartFile rekeningKoran,
            @RequestPart(value = "npwp", required = false) MultipartFile npwp,
            @AuthenticationPrincipal AppUser customer) {
        return ResponseEntity.ok(ApiResponse.success(
                pengajuanService.step2(customer.getIdKaryawan(), pengajuanId, slipGaji, rekeningKoran, npwp)));
    }

    @GetMapping("/my")
    public ResponseEntity<ApiResponse<List<PengajuanPinjamanResponse>>> findMyLoans(
            @AuthenticationPrincipal AppUser customer) {
        return ResponseEntity.ok(ApiResponse.success(
                pengajuanService.findMyLoans(customer.getIdKaryawan())));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PengajuanPinjamanResponse>> findById(
            @PathVariable UUID id,
            @AuthenticationPrincipal AppUser customer) {
        return ResponseEntity.ok(ApiResponse.success(
                pengajuanService.findById(id, customer.getIdKaryawan())));
    }
}
