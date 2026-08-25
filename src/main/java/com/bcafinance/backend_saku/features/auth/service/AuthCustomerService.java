package com.bcafinance.backend_saku.features.auth.service;

import com.bcafinance.backend_saku.core.entity.Customer;
import com.bcafinance.backend_saku.core.exception.BussinessRuleException;
import com.bcafinance.backend_saku.core.repository.CustomerRepository;
import com.bcafinance.backend_saku.core.security.AppUser;
import com.bcafinance.backend_saku.core.security.JwtService;
import com.bcafinance.backend_saku.features.auth.dto.AuthRequest;
import com.bcafinance.backend_saku.features.auth.dto.AuthResponse;
import com.bcafinance.backend_saku.features.auth.dto.ForgotPasswordRequest;
import com.bcafinance.backend_saku.features.auth.dto.RefreshTokenRequest;
import com.bcafinance.backend_saku.features.auth.dto.ResetPasswordRequest;
import com.bcafinance.backend_saku.features.auth.dto.SendOtpRequest;
import com.bcafinance.backend_saku.features.auth.dto.SendOtpResponse;
import com.bcafinance.backend_saku.features.auth.dto.UserLoginProfile;
import com.bcafinance.backend_saku.features.auth.dto.VerifyOtpRequest;
import io.jsonwebtoken.Claims;
import jakarta.transaction.Transactional;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthCustomerService {

    private final JwtService jwtService;
    private final AuthenticationManager customerAuthenticationManager;
    private final CustomerRepository customerRepository;
    private final OtpService otpService;
    private final PasswordEncoder passwordEncoder;

    public AuthCustomerService(
            JwtService jwtService,
            @Qualifier("customerAuthenticationManager") AuthenticationManager customerAuthenticationManager,
            CustomerRepository customerRepository,
            OtpService otpService,
            PasswordEncoder passwordEncoder) {
        this.jwtService = jwtService;
        this.customerAuthenticationManager = customerAuthenticationManager;
        this.customerRepository = customerRepository;
        this.otpService = otpService;
        this.passwordEncoder = passwordEncoder;
    }

    // Login

    public AuthResponse login(AuthRequest request) {
        Authentication authentication;

        try {
            authentication = customerAuthenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getIdentifier(),
                            request.getPassword()));
        } catch (BadCredentialsException e) {
            throw new BadCredentialsException(
                    "Username/email atau password salah");
        }

        AppUser user = (AppUser) authentication.getPrincipal();
        return generateAuthResponse(user);
    }

    // Refresh Token
    public AuthResponse refreshToken(RefreshTokenRequest request) {
        try {
            Claims claims = jwtService.parse(request.getRefreshToken());
            String tokenType = claims.get("tokenType", String.class);
            if (!"REFRESH".equals(tokenType)) {
                throw new BussinessRuleException("Token bukan merupakan refresh token yang valid");
            }

            String username = claims.getSubject();
            Customer customer = customerRepository.findByUsernameOrEmail(username)
                    .orElseThrow(() -> new BussinessRuleException("Nasabah tidak ditemukan"));

            AppUser user = new AppUser(
                    customer.getId(),
                    customer.getEmail(),
                    customer.getUsername(),
                    customer.getPassword(),
                    "CUSTOMER",
                    "CUSTOMER",
                    List.of());

            return generateAuthResponse(user);
        } catch (Exception e) {
            throw new BussinessRuleException("Refresh token tidak valid atau telah kedaluwarsa");
        }
    }

    private AuthResponse generateAuthResponse(AppUser user) {
        Instant now = Instant.now();
        String accessToken = jwtService.issue(user, now);
        String refreshToken = jwtService.issueRefreshToken(user, now);

        Customer customer = customerRepository.findById(user.getIdKaryawan()).orElse(null);

        UserLoginProfile profile = UserLoginProfile.builder()
                .id(user.getIdKaryawan())
                .username(user.getUsername())
                .nama(customer != null && customer.getNama() != null ? customer.getNama() : user.getUsername())
                .email(user.getEmail())
                .noHp(customer != null ? customer.getNoHp() : null)
                .role("CUSTOMER")
                .tipe("CUSTOMER")
                .status(customer != null ? customer.getStatus() : true)
                .isKycVerified(customer != null && Boolean.TRUE.equals(customer.getStatus()))
                .build();

        return AuthResponse.builder()
                .tokenType("Bearer")
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .expiresIn(jwtService.getTtlSeconds())
                .user(profile)
                .build();
    }


    // Lupa Password: Kirim OTP ke email customer
    public SendOtpResponse forgotPassword(ForgotPasswordRequest request) {
        String email = request.getEmail().trim().toLowerCase();
        customerRepository.findByEmail(email)
                .orElseThrow(() -> new BussinessRuleException("Email tidak terdaftar sebagai nasabah SAKU"));

        return otpService.sendOtp(new SendOtpRequest(email, "RESET_PASSWORD"));
    }

    // Reset Password: Verifikasi OTP + ubah password
    @Transactional
    public String resetPassword(ResetPasswordRequest request) {
        String email = request.getEmail().trim().toLowerCase();

        if (!request.getNewPassword().equals(request.getConfirmNewPassword())) {
            throw new BussinessRuleException("Password baru dan konfirmasi password tidak sama");
        }

        Customer customer = customerRepository.findByEmail(email)
                .orElseThrow(() -> new BussinessRuleException("Nasabah dengan email ini tidak ditemukan"));

        // Verifikasi OTP
        otpService.verifyOtp(new VerifyOtpRequest(email, request.getOtpCode(), "RESET_PASSWORD"));

        // Update password customer
        customer.setPassword(passwordEncoder.encode(request.getNewPassword()));
        customer.setUpdatedDate(LocalDateTime.now());
        customerRepository.save(customer);

        return "Password berhasil diubah. Silakan login dengan password baru.";
    }
}

