package com.bcafinance.backend_saku.features.backoffice.controller;

import com.bcafinance.backend_saku.core.dto.ApiResponse;
import com.bcafinance.backend_saku.features.backoffice.dto.PendingCustomerResponse;
import com.bcafinance.backend_saku.features.backoffice.dto.VerifikasiCustomerDetailResponse;
import com.bcafinance.backend_saku.features.backoffice.dto.VerifikasiCustomerItemResponse;
import com.bcafinance.backend_saku.features.backoffice.dto.VerifikasiCustomerRequest;
import com.bcafinance.backend_saku.features.backoffice.dto.VerifikasiCustomerResponse;
import com.bcafinance.backend_saku.core.security.AppUser;
import com.bcafinance.backend_saku.features.backoffice.service.VerifikasiCustomerService;
import jakarta.validation.Valid;
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

import com.bcafinance.backend_saku.core.dto.PageResponse;

@RestController
@RequestMapping("/api/backoffice/verifikasi-customer")
@RequiredArgsConstructor
public class VerifikasiCustomerController {

    private final VerifikasiCustomerService verifikasiService;

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<VerifikasiCustomerItemResponse>>> findAll(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size,
            @RequestParam(name = "search", required = false) String search,
            @RequestParam(name = "status", required = false) String status) {
        return ResponseEntity.ok(ApiResponse.success(
                verifikasiService.findAllPaginated(page, size, search, status)));
    }

    @GetMapping("/all")
    public ResponseEntity<ApiResponse<List<VerifikasiCustomerItemResponse>>> findAllList(
            @RequestParam(required = false) String status) {
        return ResponseEntity.ok(ApiResponse.success(verifikasiService.findAll(status)));
    }

    @GetMapping("/pending")
    public ResponseEntity<ApiResponse<List<PendingCustomerResponse>>> findPending() {

        return ResponseEntity.ok(ApiResponse.success(verifikasiService.findPending()));
    }

    @GetMapping("/{customerId}")
    public ResponseEntity<ApiResponse<VerifikasiCustomerDetailResponse>> getDetail(
            @PathVariable UUID customerId) {
        return ResponseEntity.ok(ApiResponse.success(verifikasiService.getDetail(customerId)));
    }

    @PutMapping({"/{customerId}", "/{customerId}/verifikasi"})
    public ResponseEntity<ApiResponse<VerifikasiCustomerResponse>> verify(
            @PathVariable UUID customerId,
            @Valid @RequestBody VerifikasiCustomerRequest request,
            @AuthenticationPrincipal AppUser karyawan) {
        return ResponseEntity.ok(ApiResponse.success(
                verifikasiService.verify(customerId, karyawan.getIdKaryawan(), request)));
    }

    @PostMapping({"/{customerId}", "/{customerId}/verifikasi"})
    public ResponseEntity<ApiResponse<VerifikasiCustomerResponse>> verifyPost(
            @PathVariable UUID customerId,
            @Valid @RequestBody VerifikasiCustomerRequest request,
            @AuthenticationPrincipal AppUser karyawan) {
        return ResponseEntity.ok(ApiResponse.success(
                verifikasiService.verify(customerId, karyawan.getIdKaryawan(), request)));
    }
}

