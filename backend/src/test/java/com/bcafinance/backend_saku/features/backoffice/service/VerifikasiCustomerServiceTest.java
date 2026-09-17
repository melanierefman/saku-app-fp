package com.bcafinance.backend_saku.features.backoffice.service;

import com.bcafinance.backend_saku.core.entity.Customer;
import com.bcafinance.backend_saku.core.entity.ScoringCustomer;
import com.bcafinance.backend_saku.core.entity.VerifikasiCustomer;
import com.bcafinance.backend_saku.core.exception.BussinessRuleException;
import com.bcafinance.backend_saku.core.repository.AlamatCustomerRepository;
import com.bcafinance.backend_saku.core.repository.CustomerRepository;
import com.bcafinance.backend_saku.core.repository.DokumenCustomerRepository;
import com.bcafinance.backend_saku.core.repository.ScoringCustomerRepository;
import com.bcafinance.backend_saku.core.repository.VerifikasiCustomerRepository;
import com.bcafinance.backend_saku.features.backoffice.dto.VerifikasiCustomerRequest;
import com.bcafinance.backend_saku.features.backoffice.dto.VerifikasiCustomerResponse;
import com.bcafinance.backend_saku.features.superadmin.auditlog.service.AuditLogService;
import com.bcafinance.backend_saku.features.superadmin.plafond.PlafondCalculationResponse;
import com.bcafinance.backend_saku.features.superadmin.plafond.PlafondService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VerifikasiCustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private ScoringCustomerRepository scoringRepository;

    @Mock
    private VerifikasiCustomerRepository verifikasiRepository;

    @Mock
    private AlamatCustomerRepository alamatRepository;

    @Mock
    private DokumenCustomerRepository dokumenRepository;

    @Mock
    private PlafondService plafondService;

    @Mock
    private com.bcafinance.backend_saku.features.scoring.service.ScoringService scoringService;

    @Mock
    private AuditLogService auditLogService;

    @Mock
    private com.bcafinance.backend_saku.features.customer.service.NotifikasiService notifikasiService;

    @InjectMocks
    private VerifikasiCustomerService verifikasiCustomerService;

    @Test
    @DisplayName("Verify: Backoffice menyetujui verifikasi KYC customer (Status customer -> true)")
    void testVerifyApproveSuccess() {
        UUID customerId = UUID.randomUUID();
        UUID karyawanId = UUID.randomUUID();
        UUID plafondId = UUID.randomUUID();

        Customer customer = new Customer();
        customer.setId(customerId);
        customer.setNama("Budi Santoso");
        customer.setStatus(false);

        ScoringCustomer scoring = new ScoringCustomer();
        scoring.setId(UUID.randomUUID());
        scoring.setMstCustomerId(customerId);
        scoring.setPenghasilanBulanan(new BigDecimal("7000000.00"));
        scoring.setTotalCicilanLainBulanan(new BigDecimal("500000.00"));
        scoring.setLamaBekerjaBulan(24);
        scoring.setStatusPekerjaan("TETAP");
        scoring.setSkor(0);
        scoring.setStatusScoring("PENDING_VERIFIKASI");

        PlafondCalculationResponse calcResponse = new PlafondCalculationResponse(
                plafondId, "Plafond Gold", "APPROVED", 100, new BigDecimal("20000000.00"), new BigDecimal("20000000.00"));

        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));
        when(scoringRepository.findFirstByMstCustomerIdOrderByCreatedDateDesc(customerId)).thenReturn(Optional.of(scoring));
        when(scoringService.calculateScore(any(), any(), org.mockito.ArgumentMatchers.anyInt(), any()))
                .thenReturn(new com.bcafinance.backend_saku.features.scoring.service.ScoringService.ScoringResult(80.0, "APPROVED"));
        when(plafondService.calculateApprovedAmount(any(), anyDouble())).thenReturn(calcResponse);
        when(verifikasiRepository.save(any(VerifikasiCustomer.class))).thenAnswer(i -> i.getArgument(0));

        VerifikasiCustomerRequest request = new VerifikasiCustomerRequest();
        request.setStatusVerifikasi("APPROVED");
        request.setCatatanVerifikasi("Dokumen KTP dan selfie valid");

        VerifikasiCustomerResponse response = verifikasiCustomerService.verify(customerId, karyawanId, request);

        assertThat(response).isNotNull();
        assertThat(customer.getStatus()).isTrue();
        assertThat(response.getStatusVerifikasi()).isEqualTo("APPROVED");
        verify(customerRepository).save(customer);
        verify(verifikasiRepository).save(any(VerifikasiCustomer.class));
    }

    @Test
    @DisplayName("Verify: Melempar exception jika customer tidak ditemukan")
    void testVerifyCustomerNotFound() {
        UUID customerId = UUID.randomUUID();
        UUID karyawanId = UUID.randomUUID();

        when(customerRepository.findById(customerId)).thenReturn(Optional.empty());

        VerifikasiCustomerRequest request = new VerifikasiCustomerRequest();
        request.setStatusVerifikasi("APPROVED");
        request.setCatatanVerifikasi("Dokumen valid");

        assertThatThrownBy(() -> verifikasiCustomerService.verify(customerId, karyawanId, request))
                .isInstanceOf(BussinessRuleException.class)
                .hasMessageContaining("tidak ditemukan");
    }
}
