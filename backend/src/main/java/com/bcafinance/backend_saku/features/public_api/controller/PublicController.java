package com.bcafinance.backend_saku.features.public_api.controller;

import com.bcafinance.backend_saku.core.dto.ApiResponse;
import com.bcafinance.backend_saku.features.public_api.dto.PublicPlafondResponse;
import com.bcafinance.backend_saku.features.public_api.dto.SimulasiPinjamanResponse;
import com.bcafinance.backend_saku.features.public_api.service.PublicService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.math.BigDecimal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/public")
@RequiredArgsConstructor
@Tag(name = "Public API", description = "Informasi publik dan kalkulator simulasi kredit SAKU App tanpa login")
public class PublicController {

    private final PublicService publicService;

    // Ambil daftar paket plafond dan bunga publik untuk landing page / kalkulator
    @Operation(summary = "Katalog plafond pinjaman", description = "Mengambil daftar paket batas kredit, suku bunga, dan tenor yang tersedia untuk publik")
    @GetMapping("/plafond")
    public ResponseEntity<ApiResponse<List<PublicPlafondResponse>>> getPublicPlafonds() {
        return ResponseEntity.ok(ApiResponse.success(publicService.getPublicPlafonds()));
    }

    // Hitung simulasi cicilan bulanan pinjaman berdasarkan nominal & tenor
    @Operation(summary = "Kalkulator simulasi pinjaman", description = "Menghitung estimasi angsuran cicilan per bulan dan total pengembalian pinjaman")
    @GetMapping("/simulasi")
    public ResponseEntity<ApiResponse<SimulasiPinjamanResponse>> hitungSimulasi(
            @RequestParam(required = false, defaultValue = "5000000") BigDecimal jumlahPinjaman,
            @RequestParam(required = false, defaultValue = "12") Integer tenorBulan) {
        return ResponseEntity.ok(ApiResponse.success(publicService.hitungSimulasi(jumlahPinjaman, tenorBulan)));
    }
}
