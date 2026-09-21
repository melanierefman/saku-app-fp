package com.bcafinance.backend_saku.features.customer.controller;

import com.bcafinance.backend_saku.core.dto.ApiResponse;
import com.bcafinance.backend_saku.features.customer.dto.RegisterStep1KtpRequest;
import com.bcafinance.backend_saku.features.customer.dto.RegisterStep1Request;
import com.bcafinance.backend_saku.features.customer.dto.RegisterStep2PersonalRequest;
import com.bcafinance.backend_saku.features.customer.dto.RegisterStep2Request;
import com.bcafinance.backend_saku.features.customer.dto.RegisterStep3Request;
import com.bcafinance.backend_saku.features.customer.dto.RegisterStep5CompleteRequest;
import com.bcafinance.backend_saku.features.customer.dto.RegisterStepResponse;
import com.bcafinance.backend_saku.features.customer.service.RegisterService;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/auth/customer/register")
@RequiredArgsConstructor
public class RegisterController {

    private final RegisterService registerService;

    @GetMapping("/check-nik")
    public ResponseEntity<ApiResponse<Boolean>> checkNik(
            @RequestParam String nik,
            @RequestParam(required = false) UUID customerId) {
        return ResponseEntity.ok(ApiResponse.success(registerService.checkNik(nik, customerId)));
    }

    @GetMapping("/check-phone")
    public ResponseEntity<ApiResponse<Boolean>> checkPhone(
            @RequestParam String phone,
            @RequestParam(required = false) UUID customerId) {
        return ResponseEntity.ok(ApiResponse.success(registerService.checkPhone(phone, customerId)));
    }

    // Endpoint Alur Baru (5-Step KYC & Email Only)
    /**
     * Step 1: Scan & Konfirmasi e-KTP (OCR)
     */
    @PostMapping(value = "/step1-ktp/{customerId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<RegisterStepResponse>> step1KtpMultipart(
            @PathVariable UUID customerId,
            @RequestPart(value = "ktp", required = false) MultipartFile ktpFile,
            @Valid @RequestPart("data") RegisterStep1KtpRequest request) {
        return ResponseEntity.ok(ApiResponse.success(registerService.registerStep1Ktp(customerId, ktpFile, request)));
    }

    @PostMapping(value = "/step1-ktp/{customerId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<RegisterStepResponse>> step1KtpJson(
            @PathVariable UUID customerId,
            @Valid @RequestBody RegisterStep1KtpRequest request) {
        return ResponseEntity.ok(ApiResponse.success(registerService.registerStep1Ktp(customerId, null, request)));
    }

    /**
     * Step 2: Data Pribadi, Pekerjaan & Rekening Bank
     */
    @PutMapping("/step2-personal/{customerId}")
    public ResponseEntity<ApiResponse<RegisterStepResponse>> step2Personal(
            @PathVariable UUID customerId,
            @Valid @RequestBody RegisterStep2PersonalRequest request) {
        return ResponseEntity.ok(ApiResponse.success(registerService.registerStep2Personal(customerId, request)));
    }

    /**
     * Step 3: Verifikasi Wajah (Liveness Selfie) & Background Scoring
     */
    @PostMapping(value = "/step3-liveness/{customerId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<RegisterStepResponse>> step3Liveness(
            @PathVariable UUID customerId,
            @RequestPart("selfie") MultipartFile selfieFile) {
        return ResponseEntity.ok(ApiResponse.success(registerService.registerStep3Liveness(customerId, selfieFile)));
    }

    /**
     * Step 4: Konfirmasi Syarat & Ketentuan SAKU
     */
    @PutMapping("/step4-tnc/{customerId}")
    public ResponseEntity<ApiResponse<RegisterStepResponse>> step4Tnc(
            @PathVariable UUID customerId) {
        return ResponseEntity.ok(ApiResponse.success(registerService.registerStep4Tnc(customerId)));
    }

    /**
     * Step 5: Buat Kredensial Password (Email Only)
     */
    @PostMapping("/step5-complete/{customerId}")
    public ResponseEntity<ApiResponse<RegisterStepResponse>> step5Complete(
            @PathVariable UUID customerId,
            @Valid @RequestBody RegisterStep5CompleteRequest request) {
        return ResponseEntity.ok(ApiResponse.success(registerService.registerStep5Complete(customerId, request)));
    }

    // Endpoint Legacy (Backward Compatibility)
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
            @RequestPart(value = "ktp", required = false) MultipartFile ktpFile,
            @RequestPart(value = "selfie", required = false) MultipartFile selfieFile) {
        return ResponseEntity.ok(ApiResponse.success(registerService.registerStep4(customerId, ktpFile, selfieFile)));
    }
}
