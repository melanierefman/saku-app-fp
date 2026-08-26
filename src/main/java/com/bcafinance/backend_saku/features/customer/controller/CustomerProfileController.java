package com.bcafinance.backend_saku.features.customer.controller;

import com.bcafinance.backend_saku.core.dto.ApiResponse;
import com.bcafinance.backend_saku.core.security.AppUser;
import com.bcafinance.backend_saku.features.customer.dto.ChangePasswordRequest;
import com.bcafinance.backend_saku.features.customer.dto.CustomerProfileResponse;
import com.bcafinance.backend_saku.features.customer.dto.UpdateDomisiliRequest;
import com.bcafinance.backend_saku.features.customer.dto.UpdatePekerjaanRequest;
import com.bcafinance.backend_saku.features.customer.dto.UpdateProfileRequest;
import com.bcafinance.backend_saku.features.customer.dto.UpdateRekeningRequest;
import com.bcafinance.backend_saku.features.customer.service.CustomerProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/customer/profile")
@RequiredArgsConstructor
@PreAuthorize("hasRole('CUSTOMER')")
public class CustomerProfileController {

    private final CustomerProfileService customerProfileService;

    @GetMapping
    public ResponseEntity<ApiResponse<CustomerProfileResponse>> getProfile(
            @AuthenticationPrincipal AppUser customer) {
        CustomerProfileResponse response = customerProfileService.getProfile(customer.getIdKaryawan());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PutMapping
    public ResponseEntity<ApiResponse<CustomerProfileResponse>> updateProfile(
            @AuthenticationPrincipal AppUser customer,
            @RequestBody UpdateProfileRequest request) {
        CustomerProfileResponse response = customerProfileService.updateProfile(customer.getIdKaryawan(), request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PutMapping("/rekening")
    public ResponseEntity<ApiResponse<CustomerProfileResponse>> updateRekening(
            @AuthenticationPrincipal AppUser customer,
            @Valid @RequestBody UpdateRekeningRequest request) {
        CustomerProfileResponse response = customerProfileService.updateRekening(customer.getIdKaryawan(), request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PutMapping("/domisili")
    public ResponseEntity<ApiResponse<CustomerProfileResponse>> updateDomisili(
            @AuthenticationPrincipal AppUser customer,
            @Valid @RequestBody UpdateDomisiliRequest request) {
        CustomerProfileResponse response = customerProfileService.updateDomisili(customer.getIdKaryawan(), request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PutMapping("/pekerjaan")
    public ResponseEntity<ApiResponse<CustomerProfileResponse>> updatePekerjaan(
            @AuthenticationPrincipal AppUser customer,
            @Valid @RequestBody UpdatePekerjaanRequest request) {
        CustomerProfileResponse response = customerProfileService.updatePekerjaan(customer.getIdKaryawan(), request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PutMapping("/change-password")
    public ResponseEntity<ApiResponse<String>> changePassword(
            @AuthenticationPrincipal AppUser customer,
            @Valid @RequestBody ChangePasswordRequest request) {
        customerProfileService.changePassword(customer.getIdKaryawan(), request);
        return ResponseEntity.ok(ApiResponse.success("Password berhasil diubah"));
    }
}
