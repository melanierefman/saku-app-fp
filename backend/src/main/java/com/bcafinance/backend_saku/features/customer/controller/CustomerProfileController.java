package com.bcafinance.backend_saku.features.customer.controller;

import com.bcafinance.backend_saku.core.dto.ApiResponse;
import com.bcafinance.backend_saku.core.security.AppUser;
import com.bcafinance.backend_saku.features.customer.dto.ChangePasswordRequest;
import com.bcafinance.backend_saku.features.customer.dto.CustomerProfileResponse;
import com.bcafinance.backend_saku.features.customer.dto.FcmTokenRequest;
import com.bcafinance.backend_saku.features.customer.dto.UpdateDomisiliRequest;
import com.bcafinance.backend_saku.features.customer.dto.UpdatePekerjaanRequest;
import com.bcafinance.backend_saku.features.customer.dto.UpdateRekeningRequest;
import com.bcafinance.backend_saku.features.customer.service.CustomerProfileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/customer/profile")
@RequiredArgsConstructor
@PreAuthorize("hasRole('CUSTOMER')")
@Tag(name = "Customer - Profil", description = "Pengelolaan profil nasabah SAKU App (Profil, Rekening, Domisili, Pekerjaan, Ganti Password, KYC)")
public class CustomerProfileController {

    private final CustomerProfileService customerProfileService;

    // Ambil data profil lengkap customer yang sedang login
    @Operation(summary = "Ambil profil nasabah", description = "Mengambil seluruh rincian informasi profil nasabah yang sedang aktif login")
    @GetMapping
    public ResponseEntity<ApiResponse<CustomerProfileResponse>> getProfile(
            @AuthenticationPrincipal AppUser customer) {
        CustomerProfileResponse response = customerProfileService.getProfile(customer.getIdKaryawan());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    // Perbarui informasi rekening bank pencairan customer
    @Operation(summary = "Update rekening bank", description = "Memperbarui informasi nama bank dan nomor rekening pencairan nasabah")
    @PutMapping("/rekening")
    public ResponseEntity<ApiResponse<CustomerProfileResponse>> updateRekening(
            @AuthenticationPrincipal AppUser customer,
            @Valid @RequestBody UpdateRekeningRequest request) {
        CustomerProfileResponse response = customerProfileService.updateRekening(customer.getIdKaryawan(), request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    // Perbarui data alamat domisili customer
    @Operation(summary = "Update alamat domisili", description = "Memperbarui rincian alamat domisili tempat tinggal nasabah")
    @PutMapping("/domisili")
    public ResponseEntity<ApiResponse<CustomerProfileResponse>> updateDomisili(
            @AuthenticationPrincipal AppUser customer,
            @Valid @RequestBody UpdateDomisiliRequest request) {
        CustomerProfileResponse response = customerProfileService.updateDomisili(customer.getIdKaryawan(), request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    // Perbarui data pekerjaan, finansial, dan kalkulasi ulang skor kredit
    @Operation(summary = "Update data pekerjaan", description = "Memperbarui informasi pekerjaan/penghasilan dan melakukan kalkulasi ulang credit score")
    @PutMapping("/pekerjaan")
    public ResponseEntity<ApiResponse<CustomerProfileResponse>> updatePekerjaan(
            @AuthenticationPrincipal AppUser customer,
            @Valid @RequestBody UpdatePekerjaanRequest request) {
        CustomerProfileResponse response = customerProfileService.updatePekerjaan(customer.getIdKaryawan(), request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    // Ganti password akun customer
    @Operation(summary = "Ganti password nasabah", description = "Mengubah password akun nasabah dengan validasi password lama")
    @PutMapping("/change-password")
    public ResponseEntity<ApiResponse<String>> changePassword(
            @AuthenticationPrincipal AppUser customer,
            @Valid @RequestBody ChangePasswordRequest request) {
        customerProfileService.changePassword(customer.getIdKaryawan(), request);
        return ResponseEntity.ok(ApiResponse.success("Password berhasil diubah"));
    }

    // Perbarui token FCM untuk push notifikasi customer
    @Operation(summary = "Update FCM Token", description = "Mendaftarkan atau memperbarui token FCM perangkat nasabah untuk push notification")
    @PutMapping("/fcm-token")
    public ResponseEntity<ApiResponse<String>> updateFcmToken(
            @AuthenticationPrincipal AppUser customer,
            @Valid @RequestBody FcmTokenRequest request) {
        customerProfileService.updateFcmToken(customer.getIdKaryawan(), request.getFcmToken());
        return ResponseEntity.ok(ApiResponse.success("FCM token berhasil diperbarui"));
    }

    // Upload ulang dokumen KYC (KTP & Selfie) untuk proses verifikasi perbaikan
    @Operation(summary = "Upload ulang dokumen KYC", description = "Upload ulang dokumen perbaikan foto KTP dan foto selfie untuk proses verifikasi")
    @PostMapping(value = "/kyc-documents", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<CustomerProfileResponse>> updateKycDocuments(
            @AuthenticationPrincipal AppUser customer,
            @RequestPart(value = "ktp", required = false) MultipartFile ktpFile,
            @RequestPart(value = "selfie", required = false) MultipartFile selfieFile) {
        CustomerProfileResponse response = customerProfileService.updateKycDocuments(customer.getIdKaryawan(), ktpFile, selfieFile);
        return ResponseEntity.ok(ApiResponse.success(200, "Dokumen KYC berhasil diperbarui dan dikirim ulang untuk verifikasi.", response));
    }
}
