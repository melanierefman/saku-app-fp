package com.bcafinance.backend_saku.features.marketing.controller;

import com.bcafinance.backend_saku.core.dto.ApiResponse;
import com.bcafinance.backend_saku.core.dto.PageResponse;
import com.bcafinance.backend_saku.core.security.AppUser;
import com.bcafinance.backend_saku.features.marketing.dto.MarketingPengajuanDetailResponse;
import com.bcafinance.backend_saku.features.marketing.dto.MarketingPengajuanItemResponse;
import com.bcafinance.backend_saku.features.marketing.dto.ReviewPengajuanRequest;
import com.bcafinance.backend_saku.features.marketing.dto.ReviewPengajuanResponse;
import com.bcafinance.backend_saku.features.marketing.service.MarketingReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
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
@RequestMapping("/api/marketing/pengajuan-pinjaman")
@RequiredArgsConstructor
@Tag(name = "Marketing - Pengajuan Pinjaman", description = "Evaluasi dan review pengajuan pinjaman nasabah oleh staf Marketing")
public class MarketingPengajuanController {

    private final MarketingReviewService marketingReviewService;

    // Ambil daftar pengajuan pinjaman marketing dengan paginasi dan filter status/search
    @Operation(summary = "Daftar pengajuan pinjaman marketing", description = "Mengambil daftar pengajuan pinjaman dengan pagination, filter status, dan pencarian")
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<MarketingPengajuanItemResponse>>> findAll(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size,
            @RequestParam(name = "search", required = false) String search,
            @RequestParam(name = "status", required = false) String status,
            @AuthenticationPrincipal AppUser karyawan) {
        UUID karyawanId = karyawan != null ? karyawan.getIdKaryawan() : null;
        return ResponseEntity.ok(ApiResponse.success(
                marketingReviewService.findAllPaginated(page, size, search, status, karyawanId)));
    }

    // Ambil rincian detail dokumen & pengajuan pinjaman nasabah untuk direview
    @Operation(summary = "Detail pengajuan pinjaman", description = "Mengambil rincian berkas dokumen, data nasabah, dan hasil scoring untuk direview oleh Marketing")
    @GetMapping("/{pengajuanId}")
    public ResponseEntity<ApiResponse<MarketingPengajuanDetailResponse>> getDetail(
            @PathVariable UUID pengajuanId) {
        return ResponseEntity.ok(ApiResponse.success(marketingReviewService.getDetail(pengajuanId)));
    }

    // Simpan keputusan review pengajuan pinjaman oleh staf Marketing (Setuju / Tolak)
    @Operation(summary = "Keputusan review marketing", description = "Menyimpan keputusan rekomendasi persetujuan atau penolakan pengajuan pinjaman oleh Marketing")
    @PutMapping("/{pengajuanId}")
    public ResponseEntity<ApiResponse<ReviewPengajuanResponse>> review(
            @PathVariable UUID pengajuanId,
            @Valid @RequestBody ReviewPengajuanRequest request,
            @AuthenticationPrincipal AppUser karyawan) {
        return ResponseEntity.ok(ApiResponse.success(
                marketingReviewService.review(pengajuanId, karyawan.getIdKaryawan(), request)));
    }
}
