package com.bcafinance.backend_saku.features.auth.service;

import com.bcafinance.backend_saku.core.entity.Customer;
import com.bcafinance.backend_saku.core.exception.BussinessRuleException;
import com.bcafinance.backend_saku.core.repository.CustomerRepository;
import com.bcafinance.backend_saku.core.security.AppUser;
import com.bcafinance.backend_saku.core.security.JwtService;
import com.bcafinance.backend_saku.features.auth.dto.AuthRequest;
import com.bcafinance.backend_saku.features.auth.dto.AuthResponse;
import com.bcafinance.backend_saku.features.auth.dto.ForgotPasswordRequest;
import com.bcafinance.backend_saku.features.auth.dto.ResetPasswordRequest;
import com.bcafinance.backend_saku.features.auth.dto.SendOtpRequest;
import com.bcafinance.backend_saku.features.auth.dto.SendOtpResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthCustomerServiceTest {

    @Mock
    private JwtService jwtService;

    @Mock
    private AuthenticationManager customerAuthenticationManager;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private OtpService otpService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthCustomerService authCustomerService;

    @Test
    @DisplayName("Login: Berhasil login dengan kredensial valid dan mengembalikan AuthResponse")
    void testLoginSuccess() {
        UUID customerId = UUID.randomUUID();
        AppUser appUser = new AppUser(
                customerId,
                "customer@example.com",
                "cust_user",
                "encodedPass",
                "CUSTOMER",
                "CUSTOMER",
                Collections.emptyList()
        );

        Authentication auth = new UsernamePasswordAuthenticationToken(appUser, null, appUser.getAuthorities());
        when(customerAuthenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(auth);
        when(jwtService.issue(any(AppUser.class), any(Instant.class))).thenReturn("fake-access-token");
        when(jwtService.issueRefreshToken(any(AppUser.class), any(Instant.class))).thenReturn("fake-refresh-token");
        when(jwtService.getTtlSeconds()).thenReturn(900L);

        Customer customer = new Customer();
        customer.setId(customerId);
        customer.setNama("Budi Santoso");
        customer.setEmail("customer@example.com");
        customer.setStatus(true);
        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));

        AuthRequest request = new AuthRequest("cust_user", "password123");
        AuthResponse response = authCustomerService.login(request);

        assertThat(response).isNotNull();
        assertThat(response.getAccessToken()).isEqualTo("fake-access-token");
        assertThat(response.getRefreshToken()).isEqualTo("fake-refresh-token");
        assertThat(response.getUser().getUsername()).isEqualTo("cust_user");
        assertThat(response.getUser().getNama()).isEqualTo("Budi Santoso");
    }

    @Test
    @DisplayName("Login: Gagal login dengan kredensial salah harus melempar BadCredentialsException")
    void testLoginBadCredentials() {
        when(customerAuthenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        AuthRequest request = new AuthRequest("cust_user", "wrong_pass");

        assertThatThrownBy(() -> authCustomerService.login(request))
                .isInstanceOf(BadCredentialsException.class)
                .hasMessageContaining("Username/email atau password salah");
    }

    @Test
    @DisplayName("Forgot Password: Harus memanggil OtpService untuk email yang terdaftar")
    void testForgotPasswordSuccess() {
        String email = "customer@example.com";
        Customer customer = new Customer();
        customer.setEmail(email);

        when(customerRepository.findByEmail(email)).thenReturn(Optional.of(customer));
        when(otpService.sendOtp(any(SendOtpRequest.class)))
                .thenReturn(SendOtpResponse.builder().message("OTP sent").build());

        ForgotPasswordRequest request = new ForgotPasswordRequest(email);
        SendOtpResponse response = authCustomerService.forgotPassword(request);

        assertThat(response).isNotNull();
        assertThat(response.getMessage()).isEqualTo("OTP sent");
    }

    @Test
    @DisplayName("Reset Password: Harus melempar exception jika konfirmasi password tidak cocok")
    void testResetPasswordMismatch() {
        ResetPasswordRequest request = new ResetPasswordRequest();
        request.setEmail("customer@example.com");
        request.setOtpCode("123456");
        request.setNewPassword("newPassword123");
        request.setConfirmNewPassword("differentPassword");

        assertThatThrownBy(() -> authCustomerService.resetPassword(request))
                .isInstanceOf(BussinessRuleException.class)
                .hasMessageContaining("tidak sama");
    }
}
