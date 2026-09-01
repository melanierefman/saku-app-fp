package com.bcafinance.backend_saku.features.master.monitoring;

import com.bcafinance.backend_saku.core.dto.ApiResponse;
import com.bcafinance.backend_saku.core.dto.PageResponse;
import com.bcafinance.backend_saku.features.marketing.dto.MarketingPengajuanDetailResponse;
import com.bcafinance.backend_saku.features.marketing.dto.MarketingPengajuanItemResponse;
import com.bcafinance.backend_saku.features.marketing.service.MarketingReviewService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/master/monitoring-pengajuan")
@RequiredArgsConstructor
@PreAuthorize("hasRole('SUPERADMIN')")
public class MonitoringPengajuanController {

    private final MarketingReviewService marketingReviewService;

    // Get Paginated Data All
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<MarketingPengajuanItemResponse>>> findAllPaginated(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size,
            @RequestParam(name = "search", required = false) String search,
            @RequestParam(name = "status", required = false) String status,
            @RequestParam(name = "branchId", required = false) UUID branchId) {

        PageResponse<MarketingPengajuanItemResponse> result = marketingReviewService
                .findAllPaginated(page, size, search, status, null);

        return ResponseEntity.ok(ApiResponse.success(result));
    }

    // Get Unpaginated Data All
    @GetMapping("/all")
    public ResponseEntity<ApiResponse<List<MarketingPengajuanItemResponse>>> findAll(
            @RequestParam(name = "status", required = false) String status) {
        return ResponseEntity.ok(ApiResponse.success(marketingReviewService.findAll(status, null)));
    }

    // Get Detail Data
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<MarketingPengajuanDetailResponse>> getDetail(
            @PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(marketingReviewService.getDetail(id)));
    }
}
