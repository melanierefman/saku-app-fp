package com.bcafinance.backend_saku.features.auth.controller;

import com.bcafinance.backend_saku.core.dto.ApiResponse;
import com.bcafinance.backend_saku.core.security.AppUser;
import com.bcafinance.backend_saku.features.auth.dto.AuthRequest;
import com.bcafinance.backend_saku.features.auth.dto.AuthResponse;
import com.bcafinance.backend_saku.features.auth.dto.ForgotPasswordRequest;
import com.bcafinance.backend_saku.features.auth.dto.RefreshTokenRequest;
import com.bcafinance.backend_saku.features.auth.dto.ResetPasswordRequest;
import com.bcafinance.backend_saku.features.auth.dto.SendOtpResponse;
import com.bcafinance.backend_saku.features.auth.service.AuthCustomerService;
import com.bcafinance.backend_saku.features.customer.service.CustomerProfileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth/customer")
@RequiredArgsConstructor
@Tag(name = "Auth Customer", description = "Autentikasi nasabah SAKU App (Login, Lupa Password, Refresh Token, Logout)")
public class AuthCustomerController {

    private final AuthCustomerService authCustomerService;
    private final CustomerProfileService customerProfileService;

    // Login akun nasabah menggunakan kredensial email/username & password
    @Operation(summary = "Login nasabah", description = "Autentikasi akun nasabah menggunakan email/username dan password")
    @PostMapping("/login")
    public ApiResponse<AuthResponse> login(@RequestBody AuthRequest request) {
        return ApiResponse.created(authCustomerService.login(request));
    }

    // Permintaan OTP untuk alur lupa password nasabah
    @Operation(summary = "Lupa password nasabah", description = "Permintaan pengiriman kode OTP untuk alur lupa password nasabah")
    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse<SendOtpResponse>> forgotPassword(
            @Valid @RequestBody ForgotPasswordRequest request) {
        return ResponseEntity.ok(ApiResponse.success(authCustomerService.forgotPassword(request)));
    }

    // Reset password akun nasabah setelah verifikasi OTP berhasil
    @Operation(summary = "Reset password nasabah", description = "Mengubah password nasabah setelah verifikasi OTP berhasil")
    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse<String>> resetPassword(
            @Valid @RequestBody ResetPasswordRequest request) {
        return ResponseEntity.ok(ApiResponse.success(authCustomerService.resetPassword(request)));
    }

    // Perbarui JWT Access Token menggunakan Refresh Token nasabah
    @Operation(summary = "Refresh token nasabah", description = "Memperbarui JWT access token menggunakan refresh token nasabah")
    @PostMapping("/refresh-token")
    public ResponseEntity<ApiResponse<AuthResponse>> refreshToken(
            @Valid @RequestBody RefreshTokenRequest request) {
        return ResponseEntity.ok(ApiResponse.success(authCustomerService.refreshToken(request)));
    }

    // Logout akun nasabah dan hapus sesi token aktif
    @Operation(summary = "Logout nasabah", description = "Logout akun nasabah dan menghapus token FCM yang tersimpan")
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<String>> logout(@AuthenticationPrincipal AppUser user) {
        if (user != null && user.getIdKaryawan() != null) {
            customerProfileService.updateFcmToken(user.getIdKaryawan(), null);
        }
        return ResponseEntity.ok(ApiResponse.success("Berhasil logout"));
    }
}
