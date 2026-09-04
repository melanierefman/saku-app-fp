package com.bcafinance.backend_saku.features.superadmin.monitoring;

import com.bcafinance.backend_saku.core.dto.ApiResponse;
import com.bcafinance.backend_saku.core.dto.PageResponse;
import com.bcafinance.backend_saku.features.superadmin.monitoring.dto.MonitoringPengajuanDetailResponse;
import com.bcafinance.backend_saku.features.superadmin.monitoring.dto.MonitoringPengajuanItemResponse;
import com.bcafinance.backend_saku.features.superadmin.monitoring.service.MonitoringPengajuanService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping({"/api/superadmin/monitoring-pengajuan", "/api/master/monitoring-pengajuan", "/api/monitoring-pengajuan"})
@RequiredArgsConstructor
@PreAuthorize("hasRole('SUPERADMIN')")
public class MonitoringPengajuanController {

    private final MonitoringPengajuanService monitoringPengajuanService;

    // Get Paginated Data All (End-to-End: Marketing -> BM -> Backoffice Pencairan)
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<MonitoringPengajuanItemResponse>>> findAllPaginated(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size,
            @RequestParam(name = "search", required = false) String search,
            @RequestParam(name = "status", required = false) String status,
            @RequestParam(name = "branchId", required = false) UUID branchId) {

        PageResponse<MonitoringPengajuanItemResponse> result = monitoringPengajuanService
                .findAllPaginated(page, size, search, status, branchId);

        return ResponseEntity.ok(ApiResponse.success(result));
    }

    // Get Unpaginated Data All
    @GetMapping("/all")
    public ResponseEntity<ApiResponse<List<MonitoringPengajuanItemResponse>>> findAll(
            @RequestParam(name = "status", required = false) String status,
            @RequestParam(name = "branchId", required = false) UUID branchId) {
        return ResponseEntity.ok(ApiResponse.success(monitoringPengajuanService.findAll(status, branchId)));
    }

    // Get Detail Data
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<MonitoringPengajuanDetailResponse>> getDetail(
            @PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(monitoringPengajuanService.getDetail(id)));
    }
}
