package com.bcafinance.backend_saku.features.scoring.service;

import com.bcafinance.backend_saku.core.entity.Plafond;
import com.bcafinance.backend_saku.core.entity.ScoringCustomer;
import com.bcafinance.backend_saku.core.repository.PlafondRepository;
import com.bcafinance.backend_saku.core.repository.ScoringCustomerRepository;
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
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ScoringServiceTest {

    @Mock
    private PlafondRepository plafondRepository;

    @Mock
    private CustomerPlafondService customerPlafondService;

    @Mock
    private ScoringCustomerRepository scoringCustomerRepository;

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

        when(customerPlafondService.resolveCustomerPlafond(scoring)).thenReturn(plafond);
        when(plafondRepository.findAllByStatusTrue()).thenReturn(List.of(plafond));

        ScoringAnalysisResponse analysis = scoringService.analyzeScoring(scoring);

        assertThat(analysis).isNotNull();
        assertThat(analysis.getSkor()).isEqualTo(80);
        assertThat(analysis.getDbrPercentage()).isEqualTo(20.0);
        assertThat(analysis.getMatchedPlafondNama()).isEqualTo("Platinum");
    }

    @Test
    @DisplayName("PlafondCalculator: Customer A (Profil Prima) harus mendapatkan Rp 14.000.000 pada Tier Silver")
    void testPersonalizedPlafondCustomerA() {
        Plafond tierSilver = new Plafond();
        tierSilver.setId(UUID.randomUUID());
        tierSilver.setNama("Tier Silver");
        tierSilver.setMinPlafond(BigDecimal.valueOf(5_000_000));
        tierSilver.setMaxPlafond(BigDecimal.valueOf(15_000_000));
        tierSilver.setPlafondMaksimal(BigDecimal.valueOf(15_000_000));
        tierSilver.setMinSkor(60);
        tierSilver.setMaxSkor(74);

        int skor = 72;
        BigDecimal pendapatan = BigDecimal.valueOf(8_000_000);
        BigDecimal cicilanLain = BigDecimal.valueOf(800_000);
        int lamaKerja = 24;

        var result = PlafondCalculator.calculate(tierSilver, skor, pendapatan, cicilanLain, lamaKerja);

        assertThat(result).isNotNull();
        assertThat(result.finalApprovedPlafond()).isEqualByComparingTo(BigDecimal.valueOf(14_000_000));
        assertThat(result.totalWeight()).isGreaterThan(0.85);
    }

    @Test
    @DisplayName("PlafondCalculator: Customer B (Profil Pas-pasan) harus mendapatkan Rp 9.000.000 pada Tier Silver")
    void testPersonalizedPlafondCustomerB() {
        Plafond tierSilver = new Plafond();
        tierSilver.setId(UUID.randomUUID());
        tierSilver.setNama("Tier Silver");
        tierSilver.setMinPlafond(BigDecimal.valueOf(5_000_000));
        tierSilver.setMaxPlafond(BigDecimal.valueOf(15_000_000));
        tierSilver.setPlafondMaksimal(BigDecimal.valueOf(15_000_000));
        tierSilver.setMinSkor(60);
        tierSilver.setMaxSkor(74);

        int skor = 62;
        BigDecimal pendapatan = BigDecimal.valueOf(5_000_000);
        BigDecimal cicilanLain = BigDecimal.valueOf(1_800_000);
        int lamaKerja = 8;

        var result = PlafondCalculator.calculate(tierSilver, skor, pendapatan, cicilanLain, lamaKerja);

        assertThat(result).isNotNull();
        assertThat(result.finalApprovedPlafond()).isEqualByComparingTo(BigDecimal.valueOf(9_000_000));
        assertThat(result.totalWeight()).isLessThan(0.50);
    }

    @Test
    @DisplayName("PlafondCalculator: Customer dengan cicilan lain sangat tinggi harus direm oleh Safe Capacity Cap")
    void testPersonalizedPlafondSafeCapacityCapTriggered() {
        Plafond tierSilver = new Plafond();
        tierSilver.setId(UUID.randomUUID());
        tierSilver.setNama("Tier Silver");
        tierSilver.setMinPlafond(BigDecimal.valueOf(5_000_000));
        tierSilver.setMaxPlafond(BigDecimal.valueOf(15_000_000));
        tierSilver.setPlafondMaksimal(BigDecimal.valueOf(15_000_000));
        tierSilver.setMinSkor(60);
        tierSilver.setMaxSkor(74);

        int skor = 74; // skor tinggi
        BigDecimal pendapatan = BigDecimal.valueOf(6_000_000);
        BigDecimal cicilanLain = BigDecimal.valueOf(4_500_000); // cicilan 75% gaji -> sisa 1.5jt
        // Sisa 1.5jt -> 35% = 525rb/bln -> 12 bln = 6.3jt -> dibulatkan 500rb = 6.0jt
        int lamaKerja = 36;

        var result = PlafondCalculator.calculate(tierSilver, skor, pendapatan, cicilanLain, lamaKerja);

        assertThat(result).isNotNull();
        assertThat(result.finalApprovedPlafond()).isEqualByComparingTo(BigDecimal.valueOf(6_000_000));
        assertThat(result.finalApprovedPlafond()).isLessThan(result.basePlafond());
    }

    @Test
    @DisplayName("Recalculate All Scoring: Harus memproses dan memperbarui seluruh data scoring nasabah")
    void testRecalculateAllScoring() {
        ScoringCustomer scoring = new ScoringCustomer();
        scoring.setId(UUID.randomUUID());
        scoring.setPenghasilanBulanan(BigDecimal.valueOf(8_000_000));
        scoring.setTotalCicilanLainBulanan(BigDecimal.valueOf(800_000));
        scoring.setLamaBekerjaBulan(24);
        scoring.setStatusPekerjaan("WIRASWASTA");

        when(scoringCustomerRepository.findAll()).thenReturn(List.of(scoring));

        var summary = scoringService.recalculateAllScoring();

        assertThat(summary).isNotNull();
        assertThat(summary.totalProcessed()).isEqualTo(1);
        assertThat(summary.totalUpdated()).isEqualTo(1);
        assertThat(scoring.getSkor()).isEqualTo(73);
    }
}
