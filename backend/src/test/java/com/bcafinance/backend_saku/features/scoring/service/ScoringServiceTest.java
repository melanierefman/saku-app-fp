package com.bcafinance.backend_saku.features.scoring.service;

import com.bcafinance.backend_saku.core.entity.Plafond;
import com.bcafinance.backend_saku.core.entity.ScoringCustomer;
import com.bcafinance.backend_saku.core.repository.PlafondRepository;
import com.bcafinance.backend_saku.features.customer.service.CustomerPlafondService;
import com.bcafinance.backend_saku.features.scoring.dto.ScoringAnalysisResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ScoringServiceTest {

    @Mock
    private PlafondRepository plafondRepository;

    @Mock
    private CustomerPlafondService customerPlafondService;

    @InjectMocks
    private ScoringService scoringService;

    @Test
    @DisplayName("Calculate Score: Customer dengan pendapatan tinggi dan cicilan 0 harus mendapatkan skor tinggi dan status LOLOS")
    void testCalculateScoreHighIncomeNoDebt() {
        BigDecimal totalCicilan = BigDecimal.ZERO;
        BigDecimal pendapatan = BigDecimal.valueOf(15_000_000);
        int lamaBekerja = 36; // 3 tahun
        String statusPekerjaan = "KARYAWAN_TETAP";

        var result = scoringService.calculateScore(totalCicilan, pendapatan, lamaBekerja, statusPekerjaan);

        assertThat(result).isNotNull();
        assertThat(result.score()).isGreaterThanOrEqualTo(70.0);
        assertThat(result.decision()).isEqualTo("APPROVED");
    }

    @Test
    @DisplayName("Calculate Score: Customer dengan DBR tinggi dan masa kerja minim harus mendapatkan skor rendah dan status REJECTED")
    void testCalculateScoreHighDbrLowIncome() {
        BigDecimal totalCicilan = BigDecimal.valueOf(4_000_000);
        BigDecimal pendapatan = BigDecimal.valueOf(5_000_000); // DBR 80%
        int lamaBekerja = 2; // 2 bulan
        String statusPekerjaan = "KONTRAK";

        var result = scoringService.calculateScore(totalCicilan, pendapatan, lamaBekerja, statusPekerjaan);

        assertThat(result).isNotNull();
        assertThat(result.score()).isLessThan(60.0);
        assertThat(result.decision()).isEqualTo("REJECTED");
    }

    @Test
    @DisplayName("Analyze Scoring: Harus menghasilkan analisis komprehensif dari data ScoringCustomer")
    void testAnalyzeScoring() {
        ScoringCustomer scoring = new ScoringCustomer();
        scoring.setId(UUID.randomUUID());
        scoring.setPenghasilanBulanan(BigDecimal.valueOf(10_000_000));
        scoring.setTotalCicilanLainBulanan(BigDecimal.valueOf(2_000_000));
        scoring.setSkor(80);
        scoring.setStatusScoring("LOLOS");

        Plafond plafond = new Plafond();
        plafond.setId(UUID.randomUUID());
        plafond.setNama("Platinum");
        plafond.setMinPendapatan(BigDecimal.valueOf(8_000_000));
        plafond.setPlafondMaksimal(BigDecimal.valueOf(50_000_000));
        plafond.setMinSkor(70);
        plafond.setMaxSkor(100);
        plafond.setStatus(true);

        when(plafondRepository.findAllByStatusTrue()).thenReturn(List.of(plafond));
        when(plafondRepository.findTopByMinPendapatanLessThanEqualAndStatusTrueOrderByMinPendapatanDesc(any()))
                .thenReturn(Optional.of(plafond));

        ScoringAnalysisResponse analysis = scoringService.analyzeScoring(scoring);

        assertThat(analysis).isNotNull();
        assertThat(analysis.getSkor()).isEqualTo(80);
        assertThat(analysis.getDbrPercentage()).isEqualTo(20.0);
        assertThat(analysis.getMatchedPlafondNama()).isEqualTo("Platinum");
    }
}
