package com.bcafinance.backend_saku.core.security;

import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Collections;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class JwtServiceTest {

    private JwtService jwtService;
    private static final String SECRET = "0123456789012345678901234567890123456789012345678901234567890123"; // 64 bytes
    private static final long TTL_MINUTES = 15;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService(SECRET, TTL_MINUTES);
    }

    @Test
    @DisplayName("Harus berhasil menerbitkan dan membaca JWT token dengan klaim yang sesuai")
    void testIssueAndParseToken() {
        UUID userId = UUID.randomUUID();
        AppUser appUser = new AppUser(
                userId,
                "user@example.com",
                "testuser",
                "password123",
                "MARKETING",
                "KARYAWAN",
                Collections.emptyList()
        );

        Instant now = Instant.now();
        String token = jwtService.issue(appUser, now);

        assertThat(token).isNotBlank();

        Claims claims = jwtService.parse(token);
        assertThat(claims.getSubject()).isEqualTo("testuser");
        assertThat(claims.get("role", String.class)).isEqualTo("MARKETING");
        assertThat(claims.get("idKaryawan", String.class)).isEqualTo(userId.toString());
        assertThat(claims.get("tipe", String.class)).isEqualTo("KARYAWAN");
        assertThat(claims.getExpiration()).isAfter(claims.getIssuedAt());
    }

    @Test
    @DisplayName("Harus berhasil menerbitkan refresh token dengan masa kedaluwarsa 30 hari")
    void testIssueRefreshToken() {
        UUID userId = UUID.randomUUID();
        AppUser appUser = new AppUser(
                userId,
                "user@example.com",
                "testuser",
                "password123",
                "CUSTOMER",
                "CUSTOMER",
                Collections.emptyList()
        );

        Instant now = Instant.now();
        String refreshToken = jwtService.issueRefreshToken(appUser, now);

        assertThat(refreshToken).isNotBlank();

        Claims claims = jwtService.parse(refreshToken);
        assertThat(claims.getSubject()).isEqualTo("testuser");
        assertThat(claims.get("tokenType", String.class)).isEqualTo("REFRESH");
        assertThat(claims.get("idKaryawan", String.class)).isEqualTo(userId.toString());
    }

    @Test
    @DisplayName("Harus mengembalikan nilai TTL dalam detik yang benar")
    void testGetTtlSeconds() {
        assertThat(jwtService.getTtlSeconds()).isEqualTo(15 * 60);
    }
}
