package com.bcafinance.backend_saku.features.auth.service;

import com.bcafinance.backend_saku.core.entity.Karyawan;
import com.bcafinance.backend_saku.core.exception.BussinessRuleException;
import com.bcafinance.backend_saku.core.repository.KaryawanRepository;
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
import java.util.List;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthKaryawanService {

    private final JwtService jwtService;
    private final AuthenticationManager karyawanAuthenticationManager;
    private final KaryawanRepository karyawanRepository;
    private final OtpService otpService;
    private final PasswordEncoder passwordEncoder;
    private final com.bcafinance.backend_saku.features.superadmin.auditlog.service.AuditLogService auditLogService;

    public AuthKaryawanService(
            JwtService jwtService,
            @Qualifier("karyawanAuthenticationManager") AuthenticationManager karyawanAuthenticationManager,
            KaryawanRepository karyawanRepository,
            OtpService otpService,
            PasswordEncoder passwordEncoder,
            com.bcafinance.backend_saku.features.superadmin.auditlog.service.AuditLogService auditLogService) {
        this.jwtService = jwtService;
        this.karyawanAuthenticationManager = karyawanAuthenticationManager;
        this.karyawanRepository = karyawanRepository;
        this.otpService = otpService;
        this.passwordEncoder = passwordEncoder;
        this.auditLogService = auditLogService;
    }

    // Login

    public AuthResponse login(AuthRequest request) {
        Authentication authentication;

        try {
            authentication = karyawanAuthenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getIdentifier(),
                            request.getPassword()));
        } catch (BadCredentialsException e) {
            throw new BadCredentialsException(
                    "Username/email atau password salah");
        }

        AppUser user = (AppUser) authentication.getPrincipal();

        // Record Audit Log for Karyawan Login
        if (auditLogService != null && user.getIdKaryawan() != null) {
            auditLogService.recordLog(
                    user.getIdKaryawan(),
                    "LOGIN",
                    "AUTH",
                    "Karyawan " + user.getUsername() + " (" + user.getRole() + ") berhasil login ke sistem");
        }

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
            Karyawan karyawan = karyawanRepository.findByUsernameOrEmail(username)
                    .orElseThrow(() -> new BussinessRuleException("Karyawan tidak ditemukan"));

            AppUser user = new AppUser(
                    karyawan.getId(),
                    karyawan.getEmail(),
                    karyawan.getUsername(),
                    karyawan.getPassword(),
                    karyawan.getRole() != null ? karyawan.getRole().getNama() : "KARYAWAN",
                    "KARYAWAN",
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

        Karyawan karyawan = karyawanRepository.findById(user.getIdKaryawan()).orElse(null);

        UserLoginProfile profile = UserLoginProfile.builder()
                .id(user.getIdKaryawan())
                .username(user.getUsername())
                .nama(karyawan != null && karyawan.getNama() != null ? karyawan.getNama() : user.getUsername())
                .email(user.getEmail())
                .role(user.getRole())
                .tipe("KARYAWAN")
                .status(karyawan != null ? karyawan.getStatus() : true)
                .cabang(karyawan != null && karyawan.getCabang() != null ? karyawan.getCabang().getNama() : null)
                .permissions(user.getPermissions())
                .build();

        return AuthResponse.builder()
                .tokenType("Bearer")
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .expiresIn(jwtService.getTtlSeconds())
                .user(profile)
                .build();
    }


    // Lupa Password Karyawan: Kirim OTP ke email karyawan
    public SendOtpResponse forgotPassword(ForgotPasswordRequest request) {
        String email = request.getEmail().trim().toLowerCase();
        karyawanRepository.findByEmail(email)
                .orElseThrow(() -> new BussinessRuleException("Email tidak terdaftar sebagai karyawan SAKU"));

        return otpService.sendOtp(new SendOtpRequest(email, "RESET_PASSWORD"));
    }

    // Reset Password Karyawan: Verifikasi OTP + ubah password
    @Transactional
    public String resetPassword(ResetPasswordRequest request) {
        String email = request.getEmail().trim().toLowerCase();

        if (!request.getNewPassword().equals(request.getConfirmNewPassword())) {
            throw new BussinessRuleException("Password baru dan konfirmasi password tidak sama");
        }

        Karyawan karyawan = karyawanRepository.findByEmail(email)
                .orElseThrow(() -> new BussinessRuleException("Karyawan dengan email ini tidak ditemukan"));

        // Verifikasi OTP
        otpService.verifyOtp(new VerifyOtpRequest(email, request.getOtpCode(), "RESET_PASSWORD"));

        // Update password karyawan
        karyawan.setPassword(passwordEncoder.encode(request.getNewPassword()));
        karyawan.setUpdatedDate(new java.util.Date());
        karyawanRepository.save(karyawan);

        return "Password karyawan berhasil diubah. Silakan login dengan password baru.";
    }
}
