package com.bcafinance.backend_saku.features.auth.controller;

import com.bcafinance.backend_saku.core.dto.ApiResponse;
import com.bcafinance.backend_saku.core.exception.BussinessRuleException;
import com.bcafinance.backend_saku.core.security.AppUser;
import com.bcafinance.backend_saku.features.auth.dto.AuthRequest;
import com.bcafinance.backend_saku.features.auth.dto.AuthResponse;
import com.bcafinance.backend_saku.features.auth.dto.ChangePasswordRequest;
import com.bcafinance.backend_saku.features.auth.dto.ForgotPasswordRequest;
import com.bcafinance.backend_saku.features.auth.dto.RefreshTokenRequest;
import com.bcafinance.backend_saku.features.auth.dto.ResetPasswordRequest;
import com.bcafinance.backend_saku.features.auth.dto.SendOtpResponse;
import com.bcafinance.backend_saku.features.auth.service.AuthKaryawanService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth/karyawan")
@RequiredArgsConstructor
@Tag(name = "Auth Karyawan", description = "Autentikasi staf internal SAKU App (Superadmin, Backoffice, Marketing, Branch Manager)")
public class AuthKaryawanController {

    private final AuthKaryawanService authKaryawanService;

    // Login akun internal karyawan (Superadmin, Backoffice, Marketing, Branch Manager)
    @Operation(summary = "Login karyawan", description = "Autentikasi staf internal menggunakan email/username dan password")
    @PostMapping("/login")
    public ApiResponse<AuthResponse> login(@RequestBody AuthRequest request) {
        return ApiResponse.created(authKaryawanService.login(request));
    }

    // Permintaan OTP untuk alur lupa password karyawan
    @Operation(summary = "Lupa password karyawan", description = "Permintaan pengiriman kode OTP untuk alur lupa password staf internal")
    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse<SendOtpResponse>> forgotPassword(
            @Valid @RequestBody ForgotPasswordRequest request) {
        return ResponseEntity.ok(ApiResponse.success(authKaryawanService.forgotPassword(request)));
    }

    // Reset password akun karyawan setelah verifikasi OTP berhasil
    @Operation(summary = "Reset password karyawan", description = "Mengubah password akun staf internal setelah verifikasi OTP berhasil")
    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse<String>> resetPassword(
            @Valid @RequestBody ResetPasswordRequest request) {
        return ResponseEntity.ok(ApiResponse.success(authKaryawanService.resetPassword(request)));
    }

    // Perbarui JWT Access Token menggunakan Refresh Token karyawan
    @Operation(summary = "Refresh token karyawan", description = "Memperbarui JWT access token menggunakan refresh token karyawan")
    @PostMapping("/refresh-token")
    public ResponseEntity<ApiResponse<AuthResponse>> refreshToken(
            @Valid @RequestBody RefreshTokenRequest request) {
        return ResponseEntity.ok(ApiResponse.success(authKaryawanService.refreshToken(request)));
    }

    // Logout akun karyawan dan akhiri sesi login
    @Operation(summary = "Logout karyawan", description = "Mengakhiri sesi aktif staf internal")
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<String>> logout() {
        return ResponseEntity.ok(ApiResponse.success("Berhasil logout"));
    }

    // Ganti password akun karyawan yang sedang login
    @Operation(summary = "Ganti password karyawan", description = "Mengubah password akun staf internal yang sedang login")
    @PutMapping("/change-password")
    public ResponseEntity<ApiResponse<String>> changePassword(
            @Valid @RequestBody ChangePasswordRequest request,
            @AuthenticationPrincipal AppUser user) {
        if (user == null || user.getIdKaryawan() == null) {
            throw new BussinessRuleException("Sesi login tidak valid. Silakan login terlebih dahulu.");
        }
        authKaryawanService.changePassword(user.getIdKaryawan(), request);
        return ResponseEntity.ok(ApiResponse.success("Password berhasil diubah"));
    }
}
