package com.bcafinance.backend_saku.features.auth.controller;

import com.bcafinance.backend_saku.core.dto.ApiResponse;
import com.bcafinance.backend_saku.features.auth.dto.SendOtpRequest;
import com.bcafinance.backend_saku.features.auth.dto.SendOtpResponse;
import com.bcafinance.backend_saku.features.auth.dto.VerifyOtpRequest;
import com.bcafinance.backend_saku.features.auth.dto.VerifyOtpResponse;
import com.bcafinance.backend_saku.features.auth.service.OtpService;
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
public class OtpController {

    private final OtpService otpService;

    @PostMapping({"/send", "/request"})
    public ResponseEntity<ApiResponse<SendOtpResponse>> sendOtp(
            @Valid @RequestBody SendOtpRequest request) {
        return ResponseEntity.ok(ApiResponse.success(otpService.sendOtp(request)));
    }

    @PostMapping("/resend")
    public ResponseEntity<ApiResponse<SendOtpResponse>> resendOtp(
            @Valid @RequestBody SendOtpRequest request) {
        return ResponseEntity.ok(ApiResponse.success(otpService.sendOtp(request)));
    }

    @PostMapping("/verify")
    public ResponseEntity<ApiResponse<VerifyOtpResponse>> verifyOtp(
            @Valid @RequestBody VerifyOtpRequest request) {
        return ResponseEntity.ok(ApiResponse.success(otpService.verifyOtp(request)));
    }
}
