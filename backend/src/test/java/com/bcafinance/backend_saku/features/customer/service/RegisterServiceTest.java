package com.bcafinance.backend_saku.features.customer.service;

import com.bcafinance.backend_saku.core.entity.Customer;
import com.bcafinance.backend_saku.core.exception.BussinessRuleException;
import com.bcafinance.backend_saku.core.repository.AlamatCustomerRepository;
import com.bcafinance.backend_saku.core.repository.CustomerRepository;
import com.bcafinance.backend_saku.core.repository.DokumenCustomerRepository;
import com.bcafinance.backend_saku.core.repository.ScoringCustomerRepository;
import com.bcafinance.backend_saku.core.repository.VerifikasiCustomerRepository;
import com.bcafinance.backend_saku.core.storage.FileStorageService;
import com.bcafinance.backend_saku.features.auth.service.OtpService;
import com.bcafinance.backend_saku.features.customer.dto.RegisterStep1Request;
import com.bcafinance.backend_saku.features.customer.dto.RegisterStepResponse;
import com.bcafinance.backend_saku.features.scoring.service.ScoringService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RegisterServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private AlamatCustomerRepository alamatRepository;

    @Mock
    private DokumenCustomerRepository dokumenRepository;

    @Mock
    private ScoringCustomerRepository scoringRepository;

    @Mock
    private VerifikasiCustomerRepository verifikasiRepository;

    @Mock
    private ScoringService scoringService;

    @Mock
    private OtpService otpService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private FileStorageService fileStorageService;

    @InjectMocks
    private RegisterService registerService;

    @Test
    @DisplayName("Register Step 1: Berhasil mendaftarkan akun nasabah baru")
    void testRegisterStep1Success() {
        RegisterStep1Request req = new RegisterStep1Request(
                "newcustomer@example.com",
                "newcust",
                "081234567890",
                "password123",
                "password123"
        );

        when(customerRepository.findByEmail(req.email())).thenReturn(Optional.empty());
        when(customerRepository.existsByUsername(req.username())).thenReturn(false);
        when(passwordEncoder.encode(req.password())).thenReturn("encodedPassword");
        when(customerRepository.save(any(Customer.class))).thenAnswer(invocation -> {
            Customer c = invocation.getArgument(0);
            c.setId(UUID.randomUUID());
            return c;
        });

        RegisterStepResponse response = registerService.registerStep1(req);

        assertThat(response).isNotNull();
        assertThat(response.step()).isEqualTo(1);
        assertThat(response.customerId()).isNotNull();
        verify(customerRepository).save(any(Customer.class));
    }

    @Test
    @DisplayName("Register Step 1: Gagal jika password dan confirm password tidak sama")
    void testRegisterStep1PasswordMismatch() {
        RegisterStep1Request req = new RegisterStep1Request(
                "newcustomer@example.com",
                "newcust",
                "081234567890",
                "password123",
                "differentPassword"
        );

        assertThatThrownBy(() -> registerService.registerStep1(req))
                .isInstanceOf(BussinessRuleException.class)
                .hasMessageContaining("tidak sama");
    }

    @Test
    @DisplayName("Register Step 1: Gagal jika email sudah terdaftar")
    void testRegisterStep1DuplicateEmail() {
        RegisterStep1Request req = new RegisterStep1Request(
                "existing@example.com",
                "newcust",
                "081234567890",
                "password123",
                "password123"
        );

        Customer existing = new Customer();
        existing.setPassword("already_set_hash");
        when(customerRepository.findByEmail(req.email())).thenReturn(Optional.of(existing));

        assertThatThrownBy(() -> registerService.registerStep1(req))
                .isInstanceOf(BussinessRuleException.class)
                .hasMessageContaining("Email sudah terdaftar");
    }
}
