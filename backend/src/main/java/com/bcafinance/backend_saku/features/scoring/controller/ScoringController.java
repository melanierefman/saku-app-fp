package com.bcafinance.backend_saku.features.scoring.controller;

import com.bcafinance.backend_saku.core.dto.ApiResponse;
import com.bcafinance.backend_saku.features.scoring.service.ScoringService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping({"/api/scoring", "/api/backoffice/scoring"})
@RequiredArgsConstructor
@Tag(name = "Scoring Engine", description = "Engine perhitungan skor kredit dan batas kelayakan plafond nasabah")
public class ScoringController {

    private final ScoringService scoringService;

    // Kalkulasi ulang seluruh data skor kredit nasabah secara massal (Batch Admin Utility)
    @Operation(summary = "Kalkulasi ulang scoring massal", description = "Menjalankan kalkulasi ulang seluruh credit score nasabah yang terdaftar di sistem")
    @PostMapping("/recalculate-all")
    public ResponseEntity<ApiResponse<ScoringService.ScoringSyncSummary>> recalculateAllScoring() {
        ScoringService.ScoringSyncSummary summary = scoringService.recalculateAllScoring();
        return ResponseEntity.ok(ApiResponse.success(200, "Sukses memperbarui seluruh data scoring nasabah", summary));
    }
}
