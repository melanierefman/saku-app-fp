package com.bcafinance.backend_saku.features.customer.service;

import com.bcafinance.backend_saku.core.entity.AlamatCustomer;
import com.bcafinance.backend_saku.core.entity.Customer;
import com.bcafinance.backend_saku.core.entity.DokumenCustomer;
import com.bcafinance.backend_saku.core.entity.ScoringCustomer;
import com.bcafinance.backend_saku.core.exception.BussinessRuleException;
import com.bcafinance.backend_saku.core.repository.AlamatCustomerRepository;
import com.bcafinance.backend_saku.core.repository.CustomerRepository;
import com.bcafinance.backend_saku.core.repository.DokumenCustomerRepository;
import com.bcafinance.backend_saku.core.repository.ScoringCustomerRepository;
import com.bcafinance.backend_saku.core.repository.VerifikasiCustomerRepository;
import com.bcafinance.backend_saku.core.storage.FileStorageService;
import com.bcafinance.backend_saku.features.auth.service.OtpService;
import com.bcafinance.backend_saku.features.customer.dto.RegisterStep1KtpRequest;
import com.bcafinance.backend_saku.features.customer.dto.RegisterStep1Request;
import com.bcafinance.backend_saku.features.customer.dto.RegisterStep2PersonalRequest;
import com.bcafinance.backend_saku.features.customer.dto.RegisterStep5CompleteRequest;
import com.bcafinance.backend_saku.features.customer.dto.RegisterStepResponse;
import com.bcafinance.backend_saku.features.scoring.service.ScoringService;
import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;

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

    // Tests for New 5-Step Registration Flow
    @Test
    @DisplayName("Step 1 (KTP): Berhasil menyimpan data e-KTP dan NIK")
    void testRegisterStep1KtpSuccess() {
        UUID customerId = UUID.randomUUID();
        Customer customer = new Customer();
        customer.setId(customerId);
        customer.setEmail("customer@example.com");

        com.bcafinance.backend_saku.features.customer.dto.AlamatCustomer alamatDto =
                new com.bcafinance.backend_saku.features.customer.dto.AlamatCustomer(
                        "Jl. Sudirman No 1", "001", "002", "Menteng", "Menteng", "Jakarta Pusat", "DKI Jakarta", "10310"
                );
        RegisterStep1KtpRequest req = new RegisterStep1KtpRequest("3171012345678901", "Budi Santoso", alamatDto);
        MockMultipartFile ktpFile = new MockMultipartFile("ktp", "ktp.jpg", "image/jpeg", "image content".getBytes());

        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));
        when(customerRepository.existsByNikAndIdNot(req.nik(), customerId)).thenReturn(false);
        when(fileStorageService.store(any(), any())).thenReturn("http://saku/ktp.jpg");
        when(dokumenRepository.findByCustomer_IdAndDocType(customerId, "KTP")).thenReturn(Optional.empty());

        RegisterStepResponse res = registerService.registerStep1Ktp(customerId, ktpFile, req);

        assertThat(res).isNotNull();
        assertThat(res.step()).isEqualTo(1);
        assertThat(res.customerId()).isEqualTo(customerId);
        assertThat(customer.getNik()).isEqualTo("3171012345678901");
        assertThat(customer.getNama()).isEqualTo("Budi Santoso");
        verify(alamatRepository).save(any(AlamatCustomer.class));
        verify(dokumenRepository).save(any(DokumenCustomer.class));
    }

    @Test
    @DisplayName("Step 2 (Personal): Berhasil menyimpan data pribadi & rekening")
    void testRegisterStep2PersonalSuccess() {
        UUID customerId = UUID.randomUUID();
        Customer customer = new Customer();
        customer.setId(customerId);

        RegisterStep2PersonalRequest req = new RegisterStep2PersonalRequest(
                "081234567890", "Budi Santoso", "BCA", "1234567890",
                "IT Engineer", "PT SAKU Tech", "TETAP",
                new BigDecimal("15000000"), 24, new BigDecimal("1000000"),
                null, true, "Siti"
        );

        AlamatCustomer mockKtp = new AlamatCustomer();
        mockKtp.setAlamatLengkap("Jl. Sudirman");
        mockKtp.setJenisAlamat("KTP");

        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));
        when(customerRepository.existsByNoHpAndIdNot(req.noHp(), customerId)).thenReturn(false);
        when(alamatRepository.findByCustomer_IdAndJenisAlamat(customerId, "KTP")).thenReturn(Optional.of(mockKtp));

        RegisterStepResponse res = registerService.registerStep2Personal(customerId, req);

        assertThat(res).isNotNull();
        assertThat(res.step()).isEqualTo(2);
        assertThat(customer.getNoHp()).isEqualTo("081234567890");
        assertThat(customer.getNamaBank()).isEqualTo("BCA");
        verify(alamatRepository).deleteByCustomer_IdAndJenisAlamat(customerId, "DOMISILI");
        verify(alamatRepository).save(any(AlamatCustomer.class));
        verify(scoringRepository).save(any(ScoringCustomer.class));
    }

    @Test
    @DisplayName("Step 3 (Liveness): Berhasil upload selfie dokumen KYC")
    void testRegisterStep3LivenessSuccess() {
        UUID customerId = UUID.randomUUID();
        Customer customer = new Customer();
        customer.setId(customerId);

        MockMultipartFile selfieFile = new MockMultipartFile("selfie", "selfie.jpg", "image/jpeg", "image".getBytes());

        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));
        when(fileStorageService.store(any(), any())).thenReturn("http://saku/selfie.jpg");
        when(dokumenRepository.findByCustomer_IdAndDocType(customerId, "SELFIE")).thenReturn(Optional.empty());

        RegisterStepResponse res = registerService.registerStep3Liveness(customerId, selfieFile);

        assertThat(res).isNotNull();
        assertThat(res.step()).isEqualTo(3);
        verify(dokumenRepository).save(any(DokumenCustomer.class));
    }

    @Test
    @DisplayName("Step 5 (Complete): Berhasil finalisasi kredensial password (email-only)")
    void testRegisterStep5CompleteSuccess() {
        UUID customerId = UUID.randomUUID();
        Customer customer = new Customer();
        customer.setId(customerId);
        customer.setEmail("budi@example.com");

        RegisterStep5CompleteRequest req = new RegisterStep5CompleteRequest("password123", "password123");

        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword123");

        RegisterStepResponse res = registerService.registerStep5Complete(customerId, req);

        assertThat(res).isNotNull();
        assertThat(res.step()).isEqualTo(5);
        assertThat(customer.getUsername()).isEqualTo("budi@example.com");
        assertThat(customer.getPassword()).isEqualTo("encodedPassword123");
        assertThat(customer.getStatus()).isFalse();
        verify(customerRepository).save(customer);
    }

    // Tests for Legacy Registration Flow
    @Test
    @DisplayName("Register Step 1 (Legacy): Berhasil mendaftarkan akun nasabah baru")
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
                "passwordDifferent"
        );

        assertThatThrownBy(() -> registerService.registerStep1(req))
                .isInstanceOf(BussinessRuleException.class)
                .hasMessage("Password dan konfirmasi password tidak sama");
    }
}
