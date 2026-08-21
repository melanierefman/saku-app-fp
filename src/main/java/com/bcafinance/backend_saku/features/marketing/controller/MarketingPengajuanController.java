package com.bcafinance.backend_saku.features.marketing.controller;

import com.bcafinance.backend_saku.core.dto.ApiResponse;
import com.bcafinance.backend_saku.features.marketing.dto.MarketingPengajuanDetailResponse;
import com.bcafinance.backend_saku.features.marketing.dto.MarketingPengajuanItemResponse;
import com.bcafinance.backend_saku.features.marketing.dto.ReviewPengajuanRequest;
import com.bcafinance.backend_saku.features.marketing.dto.ReviewPengajuanResponse;
import com.bcafinance.backend_saku.core.security.AppUser;
import com.bcafinance.backend_saku.features.marketing.service.MarketingReviewService;
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
@RequestMapping("/api/marketing/pengajuan-pinjaman")
@RequiredArgsConstructor
public class MarketingPengajuanController {

    private final MarketingReviewService marketingReviewService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<MarketingPengajuanItemResponse>>> findAll(
            @RequestParam(required = false) String status) {
        return ResponseEntity.ok(ApiResponse.success(marketingReviewService.findAll(status)));
    }

    @GetMapping("/{pengajuanId}")
    public ResponseEntity<ApiResponse<MarketingPengajuanDetailResponse>> getDetail(
            @PathVariable UUID pengajuanId) {
        return ResponseEntity.ok(ApiResponse.success(marketingReviewService.getDetail(pengajuanId)));
    }

    @PutMapping("/{pengajuanId}/review")
    public ResponseEntity<ApiResponse<ReviewPengajuanResponse>> review(
            @PathVariable UUID pengajuanId,
            @Valid @RequestBody ReviewPengajuanRequest request,
            @AuthenticationPrincipal AppUser karyawan) {
        return ResponseEntity.ok(ApiResponse.success(
                marketingReviewService.review(pengajuanId, karyawan.getIdKaryawan(), request)));
    }
}
