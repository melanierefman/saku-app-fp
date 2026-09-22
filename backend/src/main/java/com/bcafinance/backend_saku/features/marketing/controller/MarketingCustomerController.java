package com.bcafinance.backend_saku.features.marketing.controller;

import com.bcafinance.backend_saku.core.dto.ApiResponse;
import com.bcafinance.backend_saku.core.dto.PageResponse;
import com.bcafinance.backend_saku.features.marketing.dto.MarketingCustomerDetailResponse;
import com.bcafinance.backend_saku.features.marketing.dto.MarketingCustomerItemResponse;
import com.bcafinance.backend_saku.features.marketing.service.MarketingCustomerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/marketing/customers")
@RequiredArgsConstructor
@Tag(name = "Marketing - Daftar Nasabah", description = "Monitoring dan direktori nasabah terverifikasi KYC untuk tim Marketing")
public class MarketingCustomerController {

    private final MarketingCustomerService marketingCustomerService;

    @Operation(summary = "Daftar nasabah terverifikasi", description = "Mengambil daftar nasabah yang telah lolos verifikasi KYC Backoffice dengan paginasi dan pencarian")
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<MarketingCustomerItemResponse>>> findAll(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size,
            @RequestParam(name = "search", required = false) String search,
            @RequestParam(name = "tier", required = false) String tier,
            @org.springframework.security.core.annotation.AuthenticationPrincipal com.bcafinance.backend_saku.core.security.AppUser karyawan) {
        UUID karyawanId = karyawan != null ? karyawan.getIdKaryawan() : null;
        return ResponseEntity.ok(ApiResponse.success(
                marketingCustomerService.findAllPaginated(page, size, search, tier, karyawanId)));
    }

    @Operation(summary = "Detail nasabah terverifikasi", description = "Mengambil detail profil, sisa plafond, dan riwayat pengajuan pinjaman nasabah")
    @GetMapping("/{customerId}")
    public ResponseEntity<ApiResponse<MarketingCustomerDetailResponse>> getDetail(
            @PathVariable UUID customerId) {
        return ResponseEntity.ok(ApiResponse.success(
                marketingCustomerService.getDetail(customerId)));
    }
}
