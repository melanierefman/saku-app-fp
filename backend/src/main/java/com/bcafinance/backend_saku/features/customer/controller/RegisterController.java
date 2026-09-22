package com.bcafinance.backend_saku.features.customer.controller;

import com.bcafinance.backend_saku.core.dto.ApiResponse;
import com.bcafinance.backend_saku.features.customer.dto.RegisterStep1KtpRequest;
import com.bcafinance.backend_saku.features.customer.dto.RegisterStep2PersonalRequest;
import com.bcafinance.backend_saku.features.customer.dto.RegisterStep5CompleteRequest;
import com.bcafinance.backend_saku.features.customer.dto.RegisterStepResponse;
import com.bcafinance.backend_saku.features.customer.service.RegisterService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Customer - Registrasi", description = "Alur pendaftaran nasabah baru SAKU App (Cek NIK/HP, Step 1-5 Registrasi)")
public class RegisterController {

    private final RegisterService registerService;

    // Cek ketersediaan NIK customer
    @Operation(summary = "Cek ketersediaan NIK", description = "Memeriksa apakah NIK sudah pernah terdaftar di sistem")
    @GetMapping("/check-nik")
    public ResponseEntity<ApiResponse<Boolean>> checkNik(
            @RequestParam String nik,
            @RequestParam(required = false) UUID customerId) {
        return ResponseEntity.ok(ApiResponse.success(registerService.checkNik(nik, customerId)));
    }

    // Cek ketersediaan nomor HP customer
    @Operation(summary = "Cek ketersediaan nomor HP", description = "Memeriksa apakah nomor handphone sudah pernah terdaftar di sistem")
    @GetMapping("/check-phone")
    public ResponseEntity<ApiResponse<Boolean>> checkPhone(
            @RequestParam String phone,
            @RequestParam(required = false) UUID customerId) {
        return ResponseEntity.ok(ApiResponse.success(registerService.checkPhone(phone, customerId)));
    }

    // Step 1: Upload e-KTP dan data OCR (Multipart)
    @Operation(summary = "Registrasi Step 1 (KTP Multipart)", description = "Upload foto e-KTP dan input data identitas OCR")
    @PostMapping(value = "/step1-ktp/{customerId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<RegisterStepResponse>> step1KtpMultipart(
            @PathVariable UUID customerId,
            @RequestPart(value = "ktp", required = false) MultipartFile ktpFile,
            @Valid @RequestPart(value = "data", required = false) RegisterStep1KtpRequest request) {
        return ResponseEntity.ok(ApiResponse.success(registerService.registerStep1Ktp(customerId, ktpFile, request)));
    }

    // Step 1: Konfirmasi data OCR e-KTP (JSON)
    @Operation(summary = "Registrasi Step 1 (KTP JSON)", description = "Konfirmasi data identitas OCR e-KTP dalam format JSON")
    @PostMapping(value = "/step1-ktp/{customerId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<RegisterStepResponse>> step1KtpJson(
            @PathVariable UUID customerId,
            @Valid @RequestBody RegisterStep1KtpRequest request) {
        return ResponseEntity.ok(ApiResponse.success(registerService.registerStep1Ktp(customerId, null, request)));
    }

    // Step 2: Input data pribadi, pekerjaan, dan rekening bank
    @Operation(summary = "Registrasi Step 2 (Data Pribadi)", description = "Pengisian data pribadi, kontak darurat, informasi pekerjaan, dan rekening pencairan")
    @PutMapping("/step2-personal/{customerId}")
    public ResponseEntity<ApiResponse<RegisterStepResponse>> step2Personal(
            @PathVariable UUID customerId,
            @Valid @RequestBody RegisterStep2PersonalRequest request) {
        return ResponseEntity.ok(ApiResponse.success(registerService.registerStep2Personal(customerId, request)));
    }

    // Step 3: Verifikasi wajah liveness selfie & background scoring
    @Operation(summary = "Registrasi Step 3 (Selfie & Liveness)", description = "Upload foto selfie untuk verifikasi biometrik liveness dan inisiasi scoring")
    @PostMapping(value = "/step3-liveness/{customerId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<RegisterStepResponse>> step3Liveness(
            @PathVariable UUID customerId,
            @RequestPart("selfie") MultipartFile selfieFile) {
        return ResponseEntity.ok(ApiResponse.success(registerService.registerStep3Liveness(customerId, selfieFile)));
    }

    // Step 4: Konfirmasi persetujuan syarat & ketentuan SAKU
    @Operation(summary = "Registrasi Step 4 (Syarat & Ketentuan)", description = "Persetujuan nasabah terhadap syarat dan ketentuan layanan SAKU App")
    @PutMapping("/step4-tnc/{customerId}")
    public ResponseEntity<ApiResponse<RegisterStepResponse>> step4Tnc(
            @PathVariable UUID customerId) {
        return ResponseEntity.ok(ApiResponse.success(registerService.registerStep4Tnc(customerId)));
    }

    // Step 5: Pembuatan kredensial password akun nasabah
    @Operation(summary = "Registrasi Step 5 (Buat Password)", description = "Pembuatan password akun nasabah dan finalisasi pendaftaran")
    @PostMapping("/step5-complete/{customerId}")
    public ResponseEntity<ApiResponse<RegisterStepResponse>> step5Complete(
            @PathVariable UUID customerId,
            @Valid @RequestBody RegisterStep5CompleteRequest request) {
        return ResponseEntity.ok(ApiResponse.success(registerService.registerStep5Complete(customerId, request)));
    }
}
