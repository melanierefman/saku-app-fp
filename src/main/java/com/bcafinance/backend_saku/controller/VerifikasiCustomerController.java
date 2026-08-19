package com.bcafinance.backend_saku.controller;

import com.bcafinance.backend_saku.dto.ApiResponse;
import com.bcafinance.backend_saku.dto.PendingCustomerResponse;
import com.bcafinance.backend_saku.dto.VerifikasiCustomerRequest;
import com.bcafinance.backend_saku.dto.VerifikasiCustomerResponse;
import com.bcafinance.backend_saku.security.AppUser;
import com.bcafinance.backend_saku.service.VerifikasiCustomerService;
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
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/backoffice/verifikasi-customer")
@RequiredArgsConstructor
public class VerifikasiCustomerController {

    private final VerifikasiCustomerService verifikasiService;

    @GetMapping("/pending")
    public ResponseEntity<ApiResponse<List<PendingCustomerResponse>>> findPending() {
        return ResponseEntity.ok(ApiResponse.success(verifikasiService.findPending()));
    }

    @PutMapping("/{customerId}")
    public ResponseEntity<ApiResponse<VerifikasiCustomerResponse>> verify(
            @PathVariable UUID customerId,
            @Valid @RequestBody VerifikasiCustomerRequest request,
            @AuthenticationPrincipal AppUser karyawan) {
        return ResponseEntity.ok(ApiResponse.success(
                verifikasiService.verify(customerId, karyawan.getIdKaryawan(), request)));
    }
}
