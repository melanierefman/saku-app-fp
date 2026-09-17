package com.bcafinance.backend_saku.features.auth.service;

import com.bcafinance.backend_saku.core.entity.Customer;
import com.bcafinance.backend_saku.core.entity.Otp;
import com.bcafinance.backend_saku.core.exception.BussinessRuleException;
import com.bcafinance.backend_saku.core.notification.EmailService;
import com.bcafinance.backend_saku.core.repository.CustomerRepository;
import com.bcafinance.backend_saku.core.repository.KaryawanRepository;
import com.bcafinance.backend_saku.core.repository.OtpRepository;
import com.bcafinance.backend_saku.features.auth.dto.SendOtpRequest;
import com.bcafinance.backend_saku.features.auth.dto.SendOtpResponse;
import com.bcafinance.backend_saku.features.auth.dto.VerifyOtpRequest;
import com.bcafinance.backend_saku.features.auth.dto.VerifyOtpResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OtpServiceTest {

    @Mock
    private OtpRepository otpRepository;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private KaryawanRepository karyawanRepository;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private OtpService otpService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(otpService, "otpExpiryMinutes", 5);
    }

    @Test
    @DisplayName("Send OTP: Harus berhasil mengirim OTP untuk customer yang sudah terdaftar")
    void testSendOtpSuccess() {
        String email = "customer@example.com";
        UUID customerId = UUID.randomUUID();
        Customer customer = new Customer();
        customer.setId(customerId);
        customer.setEmail(email);

        when(customerRepository.findByEmail(email)).thenReturn(Optional.of(customer));
        when(otpRepository.findAllByMstCustomerIdAndPurposeAndIsUsedFalse(customerId, "RESET_PASSWORD"))
                .thenReturn(Collections.emptyList());

        SendOtpRequest request = new SendOtpRequest();
        request.setEmail(email);
        request.setPurpose("RESET_PASSWORD");

        SendOtpResponse response = otpService.sendOtp(request);

        assertThat(response).isNotNull();
        assertThat(response.getPurpose()).isEqualTo("RESET_PASSWORD");
        assertThat(response.getExpiresInMinutes()).isEqualTo(5);
        verify(emailService).sendOtpEmail(eq(email), anyString(), eq("RESET_PASSWORD"), eq(5));
        verify(otpRepository).save(any(Otp.class));
    }

    @Test
    @DisplayName("Send OTP: Harus melempar exception jika email tidak ditemukan untuk purpose non-registration")
    void testSendOtpEmailNotFound() {
        String email = "unknown@example.com";
        when(customerRepository.findByEmail(email)).thenReturn(Optional.empty());
        when(karyawanRepository.findByEmail(email)).thenReturn(Optional.empty());

        SendOtpRequest request = new SendOtpRequest();
        request.setEmail(email);
        request.setPurpose("RESET_PASSWORD");

        assertThatThrownBy(() -> otpService.sendOtp(request))
                .isInstanceOf(BussinessRuleException.class)
                .hasMessageContaining("Email tidak ditemukan");
    }

    @Test
    @DisplayName("Verify OTP: Harus berhasil memvalidasi OTP yang aktif dan valid")
    void testVerifyOtpSuccess() {
        String email = "customer@example.com";
        String otpCode = "123456";
        UUID customerId = UUID.randomUUID();

        Customer customer = new Customer();
        customer.setId(customerId);
        customer.setEmail(email);

        Otp otp = new Otp();
        otp.setId(UUID.randomUUID());
        otp.setOtpCode(otpCode);
        otp.setPurpose("RESET_PASSWORD");
        otp.setIsUsed(false);
        otp.setExpiredAt(LocalDateTime.now().plusMinutes(5));
        otp.setMstCustomerId(customerId);

        when(customerRepository.findByEmail(email)).thenReturn(Optional.of(customer));
        when(otpRepository.findFirstByMstCustomerIdAndOtpCodeAndPurposeAndIsUsedFalseOrderByCreatedDateDesc(
                customerId, otpCode, "RESET_PASSWORD")).thenReturn(Optional.of(otp));

        VerifyOtpRequest request = new VerifyOtpRequest();
        request.setEmail(email);
        request.setOtpCode(otpCode);
        request.setPurpose("RESET_PASSWORD");

        VerifyOtpResponse response = otpService.verifyOtp(request);

        assertThat(response.getValid()).isTrue();
        assertThat(response.getCustomerId()).isEqualTo(customerId);
        assertThat(otp.getIsUsed()).isTrue();
        verify(otpRepository).save(otp);
    }

    @Test
    @DisplayName("Verify OTP: Harus melempar exception jika OTP kedaluwarsa")
    void testVerifyOtpExpired() {
        String email = "customer@example.com";
        String otpCode = "123456";
        UUID customerId = UUID.randomUUID();

        Customer customer = new Customer();
        customer.setId(customerId);
        customer.setEmail(email);

        Otp otp = new Otp();
        otp.setId(UUID.randomUUID());
        otp.setOtpCode(otpCode);
        otp.setPurpose("RESET_PASSWORD");
        otp.setIsUsed(false);
        otp.setExpiredAt(LocalDateTime.now().minusMinutes(1)); // Sudah kedaluwarsa
        otp.setMstCustomerId(customerId);

        when(customerRepository.findByEmail(email)).thenReturn(Optional.of(customer));
        when(otpRepository.findFirstByMstCustomerIdAndOtpCodeAndPurposeAndIsUsedFalseOrderByCreatedDateDesc(
                customerId, otpCode, "RESET_PASSWORD")).thenReturn(Optional.of(otp));

        VerifyOtpRequest request = new VerifyOtpRequest();
        request.setEmail(email);
        request.setOtpCode(otpCode);
        request.setPurpose("RESET_PASSWORD");

        assertThatThrownBy(() -> otpService.verifyOtp(request))
                .isInstanceOf(BussinessRuleException.class)
                .hasMessageContaining("kedaluwarsa");
    }
}
