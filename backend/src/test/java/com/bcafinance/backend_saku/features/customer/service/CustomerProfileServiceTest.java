package com.bcafinance.backend_saku.features.customer.service;

import com.bcafinance.backend_saku.core.entity.Customer;
import com.bcafinance.backend_saku.core.exception.BussinessRuleException;
import com.bcafinance.backend_saku.core.repository.AlamatCustomerRepository;
import com.bcafinance.backend_saku.core.repository.CustomerRepository;
import com.bcafinance.backend_saku.core.repository.DokumenCustomerRepository;
import com.bcafinance.backend_saku.core.repository.ScoringCustomerRepository;
import com.bcafinance.backend_saku.core.repository.VerifikasiCustomerRepository;
import com.bcafinance.backend_saku.features.customer.dto.CustomerProfileResponse;
import com.bcafinance.backend_saku.features.customer.dto.UpdateRekeningRequest;
import com.bcafinance.backend_saku.features.scoring.service.ScoringService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomerProfileServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private AlamatCustomerRepository alamatRepository;

    @Mock
    private DokumenCustomerRepository dokumenCustomerRepository;

    @Mock
    private ScoringCustomerRepository scoringRepository;

    @Mock
    private VerifikasiCustomerRepository verifikasiRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private ScoringService scoringService;

    @Mock
    private CustomerPlafondService customerPlafondService;

    @InjectMocks
    private CustomerProfileService customerProfileService;

    @Test
    @DisplayName("Get Profile: Berhasil mengambil profil customer yang ada")
    void testGetProfileSuccess() {
        UUID customerId = UUID.randomUUID();
        Customer customer = new Customer();
        customer.setId(customerId);
        customer.setNama("Budi Santoso");
        customer.setEmail("budi@example.com");
        customer.setUsername("budis");
        customer.setStatus(true);

        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));
        when(alamatRepository.findByCustomer_IdAndJenisAlamat(any(), any())).thenReturn(Optional.empty());
        when(dokumenCustomerRepository.findByCustomer_IdAndDocType(any(), any())).thenReturn(Optional.empty());
        when(scoringRepository.findFirstByMstCustomerIdOrderByCreatedDateDesc(customerId)).thenReturn(Optional.empty());
        when(verifikasiRepository.findFirstByMstCustomerIdOrderByCreatedDateDesc(customerId)).thenReturn(Optional.empty());
        when(customerPlafondService.calculatePlafondSummary(customerId))
                .thenReturn(new CustomerPlafondService.CustomerPlafondSummary(BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO));

        CustomerProfileResponse response = customerProfileService.getProfile(customerId);

        assertThat(response).isNotNull();
        assertThat(response.getNama()).isEqualTo("Budi Santoso");
        assertThat(response.getEmail()).isEqualTo("budi@example.com");
    }

    @Test
    @DisplayName("Get Profile: Melempar exception jika customer ID tidak ditemukan")
    void testGetProfileNotFound() {
        UUID customerId = UUID.randomUUID();
        when(customerRepository.findById(customerId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> customerProfileService.getProfile(customerId))
                .isInstanceOf(BussinessRuleException.class)
                .hasMessageContaining("tidak ditemukan");
    }

    @Test
    @DisplayName("Update Rekening: Berhasil memperbarui data nomor rekening nasabah")
    void testUpdateRekeningSuccess() {
        UUID customerId = UUID.randomUUID();
        Customer customer = new Customer();
        customer.setId(customerId);
        customer.setNama("Budi Santoso");

        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));
        when(customerRepository.save(any(Customer.class))).thenAnswer(i -> i.getArgument(0));
        when(alamatRepository.findByCustomer_IdAndJenisAlamat(any(), any())).thenReturn(Optional.empty());
        when(dokumenCustomerRepository.findByCustomer_IdAndDocType(any(), any())).thenReturn(Optional.empty());
        when(scoringRepository.findFirstByMstCustomerIdOrderByCreatedDateDesc(customerId)).thenReturn(Optional.empty());
        when(verifikasiRepository.findFirstByMstCustomerIdOrderByCreatedDateDesc(customerId)).thenReturn(Optional.empty());
        when(customerPlafondService.calculatePlafondSummary(customerId))
                .thenReturn(new CustomerPlafondService.CustomerPlafondSummary(BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO));

        UpdateRekeningRequest request = new UpdateRekeningRequest("BCA", "1234567890", "Budi Santoso");
        CustomerProfileResponse response = customerProfileService.updateRekening(customerId, request);

        assertThat(response).isNotNull();
        assertThat(customer.getNamaBank()).isEqualTo("BCA");
        assertThat(customer.getNoRekening()).isEqualTo("1234567890");
        verify(customerRepository).save(customer);
    }
}
