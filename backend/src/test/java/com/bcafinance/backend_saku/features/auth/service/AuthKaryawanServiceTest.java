package com.bcafinance.backend_saku.features.auth.service;

import com.bcafinance.backend_saku.core.entity.Karyawan;
import com.bcafinance.backend_saku.core.repository.KaryawanRepository;
import com.bcafinance.backend_saku.core.security.AppUser;
import com.bcafinance.backend_saku.core.security.JwtService;
import com.bcafinance.backend_saku.features.auth.dto.AuthRequest;
import com.bcafinance.backend_saku.features.auth.dto.AuthResponse;
import com.bcafinance.backend_saku.features.superadmin.auditlog.service.AuditLogService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthKaryawanServiceTest {

    @Mock
    private JwtService jwtService;

    @Mock
    private AuthenticationManager karyawanAuthenticationManager;

    @Mock
    private KaryawanRepository karyawanRepository;

    @Mock
    private OtpService otpService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuditLogService auditLogService;

    @InjectMocks
    private AuthKaryawanService authKaryawanService;

    @Test
    @DisplayName("Login Karyawan: Berhasil login dengan role MARKETING dan mengembalikan profil karyawan")
    void testLoginKaryawanSuccess() {
        UUID karyawanId = UUID.randomUUID();
        AppUser appUser = new AppUser(
                karyawanId,
                "marketing@bcafinance.co.id",
                "marketing_user",
                "encodedPass",
                "MARKETING",
                "KARYAWAN",
                Collections.emptyList()
        );

        Authentication auth = new UsernamePasswordAuthenticationToken(appUser, null, appUser.getAuthorities());
        when(karyawanAuthenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(auth);
        when(jwtService.issue(any(AppUser.class), any(Instant.class))).thenReturn("karyawan-access-token");
        when(jwtService.issueRefreshToken(any(AppUser.class), any(Instant.class))).thenReturn("karyawan-refresh-token");
        when(jwtService.getTtlSeconds()).thenReturn(900L);

        Karyawan karyawan = new Karyawan();
        karyawan.setId(karyawanId);
        karyawan.setNama("Marketing Officer");
        karyawan.setEmail("marketing@bcafinance.co.id");
        karyawan.setStatus(true);
        when(karyawanRepository.findById(karyawanId)).thenReturn(Optional.of(karyawan));

        AuthRequest request = new AuthRequest("marketing_user", "password123");
        AuthResponse response = authKaryawanService.login(request);

        assertThat(response).isNotNull();
        assertThat(response.getAccessToken()).isEqualTo("karyawan-access-token");
        assertThat(response.getUser().getRole()).isEqualTo("MARKETING");
        assertThat(response.getUser().getNama()).isEqualTo("Marketing Officer");
    }

    @Test
    @DisplayName("Login Karyawan: Gagal login melempar BadCredentialsException")
    void testLoginKaryawanBadCredentials() {
        when(karyawanAuthenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Invalid"));

        AuthRequest request = new AuthRequest("marketing_user", "wrong_pass");

        assertThatThrownBy(() -> authKaryawanService.login(request))
                .isInstanceOf(BadCredentialsException.class)
                .hasMessageContaining("Username/email atau password salah");
    }
}
