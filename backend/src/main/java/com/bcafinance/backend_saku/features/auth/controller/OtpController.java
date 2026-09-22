package com.bcafinance.backend_saku.features.auth.controller;

import com.bcafinance.backend_saku.core.dto.ApiResponse;
import com.bcafinance.backend_saku.features.auth.dto.SendOtpRequest;
import com.bcafinance.backend_saku.features.auth.dto.SendOtpResponse;
import com.bcafinance.backend_saku.features.auth.dto.VerifyOtpRequest;
import com.bcafinance.backend_saku.features.auth.dto.VerifyOtpResponse;
import com.bcafinance.backend_saku.features.auth.service.OtpService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth/otp")
@RequiredArgsConstructor
@Tag(name = "Auth OTP", description = "Pengiriman dan verifikasi One-Time Password (OTP) via Email / SMS")
public class OtpController {

    private final OtpService otpService;

    // Kirim kode OTP verifikasi ke email atau nomor handphone nasabah
    @Operation(summary = "Kirim kode OTP", description = "Mengirim kode OTP verifikasi ke email atau nomor handphone nasabah/karyawan")
    @PostMapping({"/send", "/request"})
    public ResponseEntity<ApiResponse<SendOtpResponse>> sendOtp(
            @Valid @RequestBody SendOtpRequest request) {
        return ResponseEntity.ok(ApiResponse.success(otpService.sendOtp(request)));
    }

    // Kirim ulang kode OTP verifikasi baru jika kode sebelumnya kedaluwarsa
    @Operation(summary = "Kirim ulang OTP", description = "Mengirim ulang kode OTP baru jika kode sebelumnya kadaluarsa")
    @PostMapping("/resend")
    public ResponseEntity<ApiResponse<SendOtpResponse>> resendOtp(
            @Valid @RequestBody SendOtpRequest request) {
        return ResponseEntity.ok(ApiResponse.success(otpService.sendOtp(request)));
    }

    // Verifikasi validitas kode OTP yang dimasukkan oleh nasabah
    @Operation(summary = "Verifikasi OTP", description = "Memverifikasi validitas kode OTP yang dimasukkan oleh pengguna")
    @PostMapping("/verify")
    public ResponseEntity<ApiResponse<VerifyOtpResponse>> verifyOtp(
            @Valid @RequestBody VerifyOtpRequest request) {
        return ResponseEntity.ok(ApiResponse.success(otpService.verifyOtp(request)));
    }
}
