package com.bcafinance.backend_saku.controller;

import com.bcafinance.backend_saku.dto.ApiResponse;
import com.bcafinance.backend_saku.dto.register.RegisterStep1Request;
import com.bcafinance.backend_saku.dto.register.RegisterStep2Request;
import com.bcafinance.backend_saku.dto.register.RegisterStep3Request;
import com.bcafinance.backend_saku.dto.register.RegisterStepResponse;
import com.bcafinance.backend_saku.service.RegisterService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import java.util.UUID;

@RestController
@RequestMapping("/api/auth/customer/register")
@RequiredArgsConstructor
public class RegisterController {

    private final RegisterService registerService;

    @PostMapping("/step1")
    public ResponseEntity<ApiResponse<RegisterStepResponse>> step1(
            @Valid @RequestBody RegisterStep1Request request) {
        return ResponseEntity.ok(ApiResponse.success(registerService.registerStep1(request)));
    }

    @PutMapping("/step2/{customerId}")
    public ResponseEntity<ApiResponse<RegisterStepResponse>> step2(
            @PathVariable UUID customerId,
            @Valid @RequestBody RegisterStep2Request request) {
        return ResponseEntity.ok(ApiResponse.success(registerService.registerStep2(customerId, request)));
    }

    @PutMapping("/step3/{customerId}")
    public ResponseEntity<ApiResponse<RegisterStepResponse>> step3(
            @PathVariable UUID customerId,
            @Valid @RequestBody RegisterStep3Request request) {
        return ResponseEntity.ok(ApiResponse.success(registerService.registerStep3(customerId, request)));
    }

    @PostMapping(value = "/step4/{customerId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<RegisterStepResponse>> step4(
            @PathVariable UUID customerId,
            @RequestPart("ktp") MultipartFile ktpFile,
            @RequestPart("selfie") MultipartFile selfieFile) {
        return ResponseEntity.ok(ApiResponse.success(registerService.registerStep4(customerId, ktpFile, selfieFile)));
    }
}
