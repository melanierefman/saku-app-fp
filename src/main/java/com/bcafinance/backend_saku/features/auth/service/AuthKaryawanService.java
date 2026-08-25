package com.bcafinance.backend_saku.features.auth.service;

import com.bcafinance.backend_saku.core.entity.Karyawan;
import com.bcafinance.backend_saku.core.exception.BussinessRuleException;
import com.bcafinance.backend_saku.core.repository.KaryawanRepository;
import com.bcafinance.backend_saku.core.security.AppUser;
import com.bcafinance.backend_saku.core.security.JwtService;
import com.bcafinance.backend_saku.features.auth.dto.AuthRequest;
import com.bcafinance.backend_saku.features.auth.dto.AuthResponse;
import com.bcafinance.backend_saku.features.auth.dto.ForgotPasswordRequest;
import com.bcafinance.backend_saku.features.auth.dto.ResetPasswordRequest;
import com.bcafinance.backend_saku.features.auth.dto.SendOtpRequest;
import com.bcafinance.backend_saku.features.auth.dto.SendOtpResponse;
import com.bcafinance.backend_saku.features.auth.dto.VerifyOtpRequest;
import jakarta.transaction.Transactional;
import java.time.Instant;
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

    public AuthKaryawanService(
            JwtService jwtService,
            @Qualifier("karyawanAuthenticationManager") AuthenticationManager karyawanAuthenticationManager,
            KaryawanRepository karyawanRepository,
            OtpService otpService,
            PasswordEncoder passwordEncoder) {
        this.jwtService = jwtService;
        this.karyawanAuthenticationManager = karyawanAuthenticationManager;
        this.karyawanRepository = karyawanRepository;
        this.otpService = otpService;
        this.passwordEncoder = passwordEncoder;
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

        String token = jwtService.issue(
                user,
                Instant.now());

        AuthResponse response = new AuthResponse();
        response.setEmail(user.getEmail());
        response.setUsername(user.getUsername());
        response.setToken(token);

        return response;
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
