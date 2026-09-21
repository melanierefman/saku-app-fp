package com.bcafinance.backend_saku.features.scoring.controller;

import com.bcafinance.backend_saku.core.dto.ApiResponse;
import com.bcafinance.backend_saku.features.scoring.service.ScoringService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping({"/api/scoring", "/api/backoffice/scoring"})
@RequiredArgsConstructor
public class ScoringController {

    private final ScoringService scoringService;

    @PostMapping("/recalculate-all")
    public ResponseEntity<ApiResponse<ScoringService.ScoringSyncSummary>> recalculateAllScoring() {
        ScoringService.ScoringSyncSummary summary = scoringService.recalculateAllScoring();
        return ResponseEntity.ok(ApiResponse.success(200, "Sukses memperbarui seluruh data scoring nasabah", summary));
    }
}
