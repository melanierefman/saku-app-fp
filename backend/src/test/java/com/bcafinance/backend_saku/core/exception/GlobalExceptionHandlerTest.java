package com.bcafinance.backend_saku.core.exception;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler exceptionHandler;

    @BeforeEach
    void setUp() {
        exceptionHandler = new GlobalExceptionHandler();
    }

    @Test
    @DisplayName("BussinessRuleException: Harus menghasilkan status 400 BAD_REQUEST dengan pesan yang sesuai")
    void testBusinessRuleException() {
        BussinessRuleException ex = new BussinessRuleException("Saldo plafond tidak mencukupi");

        ResponseEntity<Map<String, Object>> response = exceptionHandler.bussinessRuleException(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().get("statusCode")).isEqualTo(400);
        assertThat(response.getBody().get("message")).isEqualTo("Saldo plafond tidak mencukupi");
    }

    @Test
    @DisplayName("EntityNotFoundException: Harus menghasilkan status 404 NOT_FOUND")
    void testEntityNotFoundException() {
        EntityNotFoundException ex = new EntityNotFoundException("Data nasabah tidak ditemukan");

        ResponseEntity<Map<String, Object>> response = exceptionHandler.entityNotFound(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().get("statusCode")).isEqualTo(404);
        assertThat(response.getBody().get("message")).isEqualTo("Data nasabah tidak ditemukan");
    }

    @Test
    @DisplayName("BadCredentialsException: Harus menghasilkan status 401 UNAUTHORIZED")
    void testBadCredentialsException() {
        BadCredentialsException ex = new BadCredentialsException("Username atau password salah");

        ResponseEntity<Map<String, Object>> response = exceptionHandler.badCredentials(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().get("statusCode")).isEqualTo(401);
        assertThat(response.getBody().get("message")).isEqualTo("Username atau password salah");
    }

    @Test
    @DisplayName("AccessDeniedException: Harus menghasilkan status 403 FORBIDDEN")
    void testAccessDeniedException() {
        AccessDeniedException ex = new AccessDeniedException("Forbidden");

        ResponseEntity<Map<String, Object>> response = exceptionHandler.accessDenied(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().get("statusCode")).isEqualTo(403);
        assertThat(response.getBody().get("message")).isEqualTo("Akses ditolak: Anda tidak memiliki izin untuk mengakses resource ini");
    }
}
