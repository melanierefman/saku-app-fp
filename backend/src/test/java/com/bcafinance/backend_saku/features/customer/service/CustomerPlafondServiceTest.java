package com.bcafinance.backend_saku.features.customer.service;

import com.bcafinance.backend_saku.core.entity.Plafond;
import com.bcafinance.backend_saku.core.entity.ScoringCustomer;
import com.bcafinance.backend_saku.core.repository.AngsuranRepository;
import com.bcafinance.backend_saku.core.repository.PengajuanPinjamanRepository;
import com.bcafinance.backend_saku.core.repository.PlafondRepository;
import com.bcafinance.backend_saku.core.repository.ScoringCustomerRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomerPlafondServiceTest {

    @Mock
    private ScoringCustomerRepository scoringRepository;

    @Mock
    private PlafondRepository plafondRepository;

    @Mock
    private PengajuanPinjamanRepository pengajuanRepository;

    @Mock
    private AngsuranRepository angsuranRepository;

    @InjectMocks
    private CustomerPlafondService customerPlafondService;

    @Test
    @DisplayName("Plafond Summary: Nasabah tanpa scoring harus mengembalikan summary 0")
    void testCalculatePlafondSummaryNoScoring() {
        UUID customerId = UUID.randomUUID();
        when(scoringRepository.findFirstByMstCustomerIdOrderByCreatedDateDesc(customerId))
                .thenReturn(Optional.empty());

        var summary = customerPlafondService.calculatePlafondSummary(customerId);

        assertThat(summary.totalPlafond()).isEqualTo(BigDecimal.ZERO);
        assertThat(summary.usedPlafond()).isEqualTo(BigDecimal.ZERO);
        assertThat(summary.availablePlafond()).isEqualTo(BigDecimal.ZERO);
    }

    @Test
    @DisplayName("Plafond Summary: Nasabah dengan skor 80 (100% plafond) dan pinjaman 0")
    void testCalculatePlafondSummaryFullLimit() {
        UUID customerId = UUID.randomUUID();
        UUID plafondId = UUID.randomUUID();

        ScoringCustomer scoring = new ScoringCustomer();
        scoring.setMstCustomerId(customerId);
        scoring.setMstPlafondId(plafondId);
        scoring.setSkor(80); // >= 75 gives 100% limit

        Plafond plafond = new Plafond();
        plafond.setId(plafondId);
        plafond.setNama("Prioritas");
        plafond.setMinSkor(70);
        plafond.setMaxSkor(100);
        plafond.setMinPendapatan(BigDecimal.valueOf(5_000_000));
        plafond.setPlafondMaksimal(BigDecimal.valueOf(50_000_000));
        plafond.setStatus(true);

        when(scoringRepository.findFirstByMstCustomerIdOrderByCreatedDateDesc(customerId))
                .thenReturn(Optional.of(scoring));
        when(plafondRepository.findAllByStatusTrue()).thenReturn(java.util.List.of(plafond));
        when(pengajuanRepository.findAllByMstCustomerIdOrderByCreatedDateDesc(customerId))
                .thenReturn(Collections.emptyList());

        var summary = customerPlafondService.calculatePlafondSummary(customerId);

        assertThat(summary.totalPlafond()).isEqualByComparingTo(BigDecimal.valueOf(50_000_000));
        assertThat(summary.usedPlafond()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(summary.availablePlafond()).isEqualByComparingTo(BigDecimal.valueOf(50_000_000));
    }
}
